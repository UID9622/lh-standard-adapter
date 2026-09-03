// audit_wrapper.go — seven-factor behavioral audit metadata
package lhstandard

import (
	"crypto/sha256"
	"encoding/json"
	"fmt"
	"sort"
	"time"
)

var labelMap = map[string]map[string]string{
	"P": {"HasPromise":"7F-P-有承诺","NoPromise":"7F-P-无承诺"},
	"F": {"Fulfilled":"7F-F-已兑现","Unfulfilled":"7F-F-未兑现","Partial":"7F-F-部分兑现"},
	"E": {"Willing":"7F-E-心甘情愿","Perfunctory":"7F-E-敷衍","Resentful":"7F-E-怨恨","Numb":"7F-E-麻木"},
	"A": {"Self":"7F-A-自己","Partner":"7F-A-伴侣","Family":"7F-A-家庭","Outsider":"7F-A-外人","Public":"7F-A-公众"},
	"X": {"OverExplain":"7F-X-过度解释","Silent":"7F-X-沉默","Genuine":"7F-X-真诚","Indifferent":"7F-X-冷漠"},
	"Y": {"Changed":"7F-Y-改正","Resisted":"7F-Y-抗拒","Indifferent":"7F-Y-无视","NoResponse":"7F-Y-无响应"},
}

type AuditWrapper struct{ UID string }
func NewAuditWrapper(uid string) *AuditWrapper {
	if uid == "" { uid = "9622" }
	return &AuditWrapper{uid}
}

func (w *AuditWrapper) Wrap(payload interface{}, taskType, persona string) map[string]interface{} {
	if taskType == "" { taskType = "default" }
	if persona == "" { persona = "P04" }
	sig := map[string]interface{}{"P":"HasPromise","F":"Fulfilled","T":0.0,"E":"Willing","C":0,"R":0,"A":"Self","X":"Genuine","Y":"NoResponse","Z":1.0}
	pattern := classify(sig)
	labels := makeLabels(sig, pattern)
	color := determineColor(pattern, int(sig["R"].(int)))
	pj, _ := json.Marshal(payload)
	ph := fmt.Sprintf("%x", sha256.Sum256(pj))[:16]
	return map[string]interface{}{
		"audit_version":"v1.0","uid":fmt.Sprintf("UID%s",w.UID),"persona":persona,"task_type":taskType,
		"behavior_signature":sig,"behavior_pattern":pattern,"behavior_labels":labels,
		"color":color,"timestamp":time.Now().UTC().Format(time.RFC3339),"payload_hash":ph,
	}
}

func classify(sig map[string]interface{}) string {
	f,_ := sig["F"].(string); x,_ := sig["X"].(string); a,_ := sig["A"].(string)
	y,_ := sig["Y"].(string); z,_ := sig["Z"].(float64)
	if f=="Unfulfilled" && x=="OverExplain" { return "MODE-DefensiveDefaulter" }
	if f=="Fulfilled" && a=="Outsider" { return "MODE-ExternalTrustSpender" }
	if f=="Unfulfilled" && y=="Indifferent" { return "MODE-InternalDestroyer" }
	if z > 2.0 { return "MODE-Fluctuating" }
	return "MODE-StableDisciplined"
}

func makeLabels(sig map[string]interface{}, pattern string) []string {
	var labels []string
	for _, factor := range []string{"P","F","E","A","X","Y"} {
		if v, ok := sig[factor].(string); ok {
			if lm, ok2 := labelMap[factor]; ok2 {
				if lb, ok3 := lm[v]; ok3 { labels = append(labels, lb) }
			}
		}
	}
	labels = append(labels, pattern)
	return labels
}

func determineColor(pattern string, repeat int) string {
	if pattern == "MODE-InternalDestroyer" { return "🔴" }
	if pattern == "MODE-Fluctuating" && repeat > 3 { return "🟡" }
	if pattern == "MODE-DefensiveDefaulter" && repeat > 2 { return "🟡" }
	return "🟢"
}
var _ = sort.Ints
