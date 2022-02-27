package de.bergtiger.claim.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Claim implements CommandExecutor {

	public static final String
			CMD = "claim",
			SET = "set",
			INFO = "info",
			LIST = "list",
			CHECK = "check",
			CLAIM = "claim", CREATE = "create", NEW = "new",
			DELETE = "delete",
			EXPANDCHECK = "expandcheck",
			EXPAND = "expand",
			PLUGIN = "plugin",
			RELOAD = "reload";

	/**
	 * Handle claim commands
	 * @param cs command sender
	 * @param cmd command
	 * @param label alias
	 * @param args arguments
	 * @return always true
	 */
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (args.length >= 1) {
			switch (args[0].toLowerCase()) {
				case SET -> CmdSet.setConfig(cs, args);
				case INFO -> CmdInfo.info(cs);
				case LIST -> CmdList.list(cs, args);
				case CHECK -> CmdCheck.check(cs);
				case CLAIM, CREATE, NEW -> CmdClaim.claim(cs, false);
				case DELETE -> CmdDelete.delete(cs, args);
				case EXPANDCHECK -> CmdExpand.expand(cs, args, true, false);
				case EXPAND -> CmdExpand.expand(cs, args, false, false);
				case PLUGIN -> CmdPlugin.showPluginInfo(cs);
				case RELOAD -> CmdReload.reload(cs);
				default -> CmdHelp.help(cs);
			}
		} else {
			CmdHelp.help(cs);
		}
		return true;
	}
}
