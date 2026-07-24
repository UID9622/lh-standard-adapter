package adapter

import (
	"crypto/sha256"
	"fmt"
	"math"
	"strings"
	"time"
)

var TianGan = []string{"Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"}
var DiZhi = []string{"Zi", "Chou", "Yin", "Mao", "Chen", "Si", "Wu", "Wei", "Shen", "You", "Xu", "Hai"}
var ShiChen = []string{"ZiShi", "ChouShi", "YinShi", "MaoShi", "ChenShi", "SiShi", "WuShi", "WeiShi", "ShenShi", "YouShi", "XuShi", "HaiShi"}

type Hexagram struct {
	Symbol string
	EnName string
	CnName string
	Domain string
}

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

var TaskHexagramMap = map[string]string{
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

type DNAGenerator struct {
	UID        string
	Device     string
	CycleYear  int
	CycleMonth []int
}

func NewDNAGenerator(uid, device string) *DNAGenerator {
	if uid == "" {
		uid = "9622"
	}
	if device == "" {
		device = "HM-9622-001"
	}
	return &DNAGenerator{
		UID:        uid,
		Device:     device,
		CycleYear:  1984,
		CycleMonth: []int{2, 4, 6, 8, 10, 0, 2, 4, 6, 8, 10, 0},
	}
}

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

	loc := time.FixedZone("Asia/Shanghai", 8*3600)
	now := time.Now().In(loc)

	stemYear, stemMonth, stemDay, shichen := g.computeStemBranch(now)
	hexagram := g.selectHexagram(taskType)
	body := fmt.Sprintf("ADAPTER-%s-%s-%s", strings.ToUpper(taskType), strings.ToUpper(action), version)

	raw := fmt.Sprintf("%s%s%s%s%s%s%s%s%s", stemYear, stemMonth, stemDay, shichen, hexagram.Symbol, hexagram.EnName, body, g.Device, now.Format(time.RFC3339))
	hash := sha256.Sum256([]byte(raw))
	hash8 := fmt.Sprintf("%x", hash[:4])

	return fmt.Sprintf("#LongHun⚡️%s·%s·%s·%s·%s%s-%s-%s", stemYear, stemMonth, stemDay, shichen, hexagram.Symbol, hexagram.EnName, body, hash8)
}

func (g *DNAGenerator) computeStemBranch(dt time.Time) (string, string, string, string) {
	year := dt.Year()
	yearStemIdx := int(math.Abs(float64((year - g.CycleYear) % 10)))
	yearBranchIdx := int(math.Abs(float64((year - g.CycleYear) % 12)))

	month := int(dt.Month()) - 1
	monthStemIdx := int(math.Abs(float64((g.CycleMonth[yearStemIdx] + month) % 10)))
	monthBranchIdx := int(math.Abs(float64((month + 2) % 12)))

	dayOfYear := dt.YearDay()
	dayStemIdx := int(math.Abs(float64((year - 1900 + (year-1900)/4 + dayOfYear) % 10)))
	dayBranchIdx := int(math.Abs(float64((year - 1900 + (year-1900)/4 + dayOfYear) % 12)))

	shichenIdx := dt.Hour() / 2

	stemYear := TianGan[yearStemIdx] + DiZhi[yearBranchIdx]
	stemMonth := TianGan[monthStemIdx] + DiZhi[monthBranchIdx]
	stemDay := TianGan[dayStemIdx] + DiZhi[dayBranchIdx]
	shichen := ShiChen[shichenIdx%12]

	return stemYear, stemMonth, stemDay, shichen
}

func (g *DNAGenerator) selectHexagram(taskType string) Hexagram {
	domain, ok := TaskHexagramMap[taskType]
	if !ok {
		domain = "governance"
	}
	for _, h := range Hexagrams {
		if h.Domain == domain {
			return h
		}
	}
	return Hexagrams[0]
}
