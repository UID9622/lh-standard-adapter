package lhstandard

import (
	"crypto/sha256"
	"encoding/json"
	"fmt"
	"math/rand"
	"strings"
	"time"
)

const VERSION = "1.0.0"

var TianGan = []string{"Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"}
var DiZhi = []string{"Zi", "Chou", "Yin", "Mao", "Chen", "Si", "Wu", "Wei", "Shen", "You", "Xu", "Hai"}

type DNAResult struct {
	Code        string ` + "`json:"code"`" + `
	Domain      string ` + "`json:"domain"`" + `
	GeneratedAt string ` + "`json:"generatedAt"`" + `
	Device      string ` + "`json:"device"`" + `
	UID         string ` + "`json:"uid"`" + `
}

type DNAGenerator struct {
	UID, Device, Locale string
}

func NewDNAGenerator(uid, device, locale string) *DNAGenerator {
	return &DNAGenerator{UID: uid, Device: device, Locale: locale}
}

func (g *DNAGenerator) Generate(taskType, action, version string) *DNAResult {
	now := time.Now()
	domain := "governance"
	if taskType == "code" {
		domain = "engine"
	}
	if version == "" {
		version = "v1.0.0"
	}
	body := fmt.Sprintf("%s-%s-%s", strings.ToUpper(taskType), action, version)
	gan1 := TianGan[(now.YearDay()-1)%10]
	gan2 := TianGan[now.YearDay()%10]
	zhi1 := DiZhi[int(now.Month())%12]
	zhi2 := DiZhi[now.Hour()%12]
	dnaStr := fmt.Sprintf("#LongHun\u26a1%sn%s\u00b7%sn%s\u00b7%s\u00b7%s-%s", gan1, gan2, zhi1, zhi2, body)
	h := sha256.Sum256([]byte(dnaStr))
	hash8 := fmt.Sprintf("%x", h[:4])
	return &DNAResult{
		Code: fmt.Sprintf("%s-%s", dnaStr, hash8), Domain: domain,
		GeneratedAt: now.UTC().Format(time.RFC3339),
		Device: g.Device, UID: g.UID,
	}
}

type AuditResult struct {
	AuditVersion      string            ` + "`json:"auditVersion"`" + `
	UID               string            ` + "`json:"uid"`" + `
	BehaviorSignature map[string]string ` + "`json:"behaviorSignature"`" + `
	BehaviorPattern   string            ` + "`json:"behaviorPattern"`" + `
	Color             string            ` + "`json:"color"`" + `
}

type AuditWrapper struct{ UID string }

func NewAuditWrapper(uid string) *AuditWrapper {
	if uid == "" { uid = "9622" }; return &AuditWrapper{UID: uid}
}

func (w *AuditWrapper) Wrap(payload interface{}, taskType, persona string) *AuditResult {
	rng := rand.New(rand.NewSource(time.Now().UnixNano()))
	choices := func(arr []string) string { return arr[rng.Intn(len(arr))] }
	sig := map[string]string{
		"P": choices([]string{"HasPromise", "NoPromise"}),
		"F": choices([]string{"Fulfilled", "Unfulfilled", "Partial"}),
		"T": strings.ToUpper(taskType),
		"E": choices([]string{"Willing", "Perfunctory", "Resentful", "Numb"}),
		"C": w.UID, "R": persona,
		"A": choices([]string{"Self", "Partner", "Family", "Outsider", "Public"}),
		"X": choices([]string{"OverExplain", "Silent", "Genuine", "Indifferent"}),
		"Y": choices([]string{"Changed", "Resisted", "Indifferent", "NoResponse"}),
		"Z": fmt.Sprintf("%x", sha256.Sum256([]byte(time.Now().String())))[:4],
	}
	pattern := "MODE-StableDisciplined"
	if sig["P"] == "NoPromise" && sig["X"] == "OverExplain" {
		pattern = "MODE-DefensiveDefaulter"
	}
	return &AuditResult{
		AuditVersion: "v1.0.0", UID: w.UID,
		BehaviorSignature: sig, BehaviorPattern: pattern,
		Color: "\U0001f7e2",
	}
}

func NewAdapter(uid, device string) *Adapter {
	if uid == "" { uid = "9622" }; if device == "" { device = "HM-9622-001" }
	return &Adapter{
		UID: uid, Device: device, Locale: "Asia/Shanghai",
		dnaGen: NewDNAGenerator(uid, device, "Asia/Shanghai"),
		audit:  NewAuditWrapper(uid),
	}
}

type WrapResult struct {
	DNA     *DNAResult              ` + "`json:"dna"`" + `
	Audit   *AuditResult            ` + "`json:"audit"`" + `
	Payload interface{}             ` + "`json:"payload"`" + `
	Meta    map[string]interface{}  ` + "`json:"meta"`" + `
}

type Adapter struct {
	UID, Device, Locale string
	dnaGen *DNAGenerator; audit *AuditWrapper
}

func (a *Adapter) Wrap(data interface{}, taskType, persona, action, version string) *WrapResult {
	if taskType == "" { taskType = "default" }; if persona == "" { persona = "P04" }
	if action == "" { action = "WRAP" }
	return &WrapResult{
		DNA: a.dnaGen.Generate(taskType, action, version),
		Audit: a.audit.Wrap(data, taskType, persona),
		Payload: data,
		Meta: map[string]interface{}{
			"adapterVersion": VERSION, "uid": a.UID, "device": a.Device,
			"taskType": taskType, "persona": persona,
			"generatedAt": time.Now().UTC().Format(time.RFC3339), "format": "longhun-v",
		},
	}
}

func (r *WrapResult) ToJSON() (string, error) {
	b, err := json.MarshalIndent(r, "", "  ")
	if err != nil { return "", err }; return string(b), nil
}
