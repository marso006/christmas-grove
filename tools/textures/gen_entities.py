"""Generates the entity (mob) textures for Gingerbread Grove.

Humanoid skins are painted face by face onto the standard 64x64 box layout. Mobs that reuse vanilla
geometry (reindeer = horse, pup = wolf, gumdrop blob = slime, evil snowman = snow golem) are painted
inside 1-bit UV layout masks (tools/textures/uv/*_layout.png) so the colours land where the model
samples. All colours and patterns here are original.

Usage: py tools/textures/gen_entities.py [--preview]
"""
from __future__ import annotations

import random
import sys
from pathlib import Path
from typing import Callable, Optional

from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / "common/src/main/resources/assets/gingerbread_grove/textures/entity"
UV = ROOT / "tools/textures/uv"
PREVIEW = ROOT / "tools/textures/preview_entities.png"

Colour = tuple[int, int, int]
Paint = Callable[[int, int, str, int, int], Optional[Colour]]

SKIN = (240, 196, 160)
SKIN_SHADE = (220, 170, 136)
WHITE = (250, 250, 252)
OFFWHITE = (232, 232, 238)
BLACK = (28, 24, 26)
RED = (198, 30, 42)
RED_DARK = (160, 20, 32)
GREEN = (46, 139, 58)
GREEN_DARK = (30, 100, 40)
GOLD = (236, 190, 72)
GINGER = [(176, 104, 52), (166, 96, 46), (186, 114, 60)]
BLUE = (40, 70, 160)


def rgba(c: Colour, a: int = 255) -> tuple[int, int, int, int]:
    return (c[0], c[1], c[2], a)


