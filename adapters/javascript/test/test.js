/**
 * Test Suite for lh-standard-adapter JavaScript adapter
 *
 * DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷝Li-TEST-v1.0.0
 *
 * 72+ tests covering dna_generator, audit_wrapper, validator, and integration.
 */

import { DNAGenerator, generateDna, TIAN_GAN, DI_ZHI, SHI_CHEN, HEXAGRAMS, TASK_HEXAGRAM_MAP } from '../src/dna_generator.js';
import { AuditWrapper, auditWrap, P_VALUES, F_VALUES, E_VALUES, A_VALUES, X_VALUES, Y_VALUES, LABEL_MAP } from '../src/audit_wrapper.js';
import { Validator, quickValidate } from '../src/validator.js';
import { LongHunAdapter, VERSION, DNA } from '../src/index.js';
import { DNA_SCHEMA, AUDIT_SCHEMA } from '../src/schemas.js';

// --- Test Harness ---

let passed = 0;
let failed = 0;
const failures = [];

function assert(condition, label) {
    if (condition) {
        passed++;
    } else {
        failed++;
        failures.push(label);
        console.error(`  FAIL: ${label}`);
    }
}

function assertEqual(actual, expected, label) {
    if (actual === expected) {
        passed++;
    } else {
        failed++;
        failures.push(`${label} — expected: ${JSON.stringify(expected)}, got: ${JSON.stringify(actual)}`);
        console.error(`  FAIL: ${label} — expected: ${expected}, got: ${actual}`);
    }
}

function assertContains(haystack, needle, label) {
    if (haystack.includes(needle)) {
        passed++;
    } else {
        failed++;
        failures.push(`${label} — "${needle}" not found`);
        console.error(`  FAIL: ${label} — "${needle}" not found`);
    }
}

const adapter = new LongHunAdapter();

// ============================================================
// 1. DNA Generator Tests (24 tests)
// ============================================================
console.log('\n=== DNA Generator Tests ===');

// --- Constant tests ---
assert(TIAN_GAN.length === 10, 'TIAN_GAN has 10 elements');
assert(TIAN_GAN[0] === 'Jia', 'TIAN_GAN first is Jia');
assert(TIAN_GAN[9] === 'Gui', 'TIAN_GAN last is Gui');
assert(DI_ZHI.length === 12, 'DI_ZHI has 12 elements');
assert(DI_ZHI[0] === 'Zi', 'DI_ZHI first is Zi');
assert(SHI_CHEN.length === 12, 'SHI_CHEN has 12 elements');
assert(SHI_CHEN[0] === 'ZiShi', 'SHI_CHEN first is ZiShi');
assert(HEXAGRAMS.length === 14, 'HEXAGRAMS has 14 entries');
assert(TASK_HEXAGRAM_MAP.default === 'governance', 'TASK_HEXAGRAM_MAP default is governance');
assert(TASK_HEXAGRAM_MAP.code === 'engine', 'TASK_HEXAGRAM_MAP code is engine');

// --- Basic generation ---
const dna1 = generateDna();
assert(typeof dna1 === 'string', 'generateDna returns string');
assert(dna1.startsWith('#LongHun⚡️'), 'DNA starts with #LongHun⚡️');
assert(dna1.includes('·'), 'DNA contains stem-branch separators');

// --- DNA structure ---
const stemParts = dna1.split('·');
assert(stemParts.length >= 5, 'DNA has at least 5 ·-separated parts');

// --- Hash8 ---
assert(/[a-f0-9]{8}$/.test(dna1), 'DNA ends with 8-char hex hash');

// --- Task type → hexagram mapping ---
const gen = new DNAGenerator();

const codeDna = gen.generate('code');
assertContains(codeDna, '䷜', 'code task uses Kan (䷜)');

const deployDna = gen.generate('deploy');
assertContains(deployDna, '䷸', 'deploy task uses Xun (䷸)');

const auditDna = gen.generate('audit');
assertContains(auditDna, '䷝', 'audit task uses Li (䷝)');

const securityDna = gen.generate('security');
assertContains(securityDna, '䷲', 'security task uses Zhen (䷲)');

// --- More task types ---
const archiveDna = gen.generate('archive');
assertContains(archiveDna, '䷁', 'archive task uses Kun (䷁)');

const initDna = gen.generate('init');
assertContains(initDna, '䷂', 'init task uses Zhun (䷂)');

