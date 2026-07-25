package cn.uid9622.longhun;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * DNA Generator for v∞ format traceability code generation.
 * <p>
 * Format: #LongHun⚡️{StemBranch}·{Hexagram}-{ModulePath}-{Hash8}
 */
public class DNAGenerator {

    // --- Heavenly Stems and Earthly Branches ---

    private static final String[] TIAN_GAN = {
            "Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"
    };

    private static final String[] DI_ZHI = {
            "Zi", "Chou", "Yin", "Mao", "Chen", "Si",
            "Wu", "Wei", "Shen", "You", "Xu", "Hai"
    };

    private static final String[] SHI_CHEN = {
            "ZiShi", "ChouShi", "YinShi", "MaoShi", "ChenShi", "SiShi",
            "WuShi", "WeiShi", "ShenShi", "YouShi", "XuShi", "HaiShi"
    };

    // --- I Ching Hexagrams ---

    private static final List<Hexagram> HEXAGRAMS = Arrays.asList(
            new Hexagram("\u4DC0", "Qian", "governance"),
            new Hexagram("\u4DC1", "Kun", "archive"),
            new Hexagram("\u4DC2", "Zhun", "init"),
            new Hexagram("\u4DC3", "Meng", "learn"),
            new Hexagram("\u4DC4", "Xu", "async"),
            new Hexagram("\u4DC5", "Song", "legal"),
            new Hexagram("\u4DC6", "Kan", "engine"),
            new Hexagram("\u4DC7", "Li", "audit"),
            new Hexagram("\u4DC8", "Zhen", "security"),
            new Hexagram("\u4DC9", "Gen", "privacy"),
            new Hexagram("\u4DCA", "Xun", "deploy"),
            new Hexagram("\u4DCB", "Dui", "trust"),
            new Hexagram("\u4DCC", "JiJi", "complete"),
            new Hexagram("\u4DCD", "WeiJi", "progress")
    );

    // Null means "use runtime calculation"
    private static final Integer[] CYCLE_MONTH = {2, 4, 6, 8, 10, 0, 2, 4, 6, 8, 10, 0};

    private final int CYCLE_YEAR = 1984;

    private static final Map<String, String> TASK_HEXAGRAM_MAP = new HashMap<>();

    static {
        TASK_HEXAGRAM_MAP.put("default", "governance");
        TASK_HEXAGRAM_MAP.put("code", "engine");
        TASK_HEXAGRAM_MAP.put("deploy", "deploy");
        TASK_HEXAGRAM_MAP.put("audit", "audit");
        TASK_HEXAGRAM_MAP.put("security", "security");
        TASK_HEXAGRAM_MAP.put("archive", "archive");
        TASK_HEXAGRAM_MAP.put("init", "init");
        TASK_HEXAGRAM_MAP.put("learn", "learn");
        TASK_HEXAGRAM_MAP.put("legal", "legal");
        TASK_HEXAGRAM_MAP.put("privacy", "privacy");
        TASK_HEXAGRAM_MAP.put("trust", "trust");
        TASK_HEXAGRAM_MAP.put("complete", "complete");
        TASK_HEXAGRAM_MAP.put("progress", "progress");
    }

    private final String uid;
    private final String device;

    public DNAGenerator() {
        this("9622", "HM-9622-001");
    }

    public DNAGenerator(String uid, String device) {
        this.uid = uid;
        this.device = device;
    }

    /**
     * Generate a full DNA traceability string.
     *
     * @param taskType Task category (code, deploy, audit, default, etc.)
     * @param action   Action descriptor (WRAP, GENERATE, DEPLOY, AUDIT)
     * @param version  Optional version override
     * @return Full DNA traceability code
     */
    public String generate(String taskType, String action, String version) {
        ZoneId shanghai = ZoneId.of("Asia/Shanghai");
        ZonedDateTime now = ZonedDateTime.now(shanghai);
        LocalDateTime ldt = now.toLocalDateTime();

        StemBranchResult stem = computeStemBranch(ldt);
        Hexagram hexagram = selectHexagram(taskType);
        String ver = (version != null) ? version : "V1.0";
        String task = (taskType != null) ? taskType.toUpperCase() : "DEFAULT";
        String act = (action != null) ? action.toUpperCase() : "WRAP";

        String body = "ADAPTER-" + task + "-" + act + "-" + ver;

        String raw = stem.year + stem.month + stem.day + stem.shichen
                + hexagram.symbol + hexagram.enName
                + body + device + now.toString();

        String hash8 = sha256Hex(raw).substring(0, 8);

        return "#LongHun\u26A1\uFE0F" + stem.year + "\u00B7" + stem.month + "\u00B7"
                + stem.day + "\u00B7" + stem.shichen
                + "\u00B7" + hexagram.symbol + hexagram.enName
                + "-" + body + "-" + hash8;
    }

    /**
     * Convenience method with default action and version.
     */
    public String generate(String taskType) {
        return generate(taskType, "WRAP", null);
    }

    /**
     * Compute Heavenly Stem + Earthly Branch for a datetime.
     */
    StemBranchResult computeStemBranch(LocalDateTime dt) {
        int year = dt.getYear();
        int month = dt.getMonthValue();
        int day = dt.getDayOfYear();

        int yearStemIdx = Math.floorMod(year - CYCLE_YEAR, 10);
        int yearBranchIdx = Math.floorMod(year - CYCLE_YEAR, 12);

        int cycleIdx = Math.floorMod(year - CYCLE_YEAR, 10);
        Integer monthBase = CYCLE_MONTH[cycleIdx];
        int monthStemIdx;
        if (monthBase != null) {
            monthStemIdx = (monthBase + (month - 1)) % 10;
        } else {
            monthStemIdx = (month * 2) % 10;
        }
        int monthBranchIdx = (month + 1) % 12;

        int dayOffset = (year - 1900) + (year - 1900) / 4 + day;
        int dayStemIdx = Math.floorMod(dayOffset, 10);
        int dayBranchIdx = Math.floorMod(dayOffset, 12);

        int shichenIdx = dt.getHour() / 2;
        if (shichenIdx >= 12) shichenIdx = 11;

        StemBranchResult result = new StemBranchResult();
        result.year = TIAN_GAN[yearStemIdx] + DI_ZHI[yearBranchIdx];
        result.month = TIAN_GAN[monthStemIdx % 10] + DI_ZHI[monthBranchIdx];
        result.day = TIAN_GAN[dayStemIdx] + DI_ZHI[dayBranchIdx];
        result.shichen = SHI_CHEN[shichenIdx];
        return result;
    }

    /**
     * Select I Ching hexagram based on task type.
     */
    Hexagram selectHexagram(String taskType) {
        String domain = TASK_HEXAGRAM_MAP.getOrDefault(taskType, "governance");
        for (Hexagram h : HEXAGRAMS) {
            if (h.domain.equals(domain)) {
                return h;
            }
        }
        return HEXAGRAMS.get(0); // Default: Qian (governance)
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    // --- Data classes ---

    static class StemBranchResult {
        String year;
        String month;
        String day;
        String shichen;
    }

    static class Hexagram {
        final String symbol;
        final String enName;
        final String domain;

        Hexagram(String symbol, String enName, String domain) {
            this.symbol = symbol;
            this.enName = enName;
            this.domain = domain;
        }
    }
}