// LongHun Standard Adapter — Java
// DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷝Li-ADAPTER-CODE-WRAP-V1.0.0
package lh_standard_adapter;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class LongHunAdapter {
    public static class Signature {
        public String P="HasPromise", F="Fulfilled";
        public double T=0.0;
        public String E="Willing";
        public int C=0, R=0;
        public String A="Self", X="Genuine", Y="NoResponse";
        public double Z=1.0;
    }
    public static class Audit {
        public String audit_version="v1.0", uid, persona, task_type;
        public Signature behavior_signature;
        public String behavior_pattern;
        public List<String> behavior_labels;
        public String color;
        public String timestamp;
        public String payload_hash;
    }
    public static class Wrapped {
        public String dna;
        public Audit audit;
        public Map<String,Object> payload;
        public Map<String,Object> meta;
    }

    private final String uid;
    public LongHunAdapter(String uid, String device) {
        this.uid = uid==null ? "9622" : uid;
    }
    public LongHunAdapter(String uid) { this(uid, null); }
    public LongHunAdapter() { this("9622", null); }

    public Wrapped wrap(Object data, String task_type, String persona) {
        String body = "ADAPTER-" + (task_type==null?"default":task_type).toUpperCase() + "-WRAP-V1.0";
        String hash8 = sha8(body);
        String dna = "#LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷝Li-" + body + "-" + hash8;

        Signature sig = new Signature();
        String pattern = classify(sig);
        List<String> labels = makeLabels(sig, pattern);
        String col = determineColor(pattern, sig.R);

        String payloadStr = data==null ? "null" : new com.google.gson.Gson().toJson(data);
        String payloadHash = sha16(payloadStr);

        Audit audit = new Audit();
        audit.uid = "UID" + uid;
        audit.persona = persona==null ? "P04" : persona;
        audit.task_type = task_type;
        audit.behavior_signature = sig;
        audit.behavior_pattern = pattern;
        audit.behavior_labels = labels;
        audit.color = col;
        audit.timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        audit.payload_hash = payloadHash;

        Wrapped w = new Wrapped();
        w.dna = dna;
        w.audit = audit;
        w.payload = new com.google.gson.Gson().fromJson(payloadStr, Map.class);
        w.meta = Map.of("adapter_version","1.0.0","uid",uid,"format","longhun-v∞");
        return w;
    }

    public Map<String,Object> validate(Wrapped w) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        if (w==null) { errors.add("Input is null"); return result(false,errors,warnings); }
        if (w.dna==null || w.audit==null) errors.add("Missing required fields");
        if (w.behavior_labels()==null || w.behavior_labels().isEmpty()) warnings.add("No behavior labels");
        return result(errors.isEmpty(), errors, warnings);
    }

    private List<String> behavior_labels() { return audit==null ? null : audit.behavior_labels; }

    private static String classify(Signature s) {
        if ("Unfulfilled".equals(s.F) && "OverExplain".equals(s.X)) return "MODE-DefensiveDefaulter";
        if ("Fulfilled".equals(s.F) && "Outsider".equals(s.A)) return "MODE-ExternalTrustSpender";
        if ("Unfulfilled".equals(s.F) && "Indifferent".equals(s.Y)) return "MODE-InternalDestroyer";
        if (s.Z > 2.0) return "MODE-Fluctuating";
        return "MODE-StableDisciplined";
    }
    private static String determineColor(String p, int r) {
        if ("MODE-InternalDestroyer".equals(p)) return "🔴";
        if ("MODE-Fluctuating".equals(p) && r>3) return "🟡";
        if ("MODE-DefensiveDefaulter".equals(p) && r>2) return "🟡";
        return "🟢";
    }
    private static List<String> makeLabels(Signature s, String pat) {
        List<String> labels = new ArrayList<>();
        labels.add("P:" + s.P);
        labels.add("F:" + s.F);
        labels.add(pat);
        return labels;
    }
    private static String sha8(String s) {
        try { return java.security.MessageDigest.getInstance("SHA-256").digest(s.getBytes(java.nio.charset.StandardCharsets.UTF_8))[0]; } catch (Exception e) { return "00000000"; }
    }
    private static String sha16(String s) {
        try { return bytesToHex(java.security.MessageDigest.getInstance("SHA-256").digest(s.getBytes(java.nio.charset.StandardCharsets.UTF_8))).substring(0,16); } catch (Exception e) { return "0000000000000000"; }
    }
    private static String bytesToHex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }
    private static Map<String,Object> result(boolean ok, List<String> errs, List<String> warns) {
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("valid", ok);
        m.put("errors", errs);
        m.put("warnings", warns);
        m.put("summary", ok ? "✅ VALID" : "❌ INVALID " + errs.size() + " error(s)");
        return m;
    }
}
