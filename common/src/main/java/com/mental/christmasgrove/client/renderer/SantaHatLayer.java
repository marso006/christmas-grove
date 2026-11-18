package com.mental.christmasgrove.client.renderer;

import com.mental.christmasgrove.client.ModModelLayers;
import com.mental.christmasgrove.client.SantaHatModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

/** Draws the 3D Santa hat on a humanoid mob's head (used by Santa Claus). */
public class SantaHatLayer extends RenderLayer<HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {

    private final HumanoidModel<HumanoidRenderState> hat;

    public SantaHatLayer(RenderLayerParent<HumanoidRenderState, HumanoidModel<HumanoidRenderState>> parent, EntityModelSet models) {
        super(parent);
        this.hat = new HumanoidModel<>(models.bakeLayer(ModModelLayers.SANTA_HAT));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffers, int light, HumanoidRenderState state, float yRot, float xRot) {
        getParentModel().copyPropertiesTo(hat);
        hat.setAllVisible(false);
        hat.head.visible = true;
        renderColoredCutoutModel(hat, SantaHatModel.TEXTURE, poseStack, buffers, light, state, -1);
    }
}
