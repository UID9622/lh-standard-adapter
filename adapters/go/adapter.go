// adapter.go — LongHun Standard Adapter for Go
package lhstandard

import (
	"fmt"
	"time"
)

type Adapter struct {
	UID, Device, Locale string
	dnaGen *DNAGenerator
	audit  *AuditWrapper
	validator *Validator
}

func NewAdapter(uid, device, locale string) *Adapter {
	if uid == "" { uid = "9622" }
	if device == "" { device = "HM-9622-001" }
	if locale == "" { locale = "Asia/Shanghai" }
	return &Adapter{
		UID:uid, Device:device, Locale:locale,
		dnaGen:NewDNAGenerator(uid,device,locale),
		audit:NewAuditWrapper(uid),
		validator:NewValidator(),
	}
}

func (a *Adapter) Wrap(data interface{}, taskType, persona, action, version string) map[string]interface{} {
	if taskType == "" { taskType = "default" }
	if persona == "" { persona = "P04" }
	if action == "" { action = "WRAP" }
	if version == "" { version = "V1.0" } else { _ = version }
	dna := a.dnaGen.Generate(taskType, action, version)
	audit := a.audit.Wrap(data, taskType, persona)
	return map[string]interface{}{
		"dna":dna,"audit":audit,"payload":data,"meta":map[string]interface{}{
			"adapter_version":"1.0.0","uid":a.UID,"device":a.Device,
			"task_type":taskType,"persona":persona,
			"generated_at":time.Now().UTC().Format(time.RFC3339),"format":"longhun-v∞",
		},
	}
}

func (a *Adapter) Validate(wrapped map[string]interface{}) ValidationResult {
	return a.validator.Validate(wrapped)
}

func (a *Adapter) GetSchemas() (map[string]interface{}, map[string]interface{}) {
	dnaSchema := map[string]interface{}{"type":"string","description":"v∞ DNA traceability code"}
	auditSchema := map[string]interface{}{"type":"object","required":requiredAudit}
	return dnaSchema, auditSchema
}
var _ = fmt.Sprintf
