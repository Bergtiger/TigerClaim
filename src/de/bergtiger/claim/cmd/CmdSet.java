package de.bergtiger.claim.cmd;

import static de.bergtiger.claim.data.Cons.TYPE;
import static de.bergtiger.claim.data.Cons.VALUE;

import org.bukkit.command.CommandSender;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import de.bergtiger.claim.data.ClaimUtils;
import de.bergtiger.claim.data.Config;
import de.bergtiger.claim.data.Lang;
import de.bergtiger.claim.data.Perm;

public class CmdSet {

	public static final String CMD_RADIUS = "radius", CMD_FLAG = "flag", CMD_PATTERN = "pattern",
			CMD_EXPAND_VERT = "expandvert";

	/**
	 * Set a value in Conifg. claim set type value
	 * 
	 * @param cs
	 * @param args
	 */
	public static void setConfig(CommandSender cs, String[] args) {
		if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_SET)) {
			if (args.length >= 3) {
				Config c = Config.inst();
				switch (args[1].toLowerCase()) {
				case CMD_RADIUS: {
					try {
						c.setValue(Config.REGION_RADIUS, Integer.valueOf(args[2]).toString());
						c.saveConfig();
						cs.sendMessage(Lang.SET_SAVED.get().replace(TYPE, CMD_RADIUS).replace(VALUE,
								c.getValue(Config.REGION_RADIUS)));
					} catch (NumberFormatException e) {
						cs.sendMessage(Lang.NONUMBERVALUE.get().replace(VALUE, args[2]));
					}
					break;
				}
				case CMD_PATTERN: {
					c.setValue(Config.REGION_PATTERN, args[2]);
					c.saveConfig();
					cs.sendMessage(Lang.SET_SAVED.get().replace(TYPE, CMD_PATTERN).replace(VALUE,
							c.getValue(Config.REGION_PATTERN)));
					break;
				}
				case CMD_EXPAND_VERT: {
					c.setValue(Config.REGION_EXPAND_VERT, Boolean.valueOf(args[2]).toString());
					c.saveConfig();
					cs.sendMessage(Lang.SET_SAVED.get().replace(TYPE, CMD_EXPAND_VERT).replace(VALUE,
							c.getValue(Config.REGION_EXPAND_VERT)));
					break;
				}
				case CMD_FLAG: {
					// claim set flag [flag][value]
					if (args.length >= 3) {
						// Check if is flag
						FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
						Flag<?> f = registry.get(args[2]);
						if (f != null) {
							String value = null;
							// if there is a value
							if (args.length >= 4)
								value = ClaimUtils.arrayToString(args, 3);
							// value set with 'null'
							if ((value != null) && value.equalsIgnoreCase("null"))
								value = null;
							// save flag
							c.setFlag(f, value);
							c.saveConfig();
							if (value != null) {
								cs.sendMessage(
										Lang.SET_FLAG_SAVED.get().replace(TYPE, f.getName()).replace(VALUE, value));
							} else {
								cs.sendMessage(Lang.SET_FLAG_REMOVED.get().replace(TYPE, f.getName()));
							}
						} else {
							cs.sendMessage(Lang.NOSUCHFLAG.get().replace(VALUE, args[2]));
						}
					} else {
						cs.sendMessage(Lang.SET_FLAG_MISSING.get());
					}
					break;
				}
				default: {
					cs.sendMessage(Lang.NOARGUMENT.get().replace(VALUE, args[1]));
				}
				}
			} else {
				cs.sendMessage(Lang.SET_MISSING.get());
			}
		} else {
			cs.sendMessage(Lang.NOPERMISSION.get());
		}
	}
}
