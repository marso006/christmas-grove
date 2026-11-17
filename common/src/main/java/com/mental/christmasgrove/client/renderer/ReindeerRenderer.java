package com.mental.christmasgrove.client.renderer;

import com.mental.christmasgrove.Constants;
import com.mental.christmasgrove.client.ModModelLayers;
import com.mental.christmasgrove.entity.Reindeer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Horse geometry plus antlers; the saddle shows when one is equipped, exactly like a horse. */
public class ReindeerRenderer extends MobRenderer<Reindeer, HorseModel<Reindeer>> {

    private static final ResourceLocation TEXTURE = Constants.id("textures/entity/reindeer.png");

    public ReindeerRenderer(EntityRendererProvider.Context context) {
        super(context, new HorseModel<>(context.bakeLayer(ModModelLayers.REINDEER)), 0.75F);
    }

    @Override
    public ResourceLocation getTextureLocation(Reindeer entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(Reindeer entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(0.95F, 0.95F, 0.95F);
    }
}
