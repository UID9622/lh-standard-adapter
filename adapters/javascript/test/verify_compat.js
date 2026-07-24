
// Byte-for-byte compatibility check with Python reference
// Python test: datetime(2026, 7, 24, 14, 30, 45, tzinfo=timezone(timedelta(hours=8)))
// That's UTC+8 at 2026-07-24T14:30:45+08:00

import crypto from 'node:crypto';

// Constants (same as Python)
const TIAN_GAN = ["Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"];
const DI_ZHI = ["Zi", "Chou", "Yin", "Mao", "Chen", "Si", "Wu", "Wei", "Shen", "You", "Xu", "Hai"];
const SHI_CHEN = ["ZiShi", "ChouShi", "YinShi", "MaoShi", "ChenShi", "SiShi",
                   "WuShi", "WeiShi", "ShenShi", "YouShi", "XuShi", "HaiShi"];
const CYCLE_YEAR = 1984;
const CYCLE_MONTH = [2, 4, 6, 8, 10, 0, 2, 4, 6, 8];
const HEXAGRAMS = [
    { symbol: "䷀", en_name: "Qian", cn_name: "乾", domain: "governance" },
    { symbol: "䷜", en_name: "Kan", cn_name: "坎", domain: "engine" },
];

function pymod(n, m) { return ((n % m) + m) % m; }

// The test date: 2026-07-24T14:30:45+08:00
// In UTC: 2026-07-24T06:30:45Z
const testDate = new Date('2026-07-24T06:30:45Z');

// Compute stem-branch
const ms = testDate.getTime() + 8 * 3600000;
const d = new Date(ms);
const year = d.getUTCFullYear();
const month = d.getUTCMonth() + 1;
const hour = d.getUTCHours();
const startOfYear = Date.UTC(year, 0, 1);
const yday = Math.floor((ms - startOfYear) / 86400000) + 1;

const yearStemIdx = pymod(year - CYCLE_YEAR, 10);
const yearBranchIdx = pymod(year - CYCLE_YEAR, 12);
const cycleIdx = pymod(year - CYCLE_YEAR, 10);
const monthStemIdx = pymod(CYCLE_MONTH[cycleIdx] + (month - 1), 10);
const monthBranchIdx = pymod(month + 1, 12);
const dayStemIdx = pymod(year - 1900 + Math.floor((year - 1900) / 4) + yday, 10);
const dayBranchIdx = pymod(year - 1900 + Math.floor((year - 1900) / 4) + yday, 12);
const shichenIdx = Math.floor(hour / 2);

const stem = {
    year: TIAN_GAN[yearStemIdx] + DI_ZHI[yearBranchIdx],
    month: TIAN_GAN[monthStemIdx] + DI_ZHI[monthBranchIdx],
    day: TIAN_GAN[dayStemIdx] + DI_ZHI[dayBranchIdx],
    shichen: SHI_CHEN[shichenIdx],
};

console.log('=== JS stem-branch computation ===');
console.log(`Year: ${year}, Month: ${month}, Hour: ${hour}, DayOfYear: ${yday}`);
console.log(`yearStemIdx=${yearStemIdx}, yearBranchIdx=${yearBranchIdx}`);
console.log(`monthStemIdx=${monthStemIdx}, monthBranchIdx=${monthBranchIdx}`);
console.log(`dayStemIdx=${dayStemIdx}, dayBranchIdx=${dayBranchIdx}`);
console.log(`shichenIdx=${shichenIdx}`);

const expected = { year: 'BingWu', month: 'BingShen', day: 'BingYin', shichen: 'WeiShi' };
const stemOk = JSON.stringify(stem) === JSON.stringify(expected);

// ISO format
const pad = (n) => String(n).padStart(2, '0');
const iso = `${d.getUTCFullYear()}-${pad(d.getUTCMonth()+1)}-${pad(d.getUTCDate())}T${pad(d.getUTCHours())}:${pad(d.getUTCMinutes())}:${pad(d.getUTCSeconds())}+08:00`;
const expectedIso = '2026-07-24T14:30:45+08:00';
const isoOk = iso === expectedIso;

// Full DNA generation
const hexagram = HEXAGRAMS[1]; // Kan (engine)
const body = 'ADAPTER-CODE-WRAP-V1.0';
const raw = `${stem.year}${stem.month}${stem.day}${stem.shichen}${hexagram.symbol}${hexagram.en_name}${body}HM-9622-001${iso}`;
const hash8 = crypto.createHash('sha256').update(raw, 'utf8').digest('hex').slice(0, 8);
const expectedHash8 = '249a11a2';
const hashOk = hash8 === expectedHash8;

const dna = `#LongHun⚡️${stem.year}·${stem.month}·${stem.day}·${stem.shichen}·${hexagram.symbol}${hexagram.en_name}-${body}-${hash8}`;
const expectedDna = '#LongHun⚡️BingWu·BingShen·BingYin·WeiShi·䷜Kan-ADAPTER-CODE-WRAP-V1.0-249a11a2';
const dnaOk = dna === expectedDna;

console.log(`\nStem: ${JSON.stringify(stem)}`);
console.log(`Expected: ${JSON.stringify(expected)}`);
console.log(`ISO: ${iso}`);
console.log(`Expected ISO: ${expectedIso}`);
console.log(`Raw: ${raw}`);
console.log(`Hash8: ${hash8}`);
console.log(`Expected Hash8: ${expectedHash8}`);
console.log(`DNA: ${dna}`);
console.log(`Expected DNA: ${expectedDna}`);

console.log(`\n=== Results ===`);
console.log(`Stem match: ${stemOk ? '✅' : '❌'}`);
console.log(`ISO match: ${isoOk ? '✅' : '❌'}`);
console.log(`Hash8 match: ${hashOk ? '✅' : '❌'}`);
console.log(`DNA match: ${dnaOk ? '✅' : '❌'}`);

if (!stemOk || !isoOk || !hashOk || !dnaOk) process.exit(1);
console.log('\n✅ BYTE-FOR-BYTE COMPATIBILITY VERIFIED');
