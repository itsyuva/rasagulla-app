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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.filled.SportsSoccer
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.focus.FocusRequester
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
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Module: ClusterPortalNav (Home Screen Hero Architecture)
 *
 * A tight, overlapping central group collage of real, background-removed lifelike celebrity
 * PNG cutouts on a pure #000000 OLED canvas:
 * - Inactive actor cutouts are rendered in a cool slate-blue monochrome montage tint.
 * - The actively focused celebrity pops in full lifelike warm color with a curved title callout arc above.
 * - Strictly contains ONLY selectable characters for real shows and categories present in the app:
 *   - Samantha Ruth Prabhu -> Celebrity MasterChef Tamil
 *   - Thalapathy Vijay -> Tamil Movies
 *   - Kamal Haasan -> Bigg Boss 10
 *   - Wayne Rooney -> Live Football
 *   - Gopinath -> Neeya Naana
 *   - Anirudh Ravichander -> Super Singer
 *   - Vijay Sethupathi -> Cooku with Comali
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
        label = "Pitch Green / Stadium Crimson",
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
    FOREGROUND(baseZIndex = 6f, inactiveBlurDp = 0.4f, baseScale = 1.05f, parallaxFactor = 1.0f),
    MIDGROUND(baseZIndex = 4f, inactiveBlurDp = 0.8f, baseScale = 0.97f, parallaxFactor = 0.68f),
    BACKGROUND(baseZIndex = 2f, inactiveBlurDp = 1.1f, baseScale = 0.91f, parallaxFactor = 0.40f)
}

enum class CharacterArchetype(val cutoutDrawableRes: Int) {
    THALAPATHY_VIJAY(R.drawable.img_cutout_vijay),
    BRUNO_FERNANDES(R.drawable.img_cutout_rooney),
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

/**
 * Tightly overlapping central collage coordinates matching the reference TV screenshot:
 * Real celebrity cutouts stand shoulder-to-shoulder in a cohesive 2-row pyramid in the center of the canvas.
 */
private data class ClusterSpatialSlot(
    val normalizedX: Float,
    val normalizedY: Float,
    val depthTier: ClusterDepthTier
)

object ClusterCharacterEngine {
    const val MIN_VISIBLE_CLUSTER_NODES = 5
    const val MAX_VISIBLE_CLUSTER_NODES = 7
    const val HARD_CAP_VISIBLE_NODES = 7

    /**
     * 7 tightly overlapping central collage slots arranged like the reference TV screenshot:
     * - Back row (higher up): Back-Left (0.39), Back-Center Apex (0.51), Back-Right (0.64)
     * - Front/Mid row (lower down, overlapping in front): Far-Left (0.30), Front-Left-Center (0.45),
     *   Front-Right-Center (0.58), Far-Right (0.71)
     */
    private val SPATIAL_SLOTS = listOf(
        // Slot 0: Far-Left Midground (Samantha — Celebrity MasterChef Tamil)
        ClusterSpatialSlot(normalizedX = 0.30f, normalizedY = 0.05f, depthTier = ClusterDepthTier.MIDGROUND),
        // Slot 1: Back-Left Upper (Gopinath — Neeya Naana)
        ClusterSpatialSlot(normalizedX = 0.39f, normalizedY = 0.23f, depthTier = ClusterDepthTier.BACKGROUND),
        // Slot 2: Front-Left-Center Foreground (Thalapathy Vijay — Tamil Movies)
        ClusterSpatialSlot(normalizedX = 0.45f, normalizedY = 0.06f, depthTier = ClusterDepthTier.FOREGROUND),
        // Slot 3: Back-Center Apex (Kamal Haasan — Bigg Boss 10)
        ClusterSpatialSlot(normalizedX = 0.52f, normalizedY = 0.26f, depthTier = ClusterDepthTier.MIDGROUND),
        // Slot 4: Front-Right-Center Foreground (Wayne Rooney — Live Football)
        ClusterSpatialSlot(normalizedX = 0.58f, normalizedY = 0.02f, depthTier = ClusterDepthTier.FOREGROUND),
        // Slot 5: Back-Right Upper (Anirudh — Super Singer)
        ClusterSpatialSlot(normalizedX = 0.65f, normalizedY = 0.21f, depthTier = ClusterDepthTier.BACKGROUND),
        // Slot 6: Far-Right Midground (Vijay Sethupathi — Cooku with Comali)
        ClusterSpatialSlot(normalizedX = 0.72f, normalizedY = 0.05f, depthTier = ClusterDepthTier.MIDGROUND)
    )

