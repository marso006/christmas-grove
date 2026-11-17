package com.mental.gingerbreadgrove.datagen;

import com.mental.gingerbreadgrove.registry.ModBlocks;
import com.mental.gingerbreadgrove.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blocks) {
        blocks.createTrivialCube(ModBlocks.FROSTING.get());
        blocks.createTrivialCube(ModBlocks.GINGERBREAD.get());
        blocks.createTrivialCube(ModBlocks.PEPPERMINT_STONE.get());

        // Frosting on top, dripping icing on the sides, plain gingerbread underneath.
        blocks.createTrivialBlock(ModBlocks.FROSTED_GINGERBREAD.get(), TexturedModel.CUBE_TOP_BOTTOM.updateTexture(t -> t
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(ModBlocks.FROSTING.get()))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(ModBlocks.FROSTED_GINGERBREAD.get(), "_side"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(ModBlocks.GINGERBREAD.get()))));
    }

    @Override
    public void generateItemModels(ItemModelGenerators items) {
        // Block items get a model that points at their block model automatically.
        items.generateFlatItem(ModItems.SNOW_GLOBE.get(), ModelTemplates.FLAT_ITEM);
    }
}
