package me.bitfreeze.OreLess;

import org.bukkit.Material;


public class OreLessItem {
	byte	typeId;
	int		fromHeight;
	int		thruHeight;
	byte	replaceId;

	OreLessItem(byte typeId, int fromHeight, int thruHeight, byte replaceId) {
		this.typeId		= typeId;
		this.fromHeight	= fromHeight;
		this.thruHeight	= thruHeight;
		this.typeId		= replaceId;
	}

	public static OreLessItem newItem(String text) {
		String		data[];
		String		tokens[];
		Material	mat;
		byte		typeId;
		int			fromHeight;
		int			thruHeight;
		byte		replaceId;

		// TYPE/FROM/TO=REPLACE
		data = text.split("=");
		if ((data.length < 1) || (data.length > 2)) {
			return null;
		}

		tokens = data[0].split("-");
		if ((tokens.length < 1) || (tokens.length > 4)) {
			return null;
		}
		if (tokens[0].trim().isEmpty()) {
			return null;
		}
		mat = Material.matchMaterial(tokens[0]);
		if (mat == null) {
			return null;
		}
		typeId = (byte)mat.getId();
		if (tokens.length >= 2) {
			try {
				fromHeight = Integer.parseInt(tokens[1]);
			} catch(Exception e) {
				return null;
			}
			if (tokens.length == 3) {
				try {
					thruHeight = Integer.parseInt(tokens[2]);
				} catch(Exception e) {
					return null;
				}
			} else {
				thruHeight = fromHeight;
			}
		} else {
			fromHeight = 0;
			thruHeight = 128;
		}

		if (data.length == 2) {
			if (!data[1].trim().isEmpty()) {
				mat = Material.matchMaterial(data[1]);
				if (mat == null) {
					return null;
				}
				replaceId = (byte)mat.getId();
			} else {
				replaceId = (byte)Material.STONE.getId();
			}
		} else {
			replaceId = (byte)Material.STONE.getId();
		}

		return new OreLessItem(typeId, fromHeight, thruHeight, replaceId);
	}
}
