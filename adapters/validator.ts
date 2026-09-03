export const DNA_REGEX = /^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([\u4e00-\u9fa5\u2df0-\u2dffA-Za-z]+)-(.+)-([a-f0-9]{8})$/;

export const REQUIRED_TOP_KEYS = ["dna", "audit", "payload", "meta"];
export const REQUIRED_AUDIT_KEYS = [
  "audit_version", "uid", "behavior_signature",
  "behavior_pattern", "behavior_labels", "color"
];
export const REQUIRED_SIG_KEYS = ["P", "F", "T", "E", "C", "R", "A", "X", "Y", "Z"];

export interface ValidationResult {
  valid: boolean;
  errors: string[];
  warnings: string[];
  summary: string;
}

export class Validator {
  private errors: string[] = [];
  private warnings: string[] = [];

  public validate(wrapped: any): ValidationResult {
    this.errors = [];
    this.warnings = [];

    if (!wrapped || typeof wrapped !== "object") {
      this.errors.push("Input is not a non-empty object");
      return this.getResult();
    }

    // Top level keys check
    for (const key of REQUIRED_TOP_KEYS) {
      if (!(key in wrapped)) {
        this.errors.push(`Missing top-level key: ${key}`);
      }
    }

    // DNA check
    const dna = wrapped.dna || "";
    if (!dna) {
      this.errors.push("DNA field is empty");
    } else if (!DNA_REGEX.test(dna)) {
      this.errors.push(`DNA does not match regex: ${dna.slice(0, 60)}...`);
    }

    // Audit check
    const audit = wrapped.audit;
    if (!audit || typeof audit !== "object") {
      this.errors.push("Audit is not an object");
    } else {
      for (const key of REQUIRED_AUDIT_KEYS) {
        if (!(key in audit)) {
          this.errors.push(`Missing audit key: ${key}`);
        }
      }

      const sig = audit.behavior_signature;
      if (!sig || typeof sig !== "object") {
        this.errors.push("behavior_signature is not an object");
      } else {
        for (const key of REQUIRED_SIG_KEYS) {
          if (!(key in sig)) {
            this.errors.push(`Missing signature key: ${key}`);
          }
        }
      }
    }

    return this.getResult();
  }

  private getResult(): ValidationResult {
    const valid = this.errors.length === 0;
    const summary = valid
      ? `✅ VALID — ${this.warnings.length} warning(s)`
      : `❌ INVALID — ${this.errors.length} error(s)`;

    return {
      valid,
      errors: this.errors,
      warnings: this.warnings,
      summary
    };
  }
}
