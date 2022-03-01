package de.bergtiger.claim.bdo;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.List;

public class ExpandDirectionalQueue {
    private final ProtectedRegion region;
    private final Player player;
    private final World world;
    private final int extendLength;
    private final BlockFace direction;
    private final boolean regionAngegeben;
    private final boolean isCheck;

    public ExpandDirectionalQueue(ProtectedRegion oldRegion, World world, Player player, BlockFace direction, int extendLength, boolean regionAngegeben, boolean isCheck) {
        this.region = oldRegion;
        this.world = world;
        this.player = player;
        this.direction = direction;
        this.extendLength = extendLength;
        this.regionAngegeben = regionAngegeben;
        this.isCheck = isCheck;
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

    public BlockFace getDirection() {
        return direction;
    }

    public boolean isRegionAngegeben() {
        return regionAngegeben;
    }

    public boolean isCheck() {
        return isCheck;
    }
}
