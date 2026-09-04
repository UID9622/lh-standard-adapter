declare module 'lh-standard-adapter' {
  export interface LongHunAdapterOptions { uid?: string; device?: string; locale?: string; }
  export interface WrappedPayload { dna: string; audit: AuditObject; payload: any; meta: MetaObject; }
  export interface AuditObject {
    audit_version: string; uid: string; persona?: string; task_type?: string;
    behavior_signature: BehaviorSignature; behavior_pattern: string;
    behavior_labels: string[]; color: string; timestamp: string; payload_hash: string;
  }
  export interface BehaviorSignature {
    P: string; F: string; T: number; E: string; C: number; R: number;
    A: string; X: string; Y: string; Z: number;
  }
  export interface MetaObject {
    adapter_version: string; uid: string; device: string;
    task_type: string; persona: string; generated_at: string; format: string;
  }
  export interface ValidationResult { valid: boolean; errors: string[]; warnings: string[]; summary: string; }
  export class DNAGenerator {
    constructor(uid?: string, device?: string, locale?: string);
    generate(taskType?: string, action?: string, version?: string | null): string;
  }
  export class AuditWrapper {
    constructor(uid?: string);
    wrap(payload: any, taskType?: string, persona?: string): AuditObject;
  }
  export class Validator {
    constructor();
    validate(wrapped: WrappedPayload): ValidationResult;
  }
  export class LongHunAdapter {
    constructor(options?: LongHunAdapterOptions);
    uid: string; device: string;
    wrap(data: any, taskType?: string, persona?: string, action?: string, version?: string | null): WrappedPayload;
    validate(wrapped: WrappedPayload): ValidationResult;
    getSchemas(): { dnaSchema: any; auditSchema: any };
  }
  export function wrap(data: any, taskType?: string, persona?: string, uid?: string, device?: string): WrappedPayload;
  export function generateDna(taskType?: string, action?: string, version?: string | null): string;
  export function auditWrap(payload: any, taskType?: string, persona?: string): AuditObject;
  export function quickValidate(wrapped: WrappedPayload): boolean;
}
