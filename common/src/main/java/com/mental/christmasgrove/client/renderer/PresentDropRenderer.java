package com.mental.christmasgrove.client.renderer;

import com.mental.christmasgrove.Constants;
import com.mental.christmasgrove.client.ModModelLayers;
import com.mental.christmasgrove.entity.PresentDrop;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** A gift box hanging under a candy-striped parachute, swaying as it floats down. */
public class PresentDropRenderer extends EntityRenderer<PresentDrop> {

    private static final ResourceLocation PARACHUTE_TEXTURE = Constants.id("textures/entity/present_parachute.png");
    private final ModelPart parachute;

    public PresentDropRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.parachute = context.bakeLayer(ModModelLayers.PRESENT_PARACHUTE);
        this.shadowRadius = 0.4F;
    }

    @Override
    public void render(PresentDrop entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light) {
        float age = entity.tickCount + partialTick;
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot()));
        // Swing like a pendulum hanging from the canopy
        poseStack.translate(0.0, 1.9, 0.0);
        poseStack.mulPose(Axis.ZP.rotation(Mth.sin(age / 9.0F) * 0.18F));
        poseStack.mulPose(Axis.XP.rotation(Mth.cos(age / 11.0F) * 0.12F));
        poseStack.translate(0.0, -1.9, 0.0);

        poseStack.pushPose();
        poseStack.scale(0.75F, 0.75F, 0.75F);
        poseStack.translate(-0.5, 0.0, -0.5);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(entity.boxState(), poseStack, buffers, light, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        parachute.render(poseStack, buffers.getBuffer(RenderType.entityCutoutNoCull(PARACHUTE_TEXTURE)), light, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, yaw, partialTick, poseStack, buffers, light);
    }

    @Override
    public ResourceLocation getTextureLocation(PresentDrop entity) {
        return PARACHUTE_TEXTURE;
    }
}
