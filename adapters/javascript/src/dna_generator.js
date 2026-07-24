/**
 * DNA Generator — v∞ format traceability code generation.
 *
 * DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-DNA-GENERATOR-v1.0.0
 *
 * Produces byte-for-byte compatible output with the Python reference implementation.
 */

import crypto from 'node:crypto';

// --- Heavenly Stems and Earthly Branches ---

export const TIAN_GAN = ["Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"];
export const DI_ZHI = ["Zi", "Chou", "Yin", "Mao", "Chen", "Si",
                         "Wu", "Wei", "Shen", "You", "Xu", "Hai"];
export const SHI_CHEN = ["ZiShi", "ChouShi", "YinShi", "MaoShi", "ChenShi", "SiShi",
                          "WuShi", "WeiShi", "ShenShi", "YouShi", "XuShi", "HaiShi"];

// --- I Ching Hexagrams ---

export const HEXAGRAMS = [
    { symbol: "䷀", en_name: "Qian",   cn_name: "乾", domain: "governance" },
    { symbol: "䷁", en_name: "Kun",    cn_name: "坤", domain: "archive" },
    { symbol: "䷂", en_name: "Zhun",   cn_name: "屯", domain: "init" },
    { symbol: "䷃", en_name: "Meng",   cn_name: "蒙", domain: "learn" },
    { symbol: "䷄", en_name: "Xu",     cn_name: "需", domain: "async" },
    { symbol: "䷅", en_name: "Song",   cn_name: "讼", domain: "legal" },
    { symbol: "䷜", en_name: "Kan",    cn_name: "坎", domain: "engine" },
    { symbol: "䷝", en_name: "Li",     cn_name: "离", domain: "audit" },
    { symbol: "䷲", en_name: "Zhen",   cn_name: "震", domain: "security" },
    { symbol: "䷳", en_name: "Gen",    cn_name: "艮", domain: "privacy" },
    { symbol: "䷸", en_name: "Xun",    cn_name: "巽", domain: "deploy" },
    { symbol: "䷹", en_name: "Dui",    cn_name: "兑", domain: "trust" },
    { symbol: "䷾", en_name: "JiJi",   cn_name: "既济", domain: "complete" },
    { symbol: "䷿", en_name: "WeiJi",  cn_name: "未济", domain: "progress" },
];

// --- Task-to-hexagram domain mapping ---

export const TASK_HEXAGRAM_MAP = {
    default:    "governance",
    code:       "engine",
    deploy:     "deploy",
    audit:      "audit",
    security:   "security",
    archive:    "archive",
    init:       "init",
    learn:      "learn",
    legal:      "legal",
    privacy:    "privacy",
    trust:      "trust",
    complete:   "complete",
    progress:   "progress",
};

// --- Internal helpers ---

const CYCLE_YEAR = 1984;
const CYCLE_MONTH = [2, 4, 6, 8, 10, 0, 2, 4, 6, 8];

/**
 * Python-safe modulo (always returns non-negative).
 */
function pymod(n, m) {
    return ((n % m) + m) % m;
}

/**
 * Format a Date as ISO 8601 with +08:00 offset (matching Python's
 * datetime.now(timezone(timedelta(hours=8))).isoformat()).
 */
function isoFormatUTC8(date) {
    const ms = date.getTime() + 8 * 3600000;
    const d = new Date(ms);
    const pad = (n) => String(n).padStart(2, '0');
    const msPad = String(d.getUTCMilliseconds()).padStart(3, '0');
    return `${d.getUTCFullYear()}-${pad(d.getUTCMonth() + 1)}-${pad(d.getUTCDate())}` +
        `T${pad(d.getUTCHours())}:${pad(d.getUTCMinutes())}:${pad(d.getUTCSeconds())}` +
        `.${msPad}+08:00`;
}

/**
 * Get day-of-year in UTC+8 timezone.
 */
function dayOfYearUTC8(date) {
    const ms = date.getTime() + 8 * 3600000;
    const d = new Date(ms);
    const year = d.getUTCFullYear();
    const startOfYear = Date.UTC(year, 0, 1);
    return Math.floor((ms - startOfYear) / 86400000) + 1;
}

// --- DNAGenerator class ---

