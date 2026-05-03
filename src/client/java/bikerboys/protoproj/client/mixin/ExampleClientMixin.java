package bikerboys.protoproj.client.mixin;

import bikerboys.protoproj.client.*;
import com.mojang.blaze3d.buffers.*;
import com.mojang.blaze3d.systems.*;
import com.mojang.blaze3d.textures.*;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.core.*;
import net.minecraft.util.*;
import net.minecraft.util.profiling.*;
import org.joml.*;
import org.jspecify.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.lang.Math;
import java.util.*;

@Debug(export = true)
@Mixin(LevelRenderer.class)
public abstract class ExampleClientMixin {

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

	@Inject(
			method = "prepareChunkRenders",
			at = @At("HEAD"),
			cancellable = false
	)
	private void injectCustomRenderSections(Matrix4fc modelViewMatrix, CallbackInfoReturnable<ChunkSectionsToRender> cir) {
		fakeVisibleSections.clear();
		sectionPositionOverrides.clear();


		if (PrototypingProjectClient.renderExtra) {
			for (SectionRenderDispatcher.RenderSection visibleSection : visibleSections) {
				fakeVisibleSections.add(visibleSection);
			}

			/*
			SectionRenderDispatcher.RenderSection customSection = createCustomRenderSection();

			if (customSection != null) {

				this.fakeVisibleSections.add(customSection);

				this.sectionPositionOverrides.put(
						customSection,
						PrototypingProjectClient.whereToRenderSection
				);
			}

			 */
		}
	}

	private SectionRenderDispatcher.@Nullable RenderSection createCustomRenderSection() {
		return viewArea != null ? viewArea.getRenderSectionAt(PrototypingProjectClient.positionToRender) : null;
	}

	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public ChunkSectionsToRender prepareChunkRenders(final Matrix4fc modelViewMatrix) {
		ObjectListIterator<SectionRenderDispatcher.RenderSection> iterator = this.visibleSections.listIterator(0);

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

					Vec3i rotateOffset = PrototypingProjectClient.rotateOffset;


					Quaternionf quat = new Quaternionf()
							.rotateX((float) Math.toRadians(rotateOffset.getX()))
							.rotateY((float) Math.toRadians(rotateOffset.getY()))
							.rotateZ((float) Math.toRadians(rotateOffset.getZ()));



					BlockPos renderOffset = section.getRenderOrigin()
							.offset(
									PrototypingProjectClient.whereToRenderSection.getX(),
									PrototypingProjectClient.whereToRenderSection.getY(),
									PrototypingProjectClient.whereToRenderSection.getZ()
							);


					long now = Util.getMillis();
					int uboIndex = -1;

					for (ChunkSectionLayer layer : ChunkSectionLayer.values()) {
						SectionMesh.SectionDraw draw = sectionMesh.getSectionDraw(layer);
						SectionRenderDispatcher.RenderSectionBufferSlice slice = this.sectionRenderDispatcher.getRenderSectionSlice(sectionMesh, layer);

						if (slice != null && draw != null && (!draw.hasCustomIndexBuffer() || slice.indexBuffer() != null)) {
							if (uboIndex == -1) {
								uboIndex = sectionInfos.size();

								Matrix4f rotated = new Matrix4f();


								modelViewMatrix.rotate(quat, rotated);



								sectionInfos.add(
										new DynamicUniforms.ChunkSectionInfo(
												rotated,
												renderOffset.getX(),
												renderOffset.getY(),
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

								firstIndex = (int)(slice.indexBufferOffset() / indexType.bytes);
							}

							int finalUboIndex = uboIndex;
							int baseVertex = (int)(slice.vertexBufferOffset() / vertexFormat.getVertexSize());
							List<RenderPass.Draw<GpuBufferSlice[]>> draws = (List<RenderPass.Draw<GpuBufferSlice[]>>) ((Int2ObjectOpenHashMap)drawGroups.get(layer))
									.computeIfAbsent(combinedHash, (Int2ObjectFunction<? extends List<RenderPass.Draw<GpuBufferSlice[]>>>)(var0 -> new ArrayList()));
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

				// Process normal visible sections
				while (iterator.hasNext()) {
					SectionRenderDispatcher.RenderSection section = iterator.next();
					SectionMesh sectionMesh = section.getSectionMesh();
					BlockPos renderOffset = section.getRenderOrigin();
					long now = Util.getMillis();
					int uboIndex = -1;

					for (ChunkSectionLayer layer : ChunkSectionLayer.values()) {
						SectionMesh.SectionDraw draw = sectionMesh.getSectionDraw(layer);
						SectionRenderDispatcher.RenderSectionBufferSlice slice = this.sectionRenderDispatcher.getRenderSectionSlice(sectionMesh, layer);
						if (slice != null && draw != null && (!draw.hasCustomIndexBuffer() || slice.indexBuffer() != null)) {
							if (uboIndex == -1) {
								uboIndex = sectionInfos.size();

								sectionInfos.add(
										new DynamicUniforms.ChunkSectionInfo(
												modelViewMatrix,
												renderOffset.getX(),
												renderOffset.getY(),
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

								firstIndex = (int)(slice.indexBufferOffset() / indexType.bytes);
							}

							int finalUboIndex = uboIndex;
							int baseVertex = (int)(slice.vertexBufferOffset() / vertexFormat.getVertexSize());
							List<RenderPass.Draw<GpuBufferSlice[]>> draws = (List<RenderPass.Draw<GpuBufferSlice[]>>) ((Int2ObjectOpenHashMap)drawGroups.get(layer))
									.computeIfAbsent(combinedHash, (Int2ObjectFunction<? extends List<RenderPass.Draw<GpuBufferSlice[]>>>)(var0 -> new ArrayList()));
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
				.writeChunkSections((DynamicUniforms.ChunkSectionInfo[])sectionInfos.toArray(new DynamicUniforms.ChunkSectionInfo[0]));
		return new ChunkSectionsToRender(blockAtlas, drawGroups, largestIndexCount, chunkSectionInfos);
	}


}