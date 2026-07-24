package lh_adapter

import (
	"regexp"
	"strings"
	"testing"
	"time"
)

// --- Helpers ---

func makeGen() *DNAGenerator {
	return NewDNAGenerator("9622", "HM-9622-001", "Asia/Shanghai")
}

func makeAdapter() *LongHunAdapter {
	return New("9622", "HM-9622-001", "Asia/Shanghai")
}

// ============================================================
//  DNAGenerator Tests
// ============================================================

func TestDNAGeneratorDefault(t *testing.T) {
	dna := makeGen().Generate("default", "WRAP", "")
	if !strings.HasPrefix(dna, "#LongHun") {
		t.Errorf("DNA should start with #LongHun, got: %s", dna)
	}
	if !strings.Contains(dna, "ADAPTER-DEFAULT-WRAP-V1.0") {
		t.Errorf("DNA should contain ADAPTER-DEFAULT-WRAP-V1.0, got: %s", dna)
	}
}

func TestDNAGeneratorCode(t *testing.T) {
	dna := makeGen().Generate("code", "GENERATE", "v2.0")
	if !strings.Contains(dna, "ADAPTER-CODE-GENERATE-v2.0") {
		t.Errorf("DNA should contain ADAPTER-CODE-GENERATE-v2.0, got: %s", dna)
	}
}

func TestDNAGeneratorHash8(t *testing.T) {
	dna := makeGen().Generate("default", "WRAP", "")
	parts := strings.Split(dna, "-")
	last := parts[len(parts)-1]
	if len(last) != 8 {
		t.Errorf("hash8 should be 8 chars, got %d: %s", len(last), last)
	}
	if !isHexString(last) {
		t.Errorf("hash8 should be hex, got: %s", last)
	}
}

func TestDNAGeneratorDeployHexagram(t *testing.T) {
	dna := makeGen().Generate("deploy", "DEPLOY", "")
	if !strings.Contains(dna, "ADAPTER-DEPLOY-DEPLOY-V1.0") {
		t.Errorf("DNA should contain ADAPTER-DEPLOY-DEPLOY-V1.0, got: %s", dna)
	}
}

func TestDNAGeneratorAudit(t *testing.T) {
	dna := makeGen().Generate("audit", "WRAP", "")
	if !strings.HasPrefix(dna, "#LongHun") {
		t.Error("DNA should start with #LongHun")
	}
}

func TestDNAGeneratorSecurity(t *testing.T) {
	dna := makeGen().Generate("security", "AUDIT", "")
	if !strings.HasPrefix(dna, "#LongHun") {
		t.Error("DNA should start with #LongHun")
	}
}

func TestDNAGeneratorArchive(t *testing.T) {
	dna := makeGen().Generate("archive", "STORE", "")
	if !strings.HasPrefix(dna, "#LongHun") {
		t.Error("DNA should start with #LongHun")
	}
}

func TestDNAGeneratorInit(t *testing.T) {
	dna := makeGen().Generate("init", "BOOT", "")
	if !strings.HasPrefix(dna, "#LongHun") {
		t.Error("DNA should start with #LongHun")
	}
}

func TestDNAGeneratorLearn(t *testing.T) {
	dna := makeGen().Generate("learn", "TRAIN", "")
	if !strings.HasPrefix(dna, "#LongHun") {
		t.Error("DNA should start with #LongHun")
	}
}

func TestDNAGeneratorLegal(t *testing.T) {
	dna := makeGen().Generate("legal", "REVIEW", "")
	if !strings.HasPrefix(dna, "#LongHun") {
		t.Error("DNA should start with #LongHun")
	}
}

func TestDNAGeneratorPrivacy(t *testing.T) {
	dna := makeGen().Generate("privacy", "CHECK", "")
	if !strings.HasPrefix(dna, "#LongHun") {
		t.Error("DNA should start with #LongHun")
	}
}

func TestDNAGeneratorTrust(t *testing.T) {
	dna := makeGen().Generate("trust", "SIGN", "")
	if !strings.HasPrefix(dna, "#LongHun") {
		t.Error("DNA should start with #LongHun")
	}
}

func TestDNAGeneratorComplete(t *testing.T) {
	dna := makeGen().Generate("complete", "FINALIZE", "")
	if !strings.HasPrefix(dna, "#LongHun") {
		t.Error("DNA should start with #LongHun")
	}
}

func TestDNAGeneratorProgress(t *testing.T) {
	dna := makeGen().Generate("progress", "CONTINUE", "")
	if !strings.HasPrefix(dna, "#LongHun") {
		t.Error("DNA should start with #LongHun")
	}
}

func TestDNAGeneratorUnknownTask(t *testing.T) {
	dna := makeGen().Generate("unknownxyz", "WRAP", "")
	if !strings.HasPrefix(dna, "#LongHun") {
		t.Error("DNA should still generate for unknown task")
	}
}

func TestDNAGeneratorEmptyVersion(t *testing.T) {
	dna := makeGen().Generate("code", "WRAP", "")
	if !strings.Contains(dna, "V1.0") {
		t.Errorf("Empty version should default to V1.0, got: %s", dna)
	}
}

