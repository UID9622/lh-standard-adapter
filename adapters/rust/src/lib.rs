pub mod dna_generator;
pub mod audit_wrapper;
pub mod validator;

use serde_json::Value;
use chrono::Utc;

pub struct LongHunAdapter {
    pub uid: String,
    pub device: String,
    pub locale: String,
}

impl LongHunAdapter {
    pub fn new(uid: &str, device: &str, locale: &str) -> Self {
        LongHunAdapter {
            uid: if uid.is_empty() { "9622".into() } else { uid.into() },
            device: if device.is_empty() { "HM-9622-001".into() } else { device.into() },
            locale: if locale.is_empty() { "Asia/Shanghai".into() } else { locale.into() },
        }
    }

    pub fn wrap(&self, data: Value, task_type: &str, persona: &str, action: &str, version: &str) -> Value {
        let task_type = if task_type.is_empty() { "default" } else { task_type };
        let persona = if persona.is_empty() { "P04" } else { persona };
        let action = if action.is_empty() { "WRAP" } else { action };
        let version = if version.is_empty() { "V1.0" } else { version };
        let dna = dna_generator::generate_dna(&self.uid, &self.device, task_type, action, version);
        let audit = audit_wrapper::wrap_audit(&self.uid, &data, task_type, persona);
        serde_json::json!({
            "dna":dna,"audit":audit,"payload":data,"meta":{
                "adapter_version":"1.0.0","uid":self.uid,"device":self.device,
                "task_type":task_type,"persona":persona,
                "generated_at":Utc::now().to_rfc3339(),"format":"longhun-v∞"
            }
        })
    }

    pub fn validate(&self, wrapped: &Value) -> Value {
        let mut v = validator::Validator::new();
        v.validate(wrapped)
    }

    pub fn get_schemas(&self) -> (Value, Value) {
        let dna = serde_json::json!({"type":"string","description":"v∞ DNA traceability code"});
        let audit = serde_json::json!({"type":"object","required":["audit_version","uid","behavior_signature","behavior_pattern","behavior_labels","color"]});
        (dna, audit)
    }
}
