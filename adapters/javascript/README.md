# LongHun Standard Adapter — JavaScript / TypeScript (npm)

Official npm package adapter for **AI Traceability and Audit Protocol v1.0**.

## Installation

```bash
npm install @longhun/lh-standard-adapter
```

## Usage

```typescript
import { LongHunAdapter } from "@longhun/lh-standard-adapter";

const adapter = new LongHunAdapter("9622", "HM-9622-001");
const wrapped = adapter.wrap({ code: "console.log('hello world')" }, "code");

console.log(wrapped.dna);
console.log(adapter.validate(wrapped));
```
