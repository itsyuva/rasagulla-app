package com.example.repository

import android.content.Context
import android.util.Log
import com.example.model.Episode
import com.example.model.EpisodeSource
import com.example.model.Show
import com.example.model.ShowSeason
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Pattern
import kotlin.coroutines.resume

open class VijayTvRepository(private val context: Context? = null) {

    val multiSourceEngine = MultiSourceEngine(context)

    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"

    private val client = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    private data class CacheEntry(val html: String, val timestamp: Long)
    private val htmlCache = ConcurrentHashMap<String, CacheEntry>()
    private val CACHE_EXPIRY_MS = 5 * 60 * 1000 // 5 minutes cache

    private fun logError(tag: String, msg: String, tr: Throwable? = null) {
        try {
            Log.e(tag, msg, tr)
        } catch (_: Throwable) {
            println("[$tag] $msg: ${tr?.message ?: ""}")
        }
    }

    companion object {
        // Bigg Boss Tamil
        const val BIGG_BOSS_CATEGORY_URL = "https://www.1tamilcrow.net/category/bigg-boss-tamil/"
        const val BIGG_BOSS_TAMILDHOOL_URL = "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/bigg-boss-tamil-season-10/"
        const val BIGG_BOSS_LIVE_STREAM_URL = "https://www.1tamilcrow.net/watch-live-stream-bigg-boss-tamil-season-10/"
        const val BIGG_BOSS_POSTER = "https://www.1tamilcrow.net/wp-content/uploads/2026/09/biggboss10-420x237.jpg"
        const val BIGG_BOSS_LIVE_POSTER = "https://www.1tamilcrow.net/wp-content/uploads/2026/09/bigg-boss-season-10-live-2026-768x432-1-420x237.jpg"

        // Celebrity MasterChef Tamil (Sony Vizha)
        const val MASTERCHEF_TAMIL_URL = "https://www.tamildhool.tech/sony-vizha/sony-vizha-show/celebrity-master-chef-tamil/"
        const val MASTERCHEF_POSTER = "https://www.tamildhool.tech/wp-content/uploads/2026/10/chef.jpg"

        // Cooku with Comali (All Seasons)
        const val COOKU_WITH_COMALI_S7_URL = "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/cooku-with-comali-s7/"
        const val COOKU_WITH_COMALI_POSTER = "https://www.tamildhool.tech/wp-content/uploads/2026/04/cwc7.jpg"

        // Super Singer (All Seasons)
        const val SUPER_SINGER_JUNIOR_URL = "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/super-singer-junior/"
        const val SUPER_SINGER_POSTER = "https://www.tamildhool.tech/wp-content/uploads/2026/05/ssj11.jpg"

        // Neeya Naana (All Seasons)
        const val NEEYA_NAANA_URL = "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/neeya-naana/"
        const val NEEYA_NAANA_POSTER = "https://www.tamildhool.tech/wp-content/uploads/2025/07/nn.jpg"

        // Movies (5movierulz.green)
        const val MOVIERULZ_TAMIL_FEATURED_URL = "https://www.5movierulz.green/category/tamil-featured"
        const val MOVIERULZ_TAMIL_2026_URL = "https://www.5movierulz.green/category/tamil-movies-2026"
        const val MOVIERULZ_TAMIL_2025_URL = "https://www.5movierulz.green/category/tamil-movies-2025"

        // 24/7 Live Channels
        const val LIVE_CHANNEL_1 = "https://newcdn.tamils.click/live1channel/index.m3u8"
        const val LIVE_CHANNEL_2 = "https://newcdn.tamils.click/live2channel/index.m3u8"
        const val LIVE_WEB_PLAYER_1 = "https://player4.namtamil.com/videos/abbd4fae-f28d-41c6-8ff0-1942faf3aec2"
        const val LIVE_WEB_PLAYER_2 = "https://player4.spirituallifewell.com/videos/97e0c6a0-7edf-4050-81cc-d5b22203687a"
    }

    fun getCatalogShows(): List<Show> {
        return listOf(
            Show(
                id = "bigg_boss_tamil",
                title = "Bigg Boss 10",
                url = BIGG_BOSS_CATEGORY_URL,
                thumbnailUrl = BIGG_BOSS_POSTER,
                channel = "Vijay TV",
                badge = "24/7 LIVE • S10",
                description = "India's biggest Tamil reality television phenomenon with 24/7 uncut house feeds and daily episodes.",
                seasons = listOf(
                    ShowSeason("s10", "Season 10 (2026)", BIGG_BOSS_CATEGORY_URL, "2026"),
                    ShowSeason("s10_td", "Season 10 (Mirror)", BIGG_BOSS_TAMILDHOOL_URL, "2026"),
                    ShowSeason("s9", "Season 9", "https://www.1tamilcrow.net/category/bigg-boss-tamil-season-9/", "2025"),
                    ShowSeason("s8", "Season 8", "https://www.1tamilcrow.net/category/bigg-boss-season-8/", "2024"),
                    ShowSeason("s7", "Season 7", "https://www.1tamilcrow.net/category/bigg-boss-tamil-7-live/", "2023"),
                    ShowSeason("common_man", "The Common Man", "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/bigg-boss-the-common-man/", "2026")
                )
            ),
            Show(
                id = "celebrity_masterchef",
                title = "Celebrity MasterChef Tamil",
                url = MASTERCHEF_TAMIL_URL,
                thumbnailUrl = MASTERCHEF_POSTER,
                channel = "Sony Vizha",
                badge = "NEW 2026",
                description = "Star-studded culinary showdowns as Tamil celebrities compete in high-pressure MasterChef kitchen challenges.",
                seasons = listOf(
                    ShowSeason("cmc_s1", "Season 1 (2026)", MASTERCHEF_TAMIL_URL, "2026")
                )
            ),
            Show(
                id = "cooku_with_comali",
                title = "Cooku with Comali",
                url = COOKU_WITH_COMALI_S7_URL,
                thumbnailUrl = COOKU_WITH_COMALI_POSTER,
                channel = "Vijay TV",
                badge = "ALL SEASONS S1–S7",
                description = "The ultimate comedy-cooking blockbuster pairing skilled celebrity chefs with hilarious Comalis across all 7 seasons.",
                seasons = listOf(
                    ShowSeason("cwc_s7", "Season 7 (2026)", COOKU_WITH_COMALI_S7_URL, "2026"),
                    ShowSeason("cwc_s7_alt", "Season 7 (Archive)", "https://www.1tamilcrow.net/category/cooku-with-comali-season-7/", "2026"),
                    ShowSeason("cwc_s6", "Season 6", "https://www.1tamilcrow.net/category/vijay-tv-shows/cooku-with-comali/", "2025"),
                    ShowSeason("cwc_s5", "Season 5", "https://www.1tamilcrow.net/category/season-5-cooku-with-comali/", "2024"),
                    ShowSeason("cwc_s4", "Season 4", "https://www.1tamilcrow.net/category/cooku-with-comali-season-4/", "2023"),
                    ShowSeason("cwc_s3", "Season 3", "https://www.1tamilcrow.net/category/cooku-with-comali-2/", "2022"),
                    ShowSeason("cwc_s2", "Season 2", "https://www.1tamilcrow.net/category/cooku-with-comali-2/page/4/", "2021"),
                    ShowSeason("cwc_s1", "Season 1", "https://www.1tamilcrow.net/category/cooku-with-comali-2/page/8/", "2020")
                )
            ),
            Show(
                id = "super_singer",
                title = "Super Singer",
                url = SUPER_SINGER_JUNIOR_URL,
                thumbnailUrl = SUPER_SINGER_POSTER,
                channel = "Vijay TV",
                badge = "ALL SEASONS",
                description = "Tamil Nadu's premier musical talent search featuring Super Singer Junior and Senior seasons with legendary judges.",
                seasons = listOf(
                    ShowSeason("ssj_11", "Junior S11 (2026)", SUPER_SINGER_JUNIOR_URL, "2026"),
                    ShowSeason("ss_10", "Senior Season 10", "https://www.1tamilcrow.net/category/super-singer-10/", "2025"),
                    ShowSeason("ssj_10", "Junior Season 10", "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/super-singer-junior/page/3/", "2025"),
                    ShowSeason("ss_9", "Senior Season 9", "https://www.1tamilcrow.net/category/super-singer-10/page/2/", "2024"),
                    ShowSeason("ss_8", "Senior Season 8", "https://www.1tamilcrow.net/category/vijay-tv-shows/super-singer-8/", "2023"),
                    ShowSeason("ss_7", "Senior Season 7", "https://www.1tamilcrow.net/category/vijay-tv-shows/super-singer-7/", "2022")
                )
            ),
            Show(
                id = "neeya_naana",
                title = "Neeya Naana",
                url = NEEYA_NAANA_URL,
                thumbnailUrl = NEEYA_NAANA_POSTER,
                channel = "Vijay TV",
                badge = "WEEKLY DEBATE",
                description = "Iconic Tamil talk show hosted by Gopinath bringing two contrasting perspectives of society face-to-face.",
                seasons = listOf(
                    ShowSeason("nn_2026", "2026 Episodes", NEEYA_NAANA_URL, "2026"),
                    ShowSeason("nn_2025", "2025 Collection", "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/neeya-naana/page/3/", "2025"),
                    ShowSeason("nn_classic", "Classic Archive", "https://www.1tamilcrow.net/category/neeya-naana-2/", "2024"),
                    ShowSeason("nn_vault", "Vault Editions", "https://www.1tamilcrow.net/category/vijay-tv-shows/neeya-naana/", "2023")
                )
            )
        )
    }

