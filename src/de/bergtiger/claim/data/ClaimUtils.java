package de.bergtiger.claim.data;

import static de.bergtiger.claim.data.Cons.ID;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

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
			return Lang.LIST_HOVER.get().replace(ID, r.getId());
		return null;
	}
}
