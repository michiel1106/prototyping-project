package bikerboys.protoproj.client.rendering;

import bikerboys.protoproj.*;
import bikerboys.protoproj.client.rendering.entity.*;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.model.geom.*;
import net.minecraft.resources.*;

public class ModEntityModelLayers {
    public static final ModelLayerLocation MINI_GOLEM = createMain("mini_golem");

    private static ModelLayerLocation createMain(String name) {
        return new ModelLayerLocation(Identifier.fromNamespaceAndPath(PrototypingProject.MOD_ID, name), "main");
    }

    public static void registerModelLayers() {
        ModelLayerRegistry.registerModelLayer(ModEntityModelLayers.MINI_GOLEM, ShapeEntityModel::getTexturedModelData);
    }
}
