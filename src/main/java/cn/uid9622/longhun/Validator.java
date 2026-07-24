package cn.uid9622.longhun;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Validator for DNA and audit format validation.
 * Validates wrapped payloads for LongHun standard compliance.
 */
public class Validator {

    // DNA v∞ validation regex
    private static final Pattern DNA_REGEX = Pattern.compile(
            "^#LongHun\u26A1\uFE0F" +
                    "([A-Z][a-zA-Z]+)\u00B7([A-Z][a-zA-Z]+)\u00B7([A-Z][a-zA-Z]+)\u00B7([A-Z][a-zA-Z]+)" +
                    "\u00B7([\u4DC0-\u4DCD][A-Za-z]+)" +
                    "-(.+)" +
                    "-([a-f0-9]{8})$"
    );

    private static final Set<String> REQUIRED_TOP_KEYS = new HashSet<>(Arrays.asList("dna", "audit", "payload", "meta"));
    private static final Set<String> REQUIRED_AUDIT_KEYS = new HashSet<>(Arrays.asList(
            "audit_version", "uid", "behavior_signature", "behavior_pattern", "behavior_labels", "color"
    ));
    private static final Set<String> REQUIRED_SIG_KEYS = new HashSet<>(Arrays.asList(
            "P", "F", "T", "E", "C", "R", "A", "X", "Y", "Z"
    ));
    private static final Set<String> VALID_COLORS = new HashSet<>(Arrays.asList("\uD83D\uDFE2", "\uD83D\uDFE1", "\uD83D\uDD34"));
    private static final Set<String> VALID_PATTERNS = AuditWrapper.getValidPatterns();

    private final List<String> errors = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();

