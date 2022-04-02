package de.bergtiger.claim.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import de.bergtiger.claim.bdo.*;
import de.bergtiger.claim.cmd.CmdClaim;
import de.bergtiger.claim.cmd.CmdExpand;
import de.bergtiger.claim.data.ClaimUtils;
import de.bergtiger.claim.events.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
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

	private ConfirmationListener() {
	}

	private HashMap<Player, Object> queue;

	@EventHandler
	public void onConfirmation(PlayerCommandPreprocessEvent e) {
		// is something there
		if (!e.isCancelled() && queue != null && !queue.isEmpty() && queue.containsKey(e.getPlayer())) {
			if (e.getMessage().equalsIgnoreCase("/yes")) {
				Object con = queue.remove(e.getPlayer());
				if (con instanceof TigerClaim tc) {
					createRegionThread(tc);
				} else if (con instanceof DeleteQueue dq) {
					deleteRegion(dq);
				} else if (con instanceof CheckQueue cq) {
					checkRegion(cq);
				} else if (con instanceof ExpandSelectionalQueue esq) {
					if (esq.isCheck()) {
						expandCheckRegionWithSelection(esq);
					} else {
						expandRegionWithSelection(esq);
					}
				} else if (con instanceof ExpandDirectionalQueue edq) {
					if (edq.isCheck()) {
						expandCheckRegionDirectional(edq);
					} else {
						expandRegionDirectional(edq);
					}
				} else if (con instanceof RetractSelectionalQueue rsq) {
					retractRegionWithSelection(rsq);
				} else if (con instanceof RetractDirectionalQueue rdq) {
					retractRegionDirectional(rdq);
				} else if (con instanceof AdjustHeightsQueue ahq) {
					adjustHeights(ahq);
				}
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
	 *
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
	 * add delete to queue.
	 *
	 * @param con to claim
	 */
	public void addConfirmation(DeleteQueue con) {
		if (con != null) {
			if (queue == null)
				queue = new HashMap<>();
			queue.put(con.getPlayer(), con);
		}
	}

	/**
	 * add check to queue.
	 *
	 * @param con to claim
	 */
	public void addConfirmation(CheckQueue con) {
		if (con != null) {
			if (queue == null)
				queue = new HashMap<>();
			queue.put(con.getRegion().getPlayer(), con);
		}
	}

	/**
	 * add expand directional to queue.
	 *
	 * @param con to claim
	 */
	public void addConfirmation(ExpandDirectionalQueue con) {
		if (con != null) {
			if (queue == null)
				queue = new HashMap<>();
			queue.put(con.getPlayer(), con);
		}
	}

	/**
	 * add expand selection to queue.
	 *
	 * @param con to claim
	 */
	public void addConfirmation(ExpandSelectionalQueue con) {
		if (con != null) {
			if (queue == null)
				queue = new HashMap<>();
			queue.put(con.getPlayer(), con);
		}
	}

	/**
	 * add retract directional to queue.
	 *
	 * @param con to claim
	 */
	public void addConfirmation(RetractDirectionalQueue con) {
		if (con != null) {
			if (queue == null)
				queue = new HashMap<>();
			queue.put(con.getPlayer(), con);
		}
	}

	/**
	 * add retract selection to queue.
	 *
	 * @param con to claim
	 */
	public void addConfirmation(RetractSelectionalQueue con) {
		if (con != null) {
			if (queue == null)
				queue = new HashMap<>();
			queue.put(con.getPlayer(), con);
		}
	}

	/**
	 * add heights adjusting to queue.
	 *
	 * @param con to claim
	 */
	public void addConfirmation(AdjustHeightsQueue con) {
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
	 *
	 * @param p player to clear
	 */
	public void clearQueue(Player p) {
		if (queue != null && !queue.isEmpty())
			queue.remove(p);
	}

	/**
	 * surround with Thread
	 *
	 * @param con to claim
	 */
	private void createRegionThread(TigerClaim con) {
		Bukkit.getScheduler().runTask(Claims.inst(), () -> createRegion(con));
	}

	/**
	 * create claim
	 *
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
				while (regions.getRegion(tc.getId()) != null)
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
				if (!tc.isOverlapping())
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
						tc.getFlags().forEach((f, v) -> {
							if (v instanceof String)
								v = v.toString().replace("@p", tc.getPlayer().getName()).replace(PLAYER, tc.getPlayer().getName());
							flags.put(f, v);
						});
						region.setFlags(flags);
					}
					//call RegionClaimEvent
					RegionClaimEvent event = new RegionClaimEvent(region, tc.getWorld(), tc.getPlayer(), tc.getArea(), Lang.INSERT_SUCCESS.get());
					Bukkit.getPluginManager().callEvent(event);
					if (!event.isCancelled()) {
						// Add Region to Manager - save
						regions.addRegion(region);

						tc.getPlayer().spigot().sendMessage(Lang.build(event.getMessage(),
								"/rg i " + region.getId(), Lang.build(Lang.INSERT_HOVER_SUCCESS), null));
					}
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

	/**
	 * delete claim
	 *
	 * @param dq to delete region
	 */
	private void deleteRegion(DeleteQueue dq) {
		RegionDeleteEvent event = new RegionDeleteEvent(dq.getRegion(), dq.getWorld(), dq.getPlayer(), ClaimUtils.getArea(dq.getRegion()), Lang.DELETE_SUCCESS.get());
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			// get RegionManager for world
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(dq.getWorld()));
			regions.removeRegion(dq.getRegion().getId());
			dq.getPlayer().spigot().sendMessage(Lang.build(event.getMessage().replace(VALUE, event.getRegion().getId())));
		}
	}

	/**
	 * check claim
	 *
	 * @param cq to check region
	 */
	private void checkRegion(CheckQueue cq) {
		if (cq != null) {
			// get RegionManager for world
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(cq.getRegion().getPlayer().getWorld()));
			// add all regions to candidates
			List<ProtectedRegion> candidates = Lists.newArrayList();
			regions.getRegions().forEach((k, r) -> {
				candidates.add(r);
				if (r.getOwners().contains(cq.getRegion().getPlayer().getUniqueId()))
					cq.getRegion().addPlayerRegionCount();
			});
			// Get Limit
			Integer limit = null, length = Perm.CLAIM_LIMIT.get().split("\\.").length;
			try {
				limit = cq.getRegion().getPlayer().getEffectivePermissions().parallelStream().map(p -> p.getPermission())
						.filter(p -> p.contains(Perm.CLAIM_LIMIT.get())).mapToInt(p -> {
							try {
								String[] s = p.split("\\.");
								if (s.length == length + 2) {
									// with world
									if (cq.getRegion().getPlayer().getWorld().getName().equalsIgnoreCase(s[s.length - 2]))
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
			// Can claim
			List<ProtectedRegion> overlapping = null;
			// isOverlapping false -> not allowed to overlap
			// isOverlapping true -> allowed to overlap
			if (!cq.getRegion().isOverlapping())
				overlapping = cq.getRegion().getRegionWithGab().getIntersectingRegions(candidates);

			// call RegionCheckEvent
			RegionCheckEvent event;
			// if overlapping is empty -> save region
			if (overlapping == null || overlapping.isEmpty()) {
				if (Perm.hasPermission(cq.getRegion().getPlayer(), Perm.CLAIM_LIMITLESS) || ((limit != null) && (cq.getRegion().getPlayerRegionCount() < limit))) {
					// not overlapping, not limit reached
					event = new RegionCheckEvent(cq.getRegion(), cq.getRegion().getPlayer(), Lang.CHECK_AVAILABLE.get(), false, false);
				} else {
					// limit reached
					event = new RegionCheckEvent(cq.getRegion(), cq.getRegion().getPlayer(), Lang.CHECK_LIMIT.replace(VALUE, Integer.toString(limit)), true, false);
				}
			} else {
				// is overlapping
				event = new RegionCheckEvent(cq.getRegion(), cq.getRegion().getPlayer(), Lang.CHECK_OVERLAPPING.get(), false, true);
			}

			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				event.getPlayer().spigot().sendMessage(Lang.build(event.getMessage()));
				if (event.continueClaim())
					CmdClaim.claim(cq.getRegion().getPlayer(), true);
			}
		}
	}

	/**
	 * expand claim with selection
	 *
	 * @param esq to expand region with world edit selection
	 */
	private static void expandRegionWithSelection(ExpandSelectionalQueue esq) {
		if (esq != null) {
			Player player = esq.getPlayer();
			// get RegionManager for world
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(esq.getWorld()));
			// add all regions to candidates
			List<ProtectedRegion> candidates = Lists.newArrayList();
			regions.getRegions().forEach((k, r) -> {
				candidates.add(r);
			});
			ProtectedRegion oldRegion = esq.getRegion();
			Polygonal2DRegion newRegion = new Polygonal2DRegion(BukkitAdapter.adapt(esq.getWorld()) , esq.getEckpunkteDerNeuenRegion(), oldRegion.getMinimumPoint().getY(), oldRegion.getMaximumPoint().getY());
			TigerClaim tc = new TigerClaimPolygon(esq.getPlayer(), esq.getWorld(), newRegion);
			// Can claim
			List<ProtectedRegion> overlapping = null;
			// isOverlapping false -> not allowed to overlap
			// isOverlapping true -> allowed to overlap
			if (!tc.isOverlapping()) {
				overlapping = tc.getRegionWithGab().getIntersectingRegions(candidates);
				overlapping.remove(oldRegion);
			}
			// if overlapping is empty -> save region
			if (overlapping == null || overlapping.isEmpty()) {
				String message = "Region " + oldRegion.getId() + " erfolgreich um Markierung erweitert.";
				RegionExpandEvent event = new RegionExpandEvent(
						player, esq.getWorld(), oldRegion, newRegion, esq.getSelection(), false, esq.isRegionAngegeben(), ClaimUtils.getArea(oldRegion), ClaimUtils.getArea(newRegion),
						null, null, esq.getContainsGapsFromOldRegionAndSelection(), message);
				Bukkit.getPluginManager().callEvent(event);
				if (!event.isCancelled()) {
					Map<Flag<?>,Object> flags = oldRegion.getFlags();
					DefaultDomain members = oldRegion.getMembers();
					int priority = oldRegion.getPriority();
					DefaultDomain owners = oldRegion.getOwners();
					regions.removeRegion(oldRegion.getId());
					ProtectedPolygonalRegion newProtectedRegion = new ProtectedPolygonalRegion(oldRegion.getId(),esq.getEckpunkteDerNeuenRegion(),oldRegion.getMinimumPoint().getY(), oldRegion.getMaximumPoint().getY());
					newProtectedRegion.setFlags(flags);
					newProtectedRegion.setMembers(members);
					newProtectedRegion.setPriority(priority);
					newProtectedRegion.setOwners(owners);
					regions.addRegion(newProtectedRegion);
					/*
					oldRegion.getPoints().clear();
					oldRegion.getPoints().addAll(esq.getEckpunkteDerNeuenRegion());

					 */
					esq.getPlayer().spigot().sendMessage(Lang.build(event.getMessage()));
				}
			} else {
				// is overlapping
				if (esq.isRegionAngegeben()) {
					esq.getPlayer().spigot().sendMessage(Lang.build("Die angegebene Region lässt sich nicht um die Markierung erweitern, " +
							"da die Markierung mit anderen Grundstücken überlappt/anderen Grundstücken zu nahe ist."));
				} else {
					esq.getPlayer().spigot().sendMessage(Lang.build("Die Region, in der du stehst, lässt sich nicht um die Markierung erweitern, " +
							"da die Markierung mit anderen Grundstücken überlappt/anderen Grundstücken zu nahe ist."));
				}
			}
		}
	}

	/**
	 * expand claim directional
	 *
	 * @param edq to expand region directional
	 */
	private static void expandRegionDirectional(ExpandDirectionalQueue edq) {
		if (edq != null) {
			Player player = edq.getPlayer();
			// get RegionManager for world
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(edq.getWorld()));
			// add all regions to candidates
			List<ProtectedRegion> candidates = Lists.newArrayList();
			regions.getRegions().forEach((k, r) -> {
				candidates.add(r);
			});
			ProtectedCuboidRegion oldRegion = (ProtectedCuboidRegion) edq.getRegion();
			CuboidRegion newRegion = newDirectionalExpandedRegion((ProtectedCuboidRegion) edq.getRegion(), edq.getWorld(), edq.getDirection(), edq.getExtendLength());
			TigerClaim tc = new TigerClaimCuboid(edq.getPlayer(), edq.getWorld(), newRegion);
			// Can claim
			List<ProtectedRegion> overlapping = null;
			// isOverlapping false -> not allowed to overlap
			// isOverlapping true -> allowed to overlap
			if (!tc.isOverlapping()) {
				overlapping = tc.getRegionWithGab().getIntersectingRegions(candidates);
				overlapping.remove(oldRegion);
			}
			if (overlapping == null || overlapping.isEmpty()) {
				String message = "Region " + oldRegion.getId() + " erfolgreich " + edq.getExtendLength() + " Blöcke nach " + edq.getDirection().name() + " erweitert.";
				CuboidRegion expansion = CmdExpand.theDirectionalExpansion(edq.getDirection(), oldRegion, edq.getExtendLength());
				RegionExpandEvent event = new RegionExpandEvent(
						player, edq.getWorld(), oldRegion, newRegion, expansion, true, edq.isRegionAngegeben(), ClaimUtils.getArea(oldRegion), ClaimUtils.getArea(newRegion),
						edq.getDirection(), edq.getExtendLength(), false, message);
				Bukkit.getPluginManager().callEvent(event);
				if (!event.isCancelled()) {
					// oldRegion wird expandiert (durch newRegion ersetzt)
					Map<Flag<?>, Object> flags = oldRegion.getFlags();
					DefaultDomain members = oldRegion.getMembers();
					int priority = oldRegion.getPriority();
					DefaultDomain owners = oldRegion.getOwners();
					regions.removeRegion(oldRegion.getId());
					ProtectedCuboidRegion newProtectedRegion = new ProtectedCuboidRegion(oldRegion.getId(), newRegion.getMinimumPoint(), newRegion.getMaximumPoint());
					newProtectedRegion.setFlags(flags);
					newProtectedRegion.setMembers(members);
					newProtectedRegion.setPriority(priority);
					newProtectedRegion.setOwners(owners);
					regions.addRegion(newProtectedRegion);
					edq.getPlayer().spigot().sendMessage(Lang.build(event.getMessage()));
				}
			} else {
				if (edq.isRegionAngegeben()) {
					edq.getPlayer().spigot().sendMessage(Lang.build("Die angegebene Region lässt sich nicht " + edq.getExtendLength() + " Blöcke nach " + edq.getDirection() +
							" erweitern, da die Region dann mit anderen Grundstücken überlappen würde/anderen Grundstücken zu nah wäre."));
				} else {
					edq.getPlayer().spigot().sendMessage(Lang.build("Die Region, in der du stehst, lässt sich nicht " + edq.getExtendLength() + " Blöcke nach " + edq.getDirection() +
							" erweitern, da die Region dann mit anderen Grundstücken überlappen würde/anderen Grundstücken zu nah wäre."));
				}
			}
		}
	}

	/**
	 * expand check claim with selection
	 *
	 * @param esq to expand check region with world edit selection
	 */
	private static void expandCheckRegionWithSelection(ExpandSelectionalQueue esq) {
		if (esq != null) {
			Player player = esq.getPlayer();
			// get RegionManager for world
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(esq.getWorld()));
			// add all regions to candidates
			List<ProtectedRegion> candidates = Lists.newArrayList();
			regions.getRegions().forEach((k, r) -> {
				candidates.add(r);
			});
			ProtectedRegion oldRegion = esq.getRegion();
			Polygonal2DRegion newRegion = new Polygonal2DRegion(BukkitAdapter.adapt(esq.getWorld()) , esq.getEckpunkteDerNeuenRegion(), oldRegion.getMinimumPoint().getY(), oldRegion.getMaximumPoint().getY());
			TigerClaim tc = new TigerClaimPolygon(esq.getPlayer(), esq.getWorld(), newRegion);
			// Can claim
			List<ProtectedRegion> overlapping = null;
			// isOverlapping false -> not allowed to overlap
			// isOverlapping true -> allowed to overlap
			if (!tc.isOverlapping()) {
				overlapping = tc.getRegionWithGab().getIntersectingRegions(candidates);
				overlapping.remove(oldRegion);
			}
			String message;
			boolean regionIsOverlappingAndShouldNot = overlapping != null && !overlapping.isEmpty();
			if (regionIsOverlappingAndShouldNot) {
				if (esq.isRegionAngegeben()) {
					message = "Die Fläche deiner Markierung ist nicht als Erweiterung für dein angegebenes Grundstück verfügbar.";
				} else {
					message = "Die Fläche deiner Markierung ist nicht als Erweiterung für dein Grundstück, in dem du stehst, verfügbar.";
				}
			} else {
				if (esq.isRegionAngegeben()) {
					message = "Die Fläche deiner Markierung ist verfügbar.";
				} else {
					message = "Die Fläche deiner Markierung ist verfügbar.";
				}
			}
			ExpandCheckEvent event = new ExpandCheckEvent(
					player, false, esq.isRegionAngegeben(), regionIsOverlappingAndShouldNot, ClaimUtils.getArea(oldRegion), ClaimUtils.getArea(newRegion),
					null, null, message);
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				player.spigot().sendMessage(Lang.build(event.getMessage()));
				if (event.continueExpand()) {
					CmdExpand.expandQuestion(player, esq.getWorld(), oldRegion, esq.isRegionAngegeben(), ClaimUtils.getArea(oldRegion), ClaimUtils.getArea(newRegion), false,
							(Polygonal2DRegion) esq.getSelection(), esq.getContainsGapsFromOldRegionAndSelection(),
							null, null, esq.getEckpunkteDerNeuenRegion());
				}
			}
		}
	}

	/**
	 * expand check claim directional
	 *
	 * @param edq to expand check region directional
	 */
	private static void expandCheckRegionDirectional (ExpandDirectionalQueue edq) {
		if (edq != null) {
			Player player = edq.getPlayer();
			// get RegionManager for world
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(edq.getWorld()));
			// add all regions to candidates
			List<ProtectedRegion> candidates = Lists.newArrayList();
			regions.getRegions().forEach((k, r) -> {
				candidates.add(r);
			});
			ProtectedCuboidRegion oldRegion = (ProtectedCuboidRegion) edq.getRegion();
			CuboidRegion newRegion = newDirectionalExpandedRegion((ProtectedCuboidRegion) edq.getRegion(), edq.getWorld(), edq.getDirection(), edq.getExtendLength());
			TigerClaim tc = new TigerClaimCuboid(player, edq.getWorld(), newRegion);
			// Can claim
			List<ProtectedRegion> overlapping = null;
			// isOverlapping false -> not allowed to overlap
			// isOverlapping true -> allowed to overlap
			if (!tc.isOverlapping()) {
				overlapping = tc.getRegionWithGab().getIntersectingRegions(candidates);
				overlapping.remove(oldRegion);
			}
			//call RegionClaimEvent
			String message;
			boolean regionIsOverlappingAndShouldNot = overlapping != null && !overlapping.isEmpty();
			if (regionIsOverlappingAndShouldNot) {
				if (edq.isRegionAngegeben()) {
					message = "Die angegebene Region lässt sich nicht " + edq.getExtendLength() + " Blöcke nach " + edq.getDirection() +
							" erweitern, da die Region dann mit anderen Grundstücken überlappen würde/anderen Grundstücken zu nah wäre.";
				} else {
					message = "Die Region, in der du stehst, lässt sich nicht " + edq.getExtendLength() + " Blöcke nach " + edq.getDirection() +
							" erweitern, da die Region dann mit anderen Grundstücken überlappen würde/anderen Grundstücken zu nah wäre.";
				}
			} else {
				message = "Die Fläche wäre für eine Erweiterung verfügbar.";
			}
			ExpandCheckEvent event = new ExpandCheckEvent(
					player, true, edq.isRegionAngegeben(), regionIsOverlappingAndShouldNot, ClaimUtils.getArea(oldRegion), ClaimUtils.getArea(newRegion),
					edq.getDirection(), edq.getExtendLength(), message);
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				player.spigot().sendMessage(Lang.build(event.getMessage()));
				if (!regionIsOverlappingAndShouldNot) {
					CmdExpand.expandQuestion(player, edq.getWorld(), oldRegion,  edq.isRegionAngegeben(), event.getOldArea(), event.getNewArea(), true,
							null, null,
							edq.getDirection(), edq.getExtendLength(), null);
				}
			}
		}
	}

	private static CuboidRegion newDirectionalExpandedRegion (ProtectedCuboidRegion oldRegion, World world, BlockFace direction, int extendLength) {
		// get RegionManager for world
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(world));
		// add all regions to candidates
		List<ProtectedRegion> candidates = Lists.newArrayList();
		regions.getRegions().forEach((k, r) -> {
			candidates.add(r);
		});
		CuboidRegion newRegion = null;
		switch (direction) {
			case NORTH -> newRegion = new CuboidRegion(
					oldRegion.getMaximumPoint(),
					oldRegion.getMinimumPoint().add(BlockVector3.at(0,0,-extendLength)));
			case EAST -> newRegion = new CuboidRegion(
					oldRegion.getMaximumPoint().add(BlockVector3.at(extendLength,0,0)),
					oldRegion.getMinimumPoint());
			case SOUTH -> newRegion = new CuboidRegion(
					oldRegion.getMaximumPoint().add(BlockVector3.at(0,0,extendLength)),
					oldRegion.getMinimumPoint());
			case WEST -> newRegion = new CuboidRegion(
					oldRegion.getMaximumPoint(),
					oldRegion.getMinimumPoint().add(BlockVector3.at(-extendLength,0,0)));
		}
		return newRegion;
	}

	/**
	 * retract claim with selection
	 *
	 * @param rsq to retract region with world edit selection
	 */
	private static void retractRegionWithSelection(RetractSelectionalQueue rsq) {
		if (rsq != null) {
			Player player = rsq.getPlayer();
			// get RegionManager for world
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(rsq.getWorld()));
			ProtectedRegion oldRegion = rsq.getRegion();
			Polygonal2DRegion newRegion = new Polygonal2DRegion(BukkitAdapter.adapt(rsq.getWorld()) , rsq.getEckpunkteDerNeuenRegion(), oldRegion.getMinimumPoint().getY(), oldRegion.getMaximumPoint().getY());
			String message = "Region " + oldRegion.getId() + " erfolgreich um Markierung beschnitten.";
			RegionRetractEvent event = new RegionRetractEvent(
					player, rsq.getWorld(), oldRegion, newRegion, rsq.getSelection(), false, rsq.isRegionAngegeben(), ClaimUtils.getArea(oldRegion), ClaimUtils.getArea(newRegion),
					null, null, message);
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				Map<Flag<?>,Object> flags = oldRegion.getFlags();
				DefaultDomain members = oldRegion.getMembers();
				int priority = oldRegion.getPriority();
				DefaultDomain owners = oldRegion.getOwners();
				regions.removeRegion(oldRegion.getId());
				ProtectedPolygonalRegion newProtectedRegion = new ProtectedPolygonalRegion(oldRegion.getId(),rsq.getEckpunkteDerNeuenRegion(),oldRegion.getMinimumPoint().getY(), oldRegion.getMaximumPoint().getY());
				newProtectedRegion.setFlags(flags);
				newProtectedRegion.setMembers(members);
				newProtectedRegion.setPriority(priority);
				newProtectedRegion.setOwners(owners);
				regions.addRegion(newProtectedRegion);

				rsq.getPlayer().spigot().sendMessage(Lang.build(event.getMessage()));
			}
		}
	}

	/**
	 * retract claim directional
	 *
	 * @param rdq to retract region directional
	 */
	private static void retractRegionDirectional(RetractDirectionalQueue rdq) {
		if (rdq != null) {
			Player player = rdq.getPlayer();
			// get RegionManager for world
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(rdq.getWorld()));
			ProtectedCuboidRegion oldRegion = (ProtectedCuboidRegion) rdq.getRegion();
			CuboidRegion newRegion = newDirectionalExpandedRegion((ProtectedCuboidRegion) rdq.getRegion(), rdq.getWorld(), rdq.getDirection(), - rdq.getExtendLength());
			String message = "Region " + oldRegion.getId() + " erfolgreich " + rdq.getExtendLength() + " Blöcke auf Seite " + rdq.getDirection().name() + " verkürzt.";
			CuboidRegion retraction = CmdExpand.theDirectionalExpansion(rdq.getDirection(), oldRegion, - rdq.getExtendLength());
			RegionRetractEvent event = new RegionRetractEvent(
					player, rdq.getWorld(), oldRegion, newRegion, retraction, true, rdq.isRegionAngegeben(), ClaimUtils.getArea(oldRegion), ClaimUtils.getArea(newRegion),
					rdq.getDirection(), rdq.getExtendLength(), message);
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				// oldRegion wird verkleinert (durch newRegion ersetzt)
				Map<Flag<?>, Object> flags = oldRegion.getFlags();
				DefaultDomain members = oldRegion.getMembers();
				int priority = oldRegion.getPriority();
				DefaultDomain owners = oldRegion.getOwners();
				regions.removeRegion(oldRegion.getId());
				ProtectedCuboidRegion newProtectedRegion = new ProtectedCuboidRegion(oldRegion.getId(), newRegion.getMinimumPoint(), newRegion.getMaximumPoint());
				newProtectedRegion.setFlags(flags);
				newProtectedRegion.setMembers(members);
				newProtectedRegion.setPriority(priority);
				newProtectedRegion.setOwners(owners);
				regions.addRegion(newProtectedRegion);
				rdq.getPlayer().spigot().sendMessage(Lang.build(event.getMessage()));
			}
		}
	}

	/**
	 * change claim heights
	 *
	 * @param ahq to change region heights
	 */
	private static void adjustHeights(AdjustHeightsQueue ahq) {
		// get RegionManager for world
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(ahq.getWorld()));
		ProtectedRegion oldRegion = ahq.getRegion();
		String message = "Die Höhen der Region " + oldRegion.getId() + " wurden erfolgreich angepasst.";
		ProtectedRegion newRegion;
		if (oldRegion instanceof ProtectedPolygonalRegion) {
			newRegion = new ProtectedPolygonalRegion(oldRegion.getId(), oldRegion.getPoints(), ahq.getNewMinHeight(), ahq.getNewMaxHeight());
		} else {
			newRegion = new ProtectedCuboidRegion(oldRegion.getId(),
					BlockVector3.at(oldRegion.getMinimumPoint().getX(), ahq.getNewMinHeight(), oldRegion.getMinimumPoint().getZ()),
					BlockVector3.at(oldRegion.getMaximumPoint().getX(), ahq.getNewMaxHeight(), oldRegion.getMaximumPoint().getZ()));
		}
		// oldRegion wird höhenverändert (durch newRegion ersetzt)
		Map<Flag<?>, Object> flags = oldRegion.getFlags();
		DefaultDomain members = oldRegion.getMembers();
		int priority = oldRegion.getPriority();
		DefaultDomain owners = oldRegion.getOwners();
		newRegion.setFlags(flags);
		newRegion.setMembers(members);
		newRegion.setPriority(priority);
		newRegion.setOwners(owners);

		RegionHeightsAdjustmentEvent event = new RegionHeightsAdjustmentEvent(oldRegion, newRegion, ahq.getWorld(),
				oldRegion.getMinimumPoint().getY(), oldRegion.getMaximumPoint().getY(), ahq.getNewMinHeight(), ahq.getNewMaxHeight());
		Bukkit.getPluginManager().callEvent(event);
		regions.removeRegion(oldRegion.getId());
		regions.addRegion(newRegion);
		ahq.getPlayer().spigot().sendMessage(Lang.build(message));
	}
}
