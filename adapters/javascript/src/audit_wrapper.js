/**
 * Audit Wrapper — seven-factor behavioral audit metadata generation.
 *
 * DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷝Li-AUDIT-WRAPPER-v1.0.0
 */

import crypto from 'node:crypto';

// --- Seven-Factor Value Sets (public standard) ---

export const P_VALUES = ["HasPromise", "NoPromise"];
export const F_VALUES = ["Fulfilled", "Unfulfilled", "Partial"];
export const E_VALUES = ["Willing", "Perfunctory", "Resentful", "Numb"];
export const A_VALUES = ["Self", "Partner", "Family", "Outsider", "Public"];
export const X_VALUES = ["OverExplain", "Silent", "Genuine", "Indifferent"];
export const Y_VALUES = ["Changed", "Resisted", "Indifferent", "NoResponse"];

// --- Behavior Pattern Classification ---

export const PATTERNS = {
    "MODE-DefensiveDefaulter":   "Promises fail + over-explains to deflect",
    "MODE-ExternalTrustSpender": "Keeps promises to outsiders at inner-circle expense",
    "MODE-InternalDestroyer":    "Breaks promises with indifference, no correction",
    "MODE-Fluctuating":          "High volatility in commitment-to-fulfillment ratio",
    "MODE-StableDisciplined":    "Consistent, reliable execution",
};

// --- Factor → Label Mapping (bilingual) ---

export const LABEL_MAP = {
    P: { HasPromise: "7F-P-有承诺", NoPromise: "7F-P-无承诺" },
    F: { Fulfilled: "7F-F-已兑现", Unfulfilled: "7F-F-未兑现", Partial: "7F-F-部分兑现" },
    E: { Willing: "7F-E-心甘情愿", Perfunctory: "7F-E-敷衍", Resentful: "7F-E-怨恨", Numb: "7F-E-麻木" },
    A: { Self: "7F-A-自己", Partner: "7F-A-伴侣", Family: "7F-A-家庭", Outsider: "7F-A-外人", Public: "7F-A-公众" },
    X: { OverExplain: "7F-X-过度解释", Silent: "7F-X-沉默", Genuine: "7F-X-真诚", Indifferent: "7F-X-冷漠" },
    Y: { Changed: "7F-Y-改正", Resisted: "7F-Y-抗拒", Indifferent: "7F-Y-无视", NoResponse: "7F-Y-无响应" },
};

// --- ISO 8601 formatter for UTC+8 ---

function isoFormatUTC8(date) {
    const ms = date.getTime() + 8 * 3600000;
    const d = new Date(ms);
    const pad = (n) => String(n).padStart(2, '0');
    const msPad = String(d.getUTCMilliseconds()).padStart(3, '0');
    return `${d.getUTCFullYear()}-${pad(d.getUTCMonth() + 1)}-${pad(d.getUTCDate())}` +
        `T${pad(d.getUTCHours())}:${pad(d.getUTCMinutes())}:${pad(d.getUTCSeconds())}` +
        `.${msPad}+08:00`;
}

// --- AuditWrapper class ---

export class AuditWrapper {
    /**
     * @param {string} uid — owner identifier
     */
    constructor(uid = "9622") {
        this.uid = uid;
    }

    /**
     * Generate audit wrapper with seven-factor signature.
     *
     * @param {*} payload — raw data to wrap
     * @param {string} taskType — task category
     * @param {string} persona — persona identifier
     * @returns {object} audit metadata object
     */
    wrap(payload, taskType = "default", persona = "P04") {
        const now = new Date();

        // Default signature (StableDisciplined baseline)
        const signature = {
            P: "HasPromise",
            F: "Fulfilled",
            T: 0.0,
            E: "Willing",
            C: 0,
            R: 0,
            A: "Self",
            X: "Genuine",
            Y: "NoResponse",
            Z: 1.0,
        };

        const pattern = this._classify(signature);
        const labels = this._makeLabels(signature, pattern);
        const color = this._determineColor(pattern, signature.R);

        // Payload hash (not for crypto, for integrity check)
        // Match Python: json.dumps(payload, sort_keys=True, default=str, ensure_ascii=False)
        const payloadJson = JSON.stringify(payload, (key, value) => {
            if (typeof value === 'bigint') return Number(value);
            if (value instanceof Date) return value.toISOString();
            return value;
        });
        const payloadHash = crypto.createHash('sha256').update(payloadJson, 'utf8').digest('hex').slice(0, 16);

        return {
            audit_version: "v1.0",
            uid: `UID${this.uid}`,
            persona: persona,
            task_type: taskType,
            behavior_signature: signature,
            behavior_pattern: pattern,
            behavior_labels: labels,
            color: color,
            timestamp: isoFormatUTC8(now),
            payload_hash: payloadHash,
        };
    }

    /**
     * Classify seven-factor signature into behavior pattern.
     *
     * @param {object} sig — behavior signature
     * @returns {string} pattern name
     */
    _classify(sig) {
        const fVal = sig.F || "";
        const xVal = sig.X || "";
        const aVal = sig.A || "";
        const yVal = sig.Y || "";
        const zVal = sig.Z || 1.0;

        if (fVal === "Unfulfilled" && xVal === "OverExplain") {
            return "MODE-DefensiveDefaulter";
        }
        if (fVal === "Fulfilled" && aVal === "Outsider") {
            return "MODE-ExternalTrustSpender";
        }
        if (fVal === "Unfulfilled" && yVal === "Indifferent") {
            return "MODE-InternalDestroyer";
        }
        if (zVal > 2.0) {
            return "MODE-Fluctuating";
        }
        return "MODE-StableDisciplined";
    }

    /**
     * Generate bilingual behavior labels from signature.
     *
     * @param {object} sig — behavior signature
     * @param {string} pattern — classified pattern
     * @returns {string[]} array of bilingual labels
     */
    _makeLabels(sig, pattern) {
        const labels = [];
        for (const factor of ["P", "F", "E", "A", "X", "Y"]) {
            const val = sig[factor];
            if (LABEL_MAP[factor] && LABEL_MAP[factor][val]) {
                labels.push(LABEL_MAP[factor][val]);
            }
        }
        labels.push(pattern);
        return labels;
    }

    /**
     * Determine three-color audit tag.
     *
     * @param {string} pattern — behavior pattern
     * @param {number} repeat — cumulative repeat count
     * @returns {string} emoji color indicator
     */
    _determineColor(pattern, repeat) {
        if (pattern === "MODE-InternalDestroyer") {
            return "🔴";
        }
        if (pattern === "MODE-Fluctuating" && repeat > 3) {
            return "🟡";
        }
        if (pattern === "MODE-DefensiveDefaulter" && repeat > 2) {
            return "🟡";
        }
        return "🟢";
    }
}

// --- Convenience function ---

const _defaultWrapper = new AuditWrapper();

/**
 * Quick one-shot audit wrapper.
 *
 * @param {*} payload
 * @param {string} taskType
 * @param {string} persona
 * @returns {object}
 */
export function auditWrap(payload, taskType = "default", persona = "P04") {
    return _defaultWrapper.wrap(payload, taskType, persona);
}
