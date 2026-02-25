# 发布流程（维护版）

最后更新：2026-02-25（适配 v6.3.0）

## 目标

统一版本发布动作，避免出现“代码已发版但 README/Wiki 未同步”的情况。

## 标准步骤

1. 更新版本号  
   - 文件：`app/build.gradle.kts`
   - 规则：`versionCode + 1`，`versionName` 递增

2. 更新发布日志  
   - 文件：`CHANGELOG.md`
   - 要求：新增本版本段落，记录核心能力、修复点、验证结果

3. 同步 README  
   - 文件：`README.md`、`README_EN.md`
   - 要求：同步顶部版本、Latest、Roadmap（尤其“已完成功能”）

4. 同步 Wiki  
   - 文件：`docs/wiki/FEATURE_MATRIX.md`
   - 要求：更新已完成/开发中/计划中状态

5. 最低验证  
   - 至少执行与本次改动相关的单测或构建命令
   - 至少执行一次 QA 基础检查清单（见 `docs/wiki/QA.md`）
   - 推荐：`./gradlew :app:testDebugUnitTest`

6. 提交与推送  
   - 建议拆分为：
     - `chore(release): bump version to x.y.z`
     - `docs(readme): sync release notes`
     - `docs(wiki): sync feature matrix`

## 发布检查清单

- [ ] `app/build.gradle.kts` 版本号正确
- [ ] `CHANGELOG.md` 新版本段存在
- [ ] `README.md` 已同步最新版本与已完成功能
- [ ] `README_EN.md` 已同步最新版本与 Latest
- [ ] `docs/wiki/FEATURE_MATRIX.md` 已同步
- [ ] 必要测试已执行并记录结果
