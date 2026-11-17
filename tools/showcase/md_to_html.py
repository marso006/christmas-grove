"""Converts docs/showcase/description.md to HTML for CurseForge's visual editor (paste via its
"Source" / HTML view). Handles the subset the description uses: headings, rules, paragraphs, bullet
lists, images, links, bold, italics and inline code.

Usage: py tools/showcase/md_to_html.py
"""
from __future__ import annotations

import html
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SRC = ROOT / "docs/showcase/description.md"
OUT = ROOT / "docs/showcase/description.html"


def inline(text: str) -> str:
    text = html.escape(text, quote=False)
    text = re.sub(r"`([^`]+)`", r"<code>\1</code>", text)
    text = re.sub(r"\*\*([^*]+)\*\*", r"<strong>\1</strong>", text)
    text = re.sub(r"(?<![*\w])\*([^*]+)\*(?!\w)", r"<em>\1</em>", text)
    text = re.sub(r"\[([^\]]+)\]\(([^)]+)\)", r'<a href="\2" rel="nofollow">\1</a>', text)
    return text


def convert(md: str) -> str:
    out: list[str] = []
    para: list[str] = []
    items: list[str] = []

    def flush():
        if para:
            out.append("<p>" + inline(" ".join(para)) + "</p>")
            para.clear()
        if items:
            out.append("<ul>\n" + "\n".join(f"  <li>{inline(i)}</li>" for i in items) + "\n</ul>")
            items.clear()

    for line in md.splitlines():
        stripped = line.strip()
        image = re.fullmatch(r"!\[([^\]]*)\]\(([^)]+)\)", stripped)
        if not stripped:
            flush()
        elif image:
            flush()
            alt, src = html.escape(image.group(1)), image.group(2)
            out.append(f'<p style="text-align:center"><img src="{src}" alt="{alt}" style="max-width:100%"></p>')
        elif stripped == "---":
            flush()
            out.append("<hr>")
        elif stripped.startswith("#"):
            flush()
            level = len(stripped) - len(stripped.lstrip("#"))
            out.append(f"<h{level}>{inline(stripped[level:].strip())}</h{level}>")
        elif stripped.startswith("- "):
            if para:
                flush()
            items.append(stripped[2:])
        elif items and line.startswith("  "):
            items[-1] += " " + stripped
        else:
            if items:
                flush()
            para.append(stripped)
    flush()
    return "\n".join(out) + "\n"


def main() -> None:
    OUT.write_text(convert(SRC.read_text(encoding="utf-8")), encoding="utf-8", newline="\n")
    print("wrote", OUT)


if __name__ == "__main__":
    main()
