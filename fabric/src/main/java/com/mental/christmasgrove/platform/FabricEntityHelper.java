package com.mental.christmasgrove.platform;

import com.mental.christmasgrove.platform.services.IEntityHelper;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Supplier;

/** Fabric registers entity types immediately, so everything here can resolve its supplier right away. */
public class FabricEntityHelper implements IEntityHelper {

    @Override
    public <T extends LivingEntity> void registerAttributes(Supplier<EntityType<T>> type, Supplier<AttributeSupplier.Builder> attributes) {
        FabricDefaultAttributeRegistry.register(type.get(), attributes.get());
    }

    @Override
    public <T extends Mob> void registerSpawnPlacement(Supplier<EntityType<T>> type, SpawnPlacementType placement,
                                                       Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> predicate) {
        SpawnPlacements.register(type.get(), placement, heightmap, predicate);
    }

    @Override
    public Item createSpawnEgg(Supplier<? extends EntityType<? extends Mob>> type, Item.Properties properties) {
        return new SpawnEggItem(type.get(), properties);
    }
}
