package bikerboys.protoproj.client.rendering.entity;

import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;

public class ShapeEntityModel extends EntityModel<ShapeEntityRenderState> {

    public ShapeEntityModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition getTexturedModelData() {



        MeshDefinition modelData = new MeshDefinition();
        PartDefinition root = modelData.getRoot();



        return LayerDefinition.create(modelData, 64, 32);
    }



}
