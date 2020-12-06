package de.bergtiger.claim.listener;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import de.bergtiger.claim.TigerClaim;
import de.bergtiger.claim.bdo.Confirmation;
import de.bergtiger.claim.data.Lang;
import de.bergtiger.claim.data.Perm;

import static de.bergtiger.claim.data.Cons.VALUE;
import static de.bergtiger.claim.data.Cons.LIMIT;

public class ConfirmationListener implements Listener {

	private static ConfirmationListener instance;

	public static ConfirmationListener inst() {
		if (instance == null)
			instance = new ConfirmationListener();
		return instance;
	}

	private ConfirmationListener() {
	}

	private HashMap<Player, Confirmation> queue;

	@EventHandler
	public void onConfirmation(PlayerCommandPreprocessEvent e) {
		// is something there
		if (!e.isCancelled() && queue != null && !queue.isEmpty() && queue.containsKey(e.getPlayer())) {
			if (e.getMessage().equalsIgnoreCase("/yes")) {
				Confirmation con = queue.remove(e.getPlayer());
				createRegionThread(con);
				if (queue.isEmpty())
					queue = null;
				e.setCancelled(true);
			} else if (e.getMessage().equalsIgnoreCase("/no")) {
				queue.remove(e.getPlayer());
				if (queue.isEmpty())
					queue = null;
				e.getPlayer().spigot().sendMessage(Lang.buildTC(Lang.INSERT_CANCEL.get()));
				e.setCancelled(true);
			}
		}
	}

	public void addConfirmation(Confirmation con) {
		if (con != null) {
			if (queue == null)
				queue = new HashMap<Player, Confirmation>();
			queue.put(con.getPlayer(), con);
		}
	}

	public void clearQueue() {
		if (queue != null && !queue.isEmpty())
			queue.clear();
	}

	public void clearQueue(Player p) {
		if (queue != null && !queue.isEmpty())
			queue.remove(p);
	}

	private void createRegionThread(Confirmation con) {
		Bukkit.getScheduler().runTaskAsynchronously(TigerClaim.inst(), () -> createRegion(con));
	}

	private void createRegion(Confirmation con) {
		if (con != null) {
			// create Region
			BlockVector3 min = BlockVector3.at(con.getMinX(), con.getMinY(), con.getMinZ());
			BlockVector3 max = BlockVector3.at(con.getMaxX(), con.getMaxY(), con.getMaxZ());
			ProtectedRegion region = new ProtectedCuboidRegion(con.getRegionName(), min, max);
			// get RegionManager for world
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(con.getLocation().getWorld()));
			// same name/id
			if (regions.getRegion(region.getId()) != null) {
				// existing Region
				con.getPlayer().spigot()
						.sendMessage(Lang.buildTC(Lang.INSERT_EXISTING.get().replace(VALUE, region.getId())));
				return;
			}
			// add all regions to candidates
			List<ProtectedRegion> candidates = Lists.newArrayList();
			regions.getRegions().forEach((k, r) -> {
				candidates.add(r);
				if (r.getOwners().contains(con.getPlayer().getUniqueId()))
					con.addCount();
			});

			// Get Limit
			Integer limit = null, length = Perm.CLAIM_LIMIT.get().split("\\.").length;
			try {
				limit = con.getPlayer().getEffectivePermissions().parallelStream().map(p -> p.getPermission())
						.filter(p -> p.contains(Perm.CLAIM_LIMIT.get())).mapToInt(p -> {
							try {
								String[] s = p.split("\\.");
								System.out.println("perm: " + s + ", w: " + con.getLocation().getWorld().getName());
								if (s.length == length + 2) {
									// with world
									if (con.getLocation().getWorld().getName().equalsIgnoreCase(s[s.length - 2]))
										return Integer.valueOf(s[s.length - 1]);
									return 0;
								}
								// Without world
								return Integer.valueOf(s[s.length - 1]);
							} catch (NumberFormatException e) {
								e.printStackTrace();
							}
							return 0;
						}).max().getAsInt();
			} catch (NoSuchElementException e) {
			}
//			if(con.getCount() < limit || Perm.hasPermission(con.getPlayer(), Perm.CLAIM_LIMITLESS)) {
			if ((limit != null) && (con.getCount() < limit)) {
				// Can claim
				List<ProtectedRegion> overlapping = region.getIntersectingRegions(candidates);
				// if overlapping is empty -> save region
				if (overlapping == null || overlapping.isEmpty()) {
					// no overlapping
					// Add Player as Owner (Config ?)
					DefaultDomain owners = region.getOwners();
					owners.addPlayer(con.getPlayer().getUniqueId());
					// Add Flags
					if (con.getFlags() != null)
						region.setFlags(con.getFlags());
					// Add Region to Manager - save
					regions.addRegion(region);
					con.getPlayer().spigot().sendMessage(Lang.buildTC(Lang.INSERT_SUCCESS.get(),
							"/rg i " + region.getId(), Lang.INSERT_HOVER_SUCCESS.get(), null));
				} else {
					// is overlapping
					con.getPlayer().spigot().sendMessage(Lang.buildTC(Lang.INSERT_OVERLAPPING.get()));
				}
			} else {
				// limit reached
				con.getPlayer().spigot().sendMessage(
						Lang.buildTC(Lang.INSERT_LIMIT.get().replace(VALUE, Integer.toString(con.getCount()))
								.replace(LIMIT, (limit != null) ? Integer.toString(limit) : "-")));
			}
		}
	}
}