    /**
     * 1. Locked Core Anchors (Strictly mapped to real app pillars with real lifelike cutouts):
     * - Thalapathy Vijay -> Tamil Movies
     * - Kamal Haasan -> Bigg Boss 10 (Daily & 24/7 Live)
     * - Wayne Rooney -> Live Football
     * (Spider-Man removed as requested)
     */
    val LOCKED_CORE_ANCHORS: List<ClusterNode> = listOf(
        ClusterNode(
            id = "anchor_vijay",
            displayTitle = "Tamil Movies",
            characterName = "Thalapathy Vijay",
            pillarCategory = "Tamil Featured Movies • 1080p HDRip",
            subtitleBadge = "5MOVIERULZ • TAMIL CINEMA",
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
            displayTitle = "Live Football",
            characterName = "Wayne Rooney • Bruno Fernandes",
            pillarCategory = "Live Football Hub • EPL, UCL & La Liga",
            subtitleBadge = "LIVE STREAMS • 1080P 60FPS",
            domain = ClusterDomain.LIVE_FOOTBALL,
            depthTier = SPATIAL_SLOTS[4].depthTier,
            archetype = CharacterArchetype.BRUNO_FERNANDES,
            normalizedX = SPATIAL_SLOTS[4].normalizedX,
            normalizedY = SPATIAL_SLOTS[4].normalizedY,
            cutoutImageUrl = "drawable://img_cutout_rooney",
            isLockedCoreAnchor = true,
            associatedShowId = null
        )
    )

