package de.bergtiger.claim.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Claim implements CommandExecutor {

	public static final String CMD = "claim", SET = "set", INFO = "info", LIST = "list", CLAIM = "claim", PLUGIN = "plugin", RELOAD = "reload", DELETE = "delete", CHECK = "check";
	
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (args.length >= 1) {
			switch (args[0].toLowerCase()) {
				case SET -> CmdSet.setConfig(cs, args);
				case INFO -> CmdInfo.info(cs);
				case LIST -> CmdList.list(cs, args);
				case "create", CLAIM, "new" -> CmdClaim.claim(cs, false);
				case PLUGIN -> CmdPlugin.showPluginInfo(cs);
				case RELOAD -> CmdReload.reload(cs);
				case DELETE -> CmdDelete.delete(cs, args);
				case CHECK -> CmdCheck.check(cs);
				default -> CmdHelp.help(cs);
			}
		} else {
			CmdHelp.help(cs);
		}
		return false;
	}
}
