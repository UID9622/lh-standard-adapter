1|package cn.uid9622.longhun;
2|
3|import javax.annotation.Nonnull;
4|import java.nio.charset.StandardCharsets;
5|import java.security.MessageDigest;
6|import java.security.NoSuchAlgorithmException;
7|import java.time.ZoneId;
8|import java.time.ZonedDateTime;
9|import java.time.format.DateTimeFormatter;
10|import java.util.*;
11|
12|/**
13| * Wraps payloads with seven-factor behavioral audit metadata.
14| *
15| * <p>Core scoring algorithms (weights, neural network logic) are
16| * protected engine components and NOT included in this open shell.
17| *
18| * <p>DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷝Li-AUDIT-WRAPPER-v1.0.0
19| *
20| * @since 1.0.0
21| */
22|public final class AuditWrapper {
23|
24|    // --- Seven-Factor Value Sets ---
25|    private static final Set<String> P_VALUES = setOf("HasPromise", "NoPromise");
26|    private static final Set<String> F_VALUES = setOf("Fulfilled", "Unfulfilled", "Partial");
27|    private static final Set<String> E_VALUES = setOf("Willing", "Perfunctory", "Resentful", "Numb");
28|    private static final Set<String> A_VALUES = setOf("Self", "Partner", "Family", "Outsider", "Public");
29|    private static final Set<String> X_VALUES = setOf("OverExplain", "Silent", "Genuine", "Indifferent");
30|    private static final Set<String> Y_VALUES = setOf("Changed", "Resisted", "Indifferent", "NoResponse");
31|
32|    // --- Behavior Pattern Classification ---
33|    private static final Map<String, String> PATTERNS = new LinkedHashMap<>();
34|
35|    static {
36|        PATTERNS.put("MODE-DefensiveDefaulter", "Promises fail + over-explains to deflect");
37|        PATTERNS.put("MODE-ExternalTrustSpender", "Keeps promises to outsiders at inner-circle expense");
38|        PATTERNS.put("MODE-InternalDestroyer", "Breaks promises with indifference, no correction");
39|        PATTERNS.put("MODE-Fluctuating", "High volatility in commitment-to-fulfillment ratio");
40|        PATTERNS.put("MODE-StableDisciplined", "Consistent, reliable execution");
41|    }
42|
43|    // --- Factor → Label Mapping (bilingual) ---
44|    private static final Map<String, Map<String, String>> LABEL_MAP = new LinkedHashMap<>();
45|
46|    static {
47|        LABEL_MAP.put("P", mapOf("HasPromise", "7F-P-有承诺", "NoPromise", "7F-P-无承诺"));
48|        LABEL_MAP.put("F", mapOf("Fulfilled", "7F-F-已兑现", "Unfulfilled", "7F-F-未兑现", "Partial", "7F-F-部分兑现"));
49|        LABEL_MAP.put("E", mapOf("Willing", "7F-E-心甘情愿", "Perfunctory", "7F-E-敷衍", "Resentful", "7F-E-怨恨", "Numb", "7F-E-麻木"));
50|        LABEL_MAP.put("A", mapOf("Self", "7F-A-自己", "Partner", "7F-A-伴侣", "Family", "7F-A-家庭", "Outsider", "7F-A-外人", "Public", "7F-A-公众"));
51|        LABEL_MAP.put("X", mapOf("OverExplain", "7F-X-过度解释", "Silent", "7F-X-沉默", "Genuine", "7F-X-真诚", "Indifferent", "7F-X-冷漠"));
52|        LABEL_MAP.put("Y", mapOf("Changed", "7F-Y-改正", "Resisted", "7F-Y-抗拒", "Indifferent", "7F-Y-无视", "NoResponse", "7F-Y-无响应"));
53|    }
54|
55|    private final String uid;
56|
57|    /**
58|     * Creates a new {@code AuditWrapper}.
59|     *
60|     * @param uid user identifier (e.g., "9622")
61|     */
62|    public AuditWrapper(String uid) {
63|        this.uid = Objects.requireNonNull(uid, "uid must not be null");
64|    }
65|
66|    /**
67|     * Generates the audit wrapper with seven-factor signature.
68|     *
69|     * @param payload  raw data to wrap (must be JSON-serializable)
70|     * @param taskType task category (code, deploy, audit, default, etc.)
71|     * @param persona  persona identifier (e.g., "P04", "P04-Luban")
72|     * @return map of audit metadata
73|     */
74|    @Nonnull
75|    public Map<String, Object> wrap(@Nonnull Object payload,
76|                                    @Nonnull String taskType,
77|                                    @Nonnull String persona) {
78|        Objects.requireNonNull(payload, "payload must not be null");
79|        Objects.requireNonNull(taskType, "taskType must not be null");
80|        Objects.requireNonNull(persona, "persona must not be null");
81|
82|        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
83|
84|        // Default signature (StableDisciplined baseline)
85|        Map<String, Object> signature = new LinkedHashMap<>();
86|        signature.put("P", "HasPromise");
87|        signature.put("F", "Fulfilled");
88|        signature.put("T", 0.0);
89|        signature.put("E", "Willing");
90|        signature.put("C", 0);
91|        signature.put("R", 0);
92|        signature.put("A", "Self");
93|        signature.put("X", "Genuine");
94|        signature.put("Y", "NoResponse");
95|        signature.put("Z", 1.0);
96|
97|        String pattern = classify(signature);
98|        List<String> labels = makeLabels(signature, pattern);
99|        String color = determineColor(pattern, (Integer) signature.get("R"));
100|
101|        // Payload hash (not for crypto, for integrity check)
102|        String payloadJson = payload.toString(); // simplified; Jackson serialization happens upstream
103|        String payloadHash = sha256Hex(payloadJson).substring(0, 16);
104|
105|        Map<String, Object> result = new LinkedHashMap<>();
106|        result.put("audit_version", "v1.0");
107|        result.put("uid", "UID" + uid);
108|        result.put("persona", persona);
109|        result.put("task_type", taskType);
110|        result.put("behavior_signature", signature);
111|        result.put("behavior_pattern", pattern);
112|        result.put("behavior_labels", labels);
113|        result.put("color", color);
114|        result.put("timestamp", now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
115|        result.put("payload_hash", payloadHash);
116|
117|        return result;
118|    }
119|
120|    /**
121|     * Classifies a seven-factor signature into a behavior pattern.
122|     *
123|     * @param sig the behavior signature map
124|     * @return behavior pattern string (e.g., "MODE-StableDisciplined")
125|     */
126|    @Nonnull
127|    String classify(@Nonnull Map<String, Object> sig) {
128|        String fVal = (String) sig.getOrDefault("F", "");
129|        String xVal = (String) sig.getOrDefault("X", "");
130|        String aVal = (String) sig.getOrDefault("A", "");
131|        String yVal = (String) sig.getOrDefault("Y", "");
132|        Number zVal = (Number) sig.getOrDefault("Z", 1.0);
133|
134|        if ("Unfulfilled".equals(fVal) && "OverExplain".equals(xVal)) {
135|            return "MODE-DefensiveDefaulter";
136|        }
137|        if ("Fulfilled".equals(fVal) && "Outsider".equals(aVal)) {
138|            return "MODE-ExternalTrustSpender";
139|        }
140|        if ("Unfulfilled".equals(fVal) && "Indifferent".equals(yVal)) {
141|            return "MODE-InternalDestroyer";
142|        }
143|        if (zVal.doubleValue() > 2.0) {
144|            return "MODE-Fluctuating";
145|        }
146|        return "MODE-StableDisciplined";
147|    }
148|
149|    /**
150|     * Generates bilingual behavior labels from a signature.
151|     *
152|     * @param sig     the behavior signature map
153|     * @param pattern the classified behavior pattern
154|     * @return list of bilingual label strings
155|     */
156|    @Nonnull
157|    List<String> makeLabels(@Nonnull Map<String, Object> sig, @Nonnull String pattern) {
158|        List<String> labels = new ArrayList<>();
159|        for (String factor : Arrays.asList("P", "F", "E", "A", "X", "Y")) {
160|            String val = (String) sig.get(factor);
161|            if (val != null) {
162|                Map<String, String> factorLabels = LABEL_MAP.get(factor);
163|                if (factorLabels != null && factorLabels.containsKey(val)) {
164|                    labels.add(factorLabels.get(val));
165|                }
166|            }
167|        }
168|        labels.add(pattern);
169|        return labels;
170|    }
171|
172|    /**
173|     * Determines the three-color audit tag.
174|     *
175|     * @param pattern the behavior pattern
176|     * @param repeat  repeat count (R)
177|     * @return color emoji string: 🟢, 🟡, or 🔴
178|     */
179|    @Nonnull
180|    String determineColor(@Nonnull String pattern, int repeat) {
181|        if ("MODE-InternalDestroyer".equals(pattern)) {
182|            return "\uD83D\uDD34"; // 🔴
183|        }
184|        if ("MODE-Fluctuating".equals(pattern) && repeat > 3) {
185|            return "\uD83D\uDFE1"; // 🟡
186|        }
187|        if ("MODE-DefensiveDefaulter".equals(pattern) && repeat > 2) {
188|            return "\uD83D\uDFE1"; // 🟡
189|        }
190|        return "\uD83D\uDFE2"; // 🟢
191|    }
192|
193|    @Nonnull
194|    String getUid() {
195|        return uid;
196|    }
197|
198|    @Nonnull
199|    static Set<String> getPValues() {
200|        return P_VALUES;
201|    }
202|
203|    @Nonnull
204|    static Set<String> getFValues() {
205|        return F_VALUES;
206|    }
207|
208|    @Nonnull
209|    static Set<String> getEValues() {
210|        return E_VALUES;
211|    }
212|
213|    @Nonnull
214|    static Set<String> getAValues() {
215|        return A_VALUES;
216|    }
217|
218|    @Nonnull
219|    static Set<String> getXValues() {
220|        return X_VALUES;
221|    }
222|
223|    @Nonnull
224|    static Set<String> getYValues() {
225|        return Y_VALUES;
226|    }
227|
228|    @Nonnull
229|    static Map<String, String> getPatterns() {
230|        return PATTERNS;
231|    }
232|
233|    @Nonnull
234|    static Map<String, Map<String, String>> getLabelMap() {
235|        return LABEL_MAP;
236|    }
237|
238|    // --- Private helpers ---
239|
240|    private static Set<String> setOf(String... values) {
241|        Set<String> set = new LinkedHashSet<>();
242|        Collections.addAll(set, values);
243|        return Collections.unmodifiableSet(set);
244|    }
245|
246|    private static Map<String, String> mapOf(String... entries) {
247|        Map<String, String> map = new LinkedHashMap<>();
248|        for (int i = 0; i < entries.length; i += 2) {
249|            map.put(entries[i], entries[i + 1]);
250|        }
251|        return map;
252|    }
253|
254|    @Nonnull
255|    private static String sha256Hex(@Nonnull String input) {
256|        try {
257|            MessageDigest md = MessageDigest.getInstance("SHA-256");
258|            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
259|            StringBuilder sb = new StringBuilder(64);
260|            for (byte b : digest) {
261|                sb.append(String.format("%02x", b & 0xff));
262|            }
263|            return sb.toString();
264|        } catch (NoSuchAlgorithmException e) {
265|            throw new RuntimeException("SHA-256 not available", e);
266|        }
267|    }
268|}