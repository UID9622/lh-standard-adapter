package lhstandardadapter

import (
	"testing"
)

func TestDNAGenerator_Default(t *testing.T) {
	gen := NewDNAGenerator("9622", "HM-9622-001")
	dna := gen.Generate("default", "WRAP", "")
	if !startsWith(dna, "#LongHun⚡️") {
		t.Errorf("DNA should start with prefix, got: %s", dna[:30])
	}
	if !contains(dna, "ADAPTER-DEFAULT-WRAP-V1.0") {
		t.Errorf("DNA should contain module path, got: %s", dna)
	}
	if !DNARegex.MatchString(dna) {
		t.Errorf("DNA should match regex: %s", dna[:60])
	}
}

func TestDNAGenerator_CodeTask(t *testing.T) {
	gen := NewDNAGenerator("9622", "HM-9622-001")
	dna := gen.Generate("code", "GENERATE", "v2.0")
	if !startsWith(dna, "#LongHun⚡️") {
		t.Error("should start with prefix")
	}
	if !contains(dna, "ADAPTER-CODE-GENERATE-v2.0") {
		t.Errorf("should contain module path, got: %s", dna)
	}
}

func TestDNAGenerator_DeployTask(t *testing.T) {
	gen := NewDNAGenerator("9622", "HM-9622-001")
	dna := gen.Generate("deploy", "DEPLOY", "")
	if !contains(dna, "ADAPTER-DEPLOY-DEPLOY") {
		t.Errorf("should contain deploy path, got: %s", dna)
	}
}

func TestDNAGenerator_AuditTask(t *testing.T) {
	gen := NewDNAGenerator("9622", "HM-9622-001")
	dna := gen.Generate("audit", "AUDIT", "")
	if len(dna) < 30 {
		t.Errorf("DNA too short: %s", dna)
	}
}

func TestDNAGenerator_RegexMatch(t *testing.T) {
	gen := NewDNAGenerator("9622", "HM-9622-001")
	tasks := []string{"default", "code", "deploy"}
	for _, task := range tasks {
		dna := gen.Generate(task, "WRAP", "")
		if !DNARegex.MatchString(dna) {
			t.Errorf("DNA[%s] should match regex: %s", task, dna[:60])
		}
	}
}

func TestDNAGenerator_Hash8Length(t *testing.T) {
	gen := NewDNAGenerator("9622", "HM-9622-001")
	dna := gen.Generate("default", "WRAP", "v1.0")
	if len(dna) < 8 {
		t.Fatal("DNA too short")
	}
	hash8 := dna[len(dna)-8:]
	if !isHexLower(hash8) {
		t.Errorf("hash8 should be 8 hex chars: %s", hash8)
	}
}

func TestDNAGenerator_HexagramSelection(t *testing.T) {
	gen := NewDNAGenerator("9622", "HM-9622-001")
	auditHex := gen.selectHexagram("audit")
	if auditHex.EnName == "" {
		t.Error("audit hexagram should not be empty")
	}
	// Audit should map to "Li" hexagram
	validAuditHex := map[string]bool{"Li": true, "JiJi": true, "Kan": true, "Zhen": true}
	if !validAuditHex[auditHex.EnName] {
		t.Errorf("audit hexagram unexpected: %s", auditHex.EnName)
	}
	// Unknown task should get default (Qian)
	defaultHex := gen.selectHexagram("unknown-task")
	if defaultHex.EnName == "" {
		t.Error("default hexagram should not be empty")
	}
}

func TestDNAGenerator_DifferentTasksDifferentHexagrams(t *testing.T) {
	gen := NewDNAGenerator("9622", "HM-9622-001")
	auditHex := gen.selectHexagram("audit")
	deployHex := gen.selectHexagram("deploy")
	if auditHex.EnName == deployHex.EnName {
		t.Error("audit and deploy should have different hexagrams")
	}
}

func TestDNAGenerator_ConvenienceFunction(t *testing.T) {
	gen := NewDNAGenerator("9622", "HM-9622-001")
	dna := gen.Generate("test", "TEST", "")
	if !startsWith(dna, "#LongHun⚡️") {
		t.Error("convenience func should work")
	}
}

// --- AuditWrapper tests ---