    /**
     * 2. Maps ONLY the real catalog shows present in our app to lifelike celebrity cutouts:
     * - Celebrity MasterChef Tamil (`celebrity_masterchef`) -> Samantha Ruth Prabhu cutout
     * - Cooku with Comali (`cooku_with_comali`) -> Vijay Sethupathi / Chef cutout
     * - Super Singer (`super_singer`) -> Anirudh Ravichander cutout
     * - Neeya Naana (`neeya_naana`) -> Gopinath cutout
     * Zero fake or external serials (no Baakiyalakshmi, no Spider-Man).
     */
    fun mapShowToCharacterNode(show: Show, slotIndex: Int): ClusterNode {
        val slot = SPATIAL_SLOTS[slotIndex.coerceIn(0, SPATIAL_SLOTS.lastIndex)]
        val lowerTitle = show.title.lowercase()
        val lowerId = show.id.lowercase()

        return when {
            lowerId.contains("masterchef") || lowerTitle.contains("masterchef") -> ClusterNode(
                id = "dynamic_${show.id}",
                displayTitle = "Celebrity MasterChef",
                characterName = "Samantha • Chef Koushik",
                pillarCategory = "Celebrity MasterChef Tamil • Sony Vizha",
                subtitleBadge = "SONY VIZHA • 2026 SEASON",
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
                characterName = "Chef Damu • Pugazh & Chef Damu",
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

    /**
     * Builds the ring strictly from the 3 real app core pillars + the 4 real shows in `shows`.
     * Total = 7 characters, all simultaneously visible in the 7 collage slots!
     */
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
        return LOCKED_CORE_ANCHORS + dynamicShowNodes
    }

    /**
     * Positions all 7 character cutouts into the tight central overlapping collage slots:
     * - Slot 0 (Far-Left): Samantha (Celebrity MasterChef Tamil)
     * - Slot 1 (Back-Left): Gopinath (Neeya Naana)
     * - Slot 2 (Front-Left-Center): Thalapathy Vijay (Tamil Movies)
     * - Slot 3 (Back-Center Apex): Kamal Haasan (Bigg Boss 10)
     * - Slot 4 (Front-Right-Center): Wayne Rooney (Live Football)
     * - Slot 5 (Back-Right): Anirudh (Super Singer)
     * - Slot 6 (Far-Right): Vijay Sethupathi (Cooku with Comali)
     */
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
        return result.take(HARD_CAP_VISIBLE_NODES)
    }
}

/**
 * Primary Composable for `ClusterPortalNav` (Home Screen Hero Architecture).
 */
@Composable
fun ClusterPortalNav(
    shows: List<Show>,
    showCarousels: Map<String, List<Episode>>,
    currentEpisodes: List<Episode>,
    movies: List<Episode>,
    footballMatches: List<FootballMatch>,
    liveEpisode: Episode?,
    onPlayEpisode: (Episode) -> Unit,
    onPlayFootballMirror: (FootballMatch, FootballStreamMirror) -> Unit,
    onOpenShowSeasons: (Show) -> Unit,
    onSwitchHeroShow: (Show) -> Unit = {},
    onOpenMoviesHub: () -> Unit,
    onOpenFootballHub: () -> Unit,
    onOpenLiveStudio: () -> Unit,
    onExitClusterDown: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    // Strictly build cluster from the 3 real app pillars + the 4 real catalog shows in our app
    val fullClusterRing = remember(shows) {
        ClusterCharacterEngine.buildFullClusterRing(shows)
    }

    // Default focus on Thalapathy Vijay (Tamil Movies) or Bigg Boss 10 in foreground
    var focusedRingIndex by remember { mutableIntStateOf(1.coerceAtMost(fullClusterRing.lastIndex)) }
    val activeNode = fullClusterRing.getOrElse(focusedRingIndex) { fullClusterRing.first() }

    val visibleClusterNodes = remember(fullClusterRing, activeNode.id) {
        ClusterCharacterEngine.getVisibleClusterNodes(fullClusterRing, activeNode.id)
    }

    var isShelfOpen by remember { mutableStateOf(false) }

    val parallaxTiltY = remember { Animatable(0f) }
    val parallaxShiftX = remember { Animatable(0f) }
    val hudBezierProgress = remember { Animatable(0f) }
    val hudLabelAlpha = remember { Animatable(0f) }

    val clusterFocusRequester = remember { FocusRequester() }
    var isClusterKeyboardFocused by remember { mutableStateOf(false) }

    // Whenever focus moves to a TV show character, sync the Home hero show to THAT exact show
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

    fun jumpToBackgroundLayerNode() {
        val bgIdx = fullClusterRing.indexOfFirst {
            it.depthTier == ClusterDepthTier.BACKGROUND || !it.isLockedCoreAnchor
        }
        if (bgIdx >= 0 && bgIdx != focusedRingIndex) {
            focusedRingIndex = bgIdx
            triggerParallaxPulse(0.8f)
        } else {
            stepClusterFocus(1)
        }
    }

    BackHandler(enabled = isShelfOpen) {
        isShelfOpen = false
        triggerParallaxPulse(-0.7f)
    }

    // Cool slate-blue monochrome duotone matrix for inactive celebrity cutouts (matching reference screenshot)
    val inactiveBlueMonochromeFilter = remember {
        val lumR = 0.299f
        val lumG = 0.587f
        val lumB = 0.114f
        val blueMontageMatrix = ColorMatrix(
            floatArrayOf(
                lumR * 0.56f, lumG * 0.56f, lumB * 0.56f, 0f, 10f,
                lumR * 0.74f, lumG * 0.74f, lumB * 0.74f, 0f, 20f,
                lumR * 1.10f, lumG * 1.10f, lumB * 1.10f, 0f, 38f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        ColorFilter.colorMatrix(blueMontageMatrix)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(430.dp)
            .background(Color(0xFF000000))
            .focusRequester(clusterFocusRequester)
            .onFocusChanged { isClusterKeyboardFocused = it.isFocused }
            .onPreviewKeyEvent { keyEvent ->
                if (keyEvent.nativeKeyEvent.action == AndroidKeyEvent.ACTION_DOWN) {
                    when (keyEvent.nativeKeyEvent.keyCode) {
                        AndroidKeyEvent.KEYCODE_DPAD_LEFT -> {
                            if (!isShelfOpen) {
                                stepClusterFocus(-1)
                                true
                            } else {
                                false
                            }
                        }

                        AndroidKeyEvent.KEYCODE_DPAD_RIGHT -> {
                            if (!isShelfOpen) {
                                stepClusterFocus(1)
                                true
                            } else {
                                false
                            }
                        }

                        AndroidKeyEvent.KEYCODE_DPAD_UP -> {
                            if (!isShelfOpen) {
                                jumpToBackgroundLayerNode()
                                true
                            } else {
                                false
                            }
                        }

                        AndroidKeyEvent.KEYCODE_DPAD_DOWN -> {
                            if (!isShelfOpen) {
                                onExitClusterDown()
                                false
                            } else {
                                false
                            }
                        }

                        AndroidKeyEvent.KEYCODE_DPAD_CENTER,
                        AndroidKeyEvent.KEYCODE_ENTER,
                        AndroidKeyEvent.KEYCODE_NUMPAD_ENTER -> {
                            if (!isShelfOpen) {
                                isShelfOpen = true
                                triggerParallaxPulse(-1f)
                                true
                            } else {
                                false
                            }
                        }

                        AndroidKeyEvent.KEYCODE_BACK,
                        AndroidKeyEvent.KEYCODE_ESCAPE -> {
                            if (isShelfOpen) {
                                isShelfOpen = false
                                triggerParallaxPulse(-0.7f)
                                true
                            } else {
                                false
                            }
                        }

                        else -> false
                    }
                } else {
                    false
                }
            }
            .focusable()
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

        // Pure #000000 OLED Backdrop with soft central atmospheric blue-slate & warm spotlight glow
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("cluster_oled_backdrop")
        ) {
            drawRect(color = Color(0xFF000000))

            // Central moody slate-blue group backlight (like the reference TV screenshot)
            val groupCenter = Offset(size.width * 0.50f, size.height * 0.52f)
            val groupRadius = (size.minDimension * 0.72f).coerceAtLeast(200f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A3154).copy(alpha = 0.52f),
                        Color(0xFF0D1A30).copy(alpha = 0.28f),
                        Color(0x00000000)
                    ),
                    center = groupCenter,
                    radius = groupRadius
                ),
                radius = groupRadius,
                center = groupCenter
            )

            // Focused celebrity warm/domain spotlight halo
            val focusCenter = Offset(
                x = size.width * animatedGlowCenterX,
                y = size.height * 0.48f
            )
            val focusRadius = (size.minDimension * 0.46f).coerceAtLeast(140f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        activeNode.domain.primaryGlow.copy(alpha = 0.26f),
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

        // Top-Left "My Favourites" label (matching reference screenshot)
        Text(
            text = "My Favourites",
            color = Color(0xFFA8BED8),
            fontSize = 13.5.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 22.dp, top = 14.dp)
                .zIndex(14f)
        )

        // Overlapping Real Celebrity Cutouts in Tight Central Collage
        val cutoutWidthDp = if (maxWidth >= 720.dp) 204.dp else 156.dp
        val cutoutHeightDp = if (maxWidth >= 720.dp) 268.dp else 212.dp
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
                else -> 0.95f * node.depthTier.baseScale
            }
            val animatedScale by animateFloatAsState(
                targetValue = targetScale,
                animationSpec = spring(dampingRatio = 0.65f, stiffness = 200f),
                label = "cutout_scale_${node.id}"
            )

