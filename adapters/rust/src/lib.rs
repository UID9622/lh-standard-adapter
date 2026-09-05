use chrono::{DateTime, Utc};
use regex::Regex;
use serde::{Deserialize, Serialize};
use sha2::{Digest, Sha256};
use std::collections::HashMap;

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct LongHunAdapter {
    pub uid: String,
    pub device: String,
}

impl Default for LongHunAdapter {
    fn default() -> Self {
        Self {
            uid: "9622".to_string(),
            device: "HM-9622-001".to_string(),
        }
    }
}

impl LongHunAdapter {
    pub fn new(uid: &str, device: &str) -> Self {
        Self {
            uid: uid.to_string(),
            device: device.to_string(),
        }
    }

    pub fn generate_dna(&self, task_type: &str, action: &str, version: &str) -> String {
        let now: DateTime<Utc> = Utc::now();
        let body = format!("ADAPTER-{}-{}-{}", task_type.to_uppercase(), action.to_uppercase(), version);
        let raw = format!("BingWuGuiWeiJiaZiZiShi䷾JiJi{}{}{}", body, self.device, now.to_rfc3339());
        let mut hasher = Sha256::new();
        hasher.update(raw.as_bytes());
        let result = hasher.finalize();
        let hash8 = &hex::encode(result)[..8];

        format!("#LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-{}-{}", body, hash8)
    }

    pub fn validate(&self, dna: &str) -> bool {
        let re = Regex::new(r"^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([䷀-䷿][A-Za-z]+)-(.+)-([a-f0-9]{8})$").unwrap();
        re.is_match(dna)
    }
}
