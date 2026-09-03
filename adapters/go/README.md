# lh_standard_adapter (Go)

Go implementation of the LongHun Standard Adapter v1.0.0.

## Install

```bash
go get github.com/UID9622/lh-standard-adapter
```

## Usage

```go
package main

import (
    "fmt"
    "github.com/UID9622/lh-standard-adapter/lhstandardadapter"
)

func main() {
    adapter := lhstandardadapter.NewAdapter("9622", "HM-9622-001")
    
    data := map[string]interface{}{
        "code":   "print('hello')",
        "module": "demo",
    }
    
    wrapped, err := adapter.Wrap(data, "code", "P04")
    if err != nil {
        panic(err)
    }
    
    fmt.Println("DNA:", wrapped.DNA)
    fmt.Println("Pattern:", wrapped.Audit.BehaviorPattern)
    fmt.Println("Color:", wrapped.Audit.Color)
    
    result := adapter.Validate(wrapped)
    fmt.Println("Valid:", result.Valid)
    fmt.Println("Summary:", result.Summary)
}
```

## API

### `NewAdapter(uid, device string) *Adapter`

Create a new adapter instance.

### `(*Adapter).Wrap(data interface{}, taskType, persona string) (*WrappedPayload, error)`

Wrap a payload with DNA traceability and seven-factor audit metadata.

### `(*Adapter).Validate(wrapped *WrappedPayload) ValidationResult`

Validate a wrapped payload for standard compliance.

### `(*Adapter).GetSchemas() (dnaSchema, auditSchema map[string]interface{})`

Get JSON schemas for DNA and Audit formats.

### `Wrap(data, taskType, persona, uid, device string) (*WrappedPayload, error)`

Convenience one-shot wrapper function.

## Features

- Zero external dependencies (Go stdlib only)
- Full cross-validation with Python reference implementation
- 44 test cases covering all modules
- `go vet` clean

## License

CC BY-NC-SA 4.0
