package de.bergtiger.claim.events;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegionExpandEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled = false;
    private final Player player;
    private final World world;
    private final ProtectedRegion oldRegion;
    private final Region newRegion;
    private final Region expansionOrSelection;
    private final boolean isDirectionalExtension;
    private final boolean regionStated;
    private final double oldArea;
    private final double newArea;
    private final BlockFace direction;
    private final Integer extendLength;
    private final boolean containsGapsFromOldRegionAndSelection;
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

    public RegionExpandEvent(
            Player player, World world, ProtectedRegion oldRegion, Region newRegion, Region expansionOrSelection,
            boolean isDirectionalExtension, boolean regionStated, double oldArea, double newArea, BlockFace direction, Integer extendLength, boolean containsGapsFromOldRegionAndSelection, String message) {
        this.player = player;
        this.world = world;
        this.oldRegion = oldRegion;
        this.newRegion = newRegion;
        this.expansionOrSelection = expansionOrSelection;
        this.isDirectionalExtension = isDirectionalExtension;
        this.regionStated = regionStated;
        this.oldArea = oldArea;
        this.newArea = newArea;
        this.direction = direction;
        this.extendLength = extendLength;
        this.containsGapsFromOldRegionAndSelection = containsGapsFromOldRegionAndSelection;
        this.message = message;
    }

    public Player getPlayer() {
        return player;
    }

    public World getWorld() {
        return world;
    }

    public ProtectedRegion getOldRegion() {
        return oldRegion;
    }

    public Region getNewRegion() {
        return newRegion;
    }

    public Region getExpansionOrSelection() {
        return expansionOrSelection;
    }

    public boolean isDirectionalExtension() {
        return isDirectionalExtension;
    }

    public boolean isRegionStated() {
        return regionStated;
    }

    public double getOldArea() {
        return oldArea;
    }

    public double getNewArea() {
        return newArea;
    }

    public BlockFace getDirection() {
        return direction;
    }

    public Integer getExtendLength() {
        return extendLength;
    }

    public boolean containsGapsFromOldRegionAndSelection() {
        return containsGapsFromOldRegionAndSelection;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
