package de.bergtiger.claim.bdo;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.bergtiger.claim.data.Lang;
import static de.bergtiger.claim.data.Cons.*;

public class TigerClaimPolygon extends TigerClaim {

	private final List<BlockVector2> points;
	private final int maxY, minY;
	private double length, centerX, centerZ;
	
	public TigerClaimPolygon(Player player, World world, Polygonal2DRegion pr) {
		super(player, world);
		this.points = pr.getPoints();
		this.minY = pr.getMinimumY();
		this.maxY = pr.getMaximumY();
		// find center
		if(points != null && !points.isEmpty()) {
			length = points.size();
			points.forEach(bv2 -> {
				centerX += bv2.getBlockX() / length;
				centerZ += bv2.getBlockZ() / length;
			});
		} else {
			centerX = 0;
			centerZ = 0;
		}
		
		getRegionWithGab();
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
		// calculate intersection
//		for(int i = 0; i < points.size(); i++) {
//			if(i == 0) {
//				schnittpunkt(points.get(points.size() - 1).toVector2(), points.get(i).toVector2(), points.get(i + 1).toVector2());
//			} else if(i == points.size() - 1) {
//				schnittpunkt(points.get(i - 1).toVector2(), points.get(i).toVector2(), points.get(0).toVector2());
//			} else {
//				schnittpunkt(points.get(i - 1).toVector2(), points.get(i).toVector2(), points.get(i + 1).toVector2());
//			}
//		}
		return getRegion();
	}
	
	@Override
	public String buildHover() {
		return Lang.CLAIM_POLYGON.get()
				.replace(ID, getId())
				.replace(VALUE, ((points != null) && !points.isEmpty()) ? 
						("\n" + points.stream().map(p -> Lang.CLAIM_PATTERN_POINTS.get()
								.replace(X, Integer.toString(p.getBlockX()))
								.replace(Z, Integer.toString(p.getBlockZ())))
						.collect(Collectors.joining("\n"))) : "");
//		return "Region: " + getId() + "\nPoints:\n" + ((points != null && !points.isEmpty()) ? 
//				points.stream()
//					.map(p -> (Integer.toString(p.getX()) + Integer.toString(p.getZ()))).collect(Collectors.joining("\n")) : "-");
	}
	
	private Vector2 intersection(Vector2 a, Vector2 b, Vector2 c) {
		Vector2 ab = a.subtract(b);
		Vector2 o = orthogonal(ab, 90, getGap());
		Vector2 a1 = a.add(o);
		Vector2 b1 = b.add(o);
		
		Vector2 bc = b.subtract(c);
		Vector2 o2 = orthogonal(bc, 90, getGap());
		Vector2 b2 = b.add(o2);
		Vector2 c2 = c.add(o2);
		
		// m*o + a1 = x, n*o2 + b2 = x;
		double 
			x = (b2.getX() - a1.getX()) / ((a1.subtract(b1).getX() - b2.subtract(c2).getX())),
			y = (b2.getZ() - a1.getZ()) / ((a1.subtract(b1).getZ() - b2.subtract(c2).getZ()));
		Vector2 s = a1.add(a1.subtract(b1).multiply(x));
		
		System.out.println(a1 + ", " + b1 + "; " + b2 + ", " + c2 + "; " + x + ", " + y + "; " + s);
		return s;
	}
	
	/**
	 * Rotate vector(angle) and set vector length to length.
	 * @param vector
	 * @param angle
	 * @param length
	 * @return
	 */
	private Vector2 orthogonal(@Nonnull Vector2 vector, @Nonnull double angle, @Nonnull double length) {
		return vector.transform2D(angle, 0, 0, 0, 0).normalize().multiply(length);
	}
}
