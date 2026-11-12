package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
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
    BackHandler(enabled = isRailExpanded) {
        isRailExpanded = false
    }

    val navItems = listOf(
        Triple("Featured", "Home", Icons.Filled.Home),
        Triple("Episodes", "TV Shows", Icons.Filled.Tv),
        Triple("Movies", "Movies", Icons.Filled.MovieFilter),
        Triple("Live 24/7", "24/7 Live", Icons.Filled.LiveTv),
        Triple("My List", "My Library", Icons.Filled.VideoLibrary)
    )

    Surface(
        color = Color(0xF5080A11),
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        isRailExpanded = !isRailExpanded
                        onBrandLogoClick()
                    }
                    .testTag("tv_rail_brand_logo")
            ) {
                if (!isRailExpanded) {
                    Image(
                        painter = painterResource(id = R.drawable.img_rasagulla_icon),
                        contentDescription = "Rasagulla App Icon",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.img_rasagulla_logo),
                        contentDescription = "Rasagulla Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.height(46.dp)
                    )
                }
            }

            // Center Navigation Items (Single focus node per item via Surface(onClick = ...))
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                navItems.forEach { (tabKey, label, icon) ->
                    val isSelected = activeTab == tabKey
                    var isFocused by remember { mutableStateOf(false) }

                    Surface(
                        onClick = { onTabSelected(tabKey) },
                        color = when {
                            isFocused -> CrimsonAccent
                            isSelected -> Color(0xFF1D2438)
                            else -> Color.Transparent
                        },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            width = if (isFocused) 2.5.dp else if (isSelected) 1.dp else 0.dp,
                            color = when {
                                isFocused -> Color.White
                                isSelected -> CrimsonAccent
                                else -> Color.Transparent
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                scaleX = if (isFocused) 1.06f else 1.0f
                                scaleY = if (isFocused) 1.06f else 1.0f
                            }
                            .onFocusChanged { isFocused = it.isFocused }
                            .testTag("tv_rail_tab_${label.lowercase().replace(" ", "_")}")
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
                                    isFocused -> Color.White
                                    isSelected -> CrimsonAccent
                                    else -> SecondaryText
                                },
                                modifier = Modifier.size(22.dp)
                            )
                            if (isRailExpanded) {
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(
                                    text = label,
                                    color = if (isSelected || isFocused) Color.White else SecondaryText,
                                    fontSize = 13.5.sp,
                                    fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                if (tabKey == "My List" && myListCount > 0) {
                                    Surface(
                                        color = if (isFocused) Color.White else CrimsonAccent,
                                        shape = CircleShape
                                    ) {
                                        Text(
                                            text = "$myListCount",
                                            color = if (isFocused) Color.Black else Color.White,
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

            // Bottom Search Button (Single focus node via Surface(onClick = ...))
            var isSearchFocused by remember { mutableStateOf(false) }
            Surface(
                onClick = onSearchClick,
                color = if (isSearchFocused) CyanAccent else Color(0xFF141826),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    if (isSearchFocused) 2.5.dp else 1.dp,
                    if (isSearchFocused) Color.White else Color.White.copy(alpha = 0.1f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = if (isSearchFocused) 1.06f else 1.0f
                        scaleY = if (isSearchFocused) 1.06f else 1.0f
                    }
                    .onFocusChanged { isSearchFocused = it.isFocused }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (isRailExpanded) Arrangement.Start else Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search Shows & Movies",
                        tint = if (isSearchFocused) Color.Black else CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    if (isRailExpanded) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Search",
                            color = if (isSearchFocused) Color.Black else PrimaryText,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * FotMob-Style Football Scores & Highlights Hub:
 * - Covers Big 5 European Leagues (Premier League, La Liga, Serie A, Bundesliga, Ligue 1)
 *   + UEFA European Competitions (UCL, UEL, Conference League)
 * - Clickable match results with goalscorers breakdown and FotMob-style stat comparison bars
 * - 1-Tap playback of official match highlights from Stadium Astro, YouTube, LaLiga & Serie A Official
 * - 10-foot TV D-Pad remote optimized with 1.04x focus scaling and glowing borders
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
        "All Leagues",
        "Premier League",
        "La Liga",
        "Serie A",
        "Bundesliga",
        "Ligue 1",
        "UEFA Champions League",
        "UEFA Europa League",
        "UEFA Conference League"
    )

    var selectedMatchForDetails by remember { mutableStateOf<FootballMatch?>(null) }

    val filteredMatches = remember(matches, selectedTournament) {
        if (selectedTournament == "All Leagues" || selectedTournament == "All Tournaments") {
            matches
        } else {
            matches.filter { it.tournament.equals(selectedTournament, ignoreCase = true) }
        }
    }

    val groupedByTournament = remember(filteredMatches) {
        filteredMatches.groupBy { it.tournament }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("football_hub_list"),
            contentPadding = PaddingValues(bottom = 110.dp)
        ) {
            // FotMob Sports Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Football Scores & Results",
                                color = PrimaryText,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp
                            )
                            Surface(
                                color = EmeraldAccent.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, EmeraldAccent.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = "FOTMOB",
                                    color = EmeraldAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Big 5 Leagues • UCL • UEL • Conference League • Match Stats",
                            color = SecondaryText,
                            fontSize = 12.5.sp,
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
                            contentDescription = "Refresh Scores & Highlights",
                            tint = PrimaryText
                        )
                    }
                }
            }

            // Tournament Filter Chips (D-Pad remote friendly)
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tournaments) { tourney ->
                        val isSelected = tourney == selectedTournament ||
                                (tourney == "All Leagues" && selectedTournament == "All Tournaments")
                        var isChipFocused by remember { mutableStateOf(false) }

                        Surface(
                            color = when {
                                isSelected -> PrimaryText
                                isChipFocused -> Color(0xFF283046)
                                else -> Color.White.copy(alpha = 0.07f)
                            },
                            shape = RoundedCornerShape(22.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isChipFocused) CyanAccent else Color.Transparent
                            ),
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = if (isChipFocused) 1.06f else 1.0f
                                    scaleY = if (isChipFocused) 1.06f else 1.0f
                                }
                                .onFocusChanged { isChipFocused = it.isFocused }
                                .focusable()
                                .clickable { onSelectTournament(tourney) }
                                .testTag("football_league_chip_${tourney.lowercase().replace(" ", "_")}")
                        ) {
                            Text(
                                text = tourney,
                                color = if (isSelected) Color.Black else PrimaryText,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Grouped Match Sections
            groupedByTournament.forEach { (tournamentName, tourneyMatches) ->
                item(key = "header_$tournamentName") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(CyanAccent, CircleShape)
                            )
                            Text(
                                text = tournamentName,
                                color = PrimaryText,
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "${tourneyMatches.size} ${if (tourneyMatches.size == 1) "Fixture" else "Fixtures"}",
                            color = SecondaryText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                items(tourneyMatches, key = { it.id }) { match ->
                    FotMobMatchCard(
                        match = match,
                        onPlayLiveStream = { mirror -> onPlayMirror(match, mirror) },
                        onOpenDetails = { selectedMatchForDetails = match }
                    )
                }
            }
        }

        // FotMob Match Detail Modal Dialog
        selectedMatchForDetails?.let { match ->
            FotMobMatchDetailDialog(
                match = match,
                onPlayMirror = { mirror ->
                    selectedMatchForDetails = null
                    onPlayMirror(match, mirror)
                },
                onDismiss = { selectedMatchForDetails = null }
            )
        }
    }
}

