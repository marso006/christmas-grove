package com.mental.christmasgrove.showcase;

import com.mental.christmasgrove.Constants;
import com.mental.christmasgrove.registry.ModDimensions;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;

/**
 * DEV-ONLY automated photographer for store-page screenshots. Runs only when the JVM is started with
 * {@code -Dchristmasgrove.showcase=true} (the {@code :fabric:runShowcase} Gradle task does this).
 * <p>
 * It joins a world, takes the player (as a spectator camera) into the Christmas Grove, stages each
 * scene with server commands, waits for chunks to render, hides the HUD, saves a screenshot to
 * runs/showcase/screenshots/, and finally closes the game. Nothing here runs in normal play.
 */
public final class ShowcaseDirector {

    private static final String G = Constants.MOD_ID;
    private final Deque<Step> steps = new ArrayDeque<>();
    private Step current;
    private int stepTicks;
    private int joinedTicks;

    // Scene anchors found at run time (written on the server thread, read later by later steps)
    private volatile BlockPos siteA = BlockPos.ZERO;   // the village
    private volatile BlockPos siteB = BlockPos.ZERO;   // flat stage for lineups and portraits

    private interface Step {
        /** @return true when finished. Called once per client tick. */
        boolean tick(Minecraft mc, int ticks);
    }

    public static void installIfEnabled() {
        if (Boolean.getBoolean("christmasgrove.showcase")) {
            Constants.LOG.warn("Showcase director enabled: the client will take screenshots and quit");
            ShowcaseDirector director = new ShowcaseDirector();
            ClientTickEvents.END_CLIENT_TICK.register(director::tick);
        }
    }

    private void tick(Minecraft mc) {
        if (mc.level == null || mc.player == null || mc.getSingleplayerServer() == null || mc.screen != null && joinedTicks < 5) {
            return;
        }
        if (joinedTicks++ == 0) {
            buildScript();
        }
        if (joinedTicks < 60) {
            return; // let the world settle after joining
        }
        if (current == null) {
            current = steps.poll();
            stepTicks = 0;
            if (current == null) {
                return;
            }
        }
        if (current.tick(mc, stepTicks++)) {
            current = null;
        }
    }

    // ---- Step builders -------------------------------------------------------------------------

    private void server(Consumer<MinecraftServer> task) {
        steps.add((mc, t) -> {
            if (t == 0) {
                mc.getSingleplayerServer().execute(() -> {
                    try {
                        task.accept(mc.getSingleplayerServer());
                    } catch (RuntimeException e) {
                        Constants.LOG.error("Showcase step failed", e);
                    }
                });
            }
            return t >= 2;
        });
    }

    private void client(Consumer<Minecraft> task) {
        steps.add((mc, t) -> {
            task.accept(mc);
            return true;
        });
    }

    private void waitTicks(int ticks) {
        steps.add((mc, t) -> t >= ticks);
    }

    private void waitForChunks(int minTicks, int maxTicks) {
        steps.add((mc, t) -> t >= maxTicks || t >= minTicks && mc.levelRenderer.hasRenderedAllSections());
    }

    private void shot(String name) {
        steps.add((mc, t) -> {
            Screenshot.grab(mc.gameDirectory, name + ".png", mc.getMainRenderTarget(),
                    message -> Constants.LOG.info("Showcase: {}", message.getString()));
            return true;
        });
    }

    // ---- Server helpers (run on the server thread) ---------------------------------------------

    private static ServerLevel grove(MinecraftServer server) {
        return server.getLevel(ModDimensions.GROVE);
    }

    private static ServerPlayer player(MinecraftServer server) {
        return server.getPlayerList().getPlayers().get(0);
    }

    private static void cmd(MinecraftServer server, String command) {
        server.getCommands().performPrefixedCommand(server.createCommandSourceStack()
                .withLevel(grove(server)).withPosition(Vec3.ZERO).withSuppressedOutput(), command);
    }

    private static int surface(ServerLevel level, int x, int z) {
        level.getChunk(x >> 4, z >> 4);
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
    }

    private static BlockPos find(ServerLevel level, ResourceKey<Biome> biome, BlockPos from) {
        Pair<BlockPos, Holder<Biome>> found = level.findClosestBiome3d(h -> h.is(biome), from.atY(64), 6400, 32, 64);
        if (found == null) {
            Constants.LOG.warn("Showcase: no {} found near {}", biome.location(), from);
        }
        BlockPos pos = found == null ? from : found.getFirst();
        Constants.LOG.info("Showcase: {} at {}", biome.location(), pos);
        // step a little into the biome so the camera sees mostly that biome
        return new BlockPos(pos.getX() + 24, 0, pos.getZ() + 24);
    }

