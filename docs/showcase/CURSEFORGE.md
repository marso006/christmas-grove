# CurseForge (and Modrinth) upload kit

Everything to paste into the project page, in the order the forms ask for it. The description
below is plain Markdown; on CurseForge pick the **Markdown** editor when you paste it. Image
links point at the GitHub repo, which works once it is public. Until then, upload the images
in `docs/showcase/` to the project's Images tab and swap in those URLs.

## Project settings

| Field | Value |
| --- | --- |
| Name | Christmas Grove |
| Slug | `christmas-grove` (free on Modrinth as of Nov 17; check CurseForge when you create it) |
| Summary (CurseForge, max 250) | Shake a Snow Globe and visit a candy-and-cookie Christmas dimension: five festive biomes, Santa's Workshop, a sleigh that drops presents, reindeer, elves, Krampus and more. |
| Summary (Modrinth, max 256) | Same as above |
| Main category | World Gen → Dimensions |
| Extra categories | Biomes, Mobs, Structures, Food, Adventure and RPG, Armor, Tools, and Weapons |
| Modrinth tags | adventure, decoration, food, mobs, worldgen, equipment |
| Loaders | Fabric (needs Fabric API), NeoForge |
| Game versions | 1.21.1 (26.3 to follow) |
| Environment | Client and server (required on both) |
| License | Custom: MIT for code, All Rights Reserved for assets (see LICENSE) |
| Source | https://github.com/marso006/christmas-grove (make the repo public first, or leave blank) |
| Issues | https://github.com/marso006/christmas-grove/issues |
| Icon | `docs/showcase/icon.png` (400x400) |
| Gallery | `screenshots/*.png` (13 captioned in-game shots), `screenshots/collage.png`, `banner.png`, `feature_blocks.png`, `feature_items.png` |
| Video thumbnail | `screenshots/thumbnail.png` (1280x720) |

## Files

Upload one jar per loader, and mark each with its loader and game version:

- `christmas-grove-fabric-1.21.1-<version>.jar`: Fabric, 1.21.1, depends on **Fabric API** (add it as a required dependency on both sites)
- `christmas-grove-neoforge-1.21.1-<version>.jar`: NeoForge, 1.21.1

Release type: **Beta** for the first public build, **Release** once it has had a few days of player feedback.
Paste the matching section of `CHANGELOG.md` as the file changelog.

---

## Description

The full page description is in two formats with the same content:

- **`description.md`**: paste it into CurseForge with the **Markdown** editor selected, and into Modrinth's description as is
- **`description.html`**: for CurseForge's default visual editor, open the **Source** (`<>`) view and paste it there

It includes 17 images: the banner, 12 captioned screenshots, the item and block galleries, and two
recipe sheets. Regenerate the HTML after editing the Markdown with `py tools/showcase/md_to_html.py`.

**Image hosting:** the image links point at this GitHub repository, so they only load once the repo
is public. To keep it private, upload the images to the CurseForge project's Images tab instead and
replace the links with CurseForge's image URLs.
