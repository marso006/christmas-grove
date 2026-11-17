package com.mental.gingerbreadgrove.client;

import com.mental.gingerbreadgrove.Constants;
import com.mental.gingerbreadgrove.client.renderer.EvilSnowmanRenderer;
import com.mental.gingerbreadgrove.client.renderer.GumdropBlobRenderer;
import com.mental.gingerbreadgrove.client.renderer.KrampusRenderer;
import com.mental.gingerbreadgrove.client.renderer.PeppermintPupRenderer;
import com.mental.gingerbreadgrove.client.renderer.PresentDropRenderer;
import com.mental.gingerbreadgrove.client.renderer.SantaSleighRenderer;
import com.mental.gingerbreadgrove.client.renderer.ReindeerRenderer;
import com.mental.gingerbreadgrove.client.renderer.SantaClausRenderer;
import com.mental.gingerbreadgrove.client.renderer.SimpleHumanoidRenderer;
import com.mental.gingerbreadgrove.registry.ModEntities;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Loader-neutral list of entity renderers and model layers. Each loader's client entrypoint passes in
 * its own registration callbacks.
 */
public final class ModEntityRenderers {

    /** Matches both Fabric's EntityRendererRegistry.register and NeoForge's registerEntityRenderer. */
    public interface RendererSink {
        <T extends Entity> void register(EntityType<? extends T> type, EntityRendererProvider<T> provider);
    }

    private ModEntityRenderers() {
    }

    public static void registerLayers(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> sink) {
        sink.accept(ModModelLayers.GINGERBREAD_MAN, ModModelLayers::gingerbreadMan);
        sink.accept(ModModelLayers.ELF, ModModelLayers::elf);
        sink.accept(ModModelLayers.SANTA_CLAUS, ModModelLayers::santaClaus);
        sink.accept(ModModelLayers.COOKIE_SOLDIER, ModModelLayers::cookieSoldier);
        sink.accept(ModModelLayers.REINDEER, ModModelLayers::reindeer);
        sink.accept(ModModelLayers.SANTA_HAT, SantaHatModel::createLayer);
        sink.accept(ModModelLayers.KRAMPUS, ModModelLayers::krampus);
        sink.accept(ModModelLayers.SANTA_SLEIGH, SleighModels::sleigh);
        sink.accept(ModModelLayers.PRESENT_PARACHUTE, SleighModels::parachute);
    }

    public static void registerRenderers(RendererSink sink) {
        sink.register(ModEntities.GINGERBREAD_MAN.get(), ctx -> new SimpleHumanoidRenderer<>(ctx,
                ModModelLayers.GINGERBREAD_MAN, Constants.id("textures/entity/gingerbread_man.png"), 0.5F));
        sink.register(ModEntities.ELF.get(), ctx -> new SimpleHumanoidRenderer<>(ctx,
                ModModelLayers.ELF, Constants.id("textures/entity/elf.png"), 0.68F));
        sink.register(ModEntities.SANTA_CLAUS.get(), SantaClausRenderer::new);
        sink.register(ModEntities.COOKIE_SOLDIER.get(), ctx -> new SimpleHumanoidRenderer<>(ctx,
                ModModelLayers.COOKIE_SOLDIER, Constants.id("textures/entity/cookie_soldier.png"), 1.0F));
        sink.register(ModEntities.REINDEER.get(), ReindeerRenderer::new);
        sink.register(ModEntities.PEPPERMINT_PUP.get(), PeppermintPupRenderer::new);
        sink.register(ModEntities.GUMDROP_BLOB.get(), GumdropBlobRenderer::new);
        sink.register(ModEntities.EVIL_SNOWMAN.get(), EvilSnowmanRenderer::new);
        sink.register(ModEntities.FROST_SNOWBALL.get(), ThrownItemRenderer::new);
        sink.register(ModEntities.KRAMPUS.get(), KrampusRenderer::new);
        sink.register(ModEntities.SANTA_SLEIGH.get(), SantaSleighRenderer::new);
        sink.register(ModEntities.PRESENT_DROP.get(), PresentDropRenderer::new);
    }
}
