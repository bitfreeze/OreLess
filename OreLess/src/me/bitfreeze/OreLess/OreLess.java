package me.bitfreeze.OreLess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
import java.util.logging.Logger;

//import org.bukkit.Chunk;
//import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
//import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
//import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
//import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class OreLess extends JavaPlugin implements Listener {
	public final OreLess plugin = this;
	public final Logger logger = Logger.getLogger("Minecraft");
	public String logPrefix;
	public String version;
	public static PermissionHandler permissionHandler;
	public static boolean hasPermissions = false;
	private static HashMap<String, OreLessWorld> worldRules =
			new HashMap<String, OreLessWorld>();
	FileConfiguration messages;
	FileConfiguration config;
	public int defaultBlockTo;
	public String defaultHeightRange;
	public boolean defaultFixLighting;
	public HashMap<String, String> permNodes = new HashMap<String, String>();
	public boolean opGrantAll;

	public enum Severity {
		INFO, WARNING, SEVERE
	};

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is now disabled.");
	}

	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is now enabled.");
		logPrefix = "[" + pdfFile.getName() + "] ";
		setupPermissions();
		//loadConfig();

		//PluginManager pm = getServer().getPluginManager();
		//pm.registerEvents(this, this);
	}

	private void loadConfig() {
		try {
			// Read settings from config.yml
			getDataFolder().mkdir();
			File messagesFile = new File(getDataFolder(), "messages.yml");
			if (!messagesFile.exists()) {
				copyResource("defaults/messages.yml", messagesFile);
			}

			File configFile = new File(getDataFolder(), "config.yml");
			if (!messagesFile.exists()) {
				copyResource("defaults/config.yml", configFile);
			}

			opGrantAll = config.getBoolean("op-grant-all", false);

			// Read the permission nodes or define default nodes for missing ones
			permNodes.clear();
			permNodes.put("messages-receive", config.getString("permissions.messages-receive", "oreless.messages"));
			permNodes.put("command-help", config.getString("permissions.command-help", "oreless.help"));
			//permNodes.put("command-apply", config.getString("permissions.command-apply", "oreless.apply"));
			permNodes.put("command-reload", config.getString("permissions.command-reload", "oreless.reload"));

			// Default block id for replacement
			String blockName = config.getString("default.block-to", "AIR");
			Material mat = Material.matchMaterial(blockName);
			if (mat == null) {
				defaultBlockTo = Material.AIR.getId();
				log(plugin.logPrefix + "Invalid block-to \"" + blockName + "\". Using default (" + Material.getMaterial(defaultBlockTo).toString() + ").", Severity.WARNING);
			} else {
				defaultBlockTo = mat.getId();
			}

			// Default height range
			String heightRange = config.getString("default.height", "0-127");
			boolean validHeight = false;
			if (heightRange.matches("[0-9]*-[0-9]*")) {
				try {
					String[] heightValues = heightRange.split("-");
					int minHeight = Integer.parseInt(heightValues[0]);
					int maxHeight = Integer.parseInt(heightValues[1]);
					if (minHeight >= 0 && maxHeight <= 127 && minHeight <= maxHeight) {
						validHeight = true;
					}
				} catch(Exception e2) { }
			}
			if (!validHeight) {
				defaultHeightRange = "0-127";
				log(logPrefix + "Invalid height setting \"" + heightRange + "\". Using default (" + heightRange + ").", Severity.WARNING);
			}

			// Default flag: whether or not to fix-lighting in changed chunks
			defaultFixLighting = config.getBoolean("default.fix-lighting", false);

			// Load rules
//			worldRules.clear();
//			if (config.contains("rules")) {
//				// Load the internal structure of replacement rules
//				List<Map<String, Object>> rules = config.getMapList("rules");
//				for(Map<String, Object> rule : rules) {
//					
//				}
//			} else {
//				config.set("rules", null);
//			}

			// Worlds vs. Rules
//			if (config.contains("worlds")) {
//				config.set("ignore-worlds", null);
//			} else {
//			}
		} catch(Exception e1) {
			e1.printStackTrace();
		}
	}

	private void copyResource(String resourceFileName, File targetFile) {
		try {
			InputStream in = getResource(resourceFileName);
			OutputStream out = new FileOutputStream(targetFile);
			byte[] buf = new byte[1024];
			int len;
			while((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		} catch(Exception e1) {
			e1.printStackTrace();
		}
	}

	private void log(String message, Severity severity, String[] parameters) {
		for (int i = 0; i < parameters.length; i++) {
			message.replaceAll("%" + i, parameters[i]);
		}
		log(message, severity);
	}

	private void log(String message, Severity severity) {
		switch(severity) {
		case INFO:
			logger.info(message);
			break;
		case WARNING:
			logger.warning(message);
			break;
		case SEVERE:
			logger.severe(message);
		}
		if (config.getBoolean("broadcast-messages")) {
			Player[] players = getServer().getOnlinePlayers();
			for (Player player : players) {
				if ((hasPermissions && player.hasPermission(permNodes.get("messages-receive"))) ||
						(opGrantAll && player.isOp())) {
					try {
						player.sendMessage(message);
					} catch(Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	@SuppressWarnings("static-access")
	private void setupPermissions() {
		PluginManager pm = this.getServer().getPluginManager();
		Plugin permissionsPlugin = pm.getPlugin("Permissions");
		if (this.permissionHandler == null) {
			if (permissionsPlugin != null) {
				this.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
				hasPermissions = true;
				logger.info(logPrefix + "Permissions support enabled.");
			} else {
				logger.info(logPrefix + "Permission system not detected, defaulting to OP.");
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;

		//if(label.equalsIgnoreCase("oreless") || label.equalsIgnoreCase("ol")) {
			if((args.length == 1) && (args[0].equalsIgnoreCase("reload"))) {
				if( (hasPermissions && permissionHandler.has(player, config.getString("permissions.reload")))
						|| (opGrantAll && player.isOp()) ) {
					loadConfig();
					logger.info(logPrefix + "Configurations reloaded from files.");
					player.sendMessage(getDescription().getName() + " configuration reloaded from files.");
					return true;
				}
			}
		//}

		return false;
	}

	/*
	@EventHandler
	public void chunkPopulated(ChunkPopulateEvent event) {
		String worldName = event.getWorld().getName();
		// Hard-coded, just for testing yet
		if (worldName.equalsIgnoreCase("vast")) {
			Chunk chunk = event.getChunk();
			removeOres(event.getWorld(), chunk.getX(), chunk.getZ());
		}
	}

	private void removeOres(World world, int cx, int cz) {
		if(worldRules.containsKey(world.getName())) {
			int replacementCount = 0;
			OreLessRule[] rules = worldRules.get(world.getName());
			Chunk chunk = world.getChunkAt(cx, cz);
			ChunkSnapshot cs = chunk.getChunkSnapshot();
			int currentTypeId;
			int newTypeId;
			for (int y = 0; y <= 127; y++) {
				for (int x = 0; x < 16; x++) {
					for (int z = 0; z < 16; z++) {
						currentTypeId = cs.getBlockTypeId(x, y, z);
						if (rules.containsKey(currentTypeId)) {
							newTypeId = rules.get(currentTypeId);
							Block block = world.getBlockAt(cx * 16 + x, y, cz * 16 + z);
							block.setTypeId(newTypeId);
							replacementCount++;
						}
					}
				}
			}

			if (replacementCount > 0) {
				// Broadcast message
				if (replacementCount == 1) {
					// ##Format & send message "one-block-replaced"
				} else {
					// ##Format & send message "many-blocks-replaced"
				}

				// Fix lighting, if possible and harmless
				try {
					net.minecraft.server.Chunk rawchunk = ((CraftChunk)chunk).getHandle();
		            if (world.isChunkLoaded(cx - 1, cz - 1) &&
		                world.isChunkLoaded(cx - 1, cz) &&
		                world.isChunkLoaded(cx - 1, cz + 1) &&
		                world.isChunkLoaded(cx, cz - 1) &&
		                world.isChunkLoaded(cx, cz + 1) &&
		                world.isChunkLoaded(cx + 1, cz - 1) &&
		                world.isChunkLoaded(cx + 1, cz) &&
		                world.isChunkLoaded(cx + 1, cz + 1))
		            {
		                rawchunk.initLighting();
		            }
				} catch(Exception e1) {
					e1.printStackTrace();
				}
			} else {
				// ##Format & send message no-block-replaced
			}
		}
	}
	*/
}
