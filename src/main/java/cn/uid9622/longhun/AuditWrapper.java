package cn.uid9622.longhun;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Audit Wrapper — seven-factor behavioral audit metadata generation.
 * <p>
 * Core scoring algorithms (weights, neural network logic) are
 * protected engine components and NOT included in this open shell.
 */
public class AuditWrapper {

    // --- Seven-Factor Value Sets ---

    private static final Set<String> P_VALUES = new HashSet<>(Arrays.asList("HasPromise", "NoPromise"));
    private static final Set<String> F_VALUES = new HashSet<>(Arrays.asList("Fulfilled", "Unfulfilled", "Partial"));
    private static final Set<String> E_VALUES = new HashSet<>(Arrays.asList("Willing", "Perfunctory", "Resentful", "Numb"));
    private static final Set<String> A_VALUES = new HashSet<>(Arrays.asList("Self", "Partner", "Family", "Outsider", "Public"));
    private static final Set<String> X_VALUES = new HashSet<>(Arrays.asList("OverExplain", "Silent", "Genuine", "Indifferent"));
    private static final Set<String> Y_VALUES = new HashSet<>(Arrays.asList("Changed", "Resisted", "Indifferent", "NoResponse"));

    // --- Label Map (bilingual) ---

    private static final Map<String, Map<String, String>> LABEL_MAP = new LinkedHashMap<>();

    static {
        Map<String, String> pMap = new LinkedHashMap<>();
        pMap.put("HasPromise", "7F-P-\u6709\u627F\u8BFA");
        pMap.put("NoPromise", "7F-P-\u65E0\u627F\u8BFA");
        LABEL_MAP.put("P", pMap);

        Map<String, String> fMap = new LinkedHashMap<>();
        fMap.put("Fulfilled", "7F-F-\u5DF2\u5151\u73B0");
        fMap.put("Unfulfilled", "7F-F-\u672A\u5151\u73B0");
        fMap.put("Partial", "7F-F-\u90E8\u5206\u5151\u73B0");
        LABEL_MAP.put("F", fMap);

        Map<String, String> eMap = new LinkedHashMap<>();
        eMap.put("Willing", "7F-E-\u5FC3\u7518\u60C5\u613F");
        eMap.put("Perfunctory", "7F-E-\u6577\u884D");
        eMap.put("Resentful", "7F-E-\u6028\u6068");
        eMap.put("Numb", "7F-E-\u9EBB\u6728");
        LABEL_MAP.put("E", eMap);

        Map<String, String> aMap = new LinkedHashMap<>();
        aMap.put("Self", "7F-A-\u81EA\u5DF1");
        aMap.put("Partner", "7F-A-\u4F34\u4FA3");
        aMap.put("Family", "7F-A-\u5BB6\u5EAD");
        aMap.put("Outsider", "7F-A-\u5916\u4EBA");
        aMap.put("Public", "7F-A-\u516C\u4F17");
        LABEL_MAP.put("A", aMap);

        Map<String, String> xMap = new LinkedHashMap<>();
        xMap.put("OverExplain", "7F-X-\u8FC7\u5EA6\u89E3\u91CA");
        xMap.put("Silent", "7F-X-\u6C89\u9ED8");
        xMap.put("Genuine", "7F-X-\u771F\u8BDA");
        xMap.put("Indifferent", "7F-X-\u51B7\u6F20");
        LABEL_MAP.put("X", xMap);

        Map<String, String> yMap = new LinkedHashMap<>();
        yMap.put("Changed", "7F-Y-\u6539\u6B63");
        yMap.put("Resisted", "7F-Y-\u62D2\u7EDD");
        yMap.put("Indifferent", "7F-Y-\u65E0\u89C6");
        yMap.put("NoResponse", "7F-Y-\u65E0\u54CD\u5E94");
        LABEL_MAP.put("Y", yMap);
    }

    // --- Behavior Pattern Classification ---

    private static final String PATTERN_DEFENSIVE_DEFAULTER = "MODE-DefensiveDefaulter";
    private static final String PATTERN_EXTERNAL_TRUST_SPENDER = "MODE-ExternalTrustSpender";
    private static final String PATTERN_INTERNAL_DESTROYER = "MODE-InternalDestroyer";
    private static final String PATTERN_FLUCTUATING = "MODE-Fluctuating";
    private static final String PATTERN_STABLE_DISCIPLINED = "MODE-StableDisciplined";

    private static final Set<String> VALID_PATTERNS = new HashSet<>(Arrays.asList(
            PATTERN_DEFENSIVE_DEFAULTER, PATTERN_EXTERNAL_TRUST_SPENDER,
            PATTERN_INTERNAL_DESTROYER, PATTERN_FLUCTUATING, PATTERN_STABLE_DISCIPLINED
    ));

    private final String uid;
    private final DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public AuditWrapper() {
        this("9622");
    }

