"""Quét www/audio/L<lesson>_<phần>.(mp3|m4a|wav) rồi ghi danh sách vào DEFAULT_AUDIO trong www/index.html."""
import os, re, json

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
AUDIO = os.path.join(ROOT, "www", "audio")
HTML = os.path.join(ROOT, "www", "index.html")

m = {}
for f in sorted(os.listdir(AUDIO)):
    g = re.fullmatch(r"L(\d+)_([135])\.(mp3|m4a|wav)", f, re.I)
    if g:
        m.setdefault(str(int(g.group(1))), {})[g.group(2)] = "audio/" + f

s = open(HTML, encoding="utf-8").read()
new = "const DEFAULT_AUDIO=" + json.dumps(m, separators=(",", ":")) + ";"
s2, n = re.subn(r"const DEFAULT_AUDIO=\{.*?\};", lambda _: new, s, count=1, flags=re.S)
assert n == 1, "Không tìm thấy DEFAULT_AUDIO trong index.html"
open(HTML, "w", encoding="utf-8").write(s2)
print("Đã nhúng", sum(len(v) for v in m.values()), "file audio cho", len(m), "lesson")
