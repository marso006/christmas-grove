"""Generates Christmas Grove's structures: the .nbt templates plus the worldgen JSON (structure,
template pool, structure set) and biome tags that place them.

Each design is code below, block by block, so it is reproducible and reviewable. After generating,
the templates can still be edited in-game with structure blocks and re-saved over the .nbt files
(but then remove that design from DESIGNS so it isn't regenerated).

Usage: py tools/structures/gen_structures.py
"""
from __future__ import annotations

import json
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
import nbt  # noqa: E402

ROOT = Path(__file__).resolve().parents[2]
DATA = ROOT / "common/src/main/resources/data/christmas_grove"
NS = "christmas_grove"
DATA_VERSION = 4671  # Minecraft 1.21.11 (26.x upgrades these templates on load via the data fixer)

G = lambda name: f"{NS}:{name}"
M = lambda name: f"minecraft:{name}"


class Template:
    def __init__(self, sx: int, sy: int, sz: int):
        self.size = (sx, sy, sz)
        self.blocks: dict[tuple[int, int, int], tuple[str, dict, dict | None]] = {}
        self.entities: list[tuple[tuple[float, float, float], str]] = []

    def set(self, x, y, z, name, props=None, block_nbt=None):
        if not (0 <= x < self.size[0] and 0 <= y < self.size[1] and 0 <= z < self.size[2]):
            raise ValueError(f"{name} at {(x, y, z)} is outside the template {self.size}")
        self.blocks[(x, y, z)] = (name, props or {}, block_nbt)

    def fill(self, x1, y1, z1, x2, y2, z2, name, props=None):
        for x in range(min(x1, x2), max(x1, x2) + 1):
            for y in range(min(y1, y2), max(y1, y2) + 1):
                for z in range(min(z1, z2), max(z1, z2) + 1):
                    self.set(x, y, z, name, props)

    def walls(self, x1, y1, z1, x2, y2, z2, name, props=None):
        for x in range(x1, x2 + 1):
            for y in range(y1, y2 + 1):
                for z in range(z1, z2 + 1):
                    if x in (x1, x2) or z in (z1, z2):
                        self.set(x, y, z, name, props)

    def chest(self, x, y, z, facing, loot):
        self.set(x, y, z, M("chest"), {"facing": facing},
                 nbt.Compound({"id": nbt.String(M("chest")), "LootTable": nbt.String(G(f"chests/{loot}"))}))

    def entity(self, x, y, z, entity_id):
        self.entities.append(((x, y, z), entity_id))

    def save(self, name):
        palette: list[tuple[str, tuple]] = []
        index: dict[tuple[str, tuple], int] = {}
        blocks = []
        for pos, (block, props, block_nbt) in sorted(self.blocks.items()):
            key = (block, tuple(sorted(props.items())))
            if key not in index:
                index[key] = len(palette)
                palette.append(key)
            entry = nbt.Compound({"pos": nbt.List(nbt.TAG_INT, [nbt.Int(v) for v in pos]),
                                  "state": nbt.Int(index[key])})
            if block_nbt is not None:
                entry["nbt"] = block_nbt
            blocks.append(entry)
        palette_nbt = []
        for block, props in palette:
            entry = nbt.Compound({"Name": nbt.String(block)})
            if props:
                entry["Properties"] = nbt.Compound({k: nbt.String(str(v).lower()) for k, v in props})
            palette_nbt.append(entry)
        entities = []
        for (x, y, z), entity_id in self.entities:
            entities.append(nbt.Compound({
                "pos": nbt.List(nbt.TAG_DOUBLE, [nbt.Double(x), nbt.Double(y), nbt.Double(z)]),
                "blockPos": nbt.List(nbt.TAG_INT, [nbt.Int(int(x)), nbt.Int(int(y)), nbt.Int(int(z))]),
                "nbt": nbt.Compound({"id": nbt.String(entity_id), "PersistenceRequired": nbt.Byte(1)}),
            }))
        root = nbt.Compound({
            "DataVersion": nbt.Int(DATA_VERSION),
            "size": nbt.List(nbt.TAG_INT, [nbt.Int(v) for v in self.size]),
            "palette": nbt.List(nbt.TAG_COMPOUND, palette_nbt),
            "blocks": nbt.List(nbt.TAG_COMPOUND, blocks),
            "entities": nbt.List(nbt.TAG_COMPOUND, entities),
        })
        out = DATA / "structure" / f"{name}.nbt"
        out.parent.mkdir(parents=True, exist_ok=True)
        nbt.write(out, root)
        print(f"{name}: {self.size}, {len(blocks)} blocks, {len(palette)} states, {len(entities)} entities")


