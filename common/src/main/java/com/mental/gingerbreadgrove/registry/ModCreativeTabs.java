package com.mental.gingerbreadgrove.registry;

import com.mental.gingerbreadgrove.Constants;
import com.mental.gingerbreadgrove.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public final class ModCreativeTabs {

    public static final Supplier<CreativeModeTab> MAIN = Services.REGISTRY.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, "main",
            () -> Services.PLATFORM.creativeTabBuilder()
                    .title(Component.translatable("itemGroup." + Constants.MOD_ID))
                    .icon(() -> new ItemStack(ModBlocks.FROSTED_GINGERBREAD.get()))
                    .displayItems((params, output) -> ModItems.all().forEach(item -> output.accept(item.get())))
                    .build());

    private ModCreativeTabs() {
    }

    /** Forces class loading so the static fields above register. */
    public static void init() {
    }
}
