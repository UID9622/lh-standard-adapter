package lh_adapter

import (
	"crypto/sha256"
	"fmt"
	"time"
)

// DNAGenerator generates LongHun DNA traceability strings.
// The GanZhi algorithm is byte-for-byte compatible with the Python reference.
type DNAGenerator struct {
	UID    string
	Device string
	Locale string
}

// NewDNAGenerator creates a new DNAGenerator.
func NewDNAGenerator(uid, device, locale string) *DNAGenerator {
	return &DNAGenerator{
		UID:    uid,
		Device: device,
		Locale: locale,
	}
}

// Generate produces a DNA traceability string for the given task type, action, and version.
// Format: #LongHun⚡️{Year}·{Month}·{Day}·{ShiChen}·{Hexagram}{Name}-ADAPTER-{TASK}-{ACTION}-{VERSION}-{hash8}
func (g *DNAGenerator) Generate(taskType, action, version string) string {
	if version == "" {
		version = "V1.0"
	}

	now := time.Now().In(time.FixedZone("CST", 8*3600))
	stem := g.computeStemBranch(now)
	hexagram := g.selectHexagram(taskType)

	body := fmt.Sprintf("ADAPTER-%s-%s-%s", toUpper(taskType), toUpper(action), version)

	raw := fmt.Sprintf("%s%s%s%s%s%s%s%s%s",
		stem.Year, stem.Month, stem.Day, stem.Shichen,
		hexagram.Symbol, hexagram.ENName, body, g.Device, now.Format(time.RFC3339))

	hash := sha256.Sum256([]byte(raw))
	hash8 := fmt.Sprintf("%02x%02x%02x%02x", hash[0], hash[1], hash[2], hash[3])

	return fmt.Sprintf("#LongHun⚡️%s·%s·%s·%s·%s%s-%s-%s",
		stem.Year, stem.Month, stem.Day, stem.Shichen,
		hexagram.Symbol, hexagram.ENName, body, hash8)
}

// computeStemBranch computes the four GanZhi fields (Year, Month, Day, ShiChen)
// using the identical algorithm as the Python reference implementation.
func (g *DNAGenerator) computeStemBranch(dt time.Time) StemBranch {
	year := dt.Year()
	month := int(dt.Month())
	yday := dt.YearDay()

	// Year Stem + Branch
	yearStemIdx := ((year - CYCLE_YEAR) % 10) + 10
	yearStemIdx = yearStemIdx % 10
	yearBranchIdx := ((year - CYCLE_YEAR) % 12) + 12
	yearBranchIdx = yearBranchIdx % 12

	// Month Stem + Branch
	cycleIdx := ((year - CYCLE_YEAR) % 10) + 10
	cycleIdx = cycleIdx % 10
	monthStemBase := CYCLE_MONTH[cycleIdx]
	var monthStemIdx int
	if monthStemBase >= 0 {
		monthStemIdx = (monthStemBase + (month - 1)) % 10
	} else {
		monthStemIdx = (month * 2) % 10
	}
	monthBranchIdx := (month + 1) % 12

	// Day Stem + Branch
	yearOffset := year - 1900
	dayStemIdx := ((yearOffset + yearOffset/4 + yday) % 10) + 10
	dayStemIdx = dayStemIdx % 10
	dayBranchIdx := ((yearOffset + yearOffset/4 + yday) % 12) + 12
	dayBranchIdx = dayBranchIdx % 12

	// ShiChen
	shichenIdx := dt.Hour() / 2
	if shichenIdx > 11 {
		shichenIdx = 11
	}

	return StemBranch{
		Year:    fmt.Sprintf("%s%s", TIAN_GAN[yearStemIdx], DI_ZHI[yearBranchIdx]),
		Month:   fmt.Sprintf("%s%s", TIAN_GAN[monthStemIdx], DI_ZHI[monthBranchIdx]),
		Day:     fmt.Sprintf("%s%s", TIAN_GAN[dayStemIdx], DI_ZHI[dayBranchIdx]),
		Shichen: SHI_CHEN[shichenIdx],
	}
}

// selectHexagram maps a task type to its corresponding hexagram.
func (g *DNAGenerator) selectHexagram(taskType string) Hexagram {
	domain, ok := TASK_HEXAGRAM_MAP[taskType]
	if !ok {
		domain = "governance"
	}
	for _, h := range HEXAGRAMS {
		if h.Domain == domain {
			return h
		}
	}
	return HEXAGRAMS[0]
}

// toUpper returns the uppercase version of s (simple ASCII).
func toUpper(s string) string {
	b := make([]byte, len(s))
	for i := 0; i < len(s); i++ {
		c := s[i]
		if c >= 'a' && c <= 'z' {
			b[i] = c - 32
		} else {
			b[i] = c
		}
	}
	return string(b)
}
