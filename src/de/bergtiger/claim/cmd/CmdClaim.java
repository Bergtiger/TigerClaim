package de.bergtiger.claim.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;

import de.bergtiger.claim.bdo.TigerClaim;
import de.bergtiger.claim.bdo.TigerClaimCuboid;
import de.bergtiger.claim.bdo.TigerClaimPolygon;
import de.bergtiger.claim.bdo.TigerClaimRadius;
import de.bergtiger.claim.data.Lang;
import de.bergtiger.claim.data.Perm;
import de.bergtiger.claim.listener.ConfirmationListener;

public class CmdClaim {
	
	public static void claim(CommandSender cs) {
		if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_CLAIM)) {
			if (cs instanceof Player) {
				// Get amount of claims
				Player p = (Player) cs;
				TigerClaim tc = null;
				// WorldGuard has dependency for WorldEdit
				if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_WORLDEDIT)) {
					WorldEdit we = WorldEdit.getInstance();
					BukkitPlayer bp = BukkitAdapter.adapt(p);
					try {
						Region s = we.getSessionManager().get(bp).getSelection(bp.getWorld());
						if (s != null) {
							if (s instanceof CuboidRegion) {
								// Cuboid
								tc = new TigerClaimCuboid(p, p.getWorld(), (CuboidRegion) s);
							} else {
								// Polygon
								tc = new TigerClaimPolygon(p, p.getWorld(), (Polygonal2DRegion) s);								
							}
						} else {
							// No Region
							tc = new TigerClaimRadius(p, p.getLocation());
						}
					} catch (IncompleteRegionException e) {
						// No Region
						tc = new TigerClaimRadius(p, p.getLocation());
					}
				} else {
					// Without WorlEdit
					tc = new TigerClaimRadius(p, p.getLocation());
				}
				if(tc != null) {
					// send Confirmation
					ConfirmationListener.inst().addConfirmation(tc);
					// inform Player
					p.spigot().sendMessage(Lang.buildTC(Lang.INSERT_TEXT.get(), null, tc.buildHover(), null),
						Lang.buildTC(Lang.INSERT_YES.get(), "/yes", Lang.INSERT_HOVER_YES.get(), null),
						Lang.buildTC(Lang.INSERT_NO.get(), "/no", Lang.INSERT_HOVER_NO.get(), null));
				}
			} else {
				// Not a player
				cs.sendMessage(Lang.NOPLAYER.get());
			}
		} else {
			cs.sendMessage(Lang.NOPERMISSION.get());
		}
	}
}