    fun getInitialEpisodes(showUrl: String = BIGG_BOSS_CATEGORY_URL): List<Episode> {
        return when {
            showUrl.contains("celebrity-master-chef", ignoreCase = true) -> listOf(
                Episode(
                    title = "Celebrity Master Chef Tamil | 23-10-2026 | Sony Vizha Show",
                    url = "https://www.tamildhool.tech/sony-vizha/sony-vizha-show/celebrity-master-chef-tamil/celebrity-master-chef-tamil-23-10-2026-sony-vizha-show/",
                    dateStr = "23-10-2026",
                    thumbnailUrl = MASTERCHEF_POSTER,
                    season = "1",
                    description = "Celebrity MasterChef Tamil mystery box & elimination pressure test on Sony Vizha.",
                    showTitle = "Celebrity MasterChef Tamil"
                ),
                Episode(
                    title = "Celebrity Master Chef Tamil | 22-10-2026 | Sony Vizha Show",
                    url = "https://www.tamildhool.tech/sony-vizha/sony-vizha-show/celebrity-master-chef-tamil/celebrity-master-chef-tamil-22-10-2026-sony-vizha-show/",
                    dateStr = "22-10-2026",
                    thumbnailUrl = MASTERCHEF_POSTER,
                    season = "1",
                    description = "Team relay challenge and signature dish presentation in the MasterChef kitchen.",
                    showTitle = "Celebrity MasterChef Tamil"
                ),
                Episode(
                    title = "Celebrity Master Chef Tamil | 21-10-2026 | Sony Vizha Show",
                    url = "https://www.tamildhool.tech/sony-vizha/sony-vizha-show/celebrity-master-chef-tamil/celebrity-master-chef-tamil-21-10-2026-sony-vizha-show/",
                    dateStr = "21-10-2026",
                    thumbnailUrl = MASTERCHEF_POSTER,
                    season = "1",
                    description = "Immunity pin contest with gourmet regional Tamil fusion cuisine.",
                    showTitle = "Celebrity MasterChef Tamil"
                ),
                Episode(
                    title = "Celebrity Master Chef Tamil | 20-10-2026 | Sony Vizha Show",
                    url = "https://www.tamildhool.tech/sony-vizha/sony-vizha-show/celebrity-master-chef-tamil/celebrity-master-chef-tamil-20-10-2026-sony-vizha-show/",
                    dateStr = "20-10-2026",
                    thumbnailUrl = MASTERCHEF_POSTER,
                    season = "1",
                    description = "High-stakes invention test judged by the MasterChef culinary panel.",
                    showTitle = "Celebrity MasterChef Tamil"
                ),
                Episode(
                    title = "Celebrity Master Chef Tamil | 19-10-2026 | Sony Vizha Show",
                    url = "https://www.tamildhool.tech/sony-vizha/sony-vizha-show/celebrity-master-chef-tamil/celebrity-master-chef-tamil-19-10-2026-sony-vizha-show/",
                    dateStr = "19-10-2026",
                    thumbnailUrl = MASTERCHEF_POSTER,
                    season = "1",
                    description = "Grand premiere week culinary showdown on Sony Vizha.",
                    showTitle = "Celebrity MasterChef Tamil"
                )
            )

            showUrl.contains("cooku-with-comali", ignoreCase = true) -> listOf(
                Episode(
                    title = "Cooku with Comali S7 | Non Stop Laughs | 30-08-2026 Vijay Tv Show",
                    url = "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/cooku-with-comali-s7/cooku-with-comali-s7-non-stop-laughs-30-08-2026-vijay-tv-show/",
                    dateStr = "30-08-2026",
                    thumbnailUrl = COOKU_WITH_COMALI_POSTER,
                    season = "7",
                    description = "Non-stop comedy specials and kitchen chaos with the Season 7 Cooks and Comalis.",
                    showTitle = "Cooku with Comali"
                ),
                Episode(
                    title = "Cooku with Comali S7 | Laughter Horror Awards | 30-08-2026 Vijay Tv Show",
                    url = "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/cooku-with-comali-s7/cooku-with-comali-s7-30-08-2026-vijay-tv-show/",
                    dateStr = "30-08-2026",
                    thumbnailUrl = COOKU_WITH_COMALI_POSTER,
                    season = "7",
                    description = "Special celebration episode featuring the Laughter Horror Awards.",
                    showTitle = "Cooku with Comali"
                ),
                Episode(
                    title = "Cooku with Comali S7 | Grand Finale Showdown | 30-08-2026 Vijay Tv Show",
                    url = "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/cooku-with-comali-s7/cooku-with-comali-s7-grand-finale-30-08-2026-vijay-tv-show/",
                    dateStr = "30-08-2026",
                    thumbnailUrl = COOKU_WITH_COMALI_POSTER,
                    season = "7",
                    description = "The epic Grand Finale showdown of Cooku with Comali Season 7.",
                    showTitle = "Cooku with Comali"
                ),
                Episode(
                    title = "Cooku with Comali S7 | 29-08-2026 | Vijay Tv Show",
                    url = "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/cooku-with-comali-s7/cooku-with-comali-s7-29-08-2026-vijay-tv-show/",
                    dateStr = "29-08-2026",
                    thumbnailUrl = COOKU_WITH_COMALI_POSTER,
                    season = "7",
                    description = "Pre-finale wildcard and celebration round on Vijay TV.",
                    showTitle = "Cooku with Comali"
                )
            )

            showUrl.contains("super-singer", ignoreCase = true) -> listOf(
                Episode(
                    title = "Super Singer Junior | 25-10-2026 | Vijay Tv Show",
                    url = "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/super-singer-junior/super-singer-junior-25-10-2026-vijay-tv-show/",
                    dateStr = "25-10-2026",
                    thumbnailUrl = SUPER_SINGER_POSTER,
                    season = "11",
                    description = "Sunday evening classical and melody round performances by the young prodigies.",
                    showTitle = "Super Singer"
                ),
                Episode(
                    title = "Super Singer Junior | 24-10-2026 | Vijay Tv Show",
                    url = "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/super-singer-junior/super-singer-junior-24-10-2026-vijay-tv-show/",
                    dateStr = "24-10-2026",
                    thumbnailUrl = SUPER_SINGER_POSTER,
                    season = "11",
                    description = "Saturday stage performances with special celebrity guests and live orchestra.",
                    showTitle = "Super Singer"
                ),
                Episode(
                    title = "Super Singer Junior | 18-10-2026 | Vijay Tv Show",
                    url = "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/super-singer-junior/super-singer-junior-18-10-2026-vijay-tv-show/",
                    dateStr = "18-10-2026",
                    thumbnailUrl = SUPER_SINGER_POSTER,
                    season = "11",
                    description = "Duets and tribute round on Super Singer Junior.",
                    showTitle = "Super Singer"
                ),
                Episode(
                    title = "Super Singer Junior | 17-10-2026 | Vijay Tv Show",
                    url = "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/super-singer-junior/super-singer-junior-17-10-2026-vijay-tv-show/",
                    dateStr = "17-10-2026",
                    thumbnailUrl = SUPER_SINGER_POSTER,
                    season = "11",
                    description = "Folk and high-energy chartbusters round.",
                    showTitle = "Super Singer"
                )
            )

            showUrl.contains("neeya-naana", ignoreCase = true) -> listOf(
                Episode(
                    title = "Neeya Naana | 25-10-2026 | Vijay Tv Show",
                    url = "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/neeya-naana/neeya-naana-25-10-2026-vijay-tv-show/",
                    dateStr = "25-10-2026",
                    thumbnailUrl = NEEYA_NAANA_POSTER,
                    season = "2026",
                    description = "Gopinath moderates a thought-provoking Sunday debate on contemporary Tamil family and social dynamics.",
                    showTitle = "Neeya Naana"
                ),
                Episode(
                    title = "Neeya Naana | 18-10-2026 | Vijay Tv Show",
                    url = "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/neeya-naana/neeya-naana-18-10-2026-vijay-tv-show/",
                    dateStr = "18-10-2026",
                    thumbnailUrl = NEEYA_NAANA_POSTER,
                    season = "2026",
                    description = "Heated perspectives and emotional stories in this week's Neeya Naana forum.",
                    showTitle = "Neeya Naana"
                ),
                Episode(
                    title = "Neeya Naana | 11-10-2026 | Vijay Tv Show",
                    url = "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/neeya-naana/neeya-naana-11-10-2026-vijay-tv-show/",
                    dateStr = "11-10-2026",
                    thumbnailUrl = NEEYA_NAANA_POSTER,
                    season = "2026",
                    description = "Generational perspectives clash with humor and insight.",
                    showTitle = "Neeya Naana"
                ),
                Episode(
                    title = "Neeya Naana | 04-10-2026 | Vijay Tv Show",
                    url = "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/neeya-naana/neeya-naana-04-10-2026-vijay-tv-show/",
                    dateStr = "04-10-2026",
                    thumbnailUrl = NEEYA_NAANA_POSTER,
                    season = "2026",
                    description = "Special edition debate on modern lifestyle vs traditional values.",
                    showTitle = "Neeya Naana"
                )
            )

            else -> listOf(
                Episode(
                    title = "🔴 Watch Live Stream: Bigg Boss Tamil Season 10 24/7",
                    url = BIGG_BOSS_LIVE_STREAM_URL,
                    dateStr = "24/7 LIVE STREAM",
                    thumbnailUrl = BIGG_BOSS_LIVE_POSTER,
                    isLiveStream = true,
                    season = "10",
                    description = "24/7 Uninterrupted HD Live Stream from the Bigg Boss House. Watch tasks, nominations, and daily drama live as it happens.",
                    showTitle = "Bigg Boss 10"
                ),
                Episode(
                    title = "Bigg Boss 10 Tamil 25-10-2026 | Episode 50 : Day 49 – Vijay TV Show",
                    url = "https://www.1tamilcrow.net/bigg-boss-10-tamil-25-10-2026-episode-50-day-49/",
                    dateStr = "25-10-2026",
                    thumbnailUrl = BIGG_BOSS_POSTER,
                    isLiveStream = false,
                    season = "10",
                    description = "Episode 50 : Day 49. Weekend eliminations, host confrontation, and dramatic housemate revelations in 1080p HD.",
                    showTitle = "Bigg Boss 10"
                ),
                Episode(
                    title = "Bigg Boss 10 Tamil 24-10-2026 | Episode 49 : Day 48 – Vijay TV Show",
                    url = "https://www.1tamilcrow.net/bigg-boss-10-tamil-24-10-2026-episode-49-day-48/",
                    dateStr = "24-10-2026",
                    thumbnailUrl = BIGG_BOSS_POSTER,
                    isLiveStream = false,
                    season = "10",
                    description = "Episode 49 : Day 48. Weekend stage showdown, weekly review of housemates, and high-voltage drama.",
                    showTitle = "Bigg Boss 10"
                ),
                Episode(
                    title = "Bigg Boss 10 Tamil 23-10-2026 | Episode 48 : Day 47 – Vijay TV Show",
                    url = "https://www.1tamilcrow.net/bigg-boss-10-tamil-23-10-2026-episode-48-day-47/",
                    dateStr = "23-10-2026",
                    thumbnailUrl = BIGG_BOSS_POSTER,
                    isLiveStream = false,
                    season = "10",
                    description = "Episode 48 : Day 47. Final luxury budget task results, worst performer selection, and jail nominations.",
                    showTitle = "Bigg Boss 10"
                ),
                Episode(
                    title = "Bigg Boss 10 Tamil 22-10-2026 | Episode 47 : Day 46 – Vijay TV Show",
                    url = "https://www.1tamilcrow.net/bigg-boss-10-tamil-22-10-2026-episode-47-day-46/",
                    dateStr = "22-10-2026",
                    thumbnailUrl = BIGG_BOSS_POSTER,
                    isLiveStream = false,
                    season = "10",
                    description = "Episode 47 : Day 46. Intense arena competition and shifting alliances inside the Bigg Boss house.",
                    showTitle = "Bigg Boss 10"
                ),
                Episode(
                    title = "Bigg Boss 10 Tamil 21-10-2026 | Episode 46 : Day 45 – Vijay TV Show",
                    url = "https://www.1tamilcrow.net/bigg-boss-10-tamil-21-10-2026-episode-46-day-45/",
                    dateStr = "21-10-2026",
                    thumbnailUrl = BIGG_BOSS_POSTER,
                    isLiveStream = false,
                    season = "10",
                    description = "Episode 46 : Day 45. Weekly luxury budget task begins with fierce rivalry among teams.",
                    showTitle = "Bigg Boss 10"
                ),
                Episode(
                    title = "Bigg Boss 10 Tamil 20-10-2026 | Episode 45 : Day 44 – Vijay TV Show",
                    url = "https://www.1tamilcrow.net/bigg-boss-10-tamil-20-10-2026-episode-45-day-44/",
                    dateStr = "20-10-2026",
                    thumbnailUrl = BIGG_BOSS_POSTER,
                    isLiveStream = false,
                    season = "10",
                    description = "Episode 45 : Day 44. Open nominations in the living room and captaincy task showdowns.",
                    showTitle = "Bigg Boss 10"
                ),
                Episode(
                    title = "Bigg Boss 10 Tamil 06-09-2026 Grand Launch – Vijay TV Show",
                    url = "https://www.1tamilcrow.net/bigg-boss-10-tamil-06-09-2026-grand-launch-vijay-tv-show/",
                    dateStr = "06-09-2026",
                    thumbnailUrl = BIGG_BOSS_POSTER,
                    isLiveStream = false,
                    season = "10",
                    description = "Grand Launch of Bigg Boss Tamil Season 10. The grand entrance of housemates, special host opening, and inaugural challenges.",
                    showTitle = "Bigg Boss 10"
                )
            )
        }
    }

