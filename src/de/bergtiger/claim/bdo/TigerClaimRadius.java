package de.bergtiger.claim.bdo;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.bergtiger.claim.data.configuration.Config;
import de.bergtiger.claim.data.language.Lang;
import static de.bergtiger.claim.data.language.Cons.*;

public class TigerClaimRadius extends TigerClaim {

	private final Location loc;
	private final int radius;
	
	public TigerClaimRadius( Player player, Location loc) {
		super(player, loc.getWorld());
		this.loc = loc;
		// load missing value Radius from Configuration
		radius = Config.getInt(Config.REGION_RADIUS);
	}
	
	public TigerClaimRadius(Player player, World world, Location loc, int radius) {
		super(player, world);
		this.loc = loc;
		this.radius = radius;
	}

	@Override
	public ProtectedRegion getRegion() {
		return new ProtectedCuboidRegion(
				getId(), 
				BlockVector3.at(
						(loc.getBlockX() - radius),
						(isExpandVert() ? minHeight : Math.max((loc.getBlockY() - radius), minHeight)),
						(loc.getBlockZ() - radius)),
				BlockVector3.at(
						(loc.getBlockX() + radius),
						(isExpandVert() ? maxHeight : Math.min((loc.getBlockY() + radius), maxHeight)),
						(loc.getBlockZ() + radius)));
	}

	@Override
	public ProtectedRegion getRegionWithGab() {
		if(getGap() != null && getGap() > 0)
			return new ProtectedCuboidRegion(
					getId(), 
					BlockVector3.at(
							(loc.getBlockX() - radius - getGap()),
							(isExpandVert() ? minHeight : Math.max((loc.getBlockY() - radius - getGap()), minHeight)),
							(loc.getBlockZ() - radius - getGap())), 
					BlockVector3.at(
							(loc.getBlockX() + radius + getGap()),
							(isExpandVert() ? maxHeight : Math.min((loc.getBlockY() + radius + getGap()), maxHeight)),
							(loc.getBlockZ() + radius + getGap())));
		return getRegion();
	}
	
	@Override
	public String buildHover() {
		return Lang.CLAIM_RADIUS.get()
				.replace(ID, getId())
				.replace(POS1, Lang.CLAIM_PATTERN_LOC.get()
						.replace(X, Integer.toString(loc.getBlockX()))
						.replace(Y, Integer.toString(loc.getBlockY()))
						.replace(Z, Integer.toString(loc.getBlockZ())))
				.replace(VALUE, Integer.toString(radius));
	}
}
