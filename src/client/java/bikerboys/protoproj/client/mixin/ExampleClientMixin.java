package bikerboys.protoproj.client.mixin;

import bikerboys.protoproj.*;
import bikerboys.protoproj.client.*;
import bikerboys.protoproj.client.rendering.*;
import com.llamalad7.mixinextras.sugar.*;
import com.mojang.blaze3d.buffers.*;
import com.mojang.blaze3d.resource.*;
import com.mojang.blaze3d.systems.*;
import com.mojang.blaze3d.textures.*;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.*;
import net.minecraft.client.renderer.state.level.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.core.*;
import net.minecraft.util.*;
import net.minecraft.util.profiling.*;
import net.minecraft.world.phys.*;
import org.joml.*;
import org.jspecify.annotations.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.lang.Math;
import java.util.*;

@Debug(export = true)
@Mixin(LevelRenderer.class)
public abstract class ExampleClientMixin {


	@Unique
	private ChunkSectionsToRender customChunkSectionsToRender;

	@Shadow
	@Final
	private ObjectArrayList<SectionRenderDispatcher.RenderSection> visibleSections;

	@Unique
	private ObjectArrayList<SectionRenderDispatcher.RenderSection> fakeVisibleSections = new ObjectArrayList<>();

	@Unique
	private Map<SectionRenderDispatcher.RenderSection, BlockPos> sectionPositionOverrides = new java.util.WeakHashMap<>();

	@Shadow
	private @Nullable SectionRenderDispatcher sectionRenderDispatcher;

	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow
	private @Nullable ViewArea viewArea;

	@Shadow
	private @Nullable GpuSampler chunkLayerSampler;

	@Inject(
			method = "prepareChunkRenders",
			at = @At("HEAD"),
			cancellable = false
	)
	private void injectCustomRenderSections(Matrix4fc modelViewMatrix, CallbackInfoReturnable<ChunkSectionsToRender> cir) {
		fakeVisibleSections.clear();
		sectionPositionOverrides.clear();

		if (PrototypingProjectClient.renderExtra) {
			BlockPos centerPos = PrototypingProjectClient.positionToRender;

			if (centerPos != null) {
				int centerChunkX = centerPos.getX() >> 4; // Convert to chunk coordinates
				int centerChunkZ = centerPos.getZ() >> 4;

				// Iterate through all sections within 7 chunks radius
				for (int chunkX = centerChunkX - 7; chunkX <= centerChunkX + 7; chunkX++) {
					for (int chunkZ = centerChunkZ - 7; chunkZ <= centerChunkZ + 7; chunkZ++) {
						for (int sectionY = 0; sectionY < 24; sectionY++) { // 24 sections top to bottom in modern MC
							BlockPos sectionPos = new BlockPos(chunkX << 4, sectionY << 4, chunkZ << 4);
							if (viewArea != null) {
								SectionRenderDispatcher.RenderSection renderSection = viewArea.getRenderSectionAt(sectionPos);

								if (renderSection != null) {
									this.fakeVisibleSections.add(renderSection);

									// Calculate relative position offset from center
									BlockPos relativePos = new BlockPos(
											sectionPos.getX() - centerPos.getX(),
											sectionPos.getY() - centerPos.getY(),
											sectionPos.getZ() - centerPos.getZ()
									);

									this.sectionPositionOverrides.put(
											renderSection,
											relativePos
									);

								} else {
									System.out.println("[PrototypingProject] Null render section at: " + sectionPos);
								}
							}
						}
					}
				}
			}
		}
	}

