package com.mental.christmasgrove.client.renderer;

import com.mental.christmasgrove.Constants;
import com.mental.christmasgrove.client.ModModelLayers;
import com.mental.christmasgrove.client.SantaHatModel;
import com.mental.christmasgrove.entity.SantaSleigh;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Draws Santa's sleigh: the sleigh itself, four galloping reindeer in a 2x2 team in front, and Santa
 * on the seat waving, wearing his hat. Everything is drawn fully lit, so the sleigh glows like a
 * shooting star at night. The reindeer and Santa are posed from small render states filled in here
 * (no stand-in entities are needed since 1.21.2's render-state models).
 */
public class SantaSleighRenderer extends EntityRenderer<SantaSleigh, SantaSleighRenderer.State> {

    private static final ResourceLocation SLEIGH_TEXTURE = Constants.id("textures/entity/santa_sleigh.png");
    private static final ResourceLocation REINDEER_TEXTURE = Constants.id("textures/entity/reindeer.png");
    private static final ResourceLocation SANTA_TEXTURE = Constants.id("textures/entity/santa_claus.png");
    private static final float[][] TEAM = {{-0.55F, -2.7F}, {0.55F, -2.7F}, {-0.55F, -4.5F}, {0.55F, -4.5F}};

    private final ModelPart sleigh;
    private final HorseModel reindeer;
    private final HumanoidModel<HumanoidRenderState> santa;
    private final HumanoidModel<HumanoidRenderState> hat;
    private final EquineRenderState reindeerPose = new EquineRenderState();
    private final HumanoidRenderState santaPose = new HumanoidRenderState();

    public static class State extends EntityRenderState {
        public float yRot;
    }

    public SantaSleighRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.sleigh = context.bakeLayer(ModModelLayers.SANTA_SLEIGH);
        this.reindeer = new HorseModel(context.bakeLayer(ModModelLayers.REINDEER));
        this.santa = new HumanoidModel<>(context.bakeLayer(ModModelLayers.SANTA_CLAUS));
        this.hat = new HumanoidModel<>(context.bakeLayer(ModModelLayers.SANTA_HAT));
        this.shadowRadius = 0.0F;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(SantaSleigh entity, State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = entity.getYRot();
    }

    @Override
    public void render(State state, PoseStack poseStack, MultiBufferSource buffers, int light) {
        int glow = LightTexture.FULL_BRIGHT;
        float age = state.ageInTicks;

        poseStack.pushPose();
        // A gentle bob, as if riding on the wind
        poseStack.translate(0.0, Math.sin(age / 8.0) * 0.15, 0.0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.yRot));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.501F, 0.0F);

        sleigh.render(poseStack, buffers.getBuffer(RenderType.entityCutoutNoCull(SLEIGH_TEXTURE)), glow, OverlayTexture.NO_OVERLAY);

        // The reindeer team, legs galloping
        reindeerPose.walkAnimationPos = age * 0.55F;
        reindeerPose.walkAnimationSpeed = 1.0F;
        reindeerPose.ageInTicks = age;
        reindeerPose.xRot = -5.0F;
        reindeer.setupAnim(reindeerPose);
        for (float[] slot : TEAM) {
            poseStack.pushPose();
            poseStack.translate(slot[0], Math.sin((age + slot[1] * 3) / 4.0) * 0.06, slot[1]);
            poseStack.scale(0.9F, 0.9F, 0.9F);
            reindeer.renderToBuffer(poseStack, buffers.getBuffer(reindeer.renderType(REINDEER_TEXTURE)), glow, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }

        // Santa on the seat, one arm waving
        santaPose.isPassenger = true;
        santaPose.ageInTicks = age;
        santa.setupAnim(santaPose);
        santa.rightArm.xRot = -2.6F + Mth.sin(age / 3.0F) * 0.25F;
        santa.rightArm.zRot = 0.3F;
        poseStack.pushPose();
        poseStack.translate(0.0F, -0.55F, 0.2F);
        poseStack.scale(0.95F, 0.95F, 0.95F);
        santa.renderToBuffer(poseStack, buffers.getBuffer(santa.renderType(SANTA_TEXTURE)), glow, OverlayTexture.NO_OVERLAY);
        santa.copyPropertiesTo(hat);
        hat.setAllVisible(false);
        hat.head.visible = true;
        hat.renderToBuffer(poseStack, buffers.getBuffer(hat.renderType(SantaHatModel.TEXTURE)), glow, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.popPose();
        super.render(state, poseStack, buffers, light);
    }
}
