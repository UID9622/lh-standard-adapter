use serde_json::{json, Value};
use std::collections::HashSet;

fn dna_matches(s: &str) -> bool {
    let prefix = "#LongHun\u{26a1}\u{fe0f}";
    if !s.starts_with(prefix) { return false; }
    let rest = &s[prefix.len()..];
    let parts: Vec<&str> = rest.split('\u{b7}').collect();
    if parts.len() != 5 { return false; }
    for i in 0..4 {
        if parts[i].len() < 2 || !parts[i].chars().next().unwrap().is_uppercase() {
            return false;
        }
    }
    let hx = parts[4];
    let chars: Vec<char> = hx.chars().collect();
    if chars.is_empty() { return false; }
    let c = chars[0] as u32;
    if !(0x4D00..=0x4DFF).contains(&c) { return false; }
    if let Some(last_dash) = hx.rfind('-') {
        let hash = &hx[last_dash + 1..];
        if hash.len() == 8 && hash.chars().all(|c| c.is_ascii_hexdigit()) {
            return true;
        }
    }
    false
}

const REQUIRED_TOP_KEYS: &[&str] = &["dna", "audit", "payload", "meta"];
const REQUIRED_AUDIT_KEYS: &[&str] = &[
    "audit_version", "uid", "behavior_signature",
    "behavior_pattern", "behavior_labels", "color",
];
const REQUIRED_SIG_KEYS: &[&str] = &["P", "F", "T", "E", "C", "R", "A", "X", "Y", "Z"];
const VALID_COLORS: &[&str] = &["\u{1f7e2}", "\u{1f7e1}", "\u{1f534}"];
const VALID_PATTERNS: &[&str] = &[
    "MODE-DefensiveDefaulter", "MODE-ExternalTrustSpender",
    "MODE-InternalDestroyer", "MODE-Fluctuating", "MODE-StableDisciplined",
];
const VALID_P_VALUES: &[&str] = &["HasPromise", "NoPromise"];
const VALID_F_VALUES: &[&str] = &["Fulfilled", "Unfulfilled", "Partial"];
const VALID_E_VALUES: &[&str] = &["Willing", "Perfunctory", "Resentful", "Numb"];
const VALID_A_VALUES: &[&str] = &["Self", "Partner", "Family", "Outsider", "Public"];
const VALID_X_VALUES: &[&str] = &["OverExplain", "Silent", "Genuine", "Indifferent"];
const VALID_Y_VALUES: &[&str] = &["Changed", "Resisted", "Indifferent", "NoResponse"];

pub struct Validator {
    pub errors: Vec<String>,
    pub warnings: Vec<String>,
}

impl Validator {
    pub fn new() -> Self {
        Self { errors: Vec::new(), warnings: Vec::new() }
    }

    pub fn validate(&mut self, wrapped: &Value) -> Value {
        self.errors.clear();
        self.warnings.clear();

        if !wrapped.is_object() || wrapped.as_object().map_or(true, |o| o.is_empty()) {
            self.errors.push("Input is not a non-empty object".to_string());
            return self._result();
        }

        let obj = wrapped.as_object().unwrap();
        let keys: HashSet<&str> = obj.keys().map(|k| k.as_str()).collect();

        for k in REQUIRED_TOP_KEYS {
            if !keys.contains(k) {
                self.errors.push(format!("Missing top-level key: {}", k));
            }
        }

        if let Some(dna_val) = obj.get("dna") {
            if let Some(dna) = dna_val.as_str() {
                if dna.is_empty() {
                    self.errors.push("DNA field is empty".to_string());
                } else if !dna_matches(dna) {
                    let short = if dna.len() > 60 { &dna[..60] } else { dna };
                    self.errors.push(format!("DNA does not match pattern: {}...", short));
                }
            } else {
                self.errors.push("DNA is not a string".to_string());
            }
        }

        if let Some(audit_val) = obj.get("audit") {
            if let Some(audit_obj) = audit_val.as_object() {
                self._validate_audit(audit_obj);
                if let Some(meta_val) = obj.get("meta") {
                    if let Some(meta_obj) = meta_val.as_object() {
                        let meta_uid = meta_obj.get("uid").and_then(|v| v.as_str()).unwrap_or("");
                        let audit_uid = audit_obj.get("uid").and_then(|v| v.as_str()).unwrap_or("");
                        if !meta_uid.is_empty() && !audit_uid.is_empty() {
                            let audit_clean = audit_uid.trim_start_matches("UID");
                            if meta_uid != audit_clean {
                                self.errors.push(format!(
                                    "UID mismatch: meta.uid={}, audit.uid={}", meta_uid, audit_uid
                                ));
                            }
                        }
                    }
                }
            } else {
                self.errors.push("Audit is not an object".to_string());
            }
        }

        self._result()
    }

