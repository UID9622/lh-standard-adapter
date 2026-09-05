# LongHun Standard Adapter — Rust Crate

Community Rust adapter for **AI Traceability and Audit Protocol v1.0** — DNA v∞ generation + validation.

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

> crates.io publishing of `lh-standard-adapter` is **planned, not yet published**. Use the source until publishing lands.

## Test

```bash
cargo test
```

## Community status & credits

> **Community contribution** — authored by [@rushikeshgarad2024-dev](https://github.com/rushikeshgarad2024-dev), reviewed & integrated by UID9622 (诸葛鑫) on 2026-09-05.
>
> Cross-language guarantee: for the same task at the same instant (Asia/Shanghai), every language adapter produces the **same four-pillar DNA prefix** — verified in CI against the Python reference implementation. The trailing 8-hex hash is implementation-specific and intentionally not cross-checked.

_Gratitude: 感谢 rushikesh 无偿贡献此 Rust 适配器。这是社區共建的活水。— UID9622_