    /** Finds a camera spot whose view (in the yaw direction) is dry land of the wanted biome. */
    private static BlockPos viewpoint(ServerLevel level, ResourceKey<Biome> biome, float yaw) {
        BlockPos start = find(level, biome, BlockPos.ZERO);
        double dx = -Math.sin(Math.toRadians(yaw)), dz = Math.cos(Math.toRadians(yaw));
        for (int ring = 0; ring < 40; ring++) {
            int x = start.getX() + ((ring % 7) - 3) * 40, z = start.getZ() + ((ring / 7) - 3) * 40;
            int y = surface(level, x, z);
            if (y < 66) {
                continue;
            }
            int good = 0;
            for (int d = 20; d <= 60; d += 20) {
                int ax = (int) (x + dx * d), az = (int) (z + dz * d);
                int ay = surface(level, ax, az);
                if (ay >= 65 && level.getBiome(new BlockPos(ax, ay, az)).is(biome)) {
                    good++;
                }
            }
            if (good >= 2) {
                Constants.LOG.info("Showcase: viewpoint for {} at {} {} {}", biome.location(), x, y, z);
                return new BlockPos(x, y, z);
            }
        }
        Constants.LOG.warn("Showcase: no good viewpoint for {}", biome.location());
        return new BlockPos(start.getX(), surface(level, start.getX(), start.getZ()), start.getZ());
    }

    /** Walks out from a biome until the ground is level enough (within 3 blocks) over a 60x60 area. */
    private static BlockPos flatSite(ServerLevel level, ResourceKey<Biome> biome, BlockPos from) {
        BlockPos start = find(level, biome, from);
        for (int attempt = 0; attempt < 24; attempt++) {
            int x = start.getX() + (attempt % 6) * 48, z = start.getZ() + (attempt / 6) * 48;
            int centre = surface(level, x, z), worst = 0;
            for (int dx = -30; dx <= 30; dx += 15) {
                for (int dz = -20; dz <= 40; dz += 15) {
                    worst = Math.max(worst, Math.abs(surface(level, x + dx, z + dz) - centre));
                }
            }
            if (worst <= 3 && !level.getBiome(new BlockPos(x, centre, z)).is(ModDimensions.COCOA_SEA)) {
                Constants.LOG.info("Showcase: flat site at {} {} {} (attempt {})", x, centre, z, attempt);
                return new BlockPos(x, centre, z);
            }
        }
        Constants.LOG.warn("Showcase: no flat site found, using {}", start);
        return new BlockPos(start.getX(), surface(level, start.getX(), start.getZ()), start.getZ());
    }

    private static void camera(MinecraftServer server, double x, double y, double z, float yaw, float pitch) {
        player(server).teleportTo(grove(server), x + 0.5, y, z + 0.5, yaw, pitch);
    }

    /** Clears a flat stage: air above, frosted ground at y-1, so scenes are built on level ground. */
    private static void flatten(MinecraftServer server, BlockPos centre, int halfX, int zFrom, int zTo) {
        int x1 = centre.getX() - halfX, x2 = centre.getX() + halfX, y = centre.getY();
        for (int z = centre.getZ() + zFrom; z <= centre.getZ() + zTo; z += 6) {
            int z2 = Math.min(z + 5, centre.getZ() + zTo);
            cmd(server, String.format("fill %d %d %d %d %d %d minecraft:air", x1, y, z, x2, y + 24, z2));
            cmd(server, String.format("fill %d %d %d %d %d %d %s:gingerbread", x1, y - 4, z, x2, y - 2, z2, G));
            cmd(server, String.format("fill %d %d %d %d %d %d %s:frosted_gingerbread", x1, y - 1, z, x2, y - 1, z2, G));
        }
    }

    private static String at(BlockPos p, int dx, int dy, int dz) {
        return (p.getX() + dx) + " " + (p.getY() + dy) + " " + (p.getZ() + dz);
    }

    // ---- The shot list -------------------------------------------------------------------------

