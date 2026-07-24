package lhstandard

import (
	"strings"
	"testing"
)

func TestNewAdapter(t *testing.T) {
	adapter := NewAdapter("9622", "HM-9622-001")
	if adapter == nil { t.Fatal("nil adapter") }
	if adapter.UID != "9622" { t.Errorf("uid: got %s", adapter.UID) }
}

func TestWrap(t *testing.T) {
	adapter := NewAdapter("9622", "HM-9622-001")
	result := adapter.Wrap(map[string]string{"test": "data"}, "code", "P04", "WRAP", "")
	if result.DNA == nil { t.Fatal("DNA nil") }
	if !strings.Contains(result.DNA.Code, "#LongHun") {
		t.Errorf("DNA missing #LongHun: %s", result.DNA.Code)
	}
}

func TestToJSON(t *testing.T) {
	adapter := NewAdapter("9622", "HM-9622-001")
	result := adapter.Wrap("test", "default", "P04", "WRAP", "")
	json, err := result.ToJSON()
	if err != nil { t.Fatal("ToJSON error:", err) }
	if !strings.Contains(json, "dna") { t.Error("JSON missing dna") }
}
