package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.FileDataSource
import androidx.media3.datasource.TransferListener
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.repository.MultiSourceEngine
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.io.ByteArrayInputStream
import java.util.Locale
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Custom Media3 DataSource wrapper that:
 * 1. Supports local file:// MP4 playback for offline downloads.
 * 2. Injects the appropriate Referer & Origin headers per CDN host.
 * 3. Detects and strips disguised image headers (e.g., PNG header prepended to MPEG-TS
 *    chunks) so ExoPlayer's TsExtractor receives pure 0x47-aligned MPEG-TS packets.
 */
@UnstableApi
private class SmartHlsDataSource(
    private val upstreamFactory: DefaultHttpDataSource.Factory
) : DataSource {

    private var upstream: DataSource? = null
    private var httpUpstream: DefaultHttpDataSource? = null
    private val peekBuffer = ByteArray(2048)
    private var peekPos = 0
    private var peekLen = 0
    private var isDisguisedTs = false
    private var openedUri: Uri? = null

    override fun addTransferListener(transferListener: TransferListener) {}

    override fun open(dataSpec: DataSpec): Long {
        val urlStr = dataSpec.uri.toString()
        if (dataSpec.uri.scheme == "file") {
            val fileSource = FileDataSource()
            upstream = fileSource
            httpUpstream = null
            openedUri = dataSpec.uri
            peekPos = 0
            peekLen = 0
            isDisguisedTs = false
            return fileSource.open(dataSpec)
        }

        val referer = when {
            urlStr.contains("vibrantpulselab.com", ignoreCase = true) ->
                "https://player.vibrantpulselab.com/"
            urlStr.contains("b-cdn.net", ignoreCase = true) || urlStr.contains("tamildhool", ignoreCase = true) ->
                "https://www.tamildhool.tech/"
            urlStr.contains("vcdnx.com", ignoreCase = true) || urlStr.contains("vcdnlare.com", ignoreCase = true) ->
                "https://ww7.vcdnlare.com/"
            urlStr.contains("okcdn.ru", ignoreCase = true) || urlStr.contains("ok.ru", ignoreCase = true) ->
                "https://ok.ru/"
            urlStr.contains("vimeocdn.com", ignoreCase = true) || urlStr.contains("vimeo.com", ignoreCase = true) ->
                "https://player.vimeo.com/"
            else ->
                "https://www.1tamilcrow.net/"
        }

        val customHeaders = HashMap<String, String>(dataSpec.httpRequestHeaders)
        if (!customHeaders.containsKey("Referer")) {
            customHeaders["Referer"] = referer
        }
        if (urlStr.contains("vibrantpulselab.com", ignoreCase = true) && !customHeaders.containsKey("Origin")) {
            customHeaders["Origin"] = "https://player.vibrantpulselab.com"
        } else if (urlStr.contains("vcdnx.com", ignoreCase = true) && !customHeaders.containsKey("Origin")) {
            customHeaders["Origin"] = "https://ww7.vcdnlare.com"
        }

        val updatedSpec = dataSpec.buildUpon()
            .setHttpRequestHeaders(customHeaders)
            .build()

        val source = upstreamFactory.createDataSource()
        upstream = source
        httpUpstream = source
        val rawBytesToRead = source.open(updatedSpec)
        openedUri = source.uri ?: dataSpec.uri

        peekPos = 0
        peekLen = 0
        isDisguisedTs = false

        if (urlStr.contains(".m3u8", ignoreCase = true) || urlStr.contains(".mpd", ignoreCase = true)) {
            return rawBytesToRead
        }

        while (peekLen < peekBuffer.size) {
            val read = source.read(peekBuffer, peekLen, peekBuffer.size - peekLen)
            if (read == C.RESULT_END_OF_INPUT) break
            if (read > 0) peekLen += read
        }

        if (peekLen >= 7 &&
            peekBuffer[0] == '#'.code.toByte() &&
            peekBuffer[1] == 'E'.code.toByte() &&
            peekBuffer[2] == 'X'.code.toByte() &&
            peekBuffer[3] == 'T'.code.toByte()
        ) {
            return rawBytesToRead
        }

        if (peekLen >= 377 && peekBuffer[0] != 0x47.toByte()) {
            val maxScan = peekLen - 376
            for (offset in 1 until maxScan) {
                if (peekBuffer[offset] == 0x47.toByte() &&
                    peekBuffer[offset + 188] == 0x47.toByte() &&
                    peekBuffer[offset + 376] == 0x47.toByte()
                ) {
                    peekPos = offset
                    isDisguisedTs = true
                    break
                }
            }
        } else if (peekLen >= 189 && peekBuffer[0] == 0x47.toByte() && peekBuffer[188] == 0x47.toByte()) {
            if (!urlStr.substringBefore("?").endsWith(".ts", ignoreCase = true)) {
                isDisguisedTs = true
            }
        }

        return if (isDisguisedTs && rawBytesToRead != C.LENGTH_UNSET.toLong() && rawBytesToRead > peekPos) {
            rawBytesToRead - peekPos
        } else {
            rawBytesToRead
        }
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) return 0
        if (peekPos < peekLen) {
            val toCopy = min(length, peekLen - peekPos)
            System.arraycopy(peekBuffer, peekPos, buffer, offset, toCopy)
            peekPos += toCopy
            return toCopy
        }
        val source = upstream ?: return C.RESULT_END_OF_INPUT
        return source.read(buffer, offset, length)
    }

    override fun getUri(): Uri? {
        val u = openedUri ?: upstream?.uri ?: return null
        if (isDisguisedTs) {
            val s = u.toString()
            val qIdx = s.indexOf('?')
            val pathPart = if (qIdx >= 0) s.substring(0, qIdx) else s
            val queryPart = if (qIdx >= 0) s.substring(qIdx) else ""
            val cleanPath = pathPart.replace(Regex("\\.(png|jpg|jpeg|gif|webp|txt|bin)$", RegexOption.IGNORE_CASE), "")
            val tsPath = if (cleanPath.endsWith(".ts", ignoreCase = true)) cleanPath else "$cleanPath.ts"
            return Uri.parse("$tsPath$queryPart")
        }
        return u
    }

    override fun getResponseHeaders(): Map<String, List<String>> {
        val orig = httpUpstream?.responseHeaders ?: emptyMap()
        if (!isDisguisedTs) return orig
        val modified = HashMap<String, List<String>>(orig)
        modified.remove(" content-type")
        modified.remove("content-type")
        modified["Content-Type"] = listOf("video/mp2t")
        return modified
    }

    override fun close() {
        try {
            upstream?.close()
        } finally {
            upstream = null
            httpUpstream = null
            peekPos = 0
            peekLen = 0
            isDisguisedTs = false
            openedUri = null
        }
    }

    class Factory(private val httpFactory: DefaultHttpDataSource.Factory) : DataSource.Factory {
        override fun createDataSource(): DataSource = SmartHlsDataSource(httpFactory)
    }
}