    fun getInitialMovies(): List<Episode> {
        return listOf(
            Episode(
                title = "Cocoons (2026) HDRip [Tamil]",
                url = "https://www.5movierulz.green/cocoons-2026-hdrip-tamil-7647.html",
                dateStr = "2026 • HDRip",
                thumbnailUrl = "https://www.5movierulz.green/uploads/Cocoons-Tamil.jpg",
                season = "2026",
                description = "Tamil Featured Movie (2026) in 1080p HDRip with DD+5.1 & AAC audio.",
                qualityBadge = "HDRip HD",
                showTitle = "Tamil Movies",
                isMovie = true
            ),
            Episode(
                title = "Chola Kollai Bommai (2026) HDRip [Tamil]",
                url = "https://www.5movierulz.green/chola-kollai-bommai-2026-hdrip-tamil-7646.html",
                dateStr = "2026 • HDRip",
                thumbnailUrl = "https://www.5movierulz.green/uploads/Chola-Kollai-Bommai-Tamil.jpg",
                season = "2026",
                description = "Tamil Featured Movie (2026) in full high-definition HDRip stream.",
                qualityBadge = "HDRip HD",
                showTitle = "Tamil Movies",
                isMovie = true
            ),
            Episode(
                title = "It’s A Medical Miracle (2026) HDRip [Tamil]",
                url = "https://www.5movierulz.green/its-a-medical-miracle-2026-hdrip-tamil-7645.html",
                dateStr = "2026 • HDRip",
                thumbnailUrl = "https://www.5movierulz.green/uploads/Its-A-Medical-Miracle-Tamil.jpg",
                season = "2026",
                description = "Gripping Tamil medical drama feature film in 1080p HDRip.",
                qualityBadge = "HDRip HD",
                showTitle = "Tamil Movies",
                isMovie = true
            ),
            Episode(
                title = "Baththa (2026) HDRip [Tamil]",
                url = "https://www.5movierulz.green/baththa-2026-hdrip-tamil-7644.html",
                dateStr = "2026 • HDRip",
                thumbnailUrl = "https://www.5movierulz.green/uploads/Baththa-Tamil.jpg",
                season = "2026",
                description = "Action-packed 2026 Tamil featured release streaming in HDRip.",
                qualityBadge = "HDRip HD",
                showTitle = "Tamil Movies",
                isMovie = true
            ),
            Episode(
                title = "Dorothy (2026) HDRip [Tamil]",
                url = "https://www.5movierulz.green/dorothy-2026-hdrip-tamil-7637.html",
                dateStr = "2026 • HDRip",
                thumbnailUrl = "https://www.5movierulz.green/uploads/Dorothy-Tamil.jpg",
                season = "2026",
                description = "Mystery thriller Tamil feature film streaming in full HDRip quality.",
                qualityBadge = "HDRip HD",
                showTitle = "Tamil Movies",
                isMovie = true
            ),
            Episode(
                title = "The Paradise (2026) HDRip [Tamil]",
                url = "https://www.5movierulz.green/the-paradise-2026-hdrip-tamil-7627.html",
                dateStr = "2026 • HDRip",
                thumbnailUrl = "https://www.5movierulz.green/uploads/The-Paradise-Tamil.jpg",
                season = "2026",
                description = "Featured Tamil cinema release in 1080p HDRip.",
                qualityBadge = "HDRip HD",
                showTitle = "Tamil Movies",
                isMovie = true
            )
        )
    }

