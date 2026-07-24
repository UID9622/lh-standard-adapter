1|package cn.uid9622.longhun;
2|
3|import javax.annotation.Nonnull;
4|import java.util.LinkedHashMap;
5|import java.util.Map;
6|
7|/**
8| * JSON Schema definitions for LongHun DNA and Audit formats.
9| *
10| * <p>These schemas match the Python reference implementation in
11| * {@code lh_standard_adapter/schemas/__init__.py}.
12| *
13| * @since 1.0.0
14| */
15|public final class Schemas {
16|
17|    private Schemas() {
18|        // utility class
19|    }
20|
21|    /**
22|     * Returns the JSON Schema for the DNA traceability code.
23|     *
24|     * @return map representing the DNA JSON Schema
25|     */
26|    @Nonnull
27|    @SuppressWarnings("unchecked")
28|    public static Map<String, Object> getDnaSchema() {
29|        return DNA_SCHEMA;
30|    }
31|
32|    /**
33|     * Returns the JSON Schema for the audit record.
34|     *
35|     * @return map representing the Audit JSON Schema
36|     */
37|    @Nonnull
38|    @SuppressWarnings("unchecked")
39|    public static Map<String, Object> getAuditSchema() {
40|        return AUDIT_SCHEMA;
41|    }
42|
43|    // --- DNA Schema ---
44|
45|    @SuppressWarnings("unchecked")
46|    private static final Map<String, Object> DNA_SCHEMA = buildDnaSchema();
47|
48|    private static Map<String, Object> buildDnaSchema() {
49|        Map<String, Object> schema = new LinkedHashMap<>();
50|        schema.put("$schema", "https://json-schema.org/draft/2020-12/schema");
51|        schema.put("$id", "https://uid9622.cn/schemas/dna-v1.0.json");
52|        schema.put("title", "LongHun DNA Traceability Code");
53|        schema.put("type", "object");
54|
55|        // required
56|        schema.put("required", listOf("dna", "format", "uid", "timestamp"));
57|
58|        // properties
59|        Map<String, Object> properties = new LinkedHashMap<>();
60|
61|        Map<String, Object> dnaProp = new LinkedHashMap<>();
62|        dnaProp.put("type", "string");
63|        dnaProp.put("description", "Full v∞ DNA traceability code");
64|        dnaProp.put("pattern", "^#LongHun⚡️[A-Z][a-zA-Z]+·[A-Z][a-zA-Z]+·[A-Z][a-zA-Z]+·[A-Z][a-zA-Z]+·[䷀-䷿][A-Za-z]+-.+-[a-f0-9]{8}$");
65|        properties.put("dna", dnaProp);
66|
67|        Map<String, Object> formatProp = new LinkedHashMap<>();
68|        formatProp.put("type", "string");
69|        formatProp.put("enum", listOf("v1.0", "v2.0", "v∞", "compact"));
70|        properties.put("format", formatProp);
71|
72|        Map<String, Object> uidProp = new LinkedHashMap<>();
73|        uidProp.put("type", "string");
74|        uidProp.put("pattern", "^UID\\d+$");
75|        properties.put("uid", uidProp);
76|
77|        Map<String, Object> deviceProp = new LinkedHashMap<>();
78|        deviceProp.put("type", "string");
79|        properties.put("device", deviceProp);
80|
81|        Map<String, Object> tsProp = new LinkedHashMap<>();
82|        tsProp.put("type", "string");
83|        tsProp.put("format", "date-time");
84|        properties.put("timestamp", tsProp);
85|
86|        schema.put("properties", properties);
87|        return schema;
88|    }
89|
90|    // --- Audit Schema ---
91|
92|    @SuppressWarnings("unchecked")
93|    private static final Map<String, Object> AUDIT_SCHEMA = buildAuditSchema();
94|
95|    private static Map<String, Object> buildAuditSchema() {
96|        Map<String, Object> schema = new LinkedHashMap<>();
97|        schema.put("$schema", "https://json-schema.org/draft/2020-12/schema");
98|        schema.put("$id", "https://uid9622.cn/schemas/audit-v1.0.json");
99|        schema.put("title", "LongHun Audit Record");
100|        schema.put("type", "object");
101|
102|        // required
103|        schema.put("required", listOf("dna", "audit", "payload", "meta"));
104|
105|        // properties
106|        Map<String, Object> properties = new LinkedHashMap<>();
107|
108|        // dna
109|        Map<String, Object> dnaProp = new LinkedHashMap<>();
110|        dnaProp.put("type", "string");
111|        properties.put("dna", dnaProp);
112|
113|        // audit
114|        Map<String, Object> auditProp = buildAuditObjectSchema();
115|        properties.put("audit", auditProp);
116|
117|        // payload (any)
118|        properties.put("payload", new LinkedHashMap<>());
119|
120|        // meta
121|        Map<String, Object> metaProp = buildMetaSchema();
122|        properties.put("meta", metaProp);
123|
124|        schema.put("properties", properties);
125|        return schema;
126|    }
127|
128|    @SuppressWarnings("unchecked")
129|    private static Map<String, Object> buildAuditObjectSchema() {
130|        Map<String, Object> audit = new LinkedHashMap<>();
131|        audit.put("type", "object");
132|        audit.put("required", listOf(
133|                "audit_version", "uid", "behavior_signature",
134|                "behavior_pattern", "behavior_labels", "color"
135|        ));
136|
137|        Map<String, Object> auditProps = new LinkedHashMap<>();
138|
139|        auditProps.put("audit_version", mapOf("type", "string"));
140|        auditProps.put("uid", mapOf("type", "string"));
141|        auditProps.put("persona", mapOf("type", "string"));
142|        auditProps.put("task_type", mapOf("type", "string"));
143|
144|        // behavior_signature
145|        Map<String, Object> sig = new LinkedHashMap<>();
146|        sig.put("type", "object");
147|        sig.put("required", listOf("P", "F", "T", "E", "C", "R", "A", "X", "Y", "Z"));
148|
149|        Map<String, Object> sigProps = new LinkedHashMap<>();
150|        sigProps.put("P", mapOf("enum", listOf("HasPromise", "NoPromise")));
151|        sigProps.put("F", mapOf("enum", listOf("Fulfilled", "Unfulfilled", "Partial")));
152|        sigProps.put("T", mapOf("type", "number"));
153|        sigProps.put("E", mapOf("enum", listOf("Willing", "Perfunctory", "Resentful", "Numb")));
154|        sigProps.put("C", mapOf("type", "number"));
155|        sigProps.put("R", mapOf("type", "integer", "minimum", 0));
156|        sigProps.put("A", mapOf("enum", listOf("Self", "Partner", "Family", "Outsider", "Public")));
157|        sigProps.put("X", mapOf("enum", listOf("OverExplain", "Silent", "Genuine", "Indifferent")));
158|        sigProps.put("Y", mapOf("enum", listOf("Changed", "Resisted", "Indifferent", "NoResponse")));
159|        sigProps.put("Z", mapOf("type", "number"));
160|        sig.put("properties", sigProps);
161|
162|        auditProps.put("behavior_signature", sig);
163|
164|        // behavior_pattern
165|        auditProps.put("behavior_pattern", mapOf("enum", listOf(
166|                "MODE-DefensiveDefaulter", "MODE-ExternalTrustSpender",
167|                "MODE-InternalDestroyer", "MODE-Fluctuating", "MODE-StableDisciplined"
168|        )));
169|
170|        // behavior_labels
171|        Map<String, Object> labels = new LinkedHashMap<>();
172|        labels.put("type", "array");
173|        labels.put("items", mapOf("type", "string"));
174|        auditProps.put("behavior_labels", labels);
175|
176|        // color
177|        auditProps.put("color", mapOf("enum", listOf("\uD83D\uDFE2", "\uD83D\uDFE1", "\uD83D\uDD34")));
178|
179|        // timestamp
180|        Map<String, Object> ts = new LinkedHashMap<>();
181|        ts.put("type", "string");
182|        ts.put("format", "date-time");
183|        auditProps.put("timestamp", ts);
184|
185|        // payload_hash
186|        Map<String, Object> ph = new LinkedHashMap<>();
187|        ph.put("type", "string");
188|        ph.put("pattern", "^[a-f0-9]{16}$");
189|        auditProps.put("payload_hash", ph);
190|
191|        audit.put("properties", auditProps);
192|        return audit;
193|    }
194|
195|    @SuppressWarnings("unchecked")
196|    private static Map<String, Object> buildMetaSchema() {
197|        Map<String, Object> meta = new LinkedHashMap<>();
198|        meta.put("type", "object");
199|        meta.put("required", listOf("adapter_version", "uid", "device", "task_type", "persona"));
200|
201|        Map<String, Object> metaProps = new LinkedHashMap<>();
202|        metaProps.put("adapter_version", mapOf("type", "string"));
203|        metaProps.put("uid", mapOf("type", "string"));
204|        metaProps.put("device", mapOf("type", "string"));
205|        metaProps.put("task_type", mapOf("type", "string"));
206|        metaProps.put("persona", mapOf("type", "string"));
207|
208|        Map<String, Object> genAt = new LinkedHashMap<>();
209|        genAt.put("type", "string");
210|        genAt.put("format", "date-time");
211|        metaProps.put("generated_at", genAt);
212|
213|        metaProps.put("format", mapOf("const", "longhun-v\u221E"));
214|
215|        meta.put("properties", metaProps);
216|        return meta;
217|    }
218|
219|    // --- Utility helpers ---
220|
221|    private static java.util.List<String> listOf(String... values) {
222|        java.util.List<String> list = new java.util.ArrayList<>();
223|        java.util.Collections.addAll(list, values);
224|        return list;
225|    }
226|
227|    private static Map<String, Object> mapOf(Object... entries) {
228|        Map<String, Object> map = new LinkedHashMap<>();
229|        for (int i = 0; i < entries.length; i += 2) {
230|            map.put((String) entries[i], entries[i + 1]);
231|        }
232|        return map;
233|    }
234|}