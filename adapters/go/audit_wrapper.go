package lh_adapter

import (
	"crypto/sha256"
	"encoding/json"
	"fmt"
	"time"
)

// --- Seven-Factor Value Sets ---

var P_VALUES = []string{"HasPromise", "NoPromise"}
var F_VALUES = []string{"Fulfilled", "Unfulfilled", "Partial"}
var E_VALUES = []string{"Willing", "Perfunctory", "Resentful", "Numb"}
var A_VALUES = []string{"Self", "Partner", "Family", "Outsider", "Public"}
var X_VALUES = []string{"OverExplain", "Silent", "Genuine", "Indifferent"}
var Y_VALUES = []string{"Changed", "Resisted", "Indifferent", "NoResponse"}

// --- Behavior Patterns ---

var PATTERNS = []string{
	"MODE-DefensiveDefaulter",
	"MODE-ExternalTrustSpender",
	"MODE-InternalDestroyer",
	"MODE-Fluctuating",
	"MODE-StableDisciplined",
}

// --- Factor to Label Mapping (bilingual) ---

func getLabel(factor, value string) string {
	switch factor {
	case "P":
		switch value {
		case "HasPromise":
			return "7F-P-有承诺"
		case "NoPromise":
			return "7F-P-无承诺"
		}
	case "F":
		switch value {
		case "Fulfilled":
			return "7F-F-已兑现"
		case "Unfulfilled":
			return "7F-F-未兑现"
		case "Partial":
			return "7F-F-部分兑现"
		}
	case "E":
		switch value {
		case "Willing":
			return "7F-E-心甘情愿"
		case "Perfunctory":
			return "7F-E-敷衍"
		case "Resentful":
			return "7F-E-怨恨"
		case "Numb":
			return "7F-E-麻木"
		}
	case "A":
		switch value {
		case "Self":
			return "7F-A-自己"
		case "Partner":
			return "7F-A-伴侣"
		case "Family":
			return "7F-A-家庭"
		case "Outsider":
			return "7F-A-外人"
		case "Public":
			return "7F-A-公众"
		}
	case "X":
		switch value {
		case "OverExplain":
			return "7F-X-过度解释"
		case "Silent":
			return "7F-X-沉默"
		case "Genuine":
			return "7F-X-真诚"
		case "Indifferent":
			return "7F-X-冷漠"
		}
	case "Y":
		switch value {
		case "Changed":
			return "7F-Y-改正"
		case "Resisted":
			return "7F-Y-抗拒"
		case "Indifferent":
			return "7F-Y-无视"
		case "NoResponse":
			return "7F-Y-无响应"
		}
	}
	return ""
}

// AuditWrapper produces seven-factor behavioral audit metadata for wrapped payloads.
type AuditWrapper struct {
	UID string
}

// NewAuditWrapper creates a new AuditWrapper.
func NewAuditWrapper(uid string) *AuditWrapper {
	return &AuditWrapper{UID: uid}
}

// Wrap builds a complete audit record for the given payload.
func (w *AuditWrapper) Wrap(payload interface{}, taskType, persona string) map[string]interface{} {
	now := time.Now().In(time.FixedZone("CST", 8*3600))

	// Default signature (StableDisciplined baseline)
	signature := map[string]interface{}{
		"P": "HasPromise",
		"F": "Fulfilled",
		"T": 0.0,
		"E": "Willing",
		"C": 0,
		"R": 0,
		"A": "Self",
		"X": "Genuine",
		"Y": "NoResponse",
		"Z": 1.0,
	}

	pattern := w.classify(signature)
	labels := w.makeLabels(signature, pattern)
	color := w.determineColor(pattern, 0)

	// Payload hash (sha256 of JSON-serialized payload)
	payloadJSON, _ := json.Marshal(payload)
	hash := sha256.Sum256(payloadJSON)
	payloadHash := fmt.Sprintf("%02x%02x%02x%02x%02x%02x%02x%02x",
		hash[0], hash[1], hash[2], hash[3],
		hash[4], hash[5], hash[6], hash[7])

	return map[string]interface{}{
		"audit_version":      "v1.0",
		"uid":                "UID" + w.UID,
		"persona":            persona,
		"task_type":          taskType,
		"behavior_signature": signature,
		"behavior_pattern":   pattern,
		"behavior_labels":    labels,
		"color":              color,
		"timestamp":          now.Format(time.RFC3339),
		"payload_hash":       payloadHash,
	}
}

// classify determines the behavior pattern from a signature.
func (w *AuditWrapper) classify(sig map[string]interface{}) string {
	fVal, _ := sig["F"].(string)
	xVal, _ := sig["X"].(string)
	aVal, _ := sig["A"].(string)
	yVal, _ := sig["Y"].(string)
	zVal := 1.0
	if v, ok := sig["Z"].(float64); ok {
		zVal = v
	}

	if fVal == "Unfulfilled" && xVal == "OverExplain" {
		return "MODE-DefensiveDefaulter"
	}
	if fVal == "Fulfilled" && aVal == "Outsider" {
		return "MODE-ExternalTrustSpender"
	}
	if fVal == "Unfulfilled" && yVal == "Indifferent" {
		return "MODE-InternalDestroyer"
	}
	if zVal > 2.0 {
		return "MODE-Fluctuating"
	}
	return "MODE-StableDisciplined"
}

// makeLabels builds bilingual behavior labels from a signature and pattern.
func (w *AuditWrapper) makeLabels(sig map[string]interface{}, pattern string) []string {
	labels := make([]string, 0, 7)
	for _, factor := range []string{"P", "F", "E", "A", "X", "Y"} {
		if val, ok := sig[factor].(string); ok {
			if label := getLabel(factor, val); label != "" {
				labels = append(labels, label)
			}
		}
	}
	labels = append(labels, pattern)
	return labels
}

// determineColor returns the tri-color audit indicator.
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