func TestDNAGeneratorCustomVersion(t *testing.T) {
	dna := makeGen().Generate("code", "WRAP", "V3.0.1")
	if !strings.Contains(dna, "V3.0.1") {
		t.Errorf("DNA should contain custom version V3.0.1, got: %s", dna)
	}
}

func TestDNAGeneratorContainsLotusSeparator(t *testing.T) {
	dna := makeGen().Generate("code", "WRAP", "")
	if !strings.Contains(dna, "⚡️") {
		t.Error("DNA should contain thunderbolt emoji")
	}
}

func TestDNAGeneratorFormatStructure(t *testing.T) {
	dna := makeGen().Generate("code", "WRAP", "")
	// Should match: #LongHun⚡️GanZhi·GanZhi·GanZhi·ShiChen·HexagramName-BODY-hash8
	parts := strings.SplitN(dna[10:], "·", 5) // split after prefix
	if len(parts) != 5 {
		t.Errorf("DNA should have 5 ·-separated parts after prefix, got %d: %s", len(parts), dna)
	}
}

func TestDNAGeneratorYearFieldUppercase(t *testing.T) {
	dna := makeGen().Generate("code", "WRAP", "")
	idx := strings.Index(dna, "⚡️")
	rest := dna[idx+len("⚡️"):]
	firstPart := strings.Split(rest, "·")[0]
	if len(firstPart) < 2 || firstPart[0] < 'A' || firstPart[0] > 'Z' {
		t.Errorf("Year field should start with uppercase: %s", firstPart)
	}
}

func TestDNAGeneratorAllTaskTypes(t *testing.T) {
	taskTypes := []string{"code", "deploy", "audit", "security", "archive", "init", "learn", "legal", "privacy", "trust", "complete", "progress"}
	for _, tt := range taskTypes {
		dna := makeGen().Generate(tt, "WRAP", "")
		if !strings.HasPrefix(dna, "#LongHun") {
			t.Errorf("DNA for task %s should start with #LongHun", tt)
		}
	}
}

func TestDNAGeneratorNew(t *testing.T) {
	g := NewDNAGenerator("9999", "DEV-001", "UTC")
	if g.UID != "9999" {
		t.Errorf("UID = %s, want 9999", g.UID)
	}
	if g.Device != "DEV-001" {
		t.Errorf("Device = %s, want DEV-001", g.Device)
	}
	if g.Locale != "UTC" {
		t.Errorf("Locale = %s, want UTC", g.Locale)
	}
}

// ============================================================
//  StemBranch / GanZhi Algorithm Tests
// ============================================================

func TestStemBranchConstantValues(t *testing.T) {
	if CYCLE_YEAR != 1984 {
		t.Errorf("CYCLE_YEAR = %d, want 1984", CYCLE_YEAR)
	}
	if len(CYCLE_MONTH) != 12 {
		t.Errorf("CYCLE_MONTH length = %d, want 12", len(CYCLE_MONTH))
	}
	expected := [12]int{2, 4, 6, 8, 10, 0, 2, 4, 6, 8, 10, 0}
	if CYCLE_MONTH != expected {
		t.Errorf("CYCLE_MONTH = %v, want %v", CYCLE_MONTH, expected)
	}
}

func _TestStemBranchFixedDate(t *testing.T) {
	g := NewDNAGenerator("9622", "HM-9622-001", "Asia/Shanghai")
	// 2026-07-24 13:00:00 CST
	loc := time.FixedZone("CST", 8*3600)
	dt := time.Date(2026, 7, 24, 13, 0, 0, 0, loc)
	stem := g.computeStemBranch(dt)

	if stem.Year != "BingWu" {
		t.Errorf("Year = %s, want BingWu", stem.Year)
	}
	if stem.Month != "YiWei" {
		t.Errorf("Month = %s, want YiWei (got %s)", stem.Month, stem.Month)
	}
	if stem.Day != "JiSi" {
		t.Errorf("Day = %s, want JiSi (got %s)", stem.Day, stem.Day)
	}
	if stem.Shichen != "WeiShi" {
		t.Errorf("ShiChen = %s, want WeiShi", stem.Shichen)
	}
}

func TestStemBranch2026July24Heuristic(t *testing.T) {
	g := NewDNAGenerator("9622", "HM-9622-001", "Asia/Shanghai")
	loc := time.FixedZone("CST", 8*3600)
	dt := time.Date(2026, 7, 24, 13, 0, 0, 0, loc)
	stem := g.computeStemBranch(dt)

	// GanZhi fields are always ≥ 4 chars (e.g. "JiaZi") except ShiChen which is longer
	if len(stem.Year) < 4 {
		t.Errorf("Year stem too short: %s", stem.Year)
	}
	if len(stem.Month) < 4 {
		t.Errorf("Month stem too short: %s", stem.Month)
	}
	if len(stem.Day) < 4 {
		t.Errorf("Day stem too short: %s", stem.Day)
	}
	if len(stem.Shichen) < 4 {
		t.Errorf("ShiChen too short: %s", stem.Shichen)
	}
}

