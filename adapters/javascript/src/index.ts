import { DNAGenerator } from "./dnaGenerator";
import { AuditWrapper } from "./auditWrapper";
import { Validator } from "./validator";

export class LongHunAdapter {
  private generator: DNAGenerator;
  private wrapper: AuditWrapper;
  private validator: Validator;
  private uid: string;

  constructor(uid: string = "9622", device: string = "HM-9622-001") {
    this.uid = uid;
    this.generator = new DNAGenerator(uid, device);
    this.wrapper = new AuditWrapper(uid);
    this.validator = new Validator();
  }

  public wrap(data: any, taskType: string = "default", persona: string = "P04") {
    const dna = this.generator.generate(taskType);
    const audit = this.wrapper.wrap(data, taskType, persona);
    return {
      dna,
      audit,
      payload: data,
      meta: {
        adapter_version: "1.0.0",
        uid: this.uid,
        format: "longhun-v∞",
      },
    };
  }

  public validate(wrapped: any) {
    return this.validator.validate(wrapped);
  }
}

export * from "./dnaGenerator";
export * from "./auditWrapper";
export * from "./validator";
