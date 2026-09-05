# LongHun Standard Adapter — Rust Crate

Official crates.io adapter for **AI Traceability and Audit Protocol v1.0**.

## Usage

```rust
use lh_standard_adapter::LongHunAdapter;

fn main() {
    let adapter = LongHunAdapter::default();
    let dna = adapter.generate_dna("code", "WRAP", "V1.0");
    println!("Generated DNA: {}", dna);
    assert!(adapter.validate(&dna));
}
```
