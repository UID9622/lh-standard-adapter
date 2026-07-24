use lh_standard_adapter::*;
use lh_standard_adapter::dna_generator::DNAGenerator;
use lh_standard_adapter::audit_wrapper::AuditWrapper;
use lh_standard_adapter::validator::Validator;

fn make_gen() -> DNAGenerator {
    DNAGenerator::new("9622", "HM-9622-001", "Asia/Shanghai")
}

#[test]
fn test_dna_default() {
    let dna = make_gen().generate("default", "WRAP", None);
    assert!(dna.starts_with("#LongHun"), "Should start with prefix");
    assert!(dna.contains("ADAPTER-DEFAULT-WRAP-V1.0"));
}

#[test]
fn test_dna_code() {
    let dna = make_gen().generate("code", "GENERATE", Some("v2.0"));
    assert!(dna.contains("ADAPTER-CODE-GENERATE-v2.0"));
}

#[test]
fn test_dna_hash8() {
    let dna = make_gen().generate("default", "WRAP", None);
    let last = dna.split('-').last().unwrap();
    assert_eq!(last.len(), 8);
    assert!(last.chars().all(|c| c.is_ascii_hexdigit()));
}

#[test]
fn test_dna_deploy_hexagram() {
    let dna = make_gen().generate("deploy", "DEPLOY", None);
    assert!(dna.contains("ADAPTER-DEPLOY-DEPLOY-V1.0"));
}

#[test]
fn test_dna_convenience() {
    assert!(generate_dna("audit", "WRAP", None).starts_with("#LongHun"));
}

#[test]
fn test_audit_wrap() {
    let w = AuditWrapper::new("9622");
    let a = w.wrap(&serde_json::json!({"code":"test"}), "code", "P04");
    assert_eq!(a["audit_version"], "v1.0");
    assert_eq!(a["uid"], "UID9622");
    assert!(a.contains_key("behavior_signature"));
    assert!(a.contains_key("behavior_pattern"));
    assert!(a.contains_key("color"));
    assert!(a.contains_key("payload_hash"));
}

#[test]
fn test_audit_signature() {
    let a = AuditWrapper::new("9622").wrap(&serde_json::json!({}), "default", "P04");
    let sig = a["behavior_signature"].as_object().unwrap();
    assert_eq!(sig["P"], "HasPromise");
    assert_eq!(sig["F"], "Fulfilled");
    assert_eq!(sig["E"], "Willing");
    assert_eq!(sig["Z"], 1.0);
}

#[test]
fn test_audit_pattern() {
    let a = AuditWrapper::new("9622").wrap(&serde_json::json!({}), "default", "P04");
    assert_eq!(a["behavior_pattern"], "MODE-StableDisciplined");
}

#[test]
fn test_audit_hash() {
    let a = AuditWrapper::new("9622").wrap(&serde_json::json!({"x":1}), "default", "P04");
    let h = a["payload_hash"].as_str().unwrap();
    assert_eq!(h.len(), 16);
    assert!(h.chars().all(|c| c.is_ascii_hexdigit()));
}

#[test]
fn test_audit_labels() {
    let a = AuditWrapper::new("9622").wrap(&serde_json::json!({}), "default", "P04");
    assert!(!a["behavior_labels"].as_array().unwrap().is_empty());
}

#[test]
fn test_adapter_wrap() {
    let a = LongHunAdapter::new("9622", "HM-9622-001", "Asia/Shanghai");
    let r = a.wrap(serde_json::json!({"msg":"hi"}), "default", "P04", "WRAP", None);
    assert!(r.contains_key("dna"));
    assert!(r.contains_key("audit"));
    assert!(r.contains_key("payload"));
    assert!(r.contains_key("meta"));
}

#[test]
fn test_adapter_default() {
    assert_eq!(LongHunAdapter::default().uid, "9622");
}

#[test]
fn test_validate_valid() {
    let mut a = LongHunAdapter::new("9622", "HM-9622-001", "Asia/Shanghai");
    let r = a.wrap(serde_json::json!({"code":"test"}), "code", "P04", "WRAP", None);
    let v = a.validate(&serde_json::json!(r));
    assert!(v["valid"].as_bool().unwrap());
}

#[test]
fn test_validate_empty() {
    assert!(!Validator::new().validate(&serde_json::json!({}))["valid"].as_bool().unwrap());
}

