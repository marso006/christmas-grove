"""Turns the raw showcase screenshots into store-ready images.

Reads fabric/runs/showcase/screenshots/*.png (from :fabric:runShowcase) and writes docs/showcase/screenshots/:
  <shot>.png       1920x1080, lightly graded, vignette, pixel-font caption
  collage.png      a 3x3 grid of the best shots with the title
  thumbnail.png    1280x720 hero image for a video or a news post

Usage: py tools/showcase/edit_screenshots.py
"""
from __future__ import annotations

import sys
from pathlib import Path

from PIL import Image, ImageEnhance, ImageFilter, ImageStat

sys.path.insert(0, str(Path(__file__).resolve().parent))
from gen_showcase_art import CREAM, GOLD, draw_text, text_width  # noqa: E402

ROOT = Path(__file__).resolve().parents[2]
RAW = ROOT / "fabric/runs/showcase/screenshots"
OUT = ROOT / "docs/showcase/screenshots"

CAPTIONS = {
    "01_grove_dusk": "SUNSET OVER THE COCOA SEA",
    "02_fir_forest_evening": "EVERY TREE IS A CHRISTMAS TREE",
    "03_peppermint_peaks": "WELCOME TO THE CHRISTMAS GROVE",
    "04_cocoa_sea_sunset": "MARSHMALLOW ISLANDS",
    "05_village_dusk": "SANTA'S VILLAGE",
    "06_gingerbread_house": "GINGERBREAD HOUSES",
    "07_santas_workshop_inside": "INSIDE SANTA'S WORKSHOP",
    "08_christmas_tree_night": "LIGHTS AND A STAR ON TOP",
    "09_meet_the_cast": "MEET THE LOCALS",
    "10_sleigh": "LOOK UP! SANTA'S SLEIGH",
    "11_present_rain": "PRESENTS BY PARACHUTE",
    "12_krampus": "BETTER BE GOOD...",
    "13_santa_suit": "THE SANTA SUIT",
    "14_block_gallery": "75 NEW BLOCKS",
}
COLLAGE_ORDER = ["03_peppermint_peaks", "07_santas_workshop_inside", "10_sleigh", "05_village_dusk", "09_meet_the_cast",
                 "12_krampus", "06_gingerbread_house", "11_present_rain", "13_santa_suit"]
# Shots that did not come out well enough for the page (kept in the raw folder only)
SKIP = {"02_fir_forest_evening"}


def grade(img: Image.Image) -> Image.Image:
    img = ImageEnhance.Contrast(img).enhance(1.08)
    img = ImageEnhance.Color(img).enhance(1.12)
    # vignette: darken the corners a little
    w, h = img.size
    mask = Image.new("L", (w // 8, h // 8), 0)
    for y in range(mask.height):
        for x in range(mask.width):
            dx, dy = (x - mask.width / 2) / (mask.width / 2), (y - mask.height / 2) / (mask.height / 2)
            mask.putpixel((x, y), int(min(255, max(0, (dx * dx + dy * dy - 0.55) * 140))))
    mask = mask.resize((w, h), Image.BILINEAR).filter(ImageFilter.GaussianBlur(20))
    dark = Image.new("RGB", (w, h), (8, 6, 18))
    return Image.composite(dark, img, mask)


def caption(img: Image.Image, text: str, scale: int = 6) -> Image.Image:
    img = img.convert("RGBA")
    w, h = img.size
    tw, th = text_width(text, scale), 7 * scale
    pad = scale * 3
    bar = Image.new("RGBA", (tw + pad * 2, th + pad * 2), (14, 10, 30, 170))
    img.alpha_composite(bar, (scale * 8, h - th - pad * 2 - scale * 8))
    draw_text(img, text, scale * 8 + pad, h - th - pad - scale * 8, scale, CREAM, shadow=(120, 20, 30))
    return img


def sharpness(img: Image.Image) -> float:
    edges = img.convert("L").filter(ImageFilter.FIND_EDGES)
    return ImageStat.Stat(edges).mean[0]


def best_sleigh(raw: dict[str, Image.Image]) -> Image.Image | None:
    """Of the three sleigh frames, keep the one with the most detail in the centre (the sleigh)."""
    frames = [img for name, img in raw.items() if name.startswith("10_sleigh_")]
    if not frames:
        return None
    def centre_detail(img):
        w, h = img.size
        return sharpness(img.crop((w // 4, h // 6, 3 * w // 4, 2 * h // 3)))
    return max(frames, key=centre_detail)


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    raw = {p.stem: Image.open(p).convert("RGB") for p in sorted(RAW.glob("*.png"))}
    if not raw:
        sys.exit(f"no screenshots in {RAW}")
    sleigh = best_sleigh(raw)
    shots = {k: v for k, v in raw.items() if not k.startswith("10_sleigh_") and k not in SKIP}
    if sleigh is not None:
        shots["10_sleigh"] = sleigh

    finished = {}
    for name, img in sorted(shots.items()):
        img = img.resize((1920, 1080), Image.LANCZOS) if img.size != (1920, 1080) else img
        graded = grade(img)
        final = caption(graded, CAPTIONS.get(name, name.upper().replace("_", " ")))
        final.convert("RGB").save(OUT / f"{name}.png", optimize=True)
        finished[name] = graded
        print("edited", name)

    # 3x3 collage with a title strip
    tiles = [finished[n] for n in COLLAGE_ORDER if n in finished][:9]
    if tiles:
        tw, th, gap = 640, 360, 8
        title_h = 140
        collage = Image.new("RGBA", (3 * tw + 4 * gap, title_h + 3 * th + 4 * gap), (18, 26, 64, 255))
        for i, tile in enumerate(tiles):
            collage.paste(tile.resize((tw, th), Image.LANCZOS), (gap + (i % 3) * (tw + gap), title_h + gap + (i // 3) * (th + gap)))
        title = "CHRISTMAS GROVE"
        draw_text(collage, title, (collage.width - text_width(title, 10)) // 2, 14, 10, CREAM, shadow=(120, 20, 30))
        sub = "FABRIC & NEOFORGE 1.21.1"
        draw_text(collage, sub, (collage.width - text_width(sub, 3)) // 2, 106, 3, GOLD)
        collage.convert("RGB").save(OUT / "collage.png", optimize=True)
        print("collage")

    # Thumbnail: the most dramatic shot with the big title
    hero_name = next((n for n in ("10_sleigh", "07_santas_workshop_inside", "05_village_dusk") if n in finished), None)
    if hero_name:
        hero = finished[hero_name].resize((1280, 720), Image.LANCZOS).convert("RGBA")
        shade = Image.new("RGBA", (1280, 200), (10, 8, 30, 150))
        hero.alpha_composite(shade, (0, 0))
        title = "CHRISTMAS GROVE"
        draw_text(hero, title, (1280 - text_width(title, 10)) // 2, 40, 10, CREAM, shadow=(140, 20, 30))
        sub = "A WHOLE CHRISTMAS DIMENSION"
        draw_text(hero, sub, (1280 - text_width(sub, 4)) // 2, 130, 4, GOLD, shadow=(30, 20, 50))
        hero.convert("RGB").save(OUT / "thumbnail.png", optimize=True)
        print("thumbnail from", hero_name)


if __name__ == "__main__":
    main()