def box(img: Image.Image, u: int, v: int, w: int, h: int, d: int, paint: Paint) -> None:
    """Paints one model box laid out at texOffs(u, v) with size w x h x d.
    paint(x, y, face, face_w, face_h) -> colour or None (leave transparent)."""
    faces = {
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


def jitter(rng: random.Random, c: Colour, amount: float = 0.06) -> Colour:
    f = 1.0 + rng.uniform(-amount, amount)
    return tuple(max(0, min(255, int(v * f))) for v in c)  # type: ignore[return-value]


def humanoid(parts: dict[str, Paint], extras: list[tuple[int, int, int, int, int, Paint]] = ()) -> Image.Image:
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    layout = {"head": (0, 0, 8, 8, 8), "hat": (32, 0, 8, 8, 8), "body": (16, 16, 8, 12, 4),
              "arm": (40, 16, 4, 12, 4), "leg": (0, 16, 4, 12, 4)}
    for name, paint in parts.items():
        u, v, w, h, d = layout[name]
        box(img, u, v, w, h, d, paint)
    for u, v, w, h, d, paint in extras:
        box(img, u, v, w, h, d, paint)
    return img


# ---- Humanoids -----------------------------------------------------------------------------

def gingerbread_man() -> Image.Image:
    rng = random.Random("gingerbread_man")
    dough = lambda: jitter(rng, rng.choice(GINGER))

    def head(x, y, face, w, h):
        if face == "front":
            if y == 3 and x in (2, 5):
                return WHITE                      # icing eyes
            if (y == 5 and x in (2, 5)) or (y == 6 and 3 <= x <= 4):
                return WHITE                      # icing smile
        return dough()

    def body(x, y, face, w, h):
        if face == "front" and x in (3, 4) and y in (2, 5, 8):
            return [RED, GREEN, RED][(y - 2) // 3]   # gumdrop buttons
        return dough()

    def limb(x, y, face, w, h):
        if face in ("top",):
            return dough()
        if y in (h - 3,) and face != "bottom":
            return WHITE if (x + y) % 2 == 0 else dough()   # icing zigzag at wrists/ankles
        return dough()

    return humanoid({"head": head, "body": body, "arm": limb, "leg": limb})


def elf() -> Image.Image:
    rng = random.Random("elf")

    def head(x, y, face, w, h):
        if face == "top":
            return (110, 70, 40)
        if face == "front":
            if y < 2:
                return (110, 70, 40)              # fringe
            if y == 3 and x in (2, 5):
                return BLACK
            if y == 6 and 2 <= x <= 5:
                return (200, 90, 90)              # smile
            if y == 4 and x in (1, 6):
                return (240, 160, 150)            # rosy cheeks
            return jitter(rng, SKIN, 0.03)
        return (110, 70, 40) if y < 5 else jitter(rng, SKIN, 0.03)

    def hat(x, y, face, w, h):                    # the floppy hat sits on the hat overlay
        if face == "bottom":
            return None
        if face == "top":
            return RED
        if y < 2:
            return RED
        if y == 2:
            return WHITE                          # fur brim
        return None

    def body(x, y, face, w, h):
        if y == 7:
            return GOLD if face == "front" and x in (3, 4) else BLACK   # belt
        if face == "front" and y < 3 and x in (3, 4):
            return WHITE                          # collar
        return jitter(rng, GREEN, 0.05)

    def arm(x, y, face, w, h):
        if face == "bottom" or y >= h - 2:
            return jitter(rng, SKIN, 0.03)
        if y == h - 3:
            return RED
        return jitter(rng, GREEN, 0.05)

    def leg(x, y, face, w, h):
        if face == "bottom" or y >= h - 2:
            return GREEN_DARK                     # curly shoes
        return RED if (y // 2) % 2 == 0 else WHITE   # striped stockings

    skin = lambda x, y, face, w, h: jitter(rng, SKIN, 0.03)
    tip = lambda x, y, face, w, h: WHITE if (face == "top" or y == 0) else RED
    return humanoid({"head": head, "hat": hat, "body": body, "arm": arm, "leg": leg},
                    [(0, 48, 2, 3, 1, skin), (8, 48, 2, 3, 1, skin), (16, 48, 4, 4, 4, tip)])


def santa_claus() -> Image.Image:
    rng = random.Random("santa")
    red = lambda: jitter(rng, RED, 0.05)

    def head(x, y, face, w, h):
        if face == "front":
            if y < 2:
                return WHITE                      # hair under the hat brim
            if y == 2 and x in (1, 2, 5, 6):
                return WHITE                      # bushy eyebrows
            if y == 3 and x in (2, 5):
                return (40, 90, 160)              # blue eyes
            if y == 4 and x in (3, 4):
                return (230, 150, 140)            # rosy nose
            if y >= 5:
                return WHITE                      # moustache and beard
            return jitter(rng, SKIN, 0.03)
        if face in ("right", "left") and y >= 2:
            return WHITE if y >= 4 or x < 3 else jitter(rng, SKIN, 0.03)
        return WHITE

    def coat(x, y, face, w, h):
        if face in ("top", "bottom"):
            return red()
        if y == 7:
            return GOLD if face == "front" and x in (3, 4) else BLACK
        if y >= h - 1:
            return WHITE
        if face == "front" and x in (3, 4):
            return WHITE
        return red()

    def arm(x, y, face, w, h):
        if face == "bottom" or y >= h - 1:
            return BLACK                          # gloves
        if y >= h - 3:
            return WHITE                          # fur cuffs
        return red()

    def leg(x, y, face, w, h):
        if face == "bottom" or y >= h - 4:
            return BLACK if y != h - 4 else WHITE  # boots with fur tops
        return red()

    def belly(x, y, face, w, h):
        if y == 3:
            return GOLD if face == "front" and x in (4, 5) else BLACK
        if face == "front" and x in (4, 5):
            return WHITE
        return red()

    beard = lambda x, y, face, w, h: OFFWHITE if (x + y) % 3 == 0 else WHITE
    return humanoid({"head": head, "body": coat, "arm": arm, "leg": leg},
                    [(0, 32, 9, 6, 2, belly), (24, 32, 8, 6, 1, beard)])


def cookie_soldier() -> Image.Image:
    rng = random.Random("cookie_soldier")
    cookie = lambda: jitter(rng, (222, 176, 112), 0.05)

    def head(x, y, face, w, h):
        if face == "front":
            if y == 3 and x in (2, 5):
                return BLACK
            if y == 4 and x in (1, 6):
                return (220, 90, 90)              # painted cheeks
            if y == 5 and 1 <= x <= 6:
                return WHITE                      # big moustache
            if y == 6 and 2 <= x <= 5:
                return BLACK                      # nutcracker jaw line
        return cookie()

    def jacket(x, y, face, w, h):
        if face in ("top", "bottom"):
            return RED_DARK
        if face == "front" and (x == y // 1 % 8 or x == 7 - (y % 8)) and y < 8:
            return WHITE                          # crossed belts
        if face == "front" and x in (3, 4) and y in (2, 5, 8, 10):
            return GOLD                           # buttons
        if y == 9:
            return BLACK
        return jitter(rng, RED, 0.04)

    def arm(x, y, face, w, h):
        if face == "top" and True:
            return GOLD                           # epaulettes
        if face == "bottom" or y >= h - 1:
            return WHITE                          # gloves
        if y == h - 3:
            return GOLD
        return jitter(rng, RED, 0.04)

    def leg(x, y, face, w, h):
        if face == "bottom" or y >= h - 4:
            return BLACK
        if face in ("left", "right") and x == 1:
            return GOLD                           # trouser stripe
        return jitter(rng, BLUE, 0.05)

    def shako(x, y, face, w, h):
        if face == "front" and 3 <= x <= 5 and 1 <= y <= 4:
            return GOLD                           # badge
        if face == "top":
            return RED if 3 <= x <= 5 and 3 <= y <= 5 else BLACK   # plume base
        if y == h - 1:
            return GOLD
        return BLACK

    return humanoid({"head": head, "body": jacket, "arm": arm, "leg": leg},
                    [(0, 32, 9, 6, 9, shako)])


# ---- Mask-based mobs -----------------------------------------------------------------------

def from_mask(name: str, size: tuple[int, int], paint: Callable[[int, int], Optional[tuple[int, ...]]]) -> Image.Image:
    mask = Image.open(UV / f"{name}_layout.png")
    img = Image.new("RGBA", size, (0, 0, 0, 0))
    for y in range(size[1]):
        for x in range(size[0]):
            if mask.getpixel((x, y)):
                c = paint(x, y)
                if c is not None:
                    img.putpixel((x, y), c if len(c) == 4 else rgba(c))  # type: ignore[arg-type]
    return img


def reindeer() -> Image.Image:
    rng = random.Random("reindeer")
    fur = [(122, 82, 48), (112, 74, 42), (132, 90, 54)]

    def paint(x, y):
        if 56 <= x and 36 <= y < 54:
            return jitter(rng, (84, 56, 34))                   # darker mane
        if 42 <= x < 50 and 36 <= y < 50:
            return jitter(rng, (232, 220, 200))                # pale tail
        if 48 <= x and 33 <= y < 36:
            return (50, 36, 26)                                # hooves
        if 26 <= x < 50 and y < 18:
            return jitter(rng, (90, 56, 34))                   # saddle leather
        if (x, y) in ((2, 21), (3, 21), (16, 21), (17, 21)):
            return BLACK                                       # eyes
        if 5 <= x < 9 and y == 34:
            return BLACK                                       # nose
        return jitter(rng, rng.choice(fur))

    img = from_mask("horse", (64, 64), paint)
    # Antlers live in a free part of the layout, (48,0) to (60,9).
    for y in range(9):
        for x in range(48, 60):
            img.putpixel((x, y), rgba(jitter(rng, (214, 190, 150))))
    return img


def peppermint_pup(angry: bool) -> Image.Image:
    rng = random.Random("pup")

    def paint(x, y):
        if (x, y) in ((5, 6), (8, 6)):
            return (200, 30, 30) if angry else BLACK           # eyes
        if 4 <= x < 7 and y == 14:
            return BLACK                                       # nose
        if (x + y) % 6 < 2:
            return jitter(rng, RED, 0.04)                      # peppermint stripes
        return jitter(rng, WHITE, 0.02)

    return from_mask("wolf", (64, 32), paint)


FLAVOURS = {"cherry": (226, 40, 80), "lime": (90, 200, 70), "lemon": (250, 210, 50), "grape": (150, 80, 210)}


def gumdrop(colour: Colour, name: str) -> Image.Image:
    rng = random.Random(name)
    dark = tuple(int(v * 0.75) for v in colour)

    def paint(x, y):
        if x >= 32:
            return BLACK                                       # eyes and mouth
        if y < 16:
            if rng.random() < 0.08:
                return (255, 255, 255, 200)                    # sugar crystals on the outside
            return rgba(jitter(rng, colour, 0.05), 170)        # translucent outer jelly
        return dark                                            # solid core

    return from_mask("slime", (64, 32), paint)


def evil_snowman() -> Image.Image:
    rng = random.Random("snowman")

    def paint(x, y):
        # head front is (8,8)-(16,16)
        if 8 <= x < 16 and 8 <= y < 16:
            fx, fy = x - 8, y - 8
            if (fy == 2 and fx in (1, 2, 5, 6)) or (fy == 1 and fx in (0, 7)):
                return BLACK                                   # angry eyebrows
            if fy == 3 and fx in (2, 5):
                return (230, 40, 40)                           # glowing red eyes
            if fy == 4 and fx in (3, 4):
                return (240, 130, 30)                          # carrot nose
            if (fy == 6 and 2 <= fx <= 5) or (fy == 7 and fx in (1, 6)):
                return BLACK                                   # frown
        if 32 <= x and y < 4:
            return jitter(rng, (100, 66, 40))                  # stick arms
        if 10 <= x < 20 and 26 <= y < 36 and x in (14, 15) and y in (28, 31, 34):
            return BLACK                                       # coal buttons
        return jitter(rng, (244, 246, 252), 0.025)

    return from_mask("snow_golem", (64, 64), paint)


def krampus() -> Image.Image:
    rng = random.Random("krampus")
    fur = lambda: jitter(rng, rng.choice([(46, 30, 22), (38, 24, 18), (56, 36, 26)]), 0.08)
    horn = lambda: jitter(rng, (226, 214, 190), 0.05)

    def head(x, y, face, w, h):
        if face == "front":
            if y == 3 and x in (1, 2, 5, 6):
                return (30, 10, 10)                       # deep eye sockets (the glow is a separate layer)
            if y >= 5 and 2 <= x <= 5:
                return (24, 14, 12) if y == 5 else fur()  # snarling mouth
            if y in (1, 2) and x in (0, 7):
                return (60, 40, 30)
        return fur()

    def body(x, y, face, w, h):
        if face == "front" and (x == y % 8 or x == (y + 4) % 8):
            return (130, 130, 140) if (x + y) % 2 else (90, 90, 100)   # chains across the chest
        return fur()

    def arm(x, y, face, w, h):
        if face == "bottom" or y >= h - 2:
            return (20, 14, 12)                           # claws
        if y == h - 4:
            return (110, 110, 120)                        # shackle
        return fur()

    def leg(x, y, face, w, h):
        if face == "bottom" or y >= h - 2:
            return (24, 18, 16)                           # hooves
        return fur()

    tongue = lambda x, y, face, w, h: (200, 60, 70)
    tail = lambda x, y, face, w, h: (20, 14, 12) if (face == "front" or x < 1) else fur()
    hornp = lambda x, y, face, w, h: horn()
    return humanoid({"head": head, "body": body, "arm": arm, "leg": leg},
                    [(0, 32, 2, 4, 2, hornp), (8, 32, 1, 3, 1, hornp), (16, 32, 1, 1, 7, tail), (32, 32, 1, 4, 1, tongue)])


def krampus_eyes() -> Image.Image:
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    for x in (9, 10, 13, 14):                             # head front is (8,8); eyes on row 3
        img.putpixel((x, 11), (255, 40, 30, 255))
    return img


def santa_sleigh() -> Image.Image:
    """128x128 texture for SleighModels.sleigh(): red lacquered body with gold trim and curlicues,
    gold runners, a brown sack of presents."""
    rng = random.Random("sleigh")
    img = Image.new("RGBA", (128, 128), (0, 0, 0, 0))
    gold = lambda: jitter(rng, GOLD, 0.06)
    red = lambda: jitter(rng, (180, 20, 32), 0.05)

    def body(x, y, face, w, h):
        if face == "top":
            return jitter(rng, (120, 14, 24), 0.05)               # velvet floor
        if y in (0, h - 1) or (face in ("right", "left") and x in (0, w - 1)):
            return gold()                                         # gold trim
        if face in ("right", "left") and (x + 2 * y) % 9 == 0 and 1 <= y <= h - 2:
            return gold()                                         # painted swirls
        return red()

    box(img, 0, 0, 1, 1, 24, lambda *a: gold())                  # runners
    box(img, 52, 0, 1, 5, 1, lambda *a: gold())                  # curls
    box(img, 60, 0, 1, 3, 1, lambda *a: jitter(rng, (150, 110, 40)))  # struts
    box(img, 0, 26, 12, 7, 18, body)
    box(img, 0, 52, 12, 6, 3, body)                               # seat back
    box(img, 32, 52, 12, 6, 2, body)                              # curled front
    box(img, 64, 0, 8, 8, 6, lambda x, y, f, w, h: jitter(rng, (120, 80, 46), 0.08))   # sack
    box(img, 64, 16, 3, 3, 3, lambda x, y, f, w, h: GOLD if x == 1 or y == 1 else (200, 36, 44))
    box(img, 80, 16, 3, 2, 3, lambda x, y, f, w, h: (250, 250, 250) if x == 1 else (40, 140, 70))
    box(img, 0, 64, 1, 1, 50, lambda *a: jitter(rng, (110, 60, 30)))   # leather reins
    box(img, 0, 118, 24, 1, 1, lambda *a: gold())                # harness bar
    return img


def present_parachute() -> Image.Image:
    """64x32: a red-and-white candy-striped canopy and white strings."""
    img = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    stripe = lambda x, y, face, w, h: RED if (x + (y if face in ("top", "bottom") else 0)) % 4 < 2 else WHITE
    box(img, 0, 0, 14, 3, 14, stripe)
    box(img, 0, 17, 1, 14, 1, lambda *a: OFFWHITE)
    return img


TEXTURES: dict[str, Callable[[], Image.Image]] = {
    "santa_sleigh": santa_sleigh,
    "present_parachute": present_parachute,
    "krampus": krampus,
    "krampus_eyes": krampus_eyes,
    "gingerbread_man": gingerbread_man,
    "elf": elf,
    "santa_claus": santa_claus,
    "cookie_soldier": cookie_soldier,
    "reindeer": reindeer,
    "peppermint_pup": lambda: peppermint_pup(False),
    "peppermint_pup_angry": lambda: peppermint_pup(True),
    "evil_snowman": evil_snowman,
}
for _flavour, _colour in FLAVOURS.items():
    TEXTURES[f"gumdrop_blob/{_flavour}"] = (lambda c=_colour, n=_flavour: gumdrop(c, n))


def main() -> None:
    made = []
    for name, recipe in TEXTURES.items():
        path = OUT / f"{name}.png"
        path.parent.mkdir(parents=True, exist_ok=True)
        img = recipe()
        img.save(path)
        made.append(img)
    print(f"wrote {len(made)} entity textures")
    if "--preview" in sys.argv:
        scale, pad = 4, 8
        sheet = Image.new("RGBA", (6 * (64 * scale + pad) + pad, 2 * (64 * scale + pad) + pad), (70, 74, 90, 255))
        for i, img in enumerate(made):
            x, y = pad + (i % 6) * (64 * scale + pad), pad + (i // 6) * (64 * scale + pad)
            sheet.alpha_composite(img.resize((img.width * scale, img.height * scale), Image.NEAREST), (x, y))
        sheet.save(PREVIEW)
        print(f"preview -> {PREVIEW}")


if __name__ == "__main__":
    main()
