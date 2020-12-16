package de.bergtiger.claim.cmd;

import static de.bergtiger.claim.data.Cons.FLAG;
import static de.bergtiger.claim.data.Cons.VALUE;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.bergtiger.claim.Claims;
import de.bergtiger.claim.data.Config;
import de.bergtiger.claim.data.Lang;
import de.bergtiger.claim.data.Perm;

public class CmdPlugin {

	public static void showPluginInfo(CommandSender cs) {
		Bukkit.getScheduler().runTaskAsynchronously(Claims.inst(), () -> new CmdPlugin().sendPluginInfo(cs));
	}
	
	private void sendPluginInfo(CommandSender cs) {
		if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_PLUGIN)) {
			Config c = Config.inst();
			if (cs instanceof Player) {
				Player p = (Player) cs;
				// Header
				p.spigot().sendMessage(Lang.buildTC(Lang.PLUGIN_HEADER.getValue()));
				// Version
				p.spigot().sendMessage(Lang.buildTC(
						Lang.PLUGIN_VERSION.getValue().replace(VALUE, Claims.inst().getDescription().getVersion())));
				// Radius
				p.spigot()
						.sendMessage(Lang.buildTC(
								Lang.PLUGIN_RADIUS.getValue().replace(VALUE,
										c.hasValue(Config.REGION_RADIUS) ? c.getValue(Config.REGION_RADIUS).toString()
												: "-"),
								null, Lang.PLUGIN_HOVER_RADIUS.getValue(), "/claim set " + CmdSet.CMD_RADIUS + " "));
				// Gap
				p.spigot()
						.sendMessage(
								Lang.buildTC(
										Lang.PLUGIN_GAP.getValue().replace(VALUE,
												c.hasValue(Config.REGION_GAP) ? c.getValue(Config.REGION_GAP).toString()
														: "-"),
										null, Lang.PLUGIN_HOVER_GAP.getValue(), "/claim set " + CmdSet.CMD_GAP + " "));
				// Expand Vert
				p.spigot().sendMessage(Lang.buildTC(
						Lang.PLUGIN_EXPANDVERT.getValue().replace(VALUE,
								c.hasValue(Config.REGION_EXPAND_VERT) ? c.getValue(Config.REGION_EXPAND_VERT).toString()
										: "-"),
						null, Lang.PLUGIN_HOVER_EXPANDVERT.getValue(),
						"/claim set " + CmdSet.CMD_EXPAND_VERT + " "
								+ (c.hasValue(Config.REGION_EXPAND_VERT)
										? !Boolean.valueOf(c.getValue(Config.REGION_EXPAND_VERT).toString())
										: "")));
				// Overlapping
				p.spigot().sendMessage(Lang.buildTC(
						Lang.PLUGIN_OVERLAPPING.getValue().replace(VALUE,
								c.hasValue(Config.REGION_OVERLAPPING) ? c.getValue(Config.REGION_OVERLAPPING).toString()
										: "-"),
						null, Lang.PLUGIN_HOVER_OVERLAPPING.getValue(),
						"/claim set " + CmdSet.CMD_OVERLAPPING + " "
								+ (c.hasValue(Config.REGION_OVERLAPPING)
										? !Boolean.valueOf(c.getValue(Config.REGION_OVERLAPPING).toString())
										: "")));
				// Pattern
				p.spigot()
						.sendMessage(Lang.buildTC(
								Lang.PLUGIN_PATTERN_ID.getValue().replace(VALUE,
										c.hasValue(Config.REGION_PATTERN) ? c.getValue(Config.REGION_PATTERN).toString()
												: "-"),
								null, Lang.PLUGIN_HOVER_PATTERN_ID.getValue(), "/claim set " + CmdSet.CMD_PATTERN + " "));
				// Time Pattern
				p.spigot()
						.sendMessage(Lang.buildTC(
								Lang.PLUGIN_PATTERN_TIME.getValue().replace(VALUE,
										c.hasValue(Config.TIME_PATTERN) ? c.getValue(Config.TIME_PATTERN).toString()
												: "-"),
								null, Lang.PLUGIN_HOVER_PATTERN_TIME.getValue(), "/claim set " + CmdSet.CMD_TIME + " "));
				// Flags
				p.spigot().sendMessage(Lang.buildTC(Lang.PLUGIN_FLAGS.getValue(), null, Lang.PLUGIN_HOVER_FLAGS.get(),
						"/claim set flags "));
				if (c.hasFlags()) {
					c.getFlags().forEach((f, v) -> {
						p.spigot()
								.sendMessage(Lang.buildTC(
										Lang.PLUGIN_FLAG_LIST.get().replace(FLAG, f.getName()).replace(VALUE,
												v.toString()),
										null, null, "/claim set " + CmdSet.CMD_FLAG + " " + f.getName()));
					});
				}
				// Footer
				p.spigot().sendMessage(Lang.buildTC(Lang.PLUGIN_FOOTER.get()));
			} else {
				// Header
				cs.sendMessage(Lang.PLUGIN_HEADER.get());
				// Version
				cs.sendMessage(Lang.PLUGIN_VERSION.get().replace(VALUE, Claims.inst().getDescription().getVersion()));
				// Radius
				cs.sendMessage(Lang.PLUGIN_RADIUS.get().replace(VALUE,
						c.hasValue(Config.REGION_RADIUS) ? c.getValue(Config.REGION_RADIUS).toString() : "-"));
				// Gap
				cs.sendMessage(Lang.PLUGIN_GAP.get().replace(VALUE,
						c.hasValue(Config.REGION_GAP) ? c.getValue(Config.REGION_GAP).toString() : "-"));
				// Expand Vert
				cs.sendMessage(Lang.PLUGIN_EXPANDVERT.get().replace(VALUE,
						c.hasValue(Config.REGION_EXPAND_VERT) ? c.getValue(Config.REGION_EXPAND_VERT).toString()
								: "-"));
				// Pattern
				cs.sendMessage(Lang.PLUGIN_PATTERN_ID.get().replace(VALUE,
						c.hasValue(Config.REGION_PATTERN) ? c.getValue(Config.REGION_PATTERN).toString() : "-"));
				// Time Pattern
				cs.sendMessage(Lang.PLUGIN_PATTERN_TIME.get().replace(VALUE,
						c.hasValue(Config.TIME_PATTERN) ? c.getValue(Config.TIME_PATTERN).toString() : "-"));
				// Flags
				cs.sendMessage(Lang.PLUGIN_FLAGS.get());
				if (c.hasFlags()) {
					c.getFlags().forEach((f, v) -> {
						if ((f != null) && (v != null))
							cs.sendMessage(Lang.PLUGIN_FLAG_LIST.get().replace(FLAG, f.getName()).replace(VALUE,
									v.toString()));
					});
				}
				// Footer
				cs.sendMessage(Lang.PLUGIN_FOOTER.get());
			}
		} else {
			cs.sendMessage(Lang.NOPERMISSION.get());
		}
	}
}
