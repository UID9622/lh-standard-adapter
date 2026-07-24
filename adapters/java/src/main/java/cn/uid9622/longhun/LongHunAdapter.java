package cn.uid9622.longhun;

import java.util.*;

/**
 * LongHun Standard Adapter — main entry point.
 */
public class LongHunAdapter {
    public final String uid;
    public final String device;
    public final String locale;
    private final DnaGenerator dnaGen;
    private final AuditWrapper audit;
    private final Validator validator;

    public LongHunAdapter(String uid, String device, String locale) {
        this.uid = uid;
        this.device = device;
        this.locale = locale;
        this.dnaGen = new DnaGenerator(uid, device, locale);
        this.audit = new AuditWrapper(uid);
        this.validator = new Validator();
    }

    public LongHunAdapter() { this("9622", "HM-9622-001", "Asia/Shanghai"); }

    public Map<String, Object> wrap(Object data, String taskType, String persona, String action, String version) {
        String dna = dnaGen.generate(taskType, action, version != null ? version : "V1.0");
        Map<String, Object> auditMap = audit.wrap(data, taskType, persona);

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("adapter_version", "1.0.0");
        meta.put("uid", uid);
        meta.put("device", device);
        meta.put("task_type", taskType);
        meta.put("persona", persona);
        meta.put("generated_at", java.time.ZonedDateTime.now(java.time.ZoneOffset.ofHours(8)).format(java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        meta.put("format", "longhun-v∞");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("dna", dna);
        result.put("audit", auditMap);
        result.put("payload", data);
        result.put("meta", meta);
        return result;
    }

    public Map<String, Object> validate(Map<String, Object> wrapped) {
        return validator.validate(wrapped);
    }

    public Map<String, Object> getSchemas() {
        return Map.of("dna_schema", Schemas.DNA_SCHEMA, "audit_schema", Schemas.AUDIT_SCHEMA);
    }
}
