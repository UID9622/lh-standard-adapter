package lhstandardadapter

import (
	"crypto/sha256"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"regexp"
	"strings"
	"time"
)

var (
	TianGan  = []string{"Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"}
	DiZhi    = []string{"Zi", "Chou", "Yin", "Mao", "Chen", "Si", "Wu", "Wei", "Shen", "You", "Xu", "Hai"}
	ShiChen  = []string{"ZiShi", "ChouShi", "YinShi", "MaoShi", "ChenShi", "SiShi", "WuShi", "WeiShi", "ShenShi", "YouShi", "XuShi", "HaiShi"}
	DnaRegex = regexp.MustCompile(`^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([䷀-䷿][A-Za-z]+)-(.+)-([a-f0-9]{8})$`)
)

type Hexagram struct {
	Symbol string
	EnName string
	CnName string
	Domain string
}

var Hexagrams = []Hexagram{
	{Symbol: "䷀", EnName: "Qian", CnName: "乾", Domain: "governance"},
	{Symbol: "䷁", EnName: "Kun", CnName: "坤", Domain: "archive"},
	{Symbol: "䷜", EnName: "Kan", CnName: "坎", Domain: "engine"},
	{Symbol: "䷝", EnName: "Li", CnName: "离", Domain: "audit"},
	{Symbol: "䷲", EnName: "Zhen", CnName: "震", Domain: "security"},
	{Symbol: "䷳", EnName: "Gen", CnName: "艮", Domain: "privacy"},
	{Symbol: "䷸", EnName: "Xun", CnName: "巽", Domain: "deploy"},
	{Symbol: "䷹", EnName: "Dui", CnName: "兑", Domain: "trust"},
	{Symbol: "䷾", EnName: "JiJi", CnName: "既济", Domain: "complete"},
	{Symbol: "䷿", EnName: "WeiJi", CnName: "未济", Domain: "progress"},
}

type LongHunAdapter struct {
	Uid    string
	Device string
}

func New(uid, device string) *LongHunAdapter {
	if uid == "" {
		uid = "9622"
	}
	if device == "" {
		device = "HM-9622-001"
	}
	return &LongHunAdapter{Uid: uid, Device: device}
}

func (a *LongHunAdapter) GenerateDNA(taskType, action, version string) string {
	if action == "" {
		action = "WRAP"
	}
	if version == "" {
		version = "V1.0"
	}
	now := time.Now().UTC().Add(8 * time.Hour)
	yearStem := TianGan[(now.Year()-1984+1200)%10] + DiZhi[(now.Year()-1984+1200)%12]
	monthStem := TianGan[(now.Month()+1)%10] + DiZhi[(now.Month()+1)%12]
	dayStem := TianGan[(now.YearDay()+1)%10] + DiZhi[(now.YearDay()+1)%12]
	sc := ShiChen[(now.Hour()/2)%12]

	hex := Hexagrams[0]
	if taskType == "code" {
		hex = Hexagrams[2]
	} else if taskType == "audit" {
		hex = Hexagrams[3]
	}

	body := fmt.Sprintf("ADAPTER-%s-%s-%s", strings.ToUpper(taskType), strings.ToUpper(action), version)
	raw := fmt.Sprintf("%s%s%s%s%s%s%s%s%s", yearStem, monthStem, dayStem, sc, hex.Symbol, hex.EnName, body, a.Device, now.Format(time.RFC3339))
	hash := sha256.Sum256([]byte(raw))
	hash8 := hex.EncodeToString(hash[:])[:8]

	return fmt.Sprintf("#LongHun⚡️%s·%s·%s·%s·%s%s-%s-%s", yearStem, monthStem, dayStem, sc, hex.Symbol, hex.EnName, body, hash8)
}

func (a *LongHunAdapter) Wrap(payload interface{}, taskType, persona string) map[string]interface{} {
	dna := a.GenerateDNA(taskType, "WRAP", "V1.0")
	payloadBytes, _ := json.Marshal(payload)
	pHash := sha256.Sum256(payloadBytes)
	pHash16 := hex.EncodeToString(pHash[:])[:16]

	audit := map[string]interface{}{
		"audit_version": "v1.0",
		"uid":           fmt.Sprintf("UID%s", a.Uid),
		"persona":       persona,
		"task_type":     taskType,
		"behavior_signature": map[string]interface{}{
			"P": "HasPromise", "F": "Fulfilled", "T": 0.0, "E": "Willing",
			"C": 0, "R": 0, "A": "Self", "X": "Genuine", "Y": "NoResponse", "Z": 1.0,
		},
		"behavior_pattern": "MODE-StableDisciplined",
		"behavior_labels":  []string{"7F-P-有承诺", "7F-F-已兑现", "MODE-StableDisciplined"},
		"color":            "🟢",
		"timestamp":        time.Now().UTC().Format(time.RFC3339),
		"payload_hash":     pHash16,
	}

	return map[string]interface{}{
		"dna":     dna,
		"audit":   audit,
		"payload": payload,
		"meta": map[string]interface{}{
			"adapter_version": "1.0.0",
			"uid":             a.Uid,
			"format":          "longhun-v∞",
		},
	}
}

func (a *LongHunAdapter) Validate(wrapped map[string]interface{}) bool {
	dna, ok := wrapped["dna"].(string)
	if !ok || !DnaRegex.MatchString(dna) {
		return false
	}
	if _, ok := wrapped["audit"]; !ok {
		return false
	}
	return true
}
