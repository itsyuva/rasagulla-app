package com.example.ui.screens

import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Hidden Developer Signature Easter Egg Overlay:
 * Triggered ONLY when tapping or pressing OK on the Rasagulla logo 5 times in rapid succession.
 *
 * - Visual: Looping 8-bit/16-bit pixelated sprite animation of Thalapathy Vijay doing the
 *   iconic "Vaathi Coming" shoulder-drop hook step, rendered with FilterQuality.None
 *   (nearest-neighbor / image-rendering: pixelated) so pixel edges stay razor-sharp.
 * - Text: "Created by Yuva" placed cleanly underneath the dancing sprite.
 * - Audio: Iconic Master title card / Vaathi Coming 8-bit chiptune synth BGM at ~28% volume
 *   (25-30% range) with a smooth 300ms fade-in and 300ms fade-out.
 * - Dismissal: Smooth spring animation on reveal. Auto-dismisses and stops audio cleanly
 *   after 4 seconds (4000ms), or immediately on any remote click or screen tap.
 */
@Composable
fun DeveloperEasterEggOverlay(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val currentOnDismiss by rememberUpdatedState(onDismiss)

    // Auto-dismiss after 4 real seconds (4000ms) and manage 8-bit Master BGM synth lifecycle
    DisposableEffect(visible) {
        val mainHandler = Handler(Looper.getMainLooper())
        val scheduler = if (visible) {
            Executors.newSingleThreadScheduledExecutor().also { exec ->
                exec.schedule(
                    { mainHandler.post { currentOnDismiss() } },
                    4000L,
                    TimeUnit.MILLISECONDS
                )
            }
        } else {
            null
        }
        val bgmController = if (visible) {
            MasterChiptuneBgmController().also { it.startWithFadeIn() }
        } else {
            null
        }
        onDispose {
            scheduler?.shutdownNow()
            bgmController?.stopWithFadeOut()
        }
    }

    LaunchedEffect(visible) {
        if (visible) {
            runCatching { focusRequester.requestFocus() }
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ) + scaleIn(
            initialScale = 0.82f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ),
        exit = fadeOut(animationSpec = tween(durationMillis = 220)) +
            scaleOut(targetScale = 0.90f, animationSpec = tween(durationMillis = 220))
    ) {
        val interactionSource = remember { MutableInteractionSource() }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xD9030407))
                .focusRequester(focusRequester)
                .focusable()
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown) {
                        onDismiss()
                        true
                    } else {
                        false
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Full-screen tap catcher to dismiss immediately on any screen tap
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        onDismiss()
                    }
                    .testTag("developer_easter_egg_overlay")
            )

            val springScale by animateFloatAsState(
                targetValue = if (visible) 1f else 0.86f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "easter_egg_card_spring"
            )

            Surface(
                color = Color(0xEB0B0E17),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, Color(0x38FFCC4D)),
                shadowElevation = 24.dp,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = springScale
                        scaleY = springScale
                    }
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 36.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ThalapathyPixelHookStepSprite(
                        modifier = Modifier
                            .size(156.dp)
                            .testTag("vijay_pixel_sprite")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Created by Yuva",
                        color = Color(0xFFF5F7FA),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.2.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("easter_egg_credit_text")
                    )
                }
            }
        }
    }
}

/**
 * Looping 8-bit/16-bit pixelated sprite animation of Thalapathy Vijay doing the iconic
 * "Vaathi Coming" shoulder-drop hook step.
 *
 * Rendered via native low-res pixel-matrix Bitmaps drawn with `FilterQuality.None`
 * (equivalent to CSS `image-rendering: pixelated`) so every pixel edge stays razor-sharp.
 */
