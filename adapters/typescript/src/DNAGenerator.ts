/**
 * DNA Generator — v∞ format traceability code generation.
 *
 * DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-DNA-GENERATOR-v1.0.0
 */

import { createHash } from 'node:crypto';
import type { Hexagram, StemBranch } from './types.js';

// ── Heavenly Stems and Earthly Branches ──────────────────────────────────────

const TIAN_GAN = ['Jia', 'Yi', 'Bing', 'Ding', 'Wu', 'Ji', 'Geng', 'Xin', 'Ren', 'Gui'];
const DI_ZHI = ['Zi', 'Chou', 'Yin', 'Mao', 'Chen', 'Si', 'Wu', 'Wei', 'Shen', 'You', 'Xu', 'Hai'];
const SHI_CHEN = ['ZiShi', 'ChouShi', 'YinShi', 'MaoShi', 'ChenShi', 'SiShi', 'WuShi', 'WeiShi', 'ShenShi', 'YouShi', 'XuShi', 'HaiShi'];

// ── I Ching Hexagrams ────────────────────────────────────────────────────────

const HEXAGRAMS: Hexagram[] = [
  { symbol: '䷀', enName: 'Qian', cnName: '乾', domain: 'governance' },
  { symbol: '䷁', enName: 'Kun', cnName: '坤', domain: 'archive' },
  { symbol: '䷂', enName: 'Zhun', cnName: '屯', domain: 'init' },
  { symbol: '䷃', enName: 'Meng', cnName: '蒙', domain: 'learn' },
  { symbol: '䷄', enName: 'Xu', cnName: '需', domain: 'async' },
  { symbol: '䷅', enName: 'Song', cnName: '讼', domain: 'legal' },
  { symbol: '䷜', enName: 'Kan', cnName: '坎', domain: 'engine' },
  { symbol: '䷝', enName: 'Li', cnName: '离', domain: 'audit' },
  { symbol: '䷲', enName: 'Zhen', cnName: '震', domain: 'security' },
  { symbol: '䷳', enName: 'Gen', cnName: '艮', domain: 'privacy' },
  { symbol: '䷸', enName: 'Xun', cnName: '巽', domain: 'deploy' },
  { symbol: '䷹', enName: 'Dui', cnName: '兑', domain: 'trust' },
  { symbol: '䷾', enName: 'JiJi', cnName: '既济', domain: 'complete' },
  { symbol: '䷿', enName: 'WeiJi', cnName: '未济', domain: 'progress' },
];

// ── Task-to-hexagram domain mapping ──────────────────────────────────────────

const TASK_HEXAGRAM_MAP: Record<string, string> = {
  default: 'governance',
  code: 'engine',
  deploy: 'deploy',
  audit: 'audit',
  security: 'security',
  archive: 'archive',
  init: 'init',
  learn: 'learn',
  legal: 'legal',
  privacy: 'privacy',
  trust: 'trust',
  complete: 'complete',
  progress: 'progress',
};

// ── Month stem offsets ───────────────────────────────────────────────────────

const CYCLE_YEAR = 1984; // JiaZi year reference
const CYCLE_MONTH = [2, 4, 6, 8, 10, 0, 2, 4, 6, 8, 10, 0];

// ── Generator ────────────────────────────────────────────────────────────────

export class DNAGenerator {
  private readonly uid: string;
  private readonly device: string;

  constructor(uid = '9622', device = 'HM-9622-001') {
    this.uid = uid;
    this.device = device;
  }

