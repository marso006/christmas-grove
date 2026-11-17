# Screenshots and trailer

How to capture the gallery images and a short trailer quickly, using the showcase datapack.

## Setup (5 minutes)

1. Start a dev client: `./gradlew :fabric:runClient` (or use the release jar in a normal instance).
2. Create a **Creative** world, with **Allow Commands: ON**.
3. Copy `docs/showcase/gingerbread_showcase/` (or the zip) into the world's `datapacks/` folder,
   then run `/reload`. In the dev client the world is under `fabric/runs/client/saves/<world>/`.
4. Use the Snow Globe to enter the Grove, find a flat spot, and run `/function gg_showcase:setup`.
5. Video settings: FOV 70, render distance 16 or more, Fancy graphics, smooth lighting on, GUI
   scale 2. Shaders such as Complementary or BSL make the lights and stars pop, but the page should
   also include at least two shots without shaders, so people know what to expect.
6. **F1** hides the HUD, **F2** saves a screenshot (to `screenshots/`).

Every scene builds 6 to 20 blocks **south** of where you stand. Face south (`/tp @s ~ ~ ~ 0 0`),
take the shot, then `/function gg_showcase:cleanup`.

## Gallery shots (aim for 8 to 10)

| # | Scene | Command(s) | Framing tip |
| --- | --- | --- | --- |
| 1 | **Arrival:** panorama of the Grove at golden hour | `gg_showcase:dusk` | Stand on a hill, look across candy-cane trees toward peppermint peaks |
| 2 | **Christmas tree forest at night** | `gg_showcase:night` in a Festive Fir Forest | Low angle, the lit leaves and stars glowing against the dark sky |
| 3 | **Santa's sleigh overhead** | `gg_showcase:night`, `gg_showcase:sleigh_flyover` | Look up and west, take the shot as it passes in front of the moon |
| 4 | **Presents raining down** | `gg_showcase:present_rain` | Look up slightly; the parachutes read best against the sky |
| 5 | **The village** | `gg_showcase:village`, `gg_showcase:day` | Step back about 25 blocks; the house, workshop, tree and giant candy cane all in frame |
| 6 | **Inside Santa's Workshop** | `gg_showcase:village` | Walk in through the double doors; Santa by the tree, elves at the benches |
| 7 | **Meet the cast** (mob lineup) | `gg_showcase:mob_lineup` | Crouch slightly, centred on Santa, all nine mobs in a row |
| 8 | **Krampus** | `gg_showcase:krampus_arena` | Night, low angle looking up at him; his eyes glow |
| 9 | **Cocoa Sea and marshmallow islands** | find a Cocoa Sea | From a beach at sunset, islands bobbing on the brown sea |
| 10 | **Blocks gallery** | `gg_showcase:block_gallery` | Front-on, daylight, so every texture is clear |
| 11 | **Riding a reindeer** | saddle and tame one from the lineup (`cleanup` first, then a spawn egg) | Third-person view (F5) gliding off a cliff with Sleigh Bells on |
| 12 | **Santa Suit** | wear all four pieces | F5 front view, standing in powder snow |

Captions to use on the page, matching the shots: "Welcome to the Gingerbread Grove", "Every
Christmas tree is decorated", "Look up!", "Presents by parachute", "Santa's village", "The
Workshop", "Meet the locals", "Better be good...", "The Cocoa Sea", "75 new blocks", "Reindeer
glide with sleigh bells", "The Santa Suit".

## 45-second trailer script

Record at 1080p, 60 fps with OBS. Use a slow camera: Replay Mod is ideal, or fly smoothly in
spectator mode. For music, only use tracks you have the rights to (a CC0 or royalty-free
Christmas-style track): no famous carols, and no recordings you don't own.

| Time | Shot | On-screen text |
| --- | --- | --- |
| 0:00-0:04 | Close-up of a hand shaking the Snow Globe in a dark Overworld forest | *Shake the globe...* |
| 0:04-0:09 | Arrival: the camera rises over frosted hills and candy-cane trees at dusk | **Gingerbread Grove** |
| 0:09-0:14 | Slow push through a Festive Fir Forest at night, lights glowing | *Every tree is a Christmas tree* |
| 0:14-0:20 | Santa's sleigh crosses the moon; presents float down on parachutes | *Look up!* |
| 0:20-0:25 | Inside the Workshop: Santa hands over a present, an elf trades | *Visit Santa* |
| 0:25-0:30 | Quick cuts: a Gingerbread Man runs, a reindeer glides, a pup sits | *Make friends* |
| 0:30-0:36 | Lights drop and chains rattle: Krampus steps out of the smoke | *...but don't be naughty* |
| 0:36-0:41 | Fast montage: Cocoa Sea, Peppermint Peaks, giant candy cane, gift unwrapping | |
| 0:41-0:45 | Banner art with "Fabric & NeoForge · 1.21.1" | **Download on CurseForge & Modrinth** |

Upload it to YouTube (unlisted is fine) and paste the link into the CurseForge description, near
the top.
