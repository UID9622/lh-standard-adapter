package cn.uid9622.longhun;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for LongHun Standard Adapter (Java).
 *
 * <p>Tests: 24 DNA Generator + 29 Audit Wrapper + 21 Validator = 74+ total.
 */
@DisplayName("LongHun Standard Adapter — Full Test Suite")
class LongHunAdapterTest {

    // ── Helpers ──

    private static DnaGenerator makeGen() {
        return new DnaGenerator("9622", "HM-9622-001", "Asia/Shanghai");
    }

    private static Map<String, Object> makePayload(String key, String value) {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put(key, value);
        return p;
    }

    // ════════════════════════════════════════════════════════════════
    //  DNA GENERATOR TESTS (24+)
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("DNA Generator")
    class DnaGeneratorTests {

        @Test
        @DisplayName("1. Default task type produces valid DNA")
        void testDnaDefault() {
            String dna = makeGen().generate("default", "WRAP", null);
            assertTrue(dna.startsWith("#LongHun⚡️"), "Should start with prefix");
            assertTrue(dna.contains("ADAPTER-DEFAULT-WRAP-V1.0"));
        }

        @Test
        @DisplayName("2. Code task type")
        void testDnaCode() {
            String dna = makeGen().generate("code", "GENERATE", "v2.0");
            assertTrue(dna.contains("ADAPTER-CODE-GENERATE-v2.0"));
        }

