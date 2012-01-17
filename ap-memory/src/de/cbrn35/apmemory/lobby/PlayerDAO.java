package de.cbrn35.apmemory.lobby;

import de.cbrn35.apmemory.Player;

public interface PlayerDAO {
	public Player getPlayer();
	public long persist(Player player);
	public void update(Player player);
	public void delete(Player player);
}