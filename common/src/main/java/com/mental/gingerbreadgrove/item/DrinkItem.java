package com.mental.gingerbreadgrove.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

/** A food that is drunk instead of eaten and leaves its container (a mug or bottle) behind. */
public class DrinkItem extends Item {

    private final Supplier<? extends Item> container;

    public DrinkItem(Supplier<? extends Item> container, Properties properties) {
        super(properties);
        this.container = container;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        ItemStack result = super.finishUsingItem(stack, level, entity); // applies food and effects
        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            return ItemUtils.createFilledResult(result, player, new ItemStack(container.get()), false);
        }
        return result;
    }
}
