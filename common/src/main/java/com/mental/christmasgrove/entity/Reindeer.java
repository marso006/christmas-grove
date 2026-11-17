package com.mental.christmasgrove.entity;

import net.minecraft.server.level.ServerPlayer;
import com.mental.christmasgrove.naughty.NaughtyNice;
import com.mental.christmasgrove.registry.ModEntities;
import com.mental.christmasgrove.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
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

    private static final EntityDataAccessor<Boolean> SLEIGH_BELLS =
            SynchedEntityData.defineId(Reindeer.class, EntityDataSerializers.BOOLEAN);

    public Reindeer(EntityType<? extends Reindeer> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SLEIGH_BELLS, false);
    }

    public boolean hasSleighBells() {
        return entityData.get(SLEIGH_BELLS);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(ModItems.SLEIGH_BELLS.get()) && isTamed() && !hasSleighBells()) {
            if (!level().isClientSide()) {
                entityData.set(SLEIGH_BELLS, true);
                stack.consume(1, player);
                playSound(SoundEvents.NOTE_BLOCK_BELL.value(), 1.0F, 1.8F);
            }
            return InteractionResult.sidedSuccess(level().isClientSide());
        }
        return super.mobInteract(player, hand);
    }

    /** With sleigh bells on, a ridden reindeer drifts down gently instead of falling. */
    @Override
    public void tick() {
        super.tick();
        if (!hasSleighBells()) {
            return;
        }
        if (isVehicle() && !onGround()) {
            Vec3 motion = getDeltaMovement();
            if (motion.y < -0.08) {
                setDeltaMovement(motion.x * 1.01, -0.08, motion.z * 1.01);
            }
            fallDistance = 0.0F;
        }
        if (!level().isClientSide() && tickCount % 12 == 0 && getDeltaMovement().horizontalDistanceSqr() > 0.004) {
            playSound(SoundEvents.NOTE_BLOCK_BELL.value(), 0.35F, 1.6F + random.nextFloat() * 0.4F); // jingle
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return !hasSleighBells() && super.causeFallDamage(distance, multiplier, source);
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (hasSleighBells()) {
            spawnAtLocation(new ItemStack(ModItems.SLEIGH_BELLS.get()));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("SleighBells", hasSleighBells());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(SLEIGH_BELLS, tag.getBoolean("SleighBells"));
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

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (source.getEntity() instanceof ServerPlayer player) {
            NaughtyNice.change(player, -4, "reindeer");
        }
    }
}
