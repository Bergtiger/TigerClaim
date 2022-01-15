package de.bergtiger.claim.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import de.bergtiger.claim.Claims;
import de.bergtiger.claim.data.language.Lang;
import de.bergtiger.claim.data.permission.Perm;

public class CmdInfo {

	public static void info(CommandSender cs) {
		Bukkit.getScheduler().runTaskAsynchronously(Claims.inst(), () -> new CmdInfo().sendInfo(cs));
	}
	
	private void sendInfo(CommandSender cs) {
		if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_INFO)) {
			cs.spigot().sendMessage(Lang.build(Lang.INFO));
		} else {
			cs.spigot().sendMessage(Lang.build(Lang.NOPERMISSION));
		}
	}
}
