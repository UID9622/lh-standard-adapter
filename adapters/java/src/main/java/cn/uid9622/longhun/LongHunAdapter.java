package cn.uid9622.longhun;

import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class LongHunAdapter {
    private final String uid, device, locale;
    private final DNAGenerator dnaGen;
    private final AuditWrapper audit;
    private final Validator validator;

    public LongHunAdapter(String uid, String device, String locale) {
        this.uid = uid != null ? uid : "9622";
        this.device = device != null ? device : "HM-9622-001";
        this.locale = locale != null ? locale : "Asia/Shanghai";
        this.dnaGen = new DNAGenerator(this.uid, this.device, this.locale);
        this.audit = new AuditWrapper(this.uid);
        this.validator = new Validator();
    }

    public Map<String,Object> wrap(Object data, String taskType, String persona, String action, String version) {
        if (taskType == null || taskType.isEmpty()) taskType = "default";
        if (persona == null || persona.isEmpty()) persona = "P04";
        if (action == null || action.isEmpty()) action = "WRAP";
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("dna", dnaGen.generate(taskType, action, version));
        result.put("audit", audit.wrap(data, taskType, persona));
        result.put("payload", data);
        Map<String,Object> meta = new LinkedHashMap<>();
        meta.put("adapter_version","1.0.0");
        meta.put("uid",uid);
        meta.put("device",device);
        meta.put("task_type",taskType);
        meta.put("persona",persona);
        meta.put("generated_at", ZonedDateTime.now(ZoneOffset.UTC).toString());
        meta.put("format","longhun-v∞");
        result.put("meta", meta);
        return result;
    }

    public ValidationResult validate(Map<String,Object> wrapped) { return validator.validate(wrapped); }

    public Map<String,Object> getSchemas() {
        Map<String,Object> schemas = new LinkedHashMap<>();
        schemas.put("dnaSchema", Map.of("type","string","description","v∞ DNA traceability code"));
        schemas.put("auditSchema", Map.of("type","object","required",
            List.of("audit_version","uid","behavior_signature","behavior_pattern","behavior_labels","color")));
        return schemas;
    }
}
