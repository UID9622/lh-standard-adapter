import * as crypto from 'crypto';

export const TIAN_GAN = ["Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"];
export const DI_ZHI = ["Zi", "Chou", "Yin", "Mao", "Chen", "Si", "Wu", "Wei", "Shen", "You", "Xu", "Hai"];
export const SHI_CHEN = ["ZiShi", "ChouShi", "YinShi", "MaoShi", "ChenShi", "SiShi", "WuShi", "WeiShi", "ShenShi", "YouShi", "XuShi", "HaiShi"];

export interface Hexagram {
  symbol: string;
  en_name: string;
  cn_name: string;
  domain: string;
}

export const HEXAGRAMS: Hexagram[] = [
  { symbol: "䷀", en_name: "Qian", cn_name: "乾", domain: "governance" },
  { symbol: "䷁", en_name: "Kun", cn_name: "坤", domain: "archive" },
  { symbol: "䷂", en_name: "Zhun", cn_name: "屯", domain: "init" },
  { symbol: "䷃", en_name: "Meng", cn_name: "蒙", domain: "learn" },
  { symbol: "䷄", en_name: "Xu", cn_name: "需", domain: "async" },
  { symbol: "䷅", en_name: "Song", cn_name: "讼", domain: "legal" },
  { symbol: "䷜", en_name: "Kan", cn_name: "坎", domain: "engine" },
  { symbol: "䷝", en_name: "Li", cn_name: "离", domain: "audit" },
  { symbol: "䷲", en_name: "Zhen", cn_name: "震", domain: "security" },
  { symbol: "䷳", en_name: "Gen", cn_name: "艮", domain: "privacy" },
  { symbol: "䷸", en_name: "Xun", cn_name: "巽", domain: "deploy" },
  { symbol: "䷹", en_name: "Dui", cn_name: "兑", domain: "trust" },
  { symbol: "䷾", en_name: "JiJi", cn_name: "既济", domain: "complete" },
  { symbol: "䷿", en_name: "WeiJi", cn_name: "未济", domain: "progress" }
];

export const TASK_HEXAGRAM_MAP: Record<string, string> = {
  default: "governance",
  code: "engine",
  deploy: "deploy",
  audit: "audit",
  security: "security",
  archive: "archive",
  init: "init",
  learn: "learn",
  legal: "legal",
  privacy: "privacy",
  trust: "trust",
  complete: "complete",
  progress: "progress"
};

export class DNAGenerator {
  private uid: string;
  private device: string;
  private cycleYear = 1984;
  private cycleMonth = [2, 4, 6, 8, 10, 0, 2, 4, 6, 8, 10, 0];

  constructor(uid = "9622", device = "HM-9622-001") {
    this.uid = uid;
    this.device = device;
  }

  public generate(taskType = "default", action = "WRAP", version = "V1.0"): string {
    const now = new Date();
    // Offset for UTC+8 (Asia/Shanghai)
    const utc8Offset = 8 * 60;
    const nowUtc8 = new Date(now.getTime() + (utc8Offset + now.getTimezoneOffset()) * 60000);

    const stem = this.computeStemBranch(nowUtc8);
    const hexagram = this.selectHexagram(taskType);
    const body = `ADAPTER-${taskType.toUpperCase()}-${action.toUpperCase()}-${version}`;

    const raw = `${stem.year}${stem.month}${stem.day}${stem.shichen}${hexagram.symbol}${hexagram.en_name}${body}${this.device}${nowUtc8.toISOString()}`;
    const hash8 = crypto.createHash("sha256").update(raw, "utf8").digest("hex").slice(0, 8);

    return `#LongHun⚡️${stem.year}·${stem.month}·${stem.day}·${stem.shichen}·${hexagram.symbol}${hexagram.en_name}-${body}-${hash8}`;
  }

  private computeStemBranch(dt: Date): { year: string; month: string; day: string; shichen: string } {
    const year = dt.getFullYear();
    const yearStemIdx = Math.abs((year - this.cycleYear) % 10);
    const yearBranchIdx = Math.abs((year - this.cycleYear) % 12);

    const month = dt.getMonth(); // 0-indexed
    const monthStemIdx = Math.abs((this.cycleMonth[yearStemIdx] + month) % 10);
    const monthBranchIdx = Math.abs((month + 2) % 12);

    // Approximate day stem/branch computation matching Python ref
    const dayOfYear = Math.floor((dt.getTime() - new Date(year, 0, 0).getTime()) / (1000 * 60 * 60 * 24));
    const dayStemIdx = Math.abs((year - 1900 + Math.floor((year - 1900) / 4) + dayOfYear) % 10);
    const dayBranchIdx = Math.abs((year - 1900 + Math.floor((year - 1900) / 4) + dayOfYear) % 12);

    const hour = dt.getHours();
    const shichenIdx = Math.floor(hour / 2) % 12;

    return {
      year: TIAN_GAN[yearStemIdx] + DI_ZHI[yearBranchIdx],
      month: TIAN_GAN[monthStemIdx] + DI_ZHI[monthBranchIdx],
      day: TIAN_GAN[dayStemIdx] + DI_ZHI[dayBranchIdx],
      shichen: SHI_CHEN[shichenIdx]
    };
  }

  private selectHexagram(taskType: string): Hexagram {
    const domain = TASK_HEXAGRAM_MAP[taskType] || "governance";
    const found = HEXAGRAMS.find((h) => h.domain === domain);
    return found || HEXAGRAMS[0];
  }
}
