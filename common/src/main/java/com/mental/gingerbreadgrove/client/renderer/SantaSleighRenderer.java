package com.mental.gingerbreadgrove.client.renderer;

import com.mental.gingerbreadgrove.Constants;
import com.mental.gingerbreadgrove.client.ModModelLayers;
import com.mental.gingerbreadgrove.client.SantaHatModel;
import com.mental.gingerbreadgrove.entity.Reindeer;
import com.mental.gingerbreadgrove.entity.SantaClaus;
import com.mental.gingerbreadgrove.entity.SantaSleigh;
import com.mental.gingerbreadgrove.registry.ModEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

/**
 * Draws Santa's sleigh: the sleigh itself, four galloping reindeer in a 2x2 team in front, and Santa
 * on the seat waving, wearing his hat. Everything is drawn fully lit, so the sleigh glows like a
 * shooting star at night. The reindeer and Santa are client-only stand-in entities used to drive
 * the vanilla walk animations; they are never added to the world.
 */
public class SantaSleighRenderer extends EntityRenderer<SantaSleigh> {

    private static final ResourceLocation SLEIGH_TEXTURE = Constants.id("textures/entity/santa_sleigh.png");
    private static final ResourceLocation REINDEER_TEXTURE = Constants.id("textures/entity/reindeer.png");
    private static final ResourceLocation SANTA_TEXTURE = Constants.id("textures/entity/santa_claus.png");
    private static final float[][] TEAM = {{-0.55F, -2.7F}, {0.55F, -2.7F}, {-0.55F, -4.5F}, {0.55F, -4.5F}};

    private final ModelPart sleigh;
    private final HorseModel<Reindeer> reindeer;
    private final HumanoidModel<SantaClaus> santa;
    private final HumanoidModel<LivingEntity> hat;
    private Reindeer reindeerStandIn;
    private SantaClaus santaStandIn;

    public SantaSleighRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.sleigh = context.bakeLayer(ModModelLayers.SANTA_SLEIGH);
        this.reindeer = new HorseModel<>(context.bakeLayer(ModModelLayers.REINDEER));
        this.santa = new HumanoidModel<>(context.bakeLayer(ModModelLayers.SANTA_CLAUS));
        this.hat = new HumanoidModel<>(context.bakeLayer(ModModelLayers.SANTA_HAT));
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(SantaSleigh entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light) {
        if (reindeerStandIn == null || reindeerStandIn.level() != entity.level()) {
            reindeerStandIn = ModEntities.REINDEER.get().create(entity.level());
            santaStandIn = ModEntities.SANTA_CLAUS.get().create(entity.level());
        }
        if (reindeerStandIn == null || santaStandIn == null) {
            return;
        }
        int glow = LightTexture.FULL_BRIGHT;
        float age = entity.tickCount + partialTick;

        poseStack.pushPose();
        // A gentle bob, as if riding on the wind
        poseStack.translate(0.0, Math.sin(age / 8.0) * 0.15, 0.0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entity.getYRot()));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.501F, 0.0F);

        sleigh.render(poseStack, buffers.getBuffer(RenderType.entityCutoutNoCull(SLEIGH_TEXTURE)), glow, OverlayTexture.NO_OVERLAY);

        // The reindeer team, legs galloping
        float gallop = age * 0.55F;
        reindeer.prepareMobModel(reindeerStandIn, gallop, 1.0F, partialTick);
        reindeer.setupAnim(reindeerStandIn, gallop, 1.0F, age, 0.0F, -5.0F);
        for (float[] slot : TEAM) {
            poseStack.pushPose();
            poseStack.translate(slot[0], Math.sin((age + slot[1] * 3) / 4.0) * 0.06, slot[1]);
            poseStack.scale(0.9F, 0.9F, 0.9F);
            reindeer.renderToBuffer(poseStack, buffers.getBuffer(reindeer.renderType(REINDEER_TEXTURE)), glow, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }

        // Santa on the seat, one arm waving
        santa.riding = true;
        santa.setupAnim(santaStandIn, 0.0F, 0.0F, age, 0.0F, 0.0F);
        santa.rightArm.xRot = -2.6F + Mth.sin(age / 3.0F) * 0.25F;
        santa.rightArm.zRot = 0.3F;
        poseStack.pushPose();
        poseStack.translate(0.0F, -0.55F, 0.2F);
        poseStack.scale(0.95F, 0.95F, 0.95F);
        santa.renderToBuffer(poseStack, buffers.getBuffer(santa.renderType(SANTA_TEXTURE)), glow, OverlayTexture.NO_OVERLAY);
        @SuppressWarnings({"unchecked", "rawtypes"})
        HumanoidModel<SantaClaus> hatModel = (HumanoidModel) hat;
        santa.copyPropertiesTo(hatModel);
        hat.setAllVisible(false);
        hat.head.visible = true;
        hat.renderToBuffer(poseStack, buffers.getBuffer(hat.renderType(SantaHatModel.TEXTURE)), glow, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.popPose();
        super.render(entity, yaw, partialTick, poseStack, buffers, light);
    }

    @Override
    public ResourceLocation getTextureLocation(SantaSleigh entity) {
        return SLEIGH_TEXTURE;
    }
}
