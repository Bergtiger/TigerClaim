package de.bergtiger.claim.bdo;

import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.bergtiger.claim.Claims;
import de.bergtiger.claim.data.Config;
import de.bergtiger.claim.data.Lang;
import static de.bergtiger.claim.data.Cons.*;

public class TigerClaimRadius extends TigerClaim {

	private final Location loc;
	private final int radius;
	
	public TigerClaimRadius(@Nonnull Player player, @Nonnull Location loc) {
		super(player, loc.getWorld());
		this.loc = loc;
		// load missing value Radius from Configuration
		if(Config.inst().hasValue(Config.REGION_RADIUS)) {
			int r = 0;
			try {
				r = Integer.valueOf(Config.inst().getValue(Config.REGION_RADIUS).toString());
			} catch (NumberFormatException e) {
				Claims.inst().getLogger().log(Level.SEVERE, Config.REGION_RADIUS + " has to be a Number.", e);
			}
			radius = r;
		} else {
			radius = 0;
		}
	}
	
	public TigerClaimRadius(@Nonnull Player player, @Nonnull World world, @Nonnull Location loc, int radius) {
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
						(isExpandVert() ?   0 : Math.max((loc.getBlockY() - radius),   0)),
						(loc.getBlockZ() - radius)),
				BlockVector3.at(
						(loc.getBlockX() + radius),
						(isExpandVert() ? 255 : Math.min((loc.getBlockY() + radius), 255)),
						(loc.getBlockZ() + radius)));
	}

	@Override
	public ProtectedRegion getRegionWithGab() {
		if(getGap() != null && getGap() > 0)
			return new ProtectedCuboidRegion(
					getId(), 
					BlockVector3.at(
							(loc.getBlockX() - radius - getGap()),
							(isExpandVert() ?   0 : Math.max((loc.getBlockY() - radius - getGap()),   0)),
							(loc.getBlockZ() - radius - getGap())), 
					BlockVector3.at(
							(loc.getBlockX() + radius + getGap()),
							(isExpandVert() ? 255 : Math.min((loc.getBlockY() + radius + getGap()), 255)),
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
