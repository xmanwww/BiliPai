package com.android.purebilibili.feature.login

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.android.purebilibili.core.store.TokenManager
import com.android.purebilibili.core.theme.BiliPink
import com.android.purebilibili.core.ui.LoadingAnimation
import kotlinx.coroutines.launch

@Composable
fun FloatingDecorations() {
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset1"
    )
    val offset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset2"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 大圆
        Box(
            modifier = Modifier
                .offset(x = (-60).dp, y = (100 + offset1).dp)
                .size(200.dp)
                .alpha(0.1f)
                .background(BiliPink, CircleShape)
        )
        // 小圆
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (60 + offset2).dp)
                .size(120.dp)
                .alpha(0.08f)
                .background(Color(0xFF00D4FF), CircleShape)
        )
    }
}

@Composable
fun TopBar(onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "返回",
                tint = Color.White
            )
        }

        Text(
            text = "安全登录",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.size(40.dp))
    }
}

@Composable
fun BrandingSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Logo
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = BiliPink,
            shadowElevation = 16.dp,
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "B",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "BiliPai",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = "第三方 Bilibili 客户端",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun LoginMethodTabs(
    selectedMethod: LoginMethod,
    onMethodChange: (LoginMethod) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.08f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
            ) {
            LoginMethod.entries.forEach { method ->
                val isSelected = method == selectedMethod
                Surface(
                    onClick = { onMethodChange(method) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) BiliPink else Color.Transparent,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (method) {
                                LoginMethod.QR_CODE -> Icons.Outlined.QrCode2
                                LoginMethod.WEB_LOGIN -> Icons.Outlined.Language
                            },
                            contentDescription = null,
                            tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (method) {
                                LoginMethod.QR_CODE -> "扫码登录"
                                LoginMethod.WEB_LOGIN -> "网页登录"
                            },
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QrCodeLoginContent(
    state: LoginState,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 二维码卡片
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 24.dp,
            modifier = Modifier.size(280.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                when (state) {
                    is LoginState.Loading -> {
                        LoadingQrCode()
                    }
                    is LoginState.QrCode -> {
                        QrCodeImage(bitmap = state.bitmap)
                    }
                    is LoginState.Scanned -> {
                        ScannedOverlay(bitmap = state.bitmap)
                    }
                    is LoginState.Error -> {
                        ErrorQrCode(message = state.msg, onRetry = onRefresh)
                    }
                    is LoginState.Success -> {
                        SuccessIndicator()
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 提示文字
        when (state) {
            is LoginState.QrCode -> {
                QrCodeHint()
            }
            is LoginState.Scanned -> {
                ScannedHint()
            }
            is LoginState.Error -> {
                TextButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, null, tint = BiliPink)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("刷新二维码", color = BiliPink)
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun LoadingQrCode() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 🔥 使用 Lottie 加载动画
        LoadingAnimation(
            size = 64.dp,
            text = "正在加载..."
        )
    }
}

@Composable
private fun QrCodeImage(bitmap: Bitmap) {
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "二维码",
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
    )
}

@Composable
private fun ScannedOverlay(bitmap: Bitmap) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanned")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(contentAlignment = Alignment.Center) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "二维码",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .alpha(0.2f)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                modifier = Modifier
                    .size(80.dp)
                    .scale(scale)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.PhoneAndroid,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "请在手机上确认",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
        }
    }
}

@Composable
private fun ErrorQrCode(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint = Color(0xFFFF6B6B),
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = Color.Gray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SuccessIndicator() {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "success_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFF4CAF50),
            modifier = Modifier
                .size(72.dp)
                .scale(scale)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "登录成功",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50)
        )
    }
}

@Composable
private fun QrCodeHint() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.PhoneAndroid,
                contentDescription = null,
                tint = BiliPink,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "打开 Bilibili App 扫一扫",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "首页左上角 → 扫一扫",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 13.sp
        )
    }
}

@Composable
private fun ScannedHint() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            color = Color(0xFF4CAF50),
            strokeWidth = 2.dp,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "等待确认中...",
            color = Color(0xFF4CAF50),
            fontSize = 14.sp
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebLoginContent(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var hasOpenedBrowser by remember { mutableStateOf(false) }
    var checkingLogin by remember { mutableStateOf(false) }
    var loginCheckFailed by remember { mutableStateOf(false) }
    
    // 检查登录状态
    fun checkLoginStatus() {
        scope.launch {
            checkingLogin = true
            loginCheckFailed = false
            try {
                val cookies = CookieManager.getInstance().getCookie("https://passport.bilibili.com")
                    ?: CookieManager.getInstance().getCookie("https://www.bilibili.com")
                android.util.Log.d("WebLogin", "🔥 检查 Cookie: $cookies")
                
                if (!cookies.isNullOrEmpty() && cookies.contains("SESSDATA")) {
                    val sessData = cookies.split(";")
                        .map { it.trim() }
                        .find { it.startsWith("SESSDATA=") }
                        ?.substringAfter("SESSDATA=")
                    
                    if (!sessData.isNullOrEmpty()) {
                        android.util.Log.d("WebLogin", "✅ 检测到 SESSDATA")
                        TokenManager.saveCookies(context, sessData)
                        onLoginSuccess()
                        return@launch
                    }
                }
                loginCheckFailed = true
            } finally {
                checkingLogin = false
            }
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 提示卡片
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.08f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Security,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "使用 Bilibili 官方登录页面，更安全",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        // 主卡片
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Outlined.Language,
                    contentDescription = null,
                    tint = BiliPink,
                    modifier = Modifier.size(64.dp)
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = if (hasOpenedBrowser) "在浏览器中完成登录后" else "在浏览器中登录",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = if (hasOpenedBrowser) "返回此应用并点击验证按钮" else "点击下方按钮打开浏览器完成登录",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (!hasOpenedBrowser) {
                    // 打开浏览器按钮
                    Button(
                        onClick = {
                            val intent = android.content.Intent(
                                android.content.Intent.ACTION_VIEW,
                                android.net.Uri.parse("https://passport.bilibili.com/h5-app/passport/login")
                            )
                            context.startActivity(intent)
                            hasOpenedBrowser = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BiliPink),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Outlined.Language, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("打开浏览器登录", color = Color.White)
                    }
                } else {
                    // 验证登录状态按钮
                    Button(
                        onClick = { checkLoginStatus() },
                        enabled = !checkingLogin,
                        colors = ButtonDefaults.buttonColors(containerColor = BiliPink),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (checkingLogin) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("验证中...", color = Color.White)
                        } else {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("验证登录状态", color = Color.White)
                        }
                    }
                    
                    if (loginCheckFailed) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "未检测到登录，请先在浏览器中完成登录",
                            fontSize = 12.sp,
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 重新打开浏览器
                    TextButton(onClick = {
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse("https://passport.bilibili.com/h5-app/passport/login")
                        )
                        context.startActivity(intent)
                    }) {
                        Text("重新打开浏览器", color = BiliPink)
                    }
                }
            }
        }
    }
}
