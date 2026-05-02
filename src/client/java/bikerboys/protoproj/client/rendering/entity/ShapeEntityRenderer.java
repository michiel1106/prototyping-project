package bikerboys.protoproj.client.rendering.entity;


import bikerboys.protoproj.client.rendering.*;
import bikerboys.protoproj.entity.custom.*;
import bikerboys.protoproj.translation.*;
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

        CubeListBuilder cubeListBuilder = new CubeListBuilder();

        for (int i = 0; i < entity.getCubes().size(); i++) {
            TranslationCube cube = entity.getCubes().get(i);
            int color = entity.getColor(i);


            state.colorOffsets.add(color);
        }

        state.cubeListBuilder = cubeListBuilder;
        state.colorOffsets = entity.getColors();

    }
}
