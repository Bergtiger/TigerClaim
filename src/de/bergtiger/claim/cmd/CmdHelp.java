package de.bergtiger.claim.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.bergtiger.claim.data.Lang;
import de.bergtiger.claim.data.Perm;

public class CmdHelp {
	
	public static void help(CommandSender cs) {
		if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_CLAIM, Perm.CLAIM_PLUGIN, Perm.CLAIM_RELOAD)) {
			if (cs instanceof Player) {
				Player p = (Player) cs;
				// Header
				p.spigot().sendMessage(Lang.buildTC(Lang.CMD_HEADER.get()));
				// Claim
				if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_CLAIM))
					p.spigot().sendMessage(
							Lang.buildTC(Lang.CMD_INSERT.get(), "/claim claim", Lang.CMD_HOVER_INSERT.get(), null));
				// Set
				if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_SET))
					p.spigot().sendMessage(
							Lang.buildTC(Lang.CMD_SET.get(), null, Lang.CMD_HOVER_SET.get(), "/claim set "));
				// Plugin
				if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_PLUGIN))
					p.spigot().sendMessage(
							Lang.buildTC(Lang.CMD_PLUGIN.get(), "/claim plugin", Lang.CMD_HOVER_PLUGIN.get(), null));
				// Reload
				if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_RELOAD))
					p.spigot().sendMessage(
							Lang.buildTC(Lang.CMD_RELOAD.get(), "/claim reload", Lang.CMD_HOVER_RELOAD.get(), null));
				// Footer
				p.spigot().sendMessage(Lang.buildTC(Lang.CMD_FOOTER.get()));
			} else {
				// Header
				cs.sendMessage(Lang.CMD_HEADER.get());
				// Claim
				if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_CLAIM))
					cs.sendMessage(Lang.CMD_INSERT.get());
				// Set
				if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_SET))
					cs.sendMessage(Lang.CMD_SET.get());
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
