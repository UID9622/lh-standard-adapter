# LongHun Ruby Adapter (`lh-standard-adapter/adapters/ruby`)

Official Ruby Gem adapter implementation for the **LongHun AI Traceability & Audit Protocol v1.0**.

---

## English Quickstart

### Installation

```bash
gem install lh-standard-adapter
```

### Usage

```ruby
require 'lh_standard_adapter'

adapter = LongHunAdapter.new(uid: "9622", device: "HM-9622-001")
payload = { query: "What is AI agent traceability?" }

wrapped = adapter.wrap(payload, task_type: "code", persona: "P04")
puts wrapped["dna"]

result = adapter.validate(wrapped)
puts result[:summary] # ✅ VALID — 0 warning(s)
```

---

## License

CC-BY-NC-SA 4.0
