# 视频详情页 UI 重构 - 需求文档

## 概述

对视频详情页进行全面 UI 重构，采用 **iOS/Cupertino 风格** 的精美开源组件库，打造类似 iPhone App 的高颜值界面。

---

## 🍎 推荐 iOS 风格组件库

### 核心库：Compose Cupertino

| 模块 | 用途 | 特点 |
|------|------|------|
| **compose-cupertino** | iOS 风格组件 | 模拟 SwiftUI 原生组件外观 |
| **cupertino-icons-extended** | SF Symbols 图标 | 800+ Apple 风格图标 |

> GitHub: [alexzhirkevich/compose-cupertino](https://github.com/alexzhirkevich/compose-cupertino)

### 毛玻璃/磨砂效果

| 库名 | 用途 | 特点 |
|------|------|------|
| **Haze** (Chris Banes) | 毛玻璃背景 | 专业级模糊效果 |
| **glassmorphic-composables** | iPhone 风格模糊 | 简化 iOS 模糊实现 |
| **Modifier.blur()** | 内置模糊 | Android 12+ 原生支持 |

### 动画库

| 库名 | 用途 | 特点 |
|------|------|------|
| **lottie-compose** | Lottie 动画 | After Effects 动画渲染 |
| **compose-shimmer** | 骨架屏加载 | 闪光占位效果 |

---

## 🎨 iOS 设计语言特点

## 需求列表

### 1. 加载状态优化

**用户故事**：作为用户，我希望在内容加载时看到优雅的动画效果，而不是单调的转圈。

**验收标准**：

- **AC 1.1**：视频详情页加载时，系统**应该**显示骨架屏 (Skeleton) 效果
- **AC 1.2**：骨架屏**应该**包含视频封面、标题、UP主头像的占位区域
- **AC 1.3**：骨架屏**应该**有 Shimmer 闪光动画效果
- **AC 1.4**：操作成功时（如三连），系统**可以**播放 Lottie 庆祝动画

---

### 2. 视频播放器控制栏优化

**用户故事**：作为用户，我希望视频控制栏更加美观和易用。

**验收标准**：

- **AC 2.1**：控制栏**应该**使用磨砂玻璃背景效果
- **AC 2.2**：进度条**应该**使用圆润的胶囊形状
- **AC 2.3**：按钮点击**应该**有微动效反馈 (scale/ripple)
- **AC 2.4**：音量/亮度手势滑动**应该**显示精美的指示器

---

### 3. 操作按钮行重设计

**用户故事**：作为用户，我希望点赞、投币、收藏等按钮看起来更加精美和有互动感。

**验收标准**：

- **AC 3.1**：点赞按钮点击时**应该**有心形爆裂动画
- **AC 3.2**：投币按钮**应该**有金币下落动画
- **AC 3.3**：三连按钮**应该**有特殊的彩虹渐变效果
- **AC 3.4**：所有按钮**应该**有弹性按压效果

---

### 4. 视频卡片样式优化

**用户故事**：作为用户，我希望推荐视频的卡片看起来更加精致。

**验收标准**：

- **AC 4.1**：视频封面**应该**有微妙的阴影和圆角
- **AC 4.2**：封面加载时**应该**显示 Shimmer 占位动画
- **AC 4.3**：时长标签**应该**使用毛玻璃效果背景
- **AC 4.4**：卡片悬浮/按压**应该**有 elevation 变化动效

---

### 5. UP 主信息区优化

**用户故事**：作为用户，我希望 UP 主信息区域更加突出和美观。

**验收标准**：

- **AC 5.1**：头像**应该**有渐变边框或光晕效果
- **AC 5.2**：关注按钮**应该**有状态切换动画
- **AC 5.3**：大会员/认证标识**应该**有特殊图标样式

---

## 技术方案

### 推荐依赖

```kotlin
// build.gradle.kts
dependencies {
    // Lottie 动画
    implementation("com.airbnb.android:lottie-compose:6.3.0")
    
    // Shimmer 效果
    implementation("com.valentinilk.shimmer:compose-shimmer:1.2.0")
    
    // Accompanist (系统 UI)
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")
}
```

### 实施优先级

1. **P0 - 骨架屏加载** (shimmer + skeleton)
2. **P1 - 按钮动效** (Lottie 点赞/投币动画)
3. **P2 - 控制栏磨砂效果**
4. **P3 - 卡片阴影和动效**

---

## 成功标准

1. 页面加载感知速度提升（骨架屏让等待不焦虑）
2. 用户操作有即时视觉反馈
3. 整体 UI 达到"高颜值"标准
4. 动画流畅，不影响性能 (60fps)