        @Test
        @DisplayName("3. Hash8 is 8 hex chars")
        void testDnaHash8() {
            String dna = makeGen().generate("default", "WRAP", null);
            String[] parts = dna.split("-");
            String last = parts[parts.length - 1];
            assertEquals(8, last.length());
            assertTrue(last.chars().allMatch(c -> (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f')));
        }

        @Test
        @DisplayName("4. Deploy task maps to deploy hexagram")
        void testDnaDeployHexagram() {
            String dna = makeGen().generate("deploy", "DEPLOY", null);
            assertTrue(dna.contains("ADAPTER-DEPLOY-DEPLOY-V1.0"));
        }

        @Test
        @DisplayName("5. Convenience method")
        void testDnaConvenience() {
            assertTrue(DnaGenerator.generateDna("audit", "WRAP", null).startsWith("#LongHun⚡️"));
        }

        @Test
        @DisplayName("6. DNA always starts with prefix")
        void testDnaPrefix() {
            for (String task : new String[]{"code", "deploy", "audit", "security", "archive"}) {
                String dna = makeGen().generate(task, "WRAP", null);
                assertTrue(dna.startsWith("#LongHun⚡️"), "Task " + task + " missing prefix");
            }
        }

        @Test
        @DisplayName("7. DNA format has correct structure (4 stem-branch parts)")
        void testDnaFormatStructure() {
            String dna = makeGen().generate("code", "WRAP", null);
            // Format: #LongHun⚡️Year·Month·Day·ShiChen·Hexagram-Body-hash
            assertTrue(dna.contains("·"), "Should contain middle dot separators");
            String afterPrefix = dna.substring("#LongHun⚡️".length());
            String[] parts = afterPrefix.split("·");
            assertEquals(5, parts.length, "Should split into 5 parts by ·");
            for (int i = 0; i < 4; i++) {
                assertTrue(parts[i].length() >= 2);
                assertTrue(Character.isUpperCase(parts[i].charAt(0)));
            }
        }

        @Test
        @DisplayName("8-19. All 12 task types generate valid DNA")
        void testDnaAllTaskTypes() {
            String[] tasks = {"code", "deploy", "audit", "security", "archive",
                    "init", "learn", "legal", "privacy", "trust", "complete", "progress"};
            for (String task : tasks) {
                String dna = makeGen().generate(task, "WRAP", null);
                assertTrue(dna.startsWith("#LongHun⚡️"), "Task " + task);
                String upper = task.toUpperCase();
                assertTrue(dna.contains("ADAPTER-" + upper), "Task " + task + " body missing");
            }
        }

        @Test
        @DisplayName("20. Unknown task type falls back to governance hexagram")
        void testDnaUnknownTaskType() {
            String dna = makeGen().generate("nonexistent", "WRAP", null);
            assertTrue(dna.contains("·䷀"), "Should fallback to ䷀ Qian");
        }

        @Test
        @DisplayName("21. Default version is V1.0")
        void testDnaVersionDefault() {
            String dna = makeGen().generate("code", "WRAP", null);
            assertTrue(dna.contains("-V1.0-"));
        }

        @Test
        @DisplayName("22. Custom version string")
        void testDnaVersionCustom() {
            String dna = makeGen().generate("code", "WRAP", "v3.1-beta");
            assertTrue(dna.contains("-v3.1-beta-"));
        }

        @Test
        @DisplayName("23. Null version defaults to V1.0")
        void testDnaVersionNull() {
            String dna = makeGen().generate("code", "WRAP", null);
            assertTrue(dna.contains("-V1.0-"));
        }

        @Test
        @DisplayName("24. Body format is correct")
        void testDnaBodyFormat() {
            String dna = makeGen().generate("audit", "VERIFY", "v2.0");
            assertTrue(dna.contains("ADAPTER-AUDIT-VERIFY-v2.0"));
        }

        @Test
        @DisplayName("25. ShiChen is valid (one of 12)")
        void testDnaShiChen() {
            var valid = Set.of(DnaGenerator.SHI_CHEN);
            for (int i = 0; i < 5; i++) {
                String dna = makeGen().generate("code", "WRAP", null);
                String afterPrefix = dna.substring("#LongHun⚡️".length());
                String[] parts = afterPrefix.split("·");
                assertEquals(5, parts.length);
                assertTrue(valid.contains(parts[3]), "ShiChen: " + parts[3]);
            }
        }

        @Test
        @DisplayName("26. TianGan array has 10 elements")
        void testDnaTianGanValues() {
            assertEquals(10, DnaGenerator.TIAN_GAN.length);
            assertEquals("Jia", DnaGenerator.TIAN_GAN[0]);
            assertEquals("Gui", DnaGenerator.TIAN_GAN[9]);
        }

        @Test
        @DisplayName("27. DiZhi array has 12 elements")
        void testDnaDiZhiValues() {
            assertEquals(12, DnaGenerator.DI_ZHI.length);
            assertEquals("Zi", DnaGenerator.DI_ZHI[0]);
            assertEquals("Hai", DnaGenerator.DI_ZHI[11]);
        }

        @Test
        @DisplayName("28. ShiChen array has 12 elements")
        void testDnaShiChenValues() {
            assertEquals(12, DnaGenerator.SHI_CHEN.length);
            assertEquals("ZiShi", DnaGenerator.SHI_CHEN[0]);
            assertEquals("HaiShi", DnaGenerator.SHI_CHEN[11]);
        }

        @Test
        @DisplayName("29. Hexagram array has 14 elements")
        void testDnaHexagramCount() {
            assertEquals(14, DnaGenerator.HEXAGRAMS.length);
        }

        @Test
        @DisplayName("30. SHA-256 hash helper produces 8 hex chars")
        void testSha256First4Bytes() {
            String hash = DnaGenerator.sha256First4Bytes("test");
            assertEquals(8, hash.length());
            assertTrue(hash.chars().allMatch(c -> (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f')));
        }

        @Test
        @DisplayName("31. SHA-256 8-byte helper produces 16 hex chars")
        void testSha256First8Bytes() {
            String hash = DnaGenerator.sha256First8Bytes("test");
            assertEquals(16, hash.length());
            assertTrue(hash.chars().allMatch(c -> (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f')));
        }

        @Test
        @DisplayName("32. nowIso returns valid ISO datetime")
        void testNowIso() {
            String iso = DnaGenerator.nowIso();
            assertTrue(iso.contains("T"));
            assertTrue(iso.contains("+") || iso.contains("-"));
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  AUDIT WRAPPER TESTS (29+)
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Audit Wrapper")
    class AuditWrapperTests {

        @Test
        @DisplayName("33. Audit wrap has all required keys")
        void testAuditWrap() {
            var w = new AuditWrapper("9622");
            var a = w.wrap(makePayload("code", "test"), "code", "P04");
            assertEquals("v1.0", a.get("audit_version"));
            assertEquals("UID9622", a.get("uid"));
            assertTrue(a.containsKey("behavior_signature"));
            assertTrue(a.containsKey("behavior_pattern"));
            assertTrue(a.containsKey("color"));
            assertTrue(a.containsKey("payload_hash"));
        }

        @Test
        @DisplayName("34. Default signature values")
        @SuppressWarnings("unchecked")
        void testAuditSignature() {
            var a = new AuditWrapper("9622").wrap(new LinkedHashMap<>(), "default", "P04");
            var sig = (Map<String, Object>) a.get("behavior_signature");
            assertEquals("HasPromise", sig.get("P"));
            assertEquals("Fulfilled", sig.get("F"));
            assertEquals("Willing", sig.get("E"));
            assertEquals(1.0, ((Number) sig.get("Z")).doubleValue(), 0.001);
        }

        @Test
        @DisplayName("35. Default behavior pattern is StableDisciplined")
        void testAuditPattern() {
            var a = new AuditWrapper("9622").wrap(new LinkedHashMap<>(), "default", "P04");
            assertEquals("MODE-StableDisciplined", a.get("behavior_pattern"));
        }

        @Test
        @DisplayName("36. Payload hash is 16 hex chars")
        void testAuditHash() {
            var a = new AuditWrapper("9622").wrap(makePayload("x", "1"), "default", "P04");
            String h = (String) a.get("payload_hash");
            assertEquals(16, h.length());
            assertTrue(h.chars().allMatch(c -> (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f')));
        }

        @Test
        @DisplayName("37. Labels are non-empty")
        @SuppressWarnings("unchecked")
        void testAuditLabels() {
            var a = new AuditWrapper("9622").wrap(new LinkedHashMap<>(), "default", "P04");
            var labels = (List<String>) a.get("behavior_labels");
            assertFalse(labels.isEmpty());
        }

        @Test
        @DisplayName("38. Labels include bilingual entries and pattern")
        @SuppressWarnings("unchecked")
        void testAuditLabelsBilingual() {
            var a = new AuditWrapper("9622").wrap(new LinkedHashMap<>(), "default", "P04");
            var labels = (List<String>) a.get("behavior_labels");
            assertTrue(labels.stream().anyMatch(l -> l.startsWith("7F-P-")));
            assertTrue(labels.stream().anyMatch(l -> l.startsWith("7F-F-")));
            assertTrue(labels.contains("MODE-StableDisciplined"));
        }

        @Test
        @DisplayName("39. Audit version is v1.0")
        void testAuditVersion() {
            var a = new AuditWrapper("9622").wrap(new LinkedHashMap<>(), "default", "P04");
            assertEquals("v1.0", a.get("audit_version"));
        }

        @Test
        @DisplayName("40. UID correctly formatted")
        void testAuditUid() {
            var a = new AuditWrapper("1234").wrap(new LinkedHashMap<>(), "default", "P04");
            assertEquals("UID1234", a.get("uid"));
        }

        @Test
        @DisplayName("41. Timestamp is present and ISO format")
        void testAuditTimestamp() {
            var a = new AuditWrapper("9622").wrap(new LinkedHashMap<>(), "default", "P04");
            String ts = (String) a.get("timestamp");
            assertNotNull(ts);
            assertTrue(ts.contains("T"));
        }

        @Test
        @DisplayName("42. Same payload produces same hash")
        void testAuditPayloadHashStable() {
            Map<String, Object> p = makePayload("a", "1");
            String h1 = (String) new AuditWrapper("9622").wrap(p, "default", "P04").get("payload_hash");
            String h2 = (String) new AuditWrapper("9622").wrap(p, "default", "P04").get("payload_hash");
            assertEquals(h1, h2);
        }

        @Test
        @DisplayName("43. Different payloads produce different hashes")
        void testAuditPayloadHashDifferent() {
            String h1 = (String) new AuditWrapper("9622").wrap(makePayload("a", "1"), "default", "P04").get("payload_hash");
            String h2 = (String) new AuditWrapper("9622").wrap(makePayload("a", "2"), "default", "P04").get("payload_hash");
            assertNotEquals(h1, h2);
        }

        @Test
        @DisplayName("44. Default color is green")
        void testAuditColorGreen() {
            var a = new AuditWrapper("9622").wrap(new LinkedHashMap<>(), "default", "P04");
            assertEquals("🟢", a.get("color"));
        }

        @Test
        @DisplayName("45. Persona is recorded")
        void testAuditPersona() {
            var a = new AuditWrapper("9622").wrap(new LinkedHashMap<>(), "code", "P07");
            assertEquals("P07", a.get("persona"));
        }

        @Test
        @DisplayName("46. Task type is recorded")
        void testAuditTaskType() {
            var a = new AuditWrapper("9622").wrap(new LinkedHashMap<>(), "deploy", "P04");
            assertEquals("deploy", a.get("task_type"));
        }

        @Test
        @DisplayName("47. Signature has all 10 keys")
        @SuppressWarnings("unchecked")
        void testAuditSigAllKeys() {
            var a = new AuditWrapper("9622").wrap(new LinkedHashMap<>(), "default", "P04");
            var sig = (Map<String, Object>) a.get("behavior_signature");
            for (String k : Schemas.REQUIRED_SIG_KEYS) {
                assertTrue(sig.containsKey(k), "Missing key: " + k);
            }
        }

        @Test
        @DisplayName("48. Convenience method works")
        void testAuditConvenience() {
            var a = AuditWrapper.auditWrap(new LinkedHashMap<>(), "default", "P04");
            assertEquals("v1.0", a.get("audit_version"));
        }

        @Test
        @DisplayName("49. Classify: DefensiveDefaulter (F=Unfulfilled, X=OverExplain)")
        void testAuditDefensiveDefaulter() {
            AuditWrapper w = new AuditWrapper("9622");
            Map<String, Object> sig = new LinkedHashMap<>();
            sig.put("F", "Unfulfilled");
            sig.put("X", "OverExplain");
            sig.put("A", "Self");
            sig.put("Y", "NoResponse");
            sig.put("Z", 1.0);
            assertEquals("MODE-DefensiveDefaulter", w.classify(sig));
        }

        @Test
        @DisplayName("50. Classify: ExternalTrustSpender (F=Fulfilled, A=Outsider)")
        void testAuditTrustSpender() {
            AuditWrapper w = new AuditWrapper("9622");
            Map<String, Object> sig = new LinkedHashMap<>();
            sig.put("F", "Fulfilled");
            sig.put("X", "Genuine");
            sig.put("A", "Outsider");
            sig.put("Y", "NoResponse");
            sig.put("Z", 1.0);
            assertEquals("MODE-ExternalTrustSpender", w.classify(sig));
        }

        @Test
        @DisplayName("51. Classify: InternalDestroyer (F=Unfulfilled, Y=Indifferent)")
        void testAuditDestroyer() {
            AuditWrapper w = new AuditWrapper("9622");
            Map<String, Object> sig = new LinkedHashMap<>();
            sig.put("F", "Unfulfilled");
            sig.put("X", "Genuine");
            sig.put("A", "Self");
            sig.put("Y", "Indifferent");
            sig.put("Z", 1.0);
            assertEquals("MODE-InternalDestroyer", w.classify(sig));
        }

        @Test
        @DisplayName("52. Classify: Fluctuating (Z > 2.0)")
        void testAuditFluctuating() {
            AuditWrapper w = new AuditWrapper("9622");
            Map<String, Object> sig = new LinkedHashMap<>();
            sig.put("F", "Fulfilled");
            sig.put("X", "Genuine");
            sig.put("A", "Self");
            sig.put("Y", "NoResponse");
            sig.put("Z", 3.5);
            assertEquals("MODE-Fluctuating", w.classify(sig));
        }

        @Test
        @DisplayName("53. Classify: StableDisciplined (default)")
        void testAuditStableDisciplined() {
            AuditWrapper w = new AuditWrapper("9622");
            Map<String, Object> sig = new LinkedHashMap<>();
            sig.put("F", "Fulfilled");
            sig.put("X", "Genuine");
            sig.put("A", "Self");
            sig.put("Y", "NoResponse");
            sig.put("Z", 1.0);
            assertEquals("MODE-StableDisciplined", w.classify(sig));
        }

        @Test
        @DisplayName("54. Color: red for InternalDestroyer")
        void testAuditColorRed() {
            assertEquals("🔴", AuditWrapper.determineColor("MODE-InternalDestroyer", 0));
        }

        @Test
        @DisplayName("55. Color: yellow for Fluctuating with high repeat")
        void testAuditColorYellow() {
            assertEquals("🟡", AuditWrapper.determineColor("MODE-Fluctuating", 5));
        }

        @Test
        @DisplayName("56. Color: green for Fluctuating with low repeat")
        void testAuditColorGreenFluctuating() {
            assertEquals("🟢", AuditWrapper.determineColor("MODE-Fluctuating", 2));
        }

        @Test
        @DisplayName("57. Color: yellow for DefensiveDefaulter with high repeat")
        void testAuditColorYellowDefensive() {
            assertEquals("🟡", AuditWrapper.determineColor("MODE-DefensiveDefaulter", 5));
        }

        @Test
        @DisplayName("58. Color: green for DefensiveDefaulter with low repeat")
        void testAuditColorGreenDefensive() {
            assertEquals("🟢", AuditWrapper.determineColor("MODE-DefensiveDefaulter", 1));
        }

        @Test
        @DisplayName("59. getLabel returns correct bilingual labels")
        void testGetLabel() {
            assertEquals("7F-P-有承诺", AuditWrapper.getLabel("P", "HasPromise"));
            assertEquals("7F-P-无承诺", AuditWrapper.getLabel("P", "NoPromise"));
            assertEquals("7F-F-已兑现", AuditWrapper.getLabel("F", "Fulfilled"));
            assertEquals("7F-F-未兑现", AuditWrapper.getLabel("F", "Unfulfilled"));
            assertEquals("7F-F-部分兑现", AuditWrapper.getLabel("F", "Partial"));
            assertEquals("7F-E-心甘情愿", AuditWrapper.getLabel("E", "Willing"));
            assertEquals("7F-E-敷衍", AuditWrapper.getLabel("E", "Perfunctory"));
            assertEquals("7F-E-怨恨", AuditWrapper.getLabel("E", "Resentful"));
            assertEquals("7F-E-麻木", AuditWrapper.getLabel("E", "Numb"));
            assertEquals("7F-A-自己", AuditWrapper.getLabel("A", "Self"));
            assertEquals("7F-A-公众", AuditWrapper.getLabel("A", "Public"));
            assertEquals("7F-X-沉默", AuditWrapper.getLabel("X", "Silent"));
            assertEquals("7F-X-真诚", AuditWrapper.getLabel("X", "Genuine"));
            assertEquals("7F-Y-改正", AuditWrapper.getLabel("Y", "Changed"));
            assertEquals("7F-Y-无响应", AuditWrapper.getLabel("Y", "NoResponse"));
            assertNull(AuditWrapper.getLabel("Z", "foo"));
            assertNull(AuditWrapper.getLabel("P", "unknown"));
        }

        @Test
        @DisplayName("60. toJsonString handles Maps correctly")
        void testToJsonString() {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("code", "print('hello')");
            String json = AuditWrapper.toJsonString(payload);
            assertTrue(json.contains("\"code\""));
            assertTrue(json.contains("print('hello')"));
        }

        @Test
        @DisplayName("61. toJsonString handles Lists")
        void testToJsonStringList() {
            List<Object> list = List.of("a", "b", "c");
            String json = AuditWrapper.toJsonString(list);
            assertTrue(json.startsWith("["));
            assertTrue(json.endsWith("]"));
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  VALIDATOR TESTS (21+)
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Validator")
    class ValidatorTests {

        @Test
        @DisplayName("62. Valid wrapped payload passes validation")
        @SuppressWarnings("unchecked")
        void testValidateValid() {
            var adapter = new LongHunAdapter("9622", "HM-9622-001", "Asia/Shanghai");
            var r = adapter.wrap(makePayload("code", "test"), "code", "P04", "WRAP", null);
            var v = adapter.validate(r);
            assertEquals(true, v.get("valid"));
        }

        @Test
        @DisplayName("63. Empty map is invalid")
        void testValidateEmpty() {
            var v = new Validator().validate(new LinkedHashMap<>());
            assertEquals(false, v.get("valid"));
        }

        @Test
        @DisplayName("64. Missing DNA key")
        void testValidateMissingDna() {
            var w = new LinkedHashMap<String, Object>();
            w.put("audit", new LinkedHashMap<>());
            w.put("payload", new LinkedHashMap<>());
            w.put("meta", new LinkedHashMap<>());
            var v = new Validator().validate(w);
            assertEquals(false, v.get("valid"));
        }

        @Test
        @DisplayName("65. Missing audit key")
        void testValidateMissingAudit() {
            var w = new LinkedHashMap<String, Object>();
            w.put("dna", "#LongHun⚡️Test");
            w.put("payload", new LinkedHashMap<>());
            w.put("meta", new LinkedHashMap<>());
            var v = new Validator().validate(w);
            assertEquals(false, v.get("valid"));
        }

        @Test
        @DisplayName("66. Missing payload key")
        void testValidateMissingPayload() {
            var w = new LinkedHashMap<String, Object>();
            w.put("dna", makeGen().generate("code", "WRAP", null));
            w.put("audit", new AuditWrapper("9622").wrap(new LinkedHashMap<>(), "code", "P04"));
            w.put("meta", new LinkedHashMap<>());
            var v = new Validator().validate(w);
            assertEquals(false, v.get("valid"));
        }

        @Test
        @DisplayName("67. Missing meta key")
        void testValidateMissingMeta() {
            var w = new LinkedHashMap<String, Object>();
            w.put("dna", makeGen().generate("code", "WRAP", null));
            w.put("audit", new AuditWrapper("9622").wrap(new LinkedHashMap<>(), "code", "P04"));
            w.put("payload", new LinkedHashMap<>());
            var v = new Validator().validate(w);
            assertEquals(false, v.get("valid"));
        }

        @Test
        @DisplayName("68. Empty DNA string")
        void testValidateEmptyDna() {
            var w = new LinkedHashMap<String, Object>();
            w.put("dna", "");
            w.put("audit", new LinkedHashMap<>());
            w.put("payload", new LinkedHashMap<>());
            w.put("meta", new LinkedHashMap<>());
            var v = new Validator().validate(w);
            assertEquals(false, v.get("valid"));
        }

        @Test
        @DisplayName("69. Bad DNA format")
        void testValidateBadDnaFormat() {
            var w = new LinkedHashMap<String, Object>();
            w.put("dna", "not-a-valid-dna");
            w.put("audit", new LinkedHashMap<>());
            w.put("payload", new LinkedHashMap<>());
            w.put("meta", new LinkedHashMap<>());
            var v = new Validator().validate(w);
            assertEquals(false, v.get("valid"));
        }

        @Test
        @DisplayName("70. DNA not a string")
        void testValidateDnaNotString() {
            var w = new LinkedHashMap<String, Object>();
            w.put("dna", 12345);
            w.put("audit", new LinkedHashMap<>());
            w.put("payload", new LinkedHashMap<>());
            w.put("meta", new LinkedHashMap<>());
            var v = new Validator().validate(w);
            assertEquals(false, v.get("valid"));
        }

        @Test
        @DisplayName("71. Non-object audit field")
        void testValidateAuditNotObject() {
            var w = new LinkedHashMap<String, Object>();
            w.put("dna", makeGen().generate("code", "WRAP", null));
            w.put("audit", "not-an-object");
            w.put("payload", new LinkedHashMap<>());
            w.put("meta", new LinkedHashMap<>());
            var v = new Validator().validate(w);
            assertEquals(false, v.get("valid"));
        }

        @Test
        @DisplayName("72. Missing behavior_signature key")
        @SuppressWarnings("unchecked")
        void testValidateMissingSigKey() {
            var w = new LinkedHashMap<String, Object>();
            w.put("dna", makeGen().generate("code", "WRAP", null));
            var sig = new LinkedHashMap<String, Object>();
            sig.put("P", "HasPromise");
            // Missing F, T, E, etc.
            var audit = new LinkedHashMap<String, Object>();
            audit.put("audit_version", "v1.0");
            audit.put("uid", "UID9622");
            audit.put("behavior_signature", sig);
            audit.put("behavior_pattern", "MODE-StableDisciplined");
            audit.put("behavior_labels", List.of());
            audit.put("color", "🟢");
            w.put("audit", audit);
            w.put("payload", new LinkedHashMap<>());
            w.put("meta", new LinkedHashMap<>());
            var v = new Validator().validate(w);
            assertEquals(false, v.get("valid"));
        }

        @Test
        @DisplayName("73. UID mismatch")
        @SuppressWarnings("unchecked")
        void testValidateUidMismatch() {
            var dna = makeGen().generate("code", "WRAP", null);
            var w = new LinkedHashMap<String, Object>();
            w.put("dna", dna);
            var sig = new LinkedHashMap<String, Object>();
            sig.put("P", "HasPromise"); sig.put("F", "Fulfilled"); sig.put("T", 0.0);
            sig.put("E", "Willing"); sig.put("C", 0); sig.put("R", 0);
            sig.put("A", "Self"); sig.put("X", "Genuine"); sig.put("Y", "NoResponse");
            sig.put("Z", 1.0);
            var audit = new LinkedHashMap<String, Object>();
            audit.put("audit_version", "v1.0");
            audit.put("uid", "UID1234");  // Different from meta
            audit.put("behavior_signature", sig);
            audit.put("behavior_pattern", "MODE-StableDisciplined");
            audit.put("behavior_labels", List.of());
            audit.put("color", "🟢");
            w.put("audit", audit);
            w.put("payload", new LinkedHashMap<>());
            var meta = new LinkedHashMap<String, Object>();
            meta.put("uid", "9622");  // Mismatch with audit
            w.put("meta", meta);
            var v = new Validator().validate(w);
            assertEquals(false, v.get("valid"));
        }

        @Test
        @DisplayName("74. Quick validate returns true for valid payload")
        @SuppressWarnings("unchecked")
        void testQuickValidate() {
            var adapter = new LongHunAdapter("9622", "HM-9622-001", "Asia/Shanghai");
            var r = adapter.wrap(makePayload("a", "1"), "default", "P04", "WRAP", null);
            assertTrue(Validator.quickValidate(r));
            assertFalse(Validator.quickValidate(new LinkedHashMap<>()));
            assertFalse(Validator.quickValidate(null));
        }

        @Test
        @DisplayName("75. Cross-validation: valid wrapped payload passes")
        @SuppressWarnings("unchecked")
        void testCrossValidation() {
            var dna = makeGen().generate("code", "WRAP", null);
            var sig = new LinkedHashMap<String, Object>();
            sig.put("P", "HasPromise"); sig.put("F", "Fulfilled"); sig.put("T", 0.0);
            sig.put("E", "Willing"); sig.put("C", 0); sig.put("R", 0);
            sig.put("A", "Self"); sig.put("X", "Genuine"); sig.put("Y", "NoResponse");
            sig.put("Z", 1.0);

            var audit = new LinkedHashMap<String, Object>();
            audit.put("audit_version", "v1.0");
            audit.put("uid", "UID9622");
            audit.put("persona", "P04");
            audit.put("task_type", "code");
            audit.put("behavior_signature", sig);
            audit.put("behavior_pattern", "MODE-StableDisciplined");
            audit.put("behavior_labels", List.of("7F-P-有承诺"));
            audit.put("color", "🟢");
            audit.put("timestamp", "2026-07-24T13:00:00+08:00");
            audit.put("payload_hash", "a1b2c3d4e5f67890");

            var meta = new LinkedHashMap<String, Object>();
            meta.put("adapter_version", "1.0.0");
            meta.put("uid", "9622");
            meta.put("device", "HM-9622-001");
            meta.put("task_type", "code");
            meta.put("persona", "P04");
            meta.put("generated_at", "2026-07-24T13:00:00+08:00");
            meta.put("format", "longhun-v∞");

            var wrapped = new LinkedHashMap<String, Object>();
            wrapped.put("dna", dna);
            wrapped.put("audit", audit);
            wrapped.put("payload", makePayload("code", "test"));
            wrapped.put("meta", meta);

            var v = new Validator().validate(wrapped);
            assertTrue((Boolean) v.get("valid"), "Cross-validation should pass");
        }

        @Test
        @DisplayName("76. Null input is invalid")
        void testValidateNull() {
            var v = new Validator().validate(null);
            assertEquals(false, v.get("valid"));
        }

        @Test
        @DisplayName("77. DNA hexagram Unicode check")
        void testDnaMatchesValid() {
            var dna = makeGen().generate("code", "WRAP", null);
            assertTrue(Validator.dnaMatches(dna));
        }

        @Test
        @DisplayName("78. DNA matches rejects invalid")
        void testDnaMatchesRejects() {
            assertFalse(Validator.dnaMatches("not-a-dna"));
            assertFalse(Validator.dnaMatches("#LongHun⚡️bad"));
            assertFalse(Validator.dnaMatches("#WrongPrefix"));
            assertFalse(Validator.dnaMatches(""));
        }

        @Test
        @DisplayName("79. DNA matches requires 5 dot-separated parts")
        void testDnaMatchesParts() {
            // Only 1 part after prefix: too few
            String bad = "#LongHun⚡️OnePart";
            assertFalse(Validator.dnaMatches(bad));
        }

        @Test
        @DisplayName("80. Validator result summary contains VALID/INVALID")
        @SuppressWarnings("unchecked")
        void testValidateSummary() {
            var adapter = new LongHunAdapter("9622", "HM-9622-001", "Asia/Shanghai");
            var r = adapter.wrap(makePayload("code", "test"), "code", "P04", "WRAP", null);
            var v = adapter.validate(r);
            String summary = (String) v.get("summary");
            assertTrue(summary.contains("VALID"));
        }

        @Test
        @DisplayName("81. Invalid result summary contains INVALID")
        void testValidateInvalidSummary() {
            var w = new LinkedHashMap<String, Object>();
            w.put("dna", "");
            w.put("audit", new LinkedHashMap<>());
            w.put("payload", new LinkedHashMap<>());
            w.put("meta", new LinkedHashMap<>());
            var v = new Validator().validate(w);
            String summary = (String) v.get("summary");
            assertTrue(summary.contains("INVALID"));
        }

        @Test
        @DisplayName("82. Adapter default constructor")
        void testAdapterDefault() {
            var adapter = new LongHunAdapter();
            var r = adapter.wrap(makePayload("msg", "hi"), "default", "P04", "WRAP", null);
            assertTrue(r.containsKey("dna"));
            assertTrue(r.containsKey("audit"));
            assertTrue(r.containsKey("payload"));
            assertTrue(r.containsKey("meta"));
        }
    }
}
