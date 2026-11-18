package com.mental.christmasgrove;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import com.mental.christmasgrove.client.ModEntityRenderers;
import com.mental.christmasgrove.client.SantaHatModel;
import com.mental.christmasgrove.registry.ModItems;
import com.mental.christmasgrove.registry.ModBlocks;
import com.mental.christmasgrove.showcase.ShowcaseDirector;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

public class ChristmasGroveFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ShowcaseDirector.installIfEnabled(); // dev-only screenshot automation, off unless -Dchristmasgrove.showcase=true
        // Since 26.x the chunk layer (solid/cutout/translucent) follows each block's texture transparency.

        // The Santa Hat is a real 3D hat, not a reshaped helmet.
        ArmorRenderer.register((poseStack, collector, stack, state, slot, light, wearerModel) -> {
            HumanoidModel<HumanoidRenderState> hat = SantaHatModel.armorModel();
            collector.submitModel(hat, state, poseStack, RenderTypes.armorCutoutNoCull(SantaHatModel.TEXTURE), light,
                    OverlayTexture.NO_OVERLAY, state.outlineColor);
        }, ModItems.SANTA_HAT.get());

        ModEntityRenderers.registerLayers((layer, definition) -> ModelLayerRegistry.registerModelLayer(layer, definition::get));
        ModEntityRenderers.registerRenderers(new ModEntityRenderers.RendererSink() {
            @Override
            public <T extends Entity> void register(EntityType<? extends T> type, EntityRendererProvider<T> provider) {
                EntityRendererRegistry.register(type, provider);
            }
        });
    }
}
