package com.mental.christmasgrove.registry;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/** Tool tiers. Sugar Crystal mines what iron mines, but faster, longer and with better enchants. */
public enum ModTiers implements Tier {
    SUGAR_CRYSTAL(BlockTags.INCORRECT_FOR_IRON_TOOL, 1000, 7.0F, 2.5F, 18,
            () -> Ingredient.of(ModItems.SUGAR_CRYSTAL.get()));

    private final TagKey<Block> incorrectBlocks;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantability;
    private final Supplier<Ingredient> repair;

    ModTiers(TagKey<Block> incorrectBlocks, int uses, float speed, float damage, int enchantability,
             Supplier<Ingredient> repair) {
        this.incorrectBlocks = incorrectBlocks;
        this.uses = uses;
        this.speed = speed;
        this.damage = damage;
        this.enchantability = enchantability;
        this.repair = repair;
    }

    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return damage;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return incorrectBlocks;
    }

    @Override
    public int getEnchantmentValue() {
        return enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repair.get();
    }
}
