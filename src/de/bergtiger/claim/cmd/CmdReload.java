package de.bergtiger.claim.cmd;

import org.bukkit.command.CommandSender;

import de.bergtiger.claim.data.configuration.Config;
import de.bergtiger.claim.data.language.Lang;
import de.bergtiger.claim.data.permission.Perm;

public class CmdReload {

	public static void reload(CommandSender cs) {
		if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_RELOAD)) {
			// save Config
			Config.load();
			cs.spigot().sendMessage(Lang.build(Lang.RELOAD_FINISHED));
		} else {
			cs.spigot().sendMessage(Lang.build(Lang.NOPERMISSION));
		}
	}
}
