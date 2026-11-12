/**
 * Rasagulla Cinema & TV — Unified Web, Mobile & Smart TV 10-Foot Engine
 */

const POSTERS = {
  biggBoss: "https://www.1tamilcrow.net/wp-content/uploads/2026/09/biggboss10-420x237.jpg",
  biggBossLive:
    "https://www.1tamilcrow.net/wp-content/uploads/2026/09/bigg-boss-season-10-live-2026-768x432-1-420x237.jpg",
  masterchef: "https://www.tamildhool.tech/wp-content/uploads/2026/10/chef.jpg",
  cooku: "https://www.tamildhool.tech/wp-content/uploads/2026/04/cwc7.jpg",
  superSinger: "https://www.tamildhool.tech/wp-content/uploads/2026/05/ssj11.jpg",
  neeyaNaana: "https://www.tamildhool.tech/wp-content/uploads/2025/07/nn.jpg",
};

const LIVE_STREAMS = {
  cam1: "https://newcdn.tamils.click/live1channel/index.m3u8",
  cam2: "https://newcdn.tamils.click/live2channel/index.m3u8",
  web1: "https://player4.namtamil.com/videos/abbd4fae-f28d-41c6-8ff0-1942faf3aec2",
  web2: "https://player4.spirituallifewell.com/videos/97e0c6a0-7edf-4050-81cc-d5b22203687a",
};