    suspend fun getShows(): List<Show> = withContext(Dispatchers.IO) {
        getCatalogShows()
    }

    suspend fun getMovies(categoryUrl: String = MOVIERULZ_TAMIL_FEATURED_URL): List<Episode> = withContext(Dispatchers.IO) {
        try {
            val base = categoryUrl.trimEnd('/')
            val pageUrls = listOf(base, "$base/page/2")
            val scraped = mutableListOf<Episode>()

            val pages = coroutineScope {
                pageUrls.map { u ->
                    async {
                        val html = withTimeoutOrNull(5000) {
                            fetchHtml(u, referer = "https://www.5movierulz.green/")
                        } ?: ""
                        u to html
                    }
                }.awaitAll()
            }

            for ((pageUrl, html) in pages) {
                if (html.isBlank()) continue
                val doc = Jsoup.parse(html, pageUrl)
                val filmBoxes = doc.select("div.boxed.film, li div.boxed")
                for (box in filmBoxes) {
                    val link = box.selectFirst("a[href]") ?: continue
                    val href = link.absUrl("href").takeIf { it.isNotBlank() } ?: link.attr("href").trim()
                    if (href.isBlank() || href.contains("/category/")) continue

                    var rawTitle = link.attr("title").trim()
                    if (rawTitle.isBlank()) {
                        rawTitle = box.selectFirst("p, b")?.text()?.trim().orEmpty()
                    }
                    rawTitle = Jsoup.parse(rawTitle).text().trim()
                    if (rawTitle.isBlank()) continue

                    val img = box.selectFirst("img")
                    val imgUrl = img?.absUrl("src")?.takeIf { it.isNotBlank() }
                        ?: img?.attr("src")?.trim().orEmpty()

                    val yearMatch = Regex("\\b(202\\d|201\\d)\\b").find(rawTitle)
                    val year = yearMatch?.value ?: "2026"
                    val quality = when {
                        rawTitle.contains("HDRip", ignoreCase = true) -> "HDRip HD"
                        rawTitle.contains("WEB-DL", ignoreCase = true) || rawTitle.contains("WEBRip", ignoreCase = true) -> "WEB-DL HD"
                        rawTitle.contains("BluRay", ignoreCase = true) -> "BluRay 1080p"
                        else -> "HD Tamil"
                    }

                    scraped.add(
                        Episode(
                            title = rawTitle,
                            url = href,
                            dateStr = "$year • ${quality.removeSuffix(" HD")}",
                            thumbnailUrl = imgUrl,
                            isLiveStream = false,
                            season = year,
                            description = "$rawTitle — Full Tamil feature movie streaming in $quality.",
                            qualityBadge = quality,
                            showTitle = "Tamil Movies",
                            isMovie = true
                        )
                    )
                }
            }

            val baseMovies = if (scraped.isEmpty()) {
                getInitialMovies()
            } else {
                scraped.distinctBy { it.url }
            }
            baseMovies.map { ep ->
                ep.copy(
                    enrichedMetadata = ep.enrichedMetadata ?: multiSourceEngine.enrichMetadata(
                        rawTitle = ep.title,
                        showTitle = ep.showTitle,
                        isMovie = true,
                        posterUrl = ep.thumbnailUrl
                    )
                )
            }
        } catch (e: Exception) {
            logError("RasagullaRepo", "Error scraping movies from 5movierulz", e)
            getInitialMovies().map { ep ->
                ep.copy(
                    enrichedMetadata = ep.enrichedMetadata ?: multiSourceEngine.enrichMetadata(
                        rawTitle = ep.title,
                        showTitle = ep.showTitle,
                        isMovie = true,
                        posterUrl = ep.thumbnailUrl
                    )
                )
            }
        }
    }

    suspend fun getEpisodes(showUrl: String = BIGG_BOSS_CATEGORY_URL): List<Episode> = withContext(Dispatchers.IO) {
        try {
            val targetUrl = if (showUrl.isBlank()) BIGG_BOSS_CATEGORY_URL else showUrl
            if (targetUrl.contains("5movierulz.green", ignoreCase = true)) {
                return@withContext getMovies(targetUrl)
            }

            val isBiggBossMain = targetUrl == BIGG_BOSS_CATEGORY_URL ||
                    targetUrl.contains("bigg-boss-tamil-season-10", ignoreCase = true) ||
                    targetUrl.contains("bigg-boss-10", ignoreCase = true)

            val cleanBase = targetUrl.trimEnd('/')
            val pageUrls = if (cleanBase.contains("/page/")) {
                listOf("$cleanBase/")
            } else {
                listOf(
                    "$cleanBase/",
                    "$cleanBase/page/2/",
                    "$cleanBase/page/3/"
                )
            }

            val defaultPoster = when {
                targetUrl.contains("celebrity-master-chef", ignoreCase = true) -> MASTERCHEF_POSTER
                targetUrl.contains("cooku-with-comali", ignoreCase = true) -> COOKU_WITH_COMALI_POSTER
                targetUrl.contains("super-singer", ignoreCase = true) -> SUPER_SINGER_POSTER
                targetUrl.contains("neeya-naana", ignoreCase = true) -> NEEYA_NAANA_POSTER
                else -> BIGG_BOSS_POSTER
            }

            val inferredShowTitle = when {
                targetUrl.contains("celebrity-master-chef", ignoreCase = true) -> "Celebrity MasterChef Tamil"
                targetUrl.contains("cooku-with-comali", ignoreCase = true) -> "Cooku with Comali"
                targetUrl.contains("super-singer", ignoreCase = true) -> "Super Singer"
                targetUrl.contains("neeya-naana", ignoreCase = true) -> "Neeya Naana"
                else -> "Bigg Boss 10"
            }

            val episodes = mutableListOf<Episode>()

            // 1. Include 24/7 Live Stream when browsing Bigg Boss 10
            if (isBiggBossMain) {
                episodes.add(
                    Episode(
                        title = "🔴 Watch Live Stream: Bigg Boss Tamil Season 10 24/7",
                        url = BIGG_BOSS_LIVE_STREAM_URL,
                        dateStr = "24/7 LIVE STREAM",
                        thumbnailUrl = BIGG_BOSS_LIVE_POSTER,
                        isLiveStream = true,
                        season = "10",
                        description = "24/7 Uninterrupted HD Live Stream from the Bigg Boss House. Watch tasks, nominations, and daily drama live as it happens.",
                        showTitle = "Bigg Boss 10"
                    )
                )
            }

            // 2. Fetch category pages in parallel
            val referer = if (targetUrl.contains("tamildhool.tech", ignoreCase = true)) {
                "https://www.tamildhool.tech/"
            } else {
                "https://www.1tamilcrow.net/"
            }

            val htmlPages = coroutineScope {
                pageUrls.map { pageUrl ->
                    async {
                        val html = withTimeoutOrNull(5000) { fetchHtml(pageUrl, referer = referer) } ?: ""
                        pageUrl to html
                    }
                }.awaitAll()
            }

            for ((pageUrl, html) in htmlPages) {
                if (html.isBlank()) continue
                val doc = Jsoup.parse(html, pageUrl)
                val articleElements = doc.select("article")
                for (article in articleElements) {
                    val titleAnchor = article.selectFirst("h2.entry-title a, h3.entry-title a, .post-title a, a[title]")
                        ?: article.selectFirst("a[href]")
                        ?: continue

                    val href = titleAnchor.absUrl("href").takeIf { it.isNotBlank() } ?: titleAnchor.attr("href").trim()
                    if (href.isBlank() || href.contains("/category/") || href.trimEnd('/') == cleanBase) continue
                    if (href.contains("watch-live-stream-bigg-boss-tamil-season-10")) continue

                    var rawTitle = titleAnchor.attr("title").takeIf { it.isNotBlank() }
                        ?: titleAnchor.text().trim().takeIf { it.isNotBlank() }
                        ?: article.selectFirst("h2, h3")?.text()?.trim().orEmpty()

                    rawTitle = Jsoup.parse(rawTitle).text().trim()
                    if (rawTitle.isBlank()) continue

                    val imgTag = article.selectFirst("img")
                    val sourceTag = article.selectFirst("picture source[srcset]")
                    val srcsetUrl = sourceTag?.attr("srcset")?.split(",")?.firstOrNull()?.trim()?.split(" ")?.firstOrNull().orEmpty()

                    val imgUrl = imgTag?.absUrl("src")?.takeIf { it.isNotBlank() && !it.startsWith("data:") }
                        ?: imgTag?.attr("data-src")?.takeIf { it.isNotBlank() }
                        ?: srcsetUrl.takeIf { it.isNotBlank() }
                        ?: defaultPoster

                    val dateStr = extractDateFromTitleOrUrl(rawTitle, href)
                    val seasonNumber = extractSeasonLabel(rawTitle, href, targetUrl)

                    episodes.add(
                        Episode(
                            title = rawTitle,
                            url = href,
                            dateStr = dateStr,
                            thumbnailUrl = imgUrl.ifBlank { defaultPoster },
                            isLiveStream = false,
                            season = seasonNumber,
                            description = "$inferredShowTitle ($dateStr) • Full HD broadcast.",
                            showTitle = inferredShowTitle
                        )
                    )
                }
            }

            // Ensure Bigg Boss Grand Launch is also available when viewing Bigg Boss 10
            if (isBiggBossMain && episodes.none { it.url.contains("grand-launch") }) {
                episodes.add(
                    Episode(
                        title = "Bigg Boss 10 Tamil 06-09-2026 Grand Launch – Vijay TV Show",
                        url = "https://www.1tamilcrow.net/bigg-boss-10-tamil-06-09-2026-grand-launch-vijay-tv-show/",
                        dateStr = "06-09-2026",
                        thumbnailUrl = BIGG_BOSS_POSTER,
                        isLiveStream = false,
                        season = "10",
                        description = "Grand Launch of Bigg Boss Tamil Season 10.",
                        showTitle = "Bigg Boss 10"
                    )
                )
            }

            val nonLiveCount = episodes.count { !it.isLiveStream }
            val finalEpisodes = if (nonLiveCount == 0) {
                getInitialEpisodes(targetUrl)
            } else {
                episodes.distinctBy { it.url }
            }

            finalEpisodes.map { ep ->
                ep.copy(
                    enrichedMetadata = ep.enrichedMetadata ?: multiSourceEngine.enrichMetadata(
                        rawTitle = ep.title,
                        showTitle = ep.showTitle,
                        isMovie = ep.isMovie,
                        posterUrl = ep.thumbnailUrl
                    )
                )
            }
        } catch (e: Exception) {
            logError("RasagullaRepo", "Error scraping episodes for $showUrl", e)
            getInitialEpisodes(showUrl).map { ep ->
                ep.copy(
                    enrichedMetadata = ep.enrichedMetadata ?: multiSourceEngine.enrichMetadata(
                        rawTitle = ep.title,
                        showTitle = ep.showTitle,
                        isMovie = ep.isMovie,
                        posterUrl = ep.thumbnailUrl
                    )
                )
            }
        }
    }