    private void buildScript() {
        client(mc -> {
            mc.options.hideGui = true;
            mc.options.fov().set(70);
            mc.options.renderDistance().set(16);
            mc.options.setCameraType(CameraType.FIRST_PERSON);
        });
        server(server -> {
            server.setDifficulty(Difficulty.EASY, true);
            server.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, server);
            server.getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(false, server);
            server.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(false, server);
            server.getGameRules().getRule(GameRules.RULE_DOMOBLOOT).set(false, server); // no loot clutter after kills
            player(server).setGameMode(GameType.SPECTATOR);
            camera(server, 0, 120, 0, 0, 0); // into the dimension
        });
        waitForChunks(80, 400);

        // 1. Gingerbread Grove at golden hour
        scenery("01_grove_dusk", ModDimensions.GROVE_BIOME, 12800, 16, 35, 18);
        // 2. Festive Fir Forest in the blue hour, lights glowing
        scenery("02_fir_forest_evening", ModDimensions.FESTIVE_FIR_FOREST, 12700, 4, 20, 4);
        // 3. Peppermint Peaks from above
        scenery("03_peppermint_peaks", ModDimensions.PEPPERMINT_PEAKS, 5000, 12, 140, 8);
        // 4. Cocoa Sea at sunset, with marshmallow islands
        server(server -> {
            ServerLevel g = grove(server);
            BlockPos p = find(g, ModDimensions.COCOA_SEA, BlockPos.ZERO);
            for (int i = 0; i < 5; i++) {
                int x = p.getX() - 10 + i * 9, z = p.getZ() + 14 + (i % 2) * 8;
                int y = g.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                cmd(server, String.format("place feature %s:marshmallow_island %d %d %d", G, x, y, z));
            }
            cmd(server, "time set 12300");
            camera(server, p.getX(), 66, p.getZ(), 0, 6);
        });
        waitForChunks(80, 500);
        waitTicks(140);
        shot("04_cocoa_sea_sunset");

        // 5-8. The village on a levelled stage in the Frosted Flats
        server(server -> {
            ServerLevel g = grove(server);
            siteA = flatSite(g, ModDimensions.FROSTED_FLATS, new BlockPos(400, 0, 400));
            flatten(server, siteA, 34, -18, 40);
            cmd(server, "place template " + G + ":gingerbread_house " + at(siteA, 3, -1, 8));
            cmd(server, "place template " + G + ":santas_workshop " + at(siteA, -19, -1, 8));
            String near = "execute positioned " + at(siteA, -11, 0, 14) + " run ";
            cmd(server, near + "data merge entity @e[type=" + G + ":santa_claus,sort=nearest,limit=1,distance=..20] {NoAI:1b}");
            cmd(server, near + "tp @e[type=" + G + ":santa_claus,sort=nearest,limit=1,distance=..20] "
                    + String.format("%.1f %d %.1f 180 0", siteA.getX() - 11.5, siteA.getY(), siteA.getZ() + 14.5));
            int[][] elfSpots = {{-8, 15, 150}, {-14, 16, 210}, {-7, 12, 120}};
            for (int[] spot : elfSpots) {
                cmd(server, near + "data merge entity @e[type=" + G + ":elf,sort=nearest,limit=1,distance=..20,tag=!posed] {NoAI:1b,Tags:[\"posed\"]}");
                cmd(server, near + "tp @e[type=" + G + ":elf,sort=nearest,limit=1,distance=..20,tag=posed,tag=!placed] "
                        + String.format("%.1f %d %.1f %d 0", siteA.getX() + spot[0] + 0.5, siteA.getY(), siteA.getZ() + spot[1] + 0.5, spot[2]));
                cmd(server, near + "tag @e[type=" + G + ":elf,tag=posed,tag=!placed,sort=nearest,limit=1] add placed");
            }
            cmd(server, "place feature " + G + ":festive_fir_tree " + at(siteA, 18, 0, 14));
            cmd(server, "place feature " + G + ":festive_fir_tree " + at(siteA, 24, 0, 22));
            cmd(server, "place feature " + G + ":festive_fir_tree " + at(siteA, -26, 0, 26));
            cmd(server, "place feature " + G + ":giant_candy_cane " + at(siteA, 20, 0, 4));
            cmd(server, "place feature " + G + ":gift_boxes " + at(siteA, 17, 0, 13));
            cmd(server, "place feature " + G + ":sweet_flowers " + at(siteA, 0, 0, 2));
            cmd(server, "time set 12300");
            camera(server, siteA.getX(), siteA.getY() + 9, siteA.getZ() - 16, 0, 20);
        });
        waitForChunks(100, 600);
        waitTicks(300);
        shot("05_village_dusk");
        server(server -> camera(server, siteA.getX() + 8, siteA.getY() + 2, siteA.getZ() + 1, 10, 6));
        waitTicks(120);
        shot("06_gingerbread_house");
        // inside Santa's Workshop (template origin is siteA + (-19, -1, 8))
        server(server -> camera(server, siteA.getX() - 11, siteA.getY() + 0.1, siteA.getZ() + 10, 0, 6));
        waitTicks(120);
        shot("07_santas_workshop_inside");
        server(server -> {
            cmd(server, "time set 15500");
            camera(server, siteA.getX() + 14, siteA.getY() + 1, siteA.getZ() + 8, -25, -4);
        });
        waitTicks(100);
        shot("08_christmas_tree_night");

