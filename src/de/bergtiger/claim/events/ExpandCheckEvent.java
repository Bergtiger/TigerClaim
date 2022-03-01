package de.bergtiger.claim.events;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ExpandCheckEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled = false;
    private final Player player;
    private final boolean isDirectionalExtension;
    private final boolean regionStated;
    private final boolean regionIsOverlappingAndShouldNot;
    private final double oldArea;
    private final double newArea;
    private final BlockFace direction;
    private final Integer extendLength;
    private String message;
    private boolean continueExpand;

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

    public ExpandCheckEvent(
            Player player, boolean isDirectionalExtension, boolean regionStated, boolean regionIsOverlappingAndShouldNot, double oldArea, double newArea, BlockFace direction, Integer extendLength, String message) {
        this.player = player;
        this.isDirectionalExtension = isDirectionalExtension;
        this.regionStated = regionStated;
        this.regionIsOverlappingAndShouldNot = regionIsOverlappingAndShouldNot;
        this.oldArea = oldArea;
        this.newArea = newArea;
        this.direction = direction;
        this.extendLength = extendLength;
        this.message = message;
        this.continueExpand = !regionIsOverlappingAndShouldNot;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isDirectionalExtension() {
        return isDirectionalExtension;
    }

    public boolean isRegionStated() {
        return regionStated;
    }

    public boolean regionIsOverlappingAndShouldNot() {
        return regionIsOverlappingAndShouldNot;
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

    public boolean continueExpand() {
        return continueExpand;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setContinueExpand(boolean continueExpand) {
        this.continueExpand = continueExpand;
    }
}
