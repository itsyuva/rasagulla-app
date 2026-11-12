const fs = require("fs");
const path = require("path");

const ALLOWED_FILES = new Set([
  "img_cutout_anirudh.png",
  "img_cutout_chef.png",
  "img_cutout_gopinath.png",
  "img_cutout_kamal.png",
  "img_cutout_rooney.png",
  "img_cutout_samantha.png",
  "img_cutout_vijay.png",
  "img_cutout_vijaysethupathi.png",
  "img_rasagulla_icon.png",
  "img_rasagulla_logo.png",
]);

module.exports = function handler(req, res) {
  let raw = req.query.file || req.query.name || "";
  if (!raw && req.url) {
    const cleanPath = req.url.split("?")[0];
    raw = path.basename(cleanPath);
  }
  let file = path.basename(raw);
  if (file && !file.endsWith(".png")) {
    file = `${file}.png`;
  }

  if (!ALLOWED_FILES.has(file)) {
    res.statusCode = 404;
    return res.end("Not found");
  }

  const candidatePaths = [
    path.join(process.cwd(), "public", "assets", "cutouts", file),
    path.join(process.cwd(), "app", "src", "main", "res", "drawable", file),
  ];

  for (const p of candidatePaths) {
    if (fs.existsSync(p)) {
      res.setHeader("Content-Type", "image/png");
      res.setHeader("Cache-Control", "public, max-age=31536000, immutable");
      return fs.createReadStream(p).pipe(res);
    }
  }

  res.statusCode = 404;
  return res.end("Image not found");
};
