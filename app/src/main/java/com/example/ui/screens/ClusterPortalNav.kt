package com.example.ui.screens

import android.view.KeyEvent as AndroidKeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.R
import com.example.model.Episode
import com.example.model.FootballMatch
import com.example.model.FootballStreamMirror
import com.example.model.Show
import com.example.repository.VijayTvRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Module: ClusterPortalNav (Netflix TV-Grade Hero Carousel Architecture)
 *
 * Key TV & Visual Fixes:
 * 1. Zero White Background Artifacts:
 *    - Removed positive RGB constant offsets from `ColorMatrix` and removed `Modifier.blur()`
 *      on hardware cutout layers so transparent pixels remain strictly `alpha = 0.0` on all Android TVs.
 * 2. Seamless Android TV D-Pad Navigation:
 *    - Inner decorative cutouts and ring dots have `focusProperties { canFocus = false }` so D-Pad
 *      focus never gets trapped inside 17 overlapping nodes.
 *    - `cluster_enter_shelf_button` acts as the single unified Hero Carousel D-Pad controller:
 *      - Left / Right D-Pad switches the spotlighted celebrity and updates the active show immediately.
 *      - Center / OK opens the episode shelf and transfers focus directly to the shelf's Play button.
 *      - Down D-Pad moves focus cleanly down to the show pills and episode rails below in 1 click.
 */

enum class ClusterDomain(
    val label: String,
    val primaryGlow: Color,
    val secondaryGlow: Color,
    val rimNeonColor: Color
) {
    KOLLYWOOD_CINEMA(
        label = "Saffron / Gold Cinema",
        primaryGlow = Color(0xFFFFB300),
        secondaryGlow = Color(0xFF1F3B64),
        rimNeonColor = Color(0xFFFFD54F)
    ),
    LIVE_FOOTBALL(
        label = "Emerald / Star Vijay Specials",
        primaryGlow = Color(0xFF00E676),
        secondaryGlow = Color(0xFF1A3A5C),
        rimNeonColor = Color(0xFF69F0AE)
    ),
    REALITY_BIGG_BOSS(
        label = "Electric Violet Reality",
        primaryGlow = Color(0xFFAB47BC),
        secondaryGlow = Color(0xFF1B3358),
        rimNeonColor = Color(0xFFE040FB)
    ),
    COMEDY_CULINARY_TV(
        label = "Warm Amber / Culinary Entertainment",
        primaryGlow = Color(0xFFFF8F00),
        secondaryGlow = Color(0xFF1C355E),
        rimNeonColor = Color(0xFFFFCC80)
    ),
    MUSICAL_TALK_SHOW(
        label = "Royal Blue / Stage Spotlight",
        primaryGlow = Color(0xFF42A5F5),
        secondaryGlow = Color(0xFF15294A),
        rimNeonColor = Color(0xFF90CAF9)
    )
}

enum class ClusterDepthTier(
    val baseZIndex: Float,
    val inactiveBlurDp: Float,
    val baseScale: Float,
    val parallaxFactor: Float
) {
    FOREGROUND(baseZIndex = 6f, inactiveBlurDp = 0f, baseScale = 1.05f, parallaxFactor = 1.0f),
    MIDGROUND(baseZIndex = 4f, inactiveBlurDp = 0f, baseScale = 0.97f, parallaxFactor = 0.68f),
    BACKGROUND(baseZIndex = 2f, inactiveBlurDp = 0f, baseScale = 0.91f, parallaxFactor = 0.40f)
}

enum class CharacterArchetype(val cutoutDrawableRes: Int) {
    THALAPATHY_VIJAY(R.drawable.img_cutout_vijay),
    BRUNO_FERNANDES(R.drawable.img_cutout_vijaysethupathi),
    BIGG_BOSS_HOST(R.drawable.img_cutout_kamal),
    MASTERCHEF_HOST(R.drawable.img_cutout_samantha),
    COMALI_CHEF(R.drawable.img_cutout_chef),
    SUPER_SINGER_STAR(R.drawable.img_cutout_anirudh),
    DEBATE_HOST_GOPINATH(R.drawable.img_cutout_gopinath)
}

data class ClusterNode(
    val id: String,
    val displayTitle: String,
    val characterName: String,
    val pillarCategory: String,
    val subtitleBadge: String,
    val domain: ClusterDomain,
    val depthTier: ClusterDepthTier,
    val archetype: CharacterArchetype,
    val normalizedX: Float,
    val normalizedY: Float,
    val cutoutImageUrl: String,
    val isLockedCoreAnchor: Boolean,
    val associatedShowId: String? = null
)

private data class ClusterSpatialSlot(
    val normalizedX: Float,
    val normalizedY: Float,
    val depthTier: ClusterDepthTier
)

object ClusterCharacterEngine {
    const val MIN_VISIBLE_CLUSTER_NODES = 5
    const val MAX_VISIBLE_CLUSTER_NODES = 7
    const val HARD_CAP_VISIBLE_NODES = 7

