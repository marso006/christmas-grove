package com.mental.christmasgrove.item;

import com.mental.christmasgrove.block.entity.GiftBoxBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

/** A gift box item whose tooltip says what kind of present it is (blocks lost their tooltip hook in 1.21.5). */
public class GiftBoxItem extends BlockItem {

    public GiftBoxItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        CompoundTag data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        String key;
        if (data.getBooleanOr(GiftBoxBlockEntity.SURPRISE_TAG, false)) {
            key = "surprise";
        } else if (contents.nonEmptyStream().findAny().isPresent()) {
            tooltip.accept(Component.translatable("block.christmas_grove.gift_box.tooltip.wrapped",
                    data.getStringOr("ChristmasWrapperName", "")).withStyle(ChatFormatting.GOLD));
            return;
        } else {
            key = "empty";
        }
        tooltip.accept(Component.translatable("block.christmas_grove.gift_box.tooltip." + key).withStyle(ChatFormatting.GRAY));
    }
}
