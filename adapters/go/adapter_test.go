package lhstandard

import "testing"

func assert(t *testing.T, cond bool, msg string) { if !cond { t.Error(msg) } }

func TestDNAGeneratorInstance(t *testing.T) {
	g := NewDNAGenerator("","","")
	if g.UID != "9622" { t.Error("default uid") }
	if g.Device != "HM-9622-001" { t.Error("default device") }
}

func TestDNAGeneratorCustom(t *testing.T) {
	g := NewDNAGenerator("9999","T1","")
	if g.UID != "9999" || g.Device != "T1" { t.Error("custom") }
}

func TestGeneratePrefix(t *testing.T) {
	g := NewDNAGenerator("","","")
	dna := g.Generate("","","")
	if len(dna) == 0 || dna[:10] != "#LongHun⚡️" { t.Error("prefix") }
}

func TestGenerateDnaUnique(t *testing.T) {
	g := NewDNAGenerator("","","")
	seen := make(map[string]bool)
	for i := 0; i < 10; i++ {
		dna := g.Generate("code","GEN",fmt.Sprintf("V%d",i))
		if seen[dna] { t.Error("duplicate dna") }
		seen[dna] = true
	}
}

func TestAuditWrapperInstance(t *testing.T) {
	w := NewAuditWrapper("")
	if w.UID != "9622" { t.Error("default uid") }
}

func TestAuditWrap(t *testing.T) {
	w := NewAuditWrapper("")
	r := w.Wrap(map[string]interface{}{"x":1},"","")
	if r["audit_version"] != "v1.0" { t.Error("version") }
	if r["uid"] != "UID9622" { t.Error("uid") }
}

func TestAuditSignature(t *testing.T) {
	w := NewAuditWrapper("")
	r := w.Wrap(map[string]interface{}{},"","")
	sig := r["behavior_signature"].(map[string]interface{})
	for _, k := range []string{"P","F","T","E","C","R","A","X","Y","Z"} {
		if _, ok := sig[k]; !ok { t.Error("missing sig key:",k) }
	}
}

func TestAuditPattern(t *testing.T) {
	w := NewAuditWrapper("")
	r := w.Wrap(map[string]interface{}{},"","")
	if r["behavior_pattern"] != "MODE-StableDisciplined" { t.Error("pattern") }
}

func TestValidatorNil(t *testing.T) {
	v := NewValidator()
	r := v.Validate(nil)
	if r.Valid { t.Error("nil should be invalid") }
}

func TestValidatorValid(t *testing.T) {
	a := NewAdapter("","","")
	w := a.Wrap(map[string]interface{}{"h":"w"},"","","","")
	v := NewValidator()
	r := v.Validate(w)
	if !r.Valid { t.Error("should be valid:", r.Errors) }
}

func TestValidatorMissingDNA(t *testing.T) {
	v := NewValidator()
	r := v.Validate(map[string]interface{}{"audit":map[string]interface{}{},"payload":map[string]interface{}{},"meta":map[string]interface{}{}})
	if r.Valid { t.Error("missing dna should be invalid") }
}

func TestQuickValidate(t *testing.T) {
	a := NewAdapter("","","")
	w := a.Wrap(map[string]interface{}{},"","","","")
	if !QuickValidate(w) { t.Error("quick should pass") }
	if QuickValidate(nil) { t.Error("nil should fail") }
}

func TestAdapterInstance(t *testing.T) {
	a := NewAdapter("","","")
	if a.UID != "9622" || a.Device != "HM-9622-001" { t.Error("defaults") }
}

func TestAdapterWrap(t *testing.T) {
	a := NewAdapter("","","")
	r := a.Wrap(map[string]interface{}{"code":"x"},"","","","")
	for _, k := range []string{"dna","audit","payload","meta"} {
		if _, ok := r[k]; !ok { t.Error("missing key:",k) }
	}
}

func TestAdapterValidate(t *testing.T) {
	a := NewAdapter("","","")
	w := a.Wrap(map[string]interface{}{},"","","","")
	r := a.Validate(w)
	if !r.Valid { t.Error("self-validate fail:", r.Errors) }
}

func TestGetSchemas(t *testing.T) {
	a := NewAdapter("","","")
	d, au := a.GetSchemas()
	if d == nil || au == nil { t.Error("schemas should not be nil") }
}

func TestUIDConsistency(t *testing.T) {
	a := NewAdapter("8888","","")
	w := a.Wrap(map[string]interface{}{},"","","","")
	meta := w["meta"].(map[string]interface{})
	audit := w["audit"].(map[string]interface{})
	if meta["uid"] != "8888" { t.Error("meta uid") }
	if audit["uid"] != "UID8888" { t.Error("audit uid") }
}

func TestClassifyDD(t *testing.T) {
	sig := map[string]interface{}{"F":"Unfulfilled","X":"OverExplain"}
	if classify(sig) != "MODE-DefensiveDefaulter" { t.Error("dd") }
}

func TestClassifyID(t *testing.T) {
	sig := map[string]interface{}{"F":"Unfulfilled","Y":"Indifferent"}
	if classify(sig) != "MODE-InternalDestroyer" { t.Error("id") }
}

func TestClassifyFluc(t *testing.T) {
	sig := map[string]interface{}{"Z":3.0}
	if classify(sig) != "MODE-Fluctuating" { t.Error("fluc") }
}
