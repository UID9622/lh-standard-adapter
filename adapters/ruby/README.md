# LongHun Standard Adapter — Ruby

Community Ruby adapter for **AI Traceability and Audit Protocol v1.0** — DNA v∞ generation + validation (stdlib only).

## Usage

```ruby
require "lh_standard_adapter"

adapter = LongHun::Adapter.new("9622", "HM-9622-001")
dna = adapter.generate_dna("code", "WRAP", "V1.0")
puts dna
puts adapter.validate(dna) # => true
```

> RubyGems publishing of `lh_standard_adapter` is **planned, not yet published**. Use the source until publishing lands.

## Test

```bash
ruby -c lib/lh_standard_adapter.rb
ruby -e "require './lib/lh_standard_adapter'; a=LongHun::Adapter.new; raise unless a.validate(a.generate_dna)"
```

## Community status & credits

> **Community contribution** — authored by [@rushikeshgarad2024-dev](https://github.com/rushikeshgarad2024-dev), reviewed & integrated by UID9622 (诸葛鑫) on 2026-09-05.
>
> Cross-language guarantee: for the same task at the same instant (Asia/Shanghai), every language adapter produces the **same four-pillar DNA prefix** — verified in CI against the Python reference implementation. The trailing 8-hex hash is implementation-specific and intentionally not cross-checked.

_Gratitude: 感谢 rushikesh 无偿贡献此 Ruby 适配器。这是社區共建的活水。— UID9622_
