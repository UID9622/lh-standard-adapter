package adapter

import (
	"fmt"
	"regexp"
)

var DNARegex = regexp.MustCompile(`^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([\x{4e00}-\x{9fa5}\x{2df0}-\x{2dff}A-Za-z]+)-(.+)-([a-f0-9]{8})$`)

type ValidationResult struct {
	Valid    bool     `json:"valid"`
	Errors   []string `json:"errors"`
	Warnings []string `json:"warnings"`
	Summary  string   `json:"summary"`
}

type Validator struct{}

func NewValidator() *Validator {
	return &Validator{}
}

func (v *Validator) Validate(wrapped *WrappedPayload) *ValidationResult {
	errors := make([]string, 0)
	warnings := make([]string, 0)

	if wrapped == nil {
		errors = append(errors, "Input is nil")
		return v.makeResult(errors, warnings)
	}

	if wrapped.DNA == "" {
		errors = append(errors, "DNA field is empty")
	} else if !DNARegex.MatchString(wrapped.DNA) {
		errors = append(errors, fmt.Sprintf("DNA does not match regex: %s", wrapped.DNA))
	}

	if wrapped.Audit.AuditVersion == "" {
		errors = append(errors, "Missing audit_version")
	}
	if wrapped.Audit.UID == "" {
		errors = append(errors, "Missing audit.uid")
	}

	return v.makeResult(errors, warnings)
}

func (v *Validator) makeResult(errors, warnings []string) *ValidationResult {
	valid := len(errors) == 0
	summary := ""
	if valid {
		summary = fmt.Sprintf("✅ VALID — %d warning(s)", len(warnings))
	} else {
		summary = fmt.Sprintf("❌ INVALID — %d error(s)", len(errors))
	}
	return &ValidationResult{
		Valid:    valid,
		Errors:   errors,
		Warnings: warnings,
		Summary:  summary,
	}
}