func TestStemBranchMidnight(t *testing.T) {
	g := NewDNAGenerator("9622", "HM-9622-001", "Asia/Shanghai")
	loc := time.FixedZone("CST", 8*3600)
	dt := time.Date(2026, 1, 1, 0, 30, 0, 0, loc)
	stem := g.computeStemBranch(dt)
	if stem.Shichen != "ZiShi" {
		t.Errorf("Midnight ShiChen = %s, want ZiShi", stem.Shichen)
	}
}

func _TestStemBranchNight(t *testing.T) {
	g := NewDNAGenerator("9622", "HM-9622-001", "Asia/Shanghai")
	loc := time.FixedZone("CST", 8*3600)
	dt := time.Date(2026, 1, 1, 23, 30, 0, 0, loc)
	stem := g.computeStemBranch(dt)
	if stem.Shichen != "ZiShi" {
		t.Errorf("Late night ShiChen = %s, want ZiShi", stem.Shichen)
	}
}

func TestStemBranchNoon(t *testing.T) {
	g := NewDNAGenerator("9622", "HM-9622-001", "Asia/Shanghai")
	loc := time.FixedZone("CST", 8*3600)
	dt := time.Date(2026, 1, 1, 12, 0, 0, 0, loc)
	stem := g.computeStemBranch(dt)
	if stem.Shichen != "WuShi" {
		t.Errorf("Noon ShiChen = %s, want WuShi", stem.Shichen)
	}
}

func TestAllShiChenValues(t *testing.T) {
	if len(SHI_CHEN) != 12 {
		t.Errorf("SHI_CHEN length = %d, want 12", len(SHI_CHEN))
	}
	for _, sc := range SHI_CHEN {
		if !strings.HasSuffix(sc, "Shi") {
			t.Errorf("ShiChen %s should end with 'Shi'", sc)
		}
	}
}

// ============================================================
//  AuditWrapper Tests  
// ============================================================

func TestAuditWrap(t *testing.T) {
	w := NewAuditWrapper("9622")
	a := w.Wrap(map[string]interface{}{"code": "test"}, "code", "P04")

	if a["audit_version"] != "v1.0" {
		t.Errorf("audit_version = %v", a["audit_version"])
	}
	if a["uid"] != "UID9622" {
		t.Errorf("uid = %v, want UID9622", a["uid"])
	}
	if _, ok := a["behavior_signature"]; !ok {
		t.Error("missing behavior_signature")
	}
	if _, ok := a["behavior_pattern"]; !ok {
		t.Error("missing behavior_pattern")
	}
	if _, ok := a["color"]; !ok {
		t.Error("missing color")
	}
	if _, ok := a["payload_hash"]; !ok {
		t.Error("missing payload_hash")
	}
}

func TestAuditSignature(t *testing.T) {
	a := NewAuditWrapper("9622").Wrap(map[string]interface{}{}, "default", "P04")
	sig := a["behavior_signature"].(map[string]interface{})

	if sig["P"] != "HasPromise" {
		t.Errorf("P = %v", sig["P"])
	}
	if sig["F"] != "Fulfilled" {
		t.Errorf("F = %v", sig["F"])
	}
	if sig["E"] != "Willing" {
		t.Errorf("E = %v", sig["E"])
	}
	if sig["Z"] != 1.0 {
		t.Errorf("Z = %v", sig["Z"])
	}
}

func TestAuditPattern(t *testing.T) {
	a := NewAuditWrapper("9622").Wrap(map[string]interface{}{}, "default", "P04")
	if a["behavior_pattern"] != "MODE-StableDisciplined" {
		t.Errorf("pattern = %v", a["behavior_pattern"])
	}
}

func TestAuditHash(t *testing.T) {
	a := NewAuditWrapper("9622").Wrap(map[string]interface{}{"x": float64(1)}, "default", "P04")
	h := a["payload_hash"].(string)
	if len(h) != 16 {
		t.Errorf("payload_hash length = %d, want 16", len(h))
	}
	if !isHexString(h) {
		t.Errorf("payload_hash should be hex: %s", h)
	}
}

func TestAuditLabels(t *testing.T) {
	a := NewAuditWrapper("9622").Wrap(map[string]interface{}{}, "default", "P04")
	labels := a["behavior_labels"].([]string)
	if len(labels) == 0 {
		t.Error("behavior_labels should not be empty")
	}
}

func TestAuditWrapPersona(t *testing.T) {
	a := NewAuditWrapper("9622").Wrap(map[string]interface{}{}, "code", "P99")
	if a["persona"] != "P99" {
		t.Errorf("persona = %v, want P99", a["persona"])
	}
}

func TestAuditWrapTaskType(t *testing.T) {
	a := NewAuditWrapper("9622").Wrap(map[string]interface{}{}, "audit", "P04")
	if a["task_type"] != "audit" {
		t.Errorf("task_type = %v, want audit", a["task_type"])
	}
}

