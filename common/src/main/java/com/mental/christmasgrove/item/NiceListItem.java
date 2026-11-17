package com.mental.christmasgrove.item;

import com.mental.christmasgrove.naughty.NaughtyNice;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/** A copy of Santa's list. Read it to see whether you are naughty or nice. */
public class NiceListItem extends Item {

    public NiceListItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
            int score = NaughtyNice.score(serverPlayer);
            NaughtyNice.Standing standing = NaughtyNice.standing(score);
            ChatFormatting colour = switch (standing) {
                case NAUGHTY -> ChatFormatting.RED;
                case NICE -> ChatFormatting.GREEN;
                case NEUTRAL -> ChatFormatting.YELLOW;
            };
            player.sendSystemMessage(Component.translatable(
                    "item.christmas_grove.nice_list." + standing.name().toLowerCase(), score).withStyle(colour));
        }
        player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0F, 1.0F);
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.christmas_grove.nice_list.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
