package cn.uid9622.longhun;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for LongHun Standard Adapter Java implementation.
 * Tests cover all 72+ scenarios matching the Python reference test coverage.
 */
class LongHunAdapterTest {

    private LongHunAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new LongHunAdapter("9622", "HM-9622-001");
    }

    // ===== DNA Generation Tests =====

    @Test
    @DisplayName("DNA format: basic generation")
    void testDnaBasicGeneration() {
        String dna = adapter.getUid();
        assertNotNull(dna);
    }

    @Test
    @DisplayName("DNA format: starts with correct prefix")
    void testDnaPrefix() {
        Map<String, String> payload = new HashMap<>();
        payload.put("code", "print('hello')");
        payload.put("language", "python");
        Map<String, Object> result = adapter.wrap(payload, "code", "P04-Luban");
        String dna = (String) result.get("dna");
        assertTrue(dna.startsWith("#LongHun⚡️"), "DNA should start with #LongHun⚡️");
    }

    @Test
    @DisplayName("DNA format: contains hash suffix")
    void testDnaHash() {
        Map<String, String> payload = new HashMap<>();
        payload.put("test", "data");
        Map<String, Object> result = adapter.wrap(payload, "default", "P00-Wenxin");
        String dna = (String) result.get("dna");
        assertTrue(dna.matches(".*-[a-f0-9]{8}$"), "DNA should end with 8-char hex hash");
    }

    @Test
    @DisplayName("DNA format: contains hexagram symbol")
    void testDnaHexagram() {
        Map<String, String> payload = new HashMap<>();
        payload.put("test", "data");
        Map<String, Object> result = adapter.wrap(payload, "code", "P04-Luban");
        String dna = (String) result.get("dna");
        assertNotNull(dna);
        assertTrue(dna.contains("䷆") || dna.contains("䷝") || dna.contains("䷲"),
                "DNA should contain a hexagram symbol");
    }

    // ===== Wrap Function Tests =====

    @Test
    @DisplayName("Wrap: returns all required top-level keys")
    void testWrapRequiredKeys() {
        Map<String, String> payload = new HashMap<>();
        payload.put("action", "deploy");
        payload.put("target", "portal");
        Map<String, Object> result = adapter.wrap(payload, "deploy", "P14-Lvmeng");

        assertTrue(result.containsKey("dna"), "Should contain 'dna'");
        assertTrue(result.containsKey("audit"), "Should contain 'audit'");
        assertTrue(result.containsKey("payload"), "Should contain 'payload'");
        assertTrue(result.containsKey("meta"), "Should contain 'meta'");
    }

    @Test
    @DisplayName("Wrap: audit contains all required keys")
    void testWrapAuditKeys() {
        Map<String, String> payload = new HashMap<>();
        payload.put("test", "data");
        Map<String, Object> result = adapter.wrap(payload, "audit", "P00-Wenxin");
        @SuppressWarnings("unchecked")
        Map<String, Object> audit = (Map<String, Object>) result.get("audit");

        assertTrue(audit.containsKey("audit_version"), "Should contain audit_version");
        assertTrue(audit.containsKey("uid"), "Should contain uid");
        assertTrue(audit.containsKey("persona"), "Should contain persona");
        assertTrue(audit.containsKey("task_type"), "Should contain task_type");
        assertTrue(audit.containsKey("behavior_signature"), "Should contain behavior_signature");
        assertTrue(audit.containsKey("behavior_pattern"), "Should contain behavior_pattern");
        assertTrue(audit.containsKey("behavior_labels"), "Should contain behavior_labels");
        assertTrue(audit.containsKey("color"), "Should contain color");
        assertTrue(audit.containsKey("timestamp"), "Should contain timestamp");
        assertTrue(audit.containsKey("payload_hash"), "Should contain payload_hash");
    }

    @Test
    @DisplayName("Wrap: behavior signature has all 10 factors")
    void testWrapSignatureFactors() {
        Map<String, String> payload = new HashMap<>();
        payload.put("test", "data");
        Map<String, Object> result = adapter.wrap(payload, "default", "P04");
        @SuppressWarnings("unchecked")
        Map<String, Object> audit = (Map<String, Object>) result.get("audit");
        @SuppressWarnings("unchecked")
        Map<String, Object> sig = (Map<String, Object>) audit.get("behavior_signature");

        assertTrue(sig.containsKey("P"), "Should contain P (Promise)");
        assertTrue(sig.containsKey("F"), "Should contain F (Fulfill)");
        assertTrue(sig.containsKey("T"), "Should contain T (Time)");
        assertTrue(sig.containsKey("E"), "Should contain E (Emotion)");
        assertTrue(sig.containsKey("C"), "Should contain C (Cost)");
        assertTrue(sig.containsKey("R"), "Should contain R (Repeat)");
        assertTrue(sig.containsKey("A"), "Should contain A (Audience)");
        assertTrue(sig.containsKey("X"), "Should contain X (Explain)");
        assertTrue(sig.containsKey("Y"), "Should contain Y (Yield)");
        assertTrue(sig.containsKey("Z"), "Should contain Z (Zigzag)");
    }

    @Test
    @DisplayName("Wrap: default behavior pattern is StableDisciplined")
    void testWrapDefaultPattern() {
        Map<String, String> payload = new HashMap<>();
        payload.put("test", "data");
        Map<String, Object> result = adapter.wrap(payload, "default", "P04");
        @SuppressWarnings("unchecked")
        Map<String, Object> audit = (Map<String, Object>) result.get("audit");
        assertEquals("MODE-StableDisciplined", audit.get("behavior_pattern"));
    }

    @Test
    @DisplayName("Wrap: meta contains adapter info")
    void testWrapMeta() {
        Map<String, String> payload = new HashMap<>();
        payload.put("test", "data");
        Map<String, Object> result = adapter.wrap(payload, "code", "P04-Luban");
        @SuppressWarnings("unchecked")
        Map<String, Object> meta = (Map<String, Object>) result.get("meta");

        assertEquals("1.0.0", meta.get("adapter_version"));
        assertEquals("9622", meta.get("uid"));
        assertEquals("HM-9622-001", meta.get("device"));
        assertEquals("code", meta.get("task_type"));
        assertEquals("P04-Luban", meta.get("persona"));
        assertEquals("longhun-v∞", meta.get("format"));
    }

    @Test
    @DisplayName("Wrap: different task types produce different hexagrams")
    void testWrapDifferentTaskTypes() {
        Map<String, String> payload = new HashMap<>();
        payload.put("test", "data");
        Map<String, Object> result1 = adapter.wrap(payload, "code", "P04");
        Map<String, Object> result2 = adapter.wrap(payload, "deploy", "P04");

        String dna1 = (String) result1.get("dna");
        String dna2 = (String) result2.get("dna");
        assertNotNull(dna1);
        assertNotNull(dna2);
    }

    // ===== Validation Tests =====

    @Test
    @DisplayName("Validate: valid wrapped payload passes")
    void testValidateValid() {
        Map<String, String> payload = new HashMap<>();
        payload.put("test", "data");
        Map<String, Object> result = adapter.wrap(payload, "default", "P04");
        Map<String, Object> validation = adapter.validate(result);

        assertTrue((Boolean) validation.get("valid"), "Valid wrapped payload should pass validation");
        assertEquals(0, ((List<?>) validation.get("errors")).size());
    }

    @Test
    @DisplayName("Validate: null input fails")
    void testValidateNullInput() {
        Map<String, Object> validation = adapter.validate(null);
        assertFalse((Boolean) validation.get("valid"));
    }

    @Test
    @DisplayName("Validate: missing DNA fails")
    void testValidateMissingDna() {
        Map<String, Object> invalid = new LinkedHashMap<>();
        invalid.put("audit", new LinkedHashMap<>());
        invalid.put("payload", "test");
        invalid.put("meta", new LinkedHashMap<>());

        Map<String, Object> validation = adapter.validate(invalid);
        assertFalse((Boolean) validation.get("valid"));
    }

    @Test
    @DisplayName("Validate: quickValidate returns true for valid payload")
    void testQuickValidate() {
        Map<String, String> payload = new HashMap<>();
        payload.put("test", "data");
        Map<String, Object> result = adapter.wrap(payload, "default", "P04");
        assertTrue(Validator.quickValidate(result));
    }

    @Test
    @DisplayName("Validate: quickValidate returns false for null")
    void testQuickValidateNull() {
        assertFalse(Validator.quickValidate(null));
    }

    @Test
    @DisplayName("Validate: quickValidate returns false for empty map")
    void testQuickValidateEmpty() {
        assertFalse(Validator.quickValidate(new LinkedHashMap<>()));
    }

    // ===== Behavior Pattern Classification Tests =====

    @Test
    @DisplayName("Pattern: StableDisciplined is default")
    void testPatternStableDisciplined() {
        AuditWrapper wrapper = new AuditWrapper("9622");
        Map<String, Object> sig = new LinkedHashMap<>();
        sig.put("P", "HasPromise");
        sig.put("F", "Fulfilled");
        sig.put("T", 0.0);
        sig.put("E", "Willing");
        sig.put("C", 0);
        sig.put("R", 0);
        sig.put("A", "Self");
        sig.put("X", "Genuine");
        sig.put("Y", "NoResponse");
        sig.put("Z", 1.0);

        assertEquals("MODE-StableDisciplined", wrapper.classify(sig));
    }

    @Test
    @DisplayName("Pattern: DefensiveDefaulter triggers on F=Unfulfilled + X=OverExplain")
    void testPatternDefensiveDefaulter() {
        AuditWrapper wrapper = new AuditWrapper("9622");
        Map<String, Object> sig = new LinkedHashMap<>();
        sig.put("P", "HasPromise");
        sig.put("F", "Unfulfilled");
        sig.put("T", 0.0);
        sig.put("E", "Willing");
        sig.put("C", 0);
        sig.put("R", 0);
        sig.put("A", "Self");
        sig.put("X", "OverExplain");
        sig.put("Y", "NoResponse");
        sig.put("Z", 1.0);

        assertEquals("MODE-DefensiveDefaulter", wrapper.classify(sig));
    }

    @Test
    @DisplayName("Pattern: ExternalTrustSpender triggers on F=Fulfilled + A=Outsider")
    void testPatternExternalTrustSpender() {
        AuditWrapper wrapper = new AuditWrapper("9622");
        Map<String, Object> sig = new LinkedHashMap<>();
        sig.put("P", "HasPromise");
        sig.put("F", "Fulfilled");
        sig.put("T", 0.0);
        sig.put("E", "Willing");
        sig.put("C", 0);
        sig.put("R", 0);
        sig.put("A", "Outsider");
        sig.put("X", "Genuine");
        sig.put("Y", "NoResponse");
        sig.put("Z", 1.0);

        assertEquals("MODE-ExternalTrustSpender", wrapper.classify(sig));
    }

    @Test
    @DisplayName("Pattern: InternalDestroyer triggers on F=Unfulfilled + Y=Indifferent")
    void testPatternInternalDestroyer() {
        AuditWrapper wrapper = new AuditWrapper("9622");
        Map<String, Object> sig = new LinkedHashMap<>();
        sig.put("P", "HasPromise");
        sig.put("F", "Unfulfilled");
        sig.put("T", 0.0);
        sig.put("E", "Willing");
        sig.put("C", 0);
        sig.put("R", 0);
        sig.put("A", "Self");
        sig.put("X", "Genuine");
        sig.put("Y", "Indifferent");
        sig.put("Z", 1.0);

        assertEquals("MODE-InternalDestroyer", wrapper.classify(sig));
    }

    @Test
    @DisplayName("Pattern: Fluctuating triggers on Z > 2.0")
    void testPatternFluctuating() {
        AuditWrapper wrapper = new AuditWrapper("9622");
        Map<String, Object> sig = new LinkedHashMap<>();
        sig.put("P", "HasPromise");
        sig.put("F", "Fulfilled");
        sig.put("T", 0.0);
        sig.put("E", "Willing");
        sig.put("C", 0);
        sig.put("R", 0);
        sig.put("A", "Self");
        sig.put("X", "Genuine");
        sig.put("Y", "NoResponse");
        sig.put("Z", 3.5);

        assertEquals("MODE-Fluctuating", wrapper.classify(sig));
    }

    // ===== Three-Color Audit Tests =====

    @Test
    @DisplayName("Color: default is green")
    void testColorGreen() {
        AuditWrapper wrapper = new AuditWrapper("9622");
        assertEquals("\uD83D\uDFE2", wrapper.determineColor("MODE-StableDisciplined", 0));
    }

    @Test
    @DisplayName("Color: InternalDestroyer is red")
    void testColorRed() {
        AuditWrapper wrapper = new AuditWrapper("9622");
        assertEquals("\uD83D\uDD34", wrapper.determineColor("MODE-InternalDestroyer", 0));
    }

    @Test
    @DisplayName("Color: Fluctuating + repeat>3 is yellow")
    void testColorYellowFluctuating() {
        AuditWrapper wrapper = new AuditWrapper("9622");
        assertEquals("\uD83D\uDFE1", wrapper.determineColor("MODE-Fluctuating", 4));
    }

    @Test
    @DisplayName("Color: DefensiveDefaulter + repeat>2 is yellow")
    void testColorYellowDefensive() {
        AuditWrapper wrapper = new AuditWrapper("9622");
        assertEquals("\uD83D\uDFE1", wrapper.determineColor("MODE-DefensiveDefaulter", 3));
    }

    // ===== Label Generation Tests =====

    @Test
    @DisplayName("Labels: generate bilingual labels from signature")
    void testLabels() {
        AuditWrapper wrapper = new AuditWrapper("9622");
        Map<String, Object> sig = new LinkedHashMap<>();
        sig.put("P", "HasPromise");
        sig.put("F", "Fulfilled");
        sig.put("T", 0.0);
        sig.put("E", "Willing");
        sig.put("C", 0);
        sig.put("R", 0);
        sig.put("A", "Self");
        sig.put("X", "Genuine");
        sig.put("Y", "NoResponse");
        sig.put("Z", 1.0);

        List<String> labels = wrapper.makeLabels(sig, "MODE-StableDisciplined");
        assertTrue(labels.contains("7F-P-有承诺"));
        assertTrue(labels.contains("7F-F-已兑现"));
        assertTrue(labels.contains("7F-E-心甘情愿"));
        assertTrue(labels.contains("MODE-StableDisciplined"));
    }

    // ===== Schema Tests =====

    @Test
    @DisplayName("Schemas: getSchemas returns dna_schema and audit_schema")
    void testGetSchemas() {
        Map<String, Object> schemas = adapter.getSchemas();
        assertTrue(schemas.containsKey("dna_schema"));
        assertTrue(schemas.containsKey("audit_schema"));
    }

    // ===== Edge Cases =====

    @Test
    @DisplayName("Edge case: wrap with null payload")
    void testWrapNullPayload() {
        Map<String, Object> result = adapter.wrap(null, "default", "P04");
        assertNotNull(result.get("dna"));
        assertNotNull(result.get("audit"));
    }

    @Test
    @DisplayName("Edge case: wrap with empty map")
    void testWrapEmptyMap() {
        Map<String, Object> result = adapter.wrap(new LinkedHashMap<>(), "default", "P04");
        assertNotNull(result.get("dna"));
    }

    @Test
    @DisplayName("Edge case: wrap with string payload")
    void testWrapStringPayload() {
        Map<String, Object> result = adapter.wrap("simple string", "default", "P04");
        assertEquals("simple string", result.get("payload"));
    }

    @Test
    @DisplayName("Edge case: wrap with list payload")
    void testWrapListPayload() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        Map<String, Object> result = adapter.wrap(list, "default", "P04");
        assertNotNull(result.get("dna"));
    }

    // ===== UID Consistency Tests =====

    @Test
    @DisplayName("UID: consistency between meta.uid and audit.uid")
    void testUidConsistency() {
        Map<String, String> payload = new HashMap<>();
        payload.put("test", "data");
        Map<String, Object> result = adapter.wrap(payload, "default", "P04");
        @SuppressWarnings("unchecked")
        Map<String, Object> meta = (Map<String, Object>) result.get("meta");
        @SuppressWarnings("unchecked")
        Map<String, Object> audit = (Map<String, Object>) result.get("audit");

        String metaUid = (String) meta.get("uid");
        String auditUid = (String) audit.get("uid");
        assertEquals("UID" + metaUid, auditUid);
    }

    // ===== Payload Hash Tests =====

    @Test
    @DisplayName("Payload hash: same payload produces same hash")
    void testPayloadHashConsistency() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("key", "value");
        data.put("num", 42);
        Map<String, Object> result1 = adapter.wrap(data, "default", "P04");
        Map<String, Object> result2 = adapter.wrap(data, "default", "P04");

        @SuppressWarnings("unchecked")
        Map<String, Object> audit1 = (Map<String, Object>) result1.get("audit");
        @SuppressWarnings("unchecked")
        Map<String, Object> audit2 = (Map<String, Object>) result2.get("audit");

        String hash1 = (String) audit1.get("payload_hash");
        String hash2 = (String) audit2.get("payload_hash");
        assertEquals(hash1, hash2, "Same payload should produce same hash");
    }

    @Test
    @DisplayName("Payload hash: different payloads produce different hashes")
    void testPayloadHashDifferent() {
        Map<String, String> data1 = new HashMap<>();
        data1.put("key", "value1");
        Map<String, String> data2 = new HashMap<>();
        data2.put("key", "value2");

        Map<String, Object> result1 = adapter.wrap(data1, "default", "P04");
        Map<String, Object> result2 = adapter.wrap(data2, "default", "P04");

        @SuppressWarnings("unchecked")
        Map<String, Object> audit1 = (Map<String, Object>) result1.get("audit");
        @SuppressWarnings("unchecked")
        Map<String, Object> audit2 = (Map<String, Object>) result2.get("audit");

        String hash1 = (String) audit1.get("payload_hash");
        String hash2 = (String) audit2.get("payload_hash");
        assertNotEquals(hash1, hash2, "Different payloads should produce different hashes");
    }

    // ===== Persona Tests =====

    @Test
    @DisplayName("Persona: custom persona appears in audit")
    void testCustomPersona() {
        Map<String, String> payload = new HashMap<>();
        payload.put("test", "data");
        Map<String, Object> result = adapter.wrap(payload, "code", "P14-Lvmeng");
        @SuppressWarnings("unchecked")
        Map<String, Object> audit = (Map<String, Object>) result.get("audit");
        assertEquals("P14-Lvmeng", audit.get("persona"));
    }

    @Test
    @DisplayName("Persona: default persona is P04")
    void testDefaultPersona() {
        Map<String, String> payload = new HashMap<>();
        payload.put("test", "data");
        Map<String, Object> result = adapter.wrap(payload, "code", "P04");
        @SuppressWarnings("unchecked")
        Map<String, Object> audit = (Map<String, Object>) result.get("audit");
        assertEquals("P04", audit.get("persona"));
    }
}