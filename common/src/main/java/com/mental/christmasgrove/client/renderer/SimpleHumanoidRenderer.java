package com.mental.christmasgrove.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;

/**
 * A humanoid mob drawn with one texture at a fixed scale. Held items, heads and the walking animation
 * all come from {@link HumanoidMobRenderer} (render-state based since 1.21.2).
 */
public class SimpleHumanoidRenderer<T extends Mob> extends HumanoidMobRenderer<T, HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {

    private final Identifier texture;
    private final float scale;

    public SimpleHumanoidRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer,
                                  Identifier texture, float scale) {
        super(context, new HumanoidModel<>(context.bakeLayer(layer)), 0.5F * scale);
        this.texture = texture;
        this.scale = scale;
    }

    @Override
    public HumanoidRenderState createRenderState() {
        return new HumanoidRenderState();
    }

    @Override
    public Identifier getTextureLocation(HumanoidRenderState state) {
        return texture;
    }

    @Override
    protected void scale(HumanoidRenderState state, PoseStack poseStack) {
        poseStack.scale(scale, scale, scale);
    }
}