            val targetAlpha = when {
                isFocused -> 1.0f
                isShelfOpen -> 0.12f
                else -> 0.80f
            }
            val animatedAlpha by animateFloatAsState(
                targetValue = targetAlpha,
                animationSpec = tween(durationMillis = 180),
                label = "cutout_alpha_${node.id}"
            )

            val nodeZIndex = if (isFocused) 10f else node.depthTier.baseZIndex
            val parallaxDx = parallaxShiftX.value * node.depthTier.parallaxFactor
            val centerX = (stageWidthPx * animatedNormX) + parallaxDx
            val topY = (stageHeightPx * 0.85f) - cutoutHeightPx - (stageHeightPx * node.normalizedY * 0.42f)

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (centerX - cutoutWidthPx / 2f).roundToInt(),
                            y = topY.roundToInt()
                        )
                    }
                    .width(cutoutWidthDp)
                    .height(cutoutHeightDp * 1.22f)
                    .zIndex(nodeZIndex)
                    .graphicsLayer {
                        scaleX = animatedScale
                        scaleY = animatedScale
                        rotationY = parallaxTiltY.value * node.depthTier.parallaxFactor
                        cameraDistance = 14f * density.density
                    }
                    .then(
                        if (!isFocused) {
                            Modifier.blur(node.depthTier.inactiveBlurDp.dp)
                        } else {
                            Modifier
                        }
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        val clickedRingIdx = fullClusterRing.indexOfFirst { it.id == node.id }
                        if (isFocused) {
                            // Clicking the focused actor opens its specific content immediately
                            isShelfOpen = !isShelfOpen
                            triggerParallaxPulse(-1f)
                        } else if (clickedRingIdx >= 0) {
                            val delta = clickedRingIdx - focusedRingIndex
                            focusedRingIndex = clickedRingIdx
                            triggerParallaxPulse(if (delta >= 0) 1f else -1f)
                            // Also immediately sync the selected show if this character is a show
                            node.associatedShowId?.let { showId ->
                                shows.firstOrNull { it.id == showId }?.let { matchedShow ->
                                    onSwitchHeroShow(matchedShow)
                                }
                            }
                        }
                    }
                    .testTag("cluster_node_${node.id}")
            ) {
                // Real Background-Removed Celebrity PNG Cutout
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cutoutHeightDp)
                        .align(Alignment.TopCenter)
                ) {
                    CharacterCutoutRenderer(
                        node = node,
                        isFocused = isFocused,
                        alpha = animatedAlpha,
                        inactiveColorFilter = inactiveBlueMonochromeFilter,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Subtle Floor Reflection Plane
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cutoutHeightDp * 0.24f)
                        .align(Alignment.BottomCenter)
                        .blur(4.dp)
                        .graphicsLayer {
                            scaleY = -1f
                            alpha = (animatedAlpha * 0.12f).coerceIn(0f, 0.12f)
                        }
                        .testTag("cluster_reflection_${node.id}")
                ) {
                    CharacterCutoutRenderer(
                        node = node,
                        isFocused = false,
                        alpha = 1f,
                        inactiveColorFilter = inactiveBlueMonochromeFilter,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF000000),
                                        Color(0xCC000000),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }
        }

        // Top Title + Curved Smile-Arc Pointer Arrow (matching "The Flash ︶▸" in the reference screenshot)
        if (!isShelfOpen) {
            val titleCenterX = (stageWidthPx * animatedGlowCenterX).coerceIn(
                stageWidthPx * 0.26f,
                stageWidthPx * 0.74f
            )
            val titleTopY = (stageHeightPx * 0.08f).coerceAtLeast(18f)

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
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("cluster_hud_character_name")
                    )
                    Text(
                        text = activeNode.characterName.substringBefore(" •"),
                        color = Color(0xFFA8BED8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("cluster_hud_pillar_category")
                    )
                }

                // Curved white arc arrow `︶▸` beside the title (exactly like the reference screenshot)
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

        // Minimalist Touch/D-Pad Controls at Bottom Edge (kept ultra-clean & unobtrusive)
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
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xBB000000),
                                Color(0xFF000000)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                IconButton(
                    onClick = { stepClusterFocus(-1) },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("cluster_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous Character in Cluster",
                        tint = Color.White.copy(alpha = 0.65f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        onClick = {
                            isShelfOpen = true
                            triggerParallaxPulse(-1f)
                        },
                        color = Color(0x26FFFFFF),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.28f)),
                        modifier = Modifier.testTag("cluster_enter_shelf_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Open Selected Content",
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "OPEN ${activeNode.displayTitle.uppercase()}",
                                color = Color.White,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    // Subtle ring dots for direct selection
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.testTag("cluster_ring_dots")
                    ) {
                        fullClusterRing.forEachIndexed { idx, node ->
                            val selected = idx == focusedRingIndex
                            Box(
                                modifier = Modifier
                                    .height(5.dp)
                                    .width(if (selected) 16.dp else 5.dp)
                                    .background(
                                        color = if (selected) Color.White else Color.White.copy(alpha = 0.25f),
                                        shape = CircleShape
                                    )
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
                        .testTag("cluster_next_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next Character in Cluster",
                        tint = Color.White.copy(alpha = 0.65f)
                    )
                }
            }
        }

        // Cascading Content Shelf for the selected character (Strictly isolated per show/pillar!)
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
                .fillMaxWidth(if (maxWidth >= 720.dp) 0.70f else 0.84f)
                .fillMaxHeight()
                .zIndex(25f)
        ) {
            ClusterCascadingContentShelf(
                activeNode = activeNode,
                shows = shows,
                showCarousels = showCarousels,
                currentEpisodes = currentEpisodes,
                movies = movies,
                footballMatches = footballMatches,
                liveEpisode = liveEpisode,
                onCollapseShelf = {
                    isShelfOpen = false
                    triggerParallaxPulse(-0.7f)
                },
                onPlayEpisode = onPlayEpisode,
                onPlayFootballMirror = onPlayFootballMirror,
                onOpenShowSeasons = onOpenShowSeasons,
                onOpenMoviesHub = onOpenMoviesHub,
                onOpenFootballHub = onOpenFootballHub,
                onOpenLiveStudio = onOpenLiveStudio
            )
        }
    }
}

