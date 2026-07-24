package adapter

import (
	"strings"
	"testing"
)

func TestLongHunAdapter(t *testing.T) {
	a := NewAdapter("9622", "HM-TEST-001")
	payload := map[string]interface{}{
		"action": "PING",
		"count":  1,
	}

	wrapped, err := a.Wrap(payload, "code", "P01")
	if err != nil {
		t.Fatalf("Wrap failed: %v", err)
	}

	if !strings.HasPrefix(wrapped.DNA, "#LongHun⚡️") {
		t.Errorf("Expected DNA prefix #LongHun⚡️, got %s", wrapped.DNA)
	}

	res := a.Validate(wrapped)
	if !res.Valid {
		t.Errorf("Validation failed: %v", res.Errors)
	}
}
