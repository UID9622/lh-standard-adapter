//! LongHun Standard Adapter v1.0.0
//!
//! DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-ADAPTER-v1.0.0
//! Author: LongHun Core · UID9622 · 龍芯北辰
//! License: CC BY-NC-SA 4.0
//!
//! Open the standard. Guard the engine.
//!
//! This adapter is an open-source shell tool. It wraps JSON payloads
//! with DNA traceability and seven-factor behavioral audit metadata.
//! Core compiler, training scripts, and algorithm logic are protected
//! Chinese independent intellectual property.

pub mod dna_generator;
pub mod audit_wrapper;
pub mod validator;

pub use dna_generator::DNAGenerator;
pub use dna_generator::generate_dna;
pub use audit_wrapper::AuditWrapper;
pub use validator::Validator;
pub use validator::quick_validate;

pub const VERSION: &str = "1.0.0";
pub const AUTHOR: &str = "LongHun Core · UID9622 · 龍芯北辰";
pub const LICENSE: &str = "CC BY-NC-SA 4.0";
pub const DNA: &str = "#LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-ADAPTER-v1.0.0-4f7a3b1c";

use std::collections::HashMap;

/// LongHun Adapter — wrap JSON payloads with DNA traceability
/// and seven-factor behavioral audit metadata.
pub struct LongHunAdapter {
    pub uid: String,
    pub device: String,
    pub locale: String,
    dna_gen: DNAGenerator,
    audit: AuditWrapper,
    validator: Validator,
}

impl LongHunAdapter {
    pub fn new(uid: &str, device: &str, locale: &str) -> Self {
        Self {
            uid: uid.to_string(),
            device: device.to_string(),
            locale: locale.to_string(),
            dna_gen: DNAGenerator::new(uid, device, locale),
            audit: AuditWrapper::new(uid),
            validator: Validator::new(),
        }
    }

    pub fn wrap(
        &self,
        data: serde_json::Value,
        task_type: &str,
        persona: &str,
        action: &str,
        version: Option<&str>,
    ) -> HashMap<String, serde_json::Value> {
        let dna = self.dna_gen.generate(task_type, action, version);
        let audit = self.audit.wrap(&data, task_type, persona);
        let now = dna_generator::chrono_now_iso();

        let mut meta = HashMap::new();
        meta.insert("adapter_version".to_string(), serde_json::Value::String(VERSION.to_string()));
        meta.insert("uid".to_string(), serde_json::Value::String(self.uid.clone()));
        meta.insert("device".to_string(), serde_json::Value::String(self.device.clone()));
        meta.insert("task_type".to_string(), serde_json::Value::String(task_type.to_string()));
        meta.insert("persona".to_string(), serde_json::Value::String(persona.to_string()));
        meta.insert("generated_at".to_string(), serde_json::Value::String(now));
        meta.insert("format".to_string(), serde_json::Value::String("longhun-v∞".to_string()));

        let mut result = HashMap::new();
        result.insert("dna".to_string(), serde_json::Value::String(dna));
        result.insert("audit".to_string(), serde_json::Value::Object(audit.into_iter().collect()));
        result.insert("payload".to_string(), data);
        result.insert("meta".to_string(), serde_json::Value::Object(meta.into_iter().collect()));
        result
    }

    pub fn validate(&mut self, wrapped: &serde_json::Value) -> serde_json::Value {
        self.validator.validate(wrapped)
    }
}

impl Default for LongHunAdapter {
    fn default() -> Self {
        Self::new("9622", "HM-9622-001", "Asia/Shanghai")
    }
}
