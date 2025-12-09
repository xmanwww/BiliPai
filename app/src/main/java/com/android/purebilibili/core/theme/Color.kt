package com.android.purebilibili.core.theme

import androidx.compose.ui.graphics.Color

// --- B站核心品牌色 ---
val BiliPink = Color(0xFFFA7298)
val BiliPinkDim = Color(0xFFE6688C) // 按压态
val BiliPinkLight = Color(0xFFFFEBF0) // 浅粉色背景 (用于高亮区域)

// --- 背景色 ---
val BiliBackground = Color(0xFFF1F2F3) // 经典淡灰背景 (APP底色)
val SurfaceCard = Color(0xFFFFFFFF)    // 卡片背景 (纯白)

// --- 文字颜色 ---
val TextPrimary = Color(0xFF18191C)   // 主要文字 (接近黑)
val TextSecondary = Color(0xFF61666D) // 次要文字 (深灰)
val TextTertiary = Color(0xFF9499A0)  // 辅助文字 (浅灰)

// --- 基础色 ---
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)

// --- 深色模式适配 (优化) ---
val DarkBackground = Color(0xFF0D0D0D)     // 更深的背景，减少眼睛疲劳
val DarkSurface = Color(0xFF1A1A1A)        // 卡片/表面颜色
val DarkSurfaceVariant = Color(0xFF262626) // 次级表面 (分隔区域)
val DarkSurfaceElevated = Color(0xFF2D2D2D) // 抬高的表面 (弹窗、悬浮)
val BiliPinkDark = Color(0xFFFF85A2)       // 深色模式下更亮的粉色
val TextPrimaryDark = Color(0xFFE8E8E8)    // 主要文字 (柔和白)
val TextSecondaryDark = Color(0xFFB0B0B0)  // 次要文字 (中灰)
val TextTertiaryDark = Color(0xFF707070)   // 辅助文字 (深灰)

// --- 操作按钮专用色 (深色模式优化) ---
val ActionLikeDark = Color(0xFFFF85A2)     // 点赞 - 亮粉
val ActionCoinDark = Color(0xFFFFCA28)     // 投币 - 亮金
val ActionFavoriteDark = Color(0xFFFFD54F) // 收藏 - 亮黄
val ActionShareDark = Color(0xFF64B5F6)    // 分享 - 亮蓝
val ActionCommentDark = Color(0xFF4DD0E1)  // 评论 - 亮青

// 🍎 --- iOS 风格色板 ---
val iOSPink = Color(0xFFFF2D55)      // iOS 系统粉色 (点赞)
val iOSYellow = Color(0xFFFFD60A)    // iOS 系统黄色 (投币)
val iOSOrange = Color(0xFFFF9500)    // iOS 系统橙色 (收藏)
val iOSBlue = Color(0xFF007AFF)      // iOS 系统蓝色
val iOSGreen = Color(0xFF34C759)     // iOS 系统绿色
val iOSTeal = Color(0xFF5AC8FA)      // iOS 系统青色 (评论)
val iOSPurple = Color(0xFFAF52DE)    // iOS 系统紫色 (三连)

// 🔥🔥 [新增] --- 预设主题色 (用于自定义主题) ---
val ThemeColors = listOf(
    Color(0xFFFA7298),  // 0: 粉色 (默认 BiliPink)
    Color(0xFF00A1D6),  // 1: 蓝色 (Bilibili Blue)
    Color(0xFF4CAF50),  // 2: 绿色 (Material Green)
    Color(0xFF9C27B0),  // 3: 紫色 (Material Purple)
    Color(0xFFFF5722),  // 4: 橙色 (Material Deep Orange)
    Color(0xFF607D8B),  // 5: 蓝灰色 (Material Blue Grey)
)

val ThemeColorNames = listOf("粉色", "蓝色", "绿色", "紫色", "橙色", "蓝灰")