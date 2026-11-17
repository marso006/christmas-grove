package com.mental.christmasgrove.client.renderer;

import com.mental.christmasgrove.Constants;
import com.mental.christmasgrove.entity.GumdropBlob;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SlimeRenderer;
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

    public GumdropBlobRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Slime entity) {
        return entity instanceof GumdropBlob blob ? FLAVOURS[blob.flavour()] : FLAVOURS[0];
    }
}
