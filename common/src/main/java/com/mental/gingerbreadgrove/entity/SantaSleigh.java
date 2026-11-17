package com.mental.gingerbreadgrove.entity;

import com.mental.gingerbreadgrove.naughty.NaughtyNice;
import com.mental.gingerbreadgrove.registry.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Santa's sleigh, pulled by four reindeer, crossing the sky in a straight line. While it passes near
 * players it drops presents on little parachutes (more for nice players), and a lump of coal for
 * anyone on the naughty list. Bells jingle and a trail of sparkles follows it. It cannot be
 * touched, and it leaves after about a minute.
 */
public class SantaSleigh extends Entity {

    public static final double SPEED = 0.5;
    private static final int LIFETIME = 1200;
    private static final int MAX_DROPS = 8;
    private static final double DROP_RANGE = 48.0;

    private int life;
    private int dropCooldown = 40;
    private int drops;

    public SantaSleigh(EntityType<? extends SantaSleigh> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
    }

    /** Points the sleigh in a horizontal direction and sets it moving. */
    public void launch(Vec3 direction) {
        Vec3 flat = new Vec3(direction.x, 0, direction.z).normalize();
        setDeltaMovement(flat.scale(SPEED));
        setYRot((float) (Mth.atan2(-flat.x, flat.z) * Mth.RAD_TO_DEG));
        yRotO = getYRot();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();
        if (getDeltaMovement().lengthSqr() < 1.0E-4) {
            launch(Vec3.directionFromRotation(0, getYRot())); // e.g. summoned by command
        }
        setPos(position().add(getDeltaMovement()));

        if (level().isClientSide()) {
            Vec3 back = getDeltaMovement().normalize().scale(-1.6);
            for (int i = 0; i < 3; i++) {
                level().addParticle(i == 0 ? ParticleTypes.END_ROD : ParticleTypes.FIREWORK,
                        getX() + back.x + (random.nextDouble() - 0.5), getY() + 0.4 + random.nextDouble() * 0.4,
                        getZ() + back.z + (random.nextDouble() - 0.5), 0, -0.02, 0);
            }
            return;
        }

        if (++life > LIFETIME) {
            discard();
            return;
        }
        if (tickCount % 10 == 0) {
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.NEUTRAL,
                    2.5F, 1.5F + random.nextFloat() * 0.5F);
        }
        if (tickCount % 200 == 0) {
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.VILLAGER_CELEBRATE, SoundSource.NEUTRAL, 3.0F, 0.55F);
        }
        if (--dropCooldown <= 0 && drops < MAX_DROPS) {
            dropForNearestPlayer((ServerLevel) level());
        }
    }

    private void dropForNearestPlayer(ServerLevel level) {
        ServerPlayer nearest = null;
        double best = DROP_RANGE * DROP_RANGE;
        for (ServerPlayer player : level.players()) {
            double d = Mth.square(player.getX() - getX()) + Mth.square(player.getZ() - getZ());
            if (d < best && !player.isSpectator()) {
                best = d;
                nearest = player;
            }
        }
        if (nearest == null) {
            dropCooldown = 10;
            return;
        }
        drops++;
        NaughtyNice.Standing standing = NaughtyNice.standing(NaughtyNice.score(nearest));
        dropCooldown = standing == NaughtyNice.Standing.NICE ? 25 + random.nextInt(15) : 45 + random.nextInt(30);
        if (standing == NaughtyNice.Standing.NAUGHTY) {
            ItemEntity coal = new ItemEntity(level, getX(), getY() - 1, getZ(), new ItemStack(Items.COAL));
            coal.setDeltaMovement(0, -0.3, 0);
            level.addFreshEntity(coal);
            return;
        }
        PresentDrop present = ModEntities.PRESENT_DROP.get().create(level);
        if (present != null) {
            present.setColour(random.nextInt(3));
            present.moveTo(getX(), getY() - 1.5, getZ(), random.nextFloat() * 360, 0);
            present.setDeltaMovement(getDeltaMovement().scale(0.3));
            level.addFreshEntity(present);
            level.playSound(null, getX(), getY(), getZ(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.NEUTRAL, 1.5F, 1.2F);
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 320 * 320; // visible from far away, like a shooting star
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        life = tag.getInt("Life");
        drops = tag.getInt("Drops");
        if (tag.contains("DirX")) {
            launch(new Vec3(tag.getDouble("DirX"), 0, tag.getDouble("DirZ")));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Life", life);
        tag.putInt("Drops", drops);
        tag.putDouble("DirX", getDeltaMovement().x);
        tag.putDouble("DirZ", getDeltaMovement().z);
    }
}
