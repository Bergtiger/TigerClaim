package de.bergtiger.claim.bdo;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class DeleteQueue {
    private final ProtectedRegion region;
    private final Player player;
    private final World world;

    public DeleteQueue(ProtectedRegion region, World world, Player player) {
        this.region = region;
        this.world = world;
        this.player = player;
    }

    public ProtectedRegion getRegion() {
        return region;
    }

    public Player getPlayer() {
        return player;
    }

    public World getWorld() {
        return world;
    }
}
