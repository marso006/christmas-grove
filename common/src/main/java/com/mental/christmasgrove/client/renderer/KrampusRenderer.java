package com.mental.christmasgrove.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import com.mental.christmasgrove.Constants;
import com.mental.christmasgrove.client.ModModelLayers;
import com.mental.christmasgrove.entity.Krampus;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

/** Krampus: a towering horned figure whose red eyes glow in the dark. */
public class KrampusRenderer extends SimpleHumanoidRenderer<Krampus> {

    private static final RenderType EYES = RenderTypes.eyes(Constants.id("textures/entity/krampus_eyes.png"));

    public KrampusRenderer(EntityRendererProvider.Context context) {
        super(context, ModModelLayers.KRAMPUS, Constants.id("textures/entity/krampus.png"), 1.35F);
        addLayer(new EyesLayer<HumanoidRenderState, HumanoidModel<HumanoidRenderState>>(this) {
            @Override
            public RenderType renderType() {
                return EYES;
            }
        });
    }
}
