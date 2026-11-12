const http = require("http");
const fs = require("fs");
const path = require("path");
const url = require("url");

const catalogHandler = require("./api/catalog");
const streamHandler = require("./api/stream");
const proxyHandler = require("./api/proxy");

const PORT = process.env.PORT || 3000;
const PUBLIC_DIR = path.join(__dirname, "public");

const MIME_TYPES = {
  ".html": "text/html; charset=utf-8",
  ".css": "text/css; charset=utf-8",
  ".js": "application/javascript; charset=utf-8",
  ".json": "application/json; charset=utf-8",
  ".png": "image/png",
  ".jpg": "image/jpeg",
  ".jpeg": "image/jpeg",
  ".webp": "image/webp",
  ".svg": "image/svg+xml",
};

function createVercelRes(res) {
  res.status = (code) => {
    res.statusCode = code;
    return res;
  };
  res.json = (obj) => {
    res.setHeader("Content-Type", "application/json; charset=utf-8");
    res.end(JSON.stringify(obj));
    return res;
  };
  res.send = (body) => {
    if (Buffer.isBuffer(body) || typeof body === "string") {
      res.end(body);
    } else {
      res.end(String(body));
    }
    return res;
  };
  return res;
}

const server = http.createServer(async (req, res) => {
  const parsed = url.parse(req.url, true);
  req.query = parsed.query || {};
  const vRes = createVercelRes(res);

  if (parsed.pathname === "/api/catalog") {
    return catalogHandler(req, vRes);
  }
  if (parsed.pathname === "/api/stream") {
    return streamHandler(req, vRes);
  }
  if (parsed.pathname === "/api/proxy") {
    return proxyHandler(req, vRes);
  }

  let filePath = path.join(PUBLIC_DIR, parsed.pathname === "/" ? "index.html" : parsed.pathname);
  if (!filePath.startsWith(PUBLIC_DIR)) {
    res.statusCode = 403;
    return res.end("Forbidden");
  }

  if (!fs.existsSync(filePath) || fs.statSync(filePath).isDirectory()) {
    filePath = path.join(PUBLIC_DIR, "index.html");
  }

  const ext = path.extname(filePath).toLowerCase();
  res.setHeader("Content-Type", MIME_TYPES[ext] || "application/octet-stream");
  fs.createReadStream(filePath).pipe(res);
});

if (require.main === module) {
  server.listen(PORT, () => {
    console.log(`Rasagulla Web & TV server listening on http://localhost:${PORT}`);
  });
}

module.exports = server;