private enum class VideoQualityPreset(
    val label: String,
    val badge: String,
    val minWidth: Int,
    val minHeight: Int,
    val maxWidth: Int,
    val maxHeight: Int,
    val minBitrate: Int,
    val maxBitrate: Int
) {
    FULL_HD_1080P(
        label = "1080p Full HD",
        badge = "1080p HD",
        minWidth = 1280,
        minHeight = 720,
        maxWidth = 3840,
        maxHeight = 2160,
        minBitrate = 2_200_000,
        maxBitrate = Int.MAX_VALUE
    ),
    HD_720P(
        label = "720p HD",
        badge = "720p HD",
        minWidth = 854,
        minHeight = 480,
        maxWidth = 1280,
        maxHeight = 720,
        minBitrate = 900_000,
        maxBitrate = 2_800_000
    ),
    SMOOTH_480P(
        label = "480p Fast (No Buffer)",
        badge = "480p Fast",
        minWidth = 0,
        minHeight = 0,
        maxWidth = 854,
        maxHeight = 480,
        minBitrate = 0,
        maxBitrate = 1_100_000
    ),
    AUTO_ADAPTIVE(
        label = "Auto HD",
        badge = "Auto HD",
        minWidth = 0,
        minHeight = 0,
        maxWidth = 3840,
        maxHeight = 2160,
        minBitrate = 0,
        maxBitrate = Int.MAX_VALUE
    )
}

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(
    viewModel: MainViewModel,
    episodeUrl: String,
    episodeTitle: String = "",
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val videoUrlState by viewModel.videoUrlState.collectAsStateWithLifecycle()
    val episodeSources by viewModel.episodeSources.collectAsStateWithLifecycle()
    val selectedSource by viewModel.selectedSource.collectAsStateWithLifecycle()

    var isPlaying by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(true) }
    var bufferingSeconds by remember { mutableIntStateOf(0) }
    var playbackError by remember { mutableStateOf<String?>(null) }
    var hudMessage by remember { mutableStateOf<String?>(null) }
    var isWebPlayerMode by remember { mutableStateOf(false) }

    // Exit Confirmation Dialog State (Netflix TV style when pressing Back)
    var showExitConfirmDialog by remember { mutableStateOf(false) }

    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(0L) }

    // Exact millisecond timestamp preserved during Intelligent Failover or Quality switches
    var failoverResumePositionMs by remember { mutableLongStateOf(-1L) }

    // Netflix TV OSD Controls State
    var showOsd by remember { mutableStateOf(true) }
    var osdHideCounter by remember { mutableIntStateOf(0) }
    var showTrickPlayPreview by remember { mutableStateOf(false) }
    var trickPlayPreviewMs by remember { mutableLongStateOf(0L) }

    // Default to 1080p Full HD! User can switch anytime with 1 click.
    var qualityPreset by remember { mutableStateOf(VideoQualityPreset.FULL_HD_1080P) }

    // Aspect Ratio Selector: 0 = FIT, 1 = FILL, 2 = ZOOM 16:9
    var resizeModeIndex by remember { mutableIntStateOf(0) }
    val resizeModeLabel = when (resizeModeIndex) {
        1 -> "Fill Screen"
        2 -> "Cinema Zoom"
        else -> "Original Fit"
    }

    // Mobile Vertical Edge Gestures (Left: Brightness, Right: Volume)
    var screenBrightness by remember { mutableFloatStateOf(0.85f) }
    var playerVolume by remember { mutableFloatStateOf(0.95f) }

    // Silent, Textless Screensaver after 3 minutes of pause
    var pauseElapsedSeconds by remember { mutableIntStateOf(0) }
    var isOledScreensaverActive by remember { mutableStateOf(false) }
    var savedScreensaverPositionMs by remember { mutableLongStateOf(0L) }
    var currentBackdropIndex by remember { mutableIntStateOf(0) }

    // Explicit TV Remote FocusRequesters for seamless D-Pad navigation
    val backButtonFocusRequester = remember { FocusRequester() }
    val playPauseFocusRequester = remember { FocusRequester() }
    val rewindFocusRequester = remember { FocusRequester() }
    val forwardFocusRequester = remember { FocusRequester() }
    val qualityFocusRequester = remember { FocusRequester() }
    val timelineFocusRequester = remember { FocusRequester() }
    val firstMirrorFocusRequester = remember { FocusRequester() }
    val keepWatchingFocusRequester = remember { FocusRequester() }

    val isLiveStream = episodeUrl.contains("live", ignoreCase = true) ||
        episodeUrl.startsWith("football://", ignoreCase = true) ||
        episodeUrl.contains("fixture=", ignoreCase = true)

    // Keep screen on during active video playback
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // Trigger video extraction on entry
    LaunchedEffect(episodeUrl) {
        isWebPlayerMode = false
        playbackError = null
        failoverResumePositionMs = -1L
        isOledScreensaverActive = false
        showExitConfirmDialog = false
        pauseElapsedSeconds = 0
        viewModel.extractVideoUrl(episodeUrl)
    }

    // Whenever OSD appears, automatically focus the Play/Pause button so D-Pad works immediately!
    LaunchedEffect(showOsd, showExitConfirmDialog, isOledScreensaverActive) {
        if (showExitConfirmDialog) {
            delay(60)
            try {
                keepWatchingFocusRequester.requestFocus()
            } catch (_: Throwable) {}
        } else if (showOsd && !isOledScreensaverActive) {
            delay(60)
            try {
                playPauseFocusRequester.requestFocus()
            } catch (_: Throwable) {}
        }
    }

    // Auto-hide HUD message after 2.8 seconds
    LaunchedEffect(hudMessage) {
        if (hudMessage != null) {
            delay(2800)
            hudMessage = null
        }
    }

    // Auto-hide trick-play frame preview after 1.5 seconds
    LaunchedEffect(showTrickPlayPreview, trickPlayPreviewMs) {
        if (showTrickPlayPreview) {
            delay(1500)
            showTrickPlayPreview = false
        }
    }

    // OSD auto-hide timer (6 seconds of remote inactivity during playback)
    LaunchedEffect(showOsd, osdHideCounter, isWebPlayerMode, isPlaying, showExitConfirmDialog) {
        if (showOsd && isPlaying && !isWebPlayerMode && !isOledScreensaverActive && !showExitConfirmDialog) {
            delay(6000)
            showOsd = false
        }
    }

    // TrackSelector configured to default to High-Bitrate 1080p Full HD!
    val trackSelector = remember(context) {
        DefaultTrackSelector(context).apply {
            setParameters(
                buildUponParameters()
                    .setMinVideoSize(
                        VideoQualityPreset.FULL_HD_1080P.minWidth,
                        VideoQualityPreset.FULL_HD_1080P.minHeight
                    )
                    .setMaxVideoSize(
                        VideoQualityPreset.FULL_HD_1080P.maxWidth,
                        VideoQualityPreset.FULL_HD_1080P.maxHeight
                    )
                    .setMinVideoBitrate(VideoQualityPreset.FULL_HD_1080P.minBitrate)
                    .setForceHighestSupportedBitrate(true)
            )
        }
    }

    // Apply quality preset whenever user switches resolution
    LaunchedEffect(qualityPreset) {
        try {
            val paramsBuilder = trackSelector.buildUponParameters()
                .setMinVideoSize(qualityPreset.minWidth, qualityPreset.minHeight)
                .setMaxVideoSize(qualityPreset.maxWidth, qualityPreset.maxHeight)
                .setMinVideoBitrate(qualityPreset.minBitrate)
                .setMaxVideoBitrate(qualityPreset.maxBitrate)
                .setForceHighestSupportedBitrate(qualityPreset == VideoQualityPreset.FULL_HD_1080P)
            trackSelector.setParameters(paramsBuilder)
        } catch (_: Throwable) {}
    }

    // Initialize ExoPlayer with fast-start buffer settings (0.5s start buffer)
    val exoPlayer = remember(context) {
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                8000,  // minBufferMs: 8s
                40000, // maxBufferMs: 40s
                500,   // bufferForPlaybackMs: 0.5s for instant startup!
                1200   // bufferForPlaybackAfterRebufferMs: 1.2s for fast recovery
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        val renderersFactory = DefaultRenderersFactory(context)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
            .setEnableDecoderFallback(true)

        val httpDataSource = DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36")
            .setConnectTimeoutMs(6000)
            .setReadTimeoutMs(8000)
            .setAllowCrossProtocolRedirects(true)

        val smartDataSourceFactory = SmartHlsDataSource.Factory(httpDataSource)
        val mediaSourceFactory = DefaultMediaSourceFactory(smartDataSourceFactory)

        ExoPlayer.Builder(context, renderersFactory)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .setMediaSourceFactory(mediaSourceFactory)
            .setSeekBackIncrementMs(10000)
            .setSeekForwardIncrementMs(10000)
            .build().apply {
                playWhenReady = true
            }
    }

    val onUserInteractionOrWakeUp: () -> Boolean = {
        var consumedByScreensaver = false
        if (isOledScreensaverActive) {
            isOledScreensaverActive = false
            pauseElapsedSeconds = 0
            if (savedScreensaverPositionMs > 0L && !isLiveStream) {
                exoPlayer.seekTo(savedScreensaverPositionMs)
            }
            exoPlayer.play()
            hudMessage = null
            consumedByScreensaver = true
        }
        pauseElapsedSeconds = 0
        osdHideCounter++
        consumedByScreensaver
    }

    // Intelligent Failover Trigger helper
    val triggerMillisecondFailover: (String) -> Boolean = { reason ->
        val current = viewModel.selectedSource.value
        val backup = viewModel.findIntelligentFailoverSource(current)
        if (backup != null) {
            val exactMs = exoPlayer.currentPosition.coerceAtLeast(currentPositionMs)
            failoverResumePositionMs = exactMs
            hudMessage = "Switched to faster mirror: ${backup.name}"
            viewModel.selectSource(backup)
            true
        } else if (qualityPreset == VideoQualityPreset.FULL_HD_1080P) {
            // If no other server exists and 1080p is stalling, step down to 720p automatically
            qualityPreset = VideoQualityPreset.HD_720P
            hudMessage = "Switched to 720p HD for smoother playback"
            true
        } else {
            false
        }
    }

    // Continuous Buffer Watchdog: tracks buffering seconds and auto-recovers if stalled > 7s
    LaunchedEffect(isBuffering, selectedSource, isWebPlayerMode, isOledScreensaverActive) {
        if (isBuffering && !isWebPlayerMode && !isOledScreensaverActive) {
            bufferingSeconds = 0
            while (isActive && isBuffering) {
                delay(1000)
                bufferingSeconds += 1
                if (bufferingSeconds == 7 && exoPlayer.playbackState == Player.STATE_BUFFERING) {
                    triggerMillisecondFailover("Buffer Stall")
                }
            }
        } else {
            bufferingSeconds = 0
        }
    }

    // Silent 3-Minute Pause Timeout -> Pure #000000 Textless Screensaver
    LaunchedEffect(isPlaying, isOledScreensaverActive, isWebPlayerMode) {
        while (isActive) {
            delay(1000)
            if (isOledScreensaverActive) continue

            if (!isPlaying && !isWebPlayerMode) {
                pauseElapsedSeconds += 1
                if (pauseElapsedSeconds >= 180) {
                    savedScreensaverPositionMs = exoPlayer.currentPosition.coerceAtLeast(currentPositionMs)
                    exoPlayer.pause()
                    isOledScreensaverActive = true
                }
            } else {
                pauseElapsedSeconds = 0
            }
        }
    }

    LaunchedEffect(isOledScreensaverActive) {
        while (isActive && isOledScreensaverActive) {
            delay(18000)
            currentBackdropIndex = (currentBackdropIndex + 1) % MultiSourceEngine.OLED_CINEMATIC_BACKDROPS.size
        }
    }

    // Player event listener with intelligent failover & multi-part auto-advance
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = playbackState == Player.STATE_BUFFERING
                if (playbackState == Player.STATE_READY) {
                    playbackError = null
                    durationMs = exoPlayer.duration.coerceAtLeast(0L)

                    if (failoverResumePositionMs > 0L && !isLiveStream) {
                        val targetMs = failoverResumePositionMs
                        failoverResumePositionMs = -1L
                        exoPlayer.seekTo(targetMs)
                    }
                } else if (playbackState == Player.STATE_ENDED && !isLiveStream) {
                    val sources = viewModel.episodeSources.value
                    val current = viewModel.selectedSource.value
                    var advancedToNextPart = false
                    if (sources.size > 1 && current != null) {
                        val currentPart = current.partNumber
                            ?: Regex("Part\\s*(\\d+)", RegexOption.IGNORE_CASE).find(current.name)?.groupValues?.getOrNull(1)?.toIntOrNull()
                        if (currentPart != null) {
                            val nextPartSource = sources.firstOrNull {
                                val p = it.partNumber
                                    ?: Regex("Part\\s*(\\d+)", RegexOption.IGNORE_CASE).find(it.name)?.groupValues?.getOrNull(1)?.toIntOrNull()
                                p == currentPart + 1 && !it.name.contains("Web", ignoreCase = true)
                            }
                            if (nextPartSource != null) {
                                hudMessage = "Playing ${nextPartSource.name}"
                                viewModel.selectSource(nextPartSource)
                                advancedToNextPart = true
                            }
                        }
                    }
                    if (!advancedToNextPart) {
                        savedScreensaverPositionMs = 0L
                        isOledScreensaverActive = true
                    }
                }
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlayerError(error: PlaybackException) {
                playbackError = error.message ?: "Stream error"
                isBuffering = false

                if (triggerMillisecondFailover("Stream Error")) {
                    return
                }

                val sources = viewModel.episodeSources.value
                val current = viewModel.selectedSource.value
                if (sources.isNotEmpty() && current != null) {
                    val currentIdx = sources.indexOfFirst { it.url == current.url }
                    val remaining = if (currentIdx >= 0) sources.drop(currentIdx + 1) else emptyList()
                    val nextNative = remaining.firstOrNull {
                        it.url.contains(".m3u8", ignoreCase = true) ||
                            it.url.contains(".mp4", ignoreCase = true) ||
                            it.url.contains("vcdnx.com", ignoreCase = true) ||
                            it.url.contains("b-cdn.net", ignoreCase = true) ||
                            it.url.contains("vibrantpulselab.com", ignoreCase = true) ||
                            it.url.contains("live1channel", ignoreCase = true) ||
                            it.url.contains("live2channel", ignoreCase = true)
                    } ?: remaining.firstOrNull()

                    if (nextNative != null) {
                        failoverResumePositionMs = currentPositionMs
                        hudMessage = "Switching to: ${nextNative.name}"
                        viewModel.selectSource(nextNative)
                        return
                    }
                }

                val webSource = sources.firstOrNull { it.url.contains("http") && !it.url.contains(".m3u8") && !it.url.contains("vcdnx.com") }
                if (webSource != null) {
                    hudMessage = "Switching to Web Stream"
                    viewModel.selectSource(webSource)
                    isWebPlayerMode = true
                }
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            val pos = exoPlayer.currentPosition
            val dur = exoPlayer.duration
            if (pos > 0) {
                viewModel.savePlaybackProgress(episodeUrl, pos, dur)
            }
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    // Load media URL into ExoPlayer whenever selected source or videoUrlState changes
    LaunchedEffect(videoUrlState) {
        val url = (videoUrlState as? UiState.Success)?.data
        if (!url.isNullOrBlank()) {
            val isDirectStream = url.startsWith("file://") ||
                url.contains(".m3u8", ignoreCase = true) ||
                url.contains(".mp4", ignoreCase = true) ||
                url.contains(".mpd", ignoreCase = true) ||
                url.contains("vcdnx.com", ignoreCase = true) ||
                url.contains("b-cdn.net", ignoreCase = true) ||
                url.contains("vibrantpulselab.com", ignoreCase = true) ||
                url.contains("vimeocdn.com", ignoreCase = true) ||
                url.contains("okcdn.ru", ignoreCase = true) ||
                url.contains("live1channel", ignoreCase = true) ||
                url.contains("live2channel", ignoreCase = true)

            if (!isDirectStream) {
                exoPlayer.pause()
                isWebPlayerMode = true
                isBuffering = false
                return@LaunchedEffect
            }

            isWebPlayerMode = false
            isBuffering = true
            playbackError = null

            val uri = Uri.parse(url)
            val mediaItem = when {
                url.contains(".m3u8", ignoreCase = true) || url.contains("vcdnx.com/hls/", ignoreCase = true) -> {
                    MediaItem.Builder()
                        .setUri(uri)
                        .setMimeType(MimeTypes.APPLICATION_M3U8)
                        .build()
                }
                url.contains(".mpd", ignoreCase = true) -> {
                    MediaItem.Builder()
                        .setUri(uri)
                        .setMimeType(MimeTypes.APPLICATION_MPD)
                        .build()
                }
                else -> {
                    MediaItem.fromUri(uri)
                }
            }

            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()

            val currentSrcName = selectedSource?.name.orEmpty()
            val isMultiPart = currentSrcName.startsWith("Part ", ignoreCase = true) || selectedSource?.partNumber != null
            if (failoverResumePositionMs > 0L && !isLiveStream) {
                exoPlayer.seekTo(failoverResumePositionMs)
            } else if (!isLiveStream && !isMultiPart) {
                val savedPos = viewModel.getSavedPosition(episodeUrl)
                if (savedPos > 5000L) {
                    exoPlayer.seekTo(savedPos)
                    hudMessage = "Resumed from ${formatTimestamp(savedPos)}"
                }
            }

            exoPlayer.play()
        }
    }

    // Periodic time & watch progress update
    LaunchedEffect(exoPlayer) {
        while (isActive) {
            if (exoPlayer.isPlaying) {
                currentPositionMs = exoPlayer.currentPosition.coerceAtLeast(0L)
                durationMs = exoPlayer.duration.coerceAtLeast(0L)
                viewModel.savePlaybackProgress(episodeUrl, currentPositionMs, durationMs)
            }
            delay(500)
        }
    }

    // Back button interception:
    // 1. If screensaver is active -> wake up immediately
    // 2. If exit dialog is already open -> dismiss exit dialog
    // 3. Otherwise -> open the Netflix-style "Exit Video?" confirmation dialog instead of abruptly quitting!
    BackHandler {
        when {
            isOledScreensaverActive -> onUserInteractionOrWakeUp()
            showExitConfirmDialog -> showExitConfirmDialog = false
            else -> {
                showExitConfirmDialog = true
            }
        }
    }

    // TV Remote D-Pad Key Handling:
    // CRITICAL FIX: When showOsd is TRUE, NEVER consume DPAD_UP, DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT, or ENTER!
    // Let Compose's focus system move naturally between Play/Pause, Rewind, Forward, Quality, and Stream Mirrors!
    val handleKeyEvent: (KeyEvent) -> Boolean = { keyEvent ->
        val event = keyEvent.nativeKeyEvent
        if (event.action == android.view.KeyEvent.ACTION_DOWN) {
            if (onUserInteractionOrWakeUp()) {
                true
            } else if (showExitConfirmDialog) {
                false
            } else if (!showOsd) {
                // Controls are currently hidden: first D-Pad press reveals OSD and performs quick action
                when (event.keyCode) {
                    android.view.KeyEvent.KEYCODE_DPAD_CENTER,
                    android.view.KeyEvent.KEYCODE_ENTER,
                    android.view.KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                        showOsd = true
                        osdHideCounter++
                        true
                    }
                    android.view.KeyEvent.KEYCODE_DPAD_LEFT -> {
                        if (!isLiveStream) {
                            val newPos = (exoPlayer.currentPosition - 10000L).coerceAtLeast(0L)
                            exoPlayer.seekTo(newPos)
                            currentPositionMs = newPos
                            trickPlayPreviewMs = newPos
                            showTrickPlayPreview = true
                            hudMessage = "⏪ -10s (${formatTimestamp(newPos)})"
                        }
                        showOsd = true
                        osdHideCounter++
                        true
                    }
                    android.view.KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        if (!isLiveStream) {
                            val dur = exoPlayer.duration.coerceAtLeast(0L)
                            val target = if (dur > 0L) (exoPlayer.currentPosition + 10000L).coerceAtMost(dur) else exoPlayer.currentPosition + 10000L
                            exoPlayer.seekTo(target)
                            currentPositionMs = target
                            trickPlayPreviewMs = target
                            showTrickPlayPreview = true
                            hudMessage = "⏩ +10s (${formatTimestamp(target)})"
                        }
                        showOsd = true
                        osdHideCounter++
                        true
                    }
                    android.view.KeyEvent.KEYCODE_DPAD_UP,
                    android.view.KeyEvent.KEYCODE_DPAD_DOWN -> {
                        showOsd = true
                        osdHideCounter++
                        true
                    }
                    android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                        true
                    }
                    android.view.KeyEvent.KEYCODE_MEDIA_PLAY -> {
                        exoPlayer.play()
                        true
                    }
                    android.view.KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                        exoPlayer.pause()
                        true
                    }
                    android.view.KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                        if (!isLiveStream) {
                            val newPos = exoPlayer.currentPosition + 10000L
                            exoPlayer.seekTo(newPos)
                            currentPositionMs = newPos
                        }
                        true
                    }
                    android.view.KeyEvent.KEYCODE_MEDIA_REWIND -> {
                        if (!isLiveStream) {
                            val newPos = (exoPlayer.currentPosition - 10000L).coerceAtLeast(0L)
                            exoPlayer.seekTo(newPos)
                            currentPositionMs = newPos
                        }
                        true
                    }
                    else -> false
                }
            } else {
                // Controls ARE visible: reset inactivity timer and return FALSE so D-Pad moves focus!
                osdHideCounter++
                when (event.keyCode) {
                    android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                        true
                    }
                    android.view.KeyEvent.KEYCODE_MEDIA_PLAY -> {
                        exoPlayer.play()
                        true
                    }
                    android.view.KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                        exoPlayer.pause()
                        true
                    }
                    android.view.KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                        if (!isLiveStream) {
                            val newPos = exoPlayer.currentPosition + 10000L
                            exoPlayer.seekTo(newPos)
                            currentPositionMs = newPos
                        }
                        true
                    }
                    android.view.KeyEvent.KEYCODE_MEDIA_REWIND -> {
                        if (!isLiveStream) {
                            val newPos = (exoPlayer.currentPosition - 10000L).coerceAtLeast(0L)
                            exoPlayer.seekTo(newPos)
                            currentPositionMs = newPos
                        }
                        true
                    }
                    else -> false
                }
            }
        } else {
            false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .onPreviewKeyEvent(handleKeyEvent)
            .focusable(enabled = !showOsd && !showExitConfirmDialog)
            .pointerInput(isWebPlayerMode, isLiveStream, isOledScreensaverActive, showExitConfirmDialog) {
                if (!isWebPlayerMode && !showExitConfirmDialog) {
                    detectTapGestures(
                        onTap = {
                            if (!onUserInteractionOrWakeUp()) {
                                showOsd = !showOsd
                                osdHideCounter++
                            }
                        },
                        onDoubleTap = { offset ->
                            if (!onUserInteractionOrWakeUp() && !isLiveStream) {
                                val isLeftHalf = offset.x < size.width / 2f
                                if (isLeftHalf) {
                                    val newPos = (exoPlayer.currentPosition - 10000L).coerceAtLeast(0L)
                                    exoPlayer.seekTo(newPos)
                                    currentPositionMs = newPos
                                    trickPlayPreviewMs = newPos
                                    showTrickPlayPreview = true
                                    hudMessage = "⏪ -10s (${formatTimestamp(newPos)})"
                                } else {
                                    val dur = exoPlayer.duration.coerceAtLeast(0L)
                                    val target = if (dur > 0L) (exoPlayer.currentPosition + 10000L).coerceAtMost(dur) else exoPlayer.currentPosition + 10000L
                                    exoPlayer.seekTo(target)
                                    currentPositionMs = target
                                    trickPlayPreviewMs = target
                                    showTrickPlayPreview = true
                                    hudMessage = "⏩ +10s (${formatTimestamp(target)})"
                                }
                            }
                        }
                    )
                }
            }
            .pointerInput(isWebPlayerMode, isLiveStream, isOledScreensaverActive, showExitConfirmDialog) {
                if (!isWebPlayerMode && !showExitConfirmDialog) {
                    detectDragGestures { change, dragAmount ->
                        if (onUserInteractionOrWakeUp()) return@detectDragGestures
                        val dx = dragAmount.x
                        val dy = dragAmount.y
                        if (abs(dx) > abs(dy) && abs(dx) > 6f && !isLiveStream) {
                            val deltaMs = (dx * 120f).toLong()
                            val dur = exoPlayer.duration.coerceAtLeast(3600_000L)
                            val target = (exoPlayer.currentPosition + deltaMs).coerceIn(0L, dur)
                            exoPlayer.seekTo(target)
                            currentPositionMs = target
                            trickPlayPreviewMs = target
                            showTrickPlayPreview = true
                        } else if (abs(dy) > abs(dx) && abs(dy) > 6f) {
                            val xPos = change.position.x
                            if (xPos < size.width * 0.35f) {
                                screenBrightness = (screenBrightness - dy / 600f).coerceIn(0.10f, 1.0f)
                                (context as? Activity)?.window?.let { win ->
                                    val lp = win.attributes
                                    lp.screenBrightness = screenBrightness
                                    win.attributes = lp
                                }
                                hudMessage = "☀ Brightness: ${(screenBrightness * 100).roundToInt()}%"
                            } else if (xPos > size.width * 0.65f) {
                                playerVolume = (playerVolume - dy / 600f).coerceIn(0.0f, 1.0f)
                                exoPlayer.volume = playerVolume
                                try {
                                    val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                                    if (am != null) {
                                        val maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                                        am.setStreamVolume(AudioManager.STREAM_MUSIC, (playerVolume * maxVol).roundToInt(), 0)
                                    }
                                } catch (_: Throwable) {}
                                hudMessage = "🔊 Volume: ${(playerVolume * 100).roundToInt()}%"
                            }
                        }
                    }
                }
            }
            .testTag("video_player_root")
    ) {
        // Video Rendering Layer
        var activeWebView by remember { mutableStateOf<WebView?>(null) }
        if (isWebPlayerMode) {
            val currentWebUrl = (videoUrlState as? UiState.Success)?.data ?: episodeUrl
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        activeWebView = this
                        isFocusable = false
                        isFocusableInTouchMode = false
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setBackgroundColor(android.graphics.Color.BLACK)
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            mediaPlaybackRequiresUserGesture = false
                            loadsImagesAutomatically = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36"
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            setSupportMultipleWindows(true)
                            javaScriptCanOpenWindowsAutomatically = false
                        }
                        webChromeClient = object : WebChromeClient() {
                            override fun onCreateWindow(
                                view: WebView?,
                                isDialog: Boolean,
                                isUserGesture: Boolean,
                                resultMsg: android.os.Message?
                            ): Boolean = false
                        }
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                val reqUrl = request?.url?.toString().orEmpty()
                                val allowed = reqUrl.contains("embed.st", ignoreCase = true) ||
                                    reqUrl.contains("taifood-blog.asia", ignoreCase = true) ||
                                    reqUrl.contains("streamed.st", ignoreCase = true) ||
                                    reqUrl.contains("ppv.st", ignoreCase = true) ||
                                    reqUrl.contains("1tamilcrow", ignoreCase = true) ||
                                    reqUrl.contains("tamildhool", ignoreCase = true) ||
                                    reqUrl.contains("dailymotion", ignoreCase = true) ||
                                    reqUrl.contains("vimeo", ignoreCase = true) ||
                                    reqUrl.contains("ok.ru", ignoreCase = true)
                                return if (request?.isForMainFrame == true && !allowed) true else false
                            }

                            override fun shouldInterceptRequest(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): WebResourceResponse? {
                                val reqUrl = request?.url?.toString().orEmpty().lowercase(Locale.US)
                                val isAdScript = reqUrl.contains("acscdn.com") ||
                                    reqUrl.contains("aclib") ||
                                    reqUrl.contains("optimserve") ||
                                    reqUrl.contains("wpnxiswpuyrfn") ||
                                    reqUrl.contains("histats") ||
                                    reqUrl.contains("doubleclick") ||
                                    reqUrl.contains("googlesyndication") ||
                                    reqUrl.contains("popads") ||
                                    reqUrl.contains("adsterra") ||
                                    reqUrl.contains("exoclick") ||
                                    reqUrl.endsWith("/ad.html") ||
                                    reqUrl.endsWith("/d3.php")
                                if (isAdScript) {
                                    return WebResourceResponse(
                                        "application/javascript",
                                        "UTF-8",
                                        ByteArrayInputStream(ByteArray(0))
                                    )
                                }
                                return super.shouldInterceptRequest(view, request)
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                view?.evaluateJavascript(
                                    """
                                    (function() {
                                        var sad = document.getElementById('sad');
                                        if (sad) sad.remove();
                                        window.open = function() { return null; };
                                        var v = document.querySelector('video');
                                        if (v) { v.muted = false; v.play().catch(function(){}); }
                                    })();
                                    """.trimIndent(),
                                    null
                                )
                            }
                        }
                        loadWebStream(this, currentWebUrl)
                    }
                },
                update = { webView ->
                    activeWebView = webView
                    webView.isFocusable = false
                    webView.isFocusableInTouchMode = false
                    if (webView.tag != currentWebUrl) {
                        loadWebStream(webView, currentWebUrl)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .focusProperties { canFocus = false }
            )
        } else {
            val exoResizeMode = when (resizeModeIndex) {
                1 -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                2 -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                else -> AspectRatioFrameLayout.RESIZE_MODE_FIT
            }
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        resizeMode = exoResizeMode
                        isFocusable = false
                        isFocusableInTouchMode = false
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        keepScreenOn = true
                    }
                },
                update = { playerView ->
                    playerView.resizeMode = exoResizeMode
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top-Center Heads Up Display (HUD) Pill
        AnimatedVisibility(
            visible = hudMessage != null,
            enter = fadeIn() + slideInVertically { -40 },
            exit = fadeOut() + slideOutVertically { -40 },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp)
        ) {
            Surface(
                color = Color(0xEE141824),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.25f)),
                shadowElevation = 10.dp
            ) {
                Text(
                    text = hudMessage.orEmpty(),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp)
                )
            }
        }

        // Trick-Play Visual Scrubber Overlay
        AnimatedVisibility(
            visible = showTrickPlayPreview && !isLiveStream,
            enter = fadeIn(tween(100)),
            exit = fadeOut(tween(150)),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 90.dp)
        ) {
            Surface(
                color = Color(0xEE0D111A),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Color.White)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = formatTimestamp(trickPlayPreviewMs),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Center Loading / Buffering Spinner with Smart Anti-Buffer Recovery Buttons
        if (isBuffering && playbackError == null && !isWebPlayerMode && !isOledScreensaverActive) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .background(Color(0xBB000000), RoundedCornerShape(20.dp))
                        .padding(horizontal = 28.dp, vertical = 22.dp)
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFE50914),
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (videoUrlState is UiState.Loading) {
                            "Finding Fastest ${qualityPreset.badge} Stream..."
                        } else {
                            "Loading ${qualityPreset.badge} Video..."
                        },
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (bufferingSeconds >= 3) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            TvControlPillButton(
                                label = "Switch Mirror",
                                icon = Icons.Filled.SwapHoriz,
                                isPrimary = true,
                                onClick = {
                                    triggerMillisecondFailover("Manual Switch")
                                }
                            )
                            if (qualityPreset != VideoQualityPreset.SMOOTH_480P) {
                                TvControlPillButton(
                                    label = "Switch to 480p Fast",
                                    icon = Icons.Filled.Speed,
                                    onClick = {
                                        qualityPreset = VideoQualityPreset.SMOOTH_480P
                                        hudMessage = "Switched to 480p Fast (No-Buffer Mode)"
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Center Playback Error Card with Immediate Server Switcher
        if (playbackError != null && !isBuffering && !isWebPlayerMode && !isOledScreensaverActive) {
            Surface(
                color = Color(0xF212151E),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.25f)),
                modifier = Modifier
                    .align(Alignment.Center)
                    .widthIn(max = 460.dp)
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.SwapHoriz,
                        contentDescription = "Switch Server",
                        tint = Color(0xFFE50914),
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Server Busy — Try Backup Mirror",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Select another stream mirror below to continue watching immediately.",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TvControlPillButton(
                            label = "Try Next Server",
                            icon = Icons.Filled.PlayArrow,
                            isPrimary = true,
                            onClick = {
                                if (!triggerMillisecondFailover("Manual Switch")) {
                                    val current = selectedSource
                                    val next = episodeSources.firstOrNull { it.url != current?.url } ?: episodeSources.firstOrNull()
                                    if (next != null) {
                                        failoverResumePositionMs = currentPositionMs
                                        viewModel.selectSource(next)
                                    } else {
                                        exoPlayer.prepare()
                                        exoPlayer.play()
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        TvControlPillButton(
                            label = "Exit Video",
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            onClick = onBack,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Netflix TV OSD Overlay (Unobstructed D-Pad Focus Graph!)
        AnimatedVisibility(
            visible = showOsd && !isOledScreensaverActive && !showExitConfirmDialog,
            enter = fadeIn(tween(180)),
            exit = fadeOut(tween(180)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xCC000000),
                                Color(0x22000000),
                                Color(0xEE000000)
                            )
                        )
                    )
                    .padding(horizontal = 28.dp, vertical = 22.dp)
            ) {
                // Top Header Bar: Back Button + Title + Current Quality Badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        TvPlayerIconButton(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            onClick = { showExitConfirmDialog = true },
                            modifier = Modifier
                                .focusRequester(backButtonFocusRequester)
                                .focusProperties {
                                    down = if (episodeSources.isNotEmpty()) firstMirrorFocusRequester else playPauseFocusRequester
                                }
                                .testTag("player_back_button")
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = episodeTitle.ifBlank { "Now Playing" },
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = Color(0xFFE50914),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = if (isLiveStream) "LIVE HD" else qualityPreset.badge,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                                selectedSource?.let { src ->
                                    Text(
                                        text = src.name,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }

                    // Top-Right Screensaver & Web/Native Switcher (Preserving testTags)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TvControlPillButton(
                            label = resizeModeLabel,
                            icon = Icons.Filled.AspectRatio,
                            onClick = {
                                resizeModeIndex = (resizeModeIndex + 1) % 3
                                hudMessage = "Screen: $resizeModeLabel"
                                osdHideCounter++
                            },
                            modifier = Modifier
                                .focusProperties {
                                    down = if (episodeSources.isNotEmpty()) firstMirrorFocusRequester else playPauseFocusRequester
                                }
                                .testTag("aspect_ratio_button")
                        )

                        TvControlPillButton(
                            label = "Dim Screen",
                            icon = Icons.Filled.DarkMode,
                            onClick = {
                                savedScreensaverPositionMs = exoPlayer.currentPosition.coerceAtLeast(currentPositionMs)
                                exoPlayer.pause()
                                hudMessage = null
                                isOledScreensaverActive = true
                            },
                            modifier = Modifier
                                .focusProperties {
                                    down = if (episodeSources.isNotEmpty()) firstMirrorFocusRequester else playPauseFocusRequester
                                }
                                .testTag("oled_guard_button")
                        )
                    }
                }

                // Bottom Netflix TV Control Deck:
                // Row A (Top of bottom deck): Stream Mirrors & Episode Parts (Press UP from Timeline/Play to reach!)
                // Row B (Middle): D-Pad Scrubbable Timeline Progress Bar
                // Row C (Bottom): Rewind 10s • Play/Pause (Default Focus) • Forward 10s • Quality Selector (1080p/720p/480p)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    if (episodeSources.isNotEmpty()) {
                        val hasParts = episodeSources.any { it.name.startsWith("Part ", ignoreCase = true) || it.partNumber != null }
                        Text(
                            text = if (hasParts) "EPISODE PARTS & SERVERS (Press UP / DOWN to navigate)" else "STREAM SERVERS (Switch if slow)",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(episodeSources) { index, source ->
                                val isSelected = source.url == selectedSource?.url && source.name == selectedSource?.name
                                var isServerFocused by remember { mutableStateOf(false) }

                                Surface(
                                    onClick = {
                                        val currentPart = selectedSource?.partNumber
                                        val nextPart = source.partNumber
                                        if (currentPart == nextPart && currentPositionMs > 0L && !isLiveStream) {
                                            failoverResumePositionMs = currentPositionMs
                                        }
                                        viewModel.selectSource(source)
                                        hudMessage = "Switched to ${source.name}"
                                        osdHideCounter++
                                    },
                                    color = when {
                                        isServerFocused -> Color.White
                                        isSelected -> Color(0xFFE50914)
                                        else -> Color(0xD9181C28)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(
                                        width = if (isServerFocused) 2.5.dp else 1.dp,
                                        color = when {
                                            isServerFocused -> Color.White
                                            isSelected -> Color(0xFFE50914)
                                            else -> Color.White.copy(alpha = 0.25f)
                                        }
                                    ),
                                    modifier = Modifier
                                        .then(
                                            if (index == 0) Modifier.focusRequester(firstMirrorFocusRequester) else Modifier
                                        )
                                        .focusProperties {
                                            up = backButtonFocusRequester
                                            down = if (!isLiveStream && durationMs > 0L) timelineFocusRequester else playPauseFocusRequester
                                        }
                                        .graphicsLayer {
                                            scaleX = if (isServerFocused) 1.07f else 1.0f
                                            scaleY = if (isServerFocused) 1.07f else 1.0f
                                        }
                                        .onFocusChanged {
                                            isServerFocused = it.isFocused
                                            if (it.isFocused) osdHideCounter++
                                        }
                                        .testTag("source_button_${source.name.lowercase().replace(" ", "_")}")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Filled.PlayCircleOutline,
                                            contentDescription = null,
                                            tint = if (isServerFocused) Color.Black else Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(7.dp))
                                        Text(
                                            text = source.name,
                                            color = if (isServerFocused) Color.Black else Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // D-Pad Friendly Netflix Timeline Scrubber (Left/Right seeks -10s/+10s, Up goes to Mirrors, Down goes to Play/Pause)
                    if (!isWebPlayerMode && !isLiveStream && durationMs > 0L) {
                        var isTimelineFocused by remember { mutableStateOf(false) }
                        val progressFraction = (currentPositionMs.toFloat() / durationMs.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(timelineFocusRequester)
                                .focusProperties {
                                    up = if (episodeSources.isNotEmpty()) firstMirrorFocusRequester else backButtonFocusRequester
                                    down = playPauseFocusRequester
                                }
                                .onFocusChanged {
                                    isTimelineFocused = it.isFocused
                                    if (it.isFocused) osdHideCounter++
                                }
                                .onKeyEvent { keyEvent ->
                                    if (keyEvent.nativeKeyEvent.action == android.view.KeyEvent.ACTION_DOWN) {
                                        when (keyEvent.nativeKeyEvent.keyCode) {
                                            android.view.KeyEvent.KEYCODE_DPAD_LEFT -> {
                                                val newPos = (exoPlayer.currentPosition - 10000L).coerceAtLeast(0L)
                                                exoPlayer.seekTo(newPos)
                                                currentPositionMs = newPos
                                                trickPlayPreviewMs = newPos
                                                showTrickPlayPreview = true
                                                osdHideCounter++
                                                true
                                            }
                                            android.view.KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                                val target = (exoPlayer.currentPosition + 10000L).coerceAtMost(durationMs)
                                                exoPlayer.seekTo(target)
                                                currentPositionMs = target
                                                trickPlayPreviewMs = target
                                                showTrickPlayPreview = true
                                                osdHideCounter++
                                                true
                                            }
                                            else -> false
                                        }
                                    } else {
                                        false
                                    }
                                }
                                .focusable()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatTimestamp(currentPositionMs),
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(if (isTimelineFocused) 10.dp else 6.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.White.copy(alpha = 0.28f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progressFraction)
                                        .fillMaxHeight()
                                        .background(
                                            if (isTimelineFocused) Color.White else Color(0xFFE50914)
                                        )
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = formatTimestamp(durationMs),
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Primary Transport Controls Row (Always visible & focusable in both Native and Web Player modes!)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!isLiveStream) {
                                TvTransportButton(
                                    icon = Icons.Filled.Replay10,
                                    label = "-10s",
                                    onClick = {
                                        if (isWebPlayerMode) {
                                            activeWebView?.evaluateJavascript(
                                                "(function(){var v=document.querySelector('video');if(v)v.currentTime=Math.max(0,v.currentTime-10);})();",
                                                null
                                            )
                                        } else {
                                            val newPos = (exoPlayer.currentPosition - 10000L).coerceAtLeast(0L)
                                            exoPlayer.seekTo(newPos)
                                            currentPositionMs = newPos
                                            trickPlayPreviewMs = newPos
                                            showTrickPlayPreview = true
                                        }
                                        hudMessage = "⏪ -10s"
                                        osdHideCounter++
                                    },
                                    modifier = Modifier
                                        .focusRequester(rewindFocusRequester)
                                        .focusProperties {
                                            up = if (!isWebPlayerMode && durationMs > 0L) timelineFocusRequester else if (episodeSources.isNotEmpty()) firstMirrorFocusRequester else backButtonFocusRequester
                                            right = playPauseFocusRequester
                                        }
                                        .testTag("rewind_10_button")
                                )
                            }

                            TvTransportButton(
                                icon = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                label = if (isPlaying) "Pause" else "Play",
                                isPrimary = true,
                                onClick = {
                                    if (isWebPlayerMode) {
                                        isPlaying = !isPlaying
                                        activeWebView?.evaluateJavascript(
                                            "(function(){var v=document.querySelector('video');if(v){if(v.paused)v.play();else v.pause();}})();",
                                            null
                                        )
                                    } else {
                                        if (exoPlayer.isPlaying) {
                                            exoPlayer.pause()
                                        } else {
                                            exoPlayer.play()
                                        }
                                    }
                                    osdHideCounter++
                                },
                                modifier = Modifier
                                    .focusRequester(playPauseFocusRequester)
                                    .focusProperties {
                                        up = if (!isWebPlayerMode && !isLiveStream && durationMs > 0L) {
                                            timelineFocusRequester
                                        } else if (episodeSources.isNotEmpty()) {
                                            firstMirrorFocusRequester
                                        } else {
                                            backButtonFocusRequester
                                        }
                                        if (!isLiveStream) {
                                            left = rewindFocusRequester
                                            right = forwardFocusRequester
                                        } else {
                                            right = qualityFocusRequester
                                        }
                                    }
                                    .testTag("play_pause_button")
                            )

                            if (!isLiveStream) {
                                TvTransportButton(
                                    icon = Icons.Filled.Forward10,
                                    label = "+10s",
                                    onClick = {
                                        if (isWebPlayerMode) {
                                            activeWebView?.evaluateJavascript(
                                                "(function(){var v=document.querySelector('video');if(v)v.currentTime=v.currentTime+10;})();",
                                                null
                                            )
                                        } else {
                                            val dur = exoPlayer.duration.coerceAtLeast(0L)
                                            val target = if (dur > 0L) (exoPlayer.currentPosition + 10000L).coerceAtMost(dur) else exoPlayer.currentPosition + 10000L
                                            exoPlayer.seekTo(target)
                                            currentPositionMs = target
                                            trickPlayPreviewMs = target
                                            showTrickPlayPreview = true
                                        }
                                        hudMessage = "⏩ +10s"
                                        osdHideCounter++
                                    },
                                    modifier = Modifier
                                        .focusRequester(forwardFocusRequester)
                                        .focusProperties {
                                            up = if (!isWebPlayerMode && durationMs > 0L) timelineFocusRequester else if (episodeSources.isNotEmpty()) firstMirrorFocusRequester else backButtonFocusRequester
                                            left = playPauseFocusRequester
                                            right = qualityFocusRequester
                                        }
                                        .testTag("forward_10_button")
                                )
                            }
                        }

                        // User Resolution / Quality Switcher (Defaults to 1080p Full HD!)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TvControlPillButton(
                                label = "Quality: ${qualityPreset.label}",
                                icon = Icons.Filled.HighQuality,
                                onClick = {
                                    val presets = VideoQualityPreset.entries
                                    val nextIdx = (presets.indexOf(qualityPreset) + 1) % presets.size
                                    qualityPreset = presets[nextIdx]
                                    hudMessage = "Video Quality: ${qualityPreset.label}"
                                    osdHideCounter++
                                },
                                modifier = Modifier
                                    .focusRequester(qualityFocusRequester)
                                    .focusProperties {
                                        up = if (!isWebPlayerMode && !isLiveStream && durationMs > 0L) timelineFocusRequester else if (episodeSources.isNotEmpty()) firstMirrorFocusRequester else backButtonFocusRequester
                                        left = if (!isLiveStream) forwardFocusRequester else playPauseFocusRequester
                                    }
                                    .testTag("quality_selector_button")
                            )
                        }
                    }
                }
            }
        }

        // Netflix TV Style "Exit Video?" Confirmation Dialog when pressing Back
        AnimatedVisibility(
            visible = showExitConfirmDialog,
            enter = fadeIn(tween(150)),
            exit = fadeOut(tween(120)),
            modifier = Modifier
                .fillMaxSize()
                .testTag("exit_confirmation_dialog")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xD9000000))
                    .clickable { showExitConfirmDialog = false },
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = Color(0xFF141824),
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.22f)),
                    modifier = Modifier
                        .widthIn(min = 340.dp, max = 460.dp)
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(28.dp)
                    ) {
                        Text(
                            text = "Exit Video?",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Do you want to stop watching and return to the show catalog?",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TvControlPillButton(
                                label = "Keep Watching",
                                icon = Icons.Filled.PlayArrow,
                                isPrimary = true,
                                onClick = { showExitConfirmDialog = false },
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(keepWatchingFocusRequester)
                                    .testTag("exit_dialog_keep_watching_btn")
                            )
                            TvControlPillButton(
                                label = "Exit Video",
                                icon = Icons.AutoMirrored.Filled.ArrowBack,
                                onClick = {
                                    showExitConfirmDialog = false
                                    onBack()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("exit_dialog_confirm_exit_btn")
                            )
                        }
                    }
                }
            }
        }

        // Silent, Textless Screensaver
        AnimatedVisibility(
            visible = isOledScreensaverActive,
            enter = fadeIn(tween(800)),
            exit = fadeOut(tween(180)),
            modifier = Modifier
                .fillMaxSize()
                .testTag("oled_stage2_screensaver")
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "silent_slow_pan")
            val panOffsetX by infiniteTransition.animateFloat(
                initialValue = -28f,
                targetValue = 28f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 45000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pan_x"
            )
            val panScale by infiniteTransition.animateFloat(
                initialValue = 1.04f,
                targetValue = 1.12f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 45000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pan_scale"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable {
                        onUserInteractionOrWakeUp()
                    }
            ) {
                AsyncImage(
                    model = MultiSourceEngine.OLED_CINEMATIC_BACKDROPS[currentBackdropIndex],
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = panScale
                            scaleY = panScale
                            translationX = panOffsetX
                            alpha = 0.16f
                        }
                )
            }
        }
    }
}

