package com.mental.christmasgrove.client.renderer;

import com.mental.christmasgrove.Constants;
import com.mental.christmasgrove.client.ModModelLayers;
import com.mental.christmasgrove.entity.SantaClaus;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/** Santa: the humanoid body with belly and beard, plus the 3D Santa hat on top. */
public class SantaClausRenderer extends SimpleHumanoidRenderer<SantaClaus> {

    public SantaClausRenderer(EntityRendererProvider.Context context) {
        super(context, ModModelLayers.SANTA_CLAUS, Constants.id("textures/entity/santa_claus.png"), 1.05F);
        addLayer(new SantaHatLayer<>(this, context.getModelSet()));
    }
}