func TestAuditWrapper_Default(t *testing.T) {
	w := NewAuditWrapper("9622")
	audit := w.Wrap(map[string]string{"key": "value"}, "default", "P04")
	if audit.AuditVersion != "v1.0" {
		t.Errorf("audit version should be v1.0, got: %s", audit.AuditVersion)
	}
	if audit.UID != "UID9622" {
		t.Errorf("UID should be UID9622, got: %s", audit.UID)
	}
}

func TestAuditWrapper_Signature(t *testing.T) {
	w := NewAuditWrapper("9622")
	audit := w.Wrap("test", "default", "P04")
	sig := audit.BehaviorSignature
	if sig.P != "HasPromise" {
		t.Errorf("P should be HasPromise, got: %s", sig.P)
	}
	if sig.F != "Fulfilled" {
		t.Errorf("F should be Fulfilled, got: %s", sig.F)
	}
}

func TestAuditWrapper_Pattern(t *testing.T) {
	w := NewAuditWrapper("9622")
	audit := w.Wrap("test", "default", "P04")
	// Default signature should be StableDisciplined
	if audit.BehaviorPattern != "MODE-StableDisciplined" {
		t.Errorf("pattern should be StableDisciplined, got: %s", audit.BehaviorPattern)
	}
}

func TestAuditWrapper_Color(t *testing.T) {
	w := NewAuditWrapper("9622")
	audit := w.Wrap("test", "default", "P04")
	if audit.Color != "🟢" {
		t.Errorf("color should be 🟢 for StableDisciplined, got: %s", audit.Color)
	}
}

func TestAuditWrapper_PayloadHash(t *testing.T) {
	w := NewAuditWrapper("9622")
	audit := w.Wrap(map[string]string{"code": "print('hello')"}, "code", "P04")
	if len(audit.PayloadHash) != 16 {
		t.Errorf("payload_hash should be 16 chars, got: %s", audit.PayloadHash)
	}
	if !isHexLower(audit.PayloadHash) {
		t.Errorf("payload_hash should be hex, got: %s", audit.PayloadHash)
	}
}

func TestAuditWrapper_Labels(t *testing.T) {
	w := NewAuditWrapper("9622")
	audit := w.Wrap("test", "default", "P04")
	if len(audit.BehaviorLabels) == 0 {
		t.Error("labels should not be empty")
	}
	// Last label should be the pattern
	last := audit.BehaviorLabels[len(audit.BehaviorLabels)-1]
	if last != audit.BehaviorPattern {
		t.Errorf("last label should be pattern, got: %s", last)
	}
}

func TestClassify_DefensiveDefaulter(t *testing.T) {
	sig := BehaviorSignature{F: "Unfulfilled", X: "OverExplain", Z: 1.0}
	if classify(sig) != "MODE-DefensiveDefaulter" {
		t.Error("should be DefensiveDefaulter")
	}
}

func TestClassify_ExternalTrustSpender(t *testing.T) {
	sig := BehaviorSignature{F: "Fulfilled", A: "Outsider", Z: 1.0}
	if classify(sig) != "MODE-ExternalTrustSpender" {
		t.Error("should be ExternalTrustSpender")
	}
}

func TestClassify_InternalDestroyer(t *testing.T) {
	sig := BehaviorSignature{F: "Unfulfilled", Y: "Indifferent", Z: 1.0}
	if classify(sig) != "MODE-InternalDestroyer" {
		t.Error("should be InternalDestroyer")
	}
}

func TestClassify_Fluctuating(t *testing.T) {
	sig := BehaviorSignature{F: "Fulfilled", Z: 3.0}
	if classify(sig) != "MODE-Fluctuating" {
		t.Error("should be Fluctuating")
	}
}

func TestClassify_StableDisciplined(t *testing.T) {
	sig := BehaviorSignature{F: "Fulfilled", X: "Genuine", A: "Self", Y: "NoResponse", Z: 1.0}
	if classify(sig) != "MODE-StableDisciplined" {
		t.Error("should be StableDisciplined")
	}
}

func TestDetermineColor_InternalDestroyer(t *testing.T) {
	if determineColor("MODE-InternalDestroyer", 0) != "🔴" {
		t.Error("InternalDestroyer should be 🔴")
	}
}

func TestDetermineColor_FluctuatingHighRepeat(t *testing.T) {
	if determineColor("MODE-Fluctuating", 4) != "🟡" {
		t.Error("Fluctuating with repeat>3 should be 🟡")
	}
}

