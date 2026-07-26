package cn.uid9622.longhun;

import java.security.MessageDigest;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class AuditWrapper {
    private static final Map<String,Map<String,String>> LABEL_MAP = new LinkedHashMap<>();
    static {
        LABEL_MAP.put("P", Map.of("HasPromise","7F-P-有承诺","NoPromise","7F-P-无承诺"));
        LABEL_MAP.put("F", Map.of("Fulfilled","7F-F-已兑现","Unfulfilled","7F-F-未兑现","Partial","7F-F-部分兑现"));
        LABEL_MAP.put("E", Map.of("Willing","7F-E-心甘情愿","Perfunctory","7F-E-敷衍","Resentful","7F-E-怨恨","Numb","7F-E-麻木"));
        LABEL_MAP.put("A", Map.of("Self","7F-A-自己","Partner","7F-A-伴侣","Family","7F-A-家庭","Outsider","7F-A-外人","Public","7F-A-公众"));
        LABEL_MAP.put("X", Map.of("OverExplain","7F-X-过度解释","Silent","7F-X-沉默","Genuine","7F-X-真诚","Indifferent","7F-X-冷漠"));
        LABEL_MAP.put("Y", Map.of("Changed","7F-Y-改正","Resisted","7F-Y-抗拒","Indifferent","7F-Y-无视","NoResponse","7F-Y-无响应"));
    }

    private final String uid;
    public AuditWrapper(String uid) { this.uid = uid != null ? uid : "9622"; }

    public Map<String,Object> wrap(Object payload, String taskType, String persona) {
        if (taskType == null || taskType.isEmpty()) taskType = "default";
        if (persona == null || persona.isEmpty()) persona = "P04";

        Map<String,Object> sig = new LinkedHashMap<>();
        sig.put("P","HasPromise"); sig.put("F","Fulfilled"); sig.put("T",0.0);
        sig.put("E","Willing"); sig.put("C",0); sig.put("R",0);
        sig.put("A","Self"); sig.put("X","Genuine"); sig.put("Y","NoResponse"); sig.put("Z",1.0);

        String pattern = classify(sig);
        List<String> labels = makeLabels(sig, pattern);
        String color = determineColor(pattern, (int)sig.get("R"));

        Map<String,Object> result = new LinkedHashMap<>();
        result.put("audit_version","v1.0");
        result.put("uid","UID"+uid);
        result.put("persona",persona);
        result.put("task_type",taskType);
        result.put("behavior_signature",sig);
        result.put("behavior_pattern",pattern);
        result.put("behavior_labels",labels);
        result.put("color",color);
        result.put("timestamp", ZonedDateTime.now(ZoneOffset.UTC).toString());

        try {
            String pj = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] h = md.digest(pj.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : h) sb.append(String.format("%02x", b));
            result.put("payload_hash", sb.toString().substring(0,16));
        } catch (Exception e) { result.put("payload_hash","0000000000000000"); }
        return result;
    }

    static String classify(Map<String,Object> sig) {
        String f = (String)sig.get("F"), x = (String)sig.get("X");
        String a = (String)sig.get("A"), y = (String)sig.get("Y");
        double z = ((Number)sig.getOrDefault("Z",0.0)).doubleValue();
        if ("Unfulfilled".equals(f) && "OverExplain".equals(x)) return "MODE-DefensiveDefaulter";
        if ("Fulfilled".equals(f) && "Outsider".equals(a)) return "MODE-ExternalTrustSpender";
        if ("Unfulfilled".equals(f) && "Indifferent".equals(y)) return "MODE-InternalDestroyer";
        if (z > 2.0) return "MODE-Fluctuating";
        return "MODE-StableDisciplined";
    }

    static List<String> makeLabels(Map<String,Object> sig, String pattern) {
        List<String> labels = new ArrayList<>();
        for (String factor : new String[]{"P","F","E","A","X","Y"}) {
            String val = (String)sig.get(factor);
            if (val != null && LABEL_MAP.containsKey(factor) && LABEL_MAP.get(factor).containsKey(val))
                labels.add(LABEL_MAP.get(factor).get(val));
        }
        labels.add(pattern);
        return labels;
    }

    static String determineColor(String pattern, int repeat) {
        if ("MODE-InternalDestroyer".equals(pattern)) return "🔴";
        if ("MODE-Fluctuating".equals(pattern) && repeat > 3) return "🟡";
        if ("MODE-DefensiveDefaulter".equals(pattern) && repeat > 2) return "🟡";
        return "🟢";
    }
}
