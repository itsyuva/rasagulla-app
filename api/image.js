const USER_AGENT =
  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36";

function generatePosterSvg(title = "Rasagulla Cinema", subtitle = "1080P FULL HD") {
  const safeTitle = String(title)
    .replace(/[&<>'"]/g, "")
    .slice(0, 42);
  const safeSub = String(subtitle)
    .replace(/[&<>'"]/g, "")
    .slice(0, 32);

  return `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 360" width="640" height="360">
    <defs>
      <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stop-color="#141B2D"/>
        <stop offset="50%" stop-color="#0B0F19"/>
        <stop offset="100%" stop-color="#2A0B18"/>
      </linearGradient>
      <radialGradient id="glow" cx="80%" cy="20%" r="60%">
        <stop offset="0%" stop-color="#FF2A54" stop-opacity="0.38"/>
        <stop offset="100%" stop-color="#000000" stop-opacity="0"/>
      </radialGradient>
    </defs>
    <rect width="640" height="360" fill="url(#bg)"/>
    <rect width="640" height="360" fill="url(#glow)"/>
    <rect x="28" y="28" width="118" height="28" rx="6" fill="#E50914"/>
    <text x="87" y="47" fill="#FFFFFF" font-family="system-ui, -apple-system, sans-serif" font-size="12" font-weight="800" text-anchor="middle" letter-spacing="1">${safeSub}</text>
    <text x="32" y="275" fill="#00E5FF" font-family="system-ui, -apple-system, sans-serif" font-size="14" font-weight="800" letter-spacing="1.5">RASAGULLA STREAMING</text>
    <text x="32" y="312" fill="#FFFFFF" font-family="system-ui, -apple-system, sans-serif" font-size="24" font-weight="900">${safeTitle}</text>
  </svg>`;
}

module.exports = async function handler(req, res) {
  res.setHeader("Access-Control-Allow-Origin", "*");
  const rawUrl = (req.query.url || "").trim();
  const title = req.query.title || "Rasagulla 4K";
  const show = req.query.show || "1080P HD";

  if (!rawUrl || !/^https?:\/\//i.test(rawUrl)) {
    res.setHeader("Content-Type", "image/svg+xml; charset=utf-8");
    res.setHeader("Cache-Control", "public, max-age=86400");
    return res.status(200).send(generatePosterSvg(title, show));
  }

  const referer = /tamildhool/i.test(rawUrl)
    ? "https://www.tamildhool.tech/"
    : /5movierulz/i.test(rawUrl)
    ? "https://www.5movierulz.green/"
    : "https://www.1tamilcrow.net/";

  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), 5000);

  try {
    const fetchTarget = /tamildhool\.tech/i.test(rawUrl)
      ? `https://wsrv.nl/?url=${encodeURIComponent(rawUrl)}`
      : rawUrl;

    let upstream = await fetch(fetchTarget, {
      headers: {
        "User-Agent": USER_AGENT,
        Referer: referer,
        Accept: "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8",
      },
      redirect: "follow",
      signal: controller.signal,
    });

    if ((!upstream.ok || !(upstream.headers.get("content-type") || "").startsWith("image/")) && fetchTarget === rawUrl) {
      upstream = await fetch(`https://wsrv.nl/?url=${encodeURIComponent(rawUrl)}`, {
        headers: { "User-Agent": USER_AGENT },
        redirect: "follow",
        signal: controller.signal,
      });
    }

    const contentType = upstream.headers.get("content-type") || "";
    if (!upstream.ok || !contentType.startsWith("image/")) {
      res.setHeader("Content-Type", "image/svg+xml; charset=utf-8");
      res.setHeader("Cache-Control", "public, max-age=86400");
      return res.status(200).send(generatePosterSvg(title, show));
    }

    const arrayBuffer = await upstream.arrayBuffer();
    res.setHeader("Content-Type", contentType);
    res.setHeader("Cache-Control", "public, max-age=86400, s-maxage=86400");
    return res.status(200).send(Buffer.from(arrayBuffer));
  } catch {
    res.setHeader("Content-Type", "image/svg+xml; charset=utf-8");
    res.setHeader("Cache-Control", "public, max-age=3600");
    return res.status(200).send(generatePosterSvg(title, show));
  } finally {
    clearTimeout(timer);
  }
};
