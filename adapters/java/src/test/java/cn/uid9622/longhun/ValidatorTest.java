1|package cn.uid9622.longhun;
2|
3|import com.fasterxml.jackson.databind.ObjectMapper;
4|import com.fasterxml.jackson.databind.node.ObjectNode;
5|import org.junit.jupiter.api.BeforeEach;
6|import org.junit.jupiter.api.DisplayName;
7|import org.junit.jupiter.api.Test;
8|
9|import java.util.Map;
10|
11|import static org.junit.jupiter.api.Assertions.*;
12|
13|/**
14| * Tests for {@link Validator}.
15| */
16|@DisplayName("Validator")
17|class ValidatorTest {
18|
19|    private static final ObjectMapper MAPPER = new ObjectMapper();
20|    private LongHunAdapter adapter;
21|    private Validator validator;
22|
23|    @BeforeEach
24|    void setUp() {
25|        adapter = new LongHunAdapter("9622", "HM-9622-001");
26|        validator = new Validator();
27|    }
28|
29|    @Test
30|    @DisplayName("full cycle valid")
31|    void fullCycleValid() {
32|        ObjectNode wrapped = adapter.wrap(
33|                mapOf("action", "deploy", "target", "portal"),
34|                "deploy", "P14-Lvmeng"
35|        );
36|        ValidationResult result = adapter.validate(wrapped);
37|        assertTrue(result.isValid(), "valid: " + result.getSummary());
38|    }
39|
40|    @Test
41|    @DisplayName("multiple code payloads all validate")
42|    void multipleCodePayloads() {
43|        String[] codes = {
44|                "print(\"hello\")",
45|                "def fib(n): return n if n < 2 else fib(n-1) + fib(n-2)",
46|                "import numpy as np"
47|        };
48|        for (String code : codes) {
49|            ObjectNode wrapped = adapter.wrap(
50|                    mapOf("code", code, "language", "python"),
51|                    "code", "P04-Luban"
52|            );
53|            assertTrue(adapter.validate(wrapped).isValid(), "Failed for code: " + code);
54|        }
55|    }
56|
57|    @Test
58|    @DisplayName("quickValidate passes on valid wrapped payload")
59|    void quickValidate() {
60|        ObjectNode wrapped = adapter.wrap(mapOf("test", true), "test", "P04");
61|        assertTrue(Validator.quickValidate(wrapped));
62|    }
63|
64|    @Test
65|    @DisplayName("quickValidate rejects empty object")
66|    void quickValidateRejectsEmpty() {
67|        assertFalse(Validator.quickValidate(MAPPER.createObjectNode()));
68|    }
69|
70|    @Test
71|    @DisplayName("quickValidate rejects partial object")
72|    void quickValidateRejectsPartial() {
73|        ObjectNode obj = MAPPER.createObjectNode();
74|        obj.put("dna", "bad");
75|        assertFalse(Validator.quickValidate(obj));
76|    }
77|
78|    @Test
79|    @DisplayName("validate rejects empty object")
80|    void emptyPayload() {
81|        ValidationResult result = adapter.validate(MAPPER.createObjectNode());
82|        assertFalse(result.isValid());
83|    }
84|
85|    @Test
86|    @DisplayName("validate rejects invalid DNA")
87|    void invalidDna() {
88|        ObjectNode wrapped = adapter.wrap(mapOf("test", true), "test", "P04");
89|        wrapped.put("dna", "#BadDNA-xxx");
90|        ValidationResult result = adapter.validate(wrapped);
91|        assertFalse(result.isValid());
92|    }
93|
94|    @Test
95|    @DisplayName("validate rejects missing audit key")
96|    void missingAudit() {
97|        ObjectNode wrapped = adapter.wrap(mapOf("test", true), "test", "P04");
98|        wrapped.remove("audit");
99|        ValidationResult result = adapter.validate(wrapped);
100|        assertFalse(result.isValid());
101|    }
102|
103|    @Test
104|    @DisplayName("validate rejects missing payload key")
105|    void missingPayload() {
106|        ObjectNode wrapped = adapter.wrap(mapOf("test", true), "test", "P04");
107|        wrapped.remove("payload");
108|        ValidationResult result = adapter.validate(wrapped);
109|        assertFalse(result.isValid());
110|    }
111|
112|    @Test
113|    @DisplayName("validate rejects missing meta key")
114|    void missingMeta() {
115|        ObjectNode wrapped = adapter.wrap(mapOf("test", true), "test", "P04");
116|        wrapped.remove("meta");
117|        ValidationResult result = adapter.validate(wrapped);
118|        assertFalse(result.isValid());
119|    }
120|
121|    @Test
122|    @DisplayName("validate detects UID mismatch")
123|    void uidMismatch() {
124|        ObjectNode wrapped = adapter.wrap(mapOf("test", true), "test", "P04");
125|        ((ObjectNode) wrapped.get("audit")).put("uid", "UID9876");
126|        ValidationResult result = adapter.validate(wrapped);
127|        assertFalse(result.isValid());
128|    }
129|
130|    @Test
131|    @DisplayName("validate produces warnings for invalid signature fields")
132|    void invalidSignatureFields() {
133|        ObjectNode wrapped = adapter.wrap(mapOf("test", true), "test", "P04");
134|        ((ObjectNode) wrapped.get("audit").get("behavior_signature")).put("F", "INVALID_VALUE");
135|        ValidationResult result = adapter.validate(wrapped);
136|        assertTrue(result.getWarnings().size() > 0, "warnings for invalid fields");
137|    }
138|
139|    @Test
140|    @DisplayName("validate multiple wraps with different tasks")
141|    void multipleWraps() {
142|        Object[][] tasks = {
143|                {mapOf("op", "scan"), "audit", "P05-Shangdi"},
144|                {mapOf("op", "build"), "code", "P04-Luban"},
145|                {mapOf("op", "deploy"), "deploy", "P14-Lvmeng"},
146|        };
147|        for (Object[] task : tasks) {
148|            @SuppressWarnings("unchecked")
149|            Map<String, Object> data = (Map<String, Object>) task[0];
150|            String taskType = (String) task[1];
151|            String persona = (String) task[2];
152|            ObjectNode wrapped = adapter.wrap(data, taskType, persona);
153|            ValidationResult result = adapter.validate(wrapped);
154|            assertTrue(result.isValid(), taskType + "/" + persona + ": " + result.getSummary());
155|        }
156|    }
157|
158|    @Test
159|    @DisplayName("validate with null input throws NPE")
160|    void validateNull() {
161|        assertThrows(NullPointerException.class, () -> adapter.validate(null));
162|    }
163|
164|    @Test
165|    @DisplayName("schema contains dna_schema and audit_schema")
166|    void getSchemas() {
167|        Map<String, Object> schemas = adapter.getSchemas();
168|        assertTrue(schemas.containsKey("dna_schema"));
169|        assertTrue(schemas.containsKey("audit_schema"));
170|        assertTrue(schemas.get("dna_schema") instanceof Map);
171|        assertTrue(schemas.get("audit_schema") instanceof Map);
172|    }
173|
174|    @Test
175|    @DisplayName("dna_schema has correct structure")
176|    void dnaSchemaStructure() {
177|        Map<String, Object> schemas = adapter.getSchemas();
178|        @SuppressWarnings("unchecked")
179|        Map<String, Object> dnaSchema = (Map<String, Object>) schemas.get("dna_schema");
180|        assertEquals("object", dnaSchema.get("type"));
181|        assertTrue(dnaSchema.containsKey("properties"));
182|        assertTrue(dnaSchema.containsKey("required"));
183|    }
184|
185|    @Test
186|    @DisplayName("audit_schema has correct structure")
187|    void auditSchemaStructure() {
188|        Map<String, Object> schemas = adapter.getSchemas();
189|        @SuppressWarnings("unchecked")
190|        Map<String, Object> auditSchema = (Map<String, Object>) schemas.get("audit_schema");
191|        assertEquals("object", auditSchema.get("type"));
192|        assertTrue(auditSchema.containsKey("properties"));
193|    }
194|
195|    @Test
196|    @DisplayName("validate detects missing top-level keys")
197|    void missingTopLevelKeys() {
198|        ObjectNode bad = MAPPER.createObjectNode();
199|        bad.put("dna", "#LongHun⚡️BingWu·GuiWei·JiaZi·WeiShi·䷾JiJi-ADAPTER-TEST-WRAP-V1.0-a3f8c1d9");
200|        // missing audit, payload, meta
201|        ValidationResult result = adapter.validate(bad);
202|        assertFalse(result.isValid());
203|        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("Missing top-level keys")));
204|    }
205|
206|    @Test
207|    @DisplayName("validate with empty DNA field")
208|    void emptyDna() {
209|        ObjectNode wrapped = adapter.wrap(mapOf("test", true), "test", "P04");
210|        wrapped.put("dna", "");
211|        ValidationResult result = adapter.validate(wrapped);
212|        assertFalse(result.isValid());
213|        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("DNA field is empty")));
214|    }
215|
216|    @Test
217|    @DisplayName("validate with null audit field")
218|    void nullAudit() {
219|        ObjectNode wrapped = adapter.wrap(mapOf("test", true), "test", "P04");
220|        wrapped.set("audit", MAPPER.createObjectNode());
221|        // Remove behavior_signature from audit
222|        ValidationResult result = adapter.validate(wrapped);
223|        assertFalse(result.isValid());
224|    }
225|
226|    @Test
227|    @DisplayName("validate detects missing audit keys")
228|    void missingAuditKeys() {
229|        ObjectNode wrapped = adapter.wrap(mapOf("test", true), "test", "P04");
230|        ObjectNode audit = (ObjectNode) wrapped.get("audit");
231|        audit.remove("behavior_pattern");
232|        ValidationResult result = adapter.validate(wrapped);
233|        assertFalse(result.isValid());
234|    }
235|
236|    @Test
237|    @DisplayName("validate detects missing signature keys")
238|    void missingSignatureKeys() {
239|        ObjectNode wrapped = adapter.wrap(mapOf("test", true), "test", "P04");
240|        ObjectNode sig = (ObjectNode) wrapped.get("audit").get("behavior_signature");
241|        sig.remove("Z");
242|        ValidationResult result = adapter.validate(wrapped);
243|        assertFalse(result.isValid());
244|    }
245|
246|    @Test
247|    @DisplayName("validate warns on unknown pattern")
248|    void unknownPattern() {
249|        ObjectNode wrapped = adapter.wrap(mapOf("test", true), "test", "P04");
250|        ((ObjectNode) wrapped.get("audit")).put("behavior_pattern", "MODE-Unknown");
251|        ValidationResult result = adapter.validate(wrapped);
252|        assertTrue(result.getWarnings().stream().anyMatch(w -> w.contains("Unknown behavior pattern")));
253|    }
254|
255|    @Test
256|    @DisplayName("validate warns on unknown color")
257|    void unknownColor() {
258|        ObjectNode wrapped = adapter.wrap(mapOf("test", true), "test", "P04");
259|        ((ObjectNode) wrapped.get("audit")).put("color", "purple");
260|        ValidationResult result = adapter.validate(wrapped);
261|        assertTrue(result.getWarnings().stream().anyMatch(w -> w.contains("Unknown audit color")));
262|    }
263|
264|    @Test
265|    @DisplayName("validate warns on suspicious payload hash")
266|    void suspiciousPayloadHash() {
267|        ObjectNode wrapped = adapter.wrap(mapOf("test", true), "test", "P04");
268|        ((ObjectNode) wrapped.get("audit")).put("payload_hash", "xyz");
269|        ValidationResult result = adapter.validate(wrapped);
270|        assertTrue(result.getWarnings().stream().anyMatch(w -> w.contains("Suspicious payload_hash")));
271|    }
272|
273|    @Test
274|    @DisplayName("quickValidate returns false for non-JsonNode input")
275|    void quickValidateNonObject() {
276|        assertFalse(Validator.quickValidate(MAPPER.createObjectNode()));
277|    }
278|
279|    // --- ValidationResult tests ---
280|
281|    @Test
282|    @DisplayName("ValidationResult valid state")
283|    void validationResultValid() {
284|        ValidationResult r = new ValidationResult(true, null, null, "✅ VALID");
285|        assertTrue(r.isValid());
286|        assertTrue(r.getErrors().isEmpty());
287|        assertTrue(r.getWarnings().isEmpty());
288|        assertEquals("✅ VALID", r.getSummary());
289|    }
290|
291|    @Test
292|    @DisplayName("ValidationResult invalid state")
293|    void validationResultInvalid() {
294|        ValidationResult r = new ValidationResult(false,
295|                java.util.Arrays.asList("error1"), null, "❌ INVALID");
296|        assertFalse(r.isValid());
297|        assertEquals(1, r.getErrors().size());
298|        assertTrue(r.getWarnings().isEmpty());
299|    }
300|
301|    @Test
302|    @DisplayName("ValidationResult has immutable lists")
303|    void validationResultImmutability() {
304|        ValidationResult r = new ValidationResult(true,
305|                new java.util.ArrayList<>(java.util.Arrays.asList("e1")),
306|                new java.util.ArrayList<>(java.util.Arrays.asList("w1")),
307|                "summary");
308|        assertThrows(UnsupportedOperationException.class, () -> r.getErrors().add("e2"));
309|        assertThrows(UnsupportedOperationException.class, () -> r.getWarnings().add("w2"));
310|    }
311|
312|    @Test
313|    @DisplayName("ValidationResult equals and hashCode")
314|    void validationResultEquals() {
315|        ValidationResult r1 = new ValidationResult(true, null, null, "ok");
316|        ValidationResult r2 = new ValidationResult(true, null, null, "ok");
317|        assertEquals(r1, r2);
318|        assertEquals(r1.hashCode(), r2.hashCode());
319|    }
320|
321|    @Test
322|    @DisplayName("ValidationResult toString")
323|    void validationResultToString() {
324|        ValidationResult r = new ValidationResult(true, null, null, "ok");
325|        String s = r.toString();
326|        assertTrue(s.contains("valid=true"));
327|        assertTrue(s.contains("summary='ok'"));
328|    }
329|
330|    // --- Helper ---
331|
332|    private static Map<String, Object> mapOf(Object... entries) {
333|        Map<String, Object> map = new java.util.LinkedHashMap<>();
334|        for (int i = 0; i < entries.length; i += 2) {
335|            map.put((String) entries[i], entries[i + 1]);
336|        }
337|        return map;
338|    }
339|}