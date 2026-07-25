/**
 * Tests for the DNA Generator.
 */

import { describe, it, expect } from 'vitest';
import { DNAGenerator, generateDNA } from '../src/DNAGenerator.js';

describe('DNAGenerator', () => {
  it('should generate a valid DNA string', () => {
    const dna = generateDNA();
    expect(dna).toMatch(/^#LongHun⚡️/);
    expect(dna).toContain('ADAPTER-DEFAULT-WRAP-V1.0');
    expect(dna).toMatch(/-[a-f0-9]{8}$/);
  });

  it('should include hexagram in the correct position', () => {
    const dna = generateDNA('audit', 'WRAP');
    // audit task maps to Li hexagram (䷝)
    expect(dna).toContain('䷝');
    expect(dna).toContain('ADAPTER-AUDIT-WRAP-V1.0');
  });

  it('should generate different hashes for different inputs', () => {
    const dna1 = generateDNA('code', 'WRAP');
    const dna2 = generateDNA('audit', 'WRAP');
    expect(dna1).not.toBe(dna2);
  });

  it('should support custom task types', () => {
    const dna = generateDNA('security', 'VALIDATE');
    expect(dna).toContain('ADAPTER-SECURITY-VALIDATE-V1.0');
  });

  it('should use the default hexagram for unknown task types', () => {
    const dna = generateDNA('unknown_type', 'WRAP');
    // Default is Qian (䷀)
    expect(dna).toContain('䷀');
  });

  it('should accept a custom version', () => {
    const dna = generateDNA('default', 'WRAP', 'V2.0');
    expect(dna).toContain('ADAPTER-DEFAULT-WRAP-V2.0');
  });

  it('should produce consistent results for the same input', () => {
    const generator = new DNAGenerator('9622', 'HM-9622-001');
    const dna1 = generator.generate('default', 'WRAP', 'V1.0');
    const dna2 = generator.generate('default', 'WRAP', 'V1.0');
    // Different timestamps → different hash
    expect(dna1).not.toBe(dna2);
  });
});