package com.mental.christmasgrove.block;

import com.mental.christmasgrove.block.entity.GiftBoxBlockEntity;
import com.mental.christmasgrove.naughty.NaughtyNice;
import com.mental.christmasgrove.registry.ModLootTables;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A wrapped present.
 * <ul>
 *   <li><b>Use</b> to unwrap it. A surprise box (found in the world, or from Santa) rolls random
 *   loot; a box someone wrapped pops out what they put inside.</li>
 *   <li><b>Sneak + use</b> an empty or self-wrapped box to fill it (wrap a present).</li>
 *   <li>Breaking it drops the box with its contents still inside, so it can be carried and given.</li>
 * </ul>
 * Unwrapping a present that another player wrapped makes the giver a little nicer.
 */
public class GiftBoxBlock extends BaseEntityBlock {

    public static final MapCodec<GiftBoxBlock> CODEC = simpleCodec(GiftBoxBlock::new);
    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

    public GiftBoxBlock(Properties properties) {
        super(properties);
    }

    /** Marks a gift box item as a surprise present (random loot when unwrapped). */
    public static ItemStack surprise(ItemStack stack) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(GiftBoxBlockEntity.SURPRISE_TAG, true);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GiftBoxBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level instanceof ServerLevel server) || !(level.getBlockEntity(pos) instanceof GiftBoxBlockEntity box)) {
            return InteractionResult.SUCCESS;
        }
        if (player.isShiftKeyDown()) {
            wrap(box, player);
        } else {
            unwrap(server, pos, box, player);
        }
        return InteractionResult.CONSUME;
    }

    private static void wrap(GiftBoxBlockEntity box, Player player) {
        if (box.isSurprise()) {
            player.displayClientMessage(Component.translatable("block.christmas_grove.gift_box.sealed"), true);
            return;
        }
        if (box.wrapper() != null && !box.wrapper().equals(player.getUUID()) && !box.isEmpty()) {
            player.displayClientMessage(Component.translatable("block.christmas_grove.gift_box.not_yours",
                    box.wrapperName()), true);
            return;
        }
        box.setWrapper(player.getUUID(), player.getGameProfile().getName());
        player.openMenu(box);
    }

    private static void unwrap(ServerLevel level, BlockPos pos, GiftBoxBlockEntity box, Player player) {
        if (box.isSurprise()) {
            LootTable table = level.getServer().reloadableRegistries().getLootTable(ModLootTables.GIFT_BOX);
            LootParams params = new LootParams.Builder(level)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .withLuck(player.getLuck())
                    .create(LootContextParamSets.CHEST);
            level.removeBlock(pos, false);
            table.getRandomItems(params).forEach(stack -> Block.popResource(level, pos, stack));
        } else if (!box.isEmpty()) {
            List<ItemStack> contents = box.contents();
            box.clearContent();
            level.removeBlock(pos, false);
            contents.forEach(stack -> Block.popResource(level, pos, stack));
            if (box.wrapper() != null && !box.wrapper().equals(player.getUUID())) {
                player.displayClientMessage(Component.translatable("block.christmas_grove.gift_box.from",
                        box.wrapperName()).withStyle(ChatFormatting.GOLD), false);
                if (level.getServer().getPlayerList().getPlayer(box.wrapper()) instanceof ServerPlayer giver) {
                    NaughtyNice.change(giver, 3, "gift_given");
                }
            }
        } else {
            player.displayClientMessage(Component.translatable("block.christmas_grove.gift_box.empty"), true);
            return;
        }
        level.playSound(null, pos, SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.4F, 1.6F);
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                16, 0.4, 0.4, 0.4, 0.05);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        String key;
        if (data.getBoolean(GiftBoxBlockEntity.SURPRISE_TAG)) {
            key = "surprise";
        } else if (contents.nonEmptyStream().findAny().isPresent()) {
            tooltip.add(Component.translatable("block.christmas_grove.gift_box.tooltip.wrapped",
                    data.getString("ChristmasWrapperName")).withStyle(ChatFormatting.GOLD));
            return;
        } else {
            key = "empty";
        }
        tooltip.add(Component.translatable("block.christmas_grove.gift_box.tooltip." + key).withStyle(ChatFormatting.GRAY));
    }
}
