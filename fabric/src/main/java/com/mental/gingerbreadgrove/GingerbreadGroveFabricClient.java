package com.mental.gingerbreadgrove;

import com.mental.gingerbreadgrove.client.ModEntityRenderers;
import com.mental.gingerbreadgrove.registry.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class GingerbreadGroveFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModBlocks.cutoutBlocks().forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block.get(), RenderType.cutout()));
        ModBlocks.translucentBlocks().forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block.get(), RenderType.translucent()));

        ModEntityRenderers.registerLayers((layer, definition) -> EntityModelLayerRegistry.registerModelLayer(layer, definition::get));
        ModEntityRenderers.registerRenderers(new ModEntityRenderers.RendererSink() {
            @Override
            public <T extends Entity> void register(EntityType<? extends T> type, EntityRendererProvider<T> provider) {
                EntityRendererRegistry.register(type, provider);
            }
        });
    }
}
