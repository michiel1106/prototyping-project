package bikerboys.protoproj.client.rendering;

import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.chunk.*;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.chunk.LevelChunk;

public class CustomRenderRegionCache {
    private final Long2ObjectMap<SectionCopy> sectionCopyCache = new Long2ObjectOpenHashMap<>();
    private final Long2ObjectMap<ClientboundLevelChunkWithLightPacket> packetCache = new Long2ObjectOpenHashMap<>();

    public void cachePacket(int chunkX, int chunkZ, ClientboundLevelChunkWithLightPacket packet) {
        long chunkKey = ChunkPos.pack(chunkX, chunkZ);
        packetCache.put(chunkKey, packet);
    }


    public RenderSectionRegion createRegion(final ClientLevel level, final long sectionNode) {
        int sectionX = SectionPos.x(sectionNode);
        int sectionY = SectionPos.y(sectionNode);
        int sectionZ = SectionPos.z(sectionNode);
        int minSectionX = sectionX - 1;
        int minSectionY = sectionY - 1;
        int minSectionZ = sectionZ - 1;
        int maxSectionX = sectionX + 1;
        int maxSectionY = sectionY + 1;
        int maxSectionZ = sectionZ + 1;
        SectionCopy[] regionSections = new SectionCopy[27];

        for (int regionSectionZ = minSectionZ; regionSectionZ <= maxSectionZ; regionSectionZ++) {
            for (int regionSectionY = minSectionY; regionSectionY <= maxSectionY; regionSectionY++) {
                for (int regionSectionX = minSectionX; regionSectionX <= maxSectionX; regionSectionX++) {
                    int index = RenderSectionRegion.index(minSectionX, minSectionY, minSectionZ, regionSectionX, regionSectionY, regionSectionZ);
                    regionSections[index] = this.getSectionDataCopy(level, regionSectionX, regionSectionY, regionSectionZ);
                }
            }
        }

        return new RenderSectionRegion(level, minSectionX, minSectionY, minSectionZ, regionSections);
    }


    private SectionCopy getSectionDataCopy(final Level level, final int sectionX, final int sectionY, final int sectionZ) {
        return this.sectionCopyCache.computeIfAbsent(SectionPos.asLong(sectionX, sectionY, sectionZ),
                (Long2ObjectFunction<? extends SectionCopy>)(k -> {
                    LevelChunk chunk = level.getChunk(sectionX, sectionZ);
                    long chunkKey = ChunkPos.pack(sectionX, sectionZ);

                    // Get cached packet data if available
                    ClientboundLevelChunkWithLightPacket packet = packetCache.get(chunkKey);
                    if (packet != null) {
                        chunk.replaceWithPacketData(
                                packet.getChunkData().getReadBuffer(),
                                packet.getChunkData().getHeightmaps(),
                                packet.getChunkData().getBlockEntitiesTagsConsumer(sectionX, sectionZ)
                        );
                        // Optionally clear cached packet after use
                        packetCache.remove(chunkKey);
                    }

                    return new SectionCopy(chunk, chunk.getSectionIndexFromSectionY(sectionY));
                }));
    }

    /*

    	private void updateLevelChunk(final int x, final int z, final ClientboundLevelChunkPacketData chunkData) {
		this.level.getChunkSource().replaceWithPacketData(x, z, chunkData.getReadBuffer(), chunkData.getHeightmaps(), chunkData.getBlockEntitiesTagsConsumer(x, z));
	}

     */


}



