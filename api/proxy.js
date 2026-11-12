const USER_AGENT =
  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36";

function inferRefererAndOrigin(targetUrl) {
  if (/vibrantpulselab\.com/i.test(targetUrl)) {
    return {
      referer: "https://player.vibrantpulselab.com/",
      origin: "https://player.vibrantpulselab.com",
    };
  }
  if (/b-cdn\.net|tamildhool/i.test(targetUrl)) {
    return {
      referer: "https://www.tamildhool.tech/",
      origin: "https://www.tamildhool.tech",
    };
  }
  if (/vcdnx\.com|vcdnlare\.com/i.test(targetUrl)) {
    return {
      referer: "https://ww7.vcdnlare.com/",
      origin: "https://ww7.vcdnlare.com",
    };
  }
  if (/okcdn\.ru|ok\.ru/i.test(targetUrl)) {
    return {
      referer: "https://ok.ru/",
      origin: "https://ok.ru",
    };
  }
  if (/vimeocdn\.com|vimeo\.com/i.test(targetUrl)) {
    return {
      referer: "https://player.vimeo.com/",
      origin: "https://player.vimeo.com",
    };
  }
  return {
    referer: "https://www.1tamilcrow.net/",
    origin: "https://www.1tamilcrow.net",
  };
}

module.exports = async function handler(req, res) {
  res.setHeader("Access-Control-Allow-Origin", "*");
  res.setHeader("Access-Control-Allow-Methods", "GET,OPTIONS,HEAD");
  res.setHeader("Access-Control-Allow-Headers", "Range, Content-Type, Accept");
  if (req.method === "OPTIONS") {
    return res.status(200).end();
  }

  const rawUrl = (req.query.url || "").trim();
  if (!rawUrl || !/^https?:\/\//i.test(rawUrl)) {
    return res.status(400).send("Invalid proxy URL");
  }

  const { referer, origin } = inferRefererAndOrigin(rawUrl);
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), 9500);

  try {
    const headers = {
      "User-Agent": USER_AGENT,
      Referer: referer,
      Origin: origin,
      Accept: "*/*",
    };
    if (req.headers.range) {
      headers.Range = req.headers.range;
    }

    const upstream = await fetch(rawUrl, {
      headers,
      redirect: "follow",
      signal: controller.signal,
    });

    if (!upstream.ok && upstream.status !== 206) {
      return res.status(upstream.status).send(`Upstream error ${upstream.status}`);
    }

    const finalUrl = upstream.url || rawUrl;
    const contentType = upstream.headers.get("content-type") || "";
    const isM3u8 =
      /\.m3u8(\?|$)/i.test(finalUrl) ||
      /mpegurl|vnd\.apple\.mpegurl/i.test(contentType);

    if (isM3u8) {
      const text = await upstream.text();
      if (text.trim().startsWith("#EXTM3U")) {
        const baseObj = new URL(finalUrl);
        const rewritten = text
          .split(/\r?\n/)
          .map((line) => {
            const trimmed = line.trim();
            if (!trimmed) return line;
            if (trimmed.startsWith("#")) {
              return line.replace(/URI=["']([^"']+)["']/gi, (_, uriVal) => {
                const absUri = new URL(uriVal, baseObj).toString();
                return `URI="/api/proxy?url=${encodeURIComponent(absUri)}"`;
              });
            }
            const absLineUrl = new URL(trimmed, baseObj).toString();
            return `/api/proxy?url=${encodeURIComponent(absLineUrl)}`;
          })
          .join("\n");

        res.setHeader("Content-Type", "application/vnd.apple.mpegurl; charset=utf-8");
        res.setHeader("Cache-Control", "public, max-age=4");
        return res.status(200).send(rewritten);
      }
    }

    const arrayBuffer = await upstream.arrayBuffer();
    let buffer = Buffer.from(arrayBuffer);

    // SmartHlsDataSource TS Sync-Byte Alignment (strips disguised PNG/JPG headers before 0x47 sync bytes)
    if (buffer.length >= 377 && buffer[0] !== 0x47) {
      const maxScan = Math.min(buffer.length - 376, 2048);
      for (let offset = 1; offset < maxScan; offset++) {
        if (
          buffer[offset] === 0x47 &&
          buffer[offset + 188] === 0x47 &&
          buffer[offset + 376] === 0x47
        ) {
          buffer = buffer.subarray(offset);
          res.setHeader("Content-Type", "video/mp2t");
          return res.status(200).send(buffer);
        }
      }
    }

    res.setHeader("Content-Type", contentType || "video/mp2t");
    return res.status(upstream.status).send(buffer);
  } catch (err) {
    return res.status(502).send(`Proxy error: ${err.message || err}`);
  } finally {
    clearTimeout(timer);
  }
};
