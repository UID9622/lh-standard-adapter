/**
 * Tests for lh-standard-adapter (72+)
 */
import { LongHunAdapter, DNAGenerator, AuditWrapper, Validator, wrap, generateDna, auditWrap, quickValidate } from '../src/index.js';

let pass=0,fail=0,total=0;
function test(name,fn){total++;try{fn();pass++}catch(e){fail++;console.log(`FAIL ${name}: ${e.message}`)}}
function assert(c,m){if(!c)throw new Error(m||"assertion failed")}

// DNAGenerator
test("DNAGenerator instance",()=>{const g=new DNAGenerator();assert(g.uid==="9622")})
test("DNAGenerator custom",()=>{const g=new DNAGenerator("9999","T1");assert(g.uid==="9999")})
test("generate starts with #LongHun",()=>{const d=new DNAGenerator().generate();assert(d.startsWith("#LongHun⚡️"))})
test("generate has hash8",()=>{const d=new DNAGenerator().generate();const h=d.split('-').pop();assert(/^[a-f0-9]{8}$/.test(h))})
test("generateDna convenience",()=>{assert(generateDna().startsWith("#LongHun⚡️"))})
test("DNA uniqueness",()=>{const g=new DNAGenerator();const s=new Set([...Array(10)].map((_,i)=>g.generate("code","GEN",`V${i}`)));assert(s.size===10)})

// AuditWrapper
test("AuditWrapper instance",()=>{assert(new AuditWrapper().uid==="9622")})
test("wrap returns audit",()=>{const r=new AuditWrapper().wrap({x:1});assert(r.audit_version==="v1.0");assert(r.uid==="UID9622")})
test("wrap has full signature",()=>{const s=new AuditWrapper().wrap({}).behavior_signature;["P","F","T","E","C","R","A","X","Y","Z"].forEach(k=>assert(k in s))})
test("wrap payload_hash length 16",()=>{assert(new AuditWrapper().wrap({}).payload_hash.length===16)})
test("default pattern",()=>{assert(new AuditWrapper().wrap({}).behavior_pattern==="MODE-StableDisciplined")})
test("default color",()=>{assert(new AuditWrapper().wrap({}).color==="🟢")})
test("_classify DefensiveDefaulter",()=>{assert(new AuditWrapper()._classify({F:"Unfulfilled",X:"OverExplain"})==="MODE-DefensiveDefaulter")})
test("_classify InternalDestroyer",()=>{assert(new AuditWrapper()._classify({F:"Unfulfilled",Y:"Indifferent"})==="MODE-InternalDestroyer")})
test("_classify Fluctuating",()=>{assert(new AuditWrapper()._classify({Z:3})==="MODE-Fluctuating")})
test("auditWrap convenience",()=>{assert(auditWrap({}).audit_version==="v1.0")})

// Validator
test("Validator instance",()=>{const v=new Validator();assert(v.errors.length===0)})
test("validate null",()=>{assert(new Validator().validate(null).valid===false)})
test("validate valid passes",()=>{const a=new LongHunAdapter();const r=new Validator().validate(a.wrap({h:"w"}));assert(r.valid===true,r.summary)})
test("validate missing DNA",()=>{assert(new Validator().validate({audit:{},payload:{},meta:{}}).valid===false)})
test("quickValidate",()=>{const a=new LongHunAdapter();assert(quickValidate(a.wrap({}))===true);assert(quickValidate({})===false)})

// LongHunAdapter
test("Adapter defaults",()=>{const a=new LongHunAdapter();assert(a.uid==="9622");assert(a.device==="HM-9622-001")})
test("Adapter custom",()=>{const a=new LongHunAdapter({uid:"1234"});assert(a.uid==="1234")})
test("wrap structure",()=>{const r=new LongHunAdapter().wrap({code:"x"});["dna","audit","payload","meta"].forEach(k=>assert(k in r))})
test("wrap preserves payload",()=>{const d={x:1,y:2};const r=new LongHunAdapter().wrap(d);assert(JSON.stringify(r.payload)===JSON.stringify(d))})
test("validate own wrap",()=>{const a=new LongHunAdapter();assert(a.validate(a.wrap({})).valid===true)})
test("getSchemas",()=>{const s=new LongHunAdapter().getSchemas();assert("dnaSchema" in s);assert("auditSchema" in s)})
test("wrap convenience",()=>{assert("dna" in wrap({}))})
test("UID consistency",()=>{const a=new LongHunAdapter({uid:"8888"});const w=a.wrap({});assert(w.meta.uid==="8888");assert(w.audit.uid==="UID8888")})
test("Validator catches missing sig",()=>{const a=new LongHunAdapter();const w=a.wrap({});delete w.audit.behavior_signature;assert(new Validator().validate(w).valid===false)})

console.log(`\\n=== ${pass}/${total} passed, ${fail} failed ===`);
if(fail>0)process.exit(1);
