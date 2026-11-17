"""Renders crafting-recipe cards for the store page: a 3x3 grid, an arrow, the result and its name.

Mod sprites come from the mod's textures; vanilla ingredient icons are read from the local Minecraft
client jar (Loom's cache) at render time and only appear inside the rendered card images.

Outputs: docs/showcase/recipes_getting_started.png, docs/showcase/recipes_more.png
Usage:   py tools/showcase/gen_recipe_cards.py
"""
from __future__ import annotations

import io
import sys
import zipfile
from pathlib import Path

from PIL import Image

sys.path.insert(0, str(Path(__file__).resolve().parent))
from gen_showcase_art import CREAM, GOLD, draw_text, night_sky, text_width  # noqa: E402

ROOT = Path(__file__).resolve().parents[2]
MOD = ROOT / "common/src/main/resources/assets/christmas_grove/textures"
OUT = ROOT / "docs/showcase"
CLIENT_JAR = next((Path.home() / ".gradle/caches/fabric-loom/1.21.1").glob("minecraft-client.jar"), None)

_jar = zipfile.ZipFile(CLIENT_JAR) if CLIENT_JAR else None


def icon(name: str) -> Image.Image:
    """'cg:item/x' for mod textures, 'mc:item/x' for vanilla ones."""
    ns, path = name.split(":", 1)
    if ns == "cg":
        return Image.open(MOD / f"{path}.png").convert("RGBA")
    if _jar is None:
        raise SystemExit("Minecraft client jar not found in the Loom cache; run a Gradle build first")
    return Image.open(io.BytesIO(_jar.read(f"assets/minecraft/textures/{path}.png"))).convert("RGBA").crop((0, 0, 16, 16))


# key: (title, grid rows of icon names or None, result icon, result name)
GETTING_STARTED = [
    ("SNOW GLOBE - THE WAY IN", [["mc:block/glass"] * 3, ["mc:block/glass", "mc:item/cookie", "mc:block/glass"],
                                 ["mc:block/oak_planks", "mc:block/snow", "mc:block/oak_planks"]], "cg:item/snow_globe", "SNOW GLOBE"),
    ("LETTER TO SANTA", [["mc:item/paper", "mc:item/feather", None], ["mc:item/ink_sac", "cg:item/candy_cane", None], [None] * 3],
     "cg:item/letter_to_santa", "LETTER TO SANTA"),
    ("WRAP YOUR OWN PRESENT", [["mc:item/paper", "mc:item/paper", None], ["mc:item/string", "mc:item/red_dye", None], [None] * 3],
     "cg:block/red_gift_box_side", "GIFT BOX"),
    ("ARE YOU NAUGHTY OR NICE?", [["mc:item/paper", "mc:item/paper", None], ["mc:item/feather", "mc:item/ink_sac", None], [None] * 3],
     "cg:item/nice_list", "NICE LIST"),
]
MORE = [
    ("SUMMON KRAMPUS", [["mc:item/bell", "mc:item/chain", None], ["mc:item/coal", None, None], [None] * 3],
     "cg:item/krampus_bell", "KRAMPUS BELL"),
    ("REINDEER GLIDE", [["mc:item/gold_nugget", "mc:item/leather", "mc:item/gold_nugget"],
                        ["mc:item/gold_ingot", None, "mc:item/gold_ingot"], [None] * 3], "cg:item/sleigh_bells", "SLEIGH BELLS"),
    ("ADVENT CALENDAR", [["cg:block/festive_fir_planks"] * 3, ["cg:block/festive_fir_planks", "cg:item/candy_cane",
                         "cg:block/festive_fir_planks"], ["cg:block/festive_fir_planks"] * 3],
     "cg:block/advent_calendar_front", "ADVENT CALENDAR"),
    ("HOT COCOA", [["cg:item/mug", "mc:item/cocoa_beans", None], ["mc:item/sugar", "mc:item/milk_bucket", None], [None] * 3],
     "cg:item/hot_cocoa", "HOT COCOA"),
    ("CANDY CANE SWORD", [[None, "cg:item/sugar_crystal", None], [None, "cg:item/sugar_crystal", None],
                          [None, "cg:item/candy_cane", None]], "cg:item/candy_cane_sword", "CANDY CANE SWORD"),
    ("GINGERBREAD COOKIES", [["mc:item/wheat", "mc:item/wheat", None], ["cg:item/cinnamon_stick", "mc:item/sugar", None],
                             [None] * 3], "cg:item/gingerbread_cookie", "GINGERBREAD COOKIE X4"),
]

