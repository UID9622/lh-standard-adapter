/**
 * DNA Generator — v∞ format traceability code generation.
 */
import crypto from 'crypto';

const TIAN_GAN = ["Jia","Yi","Bing","Ding","Wu","Ji","Geng","Xin","Ren","Gui"];
const DI_ZHI = ["Zi","Chou","Yin","Mao","Chen","Si","Wu","Wei","Shen","You","Xu","Hai"];
const SHI_CHEN = ["ZiShi","ChouShi","YinShi","MaoShi","ChenShi","SiShi","WuShi","WeiShi","ShenShi","YouShi","XuShi","HaiShi"];

const HEXAGRAMS = [
  {symbol:"䷀",enName:"Qian",cnName:"乾",domain:"governance"},
  {symbol:"䷁",enName:"Kun",cnName:"坤",domain:"archive"},
  {symbol:"䷂",enName:"Zhun",cnName:"屯",domain:"init"},
  {symbol:"䷃",enName:"Meng",cnName:"蒙",domain:"learn"},
  {symbol:"䷄",enName:"Xu",cnName:"需",domain:"async"},
  {symbol:"䷅",enName:"Song",cnName:"讼",domain:"legal"},
  {symbol:"䷜",enName:"Kan",cnName:"坎",domain:"engine"},
  {symbol:"䷝",enName:"Li",cnName:"离",domain:"audit"},
  {symbol:"䷲",enName:"Zhen",cnName:"震",domain:"security"},
  {symbol:"䷳",enName:"Gen",cnName:"艮",domain:"privacy"},
  {symbol:"䷸",enName:"Xun",cnName:"巽",domain:"deploy"},
  {symbol:"䷹",enName:"Dui",cnName:"兑",domain:"trust"},
  {symbol:"䷾",enName:"JiJi",cnName:"既济",domain:"complete"},
  {symbol:"䷿",enName:"WeiJi",cnName:"未济",domain:"progress"}
];

const TASK_HEXAGRAM_MAP = {
  default:"governance", code:"engine", deploy:"deploy", audit:"audit",
  security:"security", archive:"archive", init:"init", learn:"learn",
  legal:"legal", privacy:"privacy", trust:"trust", complete:"complete", progress:"progress"
};

export class DNAGenerator {
  constructor(uid="9622", device="HM-9622-001", locale="Asia/Shanghai") {
    this.uid = uid; this.device = device; this.locale = locale;
  }

  generate(taskType="default", action="WRAP", version=null) {
    const now = new Date();
    const stem = this._computeStemBranch(now);
    const hexagram = this._selectHexagram(taskType);
    version = version || "V1.0";
    const body = `ADAPTER-${taskType.toUpperCase()}-${action.toUpperCase()}-${version}`;
    const raw = `${stem.year}${stem.month}${stem.day}${stem.shichen}${hexagram.symbol}${hexagram.enName}${body}${this.device}${now.toISOString()}`;
    const hash8 = crypto.createHash('sha256').update(raw, 'utf8').digest('hex').slice(0,8);
    return `#LongHun⚡️${stem.year}·${stem.month}·${stem.day}·${stem.shichen}·${hexagram.symbol}${hexagram.enName}-${body}-${hash8}`;
  }

  _computeStemBranch(dt) {
    const CYCLE_YEAR = 1984;
    const CYCLE_MONTH = [2,4,6,8,10,0,2,4,6,8,10,0];
    const year = dt.getUTCFullYear(), month = dt.getUTCMonth() + 1;
    const dayOfYear = Math.floor((dt - new Date(Date.UTC(year,0,0))) / 86400000);
    const hour = dt.getUTCHours();
    const yearStemIdx = (year - CYCLE_YEAR) % 10;
    const yearBranchIdx = (year - CYCLE_YEAR) % 12;
    const msBase = CYCLE_MONTH[(year - CYCLE_YEAR) % 10] || 0;
    const monthStemIdx = (msBase + month - 1) % 10;
    const monthBranchIdx = (month + 1) % 12;
    const dayStemIdx = (year - 1900 + Math.floor((year - 1900) / 4) + dayOfYear) % 10;
    const dayBranchIdx = (year - 1900 + Math.floor((year - 1900) / 4) + dayOfYear) % 12;
    const shichenIdx = Math.floor(hour / 2);
    return {
      year: TIAN_GAN[yearStemIdx] + DI_ZHI[yearBranchIdx],
      month: TIAN_GAN[(monthStemIdx % 10 + 10) % 10] + DI_ZHI[monthBranchIdx],
      day: TIAN_GAN[(dayStemIdx % 10 + 10) % 10] + DI_ZHI[(dayBranchIdx % 12 + 12) % 12],
      shichen: SHI_CHEN[shichenIdx]
    };
  }

  _selectHexagram(taskType) {
    const domain = TASK_HEXAGRAM_MAP[taskType] || "governance";
    const candidates = HEXAGRAMS.filter(h => h.domain === domain);
    return candidates.length > 0 ? candidates[0] : HEXAGRAMS[0];
  }
}

const _generator = new DNAGenerator();
export function generateDna(taskType="default", action="WRAP", version=null) {
  return _generator.generate(taskType, action, version);
}
