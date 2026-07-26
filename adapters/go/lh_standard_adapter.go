// Package lh_standard_adapter implements the LongHun Standard Adapter in Go.
// DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷝Li-ADAPTER-CODE-WRAP-V1.0.0
package lh_standard_adapter

import (
	"crypto/sha256"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"time"
)

var (
	PValues = []string{"HasPromise", "NoPromise"}
	FValues = []string{"Fulfilled", "Unfulfilled", "Partial"}
	EValues = []string{"Willing", "Perfunctory", "Resentful", "Numb"}
	AValues = []string{"Self", "Partner", "Family", "Outsider", "Public"}
	XValues = []string{"OverExplain", "Silent", "Genuine", "Indifferent"}
	YValues = []string{"Changed", "Resisted", "Indifferent", "NoResponse"}

	patterns = map[string]string{
		"MODE-DefensiveDefaulter":    "Promises fail + over-explains to deflect",
		"MODE-ExternalTrustSpender":  "Keeps promises to outsiders at inner-circle expense",
		"MODE-InternalDestroyer":     "Breaks promises with indifference, no correction",
		"MODE-Fluctuating":           "High volatility in commitment-to-fulfillment ratio",
		"MODE-StableDisciplined":     "Consistent, reliable execution",
	}
	labelMap = map[string]map[string]string{
		"P": {"HasPromise": "7F-P-有承诺", "NoPromise": "7F-P-无承诺"},
		"F": {"Fulfilled": "7F-F-已兑现", "Unfulfilled": "7F-F-未兑现", "Partial": "7F-F-部分兑现"},
		"E": {"Willing": "7F-E-心甘情愿", "Perfunctory": "7F-E-敷衍", "Resentful": "7F-E-怨恨", "Numb": "7F-E-麻木"},
		"A": {"Self": "7F-A-自己", "Partner": "7F-A-伴侣", "Family": "7F-A-家庭", "Outsider": "7F-A-外人", "Public": "7F-A-公众"},
		"X": {"OverExplain": "7F-X-过度解释", "Silent": "7F-X-沉默", "Genuine": "7F-X-真诚", "Indifferent": "7F-X-冷漠"},
		"Y": {"Changed": "7F-Y-改正", "Resisted": "7F-Y-抗拒", "Indifferent": "7F-Y-无视", "NoResponse": "7F-Y-无响应"},
	}
)

type Signature struct {
	P string  `json:"P"`
	F string  `json:"F"`
	T float64 `json:"T"`
	E string  `json:"E"`
	C int     `json:"C"`
	R int     `json:"R"`
	A string  `json:"A"`
	X string  `json:"X"`
	Y string  `json:"Y"`
	Z float64 `json:"Z"`
}

type Audit struct {
	AuditVersion       string            `json:"audit_version"`
	UID                string            `json:"uid"`
	Persona            string            `json:"persona,omitempty"`
	TaskType           string            `json:"task_type,omitempty"`
	BehaviorSignature  Signature         `json:"behavior_signature"`
	BehaviorPattern    string            `json:"behavior_pattern"`
	BehaviorLabels     []string          `json:"behavior_labels"`
	Color              string            `json:"color"`
	Timestamp          string            `json:"timestamp"`
	PayloadHash        string            `json:"payload_hash"`
}

type Wrapped struct {
	DNA   string                 `json:"dna"`
	Audit Audit                  `json:"audit"`
	Meta  map[string]interface{} `json:"meta"`
}

func hash8(s string) string {
	h := sha256.Sum256([]byte(s))
	return hex.EncodeToString(h[:])[:8]
}

func hash16(s string) string {
	h := sha256.Sum256([]byte(s))
	return hex.EncodeToString(h[:])[:16]
}

func classify(sig Signature) string {
	if sig.F == "Unfulfilled" && sig.X == "OverExplain" {
		return "MODE-DefensiveDefaulter"
	}
	if sig.F == "Fulfilled" && sig.A == "Outsider" {
		return "MODE-ExternalTrustSpender"
	}
	if sig.F == "Unfulfilled" && sig.Y == "Indifferent" {
		return "MODE-InternalDestroyer"
	}
	if sig.Z > 2.0 {
		return "MODE-Fluctuating"
	}
	return "MODE-StableDisciplined"
}

func color(pattern string, repeat int) string {
	if pattern == "MODE-InternalDestroyer" {
		return "🔴"
	}
	if pattern == "MODE-Fluctuating" && repeat > 3 {
		return "🟡"
	}
	if pattern == "MODE-DefensiveDefaulter" && repeat > 2 {
		return "🟡"
	}
	return "🟢"
}

func makeLabels(sig Signature, pattern string) []string {
	var out []string
	for _, f := range []string{"P", "F", "E", "A", "X", "Y"} {
		m := labelMap[f]
		if m != nil && m[getField(&sig, f)] != "" {
			out = append(out, m[getField(&sig, f)])
		}
	}
	out = append(out, pattern)
	return out
}

func getField(s *Signature, f string) string {
	switch f {
	case "P": return s.P
	case "F": return s.F
	case "E": return s.E
	case "A": return s.A
	case "X": return s.X
	case "Y": return s.Y
	default: return ""
	}
}

type LongHunAdapter struct {
	UID    string
	Device string
}

func New(uid, device string) *LongHunAdapter {
	if uid == "" { uid = "9622" }
	if device == "" { device = "HM-9622-001" }
	return &LongHunAdapter{UID: uid, Device: device}
}

func (a *LongHunAdapter) Wrap(data interface{}, taskType, persona string) (Wrapped, error) {
	payload, _ := json.Marshal(data)
	body := fmt.Sprintf("ADAPTER-%s-WRAP-V1.0", taskType)
	h := hash8(body)
	dna := fmt.Sprintf("#LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷝Li-%s-%s", body, h)
	sig := Signature{P: "HasPromise", F: "Fulfilled", T: 0, E: "Willing", C: 0, R: 0, A: "Self", X: "Genuine", Y: "NoResponse", Z: 1.0}
	pat := classify(sig)
	audit := Audit{
		AuditVersion: "v1.0",
		UID:          fmt.Sprintf("UID%s", a.UID),
		Persona:      persona,
		TaskType:     taskType,
		BehaviorSignature: sig,
		BehaviorPattern:   pat,
		BehaviorLabels:    makeLabels(sig, pat),
		Color:             color(pat, sig.R),
		Timestamp:         time.Now().UTC().Format(time.RFC3339),
		PayloadHash:       hash16(string(payload)),
	}
	return Wrapped{
		DNA:   dna,
		Audit: audit,
		Meta: map[string]interface{}{"adapter_version": "1.0.0", "uid": a.UID, "format": "longhun-v∞", "device": a.Device},
	}, nil
}

func (a *LongHunAdapter) Validate(w Wrapped) (bool, []string, []string) {
	var errs, warns []string
	if w.DNA == "" { errs = append(errs, "DNA field is empty") }
	if w.Audit.UID == "" { errs = append(errs, "Missing audit keys") }
	if len(w.Audit.BehaviorLabels) == 0 { warns = append(warns, "No behavior labels") }
	return len(errs) == 0, errs, warns
}
