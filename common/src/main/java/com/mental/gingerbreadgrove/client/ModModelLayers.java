package com.mental.gingerbreadgrove.client;

import com.mental.gingerbreadgrove.Constants;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Entity model geometry. Humanoids start from the vanilla humanoid mesh (so walking, arm swing and
 * held items all work) and add a few boxes; the reindeer is the vanilla horse mesh plus antlers.
 * Extra boxes use texture rows 32-63, which the 64x64 humanoid mesh leaves free.
 */
public final class ModModelLayers {

    public static final ModelLayerLocation GINGERBREAD_MAN = layer("gingerbread_man");
    public static final ModelLayerLocation ELF = layer("elf");
    public static final ModelLayerLocation SANTA_CLAUS = layer("santa_claus");
    public static final ModelLayerLocation COOKIE_SOLDIER = layer("cookie_soldier");
    public static final ModelLayerLocation REINDEER = layer("reindeer");
    public static final ModelLayerLocation SANTA_HAT = layer("santa_hat");

    private ModModelLayers() {
    }

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(Constants.id(name), "main");
    }

    private static MeshDefinition humanoid() {
        return HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
    }

    public static LayerDefinition gingerbreadMan() {
        return LayerDefinition.create(humanoid(), 64, 64);
    }

    /** Pointy ears and the tip of a floppy hat. */
    public static LayerDefinition elf() {
        MeshDefinition mesh = humanoid();
        PartDefinition head = mesh.getRoot().getChild("head");
        head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(0, 48)
                .addBox(4.0F, -6.0F, -1.0F, 2.0F, 3.0F, 1.0F), PartPose.rotation(0.0F, 0.0F, -0.35F));
        head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(8, 48)
                .addBox(-6.0F, -6.0F, -1.0F, 2.0F, 3.0F, 1.0F), PartPose.rotation(0.0F, 0.0F, 0.35F));
        head.addOrReplaceChild("hat_tip", CubeListBuilder.create().texOffs(16, 48)
                .addBox(-2.0F, -12.0F, 0.0F, 4.0F, 4.0F, 4.0F), PartPose.rotation(-0.4F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    /** A round belly and a long beard (his hat is drawn by SantaHatLayer). */
    public static LayerDefinition santaClaus() {
        MeshDefinition mesh = humanoid();
        PartDefinition root = mesh.getRoot();
        root.getChild("body").addOrReplaceChild("belly", CubeListBuilder.create().texOffs(0, 32)
                .addBox(-4.5F, 4.0F, -4.0F, 9.0F, 6.0F, 2.0F), PartPose.ZERO);
        PartDefinition head = root.getChild("head");
        head.addOrReplaceChild("beard", CubeListBuilder.create().texOffs(24, 32)
                .addBox(-4.0F, -3.0F, -4.6F, 8.0F, 6.0F, 1.0F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }

    /** A tall nutcracker shako hat. */
    public static LayerDefinition cookieSoldier() {
        MeshDefinition mesh = humanoid();
        mesh.getRoot().getChild("head").addOrReplaceChild("shako", CubeListBuilder.create().texOffs(0, 32)
                .addBox(-4.5F, -14.0F, -4.5F, 9.0F, 6.0F, 9.0F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }

    /** The horse mesh with branching antlers on the head. */
    public static LayerDefinition reindeer() {
        MeshDefinition mesh = HorseModel.createBodyMesh(CubeDeformation.NONE);
        PartDefinition head = mesh.getRoot().getChild("head_parts").getChild("head");
        for (int side : new int[]{-1, 1}) {
            float x = side > 0 ? 1.5F : -2.5F;          // main beam just inside each ear
            float tineX = side > 0 ? 2.5F : -5.5F;      // tines point outwards
            head.addOrReplaceChild(side > 0 ? "left_antler" : "right_antler", CubeListBuilder.create().texOffs(48, 0)
                    .addBox(x, -18.0F, 1.0F, 1.0F, 7.0F, 1.0F)
                    .addBox(tineX, -16.0F, 1.0F, 3.0F, 1.0F, 1.0F)
                    .addBox(tineX, -18.0F, 0.0F, 3.0F, 1.0F, 1.0F)
                    .addBox(x, -19.0F, -1.0F, 1.0F, 1.0F, 3.0F), PartPose.ZERO);
        }
        return LayerDefinition.create(mesh, 64, 64);
    }
}
