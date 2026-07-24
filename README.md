# lh-standard-adapter — Rust

LongHun Standard Adapter implementation in Rust for crates.io.

## Installation

```toml
[dependencies]
lh-standard-adapter = "1.0.0"
```

## Usage

```rust
use lh_standard_adapter::{LongHunAdapter, wrap};
use serde_json::json;

fn main() {
    let adapter = LongHunAdapter::new(None, None);
    let data = json!({"code": "println!(\"hello\")"});
    let result = adapter.wrap(data, "code", "P04");
    println!("{}", result.dna.code);
}
```

## License

CC BY-NC-SA 4.0
