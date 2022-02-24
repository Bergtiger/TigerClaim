package de.bergtiger.claim.bdo;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class ExpandSelectionQueue {
    private final ProtectedRegion region;
    private final Player player;
    private final World world;
    private final List<BlockVector2> eckpunkteDerNeuenRegion;
    private final boolean regionAngegeben;

    public ExpandSelectionQueue(ProtectedRegion oldRegion, World world, Player player, List<BlockVector2> eckpunkteDerNeuenRegion, boolean regionAngegeben) {
        this.region = oldRegion;
        this.world = world;
        this.player = player;
        this.eckpunkteDerNeuenRegion = eckpunkteDerNeuenRegion;
        this.regionAngegeben = regionAngegeben;
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

    public List<BlockVector2> getEckpunkteDerNeuenRegion() {
        return eckpunkteDerNeuenRegion;
    }

    public boolean isRegionAngegeben() {
        return regionAngegeben;
    }
}
