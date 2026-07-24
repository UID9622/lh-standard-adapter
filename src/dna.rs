//! DNA Generator — v∞ format traceability code generation.

use sha2::{Sha256, Digest};
use std::time::{SystemTime, UNIX_EPOCH};

const TIAN_GAN: &[&str] = &["Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"];
const DI_ZHI: &[&str] = &["Zi", "Chou", "Yin", "Mao", "Chen", "Si", "Wu", "Wei", "Shen", "You", "Xu", "Hai"];
const SHI_CHEN: &[&str] = &["ZiShi", "ChouShi", "YinShi", "MaoShi", "ChenShi", "SiShi",
                            "WuShi", "WeiShi", "ShenShi", "YouShi", "XuShi", "HaiShi"];

pub struct DNAGenerator {
    pub uid: String,
    pub device: String,
    pub locale: String,
}

impl DNAGenerator {
    pub fn new(uid: Option<String>, device: Option<String>, locale: Option<String>) -> Self {
        Self {
            uid: uid.unwrap_or_else(|| "9622".to_string()),
            device: device.unwrap_or_else(|| "HM-9622-001".to_string()),
            locale: locale.unwrap_or_else(|| "Asia/Shanghai".to_string()),
        }
    }

    pub fn generate(&self, task_type: &str, action: &str, version: Option<&str>) -> DNAResult {
        let now = chrono_now();
        let hour = now % 24;
        let day_of_year = now / 86400 % 365;

        let gan1 = TIAN_GAN[(day_of_year as usize) % 10];
        let gan2 = TIAN_GAN[(day_of_year as usize + 1) % 10];
        let zhi1 = DI_ZHI[((now / 86400 / 30) as usize) % 12];
        let zhi2 = DI_ZHI[hour as usize % 12];
        let shi_chen = SHI_CHEN[hour as usize % 12];

        let domain = match task_type {
            "code" => "engine",
            "deploy" => "deploy",
            "audit" => "audit",
            "security" => "security",
            _ => "governance",
        };

        let ver = version.unwrap_or("v1.0.0");
        let body = format!("{}-{}-{}", task_type.to_uppercase(), action, ver);
        let dna_str = format!("#LongHun⚡️{}·{}·{}·{}·{}", gan1, gan2, zhi1, zhi2);

        let mut hasher = Sha256::new();
        hasher.update(dna_str.as_bytes());
        let hash = hasher.finalize();
        let hash8 = format!("{:x}", &hash[..4]);

        DNAResult {
            code: format!("{}-{}-{}", dna_str, body, hash8),
            domain: domain.to_string(),
            shi_chen: shi_chen.to_string(),
            generated_at: chrono_iso(),
        }
    }
}

#[derive(Debug, serde::Serialize)]
pub struct DNAResult {
    pub code: String,
    pub domain: String,
    pub shi_chen: String,
    pub generated_at: String,
}

fn chrono_now() -> u64 {
    SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .unwrap()
        .as_secs()
}

fn chrono_iso() -> String {
    // Simplified ISO timestamp
    format!("{}", chrono_now())
}

pub fn generate_dna(task_type: &str, action: &str) -> DNAResult {
    let gen = DNAGenerator::new(None, None, None);
    gen.generate(task_type, action, None)
}
