package de.bergtiger.claim.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import de.bergtiger.claim.Claims;
import de.bergtiger.claim.data.Lang;
import de.bergtiger.claim.data.Perm;

public class CmdInfo {

	public static void info(CommandSender cs) {
		Bukkit.getScheduler().runTaskAsynchronously(Claims.inst(), () -> new CmdInfo().sendInfo(cs));
	}
	
	private void sendInfo(CommandSender cs) {
		if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_INFO)) {
			cs.sendMessage(Lang.INFO.get());
		} else {
			cs.sendMessage(Lang.NOPERMISSION.get());
		}
	}
}
