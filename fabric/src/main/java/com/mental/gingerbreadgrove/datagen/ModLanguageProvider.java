package com.mental.gingerbreadgrove.datagen;

import com.mental.gingerbreadgrove.Constants;
import com.mental.gingerbreadgrove.registry.ModBlocks;
import com.mental.gingerbreadgrove.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/** English names. Most are derived from the registry id; list exceptions in {@link #OVERRIDES}. */
public class ModLanguageProvider extends FabricLanguageProvider {

    /** Registry path -> display name, for names that title-casing the id gets wrong. */
    private static final Map<String, String> OVERRIDES = Map.of();

    public ModLanguageProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, "en_us", registries);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registries, TranslationBuilder builder) {
        builder.add("itemGroup." + Constants.MOD_ID, Constants.MOD_NAME);

        for (var supplier : ModBlocks.all()) {
            Block block = supplier.get();
            builder.add(block, name(BuiltInRegistries.BLOCK.getKey(block).getPath()));
        }

        builder.add(ModItems.SNOW_GLOBE.get(), "Snow Globe");
        builder.add("item.gingerbread_grove.snow_globe.missing", "The globe is cloudy: the Gingerbread Grove dimension failed to load.");
    }

    static String name(String path) {
        return OVERRIDES.getOrDefault(path, Arrays.stream(path.split("_"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" ")));
    }
}
