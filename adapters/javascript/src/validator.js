/**
 * Validator — DNA and audit format validation.
 *
 * DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷝Li-VALIDATOR-v1.0.0
 */

// --- DNA v∞ validation regex ---
// Matches the Python regex exactly.
const DNA_REGEX = new RegExp(
    "^#LongHun⚡️" +
    "([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)" +  // Four pillars
    "·([䷀-䷿][A-Za-z]+)" +                                                  // Hexagram
    "-(.+)" +                                                                 // Body (module-action-version)
    "-([a-f0-9]{8})$"                                                         // Hash8
);

const REQUIRED_TOP_KEYS = new Set(["dna", "audit", "payload", "meta"]);
const REQUIRED_AUDIT_KEYS = new Set([
    "audit_version", "uid", "behavior_signature",
    "behavior_pattern", "behavior_labels", "color",
]);
const REQUIRED_SIG_KEYS = new Set(["P", "F", "T", "E", "C", "R", "A", "X", "Y", "Z"]);
const VALID_COLORS = new Set(["🟢", "🟡", "🔴"]);
const VALID_PATTERNS = new Set([
    "MODE-DefensiveDefaulter", "MODE-ExternalTrustSpender",
    "MODE-InternalDestroyer", "MODE-Fluctuating", "MODE-StableDisciplined",
]);
const VALID_P_VALUES = new Set(["HasPromise", "NoPromise"]);
const VALID_F_VALUES = new Set(["Fulfilled", "Unfulfilled", "Partial"]);
const VALID_E_VALUES = new Set(["Willing", "Perfunctory", "Resentful", "Numb"]);
const VALID_A_VALUES = new Set(["Self", "Partner", "Family", "Outsider", "Public"]);
const VALID_X_VALUES = new Set(["OverExplain", "Silent", "Genuine", "Indifferent"]);
const VALID_Y_VALUES = new Set(["Changed", "Resisted", "Indifferent", "NoResponse"]);

// --- Validator class ---

export class Validator {
    constructor() {
        /** @type {string[]} */
        this.errors = [];
        /** @type {string[]} */
        this.warnings = [];
    }

    /**
     * Validate a wrapped payload.
     *
     * @param {object} wrapped — the wrapped payload object
     * @returns {{ valid: boolean, errors: string[], warnings: string[], summary: string }}
     */
    validate(wrapped) {
        this.errors = [];
        this.warnings = [];

        if (!wrapped || typeof wrapped !== 'object' || Array.isArray(wrapped)) {
            this.errors.push("Input is not a non-empty dict");
            return this._result();
        }

        const keys = Object.keys(wrapped);
        if (keys.length === 0) {
            this.errors.push("Input is not a non-empty dict");
            return this._result();
        }

        // 1. Top-level keys
        const keySet = new Set(keys);
        const missing = [...REQUIRED_TOP_KEYS].filter(k => !keySet.has(k));
        if (missing.length > 0) {
            this.errors.push(`Missing top-level keys: ${JSON.stringify(missing)}`);
        }

        // 2. DNA validation
        const dna = wrapped.dna || "";
        if (!dna) {
            this.errors.push("DNA field is empty");
        } else {
            const match = DNA_REGEX.exec(dna);
            if (!match) {
                const short = dna.length > 60 ? dna.slice(0, 60) + "..." : dna;
                this.errors.push(`DNA does not match regex: ${short}`);
            } else {
                const hash8 = match[7];
                if (hash8.length !== 8 || !/^[a-f0-9]{8}$/.test(hash8)) {
                    this.errors.push(`Invalid hash8: ${hash8}`);
                }
            }

            // 3. Audit validation
            const audit = wrapped.audit;
            if (!audit || typeof audit !== 'object' || Array.isArray(audit)) {
                this.errors.push("Audit is not a dict");
            } else {
                this._validateAudit(audit);

                // 4. UID consistency check
                const meta = wrapped.meta;
                if (meta && typeof meta === 'object' && !Array.isArray(meta)) {
                    const metaUid = meta.uid || "";
                    const auditUid = audit.uid || "";
                    if (metaUid && auditUid) {
                        const auditUidClean = auditUid.replace(/^UID/, "");
                        if (metaUid !== auditUidClean) {
                            this.errors.push(
                                `UID mismatch: meta.uid=${metaUid}, audit.uid=${auditUid}`
                            );
                        }
                    }
                }
            }
        }

        return this._result();
    }

