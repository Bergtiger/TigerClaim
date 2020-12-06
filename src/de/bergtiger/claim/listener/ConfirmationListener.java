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
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import de.bergtiger.claim.Claims;
import de.bergtiger.claim.bdo.TigerClaim;
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

	private ConfirmationListener() {}
	
	private HashMap<Player, TigerClaim> queue;

	@EventHandler
	public void onConfirmation(PlayerCommandPreprocessEvent e) {
		// is something there
		if (!e.isCancelled() && queue != null && !queue.isEmpty() && queue.containsKey(e.getPlayer())) {
			if (e.getMessage().equalsIgnoreCase("/yes")) {
				TigerClaim con = queue.remove(e.getPlayer());
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
	
	/**
	 * add claim to queue.
	 * @param con
	 */
	public void addConfirmation(TigerClaim con) {
		if (con != null) {
			if (queue == null)
				queue = new HashMap<Player, TigerClaim>();
			queue.put(con.getPlayer(), con);
		}
	}

	/**
	 * clear claim queue
	 */
	public void clearQueue() {
		if (queue != null && !queue.isEmpty())
			queue.clear();
	}

	/**
	 * clear claim queue
	 * @param p
	 */
	public void clearQueue(Player p) {
		if (queue != null && !queue.isEmpty())
			queue.remove(p);
	}

	/**
	 * sorround with Thread
	 * @param con
	 */
	private void createRegionThread(TigerClaim con) {
		Bukkit.getScheduler().runTaskAsynchronously(Claims.inst(), () -> createRegion(con));
	}

	/**
	 * create claim
	 * @param tc
	 */
	private void createRegion(TigerClaim tc) {
		if (tc != null) {
			// create Region
			// with gab to check overlapping
			ProtectedRegion region = tc.getRegionWithGab();
			// get RegionManager for world
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(tc.getWorld()));
			// same name/id
			if (regions.getRegion(region.getId()) != null) {
				// existing Region
				tc.getPlayer().spigot()
						.sendMessage(Lang.buildTC(Lang.INSERT_EXISTING.get().replace(VALUE, region.getId())));
				return;
			}
			// add all regions to candidates
			List<ProtectedRegion> candidates = Lists.newArrayList();
			regions.getRegions().forEach((k, r) -> {
				candidates.add(r);
				if (r.getOwners().contains(tc.getPlayer().getUniqueId()))
					tc.addCount();
			});
			// Get Limit
			Integer limit = null, length = Perm.CLAIM_LIMIT.get().split("\\.").length;
			try {
				limit = tc.getPlayer().getEffectivePermissions().parallelStream().map(p -> p.getPermission())
						.filter(p -> p.contains(Perm.CLAIM_LIMIT.get())).mapToInt(p -> {
							try {
								String[] s = p.split("\\.");
								if (s.length == length + 2) {
									// with world
									if (tc.getWorld().getName().equalsIgnoreCase(s[s.length - 2]))
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
			if ((limit != null) && (tc.getCount() < limit)) {
				// Can claim
				List<ProtectedRegion> overlapping = region.getIntersectingRegions(candidates);
				// if overlapping is empty -> save region
				if (overlapping == null || overlapping.isEmpty()) {
					// no overlapping
					// without gab to add
					region = tc.getRegion();
					// Add Player as Owner (Config ?)
					DefaultDomain owners = region.getOwners();
					owners.addPlayer(tc.getPlayer().getUniqueId());
					// Add Flags
					if (tc.getFlags() != null)
						region.setFlags(tc.getFlags());
					// Add Region to Manager - save
					regions.addRegion(region);
					tc.getPlayer().spigot().sendMessage(Lang.buildTC(Lang.INSERT_SUCCESS.get(),
							"/rg i " + region.getId(), Lang.INSERT_HOVER_SUCCESS.get(), null));
				} else {
					// is overlapping
					tc.getPlayer().spigot().sendMessage(Lang.buildTC(Lang.INSERT_OVERLAPPING.get()));
				}
			} else {
				// limit reached
				tc.getPlayer().spigot().sendMessage(
						Lang.buildTC(Lang.INSERT_LIMIT.get().replace(VALUE, Integer.toString(tc.getCount()))
								.replace(LIMIT, (limit != null) ? Integer.toString(limit) : "-")));
			}
		}
	}
}
