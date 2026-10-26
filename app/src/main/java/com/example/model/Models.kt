package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class ShowSeason(
    val id: String,
    val label: String,
    val url: String,
    val year: String = "2026"
)

@Serializable
data class Show(
    val title: String,
    val url: String,
    val thumbnailUrl: String,
    val id: String = title.lowercase().replace(" ", "_"),
    val channel: String = "Vijay TV",
    val badge: String = "HD SERIES",
    val description: String = "",
    val seasons: List<ShowSeason> = emptyList()
)

@Serializable
data class EnrichedMetadata(
    val cleanTitle: String,
    val cleanTag: String, // e.g., "Bigg Boss Season 10 • Day 49 • Vijay TV • HD"
    val tmdbRating: String = "8.6",
    val letterboxdRating: String = "4.2 ★",
    val director: String = "Pradeep Milroy / Lokesh Kanagaraj",
    val cast: List<String> = emptyList(),
    val backdropUrl: String = "",
    val posterUrl: String = "",
    val releaseYear: String = "2026",
    val runtime: String = "1h 42m",
    val genres: List<String> = listOf("Reality", "Entertainment"),
    val synopsis: String = "",
    val trailerUrl: String = ""
)

@Serializable
data class Episode(
    val title: String,
    val url: String,
    val dateStr: String,
    val thumbnailUrl: String = "",
    val isLiveStream: Boolean = false,
    val season: String = "10",
    val description: String = "",
    val qualityBadge: String = "1080p HD",
    val showTitle: String = "",
    val isMovie: Boolean = false,
    val enrichedMetadata: EnrichedMetadata? = null
)

@Serializable
data class EpisodeSource(
    val name: String,
    val url: String,
    val healthBadge: String = "Server 1: Fast • 1080p",
    val resolution: String = "1080p",
    val fps: String = "60fps",
    val pingMs: Int = 24,
    val partNumber: Int? = null,
    val hostPortal: String = "TamilDhool / 1TamilCrow",
    val isVerifiedPlayable: Boolean = true,
    val isAdStripped: Boolean = true
)

@Serializable
data class ScheduleItem(
    val id: String,
    val showTitle: String,
    val channel: String, // "Star Vijay", "Zee Tamil", "Sun TV", "Sony Vizha"
    val telecastTime: String, // e.g., "21:30 IST"
    val dayLabel: String, // "Today", "Tomorrow", "Mon", "Tue", etc.
    val cleanTag: String, // e.g., "Bigg Boss Season 10 • Day 50 • Star Vijay • HD"
    val thumbnailUrl: String,
    val episodeUrl: String,
    val isLiveVerified: Boolean = true,
    val activeMirrorsCount: Int = 4,
    val notifyOnLive: Boolean = true
)

@Serializable
data class FootballStreamMirror(
    val id: String,
    val label: String, // e.g. "Server 1: Fast • 1080p 60fps"
    val originPortal: String, // "streamed.st", "timst.top", "streamcorner.st", "ppv.st"
    val url: String,
    val quality: String = "1080p 60fps",
    val bitrateKbps: Int = 6800,
    val pingMs: Int = 22,
    val isVerifiedActive: Boolean = true,
    val p2pPeersCount: Int = 148
)

@Serializable
data class FootballMatch(
    val id: String,
    val tournament: String, // "Premier League", "UEFA Champions League", "La Liga", "Serie A", "ISL"
    val homeTeam: String,
    val awayTeam: String,
    val homeCrestUrl: String = "",
    val awayCrestUrl: String = "",
    val kickoffTime: String, // e.g., "LIVE • 67'" or "Tonight • 00:30 IST"
    val scoreOrStatus: String, // e.g., "2 - 1" or "KICKOFF SOON"
    val isLive: Boolean = true,
    val venue: String = "Wembley / Santiago Bernabéu",
    val backdropUrl: String = "",
    val mirrors: List<FootballStreamMirror> = emptyList()
)

@Serializable
data class OfflineDownloadItem(
    val id: String,
    val title: String,
    val cleanTag: String,
    val sourceUrl: String,
    val thumbnailUrl: String,
    val localFilePath: String,
    val progressPercent: Int = 0,
    val chunksDownloaded: Int = 0,
    val totalChunks: Int = 40,
    val fileSizeMb: Double = 0.0,
    val status: String = "QUEUED", // "DOWNLOADING", "STITCHING MP4", "COMPLETED", "FAILED"
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class ContinuitySession(
    val sessionId: String,
    val deviceName: String, // e.g., "Living Room OLED TV" or "Pixel 10 Pro"
    val deviceType: String, // "TV" or "MOBILE"
    val activeTitle: String,
    val activeCleanTag: String,
    val activeUrl: String,
    val thumbnailUrl: String,
    val positionMs: Long,
    val durationMs: Long,
    val isPlaying: Boolean = true,
    val localIpAddress: String = "192.168.1.42",
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class WatchHistoryItem(
    val episodeTitle: String,
    val episodeUrl: String,
    val dateStr: String,
    val showTitle: String = "Rasagulla Cinema",
    val showUrl: String = "",
    val thumbnailUrl: String = "",
    val lastPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class WatchlistItem(
    val episodeTitle: String,
    val episodeUrl: String,
    val dateStr: String,
    val thumbnailUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
