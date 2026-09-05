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
	TianGan     = []string{"Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"}
	DiZhi       = []string{"Zi", "Chou", "Yin", "Mao", "Chen", "Si", "Wu", "Wei", "Shen", "You", "Xu", "Hai"}
	ShiChen     = []string{"ZiShi", "ChouShi", "YinShi", "MaoShi", "ChenShi", "SiShi", "WuShi", "WeiShi", "ShenShi", "YouShi", "XuShi", "HaiShi"}
	CYCLE_YEAR  = 1984 // JiaZi reference year (aligned with reference implementation)
	CYCLE_MONTH = []int{2, 4, 6, 8, 10, 0, 2, 4, 6, 8, 10, 0}

	DnaRegex = regexp.MustCompile(`^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([䷀-䷿][A-Za-z]+)-(.+)-([a-f0-9]{8})$`)
)

type Hexagram struct {
	Symbol string
	EnName string
	CnName string
	Domain string
}

// Full 14-hexagram reference table, matching the Python reference implementation.
var Hexagrams = []Hexagram{
	{Symbol: "䷀", EnName: "Qian", CnName: "乾", Domain: "governance"},
	{Symbol: "䷁", EnName: "Kun", CnName: "坤", Domain: "archive"},
	{Symbol: "䷂", EnName: "Zhun", CnName: "屯", Domain: "init"},
	{Symbol: "䷃", EnName: "Meng", CnName: "蒙", Domain: "learn"},
	{Symbol: "䷄", EnName: "Xu", CnName: "需", Domain: "async"},
	{Symbol: "䷅", EnName: "Song", CnName: "讼", Domain: "legal"},
	{Symbol: "䷜", EnName: "Kan", CnName: "坎", Domain: "engine"},
	{Symbol: "䷝", EnName: "Li", CnName: "离", Domain: "audit"},
	{Symbol: "䷲", EnName: "Zhen", CnName: "震", Domain: "security"},
	{Symbol: "䷳", EnName: "Gen", CnName: "艮", Domain: "privacy"},
	{Symbol: "䷸", EnName: "Xun", CnName: "巽", Domain: "deploy"},
	{Symbol: "䷹", EnName: "Dui", CnName: "兑", Domain: "trust"},
	{Symbol: "䷾", EnName: "JiJi", CnName: "既济", Domain: "complete"},
	{Symbol: "䷿", EnName: "WeiJi", CnName: "未济", Domain: "progress"},
}

// taskDomain maps task types to hexagram domains (reference table).
var taskDomain = map[string]string{
	"default":  "governance",
	"code":     "engine",
	"deploy":   "deploy",
	"audit":    "audit",
	"security": "security",
	"archive":  "archive",
	"init":     "init",
	"learn":    "learn",
	"legal":    "legal",
	"privacy":  "privacy",
	"trust":    "trust",
	"complete": "complete",
	"progress": "progress",
}

func selectHexagram(taskType string) Hexagram {
	domain := taskDomain[taskType]
	if domain == "" {
		domain = "governance"
	}
	for _, h := range Hexagrams {
		if h.Domain == domain {
			return h
		}
	}
	return Hexagrams[0]
}

// computeStemBranch computes the four-pillar Ganzhi (stem-branch) under Asia/Shanghai
// time, mirroring the Python reference algorithm (CYCLE_YEAR 1984, month-stem table,
// Julian-day day pillar, shichen = hour/2).
func computeStemBranch(now time.Time) (string, string, string, string) {
	base := now.Year() - CYCLE_YEAR
	yearStem := TianGan[((base%10)+10)%10] + DiZhi[((base%12)+12)%12]

	monthStemIdx := (CYCLE_MONTH[((base%10)+10)%10] + (int(now.Month()) - 1)) % 10
	monthBranchIdx := (int(now.Month()) + 1) % 12
	monthPillar := TianGan[monthStemIdx] + DiZhi[monthBranchIdx]

	julian := now.Year() - 1900 + (now.Year()-1900)/4 + now.YearDay()
	dayPillar := TianGan[julian%10] + DiZhi[julian%12]

	shichen := ShiChen[((now.Hour()+1)/2)%12]
	return yearStem, monthPillar, dayPillar, shichen
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
	if taskType == "" {
		taskType = "default"
	}
	if action == "" {
		action = "WRAP"
	}
	if version == "" {
		version = "V1.0"
	}
	now := time.Now().UTC().Add(8 * time.Hour) // Asia/Shanghai wall-clock time
	yearStem, monthPillar, dayPillar, sc := computeStemBranch(now)
	hex := selectHexagram(taskType)

	body := fmt.Sprintf("ADAPTER-%s-%s-%s", strings.ToUpper(taskType), strings.ToUpper(action), strings.ToUpper(version))
	raw := fmt.Sprintf("%s%s%s%s%s%s%s%s%s", yearStem, monthPillar, dayPillar, sc, hex.Symbol, hex.EnName, body, a.Device, now.Format(time.RFC3339))
	hash := sha256.Sum256([]byte(raw))
	hash8 := hex.EncodeToString(hash[:])[:8]

	return fmt.Sprintf("#LongHun⚡️%s·%s·%s·%s·%s%s-%s-%s", yearStem, monthPillar, dayPillar, sc, hex.Symbol, hex.EnName, body, hash8)
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
