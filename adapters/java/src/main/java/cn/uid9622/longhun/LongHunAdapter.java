1|package cn.uid9622.longhun;
2|
3|import com.fasterxml.jackson.databind.JsonNode;
4|import com.fasterxml.jackson.databind.ObjectMapper;
5|import com.fasterxml.jackson.databind.node.ObjectNode;
6|
7|import javax.annotation.Nonnull;
8|import javax.annotation.Nullable;
9|import java.time.ZonedDateTime;
10|import java.time.ZoneId;
11|import java.time.format.DateTimeFormatter;
12|import java.util.*;
13|
14|/**
15| * LongHun Standard Adapter — wrap JSON payloads with DNA traceability
16| * and seven-factor behavioral audit metadata.
17| *
18| * <p>DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-ADAPTER-v1.0.0
19| * <br>Author: LongHun Core · UID9622 · 龍芯北辰
20| * <br>License: CC BY-NC-SA 4.0
21| *
22| * <p>Usage (Java):
23| * <pre>{@code
24| * LongHunAdapter adapter = new LongHunAdapter("9622", "HM-9622-001");
25| * ObjectNode result = adapter.wrap(data, "code", "P04-Luban");
26| * ValidationResult validation = adapter.validate(result);
27| * }</pre>
28| *
29| * <p>Usage (Kotlin):
30| * <pre>{@code
31| * val adapter = LongHunAdapter("9622", "HM-9622-001")
32| * val result = adapter.wrap(data, "code", "P04-Luban")
33| * val validation = adapter.validate(result)
34| * }</pre>
35| *
36| * @since 1.0.0
37| */
38|public final class LongHunAdapter {
39|
40|    /** Current adapter version. */
41|    public static final String VERSION = "1.0.0";
42|
43|    private static final String AUTHOR = "LongHun Core · UID9622 · 龍芯北辰";
44|    private static final String LICENSE = "CC BY-NC-SA 4.0";
45|    private static final String DNA = "#LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-ADAPTER-v1.0.0-4f7a3b1c";
46|
47|    private static final ObjectMapper MAPPER = new ObjectMapper();
48|
49|    private final String uid;
50|    private final String device;
51|    private final String locale;
52|    private final DNAGenerator dnaGen;
53|    private final AuditWrapper audit;
54|    private final Validator validator;
55|
56|    /**
57|     * Creates a new {@code LongHunAdapter} with default locale "Asia/Shanghai".
58|     *
59|     * @param uid    user identifier (e.g., "9622")
60|     * @param device device identifier (e.g., "HM-9622-001")
61|     * @throws NullPointerException if uid or device is null
62|     */
63|    public LongHunAdapter(@Nonnull String uid, @Nonnull String device) {
64|        this(uid, device, "Asia/Shanghai");
65|    }
66|
67|    /**
68|     * Creates a new {@code LongHunAdapter} with a custom locale.
69|     *
70|     * @param uid    user identifier (e.g., "9622")
71|     * @param device device identifier (e.g., "HM-9622-001")
72|     * @param locale timezone locale (e.g., "Asia/Shanghai")
73|     * @throws NullPointerException if any argument is null
74|     */
75|    public LongHunAdapter(@Nonnull String uid, @Nonnull String device, @Nonnull String locale) {
76|        this.uid = Objects.requireNonNull(uid, "uid must not be null");
77|        this.device = Objects.requireNonNull(device, "device must not be null");
78|        this.locale = Objects.requireNonNull(locale, "locale must not be null");
79|        this.dnaGen = new DNAGenerator(uid, device, locale);
80|        this.audit = new AuditWrapper(uid);
81|        this.validator = new Validator();
82|    }
83|
84|    /**
85|     * Wraps a payload with DNA traceability and audit metadata.
86|     *
87|     * <p>This is the primary entry point for the adapter. The returned
88|     * {@link ObjectNode} contains four keys: {@code dna}, {@code audit},
89|     * {@code payload}, and {@code meta}.
90|     *
91|     * @param data     raw payload (any JSON-serializable object)
92|     * @param taskType task category (code, deploy, audit, default, etc.)
93|     * @param persona  persona identifier (e.g., "P04", "P04-Luban")
94|     * @return {@link ObjectNode} with keys: dna, audit, payload, meta
95|     * @throws NullPointerException if any argument is null
96|     */
97|    @Nonnull
98|    public ObjectNode wrap(@Nonnull Object data,
99|                           @Nonnull String taskType,
100|                           @Nonnull String persona) {
101|        return wrap(data, taskType, persona, "WRAP", null);
102|    }
103|
104|    /**
105|     * Wraps a payload with full control over action and version.
106|     *
107|     * @param data     raw payload (any JSON-serializable object)
108|     * @param taskType task category
109|     * @param persona  persona identifier
110|     * @param action   action descriptor (WRAP, GENERATE, DEPLOY, AUDIT)
111|     * @param version  optional version override (e.g., "v1.0")
112|     * @return {@link ObjectNode} with keys: dna, audit, payload, meta
113|     * @throws NullPointerException if data, taskType, persona, or action is null
114|     */
115|    @Nonnull
116|    public ObjectNode wrap(@Nonnull Object data,
117|                           @Nonnull String taskType,
118|                           @Nonnull String persona,
119|                           @Nonnull String action,
120|                           @Nullable String version) {
121|        Objects.requireNonNull(data, "data must not be null");
122|        Objects.requireNonNull(taskType, "taskType must not be null");
123|        Objects.requireNonNull(persona, "persona must not be null");
124|        Objects.requireNonNull(action, "action must not be null");
125|
126|        // Generate DNA
127|        String dna = dnaGen.generate(taskType, action, version);
128|
129|        // Generate audit wrapper
130|        Map<String, Object> auditResult = audit.wrap(data, taskType, persona);
131|
132|        // Build meta
133|        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(locale));
134|        Map<String, Object> meta = new LinkedHashMap<>();
135|        meta.put("adapter_version", VERSION);
136|        meta.put("uid", uid);
137|        meta.put("device", device);
138|        meta.put("task_type", taskType);
139|        meta.put("persona", persona);
140|        meta.put("generated_at", now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
141|        meta.put("format", "longhun-v\u221E");
142|
143|        // Build result
144|        ObjectNode result = MAPPER.createObjectNode();
145|        result.put("dna", dna);
146|        result.set("audit", MAPPER.valueToTree(auditResult));
147|        result.set("payload", MAPPER.valueToTree(data));
148|        result.set("meta", MAPPER.valueToTree(meta));
149|
150|        return result;
151|    }
152|
153|    /**
154|     * Validates a wrapped payload for LongHun standard compliance.
155|     *
156|     * @param wrapped the {@link ObjectNode} produced by {@link #wrap}
157|     * @return {@link ValidationResult} with valid flag, errors, warnings, and summary
158|     * @throws NullPointerException if wrapped is null
159|     */
160|    @Nonnull
161|    public ValidationResult validate(@Nonnull ObjectNode wrapped) {
162|        Objects.requireNonNull(wrapped, "wrapped must not be null");
163|        return validator.validate(wrapped);
164|    }
165|
166|    /**
167|     * Returns JSON Schemas for DNA and Audit formats.
168|     *
169|     * @return map with keys: "dna_schema", "audit_schema"
170|     */
171|    @Nonnull
172|    public Map<String, Object> getSchemas() {
173|        Map<String, Object> schemas = new LinkedHashMap<>();
174|        schemas.put("dna_schema", Schemas.getDnaSchema());
175|        schemas.put("audit_schema", Schemas.getAuditSchema());
176|        return schemas;
177|    }
178|
179|    // --- Accessors ---
180|
181|    /**
182|     * Returns the user identifier.
183|     *
184|     * @return uid string
185|     */
186|    @Nonnull
187|    public String getUid() {
188|        return uid;
189|    }
190|
191|    /**
192|     * Returns the device identifier.
193|     *
194|     * @return device string
195|     */
196|    @Nonnull
197|    public String getDevice() {
198|        return device;
199|    }
200|
201|    /**
202|     * Returns the timezone locale.
203|     *
204|     * @return locale string (e.g., "Asia/Shanghai")
205|     */
206|    @Nonnull
207|    public String getLocale() {
208|        return locale;
209|    }
210|
211|    /**
212|     * Returns the adapter version.
213|     *
214|     * @return "1.0.0"
215|     */
216|    @Nonnull
217|    public static String getVersion() {
218|        return VERSION;
219|    }
220|
221|    /**
222|     * Returns the adapter author string.
223|     *
224|     * @return author string
225|     */
226|    @Nonnull
227|    public static String getAuthor() {
228|        return AUTHOR;
229|    }
230|
231|    /**
232|     * Returns the adapter license string.
233|     *
234|     * @return "CC BY-NC-SA 4.0"
235|     */
236|    @Nonnull
237|    public static String getLicense() {
238|        return LICENSE;
239|    }
240|
241|    /**
242|     * Returns the adapter DNA signature.
243|     *
244|     * @return DNA string
245|     */
246|    @Nonnull
247|    public static String getDna() {
248|        return DNA;
249|    }
250|
251|    /**
252|     * Returns the internal {@link DNAGenerator} (for advanced use).
253|     *
254|     * @return DNAGenerator instance
255|     */
256|    @Nonnull
257|    public DNAGenerator getDnaGenerator() {
258|        return dnaGen;
259|    }
260|
261|    /**
262|     * Returns the internal {@link AuditWrapper} (for advanced use).
263|     *
264|     * @return AuditWrapper instance
265|     */
266|    @Nonnull
267|    public AuditWrapper getAuditWrapper() {
268|        return audit;
269|    }
270|
271|    /**
272|     * Returns the internal {@link Validator} (for advanced use).
273|     *
274|     * @return Validator instance
275|     */
276|    @Nonnull
277|    public Validator getValidator() {
278|        return validator;
279|    }
280|}