package cn.uid9622.longhun;

import java.security.MessageDigest;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class DNAGenerator {
    private static final String[] TIAN_GAN = {"Jia","Yi","Bing","Ding","Wu","Ji","Geng","Xin","Ren","Gui"};
    private static final String[] DI_ZHI = {"Zi","Chou","Yin","Mao","Chen","Si","Wu","Wei","Shen","You","Xu","Hai"};
    private static final String[] SHI_CHEN = {"ZiShi","ChouShi","YinShi","MaoShi","ChenShi","SiShi","WuShi","WeiShi","ShenShi","YouShi","XuShi","HaiShi"};

    private static final int[] CYCLE_MONTH = {2,4,6,8,10,0,2,4,6,8,10,0};
    private static final int CYCLE_YEAR = 1984;

    private final String uid, device, locale;

    public DNAGenerator(String uid, String device, String locale) {
        this.uid = uid != null ? uid : "9622";
        this.device = device != null ? device : "HM-9622-001";
        this.locale = locale != null ? locale : "Asia/Shanghai";
    }

    public String generate(String taskType, String action, String version) {
        if (taskType == null || taskType.isEmpty()) taskType = "default";
        if (action == null || action.isEmpty()) action = "WRAP";
        if (version == null || version.isEmpty()) version = "V1.0";

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        Map<String,String> stem = computeStemBranch(now);
        String[] hex = selectHexagram(taskType);
        String body = "ADAPTER-" + taskType.toUpperCase() + "-" + action.toUpperCase() + "-" + version;

        String raw = stem.get("year") + stem.get("month") + stem.get("day") + stem.get("shichen")
            + hex[0] + hex[1] + body + device + now.toString();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(raw.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            String hash8 = sb.toString().substring(0, 8);

            return "#LongHun⚡️" + stem.get("year") + "·" + stem.get("month") + "·" + stem.get("day")
                + "·" + stem.get("shichen") + "·" + hex[0] + hex[1] + "-" + body + "-" + hash8;
        } catch (Exception e) { return "#LongHun⚡️ERROR-" + e.getMessage(); }
    }

    private Map<String,String> computeStemBranch(ZonedDateTime dt) {
        int y = dt.getYear(), m = dt.getMonthValue(), doy = dt.getDayOfYear(), h = dt.getHour();
        int ys = ((y - CYCLE_YEAR) % 10 + 10) % 10;
        int yb = ((y - CYCLE_YEAR) % 12 + 12) % 12;
        int mb = CYCLE_MONTH[((y - CYCLE_YEAR) % 10 + 10) % 10];
        int ms = ((mb + m - 1) % 10 + 10) % 10;
        int mbr = ((m + 1) % 12 + 12) % 12;
        int ds = ((y - 1900 + (y - 1900) / 4 + doy) % 10 + 10) % 10;
        int db = ((y - 1900 + (y - 1900) / 4 + doy) % 12 + 12) % 12;
        int si = h / 2;

        Map<String,String> result = new LinkedHashMap<>();
        result.put("year", TIAN_GAN[ys] + DI_ZHI[yb]);
        result.put("month", TIAN_GAN[ms] + DI_ZHI[mbr]);
        result.put("day", TIAN_GAN[ds] + DI_ZHI[db]);
        result.put("shichen", SHI_CHEN[si]);
        return result;
    }

    private String[] selectHexagram(String taskType) {
        String domain;
        switch (taskType) {
            case "code": domain = "engine"; break;
            case "deploy": domain = "deploy"; break;
            case "audit": domain = "audit"; break;
            case "security": domain = "security"; break;
            case "archive": domain = "archive"; break;
            case "init": domain = "init"; break;
            case "learn": domain = "learn"; break;
            case "legal": domain = "legal"; break;
            case "privacy": domain = "privacy"; break;
            case "trust": domain = "trust"; break;
            case "complete": domain = "complete"; break;
            case "progress": domain = "progress"; break;
            default: domain = "governance";
        }
        String[][] all = {{"䷀","Qian","governance"},{"䷁","Kun","archive"},{"䷂","Zhun","init"},
            {"䷃","Meng","learn"},{"䷄","Xu","async"},{"䷅","Song","legal"},
            {"䷜","Kan","engine"},{"䷝","Li","audit"},{"䷲","Zhen","security"},
            {"䷳","Gen","privacy"},{"䷸","Xun","deploy"},{"䷹","Dui","trust"},
            {"䷾","JiJi","complete"},{"䷿","WeiJi","progress"}};
        for (String[] h : all) if (h[2].equals(domain)) return new String[]{h[0], h[1]};
        return new String[]{"䷀","Qian"};
    }
}
