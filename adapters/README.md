# LongHun JavaScript/TypeScript Adapter (`lh-standard-adapter`)

Official JavaScript & TypeScript adapter implementation for the **LongHun AI Traceability & Audit Protocol v1.0**.

---

## English Quickstart

### Installation

```bash
npm install lh-standard-adapter
# or
pnpm add lh-standard-adapter
```

### Usage

```typescript
import { LongHunAdapter } from 'lh-standard-adapter';

const adapter = new LongHunAdapter({ uid: "9622", device: "HM-9622-001" });

// 1. Wrap data
const payload = { query: "What is AI agent traceability?", response: "A standard for provenance." };
const wrapped = adapter.wrap(payload, "code", "P04");

console.log(wrapped.dna);
// Output: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷜Kan-ADAPTER-CODE-WRAP-V1.0-a3f8c1d9

// 2. Validate wrapped payload
const result = adapter.validate(wrapped);
console.log(result.summary); // ✅ VALID — 0 warning(s)
```

---

## 中文快速开始

### 安装

```bash
npm install lh-standard-adapter
```

### 使用示例

```typescript
import { LongHunAdapter } from 'lh-standard-adapter';

const adapter = new LongHunAdapter({ uid: "9622", device: "HM-9622-001" });

// 包装数据
const wrapped = adapter.wrap({ data: "示例" }, "code", "P04");

// 校验格式
const result = adapter.validate(wrapped);
console.log(result.summary);
```

---

## License

CC-BY-NC-SA 4.0
