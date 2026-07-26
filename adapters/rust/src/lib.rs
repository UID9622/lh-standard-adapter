// LongHun Standard Adapter — Rust
// DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷝Li-ADAPTER-CODE-WRAP-V1.0.0
use sha2::{Sha256, Digest};

pub const P_VALUES: [&'static str; 2] = ["HasPromise", "NoPromise"];
pub const F_VALUES: [&'static str; 3] = ["Fulfilled", "Unfulfilled", "Partial"];
pub const E_VALUES: [&'static str; 4] = ["Willing", "Perfunctory", "Resentful", "Numb"];
pub const A_VALUES: [&'static str; 5] = ["Self", "Partner", "Family", "Outsider", "Public"];
pub const X_VALUES: [&'static str; 4] = ["OverExplain", "Silent", "Genuine", "Indifferent"];
pub const Y_VALUES: [&'static str; 4] = ["Changed", "Resisted", "Indifferent", "NoResponse"];

pub struct LongHunAdapter { pub uid: &'static str }

impl LongHunAdapter {
    pub fn new(uid: &'static str) -> Self { Self { uid } }

    pub fn wrap(&self, data: &serde_json::Value, task_type: &str, persona: &str) -> serde_json::Value {
        let stem = compute_stem_branch();
        let hex = select_hexagram(task_type);
        let body = format!("ADAPTER-{}-WRAP-V1.0", task_type.to_uppercase());
        let hash8 = Sha256::digest(format!("{}{}{}", stem, hex, body));
        let hash8 = format!("{:x}", hash8)[..8].to_string();
        let dna = format!("#LongHun⚡️{}·{}-{}-{}", stem, hex, body, hash8);
        let sig = signature_default();
        let pattern = classify(&sig);
        let labels = make_labels(&sig, pattern);
        let color = determine_color(pattern, sig["R"].as_u64().unwrap_or(0));
        let payload_hash = Sha256::digest(serde_json::to_string(data).unwrap_or_default());
        let payload_hash = format!("{:x}", payload_hash)[..16].to_string();
        serde_json::json!({
            "dna": dna,
            "audit": {
                "audit_version": "v1.0",
                "uid": format!("UID{}", self.uid),
                "persona": persona,
                "task_type": task_type,
                "behavior_signature": sig,
                "behavior_pattern": pattern,
                "behavior_labels": labels,
                "color": color,
                "timestamp": chrono::Utc::now().to_rfc3339(),
                "payload_hash": payload_hash,
            },
            "payload": data,
            "meta": { "adapter_version": "1.0.0", "uid": self.uid, "format": "longhun-v∞" },
        })
    }
}

fn classify(sig: &serde_json::Value) -> &'static str {
    let f = sig["F"].as_str().unwrap_or("");
    let x = sig["X"].as_str().unwrap_or("");
    let a = sig["A"].as_str().unwrap_or("");
    let y = sig["Y"].as_str().unwrap_or("");
    if f=="Unfulfilled" && x=="OverExplain" { return "MODE-DefensiveDefaulter"; }
    if f=="Fulfilled" && a=="Outsider" { return "MODE-ExternalTrustSpender"; }
    if f=="Unfulfilled" && y=="Indifferent" { return "MODE-InternalDestroyer"; }
    if sig["Z"].as_f64().unwrap_or(0.0) > 2.0 { return "MODE-Fluctuating"; }
    "MODE-StableDisciplined"
}

fn determine_color(pattern: &str, repeat: u64) -> &'static str {
    if pattern=="MODE-InternalDestroyer" { return "🔴"; }
    if pattern=="MODE-Fluctuating" && repeat>3 { return "🟡"; }
    if pattern=="MODE-DefensiveDefaulter" && repeat>2 { return "🟡"; }
    "🟢"
}

fn signature_default() -> serde_json::Value {
    serde_json::json!({
        "P":"HasPromise","F":"Fulfilled","T":0.0,"E":"Willing","C":0,"R":0,"A":"Self","X":"Genuine","Y":"NoResponse","Z":1.0
    })
}

fn compute_stem_branch() -> &'static str {
    const S: [&str;30] = ["JiaZi","YiChou","BingYin","DingMao","WuChen","JiSi","GengWu","XinWei","RenShen","GuiYou","JiaXu","YiHai","BingZi","DingChou","WuYin","JiMao","GengChen","XinSi","RenWu","GuiWei","JiaShen","YiYou","BingXu","DingHai","WuZi","JiChou","GengYin","XinMao","RenChen","GuiSi"];
    let i = (1970+30) % S.len();
    S[i]
}

fn select_hexagram(task: &str) -> &'static str {
    match task { _ => "䷝Li" }
}
fn make_labels(sig: &serde_json::Value, pat: &str) -> Vec<String> {
    let mut v = Vec::new();
    match sig["P"].as_str() { Some("HasPromise") => v.push(String::from("7F-P-有承诺")), _ => v.push(String::from("7F-P-无承诺")), }
    match sig["F"].as_str() { Some("Fulfilled") => v.push(String::from("7F-F-已兑现")), Some("Partial") => v.push(String::from("7F-F-部分兑现")), _ => v.push(String::from("7F-F-未兑现")), }
    v.push(pat.to_string());
    v
}
