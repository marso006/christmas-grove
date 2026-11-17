package com.mental.gingerbreadgrove.entity;

import net.minecraft.server.level.ServerPlayer;
import com.mental.gingerbreadgrove.naughty.NaughtyNice;
import com.mental.gingerbreadgrove.block.GiftBoxBlock;
import com.mental.gingerbreadgrove.registry.ModBlocks;
import com.mental.gingerbreadgrove.registry.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.InteractGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * One of Santa's helpers. Trades festive decorations, presents and saplings for Sugar Crystals, and
 * buys sweets. Never despawns, and keeps well away from monsters.
 */
public class Elf extends AbstractVillager {

    public Elf(EntityType<? extends Elf> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.5);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Monster.class, 8.0F, 0.6, 0.7));
        goalSelector.addGoal(1, new PanicGoal(this, 0.7));
        goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.4));
        goalSelector.addGoal(8, new InteractGoal(this, Player.class, 3.0F, 1.0F));
        goalSelector.addGoal(9, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    @Override
    protected void updateTrades() {
        MerchantOffers offers = getOffers();
        // Selling decorations and presents
        offers.add(sell(ModItems.SUGAR_CRYSTAL.get(), 1, ModBlocks.FESTIVE_LIGHTS.get(), 8));
        offers.add(sell(ModItems.SUGAR_CRYSTAL.get(), 2, ModBlocks.WREATH.get(), 2));
        offers.add(sell(ModItems.SUGAR_CRYSTAL.get(), 3, ModBlocks.CHRISTMAS_STAR.get(), 1));
        offers.add(new MerchantOffer(new ItemCost(ModItems.SUGAR_CRYSTAL.get(), 4),
                GiftBoxBlock.surprise(new ItemStack(ModBlocks.RED_GIFT_BOX.get())), 12, 3, 0.05F));
        offers.add(sell(ModItems.SUGAR_CRYSTAL.get(), 6, ModItems.SLEIGH_BELLS.get(), 1));
        offers.add(sell(Items.PAPER, 4, ModItems.NICE_LIST.get(), 1));
        offers.add(sell(ModItems.SUGAR_CRYSTAL.get(), 3, ModItems.LETTER_TO_SANTA.get(), 1));
        offers.add(sell(ModItems.SUGAR_CRYSTAL.get(), 2, ModBlocks.FESTIVE_FIR.sapling().get(), 2));
        offers.add(sell(ModItems.SUGAR_CRYSTAL.get(), 2, ModBlocks.CANDY_CANE.sapling().get(), 2));
        offers.add(sell(Items.EMERALD, 1, ModItems.HOT_COCOA.get(), 1));
        offers.add(sell(Items.EMERALD, 2, ModItems.SNOW_GLOBE.get(), 1));
        // Buying sweets
        offers.add(sell(ModItems.GUMDROPS.get(), 12, ModItems.SUGAR_CRYSTAL.get(), 1));
        offers.add(sell(ModItems.GINGERBREAD_COOKIE.get(), 10, Items.EMERALD, 1));
    }

    private static MerchantOffer sell(ItemLike cost, int costCount, ItemLike result, int resultCount) {
        return new MerchantOffer(new ItemCost(cost, costCount), new ItemStack(result, resultCount), 12, 3, 0.05F);
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp()) {
            level().addFreshEntity(new ExperienceOrb(level(), getX(), getY() + 0.5, getZ(), 3 + random.nextInt(4)));
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (isAlive() && !isTrading() && !isBaby()) {
            if (hand == InteractionHand.MAIN_HAND) {
                player.awardStat(Stats.TALKED_TO_VILLAGER);
            }
            if (!level().isClientSide() && !getOffers().isEmpty()) {
                setTradingPlayer(player);
                openTradingScreen(player, getDisplayName(), 1);
            }
            return InteractionResult.sidedSuccess(level().isClientSide());
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(net.minecraft.server.level.ServerLevel level, AgeableMob partner) {
        return null;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isTrading() ? SoundEvents.WANDERING_TRADER_TRADE : SoundEvents.WANDERING_TRADER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WANDERING_TRADER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WANDERING_TRADER_DEATH;
    }

    @Override
    protected SoundEvent getTradeUpdatedSound(boolean accepted) {
        return accepted ? SoundEvents.WANDERING_TRADER_YES : SoundEvents.WANDERING_TRADER_NO;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.WANDERING_TRADER_YES;
    }

    @Override
    public float getVoicePitch() {
        return 1.5F; // a small, cheerful voice
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (source.getEntity() instanceof ServerPlayer player) {
            NaughtyNice.change(player, -6, "elf");
        }
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        super.notifyTrade(offer);
        if (getTradingPlayer() instanceof ServerPlayer player) {
            NaughtyNice.change(player, 1, "trade");
        }
    }
}
