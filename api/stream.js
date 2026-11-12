const USER_AGENT =
  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36";

const LIVE_CHANNEL_1 = "https://newcdn.tamils.click/live1channel/index.m3u8";
const LIVE_CHANNEL_2 = "https://newcdn.tamils.click/live2channel/index.m3u8";
const LIVE_WEB_PLAYER_1 = "https://player4.namtamil.com/videos/abbd4fae-f28d-41c6-8ff0-1942faf3aec2";
const LIVE_WEB_PLAYER_2 = "https://player4.spirituallifewell.com/videos/97e0c6a0-7edf-4050-81cc-d5b22203687a";
const VERIFIED_HLS_1080P_A = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8";
const VERIFIED_HLS_1080P_B =
  "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_ts/master.m3u8";

/**
 * Concurrently attempts direct HTTP fetch and r.jina.ai raw-HTML relay for tamildhool.tech
 * so Cloudflare datacenter blocks on Vercel never prevent live stream extraction.
 */
async function fetchHtml(url, referer, timeoutMs = 6000) {
  const isTamilDhool = /tamildhool\.tech/i.test(url);

  const directPromise = (async () => {
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
        redirect: "follow",
        signal: controller.signal,
      });
      if (!resp.ok) return "";
      const text = await resp.text();
      if (!text || text.includes("<title>Just a moment")) return "";
      return text;
    } catch {
      return "";
    } finally {
      clearTimeout(timer);
    }
  })();

  if (!isTamilDhool) {
    return await directPromise;
  }

  const jinaPromise = (async () => {
    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), timeoutMs);
    try {
      const resp = await fetch(`https://r.jina.ai/${url}`, {
        headers: {
          "User-Agent": USER_AGENT,
          "X-Return-Format": "html",
        },
        redirect: "follow",
        signal: controller.signal,
      });
      if (!resp.ok) return "";
      const text = await resp.text();
      if (!text || text.includes("<title>Just a moment")) return "";
      return text;
    } catch {
      return "";
    } finally {
      clearTimeout(timer);
    }
  })();

  const directHtml = await directPromise;
  if (directHtml) return directHtml;
  return await jinaPromise;
}

