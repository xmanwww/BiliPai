package com.android.purebilibili.feature.video

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.purebilibili.core.theme.BiliPink
import com.android.purebilibili.core.util.FormatUtils
import com.android.purebilibili.core.util.bouncyClickable
import com.android.purebilibili.data.model.response.RelatedVideo
import com.android.purebilibili.data.model.response.ViewInfo
import androidx.compose.foundation.isSystemInDarkTheme
import com.android.purebilibili.core.theme.ActionLikeDark
import com.android.purebilibili.core.theme.ActionCoinDark
import com.android.purebilibili.core.theme.ActionFavoriteDark
import com.android.purebilibili.core.theme.ActionShareDark
import com.android.purebilibili.core.theme.ActionCommentDark

// 🔥 1. 视频头部信息（优化布局和样式）
@Composable
fun VideoHeaderSection(
    info: ViewInfo,
    isFollowing: Boolean = false,
    onFollowClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // UP主信息行 - 简洁布局（去除多余背景）
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
                // 头像
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(FormatUtils.fixImageUrl(info.owner.face))
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // UP主名称
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = info.owner.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "UP主",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }

                // 🔥 关注按钮（支持状态切换）
                Surface(
                    onClick = onFollowClick,
                    color = if (isFollowing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        if (!isFollowing) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = if (isFollowing) "已关注" else "关注",
                            fontSize = 14.sp,
                            color = if (isFollowing) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                            fontWeight = FontWeight.Medium
                        )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 标题（可展开）
        var expanded by remember { mutableStateOf(false) }
        Text(
            text = info.title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium
            ),
            maxLines = if (expanded) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .animateContentSize()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 🔥 新增: 分区标签 + 发布时间
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 分区标签
            if (info.tname.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = info.tname,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            // 发布时间
            if (info.pubdate > 0) {
                Text(
                    text = FormatUtils.formatPublishTime(info.pubdate),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 数据统计行（优化图标和间距）
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 播放量
            Icon(
                Icons.Outlined.PlayCircle,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = FormatUtils.formatStat(info.stat.view.toLong()),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 弹幕数
            Icon(
                Icons.Outlined.Subject,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = FormatUtils.formatStat(info.stat.danmaku.toLong()),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // BV号
            Text(
                text = info.bvid,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

// 🔥 2. 操作按钮行（优化布局和视觉效果）
@Composable
fun ActionButtonsRow(
    info: ViewInfo,
    isFavorited: Boolean = false,
    isLiked: Boolean = false,
    coinCount: Int = 0,
    onFavoriteClick: () -> Unit = {},
    onLikeClick: () -> Unit = {},
    onCoinClick: () -> Unit = {},
    onTripleClick: () -> Unit = {},
    onCommentClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
            // 🔥 点赞 - 粉色（支持状态切换）
            ActionButton(
                icon = if (isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                text = FormatUtils.formatStat(info.stat.like.toLong()),
                iconColor = if (isDark) ActionLikeDark else BiliPink,
                iconSize = 26.dp,
                isActive = isLiked,
                onClick = onLikeClick
            )

            // 🔥 投币 - 金色（支持状态切换）
            ActionButton(
                icon = if (coinCount > 0) Icons.Filled.MonetizationOn else Icons.Outlined.MonetizationOn,
                text = if (coinCount > 0) "$coinCount 币" else "投币",
                iconColor = if (isDark) ActionCoinDark else Color(0xFFFFB300),
                iconSize = 26.dp,
                isActive = coinCount > 0,
                onClick = onCoinClick
            )

            // 🔥 收藏 - 黄色（支持状态切换）
            ActionButton(
                icon = if (isFavorited) Icons.Filled.Star else Icons.Outlined.Star,
                text = if (info.stat.favorite > 0) FormatUtils.formatStat(info.stat.favorite.toLong()) else "收藏",
                iconColor = if (isDark) ActionFavoriteDark else Color(0xFFFFC107),
                iconSize = 26.dp,
                isActive = isFavorited,
                onClick = onFavoriteClick
            )

            // 🔥 三连 - 渐变色
            ActionButton(
                icon = Icons.Filled.Favorite,
                text = "三连",
                iconColor = if (isDark) Color(0xFFFF6B9D) else Color(0xFFE91E63),
                iconSize = 26.dp,
                onClick = onTripleClick
            )

            // 🔥 评论 - 青色
            val replyCount = runCatching { info.stat.reply }.getOrDefault(0)
            ActionButton(
                icon = Icons.Outlined.Comment,
                text = if (replyCount > 0) FormatUtils.formatStat(replyCount.toLong()) else "评论",
                iconColor = if (isDark) ActionCommentDark else Color(0xFF00BCD4),
                onClick = onCommentClick,
                iconSize = 26.dp
            )
    }
}

// 🔥 优化版 ActionButton - 带按压动画和彩色图标
@Composable
fun ActionButton(
    icon: ImageVector,
    text: String,
    isActive: Boolean = false,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant, // 🔥 新增颜色参数
    iconSize: androidx.compose.ui.unit.Dp = 24.dp,
    onClick: () -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()
    
    // 🔥 按压动画状态
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .width(64.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
    ) {
        // 🔥 图标容器 - 使用彩色背景，深色模式下提高透明度
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = if (isDark) 0.15f else 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(iconSize)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Normal,
            maxLines = 1
        )
    }
}

// 🔥 3. 简介区域（优化样式）
@Composable
fun DescriptionSection(desc: String) {
    var expanded by remember { mutableStateOf(false) }

    if (desc.isBlank()) return

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .animateContentSize()
        ) {
            Text(
                text = desc,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                maxLines = if (expanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis
            )

            if (desc.length > 100 || desc.lines().size > 3) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (expanded) "收起" else "展开更多",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// 🔥 4. 推荐视频列表头部
@Composable
fun RelatedVideosHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "更多推荐",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

// 🔥 5. 推荐视频单项（iOS 风格优化）
@Composable
fun RelatedVideoItem(video: RelatedVideo, onClick: () -> Unit) {
    // 🔥 iOS 风格按压动画
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
        ),
        label = "cardScale"
    )
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onClick() }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // 视频封面
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(94.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(FormatUtils.fixImageUrl(video.pic))
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // 时长标签
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = FormatUtils.formatDuration(video.duration),
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                
                // 🔥 播放量遮罩
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                            )
                        )
                )
                
                // 播放量标签
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = FormatUtils.formatStat(video.stat.view.toLong()),
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 视频信息
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(94.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 标题
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 19.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // UP主信息行 🔥 优化样式
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // UP主头标
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "UP",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = video.owner.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// 🔥🔥 [新增] 投币对话框
@Composable
fun CoinDialog(
    visible: Boolean,
    currentCoinCount: Int,  // 已投币数量 0/1/2
    onDismiss: () -> Unit,
    onConfirm: (count: Int, alsoLike: Boolean) -> Unit
) {
    if (!visible) return
    
    var selectedCount by remember { mutableStateOf(1) }
    var alsoLike by remember { mutableStateOf(true) }
    
    val maxCoins = 2 - currentCoinCount  // 剩余可投数量
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("投币", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    "选择投币数量",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // 投币选项
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // 投 1 币
                    FilterChip(
                        selected = selectedCount == 1,
                        onClick = { selectedCount = 1 },
                        label = { Text("1 硬币") },
                        enabled = maxCoins >= 1
                    )
                    // 投 2 币
                    FilterChip(
                        selected = selectedCount == 2,
                        onClick = { selectedCount = 2 },
                        label = { Text("2 硬币") },
                        enabled = maxCoins >= 2
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 同时点赞
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { alsoLike = !alsoLike },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = alsoLike,
                        onCheckedChange = { alsoLike = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("同时点赞")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedCount.coerceAtMost(maxCoins), alsoLike) },
                enabled = maxCoins > 0
            ) {
                Text("投币")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}