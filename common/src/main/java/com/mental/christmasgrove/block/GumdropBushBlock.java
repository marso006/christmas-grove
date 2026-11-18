package com.mental.christmasgrove.block;

import com.mental.christmasgrove.registry.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Grows through four stages like a sweet berry bush and is harvested with right-click, but it gives
 * gumdrops, and it neither slows nor hurts anything walking through it.
 */
public class GumdropBushBlock extends SweetBerryBushBlock {


    public GumdropBushBlock(Properties properties) {
        super(properties);
    }


    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(ModItems.GUMDROPS.get());
    }

    // Soft candy: no thorns, no slowdown. Both signatures are declared (without @Override) so this one
    // source overrides the berry bush on 1.21.9, which lacks the "intersects" flag, and on 1.21.10.
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effects, boolean intersects) {
    }

    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effects) {
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        int age = state.getValue(AGE);
        if (age <= 1) {
            return super.useWithoutItem(state, level, pos, player, hit);
        }
        int count = 1 + level.getRandom().nextInt(2) + (age == MAX_AGE ? 1 : 0);
        popResource(level, pos, new ItemStack(ModItems.GUMDROPS.get(), count));
        level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS,
                1.0F, 1.1F + level.getRandom().nextFloat() * 0.4F);
        BlockState picked = state.setValue(AGE, 1);
        level.setBlock(pos, picked, 2);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, picked));
        return InteractionResult.SUCCESS;
    }
}