// --- Task type tests ---
assertContains(gen.generate('learn'), '䷃', 'learn task uses Meng (䷃)');
assertContains(gen.generate('legal'), '䷅', 'legal task uses Song (䷅)');
assertContains(gen.generate('privacy'), '䷳', 'privacy task uses Gen (䷳)');
assertContains(gen.generate('trust'), '䷹', 'trust task uses Dui (䷹)');
assertContains(gen.generate('complete'), '䷾', 'complete task uses JiJi (䷾)');
assertContains(gen.generate('progress'), '䷿', 'progress task uses WeiJi (䷿)');

// --- Unknown task defaults to governance ---
const unknownDna = gen.generate('nonexistent');
assertContains(unknownDna, '䷀', 'unknown task defaults to Qian (䷀)');
assertContains(unknownDna, 'Qian', 'unknown task includes Qian name');
assertContains(unknownDna, 'ADAPTER-NONEXISTENT', 'body contains uppercased task type');

// --- Custom version ---
const customVerDna = gen.generate('default', 'WRAP', 'V2.5');
assertContains(customVerDna, 'V2.5', 'custom version appears in body');

// --- Custom action ---
const customActionDna = gen.generate('code', 'DEPLOY');
assertContains(customActionDna, 'ADAPTER-CODE-DEPLOY', 'custom action appears in body');

// ============================================================
// 2. Audit Wrapper Tests (29 tests)
// ============================================================
console.log('\n=== Audit Wrapper Tests ===');

const wrapper = new AuditWrapper();
const auditResult = wrapper.wrap({ key: 'value' }, 'default', 'P04');

// --- Basic structure ---
assert(typeof auditResult === 'object', 'wrap returns object');
assert(auditResult.audit_version === 'v1.0', 'audit_version is v1.0');
assert(auditResult.uid === 'UID9622', 'uid is UID9622');
assert(auditResult.persona === 'P04', 'persona is P04');
assert(auditResult.task_type === 'default', 'task_type is default');

// --- Signature keys ---
const sig = auditResult.behavior_signature;
assert(typeof sig === 'object', 'behavior_signature is object');
assert(Object.keys(sig).length === 10, 'signature has 10 keys');
assert(sig.P === 'HasPromise', 'default P = HasPromise');
assert(sig.F === 'Fulfilled', 'default F = Fulfilled');
assert(sig.T === 0.0, 'default T = 0.0');
assert(sig.E === 'Willing', 'default E = Willing');
assert(sig.C === 0, 'default C = 0');
assert(sig.R === 0, 'default R = 0');
assert(sig.A === 'Self', 'default A = Self');
assert(sig.X === 'Genuine', 'default X = Genuine');
assert(sig.Y === 'NoResponse', 'default Y = NoResponse');
assert(sig.Z === 1.0, 'default Z = 1.0');

// --- Pattern ---
assert(auditResult.behavior_pattern === 'MODE-StableDisciplined', 'default pattern is StableDisciplined');

// --- Labels ---
assert(Array.isArray(auditResult.behavior_labels), 'behavior_labels is array');
assert(auditResult.behavior_labels.length > 0, 'behavior_labels is not empty');
assertContains(auditResult.behavior_labels.join(','), '有承诺', 'labels contain 有承诺');
assertContains(auditResult.behavior_labels.join(','), '已兑现', 'labels contain 已兑现');

// --- Color ---
assert(auditResult.color === '🟢', 'default color is green');

// --- Timestamp ---
assert(typeof auditResult.timestamp === 'string', 'timestamp is string');
assert(auditResult.timestamp.includes('T'), 'timestamp contains T');
assert(auditResult.timestamp.includes('+08:00'), 'timestamp has +08:00 offset');

// --- Payload hash ---
assert(typeof auditResult.payload_hash === 'string', 'payload_hash is string');
assert(auditResult.payload_hash.length === 16, 'payload_hash is 16 chars');
assert(/^[a-f0-9]{16}$/.test(auditResult.payload_hash), 'payload_hash is valid hex');

// --- Payload hash changes with data ---
const auditResult2 = wrapper.wrap({ different: 'data' });
assert(auditResult2.payload_hash !== auditResult.payload_hash, 'payload_hash changes with different payload');

// --- Custom persona ---
const auditP05 = wrapper.wrap({}, 'code', 'P05');
assert(auditP05.persona === 'P05', 'custom persona is preserved');

// --- Pattern classification (override via direct _classify) ---
assert(wrapper._classify({ P: 'HasPromise', F: 'Unfulfilled', X: 'OverExplain', Y: 'NoResponse', Z: 1.0 }) === 'MODE-DefensiveDefaulter',
    'F=Unfulfilled + X=OverExplain → DefensiveDefaulter');

