/**
 * Audit Wrapper — seven-factor behavioral audit metadata generation.
 *
 * DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷝Li-AUDIT-WRAPPER-v1.0.0
 */

import { createHash } from 'node:crypto';
import type {
  AuditWrapperResult,
  BehaviorPattern,
  BehaviorSignature,
} from './types.js';

// ── Seven-Factor Value Sets ──────────────────────────────────────────────────

const P_VALUES = ['HasPromise', 'NoPromise'] as const;
const F_VALUES = ['Fulfilled', 'Unfulfilled', 'Partial'] as const;
const E_VALUES = ['Willing', 'Perfunctory', 'Resentful', 'Numb'] as const;
const A_VALUES = ['Self', 'Partner', 'Family', 'Outsider', 'Public'] as const;
const X_VALUES = ['OverExplain', 'Silent', 'Genuine', 'Indifferent'] as const;
const Y_VALUES = ['Changed', 'Resisted', 'Indifferent', 'NoResponse'] as const;

// ── Behavior Pattern Classification ──────────────────────────────────────────

const PATTERNS: Record<BehaviorPattern, string> = {
  'MODE-DefensiveDefaulter': 'Promises fail + over-explains to deflect',
  'MODE-ExternalTrustSpender': 'Keeps promises to outsiders at inner-circle expense',
  'MODE-InternalDestroyer': 'Breaks promises with indifference, no correction',
  'MODE-Fluctuating': 'High volatility in commitment-to-fulfillment ratio',
  'MODE-StableDisciplined': 'Consistent, reliable execution',
};

// ── Factor → Label Mapping ───────────────────────────────────────────────────

const LABEL_MAP: Record<string, Record<string, string>> = {
  P: { HasPromise: '7F-P-有承诺', NoPromise: '7F-P-无承诺' },
  F: { Fulfilled: '7F-F-已兑现', Unfulfilled: '7F-F-未兑现', Partial: '7F-F-部分兑现' },
  E: { Willing: '7F-E-心甘情愿', Perfunctory: '7F-E-敷衍', Resentful: '7F-E-怨恨', Numb: '7F-E-麻木' },
  A: { Self: '7F-A-自己', Partner: '7F-A-伴侣', Family: '7F-A-家庭', Outsider: '7F-A-外人', Public: '7F-A-公众' },
  X: { OverExplain: '7F-X-过度解释', Silent: '7F-X-沉默', Genuine: '7F-X-真诚', Indifferent: '7F-X-冷漠' },
  Y: { Changed: '7F-Y-改正', Resisted: '7F-Y-抗拒', Indifferent: '7F-Y-无视', NoResponse: '7F-Y-无响应' },
};

// ── Wrapper ──────────────────────────────────────────────────────────────────

export class AuditWrapper {
  private readonly uid: string;

  constructor(uid = '9622') {
    this.uid = uid;
  }

  /**
   * Generate audit wrapper with seven-factor signature.
   *
   * @param payload - Raw data to wrap
   * @param taskType - Task category
   * @param persona - Persona identifier
   * @returns Audit metadata
   */
  wrap(payload: unknown, taskType = 'default', persona = 'P04'): AuditWrapperResult {
    const now = new Date();

    // Default signature (StableDisciplined baseline)
    const signature: BehaviorSignature = {
      P: 'HasPromise',
      F: 'Fulfilled',
      T: 0.0,
      E: 'Willing',
      C: 0,
      R: 0,
      A: 'Self',
      X: 'Genuine',
      Y: 'NoResponse',
      Z: 1.0,
    };

    const pattern = this.classify(signature);
    const labels = this.makeLabels(signature, pattern);
    const color = this.determineColor(pattern, signature.R);

    // Payload hash (not for crypto, for integrity check)
    const payloadJson = JSON.stringify(payload, Object.keys(payload as object).sort());
    const payloadHash = createHash('sha256').update(payloadJson, 'utf-8').digest('hex').slice(0, 16);

    return {
      audit_version: 'v1.0',
      uid: `UID${this.uid}`,
      persona,
      task_type: taskType,
      behavior_signature: signature,
      behavior_pattern: pattern,
      behavior_labels: labels,
      color,
      timestamp: now.toISOString(),
      payload_hash: payloadHash,
    };
  }

  /**
   * Classify seven-factor signature into behavior pattern.
   */
  private classify(sig: BehaviorSignature): BehaviorPattern {
    const fVal = sig.F;
    const xVal = sig.X;
    const aVal = sig.A;
    const yVal = sig.Y;
    const zVal = sig.Z;

    if (fVal === 'Unfulfilled' && xVal === 'OverExplain') {
      return 'MODE-DefensiveDefaulter';
    }
    if (fVal === 'Fulfilled' && aVal === 'Outsider') {
      return 'MODE-ExternalTrustSpender';
    }
    if (fVal === 'Unfulfilled' && yVal === 'Indifferent') {
      return 'MODE-InternalDestroyer';
    }
    if (zVal > 2.0) {
      return 'MODE-Fluctuating';
    }
    return 'MODE-StableDisciplined';
  }

  /**
   * Generate bilingual behavior labels from signature.
   */
  private makeLabels(sig: BehaviorSignature, pattern: BehaviorPattern): string[] {
    const labels: string[] = [];
    for (const factor of ['P', 'F', 'E', 'A', 'X', 'Y'] as const) {
      const val = sig[factor] as string;
      if (LABEL_MAP[factor]?.[val]) {
        labels.push(LABEL_MAP[factor][val]);
      }
    }
    labels.push(pattern);
    return labels;
  }

  /**
   * Determine three-color audit tag.
   */
  private determineColor(pattern: BehaviorPattern, repeat: number): '🟢' | '🟡' | '🔴' {
    if (pattern === 'MODE-InternalDestroyer') return '🔴';
    if (pattern === 'MODE-Fluctuating' && repeat > 3) return '🟡';
    if (pattern === 'MODE-DefensiveDefaulter' && repeat > 2) return '🟡';
    return '🟢';
  }
}

// ── Convenience singleton ────────────────────────────────────────────────────

const defaultWrapper = new AuditWrapper();

/**
 * Quick one-shot audit wrapper.
 */
export function auditWrap(payload: unknown, taskType = 'default', persona = 'P04'): AuditWrapperResult {
  return defaultWrapper.wrap(payload, taskType, persona);
}