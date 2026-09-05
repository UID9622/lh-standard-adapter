package lhstandardadapter

import (
	"fmt"
	"testing"
)

func TestGenerateDNAStructure(t *testing.T) {
	a := New("9622", "HM-9622-001")
	dna := a.GenerateDNA("code", "WRAP", "V1.0")
	if !DnaRegex.MatchString(dna) {
		t.Fatalf("DNA failed schema regex: %s", dna)
	}
}

func TestValidateRoundTrip(t *testing.T) {
	a := New("9622", "HM-9622-001")
	wrapped := a.Wrap(map[string]string{"msg": "hello"}, "code", "P04")
	if !a.Validate(wrapped) {
		t.Fatal("Validate returned false for self-wrapped payload")
	}
}

// TestSampleDNA prints a DNA line so CI can cross-check consistency with the
// Python reference implementation.
func TestSampleDNA(t *testing.T) {
	a := New("9622", "HM-9622-001")
	fmt.Println(a.GenerateDNA("code", "WRAP", "V1.0"))
}
