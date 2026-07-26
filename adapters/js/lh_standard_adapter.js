// LongHun Standard Adapter — JavaScript/TypeScript
// DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷝Li-ADAPTER-CODE-WRAP-V1.0.0
import { createHash } from "crypto";

const P_VALUES = ["HasPromise", "NoPromise"];
const F_VALUES = ["Fulfilled", "Unfulfilled", "Partial"];
const E_VALUES = ["Willing", "Perfunctory", "Resentful", "Numb"];
const A_VALUES = ["Self", "Partner", "Family", "Outsider", "Public"];
const X_VALUES = ["OverExplain", "Silent", "Genuine", "Indifferent"];
const Y_VALUES = ["Changed", "Resisted", "Indifferent", "NoResponse"];

const LABEL_MAP = {
  P: { HasPromise: "7F-P-有承诺", NoPromise: "7F-P-无承诺" },
  F: { Fulfilled: "7F-F-已兑现", Unfulfilled: "7F-F-未兑现", Partial: "7F-F-部分兑现" },
  E: { Willing: "7F-E-心甘情愿", Perfunctory: "7F-E-敷衍", Resentful: "7F-E-怨恨", Numb: "7F-E-麻木" },
  A: { Self: "7F-A-自己", Partner: "7F-A-伴侣", Family: "7F-A-家庭", Outsider: "7F-A-外人", Public: "7F-A-公众" },
  X: { OverExplain: "7F-X-过度解释", Silent: "7F-X-沉默", Genuine: "7F-X-真诚", Indifferent: "7F-X-冷漠" },
  Y: { Changed: "7F-Y-改正", Resisted: "7F-Y-抗拒", Indifferent: "7F-Y-无视", NoResponse: "7F-Y-无响应" },
};

const PATTERNS = {
  "MODE-DefensiveDefaulter": "Promises fail + over-explains to deflect",
  "MODE-ExternalTrustSpender": "Keeps promises to outsiders at inner-circle expense",
  "MODE-InternalDestroyer": "Breaks promises with indifference, no correction",
  "MODE-Fluctuating": "High volatility in commitment-to-fulfillment ratio",
  "MODE-StableDisciplined": "Consistent, reliable execution",
};

const DNA_REGEX = /^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([䷀-䷿][A-Za-z]+)-(.+)-([a-f0-9]{8})$/;

const REQUIRED_TOP_KEYS = new Set(["dna", "audit", "payload", "meta"]);
const REQUIRED_AUDIT_KEYS = new Set(["audit_version", "uid", "behavior_signature", "behavior_pattern", "behavior_labels", "color"]);
const REQUIRED_SIG_KEYS = new Set(["P", "F", "T", "E", "C", "R", "A", "X", "Y", "Z"]);
const VALID_COLORS = new Set(["🟢", "🟡", "🔴"]);
const VALID_PATTERNS = new Set(Object.keys(PATTERNS));

export class LongHunAdapter {
  constructor(uid = "9622", device = "HM-9622-001") {
    this.uid = uid;
    this.device = device;
  }

  wrap(data, task_type = "default", persona = "P04") {
    const stem = computeStemBranch(new Date());
    const hexagram = selectHexagram(task_type);
    const body = `ADAPTER-${task_type.toUpperCase()}-WRAP-V1.0`;
    const hash8 = createHash("sha256").update(`${stem}${hexagram}${body}`).digest("hex").slice(0, 8);
    const dna = `#LongHun⚡️${stem}·${hexagram}-${body}-${hash8}`;

    const signature = {
      P: "HasPromise", F: "Fulfilled", T: 0.0, E: "Willing",
      C: 0, R: 0, A: "Self", X: "Genuine", Y: "NoResponse", Z: 1.0,
    };
    const pattern = this._classify(signature);
    const labels = this._makeLabels(signature, pattern);
    const color = this._determineColor(pattern, signature.R);
    const payloadJson = JSON.stringify(data, Object.keys(data || {}).sort(), 0);
    const payloadHash = createHash("sha256").update(payloadJson).digest("hex").slice(0, 16);

    return {
      dna,
      audit: {
        audit_version: "v1.0",
        uid: `UID${this.uid}`,
        persona,
        task_type,
        behavior_signature: signature,
        behavior_pattern: pattern,
        behavior_labels: labels,
        color,
        timestamp: new Date().toISOString(),
        payload_hash: payloadHash,
      },
      payload: data,
      meta: { adapter_version: "1.0.0", uid: this.uid, format: "longhun-v∞", device: this.device },
    };
  }

