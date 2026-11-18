package com.mental.christmasgrove.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import com.mental.christmasgrove.Constants;
import com.mental.christmasgrove.client.ModModelLayers;
import com.mental.christmasgrove.client.SantaHatModel;
import com.mental.christmasgrove.entity.SantaSleigh;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

/**
 * Draws Santa's sleigh: the sleigh itself, four galloping reindeer in a 2x2 team in front, and Santa
 * on the seat waving, wearing his hat. Everything is drawn fully lit, so the sleigh glows like a
 * shooting star at night. The reindeer and Santa are posed from small render states filled in here
 * (no stand-in entities are needed since 1.21.2's render-state models).
 */
public class SantaSleighRenderer extends EntityRenderer<SantaSleigh, SantaSleighRenderer.State> {

    private static final Identifier SLEIGH_TEXTURE = Constants.id("textures/entity/santa_sleigh.png");
    private static final Identifier REINDEER_TEXTURE = Constants.id("textures/entity/reindeer.png");
    private static final Identifier SANTA_TEXTURE = Constants.id("textures/entity/santa_claus.png");
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

    /** Santa's body, posed sitting, with his right arm waving (models are posed when the frame is drawn). */
    private static final class WavingSantaModel extends HumanoidModel<HumanoidRenderState> {
        WavingSantaModel(ModelPart root) {
            super(root);
        }

        @Override
        public void setupAnim(HumanoidRenderState state) {
            super.setupAnim(state);
            rightArm.xRot = -2.6F + Mth.sin(state.ageInTicks / 3.0F) * 0.25F;
            rightArm.zRot = 0.3F;
        }
    }

    public SantaSleighRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.sleigh = context.bakeLayer(ModModelLayers.SANTA_SLEIGH);
        this.reindeer = new HorseModel(context.bakeLayer(ModModelLayers.REINDEER));
        this.santa = new WavingSantaModel(context.bakeLayer(ModModelLayers.SANTA_CLAUS));
        this.hat = new HumanoidModel<>(context.bakeLayer(ModModelLayers.SANTA_HAT));
        hat.setAllVisible(false);
        hat.head.visible = true;
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
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        int glow = LightTexture.FULL_BRIGHT;
        float age = state.ageInTicks;

        poseStack.pushPose();
        // A gentle bob, as if riding on the wind
        poseStack.translate(0.0, Math.sin(age / 8.0) * 0.15, 0.0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.yRot));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.501F, 0.0F);

        collector.submitModelPart(sleigh, poseStack, RenderTypes.entityCutoutNoCull(SLEIGH_TEXTURE), glow, OverlayTexture.NO_OVERLAY, null);

        // The reindeer team, legs galloping
        reindeerPose.walkAnimationPos = age * 0.55F;
        reindeerPose.walkAnimationSpeed = 1.0F;
        reindeerPose.ageInTicks = age;
        reindeerPose.xRot = -5.0F;
        for (float[] slot : TEAM) {
            poseStack.pushPose();
            poseStack.translate(slot[0], Math.sin((age + slot[1] * 3) / 4.0) * 0.06, slot[1]);
            poseStack.scale(0.9F, 0.9F, 0.9F);
            collector.submitModel(reindeer, reindeerPose, poseStack, reindeer.renderType(REINDEER_TEXTURE), glow, OverlayTexture.NO_OVERLAY, 0, null);
            poseStack.popPose();
        }

        // Santa on the seat, one arm waving
        santaPose.isPassenger = true;
        santaPose.ageInTicks = age;
        poseStack.pushPose();
        poseStack.translate(0.0F, -0.55F, 0.2F);
        poseStack.scale(0.95F, 0.95F, 0.95F);
        collector.submitModel(santa, santaPose, poseStack, santa.renderType(SANTA_TEXTURE), glow, OverlayTexture.NO_OVERLAY, 0, null);
        collector.submitModel(hat, santaPose, poseStack, hat.renderType(SantaHatModel.TEXTURE), glow, OverlayTexture.NO_OVERLAY, 0, null);
        poseStack.popPose();

        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }
}
