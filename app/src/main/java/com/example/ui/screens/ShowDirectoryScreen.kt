package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.R
import com.example.model.Episode
import com.example.model.FootballMatch
import com.example.model.FootballStreamMirror
import com.example.model.Show
import com.example.model.ShowSeason
import com.example.model.WatchHistoryItem
import com.example.model.WatchlistItem
import com.example.repository.VijayTvRepository
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.UiState

// 2026 Spatial Streaming Palette
private val BgObsidian = Color(0xFF06070B)
private val BgElevated = Color(0xFF0E111A)
private val BgCard = Color(0xFF131724)
private val BgGlass = Color(0xCC161B2A)
private val AccentCrimson = Color(0xFFFF2A54)
private val AccentCyan = Color(0xFF00E5FF)
private val AccentGold = Color(0xFFFFC857)
private val TextPrimary = Color(0xFFF5F7FA)
private val TextSecondary = Color(0xFF949EB0)
private val BorderSubtle = Color(0x1FFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDirectoryScreen(
    viewModel: MainViewModel,
    onShowSelected: (Show) -> Unit = {},
    onEpisodeSelected: (url: String, title: String, date: String) -> Unit = { _, _, _ -> }
) {
    val showsState by viewModel.showsState.collectAsStateWithLifecycle()
    val selectedShow by viewModel.selectedShow.collectAsStateWithLifecycle()
    val selectedSeason by viewModel.selectedSeason.collectAsStateWithLifecycle()
    val episodesState by viewModel.episodesState.collectAsStateWithLifecycle()
    val showCarousels by viewModel.showCarousels.collectAsStateWithLifecycle()
    val moviesState by viewModel.moviesState.collectAsStateWithLifecycle()
    val selectedMovieCategory by viewModel.selectedMovieCategory.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val watchHistory by viewModel.watchHistory.collectAsStateWithLifecycle()
    val myList by viewModel.myList.collectAsStateWithLifecycle()
    val liveStreamEpisode by viewModel.liveStreamEpisode.collectAsStateWithLifecycle()
    val footballMatches by viewModel.footballMatches.collectAsStateWithLifecycle()
    val selectedTournament by viewModel.selectedTournament.collectAsStateWithLifecycle()
    val calendarSchedule by viewModel.calendarSchedule.collectAsStateWithLifecycle()
    val selectedCalendarChannel by viewModel.selectedCalendarChannel.collectAsStateWithLifecycle()
    val offlineDownloads by viewModel.offlineDownloads.collectAsStateWithLifecycle()
    val continuitySession by viewModel.continuitySession.collectAsStateWithLifecycle()
    val continuityToastMessage by viewModel.continuityToastMessage.collectAsStateWithLifecycle()
    val p2pMeshEnabled by viewModel.p2pMeshEnabled.collectAsStateWithLifecycle()

    var showSearchOverlay by remember { mutableStateOf(false) }
    var selectedEpisodeForInfo by remember { mutableStateOf<Episode?>(null) }
    var showDeveloperEasterEgg by remember { mutableStateOf(false) }
    var rasagullaLogoTapCount by remember { mutableIntStateOf(0) }
    var lastRasagullaLogoTapMs by remember { mutableLongStateOf(0L) }

    val handleRasagullaLogoTap = {
        val now = android.os.SystemClock.uptimeMillis()
        if (now - lastRasagullaLogoTapMs <= 1500L) {
            rasagullaLogoTapCount += 1
        } else {
            rasagullaLogoTapCount = 1
        }
        lastRasagullaLogoTapMs = now

        if (rasagullaLogoTapCount >= 5) {
            rasagullaLogoTapCount = 0
            showDeveloperEasterEgg = true
        }
        viewModel.setTab("Featured")
        showSearchOverlay = false
    }

    LaunchedEffect(continuityToastMessage) {
        if (continuityToastMessage != null) {
            kotlinx.coroutines.delay(3500)
            viewModel.clearContinuityToast()
        }
    }

    val catalogShows = remember(showsState) {
        (showsState as? UiState.Success)?.data ?: emptyList()
    }
    val moviesList = remember(moviesState) {
        (moviesState as? UiState.Success)?.data ?: emptyList()
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTvOrExpanded = maxWidth >= 720.dp

        Scaffold(
            containerColor = BgObsidian,
            topBar = {
                Sleek2026TopBar(
                    activeTab = currentTab,
                    isSearchActive = showSearchOverlay,
                    onBrandClick = handleRasagullaLogoTap,
                    onSearchToggle = { showSearchOverlay = !showSearchOverlay },
                    onRefresh = {
                        viewModel.loadEpisodes()
                        viewModel.loadAllShowCarousels()
                        viewModel.loadMovies()
                        viewModel.refreshFootballMatches()
                    },
                    onLiveClick = {
                        viewModel.setTab("Live 24/7")
                        showSearchOverlay = false
                    }
                )
            },
            bottomBar = {
                if (!isTvOrExpanded) {
                    Sleek2026BottomBar(
                        activeTab = currentTab,
                        onTabSelected = { tab ->
                            viewModel.setTab(tab)
                            showSearchOverlay = false
                        },
                        myListCount = myList.size + offlineDownloads.size
                    )
                }
            }
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // TV 10-Foot Experience: Collapsible Left-Rail Navigation Drawer
                if (isTvOrExpanded) {
                    CollapsibleTvLeftRail(
                        activeTab = currentTab,
                        onTabSelected = { tab ->
                            viewModel.setTab(tab)
                            showSearchOverlay = false
                        },
                        myListCount = myList.size + offlineDownloads.size,
                        onSearchClick = { showSearchOverlay = !showSearchOverlay },
                        onBrandLogoClick = handleRasagullaLogoTap
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0D111C),
                                    BgObsidian,
                                    Color(0xFF040508)
                                )
                            )
                        )
                ) {
                    val allEpisodes = (episodesState as? UiState.Success)?.data ?: emptyList()
                    val liveEp = liveStreamEpisode ?: allEpisodes.firstOrNull { it.isLiveStream }

                    if (showSearchOverlay) {
                        val combinedPool = remember(allEpisodes, showCarousels, moviesList) {
                            (allEpisodes + showCarousels.values.flatten() + moviesList).distinctBy { it.url }
                        }
                        SearchDiscoveryView(
                            searchQuery = searchQuery,
                            onQueryChange = { viewModel.setSearchQuery(it) },
                            episodes = combinedPool,
                            shows = catalogShows,
                            onShowClick = { show ->
                                viewModel.selectShow(show)
                                viewModel.setTab("Episodes")
                                showSearchOverlay = false
                            },
                            onEpisodeClick = { ep ->
                                onEpisodeSelected(ep.url, ep.title, ep.dateStr)
                            }
                        )
                    } else {
                        when (currentTab) {
                            "Episodes" -> {
                                ShowsAndSeasonsHubView(
                                    shows = catalogShows,
                                    selectedShow = selectedShow,
                                    selectedSeason = selectedSeason,
                                    episodesState = episodesState,
                                    myList = myList,
                                    onShowSelect = { show -> viewModel.selectShow(show) },
                                    onSeasonSelect = { season -> viewModel.selectSeason(season) },
                                    onEpisodeClick = { ep ->
                                        onEpisodeSelected(ep.url, ep.title, ep.dateStr)
                                    },
                                    onToggleWatchlist = { ep -> viewModel.toggleWatchlist(ep) },
                                    onInfoClick = { ep -> selectedEpisodeForInfo = ep }
                                )
                            }

                            "Movies" -> {
                                MoviesCinemaHubView(
                                    moviesState = moviesState,
                                    selectedCategory = selectedMovieCategory,
                                    myList = myList,
                                    onCategorySelect = { cat -> viewModel.selectMovieCategory(cat) },
                                    onMovieClick = { movie ->
                                        onEpisodeSelected(movie.url, movie.title, movie.dateStr)
                                    },
                                    onToggleWatchlist = { movie -> viewModel.toggleWatchlist(movie) },
                                    onRefresh = { viewModel.loadMovies(forceLoading = true) }
                                )
                            }

                            "Football" -> {
                                FootballLiveHubView(
                                    matches = footballMatches,
                                    selectedTournament = selectedTournament,
                                    p2pMeshEnabled = p2pMeshEnabled,
                                    onSelectTournament = { tourney -> viewModel.selectTournament(tourney) },
                                    onToggleP2pMesh = { viewModel.toggleP2pMesh() },
                                    onRefreshMatches = { viewModel.refreshFootballMatches(forceRefresh = true) },
                                    onPlayMirror = { match, mirror ->
                                        onEpisodeSelected(
                                            "football://${match.id}?mirror=${mirror.id}",
                                            "${match.homeTeam} vs ${match.awayTeam} • ${mirror.quality}",
                                            match.tournament
                                        )
                                    }
                                )
                            }

                            "Live 24/7" -> {
                                LiveBroadcastStudioView(
                                    liveEpisode = liveEp,
                                    onPlayServer = { serverUrl, serverName ->
                                        onEpisodeSelected(serverUrl, serverName, "24/7 LIVE")
                                    }
                                )
                            }

                            "My List" -> {
                                UnifiedLibraryHubView(
                                    calendarSchedule = calendarSchedule,
                                    selectedChannel = selectedCalendarChannel,
                                    onSelectChannel = { ch -> viewModel.selectCalendarChannel(ch) },
                                    onToggleScheduleNotification = { id -> viewModel.toggleScheduleNotification(id) },
                                    onTriggerVerifiedLiveAlert = { item -> viewModel.triggerVerifiedLiveAlert(item) },
                                    onPlayScheduleItem = { item ->
                                        onEpisodeSelected(item.episodeUrl, item.cleanTag, item.telecastTime)
                                    },
                                    offlineDownloads = offlineDownloads,
                                    onStartSampleDownload = {
                                        val targetEp = allEpisodes.firstOrNull { !it.isLiveStream }
                                            ?: allEpisodes.firstOrNull()
                                            ?: Episode(
                                                title = "Bigg Boss Tamil Season 10 • Day 49",
                                                url = VijayTvRepository.BIGG_BOSS_LIVE_STREAM_URL,
                                                dateStr = "Today",
                                                thumbnailUrl = VijayTvRepository.BIGG_BOSS_POSTER
                                            )
                                        viewModel.startOfflineDownload(targetEp)
                                    },
                                    onPlayOfflineDownload = { dl ->
                                        onEpisodeSelected("file://${dl.localFilePath}", dl.cleanTag, "OFFLINE MP4")
                                    },
                                    onRemoveOfflineDownload = { id -> viewModel.removeOfflineDownload(id) },
                                    continuitySession = continuitySession,
                                    onHandoffToTv = { viewModel.triggerLanHandoff() },
                                    onSendVirtualRemoteText = { txt ->
                                        viewModel.pushVirtualRemoteText(txt)
                                        showSearchOverlay = true
                                    },
                                    myList = myList,
                                    allEpisodes = (allEpisodes + moviesList).distinctBy { it.url },
                                    onEpisodeClick = { ep ->
                                        onEpisodeSelected(ep.url, ep.title, ep.dateStr)
                                    },
                                    onRemoveFromWatchlist = { item ->
                                        val ep = allEpisodes.firstOrNull { it.url == item.episodeUrl }
                                            ?: Episode(item.episodeTitle, item.episodeUrl, item.dateStr, item.thumbnailUrl)
                                        viewModel.toggleWatchlist(ep)
                                    }
                                )
                            }

                            else -> {
                                // "Featured" 2026 Home Experience with ClusterPortalNav Hero Architecture
                                Sleek2026HomeDashboard(
                                    shows = catalogShows,
                                    selectedShow = selectedShow,
                                    showCarousels = showCarousels,
                                    currentEpisodes = allEpisodes,
                                    movies = moviesList,
                                    footballMatches = footballMatches,
                                    liveEpisode = liveEp,
                                    watchHistory = watchHistory,
                                    myList = myList,
                                    onSelectShow = { show ->
                                        viewModel.selectShow(show)
                                        viewModel.setTab("Episodes")
                                    },
                                    onSwitchHeroShow = { show ->
                                        viewModel.selectShow(show)
                                    },
                                    onOpenMoviesTab = {
                                        viewModel.setTab("Movies")
                                    },
                                    onOpenFootballTab = {
                                        viewModel.setTab("Football")
                                    },
                                    onOpenLiveTab = {
                                        viewModel.setTab("Live 24/7")
                                    },
                                    onPlayEpisode = { ep ->
                                        onEpisodeSelected(ep.url, ep.title, ep.dateStr)
                                    },
                                    onPlayFootballMirror = { match, mirror ->
                                        onEpisodeSelected(
                                            "football://${match.id}?mirror=${mirror.id}",
                                            "${match.homeTeam} vs ${match.awayTeam} • ${mirror.quality}",
                                            match.tournament
                                        )
                                    },
                                    onToggleWatchlist = { ep ->
                                        viewModel.toggleWatchlist(ep)
                                    },
                                    onClearHistory = {
                                        viewModel.clearWatchHistory()
                                    }
                                )
                            }
                        }
                    }

                    // Continuity / Notification Toast Pill
                    androidx.compose.animation.AnimatedVisibility(
                        visible = continuityToastMessage != null,
                        enter = fadeIn() + slideInVertically { -30 },
                        exit = fadeOut(),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 12.dp)
                    ) {
                        Surface(
                            color = Color(0xEE101524),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, AccentCyan)
                        ) {
                            Text(
                                text = continuityToastMessage.orEmpty(),
                                color = TextPrimary,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp)
                            )
                        }
                    }

                    selectedEpisodeForInfo?.let { ep ->
                        EpisodeDetailsModal(
                            episode = ep,
                            isInWatchlist = myList.any { it.episodeUrl == ep.url },
                            onDismiss = { selectedEpisodeForInfo = null },
                            onPlay = {
                                selectedEpisodeForInfo = null
                                onEpisodeSelected(ep.url, ep.title, ep.dateStr)
                            },
                            onDownloadOffline = {
                                viewModel.startOfflineDownload(ep)
                                selectedEpisodeForInfo = null
                            },
                            onToggleWatchlist = { viewModel.toggleWatchlist(ep) }
                        )
                    }
                }
            }
        }

        // Hidden Developer Signature Easter Egg Overlay (5 rapid taps / OK presses on Rasagulla logo)
        DeveloperEasterEggOverlay(
            visible = showDeveloperEasterEgg,
            onDismiss = { showDeveloperEasterEgg = false }
        )
    }
}

