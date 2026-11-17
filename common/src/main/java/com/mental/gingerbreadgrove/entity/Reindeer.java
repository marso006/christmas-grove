package com.mental.gingerbreadgrove.entity;

import com.mental.gingerbreadgrove.registry.ModEntities;
import com.mental.gingerbreadgrove.registry.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * A rideable reindeer. Tamed, saddled and ridden exactly like a horse (it reuses the horse's taming,
 * inventory and riding code); feed it sugar, carrots or candy canes to tame it faster.
 */
public class Reindeer extends AbstractHorse {

    public Reindeer(EntityType<? extends Reindeer> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractHorse.createBaseHorseAttributes()
                .add(Attributes.MAX_HEALTH, 22.0)
                .add(Attributes.MOVEMENT_SPEED, 0.24);
    }

    @Override
    protected void randomizeAttributes(RandomSource random) {
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(generateMaxHealth(random::nextInt));
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(generateSpeed(random::nextDouble) * 1.05);
        getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(generateJumpStrength(random::nextDouble) * 1.1);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.HORSE_FOOD) || stack.is(ModItems.CANDY_CANE.get()) || stack.is(ModItems.GUMDROPS.get());
    }

    @Override
    public boolean canMate(Animal other) {
        return other != this && other instanceof Reindeer reindeer && canParent() && reindeer.canParent();
    }

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ModEntities.REINDEER.get().create(level);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.GOAT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.GOAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GOAT_DEATH;
    }

    @Override
    protected SoundEvent getAngrySound() {
        return SoundEvents.GOAT_SCREAMING_AMBIENT;
    }
}
