package adapter

import (
	"crypto/sha256"
	"encoding/json"
	"fmt"
	"time"
)

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

type AuditResult struct {
	AuditVersion      string            `json:"audit_version"`
	UID               string            `json:"uid"`
	Persona           string            `json:"persona"`
	TaskType          string            `json:"task_type"`
	BehaviorSignature BehaviorSignature `json:"behavior_signature"`
	BehaviorPattern   string            `json:"behavior_pattern"`
	BehaviorLabels    []string          `json:"behavior_labels"`
	Color             string            `json:"color"`
	Timestamp         string            `json:"timestamp"`
	PayloadHash       string            `json:"payload_hash"`
}

var LabelMap = map[string]map[string]string{
	"P": {"HasPromise": "7F-P-有承诺", "NoPromise": "7F-P-无承诺"},
	"F": {"Fulfilled": "7F-F-已兑现", "Unfulfilled": "7F-F-未兑现", "Partial": "7F-F-部分兑现"},
	"E": {"Willing": "7F-E-心甘情愿", "Perfunctory": "7F-E-敷衍", "Resentful": "7F-E-怨恨", "Numb": "7F-E-麻木"},
	"A": {"Self": "7F-A-自己", "Partner": "7F-A-伴侣", "Family": "7F-A-家庭", "Outsider": "7F-A-外人", "Public": "7F-A-公众"},
	"X": {"OverExplain": "7F-X-过度解释", "Silent": "7F-X-沉默", "Genuine": "7F-X-真诚", "Indifferent": "7F-X-冷漠"},
	"Y": {"Changed": "7F-Y-改正", "Resisted": "7F-Y-抗拒", "Indifferent": "7F-Y-无视", "NoResponse": "7F-Y-无响应"},
}

type AuditWrapper struct {
	UID string
}

func NewAuditWrapper(uid string) *AuditWrapper {
	if uid == "" {
		uid = "9622"
	}
	return &AuditWrapper{UID: uid}
}

func (w *AuditWrapper) Wrap(payload interface{}, taskType, persona string) (*AuditResult, error) {
	if taskType == "" {
		taskType = "default"
	}
	if persona == "" {
		persona = "P04"
	}

	loc := time.FixedZone("Asia/Shanghai", 8*3600)
	now := time.Now().In(loc)

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

	pattern := w.classify(sig)
	labels := w.makeLabels(sig, pattern)
	color := w.determineColor(pattern, sig.R)

	payloadBytes, err := json.Marshal(payload)
	if err != nil {
		return nil, err
	}
	hash := sha256.Sum256(payloadBytes)
	payloadHash := fmt.Sprintf("%x", hash[:8])

	return &AuditResult{
		AuditVersion:      "v1.0",
		UID:               fmt.Sprintf("UID%s", w.UID),
		Persona:           persona,
		TaskType:          taskType,
		BehaviorSignature: sig,
		BehaviorPattern:   pattern,
		BehaviorLabels:    labels,
		Color:             color,
		Timestamp:         now.Format(time.RFC3339),
		PayloadHash:       payloadHash,
	}, nil
}

func (w *AuditWrapper) classify(sig BehaviorSignature) string {
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

func (w *AuditWrapper) makeLabels(sig BehaviorSignature, pattern string) []string {
	labels := make([]string, 0)
	factors := []string{"P", "F", "E", "A", "X", "Y"}
	vals := map[string]string{
		"P": sig.P, "F": sig.F, "E": sig.E, "A": sig.A, "X": sig.X, "Y": sig.Y,
	}

	for _, f := range factors {
		v := vals[f]
		if m, ok := LabelMap[f]; ok {
			if l, exists := m[v]; exists {
				labels = append(labels, l)
			}
		}
	}
	labels = append(labels, pattern)
	return labels
}

func (w *AuditWrapper) determineColor(pattern string, repeat int) string {
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
