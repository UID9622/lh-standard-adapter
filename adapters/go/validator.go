package lh_adapter

import (
	"encoding/json"
	"fmt"
	"regexp"
	"strings"
)

// --- Valid value sets for signature fields ---

var VALID_COLORS = []string{"🟢", "🟡", "🔴"}

var VALID_PATTERNS = []string{
	"MODE-DefensiveDefaulter",
	"MODE-ExternalTrustSpender",
	"MODE-InternalDestroyer",
	"MODE-Fluctuating",
	"MODE-StableDisciplined",
}

var VALID_P_VALUES_SET = []string{"HasPromise", "NoPromise"}
var VALID_F_VALUES_SET = []string{"Fulfilled", "Unfulfilled", "Partial"}
var VALID_E_VALUES_SET = []string{"Willing", "Perfunctory", "Resentful", "Numb"}
var VALID_A_VALUES_SET = []string{"Self", "Partner", "Family", "Outsider", "Public"}
var VALID_X_VALUES_SET = []string{"OverExplain", "Silent", "Genuine", "Indifferent"}
var VALID_Y_VALUES_SET = []string{"Changed", "Resisted", "Indifferent", "NoResponse"}

// REQUIRED_TOP_KEYS are the mandatory top-level keys in a wrapped record.
var REQUIRED_TOP_KEYS = []string{"dna", "audit", "payload", "meta"}

// REQUIRED_AUDIT_KEYS are the mandatory keys inside the audit object.
var REQUIRED_AUDIT_KEYS = []string{
	"audit_version", "uid", "behavior_signature",
	"behavior_pattern", "behavior_labels", "color",
}

// REQUIRED_SIG_KEYS are the mandatory keys inside the behavior_signature.
var REQUIRED_SIG_KEYS = []string{"P", "F", "T", "E", "C", "R", "A", "X", "Y", "Z"}

// dnaRegex is the compiled DNA validation regex.
var dnaRegex = regexp.MustCompile(DNA_REGEX)

// dnaMatches checks if a string matches the DNA format.
func dnaMatches(s string) bool {
	prefix := "#LongHun⚡️"
	if !strings.HasPrefix(s, prefix) {
		return false
	}
	return dnaRegex.MatchString(s)
}

// Validator validates wrapped LongHun records.
type Validator struct {
	Errors   []string
	Warnings []string
}

// NewValidator creates a new Validator.
func NewValidator() *Validator {
	return &Validator{
		Errors:   make([]string, 0),
		Warnings: make([]string, 0),
	}
}

// Validate checks a wrapped record for compliance.
func (v *Validator) Validate(wrapped interface{}) map[string]interface{} {
	v.Errors = v.Errors[:0]
	v.Warnings = v.Warnings[:0]

	obj, ok := wrapped.(map[string]interface{})
	if !ok || len(obj) == 0 {
		v.Errors = append(v.Errors, "Input is not a non-empty object")
		return v.result()
	}

	// Check required top-level keys
	for _, k := range REQUIRED_TOP_KEYS {
		if _, exists := obj[k]; !exists {
			v.Errors = append(v.Errors, fmt.Sprintf("Missing top-level key: %s", k))
		}
	}

	// Validate DNA field
	if dnaVal, exists := obj["dna"]; exists {
		if dna, ok := dnaVal.(string); ok {
			if dna == "" {
				v.Errors = append(v.Errors, "DNA field is empty")
			} else if !dnaMatches(dna) {
				short := dna
				if len(short) > 60 {
					short = short[:60]
				}
				v.Errors = append(v.Errors, fmt.Sprintf("DNA does not match pattern: %s...", short))
			}
		} else {
			v.Errors = append(v.Errors, "DNA is not a string")
		}
	}

	// Validate audit object
	if auditVal, exists := obj["audit"]; exists {
		if auditObj, ok := auditVal.(map[string]interface{}); ok {
			v.validateAudit(auditObj)

			// Cross-check UID
			if metaVal, exists := obj["meta"]; exists {
				if metaObj, ok := metaVal.(map[string]interface{}); ok {
					metaUID, _ := metaObj["uid"].(string)
					auditUID, _ := auditObj["uid"].(string)
					if metaUID != "" && auditUID != "" {
						auditClean := strings.TrimPrefix(auditUID, "UID")
						if metaUID != auditClean {
							v.Errors = append(v.Errors, fmt.Sprintf(
								"UID mismatch: meta.uid=%s, audit.uid=%s", metaUID, auditUID,
							))
						}
					}
				}
			}
		} else {
			v.Errors = append(v.Errors, "Audit is not an object")
		}
	}

	return v.result()
}

