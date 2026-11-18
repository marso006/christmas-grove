package com.mental.christmasgrove.client;

import com.mental.christmasgrove.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

/**
 * A proper 3D Santa hat: a white fur brim around the head, a red cone in three segments that flops
 * backwards, and a white pom-pom on the tip. Worn as the Santa Hat armor piece and by Santa himself.
 * <p>
 * It is a humanoid model whose only geometry hangs off the head, so it follows head movement through
 * the usual {@code copyPropertiesTo}. Texture: textures/models/armor/santa_hat_layer_1.png (64x64).
 */
public final class SantaHatModel {

    public static final Identifier TEXTURE = Constants.id("textures/models/armor/santa_hat_layer_1.png");

    private static HumanoidModel<HumanoidRenderState> armorModel;

    private SantaHatModel() {
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);

        head.addOrReplaceChild("brim", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4.5F, -9.0F, -4.5F, 9.0F, 3.0F, 9.0F, new CubeDeformation(0.25F)), PartPose.ZERO);
        head.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 12)
                .addBox(-4.0F, -13.0F, -4.0F, 8.0F, 4.0F, 8.0F), PartPose.ZERO);
        // Each segment is a child of the one below and tilts further back, so the hat droops.
        PartDefinition mid = head.addOrReplaceChild("mid", CubeListBuilder.create().texOffs(0, 24)
                .addBox(-3.0F, -4.0F, -3.0F, 6.0F, 4.0F, 6.0F), PartPose.offsetAndRotation(0.0F, -12.0F, 0.5F, -0.35F, 0.0F, 0.0F));
        PartDefinition top = mid.addOrReplaceChild("top", CubeListBuilder.create().texOffs(24, 24)
                .addBox(-2.0F, -3.0F, -2.0F, 4.0F, 3.0F, 4.0F), PartPose.offsetAndRotation(0.0F, -3.2F, 0.3F, -0.55F, 0.0F, 0.0F));
        PartDefinition tip = top.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(40, 24)
                .addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offsetAndRotation(0.0F, -2.4F, 0.2F, -0.75F, 0.0F, 0.0F));
        tip.addOrReplaceChild("pompom", CubeListBuilder.create().texOffs(0, 34)
                .addBox(-1.5F, -2.0F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, -3.0F, 0.0F));

        // HumanoidModel needs every part to exist; the rest stay empty. Since 1.21.2 "hat" lives inside "head".
        head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    /** The shared model used when a player or mob wears the Santa Hat (baked on first use). */
    public static HumanoidModel<HumanoidRenderState> armorModel() {
        if (armorModel == null) {
            armorModel = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModModelLayers.SANTA_HAT));
            // Only the head part carries the hat. The model is posed from the wearer's render state when drawn.
            armorModel.setAllVisible(false);
            armorModel.head.visible = true;
        }
        return armorModel;
    }
}
