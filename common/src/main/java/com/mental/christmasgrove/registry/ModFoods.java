package com.mental.christmasgrove.registry;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

/**
 * Food values. Since 1.21.2 the hunger values ({@link FoodProperties}) and what happens when you eat
 * (effects, eating time: {@link Consumable}) are separate components.
 */
public final class ModFoods {

    public static final FoodProperties GUMDROPS = food(2, 0.1F, false);
    public static final FoodProperties CANDY_CANE = food(3, 0.2F, false);
    public static final FoodProperties GINGERBREAD_COOKIE = food(4, 0.3F, false);
    public static final FoodProperties SUGAR_COOKIE = food(3, 0.3F, false);
    public static final FoodProperties MARSHMALLOW = food(2, 0.2F, false);
    public static final FoodProperties ROASTED_MARSHMALLOW = food(5, 0.6F, false);
    public static final FoodProperties HOT_COCOA = food(4, 0.5F, true);
    public static final FoodProperties EGGNOG = food(5, 0.6F, true);
    public static final FoodProperties FRUITCAKE_SLICE = food(6, 0.8F, false);
    public static final FoodProperties GINGERBREAD_HEART = food(4, 1.0F, true);

    /** Sweets are quick to eat. */
    public static final Consumable QUICK = Consumables.defaultFood().consumeSeconds(0.8F).build();
    public static final Consumable CANDY_CANE_EFFECT = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.SPEED, 200, 0))).build();
    public static final Consumable HOT_COCOA_EFFECT = Consumables.defaultDrink()
            .onConsume(new ApplyStatusEffectsConsumeEffect(java.util.List.of(
                    new MobEffectInstance(MobEffects.SPEED, 1200, 0),
                    new MobEffectInstance(MobEffects.HASTE, 1200, 0)))).build();
    public static final Consumable EGGNOG_EFFECT = Consumables.defaultDrink()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0))).build();
    /** Rare drop from Gingerbread Men: two extra hearts for two minutes. */
    public static final Consumable GINGERBREAD_HEART_EFFECT = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(java.util.List.of(
                    new MobEffectInstance(MobEffects.HEALTH_BOOST, 2400, 0),
                    new MobEffectInstance(MobEffects.ABSORPTION, 2400, 1)))).build();

    private ModFoods() {
    }

    private static FoodProperties food(int nutrition, float saturation, boolean alwaysEdible) {
        FoodProperties.Builder builder = new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation);
        return (alwaysEdible ? builder.alwaysEdible() : builder).build();
    }
}
