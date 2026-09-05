//! LongHun Standard Adapter — Rust crate
//!
//! DNA v∞ traceability generation + validation, mirroring the Python reference
//! implementation (`lh_standard_adapter`) so that all language adapters produce
//! identical DNA prefixes (four Ganzhi pillars + hexagram + body) for the same
//! task at the same instant under Asia/Shanghai time.

use chrono::{DateTime, Datelike, Duration, Timelike, Utc};
use regex::Regex;
use sha2::{Digest, Sha256};

const CYCLE_YEAR: i32 = 1984; // JiaZi reference year

const TIAN_GAN: [&str; 10] = ["Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"];
const DI_ZHI: [&str; 12] = ["Zi", "Chou", "Yin", "Mao", "Chen", "Si", "Wu", "Wei", "Shen", "You", "Xu", "Hai"];
const SHI_CHEN: [&str; 12] = ["ZiShi", "ChouShi", "YinShi", "MaoShi", "ChenShi", "SiShi", "WuShi", "WeiShi", "ShenShi", "YouShi", "XuShi", "HaiShi"];
const CYCLE_MONTH: [i32; 12] = [2, 4, 6, 8, 10, 0, 2, 4, 6, 8, 10, 0];

struct Hexagram {
    symbol: &'static str,
    en_name: &'static str,
    domain: &'static str,
}

const HEXAGRAMS: [Hexagram; 14] = [
    Hexagram { symbol: "䷀", en_name: "Qian", domain: "governance" },
    Hexagram { symbol: "䷁", en_name: "Kun", domain: "archive" },
    Hexagram { symbol: "䷂", en_name: "Zhun", domain: "init" },
    Hexagram { symbol: "䷃", en_name: "Meng", domain: "learn" },
    Hexagram { symbol: "䷄", en_name: "Xu", domain: "async" },
    Hexagram { symbol: "䷅", en_name: "Song", domain: "legal" },
    Hexagram { symbol: "䷜", en_name: "Kan", domain: "engine" },
    Hexagram { symbol: "䷝", en_name: "Li", domain: "audit" },
    Hexagram { symbol: "䷲", en_name: "Zhen", domain: "security" },
    Hexagram { symbol: "䷳", en_name: "Gen", domain: "privacy" },
    Hexagram { symbol: "䷸", en_name: "Xun", domain: "deploy" },
    Hexagram { symbol: "䷹", en_name: "Dui", domain: "trust" },
    Hexagram { symbol: "䷾", en_name: "JiJi", domain: "complete" },
    Hexagram { symbol: "䷿", en_name: "WeiJi", domain: "progress" },
];

fn task_domain(task_type: &str) -> &'static str {
    match task_type {
        "code" => "engine",
        "deploy" => "deploy",
        "audit" => "audit",
        "security" => "security",
        "archive" => "archive",
        "init" => "init",
        "learn" => "learn",
        "legal" => "legal",
        "privacy" => "privacy",
        "trust" => "trust",
        "complete" => "complete",
        "progress" => "progress",
        _ => "governance", // default and anything unknown
    }
}

fn select_hexagram(task_type: &str) -> &'static Hexagram {
    let domain = task_domain(task_type);
    HEXAGRAMS.iter().find(|h| h.domain == domain).unwrap_or(&HEXAGRAMS[0])
}

/// Compute the four-pillar Ganzhi under Asia/Shanghai time using the reference
/// algorithm (CYCLE_YEAR 1984, month-stem table, Julian-day day pillar, hour/2).
fn compute_stem_branch(now: DateTime<Utc>) -> (String, String, String, String) {
    let base = now.year() - CYCLE_YEAR;
    let year = format!(
        "{}{}",
        TIAN_GAN[base.rem_euclid(10) as usize],
        DI_ZHI[base.rem_euclid(12) as usize]
    );

    let month_idx = now.month() as i32;
    let m_stem = CYCLE_MONTH[base.rem_euclid(10) as usize] + month_idx - 1;
    let month = format!(
        "{}{}",
        TIAN_GAN[m_stem.rem_euclid(10) as usize],
        DI_ZHI[(month_idx + 1).rem_euclid(12) as usize]
    );

    let julian = now.year() - 1900 + (now.year() - 1900) / 4 + now.ordinal() as i32;
    let day = format!(
        "{}{}",
        TIAN_GAN[julian.rem_euclid(10) as usize],
        DI_ZHI[julian.rem_euclid(12) as usize]
    );

    let sc = SHI_CHEN[(((now.hour() as usize) + 1) / 2) % 12];
    (year, month, day, sc.to_string())
}

#[derive(Debug, Clone)]
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
        let task_type = if task_type.is_empty() { "default" } else { task_type };
        let action = if action.is_empty() { "WRAP" } else { action };
        let version = if version.is_empty() { "V1.0" } else { version };

        // Asia/Shanghai wall-clock time (UTC+8).
        let now: DateTime<Utc> = Utc::now() + Duration::hours(8);
        let (ys, ms, ds, sc) = compute_stem_branch(now);
        let hex = select_hexagram(task_type);

        let body = format!(
            "ADAPTER-{}-{}-{}",
            task_type.to_uppercase(),
            action.to_uppercase(),
            version.to_uppercase()
        );
        let raw = format!(
            "{}{}{}{}{}{}{}{}{}",
            ys, ms, ds, sc, hex.symbol, hex.en_name, body, self.device, now.to_rfc3339()
        );
        let mut hasher = Sha256::new();
        hasher.update(raw.as_bytes());
        let result = hasher.finalize();
        let hash8 = &hex::encode(result)[..8];

        format!(
            "#LongHun⚡️{}·{}·{}·{}·{}{}-{}-{}",
            ys, ms, ds, sc, hex.symbol, hex.en_name, body, hash8
        )
    }

    pub fn validate(&self, dna: &str) -> bool {
        let re = Regex::new(r"^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([䷀-䷿][A-Za-z]+)-(.+)-([a-f0-9]{8})$").unwrap();
        re.is_match(dna)
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn generated_dna_matches_regex() {
        let a = LongHunAdapter::default();
        let dna = a.generate_dna("code", "WRAP", "V1.0");
        assert!(a.validate(&dna), "DNA failed schema regex: {dna}");
    }

    #[test]
    fn print_dna_for_consistency() {
        let a = LongHunAdapter::default();
        println!("{}", a.generate_dna("code", "WRAP", "V1.0"));
    }
}