@Composable
fun ThalapathyPixelHookStepSprite(
    modifier: Modifier = Modifier
) {
    val spriteFrames = remember { buildThalapathyHookStepBitmaps() }
    val infiniteTransition = rememberInfiniteTransition(label = "vijay_pixel_hook_step")

    val rawFrame by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = spriteFrames.size.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 840, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sprite_frame_idx"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.14f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(560, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_alpha"
    )

    val frameIndex = rawFrame.toInt().coerceIn(0, spriteFrames.lastIndex)

    Canvas(modifier = modifier) {
        drawCircle(
            color = Color(0xFFFFB703).copy(alpha = glowAlpha),
            radius = size.minDimension * 0.42f
        )

        val currentBitmap = spriteFrames[frameIndex]
        val dstWidth = size.width.roundToInt().coerceAtLeast(24)
        val dstHeight = size.height.roundToInt().coerceAtLeast(24)

        // FilterQuality.None enforces nearest-neighbor scaling (image-rendering: pixelated)
        drawImage(
            image = currentBitmap,
            srcOffset = IntOffset.Zero,
            srcSize = IntSize(currentBitmap.width, currentBitmap.height),
            dstOffset = IntOffset.Zero,
            dstSize = IntSize(dstWidth, dstHeight),
            filterQuality = FilterQuality.None
        )
    }
}

