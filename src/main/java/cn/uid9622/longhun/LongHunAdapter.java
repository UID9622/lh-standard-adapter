package cn.uid9622.longhun;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * LongHun Standard Adapter — wrap JSON payloads with DNA traceability
 * and seven-factor behavioral audit metadata.
 * <p>
 * Usage:
 * <pre>
 * LongHunAdapter adapter = new LongHunAdapter("9622", "HM-9622-001");
 * Map&lt;String, Object&gt; result = adapter.wrap(data, "code", "P04-Luban", "WRAP", null);
 * Map&lt;String, Object&gt; validation = adapter.validate(result);
 * </pre>
 */
public class LongHunAdapter {

    public static final String VERSION = "1.0.0";
    public static final String AUTHOR = "LongHun Core \u00B7 UID9622 \u00B7 \u9F8D\u82AF\u5317\u8FB0";
    public static final String LICENSE = "CC BY-NC-SA 4.0";
    public static final String DNA = "#LongHun\u26A1\uFE0FBingWu\u00B7GuiWei\u00B7JiaZi\u00B7ZiShi\u00B7\u4DCCJiJi-ADAPTER-v1.0.0-4f7a3b1c";

    private final String uid;
    private final String device;
    private final String locale;
    private final DNAGenerator dnaGen;
    private final AuditWrapper audit;
    private final Validator validator;
    private final ObjectMapper objectMapper;

    public LongHunAdapter() {
        this("9622", "HM-9622-001", "Asia/Shanghai");
    }

    public LongHunAdapter(String uid, String device) {
        this(uid, device, "Asia/Shanghai");
    }

    public LongHunAdapter(String uid, String device, String locale) {
        this.uid = uid;
        this.device = device;
        this.locale = locale;
        this.dnaGen = new DNAGenerator(uid, device);
        this.audit = new AuditWrapper(uid);
        this.validator = new Validator();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    }

    /**
     * Wrap a payload with DNA traceability and audit metadata.
     *
     * @param data     Raw payload (any JSON-serializable object)
     * @param taskType Task category (code, deploy, audit, default)
     * @param persona  Persona identifier (P04-Luban, P00-Wenxin, etc.)
     * @param action   Action descriptor (WRAP, GENERATE, DEPLOY, AUDIT)
     * @param version  Optional version override
     * @return Map with keys: dna, audit, payload, meta
     */
    public Map<String, Object> wrap(Object data, String taskType, String persona,
                                    String action, String version) {
        // Generate DNA
        String dna = dnaGen.generate(taskType, action, version);

        // Generate audit wrapper
        Map<String, Object> auditMap = audit.wrap(data, taskType, persona);

        // Build meta
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("adapter_version", VERSION);
        meta.put("uid", uid);
        meta.put("device", device);
        meta.put("task_type", taskType != null ? taskType : "default");
        meta.put("persona", persona != null ? persona : "P04");
        meta.put("generated_at", now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        meta.put("format", "longhun-v\u221E");

        // Build result
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("dna", dna);
        result.put("audit", auditMap);
        result.put("payload", data);
        result.put("meta", meta);

        return result;
    }

    /**
     * Convenience wrap with defaults.
     */
    public Map<String, Object> wrap(Object data, String taskType, String persona) {
        return wrap(data, taskType, persona, "WRAP", null);
    }

    /**
     * Validate a wrapped payload.
     *
     * @param wrapped Map produced by wrap()
     * @return Map with keys: valid, errors, warnings, summary
     */
    public Map<String, Object> validate(Map<String, Object> wrapped) {
        return validator.validate(wrapped);
    }

    /**
     * Get JSON Schemas for DNA and Audit formats.
     *
     * @return Map with keys: dna_schema, audit_schema
     */
    public Map<String, Object> getSchemas() {
        Map<String, Object> schemas = new LinkedHashMap<>();

        // DNA Schema
        Map<String, Object> dnaSchema = new LinkedHashMap<>();
        dnaSchema.put("$schema", "https://json-schema.org/draft/2020-12/schema");
        dnaSchema.put("$id", "https://uid9622.cn/schemas/dna-v1.0.json");
        dnaSchema.put("title", "LongHun DNA Traceability Code");
        dnaSchema.put("description", "Schema for validating LongHun v\u221E DNA traceability codes.");
        dnaSchema.put("type", "object");
        dnaSchema.put("required", Arrays.asList("dna", "format", "uid", "timestamp"));
        schemas.put("dna_schema", dnaSchema);

        // Audit Schema
        Map<String, Object> auditSchema = new LinkedHashMap<>();
        auditSchema.put("$schema", "https://json-schema.org/draft/2020-12/schema");
        auditSchema.put("$id", "https://uid9622.cn/schemas/audit-v1.0.json");
        auditSchema.put("title", "LongHun Audit Record");
        auditSchema.put("description", "Schema for validating LongHun seven-factor behavioral audit records.");
        auditSchema.put("type", "object");
        auditSchema.put("required", Arrays.asList("dna", "audit", "payload", "meta"));
        schemas.put("audit_schema", auditSchema);

        return schemas;
    }

    public String getUid() {
        return uid;
    }

    public String getDevice() {
        return device;
    }
}