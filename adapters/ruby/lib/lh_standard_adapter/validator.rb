module LongHun
  class Validator
    DNA_REGEX = /^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([\u{4e00}-\u{9fa5}\u{2df0}-\u{2dff}A-Za-z]+)-(.+)-([a-f0-9]{8})$/

    def validate(wrapped)
      errors = []
      warnings = []

      unless wrapped.is_a?(Hash) && !wrapped.empty?
        return { valid: false, errors: ["Input is not a Hash"], warnings: [], summary: "❌ INVALID — 1 error(s)" }
      end

      dna = wrapped["dna"] || ""
      if dna.empty?
        errors << "DNA field is empty"
      elsif !DNA_REGEX.match?(dna)
        errors << "DNA does not match regex: #{dna[0..59]}..."
      end

      audit = wrapped["audit"]
      if audit.is_a?(Hash)
        errors << "Missing audit_version" unless audit["audit_version"]
        errors << "Missing audit.uid" unless audit["uid"]
      else
        errors << "Audit is not a Hash"
      end

      valid = errors.empty?
      summary = valid ? "✅ VALID — #{warnings.size} warning(s)" : "❌ INVALID — #{errors.size} error(s)"

      {
        valid: valid,
        errors: errors,
        warnings: warnings,
        summary: summary
      }
    end
  end
end
