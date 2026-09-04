/**
 * LongHun (龍魂) v∞ DNA Traceability Standard — TypeScript Adapter
 *
 * Main entry point. Exports all public classes and utilities.
 */

export { DNAGenerator, generateDNA } from './DNAGenerator.js';
export { AuditWrapper, auditWrap } from './AuditWrapper.js';
export { Validator, quickValidate } from './Validator.js';
export type {
  Hexagram,
  StemBranch,
  BehaviorSignature,
  BehaviorPattern,
  AuditWrapperResult,
  ValidationResult,
} from './types.js';