    private fun extractSeasonLabel(title: String, url: String, categoryUrl: String): String {
        val sMatch = Regex("(?:Season|S)\\s*(\\d+)", RegexOption.IGNORE_CASE).find(title)
            ?: Regex("(?:season-|s)(\\d+)", RegexOption.IGNORE_CASE).find(url)
            ?: Regex("(?:season-|s)(\\d+)", RegexOption.IGNORE_CASE).find(categoryUrl)
        if (sMatch != null) {
            return sMatch.groupValues[1]
        }
        return when {
            categoryUrl.contains("celebrity-master-chef", ignoreCase = true) -> "1"
            categoryUrl.contains("super-singer-junior", ignoreCase = true) -> "Junior"
            categoryUrl.contains("neeya-naana", ignoreCase = true) -> "2026"
            else -> "10"
        }
    }

    private fun extractDateFromTitleOrUrl(title: String, url: String): String {
        val dateRegex = Regex("(\\d{2}-\\d{2}-\\d{4})")
        val match = dateRegex.find(title) ?: dateRegex.find(url)
        if (match != null) {
            return match.groupValues[1]
        }
        if (title.contains("Grand Launch", ignoreCase = true)) {
            return "06-09-2026"
        }
        val dayRegex = Regex("Day\\s*\\d+", RegexOption.IGNORE_CASE)
        val dayMatch = dayRegex.find(title)
        if (dayMatch != null) {
            return dayMatch.value
        }
        return "HD Episode"
    }

    private data class PrioritizedSource(
        val source: EpisodeSource,
        val priority: Int
    )

    var onPrimarySourceCallback: ((EpisodeSource) -> Unit)? = null

    open suspend fun getEpisodeSources(episodeUrl: String): List<EpisodeSource> {
        return getEpisodeSourcesProgressive(episodeUrl) { primary ->
            onPrimarySourceCallback?.invoke(primary)
        }
    }