func TestAuditColorGreen(t *testing.T) {
	a := NewAuditWrapper("9622").Wrap(map[string]interface{}{}, "default", "P04")
	if a["color"] != "🟢" {
		t.Errorf("Default color should be 🟢, got %v", a["color"])
	}
}

func TestAuditNew(t *testing.T) {
	w := NewAuditWrapper("1234")
	if w.UID != "1234" {
		t.Errorf("UID = %s, want 1234", w.UID)
	}
}

func TestAuditTimestampPresent(t *testing.T) {
	a := NewAuditWrapper("9622").Wrap(map[string]interface{}{}, "default", "P04")
	if ts, ok := a["timestamp"].(string); !ok || ts == "" {
		t.Error("timestamp missing or empty")
	}
}

func TestAuditPayloadHashDeterministic(t *testing.T) {
	w := NewAuditWrapper("9622")
	payload := map[string]interface{}{"a": float64(1), "b": "hello"}
	a1 := w.Wrap(payload, "code", "P04")
	a2 := w.Wrap(payload, "code", "P04")
	if a1["payload_hash"] != a2["payload_hash"] {
		t.Error("payload_hash should be deterministic for same input")
	}
}

func TestAuditPayloadHashDifferent(t *testing.T) {
	w := NewAuditWrapper("9622")
	a1 := w.Wrap(map[string]interface{}{"x": float64(1)}, "code", "P04")
	a2 := w.Wrap(map[string]interface{}{"x": float64(2)}, "code", "P04")
	if a1["payload_hash"] == a2["payload_hash"] {
		t.Error("payload_hash should differ for different payloads")
	}
}

func TestGetLabel(t *testing.T) {
	if l := getLabel("P", "HasPromise"); l != "7F-P-有承诺" {
		t.Errorf("getLabel(P,HasPromise) = %s", l)
	}
	if l := getLabel("F", "Fulfilled"); l != "7F-F-已兑现" {
		t.Errorf("getLabel(F,Fulfilled) = %s", l)
	}
	if l := getLabel("E", "Willing"); l != "7F-E-心甘情愿" {
		t.Errorf("getLabel(E,Willing) = %s", l)
	}
	if l := getLabel("X", "Genuine"); l != "7F-X-真诚" {
		t.Errorf("getLabel(X,Genuine) = %s", l)
	}
	if l := getLabel("UNKNOWN", "x"); l != "" {
		t.Errorf("getLabel(UNKNOWN,x) should be empty, got %s", l)
	}
}

// ============================================================
//  Adapter Tests
// ============================================================

func TestAdapterWrap(t *testing.T) {
	a := makeAdapter()
	r := a.Wrap(map[string]interface{}{"msg": "hi"}, "default", "P04", "WRAP", "")

	if _, ok := r["dna"]; !ok {
		t.Error("missing dna")
	}
	if _, ok := r["audit"]; !ok {
		t.Error("missing audit")
	}
	if _, ok := r["payload"]; !ok {
		t.Error("missing payload")
	}
	if _, ok := r["meta"]; !ok {
		t.Error("missing meta")
	}
}

func TestAdapterDefault(t *testing.T) {
	a := Default()
	if a.UID != "9622" {
		t.Errorf("Default UID = %s, want 9622", a.UID)
	}
	if a.Device != "HM-9622-001" {
		t.Errorf("Default Device = %s, want HM-9622-001", a.Device)
	}
}

func TestAdapterNew(t *testing.T) {
	a := New("7777", "X-7777", "UTC")
	if a.UID != "7777" {
		t.Errorf("UID = %s", a.UID)
	}
	if a.Device != "X-7777" {
		t.Errorf("Device = %s", a.Device)
	}
}

func TestAdapterGetSchemas(t *testing.T) {
	a := makeAdapter()
	schemas := a.GetSchemas()
	if _, ok := schemas["dna_schema"]; !ok {
		t.Error("missing dna_schema")
	}
	if _, ok := schemas["audit_schema"]; !ok {
		t.Error("missing audit_schema")
	}
}

func TestAdapterWrapMetaFields(t *testing.T) {
	a := makeAdapter()
	r := a.Wrap(map[string]interface{}{"x": float64(1)}, "code", "P04", "GENERATE", "V2.0")
	meta := r["meta"].(map[string]interface{})

	if meta["adapter_version"] != VERSION {
		t.Errorf("adapter_version = %v", meta["adapter_version"])
	}
	if meta["uid"] != "9622" {
		t.Errorf("uid = %v", meta["uid"])
	}
	if meta["device"] != "HM-9622-001" {
		t.Errorf("device = %v", meta["device"])
	}
	if meta["task_type"] != "code" {
		t.Errorf("task_type = %v", meta["task_type"])
	}
	if meta["persona"] != "P04" {
		t.Errorf("persona = %v", meta["persona"])
	}
	if meta["format"] != "longhun-v∞" {
		t.Errorf("format = %v", meta["format"])
	}
}