@Composable
private fun TvTransportButton(
    icon: ImageVector,
    label: String,
    isPrimary: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = when {
            isFocused -> Color.White
            isPrimary -> Color(0xFFE50914)
            else -> Color(0xCC1A2030)
        },
        border = BorderStroke(
            width = if (isFocused) 2.5.dp else 1.dp,
            color = if (isFocused) Color.White else Color.White.copy(alpha = 0.25f)
        ),
        modifier = modifier
            .graphicsLayer {
                scaleX = if (isFocused) 1.10f else 1.0f
                scaleY = if (isFocused) 1.10f else 1.0f
            }
            .onFocusChanged { isFocused = it.isFocused }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 11.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isFocused) Color.Black else Color.White,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = if (isFocused) Color.Black else Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TvControlPillButton(
    label: String,
    icon: ImageVector,
    isPrimary: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = when {
            isFocused -> Color.White
            isPrimary -> Color(0xFFE50914)
            else -> Color(0xCC1A2030)
        },
        border = BorderStroke(
            width = if (isFocused) 2.5.dp else 1.dp,
            color = if (isFocused) Color.White else Color.White.copy(alpha = 0.22f)
        ),
        modifier = modifier
            .graphicsLayer {
                scaleX = if (isFocused) 1.07f else 1.0f
                scaleY = if (isFocused) 1.07f else 1.0f
            }
            .onFocusChanged { isFocused = it.isFocused }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isFocused) Color.Black else Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = if (isFocused) Color.Black else Color.White,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

