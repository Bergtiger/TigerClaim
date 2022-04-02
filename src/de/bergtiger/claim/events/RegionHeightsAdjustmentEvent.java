package de.bergtiger.claim.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegionHeightsAdjustmentEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final ProtectedRegion oldRegion;
    private final ProtectedRegion newRegion;
    private final World world;
    private final int oldMinHeight;
    private final int oldMaxHeight;
    private final int newMinHeight;
    private final int newMaxHeight;

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public RegionHeightsAdjustmentEvent(ProtectedRegion oldRegion, ProtectedRegion newRegion, World world, int oldMinHeight, int oldMaxHeight, int newMinHeight, int newMaxHeight) {
        this.oldRegion = oldRegion;
        this.newRegion = newRegion;
        this.world = world;
        this.oldMinHeight = oldMinHeight;
        this.oldMaxHeight = oldMaxHeight;
        this.newMinHeight = newMinHeight;
        this.newMaxHeight = newMaxHeight;
    }

    public ProtectedRegion getOldRegion() {
        return oldRegion;
    }

    public ProtectedRegion getNewRegion() {
        return newRegion;
    }

    public World getWorld() {
        return world;
    }

    public int getOldMinHeight() {
        return oldMinHeight;
    }

    public int getOldMaxHeight() {
        return oldMaxHeight;
    }

    public int getNewMinHeight() {
        return newMinHeight;
    }

    public int getNewMaxHeight() {
        return newMaxHeight;
    }
}
