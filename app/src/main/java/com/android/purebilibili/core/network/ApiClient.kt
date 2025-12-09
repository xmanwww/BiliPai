// 文件路径: core/network/ApiClient.kt
package com.android.purebilibili.core.network

import android.content.Context
import com.android.purebilibili.core.store.TokenManager
import com.android.purebilibili.data.model.response.*
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.UUID
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

interface BilibiliApi {
    // ... (保留 Nav, Stat, History, Fav 等接口) ...
    @GET("x/web-interface/nav")
    suspend fun getNavInfo(): NavResponse

    @GET("x/web-interface/nav/stat")
    suspend fun getNavStat(): NavStatResponse

    @GET("x/web-interface/history/cursor")
    suspend fun getHistoryList(@Query("ps") ps: Int = 20): ListResponse<HistoryData>

    @GET("x/v3/fav/folder/created/list-all")
    suspend fun getFavFolders(@Query("up_mid") mid: Long): FavFolderResponse

    @GET("x/v3/fav/resource/list")
    suspend fun getFavoriteList(
        @Query("media_id") mediaId: Long, 
        @Query("pn") pn: Int = 1,
        @Query("ps") ps: Int = 20
    ): ListResponse<FavoriteData>

    @GET("x/web-interface/wbi/index/top/feed/rcmd")
    suspend fun getRecommendParams(@QueryMap params: Map<String, String>): RecommendResponse

    @GET("x/web-interface/view")
    suspend fun getVideoInfo(@Query("bvid") bvid: String): VideoDetailResponse

    @GET("x/player/wbi/playurl")
    suspend fun getPlayUrl(@QueryMap params: Map<String, String>): PlayUrlResponse

    @GET("x/web-interface/archive/related")
    suspend fun getRelatedVideos(@Query("bvid") bvid: String): RelatedResponse

    @GET("x/v1/dm/list.so")
    suspend fun getDanmakuXml(@Query("oid") cid: Long): ResponseBody

    // 🔥🔥 [核心修改] 改为 wbi 路径，并接收 Map 参数以支持签名
    @GET("x/v2/reply/wbi/main")
    suspend fun getReplyList(@QueryMap params: Map<String, String>): ReplyResponse

    @GET("x/emote/user/panel/web")
    suspend fun getEmotes(
        @Query("business") business: String = "reply"
    ): EmoteResponse
    @GET("x/v2/reply/reply")
    suspend fun getReplyReply(
        @Query("oid") oid: Long,
        @Query("type") type: Int = 1,
        @Query("root") root: Long, // 根评论 ID (rpid)
        @Query("pn") pn: Int,     // 页码
        @Query("ps") ps: Int = 20 // 每页数量
    ): ReplyResponse // 复用 ReplyResponse 结构
    
    // 🔥🔥 [新增] 查询与 UP 主的关注关系
    @GET("x/relation")
    suspend fun getRelation(
        @Query("fid") fid: Long  // UP 主 mid
    ): RelationResponse
    
    // 🔥🔥 [新增] 查询视频是否已收藏
    @GET("x/v2/fav/video/favoured")
    suspend fun checkFavoured(
        @Query("aid") aid: Long
    ): FavouredResponse
    
    // 🔥🔥 [新增] 关注/取关 UP 主
    @retrofit2.http.FormUrlEncoded
    @retrofit2.http.POST("x/relation/modify")
    suspend fun modifyRelation(
        @retrofit2.http.Field("fid") fid: Long,      // UP 主 mid
        @retrofit2.http.Field("act") act: Int,        // 1=关注, 2=取关
        @retrofit2.http.Field("csrf") csrf: String
    ): SimpleApiResponse
    
    // 🔥🔥 [新增] 收藏/取消收藏视频
    @retrofit2.http.FormUrlEncoded
    @retrofit2.http.POST("x/v3/fav/resource/deal")
    suspend fun dealFavorite(
        @retrofit2.http.Field("rid") rid: Long,                    // 视频 aid
        @retrofit2.http.Field("type") type: Int = 2,               // 资源类型 2=视频
        @retrofit2.http.Field("add_media_ids") addIds: String = "", // 添加到的收藏夹 ID
        @retrofit2.http.Field("del_media_ids") delIds: String = "", // 从收藏夹移除
        @retrofit2.http.Field("csrf") csrf: String
    ): SimpleApiResponse
    
