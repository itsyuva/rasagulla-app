/* ============================================================================
   RASAGULLA — STREAMING PLATFORM FOR WEB, MOBILE & SMART TV BROWSERS
   Netflix / Apple TV+ / Prime Video Grade UI, Spatial D-Pad Engine & HLS Player
   ============================================================================ */

const ICONS = {
  home: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9.5L12 3l9 6.5V20a1 1 0 0 1-1 1h-5v-6h-6v6H4a1 1 0 0 1-1-1V9.5z"/></svg>`,
  tv: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2"/><polyline points="17 2 12 7 7 2"/></svg>`,
  film: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="3" width="20" height="18" rx="2"/><line x1="7" y1="3" x2="7" y2="21"/><line x1="17" y1="3" x2="17" y2="21"/><line x1="2" y1="12" x2="22" y2="12"/><line x1="2" y1="7.5" x2="7" y2="7.5"/><line x1="2" y1="16.5" x2="7" y2="16.5"/><line x1="17" y1="7.5" x2="22" y2="7.5"/><line x1="17" y1="16.5" x2="22" y2="16.5"/></svg>`,
  live: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="2.5" fill="currentColor"/><path d="M16.24 7.76a6 6 0 0 1 0 8.49m-8.48-.01a6 6 0 0 1 0-8.49m11.31-2.82a10 10 0 0 1 0 14.14m-14.14 0a10 10 0 0 1 0-14.14"/></svg>`,
  bookmark: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/></svg>`,
  bookmarkFilled: `<svg viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/></svg>`,
  search: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>`,
  refresh: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><polyline points="23 4 23 10 17 10"/><polyline points="1 20 1 14 7 14"/><path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/></svg>`,
  play: `<svg viewBox="0 0 24 24" fill="currentColor"><polygon points="6 3 20 12 6 21 6 3"/></svg>`,
  pause: `<svg viewBox="0 0 24 24" fill="currentColor"><rect x="6" y="4" width="4.5" height="16" rx="1"/><rect x="13.5" y="4" width="4.5" height="16" rx="1"/></svg>`,
  back: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"><line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/></svg>`,
  chevronLeft: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="15 18 9 12 15 6"/></svg>`,
  chevronRight: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 18 15 12 9 6"/></svg>`,
  rewind10: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><polyline points="1 4 1 10 7 10"/><path d="M3.51 15a9 9 0 1 0 2.13-9.36L1 10"/><text x="12" y="15.5" text-anchor="middle" fill="currentColor" stroke="none" font-size="7.5" font-weight="900">10</text></svg>`,
  forward10: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><polyline points="23 4 23 10 17 10"/><path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"/><text x="12" y="15.5" text-anchor="middle" fill="currentColor" stroke="none" font-size="7.5" font-weight="900">10</text></svg>`,
  fullscreen: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M8 3H5a2 2 0 0 0-2 2v3m18 0V5a2 2 0 0 0-2-2h-3m0 18h3a2 2 0 0 0 2-2v-3M3 16v3a2 2 0 0 0 2 2h3"/></svg>`,
  fitScreen: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="M6 8h4M6 8v4M18 16h-4M18 16v-4"/></svg>`,
  shield: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>`,
  close: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>`,
  sparkle: `<svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2L14.8 9.2L22 12L14.8 14.8L12 22L9.2 14.8L2 12L9.2 9.2L12 2Z"/></svg>`,
  server: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="2" width="20" height="8" rx="2"/><rect x="2" y="14" width="20" height="8" rx="2"/><line x1="6" y1="6" x2="6.01" y2="6"/><line x1="6" y1="18" x2="6.01" y2="18"/></svg>`,
};

/**
 * Generates an instant inline 16:9 SVG Data-URI poster if any image ever fails to load.
 * Ensures zero broken browser image icons or raw alt text anywhere in the UI.
 */
function buildSvgPosterDataUri(title = "Rasagulla HD", subtitle = "1080P STREAM") {
  const safeTitle = String(title || "Rasagulla HD")
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .slice(0, 42);
  const safeSub = String(subtitle || "1080P HD")
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .slice(0, 28);

  let hash = 0;
  for (let i = 0; i < safeTitle.length; i++) {
    hash = (hash * 31 + safeTitle.charCodeAt(i)) >>> 0;
  }
  const palettes = [
    ["#1a0912", "#3b0a1e", "#ff2a54"],
    ["#071426", "#0d2847", "#00e5ff"],
    ["#1c1108", "#3d220a", "#ff9e00"],
    ["#0c1a14", "#113628", "#00e676"],
    ["#140c24", "#28154a", "#b388ff"],
  ];
  const [c1, c2, accent] = palettes[hash % palettes.length];

  const svg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 360" width="640" height="360">
    <defs>
      <linearGradient id="g" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stop-color="${c1}"/>
        <stop offset="60%" stop-color="${c2}"/>
        <stop offset="100%" stop-color="#05060a"/>
      </linearGradient>
    </defs>
    <rect width="640" height="360" fill="url(#g)"/>
    <circle cx="520" cy="80" r="160" fill="${accent}" fill-opacity="0.14"/>
    <rect x="28" y="26" width="136" height="28" rx="14" fill="rgba(0,0,0,0.55)" stroke="${accent}" stroke-width="1.5"/>
    <text x="96" y="45" text-anchor="middle" fill="${accent}" font-family="system-ui,sans-serif" font-size="11" font-weight="800" letter-spacing="1">${safeSub}</text>
    <circle cx="320" cy="155" r="34" fill="rgba(229,9,20,0.9)" stroke="#ffffff" stroke-width="2"/>
    <polygon points="312,141 312,169 336,155" fill="#ffffff"/>
    <text x="32" y="292" fill="#ffffff" font-family="system-ui,sans-serif" font-size="23" font-weight="800">${safeTitle}</text>
    <text x="32" y="322" fill="#94a3b8" font-family="system-ui,sans-serif" font-size="13" font-weight="700" letter-spacing="1.2">RASAGULLA • 1080P SMART STREAM</text>
  </svg>`;

  return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`;
}

window.__onImgErr = function (imgEl, title, badge) {
  if (!imgEl || imgEl.dataset.fallbackApplied === "1") return;
  imgEl.dataset.fallbackApplied = "1";
  imgEl.src = buildSvgPosterDataUri(title, badge);
};

// 100% Verified HTTP-200 Posters (No hotlink-blocked tamildhool.tech direct URLs)
const SHOW_METADATA = {
  "Bigg Boss Tamil": {
    id: "Bigg Boss Tamil",
    shortTitle: "Bigg Boss 10",
    tagline: "Vijay Sethupathi • 24/7 Live & Daily Episodes",
    badge: "Vijay TV • 1080p HD",
    poster: "https://www.1tamilcrow.net/wp-content/uploads/2026/08/biggboss10-768x432.jpg",
    accent: "#ff2a54",
  },
  "Celebrity MasterChef Tamil": {
    id: "Celebrity MasterChef Tamil",
    shortTitle: "MasterChef Tamil",
    tagline: "Culinary Battles • High-Heat Kitchen Drama",
    badge: "Sony Vizha • 1080p HD",
    poster: "https://images.unsplash.com/photo-1556910103-1c02745aae4d?w=900&auto=format&fit=crop&q=80",
    accent: "#ff8a00",
  },
  "Cooku with Comali": {
    id: "Cooku with Comali",
    shortTitle: "Cooku with Comali",
    tagline: "Pugazh & Comalis • Non-Stop Kitchen Comedy",
    badge: "Vijay TV • Season 7",
    poster: "https://www.1tamilcrow.net/wp-content/uploads/2024/04/COoku.jpg",
    accent: "#ffc857",
  },
  "Super Singer": {
    id: "Super Singer",
    shortTitle: "Super Singer",
    tagline: "Anirudh & Legends • Musical Showdown",
    badge: "Vijay TV • Dolby Audio",
    poster: "https://www.1tamilcrow.net/wp-content/uploads/2023/12/Super-Singer-10-1.png",
    accent: "#00e5ff",
  },
  "Neeya Naana": {
    id: "Neeya Naana",
    shortTitle: "Neeya Naana",
    tagline: "Gopinath • Unfiltered Social Debates",
    badge: "Vijay TV • Talk Show",
    poster: "https://www.1tamilcrow.net/wp-content/uploads/2023/05/Neeya-Naana.png",
    accent: "#b388ff",
  },
};

/**
 * 7-Character Group Collage Portal (Matches Android TV ClusterPortalNav.kt)
 */
const HERO_CHARACTERS = [
  {
    id: "char_vjs",
    title: "Bigg Boss 10",
    subtitle: "Vijay Sethupathi • 24/7 Live & Episodes",
    cutoutUrl: "/assets/cutouts/cutout_vjs.png",
    accentHex: "#FF2A54",
    targetFractionX: 0.13,
    targetShow: "Bigg Boss Tamil",
    targetTab: "home",
    openShelf: true,
  },
  {
    id: "char_pugazh",
    title: "Cooku with Comali",
    subtitle: "Pugazh • Comedy Kitchen",
    cutoutUrl: "/assets/cutouts/cutout_cwc.png",
    accentHex: "#FFC857",
    targetFractionX: 0.25,
    targetShow: "Cooku with Comali",
    targetTab: "home",
    openShelf: true,
  },
  {
    id: "char_anirudh",
    title: "Super Singer",
    subtitle: "Anirudh • Musical Battle",
    cutoutUrl: "/assets/cutouts/cutout_supersinger.png",
    accentHex: "#00E5FF",
    targetFractionX: 0.37,
    targetShow: "Super Singer",
    targetTab: "home",
    openShelf: true,
  },
  {
    id: "char_gopinath",
    title: "Neeya Naana",
    subtitle: "Gopinath • Clash of Perspectives",
    cutoutUrl: "/assets/cutouts/cutout_neeyanaana.png",
    accentHex: "#B388FF",
    targetFractionX: 0.49,
    targetShow: "Neeya Naana",
    targetTab: "home",
    openShelf: true,
  },
  {
    id: "char_chef",
    title: "MasterChef Tamil",
    subtitle: "Chef Special • Culinary Finale",
    cutoutUrl: "/assets/cutouts/cutout_masterchef.png",
    accentHex: "#FF8A00",
    targetFractionX: 0.61,
    targetShow: "Celebrity MasterChef Tamil",
    targetTab: "home",
    openShelf: true,
  },
  {
    id: "char_allshows",
    title: "All Star Shows",
    subtitle: "Complete TV Shows Hub",
    cutoutUrl: "/assets/cutouts/cutout_allshows.png",
    accentHex: "#FF4081",
    targetFractionX: 0.74,
    targetShow: "Bigg Boss Tamil",
    targetTab: "shows",
    openShelf: false,
  },
  {
    id: "char_movies",
    title: "Latest Movies",
    subtitle: "2026 Tamil Blockbusters • 1080p",
    cutoutUrl: "/assets/cutouts/cutout_movies.png",
    accentHex: "#00E676",
    targetFractionX: 0.87,
    targetShow: "Bigg Boss Tamil",
    targetTab: "movies",
    openShelf: false,
  },
];

const INITIAL_EPISODES = [
  {
    id: "bb-live-24x7-main",
    showName: "Bigg Boss Tamil",
    title: "Bigg Boss Tamil Season 10 — 24/7 Live Feed",
    date: "LIVE NOW • 1080p 60fps",
    thumbnailUrl: SHOW_METADATA["Bigg Boss Tamil"].poster,
    episodePageUrl: "https://newcdn.tamils.click/live1channel/index.m3u8",
    seasonNumber: 10,
    isLiveStream: true,
  },
  {
    id: "bb-live-24x7-cam2",
    showName: "Bigg Boss Tamil",
    title: "Bigg Boss Tamil Season 10 — Live Uncut Feed 2",
    date: "LIVE CAM 2 • 1080p HD",
    thumbnailUrl: SHOW_METADATA["Bigg Boss Tamil"].poster,
    episodePageUrl: "https://newcdn.tamils.click/live2channel/index.m3u8",
    seasonNumber: 10,
    isLiveStream: true,
  },
  {
    id: "bb-ep-latest",
    showName: "Bigg Boss Tamil",
    title: "Bigg Boss Tamil Season 10 — Latest Full Episode HD",
    date: "Latest Broadcast • 1080p HD",
    thumbnailUrl: SHOW_METADATA["Bigg Boss Tamil"].poster,
    episodePageUrl: "https://www.1tamilcrow.net/category/vijay-tv-shows/bigg-boss-tamil-season-10/",
    seasonNumber: 10,
    isLiveStream: false,
  },
  {
    id: "mc-ep-latest-1",
    showName: "Celebrity MasterChef Tamil",
    title: "Celebrity MasterChef Tamil — Grand Kitchen Mystery Box",
    date: "Sony Vizha • 1080p HD",
    thumbnailUrl: SHOW_METADATA["Celebrity MasterChef Tamil"].poster,
    episodePageUrl: "https://www.tamildhool.tech/sony-vizha/celebrity-master-chef-tamil/",
    seasonNumber: 1,
    isLiveStream: false,
  },
  {
    id: "mc-ep-latest-2",
    showName: "Celebrity MasterChef Tamil",
    title: "Celebrity MasterChef Tamil — Elimination Pressure Test",
    date: "Sony Vizha • 1080p HD",
    thumbnailUrl: SHOW_METADATA["Celebrity MasterChef Tamil"].poster,
    episodePageUrl: "https://www.tamildhool.tech/sony-vizha/celebrity-master-chef-tamil/",
    seasonNumber: 1,
    isLiveStream: false,
  },
  {
    id: "cwc-ep-latest-1",
    showName: "Cooku with Comali",
    title: "Cooku with Comali Season 7 — Latest Full Episode HD",
    date: "Season 7 • 1080p HD",
    thumbnailUrl: SHOW_METADATA["Cooku with Comali"].poster,
    episodePageUrl: "https://www.1tamilcrow.net/category/vijay-tv-shows/cooku-with-comali-season-7/",
    seasonNumber: 7,
    isLiveStream: false,
  },
  {
    id: "ss-ep-latest-1",
    showName: "Super Singer",
    title: "Super Singer Junior 11 — Latest Musical Round HD",
    date: "Season 11 • 1080p HD",
    thumbnailUrl: SHOW_METADATA["Super Singer"].poster,
    episodePageUrl: "https://www.1tamilcrow.net/category/vijay-tv-shows/super-singer-junior-season-11/",
    seasonNumber: 11,
    isLiveStream: false,
  },
  {
    id: "nn-ep-latest-1",
    showName: "Neeya Naana",
    title: "Neeya Naana — Sunday Special Debate Full Episode",
    date: "Latest Debate • 1080p HD",
    thumbnailUrl: SHOW_METADATA["Neeya Naana"].poster,
    episodePageUrl: "https://www.1tamilcrow.net/category/vijay-tv-shows/neeya-naana/",
    seasonNumber: 1,
    isLiveStream: false,
  },
];

const INITIAL_MOVIES = [
  {
    id: "mov-thug-life",
    title: "Thug Life (2025)",
    languageAndYear: "Tamil • 2025",
    qualityBadge: "1080p HDRip",
    posterUrl: "https://www.5movierulz.skin/uploads/Thug-Life-Tamil.jpg",
    moviePageUrl: "https://www.5movierulz.skin/thug-life-2025-hdrip-tamil-full-movie-watch-online-free/",
  },
  {
    id: "mov-retro",
    title: "Retro (2025)",
    languageAndYear: "Tamil • 2025",
    qualityBadge: "1080p HDRip",
    posterUrl: "https://www.5movierulz.skin/uploads/Retro-Tamil.jpg",
    moviePageUrl: "https://www.5movierulz.skin/retro-2025-hdrip-tamil-full-movie-watch-online-free/",
  },
  {
    id: "mov-good-bad-ugly",
    title: "Good Bad Ugly (2025)",
    languageAndYear: "Tamil • 2025",
    qualityBadge: "1080p HDRip",
    posterUrl: "https://www.5movierulz.skin/uploads/Good-Bad-Ugly-Tamil.jpg",
    moviePageUrl: "https://www.5movierulz.skin/good-bad-ugly-2025-hdrip-tamil-full-movie-watch-online-free/",
  },
  {
    id: "mov-veera-dheera-sooran",
    title: "Veera Dheera Sooran (2025)",
    languageAndYear: "Tamil • 2025",
    qualityBadge: "1080p HDRip",
    posterUrl: "https://www.5movierulz.skin/uploads/Veera-Dheera-Sooran-Tamil.jpg",
    moviePageUrl: "https://www.5movierulz.skin/veera-dheera-sooran-2025-hdrip-tamil-full-movie-watch-online-free/",
  },
];

// Application State
const state = {
  activeTab: "home", // home | shows | movies | live | mylist
  selectedShow: "Bigg Boss Tamil",
  selectedSeason: null,
  heroIndex: 0,
  shelfOpenForShow: null,
  episodes: [...INITIAL_EPISODES],
  movies: [...INITIAL_MOVIES],
  loadingCatalog: true,
  searchModalOpen: false,
  searchQuery: "",
  toastMessage: null,
  myList: loadJsonStorage("rasagulla_mylist_v3", []),
  watchHistory: loadJsonStorage("rasagulla_history_v3", {}),
  focusMemory: {},
  player: {
    open: false,
    episode: null,
    sources: [],
    activeSourceIndex: 0,
    loadingStream: false,
    error: null,
    isPlaying: true,
    isLive: false,
    currentTime: 0,
    duration: 0,
    selectedQuality: "1080p",
    useProxy: false,
    fitCover: false,
    hudVisible: true,
    exitConfirmOpen: false,
    hlsInstance: null,
    hudTimer: null,
    progressInterval: null,
  },
};

function loadJsonStorage(key, fallback) {
  try {
    const raw = localStorage.getItem(key);
    return raw ? JSON.parse(raw) : fallback;
  } catch (_) {
    return fallback;
  }
}

function saveJsonStorage(key, val) {
  try {
    localStorage.setItem(key, JSON.stringify(val));
  } catch (_) {}
}

function showToast(msg) {
  state.toastMessage = msg;
  renderToast();
  clearTimeout(window.__toastTimer);
  window.__toastTimer = setTimeout(() => {
    state.toastMessage = null;
    renderToast();
  }, 3000);
}

function renderToast() {
  let el = document.getElementById("rasagulla-toast");
  if (!state.toastMessage) {
    if (el) el.remove();
    return;
  }
  if (!el) {
    el = document.createElement("div");
    el.id = "rasagulla-toast";
    el.className = "toast-banner";
    document.body.appendChild(el);
  }
  el.textContent = state.toastMessage;
}

function escapeHtml(str) {
  return String(str || "")
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

function escapeAttr(str) {
  return escapeHtml(str).replace(/'/g, "&#39;");
}

/**
 * Ensures any tamildhool hotlink-protected thumbnail is routed through /api/image
 * and empty thumbnails receive a verified show poster.
 */
function resolveSafeImageUrl(rawUrl, showOrMovieTitle = "Rasagulla HD") {
  const meta = SHOW_METADATA[showOrMovieTitle];
  const fallbackPoster = meta ? meta.poster : buildSvgPosterDataUri(showOrMovieTitle, "1080P HD");
  if (!rawUrl || typeof rawUrl !== "string") return fallbackPoster;
  const cleaned = rawUrl.replace(/[\r\n]+/g, "").trim();
  if (!cleaned) return fallbackPoster;
  if (cleaned.startsWith("data:") || cleaned.startsWith("/api/image") || cleaned.startsWith("/assets/")) {
    return cleaned;
  }
  if (/tamildhool\./i.test(cleaned)) {
    return `/api/image?url=${encodeURIComponent(cleaned)}&title=${encodeURIComponent(showOrMovieTitle)}`;
  }
  return cleaned;
}

function isInMyList(id) {
  return state.myList.some((item) => item.id === id);
}

function toggleMyList(item) {
  const idx = state.myList.findIndex((x) => x.id === item.id);
  if (idx >= 0) {
    state.myList.splice(idx, 1);
    showToast(`Removed from My List`);
  } else {
    state.myList.unshift({
      id: item.id,
      title: item.title,
      showName: item.showName || "Tamil Cinema",
      date: item.date || item.languageAndYear || "1080p HD",
      thumbnailUrl: item.thumbnailUrl || item.posterUrl,
      episodePageUrl: item.episodePageUrl || item.moviePageUrl,
      isLiveStream: Boolean(item.isLiveStream),
    });
    showToast(`Added to My List`);
  }
  saveJsonStorage("rasagulla_mylist_v3", state.myList);
  renderApp();
}

function saveWatchProgress(episode, positionSec, durationSec) {
  if (!episode || !episode.id || durationSec <= 0 || episode.isLiveStream) return;
  const ratio = Math.min(1, Math.max(0, positionSec / durationSec));
  state.watchHistory[episode.id] = {
    id: episode.id,
    title: episode.title,
    showName: episode.showName || "Tamil Cinema",
    date: episode.date || "1080p HD",
    thumbnailUrl: episode.thumbnailUrl || episode.posterUrl,
    episodePageUrl: episode.episodePageUrl || episode.moviePageUrl,
    isLiveStream: Boolean(episode.isLiveStream),
    positionSec: Math.floor(positionSec),
    durationSec: Math.floor(durationSec),
    progressRatio: ratio,
    updatedAt: Date.now(),
  };
  saveJsonStorage("rasagulla_history_v3", state.watchHistory);
}

function formatTime(sec) {
  if (!Number.isFinite(sec) || sec < 0) return "0:00";
  const s = Math.floor(sec);
  const hrs = Math.floor(s / 3600);
  const mins = Math.floor((s % 3600) / 60);
  const secs = s % 60;
  if (hrs > 0) {
    return `${hrs}:${String(mins).padStart(2, "0")}:${String(secs).padStart(2, "0")}`;
  }
  return `${mins}:${String(secs).padStart(2, "0")}`;
}

// Fetch Live Catalog from Serverless API
async function fetchLiveCatalog(silent = false) {
  if (!silent) {
    state.loadingCatalog = true;
    renderApp();
  }
  try {
    const res = await fetch("/api/catalog");
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const data = await res.json();
    if (Array.isArray(data.episodes) && data.episodes.length > 0) {
      state.episodes = data.episodes;
    }
    if (Array.isArray(data.movies) && data.movies.length > 0) {
      state.movies = data.movies;
    }
    if (!silent) {
      showToast(`Synced ${state.episodes.length} episodes & ${state.movies.length} movies`);
    }
  } catch (err) {
    console.warn("Catalog sync fallback:", err);
  } finally {
    state.loadingCatalog = false;
    renderApp();
  }
}

// ============================================================================
// MAIN RENDER ENGINE
// ============================================================================

function renderApp() {
  const root = document.getElementById("app");
  if (!root) return;

  const activeFocusId = document.activeElement?.getAttribute("data-focus-id");

  root.innerHTML = `
    <div class="app-shell">
      ${renderLeftRail()}
      <div class="main-stage">
        ${renderSleekTopBar()}
        ${state.activeTab === "home" ? renderClusterPortalHero() : ""}
        <div class="content-container">
          ${renderActiveTabView()}
        </div>
      </div>
      ${renderMobileBottomNav()}
    </div>
    ${state.shelfOpenForShow ? renderQuickShelfModal() : ""}
    ${state.searchModalOpen ? renderSearchModal() : ""}
    ${state.player.open ? renderPlayerModal() : ""}
  `;

  if (activeFocusId) {
    const target = document.querySelector(`[data-focus-id="${CSS.escape(activeFocusId)}"]`);
    if (target) target.focus({ preventScroll: true });
  }
}

function renderLeftRail() {
  const navItems = [
    { id: "home", label: "Home Hub", icon: ICONS.home },
    { id: "shows", label: "TV Shows", icon: ICONS.tv },
    { id: "movies", label: "Movies (1080p)", icon: ICONS.film },
    { id: "live", label: "24/7 Live TV", icon: ICONS.live },
    {
      id: "mylist",
      label: "My List",
      icon: ICONS.bookmark,
      badge: state.myList.length > 0 ? state.myList.length : null,
    },
  ];

  return `
    <aside class="tv-left-rail" aria-label="Main Navigation">
      <div class="brand-header" tabindex="0" data-focusable data-focus-id="rail-brand" onclick="window.__switchTab('home')">
        <div class="brand-emblem">
          <img src="/assets/ic_launcher_playstore.png" alt="" onerror="this.style.display='none'" />
        </div>
        <div>
          <div class="brand-title">Rasagulla</div>
          <div class="brand-subtitle">Cinema &amp; Live TV</div>
        </div>
      </div>

      <nav class="rail-nav-list">
        ${navItems
          .map(
            (item) => `
          <button
            class="rail-nav-btn ${state.activeTab === item.id ? "active" : ""}"
            data-focusable
            data-focus-id="rail-nav-${item.id}"
            onclick="window.__switchTab('${item.id}')"
          >
            ${item.icon}
            <span>${item.label}</span>
            ${item.badge ? `<span class="rail-nav-badge">${item.badge}</span>` : ""}
          </button>
        `
          )
          .join("")}
      </nav>

      <div class="rail-footer-hint">
        <div style="font-weight:800; color:#fff; margin-bottom:4px;">Smart TV &amp; Web Ready</div>
        Use <b>Arrow Keys / D-Pad</b> to navigate, <b>Enter</b> to select, and <b>Esc / Back</b> to return.
      </div>
    </aside>
  `;
}

function renderSleekTopBar() {
  return `
    <header class="sleek-top-bar">
      <div class="mobile-brand" onclick="window.__switchTab('home')">
        <div class="brand-emblem" style="width:34px; height:34px; border-radius:9px;">
          <img src="/assets/ic_launcher_playstore.png" alt="" onerror="this.style.display='none'" />
        </div>
        <div>
          <div class="brand-title" style="font-size:17px;">Rasagulla</div>
          <div class="brand-subtitle" style="font-size:8.5px;">1080P STREAM</div>
        </div>
      </div>

      <div class="top-bar-actions">
        <button
          class="icon-glass-btn"
          title="Search Shows & Movies"
          aria-label="Search Shows & Movies"
          data-focusable
          data-focus-id="top-search-btn"
          onclick="window.__openSearch()"
        >
          ${ICONS.search}
        </button>

        <button
          class="icon-glass-btn"
          title="Refresh Live Feeds"
          aria-label="Refresh Live Feeds"
          data-focusable
          data-focus-id="top-refresh-btn"
          onclick="window.__refreshFeeds()"
        >
          ${ICONS.refresh}
        </button>

        <button
          class="pill-action-btn live-pill"
          data-focusable
          data-focus-id="top-live-btn"
          onclick="window.__playLive24x7()"
        >
          <span class="live-dot"></span>
          <span>24/7 Live</span>
        </button>
      </div>
    </header>
  `;
}

function renderClusterPortalHero() {
  const focusedChar = HERO_CHARACTERS[state.heroIndex] || HERO_CHARACTERS[0];
  const hudLeftPct = Math.min(72, Math.max(12, Math.round(focusedChar.targetFractionX * 100 - 12)));

  return `
    <section class="cluster-portal-stage" aria-label="Featured Shows & Characters">
      <div
        class="cluster-radial-glow"
        style="background: radial-gradient(circle at ${Math.round(
          focusedChar.targetFractionX * 100
        )}% 58%, ${focusedChar.accentHex}44 0%, ${focusedChar.accentHex}14 36%, transparent 72%);"
      ></div>

      <div class="cluster-top-badge">
        <span class="cluster-top-pill">
          ${ICONS.sparkle}
          <span>Interactive TV Character Stage</span>
        </span>
        <div class="cluster-dots">
          ${HERO_CHARACTERS.map(
            (c, idx) =>
              `<span class="cluster-dot ${idx === state.heroIndex ? "active" : ""}"></span>`
          ).join("")}
        </div>
      </div>

      <div class="cluster-hud-header" style="left: ${hudLeftPct}%;">
        <div>
          <div class="cluster-hud-title">${escapeHtml(focusedChar.title)}</div>
          <div class="cluster-hud-sub" style="color: ${focusedChar.accentHex};">
            ${escapeHtml(focusedChar.subtitle)}
          </div>
        </div>
      </div>

      <div class="cluster-cutouts-layer">
        ${HERO_CHARACTERS.map((char, idx) => {
          const isFocused = idx === state.heroIndex;
          const zIdx = isFocused ? 16 : 6 - Math.min(5, Math.abs(idx - state.heroIndex));
          return `
            <div
              class="cluster-cutout-node ${isFocused ? "focused" : "inactive"}"
              style="left: ${(char.targetFractionX * 100).toFixed(1)}%; z-index: ${zIdx};"
              onclick="window.__selectHeroChar(${idx}, true)"
              title="${escapeAttr(char.title)}"
            >
              <img
                src="${char.cutoutUrl}"
                alt=""
                draggable="false"
                onerror="this.style.opacity='0'"
              />
            </div>
          `;
        }).join("")}
      </div>

      <div class="cluster-bottom-vignette"></div>

      <div class="cluster-controls-bar">
        <button
          class="cluster-arrow-btn"
          aria-label="Previous Character"
          data-focusable
          data-focus-id="hero-prev-btn"
          onclick="window.__cycleHero(-1)"
        >
          ${ICONS.chevronLeft}
        </button>

        <div class="cluster-center-controls">
          <button
            class="cluster-explore-btn"
            data-focusable
            data-focus-id="hero-primary-btn"
            data-hero-stage="true"
            onclick="window.__activateHeroChar()"
          >
            ${ICONS.play}
            <span>Explore ${escapeHtml(focusedChar.title)}</span>
          </button>

          ${
            focusedChar.openShelf
              ? `
            <button
              class="cluster-secondary-btn"
              data-focusable
              data-focus-id="hero-all-seasons-btn"
              onclick="window.__openShowHub('${escapeAttr(focusedChar.targetShow)}')"
            >
              <span>All Episodes</span>
            </button>
          `
              : ""
          }
        </div>

        <button
          class="cluster-arrow-btn"
          aria-label="Next Character"
          data-focusable
          data-focus-id="hero-next-btn"
          onclick="window.__cycleHero(1)"
        >
          ${ICONS.chevronRight}
        </button>
      </div>
    </section>
  `;
}

function renderQuickShelfModal() {
  const showName = state.shelfOpenForShow;
  const meta = SHOW_METADATA[showName] || SHOW_METADATA["Bigg Boss Tamil"];
  const eps = state.episodes.filter((e) => e.showName === showName).slice(0, 12);

  return `
    <div class="shelf-modal-backdrop" onclick="if(event.target===this) window.__closeShelfModal()">
      <div class="shelf-modal-sheet" role="dialog" aria-modal="true">
        <div class="section-header" style="margin-bottom:4px;">
          <div>
            <div style="font-size:11px; font-weight:800; color:${meta.accent}; text-transform:uppercase; letter-spacing:1px;">
              ${escapeHtml(meta.badge)}
            </div>
            <div style="font-size:20px; font-weight:900; color:#fff; margin-top:2px;">
              ${escapeHtml(showName)} — Quick Shelf
            </div>
          </div>
          <div style="display:flex; gap:8px; align-items:center;">
            <button
              class="section-link-btn"
              data-focusable
              data-focus-id="shelf-view-all-btn"
              onclick="window.__openShowHub('${escapeAttr(showName)}')"
            >
              <span>All Seasons</span>
              ${ICONS.chevronRight}
            </button>
            <button
              class="icon-glass-btn"
              aria-label="Close Shelf"
              data-focusable
              data-focus-id="shelf-close-btn"
              onclick="window.__closeShelfModal()"
            >
              ${ICONS.close}
            </button>
          </div>
        </div>

        <div class="shelf-episodes-list">
          ${eps
            .map((ep, idx) => {
              const safeThumb = resolveSafeImageUrl(ep.thumbnailUrl, ep.showName);
              return `
              <button
                class="shelf-ep-item"
                data-focusable
                data-focus-id="shelf-ep-${idx}"
                onclick="window.__playEpisodeById('${escapeAttr(ep.id)}')"
              >
                <div class="shelf-ep-thumb">
                  <img
                    src="${escapeAttr(safeThumb)}"
                    alt=""
                    loading="lazy"
                    onerror="window.__onImgErr(this, '${escapeAttr(ep.showName)}', '1080P HD')"
                  />
                </div>
                <div style="flex:1; min-width:0;">
                  <div style="font-size:13.5px; font-weight:800; color:#fff; line-height:1.35; display:-webkit-box; -webkit-line-clamp:2; -webkit-box-orient:vertical; overflow:hidden;">
                    ${escapeHtml(ep.title)}
                  </div>
                  <div style="font-size:11.5px; color:var(--text-secondary); margin-top:4px;">
                    ${escapeHtml(ep.date)} • 1080p HD
                  </div>
                </div>
                <div class="play-circle-icon" style="width:36px; height:36px; flex-shrink:0;">
                  ${ICONS.play}
                </div>
              </button>
            `;
            })
            .join("")}
        </div>
      </div>
    </div>
  `;
}

function renderActiveTabView() {
  if (state.activeTab === "shows") return renderShowsHubView();
  if (state.activeTab === "movies") return renderMoviesGridView();
  if (state.activeTab === "live") return renderLiveStreamHubView();
  if (state.activeTab === "mylist") return renderMyListView();
  return renderHomeHubView();
}

function renderHomeHubView() {
  const showKeys = Object.keys(SHOW_METADATA);
  const selectedShowEps = state.episodes.filter((e) => e.showName === state.selectedShow);
  const historyItems = Object.values(state.watchHistory)
    .sort((a, b) => (b.updatedAt || 0) - (a.updatedAt || 0))
    .slice(0, 10);

  return `
    <!-- Show Filter Pill Bar -->
    <div class="show-pills-bar" role="tablist" aria-label="Show Filter">
      ${showKeys
        .map((key, idx) => {
          const meta = SHOW_METADATA[key];
          const isActive = state.selectedShow === key;
          return `
            <button
              class="show-pill-btn ${isActive ? "active" : ""}"
              data-focusable
              data-focus-id="show-pill-${idx}"
              onclick="window.__selectShowPill('${escapeAttr(key)}')"
            >
              ${ICONS.tv}
              <span>${escapeHtml(meta.shortTitle)}</span>
            </button>
          `;
        })
        .join("")}
    </div>

    ${
      historyItems.length > 0
        ? `
      <section>
        <div class="section-header">
          <div class="section-title-group">
            <h2 class="section-title">${ICONS.play}<span>Continue Watching</span></h2>
            <span class="section-badge">Resume Playback</span>
          </div>
        </div>
        <div class="horizontal-rail">
          ${historyItems.map((item, idx) => renderEpisodeCard(item, `cont-${idx}`, item.progressRatio)).join("")}
        </div>
      </section>
    `
        : ""
    }

    <!-- Selected Show Latest Episodes Rail -->
    <section>
      <div class="section-header">
        <div class="section-title-group">
          <h2 class="section-title">
            ${ICONS.sparkle}
            <span>${escapeHtml(state.selectedShow)}</span>
          </h2>
          <span class="section-badge">${escapeHtml(SHOW_METADATA[state.selectedShow]?.badge || "1080p HD")}</span>
        </div>
        <button
          class="section-link-btn"
          data-focusable
          data-focus-id="home-open-show-hub"
          onclick="window.__openShowHub('${escapeAttr(state.selectedShow)}')"
        >
          <span>All Episodes</span>
          ${ICONS.chevronRight}
        </button>
      </div>
      <div class="horizontal-rail">
        ${selectedShowEps
          .slice(0, 14)
          .map((ep, idx) => renderEpisodeCard(ep, `sel-ep-${idx}`))
          .join("")}
      </div>
    </section>

    <!-- All Other Featured Shows Rails -->
    ${showKeys
      .filter((k) => k !== state.selectedShow)
      .map((showKey, sIdx) => {
        const eps = state.episodes.filter((e) => e.showName === showKey).slice(0, 10);
        if (eps.length === 0) return "";
        const meta = SHOW_METADATA[showKey];
        return `
          <section>
            <div class="section-header">
              <div class="section-title-group">
                <h2 class="section-title">${ICONS.tv}<span>${escapeHtml(showKey)}</span></h2>
                <span class="section-badge">${escapeHtml(meta.badge)}</span>
              </div>
              <button
                class="section-link-btn"
                data-focusable
                data-focus-id="home-show-more-${sIdx}"
                onclick="window.__openShowHub('${escapeAttr(showKey)}')"
              >
                <span>See All</span>
                ${ICONS.chevronRight}
              </button>
            </div>
            <div class="horizontal-rail">
              ${eps.map((ep, idx) => renderEpisodeCard(ep, `rail-${sIdx}-${idx}`)).join("")}
            </div>
          </section>
        `;
      })
      .join("")}

    <!-- Latest Tamil Movies (4-in-a-row Grid Preview) -->
    <section>
      <div class="section-header">
        <div class="section-title-group">
          <h2 class="section-title">${ICONS.film}<span>Latest Tamil Movies (1080p HD)</span></h2>
          <span class="section-badge">Multi-Mirror Direct Play</span>
        </div>
        <button
          class="section-link-btn"
          data-focusable
          data-focus-id="home-movies-all"
          onclick="window.__switchTab('movies')"
        >
          <span>Full Catalog</span>
          ${ICONS.chevronRight}
        </button>
      </div>
      <div class="movies-grid-4col">
        ${state.movies
          .slice(0, 8)
          .map((mov, idx) => renderMovieCard(mov, `home-mov-${idx}`))
          .join("")}
      </div>
    </section>
  `;
}

function renderShowsHubView() {
  const showKeys = Object.keys(SHOW_METADATA);
  const showEps = state.episodes.filter((e) => e.showName === state.selectedShow);
  const seasons = Array.from(new Set(showEps.map((e) => e.seasonNumber || 1))).sort((a, b) => b - a);
  const activeSeason = state.selectedSeason && seasons.includes(state.selectedSeason) ? state.selectedSeason : seasons[0];
  const filteredEps = activeSeason ? showEps.filter((e) => (e.seasonNumber || 1) === activeSeason) : showEps;

  return `
    <div class="section-header">
      <div class="section-title-group">
        <h1 class="section-title" style="font-size:22px;">${ICONS.tv}<span>TV Shows &amp; Seasons Hub</span></h1>
        <span class="section-badge">${filteredEps.length} Episodes Available</span>
      </div>
    </div>

    <div class="show-pills-bar">
      ${showKeys
        .map(
          (key, idx) => `
        <button
          class="show-pill-btn ${state.selectedShow === key ? "active" : ""}"
          data-focusable
          data-focus-id="shows-tab-pill-${idx}"
          onclick="window.__selectShowPill('${escapeAttr(key)}')"
        >
          ${ICONS.tv}
          <span>${escapeHtml(key)}</span>
        </button>
      `
        )
        .join("")}
    </div>

    ${
      seasons.length > 1
        ? `
      <div class="show-pills-bar" style="padding-top:0;">
        ${seasons
          .map(
            (sNum) => `
          <button
            class="pill-action-btn ${activeSeason === sNum ? "live-pill" : ""}"
            data-focusable
            data-focus-id="season-pill-${sNum}"
            onclick="window.__selectSeason(${sNum})"
          >
            <span>Season ${sNum}</span>
          </button>
        `
          )
          .join("")}
      </div>
    `
        : ""
    }

    <div class="movies-grid-4col">
      ${filteredEps.map((ep, idx) => renderEpisodeCard(ep, `hub-ep-${idx}`, undefined, true)).join("")}
    </div>
  `;
}

function renderMoviesGridView() {
  return `
    <div class="section-header">
      <div class="section-title-group">
        <h1 class="section-title" style="font-size:22px;">${ICONS.film}<span>Tamil Blockbuster Movies</span></h1>
        <span class="section-badge">Streamlare • Filelions • 1080p HLS</span>
      </div>
    </div>
    <div class="movies-grid-4col">
      ${state.movies.map((mov, idx) => renderMovieCard(mov, `grid-mov-${idx}`)).join("")}
    </div>
  `;
}

function renderLiveStreamHubView() {
  const liveEps = state.episodes.filter((e) => e.isLiveStream);
  return `
    <div class="section-header">
      <div class="section-title-group">
        <h1 class="section-title" style="font-size:22px;">${ICONS.live}<span>24/7 Live Broadcast Channels</span></h1>
        <span class="section-badge">Real-Time 60fps HLS Feeds</span>
      </div>
    </div>
    <div class="movies-grid-4col">
      ${liveEps.map((ep, idx) => renderEpisodeCard(ep, `live-ep-${idx}`, undefined, true)).join("")}
    </div>
  `;
}

function renderMyListView() {
  const historyItems = Object.values(state.watchHistory).sort(
    (a, b) => (b.updatedAt || 0) - (a.updatedAt || 0)
  );
  return `
    <section>
      <div class="section-header">
        <div class="section-title-group">
          <h1 class="section-title" style="font-size:22px;">${ICONS.bookmark}<span>My Saved List</span></h1>
          <span class="section-badge">${state.myList.length} Saved</span>
        </div>
      </div>
      ${
        state.myList.length === 0
          ? `<div style="padding:36px; border-radius:16px; background:var(--bg-card); border:1px solid var(--border-subtle); color:var(--text-secondary); text-align:center;">
              No saved episodes or movies yet. Tap the bookmark button inside the player to save titles here.
            </div>`
          : `<div class="movies-grid-4col">
              ${state.myList.map((ep, idx) => renderEpisodeCard(ep, `mylist-${idx}`, undefined, true)).join("")}
            </div>`
      }
    </section>

    ${
      historyItems.length > 0
        ? `
      <section style="margin-top:24px;">
        <div class="section-header">
          <div class="section-title-group">
            <h2 class="section-title">${ICONS.play}<span>Watch History</span></h2>
          </div>
          <button class="section-link-btn" data-focusable onclick="window.__clearHistory()">
            <span>Clear History</span>
          </button>
        </div>
        <div class="movies-grid-4col">
          ${historyItems.map((item, idx) => renderEpisodeCard(item, `hist-${idx}`, item.progressRatio, true)).join("")}
        </div>
      </section>
    `
        : ""
    }
  `;
}

function renderEpisodeCard(ep, focusId, progressRatio, fluidWidth = false) {
  const historyEntry = state.watchHistory[ep.id];
  const ratio =
    typeof progressRatio === "number"
      ? progressRatio
      : historyEntry
      ? historyEntry.progressRatio
      : 0;
  const safeThumb = resolveSafeImageUrl(ep.thumbnailUrl, ep.showName);
  const badgeLabel = ep.isLiveStream ? "LIVE 24/7" : "1080p HD";

  return `
    <button
      class="media-card"
      style="${fluidWidth ? "width:100%;" : ""}"
      data-focusable
      data-focus-id="${focusId}"
      onclick="window.__playEpisodeById('${escapeAttr(ep.id)}')"
    >
      <div class="media-thumb-wrap">
        <img
          src="${escapeAttr(safeThumb)}"
          alt=""
          loading="lazy"
          onerror="window.__onImgErr(this, '${escapeAttr(ep.showName || ep.title)}', '${escapeAttr(badgeLabel)}')"
        />
        <span class="media-badge-top ${ep.isLiveStream ? "media-badge-live" : ""}">
          ${ep.isLiveStream ? "● LIVE 1080P" : `S${ep.seasonNumber || 1} • 1080P`}
        </span>
        <div class="media-play-badge-center">
          <div class="play-circle-icon">${ICONS.play}</div>
        </div>
        ${
          ratio > 0.02
            ? `<div class="media-progress-track"><div class="media-progress-fill" style="width:${Math.round(
                ratio * 100
              )}%;"></div></div>`
            : ""
        }
      </div>
      <div class="media-card-body">
        <div class="media-card-show">${escapeHtml(ep.showName || "Tamil Series")}</div>
        <div class="media-card-title">${escapeHtml(ep.title)}</div>
        <div class="media-card-meta">
          <span>${escapeHtml(ep.date || "Latest Broadcast")}</span>
          <span style="color:var(--accent-rose); font-weight:800;">Watch</span>
        </div>
      </div>
    </button>
  `;
}

function renderMovieCard(mov, focusId) {
  const safePoster = resolveSafeImageUrl(mov.posterUrl, mov.title);
  return `
    <button
      class="movie-poster-card"
      data-focusable
      data-focus-id="${focusId}"
      onclick="window.__playMovieById('${escapeAttr(mov.id)}')"
    >
      <div class="movie-poster-wrap">
        <img
          src="${escapeAttr(safePoster)}"
          alt=""
          loading="lazy"
          onerror="window.__onImgErr(this, '${escapeAttr(mov.title)}', '${escapeAttr(mov.qualityBadge || "1080P HD")}')"
        />
        <span class="media-badge-top">${escapeHtml(mov.qualityBadge || "1080p HDRip")}</span>
        <div class="media-play-badge-center">
          <div class="play-circle-icon">${ICONS.play}</div>
        </div>
      </div>
      <div class="media-card-body">
        <div class="media-card-show">${escapeHtml(mov.languageAndYear || "Tamil • 2026")}</div>
        <div class="media-card-title">${escapeHtml(mov.title)}</div>
        <div class="media-card-meta">
          <span>Direct Stream</span>
          <span style="color:var(--accent-cyan); font-weight:800;">Play HD</span>
        </div>
      </div>
    </button>
  `;
}

function renderMobileBottomNav() {
  const items = [
    { id: "home", label: "Home", icon: ICONS.home },
    { id: "shows", label: "Shows", icon: ICONS.tv },
    { id: "movies", label: "Movies", icon: ICONS.film },
    { id: "live", label: "Live", icon: ICONS.live },
    { id: "mylist", label: "My List", icon: ICONS.bookmark },
  ];
  return `
    <nav class="mobile-bottom-nav" aria-label="Mobile Navigation">
      ${items
        .map(
          (it) => `
        <button
          class="mobile-nav-btn ${state.activeTab === it.id ? "active" : ""}"
          onclick="window.__switchTab('${it.id}')"
        >
          ${it.icon}
          <span>${it.label}</span>
        </button>
      `
        )
        .join("")}
    </nav>
  `;
}

function renderSearchModal() {
  const q = state.searchQuery.trim().toLowerCase();
  const matchedEps = q
    ? state.episodes.filter(
        (e) =>
          e.title.toLowerCase().includes(q) ||
          (e.showName && e.showName.toLowerCase().includes(q)) ||
          (e.date && e.date.toLowerCase().includes(q))
      )
    : state.episodes.slice(0, 6);

  const matchedMovies = q
    ? state.movies.filter((m) => m.title.toLowerCase().includes(q))
    : state.movies.slice(0, 4);

  return `
    <div class="modal-backdrop" onclick="if(event.target===this) window.__closeSearch()">
      <div class="modal-card" style="max-width:740px; max-height:86dvh; overflow:hidden;">
        <div style="display:flex; align-items:center; justify-content:space-between; gap:12px;">
          <div style="font-size:18px; font-weight:900;">Search Shows, Episodes &amp; Movies</div>
          <button class="icon-glass-btn" data-focusable onclick="window.__closeSearch()">
            ${ICONS.close}
          </button>
        </div>

        <input
          id="rasagulla-search-input"
          type="text"
          placeholder="Search Bigg Boss, MasterChef, Cooku with Comali, Retro..."
          value="${escapeAttr(state.searchQuery)}"
          oninput="window.__updateSearchQuery(this.value)"
          style="width:100%; padding:13px 16px; border-radius:12px; border:1px solid var(--border-bright); background:#070a12; color:#fff; font-size:14.5px; outline:none;"
        />

        <div style="overflow-y:auto; display:flex; flex-direction:column; gap:14px; padding-right:4px;">
          <div>
            <div style="font-size:11.5px; font-weight:800; color:var(--accent-cyan); text-transform:uppercase; margin-bottom:8px;">
              Episodes (${matchedEps.length})
            </div>
            <div class="shelf-episodes-list">
              ${matchedEps
                .slice(0, 8)
                .map((ep, idx) => {
                  const safeThumb = resolveSafeImageUrl(ep.thumbnailUrl, ep.showName);
                  return `
                  <button
                    class="shelf-ep-item"
                    data-focusable
                    data-focus-id="search-ep-${idx}"
                    onclick="window.__closeSearch(); window.__playEpisodeById('${escapeAttr(ep.id)}')"
                  >
                    <div class="shelf-ep-thumb">
                      <img src="${escapeAttr(safeThumb)}" alt="" onerror="window.__onImgErr(this, '${escapeAttr(ep.showName)}', '1080P')" />
                    </div>
                    <div style="flex:1; min-width:0;">
                      <div style="font-size:13px; font-weight:800; color:#fff;">${escapeHtml(ep.title)}</div>
                      <div style="font-size:11px; color:var(--text-secondary); margin-top:2px;">${escapeHtml(ep.showName)} • ${escapeHtml(ep.date)}</div>
                    </div>
                  </button>
                `;
                })
                .join("")}
            </div>
          </div>

          ${
            matchedMovies.length > 0
              ? `
            <div>
              <div style="font-size:11.5px; font-weight:800; color:var(--accent-gold); text-transform:uppercase; margin-bottom:8px;">
                Movies (${matchedMovies.length})
              </div>
              <div class="shelf-episodes-list">
                ${matchedMovies
                  .slice(0, 6)
                  .map((mov, idx) => {
                    const safePoster = resolveSafeImageUrl(mov.posterUrl, mov.title);
                    return `
                    <button
                      class="shelf-ep-item"
                      data-focusable
                      data-focus-id="search-mov-${idx}"
                      onclick="window.__closeSearch(); window.__playMovieById('${escapeAttr(mov.id)}')"
                    >
                      <div class="shelf-ep-thumb">
                        <img src="${escapeAttr(safePoster)}" alt="" onerror="window.__onImgErr(this, '${escapeAttr(mov.title)}', '1080P')" />
                      </div>
                      <div style="flex:1; min-width:0;">
                        <div style="font-size:13px; font-weight:800; color:#fff;">${escapeHtml(mov.title)}</div>
                        <div style="font-size:11px; color:var(--text-secondary); margin-top:2px;">${escapeHtml(mov.languageAndYear)} • ${escapeHtml(mov.qualityBadge)}</div>
                      </div>
                    </button>
                  `;
                  })
                  .join("")}
              </div>
            </div>
          `
              : ""
          }
        </div>
      </div>
    </div>
  `;
}

// ============================================================================
// NETFLIX / APPLE TV+ / PRIME VIDEO GRADE FULLSCREEN VIDEO PLAYER
// ============================================================================

function renderPlayerModal() {
  const p = state.player;
  const ep = p.episode || {};
  const activeSource = p.sources[p.activeSourceIndex] || null;
  const saved = isInMyList(ep.id);
  const isIframe = activeSource && activeSource.type === "iframe";
  const isLive = Boolean(
    p.isLive ||
      ep.isLiveStream ||
      activeSource?.isLive ||
      (activeSource?.url && /live1channel|live2channel|watch-live-stream/i.test(activeSource.url))
  );

  return `
    <div class="player-modal" id="rasagulla-player-modal">
      <div
        class="player-video-stage ${p.fitCover ? "fit-cover" : ""}"
        id="rasagulla-video-stage"
        onclick="window.__onVideoStageTap(event)"
      >
        ${
          p.loadingStream
            ? `
          <div style="display:flex; flex-direction:column; align-items:center; gap:12px; z-index:5;">
            <div style="width:46px; height:46px; border:3.5px solid rgba(255,255,255,0.18); border-top-color:var(--accent-rose); border-radius:50%; animation:spin 0.8s linear infinite;"></div>
            <div style="font-size:14px; font-weight:800; color:#fff;">Connecting 1080p HD Stream...</div>
          </div>
          <style>@keyframes spin { to { transform: rotate(360deg); } }</style>
        `
            : p.error
            ? `
          <div style="max-width:440px; padding:24px; border-radius:18px; background:rgba(15,19,30,0.95); border:1px solid var(--border-bright); text-align:center; z-index:10;">
            <div style="font-size:16px; font-weight:900; color:#fff; margin-bottom:6px;">Stream Mirror Busy</div>
            <div style="font-size:12.5px; color:var(--text-secondary); margin-bottom:16px;">${escapeHtml(p.error)}</div>
            <div style="display:flex; gap:10px; justify-content:center; flex-wrap:wrap;">
              <button class="player-chip-btn crimson-chip" data-focusable onclick="window.__retryWithProxy()">
                ${ICONS.shield}<span>Boost with Proxy</span>
              </button>
              <button class="player-chip-btn" data-focusable onclick="window.__switchNextMirror()">
                ${ICONS.server}<span>Next Mirror</span>
              </button>
            </div>
          </div>
        `
            : isIframe
            ? `
          <iframe
            id="rasagulla-iframe-player"
            src="${escapeAttr(activeSource.url)}"
            allow="autoplay; fullscreen; encrypted-media; picture-in-picture"
            allowfullscreen
            referrerpolicy="no-referrer"
          ></iframe>
        `
            : `
          <video
            id="rasagulla-video-el"
            playsinline
            webkit-playsinline
            autoplay
          ></video>
        `
        }
      </div>

      <!-- Sleek Netflix / Apple TV+ HUD Overlay -->
      <div class="player-hud-overlay ${p.hudVisible ? "" : "hud-hidden"}" id="rasagulla-player-hud">
        <!-- Top Bar -->
        <div class="player-top-bar">
          <div class="player-top-left">
            <button
              class="player-icon-btn"
              title="Back (Esc)"
              aria-label="Back"
              data-focusable
              data-focus-id="player-back-btn"
              onclick="window.__requestClosePlayer()"
            >
              ${ICONS.back}
            </button>
            <div class="player-title-meta">
              <div class="player-title-text">${escapeHtml(ep.title || "Rasagulla Stream")}</div>
              <div class="player-subtitle-text" id="rasagulla-player-subtitle">
                ${escapeHtml(activeSource ? activeSource.label : "Resolving 1080p Mirror...")} • ${escapeHtml(p.selectedQuality)}
              </div>
            </div>
          </div>

          <div class="player-top-right">
            <button
              class="player-icon-btn"
              title="${saved ? "Saved in My List" : "Add to My List"}"
              aria-label="Toggle My List"
              data-focusable
              data-focus-id="player-bookmark-btn"
              onclick="window.__toggleCurrentEpisodeMyList()"
            >
              ${saved ? ICONS.bookmarkFilled : ICONS.bookmark}
            </button>

            ${
              !isIframe
                ? `
              <button
                class="player-icon-btn"
                title="Aspect Ratio Fit / Fill"
                aria-label="Toggle Aspect Ratio"
                data-focusable
                data-focus-id="player-fit-btn"
                onclick="window.__playerToggleFit()"
              >
                ${ICONS.fitScreen}
              </button>
            `
                : ""
            }

            <button
              class="player-icon-btn"
              title="Fullscreen & Landscape Lock"
              aria-label="Fullscreen"
              data-focusable
              data-focus-id="player-fullscreen-btn"
              onclick="window.__playerToggleFullscreen()"
            >
              ${ICONS.fullscreen}
            </button>
          </div>
        </div>

        <!-- Center Transport Overlay (Only for native HLS/MP4 video) -->
        ${
          !isIframe && !p.loadingStream
            ? `
          <div class="player-center-transport">
            ${
              !isLive
                ? `
              <button
                class="transport-circle-btn"
                aria-label="Rewind 10 seconds"
                data-focusable
                data-focus-id="player-rw-btn"
                onclick="window.__playerSeekRelative(-10)"
              >
                ${ICONS.rewind10}
              </button>
            `
                : ""
            }

            <button
              class="transport-circle-btn main-play-pause"
              id="rasagulla-play-pause-btn"
              aria-label="Play or Pause"
              data-focusable
              data-focus-id="player-play-btn"
              onclick="window.__playerTogglePlay()"
            >
              ${p.isPlaying ? ICONS.pause : ICONS.play}
            </button>

            ${
              !isLive
                ? `
              <button
                class="transport-circle-btn"
                aria-label="Forward 10 seconds"
                data-focusable
                data-focus-id="player-ff-btn"
                onclick="window.__playerSeekRelative(10)"
              >
                ${ICONS.forward10}
              </button>
            `
                : ""
            }
          </div>
        `
            : `<div></div>`
        }

        <!-- Bottom Controls Dock -->
        <div class="player-bottom-controls">
          ${
            !isIframe
              ? isLive
                ? `
              <div class="live-broadcast-bar">
                <div style="display:flex; align-items:center; gap:8px;">
                  <span class="live-dot"></span>
                  <span style="font-size:12px; font-weight:900; letter-spacing:0.6px; color:#fff;">
                    LIVE BROADCAST • 1080P 60FPS REAL-TIME STREAM
                  </span>
                </div>
                <button
                  class="player-chip-btn crimson-chip"
                  style="height:28px; padding:0 10px; font-size:11px;"
                  data-focusable
                  data-focus-id="player-sync-live-btn"
                  onclick="window.__syncLiveEdge()"
                >
                  <span>Sync Live Edge</span>
                </button>
              </div>
            `
                : `
              <div class="player-seek-row">
                <input
                  id="rasagulla-seek-slider"
                  class="player-seek-slider"
                  type="range"
                  min="0"
                  max="${Math.max(1, Math.floor(p.duration || 100))}"
                  value="${Math.floor(p.currentTime || 0)}"
                  data-focusable
                  data-focus-id="player-seek-bar"
                  oninput="window.__playerScrub(this.value)"
                />
                <span class="player-time-badge" id="rasagulla-time-badge">
                  ${formatTime(p.currentTime)} / ${formatTime(p.duration)}
                </span>
              </div>
            `
              : ""
          }

          <!-- Single-Line Horizontal Scrollable Strip for Mirrors, Quality & Proxy -->
          <div class="player-pills-dock">
            <div class="player-scroll-strip" role="toolbar" aria-label="Stream Mirrors and Quality">
              ${p.sources
                .map(
                  (src, idx) => `
                <button
                  class="player-chip-btn ${idx === p.activeSourceIndex ? "active-chip" : ""}"
                  data-focusable
                  data-focus-id="player-mirror-${idx}"
                  onclick="window.__selectStreamMirror(${idx})"
                >
                  ${ICONS.server}
                  <span>${escapeHtml(src.label)}</span>
                </button>
              `
                )
                .join("")}

              ${
                !isIframe
                  ? ["1080p", "720p", "Auto"]
                      .map(
                        (q) => `
                  <button
                    class="player-chip-btn ${p.selectedQuality === q ? "crimson-chip" : ""}"
                    data-focusable
                    data-focus-id="player-q-${q}"
                    onclick="window.__selectQuality('${q}')"
                  >
                    <span>${q === "1080p" ? "1080p HD" : q}</span>
                  </button>
                `
                      )
                      .join("")
                  : ""
              }

              ${
                !isIframe
                  ? `
                <button
                  class="player-chip-btn ${p.useProxy ? "active-chip" : ""}"
                  data-focusable
                  data-focus-id="player-proxy-btn"
                  onclick="window.__toggleProxyMode()"
                >
                  ${ICONS.shield}
                  <span>${p.useProxy ? "Proxy Boost: ON" : "Proxy Boost"}</span>
                </button>
              `
                  : ""
              }
            </div>
          </div>
        </div>
      </div>

      ${
        p.exitConfirmOpen
          ? `
        <div class="modal-backdrop">
          <div class="modal-card" style="max-width:360px; text-align:center;">
            <div style="font-size:17px; font-weight:900;">Exit Video Player?</div>
            <div style="font-size:13px; color:var(--text-secondary);">
              Your playback position is saved automatically.
            </div>
            <div style="display:flex; gap:10px; justify-content:center; margin-top:6px;">
              <button
                class="pill-action-btn live-pill"
                data-focusable
                data-focus-id="exit-confirm-yes"
                onclick="window.__confirmClosePlayer()"
              >
                <span>Exit to Catalog</span>
              </button>
              <button
                class="pill-action-btn"
                data-focusable
                data-focus-id="exit-confirm-no"
                onclick="window.__cancelClosePlayer()"
              >
                <span>Keep Watching</span>
              </button>
            </div>
          </div>
        </div>
      `
          : ""
      }
    </div>
  `;
}

// ============================================================================
// VIDEO STREAM RESOLUTION & HLS PLAYBACK CONTROLLER
// ============================================================================

async function startPlayback(mediaItem) {
  destroyActiveVideo();

  state.player.open = true;
  state.player.episode = mediaItem;
  state.player.sources = [];
  state.player.activeSourceIndex = 0;
  state.player.loadingStream = true;
  state.player.error = null;
  state.player.isPlaying = true;
  state.player.isLive = Boolean(mediaItem.isLiveStream);
  state.player.currentTime = 0;
  state.player.duration = 0;
  state.player.useProxy = false;
  state.player.hudVisible = true;
  state.player.exitConfirmOpen = false;

  renderApp();
  scheduleHudAutoHide();

  const targetUrl = mediaItem.episodePageUrl || mediaItem.moviePageUrl || "";
  try {
    const q = new URLSearchParams({
      url: targetUrl,
      title: mediaItem.title || mediaItem.showName || "",
    });
    const res = await fetch(`/api/stream?${q.toString()}`);
    const data = await res.json();
    if (Array.isArray(data.sources) && data.sources.length > 0) {
      state.player.sources = data.sources;
    }
  } catch (err) {
    console.warn("Stream resolve fallback:", err);
  }

  if (state.player.sources.length === 0) {
    state.player.sources = [
      {
        label: "Live Fallback • 1080p HD",
        url: "https://newcdn.tamils.click/live1channel/index.m3u8",
        rawUrl: "https://newcdn.tamils.click/live1channel/index.m3u8",
        type: "hls",
        isLive: true,
      },
    ];
  }

  state.player.loadingStream = false;
  state.player.activeSourceIndex = 0;
  renderApp();
  mountActiveStreamSource();
}

function destroyActiveVideo() {
  clearInterval(state.player.progressInterval);
  clearTimeout(state.player.hudTimer);
  if (state.player.hlsInstance) {
    try {
      state.player.hlsInstance.destroy();
    } catch (_) {}
    state.player.hlsInstance = null;
  }
  const vid = document.getElementById("rasagulla-video-el");
  if (vid) {
    try {
      vid.pause();
      vid.removeAttribute("src");
      vid.load();
    } catch (_) {}
  }
}

function mountActiveStreamSource() {
  destroyActiveVideo();

  const p = state.player;
  const srcObj = p.sources[p.activeSourceIndex];
  if (!srcObj) return;

  p.isLive = Boolean(
    p.episode?.isLiveStream ||
      srcObj.isLive ||
      /live1channel|live2channel|watch-live-stream/i.test(srcObj.url || "")
  );

  // Re-render HUD so live vs VOD bar matches active source
  renderApp();
  scheduleHudAutoHide();

  setTimeout(() => {
    const playBtn = document.querySelector('[data-focus-id="player-play-btn"]');
    const mirrorBtn = document.querySelector('[data-focus-id="player-mirror-0"]');
    if (playBtn) playBtn.focus({ preventScroll: true });
    else if (mirrorBtn) mirrorBtn.focus({ preventScroll: true });
  }, 60);

  if (srcObj.type === "iframe") return;

  const video = document.getElementById("rasagulla-video-el");
  if (!video) return;

  const rawOrProxiedUrl = p.useProxy
    ? srcObj.proxiedUrl || `/api/proxy?url=${encodeURIComponent(srcObj.rawUrl || srcObj.url)}`
    : srcObj.url;

  const savedEntry = state.watchHistory[p.episode?.id];
  const resumeSec = !p.isLive && savedEntry && savedEntry.positionSec > 5 ? savedEntry.positionSec : 0;

  if (window.Hls && window.Hls.isSupported() && (srcObj.type === "hls" || /\.m3u8|\/api\/proxy|hls/i.test(rawOrProxiedUrl))) {
    const hls = new window.Hls({
      enableWorker: true,
      lowLatencyMode: true,
      maxBufferLength: 30,
    });
    p.hlsInstance = hls;
    hls.loadSource(rawOrProxiedUrl);
    hls.attachMedia(video);

    hls.on(window.Hls.Events.MANIFEST_PARSED, () => {
      applyHlsQualityPreference(hls, p.selectedQuality);
      if (resumeSec > 0) {
        video.currentTime = resumeSec;
      }
      video.play().catch(() => {});
    });

    hls.on(window.Hls.Events.ERROR, (_ev, data) => {
      if (data.fatal) {
        if (!p.useProxy) {
          p.useProxy = true;
          showToast("Engaging Proxy Boost for 1080p stream...");
          mountActiveStreamSource();
        } else if (p.activeSourceIndex + 1 < p.sources.length) {
          p.useProxy = false;
          p.activeSourceIndex += 1;
          showToast(`Switching to ${p.sources[p.activeSourceIndex].label}`);
          mountActiveStreamSource();
        } else {
          p.error = "Unable to reach upstream video server. Try another mirror.";
          renderApp();
        }
      }
    });
  } else {
    video.src = rawOrProxiedUrl;
    video.addEventListener(
      "loadedmetadata",
      () => {
        if (resumeSec > 0) video.currentTime = resumeSec;
        video.play().catch(() => {});
      },
      { once: true }
    );
    video.addEventListener(
      "error",
      () => {
        if (!p.useProxy) {
          p.useProxy = true;
          mountActiveStreamSource();
        }
      },
      { once: true }
    );
  }

  video.onplay = () => {
    p.isPlaying = true;
    updatePlayPauseIcon();
  };
  video.onpause = () => {
    p.isPlaying = false;
    updatePlayPauseIcon();
  };
  video.ontimeupdate = () => {
    p.currentTime = video.currentTime || 0;
    const dur = video.duration;
    if (Number.isFinite(dur) && dur > 0) {
      // If duration is <= 60s on a live channel, mark as live
      if (dur <= 60 && /live/i.test(srcObj.label + " " + srcObj.url)) {
        p.isLive = true;
      }
      p.duration = dur;
    }
    updateSeekDom();
  };

  p.progressInterval = setInterval(() => {
    if (!video.paused && !p.isLive && p.duration > 60) {
      saveWatchProgress(p.episode, video.currentTime, p.duration);
    }
  }, 5000);
}

function applyHlsQualityPreference(hls, quality) {
  if (!hls || !Array.isArray(hls.levels) || hls.levels.length === 0) return;
  if (quality === "Auto") {
    hls.currentLevel = -1;
    return;
  }
  const targetHeight = quality === "1080p" ? 1080 : quality === "720p" ? 720 : 480;
  let bestIdx = -1;
  let bestDiff = Infinity;
  hls.levels.forEach((lvl, idx) => {
    const h = lvl.height || 720;
    const diff = Math.abs(h - targetHeight);
    if (diff < bestDiff) {
      bestDiff = diff;
      bestIdx = idx;
    }
  });
  if (bestIdx >= 0) hls.currentLevel = bestIdx;
}

function updatePlayPauseIcon() {
  const btn = document.getElementById("rasagulla-play-pause-btn");
  if (btn) {
    btn.innerHTML = state.player.isPlaying ? ICONS.pause : ICONS.play;
  }
}

function updateSeekDom() {
  const slider = document.getElementById("rasagulla-seek-slider");
  const badge = document.getElementById("rasagulla-time-badge");
  if (slider && document.activeElement !== slider) {
    slider.max = String(Math.max(1, Math.floor(state.player.duration || 100)));
    slider.value = String(Math.floor(state.player.currentTime || 0));
  }
  if (badge) {
    badge.textContent = `${formatTime(state.player.currentTime)} / ${formatTime(state.player.duration)}`;
  }
}

function scheduleHudAutoHide() {
  clearTimeout(state.player.hudTimer);
  state.player.hudVisible = true;
  const hud = document.getElementById("rasagulla-player-hud");
  if (hud) hud.classList.remove("hud-hidden");

  state.player.hudTimer = setTimeout(() => {
    if (state.player.open && state.player.isPlaying && !state.player.exitConfirmOpen) {
      state.player.hudVisible = false;
      const h = document.getElementById("rasagulla-player-hud");
      if (h) h.classList.add("hud-hidden");
    }
  }, 4200);
}

// ============================================================================
// GLOBAL WINDOW ACTIONS (INVOKED BY UI & REMOTE KEYS)
// ============================================================================

window.__switchTab = function (tabId) {
  state.activeTab = tabId;
  state.shelfOpenForShow = null;
  renderApp();
  window.scrollTo({ top: 0, behavior: "smooth" });
};

window.__selectHeroChar = function (index, triggerAction = false) {
  state.heroIndex = (index + HERO_CHARACTERS.length) % HERO_CHARACTERS.length;
  const char = HERO_CHARACTERS[state.heroIndex];
  if (char && char.targetShow) {
    state.selectedShow = char.targetShow;
  }
  renderApp();
  if (triggerAction) {
    window.__activateHeroChar();
  }
};

window.__cycleHero = function (delta) {
  state.heroIndex = (state.heroIndex + delta + HERO_CHARACTERS.length) % HERO_CHARACTERS.length;
  const char = HERO_CHARACTERS[state.heroIndex];
  if (char && char.targetShow) {
    state.selectedShow = char.targetShow;
  }
  renderApp();
  const btn = document.querySelector('[data-focus-id="hero-primary-btn"]');
  if (btn) btn.focus({ preventScroll: true });
};

window.__activateHeroChar = function () {
  const char = HERO_CHARACTERS[state.heroIndex];
  if (!char) return;
  if (char.targetTab === "movies") {
    window.__switchTab("movies");
    return;
  }
  if (char.targetTab === "shows") {
    window.__switchTab("shows");
    return;
  }
  state.selectedShow = char.targetShow;
  state.shelfOpenForShow = char.targetShow;
  renderApp();
  setTimeout(() => {
    const firstEp = document.querySelector('[data-focus-id="shelf-ep-0"]');
    if (firstEp) firstEp.focus({ preventScroll: true });
  }, 50);
};

window.__closeShelfModal = function () {
  state.shelfOpenForShow = null;
  renderApp();
  const heroBtn = document.querySelector('[data-focus-id="hero-primary-btn"]');
  if (heroBtn) heroBtn.focus({ preventScroll: true });
};

window.__openShowHub = function (showName) {
  state.selectedShow = showName;
  state.selectedSeason = null;
  state.shelfOpenForShow = null;
  state.activeTab = "shows";
  renderApp();
  window.scrollTo({ top: 0, behavior: "smooth" });
};

window.__selectShowPill = function (showName) {
  state.selectedShow = showName;
  state.selectedSeason = null;
  renderApp();
};

window.__selectSeason = function (seasonNum) {
  state.selectedSeason = Number(seasonNum);
  renderApp();
};

window.__openSearch = function () {
  state.searchModalOpen = true;
  renderApp();
  setTimeout(() => {
    const input = document.getElementById("rasagulla-search-input");
    if (input) input.focus();
  }, 50);
};

window.__closeSearch = function () {
  state.searchModalOpen = false;
  renderApp();
};

window.__updateSearchQuery = function (val) {
  state.searchQuery = val;
  renderApp();
  const input = document.getElementById("rasagulla-search-input");
  if (input) {
    input.focus();
    input.setSelectionRange(input.value.length, input.value.length);
  }
};

window.__refreshFeeds = function () {
  showToast("Refreshing live shows & movie feeds...");
  fetchLiveCatalog(false);
};

window.__playLive24x7 = function () {
  const liveEp = state.episodes.find((e) => e.isLiveStream) || INITIAL_EPISODES[0];
  startPlayback(liveEp);
};

window.__playEpisodeById = function (epId) {
  state.shelfOpenForShow = null;
  const found =
    state.episodes.find((e) => e.id === epId) ||
    state.myList.find((e) => e.id === epId) ||
    state.watchHistory[epId];
  if (found) {
    startPlayback(found);
  }
};

window.__playMovieById = function (movId) {
  const mov = state.movies.find((m) => m.id === movId);
  if (mov) {
    startPlayback({
      id: mov.id,
      title: mov.title,
      showName: mov.languageAndYear || "Tamil Cinema",
      date: mov.qualityBadge || "1080p HDRip",
      thumbnailUrl: mov.posterUrl,
      episodePageUrl: mov.moviePageUrl,
      isLiveStream: false,
    });
  }
};

window.__clearHistory = function () {
  state.watchHistory = {};
  saveJsonStorage("rasagulla_history_v3", {});
  showToast("Watch history cleared");
  renderApp();
};

// Player Interaction Handlers
let lastStageTapTime = 0;
window.__onVideoStageTap = function (event) {
  const now = Date.now();
  const rect = event.currentTarget.getBoundingClientRect();
  const relX = (event.clientX - rect.left) / Math.max(1, rect.width);

  // Double-tap left/right 35% of video stage seeks -10s / +10s on VOD
  if (now - lastStageTapTime < 280 && !state.player.isLive) {
    if (relX < 0.35) {
      window.__playerSeekRelative(-10);
      showToast("Rewound 10s");
      return;
    } else if (relX > 0.65) {
      window.__playerSeekRelative(10);
      showToast("Forwarded 10s");
      return;
    }
  }
  lastStageTapTime = now;

  if (state.player.hudVisible) {
    state.player.hudVisible = false;
    const hud = document.getElementById("rasagulla-player-hud");
    if (hud) hud.classList.add("hud-hidden");
  } else {
    scheduleHudAutoHide();
  }
};

window.__playerTogglePlay = function () {
  scheduleHudAutoHide();
  const video = document.getElementById("rasagulla-video-el");
  if (!video) return;
  if (video.paused) {
    video.play().catch(() => {});
  } else {
    video.pause();
  }
};

window.__playerSeekRelative = function (deltaSec) {
  scheduleHudAutoHide();
  const video = document.getElementById("rasagulla-video-el");
  if (!video || state.player.isLive) return;
  const target = Math.max(0, Math.min(video.duration || Infinity, (video.currentTime || 0) + deltaSec));
  video.currentTime = target;
  state.player.currentTime = target;
  updateSeekDom();
};

window.__playerScrub = function (val) {
  scheduleHudAutoHide();
  const video = document.getElementById("rasagulla-video-el");
  const sec = Number(val);
  if (video && Number.isFinite(sec)) {
    video.currentTime = sec;
    state.player.currentTime = sec;
    updateSeekDom();
  }
};

window.__syncLiveEdge = function () {
  scheduleHudAutoHide();
  const video = document.getElementById("rasagulla-video-el");
  if (video && Number.isFinite(video.duration) && video.duration > 2) {
    video.currentTime = Math.max(0, video.duration - 1);
    video.play().catch(() => {});
    showToast("Synced to live broadcast edge");
  }
};

window.__selectStreamMirror = function (idx) {
  scheduleHudAutoHide();
  state.player.activeSourceIndex = idx;
  state.player.useProxy = false;
  state.player.error = null;
  mountActiveStreamSource();
};

window.__switchNextMirror = function () {
  if (state.player.sources.length === 0) return;
  const next = (state.player.activeSourceIndex + 1) % state.player.sources.length;
  window.__selectStreamMirror(next);
};

window.__selectQuality = function (quality) {
  scheduleHudAutoHide();
  state.player.selectedQuality = quality;
  if (state.player.hlsInstance) {
    applyHlsQualityPreference(state.player.hlsInstance, quality);
  }
  showToast(`Quality set to ${quality}`);
  renderApp();
};

window.__toggleProxyMode = function () {
  scheduleHudAutoHide();
  state.player.useProxy = !state.player.useProxy;
  state.player.error = null;
  showToast(state.player.useProxy ? "Proxy Boost Enabled" : "Direct Stream Mode");
  mountActiveStreamSource();
};

window.__retryWithProxy = function () {
  state.player.useProxy = true;
  state.player.error = null;
  mountActiveStreamSource();
};

window.__playerToggleFit = function () {
  scheduleHudAutoHide();
  state.player.fitCover = !state.player.fitCover;
  const stage = document.getElementById("rasagulla-video-stage");
  if (stage) {
    stage.classList.toggle("fit-cover", state.player.fitCover);
  }
  showToast(state.player.fitCover ? "Widescreen Zoom Fill" : "Original 16:9 Fit");
};

window.__playerToggleFullscreen = async function () {
  scheduleHudAutoHide();
  const modal = document.getElementById("rasagulla-player-modal");
  const video = document.getElementById("rasagulla-video-el");
  try {
    if (!document.fullscreenElement) {
      if (modal && modal.requestFullscreen) {
        await modal.requestFullscreen();
      } else if (video && video.webkitEnterFullscreen) {
        video.webkitEnterFullscreen();
      }
      if (screen.orientation && screen.orientation.lock) {
        screen.orientation.lock("landscape").catch(() => {});
      }
    } else {
      await document.exitFullscreen();
      if (screen.orientation && screen.orientation.unlock) {
        screen.orientation.unlock();
      }
    }
  } catch (_) {
    if (video && video.webkitEnterFullscreen) {
      try {
        video.webkitEnterFullscreen();
      } catch (_e) {}
    }
  }
};

window.__toggleCurrentEpisodeMyList = function () {
  scheduleHudAutoHide();
  if (state.player.episode) {
    toggleMyList(state.player.episode);
  }
};

window.__requestClosePlayer = function () {
  state.player.exitConfirmOpen = true;
  state.player.hudVisible = true;
  renderApp();
  setTimeout(() => {
    const btn = document.querySelector('[data-focus-id="exit-confirm-yes"]');
    if (btn) btn.focus({ preventScroll: true });
  }, 40);
};

window.__cancelClosePlayer = function () {
  state.player.exitConfirmOpen = false;
  renderApp();
  scheduleHudAutoHide();
};

window.__confirmClosePlayer = function () {
  destroyActiveVideo();
  if (document.fullscreenElement && document.exitFullscreen) {
    document.exitFullscreen().catch(() => {});
  }
  state.player.open = false;
  state.player.exitConfirmOpen = false;
  renderApp();
};

// ============================================================================
// SPATIAL 2D D-PAD & TV REMOTE NAVIGATION ENGINE
// ============================================================================

function setupSpatialDpadNavigation() {
  window.addEventListener("keydown", (e) => {
    const tag = (document.activeElement?.tagName || "").toLowerCase();
    if (tag === "input" && e.key !== "Escape" && e.key !== "ArrowDown" && e.key !== "ArrowUp") {
      return;
    }

    // Player Hardware Remote Keybindings
    if (state.player.open) {
      scheduleHudAutoHide();

      if (e.key === "Escape" || e.key === "Backspace") {
        e.preventDefault();
        if (state.player.exitConfirmOpen) {
          window.__confirmClosePlayer();
        } else {
          window.__requestClosePlayer();
        }
        return;
      }
      if (e.key === "MediaPlayPause" || e.key === " ") {
        e.preventDefault();
        window.__playerTogglePlay();
        return;
      }
      if (e.key === "MediaRewind" || e.key === "[") {
        e.preventDefault();
        window.__playerSeekRelative(-10);
        return;
      }
      if (e.key === "MediaFastForward" || e.key === "]") {
        e.preventDefault();
        window.__playerSeekRelative(10);
        return;
      }
    } else {
      if (e.key === "Escape" || e.key === "Backspace") {
        if (state.searchModalOpen) {
          e.preventDefault();
          window.__closeSearch();
          return;
        }
        if (state.shelfOpenForShow) {
          e.preventDefault();
          window.__closeShelfModal();
          return;
        }
      }
    }

    // Hero Carousel Left/Right Cycling when focused on the Hero Explore button
    if (
      document.activeElement?.getAttribute("data-hero-stage") === "true" &&
      (e.key === "ArrowLeft" || e.key === "ArrowRight")
    ) {
      e.preventDefault();
      window.__cycleHero(e.key === "ArrowRight" ? 1 : -1);
      return;
    }

    if (["ArrowUp", "ArrowDown", "ArrowLeft", "ArrowRight"].includes(e.key)) {
      e.preventDefault();
      moveSpatialFocus(e.key.replace("Arrow", "").toLowerCase());
    }
  });
}

function moveSpatialFocus(direction) {
  let scopeRoot = document;
  if (state.player.open) {
    scopeRoot = document.getElementById("rasagulla-player-modal") || document;
  } else if (state.searchModalOpen) {
    scopeRoot = document.querySelector(".modal-backdrop") || document;
  } else if (state.shelfOpenForShow) {
    scopeRoot = document.querySelector(".shelf-modal-sheet") || document;
  }

  const candidates = Array.from(scopeRoot.querySelectorAll("[data-focusable]")).filter((el) => {
    const r = el.getBoundingClientRect();
    return r.width > 0 && r.height > 0;
  });

  if (candidates.length === 0) return;

  const current = document.activeElement;
  if (!current || !candidates.includes(current)) {
    candidates[0].focus({ preventScroll: false });
    candidates[0].scrollIntoView({ block: "nearest", inline: "nearest", behavior: "smooth" });
    return;
  }

  const curRect = current.getBoundingClientRect();
  const curCx = curRect.left + curRect.width / 2;
  const curCy = curRect.top + curRect.height / 2;

  let bestCandidate = null;
  let bestScore = Infinity;

  for (const el of candidates) {
    if (el === current) continue;
    const r = el.getBoundingClientRect();
    const cx = r.left + r.width / 2;
    const cy = r.top + r.height / 2;
    const dx = cx - curCx;
    const dy = cy - curCy;

    if (direction === "right" && dx <= 6) continue;
    if (direction === "left" && dx >= -6) continue;
    if (direction === "down" && dy <= 6) continue;
    if (direction === "up" && dy >= -6) continue;

    const primaryDist = direction === "left" || direction === "right" ? Math.abs(dx) : Math.abs(dy);
    const orthoDist = direction === "left" || direction === "right" ? Math.abs(dy) : Math.abs(dx);
    const score = primaryDist + orthoDist * 2.6;

    if (score < bestScore) {
      bestScore = score;
      bestCandidate = el;
    }
  }

  if (bestCandidate) {
    bestCandidate.focus({ preventScroll: false });
    bestCandidate.scrollIntoView({ block: "nearest", inline: "nearest", behavior: "smooth" });
  }
}

// ============================================================================
// BOOTSTRAP APPLICATION
// ============================================================================

document.addEventListener("DOMContentLoaded", () => {
  renderApp();
  setupSpatialDpadNavigation();
  fetchLiveCatalog(true);
});