const CATALOG_SHOWS = [
  {
    id: "bigg_boss_tamil",
    title: "Bigg Boss 10",
    url: "https://www.1tamilcrow.net/category/bigg-boss-tamil/",
    thumbnailUrl: POSTERS.biggBoss,
    channel: "Vijay TV",
    badge: "24/7 LIVE • S10",
    description:
      "India's biggest Tamil reality television phenomenon with 24/7 uncut house feeds and daily 1080p episodes.",
    seasons: [
      { id: "s10", label: "Season 10 (2026)", url: "https://www.1tamilcrow.net/category/bigg-boss-tamil/", year: "2026" },
      { id: "s10_td", label: "Season 10 (Mirror)", url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/bigg-boss-tamil-season-10/", year: "2026" },
      { id: "s9", label: "Season 9", url: "https://www.1tamilcrow.net/category/bigg-boss-tamil-season-9/", year: "2025" },
      { id: "s8", label: "Season 8", url: "https://www.1tamilcrow.net/category/bigg-boss-season-8/", year: "2024" },
      { id: "s7", label: "Season 7", url: "https://www.1tamilcrow.net/category/bigg-boss-tamil-7-live/", year: "2023" },
      { id: "common_man", label: "The Common Man", url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/bigg-boss-the-common-man/", year: "2026" },
    ],
  },
  {
    id: "celebrity_masterchef",
    title: "Celebrity MasterChef Tamil",
    url: "https://www.tamildhool.tech/sony-vizha/sony-vizha-show/celebrity-master-chef-tamil/",
    thumbnailUrl: POSTERS.masterchef,
    channel: "Sony Vizha",
    badge: "NEW 2026",
    description:
      "Star-studded culinary showdowns as Tamil celebrities compete in high-pressure MasterChef kitchen challenges.",
    seasons: [
      { id: "cmc_s1", label: "Season 1 (2026)", url: "https://www.tamildhool.tech/sony-vizha/sony-vizha-show/celebrity-master-chef-tamil/", year: "2026" },
    ],
  },
  {
    id: "cooku_with_comali",
    title: "Cooku with Comali",
    url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/cooku-with-comali-s7/",
    thumbnailUrl: POSTERS.cooku,
    channel: "Vijay TV",
    badge: "ALL SEASONS S1–S7",
    description:
      "The ultimate comedy-cooking blockbuster pairing skilled celebrity chefs with hilarious Comalis across all 7 seasons.",
    seasons: [
      { id: "cwc_s7", label: "Season 7 (2026)", url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/cooku-with-comali-s7/", year: "2026" },
      { id: "cwc_s7_alt", label: "Season 7 (Archive)", url: "https://www.1tamilcrow.net/category/cooku-with-comali-season-7/", year: "2026" },
      { id: "cwc_s6", label: "Season 6", url: "https://www.1tamilcrow.net/category/vijay-tv-shows/cooku-with-comali/", year: "2025" },
      { id: "cwc_s5", label: "Season 5", url: "https://www.1tamilcrow.net/category/season-5-cooku-with-comali/", year: "2024" },
      { id: "cwc_s4", label: "Season 4", url: "https://www.1tamilcrow.net/category/cooku-with-comali-season-4/", year: "2023" },
      { id: "cwc_s3", label: "Season 3", url: "https://www.1tamilcrow.net/category/cooku-with-comali-2/", year: "2022" },
      { id: "cwc_s2", label: "Season 2", url: "https://www.1tamilcrow.net/category/cooku-with-comali-2/page/4/", year: "2021" },
      { id: "cwc_s1", label: "Season 1", url: "https://www.1tamilcrow.net/category/cooku-with-comali-2/page/8/", year: "2020" },
    ],
  },
  {
    id: "super_singer",
    title: "Super Singer",
    url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/super-singer-junior/",
    thumbnailUrl: POSTERS.superSinger,
    channel: "Vijay TV",
    badge: "ALL SEASONS",
    description:
      "Tamil Nadu's premier musical talent search featuring Super Singer Junior and Senior seasons with legendary judges.",
    seasons: [
      { id: "ssj_11", label: "Junior S11 (2026)", url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/super-singer-junior/", year: "2026" },
      { id: "ss_10", label: "Senior Season 10", url: "https://www.1tamilcrow.net/category/super-singer-10/", year: "2025" },
      { id: "ssj_10", label: "Junior Season 10", url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/super-singer-junior/page/3/", year: "2025" },
      { id: "ss_9", label: "Senior Season 9", url: "https://www.1tamilcrow.net/category/super-singer-10/page/2/", year: "2024" },
      { id: "ss_8", label: "Senior Season 8", url: "https://www.1tamilcrow.net/category/vijay-tv-shows/super-singer-8/", year: "2023" },
      { id: "ss_7", label: "Senior Season 7", url: "https://www.1tamilcrow.net/category/vijay-tv-shows/super-singer-7/", year: "2022" },
    ],
  },
  {
    id: "neeya_naana",
    title: "Neeya Naana",
    url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/neeya-naana/",
    thumbnailUrl: POSTERS.neeyaNaana,
    channel: "Vijay TV",
    badge: "WEEKLY DEBATE",
    description:
      "Iconic Tamil talk show hosted by Gopinath bringing two contrasting perspectives of society face-to-face.",
    seasons: [
      { id: "nn_2026", label: "2026 Episodes", url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/neeya-naana/", year: "2026" },
      { id: "nn_2025", label: "2025 Collection", url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/neeya-naana/page/3/", year: "2025" },
      { id: "nn_classic", label: "Classic Archive", url: "https://www.1tamilcrow.net/category/neeya-naana-2/", year: "2024" },
      { id: "nn_vault", label: "Vault Editions", url: "https://www.1tamilcrow.net/category/vijay-tv-shows/neeya-naana/", year: "2023" },
    ],
  },
];

// 7-Character Group Picture Cutout Nodes (Exact Match to ClusterPortalNav.kt)
const CLUSTER_RING_NODES = [
  {
    id: "dynamic_celebrity_masterchef",
    displayTitle: "Celebrity MasterChef",
    characterName: "Samantha • MasterChef Tamil",
    subtitleBadge: "SONY VIZHA • 1080P HD",
    cutoutUrl: "/assets/cutouts/img_cutout_samantha.png",
    normX: 0.20,
    zIndex: 4,
    glowPrimary: "rgba(255, 143, 0, 0.34)",
    glowRim: "#FFCC80",
    associatedShowId: "celebrity_masterchef",
    actionType: "show",
  },
  {
    id: "dynamic_neeya_naana",
    displayTitle: "Neeya Naana",
    characterName: "Gopinath • Neeya Naana",
    subtitleBadge: "VIJAY TV • WEEKLY DEBATE",
    cutoutUrl: "/assets/cutouts/img_cutout_gopinath.png",
    normX: 0.30,
    zIndex: 2,
    glowPrimary: "rgba(66, 165, 245, 0.34)",
    glowRim: "#90CAF9",
    associatedShowId: "neeya_naana",
    actionType: "show",
  },
  {
    id: "anchor_vijay",
    displayTitle: "Tamil Movies",
    characterName: "Thalapathy Vijay",
    subtitleBadge: "TAMIL CINEMA • 1080P HD",
    cutoutUrl: "/assets/cutouts/img_cutout_vijay.png",
    normX: 0.40,
    zIndex: 6,
    glowPrimary: "rgba(255, 179, 0, 0.35)",
    glowRim: "#FFD54F",
    associatedShowId: null,
    actionType: "movies",
  },
  {
    id: "anchor_biggboss",
    displayTitle: "Bigg Boss 10",
    characterName: "Kamal Haasan",
    subtitleBadge: "VIJAY TV • 24/7 UNCUT & EPISODES",
    cutoutUrl: "/assets/cutouts/img_cutout_kamal.png",
    normX: 0.50,
    zIndex: 5,
    glowPrimary: "rgba(171, 71, 188, 0.38)",
    glowRim: "#E040FB",
    associatedShowId: "bigg_boss_tamil",
    actionType: "show",
  },
  {
    id: "anchor_bruno",
    displayTitle: "Vijay TV Hits",
    characterName: "Vijay Sethupathi",
    subtitleBadge: "STAR VIJAY • ALL SEASONS",
    cutoutUrl: "/assets/cutouts/img_cutout_vijaysethupathi.png",
    normX: 0.60,
    zIndex: 6,
    glowPrimary: "rgba(0, 230, 118, 0.32)",
    glowRim: "#69F0AE",
    associatedShowId: "cooku_with_comali",
    actionType: "show",
  },
  {
    id: "dynamic_super_singer",
    displayTitle: "Super Singer",
    characterName: "Anirudh • Super Singer",
    subtitleBadge: "VIJAY TV • ALL SEASONS",
    cutoutUrl: "/assets/cutouts/img_cutout_anirudh.png",
    normX: 0.70,
    zIndex: 3,
    glowPrimary: "rgba(66, 165, 245, 0.34)",
    glowRim: "#90CAF9",
    associatedShowId: "super_singer",
    actionType: "show",
  },
  {
    id: "dynamic_cooku_with_comali",
    displayTitle: "Cooku with Comali",
    characterName: "Pugazh & Chef Damu",
    subtitleBadge: "VIJAY TV • ALL 7 SEASONS",
    cutoutUrl: "/assets/cutouts/img_cutout_chef.png",
    normX: 0.80,
    zIndex: 4,
    glowPrimary: "rgba(255, 143, 0, 0.34)",
    glowRim: "#FFCC80",
    associatedShowId: "cooku_with_comali",
    actionType: "show",
  },
];

const INITIAL_EPISODES_BY_SHOW = {
  bigg_boss_tamil: [
    {
      title: "🔴 Watch Live Stream: Bigg Boss Tamil Season 10 24/7",
      url: "https://www.1tamilcrow.net/watch-live-stream-bigg-boss-tamil-season-10/",
      dateStr: "24/7 LIVE STREAM",
      thumbnailUrl: POSTERS.biggBossLive,
      isLiveStream: true,
      season: "10",
      qualityBadge: "24/7 LIVE HD",
      showTitle: "Bigg Boss 10",
      description: "24/7 Uninterrupted HD Live Stream from the Bigg Boss House.",
    },
    {
      title: "Bigg Boss 10 Tamil 25-10-2026 | Episode 50 : Day 49 – Vijay TV Show",
      url: "https://www.1tamilcrow.net/bigg-boss-10-tamil-25-10-2026-episode-50-day-49/",
      dateStr: "25-10-2026",
      thumbnailUrl: POSTERS.biggBoss,
      isLiveStream: false,
      season: "10",
      qualityBadge: "1080p HD",
      showTitle: "Bigg Boss 10",
      description: "Episode 50 : Day 49. Weekend eliminations, host confrontation, and housemate revelations.",
    },
    {
      title: "Bigg Boss 10 Tamil 24-10-2026 | Episode 49 : Day 48 – Vijay TV Show",
      url: "https://www.1tamilcrow.net/bigg-boss-10-tamil-24-10-2026-episode-49-day-48/",
      dateStr: "24-10-2026",
      thumbnailUrl: POSTERS.biggBoss,
      isLiveStream: false,
      season: "10",
      qualityBadge: "1080p HD",
      showTitle: "Bigg Boss 10",
      description: "Episode 49 : Day 48. Weekend stage showdown and weekly review.",
    },
    {
      title: "Bigg Boss 10 Tamil 23-10-2026 | Episode 48 : Day 47 – Vijay TV Show",
      url: "https://www.1tamilcrow.net/bigg-boss-10-tamil-23-10-2026-episode-48-day-47/",
      dateStr: "23-10-2026",
      thumbnailUrl: POSTERS.biggBoss,
      isLiveStream: false,
      season: "10",
      qualityBadge: "1080p HD",
      showTitle: "Bigg Boss 10",
      description: "Episode 48 : Day 47. Final luxury budget task results and jail nominations.",
    },
    {
      title: "Bigg Boss 10 Tamil 22-10-2026 | Episode 47 : Day 46 – Vijay TV Show",
      url: "https://www.1tamilcrow.net/bigg-boss-10-tamil-22-10-2026-episode-47-day-46/",
      dateStr: "22-10-2026",
      thumbnailUrl: POSTERS.biggBoss,
      isLiveStream: false,
      season: "10",
      qualityBadge: "1080p HD",
      showTitle: "Bigg Boss 10",
      description: "Episode 47 : Day 46. Intense arena competition inside the Bigg Boss house.",
    },
  ],
  celebrity_masterchef: [
    {
      title: "Celebrity Master Chef Tamil | 23-10-2026 | Sony Vizha Show",
      url: "https://www.tamildhool.tech/sony-vizha/sony-vizha-show/celebrity-master-chef-tamil/celebrity-master-chef-tamil-23-10-2026-sony-vizha-show/",
      dateStr: "23-10-2026",
      thumbnailUrl: POSTERS.masterchef,
      season: "1",
      qualityBadge: "1080p HD",
      showTitle: "Celebrity MasterChef Tamil",
      description: "Celebrity MasterChef Tamil mystery box & elimination pressure test on Sony Vizha.",
    },
    {
      title: "Celebrity Master Chef Tamil | 22-10-2026 | Sony Vizha Show",
      url: "https://www.tamildhool.tech/sony-vizha/sony-vizha-show/celebrity-master-chef-tamil/celebrity-master-chef-tamil-22-10-2026-sony-vizha-show/",
      dateStr: "22-10-2026",
      thumbnailUrl: POSTERS.masterchef,
      season: "1",
      qualityBadge: "1080p HD",
      showTitle: "Celebrity MasterChef Tamil",
      description: "Team relay challenge and signature dish presentation in the MasterChef kitchen.",
    },
    {
      title: "Celebrity Master Chef Tamil | 21-10-2026 | Sony Vizha Show",
      url: "https://www.tamildhool.tech/sony-vizha/sony-vizha-show/celebrity-master-chef-tamil/celebrity-master-chef-tamil-21-10-2026-sony-vizha-show/",
      dateStr: "21-10-2026",
      thumbnailUrl: POSTERS.masterchef,
      season: "1",
      qualityBadge: "1080p HD",
      showTitle: "Celebrity MasterChef Tamil",
      description: "Immunity pin contest with gourmet regional Tamil fusion cuisine.",
    },
    {
      title: "Celebrity Master Chef Tamil | 20-10-2026 | Sony Vizha Show",
      url: "https://www.tamildhool.tech/sony-vizha/sony-vizha-show/celebrity-master-chef-tamil/celebrity-master-chef-tamil-20-10-2026-sony-vizha-show/",
      dateStr: "20-10-2026",
      thumbnailUrl: POSTERS.masterchef,
      season: "1",
      qualityBadge: "1080p HD",
      showTitle: "Celebrity MasterChef Tamil",
      description: "High-stakes invention test judged by the MasterChef culinary panel.",
    },
  ],
  cooku_with_comali: [
    {
      title: "Cooku with Comali S7 | Non Stop Laughs | 30-08-2026 Vijay Tv Show",
      url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/cooku-with-comali-s7/cooku-with-comali-s7-non-stop-laughs-30-08-2026-vijay-tv-show/",
      dateStr: "30-08-2026",
      thumbnailUrl: POSTERS.cooku,
      season: "7",
      qualityBadge: "1080p HD",
      showTitle: "Cooku with Comali",
      description: "Non-stop comedy specials and kitchen chaos with the Season 7 Cooks and Comalis.",
    },
    {
      title: "Cooku with Comali S7 | Laughter Horror Awards | 30-08-2026 Vijay Tv Show",
      url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/cooku-with-comali-s7/cooku-with-comali-s7-30-08-2026-vijay-tv-show/",
      dateStr: "30-08-2026",
      thumbnailUrl: POSTERS.cooku,
      season: "7",
      qualityBadge: "1080p HD",
      showTitle: "Cooku with Comali",
      description: "Special celebration episode featuring the Laughter Horror Awards.",
    },
    {
      title: "Cooku with Comali S7 | Grand Finale Showdown | 30-08-2026 Vijay Tv Show",
      url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/cooku-with-comali-s7/cooku-with-comali-s7-grand-finale-30-08-2026-vijay-tv-show/",
      dateStr: "30-08-2026",
      thumbnailUrl: POSTERS.cooku,
      season: "7",
      qualityBadge: "1080p HD",
      showTitle: "Cooku with Comali",
      description: "The epic Grand Finale showdown of Cooku with Comali Season 7.",
    },
    {
      title: "Cooku with Comali S7 | 29-08-2026 | Vijay Tv Show",
      url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/cooku-with-comali-s7/cooku-with-comali-s7-29-08-2026-vijay-tv-show/",
      dateStr: "29-08-2026",
      thumbnailUrl: POSTERS.cooku,
      season: "7",
      qualityBadge: "1080p HD",
      showTitle: "Cooku with Comali",
      description: "Pre-finale wildcard and celebration round on Vijay TV.",
    },
  ],
  super_singer: [
    {
      title: "Super Singer Junior | 25-10-2026 | Vijay Tv Show",
      url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/super-singer-junior/super-singer-junior-25-10-2026-vijay-tv-show/",
      dateStr: "25-10-2026",
      thumbnailUrl: POSTERS.superSinger,
      season: "11",
      qualityBadge: "1080p HD",
      showTitle: "Super Singer",
      description: "Sunday evening classical and melody round performances by the young prodigies.",
    },
    {
      title: "Super Singer Junior | 24-10-2026 | Vijay Tv Show",
      url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/super-singer-junior/super-singer-junior-24-10-2026-vijay-tv-show/",
      dateStr: "24-10-2026",
      thumbnailUrl: POSTERS.superSinger,
      season: "11",
      qualityBadge: "1080p HD",
      showTitle: "Super Singer",
      description: "Saturday stage performances with special celebrity guests and live orchestra.",
    },
    {
      title: "Super Singer Junior | 18-10-2026 | Vijay Tv Show",
      url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/super-singer-junior/super-singer-junior-18-10-2026-vijay-tv-show/",
      dateStr: "18-10-2026",
      thumbnailUrl: POSTERS.superSinger,
      season: "11",
      qualityBadge: "1080p HD",
      showTitle: "Super Singer",
      description: "Duets and tribute round on Super Singer Junior.",
    },
    {
      title: "Super Singer Junior | 17-10-2026 | Vijay Tv Show",
      url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/super-singer-junior/super-singer-junior-17-10-2026-vijay-tv-show/",
      dateStr: "17-10-2026",
      thumbnailUrl: POSTERS.superSinger,
      season: "11",
      qualityBadge: "1080p HD",
      showTitle: "Super Singer",
      description: "Folk and high-energy chartbusters round.",
    },
  ],
  neeya_naana: [
    {
      title: "Neeya Naana | 25-10-2026 | Vijay Tv Show",
      url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/neeya-naana/neeya-naana-25-10-2026-vijay-tv-show/",
      dateStr: "25-10-2026",
      thumbnailUrl: POSTERS.neeyaNaana,
      season: "2026",
      qualityBadge: "1080p HD",
      showTitle: "Neeya Naana",
      description: "Gopinath moderates a thought-provoking Sunday debate on contemporary Tamil family dynamics.",
    },
    {
      title: "Neeya Naana | 18-10-2026 | Vijay Tv Show",
      url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/neeya-naana/neeya-naana-18-10-2026-vijay-tv-show/",
      dateStr: "18-10-2026",
      thumbnailUrl: POSTERS.neeyaNaana,
      season: "2026",
      qualityBadge: "1080p HD",
      showTitle: "Neeya Naana",
      description: "Heated perspectives and emotional stories in this week's Neeya Naana forum.",
    },
    {
      title: "Neeya Naana | 11-10-2026 | Vijay Tv Show",
      url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/neeya-naana/neeya-naana-11-10-2026-vijay-tv-show/",
      dateStr: "11-10-2026",
      thumbnailUrl: POSTERS.neeyaNaana,
      season: "2026",
      qualityBadge: "1080p HD",
      showTitle: "Neeya Naana",
      description: "Generational perspectives clash with humor and insight.",
    },
    {
      title: "Neeya Naana | 04-10-2026 | Vijay Tv Show",
      url: "https://www.tamildhool.tech/vijay-tv/vijay-tv-show/neeya-naana/neeya-naana-04-10-2026-vijay-tv-show/",
      dateStr: "04-10-2026",
      thumbnailUrl: POSTERS.neeyaNaana,
      season: "2026",
      qualityBadge: "1080p HD",
      showTitle: "Neeya Naana",
      description: "Special edition debate on modern lifestyle vs traditional values.",
    },
  ],
};

const INITIAL_MOVIES = [
  {
    title: "Cocoons (2026) HDRip [Tamil]",
    url: "https://www.5movierulz.green/cocoons-2026-hdrip-tamil-7647.html",
    dateStr: "2026 • HDRip",
    thumbnailUrl: "https://www.5movierulz.green/uploads/Cocoons-Tamil.jpg",
    season: "2026",
    description: "Tamil Featured Movie (2026) in 1080p HDRip with DD+5.1 & AAC audio.",
    qualityBadge: "HDRip HD",
    showTitle: "Tamil Movies",
    isMovie: true,
  },
  {
    title: "Chola Kollai Bommai (2026) HDRip [Tamil]",
    url: "https://www.5movierulz.green/chola-kollai-bommai-2026-hdrip-tamil-7646.html",
    dateStr: "2026 • HDRip",
    thumbnailUrl: "https://www.5movierulz.green/uploads/Chola-Kollai-Bommai-Tamil.jpg",
    season: "2026",
    description: "Tamil Featured Movie (2026) in full high-definition HDRip stream.",
    qualityBadge: "HDRip HD",
    showTitle: "Tamil Movies",
    isMovie: true,
  },
  {
    title: "It’s A Medical Miracle (2026) HDRip [Tamil]",
    url: "https://www.5movierulz.green/its-a-medical-miracle-2026-hdrip-tamil-7645.html",
    dateStr: "2026 • HDRip",
    thumbnailUrl: "https://www.5movierulz.green/uploads/Its-A-Medical-Miracle-Tamil.jpg",
    season: "2026",
    description: "Gripping Tamil medical drama feature film in 1080p HDRip.",
    qualityBadge: "HDRip HD",
    showTitle: "Tamil Movies",
    isMovie: true,
  },
  {
    title: "Baththa (2026) HDRip [Tamil]",
    url: "https://www.5movierulz.green/baththa-2026-hdrip-tamil-7644.html",
    dateStr: "2026 • HDRip",
    thumbnailUrl: "https://www.5movierulz.green/uploads/Baththa-Tamil.jpg",
    season: "2026",
    description: "Action-packed 2026 Tamil featured release streaming in HDRip.",
    qualityBadge: "HDRip HD",
    showTitle: "Tamil Movies",
    isMovie: true,
  },
  {
    title: "Dorothy (2026) HDRip [Tamil]",
    url: "https://www.5movierulz.green/dorothy-2026-hdrip-tamil-7637.html",
    dateStr: "2026 • HDRip",
    thumbnailUrl: "https://www.5movierulz.green/uploads/Dorothy-Tamil.jpg",
    season: "2026",
    description: "Mystery thriller Tamil feature film streaming in full HDRip quality.",
    qualityBadge: "HDRip HD",
    showTitle: "Tamil Movies",
    isMovie: true,
  },
  {
    title: "The Paradise (2026) HDRip [Tamil]",
    url: "https://www.5movierulz.green/the-paradise-2026-hdrip-tamil-7627.html",
    dateStr: "2026 • HDRip",
    thumbnailUrl: "https://www.5movierulz.green/uploads/The-Paradise-Tamil.jpg",
    season: "2026",
    description: "Featured Tamil cinema release in 1080p HDRip.",
    qualityBadge: "HDRip HD",
    showTitle: "Tamil Movies",
    isMovie: true,
  },
  {
    title: "Vettaiyan (2025) BluRay 1080p [Tamil]",
    url: "https://www.5movierulz.green/category/tamil-movies-2025#vettaiyan",
    dateStr: "2025 • BluRay 1080p",
    thumbnailUrl: "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=800&q=80",
    season: "2025",
    description: "High-octane Tamil action drama streaming in 1080p BluRay.",
    qualityBadge: "BluRay 1080p",
    showTitle: "Tamil Movies",
    isMovie: true,
  },
  {
    title: "Leo: Uncut IMAX Edition [Tamil]",
    url: "https://www.5movierulz.green/category/tamil-featured#leo",
    dateStr: "IMAX 1080p",
    thumbnailUrl: "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=800&q=80",
    season: "2025",
    description: "Lokesh Kanagaraj & Thalapathy Vijay action blockbuster in 1080p.",
    qualityBadge: "1080p HD",
    showTitle: "Tamil Movies",
    isMovie: true,
  },
];

const SMART_CALENDAR = [
  {
    id: "cal_bb10",
    showTitle: "Bigg Boss Tamil Season 10",
    channel: "Vijay TV",
    telecastTime: "21:30 IST • Daily",
    cleanTag: "Bigg Boss Season 10 • Daily Telecast • Vijay TV • 1080p HD",
    thumbnailUrl: POSTERS.biggBoss,
    episodeUrl: "https://www.1tamilcrow.net/watch-live-stream-bigg-boss-tamil-season-10/",
    activeMirrorsCount: 4,
  },
  {
    id: "cal_cmc",
    showTitle: "Celebrity MasterChef Tamil",
    channel: "Sony Vizha",
    telecastTime: "20:00 IST • Mon–Fri",
    cleanTag: "Celebrity MasterChef Tamil • Kitchen Showdown • Sony Vizha • HD",
    thumbnailUrl: POSTERS.masterchef,
    episodeUrl: INITIAL_EPISODES_BY_SHOW.celebrity_masterchef[0].url,
    activeMirrorsCount: 4,
  },
  {
    id: "cal_cwc",
    showTitle: "Cooku with Comali S7",
    channel: "Vijay TV",
    telecastTime: "20:00 IST • Sat & Sun",
    cleanTag: "Cooku with Comali S7 • Weekend Special • Vijay TV • HD",
    thumbnailUrl: POSTERS.cooku,
    episodeUrl: INITIAL_EPISODES_BY_SHOW.cooku_with_comali[0].url,
    activeMirrorsCount: 4,
  },
  {
    id: "cal_ssj",
    showTitle: "Super Singer Junior S11",
    channel: "Vijay TV",
    telecastTime: "18:30 IST • Sat & Sun",
    cleanTag: "Super Singer Junior S11 • Live Stage • Vijay TV • HD",
    thumbnailUrl: POSTERS.superSinger,
    episodeUrl: INITIAL_EPISODES_BY_SHOW.super_singer[0].url,
    activeMirrorsCount: 3,
  },
  {
    id: "cal_nn",
    showTitle: "Neeya Naana",
    channel: "Vijay TV",
    telecastTime: "15:00 IST • Sunday",
    cleanTag: "Neeya Naana • Sunday Debate Forum • Vijay TV • HD",
    thumbnailUrl: POSTERS.neeyaNaana,
    episodeUrl: INITIAL_EPISODES_BY_SHOW.neeya_naana[0].url,
    activeMirrorsCount: 3,
  },
];

// Global Reactive State
const state = {
  currentTab: "Featured",
  selectedShow: CATALOG_SHOWS[0],
  selectedSeason: CATALOG_SHOWS[0].seasons[0],
  showCarousels: { ...INITIAL_EPISODES_BY_SHOW },
  currentEpisodes: [...INITIAL_EPISODES_BY_SHOW.bigg_boss_tamil],
  movies: [...INITIAL_MOVIES],
  selectedMovieCategory: "https://www.5movierulz.green/category/tamil-featured",
  clusterFocusedIdx: 3, // Starts on Kamal Haasan (Bigg Boss 10) at center
  isClusterShelfOpen: false,
  searchOpen: false,
  searchQuery: "",
  episodeFilterQuery: "",
  watchHistory: [],
  myList: [],
  toastMsg: null,
  easterEggCount: 0,
  easterEggLastTap: 0,
  showEasterEggModal: false,
  // Video Player State
  player: {
    active: false,
    episode: null,
    sources: [],
    selectedSourceIdx: 0,
    loadingSources: false,
    useProxy: false,
    hudVisible: true,
    isPlaying: true,
    currentTime: 0,
    duration: 0,
    selectedQuality: "1080p", // Defaults to high res (1080p) per user requirement
    availableQualities: ["Auto", "1080p", "720p", "480p"],
    showExitConfirm: false,
    lastFocusedSelectorBeforePlayer: null,
  },
};

let hlsInstance = null;
let hudTimer = null;
let progressSaveInterval = null;

/**
 * Resolves the exact episode, movie, or show thumbnail for any URL or title so Continue Watching
 * NEVER displays the wrong fallback thumbnail (exact port of MainViewModel.resolveAccurateThumbnail).
 */
function resolveAccurateThumbnail(episodeUrl = "", episodeTitle = "", providedThumb = "") {
  const allKnown = [
    ...state.currentEpisodes,
    ...state.movies,
    ...Object.values(state.showCarousels).flat(),
    ...INITIAL_MOVIES,
  ];

  const exactUrl = allKnown.find(
    (e) => e.url.toLowerCase() === episodeUrl.toLowerCase() && e.thumbnailUrl
  );
  if (exactUrl) return exactUrl.thumbnailUrl;

  if (episodeTitle) {
    const exactTitle = allKnown.find(
      (e) => e.title.toLowerCase() === episodeTitle.toLowerCase() && e.thumbnailUrl
    );
    if (exactTitle) return exactTitle.thumbnailUrl;
  }

  const combined = `${episodeTitle} ${episodeUrl}`.toLowerCase();
  if (combined.includes("masterchef") || combined.includes("master-chef")) return POSTERS.masterchef;
  if (combined.includes("cooku") || combined.includes("comali")) return POSTERS.cooku;
  if (combined.includes("super-singer") || combined.includes("super singer")) return POSTERS.superSinger;
  if (combined.includes("neeya-naana") || combined.includes("neeya naana")) return POSTERS.neeyaNaana;
  if (combined.includes("bigg-boss") || combined.includes("bigg boss")) return POSTERS.biggBoss;

  if (episodeTitle) {
    const cleanPrefix = episodeTitle.split("(")[0].trim().toLowerCase();
    if (cleanPrefix.length >= 4) {
      const partial = allKnown.find(
        (e) => e.title.toLowerCase().includes(cleanPrefix) && e.thumbnailUrl
      );
      if (partial) return partial.thumbnailUrl;
    }
  }

  if (providedThumb && (providedThumb !== POSTERS.biggBoss || combined.includes("bigg"))) {
    return providedThumb;
  }
  return providedThumb || POSTERS.biggBoss;
}

function loadPersistedStorage() {
  try {
    const rawHist = localStorage.getItem("rasagulla_watch_hist");
    if (rawHist) {
      const parsed = JSON.parse(rawHist);
      state.watchHistory = parsed.map((item) => ({
        ...item,
        thumbnailUrl: resolveAccurateThumbnail(item.episodeUrl, item.episodeTitle, item.thumbnailUrl),
      }));
    }
  } catch {}

  try {
    const rawList = localStorage.getItem("rasagulla_my_list");
    if (rawList) {
      state.myList = JSON.parse(rawList);
    }
  } catch {}
}

function saveWatchHistory() {
  try {
    localStorage.setItem("rasagulla_watch_hist", JSON.stringify(state.watchHistory.slice(0, 40)));
  } catch {}
}

function saveMyList() {
  try {
    localStorage.setItem("rasagulla_my_list", JSON.stringify(state.myList));
  } catch {}
}

function showToast(msg) {
  state.toastMsg = msg;
  renderToast();
  setTimeout(() => {
    if (state.toastMsg === msg) {
      state.toastMsg = null;
      renderToast();
    }
  }, 3400);
}

function renderToast() {
  const el = document.getElementById("toast-container");
  if (!el) return;
  if (!state.toastMsg) {
    el.innerHTML = "";
    return;
  }
  el.innerHTML = `<div class="toast-banner">${escapeHtml(state.toastMsg)}</div>`;
}

function escapeHtml(str = "") {
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

function formatSeconds(sec = 0) {
  if (!isFinite(sec) || sec < 0) return "0:00";
  const s = Math.floor(sec);
  const hrs = Math.floor(s / 3600);
  const mins = Math.floor((s % 3600) / 60);
  const rem = s % 60;
  if (hrs > 0) {
    return `${hrs}:${String(mins).padStart(2, "0")}:${String(rem).padStart(2, "0")}`;
  }
  return `${mins}:${String(rem).padStart(2, "0")}`;
}

// Background Catalog Fetching from Vercel Serverless Function (/api/catalog)
async function fetchEpisodesForShow(url, showId = null) {
  try {
    const resp = await fetch(`/api/catalog?url=${encodeURIComponent(url)}`);
    if (!resp.ok) return;
    const data = await resp.json();
    if (data.ok && Array.isArray(data.items) && data.items.length > 0) {
      if (showId) {
        state.showCarousels[showId] = data.items;
      }
      if (url === (state.selectedSeason?.url || state.selectedShow.url)) {
        state.currentEpisodes = data.items;
      }
      // Repair any watch history thumbnails
      state.watchHistory = state.watchHistory.map((item) => ({
        ...item,
        thumbnailUrl: resolveAccurateThumbnail(item.episodeUrl, item.episodeTitle, item.thumbnailUrl),
      }));
      saveWatchHistory();
      if (!state.player.active) renderApp();
    }
  } catch {}
}

async function fetchMoviesCatalog(categoryUrl = state.selectedMovieCategory) {
  try {
    const resp = await fetch(`/api/catalog?type=movies&url=${encodeURIComponent(categoryUrl)}`);
    if (!resp.ok) return;
    const data = await resp.json();
    if (data.ok && Array.isArray(data.items) && data.items.length > 0) {
      state.movies = data.items;
      if (!state.player.active) renderApp();
    }
  } catch {}
}

// Hero Cluster Navigation Actions
function stepClusterFocus(delta) {
  const len = CLUSTER_RING_NODES.length;
  state.clusterFocusedIdx = (state.clusterFocusedIdx + delta + len) % len;
  const activeNode = CLUSTER_RING_NODES[state.clusterFocusedIdx];
  if (activeNode.associatedShowId) {
    const matched = CATALOG_SHOWS.find((s) => s.id === activeNode.associatedShowId);
    if (matched) {
      state.selectedShow = matched;
      state.selectedSeason = matched.seasons[0] || null;
      state.currentEpisodes =
        state.showCarousels[matched.id] || INITIAL_EPISODES_BY_SHOW[matched.id] || [];
    }
  }
  renderApp();
  const btn = document.getElementById("cluster-explore-btn");
  if (btn) btn.focus();
}

function handleClusterPrimaryAction() {
  const activeNode = CLUSTER_RING_NODES[state.clusterFocusedIdx];
  if (activeNode.actionType === "movies") {
    state.currentTab = "Movies";
    renderApp();
    return;
  }
  state.isClusterShelfOpen = !state.isClusterShelfOpen;
  renderApp();
  setTimeout(() => {
    const firstShelfBtn = document.querySelector(".shelf-ep-item");
    if (state.isClusterShelfOpen && firstShelfBtn) {
      firstShelfBtn.focus();
    }
  }, 50);
}

// Watchlist & History
function toggleWatchlistByUrl(url, title, dateStr, thumb) {
  const idx = state.myList.findIndex((i) => i.episodeUrl === url);
  if (idx >= 0) {
    state.myList.splice(idx, 1);
    showToast("Removed from My List");
  } else {
    state.myList.unshift({
      episodeTitle: title,
      episodeUrl: url,
      dateStr,
      thumbnailUrl: resolveAccurateThumbnail(url, title, thumb),
      timestamp: Date.now(),
    });
    showToast("Added to My List");
  }
  saveMyList();
  renderApp();
}

function recordWatchHistory(episode) {
  const existing = state.watchHistory.find((i) => i.episodeUrl === episode.url);
  state.watchHistory = state.watchHistory.filter((i) => i.episodeUrl !== episode.url);
  const resolvedThumb = resolveAccurateThumbnail(
    episode.url,
    episode.title,
    episode.thumbnailUrl || existing?.thumbnailUrl || ""
  );
  state.watchHistory.unshift({
    episodeTitle: episode.title,
    episodeUrl: episode.url,
    dateStr: episode.dateStr || "HD",
    showTitle: episode.showTitle || state.selectedShow.title,
    thumbnailUrl: resolvedThumb,
    lastPositionSec: existing?.lastPositionSec || 0,
    durationSec: existing?.durationSec || 0,
    timestamp: Date.now(),
  });
  saveWatchHistory();
}

// Video Player Engine (hls.js + Native HLS + Multi-Mirror Failover + 1080p Default)
async function openVideoPlayer(episode) {
  const activeEl = document.activeElement;
  if (activeEl && activeEl.id) {
    state.player.lastFocusedSelectorBeforePlayer = `#${activeEl.id}`;
  }

  recordWatchHistory(episode);

  state.player.active = true;
  state.player.episode = episode;
  state.player.sources = [];
  state.player.selectedSourceIdx = 0;
  state.player.loadingSources = true;
  state.player.useProxy = false;
  state.player.hudVisible = true;
  state.player.showExitConfirm = false;
  state.player.selectedQuality = "1080p";
  renderApp();

  try {
    const resp = await fetch(`/api/stream?url=${encodeURIComponent(episode.url)}`);
    const data = await resp.json();
    if (data.ok && Array.isArray(data.sources) && data.sources.length > 0) {
      state.player.sources = data.sources;
    }
  } catch {}

  if (state.player.sources.length === 0) {
    const isBB = /bigg-boss/i.test(episode.url);
    state.player.sources = [
      {
        name: isBB ? "Live Cam 1 • 1080p 60fps HD" : "Server 1 • 1080p Full HD",
        url: isBB ? LIVE_STREAMS.cam1 : "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
        proxyUrl: `/api/proxy?url=${encodeURIComponent(
          isBB ? LIVE_STREAMS.cam1 : "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
        )}`,
        isEmbed: false,
        healthBadge: "Server 1: Fast • 1080p (19ms)",
        resolution: "1080p",
      },
      {
        name: "Live Cam 2 • High-Speed Feed",
        url: LIVE_STREAMS.cam2,
        proxyUrl: `/api/proxy?url=${encodeURIComponent(LIVE_STREAMS.cam2)}`,
        isEmbed: false,
        healthBadge: "Server 2: Backup • 1080p (24ms)",
        resolution: "1080p",
      },
    ];
  }

  state.player.loadingSources = false;
  renderApp();
  mountActiveStreamSource();
}

function mountActiveStreamSource() {
  if (hlsInstance) {
    hlsInstance.destroy();
    hlsInstance = null;
  }
  if (progressSaveInterval) {
    clearInterval(progressSaveInterval);
    progressSaveInterval = null;
  }

  const srcObj = state.player.sources[state.player.selectedSourceIdx];
  if (!srcObj) return;

  const stage = document.getElementById("player-video-stage");
  if (!stage) return;

  if (srcObj.isEmbed) {
    stage.innerHTML = `<iframe src="${escapeHtml(
      srcObj.url
    )}" allow="autoplay; fullscreen; encrypted-media" allowfullscreen></iframe>`;
    resetHudAutoHide();
    return;
  }

  stage.innerHTML = `<video id="rasagulla-video-el" playsinline autoplay crossorigin="anonymous"></video>`;
  const video = document.getElementById("rasagulla-video-el");
  if (!video) return;

  const streamUrl = state.player.useProxy ? srcObj.proxyUrl || srcObj.url : srcObj.url;
  const savedHist = state.watchHistory.find((i) => i.episodeUrl === state.player.episode?.url);
  const startPos = savedHist?.lastPositionSec > 5 ? savedHist.lastPositionSec : 0;

  if (window.Hls && window.Hls.isSupported() && /\.m3u8|\/api\/proxy/i.test(streamUrl)) {
    hlsInstance = new window.Hls({
      enableWorker: true,
      lowLatencyMode: true,
      startLevel: -1,
      capLevelToPlayerSize: false,
      maxBufferLength: 30,
    });
    hlsInstance.loadSource(streamUrl);
    hlsInstance.attachMedia(video);

    hlsInstance.on(window.Hls.Events.MANIFEST_PARSED, (_, data) => {
      // Lock/Default to 1080p highest resolution level on startup per user requirement
      if (Array.isArray(data.levels) && data.levels.length > 0) {
        let bestIdx = data.levels.length - 1;
        data.levels.forEach((lvl, idx) => {
          if (lvl.height >= 1080) bestIdx = idx;
        });
        if (state.player.selectedQuality === "1080p") {
          hlsInstance.currentLevel = bestIdx;
        }
      }
      if (startPos > 0 && !state.player.episode?.isLiveStream) {
        video.currentTime = startPos;
      }
      video.play().catch(() => {});
    });

    hlsInstance.on(window.Hls.Events.ERROR, (_, errData) => {
      if (errData.fatal) {
        handleStreamFailover();
      }
    });
  } else {
    video.src = streamUrl;
    video.addEventListener("loadedmetadata", () => {
      if (startPos > 0 && !state.player.episode?.isLiveStream) {
        video.currentTime = startPos;
      }
      video.play().catch(() => {});
    });
    video.addEventListener("error", () => {
      handleStreamFailover();
    });
  }

  video.addEventListener("timeupdate", () => {
    state.player.currentTime = video.currentTime || 0;
    state.player.duration = video.duration || 0;
    const seekEl = document.getElementById("player-seek-slider");
    const timeEl = document.getElementById("player-time-label");
    if (seekEl && isFinite(video.duration) && video.duration > 0) {
      seekEl.max = Math.floor(video.duration);
      seekEl.value = Math.floor(video.currentTime);
    }
    if (timeEl) {
      timeEl.textContent =
        isFinite(video.duration) && video.duration > 0
          ? `${formatSeconds(video.currentTime)} / ${formatSeconds(video.duration)}`
          : "LIVE STREAM • 1080p HD";
    }
  });

  video.addEventListener("play", () => {
    state.player.isPlaying = true;
    const playBtn = document.getElementById("player-play-toggle-btn");
    if (playBtn) playBtn.innerHTML = "⏸ Pause";
  });

  video.addEventListener("pause", () => {
    state.player.isPlaying = false;
    const playBtn = document.getElementById("player-play-toggle-btn");
    if (playBtn) playBtn.innerHTML = "▶ Play";
  });

  progressSaveInterval = setInterval(() => {
    if (video && video.currentTime > 3 && state.player.episode) {
      const hItem = state.watchHistory.find((i) => i.episodeUrl === state.player.episode.url);
      if (hItem) {
        hItem.lastPositionSec = Math.floor(video.currentTime);
        hItem.durationSec = isFinite(video.duration) ? Math.floor(video.duration) : 0;
        saveWatchHistory();
      }
    }
  }, 4000);

  resetHudAutoHide();
  setTimeout(() => {
    const playBtn = document.getElementById("player-play-toggle-btn");
    if (playBtn) playBtn.focus();
  }, 80);
}

function handleStreamFailover() {
  if (!state.player.useProxy) {
    state.player.useProxy = true;
    showToast("⚡ Switching to Ad-Free High-Speed Proxy Stream...");
    mountActiveStreamSource();
    return;
  }
  if (state.player.selectedSourceIdx + 1 < state.player.sources.length) {
    state.player.selectedSourceIdx += 1;
    state.player.useProxy = false;
    const nextSrc = state.player.sources[state.player.selectedSourceIdx];
    showToast(`⚡ Auto-Failover to ${nextSrc.name}`);
    renderApp();
    mountActiveStreamSource();
  }
}

function selectPlayerMirror(idx) {
  state.player.selectedSourceIdx = idx;
  state.player.useProxy = false;
  renderApp();
  mountActiveStreamSource();
}

function selectPlayerQuality(q) {
  state.player.selectedQuality = q;
  if (hlsInstance && Array.isArray(hlsInstance.levels) && hlsInstance.levels.length > 0) {
    if (q === "Auto") {
      hlsInstance.currentLevel = -1;
    } else {
      const targetH = parseInt(q, 10) || 1080;
      let chosen = hlsInstance.levels.length - 1;
      hlsInstance.levels.forEach((lvl, idx) => {
        if (lvl.height && Math.abs(lvl.height - targetH) <= 180) {
          chosen = idx;
        }
      });
      hlsInstance.currentLevel = chosen;
    }
  }
  showToast(`Resolution locked to ${q} HD`);
  renderApp();
  mountActiveStreamSource();
}

function togglePlayPause() {
  const video = document.getElementById("rasagulla-video-el");
  if (!video) return;
  if (video.paused) {
    video.play().catch(() => {});
  } else {
    video.pause();
  }
  resetHudAutoHide();
}

function seekRelative(deltaSec) {
  const video = document.getElementById("rasagulla-video-el");
  if (!video) return;
  video.currentTime = Math.max(0, (video.currentTime || 0) + deltaSec);
  resetHudAutoHide();
}

function resetHudAutoHide() {
  state.player.hudVisible = true;
  const hud = document.getElementById("player-hud-overlay");
  if (hud) hud.classList.remove("hud-hidden");
  if (hudTimer) clearTimeout(hudTimer);
  hudTimer = setTimeout(() => {
    if (state.player.active && state.player.isPlaying && !state.player.showExitConfirm) {
      state.player.hudVisible = false;
      const hudEl = document.getElementById("player-hud-overlay");
      if (hudEl) hudEl.classList.add("hud-hidden");
    }
  }, 5500);
}

function requestExitPlayer() {
  state.player.showExitConfirm = true;
  state.player.hudVisible = true;
  renderApp();
  setTimeout(() => {
    const confirmExitBtn = document.getElementById("confirm-exit-yes-btn");
    if (confirmExitBtn) confirmExitBtn.focus();
  }, 50);
}

function closeVideoPlayerCompletely() {
  if (hlsInstance) {
    hlsInstance.destroy();
    hlsInstance = null;
  }
  if (progressSaveInterval) {
    clearInterval(progressSaveInterval);
    progressSaveInterval = null;
  }
  state.player.active = false;
  state.player.showExitConfirm = false;
  renderApp();

  if (state.player.lastFocusedSelectorBeforePlayer) {
    setTimeout(() => {
      const prev = document.querySelector(state.player.lastFocusedSelectorBeforePlayer);
      if (prev) prev.focus();
    }, 60);
  }
}

// Rendering Engine
function renderApp() {
  const root = document.getElementById("app");
  if (!root) return;

  if (state.player.active) {
    root.innerHTML = renderVideoPlayerModal();
    bindPlayerDomEvents();
    return;
  }

  const listCount = state.myList.length;

  root.innerHTML = `
    <div class="app-shell">
      <!-- TV & Desktop Collapsible Left Rail -->
      <aside class="tv-left-rail">
        <div class="brand-header" data-focusable tabindex="0" id="rail-brand-logo">
          <img src="/assets/cutouts/img_rasagulla_icon.png" alt="Rasagulla Logo" class="brand-logo-img" />
          <div>
            <div class="brand-title">Rasagulla</div>
            <div class="brand-subtitle">Cinema & TV 4K</div>
          </div>
        </div>

        <nav class="rail-nav-list">
          ${[
            { id: "Featured", icon: "✨", label: "Home" },
            { id: "Episodes", icon: "📺", label: "TV Shows" },
            { id: "Movies", icon: "🎬", label: "Movies (4-Col)" },
            { id: "Live 24/7", icon: "🔴", label: "Live 24/7" },
            { id: "My List", icon: "🔖", label: "My List & Schedule", badge: listCount },
          ]
            .map(
              (tab, idx) => `
            <button
              class="rail-nav-btn ${state.currentTab === tab.id ? "active" : ""}"
              data-focusable
              id="rail-tab-${idx}"
              data-tab="${tab.id}"
            >
              <span>${tab.icon}</span>
              <span>${tab.label}</span>
              ${tab.badge ? `<span class="rail-nav-badge">${tab.badge}</span>` : ""}
            </button>
          `
            )
            .join("")}
        </nav>

        <div class="rail-footer-hint">
          🎮 <strong>TV Remote Ready</strong><br/>
          Use Arrow Keys / D-Pad to navigate, Enter/OK to play, Back/Esc to return.
        </div>
      </aside>

      <!-- Main Stage -->
      <main class="main-stage">
        <header class="sleek-top-bar">
          <div class="mobile-brand" id="mobile-brand-logo" data-focusable tabindex="0">
            <img src="/assets/cutouts/img_rasagulla_icon.png" alt="Rasagulla" class="brand-logo-img" />
            <div>
              <div class="brand-title">Rasagulla</div>
              <div class="brand-subtitle">TV & Web</div>
            </div>
          </div>

          <div class="top-bar-actions">
            <button class="pill-action-btn" data-focusable id="top-search-btn">
              🔍 ${state.searchOpen ? "Close Search" : "Search Shows & Movies"}
            </button>
            <button class="pill-action-btn" data-focusable id="top-refresh-btn">
              🔄 Refresh Feeds
            </button>
            <button class="pill-action-btn live-pill" data-focusable id="top-live-btn">
              <span class="live-dot"></span> 24/7 Live Feed
            </button>
          </div>
        </header>

        ${state.searchOpen ? renderSearchView() : renderActiveTabView()}
      </main>

      <!-- Mobile Bottom Navigation -->
      <nav class="mobile-bottom-nav">
        ${[
          { id: "Featured", icon: "🏠", label: "Home" },
          { id: "Episodes", icon: "📺", label: "Shows" },
          { id: "Movies", icon: "🎬", label: "Movies" },
          { id: "Live 24/7", icon: "🔴", label: "Live" },
          { id: "My List", icon: "🔖", label: "My List" },
        ]
          .map(
            (t) => `
          <button class="mobile-nav-btn ${state.currentTab === t.id ? "active" : ""}" data-tab="${t.id}">
            <span style="font-size:17px">${t.icon}</span>
            <span>${t.label}</span>
          </button>
        `
          )
          .join("")}
      </nav>
    </div>

    ${state.showEasterEggModal ? renderEasterEggModal() : ""}
  `;

  bindMainDomEvents();
}

function renderActiveTabView() {
  if (state.currentTab === "Episodes") return renderShowsAndSeasonsTab();
  if (state.currentTab === "Movies") return renderMoviesTab();
  if (state.currentTab === "Live 24/7") return renderLiveStudioTab();
  if (state.currentTab === "My List") return renderMyListAndScheduleTab();
  return renderFeaturedHomeTab();
}

function renderFeaturedHomeTab() {
  const activeNode = CLUSTER_RING_NODES[state.clusterFocusedIdx];
  const shelfEpisodes =
    activeNode.associatedShowId && state.showCarousels[activeNode.associatedShowId]
      ? state.showCarousels[activeNode.associatedShowId]
      : state.currentEpisodes;

  const hudLeftPercent = Math.min(72, Math.max(24, Math.round(activeNode.normX * 100)));

  return `
    <!-- 7-Character Group Picture Cutout Hero Carousel -->
    <section class="cluster-portal-stage" id="cluster-portal-stage">
      <div
        class="cluster-radial-glow"
        style="background: radial-gradient(circle at ${Math.round(
          activeNode.normX * 100
        )}% 48%, ${activeNode.glowPrimary} 0%, rgba(19, 38, 70, 0.25) 38%, #000000 76%);"
      ></div>

      <div class="cluster-top-badge">
        <span class="cluster-top-badge-title">Featured Spotlight</span>
        <span class="cluster-dpad-hint">◀ ▶ D-Pad / Arrows to Switch Character • Enter/OK to Open • ▼ Down for Shows</span>
      </div>

      ${
        !state.isClusterShelfOpen
          ? `
        <div class="cluster-hud-header" style="left: calc(${hudLeftPercent}% - 95px);">
          <div>
            <div class="cluster-hud-title">${escapeHtml(activeNode.displayTitle)}</div>
            <div class="cluster-hud-sub" style="color: ${activeNode.glowRim};">
              ${escapeHtml(activeNode.characterName)}
            </div>
          </div>
          <svg width="40" height="24" viewBox="0 0 40 24" fill="none">
            <path d="M2 9 Q 19 22 35 7" stroke="#FFFFFF" stroke-width="2.4" stroke-linecap="round" fill="none"/>
            <path d="M29 6 L36 6 L34 13" stroke="#FFFFFF" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" fill="none"/>
          </svg>
        </div>
      `
          : ""
      }

      <div class="cluster-cutouts-layer">
        ${CLUSTER_RING_NODES.map((node, idx) => {
          const isFocused = idx === state.clusterFocusedIdx;
          const leftPercent =
            state.isClusterShelfOpen && isFocused
              ? 18
              : state.isClusterShelfOpen
              ? Math.max(6, Math.round(node.normX * 100) - 12)
              : Math.round(node.normX * 100);
          return `
            <div
              class="cluster-cutout-node ${isFocused ? "focused" : "inactive"}"
              style="left: ${leftPercent}%; z-index: ${isFocused ? 16 : node.zIndex}; ${
            state.isClusterShelfOpen && !isFocused ? "opacity: 0.15;" : ""
          }"
              data-cluster-idx="${idx}"
            >
              <img src="${node.cutoutUrl}" alt="${escapeHtml(node.characterName)}" />
            </div>
          `;
        }).join("")}
      </div>

      <div class="cluster-bottom-vignette"></div>

      ${
        !state.isClusterShelfOpen
          ? `
        <div class="cluster-controls-bar">
          <button class="cluster-arrow-btn" id="cluster-prev-btn" aria-label="Previous Character">◀</button>
          <div class="cluster-center-controls">
            <button
              class="cluster-explore-btn"
              data-focusable
              id="cluster-explore-btn"
            >
              ▶ Explore ${escapeHtml(activeNode.displayTitle)}
            </button>
            <div class="cluster-dots">
              ${CLUSTER_RING_NODES.map(
                (n, idx) =>
                  `<span class="cluster-dot ${idx === state.clusterFocusedIdx ? "active" : ""}"></span>`
              ).join("")}
            </div>
          </div>
          <button class="cluster-arrow-btn" id="cluster-next-btn" aria-label="Next Character">▶</button>
        </div>
      `
          : `
        <div class="cluster-shelf-drawer">
          <div class="shelf-header">
            <div>
              <div style="font-size:11px;font-weight:800;color:${activeNode.glowRim};">${escapeHtml(
                activeNode.subtitleBadge
              )}</div>
              <div style="font-size:20px;font-weight:900;color:#fff;">${escapeHtml(
                activeNode.displayTitle
              )} — Quick Shelf</div>
            </div>
            <button class="pill-action-btn" data-focusable id="close-shelf-btn">✕ Close (Back)</button>
          </div>
          <div class="shelf-episodes-list">
            ${shelfEpisodes
              .slice(0, 5)
              .map(
                (ep, sIdx) => `
              <button
                class="shelf-ep-item"
                data-focusable
                id="shelf-ep-${sIdx}"
                data-play-url="${escapeHtml(ep.url)}"
                data-play-title="${escapeHtml(ep.title)}"
                data-play-date="${escapeHtml(ep.dateStr)}"
                data-play-thumb="${escapeHtml(ep.thumbnailUrl)}"
                data-play-show="${escapeHtml(ep.showTitle || activeNode.displayTitle)}"
              >
                <img src="${escapeHtml(ep.thumbnailUrl)}" alt="${escapeHtml(ep.title)}" />
                <div style="flex:1;min-width:0;">
                  <div style="font-size:13.5px;font-weight:800;color:#fff;">${escapeHtml(ep.title)}</div>
                  <div style="font-size:11.5px;color:#949eb0;margin-top:3px;">${escapeHtml(
                    ep.dateStr
                  )} • 1080p HD</div>
                </div>
                <span style="color:#ff2a54;font-weight:900;font-size:13px;">▶ Play</span>
              </button>
            `
              )
              .join("")}
          </div>
        </div>
      `
      }
    </section>

    <div class="content-container">
      <!-- Show Switcher Pills (Direct Down Target from Hero Carousel) -->
      <div class="show-pills-bar">
        ${CATALOG_SHOWS.map(
          (show, idx) => `
          <button
            class="show-pill-btn ${state.selectedShow.id === show.id ? "active" : ""}"
            data-focusable
            id="show-pill-${idx}"
            data-select-show="${show.id}"
          >
            📺 ${escapeHtml(show.title)}
          </button>
        `
        ).join("")}
        <button class="show-pill-btn" data-focusable id="show-pill-movies" data-tab="Movies">
          🎬 Tamil Movies (4-Col TV Grid)
        </button>
      </div>

      <!-- Continue Watching Rail (Accurate Per-Show & Per-Movie Thumbnails) -->
      ${
        state.watchHistory.length > 0
          ? `
        <section>
          <div class="section-header">
            <div class="section-title">
              ⏱ Continue Watching
              <span class="section-badge">${state.watchHistory.length} Saved</span>
            </div>
            <button class="pill-action-btn" data-focusable id="clear-history-btn">Clear History</button>
          </div>
          <div class="horizontal-rail">
            ${state.watchHistory
              .map((item, idx) => {
                const accurateThumb = resolveAccurateThumbnail(
                  item.episodeUrl,
                  item.episodeTitle,
                  item.thumbnailUrl
                );
                const pct =
                  item.durationSec > 0
                    ? Math.min(98, Math.max(8, Math.round((item.lastPositionSec / item.durationSec) * 100)))
                    : 35;
                return `
                <button
                  class="media-card"
                  data-focusable
                  id="cw-card-${idx}"
                  data-play-url="${escapeHtml(item.episodeUrl)}"
                  data-play-title="${escapeHtml(item.episodeTitle)}"
                  data-play-date="${escapeHtml(item.dateStr)}"
                  data-play-thumb="${escapeHtml(accurateThumb)}"
                  data-play-show="${escapeHtml(item.showTitle)}"
                >
                  <div class="media-thumb-wrap">
                    <img src="${escapeHtml(accurateThumb)}" alt="${escapeHtml(item.episodeTitle)}" loading="lazy" />
                    <span class="media-badge-top">Resume ${
                      item.lastPositionSec > 0 ? formatSeconds(item.lastPositionSec) : "HD"
                    }</span>
                    <div class="media-progress-track">
                      <div class="media-progress-fill" style="width:${pct}%"></div>
                    </div>
                  </div>
                  <div class="media-card-body">
                    <div class="media-card-show">${escapeHtml(item.showTitle)}</div>
                    <div class="media-card-title">${escapeHtml(item.episodeTitle)}</div>
                    <div class="media-card-meta">
                      <span>${escapeHtml(item.dateStr)}</span>
                      <span style="color:#ff2a54;font-weight:800;">▶ Resume</span>
                    </div>
                  </div>
                </button>
              `;
              })
              .join("")}
          </div>
        </section>
      `
          : ""
      }

      <!-- Active Show Latest Episodes Rail -->
      <section>
        <div class="section-header">
          <div class="section-title">
            🔥 ${escapeHtml(state.selectedShow.title)} — Latest Episodes
            <span class="section-badge">${escapeHtml(state.selectedShow.channel)} • 1080p HD</span>
          </div>
          <button class="pill-action-btn" data-focusable id="open-all-seasons-btn" data-tab="Episodes">
            All Seasons & Episodes →
          </button>
        </div>
        <div class="horizontal-rail">
          ${state.currentEpisodes
            .map((ep, idx) => renderEpisodeCard(ep, `featured-ep-${idx}`))
            .join("")}
        </div>
      </section>

      <!-- All Other Vijay TV & Sony Vizha Show Rails -->
      ${CATALOG_SHOWS.filter((s) => s.id !== state.selectedShow.id)
        .map((show) => {
          const eps = state.showCarousels[show.id] || INITIAL_EPISODES_BY_SHOW[show.id] || [];
          return `
            <section>
              <div class="section-header">
                <div class="section-title">
                  📺 ${escapeHtml(show.title)}
                  <span class="section-badge">${escapeHtml(show.badge)}</span>
                </div>
                <button
                  class="pill-action-btn"
                  data-focusable
                  data-open-show-seasons="${show.id}"
                >
                  Browse Seasons →
                </button>
              </div>
              <div class="horizontal-rail">
                ${eps.map((ep, idx) => renderEpisodeCard(ep, `rail-${show.id}-${idx}`)).join("")}
              </div>
            </section>
          `;
        })
        .join("")}

      <!-- Tamil Movies 4-Column Preview on Home -->
      <section>
        <div class="section-header">
          <div class="section-title">
            🎬 Featured Tamil Movies (1080p HDRip)
            <span class="section-badge">4 Per Row on TV</span>
          </div>
          <button class="pill-action-btn" data-focusable data-tab="Movies">
            View Full Movie Catalog →
          </button>
        </div>
        <div class="movies-grid-4col">
          ${state.movies
            .slice(0, 8)
            .map((movie, idx) => renderMovieGridCard(movie, `home-movie-${idx}`))
            .join("")}
        </div>
      </section>
    </div>
  `;
}

function renderEpisodeCard(ep, domId) {
  const isLive = Boolean(ep.isLiveStream);
  return `
    <button
      class="media-card"
      data-focusable
      id="${domId}"
      data-play-url="${escapeHtml(ep.url)}"
      data-play-title="${escapeHtml(ep.title)}"
      data-play-date="${escapeHtml(ep.dateStr)}"
      data-play-thumb="${escapeHtml(ep.thumbnailUrl)}"
      data-play-show="${escapeHtml(ep.showTitle || state.selectedShow.title)}"
      data-play-live="${isLive ? "1" : "0"}"
    >
      <div class="media-thumb-wrap">
        <img src="${escapeHtml(ep.thumbnailUrl)}" alt="${escapeHtml(ep.title)}" loading="lazy" />
        <span class="media-badge-top ${isLive ? "media-badge-live" : ""}">
          ${isLive ? "🔴 24/7 LIVE" : escapeHtml(ep.qualityBadge || "1080p HD")}
        </span>
      </div>
      <div class="media-card-body">
        <div class="media-card-show">${escapeHtml(ep.showTitle || state.selectedShow.title)}</div>
        <div class="media-card-title">${escapeHtml(ep.title)}</div>
        <div class="media-card-meta">
          <span>${escapeHtml(ep.dateStr)}</span>
          <span style="color:#00e5ff;font-weight:800;">▶ Watch HD</span>
        </div>
      </div>
    </button>
  `;
}

function renderMovieGridCard(movie, domId) {
  return `
    <button
      class="movie-poster-card"
      data-focusable
      id="${domId}"
      data-play-url="${escapeHtml(movie.url)}"
      data-play-title="${escapeHtml(movie.title)}"
      data-play-date="${escapeHtml(movie.dateStr)}"
      data-play-thumb="${escapeHtml(movie.thumbnailUrl)}"
      data-play-show="Tamil Movies"
    >
      <div class="movie-poster-wrap">
        <img
          src="${escapeHtml(movie.thumbnailUrl)}"
          alt="${escapeHtml(movie.title)}"
          loading="lazy"
          onerror="this.src='https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=800&q=80'"
        />
        <span class="media-badge-top">${escapeHtml(movie.qualityBadge || "1080p HDRip")}</span>
      </div>
      <div class="media-card-body">
        <div class="media-card-show">TAMIL CINEMA • 1080P</div>
        <div class="media-card-title">${escapeHtml(movie.title)}</div>
        <div class="media-card-meta">
          <span>${escapeHtml(movie.dateStr)}</span>
          <span style="color:#ff2a54;font-weight:800;">▶ Play Movie</span>
        </div>
      </div>
    </button>
  `;
}

function renderShowsAndSeasonsTab() {
  const filteredEps = state.episodeFilterQuery
    ? state.currentEpisodes.filter(
        (e) =>
          e.title.toLowerCase().includes(state.episodeFilterQuery.toLowerCase()) ||
          e.dateStr.toLowerCase().includes(state.episodeFilterQuery.toLowerCase())
      )
    : state.currentEpisodes;

  return `
    <div class="content-container">
      <div class="section-header">
        <div class="section-title">
          📺 TV Shows & Multi-Season Vault
          <span class="section-badge">${escapeHtml(state.selectedShow.title)}</span>
        </div>
      </div>

      <!-- Show Selector Strip -->
      <div class="show-pills-bar">
        ${CATALOG_SHOWS.map(
          (show, idx) => `
          <button
            class="show-pill-btn ${state.selectedShow.id === show.id ? "active" : ""}"
            data-focusable
            id="dir-show-${idx}"
            data-select-show="${show.id}"
          >
            ${escapeHtml(show.title)} (${show.seasons.length} Seasons)
          </button>
        `
        ).join("")}
      </div>

      <!-- Season Selector Strip -->
      <div class="show-pills-bar" style="padding-top:0;">
        ${state.selectedShow.seasons
          .map(
            (season, idx) => `
          <button
            class="pill-action-btn ${
              state.selectedSeason?.id === season.id ? "live-pill" : ""
            }"
            data-focusable
            id="dir-season-${idx}"
            data-select-season-id="${season.id}"
          >
            💿 ${escapeHtml(season.label)}
          </button>
        `
          )
          .join("")}
      </div>

      <!-- Filter Input -->
      <div>
        <input
          type="text"
          id="episode-filter-input"
          data-focusable
          placeholder="Filter ${escapeHtml(state.selectedShow.title)} episodes by date or Day..."
          value="${escapeHtml(state.episodeFilterQuery)}"
          style="width:100%;max-width:480px;padding:12px 16px;border-radius:12px;border:1.5px solid rgba(255,255,255,0.2);background:#131724;color:#fff;font-size:14px;"
        />
      </div>

      <div class="movies-grid-4col">
        ${filteredEps.map((ep, idx) => renderEpisodeCard(ep, `dir-ep-card-${idx}`)).join("")}
      </div>
    </div>
  `;
}

function renderMoviesTab() {
  const categories = [
    { label: "🔥 Featured Tamil", url: "https://www.5movierulz.green/category/tamil-featured" },
    { label: "🆕 Tamil Movies 2026", url: "https://www.5movierulz.green/category/tamil-movies-2026" },
    { label: "🎬 Tamil Blockbusters 2025", url: "https://www.5movierulz.green/category/tamil-movies-2025" },
  ];

  return `
    <div class="content-container">
      <div class="section-header">
        <div class="section-title">
          🎬 Tamil Cinema Hub (4 Movies Per Line on TV)
          <span class="section-badge">1080p HDRip & BluRay</span>
        </div>
      </div>

      <div class="show-pills-bar">
        ${categories
          .map(
            (c, idx) => `
          <button
            class="show-pill-btn ${state.selectedMovieCategory === c.url ? "active" : ""}"
            data-focusable
            id="movie-cat-${idx}"
            data-movie-cat="${c.url}"
          >
            ${c.label}
          </button>
        `
          )
          .join("")}
      </div>

      <div class="movies-grid-4col">
        ${state.movies.map((m, idx) => renderMovieGridCard(m, `movies-grid-card-${idx}`)).join("")}
      </div>
    </div>
  `;
}

function renderLiveStudioTab() {
  const liveServers = [
    {
      name: "Live Cam 1 • 1080p 60fps Uncut Feed",
      badge: "PRIMARY • 19ms",
      url: LIVE_STREAMS.cam1,
      desc: "Direct 1080p 60fps HLS feed from the Bigg Boss Tamil Season 10 house.",
    },
    {
      name: "Live Cam 2 • High-Speed Backup Feed",
      badge: "BACKUP • 24ms",
      url: LIVE_STREAMS.cam2,
      desc: "Secondary multi-CDN 24/7 live house camera with zero ad interruptions.",
    },
    {
      name: "Studio Web Player 1 (NamTamil)",
      badge: "STUDIO EMBED",
      url: LIVE_STREAMS.web1,
      desc: "Dedicated browser embed player for smart TVs and mobile browsers.",
    },
    {
      name: "Studio Web Player 2 (SpiritualLife)",
      badge: "MIRROR EMBED",
      url: LIVE_STREAMS.web2,
      desc: "Failover web broadcast mirror for uninterrupted 24/7 viewing.",
    },
  ];

  return `
    <div class="content-container">
      <div class="section-header">
        <div class="section-title">
          🔴 24/7 Live Broadcast Studio
          <span class="section-badge">Bigg Boss Tamil S10 Uncut</span>
        </div>
      </div>

      <div class="movies-grid-4col">
        ${liveServers
          .map(
            (srv, idx) => `
          <button
            class="movie-poster-card"
            data-focusable
            id="live-srv-${idx}"
            data-play-url="${escapeHtml(srv.url)}"
            data-play-title="${escapeHtml(srv.name)}"
            data-play-date="24/7 LIVE STREAM"
            data-play-thumb="${escapeHtml(POSTERS.biggBossLive)}"
            data-play-show="Bigg Boss 10 Live"
            data-play-live="1"
          >
            <div class="movie-poster-wrap">
              <img src="${escapeHtml(POSTERS.biggBossLive)}" alt="${escapeHtml(srv.name)}" />
              <span class="media-badge-top media-badge-live">🔴 ${escapeHtml(srv.badge)}</span>
            </div>
            <div class="media-card-body">
              <div class="media-card-show">STAR VIJAY 24/7 LIVE</div>
              <div class="media-card-title">${escapeHtml(srv.name)}</div>
              <div class="media-card-meta">
                <span>${escapeHtml(srv.desc)}</span>
              </div>
            </div>
          </button>
        `
          )
          .join("")}
      </div>
    </div>
  `;
}

function renderMyListAndScheduleTab() {
  return `
    <div class="content-container">
      <section>
        <div class="section-header">
          <div class="section-title">
            🔖 My Saved Watchlist
            <span class="section-badge">${state.myList.length} Items</span>
          </div>
        </div>
        ${
          state.myList.length === 0
            ? `<div style="padding:28px;background:#131724;border-radius:16px;border:1px solid rgba(255,255,255,0.1);color:#949eb0;">
                Your watchlist is empty. Select any episode or movie to start streaming, or bookmark episodes from the player!
              </div>`
            : `<div class="movies-grid-4col">
                ${state.myList
                  .map(
                    (item, idx) => `
                  <button
                    class="movie-poster-card"
                    data-focusable
                    id="mylist-card-${idx}"
                    data-play-url="${escapeHtml(item.episodeUrl)}"
                    data-play-title="${escapeHtml(item.episodeTitle)}"
                    data-play-date="${escapeHtml(item.dateStr)}"
                    data-play-thumb="${escapeHtml(
                      resolveAccurateThumbnail(item.episodeUrl, item.episodeTitle, item.thumbnailUrl)
                    )}"
                    data-play-show="Saved Watchlist"
                  >
                    <div class="movie-poster-wrap">
                      <img src="${escapeHtml(
                        resolveAccurateThumbnail(item.episodeUrl, item.episodeTitle, item.thumbnailUrl)
                      )}" alt="${escapeHtml(item.episodeTitle)}" />
                      <span class="media-badge-top">BOOKMARKED</span>
                    </div>
                    <div class="media-card-body">
                      <div class="media-card-title">${escapeHtml(item.episodeTitle)}</div>
                      <div class="media-card-meta">
                        <span>${escapeHtml(item.dateStr)}</span>
                        <span style="color:#ff2a54;font-weight:800;">▶ Watch</span>
                      </div>
                    </div>
                  </button>
                `
                  )
                  .join("")}
              </div>`
        }
      </section>

      <section>
        <div class="section-header">
          <div class="section-title">
            📅 Smart Telecast Schedule (IST)
            <span class="section-badge">Verified Multi-Mirror Broadcasts</span>
          </div>
        </div>
        <div class="movies-grid-4col">
          ${SMART_CALENDAR.map(
            (item, idx) => `
            <button
              class="movie-poster-card"
              data-focusable
              id="cal-card-${idx}"
              data-play-url="${escapeHtml(item.episodeUrl)}"
              data-play-title="${escapeHtml(item.cleanTag)}"
              data-play-date="${escapeHtml(item.telecastTime)}"
              data-play-thumb="${escapeHtml(item.thumbnailUrl)}"
              data-play-show="${escapeHtml(item.showTitle)}"
            >
              <div class="movie-poster-wrap">
                <img src="${escapeHtml(item.thumbnailUrl)}" alt="${escapeHtml(item.showTitle)}" />
                <span class="media-badge-top">${escapeHtml(item.telecastTime)}</span>
              </div>
              <div class="media-card-body">
                <div class="media-card-show">${escapeHtml(item.channel)} • ${
              item.activeMirrorsCount
            } MIRRORS</div>
                <div class="media-card-title">${escapeHtml(item.showTitle)}</div>
                <div class="media-card-meta">
                  <span>${escapeHtml(item.cleanTag)}</span>
                </div>
              </div>
            </button>
          `
          ).join("")}
        </div>
      </section>
    </div>
  `;
}

function renderSearchView() {
  const pool = [
    ...state.currentEpisodes,
    ...Object.values(state.showCarousels).flat(),
    ...state.movies,
  ];
  const seen = new Set();
  const uniquePool = pool.filter((item) => {
    if (seen.has(item.url)) return false;
    seen.add(item.url);
    return true;
  });

  const q = state.searchQuery.trim().toLowerCase();
  const results = q
    ? uniquePool.filter(
        (i) =>
          i.title.toLowerCase().includes(q) ||
          (i.showTitle && i.showTitle.toLowerCase().includes(q)) ||
          (i.dateStr && i.dateStr.toLowerCase().includes(q))
      )
    : uniquePool.slice(0, 12);

  return `
    <div class="content-container">
      <div class="section-header">
        <div class="section-title">🔍 Search Shows, Episodes & Tamil Movies</div>
      </div>
      <input
        type="text"
        id="global-search-input"
        data-focusable
        placeholder="Search Bigg Boss, MasterChef, Cooku with Comali, Super Singer, Movies..."
        value="${escapeHtml(state.searchQuery)}"
        style="width:100%;padding:14px 18px;border-radius:14px;border:2px solid #00e5ff;background:#131724;color:#fff;font-size:15px;"
      />
      <div class="movies-grid-4col">
        ${results.map((item, idx) => renderEpisodeCard(item, `search-res-${idx}`)).join("")}
      </div>
    </div>
  `;
}

function renderVideoPlayerModal() {
  const ep = state.player.episode || {};
  const activeSrc = state.player.sources[state.player.selectedSourceIdx];
  const isBookmarked = state.myList.some((i) => i.episodeUrl === ep.url);

  return `
    <div class="player-modal" id="player-modal">
      <div class="player-video-stage" id="player-video-stage">
        ${
          state.player.loadingSources
            ? `<div style="color:#fff;font-size:16px;font-weight:800;">⚡ Connecting to 1080p Full HD Stream Mirrors...</div>`
            : ""
        }
      </div>

      <div class="player-hud-overlay ${state.player.hudVisible ? "" : "hud-hidden"}" id="player-hud-overlay">
        <!-- Top Bar -->
        <div class="player-top-bar">
          <div style="display:flex;align-items:center;gap:14px;">
            <button class="player-ctrl-btn" data-focusable id="player-back-btn">
              ← Back
            </button>
            <div>
              <div style="font-size:18px;font-weight:900;color:#fff;">${escapeHtml(ep.title || "Now Playing")}</div>
              <div style="font-size:12px;color:#00e5ff;font-weight:700;">
                ${escapeHtml(activeSrc?.healthBadge || "Server 1 • 1080p Full HD")} • Resolution: ${escapeHtml(
    state.player.selectedQuality
  )}
              </div>
            </div>
          </div>

          <div style="display:flex;align-items:center;gap:10px;">
            <button class="player-ctrl-btn" data-focusable id="player-bookmark-btn">
              ${isBookmarked ? "🔖 Saved in My List" : "＋ Add to My List"}
            </button>
          </div>
        </div>

        <!-- Bottom Controls: Seekbar, Rewind/Play/Forward, Quality Selector, Stream Mirrors -->
        <div class="player-bottom-controls">
          <div class="player-seek-row">
            <input
              type="range"
              min="0"
              max="${Math.max(100, Math.floor(state.player.duration || 0))}"
              value="${Math.floor(state.player.currentTime || 0)}"
              class="player-seek-slider"
              data-focusable
              id="player-seek-slider"
            />
            <span id="player-time-label" style="font-size:12.5px;font-weight:800;color:#fff;min-width:120px;text-align:right;">
              ${formatSeconds(state.player.currentTime)} / ${formatSeconds(state.player.duration)}
            </span>
          </div>

          <!-- Primary Transport Controls Row -->
          <div class="player-transport-row">
            <div class="player-transport-group">
              <button class="player-ctrl-btn" data-focusable id="player-rewind-btn">
                ⏪ -10s
              </button>
              <button class="player-ctrl-btn primary-play" data-focusable id="player-play-toggle-btn">
                ${state.player.isPlaying ? "⏸ Pause" : "▶ Play"}
              </button>
              <button class="player-ctrl-btn" data-focusable id="player-ff-btn">
                ⏩ +10s
              </button>
              <button
                class="player-ctrl-btn ${state.player.useProxy ? "active-pill" : ""}"
                data-focusable
                id="player-proxy-toggle-btn"
              >
                🛡️ ${state.player.useProxy ? "Proxy: ON" : "Boost / Proxy"}
              </button>
            </div>

            <!-- Quality / Resolution Switcher -->
            <div class="player-transport-group">
              <span style="font-size:12px;color:#949eb0;font-weight:800;">Quality:</span>
              ${state.player.availableQualities
                .map(
                  (q, qIdx) => `
                <button
                  class="player-ctrl-btn ${state.player.selectedQuality === q ? "active-pill" : ""}"
                  data-focusable
                  id="player-quality-${qIdx}"
                  data-quality="${q}"
                >
                  ${q}
                </button>
              `
                )
                .join("")}
            </div>
          </div>

          <!-- Stream Mirrors & Multi-Part Selector Row -->
          <div>
            <div style="font-size:11.5px;font-weight:800;color:#ffc857;margin-bottom:6px;">
              📡 Stream Mirrors & Episode Parts (D-Pad Down / Left / Right to Switch):
            </div>
            <div class="player-mirrors-strip">
              ${state.player.sources
                .map(
                  (src, idx) => `
                <button
                  class="player-ctrl-btn ${
                    idx === state.player.selectedSourceIdx ? "active-pill" : ""
                  }"
                  data-focusable
                  id="player-mirror-${idx}"
                  data-mirror-idx="${idx}"
                >
                  ${idx === state.player.selectedSourceIdx ? "● " : ""}${escapeHtml(src.name)}
                </button>
              `
                )
                .join("")}
            </div>
          </div>
        </div>
      </div>

      <!-- Exit Confirmation Dialog on Back Button -->
      ${
        state.player.showExitConfirm
          ? `
        <div class="modal-backdrop">
          <div class="modal-card">
            <div style="font-size:20px;font-weight:900;color:#fff;">Exit Video Playback?</div>
            <div style="font-size:14px;color:#949eb0;line-height:1.5;">
              Your playback progress is automatically saved to Continue Watching. Would you like to exit to the catalog or keep watching?
            </div>
            <div style="display:flex;gap:12px;justify-content:flex-end;margin-top:8px;">
              <button class="player-ctrl-btn" data-focusable id="confirm-exit-stay-btn">
                Stay & Watch
              </button>
              <button class="player-ctrl-btn primary-play" data-focusable id="confirm-exit-yes-btn">
                Exit Video
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

function renderEasterEggModal() {
  return `
    <div class="modal-backdrop" id="easter-egg-backdrop">
      <div class="modal-card">
        <div style="font-size:21px;font-weight:900;color:#ffc857;">🍬 Rasagulla Cinema Engine v2026</div>
        <div style="font-size:13.5px;color:#f5f7fa;line-height:1.55;">
          • Multi-Source Simultaneous Scraper (TamilDhool, 1TamilCrow, Arivumani, TamilGun, 5MovieRulz)<br/>
          • 10-Foot Smart TV Spatial D-Pad Controller + Web & Mobile Responsive UI<br/>
          • Default 1080p High-Resolution Adaptive HLS + Smart TS Proxy
        </div>
        <button class="player-ctrl-btn primary-play" data-focusable id="close-easter-egg-btn">
          Awesome
        </button>
      </div>
    </div>
  `;
}

// Event Bindings
function bindMainDomEvents() {
  // Brand Logo 5-Tap Easter Egg
  const handleLogoTap = () => {
    const now = Date.now();
    if (now - state.easterEggLastTap <= 1500) {
      state.easterEggCount += 1;
    } else {
      state.easterEggCount = 1;
    }
    state.easterEggLastTap = now;
    if (state.easterEggCount >= 5) {
      state.easterEggCount = 0;
      state.showEasterEggModal = true;
      renderApp();
      return;
    }
    state.currentTab = "Featured";
    state.searchOpen = false;
    renderApp();
  };

  document.getElementById("rail-brand-logo")?.addEventListener("click", handleLogoTap);
  document.getElementById("mobile-brand-logo")?.addEventListener("click", handleLogoTap);
  document.getElementById("close-easter-egg-btn")?.addEventListener("click", () => {
    state.showEasterEggModal = false;
    renderApp();
  });

  // Tab Buttons
  document.querySelectorAll("[data-tab]").forEach((btn) => {
    btn.addEventListener("click", () => {
      const targetTab = btn.getAttribute("data-tab");
      if (targetTab) {
        state.currentTab = targetTab;
        state.searchOpen = false;
        renderApp();
      }
    });
  });

  // Top Bar Actions
  document.getElementById("top-search-btn")?.addEventListener("click", () => {
    state.searchOpen = !state.searchOpen;
    renderApp();
    if (state.searchOpen) {
      setTimeout(() => document.getElementById("global-search-input")?.focus(), 40);
    }
  });

  document.getElementById("top-refresh-btn")?.addEventListener("click", () => {
    showToast("🔄 Refreshing live episodes & movie mirrors...");
    fetchEpisodesForShow(state.selectedSeason?.url || state.selectedShow.url, state.selectedShow.id);
    fetchMoviesCatalog(state.selectedMovieCategory);
  });

  document.getElementById("top-live-btn")?.addEventListener("click", () => {
    state.currentTab = "Live 24/7";
    state.searchOpen = false;
    renderApp();
  });

  // Cluster Hero Carousel Events
  document.getElementById("cluster-prev-btn")?.addEventListener("click", () => stepClusterFocus(-1));
  document.getElementById("cluster-next-btn")?.addEventListener("click", () => stepClusterFocus(1));
  document.getElementById("cluster-explore-btn")?.addEventListener("click", handleClusterPrimaryAction);
  document.getElementById("close-shelf-btn")?.addEventListener("click", () => {
    state.isClusterShelfOpen = false;
    renderApp();
    document.getElementById("cluster-explore-btn")?.focus();
  });

  document.querySelectorAll("[data-cluster-idx]").forEach((nodeEl) => {
    nodeEl.addEventListener("click", () => {
      const idx = parseInt(nodeEl.getAttribute("data-cluster-idx"), 10);
      if (idx === state.clusterFocusedIdx) {
        handleClusterPrimaryAction();
      } else {
        state.clusterFocusedIdx = idx;
        const activeNode = CLUSTER_RING_NODES[idx];
        if (activeNode.associatedShowId) {
          const matched = CATALOG_SHOWS.find((s) => s.id === activeNode.associatedShowId);
          if (matched) {
            state.selectedShow = matched;
            state.selectedSeason = matched.seasons[0] || null;
            state.currentEpisodes =
              state.showCarousels[matched.id] || INITIAL_EPISODES_BY_SHOW[matched.id] || [];
          }
        }
        renderApp();
      }
    });
  });

  // Touch Swipe on Cluster Stage
  const stageEl = document.getElementById("cluster-portal-stage");
  if (stageEl) {
    let touchStartX = 0;
    stageEl.addEventListener(
      "touchstart",
      (e) => {
        touchStartX = e.changedTouches[0]?.clientX || 0;
      },
      { passive: true }
    );
    stageEl.addEventListener(
      "touchend",
      (e) => {
        const dx = (e.changedTouches[0]?.clientX || 0) - touchStartX;
        if (Math.abs(dx) > 45 && !state.isClusterShelfOpen) {
          stepClusterFocus(dx < 0 ? 1 : -1);
        }
      },
      { passive: true }
    );
  }

  // Show Selection Pills
  document.querySelectorAll("[data-select-show]").forEach((btn) => {
    btn.addEventListener("click", () => {
      const showId = btn.getAttribute("data-select-show");
      const matched = CATALOG_SHOWS.find((s) => s.id === showId);
      if (matched) {
        state.selectedShow = matched;
        state.selectedSeason = matched.seasons[0] || null;
        state.currentEpisodes =
          state.showCarousels[matched.id] || INITIAL_EPISODES_BY_SHOW[matched.id] || [];
        renderApp();
        fetchEpisodesForShow(matched.url, matched.id);
      }
    });
  });

  document.querySelectorAll("[data-open-show-seasons]").forEach((btn) => {
    btn.addEventListener("click", () => {
      const showId = btn.getAttribute("data-open-show-seasons");
      const matched = CATALOG_SHOWS.find((s) => s.id === showId);
      if (matched) {
        state.selectedShow = matched;
        state.selectedSeason = matched.seasons[0] || null;
        state.currentEpisodes =
          state.showCarousels[matched.id] || INITIAL_EPISODES_BY_SHOW[matched.id] || [];
        state.currentTab = "Episodes";
        renderApp();
        fetchEpisodesForShow(matched.url, matched.id);
      }
    });
  });

  // Season Selection Pills
  document.querySelectorAll("[data-select-season-id]").forEach((btn) => {
    btn.addEventListener("click", () => {
      const sId = btn.getAttribute("data-select-season-id");
      const season = state.selectedShow.seasons.find((s) => s.id === sId);
      if (season) {
        state.selectedSeason = season;
        showToast(`Loading ${season.label}...`);
        renderApp();
        fetchEpisodesForShow(season.url, null);
      }
    });
  });

  // Movie Category Pills
  document.querySelectorAll("[data-movie-cat]").forEach((btn) => {
    btn.addEventListener("click", () => {
      const catUrl = btn.getAttribute("data-movie-cat");
      if (catUrl) {
        state.selectedMovieCategory = catUrl;
        showToast("Loading Tamil movie category...");
        renderApp();
        fetchMoviesCatalog(catUrl);
      }
    });
  });

  // Clear History
  document.getElementById("clear-history-btn")?.addEventListener("click", () => {
    state.watchHistory = [];
    saveWatchHistory();
    showToast("Cleared Continue Watching history");
    renderApp();
  });

  // Playable Cards
  document.querySelectorAll("[data-play-url]").forEach((card) => {
    card.addEventListener("click", () => {
      const url = card.getAttribute("data-play-url");
      const title = card.getAttribute("data-play-title");
      const dateStr = card.getAttribute("data-play-date");
      const thumbnailUrl = card.getAttribute("data-play-thumb");
      const showTitle = card.getAttribute("data-play-show");
      const isLiveStream = card.getAttribute("data-play-live") === "1";
      if (url) {
        openVideoPlayer({
          url,
          title,
          dateStr,
          thumbnailUrl,
          showTitle,
          isLiveStream,
        });
      }
    });
  });

  // Search & Filter Inputs
  const searchInput = document.getElementById("global-search-input");
  if (searchInput) {
    searchInput.addEventListener("input", (e) => {
      state.searchQuery = e.target.value;
      const pos = searchInput.selectionStart;
      renderApp();
      const nextInput = document.getElementById("global-search-input");
      if (nextInput) {
        nextInput.focus();
        nextInput.setSelectionRange(pos, pos);
      }
    });
  }

  const epFilter = document.getElementById("episode-filter-input");
  if (epFilter) {
    epFilter.addEventListener("input", (e) => {
      state.episodeFilterQuery = e.target.value;
      const pos = epFilter.selectionStart;
      renderApp();
      const nextInput = document.getElementById("episode-filter-input");
      if (nextInput) {
        nextInput.focus();
        nextInput.setSelectionRange(pos, pos);
      }
    });
  }
}

function bindPlayerDomEvents() {
  const modal = document.getElementById("player-modal");
  modal?.addEventListener("mousemove", resetHudAutoHide);
  modal?.addEventListener("touchstart", resetHudAutoHide, { passive: true });

  document.getElementById("player-back-btn")?.addEventListener("click", requestExitPlayer);
  document.getElementById("player-play-toggle-btn")?.addEventListener("click", togglePlayPause);
  document.getElementById("player-rewind-btn")?.addEventListener("click", () => seekRelative(-10));
  document.getElementById("player-ff-btn")?.addEventListener("click", () => seekRelative(10));

  document.getElementById("player-proxy-toggle-btn")?.addEventListener("click", () => {
    state.player.useProxy = !state.player.useProxy;
    showToast(state.player.useProxy ? "🛡️ Smart Proxy Enabled" : "Direct CDN Mode");
    renderApp();
    mountActiveStreamSource();
  });

  document.getElementById("player-bookmark-btn")?.addEventListener("click", () => {
    const ep = state.player.episode;
    if (ep) {
      toggleWatchlistByUrl(ep.url, ep.title, ep.dateStr, ep.thumbnailUrl);
    }
  });

  const seekSlider = document.getElementById("player-seek-slider");
  seekSlider?.addEventListener("input", (e) => {
    const video = document.getElementById("rasagulla-video-el");
    if (video) {
      video.currentTime = Number(e.target.value);
    }
    resetHudAutoHide();
  });

  document.querySelectorAll("[data-quality]").forEach((btn) => {
    btn.addEventListener("click", () => {
      const q = btn.getAttribute("data-quality");
      if (q) selectPlayerQuality(q);
    });
  });

  document.querySelectorAll("[data-mirror-idx]").forEach((btn) => {
    btn.addEventListener("click", () => {
      const idx = parseInt(btn.getAttribute("data-mirror-idx"), 10);
      if (!isNaN(idx)) selectPlayerMirror(idx);
    });
  });

  document.getElementById("confirm-exit-stay-btn")?.addEventListener("click", () => {
    state.player.showExitConfirm = false;
    renderApp();
    mountActiveStreamSource();
  });

  document.getElementById("confirm-exit-yes-btn")?.addEventListener("click", closeVideoPlayerCompletely);
}

/**
 * Spatial 2D D-Pad & Smart TV Browser Remote Navigation Engine
 * Benchmarks Netflix Android TV / Smart TV Web App for deterministic Up/Down/Left/Right traversal.
 */
function findNearestFocusable(currentEl, direction) {
  const candidates = Array.from(document.querySelectorAll("[data-focusable]")).filter((el) => {
    if (el === currentEl) return false;
    const rect = el.getBoundingClientRect();
    return rect.width > 0 && rect.height > 0;
  });

  if (candidates.length === 0) return null;
  const curRect = currentEl.getBoundingClientRect();
  const curCx = curRect.left + curRect.width / 2;
  const curCy = curRect.top + curRect.height / 2;

  let best = null;
  let bestScore = Infinity;

  candidates.forEach((cand) => {
    const r = cand.getBoundingClientRect();
    const cx = r.left + r.width / 2;
    const cy = r.top + r.height / 2;
    const dx = cx - curCx;
    const dy = cy - curCy;

    if (direction === "left" && dx >= -6) return;
    if (direction === "right" && dx <= 6) return;
    if (direction === "up" && dy >= -6) return;
    if (direction === "down" && dy <= 6) return;

    const primaryDist = direction === "left" || direction === "right" ? Math.abs(dx) : Math.abs(dy);
    const crossDist = direction === "left" || direction === "right" ? Math.abs(dy) : Math.abs(dx);

    // Weight cross-axis distance heavier so D-Pad stays inside the same row/column cleanly
    const score = primaryDist + crossDist * 2.65;
    if (score < bestScore) {
      bestScore = score;
      best = cand;
    }
  });

  return best;
}

window.addEventListener("keydown", (e) => {
  const active = document.activeElement;
  const isTextInput = active && (active.tagName === "INPUT" || active.tagName === "TEXTAREA");

  // Handle Video Player Remote Keys
  if (state.player.active) {
    resetHudAutoHide();

    if (e.key === "Escape" || e.key === "Backspace" || e.key === "BrowserBack" || e.keyCode === 461) {
      e.preventDefault();
      if (state.player.showExitConfirm) {
        closeVideoPlayerCompletely();
      } else {
        requestExitPlayer();
      }
      return;
    }

    if (e.key === "MediaPlayPause" || e.key === " ") {
      e.preventDefault();
      togglePlayPause();
      return;
    }
    if (e.key === "MediaRewind") {
      e.preventDefault();
      seekRelative(-10);
      return;
    }
    if (e.key === "MediaFastForward") {
      e.preventDefault();
      seekRelative(10);
      return;
    }
  } else {
    // Back button when Cluster Shelf or Search is open
    if (e.key === "Escape" || (e.key === "Backspace" && !isTextInput)) {
      if (state.isClusterShelfOpen) {
        e.preventDefault();
        state.isClusterShelfOpen = false;
        renderApp();
        document.getElementById("cluster-explore-btn")?.focus();
        return;
      }
      if (state.searchOpen) {
        e.preventDefault();
        state.searchOpen = false;
        renderApp();
        return;
      }
    }
  }

  // Hero Carousel D-Pad Special Handling (Left/Right cycles characters, Down jumps to Show Pills)
  if (active && active.id === "cluster-explore-btn" && !state.isClusterShelfOpen) {
    if (e.key === "ArrowLeft") {
      e.preventDefault();
      stepClusterFocus(-1);
      return;
    }
    if (e.key === "ArrowRight") {
      e.preventDefault();
      stepClusterFocus(1);
      return;
    }
    if (e.key === "ArrowDown") {
      e.preventDefault();
      const firstPill = document.getElementById("show-pill-0");
      if (firstPill) {
        firstPill.focus();
        firstPill.scrollIntoView({ behavior: "smooth", block: "center" });
      }
      return;
    }
  }

  if (isTextInput && (e.key === "ArrowLeft" || e.key === "ArrowRight")) {
    return;
  }

  const dirMap = {
    ArrowLeft: "left",
    ArrowRight: "right",
    ArrowUp: "up",
    ArrowDown: "down",
  };
  const dir = dirMap[e.key];
  if (!dir) return;

  e.preventDefault();
  if (!active || !active.hasAttribute("data-focusable")) {
    const first =
      document.getElementById("cluster-explore-btn") || document.querySelector("[data-focusable]");
    if (first) first.focus();
    return;
  }

  const nextEl = findNearestFocusable(active, dir);
  if (nextEl) {
    nextEl.focus();
    nextEl.scrollIntoView({ behavior: "smooth", block: "nearest", inline: "nearest" });
  }
});

// Boot Application
document.addEventListener("DOMContentLoaded", () => {
  loadPersistedStorage();
  renderApp();

  // Focus Hero Carousel primary button on TV / Desktop launch
  setTimeout(() => {
    document.getElementById("cluster-explore-btn")?.focus();
  }, 100);

  // Staggered non-blocking background catalog hydration
  setTimeout(() => {
    CATALOG_SHOWS.forEach((show) => {
      fetchEpisodesForShow(show.url, show.id);
    });
    fetchMoviesCatalog(state.selectedMovieCategory);
  }, 300);
});