    /**
     * Validate a wrapped payload.
     *
     * @param wrapped Map produced by LongHunAdapter.wrap()
     * @return Map with keys: valid, errors, warnings, summary
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> validate(Map<String, Object> wrapped) {
        errors.clear();
        warnings.clear();

        if (wrapped == null || wrapped.isEmpty()) {
            errors.add("Input is not a non-empty map");
            return result();
        }

        // 1. Top-level keys
        Set<String> missing = new HashSet<>(REQUIRED_TOP_KEYS);
        missing.removeAll(wrapped.keySet());
        if (!missing.isEmpty()) {
            errors.add("Missing top-level keys: " + missing);
        }

        // 2. DNA validation
        String dna = (String) wrapped.getOrDefault("dna", "");
        if (dna.isEmpty()) {
            errors.add("DNA field is empty");
        } else {
            if (!DNA_REGEX.matcher(dna).matches()) {
                String truncated = dna.length() > 60 ? dna.substring(0, 60) + "..." : dna;
                errors.add("DNA does not match regex: " + truncated);
            }
            // hash8 check is done by regex
        }

        // 3. Audit validation
        Map<String, Object> audit = (Map<String, Object>) wrapped.get("audit");
        if (audit == null) {
            errors.add("Audit is not a map");
        } else {
            validateAudit(audit);

            // 4. UID consistency check
            Map<String, Object> meta = (Map<String, Object>) wrapped.get("meta");
            if (meta != null) {
                String metaUid = (String) meta.get("uid");
                String auditUid = (String) audit.get("uid");
                if (metaUid != null && auditUid != null && !metaUid.isEmpty() && !auditUid.isEmpty()) {
                    String auditUidClean = auditUid.replace("UID", "");
                    if (!metaUid.equals(auditUidClean)) {
                        errors.add("UID mismatch: meta.uid=" + metaUid + ", audit.uid=" + auditUid);
                    }
                }
            }
        }

        return result();
    }

    @SuppressWarnings("unchecked")
    private void validateAudit(Map<String, Object> audit) {
        // Required keys
        Set<String> missingAudit = new HashSet<>(REQUIRED_AUDIT_KEYS);
        missingAudit.removeAll(audit.keySet());
        if (!missingAudit.isEmpty()) {
            errors.add("Missing audit keys: " + missingAudit);
        }

        // behavior_signature
        Map<String, Object> sig = (Map<String, Object>) audit.get("behavior_signature");
        if (sig == null) {
            errors.add("behavior_signature is not a map");
        } else {
            Set<String> missingSig = new HashSet<>(REQUIRED_SIG_KEYS);
            missingSig.removeAll(sig.keySet());
            if (!missingSig.isEmpty()) {
                errors.add("Missing signature keys: " + missingSig);
            } else {
                validateSigValues(sig);
            }
        }

        // pattern
        String pattern = (String) audit.get("behavior_pattern");
        if (pattern != null && !pattern.isEmpty() && !VALID_PATTERNS.contains(pattern)) {
            warnings.add("Unknown behavior pattern: " + pattern);
        }

        // color
        String color = (String) audit.get("color");
        if (color != null && !color.isEmpty() && !VALID_COLORS.contains(color)) {
            warnings.add("Unknown audit color: " + color);
        }

        // payload_hash
        String ph = (String) audit.get("payload_hash");
        if (ph != null && !ph.isEmpty()) {
            if (ph.length() != 16 || !ph.matches("^[a-f0-9]{16}$")) {
                warnings.add("Suspicious payload_hash: " + ph);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void validateSigValues(Map<String, Object> sig) {
        Object pVal = sig.get("P");
        if (pVal instanceof String && !AuditWrapper.getPValues().contains(pVal)) {
            warnings.add("Invalid P: '" + pVal + "'");
        }
        Object fVal = sig.get("F");
        if (fVal instanceof String && !AuditWrapper.getFValues().contains(fVal)) {
            warnings.add("Invalid F: '" + fVal + "'");
        }
        if (!(sig.get("T") instanceof Number)) {
            warnings.add("Invalid T (number)");
        }
        Object eVal = sig.get("E");
        if (eVal instanceof String && !AuditWrapper.getEValues().contains(eVal)) {
            warnings.add("Invalid E: '" + eVal + "'");
        }
        if (!(sig.get("C") instanceof Number)) {
            warnings.add("Invalid C (number)");
        }
        Object rVal = sig.get("R");
        if (rVal instanceof Number && ((Number) rVal).intValue() < 0) {
            warnings.add("Invalid R (int >= 0)");
        }
        Object aVal = sig.get("A");
        if (aVal instanceof String && !AuditWrapper.getAValues().contains(aVal)) {
            warnings.add("Invalid A: '" + aVal + "'");
        }
        Object xVal = sig.get("X");
        if (xVal instanceof String && !AuditWrapper.getXValues().contains(xVal)) {
            warnings.add("Invalid X: '" + xVal + "'");
        }
        Object yVal = sig.get("Y");
        if (yVal instanceof String && !AuditWrapper.getYValues().contains(yVal)) {
            warnings.add("Invalid Y: '" + yVal + "'");
        }
        if (!(sig.get("Z") instanceof Number)) {
            warnings.add("Invalid Z (number)");
        }
    }

    private Map<String, Object> result() {
        boolean valid = errors.isEmpty();
        String summary;
        if (valid) {
            summary = "\u2705 VALID \u2014 " + warnings.size() + " warning(s)";
            if (!warnings.isEmpty()) {
                summary += " (" + String.join(", ", warnings.subList(0, Math.min(2, warnings.size()))) + ")";
            }
        } else {
            summary = "\u274C INVALID \u2014 " + errors.size() + " error(s)";
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("valid", valid);
        result.put("errors", new ArrayList<>(errors));
        result.put("warnings", new ArrayList<>(warnings));
        result.put("summary", summary);
        return result;
    }

    /**
     * Quick check: has required keys and valid DNA format?
     */
    public static boolean quickValidate(Map<String, Object> wrapped) {
        if (wrapped == null) return false;
        if (!wrapped.containsKey("dna") || !wrapped.containsKey("audit")) return false;
        String dna = (String) wrapped.get("dna");
        return dna != null && DNA_REGEX.matcher(dna).matches();
    }
}