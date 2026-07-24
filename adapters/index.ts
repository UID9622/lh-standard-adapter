import { DNAGenerator } from './dnaGenerator';
import { AuditWrapper, AuditResult } from './auditWrapper';
import { Validator, ValidationResult } from './validator';

export interface AdapterOptions {
  uid?: string;
  device?: string;
}

export interface WrappedPayload {
  dna: string;
  audit: AuditResult;
  payload: any;
  meta: {
    uid: string;
    device: string;
    version: string;
  };
}

export class LongHunAdapter {
  private dnaGenerator: DNAGenerator;
  private auditWrapper: AuditWrapper;
  private validator: Validator;
  private uid: string;
  private device: string;

  constructor(options: AdapterOptions = {}) {
    this.uid = options.uid || "9622";
    this.device = options.device || "HM-9622-001";
    this.dnaGenerator = new DNAGenerator(this.uid, this.device);
    this.auditWrapper = new AuditWrapper(this.uid);
    this.validator = new Validator();
  }

  public wrap(data: any, taskType = "default", persona = "P04"): WrappedPayload {
    const dna = this.dnaGenerator.generate(taskType, "WRAP", "V1.0");
    const audit = this.auditWrapper.wrap(data, taskType, persona);

    return {
      dna,
      audit,
      payload: data,
      meta: {
        uid: this.uid,
        device: this.device,
        version: "V1.0"
      }
    };
  }

  public validate(wrapped: any): ValidationResult {
    return this.validator.validate(wrapped);
  }

  public getSchemas(): { dnaSchema: object; auditSchema: object } {
    return {
      dnaSchema: {
        type: "string",
        pattern: "^#LongHun⚡️.*"
      },
      auditSchema: {
        type: "object",
        required: ["audit_version", "uid", "behavior_signature", "behavior_pattern", "behavior_labels", "color"]
      }
    };
  }
}

export { DNAGenerator, AuditWrapper, Validator };
