use sha2::{Sha256, Digest};
use serde_json::{Value, json};
use std::collections::HashMap;

// --- Seven-Factor Value Sets ---

const P_VALUES: &[&str] = &["HasPromise", "NoPromise"];
const F_VALUES: &[&str] = &["Fulfilled", "Unfulfilled", "Partial"];
const E_VALUES: &[&str] = &["Willing", "Perfunctory", "Resentful", "Numb"];
const A_VALUES: &[&str] = &["Self", "Partner", "Family", "Outsider", "Public"];
const X_VALUES: &[&str] = &["OverExplain", "Silent", "Genuine", "Indifferent"];
const Y_VALUES: &[&str] = &["Changed", "Resisted", "Indifferent", "NoResponse"];

// --- Behavior Patterns ---

const PATTERNS: &[&str] = &[
    "MODE-DefensiveDefaulter",
    "MODE-ExternalTrustSpender",
    "MODE-InternalDestroyer",
    "MODE-Fluctuating",
    "MODE-StableDisciplined",
];

// --- Factor to Label Mapping (bilingual) ---

fn get_label(factor: &str, value: &str) -> Option<&'static str> {
    match factor {
        "P" => match value {
            "HasPromise" => Some("7F-P-有承诺"),
            "NoPromise" => Some("7F-P-无承诺"),
            _ => None,
        },
        "F" => match value {
            "Fulfilled" => Some("7F-F-已兑现"),
            "Unfulfilled" => Some("7F-F-未兑现"),
            "Partial" => Some("7F-F-部分兑现"),
            _ => None,
        },
        "E" => match value {
            "Willing" => Some("7F-E-心甘情愿"),
            "Perfunctory" => Some("7F-E-敷衍"),
            "Resentful" => Some("7F-E-怨恨"),
            "Numb" => Some("7F-E-麻木"),
            _ => None,
        },
        "A" => match value {
            "Self" => Some("7F-A-自己"),
            "Partner" => Some("7F-A-伴侣"),
            "Family" => Some("7F-A-家庭"),
            "Outsider" => Some("7F-A-外人"),
            "Public" => Some("7F-A-公众"),
            _ => None,
        },
        "X" => match value {
            "OverExplain" => Some("7F-X-过度解释"),
            "Silent" => Some("7F-X-沉默"),
            "Genuine" => Some("7F-X-真诚"),
            "Indifferent" => Some("7F-X-冷漠"),
            _ => None,
        },
        "Y" => match value {
            "Changed" => Some("7F-Y-改正"),
            "Resisted" => Some("7F-Y-抗拒"),
            "Indifferent" => Some("7F-Y-无视"),
            "NoResponse" => Some("7F-Y-无响应"),
            _ => None,
        },
        _ => None,
    }
}

pub struct AuditWrapper {
    pub uid: String,
}

impl AuditWrapper {
    pub fn new(uid: &str) -> Self {
        Self {
            uid: uid.to_string(),
        }
    }

    pub fn wrap(&self, payload: &Value, task_type: &str, persona: &str) -> HashMap<String, Value> {
        use chrono::{DateTime, FixedOffset};

        let tz = FixedOffset::east_opt(8 * 3600).unwrap();
        let now: DateTime<FixedOffset> = chrono::Utc::now().with_timezone(&tz);

        // Default signature (StableDisciplined baseline)
        let mut signature = HashMap::new();
        signature.insert("P".to_string(), json!("HasPromise"));
        signature.insert("F".to_string(), json!("Fulfilled"));
        signature.insert("T".to_string(), json!(0.0));
        signature.insert("E".to_string(), json!("Willing"));
        signature.insert("C".to_string(), json!(0));
        signature.insert("R".to_string(), json!(0));
        signature.insert("A".to_string(), json!("Self"));
        signature.insert("X".to_string(), json!("Genuine"));
        signature.insert("Y".to_string(), json!("NoResponse"));
        signature.insert("Z".to_string(), json!(1.0));

        let pattern = self._classify(&signature);
        let labels = self._make_labels(&signature, &pattern);
        let color = self._determine_color(&pattern, 0);

        // Payload hash
        let payload_json = serde_json::to_string(payload).unwrap_or_default();
        let mut hasher = Sha256::new();
        hasher.update(payload_json.as_bytes());
        let result = hasher.finalize();
        let payload_hash = format!("{:02x}{:02x}{:02x}{:02x}{:02x}{:02x}{:02x}{:02x}",
            result[0], result[1], result[2], result[3],
            result[4], result[5], result[6], result[7]);

        let mut audit = HashMap::new();
        audit.insert("audit_version".to_string(), json!("v1.0"));
        audit.insert("uid".to_string(), json!(format!("UID{}", self.uid)));
        audit.insert("persona".to_string(), json!(persona));
        audit.insert("task_type".to_string(), json!(task_type));
        audit.insert("behavior_signature".to_string(), Value::Object(signature.into_iter().collect()));
        audit.insert("behavior_pattern".to_string(), json!(pattern));
        audit.insert("behavior_labels".to_string(), json!(labels));
        audit.insert("color".to_string(), json!(color));
        audit.insert("timestamp".to_string(), json!(now.to_rfc3339()));
        audit.insert("payload_hash".to_string(), json!(payload_hash));

        audit
    }

    fn _classify(&self, sig: &HashMap<String, Value>) -> String {
        let f_val = sig.get("F").and_then(|v| v.as_str()).unwrap_or("");
        let x_val = sig.get("X").and_then(|v| v.as_str()).unwrap_or("");
        let a_val = sig.get("A").and_then(|v| v.as_str()).unwrap_or("");
        let y_val = sig.get("Y").and_then(|v| v.as_str()).unwrap_or("");
        let z_val = sig.get("Z").and_then(|v| v.as_f64()).unwrap_or(1.0);

        if f_val == "Unfulfilled" && x_val == "OverExplain" {
            return "MODE-DefensiveDefaulter".to_string();
        }
        if f_val == "Fulfilled" && a_val == "Outsider" {
            return "MODE-ExternalTrustSpender".to_string();
        }
        if f_val == "Unfulfilled" && y_val == "Indifferent" {
            return "MODE-InternalDestroyer".to_string();
        }
        if z_val > 2.0 {
            return "MODE-Fluctuating".to_string();
        }
        "MODE-StableDisciplined".to_string()
    }

    fn _make_labels(&self, sig: &HashMap<String, Value>, pattern: &str) -> Vec<String> {
        let mut labels = Vec::new();
        for factor in &["P", "F", "E", "A", "X", "Y"] {
            if let Some(val) = sig.get(*factor).and_then(|v| v.as_str()) {
                if let Some(label) = get_label(factor, val) {
                    labels.push(label.to_string());
                }
            }
        }
        labels.push(pattern.to_string());
        labels
    }

    fn _determine_color(&self, pattern: &str, repeat: i64) -> String {
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

pub fn audit_wrap(payload: &Value, task_type: &str, persona: &str) -> HashMap<String, Value> {
    let wrapper = AuditWrapper::new("9622");
    wrapper.wrap(payload, task_type, persona)
}
