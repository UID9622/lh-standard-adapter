# LongHun Go Adapter (`lh-standard-adapter/adapters/go`)

Official Go adapter implementation for the **LongHun AI Traceability & Audit Protocol v1.0**.

---

## English Quickstart

### Installation

```bash
go get github.com/UID9622/lh-standard-adapter/adapters/go
```

### Usage

```go
package main

import (
    "fmt"
    adapter "github.com/UID9622/lh-standard-adapter/adapters/go"
)

func main() {
    a := adapter.NewAdapter("9622", "HM-9622-001")
    payload := map[string]string{"query": "What is AI agent traceability?"}

    wrapped, err := a.Wrap(payload, "code", "P04")
    if err != nil {
        panic(err)
    }

    fmt.Println("DNA:", wrapped.DNA)
    
    res := a.Validate(wrapped)
    fmt.Println("Validation:", res.Summary)
}
```

---

## License

CC-BY-NC-SA 4.0
