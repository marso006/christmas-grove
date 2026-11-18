package com.mental.christmasgrove.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

/** The Evil Snowman's ammunition: a packed, icy snowball that does a little damage and slows. */
public class FrostSnowball extends Snowball {

    public FrostSnowball(EntityType<? extends FrostSnowball> type, Level level) {
        super(type, level);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        // Not super: vanilla snowballs deal no damage, and a zero-damage hit would eat the real one.
        if (result.getEntity() instanceof LivingEntity target && level() instanceof ServerLevel serverLevel) {
            target.hurtServer(serverLevel, damageSources().thrown(this, getOwner()), 2.0F);
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1), getOwner());
        }
    }
}
