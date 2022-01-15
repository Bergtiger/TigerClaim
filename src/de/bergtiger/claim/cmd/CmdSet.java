package de.bergtiger.claim.cmd;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import de.bergtiger.claim.Claims;
import de.bergtiger.claim.data.ClaimUtils;
import de.bergtiger.claim.data.configuration.Config;
import de.bergtiger.claim.data.language.Lang;
import de.bergtiger.claim.data.permission.Perm;

import static de.bergtiger.claim.data.configuration.Config.*;

public class CmdSet {

	public static final String
			CMD_GAP			= "gap",
			CMD_RADIUS		= "radius",
			CMD_FLAG		= "flag",
			CMD_TIME		= "time",
			CMD_PATTERN		= "pattern",
			CMD_EXPAND_VERT	= "expandvert",
			CMD_OVERLAPPING	= "overlapping",
			CMD_PAGE_LENGTH	= "pagelength",
			CMD_HEIGHT_MIN	= "min",
			CMD_HEIGHT_MAX	= "max";

	public static void setConfig(CommandSender cs, String[] args) {
		Bukkit.getScheduler().runTaskAsynchronously(Claims.inst(), () -> new CmdSet().serConfigValue(cs, args));
	}

	/**
	 * Set a value in Configuration. claim set type value.
	 * /claim (0)set (1)type (2)value
	 * @param cs command sender
	 * @param args arguments
	 */
	public void serConfigValue(CommandSender cs, String[] args) {
		if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_SET)) {
			if (args.length >= 3) {
//				Config c = Config.inst();
				switch (args[1].toLowerCase()) {
					case CMD_PAGE_LENGTH -> {
						try {
							setValue(PAGE_LENGTH, Integer.valueOf(args[2]));
							cs.spigot().sendMessage(Lang.build(Lang.SET_SAVED.replace(Lang.TYPE, CMD_PAGE_LENGTH).replace(Lang.VALUE,
									getValue(PAGE_LENGTH).toString())));
						} catch (NumberFormatException e) {
							cs.spigot().sendMessage(Lang.build(Lang.NONUMBERVALUE.replace(Lang.VALUE, args[2])));
						}
					}
					case CMD_RADIUS -> {
						try {
							setValue(REGION_RADIUS, Integer.valueOf(args[2]));
							cs.spigot().sendMessage(Lang.build(Lang.SET_SAVED.replace(Lang.TYPE, CMD_RADIUS).replace(Lang.VALUE,
									getValue(REGION_RADIUS).toString())));
						} catch (NumberFormatException e) {
							cs.spigot().sendMessage(Lang.build(Lang.NONUMBERVALUE.get().replace(Lang.VALUE, args[2])));
						}
					}
					case CMD_GAP -> {
						try {
							setValue(Config.REGION_GAP, Integer.valueOf(args[2]));
							cs.spigot().sendMessage(Lang.build(Lang.SET_SAVED.replace(Lang.TYPE, CMD_GAP).replace(Lang.VALUE,
									getValue(Config.REGION_GAP).toString())));
						} catch (NumberFormatException e) {
							cs.spigot().sendMessage(Lang.build(Lang.NONUMBERVALUE.get().replace(Lang.VALUE, args[2])));
						}
					}
					case CMD_TIME -> {
						String timePattern = ClaimUtils.arrayToString(args, 2);
						try {
							DateTimeFormatter.ofPattern(timePattern).format(LocalDateTime.now());
							setValue(TIME_PATTERN, timePattern);
							cs.spigot().sendMessage(Lang.build(Lang.SET_SAVED.replace(Lang.TYPE, CMD_TIME).replace(Lang.VALUE,
									getValue(TIME_PATTERN).toString())));
						} catch (Exception e) {
							cs.spigot().sendMessage(Lang.build(Lang.SET_TIME_FAILED.replace(Lang.VALUE, timePattern)));
						}
					}
					case CMD_PATTERN -> {
						setValue(REGION_PATTERN, args[2]);
						cs.spigot().sendMessage(Lang.build(Lang.SET_SAVED.replace(Lang.TYPE, CMD_PATTERN).replace(Lang.VALUE,
								getValue(REGION_PATTERN).toString())));
					}
					case CMD_EXPAND_VERT -> {
						setValue(REGION_EXPAND_VERT, Boolean.valueOf(args[2]));
						cs.spigot().sendMessage(Lang.build(Lang.SET_SAVED.replace(Lang.TYPE, CMD_EXPAND_VERT).replace(Lang.VALUE,
								getValue(REGION_EXPAND_VERT).toString())));
					}
					case CMD_OVERLAPPING -> {
						setValue(REGION_OVERLAPPING, Boolean.valueOf(args[2]));
						cs.spigot().sendMessage(Lang.build(Lang.SET_SAVED.replace(Lang.TYPE, CMD_OVERLAPPING).replace(Lang.VALUE,
								getValue(REGION_OVERLAPPING).toString())));
					}
					case CMD_HEIGHT_MIN -> {
						setValue(REGION_MIN_HEIGHT, Integer.parseInt(args[2]));
						cs.spigot().sendMessage(Lang.build(Lang.SET_SAVED.replace(Lang.TYPE, CMD_HEIGHT_MIN).replace(Lang.VALUE,
								getValue(REGION_MIN_HEIGHT).toString())));
					}
					case CMD_HEIGHT_MAX -> {
						setValue(REGION_MAX_HEIGHT, Integer.parseInt(args[2]));
						cs.spigot().sendMessage(Lang.build(Lang.SET_SAVED.replace(Lang.TYPE, CMD_HEIGHT_MAX).replace(Lang.VALUE,
								getValue(REGION_MAX_HEIGHT).toString())));
					}
					case CMD_FLAG -> {
						// claim set flag [flag][value]
						// Check if is flag
						FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
						Flag<?> f = registry.get(args[2]);
						if (f != null) {
							String value = null;
							// if there is a value
							if (args.length >= 4)
								value = ClaimUtils.arrayToString(args, 3);
							// value set with 'null'
							if (value != null) {
								if (value.equalsIgnoreCase("null")) {
									value = null;
								} else {
									// check value
									if (f instanceof StateFlag) {
										try {
											StateFlag.State.valueOf(value.toUpperCase());
										} catch (Exception e) {
											cs.spigot().sendMessage(Lang.build(Lang.NOFLAGVALUE.replace(Lang.VALUE, value).replace(Lang.TYPE, f.getName())));
											return;
										}
									} else if (f instanceof IntegerFlag) {
										try {
											Integer.valueOf(value);
										} catch (NumberFormatException e) {
											cs.spigot().sendMessage(Lang.build(Lang.NONUMBERVALUE.replace(Lang.VALUE, value)));
											return;
										}
									} else if (f instanceof DoubleFlag) {
										try {
											Double.valueOf(value);
										} catch (NumberFormatException e) {
											cs.spigot().sendMessage(Lang.build(Lang.NONUMBERVALUE.replace(Lang.VALUE, value)));
											return;
										}
									}
								}
								// save flag
								setFlag(f, value);
								if (value != null) {
									cs.spigot().sendMessage(Lang.build(
											Lang.SET_FLAG_SAVED.replace(Lang.TYPE, f.getName()).replace(Lang.VALUE, value)));
								} else {
									cs.spigot().sendMessage(Lang.build(Lang.SET_FLAG_REMOVED.replace(Lang.TYPE, f.getName())));
								}
							} else {
								cs.spigot().sendMessage(Lang.build(Lang.NOSUCHFLAG.replace(Lang.VALUE, args[2])));
							}
						} else {
							cs.spigot().sendMessage(Lang.build(Lang.SET_FLAG_MISSING));
						}
					}
					default -> {
						cs.spigot().sendMessage(Lang.build(Lang.NOARGUMENT.replace(Lang.VALUE, args[1])));
					}
				}
			} else {
				cs.spigot().sendMessage(Lang.build(Lang.SET_MISSING));
			}
		} else {
			cs.spigot().sendMessage(Lang.build(Lang.NOPERMISSION));
		}
	}
}
