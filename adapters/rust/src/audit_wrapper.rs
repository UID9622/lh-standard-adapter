use sha2::{Sha256, Digest};
use serde_json::Value;
use chrono::Utc;
use std::collections::HashMap;

pub fn wrap_audit(uid: &str, payload: &Value, task_type: &str, persona: &str) -> Value {
    let now = Utc::now();
    let sig = serde_json::json!({
        "P":"HasPromise","F":"Fulfilled","T":0.0,"E":"Willing",
        "C":0,"R":0,"A":"Self","X":"Genuine","Y":"NoResponse","Z":1.0
    });
    let pattern = classify(&sig);
    let labels = make_labels(&sig, pattern);
    let color = determine_color(pattern, sig["R"].as_i64().unwrap_or(0) as i32);
    let payload_str = serde_json::to_string(payload).unwrap_or_default();
    let hash = Sha256::digest(payload_str.as_bytes());
    let ph = format!("{:x}", hash);

    serde_json::json!({
        "audit_version":"v1.0",
        "uid":format!("UID{}", uid),
        "persona":persona,
        "task_type":task_type,
        "behavior_signature":sig,
        "behavior_pattern":pattern,
        "behavior_labels":labels,
        "color":color,
        "timestamp":now.to_rfc3339(),
        "payload_hash":&ph[..16]
    })
}

fn classify(sig: &Value) -> &'static str {
    let f = sig["F"].as_str().unwrap_or("");
    let x = sig["X"].as_str().unwrap_or("");
    let a = sig["A"].as_str().unwrap_or("");
    let y = sig["Y"].as_str().unwrap_or("");
    let z = sig["Z"].as_f64().unwrap_or(0.0);
    if f == "Unfulfilled" && x == "OverExplain" { return "MODE-DefensiveDefaulter" }
    if f == "Fulfilled" && a == "Outsider" { return "MODE-ExternalTrustSpender" }
    if f == "Unfulfilled" && y == "Indifferent" { return "MODE-InternalDestroyer" }
    if z > 2.0 { return "MODE-Fluctuating" }
    "MODE-StableDisciplined"
}

fn make_labels(sig: &Value, pattern: &str) -> Vec<String> {
    let label_map: HashMap<&str, HashMap<&str, &str>> = HashMap::from([
        ("P", HashMap::from([("HasPromise","7F-P-有承诺"),("NoPromise","7F-P-无承诺")])),
        ("F", HashMap::from([("Fulfilled","7F-F-已兑现"),("Unfulfilled","7F-F-未兑现"),("Partial","7F-F-部分兑现")])),
        ("E", HashMap::from([("Willing","7F-E-心甘情愿"),("Perfunctory","7F-E-敷衍"),("Resentful","7F-E-怨恨"),("Numb","7F-E-麻木")])),
        ("A", HashMap::from([("Self","7F-A-自己"),("Partner","7F-A-伴侣"),("Family","7F-A-家庭"),("Outsider","7F-A-外人"),("Public","7F-A-公众")])),
        ("X", HashMap::from([("OverExplain","7F-X-过度解释"),("Silent","7F-X-沉默"),("Genuine","7F-X-真诚"),("Indifferent","7F-X-冷漠")])),
        ("Y", HashMap::from([("Changed","7F-Y-改正"),("Resisted","7F-Y-抗拒"),("Indifferent","7F-Y-无视"),("NoResponse","7F-Y-无响应")])),
    ]);
    let mut labels = Vec::new();
    for factor in &["P","F","E","A","X","Y"] {
        if let Some(val) = sig[factor].as_str() {
            if let Some(fm) = label_map.get(factor) {
                if let Some(lb) = fm.get(val) { labels.push(lb.to_string()) }
            }
        }
    }
    labels.push(pattern.to_string());
    labels
}

fn determine_color(pattern: &str, repeat: i32) -> &'static str {
    if pattern == "MODE-InternalDestroyer" { return "🔴" }
    if pattern == "MODE-Fluctuating" && repeat > 3 { return "🟡" }
    if pattern == "MODE-DefensiveDefaulter" && repeat > 2 { return "🟡" }
    "🟢"
}
