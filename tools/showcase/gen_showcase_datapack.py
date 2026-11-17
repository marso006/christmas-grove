"""Generates the Christmas Grove showcase datapack: one-command scenes for screenshots and trailers.

Copy docs/showcase/christmas_showcase/ into a creative world's datapacks folder (or use the zip),
run /reload, then /function cg_showcase:help. Not shipped in the mod jar.

Usage: py tools/showcase/gen_showcase_datapack.py
"""
from __future__ import annotations

import json
import shutil
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / "docs/showcase/christmas_showcase"
BLOCKSTATES = ROOT / "common/src/generated/resources/assets/christmas_grove/blockstates"
G = "christmas_grove"
TAG = 'Tags:["cg_showcase"],PersistenceRequired:1b'

FUNCTIONS: dict[str, list[str]] = {}


def fn(name: str, *lines: str) -> None:
    FUNCTIONS[name] = list(lines)


fn("help",
   'tellraw @s ["",{"text":"Christmas Grove showcase","color":"gold","bold":true}]',
   'tellraw @s {"text":"Stand on flat ground. Every scene builds relative to you, facing south.","color":"gray"}',
   'tellraw @s ["",{"text":" setup","color":"yellow"},{"text":"  freeze time/weather/spawning, give the showcase kit"}]',
   'tellraw @s ["",{"text":" day | dusk | night","color":"yellow"},{"text":"  set the light for the shot"}]',
   'tellraw @s ["",{"text":" mob_lineup","color":"yellow"},{"text":"  every mob standing still in a row"}]',
   'tellraw @s ["",{"text":" village","color":"yellow"},{"text":"  Gingerbread House + Santa\'s Workshop + Christmas tree"}]',
   'tellraw @s ["",{"text":" outpost","color":"yellow"},{"text":"  Candy Cane Outpost and Snowman Camp"}]',
   'tellraw @s ["",{"text":" block_gallery","color":"yellow"},{"text":"  every block on display"}]',
   'tellraw @s ["",{"text":" sleigh_flyover","color":"yellow"},{"text":"  Santa\'s sleigh crosses in front of you"}]',
   'tellraw @s ["",{"text":" present_rain","color":"yellow"},{"text":"  parachute presents float down around you"}]',
   'tellraw @s ["",{"text":" krampus_arena","color":"yellow"},{"text":"  Krampus and his snowmen, frozen mid-stride"}]',
   'tellraw @s ["",{"text":" cleanup","color":"yellow"},{"text":"  remove all showcase mobs"}]',
   'tellraw @s {"text":"Tip: F1 hides the HUD, F2 takes a screenshot. FOV 70 and render distance 16+ look best.","color":"aqua"}')

fn("setup",
   "gamerule doDaylightCycle false", "gamerule doWeatherCycle false", "gamerule doMobSpawning false",
   "gamerule doInsomnia false", "weather clear", "time set 1000", "difficulty easy",
   f"give @s {G}:snow_globe", f"give @s {G}:letter_to_santa 16", f"give @s {G}:krampus_bell 2",
   f"give @s {G}:nice_list", f"give @s {G}:sleigh_bells 2", f"give @s {G}:santa_hat", f"give @s {G}:santa_coat",
   f"give @s {G}:santa_trousers", f"give @s {G}:santa_boots", f"give @s {G}:candy_cane_sword",
   f"give @s {G}:hot_cocoa 4", "give @s minecraft:saddle 2",
   'tellraw @s {"text":"Showcase ready: time and weather frozen, mob spawning off.","color":"green"}')

fn("day", "time set 6000", "weather clear")
fn("dusk", "time set 12600", "weather clear")
fn("night", "time set 18000", "weather clear")

