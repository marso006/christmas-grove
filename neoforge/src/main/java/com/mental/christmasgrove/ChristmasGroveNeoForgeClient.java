package com.mental.christmasgrove;

import com.mental.christmasgrove.client.ModEntityRenderers;
import com.mental.christmasgrove.client.SantaHatModel;
import com.mental.christmasgrove.registry.ModItems;
import com.mental.christmasgrove.registry.ModBlocks;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.item.equipment.EquipmentModel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ChristmasGroveNeoForgeClient {

    private ChristmasGroveNeoForgeClient() {
    }

    @SubscribeEvent
    @SuppressWarnings("deprecation") // render types can also go in model JSON, but datagen is shared with Fabric
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ModBlocks.cutoutBlocks().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
            ModBlocks.translucentBlocks().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.translucent()));
        });
    }

    @SubscribeEvent
    public static void onClientExtensions(RegisterClientExtensionsEvent event) {
        // The Santa Hat is a real 3D hat; NeoForge copies the head pose and uses the hat material's texture.
        event.registerItem(new IClientItemExtensions() {
            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public Model getHumanoidArmorModel(ItemStack stack, EquipmentModel.LayerType layer, Model original) {
                HumanoidModel<HumanoidRenderState> hat = SantaHatModel.armorModel();
                if (original instanceof HumanoidModel wearer) {
                    wearer.copyPropertiesTo(hat); // follow the wearer's head
                }
                hat.setAllVisible(false);
                hat.head.visible = true;
                return hat;
            }
        }, ModItems.SANTA_HAT.get());
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        ModEntityRenderers.registerLayers(event::registerLayerDefinition);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        ModEntityRenderers.registerRenderers(new ModEntityRenderers.RendererSink() {
            @Override
            public <T extends Entity> void register(EntityType<? extends T> type, EntityRendererProvider<T> provider) {
                event.registerEntityRenderer(type, provider);
            }
        });
    }
}
