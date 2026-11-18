package com.mental.christmasgrove.client.renderer;

import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import com.mental.christmasgrove.Constants;
import com.mental.christmasgrove.client.ModModelLayers;
import com.mental.christmasgrove.entity.PresentDrop;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

/** A gift box hanging under a candy-striped parachute, swaying as it floats down. */
public class PresentDropRenderer extends EntityRenderer<PresentDrop, PresentDropRenderer.State> {

    private static final Identifier PARACHUTE_TEXTURE = Constants.id("textures/entity/present_parachute.png");
    private static final BlockDisplayContext BOX_DISPLAY = BlockDisplayContext.create();
    private final ModelPart parachute;
    private final BlockModelResolver blockModels;

    public static class State extends EntityRenderState {
        public final BlockModelRenderState box = new BlockModelRenderState();
        public float yRot;
    }

    public PresentDropRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.parachute = context.bakeLayer(ModModelLayers.PRESENT_PARACHUTE);
        this.blockModels = context.getBlockModelResolver();
        this.shadowRadius = 0.4F;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(PresentDrop entity, State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        blockModels.update(state.box, entity.boxState(), BOX_DISPLAY);
        state.yRot = entity.getYRot();
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        float age = state.ageInTicks;
        int light = state.lightCoords;
        poseStack.pushPose();
        poseStack.rotate(Axis.YP.rotationDegrees(state.yRot));
        // Swing like a pendulum hanging from the canopy
        poseStack.translate(0.0, 1.9, 0.0);
        poseStack.rotate(Axis.ZP.rotation(Mth.sin(age / 9.0F) * 0.18F));
        poseStack.rotate(Axis.XP.rotation(Mth.cos(age / 11.0F) * 0.12F));
        poseStack.translate(0.0, -1.9, 0.0);

        poseStack.pushPose();
        poseStack.scale(0.75F, 0.75F, 0.75F);
        poseStack.translate(-0.5, 0.0, -0.5);
        state.box.submit(poseStack, collector, light, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        collector.submitModelPart(parachute, poseStack, RenderTypes.entityCutout(PARACHUTE_TEXTURE), light, OverlayTexture.NO_OVERLAY, null);
        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }
}
