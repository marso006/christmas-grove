package com.mental.christmasgrove.client;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometry for Santa's sleigh (128x128 texture) and the present parachute (64x32 texture).
 * Model space: +y is down, the front of the sleigh is -z, the ground is at y = 24.
 */
public final class SleighModels {

    private SleighModels() {
    }

    public static LayerDefinition sleigh() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        // Gold runners with curled-up fronts, on short struts
        root.addOrReplaceChild("runners", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-7.0F, 23.0F, -12.0F, 1.0F, 1.0F, 24.0F)
                .addBox(6.0F, 23.0F, -12.0F, 1.0F, 1.0F, 24.0F), PartPose.ZERO);
        root.addOrReplaceChild("curls", CubeListBuilder.create().texOffs(52, 0)
                .addBox(-7.0F, 18.0F, -13.0F, 1.0F, 5.0F, 1.0F)
                .addBox(6.0F, 18.0F, -13.0F, 1.0F, 5.0F, 1.0F), PartPose.ZERO);
        root.addOrReplaceChild("struts", CubeListBuilder.create().texOffs(60, 0)
                .addBox(-6.5F, 20.0F, -7.0F, 1.0F, 3.0F, 1.0F)
                .addBox(5.5F, 20.0F, -7.0F, 1.0F, 3.0F, 1.0F)
                .addBox(-6.5F, 20.0F, 6.0F, 1.0F, 3.0F, 1.0F)
                .addBox(5.5F, 20.0F, 6.0F, 1.0F, 3.0F, 1.0F), PartPose.ZERO);
        // The red body, a high seat back and a curled front
        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 26)
                .addBox(-6.0F, 13.0F, -9.0F, 12.0F, 7.0F, 18.0F), PartPose.ZERO);
        root.addOrReplaceChild("seat_back", CubeListBuilder.create().texOffs(0, 52)
                .addBox(-6.0F, 7.0F, 7.0F, 12.0F, 6.0F, 3.0F), PartPose.ZERO);
        root.addOrReplaceChild("front", CubeListBuilder.create().texOffs(32, 52)
                .addBox(-6.0F, 9.0F, -11.0F, 12.0F, 6.0F, 2.0F), PartPose.rotation(0.25F, 0.0F, 0.0F));
        // The sack of presents behind Santa, with a couple poking out
        root.addOrReplaceChild("sack", CubeListBuilder.create().texOffs(64, 0)
                .addBox(-4.0F, 5.0F, 2.0F, 8.0F, 8.0F, 6.0F), PartPose.ZERO);
        root.addOrReplaceChild("gifts", CubeListBuilder.create()
                .texOffs(64, 16).addBox(-3.0F, 2.0F, 3.0F, 3.0F, 3.0F, 3.0F)
                .texOffs(80, 16).addBox(0.5F, 3.0F, 4.0F, 3.0F, 2.0F, 3.0F), PartPose.ZERO);
        // Reins out to the reindeer, and the harness bar they pull
        root.addOrReplaceChild("reins", CubeListBuilder.create().texOffs(0, 64)
                .addBox(-4.0F, 15.0F, -61.0F, 1.0F, 1.0F, 50.0F)
                .addBox(3.0F, 15.0F, -61.0F, 1.0F, 1.0F, 50.0F), PartPose.ZERO);
        root.addOrReplaceChild("harness", CubeListBuilder.create().texOffs(0, 118)
                .addBox(-12.0F, 14.0F, -62.0F, 24.0F, 1.0F, 1.0F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static LayerDefinition parachute() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("canopy", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-7.0F, -9.0F, -7.0F, 14.0F, 3.0F, 14.0F), PartPose.ZERO);
        root.addOrReplaceChild("strings", CubeListBuilder.create().texOffs(0, 17)
                .addBox(-6.5F, -6.0F, -6.5F, 1.0F, 14.0F, 1.0F)
                .addBox(5.5F, -6.0F, -6.5F, 1.0F, 14.0F, 1.0F)
                .addBox(-6.5F, -6.0F, 5.5F, 1.0F, 14.0F, 1.0F)
                .addBox(5.5F, -6.0F, 5.5F, 1.0F, 14.0F, 1.0F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 32);
    }
}