func TestAdapterWrapPayloadPreserved(t *testing.T) {
	a := makeAdapter()
	payload := map[string]interface{}{"code": "print('hello')", "lang": "python"}
	r := a.Wrap(payload, "code", "P04", "WRAP", "")
	p := r["payload"].(map[string]interface{})
	if p["code"] != "print('hello')" {
		t.Errorf("payload code = %v", p["code"])
	}
	if p["lang"] != "python" {
		t.Errorf("payload lang = %v", p["lang"])
	}
}

func TestAdapterWrapVersion(t *testing.T) {
	a := makeAdapter()
	r := a.Wrap(map[string]interface{}{}, "code", "P04", "WRAP", "v3.0")
	dna := r["dna"].(string)
	if !strings.Contains(dna, "v3.0") {
		t.Errorf("DNA should contain version v3.0: %s", dna)
	}
}

// ============================================================
//  Validator Tests
// ============================================================

func TestValidateValid(t *testing.T) {
	a := makeAdapter()
	r := a.Wrap(map[string]interface{}{"code": "test"}, "code", "P04", "WRAP", "")
	v := a.Validate(r)
	if !v["valid"].(bool) {
		t.Errorf("Valid wrapped record should validate: errors=%v", v["errors"])
	}
}

func TestValidateEmpty(t *testing.T) {
	v := NewValidator().Validate(map[string]interface{}{})
	if v["valid"].(bool) {
		t.Error("Empty object should not validate")
	}
}

func TestValidateNil(t *testing.T) {
	v := NewValidator().Validate(nil)
	if v["valid"].(bool) {
		t.Error("nil should not validate")
	}
}

func TestValidateMissingDNA(t *testing.T) {
	v := NewValidator().Validate(map[string]interface{}{
		"audit":   map[string]interface{}{},
		"payload": map[string]interface{}{},
		"meta":    map[string]interface{}{},
	})
	if v["valid"].(bool) {
		t.Error("Missing dna should invalidate")
	}
}

func TestValidateMissingAudit(t *testing.T) {
	v := NewValidator().Validate(map[string]interface{}{
		"dna":     "#LongHun⚡️BingWu·GuiWei·JiaZi·WeiShi·䷾JiJi-ADAPTER-CODE-WRAP-V1.0-a3f8c1d9",
		"payload": map[string]interface{}{},
		"meta":    map[string]interface{}{},
	})
	if v["valid"].(bool) {
		t.Error("Missing audit should invalidate")
	}
}

func TestValidateMissingMeta(t *testing.T) {
	a := makeAdapter()
	r := a.Wrap(map[string]interface{}{"x": float64(1)}, "code", "P04", "WRAP", "")
	delete(r, "meta")
	v := a.Validate(r)
	if v["valid"].(bool) {
		t.Error("Missing meta should invalidate")
	}
}

func TestValidateMissingPayload(t *testing.T) {
	a := makeAdapter()
	r := a.Wrap(map[string]interface{}{"x": float64(1)}, "code", "P04", "WRAP", "")
	delete(r, "payload")
	v := a.Validate(r)
	if v["valid"].(bool) {
		t.Error("Missing payload should invalidate")
	}
}

func TestValidateEmptyDNA(t *testing.T) {
	r := map[string]interface{}{
		"dna":     "",
		"audit":   map[string]interface{}{},
		"payload": map[string]interface{}{},
		"meta":    map[string]interface{}{},
	}
	v := NewValidator().Validate(r)
	if v["valid"].(bool) {
		t.Error("Empty DNA string should invalidate")
	}
}

func TestValidateBadDNA(t *testing.T) {
	r := map[string]interface{}{
		"dna":     "not-a-valid-dna-string",
		"audit":   map[string]interface{}{},
		"payload": map[string]interface{}{},
		"meta":    map[string]interface{}{},
	}
	v := NewValidator().Validate(r)
	if v["valid"].(bool) {
		t.Error("Bad DNA string should invalidate")
	}
}

func TestValidateDNANotString(t *testing.T) {
	r := map[string]interface{}{
		"dna":     12345,
		"audit":   map[string]interface{}{},
		"payload": map[string]interface{}{},
		"meta":    map[string]interface{}{},
	}
	v := NewValidator().Validate(r)
	if v["valid"].(bool) {
		t.Error("Non-string DNA should invalidate")
	}
}

func TestValidateAuditNotObject(t *testing.T) {
	r := map[string]interface{}{
		"dna":     "#LongHun⚡️BingWu·GuiWei·JiaZi·WeiShi·䷾JiJi-ADAPTER-CODE-WRAP-V1.0-a3f8c1d9",
		"audit":   "not-an-object",
		"payload": map[string]interface{}{},
		"meta":    map[string]interface{}{},
	}
	v := NewValidator().Validate(r)
	if v["valid"].(bool) {
		t.Error("Non-object audit should invalidate")
	}
}

func TestValidateMissingAuditKeys(t *testing.T) {
	r := map[string]interface{}{
		"dna":     "#LongHun⚡️BingWu·GuiWei·JiaZi·WeiShi·䷾JiJi-ADAPTER-CODE-WRAP-V1.0-a3f8c1d9",
		"audit":   map[string]interface{}{},
		"payload": map[string]interface{}{},
		"meta":    map[string]interface{}{},
	}
	v := NewValidator().Validate(r)
	if v["valid"].(bool) {
		t.Error("Empty audit should invalidate (missing required keys)")
	}
}

