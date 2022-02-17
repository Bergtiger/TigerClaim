package de.bergtiger.claim.data;

import static de.bergtiger.claim.data.language.Cons.ID;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.bergtiger.claim.data.language.Lang;

public class ClaimUtils {

	public static String arrayToString(String[] args, int beginn) {
		if (args != null) {
			if (beginn < 0)
				beginn = 0;
			String s = "";
			for (int i = beginn; i < args.length; i++) {
				if (s.length() > 0)
					s += " ";
				s += args[i];
			}
			return s;
		}
		return null;
	}
	
	public static String buildRegionHover(ProtectedRegion r) {
		if(r != null)
			return Lang.LIST_HOVER.replace(ID, r.getId());
		return null;
	}

	public static double getArea(ProtectedRegion r) {
		if (r != null) {
			if (r instanceof ProtectedPolygonalRegion pr) {
				double sum = 0;
				for (int i = 0; i < (pr.getPoints().size() - 1); i++) {
					// i, i + 1
					// sum += ax * bz - az * bx
					BlockVector2 a = pr.getPoints().get(i), b = pr.getPoints().get(i + 1);
					sum += a.getX() * b.getZ() - a.getZ() * b.getX();
				}
				// last point
				BlockVector2 a = pr.getPoints().get(pr.getPoints().size() - 1), b = pr.getPoints().get(0);
				sum += a.getX() * b.getZ() - a.getZ() * b.getX();
				// deviate by 2
				return Math.abs(sum / 2);
			} else {
				return (1 + Math.abs((r.getMinimumPoint().getX() - r.getMaximumPoint().getX())) * (1 + Math.abs(r.getMinimumPoint().getZ() - r.getMaximumPoint().getZ())));
			}
		}
		return  0;
	}
}