private fun loadWebStream(webView: WebView, url: String) {
    webView.tag = url
    if (url.contains(".m3u8", ignoreCase = true) || url.contains(".mp4", ignoreCase = true)) {
        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <script src="https://cdn.jsdelivr.net/npm/hls.js@latest"></script>
                <style>
                    html, body { margin: 0; padding: 0; width: 100%; height: 100%; background: #000; overflow: hidden; display: flex; align-items: center; justify-content: center; }
                    video { width: 100%; height: 100%; object-fit: contain; background: #000; }
                </style>
            </head>
            <body>
                <video id="video" controls autoplay playsinline></video>
                <script>
                    var video = document.getElementById('video');
                    var src = "$url";
                    if (Hls.isSupported() && src.indexOf('.m3u8') !== -1) {
                        var hls = new Hls({ enableWorker: true, lowLatencyMode: true });
                        hls.loadSource(src);
                        hls.attachMedia(video);
                        hls.on(Hls.Events.MANIFEST_PARSED, function() {
                            video.play().catch(function(){});
                        });
                    } else {
                        video.src = src;
                        video.play().catch(function(){});
                    }
                </script>
            </body>
            </html>
        """.trimIndent()
        webView.loadDataWithBaseURL("https://www.1tamilcrow.net/", html, "text/html", "UTF-8", null)
    } else {
        val referer = when {
            url.contains("embed.st", ignoreCase = true) || url.contains("streamed.st", ignoreCase = true) ->
                "https://streamed.st/"
            url.contains("taifood-blog.asia", ignoreCase = true) || url.contains("ppv.st", ignoreCase = true) ->
                "https://ppv.st/"
            else ->
                "https://www.1tamilcrow.net/"
        }
        webView.loadUrl(url, mapOf("Referer" to referer))
    }
}

@Composable
fun TvPlayerIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (isFocused) Color.White else Color(0xCC1A2030),
        border = BorderStroke(
            width = if (isFocused) 2.5.dp else 1.dp,
            color = if (isFocused) Color.White else Color.White.copy(alpha = 0.25f)
        ),
        modifier = modifier
            .size(46.dp)
            .graphicsLayer {
                scaleX = if (isFocused) 1.10f else 1.0f
                scaleY = if (isFocused) 1.10f else 1.0f
            }
            .onFocusChanged { isFocused = it.isFocused }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (isFocused) Color.Black else Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

private fun formatTimestamp(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0L)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}
