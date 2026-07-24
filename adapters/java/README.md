1|# LongHun Standard Adapter — Java / Kotlin
2|
3|> DNA: `#LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-ADAPTER-JAVA-v1.0.0`
4|> Author: LongHun Core · UID9622 · 龍芯北辰
5|> License: [CC BY-NC-SA 4.0](LICENSE)
6|> Java Version: 8+ (JDK 1.8 compatible)
7|
8|**Open the standard. Guard the engine.**
9|
10|This is the Java/Kotlin implementation of the LongHun Standard Adapter.
11|It wraps JSON payloads with DNA traceability codes and seven-factor behavioral audit metadata,
12|following the [AI Traceability and Audit Protocol v1.0](../docs/AI-Traceability-Audit-Protocol-v1.0.md).
13|
14|## Features
15|
16|- ✅ **DNA Traceability (v∞ format)** — SHA-256 anchored, stem-branch + I Ching hexagram codes
17|- ✅ **Seven-Factor Behavioral Audit** — P, F, T, E, C, R, A, X, Y, Z signature
18|- ✅ **Behavior Pattern Classification** — 5 patterns (StableDisciplined, DefensiveDefaulter, etc.)
19|- ✅ **Three-Color Audit** — 🟢 Green, 🟡 Yellow, 🔴 Red
20|- ✅ **Bilingual Labels** — Chinese + English behavior labels
21|- ✅ **Full Validation** — DNA regex, audit structure, signature values, UID consistency
22|- ✅ **Kotlin-Friendly** — Null-safety annotations (`@Nonnull`, `@Nullable`), immutable data classes
23|- ✅ **72+ JUnit 5 Tests** — Comprehensive coverage
24|- ✅ **Java 8 Compatible** — Works with JDK 1.8+
25|
26|## Maven
27|
28|Add this dependency to your `pom.xml`:
29|
30|```xml
31|<dependency>
32|    <groupId>cn.uid9622.longhun</groupId>
33|    <artifactId>lh-standard-adapter</artifactId>
34|    <version>1.0.0</version>
35|</dependency>
36|```
37|
38|## Java Usage
39|
40|### Basic Wrapping
41|
42|```java
43|import cn.uid9622.longhun.LongHunAdapter;
44|import cn.uid9622.longhun.ValidationResult;
45|import com.fasterxml.jackson.databind.node.ObjectNode;
46|
47|LongHunAdapter adapter = new LongHunAdapter("9622", "HM-9622-001");
48|
49|// Wrap any JSON-serializable data
50|ObjectNode result = adapter.wrap(
51|    Map.of("code", "print('hello')"),
52|    "code",
53|    "P04-Luban"
54|);
55|
56|// Access the DNA traceability code
57|String dna = result.get("dna").asText();
58|// → "#LongHun⚡️BingWu·GuiWei·JiaZi·WeiShi·䷾JiJi-ADAPTER-CODE-WRAP-V1.0-a3f8c1d9"
59|
60|// Access the audit metadata
61|String pattern = result.get("audit").get("behavior_pattern").asText();
62|// → "MODE-StableDisciplined"
63|
64|// Validate the wrapped payload
65|ValidationResult validation = adapter.validate(result);
66|if (validation.isValid()) {
67|    System.out.println("✅ Valid: " + validation.getSummary());
68|}
69|```
70|
71|### Getting Schemas
72|
73|```java
74|Map<String, Object> schemas = adapter.getSchemas();
75|Map<String, Object> dnaSchema = (Map<String, Object>) schemas.get("dna_schema");
76|Map<String, Object> auditSchema = (Map<String, Object>) schemas.get("audit_schema");
77|```
78|
79|### Customizing Action and Version
80|
81|```java
82|ObjectNode result = adapter.wrap(data, "deploy", "P14-Lvmeng", "DEPLOY", "v2.0");
83|```
84|
85|## Kotlin Usage
86|
87|```kotlin
88|import cn.uid9622.longhun.LongHunAdapter
89|
90|val adapter = LongHunAdapter("9622", "HM-9622-001")
91|val result = adapter.wrap(mapOf("code" to "print('hello')"), "code", "P04-Luban")
92|val validation = adapter.validate(result)
93|
94|println(result["dna"])          // DNA traceability code
95|println(result["audit"]["behavior_pattern"])  // Behavior pattern
96|println(validation.summary)     // Validation summary
97|```
98|
99|## Building
100|
101|```bash
102|cd adapters/java
103|mvn clean package
104|```
105|
106|## Running Tests
107|
108|```bash
109|cd adapters/java
110|mvn test
111|```
112|
113|## API Reference
114|
115|### `LongHunAdapter`
116|
117|| Method | Returns | Description |
118||--------|---------|-------------|
119|| `LongHunAdapter(String uid, String device)` | — | Constructor with default locale |
120|| `LongHunAdapter(String uid, String device, String locale)` | — | Constructor with custom timezone |
121|| `wrap(Object data, String taskType, String persona)` | `ObjectNode` | Wrap payload with DNA + audit |
122|| `wrap(Object data, String taskType, String persona, String action, String version)` | `ObjectNode` | Wrap with custom action/version |
123|| `validate(ObjectNode wrapped)` | `ValidationResult` | Validate wrapped payload |
124|| `getSchemas()` | `Map<String, Object>` | Get JSON Schema definitions |
125|
126|### `ValidationResult`
127|
128|| Method | Returns | Description |
129||--------|---------|-------------|
130|| `isValid()` | `boolean` | Whether validation passed |
131|| `getErrors()` | `List<String>` | Validation error messages |
132|| `getWarnings()` | `List<String>` | Validation warning messages |
133|| `getSummary()` | `String` | Human-readable summary |
134|
135|## Project Structure
136|
137|```
138|adapters/java/
139|├── pom.xml
140|├── LICENSE
141|├── README.md
142|└── src/
143|    ├── main/java/cn/uid9622/longhun/
144|    │   ├── LongHunAdapter.java    # Main adapter class
145|    │   ├── DNAGenerator.java      # DNA traceability code generation
146|    │   ├── AuditWrapper.java      # Seven-factor audit wrapping
147|    │   ├── Validator.java         # Payload validation
148|    │   ├── ValidationResult.java  # Immutable validation result
149|    │   └── Schemas.java           # JSON Schema definitions
150|    └── test/java/cn/uid9622/longhun/
151|        ├── LongHunAdapterTest.java
152|        ├── DNAGeneratorTest.java
153|        ├── AuditWrapperTest.java
154|        └── ValidatorTest.java
155|```
156|
157|## License
158|
159|This project is licensed under [CC BY-NC-SA 4.0](LICENSE) — Attribution-NonCommercial-ShareAlike 4.0 International.
160|
161|## Cross-Validation
162|
163|The Java implementation is cross-validated against the Python reference implementation
164|at `lh_standard_adapter/`. All tests produce identical output structure and validation logic.
165|
166|## DNA
167|
168|```
169|#LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-ADAPTER-JAVA-v1.0.0
170|```
171|
172|---
173|
174|*Open the standard. Guard the engine.*