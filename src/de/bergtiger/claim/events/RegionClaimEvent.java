package de.bergtiger.claim.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegionClaimEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled = false;
    private final ProtectedRegion region;
    private final Player player;
    private final double area;

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

    public RegionClaimEvent(ProtectedRegion region, Player player, double area) {
        this.region = region;
        this.player = player;
        this.area = area;
    }

    public Player getPlayer() {
        return player;
    }

    public ProtectedRegion getRegion() {
        return region;
    }

    public double getArea() {
        return area;
    }
}