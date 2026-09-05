package com.longhun.adapter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * LongHun Standard Adapter — Java
 *
 * <p>DNA v∞ traceability generation + validation, mirroring the Python reference
 * implementation so that all language adapters produce identical DNA prefixes
 * (four Ganzhi pillars + hexagram + body) for the same task at the same instant
 * under Asia/Shanghai time.</p>
 */
public class LongHunAdapter {

    private static final String[] TIAN_GAN = {"Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"};
    private static final String[] DI_ZHI = {"Zi", "Chou", "Yin", "Mao", "Chen", "Si", "Wu", "Wei", "Shen", "You", "Xu", "Hai"};
    private static final String[] SHI_CHEN = {"ZiShi", "ChouShi", "YinShi", "MaoShi", "ChenShi", "SiShi", "WuShi", "WeiShi", "ShenShi", "YouShi", "XuShi", "HaiShi"};
    private static final int CYCLE_YEAR = 1984; // JiaZi reference year
    private static final int[] CYCLE_MONTH = {2, 4, 6, 8, 10, 0, 2, 4, 6, 8, 10, 0};

    private static final Pattern DNA_REGEX = Pattern.compile(
            "^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([䷀-䷿][A-Za-z]+)-(.+)-([a-f0-9]{8})$");

    private static final class Hexagram {
        final String symbol, enName, domain;
        Hexagram(String symbol, String enName, String domain) {
            this.symbol = symbol;
            this.enName = enName;
            this.domain = domain;
        }
    }

    private static final Hexagram[] HEXAGRAMS = {
            new Hexagram("䷀", "Qian", "governance"),
            new Hexagram("䷁", "Kun", "archive"),
            new Hexagram("䷂", "Zhun", "init"),
            new Hexagram("䷃", "Meng", "learn"),
            new Hexagram("䷄", "Xu", "async"),
            new Hexagram("䷅", "Song", "legal"),
            new Hexagram("䷜", "Kan", "engine"),
            new Hexagram("䷝", "Li", "audit"),
            new Hexagram("䷲", "Zhen", "security"),
            new Hexagram("䷳", "Gen", "privacy"),
            new Hexagram("䷸", "Xun", "deploy"),
            new Hexagram("䷹", "Dui", "trust"),
            new Hexagram("䷾", "JiJi", "complete"),
            new Hexagram("䷿", "WeiJi", "progress"),
    };

    private static final java.util.Map<String, String> TASK_DOMAIN = new java.util.HashMap<>();
    static {
        TASK_DOMAIN.put("default", "governance");
        TASK_DOMAIN.put("code", "engine");
        TASK_DOMAIN.put("deploy", "deploy");
        TASK_DOMAIN.put("audit", "audit");
        TASK_DOMAIN.put("security", "security");
        TASK_DOMAIN.put("archive", "archive");
        TASK_DOMAIN.put("init", "init");
        TASK_DOMAIN.put("learn", "learn");
        TASK_DOMAIN.put("legal", "legal");
        TASK_DOMAIN.put("privacy", "privacy");
        TASK_DOMAIN.put("trust", "trust");
        TASK_DOMAIN.put("complete", "complete");
        TASK_DOMAIN.put("progress", "progress");
    }

    private final String uid;
    private final String device;

    public LongHunAdapter(String uid, String device) {
        this.uid = (uid == null || uid.isEmpty()) ? "9622" : uid;
        this.device = (device == null || device.isEmpty()) ? "HM-9622-001" : device;
    }

    private static int mod(int n, int m) {
        return ((n % m) + m) % m;
    }

    private static Hexagram selectHexagram(String taskType) {
        String domain = TASK_DOMAIN.getOrDefault(taskType, "governance");
        for (Hexagram h : HEXAGRAMS) {
            if (h.domain.equals(domain)) {
                return h;
            }
        }
        return HEXAGRAMS[0];
    }

    public String generateDNA(String taskType, String action, String version) {
        if (taskType == null || taskType.isEmpty()) taskType = "default";
        if (action == null || action.isEmpty()) action = "WRAP";
        if (version == null || version.isEmpty()) version = "V1.0";

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        int base = now.getYear() - CYCLE_YEAR;

        String yearPillar = TIAN_GAN[mod(base, 10)] + DI_ZHI[mod(base, 12)];

        int month = now.getMonthValue();
        int mStem = mod(CYCLE_MONTH[mod(base, 10)] + month - 1, 10);
        String monthPillar = TIAN_GAN[mStem] + DI_ZHI[(month + 1) % 12];

        int julian = now.getYear() - 1900 + (now.getYear() - 1900) / 4 + now.getDayOfYear();
        String dayPillar = TIAN_GAN[mod(julian, 10)] + DI_ZHI[mod(julian, 12)];

        String shichen = SHI_CHEN[((now.getHour() + 1) / 2) % 12];

        Hexagram hex = selectHexagram(taskType);
        String body = String.format("ADAPTER-%s-%s-%s",
                taskType.toUpperCase(), action.toUpperCase(), version.toUpperCase());

        String raw = yearPillar + monthPillar + dayPillar + shichen
                + hex.symbol + hex.enName + body + device
                + now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String hash8 = sha256Hex(raw).substring(0, 8);
        return String.format("#LongHun⚡️%s·%s·%s·%s·%s%s-%s-%s",
                yearPillar, monthPillar, dayPillar, shichen, hex.symbol, hex.enName, body, hash8);
    }

    public boolean validate(String dna) {
        return dna != null && DNA_REGEX.matcher(dna).matches();
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Simple CLI demo used by the CI consistency check. */
    public static void main(String[] args) {
        System.out.println(new LongHunAdapter("9622", "HM-9622-001")
                .generateDNA("code", "WRAP", "V1.0"));
    }
}