func TestDetermineColor_StableDisciplined(t *testing.T) {
	if determineColor("MODE-StableDisciplined", 0) != "🟢" {
		t.Error("StableDisciplined should be 🟢")
	}
}

// --- Validator tests ---

func TestValidator_ValidWrapped(t *testing.T) {
	adapter := NewAdapter("9622", "HM-9622-001")
	wrapped, err := adapter.Wrap(map[string]string{"key": "value"}, "default", "P04")
	if err != nil {
		t.Fatal(err)
	}
	result := adapter.Validate(wrapped)
	if !result.Valid {
		t.Errorf("expected valid, got errors: %v", result.Errors)
	}
}

func TestValidator_MissingTopLevelKeys(t *testing.T) {
	v := NewValidator()
	result := v.Validate(map[string]interface{}{"dna": "something"})
	if result.Valid {
		t.Error("should be invalid with missing keys")
	}
}

func TestValidator_EmptyDNA(t *testing.T) {
	v := NewValidator()
	result := v.Validate(map[string]interface{}{
		"dna":     "",
		"audit":   map[string]interface{}{},
		"payload": "test",
		"meta":    map[string]interface{}{},
	})
	if result.Valid {
		t.Error("should be invalid with empty DNA")
	}
}

func TestValidator_InvalidDNAFormat(t *testing.T) {
	v := NewValidator()
	result := v.Validate(map[string]interface{}{
		"dna":     "invalid-dna-string",
		"audit":   map[string]interface{}{},
		"payload": "test",
		"meta":    map[string]interface{}{},
	})
	if result.Valid {
		t.Error("should be invalid with bad DNA format")
	}
}

func TestValidator_UIDMismatch(t *testing.T) {
	adapter := NewAdapter("9622", "HM-9622-001")
	wrapped, _ := adapter.Wrap("test", "default", "P04")
	wrapped.Meta.UID = "9999"
	result := adapter.Validate(wrapped)
	if result.Valid {
		t.Error("should be invalid with UID mismatch")
	}
}

func TestQuickValidate_Valid(t *testing.T) {
	adapter := NewAdapter("9622", "HM-9622-001")
	wrapped, _ := adapter.Wrap("test", "default", "P04")

	// Convert to map
	b, _ := jsonMarshal(wrapped)
	var m map[string]interface{}
	jsonUnmarshal(b, &m)

	if !QuickValidate(m) {
		t.Error("QuickValidate should return true for valid wrapped")
	}
}

func TestQuickValidate_Invalid(t *testing.T) {
	if QuickValidate(map[string]interface{}{"foo": "bar"}) {
		t.Error("QuickValidate should return false for invalid")
	}
}

func TestQuickValidate_Empty(t *testing.T) {
	if QuickValidate(map[string]interface{}{}) {
		t.Error("QuickValidate should return false for empty")
	}
}

// --- Adapter integration tests ---

func TestAdapter_WrapAndValidate(t *testing.T) {
	adapter := NewAdapter("9622", "HM-9622-001")
	data := map[string]interface{}{
		"code":   "print('hello')",
		"module": "test",
	}
	wrapped, err := adapter.Wrap(data, "code", "P04")
	if err != nil {
		t.Fatal(err)
	}
	if wrapped.DNA == "" {
		t.Error("DNA should not be empty")
	}
	if wrapped.Audit.UID == "" {
		t.Error("audit UID should not be empty")
	}
	result := adapter.Validate(wrapped)
	if !result.Valid {
		t.Errorf("validation failed: %v", result.Errors)
	}
}

func TestAdapter_GetSchemas(t *testing.T) {
	adapter := NewAdapter("9622", "HM-9622-001")
	dnaSchema, auditSchema := adapter.GetSchemas()
	if dnaSchema == nil {
		t.Error("DNA schema should not be nil")
	}
	if auditSchema == nil {
		t.Error("Audit schema should not be nil")
	}
}

func TestAdapter_DefaultValues(t *testing.T) {
	adapter := NewAdapter("9622", "HM-9622-001")
	wrapped, err := adapter.Wrap("test", "", "")
	if err != nil {
		t.Fatal(err)
	}
	if wrapped.Meta.TaskType != "default" {
		t.Errorf("expected default task type, got: %s", wrapped.Meta.TaskType)
	}
	if wrapped.Meta.Persona != "P04" {
		t.Errorf("expected P04 persona, got: %s", wrapped.Meta.Persona)
	}
}

