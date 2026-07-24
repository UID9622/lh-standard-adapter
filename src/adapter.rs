//! LongHun Standard Adapter — main adapter module.

use crate::dna::DNAGenerator;
use crate::audit::AuditWrapper;

pub struct LongHunAdapter {
    dna_gen: DNAGenerator,
    audit: AuditWrapper,
}

impl LongHunAdapter {
    pub fn new(uid: Option<String>, device: Option<String>) -> Self {
        Self {
            dna_gen: DNAGenerator::new(uid.clone(), device.clone(), None),
            audit: AuditWrapper::new(uid),
        }
    }

    pub fn wrap(&self, data: serde_json::Value, task_type: &str, persona: &str) -> WrapResult {
        let dna = self.dna_gen.generate(task_type, "WRAP", None);
        let audit = self.audit.wrap(&data, task_type, persona);

        WrapResult {
            dna,
            audit,
            payload: data,
        }
    }
}

#[derive(Debug, serde::Serialize)]
pub struct WrapResult {
    pub dna: crate::dna::DNAResult,
    pub audit: crate::audit::AuditResult,
    pub payload: serde_json::Value,
}

pub fn wrap(data: serde_json::Value, task_type: &str) -> WrapResult {
    let adapter = LongHunAdapter::new(None, None);
    adapter.wrap(data, task_type, "P04")
}