private fun buildThalapathyHookStepBitmaps(): List<androidx.compose.ui.graphics.ImageBitmap> {
    val palette = mapOf(
        '.' to 0x00000000,
        'H' to 0xFF141218.toInt(),
        'h' to 0xFF2B2634.toInt(),
        'S' to 0xFFD99B66.toInt(),
        's' to 0xFFB87A49.toInt(),
        'B' to 0xFF1E1A22.toInt(),
        'G' to 0xFF0B0C10.toInt(),
        'g' to 0xFFFFD166.toInt(),
        'W' to 0xFFFFFFFF.toInt(),
        'M' to 0xFF9E1B32.toInt(),
        'm' to 0xFF6E1022.toInt(),
        'Y' to 0xFFFFB703.toInt(),
        'V' to 0xFFF2F4F8.toInt(),
        'v' to 0xFFCFD6E2.toInt(),
        'K' to 0xFF20242E.toInt()
    )

    val frame0 = listOf(
        "........................",
        "........HHHHHH..........",
        ".......HHhHHHHH.........",
        "......HHHHHHHHHH........",
        "......HSSGGGGSSH........",
        "......HSGgGGgGSH........",
        ".......SSBBBBSS.........",
        "........SBWWBS..........",
        ".........SSSS...........",
        "..........ggMMMm........",
        "....MMMMMggMMMMMM.......",
        "...MMYMMMMMMMMYMMM......",
        "...MMmMMMMMMMMMMSS......",
        "...SS.MMMMMMMMM.SS......",
        "...g..MMYMMMYMM.........",
        "......MMMMMMMMM.........",
        "......VVVVVVVVV.........",
        "......VVvVVVvVV.........",
        ".....VVVVgVVVVV.........",
        ".....VVVV..VVVV.........",
        ".....VVV....VVV.........",
        "....SSS......SSS........",
        "...KKKK......KKKK.......",
        "........................"
    )

    val frame1 = listOf(
        "........................",
        "..Y......HHHHHH.........",
        "...Y....HHhHHHHH........",
        ".......HHHHHHHHHH.......",
        ".......HSSGGGGSSH.......",
        ".......HSGWGGgGSH.......",
        "........SSBBBBSS........",
        ".........SBWWBS.........",
        "..........SSSS..........",
        "...........ggMMMMm......",
        "...MMMMMMggMMMMMMM......",
        "..MMMYMMMMMMMMYMMSS.....",
        "..SS.mMMMMMMMMMM.SS.....",
        "..g...MMMMMMMMM.........",
        "......MMYMMMYMM.........",
        "......MMMMMMMMM.........",
        ".....VVVVVVVVVV.........",
        ".....VVVvVVVvVV.........",
        "....VVVVVgVVVVV.........",
        "....VVVV...VVVV.........",
        "....VVV.....VVV.........",
        "...SSS.......SSS........",
        "..KKKK.......KKKK.......",
        "........................"
    )

    val frame2 = listOf(
        "........................",
        "........HHHHHH..........",
        ".......HHhHHHHH.........",
        "......HHHHHHHHHH........",
        "......HSSGGGGSSH........",
        "......HSGgGGgGSH........",
        ".......SSBBBBSS.........",
        "........SBWWBS..........",
        "........gSSSSg..........",
        "....SS.MMMMMMMM.SS......",
        "....g.MMYggggYMM.g......",
        ".....MMMMMMMMMMMM.......",
        ".....MMmMMMMMMmMM.......",
        "......MMMMMMMMMM........",
        "......MMYMMMMYMM........",
        "......MMMMMMMMMM........",
        "......VVVVVVVVVV........",
        "......VVvVVVVvVV........",
        "......VVVVggVVVV........",
        "......VVV....VVV........",
        "......VVV....VVV........",
        "......SS......SS........",
        ".....KKKK....KKKK.......",
        "........................"
    )

    val frame3 = listOf(
        "........................",
        "..........HHHHHH........",
        ".........HHHHHhHH.......",
        "........HHHHHHHHHH......",
        "........HSSGGGGSSH......",
        "........HSGgGGgGSH......",
        ".........SSBBBBSS.......",
        "..........SBWWBS........",
        "...........SSSS.........",
        "........mMMMgg..........",
        ".......MMMMMMggMMMMM....",
        "......MMMYMMMMMMMMYMM...",
        "......SSMMMMMMMMMMmMM...",
        "......SS.MMMMMMMMM.SS...",
        ".........MMYMMMYMM..g...",
        ".........MMMMMMMMM......",
        ".........VVVVVVVVV......",
        ".........VVvVVVvVV......",
        ".........VVVVVgVVVV.....",
        ".........VVVV..VVVV.....",
        ".........VVV....VVV.....",
        "........SSS......SSS....",
        ".......KKKK......KKKK...",
        "........................"
    )

    val frame4 = listOf(
        "........................",
        ".........HHHHHH......Y..",
        "........HHHHHhHH....Y...",
        ".......HHHHHHHHHH.......",
        ".......HSSGGGGSSH.......",
        ".......HSGgGGWGSH.......",
        "........SSBBBBSS........",
        ".........SBWWBS.........",
        "..........SSSS..........",
        "......mMMMMgg...........",
        "......MMMMMMMggMMMMMM...",
        ".....SSMMYMMMMMMMMYMMM..",
        ".....SS.MMMMMMMMMMm.SS..",
        ".........MMMMMMMMM...g..",
        ".........MMYMMMYMM......",
        ".........MMMMMMMMM......",
        ".........VVVVVVVVVV.....",
        ".........VVvVVVvVVV.....",
        ".........VVVVVgVVVVV....",
        ".........VVVV...VVVV....",
        ".........VVV.....VVV....",
        "........SSS.......SSS...",
        ".......KKKK.......KKKK..",
        "........................"
    )

    val frame5 = listOf(
        "........................",
        "...Y....HHHHHH....Y.....",
        ".......HHhHHHHH.........",
        "......HHHHHHHHHH........",
        "......HSSGGGGSSH........",
        "......HSGWGGWGSH........",
        ".......SSBBBBSS.........",
        "........SBWWBS..........",
        "........gSSSSg..........",
        ".....MMMMMMMMMMMM.......",
        "....MMMYMggggMYMMM......",
        "...SS.MMMMMMMMMM.SS.....",
        "...g..MMmMMMMmMM..g.....",
        "......MMMMMMMMMM........",
        "......MMYMMMMYMM........",
        "......VVVVVVVVVV........",
        "......VVvVVVVvVV........",
        ".....VVVVVggVVVVV.......",
        ".....VVVV....VVVV.......",
        ".....VVV......VVV.......",
        ".....SS........SS.......",
        "....KKKK......KKKK......",
        "........................",
        "........................"
    )

    return listOf(frame0, frame1, frame2, frame3, frame4, frame5).map { rows ->
        val height = rows.size
        val width = rows.first().length
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val row = rows[y]
            for (x in 0 until width) {
                val ch = if (x < row.length) row[x] else '.'
                pixels[y * width + x] = palette[ch] ?: 0x00000000
            }
        }
        Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888).asImageBitmap()
    }
}

internal class MasterChiptuneBgmController {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var playJob: Job? = null
    @Volatile
    private var audioTrack: AudioTrack? = null

