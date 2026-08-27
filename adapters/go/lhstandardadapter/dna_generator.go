package lhstandardadapter

import (
	"crypto/sha256"
	"encoding/hex"
	"fmt"
	"time"
)

// Heavenly Stems (天干)
var tianGan = []string{"Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"}

// Earthly Branches (地支)
var diZhi = []string{"Zi", "Chou", "Yin", "Mao", "Chen", "Si",
	"Wu", "Wei", "Shen", "You", "Xu", "Hai"}

// Shi Chen (时辰) — 12 two-hour periods
var shiChen = []string{"ZiShi", "ChouShi", "YinShi", "MaoShi", "ChenShi", "SiShi",
	"WuShi", "WeiShi", "ShenShi", "YouShi", "XuShi", "HaiShi"}

// Hexagram represents an I Ching hexagram with metadata.
type Hexagram struct {
	Symbol string `json:"symbol"`
	EnName string `json:"en_name"`
	CnName string `json:"cn_name"`
	Domain string `json:"domain"`
}

var hexagrams = []Hexagram{
	{"䷀", "Qian", "乾", "governance"},
	{"䷁", "Kun", "坤", "archive"},
	{"䷂", "Zhun", "屯", "init"},
	{"䷃", "Meng", "蒙", "learn"},
	{"䷄", "Xu", "需", "async"},
	{"䷅", "Song", "讼", "legal"},
	{"䷜", "Kan", "坎", "engine"},
	{"䷝", "Li", "离", "audit"},
	{"䷲", "Zhen", "震", "security"},
	{"䷳", "Gen", "艮", "privacy"},
	{"䷸", "Xun", "巽", "deploy"},
	{"䷹", "Dui", "兑", "trust"},
	{"䷾", "JiJi", "既济", "complete"},
	{"䷿", "WeiJi", "未济", "progress"},
}

// Task-to-hexagram domain mapping
var taskHexagramMap = map[string]string{
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

// StemBranch holds the four pillars computed from a timestamp.
type StemBranch struct {
	Year    string `json:"year"`
	Month   string `json:"month"`
	Day     string `json:"day"`
	ShiChen string `json:"shichen"`
}

const (
	cycleYear  = 1984 // JiaZi year reference
	cstOffset  = 8    // UTC+8 for Asia/Shanghai
)

// cycleMonth maps year-stem-index to month stem offsets.
var cycleMonth = []int{2, 4, 6, 8, 10, 0, 2, 4, 6, 8}

// DNAGenerator generates v∞ format DNA traceability codes.
//
// Format: #LongHun⚡️{StemBranch}·{Hexagram}-{ModulePath}-{Hash8}
type DNAGenerator struct {
	UID    string
	Device string
	Locale string
}

// NewDNAGenerator creates a DNAGenerator with defaults.
func NewDNAGenerator(uid, device string) *DNAGenerator {
	return &DNAGenerator{UID: uid, Device: device, Locale: "Asia/Shanghai"}
}

// Generate produces a full DNA traceability string.
func (g *DNAGenerator) Generate(taskType, action, version string) string {
	if taskType == "" {
		taskType = "default"
	}
	if action == "" {
		action = "WRAP"
	}
	if version == "" {
		version = "V1.0"
	}

	now := time.Now().UTC().Add(cstOffset * time.Hour)
	sb := g.computeStemBranch(now)
	hg := g.selectHexagram(taskType)

	body := fmt.Sprintf("ADAPTER-%s-%s-%s", toUpper(taskType), toUpper(action), version)

	raw := fmt.Sprintf("%s%s%s%s%s%s%s%s%s",
		sb.Year, sb.Month, sb.Day, sb.ShiChen,
		hg.Symbol, hg.EnName,
		body,
		g.Device,
		now.Format(time.RFC3339Nano),
	)

	h := sha256.Sum256([]byte(raw))
	hash8 := hex.EncodeToString(h[:])[:8]

	return fmt.Sprintf("#LongHun⚡️%s·%s·%s·%s·%s%s-%s-%s",
		sb.Year, sb.Month, sb.Day, sb.ShiChen,
		hg.Symbol, hg.EnName,
		body, hash8)
}

// computeStemBranch computes Heavenly Stem + Earthly Branch for a time.
func (g *DNAGenerator) computeStemBranch(t time.Time) StemBranch {
	year := t.Year()

	yearStemIdx := mod(year-cycleYear, 10)
	yearBranchIdx := mod(year-cycleYear, 12)

	yearOffset := mod(year-cycleYear, 10)
	monthStemIdx := mod(cycleMonth[yearOffset]+int(t.Month())-1, 10)
	monthBranchIdx := mod(int(t.Month())+1, 12)

	dayOfYear := t.YearDay()
	dayBase := (year - 1900) + (year-1900)/4 + dayOfYear
	dayStemIdx := mod(dayBase, 10)
	dayBranchIdx := mod(dayBase, 12)

	shichenIdx := t.Hour() / 2

	return StemBranch{
		Year:    tianGan[yearStemIdx] + diZhi[yearBranchIdx],
		Month:   tianGan[monthStemIdx] + diZhi[monthBranchIdx],
		Day:     tianGan[dayStemIdx] + diZhi[dayBranchIdx],
		ShiChen: shiChen[shichenIdx],
	}
}

// selectHexagram selects an I Ching hexagram based on task type.
func (g *DNAGenerator) selectHexagram(taskType string) Hexagram {
	domain, ok := taskHexagramMap[taskType]
	if !ok {
		domain = "governance"
	}
	for _, h := range hexagrams {
		if h.Domain == domain {
			return h
		}
	}
	return hexagrams[0] // Default: Qian
}

// mod returns the non-negative remainder of a % b.
func mod(a, b int) int {
	m := a % b
	if m < 0 {
		m += b
	}
	return m
}

// toUpper returns the uppercase ASCII version of s.
func toUpper(s string) string {
	b := []byte(s)
	for i := range b {
		if b[i] >= 'a' && b[i] <= 'z' {
			b[i] -= 32
		}
	}
	return string(b)
}
