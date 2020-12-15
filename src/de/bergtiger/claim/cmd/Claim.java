package de.bergtiger.claim.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Claim implements CommandExecutor {

	public static final String SET = "set", CLAIM = "claim", PLUGIN = "plugin", RELOAD = "reload";
	
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (args.length >= 1) {
			switch (args[0].toLowerCase()) {
			case SET:
				CmdSet.setConfig(cs, args);
				break;
			case "create":
			case CLAIM:
			case "new":
				CmdClaim.claim(cs);
				break;
			case PLUGIN:
				CmdPlugin.showPluginInfo(cs);
				break;
			case RELOAD:
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
