# LongHun Rust Adapter (`lh-standard-adapter/adapters/rust`)

Official Rust crate adapter implementation for the **LongHun AI Traceability & Audit Protocol v1.0**.

---

## English Quickstart

### Installation (Cargo.toml)

```toml
[dependencies]
lh-standard-adapter = "1.0"
serde_json = "1.0"
```

### Usage (Rust)

```rust
use lh_standard_adapter::LongHunAdapter;
use serde_json::json;

fn main() {
    let adapter = LongHunAdapter::new("9622", "HM-9622-001");
    let payload = json!({ "query": "What is AI agent traceability?" });

    let wrapped = adapter.wrap(payload, "code", "P04").unwrap();
    println!("DNA: {}", wrapped["dna"]);

    let result = adapter.validate(&wrapped);
    println!("{}", result.summary); // ✅ VALID — 0 warning(s)
}
```

---

## License

CC-BY-NC-SA 4.0
