package de.bergtiger.claim.cmd;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.flags.Flag;

import de.bergtiger.claim.bdo.Confirmation;
import de.bergtiger.claim.data.Config;
import de.bergtiger.claim.data.Lang;
import de.bergtiger.claim.data.Perm;
import de.bergtiger.claim.listener.ConfirmationListener;

public class CmdClaim {
	
	public static void claim(CommandSender cs) {
		if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_CLAIM)) {
			if (cs instanceof Player) {
				// Get amount of claims
				Player p = (Player) cs;
				Integer radius = 39;
				Boolean expandVert = true;
				HashMap<Flag<?>, Object> flags = null;
				// set Config values
				Config c = Config.inst();
				// radius
				if (c.hasValue(Config.REGION_RADIUS))
					radius = Integer.valueOf(c.getValue(Config.REGION_RADIUS));
				// expandVert
				if (c.hasValue(Config.REGION_EXPAND_VERT))
					expandVert = Boolean.valueOf(c.getValue(Config.REGION_EXPAND_VERT));
				// flags
				if (c.hasFlags())
					flags = c.getFlags();
				// WorldEdit
				if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit")
						&& Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_WORLDEDIT)) {
					WorldEdit we = WorldEdit.getInstance();
					BukkitPlayer bp = BukkitAdapter.adapt(p);
					try {
						Region s = we.getSessionManager().get(bp).getSelection(bp.getWorld());
						if (s != null) {
							if (s instanceof CuboidRegion) {
								// Cuboid
							} else {
								// Polygon
							}
						} else {
							// No Region
							// send Confirmation
							Confirmation con = new Confirmation(p, p.getLocation(), radius, expandVert, flags);
							ConfirmationListener.inst().addConfirmation(con);
						}
					} catch (IncompleteRegionException e) {
						// No Region
						// send Confirmation
						Confirmation con = new Confirmation(p, p.getLocation(), radius, expandVert, flags);
						ConfirmationListener.inst().addConfirmation(con);
					}
				} else {
					// send Confirmation
					Confirmation con = new Confirmation(p, p.getLocation(), radius, expandVert, flags);
					ConfirmationListener.inst().addConfirmation(con);
				}
				// inform Player
				p.spigot().sendMessage(Lang.buildTC(Lang.INSERT_TEXT.get(), null, Lang.INSERT_HOVER_TEXT.get(), null),
						Lang.buildTC(Lang.INSERT_YES.get(), "/yes", Lang.INSERT_HOVER_YES.get(), null),
						Lang.buildTC(Lang.INSERT_NO.get(), "/no", Lang.INSERT_HOVER_NO.get(), null));
			} else {
				// Not a player
				cs.sendMessage(Lang.NOPLAYER.get());
			}
		} else {
			cs.sendMessage(Lang.NOPERMISSION.get());
		}
	}
}
