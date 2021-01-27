package de.bergtiger.claim.cmd;

import java.util.HashMap;

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
import de.bergtiger.claim.data.Config;
import de.bergtiger.claim.data.Lang;
import de.bergtiger.claim.data.Perm;

import static de.bergtiger.claim.data.Cons.PAGE;
import static de.bergtiger.claim.data.Cons.PAGEMAX;
import static de.bergtiger.claim.data.Cons.VALUE;

public class CmdList {
	
	private static CmdList instance;
	
	public static CmdList inst() {
		if(instance == null)
			instance = new CmdList();
		return instance;
	}
	
	private CmdList() {}
	
	private HashMap<CommandSender, TigerList<ProtectedRegion>> regionList = new HashMap<CommandSender, TigerList<ProtectedRegion>>();
	
	public static void list(CommandSender cs, String[] args) {
		Bukkit.getScheduler().runTaskAsynchronously(Claims.inst(), () -> CmdList.inst().getRegionList(cs, args));
	}
	
	public static void clear() {
		Bukkit.getScheduler().runTaskAsynchronously(Claims.inst(), () -> inst().regionList.clear());
	}
	
	/**
	 * 
	 * @param w
	 * @return
	 */
	private TigerList<ProtectedRegion> getRegions(Player p) {
		if(p != null) {
			// get RegionManager for world
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(p.getWorld()));
			// 
			TigerList<ProtectedRegion> list = new TigerList<ProtectedRegion>();
			if(Config.inst().hasValue(Config.PAGE_LENGTH)) {
				try {
					list.setPageSize(Integer.valueOf(Config.inst().getValue(Config.PAGE_LENGTH).toString()));
				} catch(NumberFormatException e) {
				}
			}
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
	 * claim list [page]
	 * @param cs
	 * @param args
	 */
	private void getRegionList(CommandSender cs, String[] args) {
		if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_LIST)) {
			if(!regionList.containsKey(cs)) {
				// get RegionList
				if (cs instanceof Player) {
					regionList.put(cs, getRegions((Player)cs));
				} else {
					// No Player
					cs.sendMessage(Lang.NOPLAYER.get());
					return;
				}
			}
			if(args.length == 2) {
				TigerList<ProtectedRegion> b = regionList.get(cs);
				if(b != null) {
					try {
						b.setPage(Integer.valueOf(args[1]));
						showList(cs, b);
					} catch (NumberFormatException e) {
						cs.sendMessage(Lang.NONUMBERVALUE.get().replace(VALUE, args[1]));
					}
				}
			} else {
				showList(cs, regionList.get(cs));
			}
		} else {
			cs.sendMessage(Lang.NOPERMISSION.get());
		}
	}
	
	/**
	 * 
	 * @param cs
	 * @param regionList
	 */
	public void showList(CommandSender cs, TigerList<ProtectedRegion> regionList) {
		if(regionList != null && !regionList.isEmpty()) {
			// Header
			cs.sendMessage(Lang.LIST_HEADER.get().replace(VALUE, cs.getName()));
			// Buttons
			for(int i = 0; (i < regionList.getPageSize()) && (regionList.getPage() * regionList.getPageSize() + i < regionList.size()); i++) {
				try {
					// Show Regions
					ProtectedRegion r = regionList.get(regionList.getPage() * regionList.getPageSize() + i);
					if(cs instanceof Player) {
						// With Hover/ClickCommand
						((Player)cs).spigot().sendMessage(Lang.buildTC(
								buildTextString(r),
								"/rg i " + r.getId(),
								ClaimUtils.buildRegionHover(r),
								null));
					} else {
						// Only Text
						cs.sendMessage(buildTextString(r));
					}
				} catch(ArrayIndexOutOfBoundsException e) {
					break;
				}
			}
			// Footer
			if(cs instanceof Player)
				((Player)cs).spigot().sendMessage(
						Lang.buildTC(
								Lang.LIST_FOOTER_PREV.get(),
								("/claim " + Claim.LIST + " " + (regionList.getPage() - 1)),
								null,
								null),
						Lang.buildTC(
								Lang.LIST_FOOTER_PLAYER.get().replace(PAGE, Integer.toString(regionList.getPage() + 1)).replace(PAGEMAX, Integer.toString(regionList.getPageMax()))),
						Lang.buildTC(
								Lang.LIST_FOOTER_NEXT.get(),
								("/claim " + Claim.LIST + " " + (regionList.getPage() + 1)),
								null,
								null));
			else
				cs.sendMessage(Lang.LIST_FOOTER_CONSOLE.get().replace(PAGE, Integer.toString(regionList.getPage() + 1)).replace(PAGEMAX, Integer.toString(regionList.getPageMax())));
		} else {
			// No Regions
			cs.sendMessage(Lang.NOREGIONS.get());
		}
	}
	
	/**
	 * 
	 * @param r
	 * @return
	 */
	private String buildTextString(ProtectedRegion r) {
		return Lang.LIST_ROW.get().replace(VALUE, r.getId());
	}
}
