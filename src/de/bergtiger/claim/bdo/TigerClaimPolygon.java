package de.bergtiger.claim.bdo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.bergtiger.claim.data.language.Lang;
import static de.bergtiger.claim.data.language.Cons.*;

public class TigerClaimPolygon extends TigerClaim {

	private final List<BlockVector2> points;
	private final int maxY, minY;
	
	public TigerClaimPolygon(Player player, World world, Polygonal2DRegion pr) {
		super(player, world);
		this.points = pr.getPoints();
		this.minY = pr.getMinimumY();
		this.maxY = pr.getMaximumY();
	}

	@Override
	public ProtectedRegion getRegion() {
		return new ProtectedPolygonalRegion(
				getId(), 
				points, 
				isExpandVert() ? minHeight : minY,
				isExpandVert() ? maxHeight : maxY);
	}

	@Override
	public ProtectedRegion getRegionWithGab() {
		if((getGap() != null) && (getGap() > 0)) {
			List<BlockVector2> intersections = new ArrayList<>();
			// calculate intersection
			for(int i = 0; i < points.size(); i++) {
				Vector2 s;
				if(i == 0) {
					s = intersection(points.get(points.size() - 1).toVector2(), points.get(i).toVector2(), points.get(i + 1).toVector2());
				} else if(i == points.size() - 1) {
					s = intersection(points.get(i - 1).toVector2(), points.get(i).toVector2(), points.get(0).toVector2());
				} else {
					s = intersection(points.get(i - 1).toVector2(), points.get(i).toVector2(), points.get(i + 1).toVector2());
				}
				if(s != null) {
					intersections.add(s.toBlockPoint());
					// System.out.println("s: " + s);
				}
			}
			return new ProtectedPolygonalRegion(
				getId(), 
				intersections, 
				isExpandVert() ? minHeight : minY,
				isExpandVert() ? maxHeight : maxY);
		}
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
		
		TigerLinearFunction 
			f = new TigerLinearFunction(new TigerPoint(a1.getX(), a1.getZ()), new TigerPoint(b1.getX(), b1.getZ())),
			g = new TigerLinearFunction(new TigerPoint(b2.getX(), b2.getZ()), new TigerPoint(c2.getX(), c2.getZ()));
		TigerPoint s = f.getIntersection(g);
		if(s != null)
			return Vector2.at(s.x, s.z);
		return null;
	}
	
	/**
	 * Rotate vector(angle) and set vector length to length.
	 * @param vector
	 * @param angle
	 * @param length
	 * @return
	 */
	private Vector2 orthogonal(Vector2 vector, double angle, double length) {
		return vector.transform2D(angle, 0, 0, 0, 0).normalize().multiply(length);
	}
}
