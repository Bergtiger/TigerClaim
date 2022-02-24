package de.bergtiger.claim.bdo;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class ExpandDirectionalQueue {
    private final ProtectedRegion region;
    private final Player player;
    private final World world;
    private final int extendLength;
    private final String direction;
    private final boolean regionAngegeben;

    public ExpandDirectionalQueue(ProtectedRegion oldRegion, World world, Player player, String direction, int extendLength, boolean regionAngegeben) {
        this.region = oldRegion;
        this.world = world;
        this.player = player;
        this.direction = direction;
        this.extendLength = extendLength;
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

    public int getExtendLength() {
        return extendLength;
    }

    public String getDirection() {
        return direction;
    }

    public boolean isRegionAngegeben() {
        return regionAngegeben;
    }
}