  /**
   * Generate a full DNA traceability string.
   *
   * Format: #LongHun⚡️{StemBranch}·{Hexagram}-{ModulePath}-{Hash8}
   *
   * @param taskType - Task category (default, code, audit, etc.)
   * @param action - Action type (WRAP, VALIDATE, etc.)
   * @param version - Optional version string (default: V1.0)
   * @returns DNA traceability code string
   */
  generate(taskType = 'default', action = 'WRAP', version?: string): string {
    const now = new Date();
    const stem = this.computeStemBranch(now);
    const hexagram = this.selectHexagram(taskType);
    const ver = version ?? 'V1.0';
    const body = `ADAPTER-${taskType.toUpperCase()}-${action.toUpperCase()}-${ver}`;

    const raw = `${stem.year}${stem.month}${stem.day}${stem.shichen}` +
      `${hexagram.symbol}${hexagram.enName}${body}${this.device}${now.toISOString()}`;

    const hash8 = createHash('sha256').update(raw, 'utf-8').digest('hex').slice(0, 8);

    return `#LongHun⚡️${stem.year}·${stem.month}·${stem.day}·${stem.shichen}` +
      `·${hexagram.symbol}${hexagram.enName}-${body}-${hash8}`;
  }

  /**
   * Compute Heavenly Stem + Earthly Branch for a given datetime.
   */
  private computeStemBranch(dt: Date): StemBranch {
    const year = dt.getFullYear();
    const month = dt.getMonth() + 1; // 1-indexed
    const day = dt.getDate();
    const hour = dt.getHours();

    // Year pillar
    const yearStemIdx = (year - CYCLE_YEAR) % 10;
    const yearBranchIdx = (year - CYCLE_YEAR) % 12;

    // Month pillar
    const cycleIdx = (year - CYCLE_YEAR) % 10;
    const monthStemBase = CYCLE_MONTH[cycleIdx];
    const monthStemIdx = (monthStemBase + (month - 1)) % 10;
    const monthBranchIdx = (month + 1) % 12;

    // Day pillar (approximate — use a proper astronomical library for precision)
    const dayOff = this.dayOffset(year, month, day);
    const dayStemIdx = dayOff % 10;
    const dayBranchIdx = dayOff % 12;

    // Shichen (2-hour period)
    const shichenIdx = Math.floor(hour / 2);

    return {
      year: TIAN_GAN[this.posMod(yearStemIdx, 10)] + DI_ZHI[this.posMod(yearBranchIdx, 12)],
      month: TIAN_GAN[this.posMod(monthStemIdx, 10)] + DI_ZHI[this.posMod(monthBranchIdx, 12)],
      day: TIAN_GAN[this.posMod(dayStemIdx, 10)] + DI_ZHI[this.posMod(dayBranchIdx, 12)],
      shichen: SHI_CHEN[shichenIdx],
    };
  }

  /**
   * Approximate day offset from a reference date for stem-branch calculation.
   */
  private dayOffset(year: number, month: number, day: number): number {
    // Reference: 1900-01-01 is JiaZi day (day 0)
    let total = 0;
    for (let y = 1900; y < year; y++) {
      total += this.isLeapYear(y) ? 366 : 365;
    }
    const daysInMonth = [31, this.isLeapYear(year) ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
    for (let m = 0; m < month - 1; m++) {
      total += daysInMonth[m];
    }
    total += day - 1;
    return total;
  }

  private isLeapYear(y: number): boolean {
    return (y % 4 === 0 && y % 100 !== 0) || (y % 400 === 0);
  }

  private posMod(n: number, m: number): number {
    return ((n % m) + m) % m;
  }

  /**
   * Select I Ching hexagram based on task type.
   */
  private selectHexagram(taskType: string): Hexagram {
    const domain = TASK_HEXAGRAM_MAP[taskType] ?? 'governance';
    const candidates = HEXAGRAMS.filter(h => h.domain === domain);
    return candidates.length > 0 ? candidates[0] : HEXAGRAMS[0]; // Default: Qian
  }
}

// ── Convenience singleton ────────────────────────────────────────────────────

const defaultGenerator = new DNAGenerator();

/**
 * Quick one-shot DNA generation.
 */
export function generateDNA(taskType = 'default', action = 'WRAP', version?: string): string {
  return defaultGenerator.generate(taskType, action, version);
}