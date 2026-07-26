// validator.go — DNA and audit format validation
package lhstandard

import (
	"regexp"
	"strings"
)

var dnaRegex = regexp.MustCompile(`^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([\x{4DC0}-\x{4DFF}][A-Za-z]+)-(.+)-([a-f0-9]{8})$`)

var requiredTop = []string{"dna","audit","payload","meta"}
var requiredAudit = []string{"audit_version","uid","behavior_signature","behavior_pattern","behavior_labels","color"}
var requiredSig = []string{"P","F","T","E","C","R","A","X","Y","Z"}

var validP = map[string]bool{"HasPromise":true,"NoPromise":true}
var validF = map[string]bool{"Fulfilled":true,"Unfulfilled":true,"Partial":true}
var validE = map[string]bool{"Willing":true,"Perfunctory":true,"Resentful":true,"Numb":true}
var validA = map[string]bool{"Self":true,"Partner":true,"Family":true,"Outsider":true,"Public":true}
var validX = map[string]bool{"OverExplain":true,"Silent":true,"Genuine":true,"Indifferent":true}
var validY = map[string]bool{"Changed":true,"Resisted":true,"Indifferent":true,"NoResponse":true}
var validColors = map[string]bool{"🟢":true,"🟡":true,"🔴":true}
var validPatterns = map[string]bool{"MODE-DefensiveDefaulter":true,"MODE-ExternalTrustSpender":true,"MODE-InternalDestroyer":true,"MODE-Fluctuating":true,"MODE-StableDisciplined":true}

type Validator struct{ Errors, Warnings []string }
type ValidationResult struct{ Valid bool; Errors, Warnings []string; Summary string }

func NewValidator() *Validator { return &Validator{} }

func (v *Validator) Validate(wrapped map[string]interface{}) ValidationResult {
	v.Errors, v.Warnings = nil, nil
	if wrapped == nil { v.Errors = append(v.Errors, "Input is nil"); return v.result() }
	for _, k := range requiredTop {
		if _, ok := wrapped[k]; !ok { v.Errors = append(v.Errors, "Missing top key: "+k) }
	}
	dna, _ := wrapped["dna"].(string)
	if dna == "" { v.Errors = append(v.Errors, "DNA empty") } else {
		if !dnaRegex.MatchString(dna) { v.Errors = append(v.Errors, "DNA regex fail: "+dna[:min(60,len(dna))]) }
	}
	audit, _ := wrapped["audit"].(map[string]interface{})
	if audit == nil { v.Errors = append(v.Errors, "Audit not object") } else { v.validateAudit(audit) }
	meta, _ := wrapped["meta"].(map[string]interface{})
	if meta != nil && audit != nil {
		mu, _ := meta["uid"].(string)
		au, _ := audit["uid"].(string)
		auClean := strings.TrimPrefix(au, "UID")
		if mu != "" && auClean != "" && mu != auClean {
			v.Errors = append(v.Errors, "UID mismatch: "+mu+" vs "+au)
		}
	}
	return v.result()
}

func (v *Validator) validateAudit(a map[string]interface{}) {
	for _, k := range requiredAudit {
		if _, ok := a[k]; !ok { v.Errors = append(v.Errors, "Missing audit key: "+k) }
	}
	sig, _ := a["behavior_signature"].(map[string]interface{})
	if sig == nil { v.Errors = append(v.Errors, "sig not object") } else {
		for _, k := range requiredSig {
			if _, ok := sig[k]; !ok { v.Errors = append(v.Errors, "Missing sig key: "+k) }
		}
		v.validateSigValues(sig)
	}
	if p, ok := a["behavior_pattern"].(string); ok && p != "" && !validPatterns[p] {
		v.Warnings = append(v.Warnings, "Unknown pattern: "+p)
	}
	if c, ok := a["color"].(string); ok && c != "" && !validColors[c] {
		v.Warnings = append(v.Warnings, "Unknown color: "+c)
	}
}

func (v *Validator) validateSigValues(sig map[string]interface{}) {
	if s, ok := sig["P"].(string); ok && !validP[s] { v.Warnings = append(v.Warnings, "Invalid P: "+s) }
	if s, ok := sig["F"].(string); ok && !validF[s] { v.Warnings = append(v.Warnings, "Invalid F: "+s) }
	if _, ok := sig["T"].(float64); !ok { v.Warnings = append(v.Warnings, "Invalid T") }
	if s, ok := sig["E"].(string); ok && !validE[s] { v.Warnings = append(v.Warnings, "Invalid E: "+s) }
	if _, ok := sig["C"].(float64); !ok { v.Warnings = append(v.Warnings, "Invalid C") }
	if _, ok := sig["R"].(float64); !ok { v.Warnings = append(v.Warnings, "Invalid R") }
	if s, ok := sig["A"].(string); ok && !validA[s] { v.Warnings = append(v.Warnings, "Invalid A: "+s) }
	if s, ok := sig["X"].(string); ok && !validX[s] { v.Warnings = append(v.Warnings, "Invalid X: "+s) }
	if s, ok := sig["Y"].(string); ok && !validY[s] { v.Warnings = append(v.Warnings, "Invalid Y: "+s) }
	if _, ok := sig["Z"].(float64); !ok { v.Warnings = append(v.Warnings, "Invalid Z") }
}

func (v *Validator) result() ValidationResult {
	valid := len(v.Errors) == 0
	sum := "✅ VALID"
	if !valid { sum = "❌ INVALID" }
	return ValidationResult{Valid:valid, Errors:v.Errors, Warnings:v.Warnings, Summary:sum}
}

func QuickValidate(wrapped map[string]interface{}) bool {
	if wrapped == nil { return false }
	dna, _ := wrapped["dna"].(string)
	if dna == "" { return false }
	_, ok := wrapped["audit"]
	if !ok { return false }
	return dnaRegex.MatchString(dna)
}