@Composable
private fun Sleek2026TopBar(
    activeTab: String,
    isSearchActive: Boolean,
    onBrandClick: () -> Unit,
    onSearchToggle: () -> Unit,
    onRefresh: () -> Unit,
    onLiveClick: () -> Unit
) {
    Surface(
        color = Color(0xE606070B),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Uploaded Rasagulla Brand Identity
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onBrandClick() }
                    .padding(end = 8.dp)
                    .testTag("brand_logo_button")
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_rasagulla_icon),
                    contentDescription = "Rasagulla App Icon",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    painter = painterResource(id = R.drawable.img_rasagulla_logo),
                    contentDescription = "Rasagulla Brand Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.height(34.dp)
                )
            }

            // Right: Live Pill + Search + Sync
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    onClick = onLiveClick,
                    color = if (activeTab == "Live 24/7") AccentCrimson else Color(0x26FF2A54),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, AccentCrimson.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("top_live_pill")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (activeTab == "Live 24/7") Color.White else AccentCrimson)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                IconButton(
                    onClick = onSearchToggle,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isSearchActive) AccentCrimson else BgCard)
                        .testTag("top_search_button")
                ) {
                    Icon(
                        imageVector = if (isSearchActive) Icons.Filled.Close else Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BgCard)
                        .testTag("top_refresh_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Refresh Catalog",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Sleek2026BottomBar(
    activeTab: String,
    onTabSelected: (String) -> Unit,
    myListCount: Int
) {
    Surface(
        color = Color(0xF2080A10),
        tonalElevation = 12.dp,
        border = BorderStroke(0.5.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val navItems = listOf(
                Triple("Featured", "Home", Icons.Filled.Home),
                Triple("Episodes", "Shows", Icons.Filled.Tv),
                Triple("Movies", "Movies", Icons.Filled.Movie),
                Triple("Football", "Football", Icons.Filled.SportsSoccer),
                Triple("My List", "Library", Icons.Filled.VideoLibrary)
            )

            navItems.forEach { (tabKey, label, icon) ->
                val isSelected = activeTab == tabKey
                Surface(
                    onClick = { onTabSelected(tabKey) },
                    color = if (isSelected) Color(0x24FF2A54) else Color.Transparent,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 3.dp)
                        .testTag("bottom_nav_${tabKey.lowercase().replace(" ", "_")}")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 7.dp)
                    ) {
                        BadgedBox(
                            badge = {
                                if (tabKey == "My List" && myListCount > 0) {
                                    Badge(
                                        containerColor = AccentCrimson,
                                        contentColor = Color.White
                                    ) {
                                        Text("$myListCount", fontSize = 9.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) AccentCrimson else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = label,
                            color = if (isSelected) TextPrimary else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Sleek2026HomeDashboard(
    shows: List<Show>,
    selectedShow: Show,
    showCarousels: Map<String, List<Episode>>,
    currentEpisodes: List<Episode>,
    movies: List<Episode>,
    footballMatches: List<FootballMatch>,
    liveEpisode: Episode?,
    watchHistory: List<WatchHistoryItem>,
    myList: List<WatchlistItem>,
    onSelectShow: (Show) -> Unit,
    onSwitchHeroShow: (Show) -> Unit,
    onOpenMoviesTab: () -> Unit,
    onOpenFootballTab: () -> Unit,
    onOpenLiveTab: () -> Unit,
    onPlayEpisode: (Episode) -> Unit,
    onPlayFootballMirror: (FootballMatch, FootballStreamMirror) -> Unit,
    onToggleWatchlist: (Episode) -> Unit,
    onClearHistory: () -> Unit
) {
    val activeShowEpisodes = showCarousels[selectedShow.id]?.takeIf { it.isNotEmpty() } ?: currentEpisodes
    val heroEpisode = activeShowEpisodes.firstOrNull { !it.isLiveStream } ?: activeShowEpisodes.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("featured_home_list"),
        contentPadding = PaddingValues(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 0. ClusterPortalNav (Home Screen Hero Architecture — Borderless 3D Character Cutout Stage)
        item {
            ClusterPortalNav(
                shows = shows,
                showCarousels = showCarousels,
                currentEpisodes = currentEpisodes,
                movies = movies,
                footballMatches = footballMatches,
                liveEpisode = liveEpisode,
                onPlayEpisode = onPlayEpisode,
                onPlayFootballMirror = onPlayFootballMirror,
                onOpenShowSeasons = onSelectShow,
                onSwitchHeroShow = onSwitchHeroShow,
                onOpenMoviesHub = onOpenMoviesTab,
                onOpenFootballHub = onOpenFootballTab,
                onOpenLiveStudio = onOpenLiveTab
            )
        }

        // 1. 2026 Show Switcher Dock (Switch Hero Show or Open All Seasons)
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.testTag("home_show_pills_row")
                ) {
                    items(shows, key = { it.id }) { show ->
                        val isSelected = show.id == selectedShow.id
                        Surface(
                            onClick = { onSwitchHeroShow(show) },
                            color = if (isSelected) TextPrimary else BgCard,
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) TextPrimary else BorderSubtle
                            ),
                            modifier = Modifier.testTag("show_pill_${show.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = show.title,
                                    color = if (isSelected) BgObsidian else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = show.channel,
                                    color = if (isSelected) Color(0xFF475569) else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Spatial Glass Hero Banner
        item {
            heroEpisode?.let { hero ->
                SpatialHeroCard2026(
                    show = selectedShow,
                    episode = hero,
                    isInWatchlist = myList.any { it.episodeUrl == hero.url },
                    onPlay = { onPlayEpisode(hero) },
                    onBrowseSeasons = { onSelectShow(selectedShow) },
                    onToggleWatchlist = { onToggleWatchlist(hero) }
                )
            }
        }

        // 3. Continue Watching Rail (if available)
        if (watchHistory.isNotEmpty()) {
            item {
                ContinueWatchingRail2026(
                    history = watchHistory,
                    onItemClick = { item ->
                        onPlayEpisode(
                            Episode(
                                title = item.episodeTitle,
                                url = item.episodeUrl,
                                dateStr = item.dateStr,
                                thumbnailUrl = item.thumbnailUrl
                            )
                        )
                    },
                    onClear = onClearHistory
                )
            }
        }

        // 4. All Shows Spotlight Grid (Instant Access to All 5 Requested Shows + All Seasons)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader2026(
                    title = "Featured Series & All Seasons",
                    subtitle = "Vijay TV & Sony Vizha • Multi-Season Catalog",
                    actionLabel = null,
                    onAction = {}
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.testTag("shows_poster_row")
                ) {
                    items(shows, key = { it.id }) { show ->
                        ShowPosterCard2026(
                            show = show,
                            onClick = { onSelectShow(show) }
                        )
                    }
                }
            }
        }

        // 5. Featured Tamil Movies Rail (from 5movierulz.green/category/tamil-featured)
        if (movies.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader2026(
                        title = "Tamil Featured Movies",
                        subtitle = "5MovieRulz • 1080p HDRip Cinema",
                        actionLabel = "See All Movies",
                        onAction = onOpenMoviesTab
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.testTag("home_movies_row")
                    ) {
                        items(movies.take(10), key = { it.url }) { movie ->
                            MoviePosterCard2026(
                                movie = movie,
                                onClick = { onPlayEpisode(movie) }
                            )
                        }
                    }
                }
            }
        }

        // 6. Dedicated Episode Carousels for Every Show (Bigg Boss 10, Celebrity MasterChef Tamil, Cooku with Comali, Super Singer, Neeya Naana)
        items(shows, key = { "rail_${it.id}" }) { show ->
            val eps = (showCarousels[show.id] ?: emptyList()).filter { !it.isLiveStream }
            if (eps.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader2026(
                        title = show.title,
                        subtitle = "${show.channel} • ${show.seasons.size} ${if (show.seasons.size == 1) "Season" else "Seasons"} Available",
                        actionLabel = "All Seasons",
                        onAction = { onSelectShow(show) }
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(eps.take(10), key = { it.url }) { ep ->
                            EpisodeLandscapeCard2026(
                                episode = ep,
                                showTitle = show.title,
                                onClick = { onPlayEpisode(ep) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpatialHeroCard2026(
    show: Show,
    episode: Episode,
    isInWatchlist: Boolean,
    onPlay: () -> Unit,
    onBrowseSeasons: () -> Unit,
    onToggleWatchlist: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = BgElevated,
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("hero_featured_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
        ) {
            AsyncImage(
                model = episode.thumbnailUrl.ifBlank { show.thumbnailUrl },
                contentDescription = episode.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Smooth 2026 multi-stop vignette gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x2206070B),
                                Color(0xAA06070B),
                                Color(0xF506070B),
                                BgObsidian
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = AccentCrimson,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = show.badge,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Surface(
                        color = Color(0x33FFFFFF),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${show.channel} • ${episode.dateStr}",
                            color = TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Text(
                    text = show.title,
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = episode.title,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onPlay,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TextPrimary,
                            contentColor = BgObsidian
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("hero_watch_now_button")
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Watch Latest", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = onBrowseSeasons,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0x44FFFFFF)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0x33131724),
                            contentColor = TextPrimary
                        ),
                        modifier = Modifier
                            .height(46.dp)
                            .testTag("hero_seasons_button")
                    ) {
                        Icon(Icons.Filled.Layers, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (show.seasons.size > 1) "${show.seasons.size} Seasons" else "Episodes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = onToggleWatchlist,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x33FFFFFF))
                            .testTag("hero_watchlist_button")
                    ) {
                        Icon(
                            imageVector = if (isInWatchlist) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Watchlist",
                            tint = if (isInWatchlist) AccentGold else Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowsAndSeasonsHubView(
    shows: List<Show>,
    selectedShow: Show,
    selectedSeason: ShowSeason?,
    episodesState: UiState<List<Episode>>,
    myList: List<WatchlistItem>,
    onShowSelect: (Show) -> Unit,
    onSeasonSelect: (ShowSeason) -> Unit,
    onEpisodeClick: (Episode) -> Unit,
    onToggleWatchlist: (Episode) -> Unit,
    onInfoClick: (Episode) -> Unit
) {
    var localFilter by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // 1. Sleek Show Selector Strip
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.testTag("shows_hub_selector")
        ) {
            items(shows, key = { it.id }) { show ->
                val isSelected = show.id == selectedShow.id
                Surface(
                    onClick = { onShowSelect(show) },
                    color = if (isSelected) AccentCrimson else BgCard,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (isSelected) AccentCrimson else BorderSubtle),
                    modifier = Modifier.testTag("hub_show_${show.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        AsyncImage(
                            model = show.thumbnailUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = show.title,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${show.channel} • ${show.seasons.size} ${if (show.seasons.size == 1) "Season" else "Seasons"}",
                                color = if (isSelected) Color(0xFFFFD6DE) else TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // 2. Multi-Season Selector Bar (All Seasons of Super Singer, Cooku with Comali, Neeya Naana, Bigg Boss)
        if (selectedShow.seasons.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.testTag("season_selector_row")
            ) {
                items(selectedShow.seasons, key = { it.id }) { season ->
                    val isSeasonSelected = selectedSeason?.id == season.id
                    Surface(
                        onClick = { onSeasonSelect(season) },
                        color = if (isSeasonSelected) Color(0x2600E5FF) else BgElevated,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(
                            1.dp,
                            if (isSeasonSelected) AccentCyan else BorderSubtle
                        ),
                        modifier = Modifier.testTag("season_pill_${season.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            if (isSeasonSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(AccentCyan)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = season.label,
                                color = if (isSeasonSelected) AccentCyan else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSeasonSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // 3. Episode Filter Input
        OutlinedTextField(
            value = localFilter,
            onValueChange = { localFilter = it },
            placeholder = {
                Text(
                    "Filter ${selectedShow.title} episodes by date or title...",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
            },
            trailingIcon = {
                if (localFilter.isNotEmpty()) {
                    IconButton(onClick = { localFilter = "" }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BgCard,
                unfocusedContainerColor = BgCard,
                focusedBorderColor = AccentCrimson,
                unfocusedBorderColor = BorderSubtle,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // 4. Episodes List
        when (episodesState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = AccentCrimson, strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Loading ${selectedShow.title} (${selectedSeason?.label ?: "Episodes"})...",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            is UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = episodesState.message,
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }

            is UiState.Success -> {
                val filtered = remember(episodesState.data, localFilter) {
                    val list = episodesState.data.filter { !it.isLiveStream }
                    if (localFilter.isBlank()) list
                    else list.filter {
                        it.title.contains(localFilter, ignoreCase = true) ||
                                it.dateStr.contains(localFilter, ignoreCase = true)
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("episodes_directory_list")
                ) {
                    itemsIndexed(filtered, key = { _, ep -> ep.url }) { index, ep ->
                        Clean2026EpisodeRow(
                            index = index + 1,
                            episode = ep,
                            isBookmarked = myList.any { it.episodeUrl == ep.url },
                            onPlay = { onEpisodeClick(ep) },
                            onBookmark = { onToggleWatchlist(ep) },
                            onInfo = { onInfoClick(ep) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MoviesCinemaHubView(
    moviesState: UiState<List<Episode>>,
    selectedCategory: String,
    myList: List<WatchlistItem>,
    onCategorySelect: (String) -> Unit,
    onMovieClick: (Episode) -> Unit,
    onToggleWatchlist: (Episode) -> Unit,
    onRefresh: () -> Unit
) {
    var movieFilter by remember { mutableStateOf("") }

    val categories = listOf(
        "Featured Tamil" to VijayTvRepository.MOVIERULZ_TAMIL_FEATURED_URL,
        "2026 Releases" to VijayTvRepository.MOVIERULZ_TAMIL_2026_URL,
        "2025 Releases" to VijayTvRepository.MOVIERULZ_TAMIL_2025_URL
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Category Filter Row + Search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(categories, key = { it.second }) { (label, url) ->
                    val isSelected = selectedCategory == url
                    Surface(
                        onClick = { onCategorySelect(url) },
                        color = if (isSelected) AccentCrimson else BgCard,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, if (isSelected) AccentCrimson else BorderSubtle),
                        modifier = Modifier.testTag("movie_cat_${label.lowercase().replace(" ", "_")}")
                    ) {
                        Text(
                            text = label,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = movieFilter,
            onValueChange = { movieFilter = it },
            placeholder = { Text("Search Tamil movies (e.g., Cocoons, Dorothy, The Paradise)...", color = TextSecondary, fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Filled.MovieFilter, contentDescription = null, tint = AccentGold) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BgCard,
                unfocusedContainerColor = BgCard,
                focusedBorderColor = AccentGold,
                unfocusedBorderColor = BorderSubtle,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        )

        when (moviesState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = AccentGold, strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Loading Tamil Featured Movies...", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            }

            is UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Button(onClick = onRefresh) {
                        Text("Retry Loading Movies")
                    }
                }
            }

            is UiState.Success -> {
                val filteredMovies = remember(moviesState.data, movieFilter) {
                    if (movieFilter.isBlank()) moviesState.data
                    else moviesState.data.filter { it.title.contains(movieFilter, ignoreCase = true) }
                }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("movies_list")
                ) {
                    val chunked = filteredMovies.chunked(2)
                    items(chunked.size) { rowIdx ->
                        val pair = chunked[rowIdx]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            for (movie in pair) {
                                Box(modifier = Modifier.weight(1f)) {
                                    MovieGridCard2026(
                                        movie = movie,
                                        isInWatchlist = myList.any { it.episodeUrl == movie.url },
                                        onClick = { onMovieClick(movie) },
                                        onToggleWatchlist = { onToggleWatchlist(movie) }
                                    )
                                }
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowPosterCard2026(
    show: Show,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = BgCard,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .width(175.dp)
            .testTag("show_card_${show.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                AsyncImage(
                    model = show.thumbnailUrl,
                    contentDescription = show.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xCC06070B))
                            )
                        )
                )
                Surface(
                    color = Color(0xCC06070B),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = show.channel,
                        color = AccentCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = show.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = show.badge,
                    color = AccentGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun MoviePosterCard2026(
    movie: Episode,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = BgCard,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .width(148.dp)
            .testTag("movie_home_card_${movie.url.hashCode()}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(195.dp)
            ) {
                AsyncImage(
                    model = movie.thumbnailUrl,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    color = Color(0xD906070B),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(0.5.dp, AccentGold.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = movie.qualityBadge,
                        color = AccentGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = movie.title,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = movie.dateStr,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun MovieGridCard2026(
    movie: Episode,
    isInWatchlist: Boolean,
    onClick: () -> Unit,
    onToggleWatchlist: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = BgCard,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("movie_grid_card_${movie.url.hashCode()}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                AsyncImage(
                    model = movie.thumbnailUrl,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xCC06070B))
                            )
                        )
                )
                Surface(
                    color = Color(0xD906070B),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = movie.qualityBadge,
                        color = AccentGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
                IconButton(
                    onClick = onToggleWatchlist,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0x9906070B))
                ) {
                    Icon(
                        imageVector = if (isInWatchlist) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save Movie",
                        tint = if (isInWatchlist) AccentGold else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(AccentCrimson),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = movie.title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.dateStr,
                    color = AccentCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun EpisodeLandscapeCard2026(
    episode: Episode,
    showTitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = BgCard,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .width(220.dp)
            .testTag("ep_card_${episode.url.hashCode()}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(124.dp)
            ) {
                AsyncImage(
                    model = episode.thumbnailUrl,
                    contentDescription = episode.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xCC06070B))
                            )
                        )
                )
                Surface(
                    color = Color(0xCC06070B),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = episode.dateStr,
                        color = TextPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(AccentCrimson),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = episode.title,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun Clean2026EpisodeRow(
    index: Int,
    episode: Episode,
    isBookmarked: Boolean,
    onPlay: () -> Unit,
    onBookmark: () -> Unit,
    onInfo: () -> Unit
) {
    Surface(
        onClick = onPlay,
        color = BgCard,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("episode_item_$index")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(126.dp)
                    .height(74.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                AsyncImage(
                    model = episode.thumbnailUrl,
                    contentDescription = episode.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x44000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xCCFF2A54)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = episode.dateStr,
                        color = AccentCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "•",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                    Text(
                        text = episode.qualityBadge,
                        color = AccentGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = episode.title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onBookmark) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "Watchlist",
                    tint = if (isBookmarked) AccentGold else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ContinueWatchingRail2026(
    history: List<WatchHistoryItem>,
    onItemClick: (WatchHistoryItem) -> Unit,
    onClear: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader2026(
            title = "Continue Watching",
            subtitle = "Pick up right where you left off",
            actionLabel = "Clear",
            onAction = onClear
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(history.take(10), key = { it.episodeUrl }) { item ->
                val progress = if (item.durationMs > 0) {
                    (item.lastPositionMs.toFloat() / item.durationMs.toFloat()).coerceIn(0.05f, 0.98f)
                } else 0.25f

                Surface(
                    onClick = { onItemClick(item) },
                    color = BgCard,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.width(200.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(106.dp)
                        ) {
                            AsyncImage(
                                model = item.thumbnailUrl.ifBlank { VijayTvRepository.BIGG_BOSS_POSTER },
                                contentDescription = item.episodeTitle,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0x55000000)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            LinearProgressIndicator(
                                progress = { progress },
                                color = AccentCrimson,
                                trackColor = Color(0x55FFFFFF),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .align(Alignment.BottomCenter)
                            )
                        }
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = item.episodeTitle,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = item.dateStr,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader2026(
    title: String,
    subtitle: String,
    actionLabel: String?,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
        if (actionLabel != null) {
            TextButton(onClick = onAction) {
                Text(
                    text = actionLabel,
                    color = AccentCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = AccentCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun LiveBroadcastStudioView(
    liveEpisode: Episode?,
    onPlayServer: (url: String, title: String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Surface(
                color = BgCard,
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, AccentCrimson.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(AccentCrimson)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "24/7 LIVE STREAMING MATRIX",
                            color = AccentCrimson,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bigg Boss Tamil Season 10 • Uncut Feed",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Select any low-latency camera feed below for instant 1080p 60fps native playback.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        val liveServers = listOf(
            Triple("Live Cam 1 • Primary 1080p 60fps HD", VijayTvRepository.BIGG_BOSS_LIVE_STREAM_URL, "Native HLS • Smart TS"),
            Triple("Live Cam 2 • High-Speed Backup Feed", VijayTvRepository.LIVE_CHANNEL_2, "Multi-Bitrate HLS"),
            Triple("Studio Web Player 1 • Namtamil", VijayTvRepository.LIVE_WEB_PLAYER_1, "Browser Player"),
            Triple("Studio Web Player 2 • Mirror", VijayTvRepository.LIVE_WEB_PLAYER_2, "Browser Player")
        )

        itemsIndexed(liveServers) { idx, (name, url, badge) ->
            Surface(
                onClick = { onPlayServer(url, name) },
                color = BgCard,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("live_server_card_$idx")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = badge,
                            color = AccentCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AccentCrimson),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play Live", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun MyListWatchlistView(
    myList: List<WatchlistItem>,
    allEpisodes: List<Episode>,
    onEpisodeClick: (Episode) -> Unit,
    onRemove: (WatchlistItem) -> Unit
) {
    if (myList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.BookmarkBorder,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Your Watchlist is Empty",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Bookmark episodes or Tamil movies to watch them anytime.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(myList, key = { it.episodeUrl }) { item ->
                val ep = allEpisodes.firstOrNull { it.url == item.episodeUrl }
                    ?: Episode(item.episodeTitle, item.episodeUrl, item.dateStr, item.thumbnailUrl)

                Clean2026EpisodeRow(
                    index = 1,
                    episode = ep,
                    isBookmarked = true,
                    onPlay = { onEpisodeClick(ep) },
                    onBookmark = { onRemove(item) },
                    onInfo = {}
                )
            }
        }
    }
}

@Composable
private fun SearchDiscoveryView(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    episodes: List<Episode>,
    shows: List<Show>,
    onShowClick: (Show) -> Unit,
    onEpisodeClick: (Episode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    "Search MasterChef, Super Singer, Cooku with Comali, Neeya Naana, Movies...",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = null, tint = AccentCyan)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = TextSecondary)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BgCard,
                unfocusedContainerColor = BgCard,
                focusedBorderColor = AccentCyan,
                unfocusedBorderColor = BorderSubtle,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_input_field")
        )

        Spacer(modifier = Modifier.height(14.dp))

        val filteredShows = remember(shows, searchQuery) {
            if (searchQuery.isBlank()) shows
            else shows.filter { it.title.contains(searchQuery, ignoreCase = true) }
        }

        val filteredEpisodes = remember(episodes, searchQuery) {
            if (searchQuery.isBlank()) episodes.take(25)
            else episodes.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.dateStr.contains(searchQuery, ignoreCase = true) ||
                        it.showTitle.contains(searchQuery, ignoreCase = true)
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (filteredShows.isNotEmpty()) {
                item {
                    Text(
                        text = "SHOWS",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(filteredShows, key = { it.id }) { s ->
                            ShowPosterCard2026(show = s, onClick = { onShowClick(s) })
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "EPISODES & MOVIES (${filteredEpisodes.size})",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }

            itemsIndexed(filteredEpisodes, key = { _, ep -> ep.url }) { idx, ep ->
                Clean2026EpisodeRow(
                    index = idx + 1,
                    episode = ep,
                    isBookmarked = false,
                    onPlay = { onEpisodeClick(ep) },
                    onBookmark = {},
                    onInfo = {}
                )
            }
        }
    }
}

@Composable
private fun EpisodeDetailsModal(
    episode: Episode,
    isInWatchlist: Boolean,
    onDismiss: () -> Unit,
    onPlay: () -> Unit,
    onDownloadOffline: () -> Unit = {},
    onToggleWatchlist: () -> Unit
) {
    val meta = episode.enrichedMetadata
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BgElevated,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = {
            Column {
                Text(meta?.cleanTitle ?: episode.title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = meta?.cleanTag ?: "${episode.showTitle} • ${episode.dateStr} • HD",
                    color = AccentCyan,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        color = Color(0x26FFC857),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "TMDB ${meta?.tmdbRating ?: "8.7"}",
                            color = AccentGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Surface(
                        color = Color(0x2600E5FF),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Letterboxd ${meta?.letterboxdRating ?: "4.3 ★"}",
                            color = AccentCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                if (meta != null) {
                    Text("Director: ${meta.director}", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    if (meta.cast.isNotEmpty()) {
                        Text("Cast: ${meta.cast.joinToString(", ")}", color = TextSecondary, fontSize = 12.sp)
                    }
                }
                Text(
                    text = meta?.synopsis?.ifBlank { episode.description } ?: episode.description.ifBlank { "Full HD ad-free stream ready for playback across multi-source mirrors." },
                    fontSize = 12.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(
                    onClick = onDownloadOffline,
                    border = BorderStroke(1.dp, AccentCyan),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Download, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Download Offline MP4 (.TS → .MP4)", color = AccentCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onPlay,
                colors = ButtonDefaults.buttonColors(containerColor = AccentCrimson)
            ) {
                Text("Play Now", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        }
    )
}
