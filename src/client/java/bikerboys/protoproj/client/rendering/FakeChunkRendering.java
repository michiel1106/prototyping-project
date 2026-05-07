package bikerboys.protoproj.client.rendering;

import com.mojang.authlib.minecraft.client.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.*;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.player.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.*;
import net.minecraft.client.renderer.item.properties.numeric.*;
import net.minecraft.core.*;
import net.minecraft.world.phys.*;
import org.joml.*;

import java.util.*;
import java.util.Random;
import java.util.concurrent.*;

public class FakeChunkRendering {
    public static CustomRenderRegionCache cache = new CustomRenderRegionCache();
    private static final Map<Long, CompiledSectionMesh> MESH_CACHE = new WeakHashMap<>();

    public static List<SectionRenderDispatcher.RenderSection> getRenderSections(int chunkX, int chunkZ) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        SectionRenderDispatcher sectionRenderDispatcher = minecraft.levelRenderer.getSectionRenderDispatcher();

        if (sectionRenderDispatcher == null || level == null) return null;

        List<SectionRenderDispatcher.RenderSection> sections = new ArrayList<>();
        int lowSectionY = level.getMinSectionY();
        int maxSectionY = level.getMaxSectionY();

        for (int sectionY = lowSectionY; sectionY < maxSectionY; sectionY++) {
            long sectionNode = SectionPos.asLong(chunkX, sectionY, chunkZ);

            int finalSectionY = sectionY;
            CompiledSectionMesh sectionMesh = MESH_CACHE.computeIfAbsent(sectionNode, key -> {
                return getSectionMesh(chunkX, chunkZ, finalSectionY);
            });

            if (sectionMesh != null) {
                SectionRenderDispatcher.RenderSection section =
                        sectionRenderDispatcher.new RenderSection(
                                Objects.hash(chunkX, sectionY, chunkZ),
                                sectionNode
                        );
                section.sectionMesh.set(sectionMesh);
                sections.add(section);
            }
        }

        return sections;
    }

    public static CompiledSectionMesh getSectionMesh(int chunkX, int chunkZ, int sectionY) {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;

            if (level == null) return null;

            SectionRenderDispatcher dispatcher = minecraft.levelRenderer.getSectionRenderDispatcher();
            if (dispatcher == null) return null;

            // Create section node from coordinates
            long sectionNode = SectionPos.asLong(chunkX, sectionY, chunkZ);
            SectionPos sectionPos = SectionPos.of(sectionNode);

            // Get camera position for vertex sorting
            Vec3 cameraPos = minecraft.gameRenderer.getMainCamera().position();

            VertexSorting vertexSorting =
                    VertexSorting.byDistance(
                            (float)(cameraPos.x - sectionPos.minBlockX()),
                            (float)(cameraPos.y - sectionPos.minBlockY()),
                            (float)(cameraPos.z - sectionPos.minBlockZ())
                    );

            // Create render region cache and region for this section

            RenderSectionRegion region = cache.createRegion(level, sectionNode);

            // Get the section compiler from renderer
            SectionCompiler sectionCompiler = getSectionCompiler();
            if (sectionCompiler == null) {
                return null;
            }

            SectionBufferBuilderPack buffers =
                    minecraft.renderBuffers().fixedBufferPack();

            // Compile the section
            SectionCompiler.Results results = sectionCompiler.compile(
                    sectionPos,
                    region,
                    vertexSorting,
                    buffers
            );

            // Create translucency point of view
            TranslucencyPointOfView translucencyPointOfView = TranslucencyPointOfView.of(cameraPos, sectionNode);

            // Create and return the compiled mesh
            CompiledSectionMesh compiledMesh = new CompiledSectionMesh(translucencyPointOfView, results);

            return compiledMesh;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static SectionCompiler getSectionCompiler() {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            LevelRenderer levelRenderer = minecraft.levelRenderer;

            Options options = minecraft.options;
            boolean ambientOcclusion = options.ambientOcclusion().get();
            boolean cutoutLeaves = options.cutoutLeaves().get();


            return new SectionCompiler(
                    ambientOcclusion,
                    cutoutLeaves,
                    minecraft.getModelManager().getBlockStateModelSet(),
                    minecraft.getModelManager().getFluidStateModelSet(),
                    minecraft.getBlockColors(),
                    minecraft.getBlockEntityRenderDispatcher()
            );

        } catch (Exception e) {
            return null;
        }
    }

}