    fun startWithFadeIn() {
        playJob?.cancel()
        playJob = scope.launch {
            runCatching {
                val sampleRate = 22050
                val durationSeconds = 4.0
                val totalSamples = (sampleRate * durationSeconds).toInt()
                val fadeSamples = (sampleRate * 0.30).toInt() // 300ms fade-in & 300ms fade-out
                val targetPeakVolume = 0.28f // 28% volume (25-30% spec)

                val pcmBuffer = ShortArray(totalSamples)

                data class ChiptuneNote(
                    val freqHz: Double,
                    val subBassHz: Double,
                    val startMs: Int,
                    val endMs: Int
                )

                val notes = listOf(
                    ChiptuneNote(293.66, 146.83, 0, 220),
                    ChiptuneNote(293.66, 146.83, 250, 460),
                    ChiptuneNote(349.23, 174.61, 490, 720),
                    ChiptuneNote(392.00, 196.00, 750, 1020),
                    ChiptuneNote(415.30, 207.65, 1050, 1320),
                    ChiptuneNote(392.00, 196.00, 1350, 1580),
                    ChiptuneNote(349.23, 174.61, 1610, 1840),
                    ChiptuneNote(293.66, 146.83, 1870, 2220),
                    ChiptuneNote(293.66, 146.83, 2300, 2500),
                    ChiptuneNote(440.00, 220.00, 2530, 2780),
                    ChiptuneNote(415.30, 207.65, 2810, 3040),
                    ChiptuneNote(392.00, 196.00, 3070, 3290),
                    ChiptuneNote(349.23, 174.61, 3320, 3540),
                    ChiptuneNote(293.66, 146.83, 3570, 3980)
                )

                for (i in 0 until totalSamples) {
                    val timeMs = (i * 1000.0) / sampleRate
                    val t = i.toDouble() / sampleRate

                    val activeNote = notes.firstOrNull { timeMs >= it.startMs && timeMs <= it.endMs }
                    var sampleValue = 0.0

                    if (activeNote != null) {
                        val noteProgress = (timeMs - activeNote.startMs) /
                            (activeNote.endMs - activeNote.startMs).coerceAtLeast(1)
                        val noteEnv = (1.0 - 0.35 * noteProgress).coerceIn(0.0, 1.0)

                        val leadWave = if (sin(2.0 * PI * activeNote.freqHz * t) >= 0.0) 0.65 else -0.65
                        val harmonicWave = sin(2.0 * PI * (activeNote.freqHz * 2.0) * t) * 0.20
                        val subWave = sin(2.0 * PI * activeNote.subBassHz * t) * 0.35

                        sampleValue = (leadWave + harmonicWave + subWave) * noteEnv
                    }

                    val fadeEnvelope = when {
                        i < fadeSamples -> i.toFloat() / fadeSamples.toFloat()
                        i > totalSamples - fadeSamples ->
                            (totalSamples - i).toFloat() / fadeSamples.toFloat()
                        else -> 1.0f
                    }.coerceIn(0f, 1f)

                    val scaled = (sampleValue * targetPeakVolume * fadeEnvelope)
                        .coerceIn(-1.0, 1.0)
                    pcmBuffer[i] = (scaled * Short.MAX_VALUE).toInt().toShort()
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(pcmBuffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(pcmBuffer, 0, pcmBuffer.size)
                track.setVolume(targetPeakVolume)
                audioTrack = track
                track.play()

                var elapsed = 0L
                while (isActive && elapsed < 4000L) {
                    delay(50L)
                    elapsed += 50L
                }
            }
        }
    }

    fun stopWithFadeOut() {
        val track = audioTrack
        audioTrack = null
        playJob?.cancel()
        if (track != null) {
            scope.launch {
                runCatching {
                    val steps = 6
                    for (step in steps downTo 0) {
                        val vol = 0.28f * (step.toFloat() / steps.toFloat())
                        track.setVolume(vol)
                        delay(25L)
                    }
                    track.stop()
                    track.release()
                }.onFailure {
                    runCatching { track.release() }
                }
            }
        }
    }
}
