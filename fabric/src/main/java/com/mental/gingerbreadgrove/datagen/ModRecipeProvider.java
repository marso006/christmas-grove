package com.mental.gingerbreadgrove.datagen;

import com.mental.gingerbreadgrove.registry.ModBlocks;
import com.mental.gingerbreadgrove.registry.ModItems;
import com.mental.gingerbreadgrove.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        travel(output);
        buildingBlocks(output);
        woods(output);
        materials(output);
        food(output);
        decor(output);
        gear(output);
        festive(output);
    }

    private void festive(RecipeOutput output) {
        // Empty gift boxes for wrapping your own presents
        shapeless(output, ModBlocks.RED_GIFT_BOX.get(), 1, Items.PAPER, Items.PAPER, Items.STRING, Items.RED_DYE);
        shapeless(output, ModBlocks.GREEN_GIFT_BOX.get(), 1, Items.PAPER, Items.PAPER, Items.STRING, Items.GREEN_DYE);
        shapeless(output, ModBlocks.BLUE_GIFT_BOX.get(), 1, Items.PAPER, Items.PAPER, Items.STRING, Items.BLUE_DYE);
        shapeless(output, ModItems.NICE_LIST.get(), 1, Items.PAPER, Items.PAPER, Items.FEATHER, Items.INK_SAC);
        shapeless(output, ModItems.LETTER_TO_SANTA.get(), 1, Items.PAPER, Items.FEATHER, Items.INK_SAC, ModItems.CANDY_CANE.get());
        shapeless(output, ModItems.KRAMPUS_BELL.get(), 1, Items.BELL, Items.CHAIN, Items.COAL);
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ModItems.SLEIGH_BELLS.get())
                .pattern("NLN").pattern("G G")
                .define('N', Items.GOLD_NUGGET).define('L', Items.LEATHER).define('G', Items.GOLD_INGOT)
                .unlockedBy(getHasName(Items.GOLD_INGOT), has(Items.GOLD_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.COOKIE_JAR.get())
                .pattern(" S ").pattern("GCG").pattern("GGG")
                .define('S', ItemTags.WOODEN_SLABS).define('G', Items.GLASS).define('C', ModItems.GINGERBREAD_COOKIE.get())
                .unlockedBy(getHasName(ModItems.GINGERBREAD_COOKIE.get()), has(ModItems.GINGERBREAD_COOKIE.get()))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.ADVENT_CALENDAR.get())
                .pattern("PPP").pattern("PCP").pattern("PPP")
                .define('P', ModBlocks.FESTIVE_FIR.planks().get()).define('C', ModItems.CANDY_CANE.get())
                .unlockedBy(getHasName(ModItems.CANDY_CANE.get()), has(ModItems.CANDY_CANE.get()))
                .save(output);
    }

    private void gear(RecipeOutput output) {
        ItemLike crystal = ModItems.SUGAR_CRYSTAL.get();
        ItemLike cane = ModItems.CANDY_CANE.get();
        tool(output, ModItems.CANDY_CANE_SWORD.get(), crystal, cane, " C ", " C ", " S ");
        tool(output, ModItems.SUGAR_CRYSTAL_PICKAXE.get(), crystal, cane, "CCC", " S ", " S ");
        tool(output, ModItems.SUGAR_CRYSTAL_AXE.get(), crystal, cane, "CC ", "CS ", " S ");
        tool(output, ModItems.SUGAR_CRYSTAL_SHOVEL.get(), crystal, cane, " C ", " S ", " S ");
        tool(output, ModItems.SUGAR_CRYSTAL_HOE.get(), crystal, cane, "CC ", " S ", " S ");

        // Red wool suit with white wool trim
        armor(output, ModItems.SANTA_HAT.get(), "RRR", "W W");
        armor(output, ModItems.SANTA_COAT.get(), "R R", "RWR", "RRR");
        armor(output, ModItems.SANTA_TROUSERS.get(), "RRR", "R R", "W W");
        armor(output, ModItems.SANTA_BOOTS.get(), "W W", "B B");
    }

    /** C = sugar crystal, S = candy cane (the handle). */
    private static void tool(RecipeOutput output, ItemLike result, ItemLike crystal, ItemLike cane, String... pattern) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, result);
        for (String row : pattern) {
            builder.pattern(row);
        }
        builder.define('C', crystal).define('S', cane)
                .unlockedBy(getHasName(crystal), has(crystal))
                .save(output);
    }

    /** R = red wool, W = white wool, B = black wool. */
    private static void armor(RecipeOutput output, ItemLike result, String... pattern) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, result);
        String all = String.join("", pattern);
        for (String row : pattern) {
            builder.pattern(row);
        }
        if (all.contains("R")) builder.define('R', Items.RED_WOOL);
        if (all.contains("W")) builder.define('W', Items.WHITE_WOOL);
        if (all.contains("B")) builder.define('B', Items.BLACK_WOOL);
        builder.unlockedBy(getHasName(Items.RED_WOOL), has(Items.RED_WOOL)).save(output);
    }

    private void travel(RecipeOutput output) {
        // A glass dome with a cookie inside, on a wooden and snowy base.
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ModItems.SNOW_GLOBE.get())
                .pattern("GGG").pattern("GCG").pattern("WSW")
                .define('G', Items.GLASS).define('C', Items.COOKIE).define('S', Items.SNOW_BLOCK).define('W', ItemTags.PLANKS)
                .unlockedBy(getHasName(Items.COOKIE), has(Items.COOKIE))
                .save(output);
    }

    private void buildingBlocks(RecipeOutput output) {
        // Bricks: 2x2 of the raw block, and stonecutting
        twoByTwo(output, ModBlocks.PEPPERMINT_BRICKS.base().get(), ModBlocks.PEPPERMINT_STONE.base().get(), 4);
        twoByTwo(output, ModBlocks.ICING_BRICKS.base().get(), ModBlocks.FROSTING.get(), 4);
        twoByTwo(output, ModBlocks.GINGERBREAD_BRICKS.base().get(), ModBlocks.GINGERBREAD.get(), 4);
        twoByTwo(output, ModBlocks.COOKIE_TILES.base().get(), ModItems.SUGAR_COOKIE.get(), 4);
        stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, ModBlocks.PEPPERMINT_BRICKS.base().get(),
                ModBlocks.PEPPERMINT_STONE.base().get());

        for (BlockFamily family : ModFamilies.stone()) {
            generateRecipes(output, family, FeatureFlags.VANILLA_SET);
        }
        for (ModBlocks.StoneSet set : ModBlocks.stoneSets()) {
            ItemLike base = set.base().get();
            stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, set.stairs().get(), base);
            stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, set.slab().get(), base, 2);
            if (set.wall() != null) {
                stonecutterResultFromBase(output, RecipeCategory.DECORATIONS, set.wall().get(), base);
            }
        }

        // Candy glass is smelted from sugar sand, like glass from sand.
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModBlocks.SUGAR_SAND.get()), RecipeCategory.BUILDING_BLOCKS,
                        ModBlocks.CANDY_GLASS.get(), 0.1F, 200)
                .unlockedBy(getHasName(ModBlocks.SUGAR_SAND.get()), has(ModBlocks.SUGAR_SAND.get()))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.CANDY_GLASS_PANE.get(), 16)
                .pattern("GGG").pattern("GGG")
                .define('G', ModBlocks.CANDY_GLASS.get())
                .unlockedBy(getHasName(ModBlocks.CANDY_GLASS.get()), has(ModBlocks.CANDY_GLASS.get()))
                .save(output);
        // Custom unpacking name: "marshmallow" is already the sugar-and-egg recipe.
        nineBlockStorageRecipesRecipesWithCustomUnpacking(output, RecipeCategory.FOOD, ModItems.MARSHMALLOW.get(),
                RecipeCategory.BUILDING_BLOCKS, ModBlocks.MARSHMALLOW_BLOCK.get(),
                "marshmallow_from_marshmallow_block", "marshmallow");
    }

    private void woods(RecipeOutput output) {
        for (ModBlocks.WoodSet wood : ModBlocks.woodSets()) {
            planksFromLog(output, wood.planks().get(), ModTags.Items.logs(wood.name()), 4);
            woodFromLogs(output, wood.wood().get(), wood.log().get());
            woodFromLogs(output, wood.strippedWood().get(), wood.strippedLog().get());
        }
        for (BlockFamily family : ModFamilies.wood()) {
            generateRecipes(output, family, FeatureFlags.VANILLA_SET);
        }
    }

    private void materials(RecipeOutput output) {
        nineBlockStorageRecipes(output, RecipeCategory.MISC, ModItems.SUGAR_CRYSTAL.get(),
                RecipeCategory.BUILDING_BLOCKS, ModBlocks.SUGAR_CRYSTAL_BLOCK.get());
        oreSmelting(output, List.of(ModBlocks.SUGAR_CRYSTAL_ORE.get()), RecipeCategory.MISC, ModItems.SUGAR_CRYSTAL.get(),
                1.0F, 200, "sugar_crystal");
        oreBlasting(output, List.of(ModBlocks.SUGAR_CRYSTAL_ORE.get()), RecipeCategory.MISC, ModItems.SUGAR_CRYSTAL.get(),
                1.0F, 100, "sugar_crystal");
        oreSmelting(output, List.of(ModBlocks.CINNAMON_ORE.get()), RecipeCategory.MISC, ModItems.CINNAMON_STICK.get(),
                0.5F, 200, "cinnamon_stick");
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MUG.get(), 2)
                .pattern("B B").pattern(" B ")
                .define('B', Items.BRICK)
                .unlockedBy(getHasName(Items.BRICK), has(Items.BRICK))
                .save(output);
    }

    private void food(RecipeOutput output) {
        shapeless(output, ModItems.GINGERBREAD_COOKIE.get(), 4, Items.WHEAT, Items.WHEAT, ModItems.CINNAMON_STICK.get(), Items.SUGAR);
        shapeless(output, ModItems.SUGAR_COOKIE.get(), 4, Items.WHEAT, Items.WHEAT, Items.SUGAR, Items.EGG);
        shapeless(output, ModItems.CANDY_CANE.get(), 2, Items.SUGAR, Items.SUGAR, Items.RED_DYE);
        shapeless(output, ModItems.MARSHMALLOW.get(), 4, Items.SUGAR, Items.SUGAR, Items.EGG);
        shapeless(output, ModItems.HOT_COCOA.get(), 1, ModItems.MUG.get(), Items.COCOA_BEANS, Items.SUGAR, Items.MILK_BUCKET);
        shapeless(output, ModItems.EGGNOG.get(), 1, Items.GLASS_BOTTLE, Items.EGG, Items.SUGAR, Items.MILK_BUCKET,
                ModItems.CINNAMON_STICK.get());
        shapeless(output, ModItems.FRUITCAKE_SLICE.get(), 4, Items.WHEAT, Items.SUGAR, Items.EGG, Items.SWEET_BERRIES,
                ModItems.GUMDROPS.get());
        // Roast marshmallows over a campfire (or in a smoker or furnace).
        simpleCookingRecipe(output, "smelting", net.minecraft.world.item.crafting.RecipeSerializer.SMELTING_RECIPE,
                net.minecraft.world.item.crafting.SmeltingRecipe::new, 200, ModItems.MARSHMALLOW.get(), ModItems.ROASTED_MARSHMALLOW.get(), 0.35F);
        simpleCookingRecipe(output, "smoking", net.minecraft.world.item.crafting.RecipeSerializer.SMOKING_RECIPE,
                net.minecraft.world.item.crafting.SmokingRecipe::new, 100, ModItems.MARSHMALLOW.get(), ModItems.ROASTED_MARSHMALLOW.get(), 0.35F);
        simpleCookingRecipe(output, "campfire_cooking", net.minecraft.world.item.crafting.RecipeSerializer.CAMPFIRE_COOKING_RECIPE,
                net.minecraft.world.item.crafting.CampfireCookingRecipe::new, 300, ModItems.MARSHMALLOW.get(), ModItems.ROASTED_MARSHMALLOW.get(), 0.35F);
    }

    private void decor(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.FESTIVE_LIGHTS.get(), 6)
                .pattern("SSS").pattern("RGB")
                .define('S', Items.STRING).define('R', Items.RED_DYE).define('G', Items.GLOWSTONE_DUST).define('B', Items.BLUE_DYE)
                .unlockedBy(getHasName(Items.GLOWSTONE_DUST), has(Items.GLOWSTONE_DUST))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.WREATH.get(), 2)
                .pattern("LLL").pattern("LHL").pattern("LLL")
                .define('L', ModBlocks.FESTIVE_FIR.leaves().get()).define('H', ModBlocks.HOLLY.get())
                .unlockedBy(getHasName(ModBlocks.HOLLY.get()), has(ModBlocks.HOLLY.get()))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.CHRISTMAS_STAR.get())
                .pattern(" G ").pattern("GDG").pattern(" G ")
                .define('G', Items.GOLD_INGOT).define('D', Items.GLOWSTONE_DUST)
                .unlockedBy(getHasName(Items.GOLD_INGOT), has(Items.GOLD_INGOT))
                .save(output);
    }

    private static void twoByTwo(RecipeOutput output, ItemLike result, ItemLike input, int count) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result, count)
                .pattern("##").pattern("##")
                .define('#', input)
                .unlockedBy(getHasName(input), has(input))
                .save(output);
    }

    private static void shapeless(RecipeOutput output, ItemLike result, int count, ItemLike... inputs) {
        ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, result, count);
        for (ItemLike input : inputs) {
            builder.requires(input);
        }
        builder.unlockedBy(getHasName(inputs[inputs.length - 1]), has(inputs[inputs.length - 1])).save(output);
    }
}
