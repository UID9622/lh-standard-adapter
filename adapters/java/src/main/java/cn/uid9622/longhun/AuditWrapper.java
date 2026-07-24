package cn.uid9622.longhun;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuditWrapper {
    public static final Map<String, Map<String, String>> LABEL_MAP = Map.of(
        "P", Map.of("HasPromise", "7F-P-有承诺", "NoPromise", "7F-P-无承诺"),
        "F", Map.of("Fulfilled", "7F-F-已兑现", "Unfulfilled", "7F-F-未兑现", "Partial", "7F-F-部分兑现"),
        "E", Map.of("Willing", "7F-E-心甘情愿", "Perfunctory", "7F-E-敷衍", "Resentful", "7F-E-怨恨", "Numb", "7F-E-麻木"),
        "A", Map.of("Self", "7F-A-自己", "Partner", "7F-A-伴侣", "Family", "7F-A-家庭", "Outsider", "7F-A-外人", "Public", "7F-A-公众"),
        "X", Map.of("OverExplain", "7F-X-过度解释", "Silent", "7F-X-沉默", "Genuine", "7F-X-真诚", "Indifferent", "7F-X-冷漠"),
        "Y", Map.of("Changed", "7F-Y-改正", "Resisted", "7F-Y-抗拒", "Indifferent", "7F-Y-无视", "NoResponse", "7F-Y-无响应")
    );

    private final String uid;

    public AuditWrapper(String uid) {
        this.uid = uid != null ? uid : "9622";
    }

    public Map<String, Object> wrap(Object payload, String taskType, String persona) {
        if (taskType == null) taskType = "default";
        if (persona == null) persona = "P04";

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));

        Map<String, Object> signature = new HashMap<>();
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

        String payloadStr = String.valueOf(payload);
        String payloadHash = sha256Hex(payloadStr).substring(0, 16);

        Map<String, Object> audit = new HashMap<>();
        audit.put("audit_version", "v1.0");
        audit.put("uid", "UID" + uid);
        audit.put("persona", persona);
        audit.put("task_type", taskType);
        audit.put("behavior_signature", signature);
        audit.put("behavior_pattern", pattern);
        audit.put("behavior_labels", labels);
        audit.put("color", color);
        audit.put("timestamp", now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        audit.put("payload_hash", payloadHash);

        return audit;
    }

    private String classify(Map<String, Object> sig) {
        String f = (String) sig.get("F");
        String x = (String) sig.get("X");
        String a = (String) sig.get("A");
        String y = (String) sig.get("Y");
        double z = (double) sig.get("Z");

        if ("Unfulfilled".equals(f) && "OverExplain".equals(x)) return "MODE-DefensiveDefaulter";
        if ("Fulfilled".equals(f) && "Outsider".equals(a)) return "MODE-ExternalTrustSpender";
        if ("Unfulfilled".equals(f) && "Indifferent".equals(y)) return "MODE-InternalDestroyer";
        if (z > 2.0) return "MODE-Fluctuating";
        return "MODE-StableDisciplined";
    }

    private List<String> makeLabels(Map<String, Object> sig, String pattern) {
        List<String> labels = new ArrayList<>();
        String[] factors = {"P", "F", "E", "A", "X", "Y"};
        for (String factor : factors) {
            String val = (String) sig.get(factor);
            if (LABEL_MAP.containsKey(factor) && LABEL_MAP.get(factor).containsKey(val)) {
                labels.add(LABEL_MAP.get(factor).get(val));
            }
        }
        labels.add(pattern);
        return labels;
    }

    private String determineColor(String pattern, int repeat) {
        if ("MODE-InternalDestroyer".equals(pattern)) return "🔴";
        if ("MODE-Fluctuating".equals(pattern) && repeat > 3) return "🟡";
        if ("MODE-DefensiveDefaulter".equals(pattern) && repeat > 2) return "🟡";
        return "🟢";
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
