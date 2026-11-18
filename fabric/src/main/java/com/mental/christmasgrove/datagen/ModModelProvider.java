package com.mental.christmasgrove.datagen;

import com.mental.christmasgrove.Constants;
import com.mental.christmasgrove.registry.ModBlocks;
import com.mental.christmasgrove.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.BlockFamily;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;

import java.util.Optional;
import java.util.function.Supplier;

public class ModModelProvider extends FabricModelProvider {

    /** A 12x12x12 present with a ribbon; see assets/.../models/block/gift_box_template.json. */
    private static final ModelTemplate GIFT_BOX = new ModelTemplate(
            Optional.of(Constants.id("block/gift_box_template")), Optional.empty(), TextureSlot.TOP, TextureSlot.SIDE);

    public ModModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blocks) {
        // Ground
        blocks.createTrivialCube(ModBlocks.FROSTING.get());
        blocks.createTrivialCube(ModBlocks.GINGERBREAD.get());
        blocks.createTrivialCube(ModBlocks.SUGAR_SAND.get());
        blocks.createTrivialCube(ModBlocks.MARSHMALLOW_BLOCK.get());
        blocks.createTrivialBlock(ModBlocks.FROSTED_GINGERBREAD.get(), TexturedModel.CUBE_BOTTOM_TOP.updateTexture(t -> t
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(ModBlocks.FROSTING.get()))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(ModBlocks.FROSTED_GINGERBREAD.get(), "_side"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(ModBlocks.GINGERBREAD.get()))));

        // Stone and building sets: base cube plus stairs, slab and wall
        for (BlockFamily family : ModFamilies.stone()) {
            blocks.family(family.getBaseBlock()).generateFor(family);
        }
        blocks.createGlassBlocks(ModBlocks.CANDY_GLASS.get(), ModBlocks.CANDY_GLASS_PANE.get());

        // Woods
        for (ModBlocks.WoodSet wood : ModBlocks.woodSets()) {
            blocks.woodProvider(wood.log().get()).logWithHorizontal(wood.log().get()).wood(wood.wood().get());
            blocks.woodProvider(wood.strippedLog().get()).logWithHorizontal(wood.strippedLog().get()).wood(wood.strippedWood().get());
            blocks.createTrivialBlock(wood.leaves().get(), TexturedModel.LEAVES);
            blocks.createCrossBlockWithDefaultItem(wood.sapling().get(), BlockModelGenerators.PlantType.NOT_TINTED);
        }
        for (BlockFamily family : ModFamilies.wood()) {
            blocks.family(family.getBaseBlock()).generateFor(family);
        }
        blocks.createTrivialBlock(ModBlocks.RED_LIT_FIR_LEAVES.get(), TexturedModel.LEAVES);
        blocks.createTrivialBlock(ModBlocks.GOLD_LIT_FIR_LEAVES.get(), TexturedModel.LEAVES);
        blocks.createTrivialBlock(ModBlocks.BLUE_LIT_FIR_LEAVES.get(), TexturedModel.LEAVES);

        // Plants
        for (Supplier<Block> plant : java.util.List.of(ModBlocks.LOLLIPOP_FLOWER, ModBlocks.POINSETTIA, ModBlocks.HOLLY,
                ModBlocks.SUGAR_GRASS, ModBlocks.CHRISTMAS_STAR)) {
            blocks.createCrossBlockWithDefaultItem(plant.get(), BlockModelGenerators.PlantType.NOT_TINTED);
        }
        blocks.createCrossBlock(ModBlocks.GUMDROP_BUSH.get(), BlockModelGenerators.PlantType.NOT_TINTED,
                SweetBerryBushBlock.AGE, 0, 1, 2, 3);

        // Ores
        blocks.createTrivialCube(ModBlocks.SUGAR_CRYSTAL_ORE.get());
        blocks.createTrivialCube(ModBlocks.CINNAMON_ORE.get());
        blocks.createTrivialCube(ModBlocks.SUGAR_CRYSTAL_BLOCK.get());

        // Decor. Wall decorations use hand-written ladder-style models; their items are flat sprites.
        for (Supplier<Block> decor : java.util.List.of(ModBlocks.FESTIVE_LIGHTS, ModBlocks.WREATH)) {
            blocks.createNonTemplateHorizontalBlock(decor.get());
            blocks.registerSimpleFlatItemModel(decor.get()); // flat sprite from the block texture
        }
        blocks.createNonTemplateModelBlock(ModBlocks.COOKIE_JAR.get());      // hand-written jar model
        blocks.createHorizontallyRotatedBlock(ModBlocks.ADVENT_CALENDAR.get(), TexturedModel.ORIENTABLE_ONLY_TOP);
        for (Supplier<Block> box : java.util.List.of(ModBlocks.RED_GIFT_BOX, ModBlocks.GREEN_GIFT_BOX, ModBlocks.BLUE_GIFT_BOX)) {
            Block block = box.get();
            Identifier model = GIFT_BOX.create(block, new TextureMapping()
                    .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_top"))
                    .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side")), blocks.modelOutput);
            blocks.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(model)));
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerators items) {
        // Block items get a model pointing at their block model automatically.
        for (Supplier<? extends Item> item : ModItems.flatItems()) {
            if (item == ModItems.GUMDROPS) {
                continue; // the bush's createCrossBlock already writes the flat model and definition for its item
            }
            items.generateFlatItem(item.get(), ModelTemplates.FLAT_ITEM);
        }
        // Spawn eggs: one sprite each since 1.21.5 (tools/textures draws them from the egg colours)
        for (Supplier<? extends Item> egg : ModItems.spawnEggs()) {
            items.generateFlatItem(egg.get(), ModelTemplates.FLAT_ITEM);
        }
        for (Supplier<? extends Item> tool : ModItems.handheldItems()) {
            items.generateFlatItem(tool.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        }
    }
}
