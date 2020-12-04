package de.bergtiger.claim.cmd;

import org.bukkit.command.CommandSender;

import de.bergtiger.claim.data.Config;
import de.bergtiger.claim.data.Lang;
import de.bergtiger.claim.data.Perm;
import de.bergtiger.claim.listener.ConfirmationListener;

public class CmdReload {

	public static void reload(CommandSender cs) {
		if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_RELOAD)) {
			ConfirmationListener.inst().clearQueue();
			Config.inst().loadConfig();
			cs.sendMessage(Lang.RELOAD_FINISHED.get());
		} else {
			cs.sendMessage(Lang.NOPERMISSION.get());
		}
	}
}
