package lh_adapter

// ╔══════════════════════════════════════════════════════════════╗
// ║  DNA Traceability Format v∞ — Graph Structure Constants     ║
// ║  Author: LongHun Core · UID9622 · 龍芯北辰                  ║
// ║  License: CC BY-NC-SA 4.0                                   ║
// ╚══════════════════════════════════════════════════════════════╝

// --- Heavenly Stems and Earthly Branches ---

var TIAN_GAN = []string{
	"Jia", "Yi", "Bing", "Ding", "Wu",
	"Ji", "Geng", "Xin", "Ren", "Gui",
}

var DI_ZHI = []string{
	"Zi", "Chou", "Yin", "Mao", "Chen", "Si",
	"Wu", "Wei", "Shen", "You", "Xu", "Hai",
}

var SHI_CHEN = []string{
	"ZiShi", "ChouShi", "YinShi", "MaoShi", "ChenShi", "SiShi",
	"WuShi", "WeiShi", "ShenShi", "YouShi", "XuShi", "HaiShi",
}

// --- I Ching Hexagrams ---

type Hexagram struct {
	Symbol string `json:"symbol"`
	ENName string `json:"en_name"`
	CNName string `json:"cn_name"`
	Domain string `json:"domain"`
}

var HEXAGRAMS = []Hexagram{
	{Symbol: "䷀", ENName: "Qian", CNName: "乾", Domain: "governance"},
	{Symbol: "䷁", ENName: "Kun", CNName: "坤", Domain: "archive"},
	{Symbol: "䷂", ENName: "Zhun", CNName: "屯", Domain: "init"},
	{Symbol: "䷃", ENName: "Meng", CNName: "蒙", Domain: "learn"},
	{Symbol: "䷄", ENName: "Xu", CNName: "需", Domain: "async"},
	{Symbol: "䷅", ENName: "Song", CNName: "讼", Domain: "legal"},
	{Symbol: "䷜", ENName: "Kan", CNName: "坎", Domain: "engine"},
	{Symbol: "䷝", ENName: "Li", CNName: "离", Domain: "audit"},
	{Symbol: "䷲", ENName: "Zhen", CNName: "震", Domain: "security"},
	{Symbol: "䷳", ENName: "Gen", CNName: "艮", Domain: "privacy"},
	{Symbol: "䷸", ENName: "Xun", CNName: "巽", Domain: "deploy"},
	{Symbol: "䷹", ENName: "Dui", CNName: "兑", Domain: "trust"},
	{Symbol: "䷾", ENName: "JiJi", CNName: "既济", Domain: "complete"},
	{Symbol: "䷿", ENName: "WeiJi", CNName: "未济", Domain: "progress"},
}

// --- Task-to-hexagram domain mapping ---

var TASK_HEXAGRAM_MAP = map[string]string{
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

// --- Cycle Constants (GanZhi algorithm) ---

const CYCLE_YEAR = 1984

var CYCLE_MONTH = [12]int{2, 4, 6, 8, 10, 0, 2, 4, 6, 8, 10, 0}

// --- Version ---

const VERSION = "1.0.0"

const AUTHOR = "LongHun Core · UID9622 · 龍芯北辰"

const LICENSE = "CC BY-NC-SA 4.0"

const DNA = "#LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-ADAPTER-v1.0.0-4f7a3b1c"

// --- DNA Regex ---

const DNA_REGEX = `^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([` +
	"\u4DC0" + `-` + "\u4DFF" + `][A-Za-z]+)-(.+)-([a-f0-9]{8})$`

// stemBranch holds the four GanZhi fields.
type StemBranch struct {
	Year    string
	Month   string
	Day     string
	Shichen string
}
