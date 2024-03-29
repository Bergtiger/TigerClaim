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
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import de.bergtiger.claim.Claims;
import de.bergtiger.claim.bdo.TigerClaim;
import de.bergtiger.claim.data.language.Lang;
import de.bergtiger.claim.data.permission.Perm;

import static de.bergtiger.claim.data.language.Cons.VALUE;
import static de.bergtiger.claim.data.language.Cons.LIMIT;
import static de.bergtiger.claim.data.language.Cons.PLAYER;

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
				e.getPlayer().spigot().sendMessage(Lang.build(Lang.INSERT_CANCEL));
				e.setCancelled(true);
			}
		}
	}
	
	/**
	 * add claim to queue.
	 * @param con to claim
	 */
	public void addConfirmation(TigerClaim con) {
		if (con != null) {
			if (queue == null)
				queue = new HashMap<>();
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
	 * @param p player to clear
	 */
	public void clearQueue(Player p) {
		if (queue != null && !queue.isEmpty())
			queue.remove(p);
	}

	/**
	 * surround with Thread
	 * @param con to claim
	 */
	private void createRegionThread(TigerClaim con) {
		Bukkit.getScheduler().runTaskAsynchronously(Claims.inst(), () -> createRegion(con));
	}

	/**
	 * create claim
	 * @param tc to claim
	 */
	private void createRegion(TigerClaim tc) {
		if (tc != null) {
			// get RegionManager for world
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(tc.getWorld()));
			// same name/id
			if (tc.hasRegionCounter()) {
				// addCounter until value available (fills lower values)
				while(regions.getRegion(tc.getId()) != null)
					tc.addRegionCounter();
			} else {
				if (regions.getRegion(tc.getId()) != null) {
					// existing Region
					tc.getPlayer().spigot()
						.sendMessage(Lang.build(Lang.INSERT_EXISTING.replace(VALUE, tc.getId())));
					return;
				}
			}
			// add all regions to candidates
			List<ProtectedRegion> candidates = Lists.newArrayList();
			regions.getRegions().forEach((k, r) -> {
				candidates.add(r);
				if (r.getOwners().contains(tc.getPlayer().getUniqueId()))
					tc.addPlayerRegionCount();
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
										return Integer.parseInt(s[s.length - 1]);
									return 0;
								}
								// Without world
								return Integer.parseInt(s[s.length - 1]);
							} catch (NumberFormatException e) {
								e.printStackTrace();
							}
							return 0;
						}).max().getAsInt();
			} catch (NoSuchElementException ignored) {
			}
			if (Perm.hasPermission(tc.getPlayer(), Perm.CLAIM_LIMITLESS) || ((limit != null) && (tc.getPlayerRegionCount() < limit))) {
				// Can claim
				List<ProtectedRegion> overlapping = null;
				// isOverlapping false -> not allowed to overlap
				// isOverlapping true -> allowed to overlap
				if(!tc.isOverlapping())
					overlapping = tc.getRegionWithGab().getIntersectingRegions(candidates);
				// if overlapping is empty -> save region
				if (overlapping == null || overlapping.isEmpty()) {
					// no overlapping
					// create Region without gap to add
					ProtectedRegion region = tc.getRegion();
					// Add Player as Owner
					DefaultDomain owners = region.getOwners();
					owners.addPlayer(tc.getPlayer().getUniqueId());
					// Add Flags
					if (tc.getFlags() != null) {
						HashMap<Flag<?>, Object> flags = new HashMap<>();
						tc.getFlags().forEach((f,v) -> {
							if(v instanceof String)
								v = v.toString().replace("@p", tc.getPlayer().getName()).replace(PLAYER, tc.getPlayer().getName());
							flags.put(f, v);
						});
						region.setFlags(flags);
					}
					// Add Region to Manager - save
					regions.addRegion(region);
					tc.getPlayer().spigot().sendMessage(Lang.build(Lang.INSERT_SUCCESS,
							"/rg i " + region.getId(), Lang.build(Lang.INSERT_HOVER_SUCCESS), null));
				} else {
					// is overlapping
					tc.getPlayer().spigot().sendMessage(Lang.build(Lang.INSERT_OVERLAPPING));
				}
			} else {
				// limit reached
				tc.getPlayer().spigot().sendMessage(
						Lang.build(Lang.INSERT_LIMIT.replace(VALUE, Integer.toString(tc.getPlayerRegionCount()))
								.replace(LIMIT, (limit != null) ? Integer.toString(limit) : "-")));
			}
		}
	}
}
