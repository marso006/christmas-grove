package com.mental.christmasgrove.registry;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;

/** Tool materials. Sugar Crystal mines what iron mines, but faster, longer and with better enchants. */
public final class ModTiers {

    public static final ToolMaterial SUGAR_CRYSTAL = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 1000, 7.0F, 2.5F, 18,
            ModTags.Items.SUGAR_CRYSTAL_TOOL_MATERIALS);

    private ModTiers() {
    }
}
