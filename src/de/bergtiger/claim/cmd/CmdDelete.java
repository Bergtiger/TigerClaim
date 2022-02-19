package de.bergtiger.claim.cmd;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.bergtiger.claim.Claims;
import de.bergtiger.claim.bdo.DeleteQueue;
import de.bergtiger.claim.data.language.Lang;
import de.bergtiger.claim.data.permission.Perm;
import de.bergtiger.claim.events.PreDeleteConfirmationEvent;
import de.bergtiger.claim.listener.ConfirmationListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdDelete {

	public static void delete(CommandSender cs, String[] args) {
		Bukkit.getScheduler().runTaskAsynchronously(Claims.inst(), () -> new CmdDelete().deleteAsynchronous(cs, args));
	}

	/**
	 * Set a value in Configuration. claim set type value.
	 * /claim (0)delete (1)region_name
	 *
	 * @param cs   command sender
	 * @param args arguments
	 */
	private void deleteAsynchronous(CommandSender cs, String[] args) {
		if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_DELETE)) {
			if (cs instanceof Player p) {
				// get RegionManager for world
				RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
				RegionManager regions = container.get(BukkitAdapter.adapt(p.getWorld()));
				if (args.length >= 2) {
					String regionName = args[1];
					if (regions.hasRegion(regionName)) {
						ProtectedRegion region = regions.getRegion(regionName);
						if (region.getOwners().contains(p.getUniqueId()) || Perm.hasPermission(cs, Perm.CLAIM_ADMIN)) {
							//call RegionDeleteEvent
							PreDeleteConfirmationEvent event = new PreDeleteConfirmationEvent(region, p, Lang.DELETE_MESSAGE.get());
							Bukkit.getPluginManager().callEvent(event);
							if (!event.isCancelled()) {
								ConfirmationListener.inst().addConfirmation(new DeleteQueue(region, p.getWorld(), p));
								// inform Player
								p.spigot().sendMessage(Lang.build(event.getMessage().replace(Lang.VALUE, region.getId()), null, Lang.DELETE_HOVER_TEXT, null),
										Lang.build(Lang.DELETE_YES, "/yes", Lang.build(Lang.DELETE_HOVER_YES), null),
										Lang.build(Lang.DELETE_NO, "/no", Lang.build(Lang.DELETE_HOVER_NO), null));
							}
						} else {
							// no owner
							p.spigot().sendMessage(Lang.build(Lang.NOPERMISSION));
						}
					} else {
						// no region
						p.spigot().sendMessage(Lang.build(Lang.NOSUCHREGION.replace(Lang.VALUE, regionName)));
					}
				} else {
					// player wants to delete region where he stands
					ApplicableRegionSet set = regions.getApplicableRegions(BukkitAdapter.asBlockVector(p.getLocation()));
					if (set.size() == 0) {
						// not in a region
						p.spigot().sendMessage(Lang.build(Lang.NOTINREGION));
					} else if (set.size() > 1) {
						// to many regions
						p.spigot().sendMessage(Lang.build(Lang.TOMANYREGIONS));
					} else {
						// exact one region
						ProtectedRegion region = set.getRegions().stream().findFirst().get();
						if (region.getOwners().contains(p.getUniqueId()) || Perm.hasPermission(cs, Perm.CLAIM_ADMIN)) {
							//call RegionDeleteEvent
							PreDeleteConfirmationEvent event = new PreDeleteConfirmationEvent(region, p, Lang.DELETE_MESSAGE.get());
							Bukkit.getPluginManager().callEvent(event);
							if (!event.isCancelled()) {
								ConfirmationListener.inst().addConfirmation(new DeleteQueue(region, p.getWorld(), p));
								// inform Player
								p.spigot().sendMessage(Lang.build(event.getMessage().replace(Lang.VALUE, region.getId()), null, Lang.DELETE_HOVER_TEXT, null),
										Lang.build(Lang.DELETE_YES, "/yes", Lang.build(Lang.DELETE_HOVER_YES), null),
										Lang.build(Lang.DELETE_NO, "/no", Lang.build(Lang.DELETE_HOVER_NO), null));
							}
						} else {
							// no permission
							p.spigot().sendMessage(Lang.build(Lang.NOPERMISSION));
						}
					}
				}
			} else {
				// no player
				cs.spigot().sendMessage(Lang.build(Lang.NOPLAYER));
			}
		} else {
			// no permission
			cs.spigot().sendMessage(Lang.build(Lang.NOPERMISSION));
		}
	}
}
