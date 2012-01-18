package me.bitfreeze.OreLess;

import java.util.Arrays;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.WorldListener;

public class OreLessWorldListener extends WorldListener {
	OreLess plugin;

	OreLessWorldListener(OreLess plugin) {
		this.plugin = plugin;
	}

	public void onChunkPopulate(ChunkPopulateEvent event) {
		String worldName = event.getWorld().getName();
		// Hard-coded, just for testing yet
		if (worldName.equalsIgnoreCase("vast")) {
			Chunk chunk = event.getChunk();
			removeOres(event.getWorld(), chunk.getX(), chunk.getZ());
		}
	}

	@SuppressWarnings("static-access")
	private void removeOres(World world, int cx, int cz) {
		Chunk 			chunk;
		ChunkSnapshot 	chunkSnapshot;
		int 			blocksReplaced		= 0;
		int 			blockTypeId;

		if (!world.isChunkLoaded(cx, cz)) {
			if (!world.isChunkLoaded(cx, cz)) {
				world.loadChunk(cx, cz);
			}
		}
		chunk = world.getChunkAt(cx, cz);
		chunkSnapshot = chunk.getChunkSnapshot();

		// For performance, build a vector that defines all the Y positions to process.
		OreLessItem[] replacements = plugin.parameters.getParameters(world);
		boolean process[] = new boolean[128];
		Arrays.fill(process, false);

		for(OreLessItem replacement : replacements) {
			Arrays.fill(process, replacement.fromHeight, replacement.thruHeight + 1, true);
		}

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 128; y++) {
					if (process[y]) {
						// Check if the block at these coordinates is unwanted.
						// In this starting version, coal/iron/gold/diamond/lapis.
						blockTypeId = chunkSnapshot.getBlockTypeId(x, y, z);
						for(OreLessItem replacement : replacements) {
							if ((replacement.fromHeight <= y) && (replacement.thruHeight <= y)) {
								if (blockTypeId == replacement.typeId) {
									// Unwanted  block found. Replace it by the given block.
									chunk.getBlock(x, y, z).setTypeId(replacement.replaceId);
									blocksReplaced++;
									break;
								}
							}
						}
					}
				}
			}
		}
		if (blocksReplaced > 0) {
			plugin.logger.info(OreLess.logPrefix + blocksReplaced + " blocks replaced at chunk (" + cx + "," + cz + ").");
		}
	}
}
