// 文件路径: feature/video/VideoPlayerState.kt
package com.android.purebilibili.feature.video

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.view.WindowManager
import java.io.InputStream
import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.core.app.NotificationCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaMetadata
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.android.purebilibili.R
import com.android.purebilibili.core.network.NetworkModule
import com.android.purebilibili.core.util.FormatUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import master.flame.danmaku.ui.widget.DanmakuView
import kotlin.math.abs

private const val NOTIFICATION_ID = 1001
private const val CHANNEL_ID = "media_playback_channel"
private const val THEME_COLOR = 0xFFFB7299.toInt()

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class VideoPlayerState(
    val context: Context,
    val player: ExoPlayer,
    val danmakuView: DanmakuView,
    val mediaSession: MediaSession,
    // 🔥 性能优化：传入受管理的 CoroutineScope，避免内存泄漏
    private val scope: CoroutineScope
) {
    var isDanmakuOn by mutableStateOf(true)

    fun updateMediaMetadata(title: String, artist: String, coverUrl: String) {
        val currentItem = player.currentMediaItem ?: return

        // 1. 更新 Player 内部元数据
        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .setArtworkUri(Uri.parse(FormatUtils.fixImageUrl(coverUrl)))
            .setDisplayTitle(title)
            .setIsPlayable(true)
            .build()

        val newItem = currentItem.buildUpon()
            .setMediaMetadata(metadata)
            .build()

        player.replaceMediaItem(player.currentMediaItemIndex, newItem)

        // 2. 🔥 性能优化：使用传入的 scope 而非裸创建的 CoroutineScope
        scope.launch(Dispatchers.IO) {
            val bitmap = loadBitmap(context, coverUrl)

            // 切回主线程操作 Player 和发送通知
            launch(Dispatchers.Main) {
                pushMediaNotification(title, artist, bitmap)
            }
        }
    }

    private suspend fun loadBitmap(context: Context, url: String): Bitmap? {
        return try {
            // 🔥 性能优化：使用 Coil 单例，避免重复创建 ImageLoader
            val loader = context.imageLoader
            val request = ImageRequest.Builder(context)
                .data(FormatUtils.fixImageUrl(url))
                .allowHardware(false)
                .scale(Scale.FILL)
                .transformations(RoundedCornersTransformation(16f))
                .size(512, 512)
                .build()
            val result = loader.execute(request)
            (result as? SuccessResult)?.drawable?.let { (it as BitmapDrawable).bitmap }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun pushMediaNotification(title: String, artist: String, bitmap: Bitmap?) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 确保渠道存在
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(CHANNEL_ID, "媒体播放", NotificationManager.IMPORTANCE_LOW).apply {
                    description = "显示播放控制"
                    setShowBadge(false)
                    setSound(null, null)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }

        val style = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession.sessionCompatToken)
            .setShowActionsInCompactView(0)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(artist)
            .setLargeIcon(bitmap)
            .setStyle(style)
            .setColor(THEME_COLOR)
            .setColorized(true)
            .setOngoing(player.isPlaying)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            // 🔥🔥🔥 修复点：直接使用 sessionActivity
            .setContentIntent(mediaSession.sessionActivity)

        try {
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun loadDanmaku(data: ByteArray) {
        if (data.isEmpty()) {
            android.util.Log.w("Danmaku", "Empty danmaku data, skip loading")
            return
        }
        scope.launch(Dispatchers.IO) {
            try {
                android.util.Log.d("Danmaku", "Loading danmaku, data size: ${data.size} bytes")
                // 🔥 在主线程处理所有 UI/Context 相关逻辑
                launch(Dispatchers.Main) {
                    val stream = java.io.ByteArrayInputStream(data)
                    
                    // 🔥 创建 Context 并配置
                    val danmakuContext = DanmakuContext.create()
                    danmakuContext.setDanmakuStyle(master.flame.danmaku.danmaku.model.IDisplayer.DANMAKU_STYLE_STROKEN, 3f)
                        .setDuplicateMergingEnabled(false)  // 🔥 禁用重复合并
                        .setScrollSpeedFactor(1.2f)        // 🔥 设置滚动速度
                        .setScaleTextSize(1.2f)            // 🔥 稍微放大字体
                        // 🔥 禁用重叠过滤，确保能显示多少显示多少
                        .preventOverlapping(mapOf<Int, Boolean>(
                            master.flame.danmaku.danmaku.model.BaseDanmaku.TYPE_SCROLL_RL to false,
                            master.flame.danmaku.danmaku.model.BaseDanmaku.TYPE_SCROLL_LR to false,
                            master.flame.danmaku.danmaku.model.BaseDanmaku.TYPE_FIX_TOP to false,
                            master.flame.danmaku.danmaku.model.BaseDanmaku.TYPE_FIX_BOTTOM to false
                        ))
                    
                    // 🔥 创建解析器 (传入 Context)
                    val parser = com.android.purebilibili.core.util.BiliDanmakuParser(danmakuContext).apply {
                        load(com.android.purebilibili.core.util.StreamDataSource(stream))
                    }

                    // 🔥🔥 关键修复：清除旧的弹幕状态
                    if (danmakuView.isPrepared) {
                        android.util.Log.d("Danmaku", "Stopping old danmaku before re-prepare")
                        danmakuView.stop()
                        danmakuView.clearDanmakusOnScreen()
                    }
                    // 🔥🔥 辅助函数：启动弹幕
                    fun startDanmakuIfReady() {
                        if (danmakuView.width > 0 && danmakuView.height > 0 && isDanmakuOn) {
                            val pos = player.currentPosition
                            android.util.Log.d("Danmaku", "✅ Starting danmaku: ${danmakuView.width}x${danmakuView.height}, pos=${pos}ms")
                            
                            danmakuView.show()
                            danmakuView.start(pos)
                            danmakuView.seekTo(pos)
                            
                            // 🔥🔥 关键修复：使用 Handler 循环检查并重试 resume
                            val handler = android.os.Handler(android.os.Looper.getMainLooper())
                            var retryCount = 0
                            val maxRetry = 10
                            
                            fun tryResume() {
                                if (retryCount >= maxRetry) {
                                    android.util.Log.e("Danmaku", "❌ Failed to resume after $maxRetry retries")
                                    return
                                }
                                retryCount++
                                danmakuView.resume()
                                
                                // 延迟检查是否成功 resume
                                handler.postDelayed({
                                    if (danmakuView.isPaused && danmakuView.isPrepared) {
                                        android.util.Log.w("Danmaku", "⚠️ Still paused after resume(), retry #$retryCount, isShown=${danmakuView.isShown}")
                                        tryResume()
                                    } else {
                                        android.util.Log.d("Danmaku", "✅ Resume successful! isPaused=${danmakuView.isPaused}")
                                    }
                                }, 100)
                            }
                            // 启动检查
                            tryResume()
                        } else {
                            android.util.Log.w("Danmaku", "❌ startDanmakuIfReady skipped: width=${danmakuView.width}, height=${danmakuView.height}, isDanmakuOn=$isDanmakuOn")
                        }
                    }
                    
                    // 🔥🔥 关键修复：设置 Callback 监听 prepared 事件
                    danmakuView.setCallback(object : master.flame.danmaku.controller.DrawHandler.Callback {
                        override fun prepared() {
                            // 🔥 使用 postDelayed 确保布局完全完成
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                val viewWidth = danmakuView.width
                                val viewHeight = danmakuView.height
                                android.util.Log.d("Danmaku", "DanmakuView prepared! Size: ${viewWidth}x${viewHeight}")
                                
                                // 🔥🔥 关键修复：如果尺寸为0，使用 OnLayoutChangeListener 等待布局完成
                                if (viewWidth == 0 || viewHeight == 0) {
                                    android.util.Log.w("Danmaku", "⚠️ ZERO dimensions, adding OnLayoutChangeListener")
                                    danmakuView.addOnLayoutChangeListener(object : android.view.View.OnLayoutChangeListener {
                                        override fun onLayoutChange(
                                            v: android.view.View?, left: Int, top: Int, right: Int, bottom: Int,
                                            oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
                                        ) {
                                            val w = right - left
                                            val h = bottom - top
                                            android.util.Log.d("Danmaku", "OnLayoutChange: ${w}x${h}")
                                            if (w > 0 && h > 0) {
                                                danmakuView.removeOnLayoutChangeListener(this)
                                                startDanmakuIfReady()
                                            }
                                        }
                                    })
                                    // 强制请求布局
                                    danmakuView.requestLayout()
                                    return@postDelayed
                                }
                                
                                startDanmakuIfReady()
                            }, 150)  // 🔥 延迟 150ms 确保 Compose 布局完成
                        }
                        override fun updateTimer(timer: master.flame.danmaku.danmaku.model.DanmakuTimer) {}
                        override fun danmakuShown(danmaku: master.flame.danmaku.danmaku.model.BaseDanmaku?) {
                            android.util.Log.d("Danmaku", "danmakuShown: ${danmaku?.text?.take(20)}")
                        }
                        override fun drawingFinished() {}
                    })
                    
                    android.util.Log.d("Danmaku", "Calling danmakuView.prepare()")
                    danmakuView.prepare(parser, danmakuContext)
                    danmakuView.showFPS(false)
                    danmakuView.enableDanmakuDrawingCache(true)
                    
                    if (isDanmakuOn) {
                        danmakuView.show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("Danmaku", "Failed to load danmaku", e)
            }
        }
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun rememberVideoPlayerState(
    context: Context,
    viewModel: PlayerViewModel,
    bvid: String
): VideoPlayerState {

    // 🔥 尝试复用 MiniPlayerManager 中已加载的 player
    val miniPlayerManager = MiniPlayerManager.getInstance(context)
    val reuseFromMiniPlayer = miniPlayerManager.isActive && miniPlayerManager.currentBvid == bvid
    
    val player = remember(context, bvid, reuseFromMiniPlayer) {
        // 如果小窗有这个视频的 player，直接复用
        if (reuseFromMiniPlayer) {
            miniPlayerManager.player?.also {
                android.util.Log.d("VideoPlayerState", "🔥 复用小窗 player: bvid=$bvid")
            }
        } else {
            null
        } ?: run {
            // 创建新的 player
            android.util.Log.d("VideoPlayerState", "🔥 创建新 player: bvid=$bvid")
            val headers = mapOf(
                "Referer" to "https://www.bilibili.com",
                "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
            )
            val dataSourceFactory = OkHttpDataSource.Factory(NetworkModule.okHttpClient)
                .setDefaultRequestProperties(headers)

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                .build()

            // 🔥🔥 [修复] 读取硬件解码设置
            val prefs = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
            // DataStore 使用 settings_prefs 文件，但直接读 SP 需要用不同的 key 名称
            // 由于 DataStore 是异步的，这里我们用同步方式检查
            // 注意：DataStore 的 xml key 是 "hw_decode"
            val hwDecodeEnabled = kotlinx.coroutines.runBlocking {
                com.android.purebilibili.core.store.SettingsManager.getHwDecode(context).first()
            }
            android.util.Log.d("VideoPlayerState", "🔥 硬件解码设置: $hwDecodeEnabled")

            // 🔥 根据设置选择 RenderersFactory
            val renderersFactory = if (hwDecodeEnabled) {
                // 默认 Factory，优先使用硬件解码
                androidx.media3.exoplayer.DefaultRenderersFactory(context)
                    .setExtensionRendererMode(androidx.media3.exoplayer.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
            } else {
                // 强制使用软件解码
                androidx.media3.exoplayer.DefaultRenderersFactory(context)
                    .setExtensionRendererMode(androidx.media3.exoplayer.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)
                    .setEnableDecoderFallback(true)
            }

            ExoPlayer.Builder(context)
                .setRenderersFactory(renderersFactory)
                .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
                .setAudioAttributes(audioAttributes, true)
                .setHandleAudioBecomingNoisy(true)
                .build()
                .apply {
                    prepare()
                    playWhenReady = true
                }
        }
    }

    val sessionActivityPendingIntent = remember(context, bvid) {
        val intent = Intent(context, VideoActivity::class.java).apply {
            putExtra("bvid", bvid)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    // 🔥 为 MediaSession 生成唯一 ID，避免从小窗展开时冲突
    val sessionId = remember(bvid) { "bilipai_${bvid}_${System.currentTimeMillis()}" }
    
    val mediaSession = remember(player, sessionActivityPendingIntent, sessionId) {
        MediaSession.Builder(context, player)
            .setId(sessionId)  // 🔥 使用唯一 ID
            .setSessionActivity(sessionActivityPendingIntent)
            .build()
    }

    // 🔥 [清理] 删除了未使用的 danmakuContext 变量，实际在 loadDanmaku() 中创建
    // 🔥 使用 DanmakuView（不是 TextureView，因为 TextureView 的 Surface 初始化有问题）
    val danmakuView = remember(context) { DanmakuView(context) }
    
    // 🔥 性能优化：使用 rememberCoroutineScope 创建受管理的协程作用域
    val scope = rememberCoroutineScope()

    val holder = remember(player, danmakuView, mediaSession, scope) {
        VideoPlayerState(context, player, danmakuView, mediaSession, scope)
    }

    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState) {
        if (uiState is PlayerUiState.Success) {
            val info = (uiState as PlayerUiState.Success).info
            holder.updateMediaMetadata(info.title, info.owner.name, info.pic)
        }
    }

    DisposableEffect(player, danmakuView, mediaSession) {
        onDispose {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(NOTIFICATION_ID)

            // 🔥 检查是否有小窗在使用这个 player
            val miniPlayerManager = MiniPlayerManager.getInstance(context)
            if (miniPlayerManager.isMiniMode && miniPlayerManager.isActive) {
                // 小窗模式下不释放 player，只释放其他资源
                android.util.Log.d("VideoPlayerState", "🔥 小窗模式激活，不释放 player")
                danmakuView.release()
            } else {
                // 正常释放所有资源
                android.util.Log.d("VideoPlayerState", "🔥 释放所有资源")
                mediaSession.release()
                player.release()
                danmakuView.release()
            }
            
            (context as? ComponentActivity)?.window?.attributes?.screenBrightness =
                WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        }
    }

    LaunchedEffect(bvid) { viewModel.loadVideo(bvid) }
    LaunchedEffect(player) { viewModel.attachPlayer(player) }
    
    // 🔥 监听弹幕数据并在加载后初始化弹幕（仅执行一次）
    val danmakuData = (uiState as? PlayerUiState.Success)?.danmakuData
    LaunchedEffect(danmakuData) {
        android.util.Log.d("VideoPlayerState", "LaunchedEffect(danmakuData): data size = ${danmakuData?.size ?: 0}")
        if (danmakuData != null && danmakuData.isNotEmpty()) {
            android.util.Log.d("VideoPlayerState", "Calling holder.loadDanmaku()")
            holder.loadDanmaku(danmakuData)
        }
    }

    // 🔥 弹幕同步循环 - 持续同步弹幕位置
    LaunchedEffect(player, danmakuView) {
        while (true) {
            if (danmakuView.isPrepared && holder.isDanmakuOn) {
                val playerPos = player.currentPosition
                val danmakuPos = danmakuView.currentTime
                val isPlaying = player.isPlaying
                
                if (isPlaying) {
                    if (danmakuView.isPaused) {
                        android.util.Log.d("DanmakuSync", "Resuming danmaku")
                        danmakuView.resume()
                    }
                    // 如果偏差超过 1 秒，同步
                    if (abs(playerPos - danmakuPos) > 1000) {
                        android.util.Log.d("DanmakuSync", "Syncing: player=$playerPos, danmaku=$danmakuPos")
                        danmakuView.seekTo(playerPos)
                    }
                } else {
                    if (!danmakuView.isPaused) {
                        danmakuView.pause()
                    }
                }
            }
            kotlinx.coroutines.delay(500)
        }
    }
    LaunchedEffect(holder.isDanmakuOn) {
        if (holder.isDanmakuOn) danmakuView.show() else danmakuView.hide()
    }

    return holder
}