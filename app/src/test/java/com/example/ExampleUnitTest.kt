package com.example

import com.example.repository.VijayTvRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testAllCatalogShowsAndSeasons() = runBlocking {
        val repository = VijayTvRepository()
        val shows = repository.getShows()

        assertTrue("Expected at least 5 shows", shows.size >= 5)
        val titles = shows.map { it.title }
        assertTrue(titles.contains("Bigg Boss 10"))
        assertTrue(titles.contains("Celebrity MasterChef Tamil"))
        assertTrue(titles.contains("Cooku with Comali"))
        assertTrue(titles.contains("Super Singer"))
        assertTrue(titles.contains("Neeya Naana"))

        // Verify Cooku with Comali and Super Singer have multiple seasons
        val cwc = shows.first { it.title == "Cooku with Comali" }
        assertTrue("Expected multiple Cooku with Comali seasons", cwc.seasons.size >= 7)

        val ss = shows.first { it.title == "Super Singer" }
        assertTrue("Expected multiple Super Singer seasons", ss.seasons.size >= 5)
    }

    @Test
    fun testScrapeBiggBossEpisodes() = runBlocking {
        val repository = VijayTvRepository()
        val episodes = repository.getEpisodes()

        assertTrue(episodes.isNotEmpty())
        assertTrue(episodes.any { it.isLiveStream || it.url.contains("live-stream") })
        assertTrue(episodes.any { it.title.contains("Grand Launch") || it.url.contains("grand-launch") || it.season == "10" })
    }

    @Test
    fun testCelebrityMasterChefEpisodeSources() = runBlocking {
        val repository = VijayTvRepository()
        val masterChefEpisodeUrl = "https://www.tamildhool.tech/sony-vizha/sony-vizha-show/celebrity-master-chef-tamil/celebrity-master-chef-tamil-19-10-2026-sony-vizha-show/"

        val sources = repository.getEpisodeSources(masterChefEpisodeUrl)
        println("--- CELEBRITY MASTERCHEF SOURCES ---")
        sources.forEach {
            println("Source: ${it.name} -> ${it.url}")
        }

        assertTrue(sources.isNotEmpty())
        val firstUrl = sources.first().url
        assertTrue(
            "Expected direct HLS playlist.m3u8 for MasterChef Tamil, found: $firstUrl",
            firstUrl.contains(".m3u8") || firstUrl.contains("vibrantpulselab") || firstUrl.contains("b-cdn.net")
        )
    }

    @Test
    fun testTamilFeaturedMoviesAndStreamSources() = runBlocking {
        val repository = VijayTvRepository()
        val movies = repository.getMovies(VijayTvRepository.MOVIERULZ_TAMIL_FEATURED_URL)
        println("--- SCRAPED TAMIL FEATURED MOVIES (${movies.size}) ---")
        movies.take(5).forEach {
            println("Movie: ${it.title} | URL: ${it.url} | Poster: ${it.thumbnailUrl}")
        }

        assertTrue(movies.isNotEmpty())
        val firstMovie = movies.first()
        val sources = repository.getEpisodeSources(firstMovie.url)
        println("--- FIRST MOVIE SOURCES (${firstMovie.title}) ---")
        sources.forEach {
            println("Source: ${it.name} -> ${it.url}")
        }
        assertTrue(sources.isNotEmpty())
    }

    @Test
    fun testGrandLaunchEpisodeSources() = runBlocking {
        val repository = VijayTvRepository()
        val grandLaunchUrl = "https://www.1tamilcrow.net/bigg-boss-10-tamil-06-09-2026-grand-launch-vijay-tv-show/"

        val sources = repository.getEpisodeSources(grandLaunchUrl)
        assertTrue(sources.isNotEmpty())
        val firstUrl = sources.first().url
        assertTrue(
            "Expected top source to be direct HLS (.m3u8) from Vimeo/OK CDN, found: $firstUrl",
            firstUrl.contains(".m3u8") || firstUrl.contains("vimeocdn") || firstUrl.contains("okcdn") || firstUrl.contains("ok.ru")
        )
    }

    @Test
    fun testLiveStreamSources() = runBlocking {
        val repository = VijayTvRepository()
        val liveUrl = "https://www.1tamilcrow.net/watch-live-stream-bigg-boss-tamil-season-10/"

        val sources = repository.getEpisodeSources(liveUrl)
        assertTrue(sources.isNotEmpty())
    }

    @Test
    fun testMultiSourceEngineCalendarFootballAndMetadata() = runBlocking {
        val repository = VijayTvRepository()
        val engine = repository.multiSourceEngine

        // 1. Verify TMDB / Letterboxd Metadata Sanitizer produces clean tags
        val enriched = engine.enrichMetadata(
            rawTitle = "Bigg Boss Tamil Season 10 Day 19 Watch Online Free HD",
            showTitle = "Bigg Boss 10"
        )
        assertEquals("Bigg Boss Season 10 • Day 19 • Vijay TV • HD", enriched.cleanTag)

        // 2. Verify Smart Episode Calendar covers Star Vijay, Zee Tamil, Sun TV, Sony Vizha
        val calendar = engine.getSmartCalendarSchedule()
        assertTrue(calendar.any { it.channel == "Star Vijay" })
        assertTrue(calendar.any { it.channel == "Zee Tamil" })
        assertTrue(calendar.any { it.channel == "Sun TV" })
        assertTrue(calendar.any { it.channel == "Sony Vizha" })

        // 3. Verify Football Harvester groups fixtures by tournament with verified mirrors
        val matches = engine.harvestFootballMatches()
        assertTrue(matches.isNotEmpty())
        assertTrue(matches.any { it.tournament == "Premier League" })
        assertTrue(matches.any { it.tournament == "UEFA Champions League" })
        assertTrue(matches.first().mirrors.all { it.pingMs > 0 && it.p2pPeersCount > 0 })
    }

    @Test
    fun testClusterPortalNavLockedAnchorsAndDynamicEngine() {
        val repository = VijayTvRepository()
        val shows = repository.getCatalogShows()
        val fullRing = com.example.ui.screens.ClusterCharacterEngine.buildFullClusterRing(shows)

        // 1. Verify 3 Locked Core Anchors are present (Spider-Man removed)
        val lockedAnchors = fullRing.filter { it.isLockedCoreAnchor }
        assertEquals(3, lockedAnchors.size)
        assertTrue(lockedAnchors.any { it.characterName.contains("Thalapathy Vijay", ignoreCase = true) })
        assertTrue(lockedAnchors.any { it.characterName.contains("Bruno Fernandes", ignoreCase = true) })
        assertTrue(lockedAnchors.any { it.displayTitle.contains("Bigg Boss", ignoreCase = true) })
        assertTrue(fullRing.none { it.characterName.contains("Spider-Man", ignoreCase = true) })

        // 2. Verify Dynamic Show & Category Character Selection Engine maps Samantha to Celebrity MasterChef and ONLY real app shows
        val masterChefNode = fullRing.first { it.displayTitle.contains("MasterChef", ignoreCase = true) }
        assertTrue(masterChefNode.characterName.contains("Samantha", ignoreCase = true))
        assertTrue(fullRing.any { it.characterName.contains("Pugazh & Chef Damu", ignoreCase = true) })
        assertTrue(fullRing.any { it.characterName.contains("Priyanka & Ma Ka Pa", ignoreCase = true) })
        assertTrue(fullRing.any { it.characterName.contains("Gopinath", ignoreCase = true) })
        assertTrue(fullRing.none { it.characterName.contains("Baakiyalakshmi", ignoreCase = true) })

        // 3. Verify Visible Cluster Window shows all 7 characters
        fullRing.forEach { focusedNode ->
            val visibleWindow = com.example.ui.screens.ClusterCharacterEngine.getVisibleClusterNodes(fullRing, focusedNode.id)
            assertTrue(
                "Visible cluster count (${visibleWindow.size}) must be within 5..7",
                visibleWindow.size in 5..7
            )
            // Focused node is always visible in the active window
            assertTrue(visibleWindow.any { it.id == focusedNode.id })
        }
    }
}


