package com.mental.gingerbreadgrove.client.renderer;

import com.mental.gingerbreadgrove.Constants;
import com.mental.gingerbreadgrove.client.ModModelLayers;
import com.mental.gingerbreadgrove.entity.SantaClaus;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/** Santa: the humanoid body with belly and beard, plus the 3D Santa hat on top. */
public class SantaClausRenderer extends SimpleHumanoidRenderer<SantaClaus> {

    public SantaClausRenderer(EntityRendererProvider.Context context) {
        super(context, ModModelLayers.SANTA_CLAUS, Constants.id("textures/entity/santa_claus.png"), 1.05F);
        addLayer(new SantaHatLayer<>(this, context.getModelSet()));
    }
}
