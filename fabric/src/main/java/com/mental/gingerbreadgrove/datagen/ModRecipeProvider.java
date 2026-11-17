package com.mental.gingerbreadgrove.datagen;

import com.mental.gingerbreadgrove.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        // A glass dome with a cookie inside, on a wooden and snowy base.
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ModItems.SNOW_GLOBE.get())
                .pattern("GGG")
                .pattern("GCG")
                .pattern("WSW")
                .define('G', Items.GLASS)
                .define('C', Items.COOKIE)
                .define('S', Items.SNOW_BLOCK)
                .define('W', ItemTags.PLANKS)
                .unlockedBy(getHasName(Items.COOKIE), has(Items.COOKIE))
                .save(output);
    }
}
