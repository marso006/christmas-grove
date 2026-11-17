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
        builder.add(ModEntities.KRAMPUS.get(), "Krampus");
        builder.add(ModEntities.SANTA_SLEIGH.get(), "Santa's Sleigh");
        builder.add(ModEntities.PRESENT_DROP.get(), "Falling Present");
        builder.add("entity.gingerbread_grove.santa_sleigh.incoming", "Jingle bells! Look up, Santa's sleigh is flying overhead!");
        builder.add("entity.gingerbread_grove.santa_sleigh.letter", "Santa read your letter! Look to the sky...");
        builder.add("item.gingerbread_grove.letter_to_santa.tooltip", "Use it and Santa's sleigh flies over you, dropping presents");
        builder.add("item.gingerbread_grove.letter_to_santa.naughty", "Santa reads your letter and frowns. Naughty children get no visit! (Be nice first.)");
        builder.add("entity.gingerbread_grove.krampus.arrives", "Chains rattle in the dark... You have been very naughty. Krampus has come for you!");
        builder.add("entity.gingerbread_grove.krampus.bell", "The bell tolls, and Krampus answers.");
        builder.add("entity.gingerbread_grove.krampus.defeated", "Krampus is defeated! Santa wipes your name off the naughty list.");
        builder.add("entity.gingerbread_grove.krampus.peaceful", "Krampus does not visit on Peaceful difficulty.");
        builder.add("entity.gingerbread_grove.santa_claus.naughty", "Ho... hmm. You have been naughty, %s. A lump of coal for you!");
        builder.add("entity.gingerbread_grove.santa_claus.nice", "Ho ho ho! You have been very nice, %s! Extra presents for you!");

        builder.add("naughty_nice.gingerbread_grove.naughty", "Naughty! (%s)");
        builder.add("naughty_nice.gingerbread_grove.nice", "Nice! (%s)");
        builder.add("naughty_nice.gingerbread_grove.reason.gingerbread_man", "you hurt a Gingerbread Man");
        builder.add("naughty_nice.gingerbread_grove.reason.reindeer", "you hurt a Reindeer");
        builder.add("naughty_nice.gingerbread_grove.reason.pup", "you hurt a Peppermint Pup");
        builder.add("naughty_nice.gingerbread_grove.reason.elf", "you hurt an Elf");
        builder.add("naughty_nice.gingerbread_grove.reason.hit_santa", "you hit Santa!");
        builder.add("naughty_nice.gingerbread_grove.reason.trade", "you traded with an Elf");
        builder.add("naughty_nice.gingerbread_grove.reason.tamed_pup", "you adopted a Peppermint Pup");
        builder.add("naughty_nice.gingerbread_grove.reason.defeated_monster", "you protected the Grove");
        builder.add("naughty_nice.gingerbread_grove.reason.gift_given", "someone unwrapped your present");

        builder.add("item.gingerbread_grove.nice_list.tooltip", "Read it to see whether you are naughty or nice");
        builder.add("item.gingerbread_grove.nice_list.nice", "You are on the NICE list! (score %s)");
        builder.add("item.gingerbread_grove.nice_list.neutral", "You are neither naughty nor nice. Yet. (score %s)");
        builder.add("item.gingerbread_grove.nice_list.naughty", "You are on the NAUGHTY list! (score %s)");
        builder.add("item.gingerbread_grove.krampus_bell.tooltip", "Ring it to summon Krampus. Are you ready?");

        builder.add("container.gingerbread_grove.gift_box", "Gift Box");
        builder.add("container.gingerbread_grove.cookie_jar", "Cookie Jar");
        builder.add("block.gingerbread_grove.gift_box.sealed", "This present is sealed. Use it to unwrap it!");
        builder.add("block.gingerbread_grove.gift_box.not_yours", "This present was wrapped by %s.");
        builder.add("block.gingerbread_grove.gift_box.from", "A present from %s!");
        builder.add("block.gingerbread_grove.gift_box.empty", "It is empty. Sneak and use it to wrap something inside.");
        builder.add("block.gingerbread_grove.gift_box.tooltip.surprise", "A mysterious present. What could be inside?");
        builder.add("block.gingerbread_grove.gift_box.tooltip.wrapped", "Wrapped by %s");
        builder.add("block.gingerbread_grove.gift_box.tooltip.empty", "Empty. Place it, then sneak + use to wrap a present.");
        builder.add("block.gingerbread_grove.advent_calendar.sealed", "The little doors are sealed until December 1st.");
        builder.add("block.gingerbread_grove.advent_calendar.finished", "Merry Christmas! The calendar is finished for this year.");
        builder.add("block.gingerbread_grove.advent_calendar.tomorrow", "You already opened door %s. Come back tomorrow!");
        builder.add("block.gingerbread_grove.advent_calendar.opened", "You open door %s of the Advent Calendar!");
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
