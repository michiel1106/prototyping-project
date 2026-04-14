package bikerboys.protoproj.client;

import bikerboys.protoproj.client.rendering.*;
import bikerboys.protoproj.client.rendering.entity.*;
import bikerboys.protoproj.client.screen.*;
import bikerboys.protoproj.entity.*;
import com.mojang.authlib.minecraft.client.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.network.chat.*;
import net.minecraft.world.*;
import net.minecraft.world.level.block.*;

public class PrototypingProjectClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {


		UseBlockCallback.EVENT.register(((player, level, interactionHand, blockHitResult) -> {

			Minecraft.getInstance().execute(() -> {
				if (level.getBlockState(blockHitResult.getBlockPos()).getBlock() == Blocks.GRASS_BLOCK) {
					if (player.isCrouching()) {
						Minecraft.getInstance().setScreen(new ViewingScreen(Component.literal("")));
					}
				}
			});
			return InteractionResult.PASS;
		}));


		ModEntityModelLayers.registerModelLayers();
		EntityRenderers.register(ModEntities.SHAPE_ENTITY, ShapeEntityRenderer::new);
	}
}