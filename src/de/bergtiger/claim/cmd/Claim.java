package de.bergtiger.claim.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import de.bergtiger.claim.TigerClaim;
import de.bergtiger.claim.bdo.Confirmation;
import de.bergtiger.claim.data.Config;
import de.bergtiger.claim.data.Lang;
import de.bergtiger.claim.data.Perm;
import de.bergtiger.claim.listener.ConfirmationListener;
import static de.bergtiger.claim.data.Cons.VALUE;
import static de.bergtiger.claim.data.Cons.FLAG;

import java.util.HashMap;

public class Claim implements CommandExecutor {

	public static final String CMD_RADIUS = "radius", CMD_FLAG = "flag", CMD_PATTERN = "pattern", CMD_EXPAND_VERT = "expandvert";

	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (args.length >= 1) {
			switch (args[0].toLowerCase()) {
			case "set":
				setConfig(cs, args);
				break;
			case "create":
			case "claim":
			case "new":
				claim(cs);
				break;
			case "plugin":
				showPluginInfo(cs);
				break;
			case "reload":
				reload(cs);
				break;
			default:
				help(cs);
			}
		} else {
			help(cs);
		}
		return false;
	}

	private void help(CommandSender cs) {
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

	private void claim(CommandSender cs) {
		if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_CLAIM)) {
			if (cs instanceof Player) {
				// Get amount of claims
				Player p = (Player) cs;
				Integer radius = 39;
				Boolean expandVert = true;
				HashMap<Flag<?>, Object> flags = null;
				// set Config values
				Config c = Config.inst();
				// radius
				if (c.hasValue(Config.REGION_RADIUS))
					radius = Integer.valueOf(c.getValue(Config.REGION_RADIUS));
				// expandVert
				if (c.hasValue(Config.REGION_EXPAND_VERT))
					expandVert = Boolean.valueOf(c.getValue(Config.REGION_EXPAND_VERT));
				// flags
				if (c.hasFlags())
					flags = c.getFlags();
				// send Confirmation
				Confirmation con = new Confirmation(p, p.getLocation(), radius, expandVert, flags);
				ConfirmationListener.inst().addConfirmation(con);
				// inform Player
				p.spigot().sendMessage(Lang.buildTC("Confirm Claim", null, "Claim at pos", null),
						Lang.buildTC("Yes", "/yes", "creates Claim", null),
						Lang.buildTC("No", "/no", "abort Claim.", null));
			} else {
				// Not a player
				cs.sendMessage("Not a Player");
			}
		} else {
			cs.sendMessage(Lang.NOPERMISSION.get());
		}
	}

	private void showPluginInfo(CommandSender cs) {
		if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_PLUGIN)) {
			Config c = Config.inst();
			if (cs instanceof Player) {
				Player p = (Player) cs;
				// Header
				p.spigot().sendMessage(Lang.buildTC(Lang.PLUGIN_HEADER.get()));
				// Version
				p.spigot().sendMessage(Lang.buildTC(
						Lang.PLUGIN_VERSION.get().replace(VALUE, TigerClaim.inst().getDescription().getVersion())));
				// Radius
				p.spigot()
						.sendMessage(
								Lang.buildTC(
										Lang.PLUGIN_RADIUS.get().replace(VALUE,
												c.hasValue(Config.REGION_RADIUS) ? c.getValue(Config.REGION_RADIUS)
														: "-"),
										null, Lang.PLUGIN_HOVER_RADIUS.get(), "/claim set radius "));
				// Expand Vert
				p.spigot()
						.sendMessage(Lang.buildTC(
								Lang.PLUGIN_EXPANDVERT.get().replace(VALUE,
										c.hasValue(Config.REGION_EXPAND_VERT) ? c
												.getValue(Config.REGION_EXPAND_VERT) : "-"),
								null, Lang.PLUGIN_HOVER_EXPANDVERT.get(),
								"/claim set expandvert " + (c.hasValue(Config.REGION_EXPAND_VERT)
										? !Boolean.valueOf(c.getValue(Config.REGION_EXPAND_VERT))
										: "")));
				// Pattern
				p.spigot()
						.sendMessage(
								Lang.buildTC(
										Lang.PLUGIN_PATTERN.get().replace(VALUE,
												c.hasValue(Config.REGION_PATTERN) ? c.getValue(Config.REGION_PATTERN)
														: "-"),
										null, Lang.PLUGIN_HOVER_PATTERN.get(), "/claim set pattern "));
				// Flags
				p.spigot().sendMessage(Lang.buildTC(Lang.PLUGIN_FLAGS.get(), null, Lang.PLUGIN_HOVER_FLAGS.get(),
						"/claim set flags "));
				if(c.hasFlags()) {
					c.getFlags().forEach((f, v) -> {
						p.spigot().sendMessage(
								Lang.buildTC(Lang.PLUGIN_FLAG_LIST.get().replace(FLAG, f.getName()).replace(VALUE, v.toString()),null,null,"/claim set flag " + f.getName()));
					});
				}
				// Footer
				p.spigot().sendMessage(Lang.buildTC(Lang.PLUGIN_FOOTER.get()));
			} else {
				// Header
				cs.sendMessage(Lang.PLUGIN_HEADER.get());
				// Version
				cs.sendMessage(
						Lang.PLUGIN_VERSION.get().replace(VALUE, TigerClaim.inst().getDescription().getVersion()));
				// Radius
				cs.sendMessage(Lang.PLUGIN_RADIUS.get().replace(VALUE,
						c.hasValue(Config.REGION_RADIUS) ? c.getValue(Config.REGION_RADIUS) : "-"));
				// Expand Vert
				cs.sendMessage(Lang.PLUGIN_EXPANDVERT.get().replace(VALUE,
						c.hasValue(Config.REGION_EXPAND_VERT) ? c.getValue(Config.REGION_EXPAND_VERT) : "-"));
				// Pattern
				cs.sendMessage(Lang.PLUGIN_PATTERN.get().replace(VALUE,
						c.hasValue(Config.REGION_PATTERN) ? c.getValue(Config.REGION_PATTERN) : "-"));
				// Flags
				cs.sendMessage(Lang.PLUGIN_FLAGS.get());
				if(c.hasFlags()) {
					c.getFlags().forEach((f, v) -> {
						if((f != null) && (v != null))
							cs.sendMessage(Lang.PLUGIN_FLAG_LIST.get().replace(FLAG, f.getName()).replace(VALUE, v.toString()));
					});
				}
				// Footer
				cs.sendMessage(Lang.PLUGIN_FOOTER.get());
			}
		} else {
			cs.sendMessage(Lang.NOPERMISSION.get());
		}
	}

	/**
	 * Set a value in Conifg. claim set type value
	 * 
	 * @param cs
	 * @param args
	 */
	private void setConfig(CommandSender cs, String[] args) {
		if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_SET)) {
			if (args.length >= 3) {
				Config c = Config.inst();
				switch (args[1].toLowerCase()) {
				case CMD_RADIUS: {
					try {
						c.setValue(Config.REGION_RADIUS, Integer.valueOf(args[2]).toString());
						c.saveConfig();
						cs.sendMessage("saved");
					} catch (NumberFormatException e) {
						cs.sendMessage("Not a Number");
					}
					break;
				}
				case CMD_PATTERN: {
					c.setValue(Config.REGION_PATTERN, args[2]);
					c.saveConfig();
					cs.sendMessage("saved");
					break;
				}
				case CMD_EXPAND_VERT: {
					c.setValue(Config.REGION_EXPAND_VERT, Boolean.valueOf(args[2]).toString());
					c.saveConfig();
					cs.sendMessage("saved");
					break;
				}
				case CMD_FLAG: {
					// claim set flag [flag][value]
					if (args.length >= 4) {
						// Check if is flag
						FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
						Flag<?> f = registry.get(args[2]);
						if(f != null) {
							String value = arrayToString(args, 3);
							if((value != null) && value.equalsIgnoreCase("null"))
								value = null;
							c.setFlag(f, value);
							c.saveConfig();
							cs.sendMessage("saved flag");
						} else {
							cs.sendMessage("not a flag");
						}
					} else {
						cs.sendMessage("missing value");
					}
					break;
				}
				default: {
					cs.sendMessage("not valid");
				}
				}
			} else {
				cs.sendMessage("Wrong argument.");
			}
		} else {
			cs.sendMessage(Lang.NOPERMISSION.get());
		}
	}
	
	private String arrayToString(String[] args, int beginn) {
		if(args != null) {
			if(beginn < 0)
				beginn = 0;
			String s = "";
			for(int i = beginn; i < args.length; i++) {
				if(s.length() > 0)
					s += " ";
				s += args[i];
			}
			return s;
		}
		return null;
	}

	private void reload(CommandSender cs) {
		if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_RELOAD)) {
			Config.inst().loadConfig();
			cs.sendMessage(Lang.RELOAD_FINISHED.get());
		} else {
			cs.sendMessage(Lang.NOPERMISSION.get());
		}
	}
}
