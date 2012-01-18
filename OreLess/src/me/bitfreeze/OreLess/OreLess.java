package me.bitfreeze.OreLess;

import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class OreLess extends JavaPlugin {
	public final  OreLess plugin = this;
	public static Logger logger = Logger.getLogger("Minecraft");
    public static String logPrefix;
    public String version;
    public static PermissionHandler permissionHandler;
    public static boolean hasPermissions = false;
	public final OreLessParameters parameters = new OreLessParameters(this);
    public final OreLessWorldListener worldListener = new OreLessWorldListener(this);

    @Override
	public void onEnable() {
		PluginDescriptionFile pdffile = this.getDescription();
		logger.info(pdffile.getName() + " version " + pdffile.getVersion() + " is enabled.");
		setupPermissions();
    	logPrefix = "[" + pdffile.getName() + "] ";

		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.CHUNK_POPULATED, worldListener, Event.Priority.Normal, this);
	}

    @Override
	public void onDisable() {
		PluginDescriptionFile pdffile = this.getDescription();
		logger.info(pdffile.getName() + " version " + pdffile.getVersion() + " is disabled.");
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

	//public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//Player player = (Player) sender;

		//if(label.equalsIgnoreCase("mchammer") || label.equalsIgnoreCase("mch")) {
		//	if( (hasPermissions && permissionHandler.has(player, "bitcommands.mchammer")) || player.isOp()) {
		//		log.info(logPrefix + "Can't Touch This! Player: " + player.getName() + ".");
		//		player.sendMessage("--- CAN'T TOUCH THIS! ---");
		//		player.performCommand("play yctt");
		//		return true;
		//	}
		//}
		//if( (hasPermissions && permissionHandler.has(player, "bitcommands.palette")) || player.isOp()) {
		//	if(label.equalsIgnoreCase("palette")) {
		//		String msgColors = "";
		//		player.sendMessage(ChatColor.GRAY + "Color palette:");
		//		for (ChatColor color : ChatColor.values()) {
		//			msgColors = msgColors + color + "#";
		//		}
		//		player.sendMessage(msgColors);
		//		player.sendMessage(ChatColor.GRAY + "0123456789abcdef");
		//		return true;
		//	}
		//}

		//return false;
	//}
}