func TestWrap_ConvenienceFunction(t *testing.T) {
	wrapped, err := Wrap("test data", "default", "P04", "9622", "HM-9622-001")
	if err != nil {
		t.Fatal(err)
	}
	if !startsWith(wrapped.DNA, "#LongHun⚡️") {
		t.Error("convenience Wrap should produce valid DNA")
	}
}

func TestMod_NegativeInput(t *testing.T) {
	if mod(-1, 10) != 9 {
		t.Error("mod(-1, 10) should be 9")
	}
	if mod(-3, 12) != 9 {
		t.Error("mod(-3, 12) should be 9")
	}
}

func TestToUpper(t *testing.T) {
	if toUpper("hello") != "HELLO" {
		t.Error("toUpper failed")
	}
	if toUpper("Wrap") != "WRAP" {
		t.Error("toUpper failed for mixed case")
	}
}

// --- Stems/Branches tests ---

func TestStemBranch_AllFieldsPresent(t *testing.T) {
	gen := NewDNAGenerator("9622", "HM-9622-001")
	// Use a fixed time for testing
	sb := gen.computeStemBranch(timeFromYMDH(2026, 7, 24, 13))
	if sb.Year == "" {
		t.Error("year should not be empty")
	}
	if sb.Month == "" {
		t.Error("month should not be empty")
	}
	if sb.Day == "" {
		t.Error("day should not be empty")
	}
	if sb.ShiChen == "" {
		t.Error("shichen should not be empty")
	}
}

func TestStemBranch_ShiChen_13Hour(t *testing.T) {
	gen := NewDNAGenerator("9622", "HM-9622-001")
	sb := gen.computeStemBranch(timeFromYMDH(2026, 7, 24, 13))
	// hour // 2 = 6 → WuShi, matches Python reference implementation
	if sb.ShiChen != "WuShi" {
		t.Errorf("13:00 → WuShi (hour//2=6), got: %s", sb.ShiChen)
	}
}

func TestStemBranch_ShiChen_0Hour(t *testing.T) {
	gen := NewDNAGenerator("9622", "HM-9622-001")
	sb := gen.computeStemBranch(timeFromYMDH(2026, 7, 24, 0))
	if sb.ShiChen != "ZiShi" {
		t.Errorf("00:00 → ZiShi, got: %s", sb.ShiChen)
	}
}

func TestStemBranch_ShiChen_23Hour(t *testing.T) {
	gen := NewDNAGenerator("9622", "HM-9622-001")
	sb := gen.computeStemBranch(timeFromYMDH(2026, 7, 24, 23))
	// hour // 2 = 11 → HaiShi, matches Python reference implementation
	if sb.ShiChen != "HaiShi" {
		t.Errorf("23:00 → HaiShi (hour//2=11), got: %s", sb.ShiChen)
	}
}

func TestIsHexLower(t *testing.T) {
	if !isHexLower("abc123") {
		t.Error("abc123 should be hex lower")
	}
	if isHexLower("ABC123") {
		t.Error("ABC123 should not be hex lower")
	}
	if isHexLower("xyz") {
		t.Error("xyz should not be hex lower")
	}
}

func TestTruncate(t *testing.T) {
	if truncate("hello world", 5) != "hello" {
		t.Error("truncate failed")
	}
	if truncate("hi", 10) != "hi" {
		t.Error("truncate of short string should return original")
	}
}

func TestItoa(t *testing.T) {
	if itoa(0) != "0" {
		t.Error("itoa(0) failed")
	}
	if itoa(42) != "42" {
		t.Error("itoa(42) failed")
	}
	if itoa(-5) != "-5" {
		t.Error("itoa(-5) failed")
	}
}

// --- Test helpers ---

func startsWith(s, prefix string) bool {
	return len(s) >= len(prefix) && s[:len(prefix)] == prefix
}

func contains(s, sub string) bool {
	return len(s) >= len(sub) && (indexOf(s, sub) >= 0)
}

func indexOf(s, sub string) int {
	for i := 0; i <= len(s)-len(sub); i++ {
		if s[i:i+len(sub)] == sub {
			return i
		}
	}
	return -1
}

func jsonMarshal(v interface{}) ([]byte, error) {
	return jsonMarshalImpl(v)
}

func jsonUnmarshal(b []byte, v interface{}) error {
	return jsonUnmarshalImpl(b, v)
}
