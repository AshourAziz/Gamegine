package com.stealthyone.mcb.gamegine.backend.arenas;

import java.util.ArrayList;
import org.bukkit.Location;

import com.stealthyone.mcb.gamegine.backend.selection.Selection;

public class Arena {
	private String name;
	private Selection selection;
	private ArrayList<String> players = new ArrayList<String>();
	private Location spawn;
	boolean pvp;

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

	public ArrayList<String> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<String> players) {
		this.players = players;
	}

	public void addPlayer(String name) {
		if (!players.contains(name)) {
			players.add(name);
		}
	}

	public void removePlayer(String name) {
		if (players.contains(name)) {
			players.remove(name);
		}
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