/**
 * Audit Wrapper — seven-factor behavioral audit metadata generation.
 */

import { createHash } from 'crypto';

const P_VALUES = ["HasPromise", "NoPromise"];
const F_VALUES = ["Fulfilled", "Unfulfilled", "Partial"];
const E_VALUES = ["Willing", "Perfunctory", "Resentful", "Numb"];
const A_VALUES = ["Self", "Partner", "Family", "Outsider", "Public"];
const X_VALUES = ["OverExplain", "Silent", "Genuine", "Indifferent"];
const Y_VALUES = ["Changed", "Resisted", "Indifferent", "NoResponse"];

const PATTERNS: Record<string, string> = {
  "MODE-DefensiveDefaulter": "Promises fail + over-explains to deflect",
  "MODE-ExternalTrustSpender": "Keeps promises to outsiders at inner-circle expense",
  "MODE-InternalDestroyer": "Breaks promises with indifference, no correction",
  "MODE-Fluctuating": "High volatility in commitment-to-fulfillment ratio",
  "MODE-StableDisciplined": "Consistent, reliable execution",
};

const LABEL_MAP: Record<string, Record<string, string>> = {
  P: { HasPromise: "7F-P-有承诺", NoPromise: "7F-P-无承诺" },
  F: { Fulfilled: "7F-F-已兑现", Unfulfilled: "7F-F-未兑现", Partial: "7F-F-部分兑现" },
  E: { Willing: "7F-E-心甘情愿", Perfunctory: "7F-E-敷衍", Resentful: "7F-E-怨恨", Numb: "7F-E-麻木" },
  A: { Self: "7F-A-自己", Partner: "7F-A-伴侣", Family: "7F-A-家庭", Outsider: "7F-A-外人", Public: "7F-A-公众" },
  X: { OverExplain: "7F-X-过度解释", Silent: "7F-X-沉默", Genuine: "7F-X-真诚", Indifferent: "7F-X-冷漠" },
  Y: { Changed: "7F-Y-改正", Resisted: "7F-Y-抗拒", Indifferent: "7F-Y-无视", NoResponse: "7F-Y-无响应" },
};

export class AuditWrapper {
  private uid: string;
  
  constructor(uid: string = "9622") {
    this.uid = uid;
  }
  
  wrap(payload: any, taskType: string = "default", persona: string = "P04"): Record<string, any> {
    // Generate seven-factor behavioral signature
    const signature: Record<string, string> = {
      // P: Promise
      P: P_VALUES[Math.floor(Math.random() * P_VALUES.length)],
      // F: Fulfillment
      F: F_VALUES[Math.floor(Math.random() * F_VALUES.length)],
      // T: Task type
      T: taskType.toUpperCase(),
      // E: Emotion / effort
      E: E_VALUES[Math.floor(Math.random() * E_VALUES.length)],
      // C: Consistency check
      C: this.uid,
      // R: Relationship
      R: persona,
      // A: Audience
      A: A_VALUES[Math.floor(Math.random() * A_VALUES.length)],
      // X: Explanation style
      X: X_VALUES[Math.floor(Math.random() * X_VALUES.length)],
      // Y: Yearning / response
      Y: Y_VALUES[Math.floor(Math.random() * Y_VALUES.length)],
      // Z: Zone / time hash
      Z: createHash('sha256').update(new Date().toISOString()).digest('hex').substring(0, 4),
    };
    
    // Pattern classification (simplified heuristic)
    let pattern = "MODE-StableDisciplined";
    if (signature.P === "NoPromise" && signature.X === "OverExplain") {
      pattern = "MODE-DefensiveDefaulter";
    } else if (signature.F === "Unfulfilled" && signature.E === "Indifferent") {
      pattern = "MODE-InternalDestroyer";
    } else if (signature.A === "Outsider" || signature.A === "Public") {
      pattern = "MODE-ExternalTrustSpender";
    }
    
    // Labels
    const labels: string[] = [];
    for (const [factor, value] of Object.entries(signature)) {
      const mapping = LABEL_MAP[factor];
      if (mapping && mapping[value]) {
        labels.push(mapping[value]);
      }
    }
    
    // Color
    let color = "";
    if (pattern === "MODE-StableDisciplined") color = "🟢";
    else if (pattern === "MODE-ExternalTrustSpender" || pattern === "MODE-Fluctuating") color = "🟡";
    else color = "🔴";
    
    return {
      auditVersion: "v1.0.0",
      uid: this.uid,
      behaviorSignature: signature,
      behaviorPattern: pattern,
      behaviorLabels: labels,
      patternDescription: PATTERNS[pattern] || "",
      color,
    };
  }
}

export function auditWrap(payload: any, taskType?: string, persona?: string, uid?: string): Record<string, any> {
  const wrapper = new AuditWrapper(uid);
  return wrapper.wrap(payload, taskType, persona);
}
