"""Generates 16x16 base textures for Christmas Grove from a fixed palette.

Deterministic: the same seed always gives the same pixels, so re-running only changes
textures whose recipe changed. Hand-polished textures can be protected by adding their
name to KEEP (they will never be overwritten).

Two kinds of recipe:
  * procedural tiles (ground, bricks, planks, logs, leaves, ores) built from noise and patterns
  * pixel-art sprites (items, plants, decor) drawn as 16-row character grids, see SPRITES

Usage:  py tools/textures/gen_textures.py            (writes into common/src/main/resources)
        py tools/textures/gen_textures.py --preview  (also writes an 8x zoomed contact sheet)
        py tools/textures/gen_textures.py --check    (lists textures that models need but are missing)
"""
from __future__ import annotations

import json
import random
import sys
from pathlib import Path
from typing import Callable

from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
ASSETS = ROOT / "common/src/main/resources/assets/christmas_grove"
GENERATED = ROOT / "common/src/generated/resources/assets/christmas_grove"
BLOCK_DIR = ASSETS / "textures/block"
ITEM_DIR = ASSETS / "textures/item"
PREVIEW = ROOT / "tools/textures/preview.png"

# Textures that were polished by hand and must not be regenerated.
KEEP: set[str] = set()

Pixel = tuple[int, int, int]
RGBA = tuple[int, int, int, int]

# ---- Palette -------------------------------------------------------------------------------
FROST = [(255, 255, 255), (247, 244, 250), (238, 234, 246), (228, 224, 240), (252, 236, 242)]
FROST_SHADE = (214, 208, 232)
GINGER = [(176, 104, 52), (166, 96, 46), (186, 114, 60), (156, 88, 42), (194, 124, 66)]
GINGER_DARK = (120, 64, 30)
GINGER_CRUMB = (210, 146, 86)
MINT_WHITE = [(250, 248, 246), (242, 240, 238), (235, 232, 230)]
MINT_RED = [(214, 40, 52), (196, 30, 44), (226, 62, 70)]
MINT_SHADE = (208, 204, 204)
SAND = [(246, 238, 222), (238, 228, 210), (250, 244, 232), (230, 218, 198)]
MALLOW = [(255, 252, 250), (250, 244, 244), (244, 236, 238)]
COOKIE = [(222, 176, 112), (212, 164, 100), (230, 188, 126)]
COOKIE_DARK = (176, 124, 66)
CANE_RED = [(206, 34, 46), (190, 26, 40), (220, 52, 60)]
CANE_WHITE = [(250, 246, 244), (240, 236, 234)]
FIR_BARK = [(92, 60, 38), (80, 52, 32), (104, 70, 44)]
FIR_INNER = [(196, 150, 100), (184, 138, 90), (206, 162, 110)]
FIR_PLANK = [(150, 104, 64), (140, 96, 58), (160, 112, 70)]
FIR_GREEN = [(38, 104, 58), (30, 88, 48), (48, 120, 66), (26, 76, 42)]
CANE_PLANK = [(238, 206, 204), (232, 196, 194), (244, 216, 214)]
GOLD = (236, 190, 72)
GOLD_DARK = (190, 140, 40)
GOLD_LIGHT = (255, 230, 140)
CRYSTAL = [(178, 228, 246), (206, 240, 252), (150, 206, 236)]
CINNAMON = [(150, 72, 34), (128, 60, 28), (172, 88, 44)]
GUMDROP_COLOURS = [(236, 64, 120), (90, 200, 90), (250, 200, 50), (150, 90, 220), (250, 130, 40)]
BULB_COLOURS = {"red": [(255, 60, 60), (255, 140, 120)], "gold": [(255, 210, 60), (255, 240, 160)],
                "blue": [(70, 140, 255), (160, 200, 255)]}


def rgba(c: Pixel, a: int = 255) -> RGBA:
    return (c[0], c[1], c[2], a)


def blank() -> Image.Image:
    return Image.new("RGBA", (16, 16), (0, 0, 0, 0))


def noise(seed: str, colours: list[Pixel], weights: list[float] | None = None) -> Image.Image:
    img, rng = blank(), random.Random(seed)
    for y in range(16):
        for x in range(16):
            img.putpixel((x, y), rgba(rng.choices(colours, weights)[0]))
    return img


def speckle(img: Image.Image, seed: str, colour: Pixel, count: int) -> Image.Image:
    rng = random.Random(seed + "/speckle")
    for _ in range(count):
        img.putpixel((rng.randrange(16), rng.randrange(16)), rgba(colour))
    return img


def shade(c: Pixel, f: float) -> Pixel:
    return tuple(max(0, min(255, int(v * f))) for v in c)  # type: ignore[return-value]


# ---- Pattern helpers -----------------------------------------------------------------------