    private val SPATIAL_SLOTS = listOf(
        // Slot 0: Far-Left (Samantha — Celebrity MasterChef Tamil)
        ClusterSpatialSlot(normalizedX = 0.20f, normalizedY = 0.05f, depthTier = ClusterDepthTier.MIDGROUND),
        // Slot 1: Mid-Left (Gopinath — Neeya Naana)
        ClusterSpatialSlot(normalizedX = 0.30f, normalizedY = 0.09f, depthTier = ClusterDepthTier.BACKGROUND),
        // Slot 2: Center-Left (Thalapathy Vijay — Tamil Movies)
        ClusterSpatialSlot(normalizedX = 0.40f, normalizedY = 0.04f, depthTier = ClusterDepthTier.FOREGROUND),
        // Slot 3: Center (Kamal Haasan — Bigg Boss 10)
        ClusterSpatialSlot(normalizedX = 0.50f, normalizedY = 0.10f, depthTier = ClusterDepthTier.MIDGROUND),
        // Slot 4: Center-Right (Vijay Sethupathi — All Vijay TV Shows)
        ClusterSpatialSlot(normalizedX = 0.60f, normalizedY = 0.04f, depthTier = ClusterDepthTier.FOREGROUND),
        // Slot 5: Mid-Right (Anirudh — Super Singer)
        ClusterSpatialSlot(normalizedX = 0.70f, normalizedY = 0.09f, depthTier = ClusterDepthTier.BACKGROUND),
        // Slot 6: Far-Right (Pugazh & Chef Damu — Cooku with Comali)
        ClusterSpatialSlot(normalizedX = 0.80f, normalizedY = 0.05f, depthTier = ClusterDepthTier.MIDGROUND)
    )

    val LOCKED_CORE_ANCHORS: List<ClusterNode> = listOf(
        ClusterNode(
            id = "anchor_vijay",
            displayTitle = "Tamil Movies",
            characterName = "Thalapathy Vijay",
            pillarCategory = "Tamil Featured Movies • 1080p Full HD",
            subtitleBadge = "TAMIL CINEMA • 1080P HD",
            domain = ClusterDomain.KOLLYWOOD_CINEMA,
            depthTier = SPATIAL_SLOTS[2].depthTier,
            archetype = CharacterArchetype.THALAPATHY_VIJAY,
            normalizedX = SPATIAL_SLOTS[2].normalizedX,
            normalizedY = SPATIAL_SLOTS[2].normalizedY,
            cutoutImageUrl = "drawable://img_cutout_vijay",
            isLockedCoreAnchor = true,
            associatedShowId = null
        ),
        ClusterNode(
            id = "anchor_biggboss",
            displayTitle = "Bigg Boss 10",
            characterName = "Kamal Haasan",
            pillarCategory = "Bigg Boss Tamil S10 • Daily & 24/7 Live",
            subtitleBadge = "VIJAY TV • 24/7 UNCUT & EPISODES",
            domain = ClusterDomain.REALITY_BIGG_BOSS,
            depthTier = SPATIAL_SLOTS[3].depthTier,
            archetype = CharacterArchetype.BIGG_BOSS_HOST,
            normalizedX = SPATIAL_SLOTS[3].normalizedX,
            normalizedY = SPATIAL_SLOTS[3].normalizedY,
            cutoutImageUrl = "drawable://img_cutout_kamal",
            isLockedCoreAnchor = true,
            associatedShowId = "bigg_boss_tamil"
        ),
        ClusterNode(
            id = "anchor_bruno",
            displayTitle = "Vijay TV Hits",
            characterName = "Vijay Sethupathi • Bruno Fernandes",
            pillarCategory = "Star Vijay Blockbuster Shows & Specials",
            subtitleBadge = "STAR VIJAY • ALL SEASONS",
            domain = ClusterDomain.LIVE_FOOTBALL,
            depthTier = SPATIAL_SLOTS[4].depthTier,
            archetype = CharacterArchetype.BRUNO_FERNANDES,
            normalizedX = SPATIAL_SLOTS[4].normalizedX,
            normalizedY = SPATIAL_SLOTS[4].normalizedY,
            cutoutImageUrl = "drawable://img_cutout_vijaysethupathi",
            isLockedCoreAnchor = true,
            associatedShowId = "cooku_with_comali"
        )
    )

    fun mapShowToCharacterNode(show: Show, slotIndex: Int): ClusterNode {
        val slot = SPATIAL_SLOTS[slotIndex.coerceIn(0, SPATIAL_SLOTS.lastIndex)]
        val lowerTitle = show.title.lowercase()
        val lowerId = show.id.lowercase()

        return when {
            lowerId.contains("masterchef") || lowerTitle.contains("masterchef") -> ClusterNode(
                id = "dynamic_${show.id}",
                displayTitle = "Celebrity MasterChef",
                characterName = "Samantha • MasterChef Tamil",
                pillarCategory = "Celebrity MasterChef Tamil • Sony Vizha",
                subtitleBadge = "SONY VIZHA • 1080P HD",
                domain = ClusterDomain.COMEDY_CULINARY_TV,
                depthTier = slot.depthTier,
                archetype = CharacterArchetype.MASTERCHEF_HOST,
                normalizedX = slot.normalizedX,
                normalizedY = slot.normalizedY,
                cutoutImageUrl = "drawable://img_cutout_samantha",
                isLockedCoreAnchor = false,
                associatedShowId = show.id
            )

            lowerId.contains("cooku") || lowerTitle.contains("cooku") -> ClusterNode(
                id = "dynamic_${show.id}",
                displayTitle = "Cooku with Comali",
                characterName = "Pugazh & Chef Damu • Cooku with Comali",
                pillarCategory = "Cooku with Comali • All Seasons S1–S7",
                subtitleBadge = "VIJAY TV • ALL 7 SEASONS",
                domain = ClusterDomain.COMEDY_CULINARY_TV,
                depthTier = slot.depthTier,
                archetype = CharacterArchetype.COMALI_CHEF,
                normalizedX = slot.normalizedX,
                normalizedY = slot.normalizedY,
                cutoutImageUrl = "drawable://img_cutout_chef",
                isLockedCoreAnchor = false,
                associatedShowId = show.id
            )

            lowerId.contains("super_singer") || lowerTitle.contains("super singer") -> ClusterNode(
                id = "dynamic_${show.id}",
                displayTitle = "Super Singer",
                characterName = "Anirudh • Priyanka & Ma Ka Pa",
                pillarCategory = "Super Singer • Junior & Senior Seasons",
                subtitleBadge = "VIJAY TV • ALL SEASONS",
                domain = ClusterDomain.MUSICAL_TALK_SHOW,
                depthTier = slot.depthTier,
                archetype = CharacterArchetype.SUPER_SINGER_STAR,
                normalizedX = slot.normalizedX,
                normalizedY = slot.normalizedY,
                cutoutImageUrl = "drawable://img_cutout_anirudh",
                isLockedCoreAnchor = false,
                associatedShowId = show.id
            )

            else -> ClusterNode(
                id = "dynamic_${show.id}",
                displayTitle = show.title,
                characterName = "Gopinath • ${show.title}",
                pillarCategory = "${show.title} • ${show.channel} All Seasons",
                subtitleBadge = "${show.channel.uppercase()} • ${show.badge.uppercase()}",
                domain = ClusterDomain.MUSICAL_TALK_SHOW,
                depthTier = slot.depthTier,
                archetype = CharacterArchetype.DEBATE_HOST_GOPINATH,
                normalizedX = slot.normalizedX,
                normalizedY = slot.normalizedY,
                cutoutImageUrl = "drawable://img_cutout_gopinath",
                isLockedCoreAnchor = false,
                associatedShowId = show.id
            )
        }
    }

