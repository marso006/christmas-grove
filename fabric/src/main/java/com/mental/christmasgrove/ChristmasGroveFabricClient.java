package com.mental.christmasgrove;

import com.mental.christmasgrove.client.ModEntityRenderers;
import com.mental.christmasgrove.client.SantaHatModel;
import com.mental.christmasgrove.registry.ModItems;
import com.mental.christmasgrove.registry.ModBlocks;
import com.mental.christmasgrove.showcase.ShowcaseDirector;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

public class ChristmasGroveFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ShowcaseDirector.installIfEnabled(); // dev-only screenshot automation, off unless -Dchristmasgrove.showcase=true
        ModBlocks.cutoutBlocks().forEach(block -> BlockRenderLayerMap.putBlock(block.get(), ChunkSectionLayer.CUTOUT));
        ModBlocks.translucentBlocks().forEach(block -> BlockRenderLayerMap.putBlock(block.get(), ChunkSectionLayer.TRANSLUCENT));

        // The Santa Hat is a real 3D hat, not a reshaped helmet.
        ArmorRenderer.register((poseStack, buffers, stack, state, slot, light, wearerModel) -> {
            HumanoidModel<HumanoidRenderState> hat = SantaHatModel.armorModel();
            wearerModel.copyPropertiesTo(hat);
            hat.setAllVisible(false);
            hat.head.visible = true;
            ArmorRenderer.renderPart(poseStack, buffers, light, stack, hat, SantaHatModel.TEXTURE);
        }, ModItems.SANTA_HAT.get());

        ModEntityRenderers.registerLayers((layer, definition) -> EntityModelLayerRegistry.registerModelLayer(layer, definition::get));
        ModEntityRenderers.registerRenderers(new ModEntityRenderers.RendererSink() {
            @Override
            public <T extends Entity> void register(EntityType<? extends T> type, EntityRendererProvider<T> provider) {
                EntityRendererRegistry.register(type, provider);
            }
        });
    }
}
