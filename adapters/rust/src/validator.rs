use regex::Regex;
use serde::{Deserialize, Serialize};
use serde_json::Value;

#[derive(Serialize, Deserialize, Debug)]
pub struct ValidationResult {
    pub valid: bool,
    pub errors: Vec<String>,
    pub warnings: Vec<String>,
    pub summary: String,
}

pub struct Validator;

impl Validator {
    pub fn new() -> Self {
        Self
    }

    pub fn validate(&self, wrapped: &Value) -> ValidationResult {
        let mut errors = Vec::new();
        let warnings = Vec::new();

        if !wrapped.is_object() {
            errors.push("Input is not a JSON Object".to_string());
            return self.make_result(errors, warnings);
        }

        let dna = wrapped.get("dna").and_then(|v| v.as_str()).unwrap_or("");
        if dna.is_empty() {
            errors.push("DNA field is empty".to_string());
        } else {
            let re = Regex::new(r"^#LongHun⚡️.*").unwrap();
            if !re.is_match(dna) {
                errors.push(format!("DNA does not match regex: {}", dna));
            }
        }

        if let Some(audit) = wrapped.get("audit") {
            if audit.get("audit_version").is_none() {
                errors.push("Missing audit_version".to_string());
            }
            if audit.get("uid").is_none() {
                errors.push("Missing audit.uid".to_string());
            }
        } else {
            errors.push("Audit object missing".to_string());
        }

        self.make_result(errors, warnings)
    }

    fn make_result(&self, errors: Vec<String>, warnings: Vec<String>) -> ValidationResult {
        let valid = errors.is_empty();
        let summary = if valid {
            format!("✅ VALID — {} warning(s)", warnings.len())
        } else {
            format!("❌ INVALID — {} error(s)", errors.len())
        };

        ValidationResult {
            valid,
            errors,
            warnings,
            summary,
        }
    }
}
