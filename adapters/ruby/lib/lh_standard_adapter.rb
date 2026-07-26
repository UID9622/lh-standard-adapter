require_relative "lh_standard_adapter/dna_generator"
require_relative "lh_standard_adapter/audit_wrapper"
require_relative "lh_standard_adapter/validator"

module LhStandardAdapter
  VERSION = "1.0.0"

  class LongHunAdapter
    attr_reader :uid, :device, :locale

    def initialize(uid: "9622", device: "HM-9622-001", locale: "Asia/Shanghai")
      @uid = uid; @device = device; @locale = locale
      @dna_gen = DNAGenerator.new(uid: uid, device: device, locale: locale)
      @audit = AuditWrapper.new(uid: uid)
      @validator = Validator.new
    end

    def wrap(data, task_type: "default", persona: "P04", action: "WRAP", version: nil)
      dna = @dna_gen.generate(task_type: task_type, action: action, version: version)
      audit = @audit.wrap(data, task_type: task_type, persona: persona)
      {
        dna: dna, audit: audit, payload: data,
        meta: {
          adapter_version: VERSION, uid: @uid, device: @device,
          task_type: task_type, persona: persona,
          generated_at: Time.now.utc.iso8601, format: "longhun-v∞"
        }
      }
    end

    def validate(wrapped)
      @validator.validate(wrapped)
    end

    def schemas
      {
        dna_schema: { type: "string", description: "v∞ DNA traceability code" },
        audit_schema: {
          type: "object",
          required: %w[audit_version uid behavior_signature behavior_pattern behavior_labels color]
        }
      }
    end
  end
end
