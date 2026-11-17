package com.mental.christmasgrove.platform.services;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Supplier;

/** Entity registration steps that each loader does differently. */
public interface IEntityHelper {

    /** Default attributes (health, speed...) for a living entity type. */
    <T extends LivingEntity> void registerAttributes(Supplier<EntityType<T>> type, Supplier<AttributeSupplier.Builder> attributes);

    /** Where and when a mob type may spawn naturally. */
    <T extends Mob> void registerSpawnPlacement(Supplier<EntityType<T>> type, SpawnPlacementType placement,
                                                Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> predicate);

    /** A spawn egg. NeoForge needs a deferred egg because items register before entity types there. */
    Item createSpawnEgg(Supplier<? extends EntityType<? extends Mob>> type, int background, int highlight, Item.Properties properties);
}