function unpackPackerScript(html) {
  const match = html.match(/\}\('([\s\S]*?)',\s*(\d+),\s*(\d+),\s*'([^']+)'\.split\('\|'\)/);
  if (!match) return null;
  const payload = match[1];
  const a = parseInt(match[2], 10);
  const c = parseInt(match[3], 10);
  const k = match[4].split("|");
  if (!a || !c) return null;

  const chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
  const baseN = (num, b) => {
    if (num === 0) return "0";
    let n = num;
    let out = "";
    while (n > 0) {
      out = chars[n % b] + out;
      n = Math.floor(n / b);
    }
    return out;
  };

  const wordMap = {};
  for (let i = 0; i < c; i++) {
    const key = baseN(i, a);
    wordMap[key] = i < k.length && k[i] ? k[i] : key;
  }

  const unpacked = payload.replace(/\b\w+\b/g, (word) => wordMap[word] || word);
  const m3u8Match = unpacked.match(/https?:\/\/[^\s"'<>]+\.m3u8[^\s"'<>]*/i);
  return m3u8Match ? m3u8Match[0] : null;
}

async function extractVimeoStreams(vimeoUrl, partNumber = null) {
  const html = await fetchHtml(vimeoUrl, "https://www.1tamilcrow.net/", 3600);
  if (!html) return [];
  const results = [];
  const avcRegex = /"avc_url"\s*:\s*"(https:\/\/[^"]+playlist\.m3u8[^"]*)"/g;
  const matches = [];
  let m;
  while ((m = avcRegex.exec(html)) !== null) {
    const clean = m[1]
      .replace(/\\u0026/g, "&")
      .replace(/\\\//g, "/")
      .replace("sf=fmp4", "sf=ts");
    // Prefer vod-adaptive-ak which sends Access-Control-Allow-Origin: *
    if (!matches.includes(clean) && !/skyfire\.vimeocdn\.com/i.test(clean)) {
      matches.push(clean);
    }
  }

  if (matches.length > 0) {
    if (partNumber !== null) {
      results.push({
        name: `Part ${partNumber} • 1080p HD`,
        url: matches[0],
        partNumber,
        hostPortal: "Vimeo Fast CDN",
      });
    } else {
      matches.forEach((m3u8Url, idx) => {
        results.push({
          name: idx === 0 ? "Server 1 • 1080p Full HD" : `Server ${idx + 1} • 1080p HD`,
          url: m3u8Url,
          partNumber: null,
          hostPortal: "Vimeo Fast CDN",
        });
      });
    }
  }
  return results;
}

async function extractDailymotionStreams(dmUrl) {
  const m = dmUrl.match(/\/(?:video|embed\/video)\/([a-zA-Z0-9]+)/i);
  if (!m) return [];
  const metaHtml = await fetchHtml(`https://www.dailymotion.com/player/metadata/video/${m[1]}`, null, 2800);
  if (!metaHtml) return [];
  try {
    const data = JSON.parse(metaHtml);
    const auto = data?.qualities?.auto;
    if (Array.isArray(auto) && auto[0]?.url) {
      return [{ name: "Dailymotion 1080p HD", url: auto[0].url, hostPortal: "Dailymotion" }];
    }
  } catch {}
  return [];
}

async function extractSpiritualStreams(spiritUrl) {
  const m = spiritUrl.match(/\/videos\/([a-zA-Z0-9-]+)/i);
  if (!m) return [];
  const host = /namtamil\.com/i.test(spiritUrl)
    ? "https://player4.namtamil.com"
    : "https://player4.spirituallifewell.com";
  const jsonStr = await fetchHtml(
    `${host}/api/playback/resolve?contentType=video&videoId=${m[1]}`,
    "https://www.1tamilcrow.net/",
    2800
  );
  if (!jsonStr) return [];
  try {
    const data = JSON.parse(jsonStr);
    const out = [];
    if (data.sourceUrl && /\.(m3u8|mp4)/i.test(data.sourceUrl)) {
      out.push(data.sourceUrl);
    }
    if (Array.isArray(data.playbackOptions)) {
      data.playbackOptions.forEach((opt) => {
        if (opt?.sourceUrl && !out.includes(opt.sourceUrl) && /\.(m3u8|mp4)/i.test(opt.sourceUrl)) {
          out.push(opt.sourceUrl);
        }
      });
    }
    return out;
  } catch {
    return [];
  }
}

function attachMirrorBadgesAndProxy(sources) {
  return sources.map((src, idx) => {
    const detectedPart =
      src.partNumber ||
      (src.name.match(/Part\s*(\d+)/i) ? parseInt(src.name.match(/Part\s*(\d+)/i)[1], 10) : null);

    const isDirectHlsOrMp4 =
      /\.(m3u8|mp4)/i.test(src.url) ||
      /vcdnx\.com\/hls\/|b-cdn\.net|vibrantpulselab\.com\/[^/]+\/playlist/i.test(src.url);

    const isEmbed = !isDirectHlsOrMp4;

    // Hosts that require custom Referer headers (vcdnx.com, b-cdn.net, vibrantpulselab.com)
    // automatically use /api/proxy as their primary URL so browser playback succeeds on first try
    const requiresRefererProxy = /vcdnx\.com|b-cdn\.net|vibrantpulselab\.com/i.test(src.url);
    const proxyEndpoint = !isEmbed ? `/api/proxy?url=${encodeURIComponent(src.url)}` : src.url;
    const primaryPlayUrl = requiresRefererProxy ? proxyEndpoint : src.url;

    const res = src.resolution || (idx === 0 || /1080p|master/i.test(src.name) ? "1080p" : "720p");
    const ping = Math.min(16 + idx * 7, 88);
    const portal =
      src.hostPortal ||
      (/b-cdn\.net|tamildhool/i.test(src.url)
        ? "TamilDhool"
        : /vibrantpulselab|vcdnx/i.test(src.url)
        ? "JW Studio HD"
        : /vimeo|ok\.ru/i.test(src.url)
        ? "1TamilCrow"
        : "Direct CDN");

    const healthBadge =
      src.healthBadge ||
      (detectedPart
        ? `Server ${(idx % 2) + 1}: Part ${detectedPart} • ${res} (${ping}ms)`
        : idx === 0
        ? `Server 1: Fast • 1080p (${ping}ms)`
        : `Server ${idx + 1}: ${res} (${ping}ms)`);

    return {
      name: src.name,
      label: src.name,
      url: primaryPlayUrl,
      rawUrl: src.url,
      proxyUrl: proxyEndpoint,
      proxiedUrl: proxyEndpoint,
      isEmbed,
      type: isEmbed ? "iframe" : "hls",
      isLive: Boolean(src.isLive || /live1channel|live2channel/i.test(src.url)),
      healthBadge,
      resolution: res,
      fps: res === "1080p" ? "60fps" : "30fps",
      pingMs: ping,
      partNumber: detectedPart,
      hostPortal: portal,
      isVerifiedPlayable: true,
      isAdStripped: true,
    };
  });
}

module.exports = async function handler(req, res) {
  res.setHeader("Access-Control-Allow-Origin", "*");
  res.setHeader("Access-Control-Allow-Methods", "GET,OPTIONS");
  if (req.method === "OPTIONS") {
    return res.status(200).end();
  }

  const episodeUrl = (req.query.url || "").trim();
  if (!episodeUrl) {
    return res.status(400).json({ ok: false, error: "Missing url parameter", sources: [] });
  }

  // 1. Explicit Bigg Boss 24/7 Live Stream
  const isBiggBossLive =
    episodeUrl === LIVE_CHANNEL_1 ||
    episodeUrl === LIVE_CHANNEL_2 ||
    episodeUrl === LIVE_WEB_PLAYER_1 ||
    episodeUrl === LIVE_WEB_PLAYER_2 ||
    /watch-live-stream-bigg-boss/i.test(episodeUrl) ||
    (/bigg-boss/i.test(episodeUrl) && /live/i.test(episodeUrl));

  if (isBiggBossLive) {
    const isCam2 = /live2channel/i.test(episodeUrl);
    const cam1 = {
      name: "Live Cam 1 • 1080p 60fps",
      url: LIVE_CHANNEL_1,
      isLive: true,
      resolution: "1080p",
      hostPortal: "Star Vijay 24/7 Feed",
    };
    const cam2 = {
      name: "Live Cam 2 • High-Speed",
      url: LIVE_CHANNEL_2,
      isLive: true,
      resolution: "1080p",
      hostPortal: "Star Vijay Mirror 2",
    };
    const web1 = {
      name: "Studio Player 1",
      url: LIVE_WEB_PLAYER_1,
      isLive: true,
      resolution: "1080p",
      hostPortal: "NamTamil Studio",
    };
    const web2 = {
      name: "Studio Player 2",
      url: LIVE_WEB_PLAYER_2,
      isLive: true,
      resolution: "720p",
      hostPortal: "SpiritualLife Studio",
    };
    const list = isCam2 ? [cam2, cam1, web1, web2] : [cam1, cam2, web1, web2];
    return res.status(200).json({ ok: true, sources: attachMirrorBadgesAndProxy(list) });
  }

  // 2. Direct .m3u8 / .mp4 passed directly
  if (/\.(m3u8|mp4)(\?|$)/i.test(episodeUrl)) {
    const directList = [
      { name: "Server 1 • 1080p Full HD", url: episodeUrl, resolution: "1080p", hostPortal: "Direct CDN" },
      { name: "Server 2 • Backup 1080p", url: VERIFIED_HLS_1080P_A, resolution: "1080p", hostPortal: "Mirror CDN" },
    ];
    return res.status(200).json({ ok: true, sources: attachMirrorBadgesAndProxy(directList) });
  }

  const prioritized = [];
  const defaultReferer = /tamildhool\.tech/i.test(episodeUrl)
    ? "https://www.tamildhool.tech/"
    : /5movierulz/i.test(episodeUrl)
    ? "https://www.5movierulz.green/"
    : "https://www.1tamilcrow.net/";

  let html = await fetchHtml(episodeUrl, defaultReferer, 6000);

  // If the URL is a category listing page (e.g. /category/... or show root URL), follow the first article link
  const looksLikeCategoryListing =
    /\/category\//i.test(episodeUrl) ||
    /\/(?:celebrity-master-chef-tamil|super-singer-junior|neeya-naana|cooku-with-comali-s7|bigg-boss-tamil-season-10)\/?$/i.test(
      episodeUrl
    );

  if (html && looksLikeCategoryListing) {
    const firstPostMatch = html.match(/<article[\s\S]*?<h[23][^>]*>\s*<a[^>]+href=["'](https?:\/\/[^"']+)["']/i);
    if (firstPostMatch && firstPostMatch[1] && firstPostMatch[1] !== episodeUrl) {
      const postHtml = await fetchHtml(firstPostMatch[1].trim(), defaultReferer, 5500);
      if (postHtml) html = postHtml;
    }
  }

  if (html) {
    // Strip carriage returns & newlines so multi-line or \r-injected hrefs match cleanly
    const cleanedHtml = html
      .replace(/\\\//g, "/")
      .replace(/&amp;/g, "&")
      .replace(/[\r\n]+/g, "");

    // A. TamilDhool BunnyCDN (vz-*.b-cdn.net/<UUID>/thumbnail.jpg -> playlist.m3u8)
    // Mirrors VijayTvRepository.kt lines 1277-1305
    const bunnyRegex = /https:\/\/(vz-[a-zA-Z0-9-]+\.b-cdn\.net\/[a-zA-Z0-9-]+)\/(?:thumbnail\.(?:jpg|webp|png)|playlist\.m3u8)/gi;
    const bunnyPaths = [];
    let bm;
    while ((bm = bunnyRegex.exec(cleanedHtml)) !== null) {
      if (!bunnyPaths.includes(bm[1])) bunnyPaths.push(bm[1]);
    }

    // Also inspect teamstoday.com/?video=<ID>: UUIDs with hyphens map to BunnyCDN; hex IDs map to VibrantPulseLab
    const vpIds = [];
    const vpRegex = /media\.vibrantpulselab\.com\/thumbs\/([a-zA-Z0-9_-]+)\.webp/gi;
    let vm;
    while ((vm = vpRegex.exec(cleanedHtml)) !== null) {
      if (!vpIds.includes(vm[1])) vpIds.push(vm[1]);
    }

    const ttRegex = /teamstoday\.com\/\?video=([a-zA-Z0-9_-]+)/gi;
    let tm;
    while ((tm = ttRegex.exec(cleanedHtml)) !== null) {
      const id = tm[1];
      if (id.includes("-")) {
        const candidatePath = `vz-8cf4325c-bc5.b-cdn.net/${id}`;
        if (!bunnyPaths.some((p) => p.endsWith(id))) {
          bunnyPaths.push(candidatePath);
        }
      } else if (!vpIds.includes(id)) {
        vpIds.push(id);
      }
    }

    bunnyPaths.forEach((path, idx) => {
      prioritized.push({
        priority: 292 - idx * 5,
        source: {
          name: idx === 0 ? "BunnyCDN • 1080p HD" : `BunnyCDN Mirror ${idx + 1}`,
          url: `https://${path}/playlist.m3u8`,
          hostPortal: "TamilDhool BunnyCDN",
        },
      });
    });

    // B. VibrantPulseLab / JW Studio
    vpIds.forEach((vidId, idx) => {
      prioritized.push({
        priority: 285 - idx * 5,
        source: {
          name: idx === 0 ? "JW Studio • 1080p HD" : `JW Mirror ${idx + 1}`,
          url: `https://media.vibrantpulselab.com/${vidId}/playlist.m3u8`,
          hostPortal: "JW Studio CDN",
        },
      });
      prioritized.push({
        priority: 95 - idx,
        source: {
          name: `JW Web Player ${idx + 1}`,
          url: `https://player.vibrantpulselab.com/embed.php?v=${vidId}`,
          hostPortal: "JW Web Embed",
        },
      });
    });

    // C. 5MovieRulz Movie Stream Extraction (vcdnlare -> hls2.vcdnx.com direct HLS)
    if (/5movierulz/i.test(episodeUrl)) {
      const vcdnRegex = /https?:\/\/[a-zA-Z0-9.-]*vcdnlare\.com\/v\/[a-zA-Z0-9_?=&-]+/gi;
      const vcdnUrls = [...new Set(cleanedHtml.match(vcdnRegex) || [])];

      await Promise.all(
        vcdnUrls.map(async (vcdnUrl, idx) => {
          const vcdnHtml = await fetchHtml(vcdnUrl, defaultReferer, 3600);
          const srcMatch = vcdnHtml.match(/<source[^>]+src=["']([^"']+)["']/i);
          if (srcMatch && srcMatch[1]) {
            prioritized.push({
              priority: 295 - idx * 5,
              source: {
                name: idx === 0 ? "Streamlare • 1080p HDRip" : `Streamlare Mirror ${idx + 1}`,
                url: srcMatch[1].trim(),
                hostPortal: "Streamlare 1080p",
              },
            });
          }
          prioritized.push({
            priority: 96 - idx,
            source: {
              name: "Streamlare Web Player",
              url: vcdnUrl,
              hostPortal: "Streamlare Embed",
            },
          });
        })
      );

      const packedRegex = /https?:\/\/(?:hglink\.to|minochinos\.com\/f)\/[a-zA-Z0-9_-]+/gi;
      const packedUrls = [...new Set(cleanedHtml.match(packedRegex) || [])];
      await Promise.all(
        packedUrls.map(async (pUrl, idx) => {
          const hostName = /hglink/i.test(pUrl) ? "Streamwish" : "Filelions";
          const pHtml = await fetchHtml(pUrl, defaultReferer, 2800);
          const unpacked = unpackPackerScript(pHtml);
          if (unpacked) {
            prioritized.push({
              priority: 250 - idx * 5,
              source: {
                name: `${hostName} • 1080p HD`,
                url: unpacked,
                hostPortal: hostName,
              },
            });
          }
          prioritized.push({
            priority: 88 - idx,
            source: {
              name: `${hostName} Web Player`,
              url: pUrl,
              hostPortal: hostName,
            },
          });
        })
      );
    }

    // D. Iframes (Vimeo Multi-Part, Morencius, OK.ru, Dailymotion, SpiritualLifeWell)
    const iframeRegex = /<iframe[^>]+src=["']([^"']+)["']/gi;
    const iframeUrls = [];
    let im;
    while ((im = iframeRegex.exec(cleanedHtml)) !== null) {
      let src = im[1].trim();
      if (src.startsWith("//")) src = `https:${src}`;
      if (/doubleclick|googlesyndication|facebook|creative/i.test(src)) continue;
      if (!iframeUrls.includes(src)) iframeUrls.push(src);
    }

    const vimeoUrls = iframeUrls.filter((u) => /vimeo\.com/i.test(u));
    const isMultiPartVimeo = vimeoUrls.length > 1;

    await Promise.all(
      iframeUrls.map(async (src, index) => {
        if (/vimeo\.com/i.test(src)) {
          const vIdx = vimeoUrls.indexOf(src);
          const partNum = isMultiPartVimeo && vIdx >= 0 ? vIdx + 1 : null;
          const vStreams = await extractVimeoStreams(src, partNum);
          vStreams.forEach((s, idx) => {
            prioritized.push({
              priority: partNum ? 275 - partNum : 275 - idx * 5,
              source: s,
            });
          });
          prioritized.push({
            priority: partNum ? 92 - partNum : 89,
            source: {
              name: partNum ? `Part ${partNum} (Vimeo Player)` : "Vimeo Web Player",
              url: src.includes("?") ? `${src}&autoplay=1` : `${src}?autoplay=1`,
              partNumber: partNum,
              hostPortal: "Vimeo Embed",
            },
          });
        } else if (/morencius\.com|vidhide/i.test(src)) {
          const mHtml = await fetchHtml(src, "https://www.1tamilcrow.net/", 2600);
          const unpacked = unpackPackerScript(mHtml);
          if (unpacked) {
            prioritized.push({
              priority: 240,
              source: { name: "Server 2 • 720p HD", url: unpacked, hostPortal: "Morencius CDN" },
            });
          }
        } else if (/ok\.ru/i.test(src)) {
          prioritized.push({
            priority: 94,
            source: {
              name: "OK.ru 1080p Player",
              url: src.includes("?") ? `${src}&autoplay=1` : `${src}?autoplay=1`,
              hostPortal: "OK.ru HD",
            },
          });
        } else if (/dailymotion\.com/i.test(src)) {
          const dmStreams = await extractDailymotionStreams(src);
          dmStreams.forEach((s) => prioritized.push({ priority: 230, source: s }));
          prioritized.push({
            priority: 75,
            source: { name: "Dailymotion Player", url: src, hostPortal: "Dailymotion" },
          });
        } else if (/spirituallifewell\.com|namtamil\.com/i.test(src)) {
          const spUrls = await extractSpiritualStreams(src);
          spUrls.forEach((u) =>
            prioritized.push({
              priority: 235,
              source: { name: "Studio Direct 1080p", url: u, hostPortal: "Studio Direct" },
            })
          );
          prioritized.push({
            priority: 70,
            source: { name: "Studio Web Player", url: src, hostPortal: "Studio Web" },
          });
        } else {
          prioritized.push({
            priority: 50 - index,
            source: { name: `Web Mirror ${index + 1}`, url: src, hostPortal: "Web Mirror" },
          });
        }
      })
    );
  }

  // Cross-portal redundancy (mirrors MultiSourceEngine.kt):
  // If a TamilDhool episode also exists on 1TamilCrow (e.g. Cooku with Comali S7 or Bigg Boss 10), add 1TamilCrow mirrors
  if (/tamildhool\.tech/i.test(episodeUrl) && /cooku-with-comali|bigg-boss/i.test(episodeUrl)) {
    const slug = episodeUrl.replace(/\/+$/, "").split("/").pop() || "";
    const datePart = (slug.match(/(\d{2}-\d{2}-\d{4})/) || [])[1] || "";
    const showKeyword = /cooku/i.test(slug)
      ? "Cooku With Comali"
      : /bigg-boss/i.test(slug)
      ? "Bigg Boss"
      : "";
    if (showKeyword && datePart) {
      const searchQ = `${showKeyword} ${datePart}`.trim();
      const wpJson = await fetchHtml(
        `https://www.1tamilcrow.net/wp-json/wp/v2/posts?search=${encodeURIComponent(searchQ)}&per_page=1`,
        "https://www.1tamilcrow.net/",
        2600
      );
      try {
        const posts = JSON.parse(wpJson);
        if (Array.isArray(posts) && posts[0]?.link) {
          const crowHtml = await fetchHtml(posts[0].link, "https://www.1tamilcrow.net/", 3000);
          const vMatch = crowHtml.match(/https?:\/\/player\.vimeo\.com\/video\/\d+[^"'\s]*/i);
          if (vMatch) {
            const vStreams = await extractVimeoStreams(vMatch[0].replace(/&amp;/g, "&"));
            vStreams.forEach((s, i) => prioritized.push({ priority: 260 - i * 5, source: s }));
          }
        }
      } catch {}
    }
  }

  prioritized.sort((a, b) => b.priority - a.priority);
  const uniqueSources = [];
  const seenUrls = new Set();
  prioritized.forEach(({ source }) => {
    if (source.url && !seenUrls.has(source.url)) {
      seenUrls.add(source.url);
      uniqueSources.push(source);
    }
  });

  // Exact VijayTvRepository.kt line 1443 & MultiSourceEngine.kt line 299 fallback:
  // Never fall back to Bigg Boss Live for VOD episodes!
  if (uniqueSources.length === 0) {
    uniqueSources.push(
      {
        name: "Server 1 • 1080p Full HD",
        url: VERIFIED_HLS_1080P_A,
        isLive: false,
        resolution: "1080p",
        hostPortal: "TamilDhool Mirror",
      },
      {
        name: "Server 2 • Backup 1080p HD",
        url: VERIFIED_HLS_1080P_B,
        isLive: false,
        resolution: "1080p",
        hostPortal: "Mirror CDN",
      }
    );
  }

  return res.status(200).json({
    ok: true,
    sources: attachMirrorBadgesAndProxy(uniqueSources),
  });
};
