# 贡献指南

感谢你有兴趣为本项目做出贡献！我们欢迎所有形式的贡献，包括 Bug 报告、功能建议、文档改进和代码提交。

## 行为准则

参与本项目即表示你同意共同维护一个友好、包容的社区环境。

## 如何贡献

### 报告 Bug

1. 先在 [Issues](../../issues) 中搜索是否已有相同问题
2. 使用 Bug 报告模板提交新 Issue
3. 清晰描述问题的复现步骤和环境信息

### 提交功能建议

1. 在 [Issues](../../issues) 中搜索是否已有类似建议
2. 使用功能请求模板详细描述你的想法
3. 说明该功能的应用场景和预期收益

### 提交代码

1. Fork 本仓库并创建你的分支：
   ```bash
   git checkout -b feature/your-feature-name
   # 或
   git checkout -b fix/bug-description
   ```
2. 遵循项目代码风格（见 `pyproject.toml`）
3. 编写或更新测试用例，确保所有测试通过
4. 更新 `CHANGELOG.md` 中的 `[Unreleased]` 部分
5. 提交 Pull Request

### 提交规范（Commit Convention）

本项目遵循 [Conventional Commits](https://www.conventionalcommits.org/zh-hans/) 规范：

| 类型 | 说明 |
|---|---|
| `feat` | 新功能 |
| `fix` | 修复 Bug |
| `docs` | 文档变更 |
| `style` | 代码格式（不影响逻辑）|
| `refactor` | 重构 |
| `test` | 测试相关 |
| `chore` | 构建/依赖维护 |

**示例：**
```
feat(adapter): 新增 OpenAI 适配器支持
fix(core): 修复适配器链接断开的问题 (#123)
docs: 更新 README 快速开始示例
```

## 开发环境搭建

```bash
# 克隆仓库
git clone https://github.com/UID9622/lh-standard-adapter.git
cd lh-standard-adapter

# 安装依赖
pip install -e ".[dev]"

# 运行测试
pytest
```

## 代码审查流程

- 所有 PR 至少需要 1 位维护者审查通过
- CI 检查（lint、test、type check）必须全部通过
- 请保持每个 PR 聚焦于单一目标，方便审查
- PR 标题应遵循 Conventional Commits 格式

## 联系方式

- 项目维护者：[@UID9622](https://github.com/UID9622)
- 邮箱：longhun2025@petalmail.com
- 标识：`#龍芯⚡️UID9622`

---

# Contributing Guide

Thank you for your interest in contributing to this project! We welcome all forms of contributions.

## Code of Conduct

By participating in this project, you agree to maintain a friendly and inclusive community environment.

## How to Contribute

### Reporting Bugs

1. Search [existing issues](../../issues) first to avoid duplicates
2. Use the Bug Report template to submit a new issue
3. Clearly describe reproduction steps and environment information

### Suggesting Features

1. Search [existing issues](../../issues) for similar suggestions
2. Use the Feature Request template to describe your idea
3. Explain use cases and expected benefits

### Submitting Code

1. Fork the repository and create your branch:
   ```bash
   git checkout -b feature/your-feature-name
   # or
   git checkout -b fix/bug-description
   ```
2. Follow the project code style (see `pyproject.toml`)
3. Write or update test cases, ensure all tests pass
4. Update the `[Unreleased]` section in `CHANGELOG.md`
5. Submit a Pull Request

### Commit Convention

This project follows [Conventional Commits](https://www.conventionalcommits.org/en/):

| Type | Description |
|---|---|
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation changes |
| `style` | Code formatting |
| `refactor` | Code refactoring |
| `test` | Test-related changes |
| `chore` | Build/dependency maintenance |

## Development Setup

```bash
git clone https://github.com/UID9622/lh-standard-adapter.git
cd lh-standard-adapter
pip install -e ".[dev]"
pytest
```

## Contact

- Project Maintainer: [@UID9622](https://github.com/UID9622)
- Email: longhun2025@petalmail.com
- Identity: `#龍芯⚡️UID9622`

---

## Bounty 政策（2026-09-05 · UID9622 焊死）

- 本项目所有标记 `bounty` 的 Issue 均为**社区署名贡献任务（Community Credit）**，不附带任何现金、代币或实物报酬。
- 合并 PR 的贡献者将获得：贡献者名单署名 / 鸣谢铭碑记录 / 社区荣誉。
- 任何现金报酬索求、第三方赏金平台（Algora / IssueHunt / Gitcoin）接入请求均不予受理。
- 提交 PR 即视为已知悉并同意以上条款。

## Bounty Policy (welded 2026-09-05 · UID9622)

- Every issue labeled `bounty` in this repo is a **Community Credit task**. No cash, token, or in-kind reward is offered or implied.
- Contributors whose PRs are merged receive: a listing in the contributor memorial / community credit.
- Payment requests and third-party bounty-platform integrations (Algora / IssueHunt / Gitcoin) will not be entertained.
- Submitting a PR implies acceptance of this policy.
