1|package cn.uid9622.longhun;
2|
3|import javax.annotation.Nonnull;
4|import javax.annotation.Nullable;
5|import java.nio.charset.StandardCharsets;
6|import java.security.MessageDigest;
7|import java.security.NoSuchAlgorithmException;
8|import java.time.LocalDateTime;
9|import java.time.ZoneId;
10|import java.time.ZonedDateTime;
11|import java.util.*;
12|
13|/**
14| * Generates v∞ format DNA traceability codes for the LongHun standard.
15| *
16| * <p>Format: {@code #LongHun⚡️{YearStem}·{MonthStem}·{DayStem}·{ShiChen}·{Hexagram}-{Body}-{Hash8}}
17| *
18| * <p>DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-DNA-GENERATOR-v1.0.0
19| *
20| * @since 1.0.0
21| */
22|public final class DNAGenerator {
23|
24|    // --- Heavenly Stems (天干) ---
25|    private static final String[] TIAN_GAN = {
26|            "Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"
27|    };
28|
29|    // --- Earthly Branches (地支) ---
30|    private static final String[] DI_ZHI = {
31|            "Zi", "Chou", "Yin", "Mao", "Chen", "Si",
32|            "Wu", "Wei", "Shen", "You", "Xu", "Hai"
33|    };
34|
35|    // --- ShiChen (时辰) ---
36|    private static final String[] SHI_CHEN = {
37|            "ZiShi", "ChouShi", "YinShi", "MaoShi", "ChenShi", "SiShi",
38|            "WuShi", "WeiShi", "ShenShi", "YouShi", "XuShi", "HaiShi"
39|    };
40|
41|    // --- I Ching Hexagrams ---
42|    private static final List<Map<String, String>> HEXAGRAMS = buildHexagrams();
43|
44|    // --- Task-to-hexagram domain mapping ---
45|    private static final Map<String, String> TASK_HEXAGRAM_MAP = new LinkedHashMap<>();
46|
47|    static {
48|        TASK_HEXAGRAM_MAP.put("default", "governance");
49|        TASK_HEXAGRAM_MAP.put("code", "engine");
50|        TASK_HEXAGRAM_MAP.put("deploy", "deploy");
51|        TASK_HEXAGRAM_MAP.put("audit", "audit");
52|        TASK_HEXAGRAM_MAP.put("security", "security");
53|        TASK_HEXAGRAM_MAP.put("archive", "archive");
54|        TASK_HEXAGRAM_MAP.put("init", "init");
55|        TASK_HEXAGRAM_MAP.put("learn", "learn");
56|        TASK_HEXAGRAM_MAP.put("legal", "legal");
57|        TASK_HEXAGRAM_MAP.put("privacy", "privacy");
58|        TASK_HEXAGRAM_MAP.put("trust", "trust");
59|        TASK_HEXAGRAM_MAP.put("complete", "complete");
60|        TASK_HEXAGRAM_MAP.put("progress", "progress");
61|    }
62|
63|    private static final int CYCLE_YEAR = 1984; // JiaZi year reference
64|    // Month stems offset per decade
65|    private static final int[] CYCLE_MONTH = {2, 4, 6, 8, 10, 0, 2, 4, 6, 8, 10, 0};
66|
67|    private final String uid;
68|    private final String device;
69|    private final String locale;
70|
71|    /**
72|     * Creates a new {@code DNAGenerator}.
73|     *
74|     * @param uid    user identifier (e.g., "9622")
75|     * @param device device identifier (e.g., "HM-9622-001")
76|     * @param locale timezone locale (e.g., "Asia/Shanghai")
77|     */
78|    public DNAGenerator(String uid, String device, String locale) {
79|        this.uid = Objects.requireNonNull(uid, "uid must not be null");
80|        this.device = Objects.requireNonNull(device, "device must not be null");
81|        this.locale = Objects.requireNonNull(locale, "locale must not be null");
82|    }
83|
84|    /**
85|     * Generates a full DNA traceability string.
86|     *
87|     * @param taskType task category (code, deploy, audit, default, etc.)
88|     * @param action   action descriptor (WRAP, GENERATE, DEPLOY, AUDIT, etc.)
89|     * @param version  optional version override (e.g., "v1.0"), defaults to "V1.0"
90|     * @return DNA traceability string in v∞ format
91|     */
92|    @Nonnull
93|    public String generate(@Nonnull String taskType,
94|                           @Nonnull String action,
95|                           @Nullable String version) {
96|        Objects.requireNonNull(taskType, "taskType must not be null");
97|        Objects.requireNonNull(action, "action must not be null");
98|
99|        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(locale));
100|        Map<String, String> stem = computeStemBranch(now);
101|        Map<String, String> hexagram = selectHexagram(taskType);
102|        String ver = (version != null) ? version : "V1.0";
103|
104|        String body = "ADAPTER-" + taskType.toUpperCase(Locale.ROOT)
105|                + "-" + action.toUpperCase(Locale.ROOT)
106|                + "-" + ver;
107|
108|        String raw = stem.get("year") + stem.get("month") + stem.get("day") + stem.get("shichen")
109|                + hexagram.get("symbol") + hexagram.get("enName")
110|                + body
111|                + device
112|                + now.toLocalDateTime().toString();
113|
114|        String hash8 = sha256Hex(raw).substring(0, 8);
115|
116|        return "#LongHun⚡️" + stem.get("year") + "·" + stem.get("month") + "·" + stem.get("day") + "·" + stem.get("shichen")
117|                + "·" + hexagram.get("symbol") + hexagram.get("enName")
118|                + "-" + body + "-" + hash8;
119|    }
120|
121|    /**
122|     * Computes the Heavenly Stem and Earthly Branch values for a given datetime.
123|     *
124|     * @param dt the datetime to compute from
125|     * @return map with keys: year, month, day, shichen
126|     */
127|    @Nonnull
128|    Map<String, String> computeStemBranch(@Nonnull ZonedDateTime dt) {
129|        Objects.requireNonNull(dt, "dt must not be null");
130|
131|        int year = dt.getYear();
132|        int month = dt.getMonthValue();
133|        int day = dt.getDayOfYear();
134|
135|        int yearStemIdx = (year - CYCLE_YEAR) % 10;
136|        if (yearStemIdx < 0) yearStemIdx += 10;
137|        int yearBranchIdx = (year - CYCLE_YEAR) % 12;
138|        if (yearBranchIdx < 0) yearBranchIdx += 12;
139|
140|        int decadeIdx = (year - CYCLE_YEAR) % 10;
141|        if (decadeIdx < 0) decadeIdx += 10;
142|        int monthStemRaw = CYCLE_MONTH[decadeIdx];
143|        int monthStemIdx = (monthStemRaw + (month - 1)) % 10;
144|        if (monthStemIdx < 0) monthStemIdx += 10;
145|        int monthBranchIdx = (month + 1) % 12;
146|
147|        int dayOffset = (year - 1900) + (year - 1900) / 4 + day;
148|        int dayStemIdx = dayOffset % 10;
149|        if (dayStemIdx < 0) dayStemIdx += 10;
150|        int dayBranchIdx = dayOffset % 12;
151|        if (dayBranchIdx < 0) dayBranchIdx += 12;
152|
153|        int shichenIdx = dt.getHour() / 2;
154|        if (shichenIdx >= 12) shichenIdx = 11;
155|
156|        Map<String, String> result = new LinkedHashMap<>();
157|        result.put("year", TIAN_GAN[yearStemIdx] + DI_ZHI[yearBranchIdx]);
158|        result.put("month", TIAN_GAN[monthStemIdx] + DI_ZHI[monthBranchIdx]);
159|        result.put("day", TIAN_GAN[dayStemIdx] + DI_ZHI[dayBranchIdx]);
160|        result.put("shichen", SHI_CHEN[shichenIdx]);
161|        return result;
162|    }
163|
164|    /**
165|     * Selects an I Ching hexagram based on task type.
166|     *
167|     * @param taskType the task category
168|     * @return map with keys: symbol, enName, cnName, domain
169|     */
170|    @Nonnull
171|    Map<String, String> selectHexagram(@Nonnull String taskType) {
172|        String domain = TASK_HEXAGRAM_MAP.getOrDefault(taskType, "governance");
173|        for (Map<String, String> h : HEXAGRAMS) {
174|            if (domain.equals(h.get("domain"))) {
175|                return h;
176|            }
177|        }
178|        return HEXAGRAMS.get(0); // Default: Qian (governance)
179|    }
180|
181|    /**
182|     * Returns the list of all I Ching hexagrams with their metadata.
183|     *
184|     * @return unmodifiable list of hexagram maps
185|     */
186|    @Nonnull
187|    static List<Map<String, String>> getHexagrams() {
188|        return HEXAGRAMS;
189|    }
190|
191|    /**
192|     * Returns the task-to-hexagram domain mapping.
193|     *
194|     * @return unmodifiable map of task type to hexagram domain
195|     */
196|    @Nonnull
197|    static Map<String, String> getTaskHexagramMap() {
198|        return Collections.unmodifiableMap(TASK_HEXAGRAM_MAP);
199|    }
200|
201|    @Nonnull
202|    String getUid() {
203|        return uid;
204|    }
205|
206|    @Nonnull
207|    String getDevice() {
208|        return device;
209|    }
210|
211|    @Nonnull
212|    String getLocale() {
213|        return locale;
214|    }
215|
216|    // --- Private helpers ---
217|
218|    private static List<Map<String, String>> buildHexagrams() {
219|        List<Map<String, String>> list = new ArrayList<>();
220|        list.add(mapOf("symbol", "䷀", "enName", "Qian", "cnName", "乾", "domain", "governance"));
221|        list.add(mapOf("symbol", "䷁", "enName", "Kun", "cnName", "坤", "domain", "archive"));
222|        list.add(mapOf("symbol", "䷂", "enName", "Zhun", "cnName", "屯", "domain", "init"));
223|        list.add(mapOf("symbol", "䷃", "enName", "Meng", "cnName", "蒙", "domain", "learn"));
224|        list.add(mapOf("symbol", "䷄", "enName", "Xu", "cnName", "需", "domain", "async"));
225|        list.add(mapOf("symbol", "䷅", "enName", "Song", "cnName", "讼", "domain", "legal"));
226|        list.add(mapOf("symbol", "䷜", "enName", "Kan", "cnName", "坎", "domain", "engine"));
227|        list.add(mapOf("symbol", "䷝", "enName", "Li", "cnName", "离", "domain", "audit"));
228|        list.add(mapOf("symbol", "䷲", "enName", "Zhen", "cnName", "震", "domain", "security"));
229|        list.add(mapOf("symbol", "䷳", "enName", "Gen", "cnName", "艮", "domain", "privacy"));
230|        list.add(mapOf("symbol", "䷸", "enName", "Xun", "cnName", "巽", "domain", "deploy"));
231|        list.add(mapOf("symbol", "䷹", "enName", "Dui", "cnName", "兑", "domain", "trust"));
232|        list.add(mapOf("symbol", "䷾", "enName", "JiJi", "cnName", "既济", "domain", "complete"));
233|        list.add(mapOf("symbol", "䷿", "enName", "WeiJi", "cnName", "未济", "domain", "progress"));
234|        return Collections.unmodifiableList(list);
235|    }
236|
237|    private static Map<String, String> mapOf(String... entries) {
238|        Map<String, String> map = new LinkedHashMap<>();
239|        for (int i = 0; i < entries.length; i += 2) {
240|            map.put(entries[i], entries[i + 1]);
241|        }
242|        return map;
243|    }
244|
245|    @Nonnull
246|    private static String sha256Hex(@Nonnull String input) {
247|        try {
248|            MessageDigest md = MessageDigest.getInstance("SHA-256");
249|            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
250|            StringBuilder sb = new StringBuilder(64);
251|            for (byte b : digest) {
252|                sb.append(String.format("%02x", b & 0xff));
253|            }
254|            return sb.toString();
255|        } catch (NoSuchAlgorithmException e) {
256|            throw new RuntimeException("SHA-256 not available", e);
257|        }
258|    }
259|}