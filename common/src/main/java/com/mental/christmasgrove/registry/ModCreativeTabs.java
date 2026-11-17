package com.mental.christmasgrove.registry;

import com.mental.christmasgrove.Constants;
import com.mental.christmasgrove.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public final class ModCreativeTabs {

    public static final Supplier<CreativeModeTab> MAIN = Services.REGISTRY.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, "main",
            () -> Services.PLATFORM.creativeTabBuilder()
                    .title(Component.translatable("itemGroup." + Constants.MOD_ID))
                    .icon(() -> new ItemStack(ModBlocks.RED_GIFT_BOX.get()))
                    .displayItems((params, output) -> {
                        Item globe = ModItems.SNOW_GLOBE.get();
                        output.accept(globe); // the way in goes first
                        for (Supplier<? extends Item> item : ModItems.all()) {
                            if (item.get() != globe) {
                                output.accept(item.get());
                            }
                        }
                    })
                    .build());

    private ModCreativeTabs() {
    }

    /** Forces class loading so the static fields above register. */
    public static void init() {
    }
}
