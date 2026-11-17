package com.mental.christmasgrove.client.renderer;

import com.mental.christmasgrove.Constants;
import com.mental.christmasgrove.entity.EvilSnowman;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Vanilla snow golem geometry with a scowling face instead of a pumpkin. */
public class EvilSnowmanRenderer extends MobRenderer<EvilSnowman, SnowGolemModel<EvilSnowman>> {

    private static final ResourceLocation TEXTURE = Constants.id("textures/entity/evil_snowman.png");

    public EvilSnowmanRenderer(EntityRendererProvider.Context context) {
        super(context, new SnowGolemModel<>(context.bakeLayer(ModelLayers.SNOW_GOLEM)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(EvilSnowman entity) {
        return TEXTURE;
    }
}
