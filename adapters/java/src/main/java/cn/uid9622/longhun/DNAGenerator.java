package cn.uid9622.longhun;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class DNAGenerator {
    public static final List<String> TIAN_GAN = List.of("Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui");
    public static final List<String> DI_ZHI = List.of("Zi", "Chou", "Yin", "Mao", "Chen", "Si", "Wu", "Wei", "Shen", "You", "Xu", "Hai");
    public static final List<String> SHI_CHEN = List.of("ZiShi", "ChouShi", "YinShi", "MaoShi", "ChenShi", "SiShi", "WuShi", "WeiShi", "ShenShi", "YouShi", "XuShi", "HaiShi");

    public record Hexagram(String symbol, String enName, String cnName, String domain) {}

    public static final List<Hexagram> HEXAGRAMS = List.of(
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
        new Hexagram("䷿", "WeiJi", "未济", "progress")
    );

    public static final Map<String, String> TASK_HEXAGRAM_MAP = Map.ofEntries(
        Map.entry("default", "governance"),
        Map.entry("code", "engine"),
        Map.entry("deploy", "deploy"),
        Map.entry("audit", "audit"),
        Map.entry("security", "security"),
        Map.entry("archive", "archive"),
        Map.entry("init", "init"),
        Map.entry("learn", "learn"),
        Map.entry("legal", "legal"),
        Map.entry("privacy", "privacy"),
        Map.entry("trust", "trust"),
        Map.entry("complete", "complete"),
        Map.entry("progress", "progress")
    );

    private final String uid;
    private final String device;
    private final int cycleYear = 1984;
    private final int[] cycleMonth = {2, 4, 6, 8, 10, 0, 2, 4, 6, 8, 10, 0};

    public DNAGenerator(String uid, String device) {
        this.uid = uid != null ? uid : "9622";
        this.device = device != null ? device : "HM-9622-001";
    }

    public String generate(String taskType, String action, String version) {
        if (taskType == null) taskType = "default";
        if (action == null) action = "WRAP";
        if (version == null) version = "V1.0";

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        Hexagram hexagram = selectHexagram(taskType);
        String body = String.format("ADAPTER-%s-%s-%s", taskType.toUpperCase(), action.toUpperCase(), version);

        String yearStemBranch = computeYearStemBranch(now.getYear());
        String monthStemBranch = computeMonthStemBranch(now.getYear(), now.getMonthValue() - 1);
        String dayStemBranch = computeDayStemBranch(now.getYear(), now.getDayOfYear());
        String shichen = SHI_CHEN.get((now.getHour() / 2) % 12);

        String raw = yearStemBranch + monthStemBranch + dayStemBranch + shichen + hexagram.symbol() + hexagram.enName() + body + device + now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String hash8 = sha256Hex(raw).substring(0, 8);

        return String.format("#LongHun⚡️%s·%s·%s·%s·%s%s-%s-%s", yearStemBranch, monthStemBranch, dayStemBranch, shichen, hexagram.symbol(), hexagram.enName(), body, hash8);
    }

    private String computeYearStemBranch(int year) {
        int yearStemIdx = Math.abs((year - cycleYear) % 10);
        int yearBranchIdx = Math.abs((year - cycleYear) % 12);
        return TIAN_GAN.get(yearStemIdx) + DI_ZHI.get(yearBranchIdx);
    }

    private String computeMonthStemBranch(int year, int month) {
        int yearStemIdx = Math.abs((year - cycleYear) % 10);
        int monthStemIdx = Math.abs((cycleMonth[yearStemIdx] + month) % 10);
        int monthBranchIdx = Math.abs((month + 2) % 12);
        return TIAN_GAN.get(monthStemIdx) + DI_ZHI.get(monthBranchIdx);
    }

    private String computeDayStemBranch(int year, int dayOfYear) {
        int dayStemIdx = Math.abs((year - 1900 + (year - 1900) / 4 + dayOfYear) % 10);
        int dayBranchIdx = Math.abs((year - 1900 + (year - 1900) / 4 + dayOfYear) % 12);
        return TIAN_GAN.get(dayStemIdx) + DI_ZHI.get(dayBranchIdx);
    }

    private Hexagram selectHexagram(String taskType) {
        String domain = TASK_HEXAGRAM_MAP.getOrDefault(taskType, "governance");
        return HEXAGRAMS.stream().filter(h -> h.domain().equals(domain)).findFirst().orElse(HEXAGRAMS.get(0));
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
