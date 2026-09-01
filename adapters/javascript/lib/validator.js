/**
 * Validator — Format validation for DNA and Audit signatures.
 * Port of Python lh_standard_adapter/validator.py
 */

const DNA_PATTERN = /^#LongHun⚡️[A-Z][a-z]+[A-Z][a-z]+·[A-Z][a-z]+[A-Z][a-z]+·[A-Z][a-z]+[A-Z][a-z]+·[A-Z][a-z]+Shi·[䷀-䷿][A-Za-z]+-DNA-GENERATOR-v1\.0\.0-[a-f0-9]{8}$/;

const AUDIT_PATTERN = /^P\/F\/T\/E\/C\/R\/A\/X\/Y\/Z:(pass|variable)\/(low|medium|high)\/(consistent|cautious|volatile|minimal)\/(stable|guarded|aggressive|passive)\/(compliant|non-compliant)\/(standard|strict|relaxed)\/(verified|unverified)\/(success|failure|partial)\/(positive|negative|neutral)\/(stable|declining|rising)$/;

function validateDnaCode(dna) {
  if (typeof dna !== "string") return { valid: false, error: "DNA must be a string" };
  if (!dna.startsWith("#LongHun⚡️")) return { valid: false, error: "DNA must start with #LongHun⚡️" };
  if (!dna.includes("DNA-GENERATOR-v1.0.0-")) return { valid: false, error: "DNA must contain version tag" };
  const hash = dna.split("-").pop();
  if (!/^[a-f0-9]{8}$/.test(hash)) return { valid: false, error: "DNA hash must be 8 hex chars" };
  return { valid: true };
}

function validateAuditSignature(audit) {
  if (!audit || typeof audit !== "object") return { valid: false, error: "Audit must be an object" };
  if (!audit.signature) return { valid: false, error: "Audit must have signature" };
  if (!audit.pattern) return { valid: false, error: "Audit must have pattern" };
  if (!audit.factors) return { valid: false, error: "Audit must have factors" };

  const required = ["P", "F", "T", "E", "C", "R", "A", "X", "Y", "Z"];
  for (const key of required) {
    if (!(key in audit.factors)) return { valid: false, error: `Missing factor: ${key}` };
  }

  return { valid: true };
}

function validateWrappedPayload(wrapped) {
  if (!wrapped || typeof wrapped !== "object") return { valid: false, error: "Payload must be an object" };

  const dnaResult = validateDnaCode(wrapped.dna);
  if (!dnaResult.valid) return dnaResult;

  const auditResult = validateAuditSignature(wrapped.audit);
  if (!auditResult.valid) return auditResult;

  if (!wrapped.payload) return { valid: false, error: "Missing payload" };
  if (!wrapped.metadata) return { valid: false, error: "Missing metadata" };
  if (!wrapped.metadata.version) return { valid: false, error: "Missing version" };

  return { valid: true };
}

module.exports = { validateDnaCode, validateAuditSignature, validateWrappedPayload, DNA_PATTERN, AUDIT_PATTERN };
