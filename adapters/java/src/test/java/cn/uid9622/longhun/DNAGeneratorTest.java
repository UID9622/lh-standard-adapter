1|package cn.uid9622.longhun;
2|
3|import com.fasterxml.jackson.databind.node.ObjectNode;
4|import org.junit.jupiter.api.BeforeEach;
5|import org.junit.jupiter.api.DisplayName;
6|import org.junit.jupiter.api.Test;
7|
8|import java.util.Map;
9|
10|import static org.junit.jupiter.api.Assertions.*;
11|
12|/**
13| * Tests for {@link DNAGenerator}.
14| */
15|@DisplayName("DNAGenerator")
16|class DNAGeneratorTest {
17|
18|    private DNAGenerator gen;
19|
20|    @BeforeEach
21|    void setUp() {
22|        gen = new DNAGenerator("9622", "HM-9622-001", "Asia/Shanghai");
23|    }
24|
25|    @Test
26|    @DisplayName("generate default task produces valid DNA prefix")
27|    void generateDefault() {
28|        String dna = gen.generate("default", "WRAP", "V1.0");
29|        assertNotNull(dna);
30|        assertTrue(dna.startsWith("#LongHun⚡️"), "DNA should start with #LongHun⚡️");
31|        assertTrue(dna.contains("ADAPTER-DEFAULT-WRAP-V1.0"), "DNA should contain module path");
32|        assertTrue(dna.matches(".*-[a-f0-9]{8}$"), "DNA should end with 8-char hex hash");
33|    }
34|
35|    @Test
36|    @DisplayName("generate code task produces correct body")
37|    void generateCodeTask() {
38|        String dna = gen.generate("code", "GENERATE", "v2.0");
39|        assertTrue(dna.startsWith("#LongHun⚡️"));
40|        assertTrue(dna.contains("ADAPTER-CODE-GENERATE-V2.0"));
41|    }
42|
43|    @Test
44|    @DisplayName("generate deploy task produces correct body")
45|    void generateDeployTask() {
46|        String dna = gen.generate("deploy", "DEPLOY", null);
47|        assertTrue(dna.contains("#LongHun⚡️"));
48|        assertTrue(dna.contains("ADAPTER-DEPLOY-DEPLOY-V1.0"));
49|    }
50|
51|    @Test
52|    @DisplayName("generate audit task produces non-null long DNA")
53|    void generateAuditTask() {
54|        String dna = gen.generate("audit", "AUDIT", null);
55|        assertNotNull(dna);
56|        assertTrue(dna.length() > 30);
57|    }
58|
59|    @Test
60|    @DisplayName("generate multiple tasks all produce valid regex-matching DNA")
61|    void generateMultipleTasks() {
62|        String[] tasks = {"default", "code", "deploy", "audit", "security", "archive"};
63|        for (String task : tasks) {
64|            String dna = gen.generate(task, "WRAP", "V1.0");
65|            assertNotNull(dna, "DNA for " + task + " should not be null");
66|            assertTrue(dna.startsWith("#LongHun⚡️"), "DNA for " + task + " should start with prefix");
67|        }
68|    }
69|
70|    @Test
71|    @DisplayName("computeStemBranch returns all four pillars")
72|    void computeStemBranch() {
73|        java.time.ZonedDateTime dt = java.time.ZonedDateTime.of(
74|                2026, 7, 24, 13, 0, 0, 0,
75|                java.time.ZoneId.of("Asia/Shanghai")
76|        );
77|        Map<String, String> stem = gen.computeStemBranch(dt);
78|        assertNotNull(stem.get("year"));
79|        assertNotNull(stem.get("month"));
80|        assertNotNull(stem.get("day"));
81|        assertNotNull(stem.get("shichen"));
82|        assertTrue(stem.get("year").length() > 0);
83|        assertTrue(stem.get("month").length() > 0);
84|        assertEquals("WeiShi", stem.get("shichen"), "13:00 → WeiShi");
85|    }
86|
87|    @Test
88|    @DisplayName("computeStemBranch at midnight returns ZiShi")
89|    void computeStemBranchMidnight() {
90|        java.time.ZonedDateTime dt = java.time.ZonedDateTime.of(
91|                2026, 7, 24, 0, 0, 0, 0,
92|                java.time.ZoneId.of("Asia/Shanghai")
93|        );
94|        Map<String, String> stem = gen.computeStemBranch(dt);
95|        assertEquals("ZiShi", stem.get("shichen"), "00:00 → ZiShi");
96|    }
97|
98|    @Test
99|    @DisplayName("computeStemBranch at 23:00 returns ZiShi")
100|    void computeStemBranchLateNight() {
101|        java.time.ZonedDateTime dt = java.time.ZonedDateTime.of(
102|                2026, 7, 24, 23, 30, 0, 0,
103|                java.time.ZoneId.of("Asia/Shanghai")
104|        );
105|        Map<String, String> stem = gen.computeStemBranch(dt);
106|        assertEquals("ZiShi", stem.get("shichen"), "23:30 → ZiShi");
107|    }
108|
109|    @Test
110|    @DisplayName("selectHexagram for audit returns Li")
111|    void selectHexagramAudit() {
112|        Map<String, String> hex = gen.selectHexagram("audit");
113|        assertNotNull(hex);
114|        assertEquals("Li", hex.get("enName"));
115|    }
116|
117|    @Test
118|    @DisplayName("selectHexagram for deploy returns Xun")
119|    void selectHexagramDeploy() {
120|        Map<String, String> hex = gen.selectHexagram("deploy");
121|        assertNotNull(hex);
122|        assertEquals("Xun", hex.get("enName"));
123|    }
124|
125|    @Test
126|    @DisplayName("selectHexagram for code returns Kan")
127|    void selectHexagramCode() {
128|        Map<String, String> hex = gen.selectHexagram("code");
129|        assertNotNull(hex);
130|        assertEquals("Kan", hex.get("enName"));
131|    }
132|
133|    @Test
134|    @DisplayName("selectHexagram for unknown task returns Qian (default)")
135|    void selectHexagramUnknown() {
136|        Map<String, String> hex = gen.selectHexagram("unknown-task");
137|        assertNotNull(hex);
138|        assertTrue(hex.containsKey("enName"));
139|    }
140|
141|    @Test
142|    @DisplayName("selectHexagram for security returns Zhen")
143|    void selectHexagramSecurity() {
144|        Map<String, String> hex = gen.selectHexagram("security");
145|        assertEquals("Zhen", hex.get("enName"));
146|    }
147|
148|    @Test
149|    @DisplayName("selectHexagram for privacy returns Gen")
150|    void selectHexagramPrivacy() {
151|        Map<String, String> hex = gen.selectHexagram("privacy");
152|        assertEquals("Gen", hex.get("enName"));
153|    }
154|
155|    @Test
156|    @DisplayName("selectHexagram for trust returns Dui")
157|    void selectHexagramTrust() {
158|        Map<String, String> hex = gen.selectHexagram("trust");
159|        assertEquals("Dui", hex.get("enName"));
160|    }
161|
162|    @Test
163|    @DisplayName("selectHexagram for complete returns JiJi")
164|    void selectHexagramComplete() {
165|        Map<String, String> hex = gen.selectHexagram("complete");
166|        assertEquals("JiJi", hex.get("enName"));
167|    }
168|
169|    @Test
170|    @DisplayName("selectHexagram for progress returns WeiJi")
171|    void selectHexagramProgress() {
172|        Map<String, String> hex = gen.selectHexagram("progress");
173|        assertEquals("WeiJi", hex.get("enName"));
174|    }
175|
176|    @Test
177|    @DisplayName("hash8 length is always 8")
178|    void hash8Length() {
179|        String dna = gen.generate("test", "TEST", null);
180|        String hash8 = dna.substring(dna.length() - 8);
181|        assertEquals(8, hash8.length());
182|        assertTrue(hash8.matches("[a-f0-9]{8}"));
183|    }
184|
185|    @Test
186|    @DisplayName("different tasks may produce different hexagrams")
187|    void differentTasksDifferentHexagrams() {
188|        Map<String, String> hexAudit = gen.selectHexagram("audit");
189|        Map<String, String> hexDeploy = gen.selectHexagram("deploy");
190|        assertNotNull(hexAudit);
191|        assertNotNull(hexDeploy);
192|    }
193|
194|    @Test
195|    @DisplayName("getHexagrams returns all 14 hexagrams")
196|    void getHexagramsCount() {
197|        assertEquals(14, DNAGenerator.getHexagrams().size());
198|    }
199|
200|    @Test
201|    @DisplayName("getTaskHexagramMap contains all expected task types")
202|    void getTaskHexagramMap() {
203|        Map<String, String> map = DNAGenerator.getTaskHexagramMap();
204|        assertTrue(map.containsKey("default"));
205|        assertTrue(map.containsKey("code"));
206|        assertTrue(map.containsKey("deploy"));
207|        assertTrue(map.containsKey("audit"));
208|        assertTrue(map.containsKey("security"));
209|        assertTrue(map.containsKey("archive"));
210|    }
211|
212|    @Test
213|    @DisplayName("constructor stores uid, device, locale")
214|    void constructor() {
215|        DNAGenerator g = new DNAGenerator("1234", "DEVICE-001", "America/New_York");
216|        assertEquals("1234", g.getUid());
217|        assertEquals("DEVICE-001", g.getDevice());
218|        assertEquals("America/New_York", g.getLocale());
219|    }
220|
221|    @Test
222|    @DisplayName("generate with null taskType throws NPE")
223|    void generateNullTaskType() {
224|        assertThrows(NullPointerException.class, () -> gen.generate(null, "WRAP", "V1.0"));
225|    }
226|
227|    @Test
228|    @DisplayName("generate with null action throws NPE")
229|    void generateNullAction() {
230|        assertThrows(NullPointerException.class, () -> gen.generate("default", null, "V1.0"));
231|    }
232|}