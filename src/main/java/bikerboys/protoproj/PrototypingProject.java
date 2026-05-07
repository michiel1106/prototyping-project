package bikerboys.protoproj;

import com.mojang.brigadier.builder.*;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.*;
import net.minecraft.commands.*;
import net.minecraft.commands.arguments.coordinates.*;
import net.minecraft.core.*;
import net.minecraft.server.level.*;
import net.minecraft.server.network.*;
import net.minecraft.world.level.chunk.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrototypingProject implements ModInitializer {
	public static final String MOD_ID = "protoproj";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		CommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext, commandSelection) -> {
			commandDispatcher.register(
					Commands.literal("sendChunks")
							.then(Commands.argument("blockpos", BlockPosArgument.blockPos())
									.executes(context -> {

										BlockPos blockPos = BlockPosArgument.getBlockPos(context, "blockpos");
										ServerPlayer player = context.getSource().getPlayer();

										if (player != null) {
											ServerLevel level = context.getSource().getLevel();

											int centerChunkX = blockPos.getX() >> 4;
											int centerChunkZ = blockPos.getZ() >> 4;

											for (int chunkX = centerChunkX - 7; chunkX <= centerChunkX + 7; chunkX++) {
												for (int chunkZ = centerChunkZ - 7; chunkZ <= centerChunkZ + 7; chunkZ++) {

													LevelChunk chunk = level.getChunk(chunkX, chunkZ);

													PlayerChunkSender.sendChunk(player.connection, level, chunk);
												}
											}
										}

										return 1;
									}))
			);
		});


	}
}