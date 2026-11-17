package com.mental.gingerbreadgrove.client.renderer;

import com.mental.gingerbreadgrove.Constants;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;

/** The wolf renderer (collar, armor, sitting pose) with peppermint-striped fur. */
public class PeppermintPupRenderer extends WolfRenderer {

    private static final ResourceLocation TEXTURE = Constants.id("textures/entity/peppermint_pup.png");
    private static final ResourceLocation ANGRY = Constants.id("textures/entity/peppermint_pup_angry.png");

    public PeppermintPupRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Wolf entity) {
        return entity.isAngry() ? ANGRY : TEXTURE;
    }
}
