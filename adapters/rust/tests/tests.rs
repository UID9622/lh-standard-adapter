use lh_standard_adapter::LongHunAdapter;
use serde_json::json;

#[test]
fn test_dna_generation() {
    let adapter = LongHunAdapter::new("9622","HM-9622-001","");
    let wrapped = adapter.wrap(json!({"test":1}), "code", "P04", "WRAP", "");
    let dna = wrapped["dna"].as_str().unwrap();
    assert!(dna.starts_with("#LongHun⚡️"));
    assert!(dna.len() > 40);
}

#[test]
fn test_audit_wrap() {
    let adapter = LongHunAdapter::new("9622","HM-9622-001","");
    let wrapped = adapter.wrap(json!({"x":1}), "", "", "", "");
    let audit = &wrapped["audit"];
    assert_eq!(audit["audit_version"], "v1.0");
    assert_eq!(audit["uid"], "UID9622");
}

#[test]
fn test_validate_own_wrap() {
    let adapter = LongHunAdapter::new("9622","HM-9622-001","");
    let wrapped = adapter.wrap(json!({}), "", "", "", "");
    let result = adapter.validate(&wrapped);
    assert_eq!(result["valid"], true);
}

#[test]
fn test_invalid_null() {
    let adapter = LongHunAdapter::new("","","");
    let result = adapter.validate(&json!(null));
    assert_eq!(result["valid"], false);
}

#[test]
fn test_missing_dna() {
    let adapter = LongHunAdapter::new("","","");
    let result = adapter.validate(&json!({"audit":{},"payload":{},"meta":{}}));
    assert_eq!(result["valid"], false);
}

#[test]
fn test_preserves_payload() {
    let adapter = LongHunAdapter::new("","","");
    let data = json!({"code":"print('hello')"});
    let wrapped = adapter.wrap(data.clone(), "", "", "", "");
    assert_eq!(wrapped["payload"], data);
}

#[test]
fn test_get_schemas() {
    let adapter = LongHunAdapter::new("","","");
    let (dna, audit) = adapter.get_schemas();
    assert!(dna.is_object());
    assert!(audit.is_object());
}

#[test]
fn test_uid_consistency() {
    let adapter = LongHunAdapter::new("8888","","");
    let wrapped = adapter.wrap(json!({}), "", "", "", "");
    assert_eq!(wrapped["meta"]["uid"], "8888");
    assert_eq!(wrapped["audit"]["uid"], "UID8888");
}
