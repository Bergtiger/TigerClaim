package de.bergtiger.claim.cmd;

import de.bergtiger.claim.events.PreClaimConfirmationEvent;
import org.bukkit.Bukkit;
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
import de.bergtiger.claim.data.language.Lang;
import de.bergtiger.claim.data.permission.Perm;
import de.bergtiger.claim.listener.ConfirmationListener;

public class CmdClaim {
	
	public static void claim(CommandSender cs, boolean priorityPermission) {
		if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_CLAIM) || priorityPermission) {
			if (cs instanceof Player p) {
				// Get amount of claims
				TigerClaim tc;
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
				// call PreConfirmationEvent
				PreClaimConfirmationEvent event = new PreClaimConfirmationEvent(tc, p, Lang.CLAIM_MESSAGE.get());
				Bukkit.getPluginManager().callEvent(event);
				if (!event.isCancelled()) {
					// send Confirmation
					ConfirmationListener.inst().addConfirmation(tc);
					// inform Player
					p.spigot().sendMessage(Lang.build(event.getMessage(), null, Lang.build(tc.buildHover()), null),
							Lang.build(Lang.CLAIM_YES, "/yes", Lang.build(Lang.CLAIM_HOVER_YES), null),
							Lang.build(Lang.CLAIM_NO, "/no", Lang.build(Lang.CLAIM_HOVER_NO), null));
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
