/**
 * Validator — DNA and audit format validation.
 */

const DNA_REGEX = /^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([䷀-䷿][A-Za-z]+)-(.{5,})-[a-f0-9]{8}$/;

const REQUIRED_TOP_KEYS = new Set(["dna", "audit", "payload", "meta"]);
const REQUIRED_AUDIT_KEYS = new Set(["auditVersion", "uid", "behaviorSignature", "behaviorPattern", "behaviorLabels", "color"]);
const REQUIRED_SIG_KEYS = new Set(["P", "F", "T", "E", "C", "R", "A", "X", "Y", "Z"]);
const VALID_COLORS = new Set(["🟢", "🟡", "🔴"]);
const VALID_PATTERNS = new Set(["MODE-DefensiveDefaulter", "MODE-ExternalTrustSpender", "MODE-InternalDestroyer", "MODE-Fluctuating", "MODE-StableDisciplined"]);

export class Validator {
  private errors: string[];
  private warnings: string[];
  
  constructor() {
    this.errors = [];
    this.warnings = [];
  }
  
  validate(wrapped: Record<string, any>): { valid: boolean; errors: string[]; warnings: string[]; summary: string } {
    this.errors = [];
    this.warnings = [];
    
    if (!wrapped || typeof wrapped !== 'object' || Object.keys(wrapped).length === 0) {
      this.errors.push("Input is not a non-empty object");
      return this.result();
    }
    
    // 1. Top-level keys
    const topKeys = new Set(Object.keys(wrapped));
    const missingTop = [...REQUIRED_TOP_KEYS].filter(k => !topKeys.has(k));
    if (missingTop.length > 0) {
      this.errors.push(`Missing top-level keys: ${missingTop.join(", ")}`);
    }
    
    // 2. DNA validation
    if (wrapped.dna) {
      if (typeof wrapped.dna === 'object' && wrapped.dna.code) {
        if (!DNA_REGEX.test(wrapped.dna.code)) {
          this.errors.push("DNA code does not match v∞ format");
        }
      } else {
        this.errors.push("DNA entry must have a 'code' field");
      }
    }
    
    // 3. Audit validation
    if (wrapped.audit) {
      const audit = wrapped.audit;
      const auditKeys = new Set(Object.keys(audit));
      const missingAudit = [...REQUIRED_AUDIT_KEYS].filter(k => !auditKeys.has(k));
      if (missingAudit.length > 0) {
        this.errors.push(`Missing audit keys: ${missingAudit.join(", ")}`);
      }
      
      if (audit.behaviorSignature) {
        const sigKeys = new Set(Object.keys(audit.behaviorSignature));
        const missingSig = [...REQUIRED_SIG_KEYS].filter(k => !sigKeys.has(k));
        if (missingSig.length > 0) {
          this.errors.push(`Missing signature keys: ${missingSig.join(", ")}`);
        }
      }
      
      if (audit.color && !VALID_COLORS.has(audit.color)) {
        this.errors.push(`Invalid color: ${audit.color}. Must be one of ${[...VALID_COLORS].join(", ")}`);
      }
      
      if (audit.behaviorPattern && !VALID_PATTERNS.has(audit.behaviorPattern)) {
        this.warnings.push(`Unknown behavior pattern: ${audit.behaviorPattern}`);
      }
    }
    
    // 4. Schema validation warnings
    if (!wrapped.payload) {
      this.warnings.push("Payload is empty");
    }
    
    return this.result();
  }
  
  private result(): { valid: boolean; errors: string[]; warnings: string[]; summary: string } {
    const valid = this.errors.length === 0;
    let summary = valid ? "✓ Valid" : `✗ Invalid (${this.errors.length} errors)`;
    if (this.warnings.length > 0) {
      summary += `, ${this.warnings.length} warnings`;
    }
    return { valid, errors: this.errors, warnings: this.warnings, summary };
  }
}

export function quickValidate(wrapped: Record<string, any>): { valid: boolean; errors: string[]; warnings: string[]; summary: string } {
  const validator = new Validator();
  return validator.validate(wrapped);
}
