package com.mental.christmasgrove.item;

import com.mental.christmasgrove.entity.Krampus;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/** Ring it to summon Krampus for a boss fight. Used up when rung. */
public class KrampusBellItem extends Item {

    public KrampusBellItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(null, player.blockPosition(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 2.0F, 0.5F);
        if (player instanceof ServerPlayer serverPlayer) {
            Krampus.summonFor(serverPlayer, true);
            stack.consume(1, player);
            player.getCooldowns().addCooldown(this, 200);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.christmas_grove.krampus_bell.tooltip").withStyle(ChatFormatting.DARK_RED));
    }
}
