package me.bitfreeze.OreLess;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;


public class OreLessRule {
	public String label;
	public boolean[] activeHeight = new boolean[128];
	public ArrayList<Integer> blockFrom = new ArrayList<Integer>();
	public int blockTo;
	public boolean fixLighting;

	OreLessRule(OreLess plugin, String label, String blockFromList, String height, String blockTo, boolean fixLighting) {
		Material mat;

		this.label = label;

		// Parse the list of source block types, building a proper list from it
		String[] names = blockFromList.split(",");
		for (String name : names) {
			mat = Material.matchMaterial(name);
			if (mat == null) {
				plugin.logger.warning(plugin.logPrefix + "Invalid block-from \"" + name + "\".");
			} else {
				this.blockFrom.add(mat.getId());
			}
		}

		// Parse the list of height ranges, building the activeHeight flag array
		Arrays.fill(this.activeHeight, false);

		// Parse the replacement block, for it may be a block name instead
		mat = Material.matchMaterial(blockTo);
		if (mat == null) {
			this.blockTo = plugin.defaultBlockTo;
			plugin.logger.warning(plugin.logPrefix + "Invalid block-to \"" + blockTo + "\". Using default (" + Material.getMaterial(this.blockTo).toString() + ").");
		} else {
			this.blockTo = mat.getId();
		}

		// Fix lighting in case of any changes?
		this.fixLighting = fixLighting;
	}
}
