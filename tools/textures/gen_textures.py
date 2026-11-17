"""Generates 16x16 base textures for Gingerbread Grove from a fixed palette.

Deterministic: the same seed always gives the same pixels, so re-running only changes
textures whose recipe changed. Hand-polished textures can be protected by adding their
name to KEEP (they will never be overwritten).

Usage:  py tools/textures/gen_textures.py            (writes into common/src/main/resources)
        py tools/textures/gen_textures.py --preview  (also writes a 8x zoomed contact sheet)
"""
from __future__ import annotations

import random
import sys
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
BLOCK_DIR = ROOT / "common/src/main/resources/assets/gingerbread_grove/textures/block"
ITEM_DIR = ROOT / "common/src/main/resources/assets/gingerbread_grove/textures/item"
PREVIEW = ROOT / "tools/textures/preview.png"

# Textures that were polished by hand and must not be regenerated.
KEEP: set[str] = set()

# ---- Palette -------------------------------------------------------------------------------
FROST = [(255, 255, 255), (247, 244, 250), (238, 234, 246), (228, 224, 240), (252, 236, 242)]
FROST_SHADE = (214, 208, 232)
GINGER = [(176, 104, 52), (166, 96, 46), (186, 114, 60), (156, 88, 42), (194, 124, 66)]
GINGER_DARK = (120, 64, 30)
GINGER_CRUMB = (210, 146, 86)
MINT_WHITE = [(250, 248, 246), (242, 240, 238), (235, 232, 230)]
MINT_RED = [(214, 40, 52), (196, 30, 44), (226, 62, 70)]
MINT_SHADE = (208, 204, 204)

Pixel = tuple[int, int, int]


def new(seed: str) -> tuple[Image.Image, random.Random]:
    return Image.new("RGBA", (16, 16)), random.Random(seed)


def noise_fill(img: Image.Image, rng: random.Random, colours: list[Pixel], weights: list[float] | None = None) -> None:
    for y in range(16):
        for x in range(16):
            img.putpixel((x, y), rng.choices(colours, weights)[0] + (255,))


def speckle(img: Image.Image, rng: random.Random, colour: Pixel, count: int, rows: range = range(16)) -> None:
    for _ in range(count):
        img.putpixel((rng.randrange(16), rng.choice(rows)), colour + (255,))


# ---- Recipes -------------------------------------------------------------------------------

def frosting() -> Image.Image:
    img, rng = new("frosting")
    noise_fill(img, rng, FROST, [5, 4, 3, 1, 1])
    speckle(img, rng, FROST_SHADE, 10)
    return img


def gingerbread() -> Image.Image:
    img, rng = new("gingerbread")
    noise_fill(img, rng, GINGER, [5, 4, 3, 2, 1])
    speckle(img, rng, GINGER_DARK, 12)   # baked pores
    speckle(img, rng, GINGER_CRUMB, 6)   # sugar crumbs
    return img


def frosted_gingerbread_side() -> Image.Image:
    """Gingerbread with icing dripping down from the top edge, like the side of a grass block."""
    img = gingerbread().copy()
    rng = random.Random("frosted_gingerbread_side")
    for x in range(16):
        depth = rng.choice([2, 3, 3, 3, 4, 4, 5, 6])
        for y in range(depth):
            img.putpixel((x, y), rng.choice(FROST[:4]) + (255,))
        img.putpixel((x, depth), FROST_SHADE + (255,))  # shadow under the drip
    return img


def peppermint_stone() -> Image.Image:
    """White candy stone with red diagonal swirl stripes."""
    img, rng = new("peppermint_stone")
    noise_fill(img, rng, MINT_WHITE, [4, 3, 2])
    for y in range(16):
        for x in range(16):
            band = (x + y + (1 if rng.random() < 0.15 else 0)) % 8
            if band in (0, 1):
                img.putpixel((x, y), rng.choice(MINT_RED) + (255,))
            elif band == 2:
                img.putpixel((x, y), MINT_SHADE + (255,))
    return img


BLOCKS = {
    "frosting": frosting,
    "gingerbread": gingerbread,
    "frosted_gingerbread_side": frosted_gingerbread_side,
    "peppermint_stone": peppermint_stone,
}

GLASS_EDGE = (176, 214, 236)
GLASS = (214, 236, 248)
GLASS_HI = (250, 252, 255)
BASE = [(122, 62, 34), (148, 80, 44), (98, 48, 26)]
GOLD = (236, 190, 72)


def snow_globe() -> Image.Image:
    """A glass dome on a wooden base, with a tiny gingerbread house and falling snow inside."""
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    rng = random.Random("snow_globe")
    cx, cy, r = 7.5, 6.5, 6.0
    for y in range(16):
        for x in range(16):
            d = ((x - cx) ** 2 + (y - cy) ** 2) ** 0.5
            if d <= r - 0.9 and y <= 11:
                img.putpixel((x, y), GLASS + (255,))
            elif d <= r + 0.2 and y <= 11:
                img.putpixel((x, y), GLASS_EDGE + (255,))
    # snowy ground and a tiny gingerbread house
    for x in range(3, 13):
        img.putpixel((x, 11), FROST[0] + (255,))
        img.putpixel((x, 10), FROST[2] + (255,))
    for y in range(7, 10):
        for x in range(6, 10):
            img.putpixel((x, y), GINGER[0] + (255,))
    img.putpixel((7, 9), GINGER_DARK + (255,))      # door
    for x, y in ((5, 7), (6, 6), (7, 5), (8, 5), (9, 6), (10, 7)):
        img.putpixel((x, y), FROST[0] + (255,))     # iced roof
    # falling snow and a highlight on the glass
    for _ in range(5):
        x, y = rng.randrange(4, 12), rng.randrange(2, 5)
        img.putpixel((x, y), GLASS_HI + (255,))
    for x, y in ((4, 3), (3, 4), (3, 5)):
        img.putpixel((x, y), GLASS_HI + (255,))
    # wooden base with a gold band
    for y in range(12, 16):
        for x in range(2 if y > 12 else 3, 14 if y > 12 else 13):
            colour = GOLD if y == 13 else BASE[0] if y < 15 else BASE[2]
            img.putpixel((x, y), colour + (255,))
    return img


ITEMS = {
    "snow_globe": snow_globe,
}


def main() -> None:
    BLOCK_DIR.mkdir(parents=True, exist_ok=True)
    ITEM_DIR.mkdir(parents=True, exist_ok=True)
    made: list[tuple[str, Image.Image]] = []
    for folder, recipes in ((BLOCK_DIR, BLOCKS), (ITEM_DIR, ITEMS)):
        for name, recipe in recipes.items():
            if name in KEEP:
                print(f"keep  {name}")
                continue
            img = recipe()
            img.save(folder / f"{name}.png")
            made.append((name, img))
            print(f"wrote {folder.name}/{name}.png")

    if "--preview" in sys.argv and made:
        scale, pad = 8, 8
        sheet = Image.new("RGBA", (len(made) * (16 * scale + pad) + pad, 16 * scale + 2 * pad), (40, 40, 48, 255))
        for i, (_, img) in enumerate(made):
            sheet.paste(img.resize((16 * scale, 16 * scale), Image.NEAREST), (pad + i * (16 * scale + pad), pad))
        sheet.save(PREVIEW)
        print(f"preview -> {PREVIEW}")


if __name__ == "__main__":
    main()
