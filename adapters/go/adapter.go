package adapter

type Meta struct {
	UID     string `json:"uid"`
	Device  string `json:"device"`
	Version string `json:"version"`
}

type WrappedPayload struct {
	DNA     string       `json:"dna"`
	Audit   *AuditResult `json:"audit"`
	Payload interface{}  `json:"payload"`
	Meta    Meta         `json:"meta"`
}

type LongHunAdapter struct {
	uid          string
	device       string
	dnaGenerator *DNAGenerator
	auditWrapper *AuditWrapper
	validator    *Validator
}

func NewAdapter(uid, device string) *LongHunAdapter {
	if uid == "" {
		uid = "9622"
	}
	if device == "" {
		device = "HM-9622-001"
	}
	return &LongHunAdapter{
		uid:          uid,
		device:       device,
		dnaGenerator: NewDNAGenerator(uid, device),
		auditWrapper: NewAuditWrapper(uid),
		validator:    NewValidator(),
	}
}

func (a *LongHunAdapter) Wrap(data interface{}, taskType, persona string) (*WrappedPayload, error) {
	dna := a.dnaGenerator.Generate(taskType, "WRAP", "V1.0")
	audit, err := a.auditWrapper.Wrap(data, taskType, persona)
	if err != nil {
		return nil, err
	}

	return &WrappedPayload{
		DNA:     dna,
		Audit:   audit,
		Payload: data,
		Meta: Meta{
			UID:     a.uid,
			Device:  a.device,
			Version: "V1.0",
		},
	}, nil
}

func (a *LongHunAdapter) Validate(wrapped *WrappedPayload) *ValidationResult {
	return a.validator.Validate(wrapped)
}

func (a *LongHunAdapter) GetSchemas() (map[string]interface{}, map[string]interface{}) {
	dnaSchema := map[string]interface{}{
		"type":    "string",
		"pattern": "^#LongHun⚡️.*",
	}
	auditSchema := map[string]interface{}{
		"type":     "object",
		"required": []string{"audit_version", "uid", "behavior_signature", "behavior_pattern", "behavior_labels", "color"},
	}
	return dnaSchema, auditSchema
}