    fun buildFullClusterRing(shows: List<Show>): List<ClusterNode> {
        val dynamicSlots = listOf(0, 1, 5, 6)
        val filteredShows = shows
            .filter { it.id != "bigg_boss_tamil" }
            .sortedBy { show ->
                when (show.id) {
                    "celebrity_masterchef" -> 0
                    "neeya_naana" -> 1
                    "super_singer" -> 2
                    "cooku_with_comali" -> 3
                    else -> 4
                }
            }
        val dynamicShowNodes = filteredShows.mapIndexed { idx, show ->
            val targetSlot = dynamicSlots[idx % dynamicSlots.size]
            mapShowToCharacterNode(show, slotIndex = targetSlot)
        }
        return (LOCKED_CORE_ANCHORS + dynamicShowNodes).sortedBy { it.normalizedX }
    }

    fun getVisibleClusterNodes(
        fullRing: List<ClusterNode>,
        focusedNodeId: String
    ): List<ClusterNode> {
        val dynamicPool = fullRing.filter { !it.isLockedCoreAnchor }
        val focusedDynamicIdx = dynamicPool.indexOfFirst { it.id == focusedNodeId }
        val startIdx = if (focusedDynamicIdx >= 0 && dynamicPool.size > 4) focusedDynamicIdx else 0

        val visibleDynamic = if (dynamicPool.isEmpty()) {
            emptyList()
        } else {
            val count = dynamicPool.size.coerceAtMost(4)
            (0 until count).map { offset ->
                dynamicPool[(startIdx + offset) % dynamicPool.size]
            }.distinctBy { it.id }
        }

        val vijay = LOCKED_CORE_ANCHORS[0].copy(
            normalizedX = SPATIAL_SLOTS[2].normalizedX,
            normalizedY = SPATIAL_SLOTS[2].normalizedY,
            depthTier = SPATIAL_SLOTS[2].depthTier
        )
        val biggBoss = LOCKED_CORE_ANCHORS[1].copy(
            normalizedX = SPATIAL_SLOTS[3].normalizedX,
            normalizedY = SPATIAL_SLOTS[3].normalizedY,
            depthTier = SPATIAL_SLOTS[3].depthTier
        )
        val bruno = LOCKED_CORE_ANCHORS[2].copy(
            normalizedX = SPATIAL_SLOTS[4].normalizedX,
            normalizedY = SPATIAL_SLOTS[4].normalizedY,
            depthTier = SPATIAL_SLOTS[4].depthTier
        )

        val result = mutableListOf<ClusterNode>()
        visibleDynamic.getOrNull(0)?.let {
            result.add(
                it.copy(
                    normalizedX = SPATIAL_SLOTS[0].normalizedX,
                    normalizedY = SPATIAL_SLOTS[0].normalizedY,
                    depthTier = SPATIAL_SLOTS[0].depthTier
                )
            )
        }
        visibleDynamic.getOrNull(1)?.let {
            result.add(
                it.copy(
                    normalizedX = SPATIAL_SLOTS[1].normalizedX,
                    normalizedY = SPATIAL_SLOTS[1].normalizedY,
                    depthTier = SPATIAL_SLOTS[1].depthTier
                )
            )
        }
        result.add(vijay)
        result.add(biggBoss)
        result.add(bruno)
        visibleDynamic.getOrNull(2)?.let {
            result.add(
                it.copy(
                    normalizedX = SPATIAL_SLOTS[5].normalizedX,
                    normalizedY = SPATIAL_SLOTS[5].normalizedY,
                    depthTier = SPATIAL_SLOTS[5].depthTier
                )
            )
        }
        visibleDynamic.getOrNull(3)?.let {
            result.add(
                it.copy(
                    normalizedX = SPATIAL_SLOTS[6].normalizedX,
                    normalizedY = SPATIAL_SLOTS[6].normalizedY,
                    depthTier = SPATIAL_SLOTS[6].depthTier
                )
            )
        }
        return result.sortedBy { it.normalizedX }.take(HARD_CAP_VISIBLE_NODES)
    }
}

