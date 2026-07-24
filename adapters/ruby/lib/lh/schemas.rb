module Lh
  DNA_SCHEMA = {
    '$schema' => 'https://json-schema.org/draft/2020-12/schema',
    '$id' => 'https://uid9622.cn/schemas/dna-v1.0.json',
    'title' => 'LongHun DNA Traceability Code',
    'type' => 'object',
    'required' => %w[dna format uid timestamp],
    'properties' => {
      'dna' => { 'type' => 'string' },
      'format' => { 'type' => 'string', 'enum' => %w[v1.0 v2.0 v∞ compact] },
      'uid' => { 'type' => 'string', 'pattern' => '^UID\\d+$' },
      'device' => { 'type' => 'string' },
      'timestamp' => { 'type' => 'string', 'format' => 'date-time' },
    },
  }.freeze

  AUDIT_SCHEMA = {
    '$schema' => 'https://json-schema.org/draft/2020-12/schema',
    '$id' => 'https://uid9622.cn/schemas/audit-v1.0.json',
    'title' => 'LongHun Audit Record',
    'type' => 'object',
    'required' => %w[dna audit payload meta],
    'properties' => {
      'dna' => { 'type' => 'string' },
      'audit' => {
        'type' => 'object',
        'required' => %w[audit_version uid behavior_signature behavior_pattern behavior_labels color],
        'properties' => {
          'audit_version' => { 'type' => 'string' },
          'uid' => { 'type' => 'string' },
          'persona' => { 'type' => 'string' },
          'task_type' => { 'type' => 'string' },
          'behavior_signature' => {
            'type' => 'object',
            'required' => %w[P F T E C R A X Y Z],
            'properties' => {
              'P' => { 'enum' => %w[HasPromise NoPromise] },
              'F' => { 'enum' => %w[Fulfilled Unfulfilled Partial] },
              'T' => { 'type' => 'number' }, 'C' => { 'type' => 'number' },
              'E' => { 'enum' => %w[Willing Perfunctory Resentful Numb] },
              'R' => { 'type' => 'integer', 'minimum' => 0 },
              'A' => { 'enum' => %w[Self Partner Family Outsider Public] },
              'X' => { 'enum' => %w[OverExplain Silent Genuine Indifferent] },
              'Y' => { 'enum' => %w[Changed Resisted Indifferent NoResponse] },
              'Z' => { 'type' => 'number' },
            },
          },
          'behavior_pattern' => { 'enum' => Validator::VALID_PATTERNS },
          'behavior_labels' => { 'type' => 'array', 'items' => { 'type' => 'string' } },
          'color' => { 'enum' => %w[🟢 🟡 🔴] },
          'timestamp' => { 'type' => 'string', 'format' => 'date-time' },
          'payload_hash' => { 'type' => 'string', 'pattern' => '^[a-f0-9]{16}$' },
        },
      },
      'payload' => {},
      'meta' => {
        'type' => 'object',
        'required' => %w[adapter_version uid device task_type persona],
        'properties' => {
          'adapter_version' => { 'type' => 'string' }, 'uid' => { 'type' => 'string' },
          'device' => { 'type' => 'string' }, 'task_type' => { 'type' => 'string' },
          'persona' => { 'type' => 'string' }, 'generated_at' => { 'type' => 'string', 'format' => 'date-time' },
          'format' => { 'const' => 'longhun-v∞' },
        },
      },
    },
  }.freeze
end