    fn _validate_audit(&mut self, audit: &serde_json::Map<String, Value>) {
        let keys: HashSet<&str> = audit.keys().map(|k| k.as_str()).collect();
        for k in REQUIRED_AUDIT_KEYS {
            if !keys.contains(k) {
                self.errors.push(format!("Missing audit key: {}", k));
            }
        }

        if let Some(sig_val) = audit.get("behavior_signature") {
            if let Some(sig_obj) = sig_val.as_object() {
                let sig_keys: HashSet<&str> = sig_obj.keys().map(|k| k.as_str()).collect();
                for k in REQUIRED_SIG_KEYS {
                    if !sig_keys.contains(k) {
                        self.errors.push(format!("Missing signature key: {}", k));
                    }
                }
                self._validate_sig_values(sig_obj);
            } else {
                self.errors.push("behavior_signature is not an object".to_string());
            }
        }

        if let Some(p_val) = audit.get("behavior_pattern") {
            if let Some(p) = p_val.as_str() {
                if !VALID_PATTERNS.contains(&p) {
                    self.warnings.push(format!("Unknown behavior pattern: {}", p));
                }
            }
        }

        if let Some(c_val) = audit.get("color") {
            if let Some(c) = c_val.as_str() {
                if !VALID_COLORS.contains(&c) {
                    self.warnings.push(format!("Unknown audit color: {}", c));
                }
            }
        }

        if let Some(ph_val) = audit.get("payload_hash") {
            if let Some(ph) = ph_val.as_str() {
                if ph.len() != 16 || !ph.chars().all(|c| c.is_ascii_hexdigit()) {
                    self.warnings.push(format!("Suspicious payload_hash: {}", ph));
                }
            }
        }
    }

    fn _validate_sig_values(&mut self, sig: &serde_json::Map<String, Value>) {
        let checks: Vec<(&str, fn(&Value) -> bool)> = vec![
            ("P", |v| v.as_str().map_or(false, |s| VALID_P_VALUES.contains(&s))),
            ("F", |v| v.as_str().map_or(false, |s| VALID_F_VALUES.contains(&s))),
            ("T", |v| v.is_number()),
            ("E", |v| v.as_str().map_or(false, |s| VALID_E_VALUES.contains(&s))),
            ("C", |v| v.is_number()),
            ("R", |v| v.as_i64().map_or(false, |n| n >= 0)),
            ("A", |v| v.as_str().map_or(false, |s| VALID_A_VALUES.contains(&s))),
            ("X", |v| v.as_str().map_or(false, |s| VALID_X_VALUES.contains(&s))),
            ("Y", |v| v.as_str().map_or(false, |s| VALID_Y_VALUES.contains(&s))),
            ("Z", |v| v.is_number()),
        ];
        for (label, check) in &checks {
            if let Some(val) = sig.get(*label) {
                if !check(val) {
                    self.warnings.push(format!("Invalid {}: {:?}", label, val));
                }
            }
        }
    }

    fn _result(&self) -> Value {
        let valid = self.errors.is_empty();
        let summary = if valid {
            if self.warnings.is_empty() {
                "\u{2705} VALID — 0 warnings".to_string()
            } else {
                format!("\u{2705} VALID — {} warning(s) ({})", self.warnings.len(), self.warnings[0])
            }
        } else {
            format!("\u{274c} INVALID — {} error(s)", self.errors.len())
        };
        json!({ "valid": valid, "errors": self.errors, "warnings": self.warnings, "summary": summary })
    }
}

pub fn quick_validate(wrapped: &Value) -> bool {
    if !wrapped.is_object() { return false; }
    let obj = wrapped.as_object().unwrap();
    if !obj.contains_key("dna") || !obj.contains_key("audit") { return false; }
    if let Some(dna_val) = obj.get("dna") {
        if let Some(dna) = dna_val.as_str() {
            return dna_matches(dna);
        }
    }
    false
}