# ---- Shared pieces ---------------------------------------------------------------------------

def gable_roof(t: Template, x1, x2, z1, z2, y0, stairs, ridge, gable_block, gable_x=()):
    """A roof sloping down to the north and south, ridge along x, from z1..z2 starting at height y0.
    Ends at gable_x get filled with gable_block under the slope."""
    k = 0
    while z1 + k <= z2 - k:
        y, zn, zs = y0 + k, z1 + k, z2 - k
        for x in range(x1, x2 + 1):
            if zn == zs:
                t.set(x, y, zn, ridge)
            else:
                t.set(x, y, zn, stairs, {"facing": "south", "half": "bottom"})
                t.set(x, y, zs, stairs, {"facing": "north", "half": "bottom"})
        for gx in gable_x:
            for z in range(zn + 1, zs):
                t.set(gx, y, z, gable_block)
        k += 1


def pane(axis):  # glass pane connected along a wall running on this axis
    return {"east": "true", "west": "true"} if axis == "x" else {"north": "true", "south": "true"}


def door(t: Template, x, y, z, block, facing, hinge="left"):
    t.set(x, y, z, block, {"facing": facing, "half": "lower", "hinge": hinge, "open": "false"})
    t.set(x, y + 1, z, block, {"facing": facing, "half": "upper", "hinge": hinge, "open": "false"})


def lights(t: Template, xs, y, z, facing):
    for x in xs:
        t.set(x, y, z, G("festive_lights"), {"facing": facing})


def mini_tree(t: Template, x, y, z, height=2):
    """A small decorated fir with lit leaves and a star, for interiors."""
    for dy in range(height):
        t.set(x, y + dy, z, G("festive_fir_log"))
    leaf = lambda kind: {"persistent": "true", "distance": "1"}
    top = y + height
    ring = [(1, 0, "red_lit_festive_fir_leaves"), (-1, 0, "gold_lit_festive_fir_leaves"),
            (0, 1, "blue_lit_festive_fir_leaves"), (0, -1, "festive_fir_leaves")]
    for dx, dz, kind in ring:
        t.set(x + dx, top - 1, z + dz, G(kind), leaf(kind))
    t.set(x, top, z, G("festive_fir_leaves"), leaf("plain"))
    t.set(x, top + 1, z, G("christmas_star"))


# ---- Designs ---------------------------------------------------------------------------------

def gingerbread_house() -> Template:
    """A cosy 9x9 gingerbread cottage with candy-cane corners, an icing roof, a decorated tree, a
    cocoa kettle, a loot chest and an Elf living inside."""
    t = Template(11, 12, 11)
    t.fill(1, 1, 1, 9, 10, 9, M("air"))
    t.fill(1, 0, 1, 9, 0, 9, G("cookie_tiles"))
    t.walls(1, 1, 1, 9, 4, 9, G("gingerbread_bricks"))
    for cx, cz in ((1, 1), (9, 1), (1, 9), (9, 9)):
        t.fill(cx, 1, cz, cx, 4, cz, G("candy_cane_log"))
    for y in (2, 3):
        for x in (3, 7):
            t.set(x, y, 1, G("candy_glass_pane"), pane("x"))
            t.set(x, y, 9, G("candy_glass_pane"), pane("x"))
        for z in (3, 7):
            t.set(1, y, z, G("candy_glass_pane"), pane("z"))
            t.set(9, y, z, G("candy_glass_pane"), pane("z"))
    door(t, 5, 1, 1, G("candy_cane_door"), "south")
    t.set(5, 3, 0, G("wreath"), {"facing": "north"})
    gable_roof(t, 0, 10, 0, 10, 5, G("icing_bricks_stairs"), G("icing_bricks"), G("gingerbread_bricks"), (1, 9))
    # interior
    t.fill(4, 1, 4, 6, 1, 6, M("red_carpet"))
    t.chest(8, 1, 8, "north", "gingerbread_house")
    t.set(2, 1, 8, M("crafting_table"))
    t.set(2, 1, 2, M("water_cauldron"), {"level": "3"})           # the cocoa kettle
    t.set(3, 1, 2, M("campfire"), {"lit": "false", "facing": "south"})
    mini_tree(t, 8, 1, 2)
    t.set(7, 1, 3, G("red_gift_box"))
    t.set(6, 1, 2, G("green_gift_box"))
    lights(t, range(2, 9), 4, 8, "north")
    t.set(5, 9, 5, M("lantern"), {"hanging": "true"})
    t.entity(4.5, 1.0, 5.5, G("elf"))
    return t


