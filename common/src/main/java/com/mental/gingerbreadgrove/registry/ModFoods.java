package com.mental.gingerbreadgrove.registry;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

/** Food values. Sweets are quick to eat but not very filling; the drinks give short buffs. */
public final class ModFoods {

    public static final FoodProperties GUMDROPS = new FoodProperties.Builder()
            .nutrition(2).saturationModifier(0.1F).fast().build();
    public static final FoodProperties CANDY_CANE = new FoodProperties.Builder()
            .nutrition(3).saturationModifier(0.2F)
            .effect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 0), 1.0F).build();
    public static final FoodProperties GINGERBREAD_COOKIE = new FoodProperties.Builder()
            .nutrition(4).saturationModifier(0.3F).build();
    public static final FoodProperties SUGAR_COOKIE = new FoodProperties.Builder()
            .nutrition(3).saturationModifier(0.3F).fast().build();
    public static final FoodProperties MARSHMALLOW = new FoodProperties.Builder()
            .nutrition(2).saturationModifier(0.2F).fast().build();
    public static final FoodProperties ROASTED_MARSHMALLOW = new FoodProperties.Builder()
            .nutrition(5).saturationModifier(0.6F).build();
    public static final FoodProperties HOT_COCOA = new FoodProperties.Builder()
            .nutrition(4).saturationModifier(0.5F).alwaysEdible()
            .effect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 0), 1.0F).build();
    public static final FoodProperties EGGNOG = new FoodProperties.Builder()
            .nutrition(5).saturationModifier(0.6F).alwaysEdible()
            .effect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0), 1.0F).build();
    public static final FoodProperties FRUITCAKE_SLICE = new FoodProperties.Builder()
            .nutrition(6).saturationModifier(0.8F).build();
    /** Rare drop from Gingerbread Men: two extra hearts for two minutes. */
    public static final FoodProperties GINGERBREAD_HEART = new FoodProperties.Builder()
            .nutrition(4).saturationModifier(1.0F).alwaysEdible()
            .effect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 2400, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 1), 1.0F).build();

    private ModFoods() {
    }
}
