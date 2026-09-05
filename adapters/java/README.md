# LongHun Standard Adapter — Java

Community Java adapter for **AI Traceability and Audit Protocol v1.0** — DNA v∞ generation + validation (JDK only, zero external dependency).

## Build & Run

```bash
javac -d target/classes src/main/java/com/longhun/adapter/LongHunAdapter.java
java -cp target/classes com.longhun.adapter.LongHunAdapter
```

## Usage

```java
LongHunAdapter adapter = new LongHunAdapter("9622", "HM-9622-001");
String dna = adapter.generateDNA("code", "WRAP", "V1.0");
System.out.println(dna);
boolean ok = adapter.validate(dna);
```

> Maven coordinates (`com.longhun:lh-standard-adapter:1.0.0`) are **proposed, not yet published** to Maven Central. Use the source until publishing lands.

## Community status & credits

> **Community contribution** — authored by [@rushikeshgarad2024-dev](https://github.com/rushikeshgarad2024-dev), reviewed & integrated by UID9622 (诸葛鑫) on 2026-09-05.
>
> Cross-language guarantee: for the same task at the same instant (Asia/Shanghai), every language adapter produces the **same four-pillar DNA prefix** — verified in CI against the Python reference implementation. The trailing 8-hex hash is implementation-specific and intentionally not cross-checked.

_Gratitude: 感谢 rushikesh 无偿贡献此 Java 适配器。这是社區共建的活水。— UID9622_
