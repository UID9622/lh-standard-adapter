use chrono::{FixedOffset, Utc};
use serde::{Deserialize, Serialize};
use serde_json::Value;
use sha2::{Digest, Sha256};
use std::collections::HashMap;

#[derive(Serialize, Deserialize, Clone, Debug)]
pub struct BehaviorSignature {
    pub P: String,
    pub F: String,
    pub T: f64,
    pub E: String,
    pub C: i32,
    pub R: i32,
    pub A: String,
    pub X: String,
    pub Y: String,
    pub Z: f64,
}

#[derive(Serialize, Deserialize, Debug)]
pub struct AuditResult {
    pub audit_version: String,
    pub uid: String,
    pub persona: String,
    pub task_type: String,
    pub behavior_signature: BehaviorSignature,
    pub behavior_pattern: String,
    pub behavior_labels: Vec<String>,
    pub color: String,
    pub timestamp: String,
    pub payload_hash: String,
}

pub struct AuditWrapper {
    pub uid: String,
}

impl AuditWrapper {
    pub fn new(uid: &str) -> Self {
        Self {
            uid: if uid.is_empty() { "9622".to_string() } else { uid.to_string() },
        }
    }

    pub fn wrap(&self, payload: &Value, task_type: &str, persona: &str) -> AuditResult {
        let task = if task_type.is_empty() { "default" } else { task_type };
        let pers = if persona.is_empty() { "P04" } else { persona };

        let offset = FixedOffset::east_opt(8 * 3600).unwrap();
        let now = Utc::now().with_timezone(&offset);

        let sig = BehaviorSignature {
            P: "HasPromise".to_string(),
            F: "Fulfilled".to_string(),
            T: 0.0,
            E: "Willing".to_string(),
            C: 0,
            R: 0,
            A: "Self".to_string(),
            X: "Genuine".to_string(),
            Y: "NoResponse".to_string(),
            Z: 1.0,
        };

        let pattern = self.classify(&sig);
        let labels = self.make_labels(&sig, &pattern);
        let color = self.determine_color(&pattern, sig.R);

        let payload_str = payload.to_string();
        let mut hasher = Sha256::new();
        hasher.update(payload_str.as_bytes());
        let hash = hasher.finalize();
        let payload_hash = format!("{:x}", hash)[..16].to_string();

        AuditResult {
            audit_version: "v1.0".to_string(),
            uid: format!("UID{}", self.uid),
            persona: pers.to_string(),
            task_type: task.to_string(),
            behavior_signature: sig,
            behavior_pattern: pattern,
            behavior_labels: labels,
            color,
            timestamp: now.to_rfc3339(),
            payload_hash,
        }
    }

    fn classify(&self, sig: &BehaviorSignature) -> String {
        if sig.F == "Unfulfilled" && sig.X == "OverExplain" {
            return "MODE-DefensiveDefaulter".to_string();
        }
        if sig.F == "Fulfilled" && sig.A == "Outsider" {
            return "MODE-ExternalTrustSpender".to_string();
        }
        if sig.F == "Unfulfilled" && sig.Y == "Indifferent" {
            return "MODE-InternalDestroyer".to_string();
        }
        if sig.Z > 2.0 {
            return "MODE-Fluctuating".to_string();
        }
        "MODE-StableDisciplined".to_string()
    }

    fn make_labels(&self, sig: &BehaviorSignature, pattern: &str) -> Vec<String> {
        let mut map = HashMap::new();
        map.insert("HasPromise", "7F-P-有承诺");
        map.insert("Fulfilled", "7F-F-已兑现");
        map.insert("Willing", "7F-E-心甘情愿");
        map.insert("Self", "7F-A-自己");
        map.insert("Genuine", "7F-X-真诚");
        map.insert("NoResponse", "7F-Y-无响应");

        let mut labels = Vec::new();
        for val in [&sig.P, &sig.F, &sig.E, &sig.A, &sig.X, &sig.Y] {
            if let Some(&l) = map.get(val.as_str()) {
                labels.push(l.to_string());
            }
        }
        labels.push(pattern.to_string());
        labels
    }

    fn determine_color(&self, pattern: &str, repeat: i32) -> String {
        if pattern == "MODE-InternalDestroyer" {
            return "🔴".to_string();
        }
        if pattern == "MODE-Fluctuating" && repeat > 3 {
            return "🟡".to_string();
        }
        if pattern == "MODE-DefensiveDefaulter" && repeat > 2 {
            return "🟡".to_string();
        }
        "🟢".to_string()
    }
}
