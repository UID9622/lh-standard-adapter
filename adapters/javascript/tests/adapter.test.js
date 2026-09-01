const { describe, it } = require("node:test");
const assert = require("node:assert/strict");
const { LongHunAdapter, generateDnaCode, wrapPayload, validateWrappedPayload } = require("../index");

describe("DNA Generator", () => {
  it("should generate DNA code with correct prefix", () => {
    const dna = generateDnaCode();
    assert.ok(dna.startsWith("#LongHun⚡️"));
    assert.ok(dna.includes("DNA-GENERATOR-v1.0.0-"));
  });

  it("should generate unique DNA codes", () => {
    const dna1 = generateDnaCode();
    const dna2 = generateDnaCode();
    assert.notEqual(dna1, dna2);
  });

  it("should include task type in hexagram selection", () => {
    const dna = generateDnaCode({ taskType: "code" });
    assert.ok(dna.includes("Kan") || dna.includes("engine"));
  });

  it("should use custom uid and device", () => {
    const dna = generateDnaCode({ uid: "user123", device: "dev456" });
    assert.ok(dna.startsWith("#LongHun⚡️"));
  });
});

describe("Audit Wrapper", () => {
  it("should wrap payload with audit signature", () => {
    const adapter = new LongHunAdapter();
    const wrapped = adapter.wrap({ test: true });
    assert.ok(wrapped.dna);
    assert.ok(wrapped.audit);
    assert.ok(wrapped.audit.signature);
    assert.ok(wrapped.audit.pattern);
    assert.ok(wrapped.audit.factors);
  });

  it("should classify behavior patterns", () => {
    const adapter = new LongHunAdapter();
    const wrapped = adapter.wrap({ risk: "low", activity: "consistent" });
    assert.equal(wrapped.audit.pattern, "StableDisciplined");
  });

  it("should include all 10 audit factors", () => {
    const adapter = new LongHunAdapter();
    const wrapped = adapter.wrap({});
    const factors = wrapped.audit.factors;
    for (const key of ["P", "F", "T", "E", "C", "R", "A", "X", "Y", "Z"]) {
      assert.ok(key in factors, `Missing factor: ${key}`);
    }
  });

  it("should include metadata", () => {
    const adapter = new LongHunAdapter();
    const wrapped = adapter.wrap({}, "code", "test");
    assert.equal(wrapped.metadata.taskType, "code");
    assert.equal(wrapped.metadata.persona, "test");
    assert.equal(wrapped.metadata.version, "1.0.0");
  });
});

describe("Validator", () => {
  it("should validate correct DNA code", () => {
    const dna = generateDnaCode();
    const result = validateWrappedPayload({ dna, audit: { pattern: "test", signature: "P/F/T/E/C/R/A/X/Y/Z:pass/low/consistent/stable/compliant/standard/verified/success/positive/stable", factors: { P: "pass", F: "low", T: "consistent", E: "stable", C: "compliant", R: "standard", A: "verified", X: "success", Y: "positive", Z: "stable" } }, payload: {}, metadata: { version: "1.0.0" } });
    assert.ok(result.valid);
  });

  it("should reject invalid DNA", () => {
    const result = validateWrappedPayload({ dna: "invalid", audit: { pattern: "test", signature: "test", factors: { P: "", F: "", T: "", E: "", C: "", R: "", A: "", X: "", Y: "", Z: "" } }, payload: {}, metadata: { version: "1.0.0" } });
    assert.ok(!result.valid);
  });

  it("should reject missing payload", () => {
    const result = validateWrappedPayload({ dna: generateDnaCode(), audit: { pattern: "test", signature: "test", factors: { P: "", F: "", T: "", E: "", C: "", R: "", A: "", X: "", Y: "", Z: "" } }, metadata: { version: "1.0.0" } });
    assert.ok(!result.valid);
  });
});

describe("LongHunAdapter", () => {
  it("should create adapter with options", () => {
    const adapter = new LongHunAdapter({ uid: "test", device: "dev" });
    assert.ok(adapter);
  });

  it("should wrap and validate", () => {
    const adapter = new LongHunAdapter();
    const wrapped = adapter.wrap({ data: "test" }, "code");
    const result = adapter.validate(wrapped);
    assert.ok(result.valid);
  });

  it("should return schemas", () => {
    const adapter = new LongHunAdapter();
    const schemas = adapter.getSchemas();
    assert.ok(schemas.dnaSchema);
    assert.ok(schemas.auditSchema);
  });
});
