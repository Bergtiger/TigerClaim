package de.bergtiger.claim.bdo;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class TigerClaimPolygon extends TigerClaim {

	private final List<BlockVector2> points;
	private final int maxY, minY;
	private final double centerX, centerZ;
	
	public TigerClaimPolygon(Player player, World world, Polygonal2DRegion pr) {
		super(player, world);
		this.points = pr.getPoints();
		this.minY = pr.getMinimumY();
		this.maxY = pr.getMaximumY();
		centerX = 0;
		centerZ = 0;
	}

	@Override
	public ProtectedRegion getRegion() {
		return new ProtectedPolygonalRegion(
				getId(), 
				points, 
				isExpandVert() ? 0 : minY, 
				isExpandVert() ? 255 : maxY);
	}

	@Override
	public ProtectedRegion getRegionWithGab() {
		if((getGap() != null) && (getGap() > 0))
			// äußerste Punkte -> Mittelpunkt
			// Diagonale Punkt - Mittelpunkt -> auf der Diagonalen verlängern
			return new ProtectedPolygonalRegion(
					getId(),
					points,
					isExpandVert() ? 0 : Math.max(minY - getGap(), 0),
					isExpandVert() ? 255 : Math.min(maxY + getGap(), 255));
		return getRegion();
	}
	
	@Override
	public String buildHover() {
		return "Region: " + getId() + "\nPoints:\n" + ((points != null && !points.isEmpty()) ? 
				points.stream()
					.map(p -> (Integer.toString(p.getX()) + Integer.toString(p.getZ()))).collect(Collectors.joining("\n")) : "-");
	}
}