	@Inject(method = "extractLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;prepareChunkRenders(Lorg/joml/Matrix4fc;)Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;"))
	private void insertExtraction(DeltaTracker deltaTracker, Camera camera, float deltaPartialTick, CallbackInfo ci, @Local(name = "modelViewMatrix") Matrix4f modelViewMatrix) {
		customChunkSectionsToRender = prepareCustomChunkRenders(modelViewMatrix);
	}

	@Inject(method = "lambda$addMainPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;renderGroup(Lnet/minecraft/client/renderer/chunk/ChunkSectionLayerGroup;Lcom/mojang/blaze3d/textures/GpuSampler;)V", ordinal = 0))
	private void renderCustomOpaqueChunkSections(GpuBufferSlice terrainFog, LevelRenderState levelRenderState, ProfilerFiller profiler, ChunkSectionsToRender chunkSectionsToRender, ResourceHandle entityOutlineTarget, ResourceHandle translucentTarget, ResourceHandle mainTarget, ResourceHandle itemEntityTarget, ResourceHandle particleTarget, boolean renderOutline, Matrix4fc modelViewMatrix, CallbackInfo ci) {
        if (this.chunkLayerSampler != null) {
			GL11.glCullFace(GL11.GL_FRONT);
            customChunkSectionsToRender.renderGroup(ChunkSectionLayerGroup.OPAQUE, this.chunkLayerSampler);
			GL11.glCullFace(GL11.GL_BACK);
        }
    }

	@Inject(method = "lambda$addMainPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;renderGroup(Lnet/minecraft/client/renderer/chunk/ChunkSectionLayerGroup;Lcom/mojang/blaze3d/textures/GpuSampler;)V", ordinal = 1))
	private void renderCustomTranslucentChunkSections(GpuBufferSlice terrainFog, LevelRenderState levelRenderState, ProfilerFiller profiler, ChunkSectionsToRender chunkSectionsToRender, ResourceHandle entityOutlineTarget, ResourceHandle translucentTarget, ResourceHandle mainTarget, ResourceHandle itemEntityTarget, ResourceHandle particleTarget, boolean renderOutline, Matrix4fc modelViewMatrix, CallbackInfo ci) {
        if (this.chunkLayerSampler != null) {
			GL11.glCullFace(GL11.GL_FRONT);
            customChunkSectionsToRender.renderGroup(ChunkSectionLayerGroup.TRANSLUCENT, this.chunkLayerSampler);
			GL11.glCullFace(GL11.GL_BACK);
        }
    }

	private ChunkSectionsToRender prepareCustomChunkRenders(final Matrix4fc modelViewMatrix) {
		ObjectListIterator<SectionRenderDispatcher.RenderSection> fakeSectionsIterator = fakeVisibleSections.listIterator(0);

		EnumMap<ChunkSectionLayer, Int2ObjectOpenHashMap<List<RenderPass.Draw<GpuBufferSlice[]>>>> drawGroups = new EnumMap(ChunkSectionLayer.class);
		int largestIndexCount = 0;

		for (ChunkSectionLayer layer : ChunkSectionLayer.values()) {
			drawGroups.put(layer, new Int2ObjectOpenHashMap());
		}

		List<DynamicUniforms.ChunkSectionInfo> sectionInfos = new ArrayList();
		GpuTextureView blockAtlas = this.minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).getTextureView();
		int textureAtlasWidth = blockAtlas.getWidth(0);
		int textureAtlasHeight = blockAtlas.getHeight(0);
		if (this.sectionRenderDispatcher != null) {
			this.sectionRenderDispatcher.lock();

			try {
				Zone ignored = Profiler.get().zone("Upload Global Buffers");

				try {
					this.sectionRenderDispatcher.uploadGlobalGeomBuffersToGPU();
				} catch (Throwable var35) {
					if (ignored != null) {
						try {
							ignored.close();
						} catch (Throwable var34) {
							var35.addSuppressed(var34);
						}
					}

					throw var35;
				}

				if (ignored != null) {
					ignored.close();
				}

				// Process fake sections first



				while (fakeSectionsIterator.hasNext()) {
					SectionRenderDispatcher.RenderSection section = fakeSectionsIterator.next();
					SectionMesh sectionMesh = section.getSectionMesh();

					Vector3f offsetVec = new Vector3f(
							PrototypingProjectClient.whereToRenderSection.getX(),
							PrototypingProjectClient.whereToRenderSection.getY(),
							PrototypingProjectClient.whereToRenderSection.getZ()
					);

					Vec3 cameraPos = minecraft.gameRenderer.getMainCamera().position();



					Matrix4f rotated = new Matrix4f(modelViewMatrix);
					rotated.translate(0, (float) (-2.0f * cameraPos.y) - offsetVec.y, 0);

					FakeChunkRendering.modifyMatrix(rotated);


					BlockPos renderOffset = section.getRenderOrigin()
							.offset(
									(int) offsetVec.x,
									0,
									(int) offsetVec.z
							);


					long now = Util.getMillis();
					int uboIndex = -1;

					for (ChunkSectionLayer layer : ChunkSectionLayer.values()) {
						SectionMesh.SectionDraw draw = sectionMesh.getSectionDraw(layer);
						SectionRenderDispatcher.RenderSectionBufferSlice slice = this.sectionRenderDispatcher.getRenderSectionSlice(sectionMesh, layer);

						if (slice != null && draw != null && (!draw.hasCustomIndexBuffer() || slice.indexBuffer() != null)) {
							if (uboIndex == -1) {
								uboIndex = sectionInfos.size();

                                rotated.scale(1, -1, 1);


								sectionInfos.add(
										new DynamicUniforms.ChunkSectionInfo(
												rotated,
												renderOffset.getX(),
                                                (renderOffset.getY()),
												renderOffset.getZ(),
												section.getVisibility(now),
												textureAtlasWidth,
												textureAtlasHeight
										)
								);
							}

							int combinedHash = 173;
							VertexFormat vertexFormat = layer.pipeline().getVertexFormat();
							GpuBuffer vertexBuffer = slice.vertexBuffer();
							if (layer != ChunkSectionLayer.TRANSLUCENT) {
								combinedHash = 31 * combinedHash + vertexBuffer.hashCode();
							}

							int firstIndex = 0;
							GpuBuffer indexBuffer;
							VertexFormat.IndexType indexType;
							if (!draw.hasCustomIndexBuffer()) {
								if (draw.indexCount() > largestIndexCount) {
									largestIndexCount = draw.indexCount();
								}

								indexBuffer = null;
								indexType = null;
							} else {
								indexBuffer = slice.indexBuffer();
								indexType = draw.indexType();
								if (layer != ChunkSectionLayer.TRANSLUCENT) {
									combinedHash = 31 * combinedHash + indexBuffer.hashCode();
									combinedHash = 31 * combinedHash + indexType.hashCode();
								}

								firstIndex = (int) (slice.indexBufferOffset() / indexType.bytes);
							}

							int finalUboIndex = uboIndex;
							int baseVertex = (int) (slice.vertexBufferOffset() / vertexFormat.getVertexSize());
							List<RenderPass.Draw<GpuBufferSlice[]>> draws = (List<RenderPass.Draw<GpuBufferSlice[]>>) ((Int2ObjectOpenHashMap) drawGroups.get(layer))
									.computeIfAbsent(combinedHash, (Int2ObjectFunction<? extends List<RenderPass.Draw<GpuBufferSlice[]>>>) (var0 -> new ArrayList()));
							draws.add(
									new RenderPass.Draw<>(
											0,
											vertexBuffer,
											indexBuffer,
											indexType,
											firstIndex,
											draw.indexCount(),
											baseVertex,
											(sectionUbos, uploader) -> uploader.upload("ChunkSection", sectionUbos[finalUboIndex])
									)
							);
						}
					}
				}


			} finally {
				this.sectionRenderDispatcher.unlock();
			}


		}

		GpuBufferSlice[] chunkSectionInfos = RenderSystem.getDynamicUniforms()
				.writeChunkSections((DynamicUniforms.ChunkSectionInfo[]) sectionInfos.toArray(new DynamicUniforms.ChunkSectionInfo[0]));
		return new ChunkSectionsToRender(blockAtlas, drawGroups, largestIndexCount, chunkSectionInfos);
	}

}