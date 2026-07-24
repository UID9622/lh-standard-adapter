package cn.uid9622.longhun;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/**
 * AuditWrapper — seven-factor behavioral audit metadata.
 */
public final class AuditWrapper {

    public static final List<String> P_VALUES = List.of("HasPromise", "NoPromise");
    public static final List<String> F_VALUES = List.of("Fulfilled", "Unfulfilled", "Partial");
    public static final List<String> E_VALUES = List.of("Willing", "Perfunctory", "Resentful", "Numb");
    public static final List<String> A_VALUES = List.of("Self", "Partner", "Family", "Outsider", "Public");
    public static final List<String> X_VALUES = List.of("OverExplain", "Silent", "Genuine", "Indifferent");
    public static final List<String> Y_VALUES = List.of("Changed", "Resisted", "Indifferent", "NoResponse");

    private static final Map<String,Map<String,String>> LABEL_MAP = Map.of(
        "P", Map.of("HasPromise", "7F-P-有承诺", "NoPromise", "7F-P-无承诺"),
        "F", Map.of("Fulfilled", "7F-F-已兑现", "Unfulfilled", "7F-F-未兑现", "Partial", "7F-F-部分兑现"),
        "E", Map.of("Willing", "7F-E-心甘情愿", "Perfunctory", "7F-E-敷衍", "Resentful", "7F-E-怨恨", "Numb", "7F-E-麻木"),
        "A", Map.of("Self", "7F-A-自己", "Partner", "7F-A-伴侣", "Family", "7F-A-家庭", "Outsider", "7F-A-外人", "Public", "7F-A-公众"),
        "X", Map.of("OverExplain", "7F-X-过度解释", "Silent", "7F-X-沉默", "Genuine", "7F-X-真诚", "Indifferent", "7F-X-冷漠"),
        "Y", Map.of("Changed", "7F-Y-改正", "Resisted", "7F-Y-抗拒", "Indifferent", "7F-Y-无视", "NoResponse", "7F-Y-无响应")
    );

    private final String uid;

    public AuditWrapper(String uid) { this.uid = uid; }
    public AuditWrapper() { this("9622"); }

    public Map<String, Object> wrap(Object payload, String taskType, String persona) {
        Map<String, Object> sig = new LinkedHashMap<>();
        sig.put("P", "HasPromise"); sig.put("F", "Fulfilled"); sig.put("T", 0.0);
        sig.put("E", "Willing"); sig.put("C", 0); sig.put("R", 0);
        sig.put("A", "Self"); sig.put("X", "Genuine"); sig.put("Y", "NoResponse"); sig.put("Z", 1.0);

        String pattern = classify(sig);
        List<String> labels = makeLabels(sig, pattern);
        String color = determineColor(pattern, 0);

        String payloadJson = payload.toString();
        String payloadHash = sha256hex(payloadJson).substring(0, 16);

        Map<String, Object> audit = new LinkedHashMap<>();
        audit.put("audit_version", "v1.0");
        audit.put("uid", "UID" + uid);
        audit.put("persona", persona);
        audit.put("task_type", taskType);
        audit.put("behavior_signature", sig);
        audit.put("behavior_pattern", pattern);
        audit.put("behavior_labels", labels);
        audit.put("color", color);
        audit.put("timestamp", java.time.ZonedDateTime.now(java.time.ZoneOffset.ofHours(8)).format(java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        audit.put("payload_hash", payloadHash);
        return audit;
    }

    public String classify(Map<String, Object> sig) {
        String f = (String) sig.getOrDefault("F", "");
        String x = (String) sig.getOrDefault("X", "");
        String a = (String) sig.getOrDefault("A", "");
        String y = (String) sig.getOrDefault("Y", "");
        double z = ((Number) sig.getOrDefault("Z", 1.0)).doubleValue();
        if ("Unfulfilled".equals(f) && "OverExplain".equals(x)) return "MODE-DefensiveDefaulter";
        if ("Fulfilled".equals(f) && "Outsider".equals(a)) return "MODE-ExternalTrustSpender";
        if ("Unfulfilled".equals(f) && "Indifferent".equals(y)) return "MODE-InternalDestroyer";
        if (z > 2.0) return "MODE-Fluctuating";
        return "MODE-StableDisciplined";
    }

    public String determineColor(String pattern, int repeat) {
        if ("MODE-InternalDestroyer".equals(pattern)) return "🔴";
        if ("MODE-Fluctuating".equals(pattern) && repeat > 3) return "🟡";
        if ("MODE-DefensiveDefaulter".equals(pattern) && repeat > 2) return "🟡";
        return "🟢";
    }

    private List<String> makeLabels(Map<String, Object> sig, String pattern) {
        List<String> labels = new ArrayList<>();
        for (String f : List.of("P", "F", "E", "A", "X", "Y")) {
            String val = (String) sig.get(f);
            if (val != null && LABEL_MAP.containsKey(f) && LABEL_MAP.get(f).containsKey(val)) {
                labels.add(LABEL_MAP.get(f).get(val));
            }
        }
        labels.add(pattern);
        return labels;
    }

    static String sha256hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
