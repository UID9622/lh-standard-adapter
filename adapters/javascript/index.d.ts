export interface DnaOptions {
  taskType?: string;
  uid?: string;
  device?: string;
  timestamp?: Date;
}

export interface AuditFactors {
  P: string;
  F: string;
  T: string;
  E: string;
  C: string;
  R: string;
  A: string;
  X: string;
  Y: string;
  Z: string;
}

export interface AuditSignature {
  pattern: string;
  signature: string;
  factors: AuditFactors;
}

export interface WrappedPayload {
  dna: string;
  audit: AuditSignature;
  payload: any;
  metadata: {
    taskType: string;
    persona: string;
    timestamp: string;
    version: string;
  };
}

export interface ValidationResult {
  valid: boolean;
  error?: string;
}

export interface AdapterOptions {
  uid?: string;
  device?: string;
}

export declare class LongHunAdapter {
  constructor(options?: AdapterOptions);
  wrap(data: any, taskType?: string, persona?: string): WrappedPayload;
  validate(wrapped: WrappedPayload): ValidationResult;
  getSchemas(): { dnaSchema: object; auditSchema: object };
}

export declare function generateDnaCode(options?: DnaOptions): string;
export declare function wrapPayload(data: any, taskType?: string, persona?: string, options?: AdapterOptions): WrappedPayload;
export declare function validateWrappedPayload(wrapped: WrappedPayload): ValidationResult;
