package com.mental.christmasgrove.item;

import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import com.mental.christmasgrove.entity.Krampus;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(null, player.blockPosition(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 2.0F, 0.5F);
        if (player instanceof ServerPlayer serverPlayer) {
            Krampus.summonFor(serverPlayer, true);
            stack.consume(1, player);
            player.getCooldowns().addCooldown(player.getItemInHand(hand), 200);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("item.christmas_grove.krampus_bell.tooltip").withStyle(ChatFormatting.DARK_RED));
    }
}
