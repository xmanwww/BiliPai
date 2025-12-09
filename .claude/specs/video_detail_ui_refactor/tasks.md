# 视频详情页 UI 重构 - 实施计划

## 任务清单

### 阶段 1：基础设施

- [ ] **1.1 添加 UI 库依赖**
  - 在 `build.gradle.kts` 中添加 Haze、Shimmer、Lottie 依赖
  - 执行 Gradle Sync 验证依赖可用
  - 参考需求: AC 1.1

- [ ] **1.2 创建骨架屏加载组件**
  - 创建 `SkeletonComponents.kt` 文件
  - 实现 `ShimmerBox` 基础组件
  - 实现 `VideoDetailSkeleton` 组合骨架屏
  - 参考需求: AC 1.1, AC 1.2, AC 1.3

- [ ] **1.3 集成骨架屏到 VideoDetailScreen**
  - 在 Loading 状态时显示 `VideoDetailSkeleton`
  - 添加 Loading → Success 过渡动画
  - 参考需求: AC 1.1

---

### 阶段 2：毛玻璃效果

- [ ] **2.1 创建 GlassCard 组件**
  - 创建使用 Haze 的毛玻璃卡片组件
  - 支持深色/浅色模式适配
  - 参考需求: AC 2.1

- [ ] **2.2 应用毛玻璃到播放器控制栏**
  - 重构 `VideoControlOverlay` 使用毛玻璃背景
  - 优化进度条样式为圆润胶囊形
  - 参考需求: AC 2.1, AC 2.2

- [ ] **2.3 视频时长标签毛玻璃效果**
  - 更新 `RelatedVideoItem` 时长标签样式
  - 应用毛玻璃背景
  - 参考需求: AC 4.3

---

### 阶段 3：iOS 风格按钮

- [ ] **3.1 创建 IOSActionButton 组件**
  - 实现弹性按压动效 (scale 0.9 → 1.0)
  - iOS 风格图标和颜色
  - 参考需求: AC 3.4

- [ ] **3.2 重构 ActionButtonsRow**
  - 使用新的 `IOSActionButton` 替换现有按钮
  - 更新颜色为 iOS 风格 (#FF2D55, #FFD60A, #FF9500)
  - 参考需求: AC 3.1, AC 3.2, AC 3.3

---

### 阶段 4：Lottie 动画（可选）

- [ ] **4.1 添加 Lottie 动画资源**
  - 从 LottieFiles 下载点赞/投币动画 JSON
  - 放置到 `res/raw/` 目录
  - 参考需求: AC 3.1, AC 3.2

- [ ] **4.2 集成点赞 Lottie 动画**
  - 在点赞成功时播放爆裂动画
  - 动画覆盖在按钮上方
  - 参考需求: AC 3.1

- [ ] **4.3 集成三连成功动画**
  - 在三连全部成功时播放庆祝动画
  - 居中显示全屏动画
  - 参考需求: AC 1.4

---

### 阶段 5：视频卡片优化

- [ ] **5.1 优化 RelatedVideoItem 样式**
  - 增加阴影和圆角 (12dp)
  - 封面加载时显示 Shimmer 占位
  - 参考需求: AC 4.1, AC 4.2

- [ ] **5.2 添加卡片交互动效**
  - 按压时 elevation 变化
  - 缩放微动效
  - 参考需求: AC 4.4

---

### 阶段 6：验证

- [ ] **6.1 编译验证**
  - 执行 `./gradlew assembleDebug`
  - 确保所有改动编译通过
  - 参考需求: 所有

- [ ] **6.2 性能验证**
  - 检查骨架屏和毛玻璃动画流畅度
  - 确保无明显卡顿
  - 参考需求: 成功标准 4
