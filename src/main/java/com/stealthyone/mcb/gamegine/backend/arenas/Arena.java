package com.stealthyone.mcb.gamegine.backend.arenas;

import com.stealthyone.mcb.gamegine.backend.selections.Selection;
import org.bukkit.Location;

public class Arena {

	private String name;
	private Selection selection;
	private Location spawn;
	private boolean pvp;

	public Arena(String name, Selection selection) {
		this.name = name;
		this.selection = selection;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Selection getSelection() {
		return selection;
	}

	public void setSelection(Selection selection) {
		this.selection = selection;
	}

	public Location getSpawn() {
		return spawn;
	}

	public void setSpawn(Location spawn) {
		this.spawn = spawn;
	}

	public boolean isPvp() {
		return pvp;
	}

	public void setPvp(boolean pvp) {
		this.pvp = pvp;
	}
	
}