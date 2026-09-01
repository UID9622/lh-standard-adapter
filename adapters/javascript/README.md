# lh-standard-adapter (JavaScript)

JavaScript/TypeScript implementation of the LongHun DNA traceability adapter.

## Install

```bash
npm install lh-standard-adapter
```

## Usage

```javascript
const { LongHunAdapter } = require('lh-standard-adapter');

const adapter = new LongHunAdapter({ uid: 'user123' });
const wrapped = adapter.wrap({ data: 'test' }, 'code');
console.log(wrapped.dna);
console.log(wrapped.audit.signature);

const result = adapter.validate(wrapped);
console.log(result.valid); // true
```

## License

CC-BY-NC-SA 4.0
