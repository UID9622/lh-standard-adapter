# frozen_string_literal: true

Gem::Specification.new do |spec|
  spec.name          = "lh-standard-adapter"
  spec.version       = "1.0.0"
  spec.authors       = ["LongHun Core"]
  spec.summary       = "LongHun Standard Adapter — v∞ DNA traceability + seven-factor behavioral audit for Ruby"
  spec.description   = "Ruby implementation of the LongHun standard adapter with DNA generation, audit wrapping, and validation."
  spec.license       = "CC-BY-NC-SA-4.0"
  spec.required_ruby_version = ">= 2.7"

  spec.files = Dir["lib/**/*.rb", "README.md", "LICENSE"]
  spec.require_paths = ["lib"]

  spec.add_development_dependency "minitest", "~> 5.0"
end
