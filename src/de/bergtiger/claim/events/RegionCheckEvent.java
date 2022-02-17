package de.bergtiger.claim.events;

import de.bergtiger.claim.bdo.TigerClaim;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegionCheckEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled = false;
    private final TigerClaim tc;
    private final Player player;
    private String message;
    private final boolean isLimit;
    private final boolean isOverlapping;
    private boolean continueClaim;

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

    public RegionCheckEvent(TigerClaim tc, Player player, String message, boolean isLimit, boolean isOverlapping) {
        this.tc = tc;
        this.player = player;
        this.message = message;
        this.isLimit = isLimit;
        this.isOverlapping = isOverlapping;
        this.continueClaim = !(isLimit || isOverlapping);
    }

    public Player getPlayer() {
        return player;
    }

    public TigerClaim getRegion() {
        return tc;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isLimit() {
        return isLimit;
    }

    public boolean isOverlapping() {
        return isOverlapping;
    }

    public void setContinueClaim(boolean continueClaim) {
        this.continueClaim = continueClaim;
    }

    /**
     * When isLimit or isOverlapping is true, will normally not continue to claim
     * @return true when continued to claim
     */
    public boolean continueClaim() {
        return continueClaim;
    }
}
