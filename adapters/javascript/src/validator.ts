export const DNA_REGEX = /^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([䷀-䷿][A-Za-z]+)-(.+)-([a-f0-9]{8})$/;

export class Validator {
  public validate(wrapped: any): { valid: boolean; errors: string[]; warnings: string[]; summary: string } {
    const errors: string[] = [];
    const warnings: string[] = [];

    if (!wrapped || typeof wrapped !== "object") {
      return { valid: false, errors: ["Input is not a non-empty object"], warnings: [], summary: "❌ INVALID" };
    }

    const requiredTop = ["dna", "audit", "payload", "meta"];
    for (const key of requiredTop) {
      if (!(key in wrapped)) {
        errors.push(`Missing top-level key: ${key}`);
      }
    }

    const dna = wrapped.dna || "";
    if (!dna || !DNA_REGEX.test(dna)) {
      errors.push(`DNA does not match standard regex: ${dna}`);
    }

    const audit = wrapped.audit;
    if (!audit || typeof audit !== "object") {
      errors.push("Audit field is missing or invalid");
    } else {
      const requiredAudit = ["audit_version", "uid", "behavior_signature", "behavior_pattern", "behavior_labels", "color"];
      for (const k of requiredAudit) {
        if (!(k in audit)) errors.push(`Missing audit key: ${k}`);
      }
    }

    const valid = errors.length === 0;
    return {
      valid,
      errors,
      warnings,
      summary: valid ? "✅ VALID" : `❌ INVALID — ${errors.length} error(s)`,
    };
  }
}
