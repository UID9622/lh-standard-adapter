package lhstandardadapter

import (
	"encoding/json"
	"regexp"
	"strings"
)

// DNARegex validates the v∞ DNA traceability code format.
var DNARegex = regexp.MustCompile(
	`^#LongHun⚡️` +
		`([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)` + // Four pillars
		`·([䷀-䷿][A-Za-z]+)` + // Hexagram
		`-(.+)` + // Body (module-action-version)
		`-([a-f0-9]{8})$`, // Hash8
)

var (
	requiredTopKeys  = []string{"dna", "audit", "payload", "meta"}
	requiredAuditKeys = []string{"audit_version", "uid", "behavior_signature",
		"behavior_pattern", "behavior_labels", "color"}
	requiredSigKeys = []string{"P", "F", "T", "E", "C", "R", "A", "X", "Y", "Z"}

	validColors = map[string]bool{"🟢": true, "🟡": true, "🔴": true}
	validPatterns = map[string]bool{
		"MODE-DefensiveDefaulter":   true,
		"MODE-ExternalTrustSpender": true,
		"MODE-InternalDestroyer":    true,
		"MODE-Fluctuating":          true,
		"MODE-StableDisciplined":    true,
	}
	validPValues = map[string]bool{"HasPromise": true, "NoPromise": true}
	validFValues = map[string]bool{"Fulfilled": true, "Unfulfilled": true, "Partial": true}
	validEValues = map[string]bool{"Willing": true, "Perfunctory": true, "Resentful": true, "Numb": true}
	validAValues = map[string]bool{"Self": true, "Partner": true, "Family": true, "Outsider": true, "Public": true}
	validXValues = map[string]bool{"OverExplain": true, "Silent": true, "Genuine": true, "Indifferent": true}
	validYValues = map[string]bool{"Changed": true, "Resisted": true, "Indifferent": true, "NoResponse": true}
)

// ValidationResult holds the outcome of validation.
type ValidationResult struct {
	Valid    bool     `json:"valid"`
	Errors   []string `json:"errors"`
	Warnings []string `json:"warnings"`
	Summary  string   `json:"summary"`
}

// Validator validates wrapped payloads for LongHun standard compliance.
type Validator struct {
	errors   []string
	warnings []string
}

// NewValidator creates a Validator.
func NewValidator() *Validator {
	return &Validator{}
}

// Validate checks a wrapped payload map for compliance.
func (v *Validator) Validate(wrapped map[string]interface{}) ValidationResult {
	v.errors = []string{}
	v.warnings = []string{}

	if len(wrapped) == 0 {
		v.errors = append(v.errors, "Input is not a non-empty dict")
		return v.result()
	}

	// 1. Top-level keys
	for _, k := range requiredTopKeys {
		if _, ok := wrapped[k]; !ok {
			v.errors = append(v.errors, "Missing top-level key: "+k)
		}
	}

	// 2. DNA validation
	dna, _ := wrapped["dna"].(string)
	if dna == "" {
		v.errors = append(v.errors, "DNA field is empty")
	} else {
		match := DNARegex.FindStringSubmatch(dna)
		if match == nil {
			v.errors = append(v.errors, "DNA does not match regex: "+truncate(dna, 60)+"...")
		} else {
			hash8 := match[7]
			if len(hash8) != 8 || !isHexLower(hash8) {
				v.errors = append(v.errors, "Invalid hash8: "+hash8)
			}
		}
	}

	// 3. Audit validation
	audit, ok := wrapped["audit"].(map[string]interface{})
	if !ok {
		v.errors = append(v.errors, "Audit is not a dict")
	} else {
		v.validateAudit(audit)

		// 4. UID consistency check
		if meta, ok := wrapped["meta"].(map[string]interface{}); ok {
			metaUID, _ := meta["uid"].(string)
			auditUID, _ := audit["uid"].(string)
			if metaUID != "" && auditUID != "" {
				auditUIDClean := strings.TrimPrefix(auditUID, "UID")
				if metaUID != auditUIDClean {
					v.errors = append(v.errors,
						"UID mismatch: meta.uid="+metaUID+", audit.uid="+auditUID)
				}
			}
		}
	}

	return v.result()
}

