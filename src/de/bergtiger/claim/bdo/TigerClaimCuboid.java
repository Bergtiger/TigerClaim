package de.bergtiger.claim.bdo;

import javax.annotation.Nonnull;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.bergtiger.claim.data.Lang;
import static de.bergtiger.claim.data.Cons.*;

public class TigerClaimCuboid extends TigerClaim {
	
	private final BlockVector3 min, max;
	
	public TigerClaimCuboid(@Nonnull Player player, @Nonnull World world, @Nonnull CuboidRegion cr) {
		super(player, world);
		min = cr.getMinimumPoint();
		max = cr.getMaximumPoint();
	}

	@Override
	public ProtectedRegion getRegion() {
		return new ProtectedCuboidRegion(
				getId(), 
				BlockVector3.at(min.getBlockX(), isExpandVert() ?   0 : min.getBlockY(), min.getBlockZ()), 
				BlockVector3.at(max.getBlockX(), isExpandVert() ? 255 : max.getBlockY(), max.getBlockZ()));
	}

	@Override
	public ProtectedRegion getRegionWithGab() {
		if((getGap() != null) && (getGap() > 0))
			return new ProtectedCuboidRegion(
					getId(),
					BlockVector3.at(min.getBlockX() - getGap(), isExpandVert() ?   0 : Math.max(min.getBlockY() - getGap(),   0), min.getBlockZ() - getGap()),
					BlockVector3.at(max.getBlockX() + getGap(), isExpandVert() ? 255 : Math.min(max.getBlockY() + getGap(), 255), max.getBlockZ() + getGap()));
		return getRegion();
	}

	@Override
	public String buildHover() {
		return Lang.CLAIM_CUBOID.get().replace(ID, getId())
				.replace(POS1, Lang.CLAIM_PATTERN_LOC.get()
						.replace(X, Integer.toString(min.getX()))
						.replace(Y, isExpandVert() ? "0" : Integer.toString(min.getY()))
						.replace(Z, Integer.toString(min.getZ())))
				.replace(POS2, Lang.CLAIM_PATTERN_LOC.get()
						.replace(X, Integer.toString(max.getX()))
						.replace(Y, isExpandVert() ? "255" : Integer.toString(max.getY()))
						.replace(Z, Integer.toString(max.getZ())));
	}
}
