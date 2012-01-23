package me.bitfreeze.OreLess;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class OreLess extends JavaPlugin {
	public final  OreLess plugin = this;
	public final Logger logger = Logger.getLogger("Minecraft");
	public static String logPrefix;
	public String version;
	public static PermissionHandler permissionHandler;
	public static boolean hasPermissions = false;
	public final OreLessParameters parameters = new OreLessParameters(this);
	public final OreLessWorldListener worldListener = new OreLessWorldListener(this);
	FileConfiguration config;

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
		loadConfig();

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(worldListener, this);
	}

	private void loadConfig() {
		try {
			getDataFolder().mkdir();
			config = this.getConfig();
			// Define default values for missing options in the config.yml
			if (!config.contains("default-replacement")) {
				config.set("default-replacement", "AIR");
			}
			if (!config.contains("default-minimum-height")) {
				config.set("default-minimum-height", 0);
			}
			if (!config.contains("default-maximum-height")) {
				config.set("default-maximum-height", 127);
			}

			if (!config.contains("rules")) {
				config.set("rules", null);
			} else {
				// Load the internal structure of replacements
				List<Map<String, Object>> rules = config.getMapList("rules");
				for(Map<String, Object> rule : rules) {
					
				}
			}

			if (!config.contains("ignore-worlds")) {
				config.set("ignore-worlds", null);
			}
		} catch(Exception e1) {
			e1.printStackTrace();
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
						|| (config.getBoolean("permissions.op-grant-all", true) && player.isOp()) ) {
					loadConfig();
					logger.info(logPrefix + "Configurations reloaded from file.");
					return true;
				}
			}
		//}

		return false;
	}
}
