package com.mental.christmasgrove.entity;

import net.minecraft.server.level.ServerPlayer;
import com.mental.christmasgrove.naughty.NaughtyNice;
import com.mental.christmasgrove.registry.ModItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * "Run, run, as fast as you can!" A skittish living cookie that sprints away from players, leaving
 * crumbs behind. Catch one with an empty hand to collect it as cookies (and, rarely, a Gingerbread
 * Heart); killing one works too, but that is rather naughty.
 */
public class GingerbreadMan extends PathfinderMob {

    public GingerbreadMan(EntityType<? extends GingerbreadMan> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.8));
        goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 10.0F, 1.3, 1.7));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!player.getItemInHand(hand).isEmpty()) {
            return super.mobInteract(player, hand);
        }
        if (level() instanceof ServerLevel server) {
            // Caught! It crumbles into cookies.
            int cookies = 2 + random.nextInt(2);
            spawnAtLocation(new ItemStack(ModItems.GINGERBREAD_COOKIE.get(), cookies));
            if (random.nextInt(20) == 0) {
                spawnAtLocation(new ItemStack(ModItems.GINGERBREAD_HEART.get()));
            }
            server.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ModItems.GINGERBREAD_COOKIE.get())),
                    getX(), getY() + 0.5, getZ(), 20, 0.2, 0.3, 0.2, 0.08);
            playSound(SoundEvents.PLAYER_BURP, 1.0F, 1.4F);
            player.displayClientMessage(Component.translatable("entity.christmas_grove.gingerbread_man.caught"), true);
            discard();
        }
        return InteractionResult.sidedSuccess(level().isClientSide());
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // Crumbs fly off while it runs.
        if (level().isClientSide() && getDeltaMovement().horizontalDistanceSqr() > 0.003 && random.nextInt(3) == 0) {
            level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ModItems.GINGERBREAD_COOKIE.get())),
                    getX(), getY() + 0.2, getZ(), 0.0, 0.05, 0.0);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ROOTED_DIRT_BREAK;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.MUD_BRICKS_BREAK;
    }

    @Override
    public float getVoicePitch() {
        return 1.6F;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (source.getEntity() instanceof ServerPlayer player) {
            NaughtyNice.change(player, -3, "gingerbread_man");
        }
    }
}
