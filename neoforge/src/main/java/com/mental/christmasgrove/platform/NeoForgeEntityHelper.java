package com.mental.christmasgrove.platform;

import com.mental.christmasgrove.platform.services.IEntityHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * NeoForge creates entity types during the registry event, so attributes and spawn placements are
 * queued here and applied in the mod-bus events (wired up in ChristmasGroveNeoForge).
 */
public class NeoForgeEntityHelper implements IEntityHelper {

    private static final List<Consumer<EntityAttributeCreationEvent>> ATTRIBUTES = new ArrayList<>();
    private static final List<Consumer<RegisterSpawnPlacementsEvent>> PLACEMENTS = new ArrayList<>();

    @Override
    public <T extends LivingEntity> void registerAttributes(Supplier<EntityType<T>> type, Supplier<AttributeSupplier.Builder> attributes) {
        ATTRIBUTES.add(event -> event.put(type.get(), attributes.get().build()));
    }

    @Override
    public <T extends Mob> void registerSpawnPlacement(Supplier<EntityType<T>> type, SpawnPlacementType placement,
                                                       Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> predicate) {
        PLACEMENTS.add(event -> event.register(type.get(), placement, heightmap, predicate,
                RegisterSpawnPlacementsEvent.Operation.REPLACE));
    }

    @Override
    public Item createSpawnEgg(Supplier<? extends EntityType<? extends Mob>> type, int background, int highlight, Item.Properties properties) {
        // Since NeoForge 21.2 entity types are registered before items, so the type already exists here.
        return new SpawnEggItem(type.get(), background, highlight, properties);
    }

    public static void onAttributes(EntityAttributeCreationEvent event) {
        ATTRIBUTES.forEach(action -> action.accept(event));
    }

    public static void onSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        PLACEMENTS.forEach(action -> action.accept(event));
    }
}