/**
 * Renders the real, background-removed lifelike celebrity PNG cutout directly from bundled drawables:
 * - Zero cartoon or procedural drawings.
 * - Inactive characters receive the cool slate-blue monochrome filter (like the reference screenshot).
 * - Focused character renders in full lifelike natural color.
 */
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

/**
 * Cascading Right-Side Content Shelf that opens when the user selects a character node.
 * Strictly isolates each show's episodes so selecting Cooku with Comali, Super Singer,
 * Celebrity MasterChef, or Neeya Naana NEVER falls back to Bigg Boss episodes!
 */
@Composable
private fun ClusterCascadingContentShelf(
    activeNode: ClusterNode,
    shows: List<Show>,
    showCarousels: Map<String, List<Episode>>,
    currentEpisodes: List<Episode>,
    movies: List<Episode>,
    footballMatches: List<FootballMatch>,
    liveEpisode: Episode?,
    onCollapseShelf: () -> Unit,
    onPlayEpisode: (Episode) -> Unit,
    onPlayFootballMirror: (FootballMatch, FootballStreamMirror) -> Unit,
    onOpenShowSeasons: (Show) -> Unit,
    onOpenMoviesHub: () -> Unit,
    onOpenFootballHub: () -> Unit,
    onOpenLiveStudio: () -> Unit
) {
    val matchedShow = remember(activeNode.associatedShowId, shows) {
        activeNode.associatedShowId?.let { targetId ->
            shows.firstOrNull { it.id == targetId }
        }
    }

    // Strict per-show episode resolution: NEVER fall back to currentEpisodes (Bigg Boss) for other shows!
    val shelfEpisodes = remember(activeNode.id, activeNode.associatedShowId, matchedShow, showCarousels, currentEpisodes, movies) {
        when (activeNode.id) {
            "anchor_vijay" -> movies
            "anchor_biggboss" -> {
                showCarousels["bigg_boss_tamil"]?.takeIf { it.isNotEmpty() }
                    ?: VijayTvRepository().getInitialEpisodes(VijayTvRepository.BIGG_BOSS_CATEGORY_URL)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xEB05070C),
                        Color(0xFF000000)
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
                        color = Color.White.copy(alpha = 0.68f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                OutlinedButton(
                    onClick = onCollapseShelf,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("cluster_shelf_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Return to Cluster",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("CLUSTER", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (activeNode.id == "anchor_bruno") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onOpenFootballHub,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = activeNode.domain.primaryGlow,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("shelf_open_football_hub_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsSoccer,
                            contentDescription = "Open Full Football Hub",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("OPEN FULL FOOTBALL HUB", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("shelf_football_matches_list")
                ) {
                    items(footballMatches, key = { it.id }) { match ->
                        val primaryMirror = match.mirrors.firstOrNull()
                        Surface(
                            onClick = {
                                if (primaryMirror != null) {
                                    onPlayFootballMirror(match, primaryMirror)
                                } else {
                                    onOpenFootballHub()
                                }
                            },
                            color = Color(0xFF0E131F),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, activeNode.domain.rimNeonColor.copy(alpha = 0.28f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("shelf_match_${match.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${match.tournament.uppercase()} • ${match.kickoffTime}",
                                        color = activeNode.domain.rimNeonColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${match.homeTeam} vs ${match.awayTeam}",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    if (primaryMirror != null) {
                                        Text(
                                            text = "${primaryMirror.originPortal} • ${primaryMirror.quality}",
                                            color = Color.White.copy(alpha = 0.65f),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                Surface(
                                    color = activeNode.domain.primaryGlow.copy(alpha = 0.18f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, activeNode.domain.primaryGlow)
                                ) {
                                    Text(
                                        text = match.scoreOrStatus,
                                        color = activeNode.domain.rimNeonColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val firstPlayable = shelfEpisodes.firstOrNull { !it.isLiveStream } ?: shelfEpisodes.firstOrNull()
                    if (firstPlayable != null) {
                        Button(
                            onClick = { onPlayEpisode(firstPlayable) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = activeNode.domain.primaryGlow,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("shelf_instant_play_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Latest",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (activeNode.id == "anchor_vijay") {
                                    "PLAY FEATURED MOVIE"
                                } else {
                                    "PLAY LATEST EPISODE"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    if (activeNode.id == "anchor_biggboss") {
                        OutlinedButton(
                            onClick = {
                                if (liveEpisode != null) {
                                    onPlayEpisode(liveEpisode)
                                } else {
                                    onOpenLiveStudio()
                                }
                            },
                            border = BorderStroke(1.dp, activeNode.domain.rimNeonColor),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("shelf_24x7_uncut_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.LiveTv,
                                contentDescription = "24/7 Uncut Feed",
                                tint = activeNode.domain.rimNeonColor,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("24/7 UNCUT FEED", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (activeNode.id == "anchor_vijay") {
                        OutlinedButton(
                            onClick = onOpenMoviesHub,
                            border = BorderStroke(1.dp, activeNode.domain.rimNeonColor.copy(alpha = 0.7f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("shelf_open_movies_hub_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Movie,
                                contentDescription = "Browse All Movies",
                                tint = activeNode.domain.rimNeonColor,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ALL MOVIES", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (matchedShow != null) {
                        OutlinedButton(
                            onClick = { onOpenShowSeasons(matchedShow) },
                            border = BorderStroke(1.dp, activeNode.domain.rimNeonColor.copy(alpha = 0.7f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("shelf_all_seasons_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Browse All Seasons",
                                tint = activeNode.domain.rimNeonColor,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ALL SEASONS & EPISODES", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                    items(shelfEpisodes.take(10), key = { it.url }) { ep ->
                        Surface(
                            onClick = { onPlayEpisode(ep) },
                            color = Color(0xFF0D111C),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                            modifier = Modifier
                                .fillMaxWidth()
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
                                        fontSize = 13.sp,
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
                                            text = "${ep.dateStr} • ${ep.qualityBadge}",
                                            color = Color.White.copy(alpha = 0.65f),
                                            fontSize = 11.sp,
                                            maxLines = 1
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = activeNode.domain.rimNeonColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
