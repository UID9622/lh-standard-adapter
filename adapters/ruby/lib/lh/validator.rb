module Lh
  class Validator
    DNA_REGEX = /\A#LongHun\u26a1\ufe0f([A-Z][a-zA-Z]+)\u00b7([A-Z][a-zA-Z]+)\u00b7([A-Z][a-zA-Z]+)\u00b7([A-Z][a-zA-Z]+)\u00b7([\u4D00-\u4DFF][A-Za-z]+)-(.+)-([a-f0-9]{8})\z/

    REQUIRED_TOP_KEYS = %w[dna audit payload meta].freeze
    REQUIRED_AUDIT_KEYS = %w[audit_version uid behavior_signature behavior_pattern behavior_labels color].freeze
    REQUIRED_SIG_KEYS = %w[P F T E C R A X Y Z].freeze
    VALID_PATTERNS = %w[MODE-DefensiveDefaulter MODE-ExternalTrustSpender MODE-InternalDestroyer MODE-Fluctuating MODE-StableDisciplined].freeze

    P_VALUES = %w[HasPromise NoPromise].freeze
    F_VALUES = %w[Fulfilled Unfulfilled Partial].freeze
    E_VALUES = %w[Willing Perfunctory Resentful Numb].freeze
    A_VALUES = %w[Self Partner Family Outsider Public].freeze
    X_VALUES = %w[OverExplain Silent Genuine Indifferent].freeze
    Y_VALUES = %w[Changed Resisted Indifferent NoResponse].freeze

    def initialize; @errors = []; @warnings = []; end

    def validate(wrapped)
      @errors = []; @warnings = []
      return result unless wrapped.is_a?(Hash) && !wrapped.empty?

      REQUIRED_TOP_KEYS.each { |k| @errors << "Missing top-level key: #{k}" unless wrapped.key?(k) }

      dna = wrapped['dna']
      if dna.is_a?(String)
        @errors << 'DNA field is empty' if dna.empty?
        @errors << "DNA does not match regex: #{dna[0..59]}..." unless dna.match?(DNA_REGEX)
      else
        @errors << 'DNA is not a string'
      end

      audit = wrapped['audit']
      if audit.is_a?(Hash)
        validate_audit(audit)
        meta = wrapped['meta']
        if meta.is_a?(Hash)
          muid = meta['uid'].to_s; auid = audit['uid'].to_s
          unless muid.empty? || auid.empty?
            clean = auid.sub(/^UID/, '')
            @errors << "UID mismatch: meta.uid=#{muid}, audit.uid=#{auid}" unless muid == clean
          end
        end
      else
        @errors << 'Audit is not an object'
      end
      result
    end

    def validate_audit(audit)
      REQUIRED_AUDIT_KEYS.each { |k| @errors << "Missing audit key: #{k}" unless audit.key?(k) }
      sig = audit['behavior_signature']
      if sig.is_a?(Hash)
        REQUIRED_SIG_KEYS.each { |k| @errors << "Missing signature key: #{k}" unless sig.key?(k) }
        validate_sig_values(sig)
      else
        @errors << 'behavior_signature is not an object'
      end
      p = audit['behavior_pattern']; @warnings << "Unknown pattern: #{p}" if p && !VALID_PATTERNS.include?(p)
      c = audit['color']; @warnings << "Unknown color: #{c}" if c && !%w[🟢 🟡 🔴].include?(c)
      ph = audit['payload_hash']; @warnings << "Suspicious payload_hash: #{ph}" if ph.is_a?(String) && (ph.length != 16 || ph !~ /\A[a-f0-9]+\z/)
    end

    def validate_sig_values(sig)
      { 'P' => P_VALUES, 'F' => F_VALUES, 'E' => E_VALUES, 'A' => A_VALUES, 'X' => X_VALUES, 'Y' => Y_VALUES }.each do |k, v|
        val = sig[k]; @warnings << "Invalid #{k}: #{val}" if val && !v.include?(val)
      end
      %w[T C Z].each { |k| @warnings << "Invalid #{k}: #{sig[k]}" if sig[k] && !sig[k].is_a?(Numeric) }
      r = sig['R']; @warnings << "Invalid R: #{r}" if r && !(r.is_a?(Integer) && r >= 0)
    end

    def result
      valid = @errors.empty?
      summary = valid ? (if @warnings.empty? then '✅ VALID — 0 warnings' else "✅ VALID — #{@warnings.size} warning(s) (#{@warnings[0]})" end) : "❌ INVALID — #{@errors.size} error(s)"
      { 'valid' => valid, 'errors' => @errors, 'warnings' => @warnings, 'summary' => summary }
    end

    def self.quick_validate(wrapped)
      return false unless wrapped.is_a?(Hash)
      return false unless wrapped.key?('dna') && wrapped.key?('audit')
      dna = wrapped['dna']; dna.is_a?(String) && dna.match?(DNA_REGEX)
    end
  end
end
