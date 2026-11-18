package com.mental.christmasgrove.item;

import com.mental.christmasgrove.event.SantaSleighEvents;
import com.mental.christmasgrove.naughty.NaughtyNice;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Send it off and Santa's sleigh flies over you a few seconds later, dropping presents. He reads
 * the letter first, though: naughty writers get a stern reply instead (and keep the letter).
 */
public class LetterToSantaItem extends Item {

    public LetterToSantaItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            if (NaughtyNice.standing(NaughtyNice.score(serverPlayer)) == NaughtyNice.Standing.NAUGHTY) {
                serverPlayer.sendSystemMessage(Component.translatable("item.christmas_grove.letter_to_santa.naughty")
                        .withStyle(ChatFormatting.RED));
                return InteractionResult.FAIL;
            }
            SantaSleighEvents.flyOver(serverPlayer, true);
            stack.consume(1, player);
            player.getCooldowns().addCooldown(player.getItemInHand(hand), 600);
        }
        player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0F, 1.2F);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.christmas_grove.letter_to_santa.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
