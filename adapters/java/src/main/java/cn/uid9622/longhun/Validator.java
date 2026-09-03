1|package cn.uid9622.longhun;
2|
3|import com.fasterxml.jackson.databind.JsonNode;
4|import com.fasterxml.jackson.databind.node.ObjectNode;
5|
6|import javax.annotation.Nonnull;
7|import java.util.*;
8|import java.util.regex.Pattern;
9|
10|/**
11| * Validates wrapped payloads for LongHun standard compliance.
12| *
13| * <p>DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷝Li-VALIDATOR-v1.0.0
14| *
15| * @since 1.0.0
16| */
17|public final class Validator {
18|
19|    // DNA v∞ validation regex — matches Python reference
20|    private static final Pattern DNA_REGEX = Pattern.compile(
21|            "^#LongHun⚡️"
22|                    + "([A-Z][a-zA-Z]+·[A-Z][a-zA-Z]+·[A-Z][a-zA-Z]+·[A-Z][a-zA-Z]+)"  // Four pillars
23|                    + "·([䷀-䷿][A-Za-z]+)"                                            // Hexagram
24|                    + "-(.+)"                                                          // Body (module-action-version)
25|                    + "-([a-f0-9]{8})$"                                                // Hash8
26|    );
27|
28|    private static final Set<String> REQUIRED_TOP_KEYS = setOf("dna", "audit", "payload", "meta");
29|    private static final Set<String> REQUIRED_AUDIT_KEYS = setOf(
30|            "audit_version", "uid", "behavior_signature",
31|            "behavior_pattern", "behavior_labels", "color"
32|    );
33|    private static final Set<String> REQUIRED_SIG_KEYS = setOf(
34|            "P", "F", "T", "E", "C", "R", "A", "X", "Y", "Z"
35|    );
36|    private static final Set<String> VALID_COLORS = setOf("\uD83D\uDFE2", "\uD83D\uDFE1", "\uD83D\uDD34"); // 🟢🟡🔴
37|    private static final Set<String> VALID_PATTERNS = setOf(
38|            "MODE-DefensiveDefaulter", "MODE-ExternalTrustSpender",
39|            "MODE-InternalDestroyer", "MODE-Fluctuating", "MODE-StableDisciplined"
40|    );
41|    private static final Set<String> VALID_P_VALUES = setOf("HasPromise", "NoPromise");
42|    private static final Set<String> VALID_F_VALUES = setOf("Fulfilled", "Unfulfilled", "Partial");
43|    private static final Set<String> VALID_E_VALUES = setOf("Willing", "Perfunctory", "Resentful", "Numb");
44|    private static final Set<String> VALID_A_VALUES = setOf("Self", "Partner", "Family", "Outsider", "Public");
45|    private static final Set<String> VALID_X_VALUES = setOf("OverExplain", "Silent", "Genuine", "Indifferent");
46|    private static final Set<String> VALID_Y_VALUES = setOf("Changed", "Resisted", "Indifferent", "NoResponse");
47|
48|    private final List<String> errors = new ArrayList<>();
49|    private final List<String> warnings = new ArrayList<>();
50|
51|    /**
52|     * Validates a wrapped payload.
53|     *
54|     * @param wrapped the wrapped payload (produced by {@link LongHunAdapter#wrap})
55|     * @return {@link ValidationResult} with valid flag, errors, warnings, and summary
56|     */
57|    @Nonnull
58|    public ValidationResult validate(@Nonnull ObjectNode wrapped) {
59|        Objects.requireNonNull(wrapped, "wrapped must not be null");
60|        errors.clear();
61|        warnings.clear();
62|
63|        // 1. Check not empty
64|        if (wrapped.size() == 0) {
65|            errors.add("Input is an empty object");
66|            return result();
67|        }
68|
69|        // 2. Top-level keys
70|        Set<String> keys = toSet(wrapped.fieldNames());
71|        Set<String> missingTop = new LinkedHashSet<>(REQUIRED_TOP_KEYS);
72|        missingTop.removeAll(keys);
73|        if (!missingTop.isEmpty()) {
74|            errors.add("Missing top-level keys: " + missingTop);
75|        }
76|
77|        // 3. DNA validation
78|        JsonNode dnaNode = wrapped.get("dna");
79|        String dna = (dnaNode != null && dnaNode.isTextual()) ? dnaNode.asText() : "";
80|        if (dna.isEmpty()) {
81|            errors.add("DNA field is empty");
82|        } else {
83|            if (!DNA_REGEX.matcher(dna).matches()) {
84|                String truncated = dna.length() > 60 ? dna.substring(0, 60) + "..." : dna;
85|                errors.add("DNA does not match regex: " + truncated);
86|            }
87|        }
88|
89|        // 4. Audit validation
90|        JsonNode auditNode = wrapped.get("audit");
91|        Map<String, Object> audit = jsonNodeToMap(auditNode);
92|        if (auditNode == null || !auditNode.isObject()) {
93|            errors.add("Audit is not a dict");
94|        } else {
95|            validateAudit(audit);
96|        }
97|
98|        // 5. UID consistency check
99|        if (auditNode != null && auditNode.isObject()) {
100|            JsonNode metaNode = wrapped.get("meta");
101|            if (metaNode != null && metaNode.isObject()) {
102|                String metaUid = metaNode.has("uid") ? metaNode.get("uid").asText("") : "";
103|                String auditUid = auditNode.has("uid") ? auditNode.get("uid").asText("") : "";
104|                if (!metaUid.isEmpty() && !auditUid.isEmpty()) {
105|                    String auditUidClean = auditUid.replace("UID", "");
106|                    if (!metaUid.equals(auditUidClean)) {
107|                        errors.add("UID mismatch: meta.uid=" + metaUid + ", audit.uid=" + auditUid);
108|                    }
109|                }
110|            }
111|        }
112|
113|        return result();
114|    }
115|
116|    /**
117|     * Quick check: has required keys and valid DNA format?
118|     *
119|     * @param wrapped the wrapped payload
120|     * @return true if the payload has 'dna' and 'audit' keys and DNA matches regex
121|     */
122|    public static boolean quickValidate(@Nonnull ObjectNode wrapped) {
123|        Objects.requireNonNull(wrapped, "wrapped must not be null");
124|        if (!wrapped.has("dna") || !wrapped.has("audit")) {
125|            return false;
126|        }
127|        JsonNode dnaNode = wrapped.get("dna");
128|        if (dnaNode == null || !dnaNode.isTextual()) {
129|            return false;
130|        }
131|        return DNA_REGEX.matcher(dnaNode.asText()).matches();
132|    }
133|
134|    /**
135|     * Returns the DNA regex pattern used for validation.
136|     *
137|     * @return compiled Pattern
138|     */
139|    @Nonnull
140|    public static Pattern getDnaRegex() {
141|        return DNA_REGEX;
142|    }
143|
144|    @Nonnull
145|    static Set<String> getRequiredTopKeys() {
146|        return REQUIRED_TOP_KEYS;
147|    }
148|
149|    @Nonnull
150|    static Set<String> getRequiredAuditKeys() {
151|        return REQUIRED_AUDIT_KEYS;
152|    }
153|
154|    @Nonnull
155|    static Set<String> getRequiredSigKeys() {
156|        return REQUIRED_SIG_KEYS;
157|    }
158|
159|    // --- Private helpers ---
160|
161|    private void validateAudit(Map<String, Object> audit) {
162|        // Required keys
163|        Set<String> missingAudit = new LinkedHashSet<>(REQUIRED_AUDIT_KEYS);
164|        missingAudit.removeAll(audit.keySet());
165|        if (!missingAudit.isEmpty()) {
166|            errors.add("Missing audit keys: " + missingAudit);
167|        }
168|
169|        // behavior_signature
170|        @SuppressWarnings("unchecked")
171|        Map<String, Object> sig = (Map<String, Object>) audit.getOrDefault("behavior_signature", new HashMap<>());
172|        if (sig.isEmpty()) {
173|            errors.add("behavior_signature is not a dict or is empty");
174|        } else {
175|            Set<String> missingSig = new LinkedHashSet<>(REQUIRED_SIG_KEYS);
176|            missingSig.removeAll(sig.keySet());
177|            if (!missingSig.isEmpty()) {
178|                errors.add("Missing signature keys: " + missingSig);
179|            } else {
180|                validateSigValues(sig);
181|            }
182|        }
183|
184|        // pattern
185|        String pattern = (String) audit.getOrDefault("behavior_pattern", "");
186|        if (!pattern.isEmpty() && !VALID_PATTERNS.contains(pattern)) {
187|            warnings.add("Unknown behavior pattern: " + pattern);
188|        }
189|
190|        // color
191|        String color = (String) audit.getOrDefault("color", "");
192|        if (!color.isEmpty() && !VALID_COLORS.contains(color)) {
193|            warnings.add("Unknown audit color: " + color);
194|        }
195|
196|        // payload_hash
197|        String ph = (String) audit.getOrDefault("payload_hash", "");
198|        if (!ph.isEmpty() && (ph.length() != 16 || !ph.matches("^[a-f0-9]+$"))) {
199|            warnings.add("Suspicious payload_hash: " + ph);
200|        }
201|    }
202|
203|    @SuppressWarnings("unchecked")
204|    private void validateSigValues(Map<String, Object> sig) {
205|        // P
206|        String p = (String) sig.get("P");
207|        if (p != null && !VALID_P_VALUES.contains(p)) {
208|            warnings.add("Invalid P: '" + p + "'");
209|        }
210|
211|        // F
212|        String f = (String) sig.get("F");
213|        if (f != null && !VALID_F_VALUES.contains(f)) {
214|            warnings.add("Invalid F: '" + f + "'");
215|        }
216|
217|        // T (number)
218|        if (!(sig.get("T") instanceof Number)) {
219|            warnings.add("Invalid T (number)");
220|        }
221|
222|        // E
223|        String e = (String) sig.get("E");
224|        if (e != null && !VALID_E_VALUES.contains(e)) {
225|            warnings.add("Invalid E: '" + e + "'");
226|        }
227|
228|        // C (number)
229|        if (!(sig.get("C") instanceof Number)) {
230|            warnings.add("Invalid C (number)");
231|        }
232|
233|        // R (int >= 0)
234|        Object r = sig.get("R");
235|        if (r instanceof Integer) {
236|            if ((Integer) r < 0) {
237|                warnings.add("Invalid R (int >= 0)");
238|            }
239|        } else if (r instanceof Number) {
240|            // still a number but not int
241|            warnings.add("Invalid R (int >= 0)");
242|        } else {
243|            warnings.add("Invalid R (int >= 0)");
244|        }
245|
246|        // A
247|        String a = (String) sig.get("A");
248|        if (a != null && !VALID_A_VALUES.contains(a)) {
249|            warnings.add("Invalid A: '" + a + "'");
250|        }
251|
252|        // X
253|        String x = (String) sig.get("X");
254|        if (x != null && !VALID_X_VALUES.contains(x)) {
255|            warnings.add("Invalid X: '" + x + "'");
256|        }
257|
258|        // Y
259|        String y = (String) sig.get("Y");
260|        if (y != null && !VALID_Y_VALUES.contains(y)) {
261|            warnings.add("Invalid Y: '" + y + "'");
262|        }
263|
264|        // Z (number)
265|        if (!(sig.get("Z") instanceof Number)) {
266|            warnings.add("Invalid Z (number)");
267|        }
268|    }
269|
270|    private ValidationResult result() {
271|        boolean valid = errors.isEmpty();
272|        String summary;
273|        if (valid) {
274|            summary = "\u2705 VALID \u2014 " + warnings.size() + " warning(s)";
275|            if (!warnings.isEmpty()) {
276|                String firstWarnings = String.join(", ", warnings.size() > 2 ? warnings.subList(0, 2) : warnings);
277|                summary += " (" + firstWarnings + ")";
278|            }
279|        } else {
280|            summary = "\u274C INVALID \u2014 " + errors.size() + " error(s)";
281|        }
282|        return new ValidationResult(valid, errors, warnings, summary);
283|    }
284|
285|    private static Set<String> setOf(String... values) {
286|        Set<String> set = new LinkedHashSet<>();
287|        Collections.addAll(set, values);
288|        return Collections.unmodifiableSet(set);
289|    }
290|
291|    private static Set<String> toSet(Iterator<String> iterator) {
292|        Set<String> set = new LinkedHashSet<>();
293|        while (iterator.hasNext()) {
294|            set.add(iterator.next());
295|        }
296|        return set;
297|    }
298|
299|    @SuppressWarnings("unchecked")
300|    private static Map<String, Object> jsonNodeToMap(JsonNode node) {
301|        if (node == null || !node.isObject()) {
302|            return new HashMap<>();
303|        }
304|        Map<String, Object> map = new LinkedHashMap<>();
305|        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
306|        while (fields.hasNext()) {
307|            Map.Entry<String, JsonNode> entry = fields.next();
308|            map.put(entry.getKey(), jsonNodeToValue(entry.getValue()));
309|        }
310|        return map;
311|    }
312|
313|    private static Object jsonNodeToValue(JsonNode node) {
314|        if (node == null || node.isNull()) return null;
315|        if (node.isTextual()) return node.asText();
316|        if (node.isBoolean()) return node.asBoolean();
317|        if (node.isInt()) return node.asInt();
318|        if (node.isLong()) return node.asLong();
319|        if (node.isDouble() || node.isFloat()) return node.asDouble();
320|        if (node.isArray()) {
321|            List<Object> list = new ArrayList<>();
322|            for (JsonNode child : node) {
323|                list.add(jsonNodeToValue(child));
324|            }
325|            return list;
326|        }
327|        if (node.isObject()) {
328|            return jsonNodeToMap(node);
329|        }
330|        return node.asText();
331|    }
332|}