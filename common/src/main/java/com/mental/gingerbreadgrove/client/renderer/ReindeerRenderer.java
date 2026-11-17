package com.mental.gingerbreadgrove.client.renderer;

import com.mental.gingerbreadgrove.Constants;
import com.mental.gingerbreadgrove.client.ModModelLayers;
import com.mental.gingerbreadgrove.entity.Reindeer;
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
