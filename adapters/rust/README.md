# lh-standard-adapter

Rust implementation of the LongHun AI Traceability Audit Protocol adapter.

## Installation

```bash
cargo add lh-standard-adapter
```

## Usage

```rust
use lh_standard_adapter::{LongHunAdapter, ValidationResult};
use serde_json::json;

fn main() {
    let adapter = LongHunAdapter::new("user-123", "workstation-1");
    
    let data = json!({
        "query": "What is the capital of France?",
        "response": "Paris"
    });
    
    let wrapped = adapter.wrap(
        data,
        "qa_task",
        "assistant"
    ).unwrap();
    
    println!("Wrapped: {}", serde_json::to_string_pretty(&wrapped).unwrap());
    
    match adapter.validate(&wrapped) {
        ValidationResult::Valid => println!("Valid envelope"),
        ValidationResult::Invalid(msg) => println!("Invalid: {}", msg),
    }
}
```

## API

### `LongHunAdapter::new(uid: &str, device: &str) -> Self`

Create a new adapter instance.

### `wrap(&self, data: Value, task_type: &str, persona: &str) -> Result<Value, String>`

Wrap data in a LongHun envelope with metadata.

### `validate(&self, wrapped: &Value) -> ValidationResult`

Validate that a wrapped value conforms to the LongHun protocol.

### `get_schemas(&self) -> (Value, Value)`

Get JSON schemas for envelope and wrapper structures.

## Testing

```bash
cargo test
```

## License

CC-BY-NC-SA 4.0