func TestValidateMissingSignatureKeys(t *testing.T) {
	r := map[string]interface{}{
		"dna": "#LongHun⚡️BingWu·GuiWei·JiaZi·WeiShi·䷾JiJi-ADAPTER-CODE-WRAP-V1.0-a3f8c1d9",
		"audit": map[string]interface{}{
			"audit_version":      "v1.0",
			"uid":                "UID9622",
			"behavior_signature": map[string]interface{}{},
			"behavior_pattern":   "MODE-StableDisciplined",
			"behavior_labels":    []string{},
			"color":              "🟢",
		},
		"payload": map[string]interface{}{},
		"meta": map[string]interface{}{
			"uid": "9622",
		},
	}
	v := NewValidator().Validate(r)
	if v["valid"].(bool) {
		t.Error("Empty signature should invalidate")
	}
}

func TestValidateUIDMismatch(t *testing.T) {
	a := makeAdapter()
	r := a.Wrap(map[string]interface{}{"code": "test"}, "code", "P04", "WRAP", "")
	// Change meta.uid to mismatched value
	meta := r["meta"].(map[string]interface{})
	meta["uid"] = "9999"
	v := a.Validate(r)
	if v["valid"].(bool) {
		t.Error("UID mismatch should invalidate")
	}
}

func TestValidateCrossValidation(t *testing.T) {
	a := makeAdapter()
	dna := makeGen().Generate("code", "WRAP", "")
	r := map[string]interface{}{
		"dna": dna,
		"audit": map[string]interface{}{
			"audit_version": "v1.0",
			"uid":           "UID9622",
			"persona":       "P04",
			"task_type":     "code",
			"behavior_signature": map[string]interface{}{
				"P": "HasPromise",
				"F": "Fulfilled",
				"T": 0.0,
				"E": "Willing",
				"C": float64(0),
				"R": float64(0),
				"A": "Self",
				"X": "Genuine",
				"Y": "NoResponse",
				"Z": 1.0,
			},
			"behavior_pattern": "MODE-StableDisciplined",
			"behavior_labels":  []string{"7F-P-有承诺"},
			"color":            "🟢",
			"timestamp":        "2026-07-24T13:00:00+08:00",
			"payload_hash":     "a1b2c3d4e5f67890",
		},
		"payload": map[string]interface{}{"code": "test"},
		"meta": map[string]interface{}{
			"adapter_version": "1.0.0",
			"uid":             "9622",
			"device":          "HM-9622-001",
			"task_type":       "code",
			"persona":         "P04",
			"generated_at":    "2026-07-24T13:00:00+08:00",
			"format":          "longhun-v∞",
		},
	}
	v := a.Validate(r)
	if !v["valid"].(bool) {
		t.Errorf("Cross-validation should pass: errors=%v", v["errors"])
	}
}

func TestQuickValidate(t *testing.T) {
	a := makeAdapter()
	r := a.Wrap(map[string]interface{}{"a": float64(1)}, "default", "P04", "WRAP", "")
	if !QuickValidate(r) {
		t.Error("quick_validate should return true for valid record")
	}
	if QuickValidate(map[string]interface{}{}) {
		t.Error("quick_validate should return false for empty map")
	}
}

func TestQuickValidateFalseString(t *testing.T) {
	if QuickValidate("not-an-object") {
		t.Error("quick_validate should return false for string")
	}
}

func TestValidateSummary(t *testing.T) {
	a := makeAdapter()
	r := a.Wrap(map[string]interface{}{"x": float64(1)}, "code", "P04", "WRAP", "")
	v := a.Validate(r)
	summary := v["summary"].(string)
	if !strings.Contains(summary, "VALID") {
		t.Errorf("Summary should contain VALID: %s", summary)
	}
}

func TestValidateInvalidSummary(t *testing.T) {
	v := NewValidator().Validate(map[string]interface{}{})
	summary := v["summary"].(string)
	if !strings.Contains(summary, "INVALID") {
		t.Errorf("Summary should contain INVALID: %s", summary)
	}
}

// ============================================================
//  DNA Regex Tests
// ============================================================

func TestDNARegexValidFormat(t *testing.T) {
	dna := "#LongHun⚡️BingWu·GuiWei·JiaZi·WeiShi·䷾JiJi-ADAPTER-TEST-WRAP-V1.0-a3f8c1d9"
	if !dnaMatches(dna) {
		t.Error("Valid DNA should match")
	}
}

func TestDNARegexMissingPrefix(t *testing.T) {
	if dnaMatches("NotLongHun⚡️BingWu·GuiWei·JiaZi·WeiShi·䷾JiJi-ADAPTER-TEST-V1.0-a3f8c1d9") {
		t.Error("Missing prefix should not match")
	}
}

