use regex::Regex;
use serde_json::Value;

pub struct Validator { pub errors: Vec<String>, pub warnings: Vec<String> }

impl Validator {
    pub fn new() -> Self { Validator { errors: vec![], warnings: vec![] } }

    pub fn validate(&mut self, wrapped: &Value) -> serde_json::Value {
        self.errors.clear(); self.warnings.clear();
        if !wrapped.is_object() {
            self.errors.push("Not an object".into());
            return self.result();
        }
        let dna_regex = Regex::new(r"^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([\u{4DC0}-\u{4DFF}][A-Za-z]+)-(.+)-([a-f0-9]{8})$").unwrap();

        let required_top = ["dna","audit","payload","meta"];
        for k in &required_top { if wrapped.get(k).is_none() { self.errors.push(format!("Missing top key: {}", k)) } }

        if let Some(dna) = wrapped["dna"].as_str() {
            if dna.is_empty() { self.errors.push("DNA empty".into()) }
            else if !dna_regex.is_match(dna) { self.errors.push(format!("DNA regex fail: {}", &dna[..dna.len().min(60)])) }
        } else { self.errors.push("DNA missing".into()) }

        if let Some(audit) = wrapped["audit"].as_object() {
            let required_audit = ["audit_version","uid","behavior_signature","behavior_pattern","behavior_labels","color"];
            for k in &required_audit { if audit.get(*k).is_none() { self.errors.push(format!("Missing audit key: {}", k)) } }
            if let Some(sig) = audit["behavior_signature"].as_object() {
                let req_sig = ["P","F","T","E","C","R","A","X","Y","Z"];
                for k in &req_sig { if sig.get(*k).is_none() { self.errors.push(format!("Missing sig key: {}", k)) } }
                self.validate_sig_values(sig);
            } else { self.errors.push("sig not object".into()) }

            if let Some(p) = audit["behavior_pattern"].as_str() {
                let valid = ["MODE-DefensiveDefaulter","MODE-ExternalTrustSpender","MODE-InternalDestroyer","MODE-Fluctuating","MODE-StableDisciplined"];
                if !valid.contains(&p) { self.warnings.push(format!("Unknown pattern: {}", p)) }
            }
        } else { self.errors.push("Audit not object".into()) }

        self.result()
    }

    fn validate_sig_values(&mut self, sig: &serde_json::Map<String, Value>) {
        let valid_p: Vec<&str> = vec!["HasPromise","NoPromise"];
        let valid_f: Vec<&str> = vec!["Fulfilled","Unfulfilled","Partial"];
        let valid_e: Vec<&str> = vec!["Willing","Perfunctory","Resentful","Numb"];
        let valid_a: Vec<&str> = vec!["Self","Partner","Family","Outsider","Public"];
        let valid_x: Vec<&str> = vec!["OverExplain","Silent","Genuine","Indifferent"];
        let valid_y: Vec<&str> = vec!["Changed","Resisted","Indifferent","NoResponse"];

        if let Some(v) = sig.get("P").and_then(|x| x.as_str()) { if !valid_p.contains(&v) { self.warnings.push(format!("Invalid P: {}", v)) } }
        if let Some(v) = sig.get("F").and_then(|x| x.as_str()) { if !valid_f.contains(&v) { self.warnings.push(format!("Invalid F: {}", v)) } }
        if let Some(v) = sig.get("E").and_then(|x| x.as_str()) { if !valid_e.contains(&v) { self.warnings.push(format!("Invalid E: {}", v)) } }
        if let Some(v) = sig.get("A").and_then(|x| x.as_str()) { if !valid_a.contains(&v) { self.warnings.push(format!("Invalid A: {}", v)) } }
        if let Some(v) = sig.get("X").and_then(|x| x.as_str()) { if !valid_x.contains(&v) { self.warnings.push(format!("Invalid X: {}", v)) } }
        if let Some(v) = sig.get("Y").and_then(|x| x.as_str()) { if !valid_y.contains(&v) { self.warnings.push(format!("Invalid Y: {}", v)) } }
    }

    fn result(&self) -> serde_json::Value {
        let valid = self.errors.is_empty();
        serde_json::json!({
            "valid":valid,"errors":self.errors,"warnings":self.warnings,
            "summary":if valid { format!("✅ VALID") } else { format!("❌ INVALID") }
        })
    }
}
