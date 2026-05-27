# 🌊 iwmeiUI

**跨平台声明式 UI 组件库 — 一套 Token，四端一致**

[![License: AGPL v3](https://img.shields.io/badge/License-AGPL_v3-blue.svg)](https://www.gnu.org/licenses/agpl-3.0)
[![Platform](https://img.shields.io/badge/Platforms-Web%20%7C%20iOS%20%7C%20Android%20%7C%20Windows-brightgreen.svg)](#平台支持)

---

## 📖 简介

iwmeiUI 是一个**跨平台底层 UI 组件映射框架**，旨在通过统一的设计令牌（Design Tokens）体系，在 **Web (React)**、**Apple (SwiftUI)**、**Android (Jetpack Compose)** 和 **Windows (WinUI 3)** 四大平台上实现视觉与交互的高度一致性。

> **核心理念**：不做简单的 UI 封装搬运，而是从「原子层 → 语义层 → 组件层」由内向外构建，让颜色、排版、间距、圆角、阴影和动画在所有平台上保持统一的视觉语言。

---

## ✨ 特性

- 🎨 **统一设计令牌** — 基于 `design-tokens.json` 驱动所有平台的颜色、排版、间距和效果
- 🌗 **明暗主题原生支持** — Dark/Light 模式语义化切换，含毛玻璃、辉光阴影、景深动效
- 🧱 **7 大功能层级** — 布局、排版、操作、表单、反馈、导航、复合模块，覆盖现代 App 全场景
- 🔗 **跨平台一致映射** — 统一 API 定义精准对齐各平台最新声明式原生 API
- 📐 **原子化架构** — 基础原语 → 语义组件 → 复合页面，层层解耦，灵活可组合
- ♿ **可访问性优先** — 浅色模式自动修正对比度以通过 WCAG 标准

---

## 📁 项目结构

```
iwmeiUI/
├── design-tokens.json       # 🎯 统一设计令牌（原子色值、语义映射、布局参数）
├── plan.md                  # 📋 跨平台组件全字典（施工蓝图）
├── LICENSE                  # 📜 AGPL-3.0 开源许可
│
├── React/                   # 🌐 Web 平台实现 (React + TypeScript + Vite)
│   └── src/
│       ├── ui/
│       │   ├── primitives.tsx    # 基础原语组件 (Box, Text, Icon…)
│       │   └── components.tsx    # 高级组合组件 (Button, TextField…)
│       ├── tokens/               # Token 解析与绑定
│       ├── theme/                # 主题上下文与切换逻辑
│       ├── App.tsx               # 交互式展示 Demo
│       └── app.css               # Token 驱动的全局样式
│
├── SwiftUI/                 # 🍎 Apple 平台实现 (SwiftUI)
│   ├── Primitives.swift          # 基础原语
│   ├── Tokens.swift              # Token 体系
│   ├── Components.swift          # 组合组件
│   └── Demo.swift                # 交互式展示 Demo
│
└── Compose/                 # 🤖 Android 平台实现 (Jetpack Compose + Kotlin)
    ├── Primitives.kt             # 基础原语
    ├── Tokens.kt                 # Token 体系
    ├── Components.kt             # 组合组件
    ├── Demo.kt                   # 交互式展示 Demo
    └── build.gradle.kts          # Gradle 构建配置
```

---

## 🎨 设计令牌体系

设计令牌分为三层架构：

| 层级 | 说明 | 示例 |
|------|------|------|
| **原子层 (Primitives)** | 绝对物理数值，模式无关 | `teal.500: #0FF7E5`、`orange.500: #FF6D00` |
| **语义层 (Semantics)** | 根据主题动态切换引用 | `action_primary` → 暗色引用 `orange.500`，亮色引用 `orange.700` |
| **布局层 (Layout)** | 跨主题公用几何参数 | `padding.normal: 16px`、`radius.container: 15px` |

### 主色调

| 角色 | 暗色模式 | 亮色模式 |
|------|----------|----------|
| 🟢 主青色 (Teal) | `#0FF7E5` | `#C8FFF8` (包裹背景) |
| 🟠 行动色 (Orange) | `#FF6D00` | `#E65100` (WCAG 增强) |
| 🔵 基底色 (Navy) | `#0E1722` | `#FAFAFA` |

---

## 🧱 组件层级总览

<details>
<summary><b>🧱 1. 布局与容器层</b> — Box · HStack · VStack · ZStack · Grid · ScrollView · Spacer · Divider</summary>

架构核心：不带业务逻辑，只负责把东西放到正确的位置。
</details>

<details>
<summary><b>📝 2. 内容与排版层</b> — Text · Heading · Image · Icon · Avatar</summary>

严格绑定 `design-tokens.json` 中的 `typography` 字典，拒绝随意输入字号。
</details>

<details>
<summary><b>🕹️ 3. 操作与触发层</b> — Button · IconButton · FAB · Menu</summary>

处理用户的「点击」意图，实现交互态（Hover/Press）和弥散光效果。
</details>

<details>
<summary><b>🎛️ 4. 表单与录入层</b> — TextField · TextArea · Toggle · Checkbox · RadioGroup · Slider · Segmented</summary>

数据双向绑定核心，统一对外暴露 `onChange` / `onValueChange`。
</details>

<details>
<summary><b>🚨 5. 状态反馈层</b> — Spinner · ProgressBar · Badge · Toast · Dialog · Skeleton</summary>

系统与用户对话的媒介，极度依赖各平台原生动画能力。
</details>

<details>
<summary><b>🧭 6. 导航与结构层</b> — TopAppBar · BottomBar · Tabs · Drawer</summary>

App 的骨架，通常是最外层组件。
</details>

<details>
<summary><b>📦 7. 复合模块展示</b> — List · Card · Chip/Tag · Accordion</summary>

基于基础原子封装，重点处理大数据量的长列表性能优化。
</details>

---

## 🚀 快速开始

### React (Web)

```bash
# 克隆仓库
git clone https://github.com/yourpapayouknow/iwmeiUI.git
cd iwmeiUI/React

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

访问 `http://localhost:5173` 查看交互式组件展示。

### SwiftUI (Apple)

将 `SwiftUI/` 目录下的 Swift 文件拖入 Xcode 项目即可使用：

```
SwiftUI/
├── Primitives.swift    # 导入基础原语
├── Tokens.swift        # 导入令牌体系
├── Components.swift    # 导入组合组件
└── Demo.swift          # 可选：导入展示 Demo
```

### Jetpack Compose (Android)

将 `Compose/` 目录下的 Kotlin 文件添加到 Android 项目中：

```bash
cd iwmeiUI/Compose

# 使用 Gradle 构建
./gradlew build
```

---

## 🏗️ 实施路径

遵循「由内向外」的渐进式构建策略：

```
阶段一 → 阶段二 → 阶段三 → 阶段四
打地基     造积木     搭骨架     拼界面
```

| 阶段 | 目标 | 核心组件 |
|------|------|----------|
| **一、打地基** | Token 体系 + 颜色排版引擎 | `Box` · `Text` · `Icon` |
| **二、造积木** | 攻克交互态跨平台一致性 | `Button` · `TextField` |
| **三、搭骨架** | 间距引擎 + 列表性能 | `VStack` · `HStack` · `List` |
| **四、拼界面** | 高级复合组件 | `BottomBar` · `TopAppBar` · `Card` |

---

## 📋 待办事项

### 平台实现

- [x] 🌐 **React (Web)** — 基础原语 + 组合组件 + Token 驱动样式 + 交互 Demo
- [x] 🍎 **SwiftUI (Apple)** — 基础原语 + Token 体系 + 组合组件 + 交互 Demo
- [x] 🤖 **Jetpack Compose (Android)** — 基础原语 + Token 体系 + 组合组件 + 交互 Demo
- [ ] 🪟 **WinUI 3 (Windows)** — 尚未开始

### 核心功能

- [x] 设计令牌 JSON 规范定义 (`design-tokens.json`)
- [x] 跨平台组件全字典编写 (`plan.md`)
- [x] 原子层 → 语义层 → 布局层三级令牌架构
- [x] 明暗主题切换支持
- [ ] Token 热更新 / 运行时主题切换优化
- [ ] 自定义主题扩展 API

### 组件完善

- [x] 基础原语：`Box` · `Text` · `HStack` · `VStack` · `Icon` · `Spacer` · `Divider`
- [x] 操作组件：`Button` · `IconButton` · `FAB`
- [x] 表单组件：`TextField` · `TextArea` · `Toggle` · `Checkbox` · `Slider`
- [x] 反馈组件：`Spinner` · `ProgressBar` · `Badge` · `Toast` · `Dialog`
- [x] 导航组件：`TopAppBar` · `BottomBar` · `Tabs`
- [x] 数据展示：`Card` · `Chip` · `Accordion` · `List`
- [ ] 高级组件：`DatePicker` · `TimePicker` · `ColorPicker`
- [ ] 复杂手势：拖拽排序 · 滑动删除 · 长按菜单

### 工程化

- [ ] 各平台独立的包管理器发布 (npm / SwiftPM / Maven Central / NuGet)
- [ ] 完整的单元测试覆盖
- [ ] 可视化 Storybook / Catalog 组件文档
- [ ] CI/CD 自动化构建与发布流水线
- [ ] 跨平台视觉回归测试
- [ ] 无障碍 (Accessibility) 合规性自动测试

### 文档

- [x] README 文档（本文件）
- [ ] 贡献者指南 (`CONTRIBUTING.md`)
- [ ] 变更日志 (`CHANGELOG.md`)
- [ ] 各组件的详细 API 文档
- [ ] 跨平台迁移指南
- [ ] 设计令牌最佳实践指南

---

## 🤝 参与贡献

欢迎提交 Issue 和 Pull Request！在提交之前，请确保：

1. 所有组件变更遵循 `design-tokens.json` 中的令牌规范
2. 新增组件需同步更新 `plan.md` 中的跨平台映射表
3. 代码风格与现有实现保持一致

---

## 📄 许可证

本项目采用 [GNU Affero General Public License v3.0 (AGPL-3.0)](LICENSE) 许可证。

---

<p align="center">
  <sub>用 ❤️ 构建统一的跨平台 UI 体验</sub>
</p>