    /**
     * Validate audit object fields.
     *
     * @param {object} audit
     */
    _validateAudit(audit) {
        const auditKeys = new Set(Object.keys(audit));

        // Required keys
        const missingAudit = [...REQUIRED_AUDIT_KEYS].filter(k => !auditKeys.has(k));
        if (missingAudit.length > 0) {
            this.errors.push(`Missing audit keys: ${JSON.stringify(missingAudit)}`);
        }

        // behavior_signature
        const sig = audit.behavior_signature;
        if (!sig || typeof sig !== 'object' || Array.isArray(sig)) {
            this.errors.push("behavior_signature is not a dict");
        } else {
            const sigKeys = new Set(Object.keys(sig));
            const missingSig = [...REQUIRED_SIG_KEYS].filter(k => !sigKeys.has(k));
            if (missingSig.length > 0) {
                this.errors.push(`Missing signature keys: ${JSON.stringify(missingSig)}`);
            } else {
                this._validateSigValues(sig);
            }
        }

        // pattern
        const pattern = audit.behavior_pattern;
        if (pattern && !VALID_PATTERNS.has(pattern)) {
            this.warnings.push(`Unknown behavior pattern: ${pattern}`);
        }

        // color
        const color = audit.color;
        if (color && !VALID_COLORS.has(color)) {
            this.warnings.push(`Unknown audit color: ${color}`);
        }

        // payload_hash
        const ph = audit.payload_hash;
        if (ph && (ph.length !== 16 || !/^[a-f0-9]{16}$/.test(ph))) {
            this.warnings.push(`Suspicious payload_hash: ${ph}`);
        }
    }

    /**
     * Validate individual signature field values.
     *
     * @param {object} sig
     */
    _validateSigValues(sig) {
        const checks = [
            { key: "P", valid: (v) => VALID_P_VALUES.has(v), label: "P" },
            { key: "F", valid: (v) => VALID_F_VALUES.has(v), label: "F" },
            { key: "T", valid: (v) => typeof v === 'number', label: "T (number)" },
            { key: "E", valid: (v) => VALID_E_VALUES.has(v), label: "E" },
            { key: "C", valid: (v) => typeof v === 'number', label: "C (number)" },
            { key: "R", valid: (v) => Number.isInteger(v) && v >= 0, label: "R (int >= 0)" },
            { key: "A", valid: (v) => VALID_A_VALUES.has(v), label: "A" },
            { key: "X", valid: (v) => VALID_X_VALUES.has(v), label: "X" },
            { key: "Y", valid: (v) => VALID_Y_VALUES.has(v), label: "Y" },
            { key: "Z", valid: (v) => typeof v === 'number', label: "Z (number)" },
        ];

        for (const { key, valid, label } of checks) {
            const val = sig[key];
            if (val !== undefined && !valid(val)) {
                this.warnings.push(`Invalid ${label}: '${val}'`);
            }
        }
    }

    /**
     * Build result object.
     *
     * @returns {{ valid: boolean, errors: string[], warnings: string[], summary: string }}
     */
    _result() {
        const valid = this.errors.length === 0;
        let summary;
        if (valid) {
            summary = `✅ VALID — ${this.warnings.length} warning(s)`;
            if (this.warnings.length > 0) {
                summary += ` (${this.warnings.slice(0, 2).join(', ')})`;
            }
        } else {
            summary = `❌ INVALID — ${this.errors.length} error(s)`;
        }
        return {
            valid,
            errors: this.errors,
            warnings: this.warnings,
            summary,
        };
    }
}

// --- Convenience function ---

/**
 * Quick check: has required keys and valid DNA format?
 *
 * @param {object} wrapped
 * @returns {boolean}
 */
export function quickValidate(wrapped) {
    if (!wrapped || typeof wrapped !== 'object' || Array.isArray(wrapped)) {
        return false;
    }
    if (!wrapped.dna || !wrapped.audit) {
        return false;
    }
    const dna = wrapped.dna;
    if (!DNA_REGEX.test(dna)) {
        return false;
    }
    return true;
}
