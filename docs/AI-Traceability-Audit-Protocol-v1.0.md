# AI Traceability and Audit Protocol v1.0

> DNA: `#LongHun⚡️BingWu·GuiWei·JiaZi·JiJi-AI-TRACEABILITY-AUDIT-PROTOCOL-v1.0`
> Author: LongHun Core · UID9622
> GPG: `A2D0092CEE2E5BA87035600924C3704A8CC26D5F`
> License: CC BY-NC-SA 4.0
> Status: Global Release · v1.0 · 2026-07-24

**Open the standard. Guard the engine.**

---

## 1. DNA Traceability Format (v∞)

Format: `#LongHun⚡️{YearStem}·{MonthStem}·{DayStem}·{ShiChen}·{Hexagram}-{Body}-{Hash8}`

Example: `#LongHun⚡️BingWu·GuiWei·JiaZi·WeiShi·䷾JiJi-ADAPTER-CODE-WRAP-V1.0-a3f8c1d9`

| Field | Type | Example |
|:---|:---|:---|
| YearStem | GanZhi | BingWu |
| MonthStem | GanZhi | GuiWei |
| DayStem | GanZhi | JiaZi |
| ShiChen | ShiChen | WeiShi |
| Hexagram | Unicode+Alpha | ䷾JiJi |
| Body | Upper | ADAPTER-CODE-WRAP-V1.0 |
| Hash8 | hex[8] | a3f8c1d9 |

**Validation regex:**
```regex
^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([䷀-䷿][A-Za-z]+)-(.+)-([a-f0-9]{8})$
```

---

## 2. Seven-Factor Behavioral Cryptography

| Factor | ID | Values |
|:---|:---|:---|
| **Promise** | P | HasPromise, NoPromise |
| **Fulfill** | F | Fulfilled, Unfulfilled, Partial |
| **Time** | T | Float (seconds deviation) |
| **Emotion** | E | Willing, Perfunctory, Resentful, Numb |
| **Cost** | C | Integer (resource investment) |
| **Repeat** | R | Integer (cumulative failures) |
| **Audience** | A | Self, Partner, Family, Outsider, Public |
| **Explain** | X | OverExplain, Silent, Genuine, Indifferent |
| **Yield** | Y | Changed, Resisted, Indifferent, NoResponse |
| **Zigzag** | Z | Float (volatility, 1.0 = stable) |

---

## 3. Behavior Patterns

| Pattern | Trigger |
|:---|:---|
| MODE-StableDisciplined | Default (reliable) |
| MODE-DefensiveDefaulter | F=Unfulfilled + X=OverExplain |
| MODE-ExternalTrustSpender | F=Fulfilled + A=Outsider |
| MODE-InternalDestroyer | F=Unfulfilled + Y=Indifferent |
| MODE-Fluctuating | Z > 2.0 |

---

## 4. Three-Color Audit

| Color | Meaning |
|:---|:---|
| 🟢 Green | All checkpoints passed |
| 🟡 Yellow | Pending verification (48h review) |
| 🔴 Red | Redline violation (halt + traceback) |

---

## 5. Compliance Levels

| Level | Requirements |
|:---|:---|
| L0 Constitutional | DNA + 7-factor + GPG + pattern + credit + hexagram + tri-color |
| L1 Core | DNA + 7-factor + pattern + tri-color |
| L2 System | DNA + simplified audit (P/F/X/E) + tri-color |
| L3 Regional | DNA only |
| L4 User | DNA optional |

---

## 6. JSON Schema (Audit Record)

```json
{
  "dna": "#LongHun⚡️...",
  "audit": {
    "audit_version": "v1.0",
    "uid": "UID9622",
    "behavior_signature": { "P": "HasPromise", "F": "Fulfilled", "T": 0.0, "E": "Willing", "C": 0, "R": 0, "A": "Self", "X": "Genuine", "Y": "NoResponse", "Z": 1.0 },
    "behavior_pattern": "MODE-StableDisciplined",
    "behavior_labels": ["7F-P-有承诺", "7F-F-已兑现", "MODE-StableDisciplined"],
    "color": "🟢",
    "timestamp": "2026-07-24T13:00:00+08:00",
    "payload_hash": "a1b2c3d4e5f67890"
  },
  "payload": {},
  "meta": { "adapter_version": "1.0.0", "uid": "9622", "format": "longhun-v∞" }
}
```

---

## 7. Security Baseline

- Minimum: SHA-256 / AES-256 / RSA-4096
- Chinese crypto: SM2 / SM3 / SM4
- Forbidden: MD5 / SHA-1 / DES
- D1 (keys, seeds): Physical isolation, never networked
- D2 (user data): Client-side encryption, cloud stores ciphertext only
- No hardcoded keys in source
- No cross-border data without P77 security review

---

## 8. Reference: Python (lh_standard_adapter)

```python
from lh_standard_adapter import LongHunAdapter
adapter = LongHunAdapter(uid="9622", device="HM-9622-001")
result = adapter.wrap(data={"code": "print('hello')"}, task_type="code")
print(result["dna"])
validation = adapter.validate(result)
assert validation["valid"]
```

## 9. Reference: JavaScript

```javascript
function wrapPayload(data, { taskType = "default" } = {}) {
  const now = new Date();
  const stem = computeStemBranch(now);
  const hexagram = selectHexagram(taskType);
  const body = `ADAPTER-${taskType.toUpperCase()}-WRAP-V1.0`;
  const hash8 = sha256(`${stem}${hexagram}${body}`).slice(0, 8);
  return {
    dna: `#LongHun⚡️${stem}·${hexagram}-${body}-${hash8}`,
    audit: buildAudit(data),
    payload: data,
    meta: { adapter_version: "1.0.0", format: "longhun-v∞" }
  };
}
```

---

## Appendix: I Ching Hexagram Quick Reference

| Symbol | Name | Domain |
|:---|:---|:---|
| ䷀ | Qian | Governance |
| ䷁ | Kun | Archive |
| ䷜ | Kan | Engine |
| ䷝ | Li | Audit |
| ䷲ | Zhen | Security |
| ䷳ | Gen | Privacy |
| ䷸ | Xun | Deploy |
| ䷹ | Dui | Trust |
| ䷾ | JiJi | Complete |
| ䷿ | WeiJi | Progress |

---

**End of Protocol v1.0**

> DNA: `#LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-AI-TRACEABILITY-AUDIT-PROTOCOL-V1.0-85fd7a2b`
> Signed: LongHun Core · UID9622 · 龍芯北辰