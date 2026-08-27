package lhstandardadapter

import (
	"crypto/sha256"
	"encoding/hex"
	"encoding/json"
	"time"
)

// Seven-factor value sets (public standard)

var PValues = []string{"HasPromise", "NoPromise"}
var FValues = []string{"Fulfilled", "Unfulfilled", "Partial"}
var EValues = []string{"Willing", "Perfunctory", "Resentful", "Numb"}
var AValues = []string{"Self", "Partner", "Family", "Outsider", "Public"}
var XValues = []string{"OverExplain", "Silent", "Genuine", "Indifferent"}
var YValues = []string{"Changed", "Resisted", "Indifferent", "NoResponse"}

// BehaviorPattern classifications
var Patterns = map[string]string{
	"MODE-DefensiveDefaulter":    "Promises fail + over-explains to deflect",
	"MODE-ExternalTrustSpender":  "Keeps promises to outsiders at inner-circle expense",
	"MODE-InternalDestroyer":     "Breaks promises with indifference, no correction",
	"MODE-Fluctuating":           "High volatility in commitment-to-fulfillment ratio",
	"MODE-StableDisciplined":     "Consistent, reliable execution",
}

// factorLabelMap maps factor → value → bilingual label.
var factorLabelMap = map[string]map[string]string{
	"P": {"HasPromise": "7F-P-有承诺", "NoPromise": "7F-P-无承诺"},
	"F": {"Fulfilled": "7F-F-已兑现", "Unfulfilled": "7F-F-未兑现", "Partial": "7F-F-部分兑现"},
	"E": {"Willing": "7F-E-心甘情愿", "Perfunctory": "7F-E-敷衍",
		"Resentful": "7F-E-怨恨", "Numb": "7F-E-麻木"},
	"A": {"Self": "7F-A-自己", "Partner": "7F-A-伴侣",
		"Family": "7F-A-家庭", "Outsider": "7F-A-外人", "Public": "7F-A-公众"},
	"X": {"OverExplain": "7F-X-过度解释", "Silent": "7F-X-沉默",
		"Genuine": "7F-X-真诚", "Indifferent": "7F-X-冷漠"},
	"Y": {"Changed": "7F-Y-改正", "Resisted": "7F-Y-抗拒",
		"Indifferent": "7F-Y-无视", "NoResponse": "7F-Y-无响应"},
}

// BehaviorSignature holds the seven-factor audit values.
type BehaviorSignature struct {
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

// AuditRecord holds the full audit wrapper output.
type AuditRecord struct {
	AuditVersion      string           `json:"audit_version"`
	UID               string           `json:"uid"`
	Persona           string           `json:"persona"`
	TaskType          string           `json:"task_type"`
	BehaviorSignature BehaviorSignature `json:"behavior_signature"`
	BehaviorPattern   string           `json:"behavior_pattern"`
	BehaviorLabels    []string         `json:"behavior_labels"`
	Color             string           `json:"color"`
	Timestamp         string           `json:"timestamp"`
	PayloadHash       string           `json:"payload_hash"`
}

// AuditWrapper wraps payloads with seven-factor behavioral audit metadata.
type AuditWrapper struct {
	UID string
}

// NewAuditWrapper creates an AuditWrapper with the given UID.
func NewAuditWrapper(uid string) *AuditWrapper {
	return &AuditWrapper{UID: uid}
}

// Wrap generates an audit wrapper with seven-factor signature.
func (w *AuditWrapper) Wrap(payload interface{}, taskType, persona string) AuditRecord {
	if taskType == "" {
		taskType = "default"
	}
	if persona == "" {
		persona = "P04"
	}

	now := time.Now().UTC().Add(cstOffset * time.Hour)

	// Default signature (StableDisciplined baseline)
	sig := BehaviorSignature{
		P: "HasPromise",
		F: "Fulfilled",
		T: 0.0,
		E: "Willing",
		C: 0,
		R: 0,
		A: "Self",
		X: "Genuine",
		Y: "NoResponse",
		Z: 1.0,
	}

	pattern := classify(sig)
	labels := makeLabels(sig, pattern)
	color := determineColor(pattern, sig.R)

	payloadHash := computePayloadHash(payload)

	return AuditRecord{
		AuditVersion:      "v1.0",
		UID:               "UID" + w.UID,
		Persona:           persona,
		TaskType:          taskType,
		BehaviorSignature: sig,
		BehaviorPattern:   pattern,
		BehaviorLabels:    labels,
		Color:             color,
		Timestamp:         now.Format(time.RFC3339Nano),
		PayloadHash:       payloadHash,
	}
}

// classify classifies seven-factor signature into behavior pattern.
func classify(sig BehaviorSignature) string {
	switch {
	case sig.F == "Unfulfilled" && sig.X == "OverExplain":
		return "MODE-DefensiveDefaulter"
	case sig.F == "Fulfilled" && sig.A == "Outsider":
		return "MODE-ExternalTrustSpender"
	case sig.F == "Unfulfilled" && sig.Y == "Indifferent":
		return "MODE-InternalDestroyer"
	case sig.Z > 2.0:
		return "MODE-Fluctuating"
	default:
		return "MODE-StableDisciplined"
	}
}

// makeLabels generates bilingual behavior labels from signature.
func makeLabels(sig BehaviorSignature, pattern string) []string {
	labels := []string{}
	factors := []struct {
		key string
		val string
	}{
		{"P", sig.P},
		{"F", sig.F},
		{"E", sig.E},
		{"A", sig.A},
		{"X", sig.X},
		{"Y", sig.Y},
	}
	for _, f := range factors {
		if m, ok := factorLabelMap[f.key]; ok {
			if label, ok := m[f.val]; ok {
				labels = append(labels, label)
			}
		}
	}
	labels = append(labels, pattern)
	return labels
}

// determineColor determines three-color audit tag.
func determineColor(pattern string, repeat int) string {
	switch {
	case pattern == "MODE-InternalDestroyer":
		return "🔴"
	case pattern == "MODE-Fluctuating" && repeat > 3:
		return "🟡"
	case pattern == "MODE-DefensiveDefaulter" && repeat > 2:
		return "🟡"
	default:
		return "🟢"
	}
}

// computePayloadHash computes SHA-256 hash of JSON-serialized payload (first 16 hex chars).
func computePayloadHash(payload interface{}) string {
	b, err := json.Marshal(payload)
	if err != nil {
		return ""
	}
	h := sha256.Sum256(b)
	return hex.EncodeToString(h[:])[:16]
}