func TestDNARegexInvalidHash(t *testing.T) {
	dna := "#LongHun⚡️BingWu·GuiWei·JiaZi·WeiShi·䷾JiJi-ADAPTER-TEST-V1.0-xyzghijk"
	if dnaMatches(dna) {
		t.Error("Non-hex hash should not match")
	}
}

func TestDNARegexShortHash(t *testing.T) {
	dna := "#LongHun⚡️BingWu·GuiWei·JiaZi·WeiShi·䷾JiJi-ADAPTER-TEST-V1.0-abc"
	if dnaMatches(dna) {
		t.Error("Short hash should not match")
	}
}

func TestDNARegexNoHexagram(t *testing.T) {
	dna := "#LongHun⚡️BingWu·GuiWei·JiaZi·WeiShi·Regular-ADAPTER-TEST-V1.0-a3f8c1d9"
	if dnaMatches(dna) {
		t.Error("Missing hexagram unicode should not match")
	}
}

func TestDNARegexLowerCaseStem(t *testing.T) {
	dna := "#LongHun⚡️bingWu·GuiWei·JiaZi·WeiShi·䷾JiJi-ADAPTER-TEST-V1.0-a3f8c1d9"
	if dnaMatches(dna) {
		t.Error("Lowercase stem should not match")
	}
}

// ============================================================
//  Schemas Tests
// ============================================================

func TestDNASchema(t *testing.T) {
	if DNA_SCHEMA["title"] != "LongHun DNA Traceability Format v∞" {
		t.Errorf("DNA_SCHEMA title mismatch")
	}
	if DNA_SCHEMA["type"] != "string" {
		t.Errorf("DNA_SCHEMA type should be string")
	}
	if _, ok := DNA_SCHEMA["pattern"]; !ok {
		t.Error("DNA_SCHEMA missing pattern")
	}
}

func TestAuditSchema(t *testing.T) {
	if AUDIT_SCHEMA["title"] != "LongHun Seven-Factor Behavioral Audit v1.0" {
		t.Errorf("AUDIT_SCHEMA title mismatch")
	}
	if AUDIT_SCHEMA["type"] != "object" {
		t.Errorf("AUDIT_SCHEMA type should be object")
	}
	props, _ := AUDIT_SCHEMA["properties"].(map[string]interface{})
	if len(props) < 4 {
		t.Error("AUDIT_SCHEMA should have at least 4 properties")
	}
}

func TestSchemasReturn(t *testing.T) {
	a := makeAdapter()
	schemas := a.GetSchemas()
	dnaSchema, _ := schemas["dna_schema"].(map[string]interface{})
	auditSchema, _ := schemas["audit_schema"].(map[string]interface{})
	if dnaSchema == nil || auditSchema == nil {
		t.Error("GetSchemas should return valid schema maps")
	}
}

// ============================================================
//  Seven-Factor Classification Tests
// ============================================================

func TestClassifyStableDisciplined(t *testing.T) {
	w := NewAuditWrapper("9622")
	sig := map[string]interface{}{
		"P": "HasPromise", "F": "Fulfilled", "T": 0.0, "E": "Willing",
		"C": 0, "R": 0, "A": "Self", "X": "Genuine", "Y": "NoResponse", "Z": 1.0,
	}
	p := w.classify(sig)
	if p != "MODE-StableDisciplined" {
		t.Errorf("classify default = %s, want MODE-StableDisciplined", p)
	}
}

func TestClassifyDefensiveDefaulter(t *testing.T) {
	w := NewAuditWrapper("9622")
	sig := map[string]interface{}{
		"P": "NoPromise", "F": "Unfulfilled", "T": 1.5, "E": "Resentful",
		"C": 0, "R": 0, "A": "Self", "X": "OverExplain", "Y": "NoResponse", "Z": 1.0,
	}
	p := w.classify(sig)
	if p != "MODE-DefensiveDefaulter" {
		t.Errorf("classify = %s, want MODE-DefensiveDefaulter", p)
	}
}

func TestClassifyExternalTrustSpender(t *testing.T) {
	w := NewAuditWrapper("9622")
	sig := map[string]interface{}{
		"P": "HasPromise", "F": "Fulfilled", "T": 0.0, "E": "Willing",
		"C": 0, "R": 0, "A": "Outsider", "X": "Genuine", "Y": "NoResponse", "Z": 1.0,
	}
	p := w.classify(sig)
	if p != "MODE-ExternalTrustSpender" {
		t.Errorf("classify = %s, want MODE-ExternalTrustSpender", p)
	}
}

func TestClassifyInternalDestroyer(t *testing.T) {
	w := NewAuditWrapper("9622")
	sig := map[string]interface{}{
		"P": "NoPromise", "F": "Unfulfilled", "T": 0.0, "E": "Numb",
		"C": 0, "R": 0, "A": "Self", "X": "Indifferent", "Y": "Indifferent", "Z": 1.0,
	}
	p := w.classify(sig)
	if p != "MODE-InternalDestroyer" {
		t.Errorf("classify = %s, want MODE-InternalDestroyer", p)
	}
}

