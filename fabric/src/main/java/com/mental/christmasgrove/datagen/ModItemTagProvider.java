package com.mental.christmasgrove.datagen;

import net.minecraft.tags.BlockItemTags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceKey;
import com.mental.christmasgrove.registry.ModBlocks;
import com.mental.christmasgrove.registry.ModItems;
import com.mental.christmasgrove.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;

import java.util.concurrent.CompletableFuture;

/** Item tags mostly mirror the block tags, so recipes accept our planks, logs, slabs and so on. */
public class ModItemTagProvider extends FabricTagsProvider.ItemTagsProvider {

    public ModItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries,
                              FabricTagsProvider.BlockTagsProvider blockTags) {
        super(output, registries, blockTags);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        for (ModBlocks.WoodSet wood : ModBlocks.woodSets()) {
            copy(ModTags.Blocks.logs(wood.name()), ModTags.Items.logs(wood.name()));
        }
        copy(ModTags.Blocks.SUGAR_CRYSTAL_ORES, ModTags.Items.SUGAR_CRYSTAL_ORES);
        copy(ModTags.Blocks.CINNAMON_ORES, ModTags.Items.CINNAMON_ORES);
        copy(BlockItemTags.LOGS);
        copy(BlockItemTags.PLANKS);
        copy(BlockItemTags.WOODEN_STAIRS);
        copy(BlockItemTags.WOODEN_SLABS);
        copy(BlockItemTags.WOODEN_FENCES);
        copy(BlockItemTags.FENCE_GATES);
        copy(BlockItemTags.WOODEN_DOORS);
        copy(BlockItemTags.WOODEN_TRAPDOORS);
        copy(BlockItemTags.WOODEN_BUTTONS);
        copy(BlockItemTags.WOODEN_PRESSURE_PLATES);
        copy(BlockItemTags.STAIRS);
        copy(BlockItemTags.SLABS);
        copy(BlockItemTags.WALLS);
        copy(BlockItemTags.LEAVES);
        copy(BlockItemTags.SAPLINGS);
        copy(BlockItemTags.SMALL_FLOWERS);
        copy(BlockItemTags.SAND);

        // Tools and armor: these tags decide which enchantments apply
        // Repair material for the Sugar Crystal tools (ToolMaterial takes a tag since 1.21.2)
        builder(ModTags.Items.SUGAR_CRYSTAL_TOOL_MATERIALS).add(key(ModItems.SUGAR_CRYSTAL.get()));
        builder(ItemTags.SWORDS).add(key(ModItems.CANDY_CANE_SWORD.get()));
        builder(ItemTags.PICKAXES).add(key(ModItems.SUGAR_CRYSTAL_PICKAXE.get()));
        builder(ItemTags.AXES).add(key(ModItems.SUGAR_CRYSTAL_AXE.get()));
        builder(ItemTags.SHOVELS).add(key(ModItems.SUGAR_CRYSTAL_SHOVEL.get()));
        builder(ItemTags.HOES).add(key(ModItems.SUGAR_CRYSTAL_HOE.get()));
        builder(ItemTags.HEAD_ARMOR).add(key(ModItems.SANTA_HAT.get()));
        builder(ItemTags.CHEST_ARMOR).add(key(ModItems.SANTA_COAT.get()));
        builder(ItemTags.LEG_ARMOR).add(key(ModItems.SANTA_TROUSERS.get()));
        builder(ItemTags.FOOT_ARMOR).add(key(ModItems.SANTA_BOOTS.get()));
        // Wearing any Santa Suit piece stops you freezing in powder snow.
        builder(ItemTags.FREEZE_IMMUNE_WEARABLES).add(key(ModItems.SANTA_HAT.get()), key(ModItems.SANTA_COAT.get()),
                key(ModItems.SANTA_TROUSERS.get()), key(ModItems.SANTA_BOOTS.get()));

        // Foxes eat gumdrops the way they eat sweet berries.
        builder(ItemTags.FOX_FOOD).add(key(ModItems.GUMDROPS.get()));
    }

    /** Since 26.x tag builders take registry keys. */
    private static ResourceKey<Item> key(ItemLike item) {
        return item.asItem().builtInRegistryHolder().key();
    }
}
