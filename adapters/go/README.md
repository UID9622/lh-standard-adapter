# LongHun Standard Adapter — Go

[English](#english) | [中文](#chinese)

---

<a id="english"></a>
## English

> DNA: `#LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-ADAPTER-v1.0.0-4f7a3b1c`  
> Author: LongHun Core · UID9622 · 龍芯北辰  
> License: CC BY-NC-SA 4.0  

**Open the standard. Guard the engine.**

### Overview

This is the Go implementation of the LongHun Standard Adapter. It wraps JSON payloads with DNA traceability and seven-factor behavioral audit metadata, byte-for-byte compatible with the Python reference implementation.

### Features

- **DNA Traceability** — GanZhi stem-branch generation with SHA-256 hash
- **Seven-Factor Audit** — Promise, Fulfill, Time, Emotion, Cost, Repeat, Audience, Explain, Yield, Zigzag
- **Behavior Classification** — 5 classification modes (StableDisciplined, DefensiveDefaulter, ExternalTrustSpender, InternalDestroyer, Fluctuating)
- **Tri-Color Audit** — 🟢 Green / 🟡 Yellow / 🔴 Red
- **Validation** — Full DNA pattern matching, signature validation, UID cross-check
- **Zero Dependencies** — Standard library only (`crypto/sha256`, `encoding/json`, `regexp`, `time`)

### Installation

```bash
go get github.com/uid9622/lh-standard-adapter/adapters/go
```

### Quick Start

```go
package main

import (
    "fmt"
    lh "github.com/uid9622/lh-standard-adapter/adapters/go"
)

func main() {
    adapter := lh.New("9622", "HM-9622-001", "Asia/Shanghai")

    // Wrap a payload
    result := adapter.Wrap(
        map[string]interface{}{"code": "print('hello')"},
        "code", "P04", "WRAP", "",
    )

    fmt.Println("DNA:", result["dna"])

    // Validate
    validation := adapter.Validate(result)
    fmt.Println("Valid:", validation["valid"])
}
```

### API

| Method | Description |
|--------|-------------|
| `New(uid, device, locale)` | Create a new adapter |
| `Default()` | Create with UID9622 defaults |
| `Wrap(data, taskType, persona, action, version)` | Wrap payload with DNA + audit |
| `Validate(wrapped)` | Validate a wrapped record |
| `GetSchemas()` | Return DNA and audit JSON schemas |

### Testing

```bash
cd adapters/go
go test ./...
```

### Compliance Level

This adapter implements **L0 Constitutional**: DNA + 7-factor + GPG + pattern + credit + hexagram + tri-color.

---

<a id="chinese"></a>
## 中文

> DNA: `#LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-ADAPTER-v1.0.0-4f7a3b1c`  
> 作者: 龙魂核心 · UID9622 · 龍芯北辰  
> 许可: CC BY-NC-SA 4.0  

**开放标准。守护引擎。**

### 概述

龙魂标准适配器的 Go 语言实现。为 JSON 载荷添加 DNA 溯源和七因素行为审计元数据，与 Python 参考实现字节级兼容。

### 功能

- **DNA 溯源** — 干支茎支生成 + SHA-256 哈希
- **七因素审计** — 承诺/兑现/时间/情感/代价/重复/受众/解释/改变/波幅
- **行为分类** — 5 种模式（稳定自律/防御型失信/外部信任消耗/内部毁灭/波动）
- **三色审计** — 🟢 绿 / 🟡 黄 / 🔴 红
- **验证** — 完整 DNA 格式匹配、签名验证、UID 交叉校验
- **零依赖** — 仅使用标准库

### 安装

```bash
go get github.com/uid9622/lh-standard-adapter/adapters/go
```

### 快速开始

```go
adapter := lh.New("9622", "HM-9622-001", "Asia/Shanghai")

result := adapter.Wrap(
    map[string]interface{}{"code": "print('hello')"},
    "code", "P04", "WRAP", "",
)

fmt.Println("DNA:", result["dna"])
```

### 测试

```bash
cd adapters/go
go test ./...
```

### 合规级别

本适配器实现 **L0 宪法级**：DNA + 七因素 + GPG + 模式 + 署名 + 卦象 + 三色。