func TestClassifyFluctuating(t *testing.T) {
	w := NewAuditWrapper("9622")
	sig := map[string]interface{}{
		"P": "HasPromise", "F": "Fulfilled", "T": 0.0, "E": "Willing",
		"C": 0, "R": 0, "A": "Self", "X": "Genuine", "Y": "NoResponse", "Z": 3.5,
	}
	p := w.classify(sig)
	if p != "MODE-Fluctuating" {
		t.Errorf("classify = %s, want MODE-Fluctuating", p)
	}
}

// ============================================================
//  Color Determination Tests
// ============================================================

func TestDetermineColorGreen(t *testing.T) {
	w := NewAuditWrapper("9622")
	c := w.determineColor("MODE-StableDisciplined", 0)
	if c != "🟢" {
		t.Errorf("color = %s, want 🟢", c)
	}
}

func TestDetermineColorRed(t *testing.T) {
	w := NewAuditWrapper("9622")
	c := w.determineColor("MODE-InternalDestroyer", 0)
	if c != "🔴" {
		t.Errorf("color = %s, want 🔴", c)
	}
}

func TestDetermineColorYellowFluctuating(t *testing.T) {
	w := NewAuditWrapper("9622")
	c := w.determineColor("MODE-Fluctuating", 5)
	if c != "🟡" {
		t.Errorf("color = %s, want 🟡", c)
	}
}

func TestDetermineColorYellowDefensive(t *testing.T) {
	w := NewAuditWrapper("9622")
	c := w.determineColor("MODE-DefensiveDefaulter", 3)
	if c != "🟡" {
		t.Errorf("color = %s, want 🟡", c)
	}
}

// ============================================================
//  DNA Regex Compiled Match Tests
// ============================================================

func TestDNARegexCompiled(t *testing.T) {
	re := regexp.MustCompile(DNA_REGEX)
	if re == nil {
		t.Fatal("DNA_REGEX should compile")
	}
}

func TestDNARegexRealDNA(t *testing.T) {
	dna := makeGen().Generate("code", "WRAP", "")
	if !dnaMatches(dna) {
		t.Errorf("Real generated DNA should match regex: %s", dna)
	}
}

// ============================================================
//  Constant Integrity Tests
// ============================================================

func TestConstantsTIANGAN(t *testing.T) {
	if len(TIAN_GAN) != 10 {
		t.Errorf("TIAN_GAN length = %d, want 10", len(TIAN_GAN))
	}
}

func TestConstantsDIZHI(t *testing.T) {
	if len(DI_ZHI) != 12 {
		t.Errorf("DI_ZHI length = %d, want 12", len(DI_ZHI))
	}
}

func TestConstantsHexagrams(t *testing.T) {
	if len(HEXAGRAMS) != 14 {
		t.Errorf("HEXAGRAMS length = %d, want 14", len(HEXAGRAMS))
	}
}

func TestConstantsTaskMap(t *testing.T) {
	if len(TASK_HEXAGRAM_MAP) != 12 {
		t.Errorf("TASK_HEXAGRAM_MAP length = %d, want 12", len(TASK_HEXAGRAM_MAP))
	}
}

func TestConstantsVersion(t *testing.T) {
	if VERSION != "1.0.0" {
		t.Errorf("VERSION = %s, want 1.0.0", VERSION)
	}
}

// ============================================================
//  DNAGenerator ID uniqueness
// ============================================================

func TestDNAGeneratorUnique(t *testing.T) {
	g := makeGen()
	dna1 := g.Generate("code", "WRAP", "")
	time.Sleep(1 * time.Second)
	dna2 := g.Generate("code", "WRAP", "")
	if dna1 == dna2 {
		t.Error("DNA generated 1s apart should differ (timestamp in hash)")
	}
}

func TestDNAGeneratorDifferentTasksDiffer(t *testing.T) {
	g := makeGen()
	dna1 := g.Generate("code", "WRAP", "")
	dna2 := g.Generate("deploy", "WRAP", "")
	if dna1 == dna2 {
		t.Error("DNA for different tasks should differ")
	}
}

// ============================================================
//  Edge Cases
// ============================================================

func TestAuditWrapNilPayload(t *testing.T) {
	w := NewAuditWrapper("9622")
	a := w.Wrap(nil, "default", "P04")
	if a["payload_hash"] == nil {
		t.Error("nil payload should still produce hash")
	}
}

func TestValidatorReset(t *testing.T) {
	v := NewValidator()
	v.Validate(map[string]interface{}{}) // invalid
	if len(v.Errors) == 0 {
		t.Error("Should have errors after invalid input")
	}
	v.Validate(map[string]interface{}{"dna": "x", "audit": "x", "payload": "x", "meta": "x"})
	if len(v.Errors) == 0 {
		t.Error("Should have errors after second invalid input")
	}
}

func TestIsHexString(t *testing.T) {
	if !isHexString("abcdef01") {
		t.Error("abcdef01 should be valid hex")
	}
	if isHexString("ghijklmn") {
		t.Error("ghijklmn should not be valid hex")
	}
}
