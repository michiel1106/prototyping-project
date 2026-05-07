package bikerboys.protoproj.client.mixin;

import bikerboys.protoproj.client.*;
import bikerboys.protoproj.client.rendering.*;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.network.*;
import net.minecraft.network.protocol.*;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.level.chunk.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenermIxin extends ClientCommonPacketListenerImpl implements ClientGamePacketListener, TickablePacketListener {


    protected ClientPacketListenermIxin(Minecraft minecraft, Connection connection, CommonListenerCookie cookie) {
        super(minecraft, connection, cookie);
    }


    @Inject(method = "handleLevelChunkWithLight", at = @At("HEAD"))
    private void interceptChunk(ClientboundLevelChunkWithLightPacket packet, CallbackInfo ci) {
        int chunkX = packet.getX();
        int chunkZ = packet.getZ();

        // Cache the packet for later use
        FakeChunkRendering.cache.cachePacket(chunkX, chunkZ, packet);
    }

}
