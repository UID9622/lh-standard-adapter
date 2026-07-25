/**
 * Validator — DNA and audit format validation.
 *
 * DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷝Li-VALIDATOR-v1.0.0
 */

import type {
  BehaviorPattern,
  BehaviorSignature,
  ValidationResult,
} from './types.js';

// ── DNA v∞ validation regex ──────────────────────────────────────────────────

const DNA_REGEX = /^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([䷀-䷿][A-Za-z]+)-(.+)-([a-f0-9]{8})$/;

// ── Validation constants ─────────────────────────────────────────────────────

const REQUIRED_TOP_KEYS = new Set(['dna', 'audit', 'payload', 'meta']);
const REQUIRED_AUDIT_KEYS = new Set(['audit_version', 'uid', 'behavior_signature', 'behavior_pattern', 'behavior_labels', 'color']);
const REQUIRED_SIG_KEYS = new Set(['P', 'F', 'T', 'E', 'C', 'R', 'A', 'X', 'Y', 'Z']);
const VALID_COLORS = new Set(['🟢', '🟡', '🔴']);
const VALID_PATTERNS: Set<string> = new Set([
  'MODE-DefensiveDefaulter',
  'MODE-ExternalTrustSpender',
  'MODE-InternalDestroyer',
  'MODE-Fluctuating',
  'MODE-StableDisciplined',
]);
const VALID_P_VALUES = new Set(['HasPromise', 'NoPromise']);
const VALID_F_VALUES = new Set(['Fulfilled', 'Unfulfilled', 'Partial']);
const VALID_E_VALUES = new Set(['Willing', 'Perfunctory', 'Resentful', 'Numb']);
const VALID_A_VALUES = new Set(['Self', 'Partner', 'Family', 'Outsider', 'Public']);
const VALID_X_VALUES = new Set(['OverExplain', 'Silent', 'Genuine', 'Indifferent']);
const VALID_Y_VALUES = new Set(['Changed', 'Resisted', 'Indifferent', 'NoResponse']);

// ── Validator ────────────────────────────────────────────────────────────────

export class Validator {
  readonly errors: string[] = [];
  readonly warnings: string[] = [];

  /**
   * Validate a wrapped payload.
   *
   * @param wrapped - The wrapped payload object to validate
   * @returns Validation result with errors and warnings
   */
  validate(wrapped: Record<string, unknown>): ValidationResult {
    this.errors.length = 0;
    this.warnings.length = 0;

    if (!wrapped || typeof wrapped !== 'object' || Object.keys(wrapped).length === 0) {
      this.errors.push('Input is not a non-empty object');
      return this.result();
    }

    // 1. Top-level keys
    const topKeys = new Set(Object.keys(wrapped));
    const missing = [...REQUIRED_TOP_KEYS].filter(k => !topKeys.has(k));
    if (missing.length > 0) {
      this.errors.push(`Missing top-level keys: ${missing.join(', ')}`);
    }

    // 2. DNA validation
    const dna = wrapped.dna;
    if (!dna || typeof dna !== 'string') {
      this.errors.push('DNA field is empty or not a string');
    } else {
      const match = DNA_REGEX.exec(dna);
      if (!match) {
        this.errors.push(`DNA does not match regex: ${dna.slice(0, 60)}...`);
      } else {
        const hash8 = match[7];
        if (hash8.length !== 8 || !/^[0-9a-f]{8}$/.test(hash8)) {
          this.errors.push(`Invalid hash8: ${hash8}`);
        }
      }
    }

    // 3. Audit validation
    const audit = wrapped.audit;
    if (!audit || typeof audit !== 'object') {
      this.errors.push('Audit is not an object');
    } else {
      this.validateAudit(audit as Record<string, unknown>);

      // 4. UID consistency check
      const meta = wrapped.meta as Record<string, unknown> | undefined;
      if (meta && typeof meta === 'object') {
        const metaUid = meta.uid as string | undefined;
        const auditUid = (audit as Record<string, unknown>).uid as string | undefined;
        if (metaUid && auditUid) {
          const auditUidClean = (auditUid as string).replace('UID', '');
          if (metaUid !== auditUidClean) {
            this.errors.push(`UID mismatch: meta.uid=${metaUid}, audit.uid=${auditUid}`);
          }
        }
      }
    }

    return this.result();
  }

