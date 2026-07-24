package cn.uid9622.longhun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Validator {
    public static final Pattern DNA_REGEX = Pattern.compile("^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([\\u4e00-\\u9fa5\\u2df0-\\u2dffA-Za-z]+)-(.+)-([a-f0-9]{8})$");

    public Map<String, Object> validate(Map<String, Object> wrapped) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        if (wrapped == null || wrapped.isEmpty()) {
            errors.add("Input is empty or null");
            return makeResult(errors, warnings);
        }

        String dna = (String) wrapped.get("dna");
        if (dna == null || dna.isEmpty()) {
            errors.add("DNA field is empty");
        } else if (!DNA_REGEX.matcher(dna).matches()) {
            errors.add("DNA does not match regex: " + dna);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> audit = (Map<String, Object>) wrapped.get("audit");
        if (audit != null) {
            if (!audit.containsKey("audit_version")) errors.add("Missing audit_version");
            if (!audit.containsKey("uid")) errors.add("Missing audit.uid");
        } else {
            errors.add("Audit object is missing");
        }

        return makeResult(errors, warnings);
    }

    private Map<String, Object> makeResult(List<String> errors, List<String> warnings) {
        boolean valid = errors.isEmpty();
        String summary = valid ? String.format("✅ VALID — %d warning(s)", warnings.size())
                               : String.format("❌ INVALID — %d error(s)", errors.size());

        Map<String, Object> res = new HashMap<>();
        res.put("valid", valid);
        res.put("errors", errors);
        res.put("warnings", warnings);
        res.put("summary", summary);
        return res;
    }
}
