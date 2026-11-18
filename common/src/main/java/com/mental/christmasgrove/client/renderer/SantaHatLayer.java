package com.mental.christmasgrove.client.renderer;

import com.mental.christmasgrove.client.ModModelLayers;
import com.mental.christmasgrove.client.SantaHatModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

/** Draws the 3D Santa hat on a humanoid mob's head (used by Santa Claus). */
public class SantaHatLayer extends RenderLayer<HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {

    private final HumanoidModel<HumanoidRenderState> hat;

    public SantaHatLayer(RenderLayerParent<HumanoidRenderState, HumanoidModel<HumanoidRenderState>> parent, EntityModelSet models) {
        super(parent);
        this.hat = new HumanoidModel<>(models.bakeLayer(ModModelLayers.SANTA_HAT));
        // Only the head carries the hat; the model is posed from the same render state as the wearer.
        SantaHatModel.onlyHead(hat);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, HumanoidRenderState state, float yRot, float xRot) {
        renderColoredCutoutModel(hat, SantaHatModel.TEXTURE, poseStack, collector, light, state, -1, state.outlineColor);
    }
}