  private validateAudit(audit: Record<string, unknown>): void {
    // Required keys
    const auditKeys = new Set(Object.keys(audit));
    const missing = [...REQUIRED_AUDIT_KEYS].filter(k => !auditKeys.has(k));
    if (missing.length > 0) {
      this.errors.push(`Missing audit keys: ${missing.join(', ')}`);
    }

    // behavior_signature
    const sig = audit.behavior_signature as Record<string, unknown> | undefined;
    if (!sig || typeof sig !== 'object') {
      this.errors.push('behavior_signature is not an object');
    } else {
      const sigKeys = new Set(Object.keys(sig));
      const missingSig = [...REQUIRED_SIG_KEYS].filter(k => !sigKeys.has(k));
      if (missingSig.length > 0) {
        this.errors.push(`Missing signature keys: ${missingSig.join(', ')}`);
      } else {
        this.validateSigValues(sig as Record<string, unknown>);
      }
    }

    // pattern
    const pattern = audit.behavior_pattern as string;
    if (pattern && !VALID_PATTERNS.has(pattern)) {
      this.warnings.push(`Unknown behavior pattern: ${pattern}`);
    }

    // color
    const color = audit.color as string;
    if (color && !VALID_COLORS.has(color)) {
      this.warnings.push(`Unknown audit color: ${color}`);
    }

    // payload_hash
    const ph = audit.payload_hash as string | undefined;
    if (ph && (ph.length !== 16 || !/^[0-9a-f]{16}$/.test(ph))) {
      this.warnings.push(`Suspicious payload_hash: ${ph}`);
    }
  }

  private validateSigValues(sig: Record<string, unknown>): void {
    const checks: [unknown, Set<string> | boolean, string][] = [
      [sig.P, VALID_P_VALUES, 'P'],
      [sig.F, VALID_F_VALUES, 'F'],
      [typeof sig.T === 'number', true, 'T (number)'],
      [sig.E, VALID_E_VALUES, 'E'],
      [typeof sig.C === 'number', true, 'C (number)'],
      [Number.isInteger(sig.R) && (sig.R as number) >= 0, true, 'R (int >= 0)'],
      [sig.A, VALID_A_VALUES, 'A'],
      [sig.X, VALID_X_VALUES, 'X'],
      [sig.Y, VALID_Y_VALUES, 'Y'],
      [typeof sig.Z === 'number', true, 'Z (number)'],
    ];

    for (const [actual, expected, label] of checks) {
      if (expected === true) {
        if (!actual) {
          this.warnings.push(`Invalid ${label}`);
        }
      } else if (expected instanceof Set) {
        if (typeof actual === 'string' && !expected.has(actual)) {
          this.warnings.push(`Invalid ${label}: '${actual}'`);
        }
      }
    }
  }

  private result(): ValidationResult {
    const valid = this.errors.length === 0;
    let summary: string;
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
      errors: [...this.errors],
      warnings: [...this.warnings],
      summary,
    };
  }
}

// ── Convenience functions ────────────────────────────────────────────────────

/**
 * Quick check: has required keys and valid DNA format?
 */
export function quickValidate(wrapped: Record<string, unknown>): boolean {
  if (!wrapped || typeof wrapped !== 'object') return false;
  const keys = new Set(Object.keys(wrapped));
  if (!keys.has('dna') || !keys.has('audit')) return false;
  const dna = wrapped.dna;
  if (typeof dna !== 'string') return false;
  return DNA_REGEX.test(dna);
}