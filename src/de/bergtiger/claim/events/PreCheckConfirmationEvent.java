package de.bergtiger.claim.events;

import de.bergtiger.claim.bdo.TigerClaim;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PreCheckConfirmationEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled = false;
    private final TigerClaim tc;
    private final Player player;
    private String spielerBenachrichtigung;

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

    public PreCheckConfirmationEvent(TigerClaim tc, Player player, String spielerBenachrichtigung) {
        this.tc = tc;
        this.player = player;
        this.spielerBenachrichtigung = spielerBenachrichtigung;
    }

    public Player getPlayer() {
        return player;
    }

    public TigerClaim getRegion() {
        return tc;
    }

    public String getSpielerBenachrichtigung() {
        return spielerBenachrichtigung;
    }

    public void setSpielerBenachrichtigung(String spielerBenachrichtigung) {
        this.spielerBenachrichtigung = spielerBenachrichtigung;
    }
}