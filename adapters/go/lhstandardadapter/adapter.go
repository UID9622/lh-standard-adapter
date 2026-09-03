package lhstandardadapter

import (
	"encoding/json"
	"time"
)

// WrappedPayload is the top-level output of Adapter.Wrap.
type WrappedPayload struct {
	DNA     string      `json:"dna"`
	Audit   AuditRecord `json:"audit"`
	Payload interface{} `json:"payload"`
	Meta    Meta        `json:"meta"`
}

// Meta holds metadata about the wrapping operation.
type Meta struct {
	AdapterVersion string `json:"adapter_version"`
	UID            string `json:"uid"`
	Device         string `json:"device"`
	TaskType       string `json:"task_type"`
	Persona        string `json:"persona"`
	GeneratedAt    string `json:"generated_at"`
	Format         string `json:"format"`
}

// Adapter is the main LongHun Standard Adapter.
// It wraps JSON payloads with DNA traceability and seven-factor
// behavioral audit metadata.
type Adapter struct {
	UID    string
	Device string
	dnaGen *DNAGenerator
	audit  *AuditWrapper
	valid  *Validator
}

// NewAdapter creates an Adapter with the given UID and device.
func NewAdapter(uid, device string) *Adapter {
	return &Adapter{
		UID:    uid,
		Device: device,
		dnaGen: NewDNAGenerator(uid, device),
		audit:  NewAuditWrapper(uid),
		valid:  NewValidator(),
	}
}

// Wrap wraps a payload with DNA traceability and audit metadata.
//
// Parameters:
//   - data:      Raw payload (any JSON-serializable value)
//   - taskType:  Task category ("code", "deploy", "audit", "default", etc.)
//   - persona:   Persona identifier ("P04-Luban", "P00-Wenxin", etc.)
//
// Returns a WrappedPayload and nil on success.
func (a *Adapter) Wrap(data interface{}, taskType, persona string) (*WrappedPayload, error) {
	if taskType == "" {
		taskType = "default"
	}
	if persona == "" {
		persona = "P04"
	}

	dna := a.dnaGen.Generate(taskType, "WRAP", "")
	audit := a.audit.Wrap(data, taskType, persona)

	now := time.Now().UTC().Add(cstOffset * time.Hour)

	return &WrappedPayload{
		DNA:     dna,
		Audit:   audit,
		Payload: data,
		Meta: Meta{
			AdapterVersion: Version,
			UID:            a.UID,
			Device:         a.Device,
			TaskType:       taskType,
			Persona:        persona,
			GeneratedAt:    now.Format(time.RFC3339Nano),
			Format:         "longhun-v∞",
		},
	}, nil
}

// Validate checks a wrapped payload for standard compliance.
func (a *Adapter) Validate(wrapped *WrappedPayload) ValidationResult {
	// Convert to map for validation
	b, err := json.Marshal(wrapped)
	if err != nil {
		return ValidationResult{
			Valid:  false,
			Errors: []string{"Failed to marshal wrapped payload: " + err.Error()},
			Summary: "❌ INVALID — marshal error",
		}
	}
	var m map[string]interface{}
	if err := json.Unmarshal(b, &m); err != nil {
		return ValidationResult{
			Valid:  false,
			Errors: []string{"Failed to unmarshal: " + err.Error()},
			Summary: "❌ INVALID — unmarshal error",
		}
	}
	return a.valid.Validate(m)
}

// GetSchemas returns the JSON schemas for DNA and Audit formats.
func (a *Adapter) GetSchemas() (dnaSchema, auditSchema map[string]interface{}) {
	if err := json.Unmarshal([]byte(dnaSchemaJSON), &dnaSchema); err != nil {
		dnaSchema = map[string]interface{}{}
	}
	if err := json.Unmarshal([]byte(auditSchemaJSON), &auditSchema); err != nil {
		auditSchema = map[string]interface{}{}
	}
	return
}

// Wrap is a convenience one-shot wrapper function.
func Wrap(data interface{}, taskType, persona, uid, device string) (*WrappedPayload, error) {
	adapter := NewAdapter(uid, device)
	return adapter.Wrap(data, taskType, persona)
}
