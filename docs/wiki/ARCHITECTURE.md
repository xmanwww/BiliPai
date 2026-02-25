# 架构说明

最后更新：2026-02-25（v6.3.0）

## 代码分层

主路径：`app/src/main/java/com/android/purebilibili`

- `app/`：Application、Activity、启动流程
- `core/`：跨业务公共能力（network、store、cache、ui、player）
- `data/`：数据模型与仓库（Repository）
- `domain/`：用例层（UseCase）
- `feature/`：按业务场景拆分的 UI 与交互
- `navigation/`：路由定义与页面导航编排

## 播放主链路（简版）

1. `feature/video` 发起播放请求  
2. `usecase` 组装播放策略与状态  
3. `data/repository` 拉取详情与播放地址  
4. `core/network` 发起 API 请求并处理鉴权  
5. 回传 `ViewInfo + PlayUrlData` 到 ViewModel 驱动 UI/Player

## 鉴权与画质策略关键点

- 登录态由 `SESSDATA` 与 `access_token` 共同判定。
- 首播默认画质由用户配置、登录态、VIP 状态共同决定。
- 无 Cookie 但有 token 的场景允许尝试 APP API 路径，降低误降级到 720P 的概率。

## 文档与发布文件

- 版本与构建：`app/build.gradle.kts`
- 更新日志：`CHANGELOG.md`
- 产品文档入口：`README.md` / `README_EN.md`
