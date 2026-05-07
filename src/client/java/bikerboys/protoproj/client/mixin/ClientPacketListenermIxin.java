package bikerboys.protoproj.client.mixin;

import bikerboys.protoproj.client.*;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.network.*;
import net.minecraft.network.protocol.*;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.level.chunk.*;
import org.spongepowered.asm.mixin.*;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenermIxin extends ClientCommonPacketListenerImpl implements ClientGamePacketListener, TickablePacketListener {


    protected ClientPacketListenermIxin(Minecraft minecraft, Connection connection, CommonListenerCookie cookie) {
        super(minecraft, connection, cookie);
    }
}
