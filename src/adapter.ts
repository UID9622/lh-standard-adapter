/**
 * LongHun Standard Adapter — wrap JSON payloads with DNA traceability
 * and seven-factor behavioral audit metadata.
 */

import { DNAGenerator, generateDna } from './dna-generator';
import { AuditWrapper, auditWrap } from './audit-wrapper';
import { Validator, quickValidate } from './validator';
import { VERSION } from './index';

export class LongHunAdapter {
  private uid: string;
  private device: string;
  private locale: string;
  private dnaGen: DNAGenerator;
  private audit: AuditWrapper;
  private validator: Validator;

  constructor(uid: string = "9622", device: string = "HM-9622-001", locale: string = "Asia/Shanghai") {
    this.uid = uid;
    this.device = device;
    this.locale = locale;
    this.dnaGen = new DNAGenerator(uid, device, locale);
    this.audit = new AuditWrapper(uid);
    this.validator = new Validator();
  }

  wrap(data: any, taskType: string = "default", persona: string = "P04",
       action: string = "WRAP", version?: string): Record<string, any> {
    const dna = this.dnaGen.generate(taskType, action, version);
    const audit = this.audit.wrap(data, taskType, persona);

    return {
      dna,
      audit,
      payload: data,
      meta: {
        adapterVersion: VERSION,
        uid: this.uid,
        device: this.device,
        taskType,
        persona,
        generatedAt: new Date().toISOString(),
        format: "longhun-v∞",
      },
    };
  }

  validate(wrapped: Record<string, any>): { valid: boolean; errors: string[]; warnings: string[]; summary: string } {
    return this.validator.validate(wrapped);
  }
}

export function wrap(data: any, taskType: string = "default", persona: string = "P04",
                     uid: string = "9622", device: string = "HM-9622-001"): Record<string, any> {
  const adapter = new LongHunAdapter(uid, device);
  return adapter.wrap(data, taskType, persona);
}
