package de.bergtiger.claim.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.bergtiger.claim.Claims;
import de.bergtiger.claim.data.Lang;
import de.bergtiger.claim.data.Perm;

public class CmdHelp {
	
	public static void help(CommandSender cs) {
		Bukkit.getScheduler().runTaskAsynchronously(Claims.inst(), () -> new CmdHelp().sendHelp(cs));
	}
	
	private void sendHelp(CommandSender cs) {
		if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_CLAIM, Perm.CLAIM_PLUGIN, Perm.CLAIM_RELOAD)) {
			if (cs instanceof Player) {
				Player p = (Player) cs;
				// Header
				p.spigot().sendMessage(Lang.buildTC(Lang.CMD_HEADER.getValue()));
				// Claim
				if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_CLAIM))
					p.spigot().sendMessage(
							Lang.buildTC(Lang.CMD_INSERT.getValue(), "/claim " + Claim.CLAIM, Lang.CMD_HOVER_INSERT.getValue(), null));
				// Info
				if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_INFO))
					p.spigot().sendMessage(
							Lang.buildTC(Lang.CMD_INFO.getValue(), "/claim " + Claim.INFO, Lang.CMD_HOVER_INFO.getValue(), null));
				// List
				if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_LIST))
					p.spigot().sendMessage(
							Lang.buildTC(Lang.CMD_LIST.getValue(), "/claim " + Claim.LIST, Lang.CMD_HOVER_LIST.getValue(), null));
				// Set
				if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_SET))
					p.spigot().sendMessage(
							Lang.buildTC(Lang.CMD_SET.getValue(), null, Lang.CMD_HOVER_SET.getValue(), "/claim " + Claim.SET + " "));
				// Plugin
				if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_PLUGIN))
					p.spigot().sendMessage(
							Lang.buildTC(Lang.CMD_PLUGIN.getValue(), "/claim " + Claim.PLUGIN, Lang.CMD_HOVER_PLUGIN.getValue(), null));
				// Reload
				if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_RELOAD))
					p.spigot().sendMessage(
							Lang.buildTC(Lang.CMD_RELOAD.getValue(), "/claim " + Claim.RELOAD, Lang.CMD_HOVER_RELOAD.getValue(), null));
				// Footer
				p.spigot().sendMessage(Lang.buildTC(Lang.CMD_FOOTER.getValue()));
			} else {
				// Header
				cs.sendMessage(Lang.CMD_HEADER.get());
				// Claim
				if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_CLAIM))
					cs.sendMessage(Lang.CMD_INSERT.get());
				// Set
				if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_SET))
					cs.sendMessage(Lang.CMD_SET.get());
				// Info
				if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_INFO))
					cs.sendMessage(Lang.CMD_INFO.get());
				// List
				if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_LIST))
					cs.sendMessage(Lang.CMD_LIST.get());
				// Plugin
				if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_PLUGIN))
					cs.sendMessage(Lang.CMD_PLUGIN.get());
				// Reload
				if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_RELOAD))
					cs.sendMessage(Lang.CMD_RELOAD.get());
				// Footer
				cs.sendMessage(Lang.CMD_FOOTER.get());
			}
		} else {
			cs.sendMessage(Lang.NOPERMISSION.get());
		}
	}
}
