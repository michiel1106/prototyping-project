package bikerboys.protoproj.client.rendering.entity;


import bikerboys.protoproj.client.rendering.*;
import bikerboys.protoproj.entity.custom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.*;
import org.jspecify.annotations.*;

public class ShapeEntityRenderer extends MobRenderer<ShapeEntity, ShapeEntityRenderState, ShapeEntityModel> {

    public ShapeEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new ShapeEntityModel(context.bakeLayer(ModEntityModelLayers.MINI_GOLEM)), 0.375f);
        addLayer(new ShapeLayer(this));
    }

    @Override
    public @NonNull Identifier getTextureLocation(ShapeEntityRenderState state) {
        return Identifier.fromNamespaceAndPath("minecraft", "textures/entity/cow/warm.png");
    }

    @Override
    public ShapeEntityRenderState createRenderState() {

        return new ShapeEntityRenderState();
    }

    @Override
    public void extractRenderState(ShapeEntity entity, ShapeEntityRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.cubeListBuilder = new CubeListBuilder().addBox(
                "small_cube",
                -0.5F, 0.5F, -0.5F,
                1, 1, 1,
                CubeDeformation.NONE,
                0, 0
        );

        // AABBGGRR
        state.colorOffsets.addFirst(0xFF004F00);

    }
}
