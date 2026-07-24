# frozen_string_literal: true

Gem::Specification.new do |spec|
  spec.name          = "lh-standard-adapter"
  spec.version       = "1.0.0"
  spec.authors       = ["LongHun Core · UID9622 · 龍芯北辰"]
  spec.summary       = "LongHun Standard Adapter — DNA traceability & seven-factor audit wrapper"
  spec.description   = <<~DESC
    Open-source shell tool wrapping JSON payloads with DNA traceability
    (GanZhi calendar + I-Ching hexagrams + SHA-256 hash) and seven-factor
    behavioral audit metadata. Core compiler, training scripts, and algorithm
    logic are protected Chinese independent intellectual property.
  DESC
  spec.license       = "CC BY-NC-SA 4.0"
  spec.homepage      = "https://github.com/LongHun-Core/lh-standard-adapter"

  spec.required_ruby_version = ">= 2.7.0"

  spec.files = Dir["lib/**/*.rb", "README.md", "Gemfile", "*.gemspec"]
  spec.require_paths = ["lib"]

  # Zero runtime dependencies — stdlib only.
  spec.add_development_dependency "minitest", "~> 5.0"
end
