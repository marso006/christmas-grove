"""Builds CurseForge / Modrinth showcase art from the mod's own textures, so it matches the game.

Outputs (docs/showcase/):
  icon.png             400x400 project icon
  banner.png           1600x400 page header with the title in a pixel font
  feature_blocks.png   a gallery of the mod's blocks
  feature_items.png    a gallery of food, tools and festive items
Also writes the in-game mod icon (128x128) to common/src/main/resources/christmas_grove.png.

Usage: py tools/showcase/gen_showcase_art.py
"""
from __future__ import annotations

import random
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
TEX = ROOT / "common/src/main/resources/assets/christmas_grove/textures"
OUT = ROOT / "docs/showcase"

NIGHT_TOP, NIGHT_BOTTOM = (18, 26, 64), (70, 52, 110)
GOLD, GOLD_DARK, CREAM, RED, WHITE = (236, 190, 72), (170, 120, 30), (255, 244, 220), (198, 30, 42), (255, 255, 255)

# 5x7 pixel font (enough for titles and captions)
FONT = {
    "A": ["01110", "10001", "10001", "11111", "10001", "10001", "10001"],
    "B": ["11110", "10001", "10001", "11110", "10001", "10001", "11110"],
    "C": ["01110", "10001", "10000", "10000", "10000", "10001", "01110"],
    "D": ["11110", "10001", "10001", "10001", "10001", "10001", "11110"],
    "E": ["11111", "10000", "10000", "11110", "10000", "10000", "11111"],
    "F": ["11111", "10000", "10000", "11110", "10000", "10000", "10000"],
    "G": ["01110", "10001", "10000", "10111", "10001", "10001", "01111"],
    "H": ["10001", "10001", "10001", "11111", "10001", "10001", "10001"],
    "I": ["01110", "00100", "00100", "00100", "00100", "00100", "01110"],
    "J": ["00111", "00010", "00010", "00010", "00010", "10010", "01100"],
    "K": ["10001", "10010", "10100", "11000", "10100", "10010", "10001"],
    "L": ["10000", "10000", "10000", "10000", "10000", "10000", "11111"],
    "M": ["10001", "11011", "10101", "10101", "10001", "10001", "10001"],
    "N": ["10001", "11001", "10101", "10011", "10001", "10001", "10001"],
    "O": ["01110", "10001", "10001", "10001", "10001", "10001", "01110"],
    "P": ["11110", "10001", "10001", "11110", "10000", "10000", "10000"],
    "Q": ["01110", "10001", "10001", "10001", "10101", "10010", "01101"],
    "R": ["11110", "10001", "10001", "11110", "10100", "10010", "10001"],
    "S": ["01111", "10000", "10000", "01110", "00001", "00001", "11110"],
    "T": ["11111", "00100", "00100", "00100", "00100", "00100", "00100"],
    "U": ["10001", "10001", "10001", "10001", "10001", "10001", "01110"],
    "V": ["10001", "10001", "10001", "10001", "10001", "01010", "00100"],
    "W": ["10001", "10001", "10001", "10101", "10101", "10101", "01010"],
    "X": ["10001", "10001", "01010", "00100", "01010", "10001", "10001"],
    "Y": ["10001", "10001", "01010", "00100", "00100", "00100", "00100"],
    "Z": ["11111", "00001", "00010", "00100", "01000", "10000", "11111"],
    "0": ["01110", "10011", "10101", "10101", "11001", "10001", "01110"],
    "1": ["00100", "01100", "00100", "00100", "00100", "00100", "01110"],
    "2": ["01110", "10001", "00001", "00010", "00100", "01000", "11111"],
    "3": ["11110", "00001", "00001", "01110", "00001", "00001", "11110"],
    "4": ["00010", "00110", "01010", "10010", "11111", "00010", "00010"],
    "5": ["11111", "10000", "11110", "00001", "00001", "10001", "01110"],
    "6": ["01110", "10000", "10000", "11110", "10001", "10001", "01110"],
    "7": ["11111", "00001", "00010", "00100", "01000", "01000", "01000"],
    "8": ["01110", "10001", "10001", "01110", "10001", "10001", "01110"],
    "9": ["01110", "10001", "10001", "01111", "00001", "00001", "01110"],
    "&": ["01100", "10010", "10100", "01000", "10101", "10010", "01101"],
    ".": ["00000", "00000", "00000", "00000", "00000", "01100", "01100"],
    ",": ["00000", "00000", "00000", "00000", "01100", "00100", "01000"],
    "!": ["00100", "00100", "00100", "00100", "00100", "00000", "00100"],
    "-": ["00000", "00000", "00000", "11111", "00000", "00000", "00000"],
    "'": ["00100", "00100", "01000", "00000", "00000", "00000", "00000"],
    ":": ["00000", "01100", "01100", "00000", "01100", "01100", "00000"],
    " ": ["00000"] * 7,
}