    public AuditWrapper(String uid) {
        this.uid = uid;
    }

    /**
     * Generate audit wrapper with seven-factor signature.
     *
     * @param payload  Raw data to wrap (any JSON-serializable object)
     * @param taskType Task category
     * @param persona  Persona identifier
     * @return Map with audit metadata
     */
    public Map<String, Object> wrap(Object payload, String taskType, String persona) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));

        // Default signature (StableDisciplined baseline)
        Map<String, Object> signature = new LinkedHashMap<>();
        signature.put("P", "HasPromise");
        signature.put("F", "Fulfilled");
        signature.put("T", 0.0);
        signature.put("E", "Willing");
        signature.put("C", 0);
        signature.put("R", 0);
        signature.put("A", "Self");
        signature.put("X", "Genuine");
        signature.put("Y", "NoResponse");
        signature.put("Z", 1.0);

        String pattern = classify(signature);
        List<String> labels = makeLabels(signature, pattern);
        String color = determineColor(pattern, (int) signature.get("R"));

        // Payload hash
        String payloadJson = toJsonString(payload);
        String payloadHash = sha256Hex(payloadJson).substring(0, 16);

        Map<String, Object> audit = new LinkedHashMap<>();
        audit.put("audit_version", "v1.0");
        audit.put("uid", "UID" + uid);
        audit.put("persona", persona != null ? persona : "P04");
        audit.put("task_type", taskType != null ? taskType : "default");
        audit.put("behavior_signature", signature);
        audit.put("behavior_pattern", pattern);
        audit.put("behavior_labels", labels);
        audit.put("color", color);
        audit.put("timestamp", now.format(isoFormatter));
        audit.put("payload_hash", payloadHash);

        return audit;
    }

    /**
     * Classify seven-factor signature into behavior pattern.
     */
    String classify(Map<String, Object> sig) {
        String fVal = (String) sig.getOrDefault("F", "");
        String xVal = (String) sig.getOrDefault("X", "");
        String aVal = (String) sig.getOrDefault("A", "");
        String yVal = (String) sig.getOrDefault("Y", "");
        Number zVal = (Number) sig.getOrDefault("Z", 1.0);

        if ("Unfulfilled".equals(fVal) && "OverExplain".equals(xVal)) {
            return PATTERN_DEFENSIVE_DEFAULTER;
        }
        if ("Fulfilled".equals(fVal) && "Outsider".equals(aVal)) {
            return PATTERN_EXTERNAL_TRUST_SPENDER;
        }
        if ("Unfulfilled".equals(fVal) && "Indifferent".equals(yVal)) {
            return PATTERN_INTERNAL_DESTROYER;
        }
        if (zVal != null && zVal.doubleValue() > 2.0) {
            return PATTERN_FLUCTUATING;
        }
        return PATTERN_STABLE_DISCIPLINED;
    }

    /**
     * Generate bilingual behavior labels from signature.
     */
    List<String> makeLabels(Map<String, Object> sig, String pattern) {
        List<String> labels = new ArrayList<>();
        for (String factor : Arrays.asList("P", "F", "E", "A", "X", "Y")) {
            String val = (String) sig.get(factor);
            if (val != null && LABEL_MAP.containsKey(factor) && LABEL_MAP.get(factor).containsKey(val)) {
                labels.add(LABEL_MAP.get(factor).get(val));
            }
        }
        labels.add(pattern);
        return labels;
    }

    /**
     * Determine three-color audit tag.
     */
    String determineColor(String pattern, int repeat) {
        if (PATTERN_INTERNAL_DESTROYER.equals(pattern)) {
            return "\uD83D\uDD34"; // red
        }
        if (PATTERN_FLUCTUATING.equals(pattern) && repeat > 3) {
            return "\uD83D\uDFE1"; // yellow
        }
        if (PATTERN_DEFENSIVE_DEFAULTER.equals(pattern) && repeat > 2) {
            return "\uD83D\uDFE1"; // yellow
        }
        return "\uD83D\uDFE2"; // green
    }

    static Set<String> getValidPatterns() {
        return VALID_PATTERNS;
    }

    static Set<String> getPValues() { return P_VALUES; }
    static Set<String> getFValues() { return F_VALUES; }
    static Set<String> getEValues() { return E_VALUES; }
    static Set<String> getAValues() { return A_VALUES; }
    static Set<String> getXValues() { return X_VALUES; }
    static Set<String> getYValues() { return Y_VALUES; }
    static Map<String, Map<String, String>> getLabelMap() { return LABEL_MAP; }

    private String toJsonString(Object obj) {
        try {
            return com.fasterxml.jackson.databind.ObjectMapper.class
                    .getMethod("writeValueAsString", Object.class)
                    .invoke(new com.fasterxml.jackson.databind.ObjectMapper(), obj)
                    .toString();
        } catch (Exception e) {
            return String.valueOf(obj);
        }
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}