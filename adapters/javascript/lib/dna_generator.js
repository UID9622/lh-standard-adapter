/**
 * DNA Generator — v∞ format traceability code generation.
 * Port of Python lh_standard_adapter/dna_generator.py
 */

const TIAN_GAN = ["Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"];
const DI_ZHI = ["Zi", "Chou", "Yin", "Mao", "Chen", "Si", "Wu", "Wei", "Shen", "You", "Xu", "Hai"];
const SHI_CHEN = ["ZiShi", "ChouShi", "YinShi", "MaoShi", "ChenShi", "SiShi", "WuShi", "WeiShi", "ShenShi", "YouShi", "XuShi", "HaiShi"];

const HEXAGRAMS = [
  { symbol: "䷀", en: "Qian", cn: "乾", domain: "governance" },
  { symbol: "䷁", en: "Kun", cn: "坤", domain: "archive" },
  { symbol: "䷂", en: "Zhun", cn: "屯", domain: "init" },
  { symbol: "䷃", en: "Meng", cn: "蒙", domain: "learn" },
  { symbol: "䷄", en: "Xu", cn: "需", domain: "async" },
  { symbol: "䷅", en: "Song", cn: "讼", domain: "legal" },
  { symbol: "䷜", en: "Kan", cn: "坎", domain: "engine" },
  { symbol: "䷝", en: "Li", cn: "离", domain: "audit" },
  { symbol: "䷲", en: "Zhen", cn: "震", domain: "security" },
  { symbol: "䷳", en: "Gen", cn: "艮", domain: "privacy" },
  { symbol: "䷸", en: "Xun", cn: "巽", domain: "deploy" },
  { symbol: "䷹", en: "Dui", cn: "兑", domain: "trust" },
  { symbol: "䷾", en: "JiJi", cn: "既济", domain: "complete" },
  { symbol: "䷿", en: "WeiJi", cn: "未济", domain: "progress" },
];

const TASK_HEXAGRAM_MAP = {
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

function sha256hex(input) {
  const crypto = require("crypto");
  return crypto.createHash("sha256").update(input).digest("hex");
}

function getStemBranch(dt) {
  const year = dt.getUTCFullYear();
  const month = dt.getUTCMonth() + 1;
  const day = dt.getUTCDate();
  const hour = dt.getUTCHours();

  const stemIdx = (year - 4) % 10;
  const branchIdx = (year - 4) % 12;
  const monthStemIdx = (stemIdx * 2 + month) % 10;
  const dayHash = (year * 5 + Math.floor(year / 4) + month * 2 + day * 1) % 60;
  const dayStemIdx = dayHash % 10;
  const dayBranchIdx = dayHash % 12;
  const shichenIdx = Math.floor(((hour + 1) % 24) / 2);

  return {
    yearStem: TIAN_GAN[stemIdx],
    yearBranch: DI_ZHI[branchIdx],
    monthStem: TIAN_GAN[monthStemIdx],
    dayStem: TIAN_GAN[dayStemIdx],
    dayBranch: DI_ZHI[dayBranchIdx],
    shichen: SHI_CHEN[shichenIdx],
  };
}

function selectHexagram(taskType) {
  const domain = TASK_HEXAGRAM_MAP[taskType] || TASK_HEXAGRAM_MAP.default;
  return HEXAGRAMS.find((h) => h.domain === domain) || HEXAGRAMS[0];
}

function generateDnaCode(options = {}) {
  const { taskType = "default", uid = "", device = "", timestamp = new Date() } = options;
  const dt = timestamp instanceof Date ? timestamp : new Date(timestamp);
  const sb = getStemBranch(dt);
  const hex = selectHexagram(taskType);

  const stemBranch = `${sb.yearStem}${sb.yearBranch}·${sb.monthStem}${sb.dayBranch}·${sb.dayStem}${sb.dayBranch}`;
  const hashInput = `${uid}${device}${dt.toISOString()}${taskType}`;
  const hash = sha256hex(hashInput).slice(0, 8);

  return `#LongHun⚡️${stemBranch}·${sb.shichen}·${hex.symbol}${hex.en}-DNA-GENERATOR-v1.0.0-${hash}`;
}

module.exports = { generateDnaCode, getStemBranch, selectHexagram, sha256hex, TIAN_GAN, DI_ZHI, HEXAGRAMS };