export class DNAGenerator {
    /**
     * @param {string} uid — owner identifier
     * @param {string} device — device fingerprint
     * @param {string} locale — timezone locale (unused; Asia/Shanghai is the only supported)
     */
    constructor(uid = "9622", device = "HM-9622-001", locale = "Asia/Shanghai") {
        this.uid = uid;
        this.device = device;
        this.locale = locale;
    }

    /**
     * Generate a full DNA traceability string.
     *
     * @param {string} taskType — task category (default: "default")
     * @param {string} action — action tag (default: "WRAP")
     * @param {string|null} version — version tag (default: "V1.0")
     * @returns {string} the complete DNA string
     */
    generate(taskType = "default", action = "WRAP", version = null) {
        const now = new Date();
        const stem = this._computeStemBranch(now);
        const hexagram = this._selectHexagram(taskType);
        const ver = version || "V1.0";

        const body = `ADAPTER-${taskType.toUpperCase()}-${action.toUpperCase()}-${ver}`;

        const raw =
            `${stem.year}${stem.month}${stem.day}${stem.shichen}` +
            `${hexagram.symbol}${hexagram.en_name}` +
            `${body}` +
            `${this.device}` +
            `${isoFormatUTC8(now)}`;

        const hash8 = crypto.createHash('sha256').update(raw, 'utf8').digest('hex').slice(0, 8);

        return (
            `#LongHun⚡️${stem.year}·${stem.month}·${stem.day}·${stem.shichen}` +
            `·${hexagram.symbol}${hexagram.en_name}` +
            `-${body}-${hash8}`
        );
    }

    /**
     * Compute Heavenly Stem + Earthly Branch for a given UTC date
     * (converted internally to UTC+8 for pillar computation).
     *
     * @param {Date} date — a JavaScript Date object
     * @returns {{ year: string, month: string, day: string, shichen: string }}
     */
    _computeStemBranch(date) {
        // Convert to UTC+8 local components
        const ms = date.getTime() + 8 * 3600000;
        const d = new Date(ms);
        const year = d.getUTCFullYear();
        const month = d.getUTCMonth() + 1;  // 1-12
        const hour = d.getUTCHours();
        const yday = dayOfYearUTC8(date);

        const yearStemIdx = pymod(year - CYCLE_YEAR, 10);
        const yearBranchIdx = pymod(year - CYCLE_YEAR, 12);

        const cycleIdx = pymod(year - CYCLE_YEAR, 10);
        const monthStemIdx = pymod(CYCLE_MONTH[cycleIdx] + (month - 1), 10);
        const monthBranchIdx = pymod(month + 1, 12);

        const dayStemIdx = pymod(year - 1900 + Math.floor((year - 1900) / 4) + yday, 10);
        const dayBranchIdx = pymod(year - 1900 + Math.floor((year - 1900) / 4) + yday, 12);

        const shichenIdx = Math.floor(hour / 2);

        return {
            year: TIAN_GAN[yearStemIdx] + DI_ZHI[yearBranchIdx],
            month: TIAN_GAN[monthStemIdx] + DI_ZHI[monthBranchIdx],
            day: TIAN_GAN[dayStemIdx] + DI_ZHI[dayBranchIdx],
            shichen: SHI_CHEN[shichenIdx],
        };
    }

    /**
     * Select I Ching hexagram based on task type.
     *
     * @param {string} taskType
     * @returns {{ symbol: string, en_name: string, cn_name: string, domain: string }}
     */
    _selectHexagram(taskType) {
        const domain = TASK_HEXAGRAM_MAP[taskType] || "governance";
        const candidates = HEXAGRAMS.filter(h => h.domain === domain);
        if (candidates.length > 0) {
            return candidates[0];
        }
        return HEXAGRAMS[0];  // Default: Qian (governance)
    }
}

// --- Convenience function ---

const _defaultGenerator = new DNAGenerator();

/**
 * Quick one-shot DNA generation.
 *
 * @param {string} taskType
 * @param {string} action
 * @param {string|null} version
 * @returns {string}
 */
export function generateDna(taskType = "default", action = "WRAP", version = null) {
    return _defaultGenerator.generate(taskType, action, version);
}
