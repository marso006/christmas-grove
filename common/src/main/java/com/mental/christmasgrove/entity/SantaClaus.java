package com.mental.christmasgrove.entity;

import net.minecraft.world.item.Items;
import com.mental.christmasgrove.registry.ModItems;
import com.mental.christmasgrove.block.GiftBoxBlock;
import net.minecraft.server.level.ServerPlayer;
import com.mental.christmasgrove.naughty.NaughtyNice;
import com.mental.christmasgrove.registry.ModBlocks;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Santa himself. Talk to him once per in-game day for a wrapped present; over the real-world
 * Christmas days (Dec 24-26) he gives two. He cannot be hurt and never despawns.
 * <p>
 * (The Naughty or Nice system in a later phase will change what he gives.)
 */
public class SantaClaus extends PathfinderMob {

    private static final String GIFTED_TAG = "GiftedToday";
    /** Player -> in-game day number of their last present. */
    private final Map<UUID, Long> lastGiftDay = new HashMap<>();

    public SantaClaus(EntityType<? extends SantaClaus> type, Level level) {
        super(type, level);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 10.0F, 1.0F));
        goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.6));
        goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!(level() instanceof ServerLevel server)) {
            return InteractionResult.SUCCESS;
        }
        long today = server.getDayTime() / 24000L;
        Long last = lastGiftDay.get(player.getUUID());
        if (last != null && last == today) {
            player.displayClientMessage(Component.translatable("entity.christmas_grove.santa_claus.tomorrow",
                    player.getDisplayName()), false);
            playSound(SoundEvents.VILLAGER_AMBIENT, 1.0F, 0.6F);
            return InteractionResult.CONSUME;
        }

        lastGiftDay.put(player.getUUID(), today);
        NaughtyNice.Standing standing = player instanceof ServerPlayer sp
                ? NaughtyNice.standing(NaughtyNice.score(sp)) : NaughtyNice.Standing.NEUTRAL;
        if (standing == NaughtyNice.Standing.NAUGHTY) {
            // Naughty children get coal.
            giveOrDrop(player, new ItemStack(Items.COAL));
            player.displayClientMessage(Component.translatable("entity.christmas_grove.santa_claus.naughty",
                    player.getDisplayName()), false);
            playSound(SoundEvents.VILLAGER_NO, 1.2F, 0.55F);
            return InteractionResult.CONSUME;
        }
        int presents = (isChristmas() ? 2 : 1) + (standing == NaughtyNice.Standing.NICE ? 1 : 0);
        for (int i = 0; i < presents; i++) {
            giveOrDrop(player, GiftBoxBlock.surprise(new ItemStack(randomGiftBox())));
        }
        if (standing == NaughtyNice.Standing.NICE) {
            giveOrDrop(player, new ItemStack(ModItems.SUGAR_CRYSTAL.get(), 1 + random.nextInt(3)));
        }
        String message = standing == NaughtyNice.Standing.NICE ? "nice" : isChristmas() ? "christmas" : "gift";
        player.displayClientMessage(Component.translatable("entity.christmas_grove.santa_claus." + message,
                player.getDisplayName()), false);
        playSound(SoundEvents.VILLAGER_CELEBRATE, 1.2F, 0.55F);
        server.sendParticles(ParticleTypes.HAPPY_VILLAGER, getX(), getY() + 1.8, getZ(), 15, 0.5, 0.5, 0.5, 0.0);
        return InteractionResult.CONSUME;
    }

    private Block randomGiftBox() {
        return switch (random.nextInt(3)) {
            case 0 -> ModBlocks.RED_GIFT_BOX.get();
            case 1 -> ModBlocks.GREEN_GIFT_BOX.get();
            default -> ModBlocks.BLUE_GIFT_BOX.get();
        };
    }

    private static void giveOrDrop(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private static boolean isChristmas() {
        LocalDate now = LocalDate.now();
        return now.getMonth() == Month.DECEMBER && now.getDayOfMonth() >= 24 && now.getDayOfMonth() <= 26;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof ServerPlayer player && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            NaughtyNice.change(player, -5, "hit_santa");
            playSound(SoundEvents.VILLAGER_HURT, 1.0F, 0.5F);
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || super.isInvulnerableTo(source);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData data) {
        setPersistenceRequired();
        return super.finalizeSpawn(level, difficulty, reason, data);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ListTag list = new ListTag();
        lastGiftDay.forEach((uuid, day) -> {
            CompoundTag entry = new CompoundTag();
            entry.put("Player", NbtUtils.createUUID(uuid));
            entry.putLong("Day", day);
            list.add(entry);
        });
        tag.put(GIFTED_TAG, list);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        lastGiftDay.clear();
        for (Tag t : tag.getList(GIFTED_TAG, Tag.TAG_COMPOUND)) {
            CompoundTag entry = (CompoundTag) t;
            lastGiftDay.put(NbtUtils.loadUUID(entry.get("Player")), entry.getLong("Day"));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    public float getVoicePitch() {
        return 0.6F; // a deep "ho ho ho"
    }
}
