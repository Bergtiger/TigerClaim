package de.bergtiger.claim.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		ConfirmationListener.inst().clearQueue(e.getPlayer());
	}
}
