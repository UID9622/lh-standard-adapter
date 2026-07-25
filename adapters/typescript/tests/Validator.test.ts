/**
 * Tests for the Validator.
 */

import { describe, it, expect } from 'vitest';
import { Validator, quickValidate } from '../src/Validator.js';
import { generateDNA } from '../src/DNAGenerator.js';
import { auditWrap } from '../src/AuditWrapper.js';

describe('Validator', () => {
  it('should validate a correctly wrapped payload', () => {
    const dna = generateDNA('audit', 'WRAP');
    const audit = auditWrap({ data: 'test' }, 'audit');
    const wrapped = {
      dna,
      audit,
      payload: { data: 'test' },
      meta: { uid: '9622', version: '1.0' },
    };
    const result = new Validator().validate(wrapped);
    expect(result.valid).toBe(true);
    expect(result.errors).toHaveLength(0);
  });

  it('should reject an empty object', () => {
    const result = new Validator().validate({});
    expect(result.valid).toBe(false);
    expect(result.errors.length).toBeGreaterThan(0);
  });

  it('should detect missing top-level keys', () => {
    const result = new Validator().validate({ dna: 'test' });
    expect(result.valid).toBe(false);
    expect(result.errors.some(e => e.includes('Missing top-level keys'))).toBe(true);
  });

  it('should detect invalid DNA format', () => {
    const audit = auditWrap({});
    const wrapped = {
      dna: 'invalid-dna-format',
      audit,
      payload: {},
      meta: { uid: '9622' },
    };
    const result = new Validator().validate(wrapped);
    expect(result.valid).toBe(false);
    expect(result.errors.some(e => e.includes('DNA does not match regex'))).toBe(true);
  });

  it('should detect UID mismatch', () => {
    const dna = generateDNA();
    const audit = auditWrap({});
    const wrapped = {
      dna,
      audit,
      payload: {},
      meta: { uid: '9999' }, // Different from audit's UID9622
    };
    const result = new Validator().validate(wrapped);
    expect(result.valid).toBe(false);
    expect(result.errors.some(e => e.includes('UID mismatch'))).toBe(true);
  });

  it('quickValidate should return true for valid structure', () => {
    const dna = generateDNA();
    const audit = auditWrap({});
    const valid = quickValidate({ dna, audit, payload: {}, meta: {} });
    expect(valid).toBe(true);
  });

  it('quickValidate should return false for invalid structure', () => {
    expect(quickValidate({})).toBe(false);
    expect(quickValidate({ dna: 'bad' })).toBe(false);
  });
});