#[test]
fn test_validate_uid_mismatch() {
    let dna = make_gen().generate("code", "WRAP", None);
    let mut w = serde_json::Map::new();
    w.insert("dna".into(), serde_json::json!(dna));
    let mut au = serde_json::Map::new();
    au.insert("audit_version".into(), serde_json::json!("v1.0"));
    au.insert("uid".into(), serde_json::json!("UID1234"));
    let mut sig = serde_json::Map::new();
    for (k, v) in [("P","HasPromise"),("F","Fulfilled"),("E","Willing"),("A","Self"),("X","Genuine"),("Y","NoResponse")] {
        sig.insert(k.into(), serde_json::json!(v));
    }
    sig.insert("T".into(), serde_json::json!(0.0));
    sig.insert("C".into(), serde_json::json!(0));
    sig.insert("R".into(), serde_json::json!(0));
    sig.insert("Z".into(), serde_json::json!(1.0));
    au.insert("behavior_signature".into(), serde_json::Value::Object(sig));
    au.insert("behavior_pattern".into(), serde_json::json!("MODE-StableDisciplined"));
    au.insert("behavior_labels".into(), serde_json::json!([]));
    au.insert("color".into(), serde_json::json!("\u{1f7e2}"));
    w.insert("audit".into(), serde_json::Value::Object(au));
    w.insert("payload".into(), serde_json::json!({}));
    let mut m = serde_json::Map::new();
    m.insert("uid".into(), serde_json::json!("9622"));
    w.insert("meta".into(), serde_json::Value::Object(m));
    let v = Validator::new().validate(&serde_json::Value::Object(w));
    assert!(!v["valid"].as_bool().unwrap());
}

#[test]
fn test_quick_validate() {
    let mut a = LongHunAdapter::new("9622", "HM-9622-001", "Asia/Shanghai");
    let r = a.wrap(serde_json::json!({"a":1}), "default", "P04", "WRAP", None);
    assert!(quick_validate(&serde_json::json!(r)));
    assert!(!quick_validate(&serde_json::json!({})));
}

#[test]
fn test_cross_validation() {
    let dna = make_gen().generate("code", "WRAP", None);
    let mut audit = serde_json::Map::new();
    audit.insert("audit_version".into(), serde_json::json!("v1.0"));
    audit.insert("uid".into(), serde_json::json!("UID9622"));
    audit.insert("persona".into(), serde_json::json!("P04"));
    audit.insert("task_type".into(), serde_json::json!("code"));
    let mut sig = serde_json::Map::new();
    for (k, v) in [("P","HasPromise"),("F","Fulfilled"),("E","Willing"),("A","Self"),("X","Genuine"),("Y","NoResponse")] {
        sig.insert(k.into(), serde_json::json!(v));
    }
    sig.insert("T".into(), serde_json::json!(0.0));
    sig.insert("C".into(), serde_json::json!(0));
    sig.insert("R".into(), serde_json::json!(0));
    sig.insert("Z".into(), serde_json::json!(1.0));
    audit.insert("behavior_signature".into(), serde_json::Value::Object(sig));
    audit.insert("behavior_pattern".into(), serde_json::json!("MODE-StableDisciplined"));
    audit.insert("behavior_labels".into(), serde_json::json!(["7F-P-\u{6709}\u{627f}\u{8bfa}"]));
    audit.insert("color".into(), serde_json::json!("\u{1f7e2}"));
    audit.insert("timestamp".into(), serde_json::json!("2026-07-24T13:00:00+08:00"));
    audit.insert("payload_hash".into(), serde_json::json!("a1b2c3d4e5f67890"));
    let mut meta = serde_json::Map::new();
    meta.insert("adapter_version".into(), serde_json::json!("1.0.0"));
    meta.insert("uid".into(), serde_json::json!("9622"));
    meta.insert("device".into(), serde_json::json!("HM-9622-001"));
    meta.insert("task_type".into(), serde_json::json!("code"));
    meta.insert("persona".into(), serde_json::json!("P04"));
    meta.insert("generated_at".into(), serde_json::json!("2026-07-24T13:00:00+08:00"));
    meta.insert("format".into(), serde_json::json!("longhun-v\u{221e}"));
    let mut wrapped = serde_json::Map::new();
    wrapped.insert("dna".into(), serde_json::json!(dna));
    wrapped.insert("audit".into(), serde_json::Value::Object(audit));
    wrapped.insert("payload".into(), serde_json::json!({"code":"test"}));
    wrapped.insert("meta".into(), serde_json::Value::Object(meta));
    let v = Validator::new().validate(&serde_json::Value::Object(wrapped));
    assert!(v["valid"].as_bool().unwrap(), "Cross-validation should pass");
}
