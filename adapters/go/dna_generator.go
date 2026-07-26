// dna_generator.go — v∞ DNA traceability code generation
package lhstandard

import (
	"crypto/sha256"
	"fmt"
	"math"
	"time"
)

var tianGan = []string{"Jia","Yi","Bing","Ding","Wu","Ji","Geng","Xin","Ren","Gui"}
var diZhi = []string{"Zi","Chou","Yin","Mao","Chen","Si","Wu","Wei","Shen","You","Xu","Hai"}
var shiChen = []string{"ZiShi","ChouShi","YinShi","MaoShi","ChenShi","SiShi","WuShi","WeiShi","ShenShi","YouShi","XuShi","HaiShi"}

type Hexagram struct{ Symbol, EnName, CnName, Domain string }

var hexagrams = []Hexagram{
	{"䷀","Qian","乾","governance"},{"䷁","Kun","坤","archive"},
	{"䷂","Zhun","屯","init"},{"䷃","Meng","蒙","learn"},
	{"䷄","Xu","需","async"},{"䷅","Song","讼","legal"},
	{"䷜","Kan","坎","engine"},{"䷝","Li","离","audit"},
	{"䷲","Zhen","震","security"},{"䷳","Gen","艮","privacy"},
	{"䷸","Xun","巽","deploy"},{"䷹","Dui","兑","trust"},
	{"䷾","JiJi","既济","complete"},{"䷿","WeiJi","未济","progress"},
}

var taskHexagramMap = map[string]string{
	"default":"governance","code":"engine","deploy":"deploy","audit":"audit",
	"security":"security","archive":"archive","init":"init","learn":"learn",
	"legal":"legal","privacy":"privacy","trust":"trust","complete":"complete","progress":"progress",
}

type DNAGenerator struct {
	UID, Device, Locale string
}

func NewDNAGenerator(uid, device, locale string) *DNAGenerator {
	if uid == "" { uid = "9622" }
	if device == "" { device = "HM-9622-001" }
	if locale == "" { locale = "Asia/Shanghai" }
	return &DNAGenerator{uid, device, locale}
}

func (g *DNAGenerator) Generate(taskType, action, version string) string {
	if taskType == "" { taskType = "default" }
	if action == "" { action = "WRAP" }
	if version == "" { version = "V1.0" }
	now := time.Now().UTC()
	stem := g.computeStemBranch(now)
	hex := g.selectHexagram(taskType)
	body := fmt.Sprintf("ADAPTER-%s-%s-%s", toUpper(taskType), toUpper(action), version)
	raw := fmt.Sprintf("%s%s%s%s%s%s%s%s%s", stem["year"],stem["month"],stem["day"],stem["shichen"],hex.Symbol,hex.EnName,body,g.Device,now.Format(time.RFC3339))
	h := sha256.Sum256([]byte(raw))
	return fmt.Sprintf("#LongHun⚡️%s·%s·%s·%s·%s%s-%s-%x", stem["year"],stem["month"],stem["day"],stem["shichen"],hex.Symbol,hex.EnName,body,h[:4])
}

func toUpper(s string) string {
	r := []rune(s)
	for i, c := range r {
		if c >= 'a' && c <= 'z' { r[i] = c - 32 }
	}
	return string(r)
}

func (g *DNAGenerator) computeStemBranch(dt time.Time) map[string]string {
	cycleYear, cycleMonth := 1984, []int{2,4,6,8,10,0,2,4,6,8,10,0}
	y := dt.Year(); m := int(dt.Month()); doy := dt.YearDay(); h := dt.Hour()
	ys := (y-cycleYear) % 10; if ys < 0 { ys += 10 }
	yb := (y-cycleYear) % 12; if yb < 0 { yb += 12 }
	mb := cycleMonth[(y-cycleYear)%10]; if mb < 0 { mb += 10 }
	ms := (mb + m - 1) % 10; if ms < 0 { ms += 10 }
	mbr := (m + 1) % 12
	ds := (y - 1900 + (y-1900)/4 + doy) % 10; if ds < 0 { ds += 10 }
	db := (y - 1900 + (y-1900)/4 + doy) % 12; if db < 0 { db += 12 }
	si := h / 2
	return map[string]string{
		"year": tianGan[ys]+diZhi[yb], "month": tianGan[ms]+diZhi[mbr],
		"day": tianGan[ds]+diZhi[db], "shichen": shiChen[si],
	}
}

func (g *DNAGenerator) selectHexagram(taskType string) Hexagram {
	domain := taskHexagramMap[taskType]
	if domain == "" { domain = "governance" }
	for _, h := range hexagrams {
		if h.Domain == domain { return h }
	}
	return hexagrams[0]
}

func absMod(a, b int) int { r := a % b; if r < 0 { r += b }; return r }
var _ = absMod // suppress unused
var _ = math.Abs