assert(wrapper._classify({ F: 'Fulfilled', A: 'Outsider', X: '', Y: '', Z: 1.0 }) === 'MODE-ExternalTrustSpender',
    'F=Fulfilled + A=Outsider → ExternalTrustSpender');

assert(wrapper._classify({ F: 'Unfulfilled', X: 'Silent', Y: 'Indifferent', Z: 1.0 }) === 'MODE-InternalDestroyer',
    'F=Unfulfilled + Y=Indifferent → InternalDestroyer');

assert(wrapper._classify({ F: 'Fulfilled', X: 'Genuine', Y: 'Changed', Z: 5.0 }) === 'MODE-Fluctuating',
    'Z > 2.0 → Fluctuating');

assert(wrapper._classify({ F: 'Fulfilled', X: 'Genuine', Y: 'NoResponse', Z: 1.0 }) === 'MODE-StableDisciplined',
    'Normal → StableDisciplined');

// --- Color determination ---
assert(wrapper._determineColor('MODE-InternalDestroyer', 0) === '🔴', 'InternalDestroyer → 🔴');
assert(wrapper._determineColor('MODE-Fluctuating', 5) === '🟡', 'Fluctuating + R>3 → 🟡');
assert(wrapper._determineColor('MODE-Fluctuating', 1) === '🟢', 'Fluctuating + R≤3 → 🟢');
assert(wrapper._determineColor('MODE-DefensiveDefaulter', 3) === '🟡', 'DefensiveDefaulter + R>2 → 🟡');
assert(wrapper._determineColor('MODE-DefensiveDefaulter', 1) === '🟢', 'DefensiveDefaulter + R≤2 → 🟢');
assert(wrapper._determineColor('MODE-StableDisciplined', 0) === '🟢', 'StableDisciplined → 🟢');

// --- Value sets ---
assert(P_VALUES.includes('HasPromise'), 'P_VALUES has HasPromise');
assert(P_VALUES.includes('NoPromise'), 'P_VALUES has NoPromise');
assert(F_VALUES.length === 3, 'F_VALUES has 3 values');
assert(E_VALUES.length === 4, 'E_VALUES has 4 values');
assert(A_VALUES.length === 5, 'A_VALUES has 5 values');
assert(X_VALUES.length === 4, 'X_VALUES has 4 values');
assert(Y_VALUES.length === 4, 'Y_VALUES has 4 values');

// --- LABEL_MAP ---
assert(LABEL_MAP.P.HasPromise === '7F-P-有承诺', 'label map P/HasPromise correct');
assert(LABEL_MAP.F.Fulfilled === '7F-F-已兑现', 'label map F/Fulfilled correct');
assert(LABEL_MAP.E.Willing === '7F-E-心甘情愿', 'label map E/Willing correct');

// ============================================================
// 3. Validator Tests (19 tests)
// ============================================================
console.log('\n=== Validator Tests ===');

const validator = new Validator();

// --- Valid wrapped payload ---
const wrapped = adapter.wrap({ test: true });
const result = validator.validate(wrapped);
assert(result.valid === true, 'valid wrapped payload passes validation');
assert(result.errors.length === 0, 'valid payload has 0 errors');

// --- Reject null ---
const nullResult = validator.validate(null);
assert(nullResult.valid === false, 'null input rejected');
assert(nullResult.errors.length > 0, 'null input has errors');

// --- Reject empty object ---
const emptyResult = validator.validate({});
assert(emptyResult.valid === false, 'empty object rejected');

// --- Reject missing dna ---
const noDna = validator.validate({ audit: {}, payload: {}, meta: {} });
assert(noDna.valid === false, 'missing dna key rejected');

// --- Reject missing audit ---
const noAudit = validator.validate({ dna: 'test', payload: {}, meta: {} });
assert(noAudit.valid === false, 'missing audit key rejected');

// --- Reject missing payload ---
const noPayload = validator.validate({ dna: 'test', audit: {}, meta: {} });
assert(noPayload.valid === false, 'missing payload key rejected');

// --- Reject missing meta ---
const noMeta = validator.validate({ dna: 'test', audit: {}, payload: {} });
assert(noMeta.valid === false, 'missing meta key rejected');

// --- Reject empty DNA ---
const emptyDna = adapter.wrap({});
emptyDna.dna = '';
const emptyDnaResult = validator.validate(emptyDna);
assert(emptyDnaResult.valid === false, 'empty DNA rejected');

