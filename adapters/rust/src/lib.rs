use serde::{Deserialize, Serialize};
use serde_json::{json, Value};
use std::collections::HashMap;
use std::time::{SystemTime, UNIX_EPOCH};

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum ValidationResult {
    Valid,
    Invalid(String),
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct LongHunAdapter {
    uid: String,
    device: String,
}

impl LongHunAdapter {
    pub fn new(uid: &str, device: &str) -> Self {
        Self {
            uid: uid.to_string(),
            device: device.to_string(),
        }
    }

    pub fn wrap(&self, data: Value, task_type: &str, persona: &str) -> Result<Value, String> {
        let timestamp = SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .map_err(|e| format!("System time error: {}", e))?
            .as_secs();

        let wrapped = json!({
            "longhun_envelope": {
                "version": "1.0",
                "uid": self.uid,
                "device": self.device,
                "timestamp": timestamp,
                "task_type": task_type,
                "persona": persona
            },
            "payload": data
        });

        Ok(wrapped)
    }

    pub fn validate(&self, wrapped: &Value) -> ValidationResult {
        let envelope = match wrapped.get("longhun_envelope") {
            Some(e) => e,
            None => return ValidationResult::Invalid("Missing longhun_envelope".to_string()),
        };

        if envelope.get("version").and_then(|v| v.as_str()) != Some("1.0") {
            return ValidationResult::Invalid("Invalid or missing version".to_string());
        }

        if envelope.get("uid").and_then(|v| v.as_str()).is_none() {
            return ValidationResult::Invalid("Missing uid".to_string());
        }

        if envelope.get("device").and_then(|v| v.as_str()).is_none() {
            return ValidationResult::Invalid("Missing device".to_string());
        }

        if envelope.get("timestamp").and_then(|v| v.as_u64()).is_none() {
            return ValidationResult::Invalid("Missing or invalid timestamp".to_string());
        }

        if envelope.get("task_type").and_then(|v| v.as_str()).is_none() {
            return ValidationResult::Invalid("Missing task_type".to_string());
        }

        if envelope.get("persona").and_then(|v| v.as_str()).is_none() {
            return ValidationResult::Invalid("Missing persona".to_string());
        }

        if wrapped.get("payload").is_none() {
            return ValidationResult::Invalid("Missing payload".to_string());
        }

        ValidationResult::Valid
    }

    pub fn get_schemas(&self) -> (Value, Value) {
        let envelope_schema = json!({
            "type": "object",
            "required": ["version", "uid", "device", "timestamp", "task_type", "persona"],
            "properties": {
                "version": {"type": "string", "const": "1.0"},
                "uid": {"type": "string"},
                "device": {"type": "string"},
                "timestamp": {"type": "integer"},
                "task_type": {"type": "string"},
                "persona": {"type": "string"}
            }
        });

        let wrapper_schema = json!({
            "type": "object",
            "required": ["longhun_envelope", "payload"],
            "properties": {
                "longhun_envelope": envelope_schema,
                "payload": {"type": "object"}
            }
        });

        (envelope_schema, wrapper_schema)
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_new_adapter() {
        let adapter = LongHunAdapter::new("test-uid", "test-device");
        assert_eq!(adapter.uid, "test-uid");
        assert_eq!(adapter.device, "test-device");
    }

    #[test]
    fn test_wrap_basic() {
        let adapter = LongHunAdapter::new("uid123", "device456");
        let data = json!({"key": "value"});
        let result = adapter.wrap(data, "test_task", "test_persona");
        assert!(result.is_ok());
        let wrapped = result.unwrap();
        assert!(wrapped.get("longhun_envelope").is_some());
        assert!(wrapped.get("payload").is_some());
    }

    #[test]
    fn test_wrap_envelope_fields() {
        let adapter = LongHunAdapter::new("uid123", "device456");
        let data = json!({"test": "data"});
        let wrapped = adapter.wrap(data, "analysis", "researcher").unwrap();
        let envelope = wrapped.get("longhun_envelope").unwrap();
        assert_eq!(envelope.get("version").unwrap(), "1.0");
        assert_eq!(envelope.get("uid").unwrap(), "uid123");
        assert_eq!(envelope.get("device").unwrap(), "device456");
        assert_eq!(envelope.get("task_type").unwrap(), "analysis");
        assert_eq!(envelope.get("persona").unwrap(), "researcher");
        assert!(envelope.get("timestamp").unwrap().is_u64());
    }

    #[test]
    fn test_validate_valid() {
        let adapter = LongHunAdapter::new("uid", "device");
        let data = json!({"test": "data"});
        let wrapped = adapter.wrap(data, "task", "persona").unwrap();
        let result = adapter.validate(&wrapped);
        assert_eq!(result, ValidationResult::Valid);
    }

    #[test]
    fn test_validate_missing_envelope() {
        let adapter = LongHunAdapter::new("uid", "device");
        let invalid = json!({"payload": {}});
        let result = adapter.validate(&invalid);
        assert!(matches!(result, ValidationResult::Invalid(_)));
    }

    #[test]
    fn test_validate_missing_version() {
        let adapter = LongHunAdapter::new("uid", "device");
        let invalid = json!({
            "longhun_envelope": {
                "uid": "test",
                "device": "test",
                "timestamp": 123456,
                "task_type": "test",
                "persona": "test"
            },
            "payload": {}
        });
        let result = adapter.validate(&invalid);
        assert!(matches!(result, ValidationResult::Invalid(_)));
    }

    #[test]
    fn test_validate_missing_uid() {
        let adapter = LongHunAdapter::new("uid", "device");
        let invalid = json!({
            "longhun_envelope": {
                "version": "1.0",
                "device": "test",
                "timestamp": 123456,
                "task_type": "test",
                "persona": "test"
            },
            "payload": {}
        });
        let result = adapter.validate(&invalid);
        assert!(matches!(result, ValidationResult::Invalid(_)));
    }

    #[test]
    fn test_validate_missing_payload() {
        let adapter = LongHunAdapter::new("uid", "device");
        let invalid = json!({
            "longhun_envelope": {
                "version": "1.0",
                "uid": "test",
                "device": "test",
                "timestamp": 123456,
                "task_type": "test",
                "persona": "test"
            }
        });
        let result = adapter.validate(&invalid);
        assert!(matches!(result, ValidationResult::Invalid(_)));
    }

    #[test]
    fn test_get_schemas() {
        let adapter = LongHunAdapter::new("uid", "device");
        let (envelope_schema, wrapper_schema) = adapter.get_schemas();
        assert!(envelope_schema.get("type").is_some());
        assert!(wrapper_schema.get("type").is_some());
        assert!(wrapper_schema.get("required").is_some());
    }

    #[test]
    fn test_wrap_preserves_payload() {
        let adapter = LongHunAdapter::new("uid", "device");
        let data = json!({"nested": {"key": "value"}, "array": [1, 2, 3]});
        let wrapped = adapter.wrap(data.clone(), "task", "persona").unwrap();
        assert_eq!(wrapped.get("payload").unwrap(), &data);
    }

    #[test]
    fn test_multiple_wraps_different_timestamps() {
        let adapter = LongHunAdapter::new("uid", "device");
        let data = json!({"test": "data"});
        let wrapped1 = adapter.wrap(data.clone(), "task", "persona").unwrap();
        std::thread::sleep(std::time::Duration::from_millis(10));
        let wrapped2 = adapter.wrap(data, "task", "persona").unwrap();
        let ts1 = wrapped1["longhun_envelope"]["timestamp"].as_u64().unwrap();
        let ts2 = wrapped2["longhun_envelope"]["timestamp"].as_u64().unwrap();
        assert!(ts2 >= ts1);
    }
}