def santas_workshop() -> Template:
    """Santa's Workshop: a big fir-plank hall with a snowy roof, a fireplace and chimney, workbenches,
    piles of presents, a Christmas tree, two chests, Santa himself and three elves."""
    t = Template(17, 15, 13)
    t.fill(1, 1, 1, 15, 12, 11, M("air"))
    t.fill(1, 0, 1, 15, 0, 11, G("festive_fir_planks"))
    t.walls(1, 0, 1, 15, 0, 11, G("peppermint_bricks"))
    t.walls(1, 1, 1, 15, 5, 11, G("festive_fir_planks"))
    for px in (1, 8, 15):
        for pz in (1, 6, 11):
            if px in (1, 15) or pz in (1, 11):
                t.fill(px, 1, pz, px, 5, pz, G("festive_fir_log"))
    for y in (2, 3):
        for x in (3, 4, 5, 11, 12, 13):
            t.set(x, y, 1, G("candy_glass_pane"), pane("x"))
            t.set(x, y, 11, G("candy_glass_pane"), pane("x"))
        for z in (3, 4, 8, 9):
            t.set(1, y, z, G("candy_glass_pane"), pane("z"))
            t.set(15, y, z, G("candy_glass_pane"), pane("z"))
    t.set(8, 1, 1, M("air"))
    t.set(8, 2, 1, M("air"))                                      # doorway between the doors
    door(t, 7, 1, 1, G("festive_fir_door"), "south", "left")
    door(t, 9, 1, 1, G("festive_fir_door"), "south", "right")
    t.set(8, 4, 0, G("wreath"), {"facing": "north"})
    lights(t, range(2, 15), 5, 0, "north")                        # lights along the front
    gable_roof(t, 0, 16, 0, 12, 6, G("icing_bricks_stairs"), G("icing_bricks"), G("festive_fir_planks"), (1, 15))
    # fireplace and chimney on the back wall
    for x in (12, 14):
        t.fill(x, 1, 10, x, 3, 10, G("gingerbread_bricks"))
    t.set(13, 1, 10, M("campfire"), {"lit": "true", "facing": "north"})
    t.fill(12, 4, 10, 14, 4, 10, G("gingerbread_bricks"))
    t.fill(13, 2, 11, 13, 14, 11, G("gingerbread_bricks"))
    # workshop floor
    for x in range(3, 7):
        t.set(x, 1, 3, M("crafting_table"))
    for x, z, box in ((10, 3, "red"), (11, 3, "green"), (12, 3, "blue"), (10, 4, "blue"), (11, 4, "red"), (13, 4, "green")):
        t.set(x, 1, z, G(f"{box}_gift_box"))
    t.set(10, 2, 3, G("green_gift_box"))
    mini_tree(t, 8, 1, 8, height=3)
    for x, z in ((7, 8), (9, 8), (8, 9)):
        t.set(x, 1, z, G("red_gift_box" if x != 9 else "blue_gift_box"))
    t.chest(2, 1, 10, "south", "santas_workshop")
    t.chest(3, 1, 10, "south", "santas_workshop")
    t.fill(7, 1, 2, 9, 1, 6, M("red_carpet"))
    lights(t, range(2, 15), 5, 10, "north")
    for x in (4, 8, 12):
        t.set(x, 11, 6, M("lantern"), {"hanging": "true"})
    t.entity(8.5, 1.0, 5.5, G("santa_claus"))
    for x, z in ((4.5, 4.5), (11.5, 5.5), (5.5, 8.5)):
        t.entity(x, 1.0, z, G("elf"))
    return t


