use chrono::{FixedOffset, Utc};
use sha2::{Digest, Sha256};
use std::collections::HashMap;

pub const TIAN_GAN: [&str; 10] = ["Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"];
pub const DI_ZHI: [&str; 12] = ["Zi", "Chou", "Yin", "Mao", "Chen", "Si", "Wu", "Wei", "Shen", "You", "Xu", "Hai"];
pub const SHI_CHEN: [&str; 12] = ["ZiShi", "ChouShi", "YinShi", "MaoShi", "ChenShi", "SiShi", "WuShi", "WeiShi", "ShenShi", "YouShi", "XuShi", "HaiShi"];

pub struct Hexagram {
    pub symbol: &'static str,
    pub en_name: &'static str,
    pub domain: &'static str,
}

pub const HEXAGRAMS: [Hexagram; 14] = [
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

pub struct DNAGenerator {
    pub uid: String,
    pub device: String,
    cycle_year: i32,
    cycle_month: [usize; 12],
}

impl DNAGenerator {
    pub fn new(uid: &str, device: &str) -> Self {
        Self {
            uid: if uid.is_empty() { "9622".to_string() } else { uid.to_string() },
            device: if device.is_empty() { "HM-9622-001".to_string() } else { device.to_string() },
            cycle_year: 1984,
            cycle_month: [2, 4, 6, 8, 10, 0, 2, 4, 6, 8, 10, 0],
        }
    }

    pub fn generate(&self, task_type: &str, action: &str, version: &str) -> String {
        let task = if task_type.is_empty() { "default" } else { task_type };
        let act = if action.is_empty() { "WRAP" } else { action };
        let ver = if version.is_empty() { "V1.0" } else { version };

        let offset = FixedOffset::east_opt(8 * 3600).unwrap();
        let now = Utc::now().with_timezone(&offset);

        let (stem_year, stem_month, stem_day, shichen) = self.compute_stem_branch(&now);
        let hexagram = self.select_hexagram(task);
        let body = format!("ADAPTER-{}-{}-{}", task.to_uppercase(), act.to_uppercase(), ver);

        let raw = format!(
            "{}{}{}{}{}{}{}{}{}",
            stem_year, stem_month, stem_day, shichen, hexagram.symbol, hexagram.en_name, body, self.device, now.to_rfc3339()
        );

        let mut hasher = Sha256::new();
        hasher.update(raw.as_bytes());
        let result = hasher.finalize();
        let hash8 = &format!("{:x}", result)[..8];

        format!(
            "#LongHun⚡️{}·{}·{}·{}·{}{}-{}-{}",
            stem_year, stem_month, stem_day, shichen, hexagram.symbol, hexagram.en_name, body, hash8
        )
    }

    fn compute_stem_branch(&self, dt: &chrono::DateTime<FixedOffset>) -> (String, String, String, String) {
        use chrono::Datelike;
        let year = dt.year();
        let year_stem_idx = ((year - self.cycle_year) % 10).abs() as usize;
        let year_branch_idx = ((year - self.cycle_year) % 12).abs() as usize;

        let month = (dt.month() - 1) as usize;
        let month_stem_idx = ((self.cycle_month[year_stem_idx] + month) % 10) as usize;
        let month_branch_idx = ((month + 2) % 12) as usize;

        let day_of_year = dt.ordinal() as i32;
        let day_stem_idx = ((year - 1900 + (year - 1900) / 4 + day_of_year) % 10).abs() as usize;
        let day_branch_idx = ((year - 1900 + (year - 1900) / 4 + day_of_year) % 12).abs() as usize;

        use chrono::Timelike;
        let shichen_idx = (dt.hour() / 2) as usize;

        (
            format!("{}{}", TIAN_GAN[year_stem_idx], DI_ZHI[year_branch_idx]),
            format!("{}{}", TIAN_GAN[month_stem_idx], DI_ZHI[month_branch_idx]),
            format!("{}{}", TIAN_GAN[day_stem_idx], DI_ZHI[day_branch_idx]),
            SHI_CHEN[shichen_idx % 12].to_string(),
        )
    }

    fn select_hexagram(&self, task_type: &str) -> &'static Hexagram {
        let mut map = HashMap::new();
        map.insert("code", "engine");
        map.insert("deploy", "deploy");
        map.insert("audit", "audit");
        map.insert("security", "security");

        let domain = map.get(task_type).copied().unwrap_or("governance");
        HEXAGRAMS.iter().find(|h| h.domain == domain).unwrap_or(&HEXAGRAMS[0])
    }
}
