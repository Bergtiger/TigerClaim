package de.bergtiger.claim.cmd;

import static de.bergtiger.claim.data.Cons.FLAG;
import static de.bergtiger.claim.data.Cons.VALUE;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.bergtiger.claim.TigerClaim;
import de.bergtiger.claim.data.Config;
import de.bergtiger.claim.data.Lang;
import de.bergtiger.claim.data.Perm;

public class CmdPlugin {

	public static void showPluginInfo(CommandSender cs) {
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
				if (c.hasFlags()) {
					c.getFlags().forEach((f, v) -> {
						p.spigot().sendMessage(Lang.buildTC(
								Lang.PLUGIN_FLAG_LIST.get().replace(FLAG, f.getName()).replace(VALUE, v.toString()),
								null, null, "/claim set flag " + f.getName()));
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