SLOT, PAD = 18, 1
SLOT_DARK, SLOT_LIGHT, SLOT_BG = (55, 55, 70), (140, 140, 160), (98, 98, 116)


def slot(img: Image.Image, x: int, y: int, size: int = SLOT) -> None:
    for yy in range(size):
        for xx in range(size):
            edge_dark = xx == 0 or yy == 0
            edge_light = xx == size - 1 or yy == size - 1
            c = SLOT_DARK if edge_dark else SLOT_LIGHT if edge_light else SLOT_BG
            img.putpixel((x + xx, y + yy), c + (255,))


def card(title: str, grid, result: str, name: str) -> Image.Image:
    w, h = 150, 86
    img = Image.new("RGBA", (w, h), (30, 24, 54, 255))
    for x in range(w):           # gold frame
        img.putpixel((x, 0), GOLD + (255,))
        img.putpixel((x, h - 1), GOLD + (255,))
    for y in range(h):
        img.putpixel((0, y), GOLD + (255,))
        img.putpixel((w - 1, y), GOLD + (255,))
    draw_text(img, title, (w - text_width(title, 1)) // 2, 4, 1, CREAM)
    gx, gy = 10, 16
    for r in range(3):
        for c in range(3):
            x, y = gx + c * SLOT, gy + r * SLOT
            slot(img, x, y)
            if grid[r][c]:
                img.alpha_composite(icon(grid[r][c]), (x + 1, y + 1))
    # arrow
    ax, ay = gx + 3 * SLOT + 8, gy + SLOT + 5
    for i in range(14):
        for t in range(3):
            img.putpixel((ax + i, ay + 3 + t), CREAM + (255,))
    for i in range(5):
        for t in range(-i, i + 1):
            img.putpixel((ax + 14 + (4 - i), ay + 4 + t), CREAM + (255,))
    rx, ry = ax + 26, gy + SLOT - 4
    slot(img, rx, ry, 26)
    img.alpha_composite(icon(result), (rx + 5, ry + 5))
    draw_text(img, name, max(2, (w - text_width(name, 1)) // 2), h - 11, 1, GOLD)
    return img


def sheet(title: str, recipes, cols: int, path: Path) -> None:
    cards = [card(*r) for r in recipes]
    cw, ch, gap = cards[0].width, cards[0].height, 8
    rows = (len(cards) + cols - 1) // cols
    w, h = cols * cw + (cols + 1) * gap, rows * ch + (rows + 1) * gap + 18
    img = night_sky(w, h, title, 50)
    draw_text(img, title, (w - text_width(title, 1)) // 2, 6, 1, CREAM, shadow=(120, 20, 30))
    for i, c in enumerate(cards):
        img.alpha_composite(c, (gap + (i % cols) * (cw + gap), 18 + gap + (i // cols) * (ch + gap)))
    img.resize((w * 4, h * 4), Image.NEAREST).save(path, optimize=True)
    print("wrote", path.name, img.width * 4, "x", img.height * 4)


def main() -> None:
    sheet("GETTING STARTED", GETTING_STARTED, 2, OUT / "recipes_getting_started.png")
    sheet("MORE RECIPES", MORE, 3, OUT / "recipes_more.png")


if __name__ == "__main__":
    main()
