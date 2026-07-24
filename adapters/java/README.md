# LongHun Java & Kotlin Adapter (`lh-standard-adapter/adapters/java`)

Official Java and Kotlin adapter implementation for the **LongHun AI Traceability & Audit Protocol v1.0**.

---

## English Quickstart

### Installation (Maven)

```xml
<dependency>
    <groupId>cn.uid9622.longhun</groupId>
    <artifactId>lh-standard-adapter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Usage (Java)

```java
import cn.uid9622.longhun.LongHunAdapter;

public class Main {
    public static void main(String[] args) {
        LongHunAdapter adapter = new LongHunAdapter("9622", "HM-9622-001");
        var wrapped = adapter.wrap("Sample Payload", "code", "P04");
        
        System.out.println(wrapped.get("dna"));
        
        var result = adapter.validate(wrapped);
        System.out.println(result.get("summary")); // ✅ VALID
    }
}
```

---

## License

CC-BY-NC-SA 4.0
