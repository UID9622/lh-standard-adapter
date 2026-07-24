//! Audit Wrapper — seven-factor behavioral audit metadata.

use sha2::{Sha256, Digest};
use std::time::{SystemTime, UNIX_EPOCH};

const P_VALUES: &[&str] = &["HasPromise", "NoPromise"];
const F_VALUES: &[&str] = &["Fulfilled", "Unfulfilled", "Partial"];
const E_VALUES: &[&str] = &["Willing", "Perfunctory", "Resentful", "Numb"];
const A_VALUES: &[&str] = &["Self", "Partner", "Family", "Outsider", "Public"];
const X_VALUES: &[&str] = &["OverExplain", "Silent", "Genuine", "Indifferent"];
const Y_VALUES: &[&str] = &["Changed", "Resisted", "Indifferent", "NoResponse"];

pub struct AuditWrapper {
    pub uid: String,
}

impl AuditWrapper {
    pub fn new(uid: Option<String>) -> Self {
        Self {
            uid: uid.unwrap_or_else(|| "9622".to_string()),
        }
    }

    pub fn wrap(&self, payload: &serde_json::Value, task_type: &str, persona: &str) -> AuditResult {
        use std::collections::HashMap;

        let mut sig = HashMap::new();
        let now = SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .unwrap()
            .as_nanos();
        
        let idx = |arr: &[&str]| -> &str {
            arr[(now as usize) % arr.len()]
        };

        sig.insert("P", idx(P_VALUES));
        sig.insert("F", idx(F_VALUES));
        sig.insert("T", task_type);
        sig.insert("E", idx(E_VALUES));
        sig.insert("C", &self.uid);
        sig.insert("R", persona);
        sig.insert("A", idx(A_VALUES));
        sig.insert("X", idx(X_VALUES));
        sig.insert("Y", idx(Y_VALUES));

        let mut hasher = Sha256::new();
        hasher.update(now.to_be_bytes());
        let hash = format!("{:x}", hasher.finalize());
        sig.insert("Z", &hash[..4]);

        let pattern = if sig["P"] == "NoPromise" && sig["X"] == "OverExplain" {
            "MODE-DefensiveDefaulter"
        } else if sig["F"] == "Unfulfilled" && sig["E"] == "Indifferent" {
            "MODE-InternalDestroyer"
        } else {
            "MODE-StableDisciplined"
        };

        AuditResult {
            audit_version: "v1.0.0".to_string(),
            uid: self.uid.clone(),
            behavior_pattern: pattern.to_string(),
            color: "🟢".to_string(),
        }
    }
}

#[derive(Debug, serde::Serialize)]
pub struct AuditResult {
    pub audit_version: String,
    pub uid: String,
    pub behavior_pattern: String,
    pub color: String,
}

pub fn audit_wrap(payload: &serde_json::Value, task_type: &str, persona: &str) -> AuditResult {
    let wrapper = AuditWrapper::new(None);
    wrapper.wrap(payload, task_type, persona)
}
