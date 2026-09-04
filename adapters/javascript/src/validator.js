/**
 * Validator — DNA and audit format validation.
 */
const DNA_REGEX = /^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([\u4DC0-\u4DFF][A-Za-z]+)-(.+)-([a-f0-9]{8})$/;

const REQUIRED_TOP_KEYS = new Set(["dna","audit","payload","meta"]);
const REQUIRED_AUDIT_KEYS = new Set(["audit_version","uid","behavior_signature","behavior_pattern","behavior_labels","color"]);
const REQUIRED_SIG_KEYS = new Set(["P","F","T","E","C","R","A","X","Y","Z"]);
const VALID_COLORS = new Set(["🟢","🟡","🔴"]);
const VALID_PATTERNS = new Set(["MODE-DefensiveDefaulter","MODE-ExternalTrustSpender","MODE-InternalDestroyer","MODE-Fluctuating","MODE-StableDisciplined"]);
const VALID_P = new Set(["HasPromise","NoPromise"]);
const VALID_F = new Set(["Fulfilled","Unfulfilled","Partial"]);
const VALID_E = new Set(["Willing","Perfunctory","Resentful","Numb"]);
const VALID_A = new Set(["Self","Partner","Family","Outsider","Public"]);
const VALID_X = new Set(["OverExplain","Silent","Genuine","Indifferent"]);
const VALID_Y = new Set(["Changed","Resisted","Indifferent","NoResponse"]);

export class Validator {
  constructor() { this.errors = []; this.warnings = []; }

  validate(wrapped) {
    this.errors = []; this.warnings = [];
    if (typeof wrapped !== 'object' || !wrapped || Array.isArray(wrapped)) {
      this.errors.push("Input is not a non-empty object");
      return this._result();
    }
    const missing = [...REQUIRED_TOP_KEYS].filter(k => !(k in wrapped));
    if (missing.length) this.errors.push(`Missing top-level keys: ${missing.join(', ')}`);

    const dna = wrapped.dna || "";
    if (!dna) { this.errors.push("DNA field is empty"); }
    else {
      const match = DNA_REGEX.exec(dna);
      if (!match) { this.errors.push(`DNA does not match regex: ${dna.slice(0,60)}...`); }
      else {
        const hash8 = match[7];
        if (!/^[a-f0-9]{8}$/.test(hash8)) this.errors.push(`Invalid hash8: ${hash8}`);
      }
    }

    const audit = wrapped.audit || {};
    if (typeof audit !== 'object' || Array.isArray(audit)) {
      this.errors.push("Audit is not an object");
    } else {
      this._validateAudit(audit);
      const meta = wrapped.meta || {};
      if (typeof meta === 'object' && !Array.isArray(meta)) {
        const metaUid = meta.uid || "";
        const auditUid = (audit.uid || "").replace("UID","");
        if (metaUid && auditUid && metaUid !== auditUid) {
          this.errors.push(`UID mismatch: meta.uid=${metaUid}, audit.uid=${audit.uid}`);
        }
      }
    }
    return this._result();
  }

  _validateAudit(audit) {
    const missing = [...REQUIRED_AUDIT_KEYS].filter(k => !(k in audit));
    if (missing.length) this.errors.push(`Missing audit keys: ${missing.join(', ')}`);
    const sig = audit.behavior_signature || {};
    if (typeof sig !== 'object' || Array.isArray(sig)) {
      this.errors.push("behavior_signature is not an object");
    } else {
      const sigMiss = [...REQUIRED_SIG_KEYS].filter(k => !(k in sig));
      if (sigMiss.length) this.errors.push(`Missing signature keys: ${sigMiss.join(', ')}`);
      else this._validateSigValues(sig);
    }
    const pattern = audit.behavior_pattern || "";
    if (pattern && !VALID_PATTERNS.has(pattern)) this.warnings.push(`Unknown behavior pattern: ${pattern}`);
    const color = audit.color || "";
    if (color && !VALID_COLORS.has(color)) this.warnings.push(`Unknown audit color: ${color}`);
    const ph = audit.payload_hash || "";
    if (ph && !/^[a-f0-9]{16}$/.test(ph)) this.warnings.push(`Suspicious payload_hash: ${ph}`);
  }

  _validateSigValues(sig) {
    const checks = [
      [sig.P, VALID_P, "P"], [sig.F, VALID_F, "F"],
      [typeof sig.T === 'number', null, "T (number)"],
      [sig.E, VALID_E, "E"], [typeof sig.C === 'number', null, "C (number)"],
      [Number.isInteger(sig.R) && sig.R >= 0, null, "R (int >= 0)"],
      [sig.A, VALID_A, "A"], [sig.X, VALID_X, "X"],
      [sig.Y, VALID_Y, "Y"], [typeof sig.Z === 'number', null, "Z (number)"]
    ];
    for (const [actual, expected, label] of checks) {
      if (expected === null) { if (!actual) this.warnings.push(`Invalid ${label}`); }
      else { if (!expected.has(actual)) this.warnings.push(`Invalid ${label}: '${actual}'`); }
    }
  }

  _result() {
    const valid = this.errors.length === 0;
    const summary = valid
      ? `✅ VALID — ${this.warnings.length} warning(s)` + (this.warnings.length ? ` (${this.warnings.slice(0,2).join(', ')})` : "")
      : `❌ INVALID — ${this.errors.length} error(s)`;
    return { valid, errors: this.errors, warnings: this.warnings, summary };
  }
}

export function quickValidate(wrapped) {
  if (typeof wrapped !== 'object' || !wrapped || Array.isArray(wrapped)) return false;
  if (!wrapped.dna || !wrapped.audit) return false;
  return DNA_REGEX.test(wrapped.dna);
}
