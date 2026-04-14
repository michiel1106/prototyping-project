package bikerboys.protoproj.client.rendering;

import com.mojang.blaze3d.pipeline.*;
import com.mojang.blaze3d.platform.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import static net.minecraft.client.renderer.RenderPipelines.*;
import net.minecraft.client.renderer.rendertype.*;

public class BlockRenderHelper {

    public static final BlendFunction OPAQUE =
            new BlendFunction(SourceFactor.ONE, DestFactor.ZERO);

    public static final RenderPipeline.Snippet BLOCK_SNIPPETD = RenderPipeline.builder(MATRICES_PROJECTION_SNIPPET)
            .withVertexShader("core/position_color")
            .withFragmentShader("core/position_color")
            .withColorTargetState(new ColorTargetState(OPAQUE))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .withDepthStencilState(DepthStencilState.DEFAULT)
          //  .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .buildSnippet();

    public static final RenderPipeline SOLID_BLOCK = register(RenderPipeline.builder(BLOCK_SNIPPETD).withCull(false).withLocation("pipeline/solid_block").build());

    private static final RenderType SOLID_COLOR_CUBE = RenderType.create(
            "solid_color_cube",
            RenderSetup.builder(SOLID_BLOCK)// or custom pipeline if available
                    .setOutline(RenderSetup.OutlineProperty.NONE)
                    .createRenderSetup()
    );


    public static void renderCube(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            float minX, float maxX,
            float minY, float maxY,
            float minZ, float maxZ,
            int color
    ) {
        VertexConsumer consumer = bufferSource.getBuffer(SOLID_COLOR_CUBE);

        PoseStack.Pose pose = poseStack.last();

        // Front (Z+)
        quad(consumer, pose,
                minX, minY, maxZ,
                maxX, minY, maxZ,
                maxX, maxY, maxZ,
                minX, maxY, maxZ,
                color
        );

        // Back (Z-)
        quad(consumer, pose,
                maxX, minY, minZ,
                minX, minY, minZ,
                minX, maxY, minZ,
                maxX, maxY, minZ,
                color
        );

        // Top (Y+)
        quad(consumer, pose,
                minX, maxY, minZ,
                maxX, maxY, minZ,
                maxX, maxY, maxZ,
                minX, maxY, maxZ,
                color
        );

        // Bottom (Y-)
        quad(consumer, pose,
                minX, minY, maxZ,
                maxX, minY, maxZ,
                maxX, minY, minZ,
                minX, minY, minZ,
                color
        );

        // Right (X+)
        quad(consumer, pose,
                maxX, minY, maxZ,
                maxX, minY, minZ,
                maxX, maxY, minZ,
                maxX, maxY, maxZ,
                color
        );

        // Left (X-)
        quad(consumer, pose,
                minX, minY, minZ,
                minX, minY, maxZ,
                minX, maxY, maxZ,
                minX, maxY, minZ,
                color
        );
    }

    private static void quad(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4,
            int color
    ) {
        consumer.addVertex(pose, x1, y1, z1)
                .setColor(color)
                .setUv(0.0F, 0.0F)
                .setLight(0xFFFFFF);

        consumer.addVertex(pose, x2, y2, z2)
                .setColor(color)
                .setUv(1.0F, 0.0F)
                .setLight(0xFFFFFF);

        consumer.addVertex(pose, x3, y3, z3)
                .setColor(color)
                .setUv(1.0F, 1.0F)
                .setLight(0xFFFFFF);

        consumer.addVertex(pose, x4, y4, z4)
                .setColor(color)
                .setUv(0.0F, 1.0F)
                .setLight(0xFFFFFF);
    }
}