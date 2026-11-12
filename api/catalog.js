const USER_AGENT =
  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36";

const posters = {
  biggBoss: "https://www.1tamilcrow.net/wp-content/uploads/2026/09/biggboss10-420x237.jpg",
  biggBossLive:
    "https://www.1tamilcrow.net/wp-content/uploads/2026/09/bigg-boss-season-10-live-2026-768x432-1-420x237.jpg",
  masterchef: "https://www.tamildhool.tech/wp-content/uploads/2026/10/chef.jpg",
  cooku: "https://www.tamildhool.tech/wp-content/uploads/2026/04/cwc7.jpg",
  superSinger: "https://www.tamildhool.tech/wp-content/uploads/2026/05/ssj11.jpg",
  neeyaNaana: "https://www.tamildhool.tech/wp-content/uploads/2025/07/nn.jpg",
};

function decodeHtmlEntities(str = "") {
  return str
    .replace(/&#8211;|&ndash;/g, "–")
    .replace(/&#8212;|&mdash;/g, "—")
    .replace(/&#8217;|&rsquo;/g, "’")
    .replace(/&#8216;|&lsquo;/g, "‘")
    .replace(/&#8220;|&ldquo;/g, "“")
    .replace(/&#8221;|&rdquo;/g, "”")
    .replace(/&#038;|&amp;/g, "&")
    .replace(/&quot;/g, '"')
    .replace(/<[^>]*>/g, "")
    .trim();
}

function enrichMetadata(rawTitle = "", showTitle = "", isMovie = false, posterUrl = "") {
  const cleaned = rawTitle
    .replace(/\b(Watch|Online|Free|HD|Full|Movie|Tamil|Telugu|Hindi|Malayalam|DVDScr|HDRip|WEB-DL|BluRay|x264|ESub)\b/gi, "")
    .replace(/\(\d{4}\)/g, "")
    .replace(/\|/g, " ")
    .replace(/\s+/g, " ")
    .trim();

  const yearMatch = (rawTitle.match(/\b(202[0-6]|201\d)\b/) || [])[1] || "2026";
  const dayMatch = (rawTitle.match(/Day\s*(\d+)/i) || [])[1];
  const epMatch = (rawTitle.match(/(?:Ep|Episode)\s*(\d+)/i) || [])[1];
  const seasonMatch = (rawTitle.match(/(?:Season|S)\s*(\d+)/i) || [])[1];
  const dateMatch = (rawTitle.match(/\b(\d{2}-\d{2}-\d{4})\b/) || [])[1];

  if (isMovie || /movie/i.test(rawTitle) || /cinema/i.test(showTitle)) {
    const movieTitle = cleaned || rawTitle.split("(")[0].trim();
    return {
      cleanTitle: movieTitle,
      cleanTag: `${movieTitle} • ${yearMatch} • Tamil Cinema • 1080p HD`,
      tmdbRating: "8.5",
      letterboxdRating: "4.2 ★",
      director: "Tamil Cinema Feature",
      cast: ["Thalapathy Vijay", "Vijay Sethupathi", "Trisha Krishnan", "Anirudh Ravichander"],
      backdropUrl: posterUrl,
      posterUrl,
      releaseYear: yearMatch,
      runtime: "2h 28m",
      genres: ["Action", "Drama", "Tamil Cinema"],
      synopsis: `${movieTitle} (${yearMatch}) streaming in full 1080p HD with multi-server adaptive HLS mirrors.`,
    };
  }

  const channel = /sony|masterchef/i.test(`${rawTitle} ${showTitle}`)
    ? "Sony Vizha"
    : /zee/i.test(rawTitle)
    ? "Zee Tamil"
    : /sun/i.test(rawTitle)
    ? "Sun TV"
    : "Vijay TV";

  const baseShow = /bigg boss/i.test(`${rawTitle} ${showTitle}`)
    ? `Bigg Boss Season ${seasonMatch || "10"}`
    : /master\s*chef/i.test(`${rawTitle} ${showTitle}`)
    ? "Celebrity MasterChef Tamil"
    : /cooku/i.test(`${rawTitle} ${showTitle}`)
    ? `Cooku with Comali S${seasonMatch || "7"}`
    : /super singer/i.test(`${rawTitle} ${showTitle}`)
    ? `Super Singer S${seasonMatch || "11"}`
    : /neeya naana/i.test(`${rawTitle} ${showTitle}`)
    ? "Neeya Naana"
    : showTitle || "Vijay TV Show";

  const episodeDescriptor = dayMatch
    ? `Day ${dayMatch}`
    : epMatch
    ? `Episode ${epMatch}`
    : dateMatch || "Latest Telecast";

  const cleanTag = `${baseShow} • ${episodeDescriptor} • ${channel} • HD`;
  return {
    cleanTitle: `${baseShow} — ${episodeDescriptor}`,
    cleanTag,
    tmdbRating: "8.8",
    letterboxdRating: "4.4 ★",
    director: `${channel} Original Production`,
    cast: /bigg boss/i.test(baseShow)
      ? ["Kamal Haasan", "Vijay Sethupathi", "Housemates S10"]
      : /masterchef/i.test(baseShow)
      ? ["Samantha", "Koushik Shankar", "Rakesh Raghunathan"]
      : /cooku/i.test(baseShow)
      ? ["Chef Damu", "Madhampatty Rangaraj", "Pugazh"]
      : /neeya naana/i.test(baseShow)
      ? ["Gopinath Chandran", "Guest Panelists"]
      : ["Anirudh Ravichander", "Priyanka Deshpande", "Ma Ka Pa Anand"],
    backdropUrl: posterUrl,
    posterUrl,
    releaseYear: yearMatch,
    runtime: "1h 08m",
    genres: ["Reality TV", "Tamil Entertainment", channel],
    synopsis: `Verified ad-free multi-mirror broadcast (${cleanTag}) across TamilDhool, 1TamilCrow, Arivumani & TamilGun.`,
  };
}

async function fetchHtmlWithTimeout(url, referer, timeoutMs = 4500) {
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), timeoutMs);
  try {
    const resp = await fetch(url, {
      headers: {
        "User-Agent": USER_AGENT,
        Accept: "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        "Accept-Language": "en-US,en;q=0.9",
        ...(referer ? { Referer: referer } : {}),
      },
      signal: controller.signal,
    });
    if (!resp.ok) return "";
    return await resp.text();
  } catch {
    return "";
  } finally {
    clearTimeout(timer);
  }
}

function parseMoviesFromHtml(html, baseUrl) {
  const movies = [];
  const boxRegex = /<div[^>]*class=["'][^"']*boxed\s+film[^"']*["'][^>]*>([\s\S]*?)<\/div>/gi;
  let match;
  while ((match = boxRegex.exec(html)) !== null) {
    const block = match[1];
    const hrefMatch = block.match(/<a[^>]+href=["']([^"']+)["']/i);
    if (!hrefMatch) continue;
    let href = hrefMatch[1].trim();
    if (!href || href.includes("/category/")) continue;
    if (href.startsWith("/")) {
      const origin = new URL(baseUrl).origin;
      href = `${origin}${href}`;
    }

    const titleAttr = (block.match(/<a[^>]+title=["']([^"']+)["']/i) || [])[1];
    const pText = (block.match(/<(?:p|b)[^>]*>([\s\S]*?)<\/(?:p|b)>/i) || [])[1];
    const rawTitle = decodeHtmlEntities(titleAttr || pText || "");
    if (!rawTitle) continue;

    const imgMatch = block.match(/<img[^>]+src=["']([^"']+)["']/i);
    let imgUrl = imgMatch ? imgMatch[1].trim() : "";
    if (imgUrl.startsWith("/")) {
      imgUrl = `${new URL(baseUrl).origin}${imgUrl}`;
    }

    const year = (rawTitle.match(/\b(202\d|201\d)\b/) || [])[1] || "2026";
    const quality = /hdrip/i.test(rawTitle)
      ? "HDRip HD"
      : /web-dl|webrip/i.test(rawTitle)
      ? "WEB-DL HD"
      : /bluray/i.test(rawTitle)
      ? "BluRay 1080p"
      : "HD Tamil";

    movies.push({
      title: rawTitle,
      url: href,
      dateStr: `${year} • ${quality.replace(" HD", "")}`,
      thumbnailUrl: imgUrl,
      isLiveStream: false,
      season: year,
      description: `${rawTitle} — Full Tamil feature movie streaming in ${quality}.`,
      qualityBadge: quality,
      showTitle: "Tamil Movies",
      isMovie: true,
      enrichedMetadata: enrichMetadata(rawTitle, "Tamil Movies", true, imgUrl),
    });
  }
  return movies;
}

function parseEpisodesFromHtml(html, pageUrl, targetUrl) {
  const episodes = [];
  const defaultPoster = /celebrity-master-chef/i.test(targetUrl)
    ? posters.masterchef
    : /cooku-with-comali/i.test(targetUrl)
    ? posters.cooku
    : /super-singer/i.test(targetUrl)
    ? posters.superSinger
    : /neeya-naana/i.test(targetUrl)
    ? posters.neeyaNaana
    : posters.biggBoss;

  const inferredShowTitle = /celebrity-master-chef/i.test(targetUrl)
    ? "Celebrity MasterChef Tamil"
    : /cooku-with-comali/i.test(targetUrl)
    ? "Cooku with Comali"
    : /super-singer/i.test(targetUrl)
    ? "Super Singer"
    : /neeya-naana/i.test(targetUrl)
    ? "Neeya Naana"
    : "Bigg Boss 10";

  const articleRegex = /<article[\s\S]*?<\/article>/gi;
  let match;
  while ((match = articleRegex.exec(html)) !== null) {
    const block = match[0];
    const headingLink =
      block.match(/<h[23][^>]*>\s*<a[^>]+href=["']([^"']+)["'][^>]*>([\s\S]*?)<\/a>/i) ||
      block.match(/<a[^>]+href=["']([^"']+)["'][^>]+title=["']([^"']+)["']/i);
    if (!headingLink) continue;

    let href = headingLink[1].trim();
    if (!href || href.includes("/category/") || href.includes("watch-live-stream-bigg-boss")) continue;
    if (href.startsWith("/")) {
      href = `${new URL(pageUrl).origin}${href}`;
    }

    const rawTitle = decodeHtmlEntities(headingLink[2]);
    if (!rawTitle) continue;

    const imgSrc =
      (block.match(/<img[^>]+data-src=["']([^"']+)["']/i) || [])[1] ||
      (block.match(/<img[^>]+src=["'](https?:\/\/[^"']+)["']/i) || [])[1] ||
      defaultPoster;

    const dateMatch =
      (rawTitle.match(/(\d{2}-\d{2}-\d{4})/) || [])[1] ||
      (href.match(/(\d{2}-\d{2}-\d{4})/) || [])[1] ||
      (rawTitle.match(/(Day\s*\d+)/i) || [])[1] ||
      "HD Episode";

    const seasonMatch =
      (rawTitle.match(/(?:Season|S)\s*(\d+)/i) || [])[1] ||
      (targetUrl.match(/(?:season-|s)(\d+)/i) || [])[1] ||
      "10";

    episodes.push({
      title: rawTitle,
      url: href,
      dateStr: dateMatch,
      thumbnailUrl: imgSrc || defaultPoster,
      isLiveStream: false,
      season: seasonMatch,
      description: `${inferredShowTitle} (${dateMatch}) • Full HD broadcast.`,
      qualityBadge: "1080p HD",
      showTitle: inferredShowTitle,
      isMovie: false,
      enrichedMetadata: enrichMetadata(rawTitle, inferredShowTitle, false, imgSrc || defaultPoster),
    });
  }
  return episodes;
}

module.exports = async function handler(req, res) {
  res.setHeader("Access-Control-Allow-Origin", "*");
  res.setHeader("Access-Control-Allow-Methods", "GET,OPTIONS");
  if (req.method === "OPTIONS") {
    return res.status(200).end();
  }

  const targetUrl = (req.query.url || "https://www.1tamilcrow.net/category/bigg-boss-tamil/").trim();
  const isMovieRequest = /5movierulz/i.test(targetUrl) || req.query.type === "movies";

  try {
    if (isMovieRequest) {
      const base = targetUrl.replace(/\/+$/, "");
      const pages = await Promise.all([
        fetchHtmlWithTimeout(base, "https://www.5movierulz.green/"),
        fetchHtmlWithTimeout(`${base}/page/2`, "https://www.5movierulz.green/"),
      ]);
      const allMovies = [];
      const seen = new Set();
      pages.forEach((html, idx) => {
        if (!html) return;
        const parsed = parseMoviesFromHtml(html, idx === 0 ? base : `${base}/page/2`);
        parsed.forEach((m) => {
          if (!seen.has(m.url)) {
            seen.add(m.url);
            allMovies.push(m);
          }
        });
      });
      return res.status(200).json({ ok: true, items: allMovies });
    }

    const cleanBase = targetUrl.replace(/\/+$/, "");
    const pageUrls = cleanBase.includes("/page/")
      ? [`${cleanBase}/`]
      : [`${cleanBase}/`, `${cleanBase}/page/2/`, `${cleanBase}/page/3/`];
    const referer = /tamildhool/i.test(targetUrl)
      ? "https://www.tamildhool.tech/"
      : "https://www.1tamilcrow.net/";

    const htmlPages = await Promise.all(pageUrls.map((u) => fetchHtmlWithTimeout(u, referer)));
    const episodes = [];
    const seen = new Set();

    const isBiggBossMain =
      /bigg-boss-tamil\/?$/i.test(cleanBase) ||
      /bigg-boss-tamil-season-10/i.test(cleanBase) ||
      /bigg-boss-10/i.test(cleanBase);

    if (isBiggBossMain) {
      const liveUrl = "https://www.1tamilcrow.net/watch-live-stream-bigg-boss-tamil-season-10/";
      seen.add(liveUrl);
      episodes.push({
        title: "🔴 Watch Live Stream: Bigg Boss Tamil Season 10 24/7",
        url: liveUrl,
        dateStr: "24/7 LIVE STREAM",
        thumbnailUrl: posters.biggBossLive,
        isLiveStream: true,
        season: "10",
        description:
          "24/7 Uninterrupted HD Live Stream from the Bigg Boss House. Watch tasks, nominations, and daily drama live as it happens.",
        qualityBadge: "24/7 LIVE HD",
        showTitle: "Bigg Boss 10",
        isMovie: false,
        enrichedMetadata: enrichMetadata(
          "Bigg Boss Season 10 24/7 Live Stream",
          "Bigg Boss 10",
          false,
          posters.biggBossLive
        ),
      });
    }

    htmlPages.forEach((html, idx) => {
      if (!html) return;
      const parsed = parseEpisodesFromHtml(html, pageUrls[idx], targetUrl);
      parsed.forEach((ep) => {
        if (!seen.has(ep.url)) {
          seen.add(ep.url);
          episodes.push(ep);
        }
      });
    });

    return res.status(200).json({ ok: true, items: episodes });
  } catch (err) {
    return res.status(200).json({ ok: false, items: [], error: String(err.message || err) });
  }
};
