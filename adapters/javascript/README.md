# lh-standard-adapter (JS/TS)

LongHun Standard Adapter — v∞ DNA traceability + seven-factor behavioral audit for npm.

## Install
```bash
npm install lh-standard-adapter
```

## Usage
```js
import { LongHunAdapter } from 'lh-standard-adapter';

const adapter = new LongHunAdapter({ uid: '9622', device: 'HM-9622-001' });
const wrapped = adapter.wrap({ code: "console.log('hello')" }, 'code', 'P04-Luban');
const result = adapter.validate(wrapped);
console.log(result.summary); // ✅ VALID
```

## API
- `new LongHunAdapter(options?)` — uid, device, locale
- `.wrap(data, taskType?, persona?, action?, version?)` → WrappedPayload
- `.validate(wrapped)` → ValidationResult
- `.getSchemas()` → { dnaSchema, auditSchema }

## License
CC-BY-NC-SA 4.0 — LongHun Core · UID9622
