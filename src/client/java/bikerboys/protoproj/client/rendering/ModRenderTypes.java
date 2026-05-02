package bikerboys.protoproj.client.rendering;

import com.mojang.blaze3d.pipeline.*;
import com.mojang.blaze3d.platform.*;
import com.mojang.blaze3d.vertex.*;
import static net.minecraft.client.renderer.RenderPipelines.*;
import net.minecraft.client.renderer.rendertype.*;

public class ModRenderTypes {

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

    public static final RenderType SOLID_COLOR_CUBE = RenderType.create(
            "solid_color_cube",
            RenderSetup.builder(SOLID_BLOCK)// or custom pipeline if available
                    .setOutline(RenderSetup.OutlineProperty.NONE)
                    .createRenderSetup()
    );

}