@Composable
fun ClusterPortalNav(
    shows: List<Show>,
    showCarousels: Map<String, List<Episode>>,
    currentEpisodes: List<Episode>,
    movies: List<Episode>,
    footballMatches: List<FootballMatch> = emptyList(),
    liveEpisode: Episode?,
    onPlayEpisode: (Episode) -> Unit,
    onPlayFootballMirror: (FootballMatch, FootballStreamMirror) -> Unit = { _, _ -> },
    onOpenShowSeasons: (Show) -> Unit,
    onSwitchHeroShow: (Show) -> Unit = {},
    onOpenMoviesHub: () -> Unit,
    onOpenFootballHub: () -> Unit = {},
    onOpenLiveStudio: () -> Unit,
    onExitClusterDown: () -> Unit = {},
    downFocusRequester: FocusRequester? = null,
    carouselFocusRequester: FocusRequester? = null,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val fullClusterRing = remember(shows) {
        ClusterCharacterEngine.buildFullClusterRing(shows)
    }

    val initialCenterIdx = remember(fullClusterRing) {
        fullClusterRing.indexOfFirst { it.id == "anchor_biggboss" }
            .takeIf { it >= 0 }
            ?: (fullClusterRing.size / 2).coerceAtLeast(0)
    }

    var focusedRingIndex by remember { mutableIntStateOf(initialCenterIdx) }
    val activeNode = fullClusterRing.getOrElse(focusedRingIndex) { fullClusterRing.first() }

    val visibleClusterNodes = remember(fullClusterRing, activeNode.id) {
        ClusterCharacterEngine.getVisibleClusterNodes(fullClusterRing, activeNode.id)
    }

    var isShelfOpen by remember { mutableStateOf(false) }

    val parallaxTiltY = remember { Animatable(0f) }
    val parallaxShiftX = remember { Animatable(0f) }
    val hudBezierProgress = remember { Animatable(0f) }
    val hudLabelAlpha = remember { Animatable(0f) }

    val internalFocusRequester = remember { FocusRequester() }
    val enterShelfButtonFocusRequester = carouselFocusRequester ?: internalFocusRequester
    var isEnterShelfButtonFocused by remember { mutableStateOf(false) }

    // Sync the Home hero show when focus moves to a show character
    LaunchedEffect(activeNode.id) {
        activeNode.associatedShowId?.let { showId ->
            shows.firstOrNull { it.id == showId }?.let { matchedShow ->
                onSwitchHeroShow(matchedShow)
            }
        }
        hudBezierProgress.snapTo(0f)
        hudLabelAlpha.snapTo(0f)
        hudBezierProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
        )
        hudLabelAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing)
        )
    }

    fun triggerParallaxPulse(direction: Float) {
        scope.launch {
            launch {
                parallaxTiltY.snapTo((direction * 3f).coerceIn(-3f, 3f))
                parallaxTiltY.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(dampingRatio = 0.65f, stiffness = 200f)
                )
            }
            launch {
                parallaxShiftX.snapTo(direction * 14f)
                parallaxShiftX.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(dampingRatio = 0.65f, stiffness = 200f)
                )
            }
        }
    }

    fun stepClusterFocus(delta: Int) {
        if (fullClusterRing.isEmpty()) return
        val nextIndex = (focusedRingIndex + delta + fullClusterRing.size) % fullClusterRing.size
        focusedRingIndex = nextIndex
        triggerParallaxPulse(if (delta >= 0) 1f else -1f)
    }

    BackHandler(enabled = isShelfOpen) {
        isShelfOpen = false
        triggerParallaxPulse(-0.7f)
        scope.launch {
            delay(50)
            try {
                enterShelfButtonFocusRequester.requestFocus()
            } catch (_: Exception) {}
        }
    }

    /**
     * CRITICAL TV FIX: Zero constant RGB offset in the 5th column of ColorMatrix!
     * Positive constant offsets in the 5th column add brightness to transparent (0,0,0,0) pixels
     * on Android TV hardware layers, creating a visible white/blue rectangle box behind cutouts.
     * With 0f in the 5th column, transparent pixels remain 100% invisible (0,0,0,0).
     */
    val inactiveBlueMonochromeFilter = remember {
        val lumR = 0.299f
        val lumG = 0.587f
        val lumB = 0.114f
        val blueMontageMatrix = ColorMatrix(
            floatArrayOf(
                lumR * 0.62f, lumG * 0.62f, lumB * 0.62f, 0f, 0f,
                lumR * 0.80f, lumG * 0.80f, lumB * 0.80f, 0f, 0f,
                lumR * 1.15f, lumG * 1.15f, lumB * 1.15f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        ColorFilter.colorMatrix(blueMontageMatrix)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(410.dp)
            .background(Color(0xFF000000))
            .pointerInput(isShelfOpen) {
                var accumulatedDrag = 0f
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (!isShelfOpen && abs(accumulatedDrag) > 40f) {
                            if (accumulatedDrag < 0f) stepClusterFocus(1) else stepClusterFocus(-1)
                        }
                        accumulatedDrag = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        accumulatedDrag += dragAmount
                    }
                )
            }
            .testTag("cluster_portal_nav")
    ) {
        val stageWidthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
        val stageHeightPx = constraints.maxHeight.toFloat().coerceAtLeast(1f)
        val density = LocalDensity.current

        val activeVisibleNode = visibleClusterNodes.firstOrNull { it.id == activeNode.id }
            ?: visibleClusterNodes.first()
        val targetGlowCenterX = if (isShelfOpen) 0.18f else activeVisibleNode.normalizedX
        val animatedGlowCenterX by animateFloatAsState(
            targetValue = targetGlowCenterX,
            animationSpec = spring(dampingRatio = 0.7f, stiffness = 200f),
            label = "domain_glow_x"
        )

        // Pure #000000 OLED Backdrop with soft central atmospheric spotlight
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("cluster_oled_backdrop")
        ) {
            drawRect(color = Color(0xFF000000))

            val groupCenter = Offset(size.width * 0.50f, size.height * 0.52f)
            val groupRadius = (size.minDimension * 0.72f).coerceAtLeast(200f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A3154).copy(alpha = 0.46f),
                        Color(0xFF0D1A30).copy(alpha = 0.24f),
                        Color(0x00000000)
                    ),
                    center = groupCenter,
                    radius = groupRadius
                ),
                radius = groupRadius,
                center = groupCenter
            )

            val focusCenter = Offset(
                x = size.width * animatedGlowCenterX,
                y = size.height * 0.48f
            )
            val focusRadius = (size.minDimension * 0.46f).coerceAtLeast(140f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        activeNode.domain.primaryGlow.copy(alpha = 0.28f),
                        activeNode.domain.secondaryGlow.copy(alpha = 0.12f),
                        Color(0x00000000)
                    ),
                    center = focusCenter,
                    radius = focusRadius
                ),
                radius = focusRadius,
                center = focusCenter
            )
        }

        // Top-Left Section Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 22.dp, top = 14.dp)
                .zIndex(14f)
        ) {
            Text(
                text = "Featured Spotlight",
                color = Color(0xFFA8BED8),
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold
            )
            if (isEnterShelfButtonFocused) {
                Surface(
                    color = Color(0x3300E5FF),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, Color(0xFF00E5FF))
                ) {
                    Text(
                        text = "◀ ▶ D-Pad to Switch • OK to Open • ▼ Down for Rows",
                        color = Color(0xFF00E5FF),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Overlapping Real Celebrity Cutouts in Tight Central Collage
        val cutoutWidthDp = if (maxWidth >= 720.dp) 198.dp else 152.dp
        val cutoutHeightDp = if (maxWidth >= 720.dp) 260.dp else 206.dp
        val cutoutWidthPx = with(density) { cutoutWidthDp.toPx() }
        val cutoutHeightPx = with(density) { cutoutHeightDp.toPx() }

        visibleClusterNodes.forEach { node ->
            val isFocused = node.id == activeNode.id

            val targetNormX = when {
                isShelfOpen && isFocused -> 0.16f
                isShelfOpen && !isFocused -> (node.normalizedX - 0.10f).coerceIn(0.06f, 0.38f)
                else -> node.normalizedX
            }

            val animatedNormX by animateFloatAsState(
                targetValue = targetNormX,
                animationSpec = spring(dampingRatio = 0.68f, stiffness = 200f),
                label = "cutout_x_${node.id}"
            )

            val targetScale = when {
                isFocused && isShelfOpen -> 1.06f
                isFocused -> 1.15f
                else -> 0.94f * node.depthTier.baseScale
            }
            val animatedScale by animateFloatAsState(
                targetValue = targetScale,
                animationSpec = spring(dampingRatio = 0.65f, stiffness = 200f),
                label = "cutout_scale_${node.id}"
            )

            val targetAlpha = when {
                isFocused -> 1.0f
                isShelfOpen -> 0.12f
                else -> 0.76f
            }
            val animatedAlpha by animateFloatAsState(
                targetValue = targetAlpha,
                animationSpec = tween(durationMillis = 180),
                label = "cutout_alpha_${node.id}"
            )

            val nodeZIndex = if (isFocused) 10f else node.depthTier.baseZIndex
            val parallaxDx = parallaxShiftX.value * node.depthTier.parallaxFactor
            val centerX = (stageWidthPx * animatedNormX) + parallaxDx
            val topY = (stageHeightPx * 0.84f) - cutoutHeightPx - (stageHeightPx * node.normalizedY * 0.40f)

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (centerX - cutoutWidthPx / 2f).roundToInt(),
                            y = topY.roundToInt()
                        )
                    }
                    .width(cutoutWidthDp)
                    .height(cutoutHeightDp)
                    .zIndex(nodeZIndex)
                    .graphicsLayer {
                        scaleX = animatedScale
                        scaleY = animatedScale
                        rotationY = parallaxTiltY.value * node.depthTier.parallaxFactor
                        cameraDistance = 14f * density.density
                    }
                    // CRITICAL TV FIX: Prevent D-Pad focus from getting trapped on overlapping cutout nodes
                    .focusProperties { canFocus = false }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        val clickedRingIdx = fullClusterRing.indexOfFirst { it.id == node.id }
                        if (isFocused) {
                            isShelfOpen = !isShelfOpen
                            triggerParallaxPulse(-1f)
                        } else if (clickedRingIdx >= 0) {
                            val delta = clickedRingIdx - focusedRingIndex
                            focusedRingIndex = clickedRingIdx
                            triggerParallaxPulse(if (delta >= 0) 1f else -1f)
                            node.associatedShowId?.let { showId ->
                                shows.firstOrNull { it.id == showId }?.let { matchedShow ->
                                    onSwitchHeroShow(matchedShow)
                                }
                            }
                        }
                    }
                    .testTag("cluster_node_${node.id}")
            ) {
                CharacterCutoutRenderer(
                    node = node,
                    isFocused = isFocused,
                    alpha = animatedAlpha,
                    inactiveColorFilter = inactiveBlueMonochromeFilter,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Bottom Vignette Gradient to blend cutouts seamlessly into the dark stage floor
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(95.dp)
                .zIndex(12f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xCC000000),
                            Color(0xFF06070B)
                        )
                    )
                )
        )

        // Top Title + Curved Smile-Arc Pointer Arrow
        if (!isShelfOpen) {
            val titleCenterX = (stageWidthPx * animatedGlowCenterX).coerceIn(
                stageWidthPx * 0.26f,
                stageWidthPx * 0.74f
            )
            val titleTopY = (stageHeightPx * 0.07f).coerceAtLeast(16f)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (titleCenterX - with(density) { 85.dp.toPx() }).roundToInt(),
                            y = titleTopY.roundToInt()
                        )
                    }
                    .zIndex(16f)
                    .graphicsLayer { alpha = hudLabelAlpha.value }
                    .testTag("cluster_hud_header")
            ) {
                Column {
                    Text(
                        text = activeNode.displayTitle,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("cluster_hud_character_name")
                    )
                    Text(
                        text = activeNode.characterName.substringBefore(" •"),
                        color = activeNode.domain.rimNeonColor,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("cluster_hud_pillar_category")
                    )
                }

                Canvas(
                    modifier = Modifier
                        .size(width = 38.dp, height = 22.dp)
                        .testTag("cluster_hud_bezier_canvas")
                ) {
                    val arcPath = Path().apply {
                        moveTo(2f, size.height * 0.36f)
                        quadraticTo(
                            size.width * 0.48f,
                            size.height * 0.92f,
                            size.width * 0.88f,
                            size.height * 0.30f
                        )
                    }

                    val pathMeasure = PathMeasure()
                    pathMeasure.setPath(arcPath, false)
                    val partialPath = Path()
                    pathMeasure.getSegment(
                        startDistance = 0f,
                        stopDistance = pathMeasure.length * hudBezierProgress.value,
                        destination = partialPath,
                        startWithMoveTo = true
                    )

                    drawPath(
                        path = partialPath,
                        color = Color.White,
                        style = Stroke(
                            width = 2.2.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    if (hudBezierProgress.value > 0.8f) {
                        val tipX = size.width * 0.88f
                        val tipY = size.height * 0.30f
                        val arrowHead = Path().apply {
                            moveTo(tipX - 6.dp.toPx(), tipY - 2.dp.toPx())
                            lineTo(tipX + 1.dp.toPx(), tipY - 1.dp.toPx())
                            lineTo(tipX - 2.dp.toPx(), tipY + 6.dp.toPx())
                        }
                        drawPath(
                            path = arrowHead,
                            color = Color.White,
                            style = Stroke(
                                width = 2.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }
        }

        // Unified TV Remote & Touch Controls Bar at Bottom Edge
        AnimatedVisibility(
            visible = !isShelfOpen,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .zIndex(18f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                IconButton(
                    onClick = { stepClusterFocus(-1) },
                    modifier = Modifier
                        .size(48.dp)
                        .focusProperties { canFocus = false }
                        .testTag("cluster_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous Character in Cluster",
                        tint = Color.White.copy(alpha = 0.75f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Single Primary D-Pad Focus Target for the Carousel
                    Surface(
                        onClick = {
                            isShelfOpen = true
                            triggerParallaxPulse(-1f)
                        },
                        color = if (isEnterShelfButtonFocused) Color(0xFFFF2A54) else Color.White,
                        shape = RoundedCornerShape(50),
                        border = if (isEnterShelfButtonFocused) {
                            BorderStroke(2.5.dp, Color.White)
                        } else null,
                        modifier = Modifier
                            .focusRequester(enterShelfButtonFocusRequester)
                            .focusProperties {
                                canFocus = !isShelfOpen
                                if (downFocusRequester != null) {
                                    down = downFocusRequester
                                }
                            }
                            .onFocusChanged { isEnterShelfButtonFocused = it.isFocused }
                            .graphicsLayer {
                                scaleX = if (isEnterShelfButtonFocused) 1.08f else 1.0f
                                scaleY = if (isEnterShelfButtonFocused) 1.08f else 1.0f
                            }
                            .onPreviewKeyEvent { keyEvent ->
                                if (keyEvent.nativeKeyEvent.action == AndroidKeyEvent.ACTION_DOWN && !isShelfOpen) {
                                    when (keyEvent.nativeKeyEvent.keyCode) {
                                        AndroidKeyEvent.KEYCODE_DPAD_LEFT -> {
                                            stepClusterFocus(-1)
                                            true
                                        }
                                        AndroidKeyEvent.KEYCODE_DPAD_RIGHT -> {
                                            stepClusterFocus(1)
                                            true
                                        }
                                        AndroidKeyEvent.KEYCODE_DPAD_DOWN -> {
                                            onExitClusterDown()
                                            if (downFocusRequester != null) {
                                                try {
                                                    downFocusRequester.requestFocus()
                                                    true
                                                } catch (_: Throwable) {
                                                    false
                                                }
                                            } else {
                                                false
                                            }
                                        }
                                        AndroidKeyEvent.KEYCODE_DPAD_UP -> {
                                            false // Let Compose move focus naturally up to TopBar
                                        }
                                        else -> false
                                    }
                                } else {
                                    false
                                }
                            }
                            .testTag("cluster_enter_shelf_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 9.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Open Selected Content",
                                tint = if (isEnterShelfButtonFocused) Color.White else Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Explore ${activeNode.displayTitle}",
                                color = if (isEnterShelfButtonFocused) Color.White else Color.Black,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    // Ring indicator dots (non-focusable for D-Pad so they don't trap TV remote)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.testTag("cluster_ring_dots")
                    ) {
                        fullClusterRing.forEachIndexed { idx, node ->
                            val selected = idx == focusedRingIndex
                            Box(
                                modifier = Modifier
                                    .height(6.dp)
                                    .width(if (selected) 18.dp else 6.dp)
                                    .background(
                                        color = if (selected) Color.White else Color.White.copy(alpha = 0.28f),
                                        shape = CircleShape
                                    )
                                    .focusProperties { canFocus = false }
                                    .clickable {
                                        focusedRingIndex = idx
                                        triggerParallaxPulse(1f)
                                    }
                                    .testTag("cluster_dot_${node.id}")
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { stepClusterFocus(1) },
                    modifier = Modifier
                        .size(48.dp)
                        .focusProperties { canFocus = false }
                        .testTag("cluster_next_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next Character in Cluster",
                        tint = Color.White.copy(alpha = 0.75f)
                    )
                }
            }
        }

        // Cascading Content Shelf for the selected character
        AnimatedVisibility(
            visible = isShelfOpen,
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> (fullWidth * 0.65f).roundToInt() },
                animationSpec = spring(dampingRatio = 0.75f, stiffness = 200f)
            ) + fadeIn(animationSpec = tween(180)),
            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth -> (fullWidth * 0.75f).roundToInt() },
                animationSpec = spring(dampingRatio = 0.8f, stiffness = 220f)
            ) + fadeOut(animationSpec = tween(140)),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxWidth(if (maxWidth >= 720.dp) 0.70f else 0.86f)
                .fillMaxHeight()
                .zIndex(25f)
        ) {
            ClusterCascadingContentShelf(
                activeNode = activeNode,
                shows = shows,
                showCarousels = showCarousels,
                currentEpisodes = currentEpisodes,
                movies = movies,
                liveEpisode = liveEpisode,
                onCollapseShelf = {
                    isShelfOpen = false
                    triggerParallaxPulse(-0.7f)
                    scope.launch {
                        delay(50)
                        try {
                            enterShelfButtonFocusRequester.requestFocus()
                        } catch (_: Exception) {}
                    }
                },
                onPlayEpisode = onPlayEpisode,
                onOpenShowSeasons = onOpenShowSeasons,
                onOpenMoviesHub = onOpenMoviesHub,
                onOpenFootballHub = onOpenFootballHub,
                onOpenLiveStudio = onOpenLiveStudio
            )
        }
    }
}

@Composable
private fun CharacterCutoutRenderer(
    node: ClusterNode,
    isFocused: Boolean,
    alpha: Float,
    inactiveColorFilter: ColorFilter,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = node.archetype.cutoutDrawableRes),
        contentDescription = "${node.characterName} — ${node.displayTitle}",
        contentScale = ContentScale.Fit,
        alignment = Alignment.BottomCenter,
        colorFilter = if (isFocused) null else inactiveColorFilter,
        modifier = modifier.graphicsLayer {
            this.alpha = alpha
        }
    )
}

@Composable
private fun ClusterCascadingContentShelf(
    activeNode: ClusterNode,
    shows: List<Show>,
    showCarousels: Map<String, List<Episode>>,
    currentEpisodes: List<Episode>,
    movies: List<Episode>,
    liveEpisode: Episode?,
    onCollapseShelf: () -> Unit,
    onPlayEpisode: (Episode) -> Unit,
    onOpenShowSeasons: (Show) -> Unit,
    onOpenMoviesHub: () -> Unit,
    onOpenFootballHub: () -> Unit,
    onOpenLiveStudio: () -> Unit
) {
    val shelfPrimaryFocusRequester = remember { FocusRequester() }

    val matchedShow = remember(activeNode.associatedShowId, shows) {
        activeNode.associatedShowId?.let { targetId ->
            shows.firstOrNull { it.id == targetId }
        } ?: shows.firstOrNull()
    }

    val shelfEpisodes = remember(activeNode.id, activeNode.associatedShowId, matchedShow, showCarousels, currentEpisodes, movies) {
        when (activeNode.id) {
            "anchor_vijay" -> movies
            "anchor_biggboss" -> {
                showCarousels["bigg_boss_tamil"]?.takeIf { it.isNotEmpty() }
                    ?: VijayTvRepository().getInitialEpisodes(VijayTvRepository.BIGG_BOSS_CATEGORY_URL)
            }
            "anchor_bruno" -> {
                showCarousels["cooku_with_comali"]?.takeIf { it.isNotEmpty() }
                    ?: currentEpisodes
            }
            else -> {
                val showId = activeNode.associatedShowId
                if (showId != null) {
                    showCarousels[showId]?.takeIf { it.isNotEmpty() }
                        ?: matchedShow?.let { VijayTvRepository().getInitialEpisodes(it.url) }
                        ?: emptyList()
                } else {
                    emptyList()
                }
            }
        }
    }

    // Automatically transfer D-Pad focus into the shelf when it opens!
    LaunchedEffect(activeNode.id) {
        delay(80)
        try {
            shelfPrimaryFocusRequester.requestFocus()
        } catch (_: Exception) {}
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xCC05070C),
                        Color(0xF5070910),
                        Color(0xFF05070C)
                    )
                )
            )
            .padding(start = 24.dp, end = 16.dp, top = 14.dp, bottom = 14.dp)
            .testTag("cluster_cascading_shelf")
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(activeNode.domain.rimNeonColor, CircleShape)
                        )
                        Text(
                            text = activeNode.subtitleBadge,
                            color = activeNode.domain.rimNeonColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.9.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = activeNode.displayTitle,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${activeNode.characterName.substringBefore(" •")} • ${activeNode.pillarCategory}",
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                var isBackFocused by remember { mutableStateOf(false) }
                OutlinedButton(
                    onClick = onCollapseShelf,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(
                        if (isBackFocused) 2.5.dp else 1.dp,
                        if (isBackFocused) Color.White else Color.White.copy(alpha = 0.35f)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isBackFocused) Color(0x33FFFFFF) else Color.Transparent,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier
                        .onFocusChanged { isBackFocused = it.isFocused }
                        .testTag("cluster_shelf_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Return to Cluster",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Back", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                if (activeNode.id == "anchor_bruno") {
                    var isHubBtnFocused by remember { mutableStateOf(false) }
                    Button(
                        onClick = {
                            if (matchedShow != null) {
                                onOpenShowSeasons(matchedShow)
                            } else {
                                onOpenFootballHub()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isHubBtnFocused) Color(0xFFFF2A54) else Color.White,
                            contentColor = if (isHubBtnFocused) Color.White else Color.Black
                        ),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier
                            .focusRequester(shelfPrimaryFocusRequester)
                            .onFocusChanged { isHubBtnFocused = it.isFocused }
                            .testTag("shelf_open_football_hub_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = "Browse All Vijay TV Shows",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Browse All Shows & Seasons", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    val firstPlayable = shelfEpisodes.firstOrNull { !it.isLiveStream } ?: shelfEpisodes.firstOrNull()
                    if (firstPlayable != null) {
                        var isPlayBtnFocused by remember { mutableStateOf(false) }
                        Button(
                            onClick = { onPlayEpisode(firstPlayable) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlayBtnFocused) Color(0xFFFF2A54) else Color.White,
                                contentColor = if (isPlayBtnFocused) Color.White else Color.Black
                            ),
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 7.dp),
                            modifier = Modifier
                                .focusRequester(shelfPrimaryFocusRequester)
                                .onFocusChanged { isPlayBtnFocused = it.isFocused }
                                .testTag("shelf_instant_play_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Latest",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (activeNode.id == "anchor_vijay") "Play Movie" else "Play Latest Episode",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }

                if (activeNode.id == "anchor_biggboss") {
                    var isLiveFocused by remember { mutableStateOf(false) }
                    OutlinedButton(
                        onClick = {
                            if (liveEpisode != null) {
                                onPlayEpisode(liveEpisode)
                            } else {
                                onOpenLiveStudio()
                            }
                        },
                        border = BorderStroke(
                            if (isLiveFocused) 2.5.dp else 1.dp,
                            if (isLiveFocused) Color.White else Color.White.copy(alpha = 0.28f)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 7.dp),
                        modifier = Modifier
                            .onFocusChanged { isLiveFocused = it.isFocused }
                            .testTag("shelf_24x7_uncut_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.LiveTv,
                            contentDescription = "24/7 Live Feed",
                            tint = activeNode.domain.rimNeonColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "24/7 Live",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                if (activeNode.id == "anchor_vijay") {
                    var isMoviesBtnFocused by remember { mutableStateOf(false) }
                    OutlinedButton(
                        onClick = onOpenMoviesHub,
                        border = BorderStroke(
                            if (isMoviesBtnFocused) 2.5.dp else 1.dp,
                            if (isMoviesBtnFocused) Color.White else Color.White.copy(alpha = 0.28f)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 7.dp),
                        modifier = Modifier
                            .onFocusChanged { isMoviesBtnFocused = it.isFocused }
                            .testTag("shelf_open_movies_hub_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = "Browse All Movies",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "All Movies",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                } else if (matchedShow != null && activeNode.id != "anchor_bruno") {
                    var isSeasonsBtnFocused by remember { mutableStateOf(false) }
                    OutlinedButton(
                        onClick = { onOpenShowSeasons(matchedShow) },
                        border = BorderStroke(
                            if (isSeasonsBtnFocused) 2.5.dp else 1.dp,
                            if (isSeasonsBtnFocused) Color.White else Color.White.copy(alpha = 0.28f)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 7.dp),
                        modifier = Modifier
                            .onFocusChanged { isSeasonsBtnFocused = it.isFocused }
                            .testTag("shelf_all_seasons_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Browse All Seasons",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "All Seasons",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("shelf_episodes_list")
            ) {
                itemsIndexed(shelfEpisodes.take(10), key = { _, ep -> ep.url }) { _, ep ->
                    var isEpFocused by remember { mutableStateOf(false) }
                    Surface(
                        onClick = { onPlayEpisode(ep) },
                        color = if (isEpFocused) Color(0xFF1D263B) else Color(0xFF101522),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            if (isEpFocused) 2.5.dp else 1.dp,
                            if (isEpFocused) Color.White else Color.White.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isEpFocused = it.isFocused }
                            .testTag("shelf_episode_card")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            AsyncImage(
                                model = ep.thumbnailUrl,
                                contentDescription = ep.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(96.dp)
                                    .height(56.dp)
                                    .background(Color(0xFF161B28), RoundedCornerShape(10.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ep.enrichedMetadata?.cleanTitle?.takeIf { it.isNotBlank() } ?: ep.title,
                                    color = Color.White,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = activeNode.domain.rimNeonColor,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "${ep.dateStr} • 1080p Full HD",
                                        color = Color.White.copy(alpha = 0.72f),
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = if (isEpFocused) Color(0xFFFF2A54) else Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
