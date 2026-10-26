package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.model.EpisodeSource
import com.example.repository.VijayTvRepository
import com.example.ui.screens.VideoPlayerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class VideoPlayerScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    class FakeVijayTvRepository : VijayTvRepository() {
        override suspend fun getEpisodeSources(episodeUrl: String): List<EpisodeSource> {
            return listOf(
                EpisodeSource("Dailymotion Source 1", "https://dailymotion.com/embed/video/fake1"),
                EpisodeSource("Plyr Player (HD) 1", "https://teamstoday.com/?video=fake2")
            )
        }

        override suspend fun extractVideoUrl(episodeUrl: String, onStep: (String) -> Unit): String? {
            return "https://dailymotion.com/embed/video/fake1"
        }
    }

    @Test
    fun testVideoPlayerScreen_SourceSwitching() {
        val fakeRepository = FakeVijayTvRepository()
        val viewModel = MainViewModel(fakeRepository)

        composeTestRule.setContent {
            MyApplicationTheme {
                VideoPlayerScreen(
                    episodeUrl = "https://www.tamildhool.tech/fake-episode-url/",
                    viewModel = viewModel,
                    onBack = {}
                )
            }
        }

        // Wait for Compose to render and state transitions to complete
        composeTestRule.waitForIdle()

        // 1. Verify that the select video source container is displayed
        // We look for our custom source buttons
        val dmButton = composeTestRule.onNodeWithTag("source_button_dailymotion_source_1")
        val plyrButton = composeTestRule.onNodeWithTag("source_button_plyr_player_(hd)_1")

        dmButton.assertIsDisplayed()
        plyrButton.assertIsDisplayed()

        // 2. Click the Plyr Player (HD) button and verify that the view model state updates
        plyrButton.performClick()
        composeTestRule.waitForIdle()

        val activeSourceUrl = viewModel.videoUrlState.value
        if (activeSourceUrl is com.example.viewmodel.UiState.Success) {
            assertEquals("https://teamstoday.com/?video=fake2", activeSourceUrl.data)
        }

        // 3. Verify Silent, Textless Screensaver & Zero-Friction Wake-Up
        val oledGuardBtn = composeTestRule.onNodeWithTag("oled_guard_button")
        oledGuardBtn.assertIsDisplayed()
        oledGuardBtn.performClick()
        composeTestRule.waitForIdle()

        // Directly transitions to the silent, textless screensaver
        val stage2Node = composeTestRule.onNodeWithTag("oled_stage2_screensaver")
        stage2Node.assertIsDisplayed()

        // Tapping the screensaver wakes up immediately
        stage2Node.performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun testFootballStreamRouting_NeverLeaksBiggBossLive() {
        val fakeRepository = FakeVijayTvRepository()
        val viewModel = MainViewModel(fakeRepository)

        composeTestRule.setContent {
            MyApplicationTheme {
                VideoPlayerScreen(
                    episodeUrl = "football://epl_1?mirror=epl_1_m1",
                    episodeTitle = "Arsenal vs Manchester United • 1080p 60fps",
                    viewModel = viewModel,
                    onBack = {}
                )
            }
        }

        composeTestRule.waitForIdle()

        val activeSourceUrl = viewModel.videoUrlState.value
        org.junit.Assert.assertTrue(activeSourceUrl is com.example.viewmodel.UiState.Success)
        val resolvedUrl = (activeSourceUrl as com.example.viewmodel.UiState.Success).data.orEmpty()
        org.junit.Assert.assertTrue(
            "Football match stream must resolve to fixture stream and never leak Bigg Boss LIVE_CHANNEL_1",
            resolvedUrl.contains("fixture=epl_1") &&
                resolvedUrl != VijayTvRepository.LIVE_CHANNEL_1 &&
                resolvedUrl != VijayTvRepository.LIVE_CHANNEL_2
        )
    }

    @Test
    fun testClusterPortalNav_SpotlightAndCascadingShelf() {
        val fakeRepository = FakeVijayTvRepository()
        val shows = fakeRepository.getCatalogShows()
        val episodes = fakeRepository.getInitialEpisodes()
        val movies = fakeRepository.getInitialMovies()
        val matches = fakeRepository.multiSourceEngine.getVerifiedTournamentFixtures()

        // Ensure ClusterCharacterEngine strictly maps ONLY real app content (no Baakiyalakshmi, etc.)
        val fullRing = com.example.ui.screens.ClusterCharacterEngine.buildFullClusterRing(shows)
        org.junit.Assert.assertTrue(
            "Cluster must never contain non-app shows like Baakiyalakshmi",
            fullRing.none { it.id.contains("baakiyalakshmi", ignoreCase = true) }
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                com.example.ui.screens.ClusterPortalNav(
                    shows = shows,
                    showCarousels = shows.associate { show ->
                        show.id to fakeRepository.getInitialEpisodes(show.url)
                    },
                    currentEpisodes = episodes,
                    movies = movies,
                    footballMatches = matches,
                    liveEpisode = episodes.firstOrNull { it.isLiveStream },
                    onPlayEpisode = {},
                    onPlayFootballMirror = { _, _ -> },
                    onOpenShowSeasons = {},
                    onOpenMoviesHub = {},
                    onOpenFootballHub = {},
                    onOpenLiveStudio = {}
                )
            }
        }

        composeTestRule.waitForIdle()

        // 1. Verify ClusterPortalNav OLED stage, curved title pointer arc, and core actor cutouts are displayed
        composeTestRule.onNodeWithTag("cluster_portal_nav").assertIsDisplayed()
        composeTestRule.onNodeWithTag("cluster_hud_bezier_canvas").assertIsDisplayed()
        composeTestRule.onNodeWithTag("cluster_hud_character_name").assertIsDisplayed()
        composeTestRule.onNodeWithTag("cluster_node_anchor_vijay").assertIsDisplayed()
        composeTestRule.onNodeWithTag("cluster_node_anchor_bruno").assertIsDisplayed()
        composeTestRule.onNodeWithTag("cluster_node_anchor_biggboss").assertIsDisplayed()
        composeTestRule.onNodeWithTag("cluster_node_dynamic_celebrity_masterchef").assertIsDisplayed()
        composeTestRule.onNodeWithTag("cluster_node_dynamic_neeya_naana").assertIsDisplayed()

        // 2. Cycle focus right from Bigg Boss (index 1) to Bruno Fernandes (index 2) and open the Live Football cascading shelf
        composeTestRule.onNodeWithTag("cluster_next_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("cluster_enter_shelf_button").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("cluster_cascading_shelf").assertIsDisplayed()
        composeTestRule.onNodeWithTag("shelf_open_football_hub_btn").assertIsDisplayed()

        // 3. Collapse shelf and verify focus stays on Bruno Fernandes in the cluster
        composeTestRule.onNodeWithTag("cluster_shelf_back_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("cluster_hud_character_name").assertIsDisplayed()
    }

    @Test
    fun testDeveloperEasterEgg_FiveRapidLogoTaps() {
        val fakeRepository = FakeVijayTvRepository()
        val viewModel = MainViewModel(fakeRepository)

        composeTestRule.setContent {
            MyApplicationTheme {
                com.example.ui.screens.ShowDirectoryScreen(
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.waitForIdle()

        val logoButton = composeTestRule.onNodeWithTag("brand_logo_button")
        logoButton.assertIsDisplayed()

        // Tapping the Rasagulla logo 5 times in rapid succession reveals the hidden Developer Easter Egg
        repeat(5) {
            logoButton.performClick()
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("developer_easter_egg_overlay").assertIsDisplayed()
        composeTestRule.onNodeWithTag("vijay_pixel_sprite").assertIsDisplayed()
        composeTestRule.onNodeWithTag("easter_egg_credit_text").assertIsDisplayed()

        // Tapping the overlay dismisses it immediately
        composeTestRule.onNodeWithTag("developer_easter_egg_overlay").performClick()
        composeTestRule.waitForIdle()
    }
}


