package com.mental.gingerbreadgrove.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

/**
 * A humanoid mob drawn with one texture at a fixed scale. Held items, armor-free heads and the
 * walking animation all come from {@link HumanoidMobRenderer}.
 */
public class SimpleHumanoidRenderer<T extends Mob> extends HumanoidMobRenderer<T, HumanoidModel<T>> {

    private final ResourceLocation texture;
    private final float scale;

    public SimpleHumanoidRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer,
                                  ResourceLocation texture, float scale) {
        super(context, new HumanoidModel<>(context.bakeLayer(layer)), 0.5F * scale);
        this.texture = texture;
        this.scale = scale;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return texture;
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(scale, scale, scale);
    }
}