def tex(path: str) -> Image.Image:
    return Image.open(TEX / f"{path}.png").convert("RGBA")


def text_width(text: str, scale: int) -> int:
    return len(text) * 6 * scale - scale


def draw_text(img: Image.Image, text: str, x: int, y: int, scale: int, colour, shadow=None) -> None:
    for layer, (dx, dy, c) in enumerate(([(scale, scale, shadow)] if shadow else []) + [(0, 0, colour)]):
        cx = x + dx
        for ch in text.upper():
            glyph = FONT.get(ch, FONT[" "])
            for gy, row in enumerate(glyph):
                for gx, bit in enumerate(row):
                    if bit == "1":
                        for sy in range(scale):
                            for sx in range(scale):
                                px, py = cx + gx * scale + sx, y + dy + gy * scale + sy
                                if 0 <= px < img.width and 0 <= py < img.height:
                                    img.putpixel((px, py), c + (255,) if len(c) == 3 else c)
            cx += 6 * scale


def night_sky(w: int, h: int, seed: str, flakes: int) -> Image.Image:
    img = Image.new("RGBA", (w, h))
    for y in range(h):
        t = y / max(1, h - 1)
        c = tuple(int(NIGHT_TOP[i] + (NIGHT_BOTTOM[i] - NIGHT_TOP[i]) * t) for i in range(3))
        for x in range(w):
            img.putpixel((x, y), c + (255,))
    rng = random.Random(seed)
    for _ in range(flakes):
        x, y = rng.randrange(w), rng.randrange(h)
        img.putpixel((x, y), (255, 255, 255, 255) if rng.random() < 0.7 else (200, 210, 255, 255))
    return img


def paste(base: Image.Image, sprite: Image.Image, x: int, y: int, scale: int = 1) -> None:
    if scale != 1:
        sprite = sprite.resize((sprite.width * scale, sprite.height * scale), Image.NEAREST)
    base.alpha_composite(sprite, (x, y))


def upscale(img: Image.Image, factor: int) -> Image.Image:
    return img.resize((img.width * factor, img.height * factor), Image.NEAREST)


def ground_row(base: Image.Image, y: int) -> None:
    top, side, below = tex("block/frosting"), tex("block/frosted_gingerbread_side"), tex("block/gingerbread")
    for x in range(0, base.width, 16):
        paste(base, side, x, y)
        paste(base, below, x, y + 16)


def icon() -> Image.Image:
    """The Snow Globe (the way into the Grove) on a gold-ringed festive medallion."""
    size = 40
    img = night_sky(size, size, "icon", 26)
    cx = cy = (size - 1) / 2
    for y in range(size):
        for x in range(size):
            d = ((x - cx) ** 2 + (y - cy) ** 2) ** 0.5
            if 17.5 < d <= 19.5:
                img.putpixel((x, y), (GOLD if d < 18.6 else GOLD_DARK) + (255,))
            elif d > 19.5:
                img.putpixel((x, y), (0, 0, 0, 0))
    paste(img, tex("item/snow_globe"), 4, 4, 2)
    return upscale(img, 10)


