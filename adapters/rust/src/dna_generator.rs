use sha2::{Digest, Sha256};
use chrono::{DateTime, Datelike, FixedOffset, Timelike};

// --- Heavenly Stems and Earthly Branches ---

const TIAN_GAN: &[&str] = &["Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"];
const DI_ZHI: &[&str] = &["Zi", "Chou", "Yin", "Mao", "Chen", "Si", "Wu", "Wei", "Shen", "You", "Xu", "Hai"];
const SHI_CHEN: &[&str] = &["ZiShi", "ChouShi", "YinShi", "MaoShi", "ChenShi", "SiShi",
                            "WuShi", "WeiShi", "ShenShi", "YouShi", "XuShi", "HaiShi"];

// --- I Ching Hexagrams ---

pub struct Hexagram {
    pub symbol: &'static str,
    pub en_name: &'static str,
    pub cn_name: &'static str,
    pub domain: &'static str,
}

const HEXAGRAMS: &[Hexagram] = &[
    Hexagram { symbol: "䷀", en_name: "Qian", cn_name: "乾", domain: "governance" },
    Hexagram { symbol: "䷁", en_name: "Kun", cn_name: "坤", domain: "archive" },
    Hexagram { symbol: "䷂", en_name: "Zhun", cn_name: "屯", domain: "init" },
    Hexagram { symbol: "䷃", en_name: "Meng", cn_name: "蒙", domain: "learn" },
    Hexagram { symbol: "䷄", en_name: "Xu", cn_name: "需", domain: "async" },
    Hexagram { symbol: "䷅", en_name: "Song", cn_name: "讼", domain: "legal" },
    Hexagram { symbol: "䷜", en_name: "Kan", cn_name: "坎", domain: "engine" },
    Hexagram { symbol: "䷝", en_name: "Li", cn_name: "离", domain: "audit" },
    Hexagram { symbol: "䷲", en_name: "Zhen", cn_name: "震", domain: "security" },
    Hexagram { symbol: "䷳", en_name: "Gen", cn_name: "艮", domain: "privacy" },
    Hexagram { symbol: "䷸", en_name: "Xun", cn_name: "巽", domain: "deploy" },
    Hexagram { symbol: "䷹", en_name: "Dui", cn_name: "兑", domain: "trust" },
    Hexagram { symbol: "䷾", en_name: "JiJi", cn_name: "既济", domain: "complete" },
    Hexagram { symbol: "䷿", en_name: "WeiJi", cn_name: "未济", domain: "progress" },
];

// --- Task-to-hexagram domain mapping ---

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
        _ => "governance",
    }
}

const CYCLE_YEAR: i32 = 1984;
const CYCLE_MONTH: [i32; 12] = [2, 4, 6, 8, 10, 0, 2, 4, 6, 8, 10, 0];

pub struct StemBranch {
    pub year: String,
    pub month: String,
    pub day: String,
    pub shichen: String,
}

pub struct DNAGenerator {
    pub uid: String,
    pub device: String,
    pub locale: String,
}

impl DNAGenerator {
    pub fn new(uid: &str, device: &str, locale: &str) -> Self {
        Self {
            uid: uid.to_string(),
            device: device.to_string(),
            locale: locale.to_string(),
        }
    }

    pub fn generate(&self, task_type: &str, action: &str, version: Option<&str>) -> String {
        let tz = FixedOffset::east_opt(8 * 3600).unwrap();
        let now: DateTime<FixedOffset> = chrono::Utc::now().with_timezone(&tz);

        let stem = self._compute_stem_branch(&now);
        let hexagram = self._select_hexagram(task_type);
        let ver = version.unwrap_or("V1.0");

        let body = format!("ADAPTER-{}-{}-{}", task_type.to_uppercase(), action.to_uppercase(), ver);

        let raw = format!(
            "{}{}{}{}{}{}{}{}{}",
            stem.year, stem.month, stem.day, stem.shichen,
            hexagram.symbol, hexagram.en_name, body, self.device, now.to_rfc3339()
        );

        let hash8 = {
            let mut hasher = Sha256::new();
            hasher.update(raw.as_bytes());
            let result = hasher.finalize();
            format!("{:02x}{:02x}{:02x}{:02x}", result[0], result[1], result[2], result[3])
        };

        format!(
            "#LongHun⚡️{}·{}·{}·{}·{}{}-{}-{}",
            stem.year, stem.month, stem.day, stem.shichen,
            hexagram.symbol, hexagram.en_name, body, hash8
        )
    }

    fn _compute_stem_branch(&self, dt: &DateTime<FixedOffset>) -> StemBranch {
        let year_stem_idx = ((dt.year() - CYCLE_YEAR) % 10 + 10) % 10;
        let year_branch_idx = ((dt.year() - CYCLE_YEAR) % 12 + 12) % 12;

        let cycle_idx = ((dt.year() - CYCLE_YEAR) % 10 + 10) % 10;
        let month_stem_base = CYCLE_MONTH[cycle_idx as usize];
        let month_stem_idx = if month_stem_base >= 0 {
            (month_stem_base + (dt.month() as i32 - 1)) % 10
        } else {
            (dt.month() as i32 * 2) % 10
        };
        let month_branch_idx = (dt.month() as i32 + 1) % 12;

        let yday = dt.ordinal() as i32;
        let day_stem_idx = ((dt.year() - 1900 + (dt.year() - 1900) / 4 + yday) % 10 + 10) % 10;
        let day_branch_idx = ((dt.year() - 1900 + (dt.year() - 1900) / 4 + yday) % 12 + 12) % 12;

        let shichen_idx = (dt.hour() / 2) as usize;

        StemBranch {
            year: format!("{}{}", TIAN_GAN[year_stem_idx as usize], DI_ZHI[year_branch_idx as usize]),
            month: format!("{}{}", TIAN_GAN[month_stem_idx as usize], DI_ZHI[month_branch_idx as usize]),
            day: format!("{}{}", TIAN_GAN[day_stem_idx as usize], DI_ZHI[day_branch_idx as usize]),
            shichen: SHI_CHEN[shichen_idx.min(11)].to_string(),
        }
    }

    fn _select_hexagram(&self, task_type: &str) -> &Hexagram {
        let domain = task_domain(task_type);
        for h in HEXAGRAMS {
            if h.domain == domain {
                return h;
            }
        }
        &HEXAGRAMS[0]
    }
}

pub fn generate_dna(task_type: &str, action: &str, version: Option<&str>) -> String {
    let gen = DNAGenerator::new("9622", "HM-9622-001", "Asia/Shanghai");
    gen.generate(task_type, action, version)
}

pub fn chrono_now_iso() -> String {
    let tz = FixedOffset::east_opt(8 * 3600).unwrap();
    chrono::Utc::now().with_timezone(&tz).to_rfc3339()
}
