package de.bergtiger.claim.listener;

import java.util.HashMap;
import java.util.List;

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

import de.bergtiger.claim.bdo.Confirmation;
import de.bergtiger.claim.data.Lang;

public class ConfirmationListener implements Listener {

	private static ConfirmationListener instance;
	
	public static ConfirmationListener inst() {
		if(instance == null)
			instance = new ConfirmationListener();
		return instance;
	}
	
	private ConfirmationListener() {
	}
	
	private HashMap<Player, Confirmation> queue;
	
	@EventHandler
	public void onConfirmation(PlayerCommandPreprocessEvent e) {
		System.out.println("confirmation: " + e.getPlayer().getName() + ", " + e.getMessage());
		// is something there
		if(!e.isCancelled() && queue != null && !queue.isEmpty() && queue.containsKey(e.getPlayer())) {
			if(e.getMessage().equalsIgnoreCase("/yes")) {
				Confirmation con = queue.remove(e.getPlayer());
				createRegion(con);
				if(queue.isEmpty())
					queue = null;
				e.setCancelled(true);
			} else if(e.getMessage().equalsIgnoreCase("/no")) {
				queue.remove(e.getPlayer());
				if(queue.isEmpty())
					queue = null;
				e.setCancelled(true);
			}
		}
	}
	
	public void addConfirmation(Confirmation con) {
		if(con != null) {
			if(queue == null)
				queue = new HashMap<Player, Confirmation>();
			queue.put(con.getPlayer(), con);
		}
	}
	
	private void createRegion(Confirmation con) {
		if(con != null) {
			BlockVector3 min = BlockVector3.at(con.getMinX(),   con.getMinY(), con.getMinZ());
			BlockVector3 max = BlockVector3.at(con.getMaxX(),   con.getMaxY(), con.getMaxZ());
			ProtectedRegion region = new ProtectedCuboidRegion(con.getRegionName(), min, max);
		
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(con.getLocation().getWorld()));
			// add all regions to candidates
			List<ProtectedRegion> candidates = Lists.newArrayList();
			regions.getRegions().forEach((k, r) -> candidates.add(r));
			List<ProtectedRegion> overlapping = region.getIntersectingRegions(candidates);
			// if overlapping is empty -> save region
			if(overlapping == null || overlapping.isEmpty()) {
				// no overlapping
				// Add Player as Owner (Config ?)
				DefaultDomain owners = region.getOwners();
				owners.addPlayer(con.getPlayer().getUniqueId());
				// Add Flags
				if(con.getFlags() != null)
					region.setFlags(con.getFlags());
				// Add Region to Manager - save
				regions.addRegion(region);
				con.getPlayer().spigot().sendMessage(Lang.buildTC("Saved Claim"));
			} else {
				// is overlapping
				con.getPlayer().spigot().sendMessage(Lang.buildTC("Overlapping Regions"));
			}
		}
	}
}
