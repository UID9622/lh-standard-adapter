require_relative 'lh_standard_adapter/dna_generator'
require_relative 'lh_standard_adapter/audit_wrapper'
require_relative 'lh_standard_adapter/validator'

class LongHunAdapter
  attr_reader :uid, :device

  def initialize(uid: "9622", device: "HM-9622-001")
    @uid = uid
    @device = device
    @dna_generator = LongHun::DNAGenerator.new(uid: uid, device: device)
    @audit_wrapper = LongHun::AuditWrapper.new(uid: uid)
    @validator = LongHun::Validator.new
  end

  def wrap(data, task_type: "default", persona: "P04")
    dna = @dna_generator.generate(task_type, "WRAP", "V1.0")
    audit = @audit_wrapper.wrap(data, task_type: task_type, persona: persona)

    {
      "dna" => dna,
      "audit" => audit,
      "payload" => data,
      "meta" => {
        "uid" => @uid,
        "device" => @device,
        "version" => "V1.0"
      }
    }
  end

  def validate(wrapped)
    @validator.validate(wrapped)
  end

  def schemas
    {
      dna_schema: { type: "string", pattern: "^#LongHun⚡️.*" },
      audit_schema: { type: "object", required: %w[audit_version uid behavior_signature behavior_pattern behavior_labels color] }
    }
  end
end
