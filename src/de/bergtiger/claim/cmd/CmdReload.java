package de.bergtiger.claim.cmd;

import org.bukkit.command.CommandSender;

import de.bergtiger.claim.data.Config;
import de.bergtiger.claim.data.Lang;
import de.bergtiger.claim.data.Perm;
import de.bergtiger.claim.data.ReadMe;
import de.bergtiger.claim.listener.ConfirmationListener;

public class CmdReload {

	public static void reload(CommandSender cs) {
		if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_RELOAD)) {
			// clear cache
			ConfirmationListener.inst().clearQueue();
			// save Config
			Config.inst().loadConfig();
			// save ReadMe
			ReadMe.save();
			cs.sendMessage(Lang.RELOAD_FINISHED.get());
		} else {
			cs.sendMessage(Lang.NOPERMISSION.get());
		}
	}
}
