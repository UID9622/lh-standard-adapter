module LhStandardAdapter
  DNA_REGEX = /^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([\u{4DC0}-\u{4DFF}][A-Za-z]+)-(.+)-([a-f0-9]{8})$/

  REQUIRED_TOP = %w[dna audit payload meta].freeze
  REQUIRED_AUDIT = %w[audit_version uid behavior_signature behavior_pattern behavior_labels color].freeze
  REQUIRED_SIG = %w[P F T E C R A X Y Z].freeze

  VALID_P = %w[HasPromise NoPromise].freeze
  VALID_F = %w[Fulfilled Unfulfilled Partial].freeze
  VALID_E = %w[Willing Perfunctory Resentful Numb].freeze
  VALID_A = %w[Self Partner Family Outsider Public].freeze
  VALID_X = %w[OverExplain Silent Genuine Indifferent].freeze
  VALID_Y = %w[Changed Resisted Indifferent NoResponse].freeze
  VALID_COLORS = %w[🟢 🟡 🔴].freeze
  VALID_PATTERNS = %w[MODE-DefensiveDefaulter MODE-ExternalTrustSpender MODE-InternalDestroyer MODE-Fluctuating MODE-StableDisciplined].freeze

  class Validator
    attr_reader :errors, :warnings

    def initialize
      @errors = []; @warnings = []
    end

    def validate(wrapped)
      @errors.clear; @warnings.clear
      unless wrapped.is_a?(Hash) && !wrapped.empty?
        @errors << "Input not a non-empty Hash"
        return result
      end

      missing = REQUIRED_TOP - wrapped.keys
      @errors << "Missing top keys: #{missing.join(',')}" unless missing.empty?

      dna = wrapped["dna"] || wrapped[:dna] || ""
      if dna.empty?
        @errors << "DNA empty"
      elsif !DNA_REGEX.match?(dna)
        @errors << "DNA regex fail: #{dna[0..59]}"
      end

      audit = wrapped["audit"] || wrapped[:audit] || {}
      if audit.is_a?(Hash) && !audit.empty?
        validate_audit(audit)
      else
        @errors << "Audit not a Hash"
      end

      result
    end

    private

    def validate_audit(audit)
      missing = REQUIRED_AUDIT - audit.keys.map(&:to_s)
      @errors << "Missing audit keys: #{missing.join(',')}" unless missing.empty?

      sig = audit["behavior_signature"] || audit[:behavior_signature] || {}
      if sig.is_a?(Hash)
        missing_sig = REQUIRED_SIG - sig.keys.map(&:to_s)
        @errors << "Missing sig keys: #{missing_sig.join(',')}" unless missing_sig.empty?
        validate_sig_values(sig)
      else
        @errors << "sig not Hash"
      end

      validate_optional(audit)
    end

    def validate_sig_values(sig)
      @warnings << "Invalid P: #{sig['P']||sig[:P]}" unless VALID_P.include?(sig["P"] || sig[:P])
      @warnings << "Invalid F: #{sig['F']||sig[:F]}" unless VALID_F.include?(sig["F"] || sig[:F])
      @warnings << "Invalid E: #{sig['E']||sig[:E]}" unless VALID_E.include?(sig["E"] || sig[:E])
      @warnings << "Invalid A: #{sig['A']||sig[:A]}" unless VALID_A.include?(sig["A"] || sig[:A])
      @warnings << "Invalid X: #{sig['X']||sig[:X]}" unless VALID_X.include?(sig["X"] || sig[:X])
      @warnings << "Invalid Y: #{sig['Y']||sig[:Y]}" unless VALID_Y.include?(sig["Y"] || sig[:Y])
    end

    def validate_optional(audit)
      pattern = audit["behavior_pattern"] || audit[:behavior_pattern] || ""
      @warnings << "Unknown pattern: #{pattern}" if !pattern.empty? && !VALID_PATTERNS.include?(pattern)

      color = audit["color"] || audit[:color]
      @warnings << "Unknown color: #{color}" if color && !VALID_COLORS.include?(color)

      ph = audit["payload_hash"] || audit[:payload_hash]
      @warnings << "Bad payload_hash: #{ph}" if ph && ph.length != 16
    end

    def result
      valid = @errors.empty?
      {
        valid: valid, errors: @errors, warnings: @warnings,
        summary: valid ? "✅ VALID — #{@warnings.length} warning(s)" : "❌ INVALID — #{@errors.length} error(s)"
      }
    end
  end
end
