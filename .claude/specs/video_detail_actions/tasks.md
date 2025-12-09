# 视频详情页功能增强 - 实施计划

## 任务清单

### 1. API 层扩展

- [ ] **1.1 添加点赞相关 API 接口**
  - 在 `ApiClient.kt` 添加 `likeVideo()` POST 接口
  - 在 `ApiClient.kt` 添加 `hasLiked()` GET 接口
  - 在 `ListModels.kt` 添加 `HasLikedResponse` 数据类
  - 参考需求: AC 2.1, AC 2.6

- [ ] **1.2 添加投币相关 API 接口**
  - 在 `ApiClient.kt` 添加 `coinVideo()` POST 接口
  - 在 `ApiClient.kt` 添加 `hasCoined()` GET 接口
  - 在 `ListModels.kt` 添加 `HasCoinedResponse` 和 `CoinData` 数据类
  - 参考需求: AC 3.1, AC 3.5

---

### 2. Repository 层扩展

- [ ] **2.1 实现点赞功能方法**
  - 在 `ActionRepository` 添加 `likeVideo(aid, like)` 方法
  - 在 `ActionRepository` 添加 `checkLikeStatus(aid)` 方法
  - 包含 CSRF Token 检查和错误处理
  - 参考需求: AC 2.2, AC 2.3, AC 2.4, AC 2.5

- [ ] **2.2 实现投币功能方法**
  - 在 `ActionRepository` 添加 `coinVideo(aid, count, alsoLike)` 方法
  - 在 `ActionRepository` 添加 `checkCoinStatus(aid)` 方法
  - 处理错误码 34004/34005
  - 参考需求: AC 3.3, AC 3.4, AC 3.5, AC 3.7

- [ ] **2.3 实现一键三连方法**
  - 在 `ActionRepository` 添加 `tripleAction(aid)` 方法
  - 定义 `TripleResult` 数据类
  - 实现顺序执行：点赞 → 投币 → 收藏
  - 处理部分失败场景
  - 参考需求: AC 4.2, AC 4.5

---

### 3. ViewModel 层扩展

- [ ] **3.1 扩展 PlayerUiState.Success 状态**
  - 添加 `isLiked: Boolean` 字段
  - 添加 `coinCount: Int` 字段 (0/1/2)
  - 参考需求: AC 2.6, AC 3.5

- [ ] **3.2 实现视频加载时状态检查**
  - 在 `loadVideo()` 中添加 `checkLikeStatus()` 调用
  - 在 `loadVideo()` 中添加 `checkCoinStatus()` 调用
  - 将结果填充到 `PlayerUiState.Success`
  - 参考需求: AC 2.6, AC 4.6

- [ ] **3.3 实现点赞切换方法**
  - 在 `PlayerViewModel` 添加 `toggleLike()` 方法
  - 更新 `isLiked` 状态并发送 toast 事件
  - 参考需求: AC 2.2, AC 2.3

- [ ] **3.4 实现投币相关方法**
  - 添加 `coinDialogVisible` StateFlow
  - 添加 `openCoinDialog()` 方法
  - 添加 `doCoin(count, alsoLike)` 方法
  - 参考需求: AC 3.2, AC 3.4

- [ ] **3.5 实现一键三连方法**
  - 在 `PlayerViewModel` 添加 `doTripleAction()` 方法
  - 执行三连并更新所有相关状态
  - 根据结果生成反馈消息
  - 参考需求: AC 4.2, AC 4.4, AC 4.5

---

### 4. UI 组件开发

- [ ] **4.1 更新 ActionButtonsRow 点赞按钮**
  - 接收 `isLiked` 状态参数
  - 绑定 `onLikeClick` 回调
  - 根据状态显示填充/描边图标
  - 参考需求: AC 2.2

- [ ] **4.2 更新 ActionButtonsRow 投币按钮**
  - 接收 `coinCount` 状态参数
  - 绑定 `onCoinClick` 回调
  - 显示已投币数量或"投币"文字
  - 参考需求: AC 3.5

- [ ] **4.3 添加三连按钮**
  - 在 ActionButtonsRow 添加三连按钮
  - 使用合适的图标 (可用 Favorite + 动画)
  - 绑定 `onTripleClick` 回调
  - 参考需求: AC 4.1

- [ ] **4.4 创建 CoinDialog 组件**
  - 创建投币选择对话框 Composable
  - 显示投 1 币/2 币选项
  - 显示"同时点赞"复选框
  - 根据已投币数禁用选项
  - 参考需求: AC 3.2, AC 3.3, AC 3.5

- [ ] **4.5 集成 CoinDialog 到 VideoDetailScreen**
  - 在 VideoDetailScreen 添加 CoinDialog
  - 监听 `coinDialogVisible` 状态
  - 连接 `doCoin()` 回调
  - 参考需求: AC 3.2

---

### 5. 集成与连接

- [ ] **5.1 更新 VideoDetailScreen 传递新状态和回调**
  - 从 `PlayerUiState.Success` 提取 `isLiked`, `coinCount`
  - 传递 `onLikeClick`, `onCoinClick`, `onTripleClick` 回调
  - 连接到 ViewModel 方法
  - 参考需求: AC 2.1, AC 3.1, AC 4.1

- [ ] **5.2 更新 VideoContentSection 组件签名**
  - 添加新状态和回调参数
  - 传递给 ActionButtonsRow
  - 参考需求: 所有 UI 相关需求

---

### 6. 测试验证

- [ ] **6.1 编写 ActionRepository 单元测试**
  - 测试 `likeVideo()` 成功和失败场景
  - 测试 `coinVideo()` 成功和错误码处理
  - 测试 `tripleAction()` 部分失败场景
  - 参考需求: AC 2.5, AC 3.7, AC 4.5

- [ ] **6.2 编译验证**
  - 执行 `./gradlew assembleDebug` 确保编译通过
  - 参考需求: 所有需求
