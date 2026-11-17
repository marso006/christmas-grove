package com.mental.gingerbreadgrove.registry;

import com.mental.gingerbreadgrove.entity.CookieSoldier;
import com.mental.gingerbreadgrove.entity.Elf;
import com.mental.gingerbreadgrove.entity.EvilSnowman;
import com.mental.gingerbreadgrove.entity.FrostSnowball;
import com.mental.gingerbreadgrove.entity.GingerbreadMan;
import com.mental.gingerbreadgrove.entity.GumdropBlob;
import com.mental.gingerbreadgrove.entity.PeppermintPup;
import com.mental.gingerbreadgrove.entity.Reindeer;
import com.mental.gingerbreadgrove.entity.SantaClaus;
import com.mental.gingerbreadgrove.platform.Services;
import com.mental.gingerbreadgrove.platform.services.IEntityHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Supplier;

/** Entity types, their default attributes, and where they may spawn. Biome spawn lists are datagen. */
public final class ModEntities {

    // ---- Friendly ------------------------------------------------------------------------------
    public static final Supplier<EntityType<GingerbreadMan>> GINGERBREAD_MAN = register("gingerbread_man",
            EntityType.Builder.of(GingerbreadMan::new, MobCategory.CREATURE).sized(0.45F, 0.95F).eyeHeight(0.8F).clientTrackingRange(8));
    public static final Supplier<EntityType<Reindeer>> REINDEER = register("reindeer",
            EntityType.Builder.of(Reindeer::new, MobCategory.CREATURE).sized(1.2F, 1.6F).eyeHeight(1.52F).clientTrackingRange(10));
    public static final Supplier<EntityType<PeppermintPup>> PEPPERMINT_PUP = register("peppermint_pup",
            EntityType.Builder.of(PeppermintPup::new, MobCategory.CREATURE).sized(0.6F, 0.85F).eyeHeight(0.68F).clientTrackingRange(10));
    public static final Supplier<EntityType<Elf>> ELF = register("elf",
            EntityType.Builder.of(Elf::new, MobCategory.CREATURE).sized(0.5F, 1.35F).eyeHeight(1.15F).clientTrackingRange(10));
    public static final Supplier<EntityType<SantaClaus>> SANTA_CLAUS = register("santa_claus",
            EntityType.Builder.of(SantaClaus::new, MobCategory.CREATURE).sized(0.8F, 2.1F).eyeHeight(1.85F).clientTrackingRange(10));

    // ---- Hostile -------------------------------------------------------------------------------
    public static final Supplier<EntityType<CookieSoldier>> COOKIE_SOLDIER = register("cookie_soldier",
            EntityType.Builder.of(CookieSoldier::new, MobCategory.MONSTER).sized(0.6F, 2.2F).eyeHeight(1.85F).clientTrackingRange(8));
    public static final Supplier<EntityType<GumdropBlob>> GUMDROP_BLOB = register("gumdrop_blob",
            EntityType.Builder.of(GumdropBlob::new, MobCategory.MONSTER).sized(0.52F, 0.52F).eyeHeight(0.325F).clientTrackingRange(10));
    public static final Supplier<EntityType<EvilSnowman>> EVIL_SNOWMAN = register("evil_snowman",
            EntityType.Builder.of(EvilSnowman::new, MobCategory.MONSTER).sized(0.7F, 1.9F).eyeHeight(1.7F).clientTrackingRange(8));

    // ---- Projectiles ---------------------------------------------------------------------------
    public static final Supplier<EntityType<FrostSnowball>> FROST_SNOWBALL = register("frost_snowball",
            EntityType.Builder.<FrostSnowball>of(FrostSnowball::new, MobCategory.MISC).sized(0.25F, 0.25F)
                    .clientTrackingRange(4).updateInterval(10));

    private ModEntities() {
    }

    private static <T extends Entity> Supplier<EntityType<T>> register(String name, EntityType.Builder<T> builder) {
        return Services.REGISTRY.register(BuiltInRegistries.ENTITY_TYPE, name, () -> builder.build(name));
    }

    /** Forces class loading so the static fields above register. Call before ModItems (spawn eggs). */
    public static void init() {
        IEntityHelper helper = Services.ENTITIES;
        helper.registerAttributes(GINGERBREAD_MAN, GingerbreadMan::createAttributes);
        helper.registerAttributes(REINDEER, Reindeer::createAttributes);
        helper.registerAttributes(PEPPERMINT_PUP, Wolf::createAttributes);
        helper.registerAttributes(ELF, Elf::createAttributes);
        helper.registerAttributes(SANTA_CLAUS, SantaClaus::createAttributes);
        helper.registerAttributes(COOKIE_SOLDIER, CookieSoldier::createAttributes);
        helper.registerAttributes(GUMDROP_BLOB, Monster::createMonsterAttributes);
        helper.registerAttributes(EVIL_SNOWMAN, EvilSnowman::createAttributes);

        Heightmap.Types ground = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;
        helper.registerSpawnPlacement(GINGERBREAD_MAN, SpawnPlacementTypes.ON_GROUND, ground, Mob::checkMobSpawnRules);
        helper.registerSpawnPlacement(REINDEER, SpawnPlacementTypes.ON_GROUND, ground, Mob::checkMobSpawnRules);
        helper.registerSpawnPlacement(PEPPERMINT_PUP, SpawnPlacementTypes.ON_GROUND, ground, Mob::checkMobSpawnRules);
        helper.registerSpawnPlacement(ELF, SpawnPlacementTypes.ON_GROUND, ground, Mob::checkMobSpawnRules);
        helper.registerSpawnPlacement(COOKIE_SOLDIER, SpawnPlacementTypes.ON_GROUND, ground, Monster::checkMonsterSpawnRules);
        helper.registerSpawnPlacement(GUMDROP_BLOB, SpawnPlacementTypes.ON_GROUND, ground, GumdropBlob::checkGumdropSpawnRules);
        helper.registerSpawnPlacement(EVIL_SNOWMAN, SpawnPlacementTypes.ON_GROUND, ground, Monster::checkMonsterSpawnRules);
    }
}
