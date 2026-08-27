package lhstandardadapter

const dnaSchemaJSON = `{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "https://uid9622.cn/schemas/dna-v1.0.json",
  "title": "LongHun DNA Traceability Code",
  "type": "object",
  "required": ["dna", "format", "uid", "timestamp"],
  "properties": {
    "dna": {
      "type": "string",
      "description": "Full v∞ DNA traceability code",
      "pattern": "^#LongHun⚡️[A-Z][a-zA-Z]+·[A-Z][a-zA-Z]+·[A-Z][a-zA-Z]+·[A-Z][a-zA-Z]+·[䷀-䷿][A-Za-z]+-.+-[a-f0-9]{8}$"
    },
    "format": { "type": "string", "enum": ["v1.0", "v2.0", "v∞", "compact"] },
    "uid": { "type": "string", "pattern": "^UID\\d+$" },
    "device": { "type": "string" },
    "timestamp": { "type": "string", "format": "date-time" }
  }
}`

const auditSchemaJSON = `{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "https://uid9622.cn/schemas/audit-v1.0.json",
  "title": "LongHun Audit Record",
  "type": "object",
  "required": ["dna", "audit", "payload", "meta"],
  "properties": {
    "dna": { "type": "string" },
    "audit": {
      "type": "object",
      "required": ["audit_version", "uid", "behavior_signature", "behavior_pattern", "behavior_labels", "color"],
      "properties": {
        "audit_version": { "type": "string" },
        "uid": { "type": "string" },
        "persona": { "type": "string" },
        "task_type": { "type": "string" },
        "behavior_signature": {
          "type": "object",
          "required": ["P", "F", "T", "E", "C", "R", "A", "X", "Y", "Z"],
          "properties": {
            "P": { "enum": ["HasPromise", "NoPromise"] },
            "F": { "enum": ["Fulfilled", "Unfulfilled", "Partial"] },
            "T": { "type": "number" },
            "E": { "enum": ["Willing", "Perfunctory", "Resentful", "Numb"] },
            "C": { "type": "number" },
            "R": { "type": "integer", "minimum": 0 },
            "A": { "enum": ["Self", "Partner", "Family", "Outsider", "Public"] },
            "X": { "enum": ["OverExplain", "Silent", "Genuine", "Indifferent"] },
            "Y": { "enum": ["Changed", "Resisted", "Indifferent", "NoResponse"] },
            "Z": { "type": "number" }
          }
        },
        "behavior_pattern": {
          "enum": ["MODE-DefensiveDefaulter", "MODE-ExternalTrustSpender", "MODE-InternalDestroyer", "MODE-Fluctuating", "MODE-StableDisciplined"]
        },
        "behavior_labels": { "type": "array", "items": { "type": "string" } },
        "color": { "enum": ["🟢", "🟡", "🔴"] },
        "timestamp": { "type": "string", "format": "date-time" },
        "payload_hash": { "type": "string", "pattern": "^[a-f0-9]{16}$" }
      }
    },
    "payload": {},
    "meta": {
      "type": "object",
      "required": ["adapter_version", "uid", "device", "task_type", "persona"],
      "properties": {
        "adapter_version": { "type": "string" },
        "uid": { "type": "string" },
        "device": { "type": "string" },
        "task_type": { "type": "string" },
        "persona": { "type": "string" },
        "generated_at": { "type": "string", "format": "date-time" },
        "format": { "const": "longhun-v∞" }
      }
    }
  }
}`
