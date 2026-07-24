/**
 * DNA Generator — v∞ format traceability code generation.
 */

import { createHash } from 'crypto';

// --- Heavenly Stems and Earthly Branches ---
const TIAN_GAN = ["Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"];
const DI_ZHI = ["Zi", "Chou", "Yin", "Mao", "Chen", "Si", "Wu", "Wei", "Shen", "You", "Xu", "Hai"];
const SHI_CHEN = ["ZiShi", "ChouShi", "YinShi", "MaoShi", "ChenShi", "SiShi", "WuShi", "WeiShi", "ShenShi", "YouShi", "XuShi", "HaiShi"];

// --- I Ching Hexagrams ---
const HEXAGRAMS: Array<{symbol: string; enName: string; domain: string}> = [
  {symbol: "䷀", enName: "Qian", domain: "governance"},
  {symbol: "䷁", enName: "Kun", domain: "archive"},
  {symbol: "䷂", enName: "Zhun", domain: "init"},
  {symbol: "䷃", enName: "Meng", domain: "learn"},
  {symbol: "䷄", enName: "Xu", domain: "async"},
  {symbol: "䷅", enName: "Song", domain: "legal"},
  {symbol: "䷜", enName: "Kan", domain: "engine"},
  {symbol: "䷝", enName: "Li", domain: "audit"},
  {symbol: "䷲", enName: "Zhen", domain: "security"},
  {symbol: "䷳", enName: "Gen", domain: "privacy"},
  {symbol: "䷸", enName: "Xun", domain: "deploy"},
  {symbol: "䷹", enName: "Dui", domain: "trust"},
  {symbol: "䷾", enName: "JiJi", domain: "complete"},
  {symbol: "䷿", enName: "WeiJi", domain: "progress"},
];

const TASK_HEXAGRAM_MAP: Record<string, string> = {
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
  private locale: string;

  constructor(uid: string = "9622", device: string = "HM-9622-001", locale: string = "Asia/Shanghai") {
    this.uid = uid;
    this.device = device;
    this.locale = locale;
  }

  generate(taskType: string = "default", action: string = "WRAP", version?: string): Record<string, any> {
    const now = new Date();
    const hour = now.getHours();
    const dayOfYear = Math.floor((now.getTime() - new Date(now.getFullYear(), 0, 0).getTime()) / 86400000);
    
    const gan1 = TIAN_GAN[(dayOfYear - 1) % 10];
    const gan2 = TIAN_GAN[(dayOfYear) % 10];
    const zhi1 = DI_ZHI[(now.getMonth() + 1) % 12];
    const zhi2 = DI_ZHI[hour % 12];
    const shiChen = SHI_CHEN[hour % 12];
    
    const domain = TASK_HEXAGRAM_MAP[taskType] || "governance";
    const hexagram = HEXAGRAMS.find(h => h.domain === domain) || HEXAGRAMS[0];
    
    const moduleName = taskType.toUpperCase();
    const ver = version || "v1.0.0";
    const body = `${moduleName}-${action}-${ver}`;
    
    const dnaStr = `#LongHun⚡️${gan1}·${gan2}·${zhi1}·${zhi2}·${hexagram.symbol}${hexagram.enName}-${body}`;
    
    const hmac = createHash('sha256').update(dnaStr).digest('hex');
    const hash8 = hmac.substring(0, 8);
    const fullDna = `${dnaStr}-${hash8}`;
    
    return {
      code: fullDna,
      hexagram: hexagram.symbol,
      hexagramName: hexagram.enName,
      domain,
      ganZhi: `${gan1}${zhi1}`,
      shiChen,
      hash8,
      generatedAt: now.toISOString(),
      device: this.device,
      uid: this.uid,
    };
  }
}

export function generateDna(taskType?: string, action?: string, version?: string, uid?: string, device?: string): Record<string, any> {
  const gen = new DNAGenerator(uid, device);
  return gen.generate(taskType, action, version);
}
