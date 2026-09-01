/**
 * Audit Wrapper — Seven-factor behavioral audit signatures.
 * Port of Python lh_standard_adapter/audit_wrapper.py
 */

const { generateDnaCode } = require("./dna_generator");

const BEHAVIOR_PATTERNS = {
  StableDisciplined: { P: "pass", F: "low", T: "consistent", E: "stable" },
  DefensiveDefaulter: { P: "pass", F: "medium", T: "cautious", E: "guarded" },
  AggressiveSpeculator: { P: "variable", F: "high", T: "volatile", E: "aggressive" },
  PassiveObserver: { P: "pass", F: "low", T: "minimal", E: "passive" },
};

function classifyBehavior(data) {
  const risk = data.risk || "low";
  const activity = data.activity || "minimal";

  if (risk === "low" && activity === "consistent") return "StableDisciplined";
  if (risk === "medium") return "DefensiveDefaulter";
  if (risk === "high") return "AggressiveSpeculator";
  return "PassiveObserver";
}

function generateAuditSignature(data, options = {}) {
  const pattern = classifyBehavior(data);
  const behavior = BEHAVIOR_PATTERNS[pattern];

  const P = behavior.P; // Performance
  const F = behavior.F; // Risk Factor
  const T = behavior.T; // Temporal
  const E = behavior.E; // Emotional
  const C = data.compliance || "compliant"; // Compliance
  const R = data.regulatory || "standard"; // Regulatory
  const A = data.accountability || "verified"; // Accountability
  const X = data.execution || "success"; // Execution
  const Y = data.yield || "positive"; // Yield
  const Z = data.zenith || "stable"; // Zenith (peak state)

  return {
    pattern,
    signature: `P/F/T/E/C/R/A/X/Y/Z:${P}/${F}/${T}/${E}/${C}/${R}/${A}/${X}/${Y}/${Z}`,
    factors: { P, F, T, E, C, R, A, X, Y, Z },
  };
}

function wrapPayload(data, taskType = "default", persona = "default", options = {}) {
  const uid = options.uid || "";
  const device = options.device || "";
  const dna = generateDnaCode({ taskType, uid, device });
  const audit = generateAuditSignature(data, options);

  return {
    dna,
    audit,
    payload: data,
    metadata: {
      taskType,
      persona,
      timestamp: new Date().toISOString(),
      version: "1.0.0",
    },
  };
}

module.exports = { wrapPayload, generateAuditSignature, classifyBehavior, BEHAVIOR_PATTERNS };
