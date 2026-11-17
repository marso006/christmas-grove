package com.mental.gingerbreadgrove;

import com.mental.gingerbreadgrove.registry.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

public class GingerbreadGroveFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModBlocks.cutoutBlocks().forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block.get(), RenderType.cutout()));
        ModBlocks.translucentBlocks().forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block.get(), RenderType.translucent()));
    }
}