MOBS = [
    ("gingerbread_man", ""), ("peppermint_pup", ""), ("elf", ""), ("santa_claus", ""),
    ("reindeer", ',Tame:1b,SaddleItem:{id:"minecraft:saddle",count:1}'), ("cookie_soldier", ""),
    ("gumdrop_blob", ",Size:1"), ("evil_snowman", ""), ("krampus", ""),
]
lineup = ["kill @e[tag=cg_showcase]"]
for i, (mob, extra) in enumerate(MOBS):
    x = (i - len(MOBS) // 2) * 3
    lineup.append(f"summon {G}:{mob} ~{x} ~ ~6 {{NoAI:1b,Rotation:[180f,0f],{TAG}{extra}}}")
lineup.append('tellraw @s {"text":"Mob lineup placed 6 blocks south of you. /function cg_showcase:cleanup removes it.","color":"green"}')
fn("mob_lineup", *lineup)

fn("village",
   f"place template {G}:gingerbread_house ~4 ~-1 ~6",
   f"place template {G}:santas_workshop ~-20 ~-1 ~6",
   f"fill ~-3 ~-1 ~16 ~-1 ~-1 ~18 {G}:frosted_gingerbread",
   f"place feature {G}:festive_fir_tree ~-2 ~ ~17",
   f"place feature {G}:gift_boxes ~-2 ~ ~17",
   f"fill ~16 ~-1 ~8 ~18 ~-1 ~10 {G}:frosted_gingerbread",
   f"place feature {G}:giant_candy_cane ~17 ~ ~9",
   'tellraw @s {"text":"Village built to the south: house, workshop (with Santa and elves), tree and a giant candy cane.","color":"green"}')

fn("outpost",
   f"place template {G}:candy_cane_outpost ~4 ~-1 ~8",
   f"place template {G}:snowman_camp ~-14 ~-1 ~8",
   'tellraw @s {"text":"Outpost and snowman camp built to the south.","color":"green"}')

fn("sleigh_flyover",
   f"summon {G}:santa_sleigh ~-70 ~14 ~14 {{Rotation:[-90f,0f]}}",
   'tellraw @s {"text":"Here comes Santa, from the west!","color":"gold"}')

rain = []
for i, (dx, dz) in enumerate([(-4, 6), (0, 9), (4, 6), (-7, 12), (7, 12), (-2, 15), (3, 18), (0, 4)]):
    rain.append(f"summon {G}:present_drop ~{dx} ~{14 + i * 2} ~{dz} {{Colour:{i % 3}}}")
fn("present_rain", *rain)

fn("krampus_arena",
   "kill @e[tag=cg_showcase]", "time set 18000",
   f"summon {G}:krampus ~ ~ ~10 {{NoAI:1b,Rotation:[180f,0f],{TAG}}}",
   f"summon {G}:evil_snowman ~-4 ~ ~8 {{NoAI:1b,Rotation:[160f,0f],{TAG}}}",
   f"summon {G}:evil_snowman ~4 ~ ~8 {{NoAI:1b,Rotation:[200f,0f],{TAG}}}",
   f"summon {G}:cookie_soldier ~-7 ~ ~11 {{NoAI:1b,Rotation:[150f,0f],{TAG}}}",
   f"summon {G}:cookie_soldier ~7 ~ ~11 {{NoAI:1b,Rotation:[210f,0f],{TAG}}}")

fn("cleanup", "kill @e[tag=cg_showcase]", f"kill @e[type={G}:present_drop]", f"kill @e[type={G}:santa_sleigh]")


def block_gallery() -> list[str]:
    """Every block with a block item, on a floor of frosted gingerbread, plants on top of it."""
    ids = sorted(p.stem for p in BLOCKSTATES.glob("*.json"))
    plants = {"lollipop_flower", "poinsettia", "holly", "sugar_grass", "candy_cane_sapling", "festive_fir_sapling",
              "gumdrop_bush", "christmas_star"}
    skip = {"festive_lights", "wreath"}  # wall decorations, placed on the back wall instead
    solid = [i for i in ids if i not in plants and i not in skip and not i.endswith(("_door", "_button", "_pressure_plate"))]
    lines = [f"fill ~-1 ~-1 ~3 ~{min(len(solid), 24)} ~-1 ~{3 + 2 * ((len(solid) + 23) // 24) + 2} {G}:frosted_gingerbread"]
    for i, block in enumerate(solid):
        x, z = i % 24, 4 + 2 * (i // 24)
        lines.append(f"setblock ~{x} ~ ~{z} {G}:{block}")
    z_plants = 4 + 2 * ((len(solid) + 23) // 24)
    for i, plant in enumerate(sorted(plants)):
        state = "[age=3]" if plant == "gumdrop_bush" else ""
        lines.append(f"setblock ~{i * 2} ~ ~{z_plants} {G}:{plant}{state}")
    lines.append(f"fill ~-1 ~ ~{z_plants + 2} ~24 ~3 ~{z_plants + 2} {G}:gingerbread_bricks")
    for x in range(0, 24, 2):
        lines.append(f"setblock ~{x} ~2 ~{z_plants + 1} {G}:festive_lights[facing=north]")
    lines.append(f"setblock ~11 ~1 ~{z_plants + 1} {G}:wreath[facing=north]")
    lines.append('tellraw @s {"text":"Block gallery built to the south.","color":"green"}')
    return lines


def main() -> None:
    if OUT.exists():
        shutil.rmtree(OUT)
    fn("block_gallery", *block_gallery())
    (OUT / "data/cg_showcase/function").mkdir(parents=True)
    (OUT / "pack.mcmeta").write_text(json.dumps({"pack": {
        "pack_format": 48, "description": "Christmas Grove showcase scenes (screenshots and trailers)"}}, indent=2) + "\n")
    for name, lines in FUNCTIONS.items():
        (OUT / f"data/cg_showcase/function/{name}.mcfunction").write_text("\n".join(lines) + "\n", newline="\n")
    zip_base = ROOT / "docs/showcase/christmas_showcase_datapack"
    shutil.make_archive(str(zip_base), "zip", OUT)
    print(f"{len(FUNCTIONS)} functions -> {OUT} (+ {zip_base.name}.zip)")


if __name__ == "__main__":
    main()