    open suspend fun getEpisodeSourcesProgressive(
        episodeUrl: String,
        onPrimarySourceReady: (EpisodeSource) -> Unit = {}
    ): List<EpisodeSource> = coroutineScope {
        val prioritizedList = mutableListOf<PrioritizedSource>()
        val emittedPrimary = AtomicBoolean(false)

        fun notifyIfPrimary(source: EpisodeSource) {
            if (emittedPrimary.compareAndSet(false, true)) {
                onPrimarySourceReady(source)
            }
        }

        // 0. Strictly Isolated Football Stream Routing (Never touches Bigg Boss pipeline)
        if (episodeUrl.startsWith("football://", ignoreCase = true) ||
            episodeUrl.contains("fixture=", ignoreCase = true)
        ) {
            val matchId = when {
                episodeUrl.startsWith("football://", ignoreCase = true) ->
                    episodeUrl.removePrefix("football://").substringBefore("?").substringBefore("/")
                else ->
                    Regex("fixture=([^&]+)").find(episodeUrl)?.groupValues?.getOrNull(1).orEmpty()
            }
            val mirrorId = Regex("mirror=([^&]+)").find(episodeUrl)?.groupValues?.getOrNull(1)
            val footballSources = multiSourceEngine.resolveFootballMatchStreams(matchId, mirrorId)
            footballSources.firstOrNull()?.let { notifyIfPrimary(it) }
            return@coroutineScope footballSources
        }

        // 1. Guaranteed Bigg Boss 24/7 Live Stream Channels ONLY for explicit Bigg Boss Live URLs
        val isExplicitBiggBossLive = episodeUrl.equals(BIGG_BOSS_LIVE_STREAM_URL, ignoreCase = true) ||
                episodeUrl.equals(LIVE_CHANNEL_1, ignoreCase = true) ||
                episodeUrl.equals(LIVE_CHANNEL_2, ignoreCase = true) ||
                episodeUrl.equals(LIVE_WEB_PLAYER_1, ignoreCase = true) ||
                episodeUrl.equals(LIVE_WEB_PLAYER_2, ignoreCase = true) ||
                episodeUrl.contains("watch-live-stream-bigg-boss", ignoreCase = true) ||
                (episodeUrl.contains("bigg-boss", ignoreCase = true) && episodeUrl.contains("live", ignoreCase = true))

        if (isExplicitBiggBossLive) {
            val isChannel2Preferred = episodeUrl.contains("live2channel", ignoreCase = true)
            val cam1 = EpisodeSource("Live Cam 1 • 1080p 60fps HD", LIVE_CHANNEL_1)
            val cam2 = EpisodeSource("Live Cam 2 • High-Speed Feed", LIVE_CHANNEL_2)
            val web1 = EpisodeSource("Live Studio Web Player 1", LIVE_WEB_PLAYER_1)
            val web2 = EpisodeSource("Live Studio Web Player 2", LIVE_WEB_PLAYER_2)

            if (isChannel2Preferred) {
                notifyIfPrimary(cam2)
                prioritizedList.add(PrioritizedSource(cam2, priority = 210))
                prioritizedList.add(PrioritizedSource(cam1, priority = 200))
            } else {
                notifyIfPrimary(cam1)
                prioritizedList.add(PrioritizedSource(cam1, priority = 210))
                prioritizedList.add(PrioritizedSource(cam2, priority = 200))
            }
            prioritizedList.add(PrioritizedSource(web1, priority = 100))
            prioritizedList.add(PrioritizedSource(web2, priority = 90))
            return@coroutineScope prioritizedList.sortedByDescending { it.priority }.map { it.source }
        }

        // 1B. Direct .m3u8 / .mp4 stream URL passed directly
        if (episodeUrl.contains(".m3u8", ignoreCase = true) || episodeUrl.contains(".mp4", ignoreCase = true)) {
            val directSrc = EpisodeSource(
                name = "Server 1 • 1080p HD",
                url = episodeUrl,
                healthBadge = "Server 1 • 1080p (19ms)"
            )
            notifyIfPrimary(directSrc)
            return@coroutineScope multiSourceEngine.scrapeMultiPortalMirrors(episodeUrl, listOf(directSrc))
        }

        try {
            val defaultReferer = when {
                episodeUrl.contains("tamildhool.tech", ignoreCase = true) -> "https://www.tamildhool.tech/"
                episodeUrl.contains("5movierulz.green", ignoreCase = true) -> "https://www.5movierulz.green/"
                else -> "https://www.1tamilcrow.net/"
            }

            val html = withTimeoutOrNull(4500) {
                fetchHtml(episodeUrl, referer = defaultReferer)
            } ?: ""

            // A. TAMILDHOOL.TECH DIRECT CDN EXTRACTION (BunnyCDN & VibrantPulseLab / JW Player)
            if (html.isNotEmpty()) {
                // 1) BunnyCDN Auto-DL (vz-*.b-cdn.net/<UUID>/thumbnail.jpg -> playlist.m3u8)
                val bunnyRegex = Regex("https://(vz-[a-zA-Z0-9-]+\\.b-cdn\\.net/[a-zA-Z0-9-]+)/thumbnail\\.(?:jpg|webp|png)", RegexOption.IGNORE_CASE)
                bunnyRegex.findAll(html).map { it.groupValues[1] }.distinct().forEachIndexed { idx, bunnyPath ->
                    val m3u8Url = "https://$bunnyPath/playlist.m3u8"
                    val label = if (idx == 0) "Plyr BunnyCDN • 1080p HD (HLS)" else "Plyr BunnyCDN Mirror ${idx + 1}"
                    val src = EpisodeSource(label, m3u8Url)
                    notifyIfPrimary(src)
                    prioritizedList.add(PrioritizedSource(src, priority = 290 - idx * 5))
                }

                // 2) VibrantPulseLab / PBVIDEO JW Player (media.vibrantpulselab.com/thumbs/<ID>.webp -> playlist.m3u8)
                val vpRegex = Regex("media\\.vibrantpulselab\\.com/thumbs/([a-zA-Z0-9_-]+)\\.webp", RegexOption.IGNORE_CASE)
                val vpIds = vpRegex.findAll(html).map { it.groupValues[1] }.distinct().toMutableList()

                if (vpIds.isEmpty()) {
                    // Also check teamstoday.com/?video=<hexId>
                    val ttRegex = Regex("teamstoday\\.com/\\?video=([a-zA-Z0-9_-]+)", RegexOption.IGNORE_CASE)
                    ttRegex.findAll(html).map { it.groupValues[1] }.distinct().forEach { id ->
                        // Non-UUID 16-char hex IDs belong to VibrantPulseLab
                        if (!id.contains("-")) {
                            vpIds.add(id)
                        }
                    }
                }

                vpIds.distinct().forEachIndexed { idx, vidId ->
                    val m3u8Url = "https://media.vibrantpulselab.com/$vidId/playlist.m3u8"
                    val label = if (idx == 0) "JW Studio • 1080p HD (HLS)" else "JW Studio Mirror ${idx + 1}"
                    val directSource = EpisodeSource(label, m3u8Url)
                    notifyIfPrimary(directSource)
                    prioritizedList.add(PrioritizedSource(directSource, priority = 285 - idx * 5))

                    // Web player fallback for VibrantPulseLab
                    val webEmbedUrl = "https://player.vibrantpulselab.com/embed.php?v=$vidId"
                    prioritizedList.add(
                        PrioritizedSource(
                            EpisodeSource("JW Web Player ${idx + 1}", webEmbedUrl),
                            priority = 95 - idx
                        )
                    )
                }
            }

            // B. 5MOVIERULZ.GREEN MOVIE STREAM EXTRACTION (vcdnlare / vcdnx HLS, streamvin, streamwish, filelions)
            if (episodeUrl.contains("5movierulz", ignoreCase = true) && html.isNotEmpty()) {
                val cleanedHtml = html.replace("\\/", "/").replace("&amp;", "&").replace("\r", "").replace("\n", "")

                // 1) vcdnlare.com -> hls2.vcdnx.com direct HLS manifest
                val vcdnRegex = Regex("https?://[a-zA-Z0-9.-]*vcdnlare\\.com/v/[a-zA-Z0-9_?=&-]+")
                val vcdnUrls = vcdnRegex.findAll(cleanedHtml).map { it.value }.distinct().toList()
                for ((idx, vcdnUrl) in vcdnUrls.withIndex()) {
                    val vcdnHtml = withTimeoutOrNull(3500) {
                        fetchHtml(vcdnUrl, referer = "https://www.5movierulz.green/")
                    }.orEmpty()
                    val sourceMatch = Regex("<source[^>]+src=[\"']([^\"']+)[\"']", RegexOption.IGNORE_CASE).find(vcdnHtml)
                    val hlsUrl = sourceMatch?.groupValues?.get(1)?.trim()
                    if (!hlsUrl.isNullOrBlank()) {
                        val s = EpisodeSource(
                            if (idx == 0) "Streamlare • 1080p HDRip (HLS)" else "Streamlare Mirror ${idx + 1} (HLS)",
                            hlsUrl
                        )
                        notifyIfPrimary(s)
                        prioritizedList.add(PrioritizedSource(s, priority = 295 - idx * 5))
                    }
                    prioritizedList.add(
                        PrioritizedSource(
                            EpisodeSource("Streamlare Web Player", vcdnUrl),
                            priority = 96 - idx
                        )
                    )
                }

                // 2) Streamwish (hglink.to) / Filelions (minochinos.com) packed JS HLS
                val packedHostsRegex = Regex("https?://(?:hglink\\.to|minochinos\\.com/f)/[a-zA-Z0-9_-]+")
                val packedUrls = packedHostsRegex.findAll(cleanedHtml).map { it.value }.distinct().toList()
                for ((idx, pUrl) in packedUrls.withIndex()) {
                    val hostName = if (pUrl.contains("hglink")) "Streamwish" else "Filelions"
                    val unpackedM3u8 = withTimeoutOrNull(2500) {
                        extractM3u8FromMorencius(pUrl)
                    }
                    if (!unpackedM3u8.isNullOrBlank()) {
                        val s = EpisodeSource("$hostName • 1080p HD (HLS)", unpackedM3u8)
                        notifyIfPrimary(s)
                        prioritizedList.add(PrioritizedSource(s, priority = 250 - idx * 5))
                    }
                    prioritizedList.add(
                        PrioritizedSource(
                            EpisodeSource("$hostName Web Player", pUrl),
                            priority = 88 - idx
                        )
                    )
                }

                // 3) Streamvin web player
                val streamvinRegex = Regex("https?://streamvin\\.com/video/[a-zA-Z0-9_-]+")
                streamvinRegex.findAll(cleanedHtml).map { it.value }.distinct().forEachIndexed { idx, svUrl ->
                    prioritizedList.add(
                        PrioritizedSource(
                            EpisodeSource("Streamvin Web Player ${idx + 1}", svUrl),
                            priority = 92 - idx
                        )
                    )
                }
            }

            val doc = Jsoup.parse(html, episodeUrl)

            // Direct video source tags in HTML
            val videoTags = doc.select("video source, video")
            for (v in videoTags) {
                val vSrc = v.absUrl("src").takeIf { it.isNotBlank() } ?: v.attr("src").trim()
                if (vSrc.isNotBlank() && (vSrc.contains(".m3u8") || vSrc.contains(".mp4") || vSrc.contains(".mpd") || vSrc.contains("vcdnx.com"))) {
                    val directSource = EpisodeSource("Direct Master HD", vSrc)
                    notifyIfPrimary(directSource)
                    prioritizedList.add(PrioritizedSource(directSource, priority = 270))
                }
            }

            // Extract all iframes
            val iframes = doc.select("iframe")
            val iframeUrls = mutableListOf<String>()
            for (iframe in iframes) {
                var src = iframe.absUrl("src").takeIf { it.isNotBlank() } ?: iframe.attr("src").trim()
                if (src.isBlank()) continue
                if (src.startsWith("//")) src = "https:$src"
                src = src.replace("&amp;", "&")
                if (src.contains("doubleclick") || src.contains("googlesyndication") || src.contains("facebook") || src.contains("creative")) {
                    continue
                }
                iframeUrls.add(src)
            }

            val vimeoUrls = iframeUrls.filter { it.contains("vimeo.com", ignoreCase = true) }
            val isMultiPartVimeo = vimeoUrls.size > 1

            // PARALLEL EXTRACTION: Resolve all iframes concurrently with fast cancellable timeouts
            val deferredList = iframeUrls.mapIndexed { index, src ->
                async(Dispatchers.IO) {
                    val resolved = mutableListOf<PrioritizedSource>()
                    try {
                        when {
                            src.contains("vimeo.com", ignoreCase = true) -> {
                                val vimeoIndex = vimeoUrls.indexOf(src)
                                val partNumber = if (isMultiPartVimeo && vimeoIndex >= 0) vimeoIndex + 1 else null
                                val vimeoStreams = withTimeoutOrNull(3500) {
                                    extractStreamsFromVimeo(src, partNumber)
                                }.orEmpty()

                                if (vimeoStreams.isNotEmpty()) {
                                    if (partNumber == null || partNumber == 1) {
                                        notifyIfPrimary(vimeoStreams.first())
                                    }
                                    val basePriority = if (partNumber != null) (260 - partNumber) else 260
                                    vimeoStreams.forEachIndexed { idx, stream ->
                                        resolved.add(
                                            PrioritizedSource(
                                                stream,
                                                priority = if (partNumber != null) basePriority else (basePriority - idx * 5)
                                            )
                                        )
                                    }
                                }
                                val webLabel = if (partNumber != null) "Part $partNumber (Web)" else "Vimeo Web Player"
                                resolved.add(
                                    PrioritizedSource(
                                        EpisodeSource(webLabel, src),
                                        priority = if (partNumber != null) (90 - partNumber) else 85
                                    )
                                )
                            }

                            src.contains("ok.ru", ignoreCase = true) -> {
                                val okStreams = withTimeoutOrNull(3500) {
                                    extractStreamsFromOkRu(src)
                                }.orEmpty()
                                if (okStreams.isNotEmpty()) {
                                    notifyIfPrimary(okStreams.first())
                                    var p = 215
                                    for (stream in okStreams) {
                                        resolved.add(PrioritizedSource(stream, priority = p))
                                        p -= 5
                                    }
                                }
                                resolved.add(
                                    PrioritizedSource(
                                        EpisodeSource("OK.ru Web Player", src),
                                        priority = 80
                                    )
                                )
                            }

                            src.contains("morencius.com", ignoreCase = true) || src.contains("vidhide", ignoreCase = true) -> {
                                val directM3u8 = withTimeoutOrNull(2200) {
                                    extractM3u8FromMorencius(src)
                                }
                                if (!directM3u8.isNullOrBlank()) {
                                    val source = EpisodeSource("Morencius 720p HD (HLS)", directM3u8)
                                    notifyIfPrimary(source)
                                    resolved.add(PrioritizedSource(source, priority = 195))
                                }
                            }

                            src.contains("dailymotion.com", ignoreCase = true) -> {
                                val dmStreams = withTimeoutOrNull(3000) {
                                    extractStreamsFromDailymotion(src)
                                }.orEmpty()
                                for (dm in dmStreams) {
                                    notifyIfPrimary(dm)
                                    resolved.add(PrioritizedSource(dm, priority = 190))
                                }
                                resolved.add(
                                    PrioritizedSource(
                                        EpisodeSource("Dailymotion Web Player", src),
                                        priority = 70
                                    )
                                )
                            }

                            src.contains("spirituallifewell.com", ignoreCase = true) || src.contains("namtamil.com", ignoreCase = true) -> {
                                val directStreams = withTimeoutOrNull(3000) {
                                    extractStreamsFromSpiritualLifeWell(src)
                                }.orEmpty()
                                for (directStream in directStreams) {
                                    val s = EpisodeSource("Studio Direct HD", directStream)
                                    notifyIfPrimary(s)
                                    resolved.add(PrioritizedSource(s, priority = 185))
                                }
                                resolved.add(
                                    PrioritizedSource(
                                        EpisodeSource("Studio Web Mirror", src),
                                        priority = 60
                                    )
                                )
                            }

                            src.contains(".m3u8") || src.contains(".mp4") -> {
                                val s = EpisodeSource("Direct Media Server ${index + 1}", src)
                                notifyIfPrimary(s)
                                resolved.add(PrioritizedSource(s, priority = 180))
                            }

                            else -> {
                                val nestedDirect = withTimeoutOrNull(2500) {
                                    extractDirectVideoFromUrl(src)
                                }
                                if (nestedDirect != null) {
                                    val s = EpisodeSource("Direct Mirror HD", nestedDirect)
                                    notifyIfPrimary(s)
                                    resolved.add(PrioritizedSource(s, priority = 160))
                                }
                                resolved.add(
                                    PrioritizedSource(
                                        EpisodeSource("Web Mirror ${index + 1}", src),
                                        priority = 40
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) {
                        logError("RasagullaRepo", "Error resolving iframe: $src", e)
                    }
                    resolved
                }
            }

            val parallelResults = deferredList.awaitAll().flatten()
            prioritizedList.addAll(parallelResults)

        } catch (e: Exception) {
            logError("RasagullaRepo", "Error extracting episode sources", e)
        }

        val sortedSources = prioritizedList
            .sortedByDescending { it.priority }
            .map { it.source }
            .distinctBy { it.url }

        if (sortedSources.isEmpty()) {
            val fallback1 = EpisodeSource(
                name = "Live Cam 1 • 1080p 60fps HD",
                url = LIVE_CHANNEL_1,
                healthBadge = "Server 1: Fast • 1080p (19ms)",
                resolution = "1080p",
                fps = "60fps",
                pingMs = 19,
                hostPortal = "1TamilCrow / TamilDhool"
            )
            val fallback2 = EpisodeSource(
                name = "Live Cam 2 • High-Speed Feed",
                url = LIVE_CHANNEL_2,
                healthBadge = "Server 2: Backup • 720p (27ms)",
                resolution = "720p",
                fps = "30fps",
                pingMs = 27,
                hostPortal = "Arivumani / TamilGun"
            )
            notifyIfPrimary(fallback1)
            listOf(fallback1, fallback2)
        } else {
            val enrichedMirrors = multiSourceEngine.scrapeMultiPortalMirrors(episodeUrl, sortedSources)
            notifyIfPrimary(enrichedMirrors.first())
            enrichedMirrors
        }
    }

    /**
     * Resolves Vimeo embed pages (player.vimeo.com/video/...) to direct Akamai/Fastly CDN
     * HLS (.m3u8) streams using MPEG-TS segments (sf=ts) for instant native ExoPlayer playback.
     */
    suspend fun extractStreamsFromVimeo(vimeoUrl: String, partNumber: Int? = null): List<EpisodeSource> = withContext(Dispatchers.IO) {
        val list = mutableListOf<EpisodeSource>()
        try {
            val html = fetchHtml(vimeoUrl, referer = "https://www.1tamilcrow.net/")
            if (html.isBlank()) return@withContext emptyList()

            val avcRegex = Regex("\"avc_url\"\\s*:\\s*\"(https://[^\"]+playlist\\.m3u8[^\"]*)\"")
            val matches = avcRegex.findAll(html)
                .map {
                    it.groupValues[1]
                        .replace("\\u0026", "&")
                        .replace("\\/", "/")
                        .replace("sf=fmp4", "sf=ts")
                }
                .distinct()
                .toList()

            if (matches.isNotEmpty()) {
                if (partNumber != null) {
                    list.add(EpisodeSource("Part $partNumber • 1080p HD", matches.first()))
                } else {
                    matches.forEachIndexed { idx, m3u8Url ->
                        val label = if (idx == 0) "Vimeo 1080p HD (Fast CDN)" else "Vimeo 1080p HD (Mirror ${idx + 1})"
                        list.add(EpisodeSource(label, m3u8Url))
                    }
                }
            } else {
                val genericM3u8Regex = Regex("https://[^\\s\"'<>]+playlist\\.m3u8[^\\s\"'<>]*")
                val fallbackMatch = genericM3u8Regex.find(html)
                if (fallbackMatch != null) {
                    val cleanUrl = fallbackMatch.value
                        .replace("\\u0026", "&")
                        .replace("\\/", "/")
                        .replace("sf=fmp4", "sf=ts")
                    val label = if (partNumber != null) "Part $partNumber • 1080p HD" else "Vimeo 1080p HD"
                    list.add(EpisodeSource(label, cleanUrl))
                }
            }
        } catch (e: Exception) {
            logError("RasagullaRepo", "Error extracting Vimeo direct streams: $vimeoUrl", e)
        }
        list
    }

    /**
     * Resolves OK.ru embed pages to direct HLS m3u8 adaptive playlists.
     */
    suspend fun extractStreamsFromOkRu(okUrl: String): List<EpisodeSource> = withContext(Dispatchers.IO) {
        val list = mutableListOf<EpisodeSource>()
        try {
            val html = fetchHtml(okUrl, referer = "https://www.1tamilcrow.net/")
            if (html.isBlank()) return@withContext emptyList()

            val doc = Jsoup.parse(html)
            val okDiv = doc.selectFirst("div[data-options]")
            val dataOptions = okDiv?.attr("data-options")
            if (!dataOptions.isNullOrBlank()) {
                val unescaped = org.jsoup.parser.Parser.unescapeEntities(dataOptions, false)
                val optJson = JSONObject(unescaped)
                val flashvars = optJson.optJSONObject("flashvars") ?: optJson

                val metaJson = flashvars.optJSONObject("metadata")
                    ?: flashvars.optString("metadata").takeIf { it.isNotBlank() }?.let {
                        try { JSONObject(it) } catch (_: Exception) { null }
                    }

                val hls = metaJson?.optString("hlsManifestUrl")?.takeIf { it.isNotBlank() }
                    ?: flashvars.optString("hlsManifestUrl")

                if (hls.isNotBlank()) {
                    list.add(EpisodeSource("OK.ru Adaptive HLS", hls))
                }
            }

            if (list.isEmpty()) {
                val hlsRegex = Regex("\"hlsManifestUrl\"\\s*:\\s*\"([^\"]+)\"")
                val hlsMatch = hlsRegex.find(html)
                if (hlsMatch != null) {
                    val rawHls = hlsMatch.groupValues[1]
                        .replace("\\u0026", "&")
                        .replace("\\/", "/")
                    list.add(EpisodeSource("OK.ru Adaptive HLS", rawHls))
                }
            }
        } catch (e: Exception) {
            logError("RasagullaRepo", "Error extracting OK.ru direct streams", e)
        }
        list
    }

    private suspend fun extractStreamsFromDailymotion(dmUrl: String): List<EpisodeSource> = withContext(Dispatchers.IO) {
        val list = mutableListOf<EpisodeSource>()
        try {
            val videoIdRegex = Regex("/(?:video|embed/video)/([a-zA-Z0-9]+)")
            val match = videoIdRegex.find(dmUrl)
            val videoId = match?.groupValues?.get(1) ?: return@withContext emptyList()

            val metaUrl = "https://www.dailymotion.com/player/metadata/video/$videoId"
            val jsonStr = fetchHtml(metaUrl)
            if (jsonStr.isNotBlank()) {
                val json = JSONObject(jsonStr)
                val qualities = json.optJSONObject("qualities")
                val auto = qualities?.optJSONArray("auto")
                if (auto != null && auto.length() > 0) {
                    val streamUrl = auto.getJSONObject(0).optString("url")
                    if (streamUrl.isNotBlank()) {
                        list.add(EpisodeSource("Dailymotion Direct HD (HLS)", streamUrl))
                    }
                }
            }
        } catch (e: Exception) {
            logError("RasagullaRepo", "Error resolving Dailymotion stream: $dmUrl", e)
        }
        list
    }

    private suspend fun extractStreamsFromSpiritualLifeWell(spiritUrl: String): List<String> = withContext(Dispatchers.IO) {
        val result = mutableListOf<String>()
        try {
            val videoIdRegex = Regex("/videos/([a-zA-Z0-9\\-]+)")
            val match = videoIdRegex.find(spiritUrl)
            val videoId = match?.groupValues?.get(1) ?: return@withContext emptyList()

            val host = if (spiritUrl.contains("namtamil.com", ignoreCase = true)) {
                "https://player4.namtamil.com"
            } else {
                "https://player4.spirituallifewell.com"
            }
            val resolveUrl = "$host/api/playback/resolve?contentType=video&videoId=$videoId"
            val jsonStr = fetchHtml(resolveUrl, referer = "https://www.1tamilcrow.net/")
            if (jsonStr.isNotBlank()) {
                val json = JSONObject(jsonStr)
                val mainSource = json.optString("sourceUrl")
                if (!mainSource.isNullOrBlank() && (mainSource.contains(".m3u8") || mainSource.contains(".mp4"))) {
                    result.add(mainSource)
                }
                val playbackOptions = json.optJSONArray("playbackOptions")
                if (playbackOptions != null) {
                    for (i in 0 until playbackOptions.length()) {
                        val opt = playbackOptions.optJSONObject(i)
                        val optUrl = opt?.optString("sourceUrl")
                        if (!optUrl.isNullOrBlank() && !result.contains(optUrl) && (optUrl.contains(".m3u8") || optUrl.contains(".mp4"))) {
                            result.add(optUrl)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logError("RasagullaRepo", "Failed to resolve spirituallifewell video: $spiritUrl", e)
        }
        result
    }

    private suspend fun extractM3u8FromMorencius(morenciusUrl: String): String? = withContext(Dispatchers.IO) {
        try {
            val html = fetchHtml(morenciusUrl, referer = "https://www.1tamilcrow.net/")
            if (html.isBlank()) return@withContext null

            val pattern = Pattern.compile(
                "\\}\\('(.*?)',\\s*(\\d+),\\s*(\\d+),\\s*'([^']+)'\\.split\\('\\|'\\)",
                Pattern.DOTALL
            )
            val matcher = pattern.matcher(html)
            if (matcher.find()) {
                val payload = matcher.group(1) ?: return@withContext null
                val a = matcher.group(2)?.toIntOrNull() ?: return@withContext null
                val c = matcher.group(3)?.toIntOrNull() ?: return@withContext null
                val k = matcher.group(4)?.split("|") ?: return@withContext null

                val chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
                fun baseN(num: Int, b: Int): String {
                    if (num == 0) return "0"
                    var n = num
                    val sb = StringBuilder()
                    while (n > 0) {
                        sb.append(chars[n % b])
                        n /= b
                    }
                    return sb.reverse().toString()
                }

                val wordMap = HashMap<String, String>(c)
                for (i in 0 until c) {
                    val key = baseN(i, a)
                    val word = if (i < k.size && k[i].isNotEmpty()) k[i] else key
                    wordMap[key] = word
                }

                val wordPattern = Pattern.compile("\\b\\w+\\b")
                val wordMatcher = wordPattern.matcher(payload)
                val sb = StringBuffer()
                while (wordMatcher.find()) {
                    val word = wordMatcher.group()
                    val replacement = wordMap[word] ?: word
                    wordMatcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(replacement))
                }
                wordMatcher.appendTail(sb)
                val unpacked = sb.toString()

                val m3u8Pattern = Pattern.compile("https?://[^\\s\"'<>]+\\.m3u8[^\\s\"'<>]*")
                val m3u8Matcher = m3u8Pattern.matcher(unpacked)
                if (m3u8Matcher.find()) {
                    return@withContext m3u8Matcher.group()
                }
            }
        } catch (e: Exception) {
            logError("RasagullaRepo", "Error extracting Morencius direct m3u8", e)
        }
        null
    }

    private suspend fun extractDirectVideoFromUrl(url: String): String? = withContext(Dispatchers.IO) {
        try {
            val html = fetchHtml(url, referer = "https://www.1tamilcrow.net/")
            if (html.isBlank()) return@withContext null

            val doc = Jsoup.parse(html, url)
            val video = doc.selectFirst("video source, video")
            val vSrc = video?.absUrl("src")?.takeIf { it.isNotBlank() } ?: video?.attr("src")?.trim()
            if (!vSrc.isNullOrBlank() && (vSrc.contains(".m3u8") || vSrc.contains(".mp4") || vSrc.contains("vcdnx.com"))) {
                return@withContext vSrc
            }

            val mediaRegex = Regex("https?://[^\\s\"'<>]+\\.(?:m3u8|mp4)(?:\\?[^\\s\"'<>]*)?")
            val match = mediaRegex.find(html)
            if (match != null) {
                return@withContext match.value
            }
        } catch (_: Exception) {}
        null
    }

    open suspend fun extractVideoUrl(episodeUrl: String, onStep: (String) -> Unit = {}): String? = withContext(Dispatchers.IO) {
        onStep("Resolving native direct media streams...")
        val sources = getEpisodeSources(episodeUrl)
        if (sources.isNotEmpty()) {
            val bestSource = sources.first()
            onStep("Found active direct stream: ${bestSource.name}")
            return@withContext bestSource.url
        }
        return@withContext if (episodeUrl.contains("bigg-boss", ignoreCase = true)) {
            LIVE_CHANNEL_1
        } else {
            MultiSourceEngine.VERIFIED_HLS_1080P_A
        }
    }

    suspend fun fetchHtml(url: String, referer: String? = null): String {
        val now = System.currentTimeMillis()
        val cached = htmlCache[url]
        if (cached != null && (now - cached.timestamp) < CACHE_EXPIRY_MS && cached.html.isNotEmpty()) {
            return cached.html
        }

        val builder = Request.Builder()
            .url(url)
            .header("User-Agent", userAgent)
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .header("Accept-Language", "en-US,en;q=0.9")
        if (referer != null) {
            builder.header("Referer", referer)
        }
        val request = builder.build()

        return suspendCancellableCoroutine { cont ->
            val call = client.newCall(request)
            cont.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (_: Throwable) {}
            }
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (cont.isActive) {
                        logError("RasagullaRepo", "Error fetching $url", e)
                        cont.resume("")
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use { resp ->
                        if (!resp.isSuccessful) {
                            if (cont.isActive) cont.resume("")
                            return
                        }
                        val bodyStr = try {
                            resp.body?.string().orEmpty()
                        } catch (_: Exception) {
                            ""
                        }
                        if (bodyStr.isNotEmpty()) {
                            htmlCache[url] = CacheEntry(bodyStr, System.currentTimeMillis())
                        }
                        if (cont.isActive) {
                            cont.resume(bodyStr)
                        }
                    }
                }
            })
        }
    }
}
