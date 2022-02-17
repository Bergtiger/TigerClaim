package de.bergtiger.claim.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PreDeleteConfirmationEvent extends Event implements Cancellable {
	private static final HandlerList HANDLERS = new HandlerList();
	private boolean isCancelled = false;
	private final ProtectedRegion pr;
	private final Player player;
	private String message;

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		isCancelled = b;
	}

	public PreDeleteConfirmationEvent(ProtectedRegion pr, Player player, String message) {
		this.pr = pr;
		this.player = player;
		this.message = message;
	}

	public Player getPlayer() {
		return player;
	}

	public ProtectedRegion getRegion() {
		return pr;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