def banner() -> Image.Image:
    w, h = 320, 80
    img = night_sky(w, h, "banner", 140)
    ground_row(img, h - 22)
    rng = random.Random("banner-props")
    props = ["block/festive_fir_sapling", "block/lollipop_flower", "block/poinsettia", "block/holly",
             "block/candy_cane_sapling", "block/sugar_grass", "block/gumdrop_bush_stage3"]
    for x in range(4, w - 16, 14):
        if 92 < x < 226:
            continue  # keep the title area clean
        paste(img, tex(rng.choice(props)), x, h - 38)
    for x, name in ((12, "item/snow_globe"), (290, "item/candy_cane"), (36, "item/gingerbread_cookie"),
                    (268, "block/red_gift_box_side")):
        paste(img, tex(name), x, h - 38)
    title = "CHRISTMAS GROVE"
    draw_text(img, title, (w - text_width(title, 2)) // 2, 12, 2, CREAM, shadow=(120, 20, 30))
    sub = "A FESTIVE CANDY DIMENSION"
    draw_text(img, sub, (w - text_width(sub, 1)) // 2, 34, 1, GOLD, shadow=(40, 20, 50))
    tag = "FABRIC & NEOFORGE"
    draw_text(img, tag, (w - text_width(tag, 1)) // 2, 44, 1, WHITE, shadow=(40, 20, 50))
    return upscale(img, 5)


def gallery(title: str, sprites: list[str], seed: str, cols: int = 12) -> Image.Image:
    cell, pad = 20, 10
    rows = (len(sprites) + cols - 1) // cols
    w, h = cols * cell + pad * 2, rows * cell + pad * 2 + 18
    img = night_sky(w, h, seed, 60)
    draw_text(img, title, (w - text_width(title, 1)) // 2, 6, 1, CREAM, shadow=(120, 20, 30))
    for i, name in enumerate(sprites):
        x, y = pad + (i % cols) * cell + 2, pad + 14 + (i // cols) * cell + 2
        for yy in range(y - 1, y + 17):                       # a soft tile behind each sprite
            for xx in range(x - 1, x + 17):
                r, g, b, _ = img.getpixel((xx, yy))
                img.putpixel((xx, yy), (min(255, r + 18), min(255, g + 18), min(255, b + 24), 255))
        paste(img, tex(name), x, y)
    return upscale(img, 4)


BLOCKS = ["frosted_gingerbread_side", "gingerbread", "frosting", "peppermint_stone", "peppermint_bricks", "icing_bricks",
          "gingerbread_bricks", "cookie_tiles", "sugar_sand", "marshmallow_block", "candy_glass", "sugar_crystal_block",
          "candy_cane_log", "candy_cane_log_top", "candy_cane_planks", "gumdrop_leaves", "festive_fir_log", "festive_fir_planks",
          "festive_fir_leaves", "red_lit_festive_fir_leaves", "gold_lit_festive_fir_leaves", "blue_lit_festive_fir_leaves",
          "sugar_crystal_ore", "cinnamon_ore", "red_gift_box_side", "green_gift_box_side", "blue_gift_box_side", "christmas_star",
          "festive_lights", "wreath", "advent_calendar_front", "cookie_jar", "lollipop_flower", "poinsettia", "holly",
          "gumdrop_bush_stage3"]
ITEMS = ["snow_globe", "gingerbread_cookie", "sugar_cookie", "candy_cane", "gumdrops", "marshmallow", "roasted_marshmallow",
         "hot_cocoa", "eggnog", "fruitcake_slice", "gingerbread_heart", "sugar_crystal", "cinnamon_stick", "candy_cane_sword",
         "sugar_crystal_pickaxe", "sugar_crystal_axe", "sugar_crystal_shovel", "sugar_crystal_hoe", "santa_hat", "santa_coat",
         "santa_trousers", "santa_boots", "nice_list", "letter_to_santa", "sleigh_bells", "krampus_bell", "krampus_horn", "mug"]


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    ic = icon()
    ic.save(OUT / "icon.png")
    ic.resize((128, 128), Image.NEAREST).save(ROOT / "common/src/main/resources/christmas_grove.png")
    banner().save(OUT / "banner.png")
    gallery("BLOCKS OF THE GROVE", [f"block/{b}" for b in BLOCKS], "blocks").save(OUT / "feature_blocks.png")
    gallery("TREATS, TOOLS & FESTIVE GEAR", [f"item/{i}" for i in ITEMS], "items", cols=14).save(OUT / "feature_items.png")
    print("showcase art ->", OUT)


if __name__ == "__main__":
    main()
