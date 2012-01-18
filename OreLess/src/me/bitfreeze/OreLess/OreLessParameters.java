package me.bitfreeze.OreLess;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.World;

public class OreLessParameters {
	private OreLess							plugin;
	private HashMap<World, String>			worldParameters			= new HashMap<World, String>();
	private HashMap<World, OreLessItem[]>	worldItemReplacements	= new HashMap<World, OreLessItem[]>();

	OreLessParameters(OreLess plugin) {
		this.plugin = plugin;
	}

	public OreLessItem[] getParameters(World world) {
		if (worldParameters.containsKey(world)) {
			return worldItemReplacements.get(world);
		} else {
			// The parameters should already have been set prior to use.
			return null;
		}
	}

	public OreLessItem[] getParameters(World world, String parameters) {
		ArrayList<OreLessItem>	repl			= new ArrayList<OreLessItem>();
		OreLessItem[]			replacements	= null;
		OreLessItem				replacement;
		String[]				tokens;

		// If the world has already been processed for these very same parameters,
		// do not do it all again, just use the previously saved replacements.
		if (worldParameters.containsKey(world)) {
			if (worldParameters.get(world) == parameters) {
				return worldItemReplacements.get(world);
			}
			// If reaches this point, World parameters have changed.  Rebuild it
		}
		// New/changed world parameters.  Process them one by one.
		if(parameters.trim().isEmpty()) {
			parameters = "CLAY,COALORE,IRONORE,GOLDORE,DIAMONDORE,REDSTONEORE,GLOWINGREDSTONEORE";
		}
		worldParameters.put(world, parameters);
		tokens = parameters.split(",");
		if (tokens.length < 1) {
			OreLess.logger.info(OreLess.logPrefix + "Wrong parameters informed for world " + world.getName() + ":" + parameters);
			return null;
		}
		for(String parameter : tokens) {
			replacement = OreLessItem.newItem(parameter);
			if (replacement == (OreLessItem)null) {
				OreLess.logger.info(OreLess.logPrefix + "Wrong parameter informed for world " + world.getName() + ": " + parameter);
			} else {
				repl.add(replacement);
			}
		}
		return replacements;
	}
}
