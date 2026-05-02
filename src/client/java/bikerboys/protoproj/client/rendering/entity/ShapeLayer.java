package bikerboys.protoproj.client.rendering.entity;

import bikerboys.protoproj.client.rendering.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.*;

import java.util.*;

public class ShapeLayer extends RenderLayer<ShapeEntityRenderState, ShapeEntityModel> {
    public ShapeLayer(RenderLayerParent<ShapeEntityRenderState, ShapeEntityModel> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, ShapeEntityRenderState state, float yRot, float xRot) {
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(new ByteBufferBuilder(786432));



        for (int i = 0; i < state.cubeListBuilder.getCubes().size(); i++) {
            CubeDefinition cube = state.cubeListBuilder.getCubes().get(i);

            ModelPart.Cube bake = cube.bake(64, 64);

        }

        bufferSource.endBatch();
    }
}
