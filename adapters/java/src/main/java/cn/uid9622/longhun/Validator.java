package cn.uid9622.longhun;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Validator — DNA and audit format validation.
 */
public class Validator {

    private static final Pattern DNA_REGEX = Pattern.compile(
        "^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([\u4D00-\u4DFF][A-Za-z]+)-(.+)-([a-f0-9]{8})$"
    );

    private static final Set<String> REQUIRED_TOP_KEYS = Set.of("dna", "audit", "payload", "meta");
    private static final Set<String> REQUIRED_AUDIT_KEYS = Set.of("audit_version", "uid", "behavior_signature", "behavior_pattern", "behavior_labels", "color");
    private static final Set<String> REQUIRED_SIG_KEYS = Set.of("P", "F", "T", "E", "C", "R", "A", "X", "Y", "Z");
    private static final Set<String> VALID_PATTERNS = Set.of("MODE-DefensiveDefaulter", "MODE-ExternalTrustSpender", "MODE-InternalDestroyer", "MODE-Fluctuating", "MODE-StableDisciplined");

    private final List<String> errors = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();

    public Map<String, Object> validate(Map<String, Object> wrapped) {
        errors.clear();
        warnings.clear();

        if (wrapped == null || wrapped.isEmpty()) {
            errors.add("Input is not a non-empty object");
            return result();
        }

        // Top-level keys
        for (String k : REQUIRED_TOP_KEYS) {
            if (!wrapped.containsKey(k)) errors.add("Missing top-level key: " + k);
        }

        // DNA
        Object dnaObj = wrapped.get("dna");
        if (dnaObj instanceof String dna) {
            if (dna.isEmpty()) {
                errors.add("DNA field is empty");
            } else if (!DNA_REGEX.matcher(dna).matches()) {
                String short_ = dna.length() > 60 ? dna.substring(0, 60) : dna;
                errors.add("DNA does not match regex: " + short_ + "...");
            }
        } else {
            errors.add("DNA is not a string");
        }

        // Audit
        Object auditObj = wrapped.get("audit");
        if (auditObj instanceof Map<?, ?> audit) {
            validateAudit((Map<String, Object>) audit);
            // UID consistency
            Object metaObj = wrapped.get("meta");
            if (metaObj instanceof Map<?, ?> meta) {
                String metaUid = String.valueOf(((Map<String, Object>) meta).getOrDefault("uid", ""));
                String auditUid = String.valueOf(audit.getOrDefault("uid", ""));
                if (!metaUid.isEmpty() && !auditUid.isEmpty()) {
                    String clean = auditUid.startsWith("UID") ? auditUid.substring(3) : auditUid;
                    if (!metaUid.equals(clean)) {
                        errors.add("UID mismatch: meta.uid=" + metaUid + ", audit.uid=" + auditUid);
                    }
                }
            }
        } else {
            errors.add("Audit is not an object");
        }

        return result();
    }

    @SuppressWarnings("unchecked")
    private void validateAudit(Map<String, Object> audit) {
        for (String k : REQUIRED_AUDIT_KEYS) {
            if (!audit.containsKey(k)) errors.add("Missing audit key: " + k);
        }
        Object sigObj = audit.get("behavior_signature");
        if (sigObj instanceof Map<?, ?> sig) {
            Map<String, Object> sigMap = (Map<String, Object>) sig;
            for (String k : REQUIRED_SIG_KEYS) {
                if (!sigMap.containsKey(k)) errors.add("Missing signature key: " + k);
            }
            validateSigValues(sigMap);
        } else {
            errors.add("behavior_signature is not an object");
        }

        Object p = audit.get("behavior_pattern");
        if (p instanceof String ps && !VALID_PATTERNS.contains(ps)) warnings.add("Unknown behavior pattern: " + ps);

        Object c = audit.get("color");
        if (c instanceof String cs && !Set.of("🟢", "🟡", "🔴").contains(cs)) warnings.add("Unknown color: " + cs);

        Object ph = audit.get("payload_hash");
        if (ph instanceof String phs && (phs.length() != 16 || !phs.matches("[a-f0-9]+"))) warnings.add("Suspicious payload_hash: " + phs);
    }

    private void validateSigValues(Map<String, Object> sig) {
        for (var e : sig.entrySet()) {
            String k = e.getKey();
            Object v = e.getValue();
            switch (k) {
                case "P": if (!(v instanceof String s && List.of("HasPromise","NoPromise").contains(s))) warnings.add("Invalid P: " + v); break;
                case "F": if (!(v instanceof String s && List.of("Fulfilled","Unfulfilled","Partial").contains(s))) warnings.add("Invalid F: " + v); break;
                case "T": if (!(v instanceof Number)) warnings.add("Invalid T (not number): " + v); break;
                case "E": if (!(v instanceof String s && List.of("Willing","Perfunctory","Resentful","Numb").contains(s))) warnings.add("Invalid E: " + v); break;
                case "C": if (!(v instanceof Number)) warnings.add("Invalid C: " + v); break;
                case "R": if (!(v instanceof Number n && n.intValue() >= 0)) warnings.add("Invalid R: " + v); break;
                case "A": if (!(v instanceof String s && List.of("Self","Partner","Family","Outsider","Public").contains(s))) warnings.add("Invalid A: " + v); break;
                case "X": if (!(v instanceof String s && List.of("OverExplain","Silent","Genuine","Indifferent").contains(s))) warnings.add("Invalid X: " + v); break;
                case "Y": if (!(v instanceof String s && List.of("Changed","Resisted","Indifferent","NoResponse").contains(s))) warnings.add("Invalid Y: " + v); break;
                case "Z": if (!(v instanceof Number)) warnings.add("Invalid Z: " + v); break;
            }
        }
    }

    private Map<String, Object> result() {
        boolean valid = errors.isEmpty();
        String summary = valid
            ? (warnings.isEmpty() ? "✅ VALID — 0 warnings" : "✅ VALID — " + warnings.size() + " warning(s) (" + warnings.get(0) + ")")
            : "❌ INVALID — " + errors.size() + " error(s)";
        return Map.of("valid", valid, "errors", List.copyOf(errors), "warnings", List.copyOf(warnings), "summary", summary);
    }

    public static boolean quickValidate(Map<String, Object> wrapped) {
        if (wrapped == null) return false;
        if (!wrapped.containsKey("dna") || !wrapped.containsKey("audit")) return false;
        Object dna = wrapped.get("dna");
        return dna instanceof String s && DNA_REGEX.matcher(s).matches();
    }
}
