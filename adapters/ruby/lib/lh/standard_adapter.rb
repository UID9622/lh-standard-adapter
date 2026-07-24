require_relative 'dna_generator'
require_relative 'audit_wrapper'
require_relative 'validator'
require_relative 'schemas'

module Lh
  class StandardAdapter
    attr_reader :uid, :device, :locale

    def initialize(uid: '9622', device: 'HM-9622-001', locale: 'Asia/Shanghai')
      @uid, @device, @locale = uid, device, locale
      @dna_gen = DnaGenerator.new(uid: uid, device: device, locale: locale)
      @audit = AuditWrapper.new(uid)
      @validator = Validator.new
    end

    def wrap(data, task_type: 'default', persona: 'P04', action: 'WRAP', version: nil)
      dna = @dna_gen.generate(task_type, action, version)
      audit = @audit.wrap(data, task_type, persona)
      meta = {
        'adapter_version' => '1.0.0', 'uid' => @uid, 'device' => @device,
        'task_type' => task_type, 'persona' => persona,
        'generated_at' => Time.now.getlocal('+08:00').iso8601,
        'format' => 'longhun-v∞',
      }
      { 'dna' => dna, 'audit' => audit, 'payload' => data, 'meta' => meta }
    end

    def validate(wrapped)
      @validator.validate(wrapped)
    end

    def get_schemas
      { 'dna_schema' => DNA_SCHEMA, 'audit_schema' => AUDIT_SCHEMA }
    end
  end
end