    // 🔥🔥 [新增] 点赞/取消点赞视频
    @retrofit2.http.FormUrlEncoded
    @retrofit2.http.POST("x/web-interface/archive/like")
    suspend fun likeVideo(
        @retrofit2.http.Field("aid") aid: Long,
        @retrofit2.http.Field("like") like: Int,   // 1=点赞, 2=取消点赞
        @retrofit2.http.Field("csrf") csrf: String
    ): SimpleApiResponse
    
    // 🔥🔥 [新增] 查询是否已点赞
    @GET("x/web-interface/archive/has/like")
    suspend fun hasLiked(
        @Query("aid") aid: Long
    ): HasLikedResponse
    
    // 🔥🔥 [新增] 投币
    @retrofit2.http.FormUrlEncoded
    @retrofit2.http.POST("x/web-interface/coin/add")
    suspend fun coinVideo(
        @retrofit2.http.Field("aid") aid: Long,
        @retrofit2.http.Field("multiply") multiply: Int,       // 投币数量 1 或 2
        @retrofit2.http.Field("select_like") selectLike: Int,  // 1=同时点赞, 0=不点赞
        @retrofit2.http.Field("csrf") csrf: String
    ): SimpleApiResponse
    
    // 🔥🔥 [新增] 查询已投币数
    @GET("x/web-interface/archive/coins")
    suspend fun hasCoined(
        @Query("aid") aid: Long
    ): HasCoinedResponse
}


// ... (SearchApi, PassportApi, NetworkModule 保持不变，直接保留你现有的即可) ...
// (为了节省篇幅，NetworkModule 部分代码与上一版相同，不需要变动，只改上面的 Interface 即可)
interface SearchApi {
    @GET("x/web-interface/search/square")
    suspend fun getHotSearch(@Query("limit") limit: Int = 10): HotSearchResponse

    @GET("x/web-interface/search/all/v2")
    suspend fun search(@QueryMap params: Map<String, String>): SearchResponse
}

// 🔥 动态 API
interface DynamicApi {
    @GET("x/polymer/web-dynamic/v1/feed/all")
    suspend fun getDynamicFeed(
        @Query("type") type: String = "all",
        @Query("offset") offset: String = "",
        @Query("page") page: Int = 1
    ): DynamicFeedResponse
}

interface PassportApi {
    @GET("x/passport-login/web/qrcode/generate")
    suspend fun generateQrCode(): QrCodeResponse

    @GET("x/passport-login/web/qrcode/poll")
    suspend fun pollQrCode(@Query("qrcode_key") key: String): Response<PollResponse>
}


object NetworkModule {
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    val okHttpClient: OkHttpClient by lazy {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        OkHttpClient.Builder()
            .protocols(listOf(Protocol.HTTP_1_1))
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            // 🔥 [新增] 超时配置，提高网络稳定性
            .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            // 🔥 [新增] 自动重试和重定向
            .retryOnConnectionFailure(true)
            .followRedirects(true)
            .followSslRedirects(true)
            .addInterceptor { chain ->
                val original = chain.request()
                val builder = original.newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Referer", "https://www.bilibili.com")

                val cookieBuilder = StringBuilder()

                var buvid3 = TokenManager.buvid3Cache
                if (buvid3.isNullOrEmpty()) {
                    buvid3 = UUID.randomUUID().toString() + "infoc"
                    TokenManager.buvid3Cache = buvid3
                }
                cookieBuilder.append("buvid3=$buvid3;")

                val sessData = TokenManager.sessDataCache
                if (!sessData.isNullOrEmpty()) {
                    cookieBuilder.append("SESSDATA=$sessData;")
                }

                val finalCookie = cookieBuilder.toString()
                android.util.Log.d("ApiClient", "🔥 Sending request to ${original.url}, Cookie contains SESSDATA: ${sessData != null && sessData.isNotEmpty()}")
                builder.header("Cookie", finalCookie)

                chain.proceed(builder.build())
            }
            .build()
    }

    val api: BilibiliApi by lazy {
        Retrofit.Builder().baseUrl("https://api.bilibili.com/").client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType())).build()
            .create(BilibiliApi::class.java)
    }
    val passportApi: PassportApi by lazy {
        Retrofit.Builder().baseUrl("https://passport.bilibili.com/").client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType())).build()
            .create(PassportApi::class.java)
    }
    val searchApi: SearchApi by lazy {
        Retrofit.Builder().baseUrl("https://api.bilibili.com/").client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType())).build()
            .create(SearchApi::class.java)
    }
    
    // 🔥 动态 API
    val dynamicApi: DynamicApi by lazy {
        Retrofit.Builder().baseUrl("https://api.bilibili.com/").client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType())).build()
            .create(DynamicApi::class.java)
    }
}