/**
 * Tests for the Audit Wrapper.
 */

import { describe, it, expect } from 'vitest';
import { AuditWrapper, auditWrap } from '../src/AuditWrapper.js';

describe('AuditWrapper', () => {
  it('should wrap a payload with audit metadata', () => {
    const result = auditWrap({ hello: 'world' });
    expect(result.audit_version).toBe('v1.0');
    expect(result.uid).toBe('UID9622');
    expect(result.behavior_pattern).toBe('MODE-StableDisciplined');
    expect(result.color).toBe('🟢');
  });

  it('should include behavior labels', () => {
    const result = auditWrap({ test: 'data' });
    expect(result.behavior_labels).toContain('MODE-StableDisciplined');
    expect(result.behavior_labels).toContain('7F-P-有承诺');
    expect(result.behavior_labels).toContain('7F-F-已兑现');
  });

  it('should include a payload hash', () => {
    const result = auditWrap({ data: 'test' });
    expect(result.payload_hash).toMatch(/^[a-f0-9]{16}$/);
  });

  it('should support custom personas', () => {
    const result = auditWrap({}, 'default', 'P07');
    expect(result.persona).toBe('P07');
  });

  it('should support custom task types', () => {
    const result = auditWrap({}, 'security');
    expect(result.task_type).toBe('security');
  });

  it('should generate different hashes for different payloads', () => {
    const r1 = auditWrap({ a: 1 });
    const r2 = auditWrap({ a: 2 });
    expect(r1.payload_hash).not.toBe(r2.payload_hash);
  });

  it('should produce a timestamp in ISO format', () => {
    const result = auditWrap({});
    expect(() => new Date(result.timestamp)).not.toThrow();
  });
});