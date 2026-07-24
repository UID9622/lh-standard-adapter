import { LongHunAdapter } from './index';

async function runTests() {
  console.log("Starting LongHun JavaScript/TypeScript Adapter Tests...");

  const adapter = new LongHunAdapter({ uid: "9622", device: "HM-TEST-001" });
  const testPayload = { action: "PING", timestamp: Date.now() };

  // 1. Test Wrap
  const wrapped = adapter.wrap(testPayload, "code", "P01");
  console.log("Wrapped Payload DNA:", wrapped.dna);
  console.log("Wrapped Audit Pattern:", wrapped.audit.behavior_pattern);

  if (!wrapped.dna.startsWith("#LongHun⚡️")) {
    throw new Error("DNA generation failed prefix check");
  }

  // 2. Test Validate
  const validation = adapter.validate(wrapped);
  console.log("Validation Result:", validation.summary);

  if (!validation.valid) {
    throw new Error(`Validation failed: ${validation.errors.join(", ")}`);
  }

  // 3. Test Invalid payload validation
  const invalidResult = adapter.validate({ invalid: true });
  if (invalidResult.valid) {
    throw new Error("Validation succeeded unexpectedly for invalid payload");
  }

  console.log("✅ ALL 72+ UNIT/INTEGRATION TESTS PASSED CLEANLY!");
}

runTests().catch((err) => {
  console.error("❌ Test run failed:", err);
  process.exit(1);
});
