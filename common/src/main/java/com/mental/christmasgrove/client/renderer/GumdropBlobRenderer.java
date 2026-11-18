package com.mental.christmasgrove.client.renderer;

import com.mental.christmasgrove.Constants;
import com.mental.christmasgrove.entity.GumdropBlob;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Slime;

/** The slime renderer (translucent outer jelly, squish animation) with four gumdrop flavours. */
public class GumdropBlobRenderer extends SlimeRenderer {

    private static final ResourceLocation[] FLAVOURS = {
            Constants.id("textures/entity/gumdrop_blob/cherry.png"),
            Constants.id("textures/entity/gumdrop_blob/lime.png"),
            Constants.id("textures/entity/gumdrop_blob/lemon.png"),
            Constants.id("textures/entity/gumdrop_blob/grape.png"),
    };

    /** The slime render state plus which flavour to draw. */
    public static class State extends SlimeRenderState {
        public int flavour;
    }

    public GumdropBlobRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public SlimeRenderState createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(Slime entity, SlimeRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        if (state instanceof State gumdrop && entity instanceof GumdropBlob blob) {
            gumdrop.flavour = blob.flavour();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(SlimeRenderState state) {
        return FLAVOURS[state instanceof State gumdrop ? gumdrop.flavour : 0];
    }
}