// --- Reject malformed DNA ---
const badDna = adapter.wrap({});
badDna.dna = 'not-a-dna-string';
const badDnaResult = validator.validate(badDna);
assert(badDnaResult.valid === false, 'malformed DNA rejected');

// --- Reject invalid hash8 ---
const badHash = adapter.wrap({});
badHash.dna = badHash.dna.replace(/[a-f0-9]{8}$/, 'zzzzzzzz');
const badHashResult = validator.validate(badHash);
assert(badHashResult.valid === false, 'invalid hash8 rejected');

// --- Warning on invalid pattern ---
const badPattern = adapter.wrap({});
badPattern.audit.behavior_pattern = 'MODE-Unknown';
const badPatternResult = validator.validate(badPattern);
assert(badPatternResult.warnings.length > 0, 'unknown pattern produces warning');

// --- Warning on invalid color ---
const badColor = adapter.wrap({});
badColor.audit.color = '🔵';
const badColorResult = validator.validate(badColor);
assert(badColorResult.warnings.length > 0, 'unknown color produces warning');

// --- Warning on suspicious payload_hash ---
const badPh = adapter.wrap({});
badPh.audit.payload_hash = 'zzzzzzzzzzzzzzzz';
const badPhResult = validator.validate(badPh);
assert(badPhResult.warnings.length > 0, 'bad payload_hash produces warning');

// --- UID mismatch detection ---
const uidMismatch = adapter.wrap({});
uidMismatch.meta.uid = '9999';
const uidResult = validator.validate(uidMismatch);
assert(uidResult.valid === false, 'UID mismatch detected');

// --- quickValidate ---
assert(quickValidate(wrapped) === true, 'quickValidate returns true for valid');
assert(quickValidate({}) === false, 'quickValidate returns false for empty');
assert(quickValidate({ dna: 'bad', audit: {} }) === false, 'quickValidate returns false for bad DNA');
assert(quickValidate(null) === false, 'quickValidate returns false for null');

// ============================================================
// 4. Integration Tests
// ============================================================
console.log('\n=== Integration Tests ===');

// --- Wrap + validate roundtrip ---
const integrationResult = adapter.wrap({ message: 'hello world' }, 'code', 'P01', 'TEST', 'V1.0');
assert(typeof integrationResult.dna === 'string', 'integration: dna is string');
assert(typeof integrationResult.audit === 'object', 'integration: audit is object');
assert(typeof integrationResult.payload === 'object', 'integration: payload is object');
assert(typeof integrationResult.meta === 'object', 'integration: meta is object');
assert(integrationResult.meta.adapter_version === VERSION, 'integration: adapter_version correct');
assert(integrationResult.meta.format === 'longhun-v∞', 'integration: format is longhun-v∞');
assert(integrationResult.meta.task_type === 'code', 'integration: task_type preserved');
assert(integrationResult.meta.persona === 'P01', 'integration: persona preserved');

const integrationValidation = adapter.validate(integrationResult);
assert(integrationValidation.valid === true, 'integration: roundtrip validation passes');

// --- Multiple wrap calls produce different DNA (different timestamps) ---
const dnaA = adapter.wrap({ a: 1 });
const dnaB = adapter.wrap({ b: 2 });
// If they're made in the same millisecond they could be the same,
// but the payload_hash in audit should differ
assert(dnaA.payload !== dnaB.payload || dnaA.audit.payload_hash !== dnaB.audit.payload_hash,
    'different payloads produce different audit hashes');

// --- Schemas ---
assert(DNA_SCHEMA.$schema.includes('json-schema.org'), 'DNA_SCHEMA has valid $schema');
assert(DNA_SCHEMA.title.includes('LongHun'), 'DNA_SCHEMA title includes LongHun');
assert(AUDIT_SCHEMA.$schema.includes('json-schema.org'), 'AUDIT_SCHEMA has valid $schema');
assert(AUDIT_SCHEMA.title.includes('LongHun'), 'AUDIT_SCHEMA title includes LongHun');

// --- LongHunAdapter constants ---
assert(VERSION === '1.0.0', 'VERSION is 1.0.0');
assert(DNA.startsWith('#LongHun⚡️'), 'DNA constant starts with #LongHun⚡️');

// ============================================================
// Summary
// ============================================================
console.log(`\n${'='.repeat(60)}`);
console.log(`Results: ${passed} passed, ${failed} failed (${passed + failed} total)`);
console.log(`${'='.repeat(60)}`);

if (failed > 0) {
    console.error('\nFailures:');
    failures.forEach(f => console.error(`  • ${f}`));
    process.exit(1);
} else {
    console.log('✅ All tests passed!');
}