func (v *Validator) validateAudit(audit map[string]interface{}) {
	// Required keys
	for _, k := range requiredAuditKeys {
		if _, ok := audit[k]; !ok {
			v.errors = append(v.errors, "Missing audit key: "+k)
		}
	}

	// behavior_signature
	sig, ok := audit["behavior_signature"].(map[string]interface{})
	if !ok {
		v.errors = append(v.errors, "behavior_signature is not a dict")
	} else {
		for _, k := range requiredSigKeys {
			if _, ok := sig[k]; !ok {
				v.errors = append(v.errors, "Missing signature key: "+k)
			}
		}
		if len(sig) >= len(requiredSigKeys) {
			v.validateSigValues(sig)
		}
	}

	// pattern
	pattern, _ := audit["behavior_pattern"].(string)
	if pattern != "" && !validPatterns[pattern] {
		v.warnings = append(v.warnings, "Unknown behavior pattern: "+pattern)
	}

	// color
	color, _ := audit["color"].(string)
	if color != "" && !validColors[color] {
		v.warnings = append(v.warnings, "Unknown audit color: "+color)
	}

	// payload_hash
	ph, _ := audit["payload_hash"].(string)
	if ph != "" && (len(ph) != 16 || !isHexLower(ph)) {
		v.warnings = append(v.warnings, "Suspicious payload_hash: "+ph)
	}
}

func (v *Validator) validateSigValues(sig map[string]interface{}) {
	// P
	if val, ok := sig["P"].(string); ok && !validPValues[val] {
		v.warnings = append(v.warnings, "Invalid P: '"+val+"'")
	}
	// F
	if val, ok := sig["F"].(string); ok && !validFValues[val] {
		v.warnings = append(v.warnings, "Invalid F: '"+val+"'")
	}
	// T (number)
	if _, ok := toFloat(sig["T"]); !ok {
		v.warnings = append(v.warnings, "Invalid T (number)")
	}
	// E
	if val, ok := sig["E"].(string); ok && !validEValues[val] {
		v.warnings = append(v.warnings, "Invalid E: '"+val+"'")
	}
	// C (number)
	if _, ok := toFloat(sig["C"]); !ok {
		v.warnings = append(v.warnings, "Invalid C (number)")
	}
	// R (int >= 0)
	if r, ok := toInt(sig["R"]); !ok || r < 0 {
		v.warnings = append(v.warnings, "Invalid R (int >= 0)")
	}
	// A
	if val, ok := sig["A"].(string); ok && !validAValues[val] {
		v.warnings = append(v.warnings, "Invalid A: '"+val+"'")
	}
	// X
	if val, ok := sig["X"].(string); ok && !validXValues[val] {
		v.warnings = append(v.warnings, "Invalid X: '"+val+"'")
	}
	// Y
	if val, ok := sig["Y"].(string); ok && !validYValues[val] {
		v.warnings = append(v.warnings, "Invalid Y: '"+val+"'")
	}
	// Z (number)
	if _, ok := toFloat(sig["Z"]); !ok {
		v.warnings = append(v.warnings, "Invalid Z (number)")
	}
}

func (v *Validator) result() ValidationResult {
	valid := len(v.errors) == 0
	var summary string
	if valid {
		summary = "✅ VALID — " + itoa(len(v.warnings)) + " warning(s)"
	} else {
		summary = "❌ INVALID — " + itoa(len(v.errors)) + " error(s)"
	}
	return ValidationResult{
		Valid:    valid,
		Errors:   v.errors,
		Warnings: v.warnings,
		Summary:  summary,
	}
}

// QuickValidate checks if a wrapped payload has required keys and valid DNA.
func QuickValidate(wrapped map[string]interface{}) bool {
	if len(wrapped) == 0 {
		return false
	}
	for _, k := range []string{"dna", "audit"} {
		if _, ok := wrapped[k]; !ok {
			return false
		}
	}
	dna, _ := wrapped["dna"].(string)
	return DNARegex.MatchString(dna)
}

// --- helpers ---

func isHexLower(s string) bool {
	for _, c := range s {
		if !((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f')) {
			return false
		}
	}
	return true
}

func truncate(s string, n int) string {
	if len(s) <= n {
		return s
	}
	return s[:n]
}

func toFloat(v interface{}) (float64, bool) {
	switch n := v.(type) {
	case float64:
		return n, true
	case float32:
		return float64(n), true
	case int:
		return float64(n), true
	case int64:
		return float64(n), true
	case json.Number:
		f, err := n.Float64()
		return f, err == nil
	default:
		return 0, false
	}
}

func toInt(v interface{}) (int, bool) {
	switch n := v.(type) {
	case float64:
		return int(n), true
	case int:
		return n, true
	case int64:
		return int(n), true
	case json.Number:
		i, err := n.Int64()
		return int(i), err == nil
	default:
		return 0, false
	}
}

func itoa(n int) string {
	if n == 0 {
		return "0"
	}
	neg := n < 0
	if neg {
		n = -n
	}
	var buf [20]byte
	i := len(buf)
	for n > 0 {
		i--
		buf[i] = byte('0' + n%10)
		n /= 10
	}
	if neg {
		i--
		buf[i] = '-'
	}
	return string(buf[i:])
}
