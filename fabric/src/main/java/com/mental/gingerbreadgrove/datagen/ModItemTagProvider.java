package com.mental.gingerbreadgrove.datagen;

import com.mental.gingerbreadgrove.registry.ModBlocks;
import com.mental.gingerbreadgrove.registry.ModItems;
import com.mental.gingerbreadgrove.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;

import java.util.concurrent.CompletableFuture;

/** Item tags mostly mirror the block tags, so recipes accept our planks, logs, slabs and so on. */
public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries,
                              BlockTagProvider blockTags) {
        super(output, registries, blockTags);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        for (ModBlocks.WoodSet wood : ModBlocks.woodSets()) {
            copy(ModTags.Blocks.logs(wood.name()), ModTags.Items.logs(wood.name()));
        }
        copy(ModTags.Blocks.SUGAR_CRYSTAL_ORES, ModTags.Items.SUGAR_CRYSTAL_ORES);
        copy(ModTags.Blocks.CINNAMON_ORES, ModTags.Items.CINNAMON_ORES);
        copy(BlockTags.LOGS, ItemTags.LOGS);
        copy(BlockTags.PLANKS, ItemTags.PLANKS);
        copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);
        copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
        copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
        copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
        copy(BlockTags.STAIRS, ItemTags.STAIRS);
        copy(BlockTags.SLABS, ItemTags.SLABS);
        copy(BlockTags.WALLS, ItemTags.WALLS);
        copy(BlockTags.LEAVES, ItemTags.LEAVES);
        copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
        copy(BlockTags.SMALL_FLOWERS, ItemTags.SMALL_FLOWERS);
        copy(BlockTags.SAND, ItemTags.SAND);

        // Tools and armor: these tags decide which enchantments apply
        getOrCreateTagBuilder(ItemTags.SWORDS).add(ModItems.CANDY_CANE_SWORD.get());
        getOrCreateTagBuilder(ItemTags.PICKAXES).add(ModItems.SUGAR_CRYSTAL_PICKAXE.get());
        getOrCreateTagBuilder(ItemTags.AXES).add(ModItems.SUGAR_CRYSTAL_AXE.get());
        getOrCreateTagBuilder(ItemTags.SHOVELS).add(ModItems.SUGAR_CRYSTAL_SHOVEL.get());
        getOrCreateTagBuilder(ItemTags.HOES).add(ModItems.SUGAR_CRYSTAL_HOE.get());
        getOrCreateTagBuilder(ItemTags.HEAD_ARMOR).add(ModItems.SANTA_HAT.get());
        getOrCreateTagBuilder(ItemTags.CHEST_ARMOR).add(ModItems.SANTA_COAT.get());
        getOrCreateTagBuilder(ItemTags.LEG_ARMOR).add(ModItems.SANTA_TROUSERS.get());
        getOrCreateTagBuilder(ItemTags.FOOT_ARMOR).add(ModItems.SANTA_BOOTS.get());
        // Wearing any Santa Suit piece stops you freezing in powder snow.
        getOrCreateTagBuilder(ItemTags.FREEZE_IMMUNE_WEARABLES).add(ModItems.SANTA_HAT.get(), ModItems.SANTA_COAT.get(),
                ModItems.SANTA_TROUSERS.get(), ModItems.SANTA_BOOTS.get());

        // Foxes eat gumdrops the way they eat sweet berries.
        getOrCreateTagBuilder(ItemTags.FOX_FOOD).add(ModItems.GUMDROPS.get());
    }
}
