pub mod audit;
pub mod dna;
pub mod validator;

use audit::{AuditResult, AuditWrapper};
use dna::DNAGenerator;
use serde_json::{json, Value};
use validator::{ValidationResult, Validator};

pub struct LongHunAdapter {
    uid: String,
    device: String,
    dna_generator: DNAGenerator,
    audit_wrapper: AuditWrapper,
    validator: Validator,
}

impl LongHunAdapter {
    pub fn new(uid: &str, device: &str) -> Self {
        let u = if uid.is_empty() { "9622" } else { uid };
        let d = if device.is_empty() { "HM-9622-001" } else { device };

        Self {
            uid: u.to_string(),
            device: d.to_string(),
            dna_generator: DNAGenerator::new(u, d),
            audit_wrapper: AuditWrapper::new(u),
            validator: Validator::new(),
        }
    }

    pub fn wrap(&self, data: Value, task_type: &str, persona: &str) -> Result<Value, String> {
        let dna = self.dna_generator.generate(task_type, "WRAP", "V1.0");
        let audit = self.audit_wrapper.wrap(&data, task_type, persona);

        let wrapped = json!({
            "dna": dna,
            "audit": audit,
            "payload": data,
            "meta": {
                "uid": self.uid,
                "device": self.device,
                "version": "V1.0"
            }
        });

        Ok(wrapped)
    }

    pub fn validate(&self, wrapped: &Value) -> ValidationResult {
        self.validator.validate(wrapped)
    }

    pub fn get_schemas(&self) -> (Value, Value) {
        let dna_schema = json!({
            "type": "string",
            "pattern": "^#LongHun⚡️.*"
        });

        let audit_schema = json!({
            "type": "object",
            "required": ["audit_version", "uid", "behavior_signature", "behavior_pattern", "behavior_labels", "color"]
        });

        (dna_schema, audit_schema)
    }
}
