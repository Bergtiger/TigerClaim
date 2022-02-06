package de.bergtiger.claim.bdo;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;

public class DeleteQueue {
    private final ProtectedRegion region;
    private final Player player;

    public DeleteQueue(ProtectedRegion region, Player player) {
        this.region = region;
        this.player = player;
    }

    public ProtectedRegion getRegion() {
        return region;
    }

    public Player getPlayer() {
        return player;
    }
}
