package com.mental.christmasgrove.entity;

import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerPlayer;
import com.mental.christmasgrove.naughty.NaughtyNice;
import com.mental.christmasgrove.registry.ModEntities;
import com.mental.christmasgrove.registry.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * A red-and-white striped puppy. Behaves like a wolf (follows, sits, defends its owner) but is
 * tamed with Candy Canes and loves cookies instead of meat.
 */
public class PeppermintPup extends Wolf {

    public PeppermintPup(EntityType<? extends PeppermintPup> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ModItems.CANDY_CANE.get()) || stack.is(ModItems.GINGERBREAD_COOKIE.get())
                || stack.is(ModItems.SUGAR_COOKIE.get());
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!isTame() && !isAngry() && stack.is(ModItems.CANDY_CANE.get())) {
            if (!level().isClientSide()) {
                stack.consume(1, player);
                if (random.nextInt(3) == 0) {
                    tame(player);
                    navigation.stop();
                    setTarget(null);
                    setOrderedToSit(true);
                    level().broadcastEntityEvent(this, (byte) 7); // hearts
                    if (player instanceof ServerPlayer serverPlayer) {
                        NaughtyNice.change(serverPlayer, 2, "tamed_pup");
                    }
                } else {
                    level().broadcastEntityEvent(this, (byte) 6); // smoke
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    @Nullable
    public Wolf getBreedOffspring(ServerLevel level, AgeableMob partner) {
        PeppermintPup pup = ModEntities.PEPPERMINT_PUP.get().create(level, EntitySpawnReason.BREEDING);
        if (pup != null && isTame() && getOwnerUUID() != null) {
            pup.setOwnerUUID(getOwnerUUID());
            pup.setTame(true, true);
        }
        return pup;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (source.getEntity() instanceof ServerPlayer player) {
            NaughtyNice.change(player, -5, "pup");
        }
    }
}
