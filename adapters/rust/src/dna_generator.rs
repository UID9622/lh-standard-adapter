use chrono::{DateTime, Utc, Timelike, Datelike};
use sha2::{Sha256, Digest};
use std::collections::HashMap;

const TIAN_GAN: [&str; 10] = ["Jia","Yi","Bing","Ding","Wu","Ji","Geng","Xin","Ren","Gui"];
const DI_ZHI: [&str; 12] = ["Zi","Chou","Yin","Mao","Chen","Si","Wu","Wei","Shen","You","Xu","Hai"];
const SHI_CHEN: [&str; 12] = ["ZiShi","ChouShi","YinShi","MaoShi","ChenShi","SiShi","WuShi","WeiShi","ShenShi","YouShi","XuShi","HaiShi"];

pub fn generate_dna(uid: &str, device: &str, task_type: &str, action: &str, version: &str) -> String {
    let now = Utc::now();
    let stem = compute_stem_branch(now);
    let hexagram = select_hexagram(task_type);
    let version = if version.is_empty() { "V1.0" } else { version };
    let body = format!("ADAPTER-{}-{}-{}", task_type.to_uppercase(), action.to_uppercase(), version);
    let raw = format!("{}{}{}{}{}{}{}{}{}", stem.year, stem.month, stem.day, stem.shichen, hexagram.0, hexagram.1, body, device, now.to_rfc3339());
    let hash = Sha256::digest(raw.as_bytes());
    let hash8 = format!("{:x}", hash);
    format!("#LongHun⚡️{}·{}·{}·{}·{}{}-{}-{}", stem.year, stem.month, stem.day, stem.shichen, hexagram.0, hexagram.1, body, &hash8[..8])
}

struct StemBranch { year: String, month: String, day: String, shichen: String }

fn compute_stem_branch(dt: DateTime<Utc>) -> StemBranch {
    let cycle_year: i32 = 1984;
    let cycle_month: [i32; 12] = [2,4,6,8,10,0,2,4,6,8,10,0];
    let y = dt.year(); let m = dt.month() as i32; let doy = dt.ordinal() as i32; let h = dt.hour() as i32;

    let ys = ((y - cycle_year) % 10 + 10) % 10;
    let yb = ((y - cycle_year) % 12 + 12) % 12;

    let mb = cycle_month[((y - cycle_year) % 10 + 10) as usize % 10];
    let ms = ((mb + m - 1) % 10 + 10) % 10;
    let mbr = ((m + 1) % 12 + 12) % 12;

    let ds = ((y - 1900 + (y - 1900) / 4 + doy) % 10 + 10) % 10;
    let db = ((y - 1900 + (y - 1900) / 4 + doy) % 12 + 12) % 12;

    let si = (h / 2) as usize;

    StemBranch {
        year: format!("{}{}", TIAN_GAN[ys as usize], DI_ZHI[yb as usize]),
        month: format!("{}{}", TIAN_GAN[ms as usize], DI_ZHI[mbr as usize]),
        day: format!("{}{}", TIAN_GAN[ds as usize], DI_ZHI[db as usize]),
        shichen: SHI_CHEN[si].to_string(),
    }
}

fn select_hexagram(task_type: &str) -> (&'static str, &'static str) {
    let domain = match task_type {
        "code" => "engine", "deploy" => "deploy", "audit" => "audit",
        "security" => "security", "archive" => "archive", "init" => "init",
        "learn" => "learn", "legal" => "legal", "privacy" => "privacy",
        "trust" => "trust", "complete" => "complete", "progress" => "progress",
        _ => "governance",
    };
    let hexagrams: [(&str,&str,&str); 14] = [
        ("䷀","Qian","governance"),("䷁","Kun","archive"),("䷂","Zhun","init"),
        ("䷃","Meng","learn"),("䷄","Xu","async"),("䷅","Song","legal"),
        ("䷜","Kan","engine"),("䷝","Li","audit"),("䷲","Zhen","security"),
        ("䷳","Gen","privacy"),("䷸","Xun","deploy"),("䷹","Dui","trust"),
        ("䷾","JiJi","complete"),("䷿","WeiJi","progress"),
    ];
    for h in hexagrams.iter() { if h.2 == domain { return (h.0, h.1) } }
    (hexagrams[0].0, hexagrams[0].1)
}
