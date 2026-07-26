/**
 * Audit Wrapper — seven-factor behavioral audit metadata generation.
 */
import crypto from 'crypto';

const LABEL_MAP = {
  P: {HasPromise:"7F-P-有承诺",NoPromise:"7F-P-无承诺"},
  F: {Fulfilled:"7F-F-已兑现",Unfulfilled:"7F-F-未兑现",Partial:"7F-F-部分兑现"},
  E: {Willing:"7F-E-心甘情愿",Perfunctory:"7F-E-敷衍",Resentful:"7F-E-怨恨",Numb:"7F-E-麻木"},
  A: {Self:"7F-A-自己",Partner:"7F-A-伴侣",Family:"7F-A-家庭",Outsider:"7F-A-外人",Public:"7F-A-公众"},
  X: {OverExplain:"7F-X-过度解释",Silent:"7F-X-沉默",Genuine:"7F-X-真诚",Indifferent:"7F-X-冷漠"},
  Y: {Changed:"7F-Y-改正",Resisted:"7F-Y-抗拒",Indifferent:"7F-Y-无视",NoResponse:"7F-Y-无响应"}
};

export class AuditWrapper {
  constructor(uid="9622") { this.uid = uid; }

  wrap(payload, taskType="default", persona="P04") {
    const now = new Date();
    const signature = {
      P:"HasPromise", F:"Fulfilled", T:0.0, E:"Willing",
      C:0, R:0, A:"Self", X:"Genuine", Y:"NoResponse", Z:1.0
    };
    const pattern = this._classify(signature);
    const labels = this._makeLabels(signature, pattern);
    const color = this._determineColor(pattern, signature.R);
    const payloadJson = JSON.stringify(payload, Object.keys(payload).sort());
    const payloadHash = crypto.createHash('sha256').update(payloadJson,'utf8').digest('hex').slice(0,16);
    return {
      audit_version:"v1.0", uid:`UID${this.uid}`, persona, task_type:taskType,
      behavior_signature:signature, behavior_pattern:pattern, behavior_labels:labels,
      color, timestamp:now.toISOString(), payload_hash:payloadHash
    };
  }

  _classify(sig) {
    const f = sig.F, x = sig.X, a = sig.A, y = sig.Y, z = sig.Z;
    if (f==="Unfulfilled" && x==="OverExplain") return "MODE-DefensiveDefaulter";
    if (f==="Fulfilled" && a==="Outsider") return "MODE-ExternalTrustSpender";
    if (f==="Unfulfilled" && y==="Indifferent") return "MODE-InternalDestroyer";
    if (z > 2.0) return "MODE-Fluctuating";
    return "MODE-StableDisciplined";
  }

  _makeLabels(sig, pattern) {
    const labels = [];
    for (const factor of ["P","F","E","A","X","Y"]) {
      const val = sig[factor];
      if (LABEL_MAP[factor] && LABEL_MAP[factor][val]) labels.push(LABEL_MAP[factor][val]);
    }
    labels.push(pattern);
    return labels;
  }

  _determineColor(pattern, repeat) {
    if (pattern==="MODE-InternalDestroyer") return "🔴";
    if (pattern==="MODE-Fluctuating" && repeat>3) return "🟡";
    if (pattern==="MODE-DefensiveDefaulter" && repeat>2) return "🟡";
    return "🟢";
  }
}

const _wrapper = new AuditWrapper();
export function auditWrap(payload, taskType="default", persona="P04") {
  return _wrapper.wrap(payload, taskType, persona);
}
