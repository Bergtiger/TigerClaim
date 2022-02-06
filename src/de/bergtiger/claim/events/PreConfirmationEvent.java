package de.bergtiger.claim.events;

import de.bergtiger.claim.bdo.TigerClaim;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PreConfirmationEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled = false;
    private final TigerClaim tc;
    private final Player player;

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

    public PreConfirmationEvent(TigerClaim tc, Player player) {
        this.tc = tc;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public TigerClaim getRegion() {
        return tc;
    }
}
