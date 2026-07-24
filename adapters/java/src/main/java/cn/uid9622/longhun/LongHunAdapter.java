package cn.uid9622.longhun;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LongHunAdapter {
    private final String uid;
    private final String device;
    private final DNAGenerator dnaGenerator;
    private final AuditWrapper auditWrapper;
    private final Validator validator;

    public LongHunAdapter(String uid, String device) {
        this.uid = uid != null ? uid : "9622";
        this.device = device != null ? device : "HM-9622-001";
        this.dnaGenerator = new DNAGenerator(this.uid, this.device);
        this.auditWrapper = new AuditWrapper(this.uid);
        this.validator = new Validator();
    }

    public Map<String, Object> wrap(Object data, String taskType, String persona) {
        String dna = dnaGenerator.generate(taskType, "WRAP", "V1.0");
        Map<String, Object> audit = auditWrapper.wrap(data, taskType, persona);

        Map<String, Object> meta = Map.of(
            "uid", uid,
            "device", device,
            "version", "V1.0"
        );

        Map<String, Object> wrapped = new HashMap<>();
        wrapped.put("dna", dna);
        wrapped.put("audit", audit);
        wrapped.put("payload", data);
        wrapped.put("meta", meta);

        return wrapped;
    }

    public Map<String, Object> validate(Map<String, Object> wrapped) {
        return validator.validate(wrapped);
    }

    public Map<String, Object> getSchemas() {
        return Map.of(
            "dnaSchema", Map.of("type", "string", "pattern", "^#LongHun⚡️.*"),
            "auditSchema", Map.of("type", "object", "required", List.of("audit_version", "uid", "behavior_signature", "behavior_pattern", "behavior_labels", "color"))
        );
    }
}
