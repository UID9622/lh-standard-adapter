/**
 * LongHun Standard Adapter — JavaScript/TypeScript
 */
import { DNAGenerator, generateDna } from './dna_generator.js';
import { AuditWrapper, auditWrap } from './audit_wrapper.js';
import { Validator, quickValidate } from './validator.js';

export class LongHunAdapter {
  constructor(options = {}) {
    const { uid = "9622", device = "HM-9622-001", locale = "Asia/Shanghai" } = options;
    this.uid = uid; this.device = device; this.locale = locale;
    this._dnaGen = new DNAGenerator(uid, device, locale);
    this._audit = new AuditWrapper(uid);
    this._validator = new Validator();
  }

  wrap(data, taskType = "default", persona = "P04", action = "WRAP", version = null) {
    const dna = this._dnaGen.generate(taskType, action, version);
    const audit = this._audit.wrap(data, taskType, persona);
    return {
      dna, audit, payload: data,
      meta: {
        adapter_version: "1.0.0", uid: this.uid, device: this.device,
        task_type: taskType, persona,
        generated_at: new Date().toISOString(), format: "longhun-v∞"
      }
    };
  }

  validate(wrapped) { return this._validator.validate(wrapped); }

  getSchemas() {
    return {
      dnaSchema: {
        type: "string",
        pattern: "^#LongHun⚡️[A-Z][a-zA-Z]+·[A-Z][a-zA-Z]+·[A-Z][a-zA-Z]+·[A-Z][a-zA-Z]+·[\\u4DC0-\\u4DFF][A-Za-z]+-.+-[a-f0-9]{8}$",
        description: "v∞ DNA traceability code"
      },
      auditSchema: {
        type: "object",
        required: ["audit_version","uid","behavior_signature","behavior_pattern","behavior_labels","color"],
        properties: {
          audit_version: {type:"string"}, uid: {type:"string"},
          behavior_signature: {type:"object", required:["P","F","T","E","C","R","A","X","Y","Z"]},
          behavior_pattern: {type:"string"}, color: {type:"string", enum:["🟢","🟡","🔴"]},
          payload_hash: {type:"string", pattern:"^[a-f0-9]{16}$"}
        }
      }
    };
  }
}

export function wrap(data, taskType="default", persona="P04", uid="9622", device="HM-9622-001") {
  return new LongHunAdapter({uid, device}).wrap(data, taskType, persona);
}

export { DNAGenerator, AuditWrapper, Validator, generateDna, auditWrap, quickValidate };
