package de.bergtiger.claim.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Claim implements CommandExecutor {

	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (args.length >= 1) {
			switch (args[0].toLowerCase()) {
			case "set":
				CmdSet.setConfig(cs, args);
				break;
			case "create":
			case "claim":
			case "new":
				CmdClaim.claim(cs);
				break;
			case "plugin":
				CmdPlugin.showPluginInfo(cs);
				break;
			case "reload":
				CmdReload.reload(cs);
				break;
			default:
				CmdHelp.help(cs);
			}
		} else {
			CmdHelp.help(cs);
		}
		return false;
	}
}