/**
 * FotMob-style Match Fixture Card:
 * - Shows Home vs Away with club badges, bold score, kickoff status, and goalscorers summary
 * - 1-Tap "Watch Live" stream button powered by ppv.st & streamed.st live embeds
 * - Tapping card or "Stats" opens comprehensive FotMob result sheet with stats & goal timeline
 */
@Composable
private fun FotMobMatchCard(
    match: FootballMatch,
    onPlayLiveStream: (FootballStreamMirror) -> Unit,
    onOpenDetails: () -> Unit
) {
    var isCardFocused by remember { mutableStateOf(false) }

    Surface(
        color = Color(0xFF101420),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            if (isCardFocused) 2.dp else 1.dp,
            if (isCardFocused) CyanAccent else Color.White.copy(alpha = 0.08f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .graphicsLayer {
                scaleX = if (isCardFocused) 1.02f else 1.0f
                scaleY = if (isCardFocused) 1.02f else 1.0f
            }
            .onFocusChanged { isCardFocused = it.isFocused }
            .focusable()
            .clickable { onOpenDetails() }
            .testTag("football_match_card_${match.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Tournament & Match Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${match.tournament} • ${match.venue.substringBefore(",")}",
                    color = SecondaryText,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    color = if (match.isLive) CrimsonAccent.copy(alpha = 0.22f) else Color(0x33283046),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, if (match.isLive) CrimsonAccent else Color.White.copy(alpha = 0.15f))
                ) {
                    Text(
                        text = if (match.isLive) "● LIVE  ${match.kickoffTime}" else match.kickoffTime,
                        color = if (match.isLive) CrimsonAccent else PrimaryText,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scoreboard Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Team
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    TeamInitialsBadge(teamName = match.homeTeam, isHome = true)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = match.homeTeam,
                        color = PrimaryText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Center Score
                Surface(
                    color = Color(0xFF181F2E),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                    modifier = Modifier.padding(horizontal = 10.dp)
                ) {
                    Text(
                        text = match.scoreOrStatus,
                        color = PrimaryText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                // Away Team
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = match.awayTeam,
                        color = PrimaryText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    TeamInitialsBadge(teamName = match.awayTeam, isHome = false)
                }
            }

            // Goalscorers Summary Preview (FotMob style)
            if (match.goalScorers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x33000000), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = match.goalScorers.joinToString("  •  "),
                        color = SecondaryText,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Row: Possession Info + Live Stream & Stats Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (match.homeStats.isNotEmpty()) {
                        "Possession ${match.homeStats["Possession"] ?: "50%"} • ${match.awayStats["Possession"] ?: "50%"}"
                    } else {
                        "Live HD Stream & Match Report"
                    },
                    color = SecondaryText,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val primaryMirror = match.mirrors.firstOrNull()
                    if (primaryMirror != null) {
                        Surface(
                            onClick = { onPlayLiveStream(primaryMirror) },
                            color = PrimaryText,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.testTag("football_watch_live_btn_${match.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Watch Live Stream",
                                    tint = Color.Black,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Watch Live",
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }

                    Surface(
                        onClick = onOpenDetails,
                        color = Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)),
                        modifier = Modifier.testTag("football_details_btn_${match.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.BarChart,
                                contentDescription = "View Match Stats",
                                tint = CyanAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Stats",
                                color = PrimaryText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * FotMob Club Avatar Badge with 3-letter abbreviation & Club Theme Color
 */
@Composable
private fun TeamInitialsBadge(teamName: String, isHome: Boolean) {
    val abbreviation = remember(teamName) {
        val parts = teamName.split(" ")
        if (parts.size >= 2) {
            (parts[0].take(1) + parts[1].take(2)).uppercase()
        } else {
            teamName.take(3).uppercase()
        }
    }

    val badgeColor = remember(teamName) {
        when {
            teamName.contains("Arsenal", ignoreCase = true) -> Color(0xFFEF0107)
            teamName.contains("Chelsea", ignoreCase = true) -> Color(0xFF034694)
            teamName.contains("City", ignoreCase = true) -> Color(0xFF6CABDD)
            teamName.contains("Liverpool", ignoreCase = true) -> Color(0xFFC8102E)
            teamName.contains("United", ignoreCase = true) -> Color(0xFFDA291C)
            teamName.contains("Real Madrid", ignoreCase = true) -> Color(0xFFFEE100)
            teamName.contains("Barcelona", ignoreCase = true) -> Color(0xFFA50044)
            teamName.contains("Inter", ignoreCase = true) -> Color(0xFF001EA0)
            teamName.contains("Juventus", ignoreCase = true) -> Color(0xFFFFFFFF)
            teamName.contains("Milan", ignoreCase = true) -> Color(0xFFFB090B)
            teamName.contains("Bayern", ignoreCase = true) -> Color(0xFFDC052D)
            teamName.contains("Dortmund", ignoreCase = true) -> Color(0xFFFDE100)
            teamName.contains("PSG", ignoreCase = true) || teamName.contains("Paris", ignoreCase = true) -> Color(0xFF004170)
            else -> if (isHome) CyanAccent else GoldAccent
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(32.dp)
            .background(badgeColor.copy(alpha = 0.25f), CircleShape)
            .border(1.5.dp, badgeColor, CircleShape)
    ) {
        Text(
            text = abbreviation,
            color = PrimaryText,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Black
        )
    }
}

/**
 * Comprehensive FotMob Match Results & Statistics Dialog
 * - Full goalscorer timeline with minute markers
 * - Live Stream server selector buttons (PPV.ST & Streamed.st)
 * - Dual-color horizontal comparison progress bars for possession, shots, xG, corners, fouls
 */
@Composable
private fun FotMobMatchDetailDialog(
    match: FootballMatch,
    onPlayMirror: (FootballStreamMirror) -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = Color(0xFF0C101A),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("fotmob_match_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header: Tournament + Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = match.tournament.uppercase(),
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = match.venue,
                            color = SecondaryText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close Match Details",
                            tint = PrimaryText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scoreboard Banner
                Surface(
                    color = Color(0xFF141A28),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Home
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                TeamInitialsBadge(teamName = match.homeTeam, isHome = true)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = match.homeTeam,
                                    color = PrimaryText,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Big Score
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = match.scoreOrStatus,
                                    color = PrimaryText,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = match.kickoffTime,
                                    color = EmeraldAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Away
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                TeamInitialsBadge(teamName = match.awayTeam, isHome = false)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = match.awayTeam,
                                    color = PrimaryText,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // Live Match Stream Mirrors (PPV.ST & Streamed.st)
                if (match.mirrors.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "WATCH LIVE STREAM",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        match.mirrors.take(3).forEachIndexed { index, mirror ->
                            val isPrimary = index == 0
                            Surface(
                                onClick = { onPlayMirror(mirror) },
                                color = if (isPrimary) PrimaryText else Color(0xFF1A2234),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(
                                    1.dp,
                                    if (isPrimary) PrimaryText else Color.White.copy(alpha = 0.16f)
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PlayArrow,
                                        contentDescription = mirror.label,
                                        tint = if (isPrimary) Color.Black else PrimaryText,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (index == 0) "Live HD 1" else "Server ${index + 1}",
                                        color = if (isPrimary) Color.Black else PrimaryText,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Goalscorers Breakdown
                if (match.goalScorers.isNotEmpty()) {
                    Text(
                        text = "GOALSCORERS",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF141A28), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        match.goalScorers.forEach { scorer ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = scorer,
                                    color = PrimaryText,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Match Statistics Bars (FotMob Style)
                if (match.homeStats.isNotEmpty() && match.awayStats.isNotEmpty()) {
                    Text(
                        text = "MATCH STATISTICS",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val statKeys = listOf("Possession", "Shots", "Shots on Target", "xG", "Corners", "Fouls")
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF141A28), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        statKeys.forEach { key ->
                            val homeVal = match.homeStats[key] ?: "-"
                            val awayVal = match.awayStats[key] ?: "-"
                            FotMobStatRow(label = key, homeVal = homeVal, awayVal = awayVal)
                        }
                    }
                }
            }
        }
    }
}

/**
 * FotMob Stat Comparison Row with Dual Horizontal Progress Bars
 */
@Composable
private fun FotMobStatRow(label: String, homeVal: String, awayVal: String) {
    val homeNum = homeVal.replace("%", "").toFloatOrNull() ?: 50f
    val awayNum = awayVal.replace("%", "").toFloatOrNull() ?: 50f
    val total = (homeNum + awayNum).coerceAtLeast(1f)
    val homeRatio = (homeNum / total).coerceIn(0.1f, 0.9f)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = homeVal,
                color = PrimaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = SecondaryText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = awayVal,
                color = PrimaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape)
                .background(Color(0xFF222B3D))
        ) {
            Box(
                modifier = Modifier
                    .weight(homeRatio)
                    .fillMaxHeight()
                    .background(CyanAccent)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Box(
                modifier = Modifier
                    .weight(1f - homeRatio)
                    .fillMaxHeight()
                    .background(GoldAccent)
            )
        }
    }
}

/**
 * Apple TV+ Inspired Family Library Hub ("My List" / Library Tab):
 * Designed for effortless everyday use by Mom & family:
 * 1. My Saved Shows & Movies (Watchlist) right at the top with large thumbnails & 1-tap Play
 * 2. Today's TV Serial Timings (Star Vijay, Zee Tamil, Sun TV, Sony Vizha) with simple reminder bells
 * 3. Saved to Watch Offline (Simple 1-tap episode saver without technical jargon)
 * 4. Watch on Living Room TV (Simple 1-tap TV handoff & TV keyboard)
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
        // Clean Apple TV+ Library Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Library & TV Guide",
                    color = PrimaryText,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your saved serials, daily TV timings & offline episodes",
                    color = SecondaryText,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 1. My Saved Shows & Movies (Watchlist) — First & easiest to reach!
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "My Saved Shows & Movies",
                        color = PrimaryText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (myList.isNotEmpty()) {
                    Text(
                        text = "${myList.size} Saved",
                        color = SecondaryText,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        if (myList.isEmpty()) {
            item {
                Surface(
                    color = Color(0xFF101522),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(44.dp)
                                .background(GoldAccent.copy(alpha = 0.14f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.BookmarkAdd,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Your saved list is empty",
                                color = PrimaryText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Tap '+ My List' on any serial or movie so you can find it right here anytime.",
                                color = SecondaryText,
                                fontSize = 12.5.sp
                            )
                        }
                    }
                }
            }
        } else {
            items(myList, key = { it.episodeUrl }) { item ->
                val ep = allEpisodes.firstOrNull { it.url == item.episodeUrl }
                    ?: Episode(item.episodeTitle, item.episodeUrl, item.dateStr, item.thumbnailUrl)
                Surface(
                    color = Color(0xFF101522),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
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
                                .size(width = 104.dp, height = 62.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1B2030))
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.episodeTitle,
                                color = PrimaryText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = ep.enrichedMetadata?.cleanTag ?: item.dateStr,
                                color = SecondaryText,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Surface(
                            color = PrimaryText,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.Black,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Play",
                                    color = Color.Black,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        IconButton(onClick = { onRemoveFromWatchlist(item) }) {
                            Icon(
                                imageVector = Icons.Filled.BookmarkRemove,
                                contentDescription = "Remove from Saved",
                                tint = SecondaryText
                            )
                        }
                    }
                }
            }
        }

        // 2. Daily Serial & TV Timings Guide (Star Vijay, Zee Tamil, Sun TV, Sony Vizha)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = "Today's TV Serial Timings",
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Today's TV Serial Timings",
                            color = PrimaryText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap any show to watch the latest episode or tap the bell for a reminder",
                            color = SecondaryText,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(channels) { channel ->
                        val isSelected = channel == selectedChannel
                        Surface(
                            color = if (isSelected) PrimaryText else Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) PrimaryText else Color.White.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.clickable { onSelectChannel(channel) }
                        ) {
                            Text(
                                text = channel,
                                color = if (isSelected) Color.Black else PrimaryText,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // TV Timings Cards
        items(filteredSchedule, key = { it.id }) { item ->
            Surface(
                color = Color(0xFF101522),
                shape = RoundedCornerShape(18.dp),
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
                            .size(width = 100.dp, height = 64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1B2030))
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = Color(0xFF1D2436),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${item.channel} • ${item.telecastTime}",
                                    color = GoldAccent,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            if (item.isLiveVerified) {
                                Surface(
                                    color = EmeraldAccent.copy(alpha = 0.16f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Ready to Watch",
                                        color = EmeraldAccent,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = item.showTitle,
                            color = PrimaryText,
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${item.dayLabel} Episode",
                            color = SecondaryText,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Reminder Bell Button
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
                            contentDescription = "Remind me when new episode arrives",
                            tint = if (item.notifyOnLive) GoldAccent else SecondaryText
                        )
                    }
                }
            }
        }

        // 3. Watch Without Internet (Offline Saved Episodes)
        item {
            Surface(
                color = Color(0xFF101522),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Downloaded to Watch Offline",
                                color = PrimaryText,
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Save today's latest episode to watch without Wi-Fi",
                                color = SecondaryText,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = onStartSampleDownload,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryText,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
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
                                text = "Save Episode",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (offlineDownloads.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        offlineDownloads.forEach { dl ->
                            Surface(
                                color = Color(0xFF181F30),
                                shape = RoundedCornerShape(14.dp),
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
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = if (dl.status == "COMPLETED") {
                                                    "Ready to Watch Offline • ${dl.fileSizeMb} MB"
                                                } else {
                                                    "Saving Episode... ${dl.progressPercent}%"
                                                },
                                                color = if (dl.status == "COMPLETED") EmeraldAccent else CyanAccent,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        IconButton(onClick = { onRemoveOfflineDownload(dl.id) }) {
                                            Icon(
                                                imageVector = Icons.Filled.DeleteOutline,
                                                contentDescription = "Delete Saved Episode",
                                                tint = SecondaryText
                                            )
                                        }
                                    }
                                    if (dl.status != "COMPLETED") {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LinearProgressIndicator(
                                            progress = { (dl.progressPercent / 100f).coerceIn(0f, 1f) },
                                            color = CyanAccent,
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
        }

        // 4. Play on Living Room TV (Simple Family TV Remote & Handoff)
        item {
            Surface(
                color = Color(0xFF101522),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("continuity_handoff_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Tv,
                                contentDescription = "Living Room TV",
                                tint = CyanAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Living Room TV Companion",
                                color = PrimaryText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            color = EmeraldAccent.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "Connected",
                                color = EmeraldAccent,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = continuitySession?.activeCleanTag ?: "Bigg Boss Season 10 • Vijay TV • HD",
                        color = PrimaryText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Continue watching on ${continuitySession?.deviceName ?: "Living Room TV"} from where you left off",
                        color = SecondaryText,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = onHandoffToTv,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryText,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 9.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("continue_on_living_room_tv_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Tv,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Play on Living Room TV",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        OutlinedButton(
                            onClick = { showVirtualRemoteSheet = !showVirtualRemoteSheet },
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.24f)),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 9.dp),
                            modifier = Modifier.testTag("virtual_remote_keyboard_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Keyboard,
                                contentDescription = "TV Keyboard",
                                tint = PrimaryText,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TV Keyboard",
                                color = PrimaryText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    AnimatedVisibility(visible = showVirtualRemoteSheet) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            OutlinedTextField(
                                value = remoteKeyboardInput,
                                onValueChange = { remoteKeyboardInput = it },
                                label = { Text("Type a show or movie name to search on TV", color = SecondaryText, fontSize = 12.sp) },
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
                                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send to TV", tint = CyanAccent)
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
    }
}

