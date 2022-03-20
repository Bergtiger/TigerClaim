package de.bergtiger.claim.bdo;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class AdjustHeightsQueue {
    private final ProtectedRegion region;
    private final World world;
    private final Player player;
    private final int newMinHeight;
    private final int newMaxHeight;

    public AdjustHeightsQueue(ProtectedRegion region, World world, Player player, int newMinHeight, int newMaxHeight) {
        this.region = region;
        this.world = world;
        this.player = player;
        this.newMinHeight = newMinHeight;
        this.newMaxHeight = newMaxHeight;
    }

    public ProtectedRegion getRegion() {
        return region;
    }

    public World getWorld() {
        return world;
    }

    public Player getPlayer() {
        return player;
    }

    public int getNewMinHeight() {
        return newMinHeight;
    }

    public int getNewMaxHeight() {
        return newMaxHeight;
    }
}
