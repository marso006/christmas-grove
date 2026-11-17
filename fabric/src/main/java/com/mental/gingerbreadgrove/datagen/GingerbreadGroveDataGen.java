package com.mental.gingerbreadgrove.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

/**
 * Runs with {@code ./gradlew :fabric:runDatagen}. Output goes to common/src/generated/resources,
 * which both the Fabric and NeoForge jars include, so data is generated once for both loaders.
 */
public class GingerbreadGroveDataGen implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(ModModelProvider::new);
        pack.addProvider(ModLanguageProvider::new);
        pack.addProvider(ModBlockLootProvider::new);
        pack.addProvider(ModBlockTagProvider::new);
        pack.addProvider(ModRecipeProvider::new);
    }
}