def candy_cane_outpost() -> Template:
    """A peppermint watchtower guarded by Cookie Soldiers, with a soldier spawner on the ground floor,
    a ladder to the battlements, a loot chest on top and a giant candy cane flagpole."""
    t = Template(9, 18, 9)
    t.fill(1, 1, 1, 7, 12, 7, M("air"))
    t.walls(1, 0, 1, 7, 12, 7, G("peppermint_bricks"))
    for cx, cz in ((1, 1), (7, 1), (1, 7), (7, 7)):
        t.fill(cx, 0, cz, cx, 13, cz, G("candy_cane_log"))
    t.fill(2, 0, 2, 6, 0, 6, G("peppermint_stone"))
    t.fill(2, 6, 2, 6, 6, 6, G("peppermint_bricks"))
    t.fill(2, 12, 2, 6, 12, 6, G("peppermint_bricks"))
    for x in range(1, 8):                                         # battlements
        for z in (1, 7):
            if x % 2 == 0:
                t.set(x, 13, z, G("peppermint_bricks_wall"), {"up": "true"})
    for z in range(2, 7):
        for x in (1, 7):
            if z % 2 == 0:
                t.set(x, 13, z, G("peppermint_bricks_wall"), {"up": "true"})
    t.set(4, 1, 1, M("air"))
    t.set(4, 2, 1, M("air"))                                      # doorway
    for y in (4, 9):                                              # arrow slits
        for x, z in ((1, 4), (7, 4), (4, 7)):
            t.set(x, y, z, M("air"))
    for y in range(1, 13):                                        # ladder up the back wall
        t.set(4, y, 6, M("ladder"), {"facing": "north"})
    t.set(4, 6, 6, M("ladder"), {"facing": "north"})
    t.set(4, 12, 6, M("ladder"), {"facing": "north"})
    t.set(3, 1, 3, M("spawner"), None, nbt.Compound({
        "id": nbt.String(M("mob_spawner")),
        "SpawnData": nbt.Compound({"entity": nbt.Compound({"id": nbt.String(G("cookie_soldier"))})}),
    }))
    t.chest(2, 13, 2, "south", "candy_cane_outpost")
    for y in range(13, 17):                                       # candy cane flagpole
        t.set(6, y, 6, G("candy_cane_log"))
    t.set(5, 17, 6, G("candy_cane_log"), {"axis": "x"})
    t.set(4, 16, 6, G("candy_cane_log"))
    t.entity(3.5, 7.0, 3.5, G("cookie_soldier"))
    t.entity(5.5, 13.0, 4.5, G("cookie_soldier"))
    t.entity(2.5, 1.0, 5.5, G("cookie_soldier"))
    return t


def snowman_camp() -> Template:
    """A ring of snow around a campfire where Evil Snowmen gather, with their stash of loot."""
    t = Template(11, 4, 11)
    t.fill(0, 1, 0, 10, 3, 10, M("air"))
    for x in range(11):
        for z in range(11):
            d = ((x - 5) ** 2 + (z - 5) ** 2) ** 0.5
            if d <= 5.2:
                t.set(x, 0, z, M("snow_block"))
            if 4.2 < d <= 5.2 and (x + z) % 3:
                t.set(x, 1, z, M("snow_block"))
            elif 2 < d <= 4.2 and (x * 7 + z * 3) % 4 == 0:
                t.set(x, 1, z, M("snow"), {"layers": "2"})
    t.set(5, 1, 5, M("campfire"), {"lit": "true", "facing": "north"})
    t.chest(7, 1, 3, "west", "snowman_camp")
    t.set(3, 1, 7, M("powder_snow"))
    t.entity(3.5, 1.0, 4.5, G("evil_snowman"))
    t.entity(6.5, 1.0, 7.5, G("evil_snowman"))
    return t


# name -> (design, biomes, spacing, separation, salt)
DESIGNS = {
    "gingerbread_house": (gingerbread_house, ["gingerbread_grove", "frosted_flats"], 16, 6, 1225851701),
    "santas_workshop": (santas_workshop, ["festive_fir_forest"], 24, 8, 1225851702),
    "candy_cane_outpost": (candy_cane_outpost, ["peppermint_peaks", "gingerbread_grove"], 24, 8, 1225851703),
    "snowman_camp": (snowman_camp, ["frosted_flats", "festive_fir_forest"], 18, 6, 1225851704),
}


def write_json(path: Path, data) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=2) + "\n", newline="\n")


def main() -> None:
    for name, (design, biomes, spacing, separation, salt) in DESIGNS.items():
        design().save(name)
        write_json(DATA / f"tags/worldgen/biome/has_structure/{name}.json",
                   {"values": [G(b) for b in biomes]})
        write_json(DATA / f"worldgen/template_pool/{name}/start.json", {
            "fallback": "minecraft:empty",
            "elements": [{"weight": 1, "element": {
                "element_type": "minecraft:single_pool_element", "location": G(name),
                "processors": "minecraft:empty", "projection": "rigid"}}],
        })
        write_json(DATA / f"worldgen/structure/{name}.json", {
            "type": "minecraft:jigsaw",
            "biomes": f"#{NS}:has_structure/{name}",
            "step": "surface_structures",
            "spawn_overrides": {},
            "terrain_adaptation": "beard_thin",
            "start_pool": G(f"{name}/start"),
            "size": 1,
            "start_height": {"absolute": 0},
            "project_start_to_heightmap": "WORLD_SURFACE_WG",
            "max_distance_from_center": 80,
            "use_expansion_hack": False,
        })
        write_json(DATA / f"worldgen/structure_set/{name}.json", {
            "structures": [{"structure": G(name), "weight": 1}],
            "placement": {"type": "minecraft:random_spread", "spacing": spacing, "separation": separation, "salt": salt},
        })


if __name__ == "__main__":
    main()
