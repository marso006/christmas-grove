package com.mental.christmasgrove.datagen;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.advancements.Advancement;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.data.worldgen.BootstrapContext;
import com.mental.christmasgrove.registry.ModBlocks;
import com.mental.christmasgrove.registry.ModItems;
import com.mental.christmasgrove.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
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

    public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, BootstrapContext<Recipe<?>> recipes,
                                                  BootstrapContext<Advancement> advancements) {
        return new Recipes(recipes, advancements);
    }

    @Override
    public String getName() {
        return "Christmas Grove recipes";
    }

    /** Since 1.21.2 recipes are built inside a RecipeProvider instance that holds the output. */
    private static final class Recipes extends RecipeProvider {

        Recipes(BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
            super(recipes, advancements);
        }

        @Override
        public void buildRecipes() {
            travel();
            buildingBlocks();
            woods();
            materials();
            food();
            decor();
            gear();
            festive();
        }

        private void festive() {
            // Empty gift boxes for wrapping your own presents
            shapelessOf(ModBlocks.RED_GIFT_BOX.get(), 1, Items.PAPER, Items.PAPER, Items.STRING, Items.DYE.pick(DyeColor.RED));
            shapelessOf(ModBlocks.GREEN_GIFT_BOX.get(), 1, Items.PAPER, Items.PAPER, Items.STRING, Items.DYE.pick(DyeColor.GREEN));
            shapelessOf(ModBlocks.BLUE_GIFT_BOX.get(), 1, Items.PAPER, Items.PAPER, Items.STRING, Items.DYE.pick(DyeColor.BLUE));
            shapelessOf(ModItems.NICE_LIST.get(), 1, Items.PAPER, Items.PAPER, Items.FEATHER, Items.INK_SAC);
            shapelessOf(ModItems.LETTER_TO_SANTA.get(), 1, Items.PAPER, Items.FEATHER, Items.INK_SAC, ModItems.CANDY_CANE.get());
            shapelessOf(ModItems.KRAMPUS_BELL.get(), 1, Items.BELL, Items.IRON_CHAIN, Items.COAL);
            shaped(RecipeCategory.TRANSPORTATION, ModItems.SLEIGH_BELLS.get())
                    .pattern("NLN").pattern("G G")
                    .define('N', Items.GOLD_NUGGET).define('L', Items.LEATHER).define('G', Items.GOLD_INGOT)
                    .unlockedBy(getHasName(Items.GOLD_INGOT), has(Items.GOLD_INGOT))
                    .save(output);
            shaped(RecipeCategory.DECORATIONS, ModBlocks.COOKIE_JAR.get())
                    .pattern(" S ").pattern("GCG").pattern("GGG")
                    .define('S', ItemTags.WOODEN_SLABS).define('G', Items.GLASS).define('C', ModItems.GINGERBREAD_COOKIE.get())
                    .unlockedBy(getHasName(ModItems.GINGERBREAD_COOKIE.get()), has(ModItems.GINGERBREAD_COOKIE.get()))
                    .save(output);
            shaped(RecipeCategory.DECORATIONS, ModBlocks.ADVENT_CALENDAR.get())
                    .pattern("PPP").pattern("PCP").pattern("PPP")
                    .define('P', ModBlocks.FESTIVE_FIR.planks().get()).define('C', ModItems.CANDY_CANE.get())
                    .unlockedBy(getHasName(ModItems.CANDY_CANE.get()), has(ModItems.CANDY_CANE.get()))
                    .save(output);
        }

        private void gear() {
            ItemLike crystal = ModItems.SUGAR_CRYSTAL.get();
            ItemLike cane = ModItems.CANDY_CANE.get();
            tool(ModItems.CANDY_CANE_SWORD.get(), crystal, cane, " C ", " C ", " S ");
            tool(ModItems.SUGAR_CRYSTAL_PICKAXE.get(), crystal, cane, "CCC", " S ", " S ");
            tool(ModItems.SUGAR_CRYSTAL_AXE.get(), crystal, cane, "CC ", "CS ", " S ");
            tool(ModItems.SUGAR_CRYSTAL_SHOVEL.get(), crystal, cane, " C ", " S ", " S ");
            tool(ModItems.SUGAR_CRYSTAL_HOE.get(), crystal, cane, "CC ", " S ", " S ");

            // Red wool suit with white wool trim
            armor(ModItems.SANTA_HAT.get(), "RRR", "W W");
            armor(ModItems.SANTA_COAT.get(), "R R", "RWR", "RRR");
            armor(ModItems.SANTA_TROUSERS.get(), "RRR", "R R", "W W");
            armor(ModItems.SANTA_BOOTS.get(), "W W", "B B");
        }

        /** C = sugar crystal, S = candy cane (the handle). */
        private void tool(ItemLike result, ItemLike crystal, ItemLike cane, String... pattern) {
            ShapedRecipeBuilder builder = shaped(RecipeCategory.TOOLS, result);
            for (String row : pattern) {
                builder.pattern(row);
            }
            builder.define('C', crystal).define('S', cane)
                    .unlockedBy(getHasName(crystal), has(crystal))
                    .save(output);
        }

        /** R = red wool, W = white wool, B = black wool. */
        private void armor(ItemLike result, String... pattern) {
            ShapedRecipeBuilder builder = shaped(RecipeCategory.COMBAT, result);
            String all = String.join("", pattern);
            for (String row : pattern) {
                builder.pattern(row);
            }
            if (all.contains("R")) builder.define('R', Items.WOOL.pick(DyeColor.RED));
            if (all.contains("W")) builder.define('W', Items.WOOL.pick(DyeColor.WHITE));
            if (all.contains("B")) builder.define('B', Items.WOOL.pick(DyeColor.BLACK));
            builder.unlockedBy(getHasName(Items.WOOL.pick(DyeColor.RED)), has(Items.WOOL.pick(DyeColor.RED))).save(output);
        }

        private void travel() {
            // A glass dome with a cookie inside, on a wooden and snowy base.
            shaped(RecipeCategory.TRANSPORTATION, ModItems.SNOW_GLOBE.get())
                    .pattern("GGG").pattern("GCG").pattern("WSW")
                    .define('G', Items.GLASS).define('C', Items.COOKIE).define('S', Items.SNOW_BLOCK).define('W', ItemTags.PLANKS)
                    .unlockedBy(getHasName(Items.COOKIE), has(Items.COOKIE))
                    .save(output);
        }

        private void buildingBlocks() {
            // Bricks: 2x2 of the raw block, and stonecutting
            twoByTwo(ModBlocks.PEPPERMINT_BRICKS.base().get(), ModBlocks.PEPPERMINT_STONE.base().get(), 4);
            twoByTwo(ModBlocks.ICING_BRICKS.base().get(), ModBlocks.FROSTING.get(), 4);
            twoByTwo(ModBlocks.GINGERBREAD_BRICKS.base().get(), ModBlocks.GINGERBREAD.get(), 4);
            twoByTwo(ModBlocks.COOKIE_TILES.base().get(), ModItems.SUGAR_COOKIE.get(), 4);
            stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PEPPERMINT_BRICKS.base().get(),
                    ModBlocks.PEPPERMINT_STONE.base().get());

            for (BlockFamily family : ModFamilies.stone()) {
                generateRecipes(family, FeatureFlags.VANILLA_SET);
            }
            for (ModBlocks.StoneSet set : ModBlocks.stoneSets()) {
                ItemLike base = set.base().get();
                stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, set.stairs().get(), base);
                stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, set.slab().get(), base, 2);
                if (set.wall() != null) {
                    stonecutterResultFromBase(RecipeCategory.DECORATIONS, set.wall().get(), base);
                }
            }

            // Candy glass is smelted from sugar sand, like glass from sand.
            SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModBlocks.SUGAR_SAND.get()), RecipeCategory.BUILDING_BLOCKS,
                            CookingBookCategory.BLOCKS, ModBlocks.CANDY_GLASS.get(), 0.1F, 200)
                    .unlockedBy(getHasName(ModBlocks.SUGAR_SAND.get()), has(ModBlocks.SUGAR_SAND.get()))
                    .save(output);
            shaped(RecipeCategory.DECORATIONS, ModBlocks.CANDY_GLASS_PANE.get(), 16)
                    .pattern("GGG").pattern("GGG")
                    .define('G', ModBlocks.CANDY_GLASS.get())
                    .unlockedBy(getHasName(ModBlocks.CANDY_GLASS.get()), has(ModBlocks.CANDY_GLASS.get()))
                    .save(output);
            // Custom unpacking name: "marshmallow" is already the sugar-and-egg recipe.
            nineBlockStorageRecipesRecipesWithCustomUnpacking(RecipeCategory.FOOD, ModItems.MARSHMALLOW.get(),
                    RecipeCategory.BUILDING_BLOCKS, ModBlocks.MARSHMALLOW_BLOCK.get(),
                    "marshmallow_from_marshmallow_block", "marshmallow");
        }

        private void woods() {
            for (ModBlocks.WoodSet wood : ModBlocks.woodSets()) {
                planksFromLog(wood.planks().get(), ModTags.Items.logs(wood.name()), 4);
                woodFromLogs(wood.wood().get(), wood.log().get());
                woodFromLogs(wood.strippedWood().get(), wood.strippedLog().get());
            }
            for (BlockFamily family : ModFamilies.wood()) {
                generateRecipes(family, FeatureFlags.VANILLA_SET);
            }
        }

        private void materials() {
            nineBlockStorageRecipes(RecipeCategory.MISC, ModItems.SUGAR_CRYSTAL.get(),
                    RecipeCategory.BUILDING_BLOCKS, ModBlocks.SUGAR_CRYSTAL_BLOCK.get());
            oreSmelting(List.of(ModBlocks.SUGAR_CRYSTAL_ORE.get()), RecipeCategory.MISC, CookingBookCategory.MISC, ModItems.SUGAR_CRYSTAL.get(),
                    1.0F, 200, "sugar_crystal");
            oreBlasting(List.of(ModBlocks.SUGAR_CRYSTAL_ORE.get()), RecipeCategory.MISC, CookingBookCategory.MISC, ModItems.SUGAR_CRYSTAL.get(),
                    1.0F, 100, "sugar_crystal");
            oreSmelting(List.of(ModBlocks.CINNAMON_ORE.get()), RecipeCategory.MISC, CookingBookCategory.MISC, ModItems.CINNAMON_STICK.get(),
                    0.5F, 200, "cinnamon_stick");
            shaped(RecipeCategory.MISC, ModItems.MUG.get(), 2)
                    .pattern("B B").pattern(" B ")
                    .define('B', Items.BRICK)
                    .unlockedBy(getHasName(Items.BRICK), has(Items.BRICK))
                    .save(output);
        }

        private void food() {
            shapelessOf(ModItems.GINGERBREAD_COOKIE.get(), 4, Items.WHEAT, Items.WHEAT, ModItems.CINNAMON_STICK.get(), Items.SUGAR);
            shapelessOf(ModItems.SUGAR_COOKIE.get(), 4, Items.WHEAT, Items.WHEAT, Items.SUGAR, Items.EGG);
            shapelessOf(ModItems.CANDY_CANE.get(), 2, Items.SUGAR, Items.SUGAR, Items.DYE.pick(DyeColor.RED));
            shapelessOf(ModItems.MARSHMALLOW.get(), 4, Items.SUGAR, Items.SUGAR, Items.EGG);
            shapelessOf(ModItems.HOT_COCOA.get(), 1, ModItems.MUG.get(), Items.COCOA_BEANS, Items.SUGAR, Items.MILK_BUCKET);
            shapelessOf(ModItems.EGGNOG.get(), 1, Items.GLASS_BOTTLE, Items.EGG, Items.SUGAR, Items.MILK_BUCKET,
                    ModItems.CINNAMON_STICK.get());
            shapelessOf(ModItems.FRUITCAKE_SLICE.get(), 4, Items.WHEAT, Items.SUGAR, Items.EGG, Items.SWEET_BERRIES,
                    ModItems.GUMDROPS.get());
            // Roast marshmallows over a campfire (or in a smoker or furnace).
            Ingredient marshmallow = Ingredient.of(ModItems.MARSHMALLOW.get());
            ItemLike roasted = ModItems.ROASTED_MARSHMALLOW.get();
            SimpleCookingRecipeBuilder.smelting(marshmallow, RecipeCategory.FOOD, CookingBookCategory.FOOD, roasted, 0.35F, 200)
                    .unlockedBy(getHasName(ModItems.MARSHMALLOW.get()), has(ModItems.MARSHMALLOW.get())).save(output, "roasted_marshmallow_from_smelting");
            SimpleCookingRecipeBuilder.smoking(marshmallow, RecipeCategory.FOOD, roasted, 0.35F, 100)
                    .unlockedBy(getHasName(ModItems.MARSHMALLOW.get()), has(ModItems.MARSHMALLOW.get())).save(output, "roasted_marshmallow_from_smoking");
            SimpleCookingRecipeBuilder.campfireCooking(marshmallow, RecipeCategory.FOOD, roasted, 0.35F, 300)
                    .unlockedBy(getHasName(ModItems.MARSHMALLOW.get()), has(ModItems.MARSHMALLOW.get())).save(output, "roasted_marshmallow_from_campfire_cooking");
        }

        private void decor() {
            shaped(RecipeCategory.DECORATIONS, ModBlocks.FESTIVE_LIGHTS.get(), 6)
                    .pattern("SSS").pattern("RGB")
                    .define('S', Items.STRING).define('R', Items.DYE.pick(DyeColor.RED)).define('G', Items.GLOWSTONE_DUST).define('B', Items.DYE.pick(DyeColor.BLUE))
                    .unlockedBy(getHasName(Items.GLOWSTONE_DUST), has(Items.GLOWSTONE_DUST))
                    .save(output);
            shaped(RecipeCategory.DECORATIONS, ModBlocks.WREATH.get(), 2)
                    .pattern("LLL").pattern("LHL").pattern("LLL")
                    .define('L', ModBlocks.FESTIVE_FIR.leaves().get()).define('H', ModBlocks.HOLLY.get())
                    .unlockedBy(getHasName(ModBlocks.HOLLY.get()), has(ModBlocks.HOLLY.get()))
                    .save(output);
            shaped(RecipeCategory.DECORATIONS, ModBlocks.CHRISTMAS_STAR.get())
                    .pattern(" G ").pattern("GDG").pattern(" G ")
                    .define('G', Items.GOLD_INGOT).define('D', Items.GLOWSTONE_DUST)
                    .unlockedBy(getHasName(Items.GOLD_INGOT), has(Items.GOLD_INGOT))
                    .save(output);
        }

        private void twoByTwo(ItemLike result, ItemLike input, int count) {
            shaped(RecipeCategory.BUILDING_BLOCKS, result, count)
                    .pattern("##").pattern("##")
                    .define('#', input)
                    .unlockedBy(getHasName(input), has(input))
                    .save(output);
        }

        private void shapelessOf(ItemLike result, int count, ItemLike... inputs) {
            ShapelessRecipeBuilder builder = shapeless(RecipeCategory.FOOD, result, count);
            for (ItemLike input : inputs) {
                builder.requires(input);
            }
            builder.unlockedBy(getHasName(inputs[inputs.length - 1]), has(inputs[inputs.length - 1])).save(output);
        }    }
}
