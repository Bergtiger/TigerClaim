package de.bergtiger.claim.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import de.bergtiger.claim.Claims;
import de.bergtiger.claim.bdo.TigerList;
import de.bergtiger.claim.data.ClaimUtils;
import de.bergtiger.claim.data.configuration.Config;
import de.bergtiger.claim.data.language.Lang;
import de.bergtiger.claim.data.permission.Perm;

import static de.bergtiger.claim.data.language.Cons.PAGE;
import static de.bergtiger.claim.data.language.Cons.PAGEMAX;
import static de.bergtiger.claim.data.language.Cons.VALUE;

public class CmdList {
	
	private static CmdList instance;
	
	public static CmdList inst() {
		if(instance == null)
			instance = new CmdList();
		return instance;
	}
	
	private CmdList() {}
	
	private final HashMap<CommandSender, TigerList<ProtectedRegion>> regionList = new HashMap<>();
	
	public static void list(CommandSender cs, String[] args) {
		Bukkit.getScheduler().runTaskAsynchronously(Claims.inst(), () -> CmdList.inst().getRegionList(cs, args));
	}
	
	public static void clear() {
		Bukkit.getScheduler().runTaskAsynchronously(Claims.inst(), () -> inst().regionList.clear());
	}
	
	/**
	 * Get regions from a player
	 * @param p player
	 * @return list of protected regions
	 */
	private TigerList<ProtectedRegion> getRegions(Player p) {
		if(p != null) {
			// get RegionManager for world
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(p.getWorld()));
			// 
			TigerList<ProtectedRegion> list = new TigerList<>();
			list.setPageSize(Config.getInt(Config.PAGE_LENGTH));
			// LocalPlayer for WorldGuard
			LocalPlayer pLocal = WorldGuardPlugin.inst().wrapPlayer(p);
			regions.getRegions().forEach((s,r) -> {
				if(r.isOwner(pLocal)) {
					list.add(r);
				}
			});
			return list;
		}
		return null;
	}
	
	/**
	 * Handle list command
	 * claim list [page]
	 * @param cs command sender
	 * @param args arguments
	 */
	private void getRegionList(CommandSender cs, String[] args) {
		if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_LIST)) {
			if(!regionList.containsKey(cs)) {
				// get RegionList
				if (cs instanceof Player) {
					regionList.put(cs, getRegions((Player)cs));
				} else {
					// No Player
					cs.spigot().sendMessage(Lang.build(Lang.NOPLAYER));
					return;
				}
			}
			if(args.length == 2) {
				TigerList<ProtectedRegion> b = regionList.get(cs);
				if(b != null) {
					try {
						b.setPage(Integer.parseInt(args[1]));
						showList(cs, b);
					} catch (NumberFormatException e) {
						cs.spigot().sendMessage(Lang.build(Lang.NONUMBERVALUE.replace(VALUE, args[1])));
					}
				}
			} else {
				showList(cs, regionList.get(cs));
			}
		} else {
			cs.spigot().sendMessage(Lang.build(Lang.NOPERMISSION));
		}
	}
	
	/**
	 * handle show player list of regions
	 * @param cs command sender
	 * @param regionList list to show
	 */
	public void showList(CommandSender cs, TigerList<ProtectedRegion> regionList) {
		if(regionList != null && !regionList.isEmpty()) {
			List<TextComponent> components = new ArrayList<>();
			// Header
			components.add(Lang.build(Lang.LIST_HEADER.replace(VALUE, cs.getName())));
			components.add(Lang.newLine());
			// Buttons
			for(int i = 0; (i < regionList.getPageSize()) && ((regionList.getPage() * regionList.getPageSize() + i) < regionList.size()); i++) {
				try {
					// Show Regions
					ProtectedRegion r = regionList.get(regionList.getPage() * regionList.getPageSize() + i);
					components.add(Lang.build(
							Lang.LIST_ROW.replace(VALUE, r.getId()),
							"/rg i " + r.getId(),
							Lang.build(ClaimUtils.buildRegionHover(r)),
							null));
					components.add(Lang.newLine());
				} catch(ArrayIndexOutOfBoundsException e) {
					break;
				}
			}
			// Footer
			components.add(Lang.combine(
						Lang.build(
								Lang.LIST_FOOTER_PREV,
								(String.format("/%s %s %d", Claim.CMD, Claim.LIST, (regionList.getPage() - 1))),
								null,
								null),
						Lang.build(
								Lang.LIST_FOOTER_PLAYER.replace(PAGE, Integer.toString(regionList.getPage() + 1)).replace(PAGEMAX, Integer.toString(regionList.getPageMax()))),
						Lang.build(
								Lang.LIST_FOOTER_NEXT,
								(String.format("/%s %s %d", Claim.CMD, Claim.LIST, (regionList.getPage() + 1))),
								null,
								null)));
			// show message to player
			cs.spigot().sendMessage(Lang.combine(components));
		} else {
			// No Regions
			cs.spigot().sendMessage(Lang.build(Lang.NOREGIONS));
		}
	}
}
