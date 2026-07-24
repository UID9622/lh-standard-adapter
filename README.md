# lh-standard-adapter — JavaScript/TypeScript

LongHun Standard Adapter implementation in TypeScript for npm.

## Installation

```bash
npm install lh-standard-adapter
```

## Usage

```typescript
import { LongHunAdapter } from 'lh-standard-adapter';

const adapter = new LongHunAdapter();
const result = adapter.wrap({ code: "console.log('hello')" }, "code");
console.log(result.dna.code);
console.log(result.audit.behaviorPattern);
```

## API

- `LongHunAdapter(uid?, device?, locale?)` — Create an adapter instance
- `.wrap(data, taskType?, persona?, action?, version?)` — Wrap payload with DNA + audit
- `.validate(wrapped)` — Validate a wrapped payload

## License

CC BY-NC-SA 4.0
