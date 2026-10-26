package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.R
import com.example.model.ContinuitySession
import com.example.model.Episode
import com.example.model.FootballMatch
import com.example.model.FootballStreamMirror
import com.example.model.OfflineDownloadItem
import com.example.model.ScheduleItem
import com.example.model.WatchlistItem

private val ObsidianBg = Color(0xFF06070B)
private val CardSurface = Color(0xFF131724)
private val GlassSurface = Color(0xCC161B2A)
private val CrimsonAccent = Color(0xFFFF2A54)
private val CyanAccent = Color(0xFF00E5FF)
private val GoldAccent = Color(0xFFFFC857)
private val EmeraldAccent = Color(0xFF00E676)
private val PrimaryText = Color(0xFFF5F7FA)
private val SecondaryText = Color(0xFF949EB0)

/**
 * Collapsible Left-Rail Navigation Drawer for TV / 10-Foot / Wide Screens.
 * Expands automatically when any child item receives D-Pad focus, with 1.08x focus scale-up
 * and glowing accent border.
 */
@Composable
fun CollapsibleTvLeftRail(
    activeTab: String,
    onTabSelected: (String) -> Unit,
    myListCount: Int,
    onSearchClick: () -> Unit,
    onBrandLogoClick: () -> Unit = {}
) {
    var isRailExpanded by remember { mutableStateOf(false) }

    val navItems = listOf(
        Triple("Featured", "Home", Icons.Filled.Home),
        Triple("Episodes", "Shows", Icons.Filled.Tv),
        Triple("Movies", "Movies", Icons.Filled.MovieFilter),
        Triple("Football", "Football", Icons.Filled.SportsSoccer),
        Triple("My List", "Library", Icons.Filled.VideoLibrary)
    )

    Surface(
        color = Color(0xF2080A11),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        modifier = Modifier
            .fillMaxHeight()
            .width(if (isRailExpanded) 216.dp else 76.dp)
            .animateContentSize()
            .onFocusChanged { focusState ->
                isRailExpanded = focusState.hasFocus
            }
            .testTag("tv_collapsible_left_rail")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp, horizontal = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = if (isRailExpanded) Alignment.Start else Alignment.CenterHorizontally
        ) {
            // Top Brand Identity (Uploaded Rasagulla Icon & Logo)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .clickable {
                        isRailExpanded = !isRailExpanded
                        onBrandLogoClick()
                    }
                    .testTag("tv_rail_brand_logo")
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_rasagulla_icon),
                    contentDescription = "Rasagulla App Icon",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                if (isRailExpanded) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(
                        painter = painterResource(id = R.drawable.img_rasagulla_logo),
                        contentDescription = "Rasagulla Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.height(34.dp)
                    )
                }
            }

            // Center Navigation Items
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                navItems.forEach { (tabKey, label, icon) ->
                    val isSelected = activeTab == tabKey
                    var isFocused by remember { mutableStateOf(false) }

                    Surface(
                        color = when {
                            isFocused -> CrimsonAccent.copy(alpha = 0.25f)
                            isSelected -> Color(0xFF1A2032)
                            else -> Color.Transparent
                        },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            width = if (isFocused) 2.dp else if (isSelected) 1.dp else 0.dp,
                            color = when {
                                isFocused -> CyanAccent
                                isSelected -> CrimsonAccent
                                else -> Color.Transparent
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                scaleX = if (isFocused) 1.08f else 1.0f
                                scaleY = if (isFocused) 1.08f else 1.0f
                            }
                            .onFocusChanged { isFocused = it.isFocused }
                            .focusable()
                            .clickable { onTabSelected(tabKey) }
                            .testTag("tv_rail_tab_${label.lowercase()}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = if (isRailExpanded) Arrangement.Start else Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = when {
                                    isFocused -> CyanAccent
                                    isSelected -> CrimsonAccent
                                    else -> SecondaryText
                                },
                                modifier = Modifier.size(22.dp)
                            )
                            if (isRailExpanded) {
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(
                                    text = label,
                                    color = if (isSelected || isFocused) PrimaryText else SecondaryText,
                                    fontSize = 13.5.sp,
                                    fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                if (tabKey == "My List" && myListCount > 0) {
                                    Surface(
                                        color = CrimsonAccent,
                                        shape = CircleShape
                                    ) {
                                        Text(
                                            text = "$myListCount",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Search & Expand Button
            var isSearchFocused by remember { mutableStateOf(false) }
            Surface(
                color = if (isSearchFocused) CyanAccent.copy(alpha = 0.2f) else Color(0xFF141826),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (isSearchFocused) CyanAccent else Color.White.copy(alpha = 0.1f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = if (isSearchFocused) 1.08f else 1.0f
                        scaleY = if (isSearchFocused) 1.08f else 1.0f
                    }
                    .onFocusChanged { isSearchFocused = it.isFocused }
                    .focusable()
                    .clickable { onSearchClick() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (isRailExpanded) Arrangement.Start else Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search & Phone Remote",
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    if (isRailExpanded) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Search / Remote",
                            color = PrimaryText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dedicated Football Streaming Tab:
 * - Concurrent Match Harvester across streamed.st/category/football, timst.top/streams, streamcorner.st, ppv.st
 * - Match Fixture Aggregation grouped by Tournament (Premier League, UEFA Champions League, La Liga, Serie A, ISL)
 * - Bitrate & Mirror Quick-Selector (1080p 60fps, 720p 30fps) with live ping latency checks & WebRTC P2P peer mesh status
 */
@Composable
fun FootballLiveHubView(
    matches: List<FootballMatch>,
    selectedTournament: String,
    p2pMeshEnabled: Boolean,
    onSelectTournament: (String) -> Unit,
    onToggleP2pMesh: () -> Unit,
    onRefreshMatches: () -> Unit,
    onPlayMirror: (FootballMatch, FootballStreamMirror) -> Unit
) {
    val tournaments = listOf(
        "All Tournaments",
        "Premier League",
        "UEFA Champions League",
        "La Liga",
        "Serie A",
        "Bundesliga",
        "Indian Super League"
    )

    val filteredMatches = remember(matches, selectedTournament) {
        if (selectedTournament == "All Tournaments") {
            matches
        } else {
            matches.filter { it.tournament.equals(selectedTournament, ignoreCase = true) }
        }
    }

    val groupedByTournament = remember(filteredMatches) {
        filteredMatches.groupBy { it.tournament }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("football_hub_list"),
        contentPadding = PaddingValues(bottom = 110.dp)
    ) {
        // Minimalist Apple TV Sports Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Live Football",
                        color = PrimaryText,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${matches.count { it.isLive }} Live Now • Verified 1080p 60fps Feeds",
                        color = SecondaryText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(
                    onClick = onRefreshMatches,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .testTag("football_refresh_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Refresh Live Matches",
                        tint = PrimaryText
                    )
                }
            }
        }

        // Tournament Selector Chips
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tournaments) { tourney ->
                    val isSelected = tourney == selectedTournament
                    var isFocused by remember { mutableStateOf(false) }
                    Surface(
                        color = when {
                            isSelected -> PrimaryText
                            isFocused -> Color(0xFF283046)
                            else -> Color.White.copy(alpha = 0.07f)
                        },
                        shape = RoundedCornerShape(22.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isFocused) CyanAccent else Color.Transparent
                        ),
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = if (isFocused) 1.06f else 1.0f
                                scaleY = if (isFocused) 1.06f else 1.0f
                            }
                            .onFocusChanged { isFocused = it.isFocused }
                            .focusable()
                            .clickable { onSelectTournament(tourney) }
                    ) {
                        Text(
                            text = tourney,
                            color = if (isSelected) Color.Black else PrimaryText,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Competition Sections & Live Fixtures
        groupedByTournament.forEach { (tournamentName, tourneyMatches) ->
            item(key = "header_$tournamentName") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = tournamentName,
                        color = PrimaryText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${tourneyMatches.size} ${if (tourneyMatches.size == 1) "Match" else "Matches"}",
                        color = SecondaryText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            items(tourneyMatches, key = { it.id }) { match ->
                FootballFixtureCard(
                    match = match,
                    onPlayMirror = { mirror -> onPlayMirror(match, mirror) }
                )
            }
        }
    }
}

@Composable
private fun FootballFixtureCard(
    match: FootballMatch,
    onPlayMirror: (FootballStreamMirror) -> Unit
) {
    var isCardFocused by remember { mutableStateOf(false) }
    val primaryMirror = match.mirrors.firstOrNull()

    Surface(
        color = Color(0xFF10131C),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            if (isCardFocused) 2.dp else 1.dp,
            if (isCardFocused) Color.White else Color.White.copy(alpha = 0.08f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .graphicsLayer {
                scaleX = if (isCardFocused) 1.02f else 1.0f
                scaleY = if (isCardFocused) 1.02f else 1.0f
            }
            .onFocusChanged { isCardFocused = it.isFocused }
            .focusable()
            .clickable {
                if (primaryMirror != null) {
                    onPlayMirror(primaryMirror)
                }
            }
            .testTag("football_match_card_${match.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(144.dp)
            ) {
                if (match.backdropUrl.isNotBlank()) {
                    AsyncImage(
                        model = match.backdropUrl,
                        contentDescription = "${match.homeTeam} vs ${match.awayTeam}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0x6606070B),
                                    Color(0xF210131C)
                                )
                            )
                        )
                )

                // Top Row: Live Status Pill & Venue
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = if (match.isLive) CrimsonAccent else Color.White.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            if (match.isLive) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                            }
                            Text(
                                text = match.kickoffTime,
                                color = Color.White,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = match.venue,
                        color = SecondaryText,
                        fontSize = 11.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Center Scoreboard
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = match.homeTeam,
                        color = PrimaryText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        color = Color(0xCC06080E),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
                    ) {
                        Text(
                            text = match.scoreOrStatus,
                            color = PrimaryText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    Text(
                        text = match.awayTeam,
                        color = PrimaryText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Stream Mirror Quick-Selector Pills
            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(match.mirrors, key = { it.id }) { mirror ->
                    var isMirrorFocused by remember { mutableStateOf(false) }
                    Surface(
                        color = if (isMirrorFocused) PrimaryText else Color.White.copy(alpha = 0.07f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = if (isMirrorFocused) 1.05f else 1.0f
                                scaleY = if (isMirrorFocused) 1.05f else 1.0f
                            }
                            .onFocusChanged { isMirrorFocused = it.isFocused }
                            .focusable()
                            .clickable { onPlayMirror(mirror) }
                            .testTag("football_mirror_${mirror.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = "Play ${mirror.quality}",
                                tint = if (isMirrorFocused) Color.Black else EmeraldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${mirror.quality} • ${mirror.originPortal} (${mirror.pingMs}ms)",
                                color = if (isMirrorFocused) Color.Black else PrimaryText,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Unified Library Hub ("My List" / Library Tab):
 * 1. Cross-Device Continuity & Ecosystem Sync (Local Network Handoff + Phone as Virtual Remote & Keyboard)
 * 2. Smart Episode Calendar (Star Vijay, Zee Tamil, Sun TV, Sony Vizha) with Verified Live Push Notifications
 * 3. Offline Chunked Downloader (.TS -> .MP4) with progress & offline playback
 * 4. Saved Watchlist (My List)
 */
@Composable
fun UnifiedLibraryHubView(
    calendarSchedule: List<ScheduleItem>,
    selectedChannel: String,
    onSelectChannel: (String) -> Unit,
    onToggleScheduleNotification: (String) -> Unit,
    onTriggerVerifiedLiveAlert: (ScheduleItem) -> Unit,
    onPlayScheduleItem: (ScheduleItem) -> Unit,
    offlineDownloads: List<OfflineDownloadItem>,
    onStartSampleDownload: () -> Unit,
    onPlayOfflineDownload: (OfflineDownloadItem) -> Unit,
    onRemoveOfflineDownload: (String) -> Unit,
    continuitySession: ContinuitySession?,
    onHandoffToTv: () -> Unit,
    onSendVirtualRemoteText: (String) -> Unit,
    myList: List<WatchlistItem>,
    allEpisodes: List<Episode>,
    onEpisodeClick: (Episode) -> Unit,
    onRemoveFromWatchlist: (WatchlistItem) -> Unit
) {
    val channels = listOf("All Channels", "Star Vijay", "Zee Tamil", "Sun TV", "Sony Vizha")
    var remoteKeyboardInput by remember { mutableStateOf("") }
    var showVirtualRemoteSheet by remember { mutableStateOf(false) }

    // Runtime permission launcher for POST_NOTIFICATIONS on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    val filteredSchedule = remember(calendarSchedule, selectedChannel) {
        if (selectedChannel == "All Channels") {
            calendarSchedule
        } else {
            calendarSchedule.filter { it.channel.equals(selectedChannel, ignoreCase = true) }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("unified_library_hub"),
        contentPadding = PaddingValues(bottom = 110.dp)
    ) {
        // 1. Cross-Device Continuity & Ecosystem Sync Banner
        item {
            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("continuity_handoff_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CastConnected,
                                contentDescription = "Cross-Device Continuity",
                                tint = CyanAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CROSS-DEVICE CONTINUITY & LAN HANDOFF",
                                color = CyanAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp
                            )
                        }

                        Surface(
                            color = EmeraldAccent.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "CLOUD + LAN SYNCED",
                                color = EmeraldAccent,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = continuitySession?.activeCleanTag ?: "Bigg Boss Season 10 • Day 49 • Vijay TV • HD",
                        color = PrimaryText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Detected on ${continuitySession?.deviceName ?: "Living Room 4K OLED TV"} (${continuitySession?.localIpAddress ?: "192.168.1.48"}) • Resume point synced",
                        color = SecondaryText,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = onHandoffToTv,
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("continue_on_living_room_tv_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Tv,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Continue on Living Room TV",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        OutlinedButton(
                            onClick = { showVirtualRemoteSheet = !showVirtualRemoteSheet },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, CyanAccent),
                            modifier = Modifier.testTag("virtual_remote_keyboard_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Keyboard,
                                contentDescription = "Phone as Virtual Remote",
                                tint = CyanAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Phone Remote",
                                color = CyanAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Expandable Phone as Virtual Remote & Keyboard Input
                    AnimatedVisibility(visible = showVirtualRemoteSheet) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            OutlinedTextField(
                                value = remoteKeyboardInput,
                                onValueChange = { remoteKeyboardInput = it },
                                label = { Text("Push text to Living Room TV Search / Live Chat", color = SecondaryText, fontSize = 12.sp) },
                                singleLine = true,
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            if (remoteKeyboardInput.isNotBlank()) {
                                                onSendVirtualRemoteText(remoteKeyboardInput)
                                                remoteKeyboardInput = ""
                                            }
                                        }
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Push to TV", tint = CyanAccent)
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanAccent,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    focusedTextColor = PrimaryText,
                                    unfocusedTextColor = PrimaryText
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        // 2. Smart Episode Calendar Header & Channel Filter
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = "Smart Episode Calendar",
                            tint = GoldAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Smart Episode Calendar",
                                color = PrimaryText,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Star Vijay • Zee Tamil • Sun TV • Sony Vizha (Push Alert when verified playable)",
                                color = SecondaryText,
                                fontSize = 11.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(channels) { channel ->
                        val isSelected = channel == selectedChannel
                        Surface(
                            color = if (isSelected) CrimsonAccent else CardSurface,
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(1.dp, if (isSelected) CrimsonAccent else Color.White.copy(alpha = 0.12f)),
                            modifier = Modifier.clickable { onSelectChannel(channel) }
                        ) {
                            Text(
                                text = channel,
                                color = PrimaryText,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }

        // Smart Calendar Schedule Cards
        items(filteredSchedule, key = { it.id }) { item ->
            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp)
                    .clickable { onPlayScheduleItem(item) }
                    .testTag("calendar_item_${item.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = item.thumbnailUrl,
                        contentDescription = item.showTitle,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(width = 96.dp, height = 62.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1B2030))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = Color(0xFF23293D),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "${item.dayLabel} • ${item.telecastTime}",
                                    color = GoldAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            if (item.isLiveVerified) {
                                Surface(
                                    color = EmeraldAccent.copy(alpha = 0.18f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "✓ ${item.activeMirrorsCount} Mirrors Verified",
                                        color = EmeraldAccent,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.showTitle,
                            color = PrimaryText,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = item.cleanTag,
                            color = CyanAccent,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Push Notification Bell + Instant Verified Alert Button
                    IconButton(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            onToggleScheduleNotification(item.id)
                            onTriggerVerifiedLiveAlert(item)
                        },
                        modifier = Modifier.testTag("calendar_notify_${item.id}")
                    ) {
                        Icon(
                            imageVector = if (item.notifyOnLive) Icons.Filled.NotificationsActive else Icons.Outlined.Notifications,
                            contentDescription = "Toggle Live Push Alert",
                            tint = if (item.notifyOnLive) GoldAccent else SecondaryText
                        )
                    }
                }
            }
        }

        // 3. Offline Chunked Downloader Section (.TS -> .MP4)
        item {
            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Offline Chunked Downloader (.TS → .MP4)",
                                color = PrimaryText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Concurrently fetches HLS .ts segments in background and stitches into local .mp4",
                                color = SecondaryText,
                                fontSize = 11.5.sp
                            )
                        }
                        Button(
                            onClick = onStartSampleDownload,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("download_latest_mp4_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Download,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Fetch MP4",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    if (offlineDownloads.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        offlineDownloads.forEach { dl ->
                            Surface(
                                color = Color(0xFF1A2032),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        if (dl.status == "COMPLETED") {
                                            onPlayOfflineDownload(dl)
                                        }
                                    }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = dl.cleanTag,
                                                color = PrimaryText,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${dl.status} • Chunks: ${dl.chunksDownloaded}/${dl.totalChunks} • ${dl.fileSizeMb} MB",
                                                color = if (dl.status == "COMPLETED") EmeraldAccent else CyanAccent,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        IconButton(onClick = { onRemoveOfflineDownload(dl.id) }) {
                                            Icon(
                                                imageVector = Icons.Filled.DeleteOutline,
                                                contentDescription = "Delete Offline MP4",
                                                tint = SecondaryText
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = { (dl.progressPercent / 100f).coerceIn(0f, 1f) },
                                        color = if (dl.status == "COMPLETED") EmeraldAccent else CyanAccent,
                                        trackColor = Color.White.copy(alpha = 0.12f),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(5.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Saved Watchlist (My List)
        item {
            Text(
                text = "MY SAVED WATCHLIST (${myList.size})",
                color = GoldAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
            )
        }

        if (myList.isEmpty()) {
            item {
                Text(
                    text = "Tap the + button on any show, movie, or episode to sync it across your TV and Phone.",
                    color = SecondaryText,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                )
            }
        } else {
            items(myList, key = { it.episodeUrl }) { item ->
                val ep = allEpisodes.firstOrNull { it.url == item.episodeUrl }
                    ?: Episode(item.episodeTitle, item.episodeUrl, item.dateStr, item.thumbnailUrl)
                Surface(
                    color = CardSurface,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                        .clickable { onEpisodeClick(ep) }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = item.thumbnailUrl,
                            contentDescription = item.episodeTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(width = 88.dp, height = 54.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.episodeTitle,
                                color = PrimaryText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = ep.enrichedMetadata?.cleanTag ?: item.dateStr,
                                color = CyanAccent,
                                fontSize = 11.5.sp
                            )
                        }
                        IconButton(onClick = { onRemoveFromWatchlist(item) }) {
                            Icon(
                                imageVector = Icons.Filled.BookmarkRemove,
                                contentDescription = "Remove from Watchlist",
                                tint = CrimsonAccent
                            )
                        }
                    }
                }
            }
        }
    }
}
