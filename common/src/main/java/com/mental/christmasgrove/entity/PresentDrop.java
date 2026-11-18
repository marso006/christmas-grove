package com.mental.christmasgrove.entity;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.damagesource.DamageSource;
import com.mental.christmasgrove.block.GiftBoxBlock;
import com.mental.christmasgrove.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * A present floating down from Santa's sleigh under a candy-striped parachute. It sways gently as it
 * falls and, on landing, becomes a surprise Gift Box block (or drops as an item if there is no room).
 */
public class PresentDrop extends Entity {

    private static final EntityDataAccessor<Integer> COLOUR = SynchedEntityData.defineId(PresentDrop.class, EntityDataSerializers.INT);
    private static final int MAX_AGE = 1200;

    public PresentDrop(EntityType<? extends PresentDrop> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(COLOUR, 0);
    }

    public int colour() {
        return entityData.get(COLOUR);
    }

    public void setColour(int colour) {
        entityData.set(COLOUR, Mth.clamp(colour, 0, 2));
    }

    public BlockState boxState() {
        return (switch (colour()) {
            case 1 -> ModBlocks.GREEN_GIFT_BOX;
            case 2 -> ModBlocks.BLUE_GIFT_BOX;
            default -> ModBlocks.RED_GIFT_BOX;
        }).get().defaultBlockState();
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 motion = getDeltaMovement();
        // Drift down slowly, swaying under the canopy; the sleigh's forward push fades away.
        double sway = Math.sin(tickCount / 9.0) * 0.008;
        double heading = Math.toRadians(getYRot());
        setDeltaMovement(motion.x * 0.9 + sway * Math.cos(heading), -0.09, motion.z * 0.9 + sway * Math.sin(heading));
        move(MoverType.SELF, getDeltaMovement());

        if (level().isClientSide()) {
            if (random.nextInt(4) == 0) {
                level().addParticle(ParticleTypes.END_ROD, getX(), getY() + 0.3, getZ(), 0, 0.01, 0);
            }
            return;
        }
        if (onGround() || isInWater() || horizontalCollision || tickCount > MAX_AGE) {
            land((ServerLevel) level());
        }
    }

    private void land(ServerLevel level) {
        BlockPos pos = blockPosition();
        BlockState box = boxState();
        if (level.getBlockState(pos).canBeReplaced() && box.canSurvive(level, pos) && !level.getBlockState(pos.below()).isAir()) {
            level.setBlockAndUpdate(pos, box); // a fresh gift box block is a surprise by default
        } else {
            spawnAtLocation(level, GiftBoxBlock.surprise(new ItemStack(box.getBlock())));
        }
        level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.2F);
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, getX(), getY() + 0.5, getZ(), 12, 0.4, 0.3, 0.4, 0.02);
        discard();
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false; // presents can't be shot down
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 160 * 160;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput tag) {
        setColour(tag.getIntOr("Colour", 0));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput tag) {
        tag.putInt("Colour", colour());
    }
}
