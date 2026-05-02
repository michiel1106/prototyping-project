package bikerboys.protoproj.client;

import bikerboys.protoproj.client.rendering.*;
import bikerboys.protoproj.client.rendering.entity.*;
import bikerboys.protoproj.entity.*;
import com.mojang.blaze3d.platform.*;
import com.mojang.brigadier.arguments.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.client.keymapping.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.hud.*;
import net.fabricmc.fabric.impl.client.rendering.hud.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.core.*;
import net.minecraft.resources.*;
import org.lwjgl.glfw.*;

public class PrototypingProjectClient implements ClientModInitializer {
	KeyMapping sendToChatKey = KeyMappingHelper.registerKeyMapping(
			new KeyMapping(
					"key.protoproj.toggle_debug", // The translation key for the key mapping.
					InputConstants.Type.KEYSYM, // // The type of the keybinding; KEYSYM for keyboard, MOUSE for mouse.
					GLFW.GLFW_KEY_J, // The GLFW keycode of the key.
					KeyMapping.Category.DEBUG // The category of the mapping.
			));

	public static boolean renderExtra = false;

	public static BlockPos positionToRender = BlockPos.ZERO;
	public static BlockPos whereToRenderSection = BlockPos.ZERO;
	public static Vec3i rotateOffset = BlockPos.ZERO;


	@Override
	public void onInitializeClient() {



		ClientTickEvents.START_CLIENT_TICK.register((client) -> {
			if (sendToChatKey.consumeClick()) {
				renderExtra = !renderExtra;
			}
		});

		HudElementRegistryImpl.attachElementAfter(VanillaHudElements.OVERLAY_MESSAGE, Identifier.parse("protoproj:renderhud"), ((graphics, deltaTracker) -> {
			String textThing = renderExtra ? "Enabled!" : "Disabled!";

			Font font = Minecraft.getInstance().font;


			graphics.text(
					font,
					textThing,
					6,
					6,
					0xFFFFFFFF
			);

		}));



		ClientCommandRegistrationCallback.EVENT.register(((commandDispatcher, commandBuildContext) -> {
			commandDispatcher.register(ClientCommands.literal("positiontorender")
					.then(ClientCommands.argument("x", IntegerArgumentType.integer())
							.then(ClientCommands.argument("y", IntegerArgumentType.integer())
									.then(ClientCommands.argument("z", IntegerArgumentType.integer())
											.executes(context -> {
												int x = IntegerArgumentType.getInteger(context, "x");
												int y = IntegerArgumentType.getInteger(context, "y");
												int z = IntegerArgumentType.getInteger(context, "z");

												positionToRender = new BlockPos(x, y, z);

												return 1;
											})))));
		}));

		ClientCommandRegistrationCallback.EVENT.register(((commandDispatcher, commandBuildContext) -> {
			commandDispatcher.register(ClientCommands.literal("rotateoffset")
					.then(ClientCommands.argument("x", IntegerArgumentType.integer())
							.then(ClientCommands.argument("y", IntegerArgumentType.integer())
									.then(ClientCommands.argument("z", IntegerArgumentType.integer())
											.executes(context -> {
												int x = IntegerArgumentType.getInteger(context, "x");
												int y = IntegerArgumentType.getInteger(context, "y");
												int z = IntegerArgumentType.getInteger(context, "z");

												rotateOffset = new BlockPos(x, y, z);

												return 1;
											})))));
		}));


		ClientCommandRegistrationCallback.EVENT.register(((commandDispatcher, commandBuildContext) -> {
			commandDispatcher.register(ClientCommands.literal("wheretorendersection")
					.then(ClientCommands.argument("x", IntegerArgumentType.integer())
							.then(ClientCommands.argument("y", IntegerArgumentType.integer())
									.then(ClientCommands.argument("z", IntegerArgumentType.integer())
											.executes(context -> {
												int x = IntegerArgumentType.getInteger(context, "x");
												int y = IntegerArgumentType.getInteger(context, "y");
												int z = IntegerArgumentType.getInteger(context, "z");

												whereToRenderSection = new BlockPos(x, y, z);

												return 1;
											})))));
		}));

		ModEntityModelLayers.registerModelLayers();
		EntityRenderers.register(ModEntities.SHAPE_ENTITY, ShapeEntityRenderer::new);
	}
}