// validateAudit validates the audit sub-object.
func (v *Validator) validateAudit(audit map[string]interface{}) {
	// Check required audit keys
	for _, k := range REQUIRED_AUDIT_KEYS {
		if _, exists := audit[k]; !exists {
			v.Errors = append(v.Errors, fmt.Sprintf("Missing audit key: %s", k))
		}
	}

	// Validate behavior_signature
	if sigVal, exists := audit["behavior_signature"]; exists {
		if sigObj, ok := sigVal.(map[string]interface{}); ok {
			for _, k := range REQUIRED_SIG_KEYS {
				if _, exists := sigObj[k]; !exists {
					v.Errors = append(v.Errors, fmt.Sprintf("Missing signature key: %s", k))
				}
			}
			v.validateSigValues(sigObj)
		} else {
			v.Errors = append(v.Errors, "behavior_signature is not an object")
		}
	}

	// Validate behavior_pattern
	if pVal, exists := audit["behavior_pattern"]; exists {
		if p, ok := pVal.(string); ok {
			if !contains(VALID_PATTERNS, p) {
				v.Warnings = append(v.Warnings, fmt.Sprintf("Unknown behavior pattern: %s", p))
			}
		}
	}

	// Validate color
	if cVal, exists := audit["color"]; exists {
		if c, ok := cVal.(string); ok {
			if !contains(VALID_COLORS, c) {
				v.Warnings = append(v.Warnings, fmt.Sprintf("Unknown audit color: %s", c))
			}
		}
	}

	// Validate payload_hash
	if phVal, exists := audit["payload_hash"]; exists {
		if ph, ok := phVal.(string); ok {
			if len(ph) != 16 || !isHexString(ph) {
				v.Warnings = append(v.Warnings, fmt.Sprintf("Suspicious payload_hash: %s", ph))
			}
		}
	}
}

// validateSigValues checks the types and values of behavior_signature fields.
func (v *Validator) validateSigValues(sig map[string]interface{}) {
	checks := []struct {
		label string
		fn    func(interface{}) bool
	}{
		{"P", func(val interface{}) bool { s, ok := val.(string); return ok && contains(VALID_P_VALUES_SET, s) }},
		{"F", func(val interface{}) bool { s, ok := val.(string); return ok && contains(VALID_F_VALUES_SET, s) }},
		{"T", func(val interface{}) bool { return isNumber(val) }},
		{"E", func(val interface{}) bool { s, ok := val.(string); return ok && contains(VALID_E_VALUES_SET, s) }},
		{"C", func(val interface{}) bool { return isNumber(val) }},
		{"R", func(val interface{}) bool { n, ok := toInt(val); return ok && n >= 0 }},
		{"A", func(val interface{}) bool { s, ok := val.(string); return ok && contains(VALID_A_VALUES_SET, s) }},
		{"X", func(val interface{}) bool { s, ok := val.(string); return ok && contains(VALID_X_VALUES_SET, s) }},
		{"Y", func(val interface{}) bool { s, ok := val.(string); return ok && contains(VALID_Y_VALUES_SET, s) }},
		{"Z", func(val interface{}) bool { return isNumber(val) }},
	}

	for _, check := range checks {
		if val, exists := sig[check.label]; exists {
			if !check.fn(val) {
				// Use json.Marshal for safe stringification
				j, _ := json.Marshal(val)
				v.Warnings = append(v.Warnings, fmt.Sprintf("Invalid %s: %s", check.label, string(j)))
			}
		}
	}
}

// result builds the validation result map.
func (v *Validator) result() map[string]interface{} {
	valid := len(v.Errors) == 0
	var summary string
	if valid {
		if len(v.Warnings) == 0 {
			summary = "✅ VALID — 0 warnings"
		} else {
			summary = fmt.Sprintf("✅ VALID — %d warning(s) (%s)", len(v.Warnings), v.Warnings[0])
		}
	} else {
		summary = fmt.Sprintf("❌ INVALID — %d error(s)", len(v.Errors))
	}

	return map[string]interface{}{
		"valid":    valid,
		"errors":   v.Errors,
		"warnings": v.Warnings,
		"summary":  summary,
	}
}

// --- Helper functions ---

func contains(slice []string, item string) bool {
	for _, s := range slice {
		if s == item {
			return true
		}
	}
	return false
}

func isHexString(s string) bool {
	for _, c := range s {
		if !((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f')) {
			return false
		}
	}
	return true
}

func isNumber(val interface{}) bool {
	switch val.(type) {
	case float64, float32, int, int64, int32, int16, int8, uint, uint64, uint32, uint16, uint8:
		return true
	default:
		return false
	}
}

func toInt(val interface{}) (int, bool) {
	switch v := val.(type) {
	case float64:
		return int(v), true
	case int:
		return v, true
	case int64:
		return int(v), true
	default:
		return 0, false
	}
}

// QuickValidate performs a fast check: non-empty object, has dna + audit keys, dna passes regex.
func QuickValidate(wrapped interface{}) bool {
	obj, ok := wrapped.(map[string]interface{})
	if !ok {
		return false
	}

	dnaVal, hasDNA := obj["dna"]
	_, hasAudit := obj["audit"]
	if !hasDNA || !hasAudit {
		return false
	}

	dna, ok := dnaVal.(string)
	if !ok {
		return false
	}

	return dnaMatches(dna)
}
