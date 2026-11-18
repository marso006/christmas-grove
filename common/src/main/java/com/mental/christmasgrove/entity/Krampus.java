package com.mental.christmasgrove.entity;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import com.mental.christmasgrove.naughty.NaughtyNice;
import com.mental.christmasgrove.registry.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

/**
 * Krampus, Santa's dark companion from Alpine folklore, who comes for naughty children. A boss with
 * a boss bar: he hits hard, yanks players toward him with his chains, and calls Evil Snowmen when
 * wounded. Defeating him wipes your naughty record clean.
 * <p>
 * He comes on his own when a player's Naughty-or-Nice score sinks low enough, or when someone rings
 * a Krampus Bell.
 */
public class Krampus extends Monster {

    private static final int CHAIN_COOLDOWN = 140;

    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(getDisplayName(),
            BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10).setDarkenScreen(true);
    private int chainTimer = CHAIN_COOLDOWN;
    private boolean calledMinions;

    public Krampus(EntityType<? extends Krampus> type, Level level) {
        super(type, level);
        xpReward = 100;
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 300.0)
                .add(Attributes.ATTACK_DAMAGE, 11.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.STEP_HEIGHT, 1.5);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.25, true));
        goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 16.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    /** Brings Krampus to a player: a dozen blocks away, on the surface, already hunting them. */
    public static void summonFor(ServerPlayer player, boolean rungBell) {
        ServerLevel level = player.level();
        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            player.displayClientMessage(Component.translatable("entity.christmas_grove.krampus.peaceful"), true);
            return;
        }
        Krampus krampus = ModEntities.KRAMPUS.get().create(level, EntitySpawnReason.MOB_SUMMONED);
        if (krampus == null) {
            return;
        }
        double angle = level.random.nextDouble() * Math.PI * 2;
        int x = (int) (player.getX() + Math.cos(angle) * 12);
        int z = (int) (player.getZ() + Math.sin(angle) * 12);
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        krampus.snapTo(x + 0.5, y, z + 0.5, player.getYRot() + 180, 0);
        krampus.finalizeSpawn(level, level.getCurrentDifficultyAt(new BlockPos(x, y, z)), EntitySpawnReason.EVENT, null);
        krampus.setTarget(player);
        level.addFreshEntity(krampus);
        level.sendParticles(ParticleTypes.LARGE_SMOKE, x + 0.5, y + 1, z + 0.5, 60, 0.6, 1.2, 0.6, 0.02);
        level.playSound(null, x, y, z, SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1.5F, 0.6F);
        level.playSound(null, x, y, z, SoundEvents.CHAIN_BREAK, SoundSource.HOSTILE, 2.0F, 0.5F);
        player.sendSystemMessage(Component.translatable(rungBell
                ? "entity.christmas_grove.krampus.bell" : "entity.christmas_grove.krampus.arrives")
                .withStyle(ChatFormatting.DARK_RED));
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);
        bossEvent.setProgress(getHealth() / getMaxHealth());

        LivingEntity target = getTarget();
        if (target != null && --chainTimer <= 0) {
            chainTimer = CHAIN_COOLDOWN;
            if (distanceTo(target) > 4 && distanceTo(target) < 20 && hasLineOfSight(target)) {
                chainPull(target);
            }
        }
        if (!calledMinions && getHealth() < getMaxHealth() / 2) {
            calledMinions = true;
            callMinions();
        }
    }

    /** Lashes out with his chains and drags the target toward him. */
    private void chainPull(LivingEntity target) {
        Vec3 pull = position().subtract(target.position()).normalize().scale(1.4).add(0, 0.45, 0);
        target.setDeltaMovement(pull);
        target.hurtMarked = true; // makes the velocity reach a player's client
        target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1), this);
        playSound(SoundEvents.CHAIN_PLACE, 2.0F, 0.5F);
        if (level() instanceof ServerLevel server) {
            Vec3 from = position().add(0, 1.5, 0);
            Vec3 to = target.position().add(0, 1.0, 0);
            for (int i = 0; i <= 10; i++) {
                Vec3 p = from.lerp(to, i / 10.0);
                server.sendParticles(ParticleTypes.SMOKE, p.x, p.y, p.z, 1, 0, 0, 0, 0);
            }
        }
    }

    private void callMinions() {
        if (!(level() instanceof ServerLevel server)) {
            return;
        }
        playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 2.0F, 0.6F);
        for (int i = 0; i < 3; i++) {
            EvilSnowman snowman = ModEntities.EVIL_SNOWMAN.get().create(server, EntitySpawnReason.MOB_SUMMONED);
            if (snowman != null) {
                snowman.snapTo(getX() + random.nextInt(7) - 3, getY(), getZ() + random.nextInt(7) - 3, 0, 0);
                snowman.finalizeSpawn(server, server.getCurrentDifficultyAt(blockPosition()), EntitySpawnReason.MOB_SUMMONED, null);
                snowman.setTarget(getTarget());
                server.addFreshEntity(snowman);
                server.sendParticles(ParticleTypes.SNOWFLAKE, snowman.getX(), snowman.getY() + 1, snowman.getZ(), 20, 0.3, 0.6, 0.3, 0.02);
            }
        }
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (source.getEntity() instanceof ServerPlayer player) {
            NaughtyNice.reset(player);
            player.sendSystemMessage(Component.translatable("entity.christmas_grove.krampus.defeated")
                    .withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput tag) {
        super.readAdditionalSaveData(tag);
        calledMinions = tag.getBooleanOr("CalledMinions", false);
        if (hasCustomName()) {
            bossEvent.setName(getDisplayName());
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("CalledMinions", calledMinions);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RAVAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.RAVAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RAVAGER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        playSound(SoundEvents.CHAIN_STEP, 0.8F, 0.6F);
    }
}
