package com.mental.gingerbreadgrove;

import com.mental.gingerbreadgrove.registry.ModBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class GingerbreadGroveNeoForgeClient {

    private GingerbreadGroveNeoForgeClient() {
    }

    @SubscribeEvent
    @SuppressWarnings("deprecation") // render types can also go in model JSON, but datagen is shared with Fabric
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ModBlocks.cutoutBlocks().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
            ModBlocks.translucentBlocks().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.translucent()));
        });
    }
}
