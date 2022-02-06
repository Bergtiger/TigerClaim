package de.bergtiger.claim.data;

import static de.bergtiger.claim.data.language.Cons.ID;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.bergtiger.claim.data.language.Lang;
import de.bergtiger.claim.data.logger.TigerLogger;

import java.util.logging.Level;

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

	public static double getArea(ProtectedRegion region) {
		if(region != null) {
			if (region instanceof ProtectedPolygonalRegion polyRegion) {
				double summe = 0;
				BlockVector2 letzterPunkt = null;
				for (int i = 0; i < polyRegion.getPoints().size(); i++) {
					BlockVector2 punkt = polyRegion.getPoints().get(i);
					if (i != 0) {
						summe = summe + letzterPunkt.getBlockX() * punkt.getBlockZ() - letzterPunkt.getBlockZ() * punkt.getBlockX();
					}
					letzterPunkt = punkt;
				}
				BlockVector2 ersterPunkt = polyRegion.getPoints().get(0);
				summe = summe + letzterPunkt.getBlockX() * ersterPunkt.getBlockZ() - letzterPunkt.getBlockZ() * ersterPunkt.getBlockX();
				return Math.abs(summe / 2.0);
			} else {
				//Rechtecks-Grundfläche
				double area = Math.abs(
						(region.getMaximumPoint().getX() - region.getMinimumPoint().getX()) *
								(region.getMaximumPoint().getZ() - region.getMinimumPoint().getZ()));
				TigerLogger.log(Level.INFO, "ClaimUtils: Area: " + area);
				return area;
			}
		}
		return 0.0;
	}
}
