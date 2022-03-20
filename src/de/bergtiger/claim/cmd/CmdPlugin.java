package de.bergtiger.claim.cmd;

import static de.bergtiger.claim.data.configuration.Config.*;
import static de.bergtiger.claim.data.language.Lang.*;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import de.bergtiger.claim.Claims;
import de.bergtiger.claim.data.configuration.Config;
import de.bergtiger.claim.data.language.Lang;
import de.bergtiger.claim.data.permission.Perm;

import java.util.ArrayList;
import java.util.List;

public class CmdPlugin {

	public static void showPluginInfo(CommandSender cs) {
		Bukkit.getScheduler().runTaskAsynchronously(Claims.inst(), () -> new CmdPlugin().sendPluginInfo(cs));
	}
	
	private void sendPluginInfo(CommandSender cs) {
		String cmd = "/claim set %s ";
		if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_PLUGIN)) {
			List<TextComponent> components = new ArrayList<>();
			// Header
			components.add(build(PLUGIN_HEADER));
			components.add(newLine());
			// Version
			components.add(build(
					PLUGIN_VERSION.replace(VALUE, Claims.inst().getDescription().getVersion())));
			components.add(newLine());
			// Page Length
			components.add(build(
					PLUGIN_PAGE_LENGTH.replace(VALUE, Integer.toString(getInt(PAGE_LENGTH))),
					null,
					build(PLUGIN_HOVER_PAGE_LENGTH),
					String.format(cmd, CmdSet.CMD_PAGE_LENGTH)));
			components.add(newLine());
			// Radius
			components.add(build(
					PLUGIN_RADIUS.replace(VALUE, Integer.toString(getInt(REGION_RADIUS))),
					null,
					build(PLUGIN_HOVER_RADIUS),
					String.format(cmd, CmdSet.CMD_RADIUS)));
			components.add(newLine());
			// Gap
			components.add(build(
					PLUGIN_GAP.replace(VALUE, Integer.toString(getInt(REGION_GAP))),
					null,
					build(PLUGIN_HOVER_GAP),
					String.format(cmd, CmdSet.CMD_GAP)));
			components.add(newLine());
			// Expand Vert
			components.add(build(
					PLUGIN_EXPANDVERT.replace(VALUE, Boolean.toString(getBoolean(REGION_EXPAND_VERT))),
					null,
					build(PLUGIN_HOVER_EXPANDVERT),
					String.format(cmd, CmdSet.CMD_EXPAND_VERT)
							+ (hasValue(Config.REGION_EXPAND_VERT)
									? Boolean.toString(!getBoolean(Config.REGION_EXPAND_VERT)) : "")));
			components.add(newLine());
			// Overlapping
			components.add(build(
					PLUGIN_OVERLAPPING.replace(VALUE, Boolean.toString(getBoolean(REGION_OVERLAPPING))),
					null,
					build(PLUGIN_HOVER_OVERLAPPING),
					"/claim set " + CmdSet.CMD_OVERLAPPING + " "
							+ (hasValue(Config.REGION_OVERLAPPING)
									? Boolean.toString(!getBoolean(REGION_OVERLAPPING)) : "")));
			components.add(newLine());
			// Check
			components.add(build(
					PLUGIN_CHECK.replace(VALUE, Boolean.toString(getBoolean(REGION_CHECK))),
					null,
					build(PLUGIN_HOVER_CHECK),
					"/claim set " + CmdSet.CMD_CHECK + " "
							+ (hasValue(REGION_CHECK)
							? Boolean.toString(!getBoolean(REGION_CHECK)) : "")));
			components.add(newLine());
			// Pattern
			components.add(build(
					PLUGIN_PATTERN_ID.replace(VALUE, hasValue(REGION_PATTERN) ? getString(REGION_PATTERN) : "-"),
					null,
					build(PLUGIN_HOVER_PATTERN_ID),
					String.format(cmd, CmdSet.CMD_PATTERN)));
			components.add(newLine());
			// Time Pattern
			components.add(build(
					PLUGIN_PATTERN_TIME.replace(VALUE, hasValue(TIME_PATTERN) ? getString(TIME_PATTERN) : "-"),
					null,
					build(PLUGIN_HOVER_PATTERN_TIME),
					String.format(cmd, CmdSet.CMD_TIME)));
			components.add(newLine());
			// height
			// min
			components.add(build(
					PLUGIN_HEIGHT_MIN.replace(VALUE, Integer.toString(getInt(REGION_MIN_HEIGHT))),
					null,
					build(PLUGIN_HOVER_HEIGHT_MIN),
					String.format(cmd, CmdSet.CMD_HEIGHT_MIN)));
			components.add(newLine());
			// max
			components.add(build(
					PLUGIN_HEIGHT_MAX.replace(VALUE, Integer.toString(getInt(REGION_MAX_HEIGHT))),
					null,
					build(PLUGIN_HOVER_HEIGHT_MAX),
					String.format(cmd, CmdSet.CMD_HEIGHT_MAX)));
			components.add(newLine());
			// Flags
			components.add(build(
					PLUGIN_FLAGS,
					null,
					build(PLUGIN_HOVER_FLAGS),
					String.format(cmd, CmdSet.CMD_FLAG)));
			components.add(newLine());
			if (hasValue(REGION_FLAGS)) {
				getFlags().forEach((f, v) -> {
					components.add(build(
							PLUGIN_FLAG_LIST.replace(FLAG, f.getName()).replace(VALUE, v.toString()),
							null,
							null,
							String.format("/claim %s %s %s ", Claim.SET, CmdSet.CMD_FLAG, f.getName())));
					components.add(newLine());
				});
			}
			// Footer
			components.add(build(Lang.PLUGIN_FOOTER));
			// show message
			cs.spigot().sendMessage(Lang.combine(components));
		} else {
			cs.sendMessage(Lang.NOPERMISSION.get());
		}
	}
}
