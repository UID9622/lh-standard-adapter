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
14| * Tests for {@link LongHunAdapter}.
15| */
16|@DisplayName("LongHunAdapter")
17|class LongHunAdapterTest {
18|
19|    private static final ObjectMapper MAPPER = new ObjectMapper();
20|    private LongHunAdapter adapter;
21|
22|    @BeforeEach
23|    void setUp() {
24|        adapter = new LongHunAdapter("9622", "HM-9622-001");
25|    }
26|
27|    @Test
28|    @DisplayName("wrap produces all four top-level keys")
29|    void wrapProducesTopLevelKeys() {
30|        ObjectNode result = adapter.wrap("hello", "code", "P04");
31|        assertNotNull(result);
32|        assertTrue(result.has("dna"));
33|        assertTrue(result.has("audit"));
34|        assertTrue(result.has("payload"));
35|        assertTrue(result.has("meta"));
36|    }
37|
38|    @Test
39|    @DisplayName("wrap preserves payload data")
40|    void wrapPreservesPayload() {
41|        Map<String, Object> data = mapOf("name", "test", "value", 42);
42|        ObjectNode result = adapter.wrap(data, "code", "P04");
43|        assertEquals("test", result.get("payload").get("name").asText());
44|        assertEquals(42, result.get("payload").get("value").asInt());
45|    }
46|
47|    @Test
48|    @DisplayName("wrap dna starts with prefix")
49|    void wrapDnaPrefix() {
50|        ObjectNode result = adapter.wrap("test", "code", "P04");
51|        String dna = result.get("dna").asText();
52|        assertTrue(dna.startsWith("#LongHun⚡️"));
53|    }
54|
55|    @Test
56|    @DisplayName("wrap audit has behavior_signature")
57|    void wrapAuditSignature() {
58|        ObjectNode result = adapter.wrap("test", "code", "P04");
59|        assertTrue(result.get("audit").has("behavior_signature"));
60|    }
61|
62|    @Test
63|    @DisplayName("wrap meta has adapter_version")
64|    void wrapMetaVersion() {
65|        ObjectNode result = adapter.wrap("test", "code", "P04");
66|        assertEquals("1.0.0", result.get("meta").get("adapter_version").asText());
67|    }
68|
69|    @Test
70|    @DisplayName("wrap meta has uid matching constructor")
71|    void wrapMetaUid() {
72|        ObjectNode result = adapter.wrap("test", "code", "P04");
73|        assertEquals("9622", result.get("meta").get("uid").asText());
74|    }
75|
76|    @Test
77|    @DisplayName("wrap meta has device matching constructor")
78|    void wrapMetaDevice() {
79|        ObjectNode result = adapter.wrap("test", "code", "P04");
80|        assertEquals("HM-9622-001", result.get("meta").get("device").asText());
81|    }
82|
83|    @Test
84|    @DisplayName("wrap meta has task_type")
85|    void wrapMetaTaskType() {
86|        ObjectNode result = adapter.wrap("test", "audit", "P04");
87|        assertEquals("audit", result.get("meta").get("task_type").asText());
88|    }
89|
90|    @Test
91|    @DisplayName("wrap meta has persona")
92|    void wrapMetaPersona() {
93|        ObjectNode result = adapter.wrap("test", "code", "P04-Luban");
94|        assertEquals("P04-Luban", result.get("meta").get("persona").asText());
95|    }
96|
97|    @Test
98|    @DisplayName("wrap meta has format longhun-v∞")
99|    void wrapMetaFormat() {
100|        ObjectNode result = adapter.wrap("test", "code", "P04");
101|        assertEquals("longhun-v∞", result.get("meta").get("format").asText());
102|    }
103|
104|    @Test
105|    @DisplayName("wrap meta has generated_at timestamp")
106|    void wrapMetaTimestamp() {
107|        ObjectNode result = adapter.wrap("test", "code", "P04");
108|        assertTrue(result.get("meta").get("generated_at").asText().contains("T"));
109|    }
110|
111|    @Test
112|    @DisplayName("wrap with custom locale")
113|    void wrapCustomLocale() {
114|        LongHunAdapter custom = new LongHunAdapter("9622", "HM-9622-002", "America/New_York");
115|        ObjectNode result = custom.wrap("test", "code", "P04");
116|        assertTrue(result.get("dna").asText().startsWith("#LongHun⚡️"));
117|    }
118|
119|    @Test
120|    @DisplayName("wrap with null data throws NPE")
121|    void wrapNullData() {
122|        assertThrows(NullPointerException.class, () -> adapter.wrap(null, "code", "P04"));
123|    }
124|
125|    @Test
126|    @DisplayName("wrap with null taskType throws NPE")
127|    void wrapNullTaskType() {
128|        assertThrows(NullPointerException.class, () -> adapter.wrap("data", null, "P04"));
129|    }
130|
131|    @Test
132|    @DisplayName("wrap with null persona throws NPE")
133|    void wrapNullPersona() {
134|        assertThrows(NullPointerException.class, () -> adapter.wrap("data", "code", null));
135|    }
136|
137|    @Test
138|    @DisplayName("getSchemas returns non-null map")
139|    void getSchemasNonNull() {
140|        Map<String, Object> schemas = adapter.getSchemas();
141|        assertNotNull(schemas);
142|        assertEquals(2, schemas.size());
143|    }
144|
145|    @Test
146|    @DisplayName("getVersion returns 1.0.0")
147|    void getVersion() {
148|        assertEquals("1.0.0", LongHunAdapter.getVersion());
149|    }
150|
151|    @Test
152|    @DisplayName("getAuthor returns author string")
153|    void getAuthor() {
154|        String author = LongHunAdapter.getAuthor();
155|        assertNotNull(author);
156|        assertTrue(author.contains("LongHun"));
157|    }
158|
159|    @Test
160|    @DisplayName("getLicense returns CC BY-NC-SA 4.0")
161|    void getLicense() {
162|        assertEquals("CC BY-NC-SA 4.0", LongHunAdapter.getLicense());
163|    }
164|
165|    @Test
166|    @DisplayName("getDna returns DNA string")
167|    void getDna() {
168|        String dna = LongHunAdapter.getDna();
169|        assertNotNull(dna);
170|        assertTrue(dna.startsWith("#LongHun⚡️"));
171|    }
172|
173|    @Test
174|    @DisplayName("adapter has correct uid")
175|    void getUid() {
176|        assertEquals("9622", adapter.getUid());
177|    }
178|
179|    @Test
180|    @DisplayName("adapter has correct device")
181|    void getDevice() {
182|        assertEquals("HM-9622-001", adapter.getDevice());
183|    }
184|
185|    @Test
186|    @DisplayName("adapter has correct locale")
187|    void getLocale() {
188|        assertEquals("Asia/Shanghai", adapter.getLocale());
189|    }
190|
191|    @Test
192|    @DisplayName("getDnaGenerator returns non-null")
193|    void getDnaGenerator() {
194|        assertNotNull(adapter.getDnaGenerator());
195|    }
196|
197|    @Test
198|    @DisplayName("getAuditWrapper returns non-null")
199|    void getAuditWrapper() {
200|        assertNotNull(adapter.getAuditWrapper());
201|    }
202|
203|    @Test
204|    @DisplayName("getValidator returns non-null")
205|    void getValidator() {
206|        assertNotNull(adapter.getValidator());
207|    }
208|
209|    @Test
210|    @DisplayName("wrap with extended signature")
211|    void wrapExtended() {
212|        ObjectNode result = adapter.wrap("data", "code", "P04", "GENERATE", "v2.0");
213|        String dna = result.get("dna").asText();
214|        assertTrue(dna.contains("ADAPTER-CODE-GENERATE-V2.0"));
215|    }
216|
217|    @Test
218|    @DisplayName("wrap with complex nested payload")
219|    void wrapComplexPayload() {
220|        Map<String, Object> data = mapOf(
221|                "level1", mapOf(
222|                        "level2", mapOf(
223|                                "value", 42
224|                        )
225|                ),
226|                "array", java.util.Arrays.asList(1, 2, 3)
227|        );
228|        ObjectNode result = adapter.wrap(data, "code", "P04");
229|        assertEquals(42, result.get("payload").get("level1").get("level2").get("value").asInt());
230|    }
231|
232|    @Test
233|    @DisplayName("wrap with numeric payload")
234|    void wrapNumericPayload() {
235|        ObjectNode result = adapter.wrap(42, "code", "P04");
236|        assertEquals(42, result.get("payload").asInt());
237|    }
238|
239|    @Test
240|    @DisplayName("wrap with boolean payload")
241|    void wrapBooleanPayload() {
242|        ObjectNode result = adapter.wrap(true, "code", "P04");
243|        assertTrue(result.get("payload").asBoolean());
244|    }
245|
246|    @Test
247|    @DisplayName("wrap with list payload")
248|    void wrapListPayload() {
249|        ObjectNode result = adapter.wrap(java.util.Arrays.asList("a", "b", "c"), "code", "P04");
250|        assertTrue(result.get("payload").isArray());
251|        assertEquals(3, result.get("payload").size());
252|    }
253|
254|    @Test
255|    @DisplayName("wrap with null version defaults to V1.0")
256|    void wrapNullVersion() {
257|        ObjectNode result = adapter.wrap("test", "code", "P04", "WRAP", null);
258|        String dna = result.get("dna").asText();
259|        assertTrue(dna.contains("-WRAP-V1.0"), "DNA should contain V1.0: " + dna);
260|    }
261|
262|    @Test
263|    @DisplayName("wrap produces valid full cycle")
264|    void wrapValidateCycle() {
265|        ObjectNode result = adapter.wrap(mapOf("key", "value"), "default", "P04");
266|        ValidationResult validation = adapter.validate(result);
267|        assertTrue(validation.isValid());
268|    }
269|
270|    @Test
271|    @DisplayName("constructor with null uid throws NPE")
272|    void constructorNullUid() {
273|        assertThrows(NullPointerException.class, () -> new LongHunAdapter(null, "device"));
274|    }
275|
276|    @Test
277|    @DisplayName("constructor with null device throws NPE")
278|    void constructorNullDevice() {
279|        assertThrows(NullPointerException.class, () -> new LongHunAdapter("uid", null));
280|    }
281|
282|    // --- Schemas tests ---
283|
284|    @Test
285|    @DisplayName("Schemas.getDnaSchema returns non-null")
286|    void schemasGetDnaSchema() {
287|        assertNotNull(Schemas.getDnaSchema());
288|    }
289|
290|    @Test
291|    @DisplayName("Schemas.getAuditSchema returns non-null")
292|    void schemasGetAuditSchema() {
293|        assertNotNull(Schemas.getAuditSchema());
294|    }
295|
296|    @Test
297|    @DisplayName("Schemas.getDnaSchema has correct $schema")
298|    void schemasDnaSchemaField() {
299|        Map<String, Object> schema = Schemas.getDnaSchema();
300|        assertEquals("https://json-schema.org/draft/2020-12/schema", schema.get("$schema"));
301|    }
302|
303|    @Test
304|    @DisplayName("Schemas.getAuditSchema has correct $schema")
305|    void schemasAuditSchemaField() {
306|        Map<String, Object> schema = Schemas.getAuditSchema();
307|        assertEquals("https://json-schema.org/draft/2020-12/schema", schema.get("$schema"));
308|    }
309|
310|    // --- Helper ---
311|
312|    private static Map<String, Object> mapOf(Object... entries) {
313|        Map<String, Object> map = new java.util.LinkedHashMap<>();
314|        for (int i = 0; i < entries.length; i += 2) {
315|            map.put((String) entries[i], entries[i + 1]);
316|        }
317|        return map;
318|    }
319|}