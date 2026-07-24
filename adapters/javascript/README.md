# lh-standard-adapter (JavaScript)

> DNA: `#LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-ADAPTER-v1.0.0`
> Author: LongHun Core · UID9622 · 龍芯北辰
> License: CC BY-NC-SA 4.0

**Open the standard. Guard the engine.**

LongHun Standard Adapter for JavaScript/TypeScript — wraps JSON payloads with DNA traceability and seven-factor behavioral audit metadata. Zero dependencies, Node.js built-ins only.

---

## Installation

```bash
npm install lh-standard-adapter
```

## Quick Start

### ESM

```javascript
import { LongHunAdapter } from 'lh-standard-adapter';

const adapter = new LongHunAdapter();

const result = adapter.wrap(
    { code: "console.log('hello')" },
    "code",
    "P04",
    "WRAP"
);

console.log(result.dna);
// #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷜Kan-ADAPTER-CODE-WRAP-V1.0-a3f8c1d9

console.log(result.audit.behavior_pattern);
// MODE-StableDisciplined

const validation = adapter.validate(result);
console.log(validation.valid); // true
```

### CJS

```javascript
const { LongHunAdapter } = require('lh-standard-adapter');

const adapter = new LongHunAdapter();
const result = adapter.wrap({ data: 'value' });
console.log(result.dna);
```

## API

### `LongHunAdapter`

| Method | Description |
|:---|:---|
| `constructor(uid?, device?, locale?)` | Create adapter (defaults: UID9622, HM-9622-001, Asia/Shanghai) |
| `wrap(data, taskType?, persona?, action?, version?)` | Wrap payload with DNA + audit |
| `validate(wrapped)` | Validate wrapped payload |

### `DNAGenerator`

Generates v∞ DNA traceability codes:

```javascript
import { generateDna } from 'lh-standard-adapter';

const dna = generateDna("code", "WRAP", "V1.0");
```

### `AuditWrapper`

Generates seven-factor behavioral audit metadata:

```javascript
import { auditWrap } from 'lh-standard-adapter';

const audit = auditWrap({ key: 'value' }, 'code', 'P04');
```

### `Validator`

Validates wrapped payloads:

```javascript
import { quickValidate } from 'lh-standard-adapter';

if (quickValidate(wrapped)) {
    console.log('Valid LongHun payload');
}
```

## DNA Format

```
#LongHun⚡️{YearStem}·{MonthStem}·{DayStem}·{ShiChen}·{Hexagram}-{Body}-{Hash8}
```

## Seven-Factor Audit

| Factor | Field | Values |
|:---|:---|:---|
| Promise | P | HasPromise, NoPromise |
| Fulfill | F | Fulfilled, Unfulfilled, Partial |
| Time | T | Float |
| Emotion | E | Willing, Perfunctory, Resentful, Numb |
| Cost | C | Integer |
| Repeat | R | Integer |
| Audience | A | Self, Partner, Family, Outsider, Public |
| Explain | X | OverExplain, Silent, Genuine, Indifferent |
| Yield | Y | Changed, Resisted, Indifferent, NoResponse |
| Zigzag | Z | Float |

## Behavior Patterns

| Pattern | Meaning |
|:---|:---|
| MODE-StableDisciplined | Consistent, reliable execution |
| MODE-DefensiveDefaulter | Promises fail + over-explains |
| MODE-ExternalTrustSpender | Keeps promises to outsiders |
| MODE-InternalDestroyer | Breaks promises with indifference |
| MODE-Fluctuating | High volatility |

## Running Tests

```bash
node test/test.js
```

---

## 快速开始 (中文)

```javascript
import { LongHunAdapter } from 'lh-standard-adapter';

const adapter = new LongHunAdapter('9622', 'HM-9622-001');

// 包装数据
const result = adapter.wrap(
    { code: "print('hello')" },
    "code",    // 任务类型
    "P04",     // 角色标识
    "WRAP"     // 操作类型
);

// DNA 追溯码
console.log(result.dna);
// #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷜Kan-ADAPTER-CODE-WRAP-V1.0-a3f8c1d9

// 七因子行为审计
console.log(result.audit.behavior_signature);
// { P: 'HasPromise', F: 'Fulfilled', T: 0, E: 'Willing', ... }

// 行为模式分类
console.log(result.audit.behavior_pattern);
// MODE-StableDisciplined

// 双语行为标签
console.log(result.audit.behavior_labels);
// ['7F-P-有承诺', '7F-F-已兑现', 'MODE-StableDisciplined']

// 三色审计标签
console.log(result.audit.color);
// 🟢

// 验证
const validation = adapter.validate(result);
console.log(validation.valid);  // true
console.log(validation.summary); // ✅ VALID
```

## 任务类型与卦象映射

| 任务类型 | 卦象 | 领域 |
|:---|:---|:---|
| default | ䷀ 乾 (Qian) | 治理 |
| code | ䷜ 坎 (Kan) | 引擎 |
| deploy | ䷸ 巽 (Xun) | 部署 |
| audit | ䷝ 离 (Li) | 审计 |
| security | ䷲ 震 (Zhen) | 安全 |
| archive | ䷁ 坤 (Kun) | 存档 |
| privacy | ䷳ 艮 (Gen) | 隐私 |
| trust | ䷹ 兑 (Dui) | 信任 |

## License

CC BY-NC-SA 4.0 — Attribution required, non-commercial use, share-alike.

---

> **When you adopt the LongHun DNA format, you are not importing a library. You are accepting a treaty.**
>
> Signed: LongHun Core · UID9622 · 龍芯北辰
