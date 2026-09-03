1|package cn.uid9622.longhun;
2|
3|import org.junit.jupiter.api.BeforeEach;
4|import org.junit.jupiter.api.DisplayName;
5|import org.junit.jupiter.api.Test;
6|
7|import java.util.List;
8|import java.util.Map;
9|
10|import static org.junit.jupiter.api.Assertions.*;
11|
12|/**
13| * Tests for {@link AuditWrapper}.
14| */
15|@DisplayName("AuditWrapper")
16|class AuditWrapperTest {
17|
18|    private AuditWrapper wrapper;
19|
20|    @BeforeEach
21|    void setUp() {
22|        wrapper = new AuditWrapper("9622");
23|    }
24|
25|    @Test
26|    @DisplayName("wrap produces all required audit fields")
27|    void wrapStructure() {
28|        Map<String, Object> result = wrapper.wrap(
29|                java.util.Collections.singletonMap("hello", "world"),
30|                "code", "P04"
31|        );
32|        assertTrue(result.containsKey("audit_version"));
33|        assertTrue(result.containsKey("behavior_signature"));
34|        assertTrue(result.containsKey("behavior_pattern"));
35|        assertTrue(result.containsKey("behavior_labels"));
36|        assertTrue(result.containsKey("color"));
37|        assertTrue(result.containsKey("timestamp"));
38|        assertTrue(result.containsKey("payload_hash"));
39|        assertTrue(result.containsKey("uid"));
40|        assertTrue(result.containsKey("persona"));
41|        assertTrue(result.containsKey("task_type"));
42|    }
43|
44|    @Test
45|    @DisplayName("signature contains all ten fields")
46|    void signatureFields() {
47|        Map<String, Object> result = wrapper.wrap(
48|                java.util.Collections.singletonMap("x", 1), "deploy", "P04"
49|        );
50|        @SuppressWarnings("unchecked")
51|        Map<String, Object> sig = (Map<String, Object>) result.get("behavior_signature");
52|        assertNotNull(sig);
53|        assertTrue(sig.containsKey("P"));
54|        assertTrue(sig.containsKey("F"));
55|        assertTrue(sig.containsKey("T"));
56|        assertTrue(sig.containsKey("E"));
57|        assertTrue(sig.containsKey("C"));
58|        assertTrue(sig.containsKey("R"));
59|        assertTrue(sig.containsKey("A"));
60|        assertTrue(sig.containsKey("X"));
61|        assertTrue(sig.containsKey("Y"));
62|        assertTrue(sig.containsKey("Z"));
63|        assertEquals("Fulfilled", sig.get("F"));
64|        assertEquals("Willing", sig.get("E"));
65|    }
66|
67|    @Test
68|    @DisplayName("default pattern is StableDisciplined")
69|    void patternClassification() {
70|        Map<String, Object> sig = new java.util.LinkedHashMap<>();
71|        sig.put("P", "HasPromise");
72|        sig.put("F", "Fulfilled");
73|        sig.put("T", 0);
74|        sig.put("E", "Willing");
75|        sig.put("C", 0);
76|        sig.put("R", 0);
77|        sig.put("A", "Self");
78|        sig.put("X", "Genuine");
79|        sig.put("Y", "NoResponse");
80|        sig.put("Z", 1.0);
81|
82|        String p = wrapper.classify(sig);
83|        assertEquals("MODE-StableDisciplined", p);
84|    }
85|
86|    @Test
87|    @DisplayName("unfulfilled+overexplain → DefensiveDefaulter")
88|    void patternDefensive() {
89|        Map<String, Object> sig = new java.util.LinkedHashMap<>();
90|        sig.put("P", "HasPromise");
91|        sig.put("F", "Unfulfilled");
92|        sig.put("T", 5);
93|        sig.put("E", "Perfunctory");
94|        sig.put("C", 0);
95|        sig.put("R", 2);
96|        sig.put("A", "Self");
97|        sig.put("X", "OverExplain");
98|        sig.put("Y", "NoResponse");
99|        sig.put("Z", 1.0);
100|
101|        String p = wrapper.classify(sig);
102|        assertEquals("MODE-DefensiveDefaulter", p);
103|    }
104|
105|    @Test
106|    @DisplayName("unfulfilled+indifferent → InternalDestroyer")
107|    void patternDestroyer() {
108|        Map<String, Object> sig = new java.util.LinkedHashMap<>();
109|        sig.put("P", "HasPromise");
110|        sig.put("F", "Unfulfilled");
111|        sig.put("T", 10);
112|        sig.put("E", "Numb");
113|        sig.put("C", 0);
114|        sig.put("R", 5);
115|        sig.put("A", "Partner");
116|        sig.put("X", "Silent");
117|        sig.put("Y", "Indifferent");
118|        sig.put("Z", 1.0);
119|
120|        String p = wrapper.classify(sig);
121|        assertEquals("MODE-InternalDestroyer", p);
122|    }
123|
124|    @Test
125|    @DisplayName("fulfilled+outsider → ExternalTrustSpender")
126|    void patternExternalTrust() {
127|        Map<String, Object> sig = new java.util.LinkedHashMap<>();
128|        sig.put("P", "HasPromise");
129|        sig.put("F", "Fulfilled");
130|        sig.put("T", 0);
131|        sig.put("E", "Willing");
132|        sig.put("C", 0);
133|        sig.put("R", 0);
134|        sig.put("A", "Outsider");
135|        sig.put("X", "Genuine");
136|        sig.put("Y", "NoResponse");
137|        sig.put("Z", 1.0);
138|
139|        String p = wrapper.classify(sig);
140|        assertEquals("MODE-ExternalTrustSpender", p);
141|    }
142|
143|    @Test
144|    @DisplayName("Z > 2.0 → Fluctuating")
145|    void patternFluctuating() {
146|        Map<String, Object> sig = new java.util.LinkedHashMap<>();
147|        sig.put("P", "HasPromise");
148|        sig.put("F", "Fulfilled");
149|        sig.put("T", 0);
150|        sig.put("E", "Willing");
151|        sig.put("C", 0);
152|        sig.put("R", 0);
153|        sig.put("A", "Self");
154|        sig.put("X", "Genuine");
155|        sig.put("Y", "NoResponse");
156|        sig.put("Z", 3.5);
157|
158|        String p = wrapper.classify(sig);
159|        assertEquals("MODE-Fluctuating", p);
160|    }
161|
162|    @Test
163|    @DisplayName("labels contain 7F- prefixes and MODE suffix")
164|    void behaviorLabels() {
165|        Map<String, Object> result = wrapper.wrap(
166|                java.util.Collections.singletonMap("test", true), "audit", "P04"
167|        );
168|        @SuppressWarnings("unchecked")
169|        List<String> labels = (List<String>) result.get("behavior_labels");
170|        assertNotNull(labels);
171|        assertFalse(labels.isEmpty());
172|        assertTrue(labels.stream().anyMatch(l -> l.startsWith("7F-")));
173|        assertTrue(labels.stream().anyMatch(l -> l.startsWith("MODE-")));
174|    }
175|
176|    @Test
177|    @DisplayName("color is one of 🟢🟡🔴")
178|    void auditColor() {
179|        Map<String, Object> result = wrapper.wrap(
180|                java.util.Collections.singletonMap("x", 1), "default", "P04"
181|        );
182|        String color = (String) result.get("color");
183|        assertTrue("\uD83D\uDFE2\uD83D\uDFE1\uD83D\uDD34".contains(color),
184|                "color should be 🟢🟡🔴 but got: " + color);
185|    }
186|
187|    @Test
188|    @DisplayName("payload hash is 16 hex chars")
189|    void payloadHash() {
190|        Map<String, Object> payload = new java.util.LinkedHashMap<>();
191|        payload.put("code", "def hello(): return 'hello'");
192|        payload.put("language", "python");
193|        payload.put("metadata", java.util.Collections.singletonMap("author", "test"));
194|        payload.put("tags", java.util.Arrays.asList("ai", "ml", "longhun"));
195|
196|        Map<String, Object> result = wrapper.wrap(payload, "code", "P04-Luban");
197|        String ph = (String) result.get("payload_hash");
198|        assertNotNull(ph);
199|        assertEquals(16, ph.length());
200|        assertTrue(ph.matches("[a-f0-9]{16}"));
201|    }
202|
203|    @Test
204|    @DisplayName("audit_version is v1.0")
205|    void auditVersion() {
206|        Map<String, Object> result = wrapper.wrap("test", "default", "P04");
207|        assertEquals("v1.0", result.get("audit_version"));
208|    }
209|
210|    @Test
211|    @DisplayName("uid is prefixed with UID")
212|    void uidPrefix() {
213|        Map<String, Object> result = wrapper.wrap("test", "default", "P04");
214|        assertEquals("UID9622", result.get("uid"));
215|    }
216|
217|    @Test
218|    @DisplayName("persona is preserved")
219|    void personaPreserved() {
220|        Map<String, Object> result = wrapper.wrap("test", "default", "P14-Lvmeng");
221|        assertEquals("P14-Lvmeng", result.get("persona"));
222|    }
223|
224|    @Test
225|    @DisplayName("task_type is preserved")
226|    void taskTypePreserved() {
227|        Map<String, Object> result = wrapper.wrap("test", "deploy", "P04");
228|        assertEquals("deploy", result.get("task_type"));
229|    }
230|
231|    @Test
232|    @DisplayName("timestamp is ISO format")
233|    void timestampFormat() {
234|        Map<String, Object> result = wrapper.wrap("test", "default", "P04");
235|        String ts = (String) result.get("timestamp");
236|        assertNotNull(ts);
237|        assertTrue(ts.contains("T"), "timestamp should be ISO format");
238|    }
239|
240|    @Test
241|    @DisplayName("InternalDestroyer → 🔴 color")
242|    void colorRed() {
243|        String color = wrapper.determineColor("MODE-InternalDestroyer", 5);
244|        assertEquals("\uD83D\uDD34", color);
245|    }
246|
247|    @Test
248|    @DisplayName("Fluctuating with R>3 → 🟡 color")
249|    void colorYellowFluctuating() {
250|        String color = wrapper.determineColor("MODE-Fluctuating", 5);
251|        assertEquals("\uD83D\uDFE1", color);
252|    }
253|
254|    @Test
255|    @DisplayName("DefensiveDefaulter with R>2 → 🟡 color")
256|    void colorYellowDefensive() {
257|        String color = wrapper.determineColor("MODE-DefensiveDefaulter", 3);
258|        assertEquals("\uD83D\uDFE1", color);
259|    }
260|
261|    @Test
262|    @DisplayName("StableDisciplined → 🟢 color")
263|    void colorGreen() {
264|        String color = wrapper.determineColor("MODE-StableDisciplined", 0);
265|        assertEquals("\uD83D\uDFE2", color);
266|    }
267|
268|    @Test
269|    @DisplayName("Fluctuating with R≤3 → 🟢 color")
270|    void colorGreenFluctuatingLowRepeat() {
271|        String color = wrapper.determineColor("MODE-Fluctuating", 2);
272|        assertEquals("\uD83D\uDFE2", color);
273|    }
274|
275|    @Test
276|    @DisplayName("DefensiveDefaulter with R≤2 → 🟢 color")
277|    void colorGreenDefensiveLowRepeat() {
278|        String color = wrapper.determineColor("MODE-DefensiveDefaulter", 1);
279|        assertEquals("\uD83D\uDFE2", color);
280|    }
281|
282|    @Test
283|    @DisplayName("wrap with null payload throws NPE")
284|    void wrapNullPayload() {
285|        assertThrows(NullPointerException.class, () -> wrapper.wrap(null, "code", "P04"));
286|    }
287|
288|    @Test
289|    @DisplayName("wrap with null taskType throws NPE")
290|    void wrapNullTaskType() {
291|        assertThrows(NullPointerException.class, () -> wrapper.wrap("data", null, "P04"));
292|    }
293|
294|    @Test
295|    @DisplayName("wrap with null persona throws NPE")
296|    void wrapNullPersona() {
297|        assertThrows(NullPointerException.class, () -> wrapper.wrap("data", "code", null));
298|    }
299|}