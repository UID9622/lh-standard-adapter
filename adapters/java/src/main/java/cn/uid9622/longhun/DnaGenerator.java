package cn.uid9622.longhun;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DNA Generator — produces LongHun v∞ DNA traceability codes.
 *
 * <p>The DNA format is:<br>
 * {@code #LongHun⚡️{YearStem}·{MonthStem}·{DayStem}·{ShiChen}·{HexagramSymbol}{HexagramName}-{Body}-{hash8}}
 *
 * <p>Uses the Gan-Zhi (stem-branch) calendar with reference year 1984.
 * DNA is byte-for-byte compatible with the Python reference implementation.
 */
public final class DnaGenerator {

    // ── Heavenly Stems (天干) ──
    public static final String[] TIAN_GAN = {
            "Jia", "Yi", "Bing", "Ding", "Wu",
            "Ji", "Geng", "Xin", "Ren", "Gui"
    };

    // ── Earthly Branches (地支) ──
    public static final String[] DI_ZHI = {
            "Zi", "Chou", "Yin", "Mao", "Chen", "Si",
            "Wu", "Wei", "Shen", "You", "Xu", "Hai"
    };

    // ── Time Periods (时辰) ──
    public static final String[] SHI_CHEN = {
            "ZiShi", "ChouShi", "YinShi", "MaoShi", "ChenShi", "SiShi",
            "WuShi", "WeiShi", "ShenShi", "YouShi", "XuShi", "HaiShi"
    };

    // ── I Ching Hexagrams (14) ──
    public static final Hexagram[] HEXAGRAMS = {
            new Hexagram("䷀", "Qian", "乾", "governance"),
            new Hexagram("䷁", "Kun", "坤", "archive"),
            new Hexagram("䷂", "Zhun", "屯", "init"),
            new Hexagram("䷃", "Meng", "蒙", "learn"),
            new Hexagram("䷄", "Xu", "需", "async"),
            new Hexagram("䷅", "Song", "讼", "legal"),
            new Hexagram("䷜", "Kan", "坎", "engine"),
            new Hexagram("䷝", "Li", "离", "audit"),
            new Hexagram("䷲", "Zhen", "震", "security"),
            new Hexagram("䷳", "Gen", "艮", "privacy"),
            new Hexagram("䷸", "Xun", "巽", "deploy"),
            new Hexagram("䷹", "Dui", "兑", "trust"),
            new Hexagram("䷾", "JiJi", "既济", "complete"),
            new Hexagram("䷿", "WeiJi", "未济", "progress"),
    };

    // ── Task-to-hexagram domain mapping ──
    private static final Map<String, String> TASK_DOMAIN_MAP = new LinkedHashMap<>();

    static {
        TASK_DOMAIN_MAP.put("code", "engine");
        TASK_DOMAIN_MAP.put("deploy", "deploy");
        TASK_DOMAIN_MAP.put("audit", "audit");
        TASK_DOMAIN_MAP.put("security", "security");
        TASK_DOMAIN_MAP.put("archive", "archive");
        TASK_DOMAIN_MAP.put("init", "init");
        TASK_DOMAIN_MAP.put("learn", "learn");
        TASK_DOMAIN_MAP.put("legal", "legal");
        TASK_DOMAIN_MAP.put("privacy", "privacy");
        TASK_DOMAIN_MAP.put("trust", "trust");
        TASK_DOMAIN_MAP.put("complete", "complete");
        TASK_DOMAIN_MAP.put("progress", "progress");
    }

    /**
     * Immutable hexagram record.
     */
    public static final class Hexagram {
        public final String symbol;
        public final String enName;
        public final String cnName;
        public final String domain;

        public Hexagram(String symbol, String enName, String cnName, String domain) {
            this.symbol = symbol;
            this.enName = enName;
            this.cnName = cnName;
            this.domain = domain;
        }
    }

    /**
     * Stem-Branch (干支) computation result.
     */
    public static final class StemBranch {
        public final String year;      // e.g. "BingWu"
        public final String month;     // e.g. "GuiWei"
        public final String day;       // e.g. "JiaZi"
        public final String shichen;   // e.g. "WeiShi"

        StemBranch(String year, String month, String day, String shichen) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.shichen = shichen;
        }
    }

    // ── GanZhi constants ──
    private static final int CYCLE_YEAR = 1984;
    private static final int[] CYCLE_MONTH = {2, 4, 6, 8, 10, 0, 2, 4, 6, 8, 10, 0};

    // ── Instance fields ──
    private final String uid;
    private final String device;
    private final ZoneId zone;

    /**
     * Create a DNAGenerator.
     *
     * @param uid    user identifier (e.g. "9622")
     * @param device device identifier (e.g. "HM-9622-001")
     * @param locale timezone string (e.g. "Asia/Shanghai")
     */
    public DnaGenerator(String uid, String device, String locale) {
        this.uid = uid;
        this.device = device;
        this.zone = ZoneId.of(locale);
    }

    // ── Public API ──

    /**
     * Generate a full DNA traceability code.
     *
     * @param taskType task domain (code, deploy, audit, …)
     * @param action   action verb (WRAP, GENERATE, …)
     * @param version  version string; defaults to "V1.0" if null
     * @return DNA string like {@code #LongHun⚡️BingWu·GuiWei·JiaZi·WeiShi·䷾JiJi-ADAPTER-CODE-WRAP-V1.0-a3f8c1d9}
     */
    public String generate(String taskType, String action, String version) {
        ZonedDateTime now = ZonedDateTime.now(zone);

        StemBranch stem = computeStemBranch(now);
        Hexagram hexagram = selectHexagram(taskType);
        String ver = (version != null && !version.isEmpty()) ? version : "V1.0";

        String body = "ADAPTER-" + taskType.toUpperCase() + "-" + action.toUpperCase() + "-" + ver;

        // Build the raw input for SHA-256
        String raw = stem.year + stem.month + stem.day + stem.shichen
                + hexagram.symbol + hexagram.enName + body
                + device + now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String hash8 = sha256First4Bytes(raw);

        return "#LongHun⚡️"
                + stem.year + "·" + stem.month + "·" + stem.day + "·" + stem.shichen
                + "·" + hexagram.symbol + hexagram.enName
                + "-" + body + "-" + hash8;
    }

    /**
     * Convenience method — default device and locale.
     */
    public static String generateDna(String taskType, String action, String version) {
        DnaGenerator gen = new DnaGenerator("9622", "HM-9622-001", "Asia/Shanghai");
        return gen.generate(taskType, action, version);
    }

    // ── GanZhi computation ──

    /**
     * Compute the four-component stem-branch for a given datetime.
     *
     * <p>Reference year: 1984 (Jia-Zi year). The algorithm matches the
     * Python and Rust reference implementations exactly.
     */
    StemBranch computeStemBranch(ZonedDateTime dt) {
        int year = dt.getYear();
        int month = dt.getMonthValue();
        int dayOfYear = dt.getDayOfYear();
        int hour = dt.getHour();

        // ── Year stem-branch ──
        int yearStemIdx = Math.floorMod(year - CYCLE_YEAR, 10);
        int yearBranchIdx = Math.floorMod(year - CYCLE_YEAR, 12);

        // ── Month stem-branch ──
        int cycleIdx = Math.floorMod(year - CYCLE_YEAR, 10);
        int monthStemBase = CYCLE_MONTH[cycleIdx];
        int monthStemIdx;
        if (monthStemBase >= 0) {
            monthStemIdx = (monthStemBase + (month - 1)) % 10;
        } else {
            monthStemIdx = (month * 2) % 10;
        }
        int monthBranchIdx = (month + 1) % 12;

        // ── Day stem-branch ──
        int dayRef = year - 1900;
        int dayAccum = dayRef + dayRef / 4 + dayOfYear;
        int dayStemIdx = Math.floorMod(dayAccum, 10);
        int dayBranchIdx = Math.floorMod(dayAccum, 12);

        // ── ShiChen ──
        int shichenIdx = Math.min(hour / 2, 11);

        return new StemBranch(
                TIAN_GAN[yearStemIdx] + DI_ZHI[yearBranchIdx],
                TIAN_GAN[monthStemIdx] + DI_ZHI[monthBranchIdx],
                TIAN_GAN[dayStemIdx] + DI_ZHI[dayBranchIdx],
                SHI_CHEN[shichenIdx]
        );
    }

    // ── Hexagram selection ──

    Hexagram selectHexagram(String taskType) {
        String domain = TASK_DOMAIN_MAP.getOrDefault(taskType.toLowerCase(), "governance");
        for (Hexagram h : HEXAGRAMS) {
            if (h.domain.equals(domain)) {
                return h;
            }
        }
        return HEXAGRAMS[0]; // fallback: ䷀ Qian
    }

    // ── SHA-256 helpers ──

    static String sha256First4Bytes(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return String.format("%02x%02x%02x%02x",
                    digest[0] & 0xFF, digest[1] & 0xFF,
                    digest[2] & 0xFF, digest[3] & 0xFF);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    static String sha256First8Bytes(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return String.format("%02x%02x%02x%02x%02x%02x%02x%02x",
                    digest[0] & 0xFF, digest[1] & 0xFF,
                    digest[2] & 0xFF, digest[3] & 0xFF,
                    digest[4] & 0xFF, digest[5] & 0xFF,
                    digest[6] & 0xFF, digest[7] & 0xFF);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    // ── Utility ──

    public static String nowIso() {
        return ZonedDateTime.now(ZoneId.of("Asia/Shanghai"))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