        // 9-13. Portraits on a second stage
        server(server -> {
            ServerLevel g = grove(server);
            siteB = flatSite(g, ModDimensions.FROSTED_FLATS, new BlockPos(siteA.getX() + 200, 0, siteA.getZ()));
            flatten(server, siteB, 26, -20, 30);
            String[] mobs = {"gingerbread_man", "peppermint_pup", "elf", "santa_claus", "reindeer",
                    "cookie_soldier", "gumdrop_blob", "evil_snowman", "krampus"};
            for (int i = 0; i < mobs.length; i++) {
                String extra = mobs[i].equals("reindeer") ? ",Tame:1b,SaddleItem:{id:\"minecraft:saddle\",count:1}"
                        : mobs[i].equals("gumdrop_blob") ? ",Size:1" : "";
                cmd(server, "summon " + G + ":" + mobs[i] + " " + String.format("%.1f %d %d",
                        siteB.getX() + 0.5 + (i - 4) * 2.3, siteB.getY(), siteB.getZ() + 6)
                        + " {NoAI:1b,Rotation:[180f,0f],Tags:[\"cg\"],PersistenceRequired:1b" + extra + "}");
            }
            cmd(server, "fill " + at(siteB, -16, 0, 14) + " " + at(siteB, 16, 0, 14) + " " + G + ":gingerbread_bricks");
            for (int x = -14; x <= 14; x += 4) {
                cmd(server, "place feature " + G + ":festive_fir_tree " + at(siteB, x, 0, 17));
            }
            cmd(server, "time set 6000");
            camera(server, siteB.getX(), siteB.getY() + 0.6, siteB.getZ() - 2, 0, 4);
        });
        waitForChunks(80, 400);
        waitTicks(300);
        shot("09_meet_the_cast");

        // Santa's sleigh crossing the evening sky
        server(server -> {
            cmd(server, "kill @e[tag=cg]");
            cmd(server, "kill @e[type=item]");
            cmd(server, "time set 12900");
            camera(server, siteB.getX(), siteB.getY() + 1, siteB.getZ() - 2, 0, -26);
            cmd(server, "summon " + G + ":santa_sleigh " + at(siteB, -46, 7, 7) + " {Rotation:[-90f,0f]}");
        });
        client(mc -> mc.options.fov().set(45));
        waitTicks(82);
        shot("10_sleigh_a");
        waitTicks(8);
        shot("10_sleigh_b");
        waitTicks(8);
        shot("10_sleigh_c");
        waitTicks(8);
        shot("10_sleigh_d");
        waitTicks(8);
        shot("10_sleigh_e");
        client(mc -> mc.options.fov().set(70));

        // Presents floating down on parachutes
        server(server -> {
            cmd(server, "kill @e[type=" + G + ":santa_sleigh]");
            cmd(server, "time set 12600");
            int[][] spots = {{-3, 8, 5}, {0, 10, 7}, {3, 7, 5}, {-5, 12, 8}, {5, 11, 8}, {-1, 13, 9}, {2, 6, 4}};
            for (int i = 0; i < spots.length; i++) {
                cmd(server, "summon " + G + ":present_drop " + at(siteB, spots[i][0], spots[i][1], spots[i][2])
                        + " {Colour:" + (i % 3) + "}");
            }
            camera(server, siteB.getX(), siteB.getY() + 1, siteB.getZ() - 2, 0, -22);
        });
        waitTicks(25);
        shot("11_present_rain");

