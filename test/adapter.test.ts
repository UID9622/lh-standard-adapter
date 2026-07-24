import { LongHunAdapter } from '../src/adapter';
import { generateDna } from '../src/dna-generator';
import { auditWrap } from '../src/audit-wrapper';
import { quickValidate } from '../src/validator';

describe('LongHunAdapter', () => {
  const adapter = new LongHunAdapter();

  test('should wrap payload with dna and audit', () => {
    const result = adapter.wrap({ test: 'data' });
    expect(result).toHaveProperty('dna');
    expect(result).toHaveProperty('audit');
    expect(result).toHaveProperty('payload');
    expect(result).toHaveProperty('meta');
    expect(result.payload).toEqual({ test: 'data' });
  });

  test('should validate wrapped payload', () => {
    const result = adapter.wrap({ test: 'data' });
    const validation = adapter.validate(result);
    expect(validation).toHaveProperty('valid');
  });

  test('should generate dna code', () => {
    const dna = generateDna('code', 'WRAP');
    expect(dna).toHaveProperty('code');
    expect(dna.code).toContain('#LongHun');
  });

  test('should audit wrap payload', () => {
    const audit = auditWrap({ msg: 'hello' }, 'code', 'P04', '9622');
    expect(audit).toHaveProperty('behaviorSignature');
    expect(audit).toHaveProperty('behaviorPattern');
  });

  test('should validate correctly', () => {
    const result = adapter.wrap({ key: 'value' });
    const validation = quickValidate(result);
    expect(validation).toHaveProperty('valid');
  });
});
