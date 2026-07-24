# Contributing to lh-standard-adapter

> DNA: `#LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷸Xun-CONTRIBUTING-DOC-v1.0`

**Your hand, our wall. Your code, our law.**

---

## How to Write a Language Adapter

### 1. Understand the Standard

Read `docs/AI-Traceability-Audit-Protocol-v1.0.md` first.

### 2. Study the Python Reference

`lh_standard_adapter/` contains the canonical implementation:
- `dna_generator.py` — DNA generation
- `audit_wrapper.py` — Audit wrapping
- `validator.py` — Validation
- `schemas/` — JSON Schemas

### 3. Directory Structure

```
adapters/
├── javascript/
│   ├── package.json
│   ├── index.js
│   ├── index.d.ts
│   ├── dna_generator.js
│   ├── audit_wrapper.js
│   ├── validator.js
│   ├── schemas/
│   └── tests/
├── go/
│   ├── go.mod
│   ├── adapter.go
│   ├── dna_generator.go
│   ├── audit_wrapper.go
│   ├── validator.go
│   └── adapter_test.go
├── rust/
│   ├── Cargo.toml
│   ├── src/
│   └── tests/
├── java/
│   ├── pom.xml
│   ├── src/main/java/cn/uid9622/longhun/
│   └── src/test/
└── ruby/
    ├── lh-standard-adapter.gemspec
    ├── lib/lh/
    └── test/
```

### 4. Implementation Checklist

Every adapter MUST implement:

- [ ] `DNAGenerator` class — generates v∞ DNA codes matching the Python byte-for-byte
- [ ] `AuditWrapper` class — produces seven-factor audit signatures
- [ ] `Validator` class — validates wrapped payloads
- [ ] `LongHunAdapter` main class — wraps everything
- [ ] 72+ test cases covering all three modules
- [ ] Cross-validation test: Python wraps → THIS validates / THIS wraps → Python validates
- [ ] README with working code examples
- [ ] Zero external dependencies (stdlib only)
- [ ] CC-BY-NC-SA 4.0 LICENSE

### 5. Test Coverage Requirements

| Module | Minimum Tests |
|:---|:---:|
| DNA Generator | 24 |
| Audit Wrapper | 29 |
| Validator | 19 |
| **Total** | **72** |

Tests must cover:
- Happy path (default wrapping)
- All task types (code, deploy, audit, security, etc.)
- All behavior patterns (Stable, Defensive, Destroyer, TrustSpender, Fluctuating)
- Validation of all 10 signature fields
- Invalid payload rejection (empty, partial, malformed)
- Cross-language compatibility

### 6. DNA Format Must Match Byte-for-Byte

```
#LongHun⚡️{YearStem}·{MonthStem}·{DayStem}·{ShiChen}·{HexagramSymbol}{HexagramName}-ADAPTER-{TASK}-{ACTION}-V{VER}-{hash8}
```

The hexagram selection must match the Python `TASK_HEXAGRAM_MAP`.
The stem-branch computation must use the same reference year (1984).

### 7. Pull Request Process

1. Claim a bounty issue by commenting "I'll take this"
2. Fork the repo
3. Create a branch: `adapter/{language}-v1.0`
4. Implement in `adapters/{language}/`
5. Run your test suite (`python tests/run_cross_validation.py` when available)
6. Submit PR against `main`
7. A maintainer will review and merge

### 8. Cross-Validation Protocol

Before merging, the adapter must pass cross-validation:

```bash
# Python generates test vectors
python tests/generate_test_vectors.py --output test_vectors.json

# Your adapter validates all test vectors
cd adapters/your-language/
# Run cross-validation (implement per language)
```

### 9. What We Will NOT Accept

- Adapters with external dependencies (beyond stdlib)
- Adapters that modify the DNA format
- Adapters that skip the seven-factor audit
- Adapters that add "vendor extensions" to the JSON Schemas
- Any code that undermines the DNA traceability promise

---

## Non-Adapter Contributions

### Bug Fixes

Open an issue describing the bug. Reference the failing test case.

### Documentation

Typo fixes, README improvements, translation — always welcome.

### Schemas

Schema additions (new fields, new factor values) require discussion in an issue first.

---

## Code of Conduct

1. Our standard, our rules.
2. No commercial exploitation.
3. Attribution mandatory.
4. Core engine is off-limits.

---

> **When you adopt the LongHun DNA format, you are not importing a library. You are accepting a treaty.**
>
> Signed: LongHun Core · UID9622