        // Krampus and his minions
        server(server -> {
            cmd(server, "kill @e[type=" + G + ":present_drop]");
            cmd(server, "time set 12950");
            cmd(server, "summon " + G + ":krampus " + at(siteB, 0, 0, 5) + " {NoAI:1b,Rotation:[180f,0f],Tags:[\"cg\"]}");
            cmd(server, "summon " + G + ":evil_snowman " + at(siteB, -3, 0, 9) + " {NoAI:1b,Rotation:[165f,0f],Tags:[\"cg\"]}");
            cmd(server, "summon " + G + ":evil_snowman " + at(siteB, 3, 0, 9) + " {NoAI:1b,Rotation:[195f,0f],Tags:[\"cg\"]}");
            camera(server, siteB.getX(), siteB.getY() - 0.4, siteB.getZ() + 1, 0, -16);
        });
        waitTicks(40);
        shot("12_krampus");

        // The Santa Suit, worn: third person, facing the camera
        server(server -> {
            cmd(server, "kill @e[tag=cg]");
            cmd(server, "kill @e[type=item]");
            cmd(server, "time set 6000");
            ServerPlayer player = player(server);
            player.setGameMode(GameType.CREATIVE);
            camera(server, siteB.getX(), siteB.getY(), siteB.getZ() + 8, 180, 8);
            for (String[] piece : new String[][]{{"head", "santa_hat"}, {"chest", "santa_coat"},
                    {"legs", "santa_trousers"}, {"feet", "santa_boots"}}) {
                server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(),
                        "item replace entity @p armor." + piece[0] + " with " + G + ":" + piece[1]);
            }
            server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(),
                    "item replace entity @p weapon.mainhand with " + G + ":candy_cane_sword");
        });
        client(mc -> {
            mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT);
            mc.options.fov().set(38);
        });
        waitTicks(40);
        shot("13_santa_suit");
        client(mc -> {
            mc.options.setCameraType(CameraType.FIRST_PERSON);
            mc.options.fov().set(70);
        });
        server(server -> player(server).setGameMode(GameType.SPECTATOR));

        // A wall of every block, plants in front
        server(server -> {
            List<String> blocks = new ArrayList<>();
            for (var key : BuiltInRegistries.BLOCK.keySet()) {
                String path = key.getPath();
                if (key.getNamespace().equals(G) && !path.endsWith("_door") && !path.endsWith("_button")
                        && !path.endsWith("_pressure_plate") && !path.endsWith("_sapling") && !path.equals("gumdrop_bush")
                        && !path.equals("festive_lights") && !path.equals("wreath") && !path.equals("christmas_star")
                        && !path.equals("sugar_grass") && !path.equals("lollipop_flower") && !path.equals("poinsettia")
                        && !path.equals("holly")) {
                    blocks.add(path);
                }
            }
            blocks.sort(String::compareTo);
            int perRow = 16;
            for (int i = 0; i < blocks.size(); i++) {
                int x = (i % perRow) - perRow / 2, y = 4 - i / perRow;
                cmd(server, "setblock " + at(siteB, x, y, 12) + " " + G + ":" + blocks.get(i));
            }
            String[] plants = {"lollipop_flower", "poinsettia", "holly", "sugar_grass", "candy_cane_sapling",
                    "festive_fir_sapling", "gumdrop_bush[age=3]", "christmas_star"};
            for (int i = 0; i < plants.length; i++) {
                cmd(server, "setblock " + at(siteB, -7 + i * 2, 0, 10) + " " + G + ":" + plants[i]);
            }
            cmd(server, "time set 6000");
            cmd(server, "kill @e[type=item]");
            camera(server, siteB.getX(), siteB.getY() + 2.4, siteB.getZ() + 2, 0, -2);
        });
        client(mc -> mc.options.fov().set(55));
        waitTicks(80);
        shot("14_block_gallery");

        client(mc -> {
            Constants.LOG.info("Showcase finished; closing the game");
            mc.stop();
        });
    }

    /** A landscape shot: the camera hovers above a biome, looking out across it. */
    private void scenery(String name, ResourceKey<Biome> biome, int time, int height, int yaw, int pitch) {
        server(server -> {
            BlockPos p = viewpoint(grove(server), biome, yaw);
            cmd(server, "time set " + time);
            camera(server, p.getX(), p.getY() + height, p.getZ(), yaw, pitch);
        });
        waitForChunks(100, 600);
        waitTicks(140);
        shot(name);
    }
}
