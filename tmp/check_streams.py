import urllib.request, re

urls = [
    "https://www.1tamilcrow.net/bigg-boss-10-tamil-06-09-2026-grand-launch-vijay-tv-show/",
    "https://www.1tamilcrow.net/watch-live-stream-bigg-boss-tamil-season-10/"
]

headers = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
}

for url in urls:
    print("=" * 60)
    print("FETCHING:", url)
    try:
        req = urllib.request.Request(url, headers=headers)
        html = urllib.request.urlopen(req, timeout=10).read().decode("utf-8", "ignore")
        print("HTML length:", len(html))
        
        # iframes
        iframes = re.findall(r'<iframe[^>]*src=[\'"]([^\'"]+)[\'"]', html, re.IGNORECASE)
        print("Iframes:", iframes)
        
        # look for video tags
        video_tags = re.findall(r'<video[^>]*src=[\'"]([^\'"]+)[\'"]', html, re.IGNORECASE)
        print("Video tags:", video_tags)

        # find links in main content / entry content
        links = re.findall(r'<a\s+[^>]*href=[\'"]([^\'"]+)[\'"][^>]*>(.*?)</a>', html, re.DOTALL | re.IGNORECASE)
        print(f"Total links: {len(links)}")
        for href, text in links:
            clean = re.sub(r'<[^>]+>', ' ', text).strip()
            if any(k in href.lower() for k in ['embed', 'player', 'video', 'stream', 'live', 'dailymotion', 'b-cdn']) or any(k in clean.lower() for k in ['watch', 'server', 'player', 'stream', 'part']):
                print(f"  LINK: {href} -> {clean[:50]}")
    except Exception as e:
        print("Error:", e)
