package com.mental.christmasgrove.client.renderer;

import com.mental.christmasgrove.Constants;
import com.mental.christmasgrove.client.ModModelLayers;
import com.mental.christmasgrove.entity.Reindeer;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.resources.Identifier;

/** Horse geometry plus antlers; the saddle shows when one is equipped, exactly like a horse. */
public class ReindeerRenderer extends AbstractHorseRenderer<Reindeer, EquineRenderState, HorseModel> {

    private static final Identifier TEXTURE = Constants.id("textures/entity/reindeer.png");

    public ReindeerRenderer(EntityRendererProvider.Context context) {
        super(context, new HorseModel(context.bakeLayer(ModModelLayers.REINDEER)),
                new HorseModel(context.bakeLayer(ModModelLayers.REINDEER)));
        this.shadowRadius = 0.95F;
    }

    @Override
    public EquineRenderState createRenderState() {
        return new EquineRenderState();
    }

    @Override
    public Identifier getTextureLocation(EquineRenderState state) {
        return TEXTURE;
    }
}