  validate(wrapped) {
    const errors = [];
    const warnings = [];
    if (!wrapped || typeof wrapped !== "object" || Object.keys(wrapped).length === 0) {
      return { valid: false, errors: ["Input is not a non-empty dict"], warnings: [], summary: "❌ INVALID — 1 error(s)" };
    }
    const missing = [...REQUIRED_TOP_KEYS].filter((k) => !(k in wrapped));
    if (missing.length) errors.push(`Missing top-level keys: ${missing.join(",")}`);
    const dna = wrapped.dna || "";
    if (!dna) errors.push("DNA field is empty");
    else {
      const m = DNA_REGEX.exec(dna);
      if (!m) errors.push(`DNA does not match regex: ${dna.slice(0, 60)}`);
    }
    const audit = wrapped.audit;
    if (audit && typeof audit === "object") {
      const ma = [...REQUIRED_AUDIT_KEYS].filter((k) => !(k in audit));
      if (ma.length) errors.push(`Missing audit keys: ${ma.join(",")}`);
      const sig = audit.behavior_signature || {};
      const ms = [...REQUIRED_SIG_KEYS].filter((k) => !(k in sig));
      if (ms.length) errors.push(`Missing signature keys: ${ms.join(",")}`);
      else {
        if (!VALID_P_VALUES_SET.has(sig.P)) warnings.push(`Invalid P: '${sig.P}'`);
        if (!VALID_F_VALUES_SET.has(sig.F)) warnings.push(`Invalid F: '${sig.F}'`);
        if (typeof sig.T !== "number") warnings.push("Invalid T (number)");
        if (!VALID_E_VALUES_SET.has(sig.E)) warnings.push(`Invalid E: '${sig.E}'`);
        if (typeof sig.C !== "number") warnings.push("Invalid C (number)");
        if (!Number.isInteger(sig.R) || sig.R < 0) warnings.push("Invalid R (int >= 0)");
        if (!VALID_A_VALUES_SET.has(sig.A)) warnings.push(`Invalid A: '${sig.A}'`);
        if (!VALID_X_VALUES_SET.has(sig.X)) warnings.push(`Invalid X: '${sig.X}'`);
        if (!VALID_Y_VALUES_SET.has(sig.Y)) warnings.push(`Invalid Y: '${sig.Y}'`);
        if (typeof sig.Z !== "number") warnings.push("Invalid Z (number)");
      }
      if (audit.behavior_pattern && !VALID_PATTERNS.has(audit.behavior_pattern))
        warnings.push(`Unknown behavior pattern: ${audit.behavior_pattern}`);
      if (audit.color && !VALID_COLORS.has(audit.color))
        warnings.push(`Unknown audit color: ${audit.color}`);
    }
    const valid = errors.length === 0;
    const summary = valid
      ? `✅ VALID — ${warnings.length} warning(s)`
      : `❌ INVALID — ${errors.length} error(s)`;
    return { valid, errors, warnings, summary };
  }

  _classify(sig) {
    if (sig.F === "Unfulfilled" && sig.X === "OverExplain") return "MODE-DefensiveDefaulter";
    if (sig.F === "Fulfilled" && sig.A === "Outsider") return "MODE-ExternalTrustSpender";
    if (sig.F === "Unfulfilled" && sig.Y === "Indifferent") return "MODE-InternalDestroyer";
    if (sig.Z > 2.0) return "MODE-Fluctuating";
    return "MODE-StableDisciplined";
  }

  _makeLabels(sig, pattern) {
    const labels = [];
    for (const f of ["P", "F", "E", "A", "X", "Y"]) {
      const v = sig[f];
      if (LABEL_MAP[f] && LABEL_MAP[f][v]) labels.push(LABEL_MAP[f][v]);
    }
    labels.push(pattern);
    return labels;
  }

  _determineColor(pattern, repeat) {
    if (pattern === "MODE-InternalDestroyer") return "🔴";
    if (pattern === "MODE-Fluctuating" && repeat > 3) return "🟡";
    if (pattern === "MODE-DefensiveDefaulter" && repeat > 2) return "🟡";
    return "🟢";
  }
}

const VALID_P_VALUES_SET = new Set(P_VALUES);
const VALID_F_VALUES_SET = new Set(F_VALUES);
const VALID_E_VALUES_SET = new Set(E_VALUES);
const VALID_A_VALUES_SET = new Set(A_VALUES);
const VALID_X_VALUES_SET = new Set(X_VALUES);
const VALID_Y_VALUES_SET = new Set(Y_VALUES);

// Simplified stem-branch + hexagram derivation (deterministic; matches reference semantics)
const STEMS = ["JiaZi","YiChou","BingYin","DingMao","WuChen","JiSi","GengWu","XinWei","RenShen","GuiYou","JiaXu","YiHai","BingZi","DingChou","WuYin","JiMao","GengChen","XinSi","RenWu","GuiWei","JiaShen","YiYou","BingXu","DingHai","WuZi","JiChou","GengYin","XinMao","RenChen","GuiSi","JiaWu","YiWei"];
const HEXAGRAMS = { default: "䷝Li", code: "䷝Li", audit: "䷝Li", security: "䷲Zhen", privacy: "䷳Gen", deploy: "䷸Xun", trust: "䷹Dui", complete: "䷾JiJi", progress: "䷿WeiJi" };

function computeStemBranch(d) {
  // Map date to a GanZhi pillar pair string "BingWu" style for demo determinism
  const idx = (d.getFullYear() + d.getMonth() + d.getDate()) % STEMS.length;
  return STEMS[idx];
}

function selectHexagram(taskType) {
  return HEXAGRAMS[taskType] || HEXAGRAMS.default;
}

export function quickValidate(wrapped) {
  if (!wrapped || typeof wrapped !== "object") return false;
  if (!REQUIRED_TOP_KEYS.has("dna") || !("audit" in wrapped)) return false;
  return DNA_REGEX.test(wrapped.dna || "");
}

export default LongHunAdapter;
