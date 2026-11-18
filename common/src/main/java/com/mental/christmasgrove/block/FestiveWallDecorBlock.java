package com.mental.christmasgrove.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.LadderBlock;

/**
 * Flat decorations that hang on a wall: Festive Lights and Wreaths. Placement, facing, shape and
 * waterlogging come from {@link LadderBlock}; it is only climbable if added to #minecraft:climbable,
 * which these blocks are not.
 */
public class FestiveWallDecorBlock extends LadderBlock {


    public FestiveWallDecorBlock(Properties properties) {
        super(properties);
    }

}
