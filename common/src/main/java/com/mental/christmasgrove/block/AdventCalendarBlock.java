package com.mental.christmasgrove.block;

import com.mental.christmasgrove.block.entity.AdventCalendarBlockEntity;
import com.mental.christmasgrove.registry.ModBlocks;
import com.mental.christmasgrove.registry.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.function.Supplier;

/**
 * An Advent Calendar. From December 1st to 25th (real-world date) each player can open one little
 * door a day for a small gift, with the best ones saved for Christmas Eve and Christmas Day.
 */
public class AdventCalendarBlock extends BaseEntityBlock {

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    /** Day 1..25 (index 0 unused). */
    private static final List<Supplier<List<ItemStack>>> GIFTS = List.of(
            List::of,
            () -> List.of(new ItemStack(ModItems.GINGERBREAD_COOKIE.get(), 4)),
            () -> List.of(new ItemStack(ModItems.CANDY_CANE.get(), 3)),
            () -> List.of(new ItemStack(ModItems.HOT_COCOA.get())),
            () -> List.of(new ItemStack(ModBlocks.FESTIVE_LIGHTS.get(), 8)),
            () -> List.of(new ItemStack(ModItems.SUGAR_CRYSTAL.get(), 2)),
            () -> List.of(new ItemStack(ModItems.MARSHMALLOW.get(), 6)),
            () -> List.of(new ItemStack(ModItems.EGGNOG.get())),
            () -> List.of(new ItemStack(ModBlocks.WREATH.get(), 2)),
            () -> List.of(new ItemStack(ModItems.SUGAR_COOKIE.get(), 6)),
            () -> List.of(new ItemStack(ModItems.GUMDROPS.get(), 8)),
            () -> List.of(new ItemStack(ModItems.FRUITCAKE_SLICE.get(), 3)),
            () -> List.of(GiftBoxBlock.surprise(new ItemStack(ModBlocks.GREEN_GIFT_BOX.get()))),
            () -> List.of(new ItemStack(ModBlocks.FESTIVE_FIR.sapling().get(), 2)),
            () -> List.of(new ItemStack(ModItems.CINNAMON_STICK.get(), 6)),
            () -> List.of(new ItemStack(ModItems.ROASTED_MARSHMALLOW.get(), 4)),
            () -> List.of(new ItemStack(ModBlocks.CANDY_CANE.sapling().get(), 2)),
            () -> List.of(new ItemStack(ModItems.SUGAR_CRYSTAL.get(), 4)),
            () -> List.of(new ItemStack(Items.EMERALD, 4)),
            () -> List.of(GiftBoxBlock.surprise(new ItemStack(ModBlocks.BLUE_GIFT_BOX.get()))),
            () -> List.of(new ItemStack(ModItems.SNOW_GLOBE.get())),
            () -> List.of(new ItemStack(ModItems.GINGERBREAD_HEART.get())),
            () -> List.of(new ItemStack(ModBlocks.CHRISTMAS_STAR.get())),
            () -> List.of(new ItemStack(Items.DIAMOND, 2)),
            () -> List.of(GiftBoxBlock.surprise(new ItemStack(ModBlocks.RED_GIFT_BOX.get())),
                    GiftBoxBlock.surprise(new ItemStack(ModBlocks.GREEN_GIFT_BOX.get()))),
            () -> List.of(GiftBoxBlock.surprise(new ItemStack(ModBlocks.RED_GIFT_BOX.get())),
                    GiftBoxBlock.surprise(new ItemStack(ModBlocks.GREEN_GIFT_BOX.get())),
                    GiftBoxBlock.surprise(new ItemStack(ModBlocks.BLUE_GIFT_BOX.get())),
                    new ItemStack(Items.DIAMOND, 3), new ItemStack(ModItems.SANTA_HAT.get())));

    public AdventCalendarBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AdventCalendarBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level instanceof ServerLevel server) || !(level.getBlockEntity(pos) instanceof AdventCalendarBlockEntity calendar)) {
            return InteractionResult.SUCCESS;
        }
        LocalDate today = LocalDate.now();
        if (today.getMonth() != Month.DECEMBER) {
            player.sendOverlayMessage(Component.translatable("block.christmas_grove.advent_calendar.sealed"));
            return InteractionResult.CONSUME;
        }
        int day = today.getDayOfMonth();
        if (day > 25) {
            player.sendOverlayMessage(Component.translatable("block.christmas_grove.advent_calendar.finished"));
            return InteractionResult.CONSUME;
        }
        if (!calendar.open(player.getUUID(), today.getYear(), day)) {
            player.sendOverlayMessage(Component.translatable("block.christmas_grove.advent_calendar.tomorrow", day));
            return InteractionResult.CONSUME;
        }
        for (ItemStack gift : GIFTS.get(day).get()) {
            Block.popResourceFromFace(level, pos, state.getValue(FACING), gift);
        }
        player.sendOverlayMessage(Component.translatable("block.christmas_grove.advent_calendar.opened", day)
                .withStyle(ChatFormatting.GOLD));
        server.playSound(null, pos, SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 0.8F, 1.6F);
        server.playSound(null, pos, SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.BLOCKS, 0.8F, day == 25 ? 2.0F : 1.2F);
        Direction face = state.getValue(FACING);
        server.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5 + face.getStepX() * 0.6, pos.getY() + 0.6,
                pos.getZ() + 0.5 + face.getStepZ() * 0.6, 10, 0.3, 0.3, 0.3, 0.02);
        return InteractionResult.CONSUME;
    }
}
