package de.bergtiger.claim.bdo;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.bergtiger.claim.data.language.Lang;
import static de.bergtiger.claim.data.language.Cons.*;

public class TigerClaimCuboid extends TigerClaim {
	
	private final BlockVector3 min, max;
	
	public TigerClaimCuboid(Player player, World world, CuboidRegion cr) {
		super(player, world);
		min = cr.getMinimumPoint();
		max = cr.getMaximumPoint();
	}

	@Override
	public ProtectedRegion getRegion() {
		return new ProtectedCuboidRegion(
				getId(), 
				BlockVector3.at(min.getBlockX(), isExpandVert() ? minHeight : min.getBlockY(), min.getBlockZ()),
				BlockVector3.at(max.getBlockX(), isExpandVert() ? maxHeight : max.getBlockY(), max.getBlockZ()));
	}

	@Override
	public ProtectedRegion getRegionWithGab() {
		if((getGap() != null) && (getGap() > 0))
			return new ProtectedCuboidRegion(
					getId(),
					BlockVector3.at(min.getBlockX() - getGap(), isExpandVert() ? minHeight : Math.max(min.getBlockY() - getGap(), minHeight), min.getBlockZ() - getGap()),
					BlockVector3.at(max.getBlockX() + getGap(), isExpandVert() ? maxHeight : Math.min(max.getBlockY() + getGap(), maxHeight), max.getBlockZ() + getGap()));
		return getRegion();
	}

	@Override
	public String buildHover() {
		return Lang.CLAIM_CUBOID.get().replace(ID, getId())
				.replace(POS1, Lang.CLAIM_PATTERN_LOC.get()
						.replace(X, Integer.toString(min.getX()))
						.replace(Y, isExpandVert() ? Integer.toString(minHeight) : Integer.toString(min.getY()))
						.replace(Z, Integer.toString(min.getZ())))
				.replace(POS2, Lang.CLAIM_PATTERN_LOC.get()
						.replace(X, Integer.toString(max.getX()))
						.replace(Y, isExpandVert() ? Integer.toString(maxHeight) : Integer.toString(max.getY()))
						.replace(Z, Integer.toString(max.getZ())));
	}
}
