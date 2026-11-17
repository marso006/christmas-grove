package com.mental.gingerbreadgrove.datagen;

import com.mental.gingerbreadgrove.Constants;
import com.mental.gingerbreadgrove.registry.ModBlocks;
import com.mental.gingerbreadgrove.registry.ModEntities;
import com.mental.gingerbreadgrove.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/** English names. Most are derived from the registry id; list exceptions in {@link #OVERRIDES}. */
public class ModLanguageProvider extends FabricLanguageProvider {

    /** Registry path -> display name, for names that title-casing the id gets wrong. */
    private static final Map<String, String> OVERRIDES = Map.of(
            "red_lit_festive_fir_leaves", "Festive Fir Leaves with Red Lights",
            "gold_lit_festive_fir_leaves", "Festive Fir Leaves with Gold Lights",
            "blue_lit_festive_fir_leaves", "Festive Fir Leaves with Blue Lights");

    public ModLanguageProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, "en_us", registries);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registries, TranslationBuilder builder) {
        builder.add("itemGroup." + Constants.MOD_ID, Constants.MOD_NAME);

        for (Supplier<? extends Block> supplier : ModBlocks.all()) {
            Block block = supplier.get();
            builder.add(block, name(BuiltInRegistries.BLOCK.getKey(block).getPath()));
        }
        for (Supplier<? extends Item> supplier : ModItems.flatItems()) {
            Item item = supplier.get();
            if (item.getDescriptionId().startsWith("block.")) {
                continue; // items named after their block (e.g. gumdrops place a bush) are covered above
            }
            builder.add(item, name(BuiltInRegistries.ITEM.getKey(item).getPath()));
        }

        for (Supplier<? extends Item> supplier : ModItems.handheldItems()) {
            builder.add(supplier.get(), name(BuiltInRegistries.ITEM.getKey(supplier.get()).getPath()));
        }

        for (Supplier<? extends Item> egg : ModItems.spawnEggs()) {
            builder.add(egg.get(), name(BuiltInRegistries.ITEM.getKey(egg.get()).getPath()));
        }
        builder.add(ModEntities.GINGERBREAD_MAN.get(), "Gingerbread Man");
        builder.add(ModEntities.REINDEER.get(), "Reindeer");
        builder.add(ModEntities.PEPPERMINT_PUP.get(), "Peppermint Pup");
        builder.add(ModEntities.ELF.get(), "Elf");
        builder.add(ModEntities.SANTA_CLAUS.get(), "Santa Claus");
        builder.add(ModEntities.COOKIE_SOLDIER.get(), "Cookie Soldier");
        builder.add(ModEntities.GUMDROP_BLOB.get(), "Gumdrop Blob");
        builder.add(ModEntities.EVIL_SNOWMAN.get(), "Evil Snowman");
        builder.add(ModEntities.FROST_SNOWBALL.get(), "Frost Snowball");
        builder.add("entity.gingerbread_grove.gingerbread_man.caught", "You caught the Gingerbread Man! It crumbles into cookies.");
        builder.add("entity.gingerbread_grove.santa_claus.gift", "Ho ho ho! Here is a present for you, %s!");
        builder.add("entity.gingerbread_grove.santa_claus.christmas", "Ho ho ho! Merry Christmas, %s! Two presents today!");
        builder.add("entity.gingerbread_grove.santa_claus.tomorrow", "Ho ho! Patience, %s. Come back tomorrow for another present.");

        builder.add("item.gingerbread_grove.snow_globe.missing", "The globe is cloudy: the Gingerbread Grove dimension failed to load.");
    }

    static String name(String path) {
        return OVERRIDES.getOrDefault(path, Arrays.stream(path.split("_"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" ")));
    }
}
