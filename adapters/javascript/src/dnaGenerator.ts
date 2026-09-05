import * as crypto from "crypto";

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
  { symbol: "䷿", en_name: "WeiJi", cn_name: "未济", domain: "progress" },
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
  progress: "progress",
};

export class DNAGenerator {
  private uid: string;
  private device: string;
  private static CYCLE_YEAR = 1984;
  private static CYCLE_MONTH = [2, 4, 6, 8, 10, 0, 2, 4, 6, 8, 10, 0];

  constructor(uid: string = "9622", device: string = "HM-9622-001") {
    this.uid = uid;
    this.device = device;
  }

  public generate(taskType: string = "default", action: string = "WRAP", version: string = "V1.0"): string {
    const now = new Date();
    // UTC+8
    const utc8Time = new Date(now.getTime() + (8 * 60 + now.getTimezoneOffset()) * 60000);
    const stem = this.computeStemBranch(utc8Time);
    const hex = this.selectHexagram(taskType);
    const body = `ADAPTER-${taskType.toUpperCase()}-${action.toUpperCase()}-${version}`;

    const raw = `${stem.year}${stem.month}${stem.day}${stem.shichen}${hex.symbol}${hex.en_name}${body}${this.device}${utc8Time.toISOString()}`;
    const hash8 = crypto.createHash("sha256").update(raw, "utf8").digest("hex").slice(0, 8);

    return `#LongHun⚡️${stem.year}·${stem.month}·${stem.day}·${stem.shichen}·${hex.symbol}${hex.en_name}-${body}-${hash8}`;
  }

  private computeStemBranch(dt: Date): { year: string; month: string; day: string; shichen: string } {
    const year = dt.getFullYear();
    const month = dt.getMonth() + 1;
    const yearStemIdx = (year - DNAGenerator.CYCLE_YEAR + 1200) % 10;
    const yearBranchIdx = (year - DNAGenerator.CYCLE_YEAR + 1200) % 12;

    const monthStemIdx = (DNAGenerator.CYCLE_MONTH[(year - DNAGenerator.CYCLE_YEAR + 1200) % 10] + (month - 1)) % 10;
    const monthBranchIdx = (month + 1) % 12;

    const startOfYear = new Date(year, 0, 1);
    const dayOfYear = Math.floor((dt.getTime() - startOfYear.getTime()) / (1000 * 60 * 60 * 24)) + 1;
    const dayStemIdx = Math.abs((year - 1900 + Math.floor((year - 1900) / 4) + dayOfYear)) % 10;
    const dayBranchIdx = Math.abs((year - 1900 + Math.floor((year - 1900) / 4) + dayOfYear)) % 12;

    const shichenIdx = Math.floor(dt.getHours() / 2) % 12;

    return {
      year: TIAN_GAN[yearStemIdx] + DI_ZHI[yearBranchIdx],
      month: TIAN_GAN[monthStemIdx] + DI_ZHI[monthBranchIdx],
      day: TIAN_GAN[dayStemIdx] + DI_ZHI[dayBranchIdx],
      shichen: SHI_CHEN[shichenIdx],
    };
  }

  private selectHexagram(taskType: string): Hexagram {
    const domain = TASK_HEXAGRAM_MAP[taskType] || "governance";
    return HEXAGRAMS.find((h) => h.domain === domain) || HEXAGRAMS[0];
  }
}