def bricks(seed: str, colours: list[Pixel], mortar: Pixel, brick_h: int = 4, brick_w: int = 8) -> Image.Image:
    img, rng = noise(seed, colours), random.Random(seed)
    for y in range(16):
        row = y // brick_h
        offset = (brick_w // 2) * (row % 2)
        for x in range(16):
            if y % brick_h == brick_h - 1 or (x + offset) % brick_w == 0:
                img.putpixel((x, y), rgba(mortar))
            elif y % brick_h == 0 and rng.random() < 0.5:
                img.putpixel((x, y), rgba(shade(img.getpixel((x, y))[:3], 1.08)))
    return img


def planks(seed: str, colours: list[Pixel]) -> Image.Image:
    img, rng = noise(seed, colours, [4, 3, 2]), random.Random(seed)
    dark = shade(colours[0], 0.72)
    for y in range(16):
        for x in range(16):
            if y % 4 == 3:
                img.putpixel((x, y), rgba(dark))
        seam = (y // 4 * 5 + 3) % 16
        if y % 4 != 3:
            img.putpixel((seam, y), rgba(dark))
    for _ in range(6):  # wood grain flecks
        x, y = rng.randrange(16), rng.randrange(16)
        if y % 4 != 3:
            img.putpixel((x, y), rgba(shade(colours[0], 0.85)))
    return img


def log_top(seed: str, bark: list[Pixel], inner: list[Pixel]) -> Image.Image:
    img, rng = blank(), random.Random(seed)
    for y in range(16):
        for x in range(16):
            d = max(abs(x - 7.5), abs(y - 7.5))
            if d > 6.5:
                c = rng.choice(bark)
            else:
                ring = int(d) % 2
                c = shade(rng.choice(inner), 0.88 if ring else 1.0)
            img.putpixel((x, y), rgba(c))
    return img


def bark(seed: str, colours: list[Pixel]) -> Image.Image:
    img, rng = noise(seed, colours, [4, 3, 2]), random.Random(seed)
    dark = shade(colours[0], 0.7)
    for x in range(0, 16, 3):
        for y in range(16):
            if rng.random() < 0.75:
                img.putpixel(((x + (y // 5)) % 16, y), rgba(dark))
    return img


def stripes(seed: str, a: list[Pixel], b: list[Pixel], width: int = 3, period: int = 6) -> Image.Image:
    img, rng = blank(), random.Random(seed)
    for y in range(16):
        for x in range(16):
            band = (x + y) % period
            img.putpixel((x, y), rgba(rng.choice(a if band < width else b)))
    return img


def leaves(seed: str, colours: list[Pixel], holes: float = 0.18) -> Image.Image:
    img, rng = noise(seed, colours), random.Random(seed + "/holes")
    for y in range(16):
        for x in range(16):
            if rng.random() < holes:
                img.putpixel((x, y), (0, 0, 0, 0))
    return img


def ore(seed: str, base: Image.Image, colours: list[Pixel], clusters: int = 5) -> Image.Image:
    img, rng = base.copy(), random.Random(seed)
    for _ in range(clusters):
        cx, cy = rng.randrange(2, 14), rng.randrange(2, 14)
        for dx, dy in ((0, 0), (1, 0), (0, 1), (1, 1), (-1, 0), (0, -1)):
            if rng.random() < 0.8:
                img.putpixel((cx + dx, cy + dy), rgba(rng.choice(colours)))
        img.putpixel((cx + 1, cy + 1), rgba(shade(colours[0], 0.7)))
    return img


def door_half(seed: str, colours: list[Pixel], top: bool) -> Image.Image:
    img = planks(seed, colours)
    frame = shade(colours[0], 0.6)
    for i in range(16):
        img.putpixel((0, i), rgba(frame))
        img.putpixel((15, i), rgba(frame))
        img.putpixel((i, 0 if top else 15), rgba(frame))
    if top:  # a small window
        for y in range(3, 8):
            for x in range(4, 12):
                img.putpixel((x, y), rgba((200, 228, 240) if (x + y) % 5 else (240, 250, 255)))
    else:  # handle
        img.putpixel((12, 2), rgba(GOLD))
        img.putpixel((12, 3), rgba(GOLD_DARK))
    return img


def trapdoor(seed: str, colours: list[Pixel]) -> Image.Image:
    img = planks(seed, colours)
    frame = shade(colours[0], 0.6)
    for i in range(16):
        for p in ((0, i), (15, i), (i, 0), (i, 15)):
            img.putpixel(p, rgba(frame))
    for y in (5, 6, 9, 10):
        for x in (5, 6, 9, 10):
            img.putpixel((x, y), (0, 0, 0, 0))
    return img


def glass(seed: str, tint: Pixel, alpha: int) -> Image.Image:
    img, rng = blank(), random.Random(seed)
    edge = shade(tint, 0.8)
    for y in range(16):
        for x in range(16):
            if x in (0, 15) or y in (0, 15):
                img.putpixel((x, y), rgba(edge, 230))
            else:
                img.putpixel((x, y), rgba(tint, alpha))
    for i in range(3, 7):  # glint
        img.putpixel((i, i - 1), rgba((255, 255, 255), 200))
    return img


# ---- Sprite grids --------------------------------------------------------------------------

def sprite(rows: list[str], palette: dict[str, Pixel | RGBA]) -> Image.Image:
    """Draws a pixel-art grid. Rows shorter than 16 are padded with '.', which is transparent."""
    if len(rows) > 16:
        raise ValueError(f"sprite has {len(rows)} rows")
    img = blank()
    for y, row in enumerate(rows):
        if len(row) > 16:
            raise ValueError(f"sprite row {y} is {len(row)} wide: {row!r}")
        for x, ch in enumerate(row):
            if ch in ". ":
                continue
            c = palette[ch]
            img.putpixel((x, y), c if len(c) == 4 else rgba(c))  # type: ignore[arg-type]
    return img


P = {  # shared sprite palette
    "w": (255, 255, 255), "W": (226, 222, 236), "r": CANE_RED[0], "R": (150, 20, 32), "p": (240, 120, 170),
    "P": (200, 70, 120), "g": (80, 180, 80), "G": (40, 110, 50), "d": (26, 76, 42), "y": (250, 210, 60),
    "Y": GOLD_DARK, "o": GOLD, "O": GOLD_LIGHT, "b": GINGER[0], "B": GINGER_DARK, "c": COOKIE[0], "C": COOKIE_DARK,
    "k": (90, 52, 30), "K": (60, 34, 20), "m": (110, 64, 40), "s": (150, 150, 160), "S": (200, 200, 210),
    "u": (110, 160, 255), "U": (60, 100, 200), "l": CRYSTAL[0], "L": CRYSTAL[2], "n": (230, 220, 190), "N": (200, 186, 150),
    "t": (120, 80, 50), "v": (170, 100, 220), "e": (250, 240, 210), "E": (220, 200, 160), "x": (60, 60, 70),
    "h": (255, 255, 255, 140), "a": (250, 130, 40),
}

SPRITES: dict[str, list[str]] = {
    # ---- items
    "item/gumdrops": [
        "", "", "",
        "......pp",
        ".....pwpp",
        "....pppppp",
        "....ppppPp.gg",
        "....pPPPPPggwg",
        "..yy......gggggG",
        ".ywyy.....ggggGG",
        "yyyyyy....GGGGGG",
        "yyyyYy",
        "YYYYYY",
    ],
    "item/candy_cane": [
        "",
        ".....rwwr",
        "....wrrwwr",
        "...rw....wr",
        "...wr.....rw",
        "..........wr",
        "..........rw",
        "..........wr",
        "..........rw",
        "..........wr",
        "..........rw",
        "..........wr",
        "..........rw",
        "..........wr",
    ],
    "item/gingerbread_cookie": [
        "",
        "......bbbb",
        ".....bbbbbb",
        ".....bwbbwb",
        ".....bbbbbb",
        ".....bbwwbb",
        "..bbb.bbbb.bbb",
        ".bwbbbbbbbbbbwb",
        ".bbbbbbrbbbbbbB",
        "..BB.bbbbbb.BB",
        ".....bbrbbb",
        ".....bbbbbb",
        "....bbb..bbb",
        "...bbb....bbb",
        "...bwb....bwb",
        "...BBB....BBB",
    ],
    "item/sugar_cookie": [
        "", "",
        ".....cccccc",
        "....cccccccc",
        "...ccrcccucccC",
        "...cccccccccC",
        "..ccgccccrcccC",
        "..cccccccccccC",
        "..ccucccgccccC",
        "..cccccccccccC",
        "...ccrcccccuC",
        "...cccccccccC",
        "....CcccccCC",
        ".....CCCCCC",
    ],
    "item/marshmallow": [
        "", "", "", "",
        ".....wwwwww",
        "....wwwwwwww",
        "....Wwwwwwww",
        "....Wwwwwwww",
        "....Wwwwwwww",
        "....Wwwwwwww",
        "....WWwwwwww",
        ".....WWWWWW",
    ],
    "item/roasted_marshmallow": [
        "",
        ".........cccc",
        "........cCcccC",
        "........CccCcc",
        "........ccCcCC",
        ".........CCCC",
        "........t",
        ".......t",
        "......t",
        ".....t",
        "....t",
        "...t",
        "..t",
        ".t",
    ],
    "item/fruitcake_slice": [
        "", "", "", "",
        "........ww",
        "......wwwwww",
        "....wwwwwwwwww",
        "..wwwwwwwwwwwwk",
        "..krkkgkkrkkkkk",
        "..kkkkkkkkgkkkk",
        "..kgkkrkkkkkrkk",
        "..kkkkkkkgkkkkk",
        "..KKKKKKKKKKKKK",
    ],
    "item/gingerbread_heart": [
        "", "",
        "..wwww....wwww",
        ".wbbbbw..wbbbbw",
        "wbbbbbbwwbbbbbbw",
        "wbbrbbbbbbbbrbbw",
        "wbbbbbbbbbbbbbbw",
        ".wbbbbbbbbbbbbw",
        "..wbbbbbbbbbbw",
        "...wbbbrbbbbw",
        "....wbbbbbbw",
        ".....wbbbbw",
        "......wbbw",
        ".......ww",
    ],
    "item/mug": [
        "", "", "", "",
        "...nnnnnnnn",
        "...nKKKKKKn",
        "...nnnnnnnnnn",
        "...nnnnnnnn.n",
        "...nnnnnnnn.n",
        "...nnnnnnnnnn",
        "...Nnnnnnnn",
        "...NNnnnnnn",
        "....NNNNNN",
    ],
    "item/hot_cocoa": [
        "",
        ".....h..h",
        "....h..h",
        ".....h..h",
        "...nnnnnnnn",
        "...nmmwwmmn",
        "...nnnnnnnnnn",
        "...nrnnrnnn.n",
        "...nnnnnnnn.n",
        "...nnrnnrnnnn",
        "...Nnnnnnnn",
        "...NNnnnnnn",
        "....NNNNNN",
    ],
    "item/eggnog": [
        "",
        "......kk",
        "......kk",
        ".....SSSS",
        ".....S..S",
        "....SeeeeS",
        "...SeeeeeeS",
        "...SeEeeeeS",
        "...SeeeeeeS",
        "...SeeeEeeS",
        "...SeeeeeeS",
        "...SEeeeeES",
        "....SSSSSS",
    ],
    "item/sugar_crystal": [
        "", "",
        ".......w",
        "......wlw",
        ".....wlllw",
        "....wllLllw",
        "...wlllLlllw",
        "...lllLLLlll",
        "...LllLLLllL",
        "....LllLllL",
        ".....LlllL",
        "......LlL",
        ".......L",
    ],
    "item/cinnamon_stick": [
        "", "",
        "............tm",
        "...........tmm",
        "..........tmmt",
        ".........tmmt",
        "........tmmt",
        ".......tmmt",
        "......tmmt",
        ".....tmmt",
        "....tmmt",
        "...tmmt",
        "..tmmt",
        "..mmt",
    ],
    "item/candy_cane_door": [
        "", "",
        ".....pppppp",
        ".....pwwwwp",
        ".....pwppwp",
        ".....pwwwwp",
        ".....pppppp",
        ".....prprpp",
        ".....pppppp",
        ".....pprpyp",
        ".....pppppp",
        ".....prprpp",
        ".....pppppp",
        ".....PPPPPP",
    ],
    "item/festive_fir_door": [
        "", "",
        ".....kkkkkk",
        ".....kSSSSk",
        ".....kSkkSk",
        ".....kSSSSk",
        ".....kkkkkk",
        ".....kmkmkk",
        ".....kkkkkk",
        ".....kkkkok",
        ".....kkkkkk",
        ".....kmkmkk",
        ".....kkkkkk",
        ".....KKKKKK",
    ],
    # ---- plants
    "block/lollipop_flower": [
        "",
        ".....ppppp",
        "....pwwwwpp",
        "...pwppppwpp",
        "...pwpwwpwpp",
        "...pwpwppwpp",
        "...pwppwwppp",
        "....pwwwppp",
        ".....ppppp",
        ".......w",
        ".......w",
        ".......w..g",
        ".......wgg",
        "...gg..w",
        "....gg.w",
        ".......w",
    ],
    "block/poinsettia": [
        "", "",
        ".......r",
        "...r..rrr..r",
        "....rrrrrrr",
        "...rrrRyRrrr",
        ".rrrrRyyyRrrrr",
        "...rrrRyRrrr",
        "....rrrrrrr",
        "...r..rrr..r",
        "...g...g...g",
        "....gg.g.gg",
        "......ggg",
        ".......g",
        ".......g",
        ".......g",
    ],
    "block/holly": [
        "", "", "",
        "....g.....g",
        "...ggg...ggg",
        "..gGgGg.gGgGg",
        ".gggGggggGggg",
        "..gGgrrrrgGg",
        "...g.rRrRr.g",
        "......rrr",
        ".......G",
        "......gG",
        ".....g.G",
        ".......G",
        ".......G",
        ".......G",
    ],
    "block/sugar_grass": [
        "", "", "", "", "",
        "...w.......w",
        "...w...w...w",
        "....w..w..w",
        "....w..W..w..w",
        ".w..W..w..W..w",
        ".w..w..W..w.W",
        "..w.W.wW.wW.W",
        "..W.w.ww.ww.w",
        "..WwW.wW.wWwW",
        "..WWWwWWWWWWW",
        "..WWWWWWWWWWW",
    ],
    "block/gumdrop_bush_stage0": [
        "", "", "", "", "", "", "", "", "", "", "",
        "......g",
        "......gg.g",
        ".......gg",
        ".......G",
        ".......G",
    ],
    "block/gumdrop_bush_stage1": [
        "", "", "", "", "", "", "",
        "......gg",
        "....gggggg",
        "...gGggGggg",
        "...ggggggGg",
        "....gGgggg",
        ".....gGgg",
        "......GG",
        ".......G",
        ".......G",
    ],
    "block/gumdrop_bush_stage2": [
        "", "", "", "", "",
        "......gggg",
        "....gggpgggg",
        "...gGggggGggg",
        "..gggggGgggygg",
        "..gyGggggGgggg",
        "..ggggGggggpgg",
        "...gGgggggGgg",
        "....gggGggg",
        "......GGG",
        ".......G",
        ".......G",
    ],
    "block/gumdrop_bush_stage3": [
        "", "", "",
        "......gggg",
        "....ggppgggg",
        "...gGpwpgGyyg",
        "..gggppgGgywyg",
        "..gyygggggyygg",
        "..ywyyGggGgvvg",
        "..gyygggggvwvg",
        "..ggggGpppgvvg",
        "...gGgpwpgGgg",
        "....ggpppggg",
        "......GGG",
        ".......G",
        ".......G",
    ],
    "block/candy_cane_sapling": [
        "", "", "",
        "......pp.yy",
        ".....pwppywy",
        ".....ppppyyy",
        "......pp..y",
        "......rw",
        ".....wr.",
        ".....r",
        "......w",
        "......r",
        "......w",
        "......r",
        "......w",
        "......r",
    ],
    "block/festive_fir_sapling": [
        "", "",
        ".......o",
        ".......g",
        "......ggg",
        ".....gGgGg",
        "......ggg",
        ".....gGrGg",
        "....gggGggg",
        ".....ggggg",
        "....gGgggGg",
        "...gggGgGggg",
        ".......k",
        ".......k",
        ".......k",
        ".......k",
    ],
    # ---- decor
    "block/christmas_star": [
        "", "",
        ".......o",
        ".......o",
        "......oOo",
        "......oOo",
        "ooooooOOOoooooo",
        ".ooOOOOOOOOOoo",
        "..ooOOOOOOOoo",
        "...ooOOOOOoo",
        "...oOOoOoOOo",
        "..oOOo...oOOo",
        "..oOo.....oOo",
        ".oOo.......oOo",
        ".oo.........oo",
        "",
    ],
    "block/festive_lights": [
        "",
        "dd....dd....dd..",
        "..dddd..dddd..dd",
        "...r....y....u",
        "..rRr..yYy..uUu",
        "..rrr..yyy..uuu",
        "...r....y....u",
        "", "",
        "dd....dd....dd..",
        "..dddd..dddd..dd",
        "...y....u....r",
        "..yYy..uUu..rRr",
        "..yyy..uuu..rrr",
        "...y....u....r",
        "",
    ],
    "block/wreath": [
        "",
        ".....gggggg",
        "...ggGgggGgg",
        "..gGggrggGggg",
        ".ggggg....gGgg",
        ".gGgg......ggg",
        "gggr........gGg",
        "gGgg........ggg",
        "gggg........grg",
        "ggGg........ggg",
        ".gggg......gGg",
        ".ggGgg....gggg",
        "..gggggrrgggg",
        "...ggGrRRrgg",
        ".....rRrrRr",
        "......r..r",
    ],
    # ---- tools (candy-cane handles, crystal heads)
    "item/candy_cane_sword": [
        "..............wl",
        ".............wlL",
        "............wlL",
        "...........wlL",
        "..........wlL",
        ".........wlL",
        "........wlL",
        ".......wlL",
        "..o...wlL",
        "...o.wlL",
        "....olL",
        "....ro",
        "...w..o",
        "..r",
        ".w",
        "o",
    ],
    "item/sugar_crystal_pickaxe": [
        "",
        "....wllllll",
        "...wlLLLLLLl",
        "..wlL....rwLl",
        "..lL....rw..Ll",
        "..l....rw....l",
        "......rw",
        ".....rw",
        "....rw",
        "...rw",
        "..rw",
        ".rw",
        "rw",
    ],
    "item/sugar_crystal_axe": [
        "",
        ".......wll",
        "......wlLLl",
        ".....wlLLLLl",
        ".....lLLLrwL",
        "......lLrwL",
        "........rw",
        ".......rw",
        "......rw",
        ".....rw",
        "....rw",
        "...rw",
        "..rw",
        ".rw",
    ],
    "item/sugar_crystal_shovel": [
        "",
        "...........wll",
        "..........wlLLl",
        "..........lLLLl",
        "...........lLL",
        "..........rw",
        ".........rw",
        "........rw",
        ".......rw",
        "......rw",
        ".....rw",
        "....rw",
        "...rw",
        "..rw",
    ],
    "item/sugar_crystal_hoe": [
        "",
        "....wlllll",
        "...wlLLLLLl",
        "...L....rwL",
        "........rw",
        ".......rw",
        "......rw",
        ".....rw",
        "....rw",
        "...rw",
        "..rw",
        ".rw",
    ],
    # ---- Phase 5 items
    "item/nice_list": [
        "",
        "...nnnnnnnnn",
        "..NnnnnnnnnnN",
        "..NnxxxnxxnnN",
        "...nnnnnnnnn",
        "...nxxnxxxxn",
        "...nnnnnnnnn",
        "...nxxxxnxxn",
        "...nnnnnnnnn",
        "...nxxnxxxgn",
        "...nnnnnnnnn",
        "..NnnnnnnnnnN",
        "..NNNNNNNNNNN",
        "...rr",
        "....r",
    ],
    "item/krampus_bell": [
        "",
        ".......ss",
        "......s..s",
        ".......kk",
        "......kKKk",
        ".....kKKKKk",
        ".....kKKKKk",
        ".....kKKKKk",
        "....kKKKKKKk",
        "....kKKKKKKk",
        "...kkkkkkkkkk",
        "......rRr",
        ".......r",
    ],
    "item/krampus_horn": [
        "",
        "...........ee",
        "..........eeE",
        ".........eeE",
        "........eeE",
        ".......eeE",
        "......eeE",
        ".....eeEE",
        "....eeEE",
        "...eEEE",
        "..EEEE",
        "..EEE",
        "..kk",
    ],
    "item/sleigh_bells": [
        "",
        "..tttttttttttt",
        "..t..........t",
        "..t.oO...oO..t",
        "..toOOo.oOOo.t",
        "...oOOo.oOOo",
        "...oooo.oooo",
        "....kk...kk",
        "",
        ".......oO",
        "......oOOo",
        "......oOOo",
        "......oooo",
        ".......kk",
    ],
    "item/letter_to_santa": [
        "", "", "",
        "..nnnnnnnnnnnn",
        "..nNnnnnnnnnNn",
        "..nnNnnnnnnNnn",
        "..nnnNnnnnNnnn",
        "..nnnnNnnNnnnn",
        "..nnnnnrrnnnnn",
        "..nnnnnrrnnnnn",
        "..nnnnnnnnnnnn",
        "..nnnnnnnnnnnn",
        "..NNNNNNNNNNNN",
    ],
    # ---- Santa Suit
    "item/santa_hat": [
        "",
        "............ww",
        "...........wwww",
        "..........rrww",
        ".........rrrR",
        "........rrrrR",
        ".......rrrrrR",
        "......rrrrrrR",
        ".....rrrrrrrR",
        "....rrrrrrrrrR",
        "...rrrrrrrrrrR",
        "..wwwwwwwwwwwww",
        "..WwwwwwwwwwwwW",
        "...WWWWWWWWWWW",
    ],
    "item/santa_coat": [
        "",
        "...rrr....rrr",
        "..rrrrrwwrrrrr",
        ".rrrrrrwwrrrrrr",
        ".rrrrrrwwrrrrrr",
        ".rrr.rrwwrr.rrr",
        ".rrr.rrwwrr.rrr",
        ".rrr.xxoxxx.rrr",
        ".www.rrwwrr.www",
        ".....rrwwrr",
        ".....RrwwrR",
        ".....wwwwww",
    ],
    "item/santa_trousers": [
        "",
        "...xxxoxxx",
        "...rrrrrrr",
        "...rrrrrrR",
        "...rrr.rrR",
        "...rrr.rrR",
        "...rrr.rrR",
        "...rrr.rrR",
        "...rrR.rrR",
        "...www.www",
    ],
    "item/santa_boots": [
        "", "", "", "",
        "..ww.....ww",
        "..WW.....WW",
        "..xx.....xx",
        "..xx.....xx",
        "..xx.....xx",
        ".xxx....xxx",
        "xxxx...xxxx",
        "xxxx...xxxx",
    ],
}


# Spawn eggs (1.21.5 removed the tinted vanilla template; each egg has its own sprite).
# {background, highlight} colours match ModItems.
SPAWN_EGGS = {
    "gingerbread_man": (0xB0682F, 0xFFFFFF), "reindeer": (0x7A5230, 0xE8D8C0),
    "peppermint_pup": (0xFFFFFF, 0xD62834), "elf": (0x2E8B3A, 0xD62834),
    "santa_claus": (0xC81E2A, 0xFFFFFF), "cookie_soldier": (0x9A2A2A, 0xE0B070),
    "gumdrop_blob": (0xEC4078, 0x5AC85A), "evil_snowman": (0xF4F4F4, 0x222222),
    "krampus": (0x2A1A12, 0xB01020),
}
EGG_ROWS = [
    "......XXXX......",
    ".....XBBBBX.....",
    "....XBBSBBBX....",
    "...XBBSSBBBBX...",
    "...XBBBBBHBBX...",
    "..XBBBBBBHHBBX..",
    "..XBBHBBBBBBBX..",
    "..XBHHBBBBBBBX..",
    ".XBBBBBBBHBBBBX.",
    ".XBBBBBBBHHBBBX.",
    ".XBBBHBBBBBBBDX.",
    ".XBBHHBBBBBBBDX.",
    "..XBBBBBBHBBDX..",
    "..XDBBBBBBBDDX..",
    "...XXDDDDDDXX...",
    ".....XXXXXX.....",
]


def spawn_egg(background: int, highlight: int) -> Image.Image:
    bg = ((background >> 16) & 255, (background >> 8) & 255, background & 255)
    hl = ((highlight >> 16) & 255, (highlight >> 8) & 255, highlight & 255)
    return sprite(EGG_ROWS, {"X": shade(bg, 0.45), "B": bg, "S": shade(bg, 1.35), "D": shade(bg, 0.75), "H": hl})


def gift_box(colour: Pixel, ribbon: Pixel) -> tuple[Image.Image, Image.Image]:
    rng = random.Random(str(colour))
    side, top = blank(), blank()
    ribbon_dark = shade(ribbon, 0.75)
    for y in range(16):
        for x in range(16):
            # rows 0-3 of the side texture hold the bow, which the model draws as two crossed planes
            if y < 4:
                if 5 <= x <= 10 and not (y == 0 and x in (5, 10)) and not (y == 3 and x in (7, 8)):
                    side.putpixel((x, y), rgba(ribbon if (x + y) % 3 else ribbon_dark))
                continue
            c = shade(colour, rng.choice([1.0, 0.95, 1.05]))
            if x in (7, 8):
                c = ribbon if x == 7 else ribbon_dark
            if y == 15:
                c = shade(c, 0.8)
            side.putpixel((x, y), rgba(c))
            tc = shade(colour, rng.choice([1.0, 0.95, 1.05]))
            if x in (7, 8) or y in (7, 8):
                tc = ribbon if (x == 7 or y == 7) else ribbon_dark
            top.putpixel((x, y), rgba(tc))
    for y in range(4):
        for x in range(16):
            tc = shade(colour, rng.choice([1.0, 0.95, 1.05]))
            if x in (7, 8):
                tc = ribbon if x == 7 else ribbon_dark
            top.putpixel((x, y), rgba(tc))
    return side, top


def lit_leaves(colour: str) -> Image.Image:
    img = leaves("festive_fir_leaves", FIR_GREEN, 0.14)
    rng = random.Random("lit_" + colour)
    bulb, glow = BULB_COLOURS[colour]
    for _ in range(7):
        x, y = rng.randrange(1, 15), rng.randrange(1, 15)
        img.putpixel((x, y), rgba(glow))
        for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            img.putpixel((x + dx, y + dy), rgba(bulb))
    return img


def gumdrop_leaves() -> Image.Image:
    img = leaves("gumdrop_leaves", [(240, 120, 170), (226, 100, 150), (250, 150, 190)], 0.12)
    rng = random.Random("gumdrops")
    for _ in range(9):
        x, y = rng.randrange(1, 15), rng.randrange(1, 15)
        c = rng.choice(GUMDROP_COLOURS)
        img.putpixel((x, y), rgba(c))
        img.putpixel((x + 1, y), rgba(c))
        img.putpixel((x, y - 1), rgba((255, 255, 255)))
    return img


# ---- Recipe table --------------------------------------------------------------------------

def frosting() -> Image.Image:
    return speckle(noise("frosting", FROST, [5, 4, 3, 1, 1]), "frosting", FROST_SHADE, 10)


def gingerbread() -> Image.Image:
    img = noise("gingerbread", GINGER, [5, 4, 3, 2, 1])
    speckle(img, "gingerbread", GINGER_DARK, 12)
    return speckle(img, "gingerbread2", GINGER_CRUMB, 6)


def frosted_gingerbread_side() -> Image.Image:
    img = gingerbread().copy()
    rng = random.Random("frosted_gingerbread_side")
    for x in range(16):
        depth = rng.choice([2, 3, 3, 3, 4, 4, 5, 6])
        for y in range(depth):
            img.putpixel((x, y), rgba(rng.choice(FROST[:4])))
        img.putpixel((x, depth), rgba(FROST_SHADE))
    return img


def peppermint_stone() -> Image.Image:
    img, rng = noise("peppermint_stone", MINT_WHITE, [4, 3, 2]), random.Random("peppermint_stone")
    for y in range(16):
        for x in range(16):
            band = (x + y + (1 if rng.random() < 0.15 else 0)) % 8
            if band in (0, 1):
                img.putpixel((x, y), rgba(rng.choice(MINT_RED)))
            elif band == 2:
                img.putpixel((x, y), rgba(MINT_SHADE))
    return img


def peppermint_bricks() -> Image.Image:
    img = bricks("peppermint_bricks", MINT_WHITE, MINT_SHADE)
    for y in range(16):  # a red stripe through every brick
        for x in range(16):
            if y % 4 == 1 and (x + (4 * ((y // 4) % 2))) % 8 not in (0,):
                img.putpixel((x, y), rgba(MINT_RED[(x + y) % 3]))
    return img


def cookie_tiles() -> Image.Image:
    img, rng = blank(), random.Random("cookie_tiles")
    for y in range(16):
        for x in range(16):
            tx, ty = x % 8, y % 8
            edge = tx in (0, 7) or ty in (0, 7)
            c = COOKIE_DARK if edge else rng.choice(COOKIE)
            if not edge and rng.random() < 0.08:
                c = (90, 52, 30)  # chocolate chip
            img.putpixel((x, y), rgba(c))
    return img


def candy_cane_log() -> Image.Image:
    return stripes("candy_cane_log", CANE_RED, CANE_WHITE, 3, 6)


def stripped_candy_cane_log() -> Image.Image:
    return stripes("stripped_candy_cane_log", [(240, 170, 170), (232, 160, 160)], CANE_WHITE, 3, 6)


def candy_cane_log_top() -> Image.Image:
    img, rng = blank(), random.Random("candy_cane_log_top")
    import math
    for y in range(16):
        for x in range(16):
            ang = math.atan2(y - 7.5, x - 7.5)
            r = math.hypot(x - 7.5, y - 7.5)
            spiral = int((ang / math.tau * 6 + r / 3) % 2)
            c = rng.choice(CANE_RED if spiral else CANE_WHITE)
            img.putpixel((x, y), rgba(c))
    return img


def stripped_candy_cane_log_top() -> Image.Image:
    return noise("stripped_candy_cane_log_top", CANE_WHITE + [(240, 200, 200)])


def sugar_crystal_ore() -> Image.Image:
    return ore("sugar_crystal_ore", peppermint_stone(), CRYSTAL, 5)


def cinnamon_ore() -> Image.Image:
    return ore("cinnamon_ore", peppermint_stone(), CINNAMON, 6)


def sugar_crystal_block() -> Image.Image:
    img = bricks("sugar_crystal_block", CRYSTAL, (130, 190, 222), 8, 8)
    return speckle(img, "sugar_crystal_block", (255, 255, 255), 8)


def advent_front() -> Image.Image:
    """24 little numbered doors in a 6x4 grid on a red board, with a gold star door for Christmas."""
    img = planks("advent_front", [(190, 30, 40), (178, 24, 34), (204, 40, 50)])
    rng = random.Random("advent")
    doors = [(196, 40, 50), (40, 140, 70), (240, 200, 70), (240, 240, 240)]
    for row in range(4):
        for col in range(4):
            x0, y0 = 1 + col * 4 - (1 if col else 0), 1 + row * 4 - (1 if row else 0)
            colour = doors[(row * 4 + col) % 4] if (row, col) != (3, 3) else GOLD
            for y in range(y0, y0 + 3):
                for x in range(x0, x0 + 3):
                    img.putpixel((x, y), rgba(shade(colour, rng.choice([0.95, 1.0, 1.05]))))
            img.putpixel((x0 + 2, y0 + 1), rgba(GOLD_DARK))   # door knob
    return img


def cookie_jar_side() -> Image.Image:
    img, rng = blank(), random.Random("cookie_jar")
    for y in range(6, 16):
        for x in range(4, 12):
            edge = x in (4, 11) or y == 15
            img.putpixel((x, y), (190, 220, 235, 255) if edge else (220, 238, 246, 255))
    for cx, cy in ((6, 13), (9, 12), (7, 10), (9, 14)):     # cookies inside
        for dx, dy in ((0, 0), (1, 0), (0, 1), (1, 1)):
            img.putpixel((cx + dx, cy + dy), rgba(COOKIE[rng.randrange(3)]))
        img.putpixel((cx, cy), rgba((90, 52, 30)))
    for x in (5, 6):
        img.putpixel((x, 7), (255, 255, 255, 255))           # glint
    return img


def cookie_jar_top() -> Image.Image:
    img = blank()
    for y in range(16):
        for x in range(16):
            img.putpixel((x, y), rgba(shade(FIR_PLANK[0], 1.0 if (x + y) % 3 else 0.9)))
    return img


BLOCKS: dict[str, Callable[[], Image.Image]] = {
    "advent_calendar_front": advent_front,
    "advent_calendar_side": lambda: planks("advent_side", [(190, 30, 40), (178, 24, 34), (204, 40, 50)]),
    "advent_calendar_top": lambda: planks("advent_top", FIR_PLANK),
    "cookie_jar": cookie_jar_side,
    "cookie_jar_top": cookie_jar_top,
    "frosting": frosting,
    "gingerbread": gingerbread,
    "frosted_gingerbread_side": frosted_gingerbread_side,
    "sugar_sand": lambda: speckle(noise("sugar_sand", SAND, [4, 3, 2, 1]), "sugar_sand", (255, 255, 255), 14),
    "marshmallow_block": lambda: speckle(noise("marshmallow", MALLOW, [5, 3, 1]), "marshmallow", (236, 226, 230), 6),
    "peppermint_stone": peppermint_stone,
    "peppermint_bricks": peppermint_bricks,
    "icing_bricks": lambda: bricks("icing_bricks", FROST[:4], FROST_SHADE),
    "gingerbread_bricks": lambda: bricks("gingerbread_bricks", GINGER, (240, 236, 244)),
    "cookie_tiles": cookie_tiles,
    "candy_glass": lambda: glass("candy_glass", (250, 160, 200), 110),
    "candy_glass_pane_top": lambda: glass("candy_glass_pane_top", (236, 130, 180), 220),
    # candy cane wood
    "candy_cane_log": candy_cane_log,
    "candy_cane_log_top": candy_cane_log_top,
    "stripped_candy_cane_log": stripped_candy_cane_log,
    "stripped_candy_cane_log_top": stripped_candy_cane_log_top,
    "candy_cane_planks": lambda: planks("candy_cane_planks", CANE_PLANK),
    "candy_cane_door_top": lambda: door_half("candy_cane_door_top", CANE_PLANK, True),
    "candy_cane_door_bottom": lambda: door_half("candy_cane_door_bottom", CANE_PLANK, False),
    "candy_cane_trapdoor": lambda: trapdoor("candy_cane_trapdoor", CANE_PLANK),
    "gumdrop_leaves": gumdrop_leaves,
    # festive fir wood
    "festive_fir_log": lambda: bark("festive_fir_log", FIR_BARK),
    "festive_fir_log_top": lambda: log_top("festive_fir_log_top", FIR_BARK, FIR_INNER),
    "stripped_festive_fir_log": lambda: bark("stripped_festive_fir_log", FIR_INNER),
    "stripped_festive_fir_log_top": lambda: log_top("stripped_festive_fir_log_top", FIR_INNER, FIR_INNER),
    "festive_fir_planks": lambda: planks("festive_fir_planks", FIR_PLANK),
    "festive_fir_door_top": lambda: door_half("festive_fir_door_top", FIR_PLANK, True),
    "festive_fir_door_bottom": lambda: door_half("festive_fir_door_bottom", FIR_PLANK, False),
    "festive_fir_trapdoor": lambda: trapdoor("festive_fir_trapdoor", FIR_PLANK),
    "festive_fir_leaves": lambda: leaves("festive_fir_leaves", FIR_GREEN, 0.14),
    "red_lit_festive_fir_leaves": lambda: lit_leaves("red"),
    "gold_lit_festive_fir_leaves": lambda: lit_leaves("gold"),
    "blue_lit_festive_fir_leaves": lambda: lit_leaves("blue"),
    # ores
    "sugar_crystal_ore": sugar_crystal_ore,
    "cinnamon_ore": cinnamon_ore,
    "sugar_crystal_block": sugar_crystal_block,
}

for _name, (_c, _r) in {"red_gift_box": ((200, 36, 44), GOLD), "green_gift_box": ((40, 140, 70), (240, 60, 60)),
                        "blue_gift_box": ((50, 100, 210), (250, 250, 250))}.items():
    BLOCKS[_name + "_side"] = (lambda c=_c, r=_r: gift_box(c, r)[0])
    BLOCKS[_name + "_top"] = (lambda c=_c, r=_r: gift_box(c, r)[1])


# ---- Snow globe (kept from the first pass) -------------------------------------------------

GLASS_EDGE, GLASS_C, GLASS_HI = (176, 214, 236), (214, 236, 248), (250, 252, 255)
BASE = [(122, 62, 34), (148, 80, 44), (98, 48, 26)]


def snow_globe() -> Image.Image:
    img = blank()
    rng = random.Random("snow_globe")
    cx, cy, r = 7.5, 6.5, 6.0
    for y in range(16):
        for x in range(16):
            d = ((x - cx) ** 2 + (y - cy) ** 2) ** 0.5
            if d <= r - 0.9 and y <= 11:
                img.putpixel((x, y), rgba(GLASS_C))
            elif d <= r + 0.2 and y <= 11:
                img.putpixel((x, y), rgba(GLASS_EDGE))
    for x in range(3, 13):
        img.putpixel((x, 11), rgba(FROST[0]))
        img.putpixel((x, 10), rgba(FROST[2]))
    for y in range(7, 10):
        for x in range(6, 10):
            img.putpixel((x, y), rgba(GINGER[0]))
    img.putpixel((7, 9), rgba(GINGER_DARK))
    for x, y in ((5, 7), (6, 6), (7, 5), (8, 5), (9, 6), (10, 7)):
        img.putpixel((x, y), rgba((200, 40, 50)))
    for _ in range(5):
        img.putpixel((rng.randrange(4, 12), rng.randrange(2, 5)), rgba(GLASS_HI))
    for x, y in ((4, 3), (3, 4), (3, 5)):
        img.putpixel((x, y), rgba(GLASS_HI))
    for y in range(12, 16):
        for x in range(2 if y > 12 else 3, 14 if y > 12 else 13):
            colour = GOLD if y == 13 else BASE[0] if y < 15 else BASE[2]
            img.putpixel((x, y), rgba(colour))
    return img


ITEMS: dict[str, Callable[[], Image.Image]] = {"snow_globe": snow_globe}

for _key, _rows in SPRITES.items():
    _folder, _name = _key.split("/")
    (ITEMS if _folder == "item" else BLOCKS)[_name] = (lambda rows=_rows: sprite(rows, P))



# ---- Armor layers (64x32 humanoid armor textures) ------------------------------------------

ARMOR_DIR = ASSETS / "textures/models/armor"
SUIT_RED = [(198, 30, 42), (186, 24, 36), (210, 40, 50)]
SUIT_WHITE = [(250, 250, 250), (238, 238, 242)]
BELT = (30, 26, 28)


def _box(img: Image.Image, u: int, v: int, w: int, h: int, d: int, paint) -> None:
    """Paints a Minecraft model box UV layout (top/bottom row of height d, then the side strip of height h).
    paint(x_in_face, y_in_face, face, face_w, face_h) returns a colour or None."""
    faces = {  # name: (x, y, width, height)
        "top": (u + d, v, w, d), "bottom": (u + d + w, v, w, d),
        "right": (u, v + d, d, h), "front": (u + d, v + d, w, h),
        "left": (u + d + w, v + d, d, h), "back": (u + d + w + d, v + d, w, h),
    }
    for face, (fx, fy, fw, fh) in faces.items():
        for y in range(fh):
            for x in range(fw):
                c = paint(x, y, face, fw, fh)
                if c is not None:
                    img.putpixel((fx + x, fy + y), rgba(c))


def santa_layers() -> tuple[Image.Image, Image.Image]:
    rng = random.Random("santa_suit")
    red = lambda: rng.choice(SUIT_RED)
    white = lambda: rng.choice(SUIT_WHITE)
    l1 = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    l2 = Image.new("RGBA", (64, 32), (0, 0, 0, 0))

    # Hat: red with a white fur brim; a white pompom on top
    def hat(x, y, face, w, h):
        if face == "top":
            return white() if 2 <= x <= 5 and 2 <= y <= 5 else red()
        if face == "bottom":
            return None
        return white() if y >= h - 2 else red()
    _box(l1, 0, 0, 8, 8, 8, hat)

    # Coat: red, white fur down the front and at the hem, black belt with a gold buckle
    def coat(x, y, face, w, h):
        if face in ("top", "bottom"):
            return red()
        if y == 8:
            return GOLD if face == "front" and x in (3, 4) else BELT
        if y >= h - 1:
            return white()
        if face == "front" and x in (3, 4):
            return white()
        return red()
    _box(l1, 16, 16, 8, 12, 4, coat)

    # Sleeves: red with white cuffs
    def sleeve(x, y, face, w, h):
        if face == "top":
            return red()
        if face == "bottom":
            return white()
        return white() if y >= h - 2 else red()
    _box(l1, 40, 16, 4, 12, 4, sleeve)

    # Boots (layer 1 legs): black boots with a white fur top
    def boots(x, y, face, w, h):
        if face == "top":
            return None
        if face == "bottom":
            return BELT
        if y < 6:
            return None
        return white() if y in (6, 7) else (shade(BELT, 1.3) if (x + y) % 5 == 0 else BELT)
    _box(l1, 0, 16, 4, 12, 4, boots)

    # Trousers (layer 2): red legs, a red waistband
    def trousers(x, y, face, w, h):
        if face in ("top", "bottom"):
            return red()
        return red()
    _box(l2, 0, 16, 4, 12, 4, trousers)

    def waist(x, y, face, w, h):
        if face in ("top", "bottom"):
            return None
        if y < 8:
            return None
        return BELT if y == 8 else red()
    _box(l2, 16, 16, 8, 12, 4, waist)
    return l1, l2


def santa_hat() -> Image.Image:
    """64x64 texture for the 3D Santa hat model (client/SantaHatModel.java): brim, three cone
    segments, tip and pom-pom, each at its texOffs."""
    rng = random.Random("santa_hat")
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    fur = lambda: rng.choice([(252, 252, 252), (240, 240, 244), (230, 230, 236)])

    def red_part(shade_top: float):
        def paint(x, y, face, w, h):
            c = rng.choice(SUIT_RED)
            if face in ("right", "left", "front", "back") and y == 0:
                c = shade(c, shade_top)            # a soft fold line at the top of each segment
            if face == "bottom":
                c = shade(c, 0.75)
            return c
        return paint

    _box(img, 0, 0, 9, 3, 9, lambda x, y, face, w, h: fur())          # brim
    _box(img, 0, 12, 8, 4, 8, red_part(1.08))                         # base
    _box(img, 0, 24, 6, 4, 6, red_part(1.08))                         # mid
    _box(img, 24, 24, 4, 3, 4, red_part(1.1))                         # top
    _box(img, 40, 24, 2, 3, 2, red_part(1.1))                         # tip
    _box(img, 0, 34, 3, 3, 3, lambda x, y, face, w, h: fur())         # pom-pom
    return img


def write_armor() -> None:
    ARMOR_DIR.mkdir(parents=True, exist_ok=True)
    l1, l2 = santa_layers()
    l1.save(ARMOR_DIR / "santa_suit_layer_1.png")
    l2.save(ARMOR_DIR / "santa_suit_layer_2.png")
    santa_hat().save(ARMOR_DIR / "santa_hat_layer_1.png")
    # 1.21.2+ equipment textures (referenced by models/equipment/*.json)
    equipment = ASSETS / "textures/entity/equipment"
    (equipment / "humanoid").mkdir(parents=True, exist_ok=True)
    (equipment / "humanoid_leggings").mkdir(parents=True, exist_ok=True)
    l1.save(equipment / "humanoid/santa_suit.png")
    l2.save(equipment / "humanoid_leggings/santa_suit.png")
    santa_hat().save(equipment / "humanoid/santa_hat.png")
    print("wrote armor layers")


# ---- Missing-texture check -----------------------------------------------------------------

def referenced_textures() -> set[str]:
    """Every christmas_grove texture that a block or item model refers to."""
    found: set[str] = set()
    for base in (ASSETS, GENERATED):
        for model in (base / "models").rglob("*.json"):
            data = json.loads(model.read_text(encoding="utf-8"))
            for value in data.get("textures", {}).values():
                if isinstance(value, str) and value.startswith("christmas_grove:"):
                    found.add(value.split(":", 1)[1])
    return found


def check() -> int:
    missing = sorted(t for t in referenced_textures() if not (ASSETS / "textures" / f"{t}.png").exists())
    for t in missing:
        print(f"MISSING textures/{t}.png")
    print(f"{len(missing)} missing texture(s)")
    return 1 if missing else 0


def main() -> None:
    if "--check" in sys.argv:
        sys.exit(check())
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
    for mob, (bg, hl) in SPAWN_EGGS.items():
        img = spawn_egg(bg, hl)
        img.save(ITEM_DIR / f"{mob}_spawn_egg.png")
        made.append((f"{mob}_spawn_egg", img))
    print(f"wrote {len(made)} textures")
    write_armor()

    if "--preview" in sys.argv and made:
        scale, pad, per_row = 5, 6, 12
        rows = (len(made) + per_row - 1) // per_row
        cell = 16 * scale + pad
        sheet = Image.new("RGBA", (per_row * cell + pad, rows * cell + pad), (70, 74, 90, 255))
        for i, (_, img) in enumerate(made):
            x, y = pad + (i % per_row) * cell, pad + (i // per_row) * cell
            sheet.alpha_composite(img.resize((16 * scale, 16 * scale), Image.NEAREST), (x, y))
        sheet.save(PREVIEW)
        print(f"preview -> {PREVIEW}")


if __name__ == "__main__":
    main()
