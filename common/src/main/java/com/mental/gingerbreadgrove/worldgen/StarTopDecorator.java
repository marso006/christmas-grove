package com.mental.gingerbreadgrove.worldgen;

import com.mental.gingerbreadgrove.registry.ModBlocks;
import com.mental.gingerbreadgrove.registry.ModWorldgenTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

/** Puts a Christmas Star on the very top of a tree, above its highest leaf. */
public class StarTopDecorator extends TreeDecorator {

    public static final StarTopDecorator INSTANCE = new StarTopDecorator();
    public static final MapCodec<StarTopDecorator> CODEC = MapCodec.unit(() -> INSTANCE);

    @Override
    protected TreeDecoratorType<?> type() {
        return ModWorldgenTypes.STAR_TOP.get();
    }

    @Override
    public void place(Context context) {
        if (context.logs().isEmpty() || context.leaves().isEmpty()) {
            return;
        }
        BlockPos trunkTop = context.logs().stream().max((a, b) -> Integer.compare(a.getY(), b.getY())).orElseThrow();
        // The highest leaf directly above the trunk, falling back to the trunk top itself.
        BlockPos top = context.leaves().stream()
                .filter(p -> p.getX() == trunkTop.getX() && p.getZ() == trunkTop.getZ())
                .max((a, b) -> Integer.compare(a.getY(), b.getY()))
                .orElse(trunkTop);
        BlockPos star = top.above();
        if (context.isAir(star)) {
            context.setBlock(star, ModBlocks.CHRISTMAS_STAR.get().defaultBlockState());
        }
    }
}
