package cn.uid9622.longhun;

import java.util.*;
import java.util.regex.Pattern;

public class Validator {
    private static final Pattern DNA_REGEX = Pattern.compile(
        "^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([\\u4DC0-\\u4DFF][A-Za-z]+)-(.+)-([a-f0-9]{8})$");
    private static final Set<String> REQUIRED_TOP = Set.of("dna","audit","payload","meta");
    private static final Set<String> REQUIRED_AUDIT = Set.of("audit_version","uid","behavior_signature","behavior_pattern","behavior_labels","color");
    private static final Set<String> REQUIRED_SIG = Set.of("P","F","T","E","C","R","A","X","Y","Z");

    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    public ValidationResult validate(Map<String,Object> wrapped) {
        errors.clear(); warnings.clear();
        if (wrapped == null || wrapped.isEmpty()) { errors.add("Input null/empty"); return result(); }

        for (String k : REQUIRED_TOP) if (!wrapped.containsKey(k)) errors.add("Missing top key: "+k);

        Object dnaObj = wrapped.get("dna");
        if (dnaObj == null || "".equals(dnaObj)) errors.add("DNA empty");
        else if (!DNA_REGEX.matcher((String)dnaObj).matches())
            errors.add("DNA regex fail: "+((String)dnaObj).substring(0, Math.min(60, ((String)dnaObj).length())));

        Object auditObj = wrapped.get("audit");
        if (!(auditObj instanceof Map)) errors.add("Audit not object");
        else validateAudit((Map<String,Object>)auditObj);

        return result();
    }

    private void validateAudit(Map<String,Object> audit) {
        for (String k : REQUIRED_AUDIT) if (!audit.containsKey(k)) errors.add("Missing audit key: "+k);
        Object sigObj = audit.get("behavior_signature");
        if (!(sigObj instanceof Map)) errors.add("sig not object");
        else {
            Map<String,Object> sig = (Map<String,Object>)sigObj;
            for (String k : REQUIRED_SIG) if (!sig.containsKey(k)) errors.add("Missing sig key: "+k);
            validateSigValues(sig);
        }
    }

    private void validateSigValues(Map<String,Object> sig) {
        Set<String> vp = Set.of("HasPromise","NoPromise");
        Set<String> vf = Set.of("Fulfilled","Unfulfilled","Partial");
        Set<String> ve = Set.of("Willing","Perfunctory","Resentful","Numb");
        Set<String> va = Set.of("Self","Partner","Family","Outsider","Public");
        Set<String> vx = Set.of("OverExplain","Silent","Genuine","Indifferent");
        Set<String> vy = Set.of("Changed","Resisted","Indifferent","NoResponse");

        if (!vp.contains(sig.get("P"))) warnings.add("Invalid P: "+sig.get("P"));
        if (!vf.contains(sig.get("F"))) warnings.add("Invalid F: "+sig.get("F"));
        if (!ve.contains(sig.get("E"))) warnings.add("Invalid E: "+sig.get("E"));
        if (!va.contains(sig.get("A"))) warnings.add("Invalid A: "+sig.get("A"));
        if (!vx.contains(sig.get("X"))) warnings.add("Invalid X: "+sig.get("X"));
        if (!vy.contains(sig.get("Y"))) warnings.add("Invalid Y: "+sig.get("Y"));
    }

    private ValidationResult result() {
        boolean valid = errors.isEmpty();
        return new ValidationResult(valid, errors, warnings, valid ? "✅ VALID" : "❌ INVALID");
    }
}
