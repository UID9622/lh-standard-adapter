// Package lh_adapter provides LongHun DNA traceability wrapping and
// seven-factor behavioral audit for JSON payloads.
//
//	DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-ADAPTER-v1.0.0-4f7a3b1c
//	Author: LongHun Core · UID9622 · 龍芯北辰
//	License: CC BY-NC-SA 4.0
//
// Open the standard. Guard the engine.
package lh_adapter

import (
	"encoding/json"
	"time"
)

// LongHunAdapter wraps JSON payloads with DNA traceability
// and seven-factor behavioral audit metadata.
type LongHunAdapter struct {
	UID    string
	Device string
	Locale string

	dnaGen *DNAGenerator
	audit  *AuditWrapper
	val    *Validator
}

// New creates a new LongHunAdapter.
func New(uid, device, locale string) *LongHunAdapter {
	return &LongHunAdapter{
		UID:    uid,
		Device: device,
		Locale: locale,
		dnaGen: NewDNAGenerator(uid, device, locale),
		audit:  NewAuditWrapper(uid),
		val:    NewValidator(),
	}
}

// Default returns a LongHunAdapter with default UID9622 settings.
func Default() *LongHunAdapter {
	return New("9622", "HM-9622-001", "Asia/Shanghai")
}

// Wrap produces a fully wrapped record: DNA + audit + payload + meta.
func (a *LongHunAdapter) Wrap(data interface{}, taskType, persona, action, version string) map[string]interface{} {
	dna := a.dnaGen.Generate(taskType, action, version)
	audit := a.audit.Wrap(data, taskType, persona)
	now := time.Now().In(time.FixedZone("CST", 8*3600))

	meta := map[string]interface{}{
		"adapter_version": VERSION,
		"uid":             a.UID,
		"device":          a.Device,
		"task_type":       taskType,
		"persona":         persona,
		"generated_at":    now.Format(time.RFC3339),
		"format":          "longhun-v∞",
	}

	return map[string]interface{}{
		"dna":     dna,
		"audit":   audit,
		"payload": data,
		"meta":    meta,
	}
}

// Validate checks a wrapped record for compliance.
func (a *LongHunAdapter) Validate(wrapped interface{}) map[string]interface{} {
	return a.val.Validate(wrapped)
}

// GetSchemas returns the DNA and audit JSON schemas.
func (a *LongHunAdapter) GetSchemas() map[string]interface{} {
	return map[string]interface{}{
		"dna_schema":   DNA_SCHEMA,
		"audit_schema": AUDIT_SCHEMA,
	}
}

// WrapJSON is a convenience function that accepts raw JSON bytes and returns
// a wrapped record as a map.
func WrapJSON(data json.RawMessage, taskType, persona, action, version string, uid, device, locale string) map[string]interface{} {
	var payload interface{}
	json.Unmarshal(data, &payload)
	a := New(uid, device, locale)
	return a.Wrap(payload, taskType, persona, action, version)
}
