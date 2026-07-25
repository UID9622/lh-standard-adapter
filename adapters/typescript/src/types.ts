/**
 * Common types for the LongHun DNA Traceability Standard.
 */

export interface Hexagram {
  symbol: string;
  enName: string;
  cnName: string;
  domain: string;
}

export interface StemBranch {
  year: string;
  month: string;
  day: string;
  shichen: string;
}

export interface BehaviorSignature {
  P: string;
  F: string;
  T: number;
  E: string;
  C: number;
  R: number;
  A: string;
  X: string;
  Y: string;
  Z: number;
}

export type BehaviorPattern =
  | 'MODE-DefensiveDefaulter'
  | 'MODE-ExternalTrustSpender'
  | 'MODE-InternalDestroyer'
  | 'MODE-Fluctuating'
  | 'MODE-StableDisciplined';

export interface AuditWrapperResult {
  audit_version: string;
  uid: string;
  persona: string;
  task_type: string;
  behavior_signature: BehaviorSignature;
  behavior_pattern: BehaviorPattern;
  behavior_labels: string[];
  color: '🟢' | '🟡' | '🔴';
  timestamp: string;
  payload_hash: string;
}

export interface ValidationResult {
  valid: boolean;
  errors: string[];
  warnings: string[];
  summary: string;
}