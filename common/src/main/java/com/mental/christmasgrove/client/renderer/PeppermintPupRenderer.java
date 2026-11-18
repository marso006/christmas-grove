package com.mental.christmasgrove.client.renderer;

import com.mental.christmasgrove.Constants;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.resources.Identifier;

/** The wolf renderer (collar, armor, sitting pose) with peppermint-striped fur. */
public class PeppermintPupRenderer extends WolfRenderer {

    private static final Identifier TEXTURE = Constants.id("textures/entity/peppermint_pup.png");
    private static final Identifier ANGRY = Constants.id("textures/entity/peppermint_pup_angry.png");

    public PeppermintPupRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Identifier getTextureLocation(WolfRenderState state) {
        return state.isAngry ? ANGRY : TEXTURE;
    }
}
