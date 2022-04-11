package de.bergtiger.claim.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import de.bergtiger.claim.data.permission.Perm;

public class ClaimTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
		List<String> list = new ArrayList<>();
		if (args.length <= 1) {
			// Check
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_CHECK))
				list.add(Claim.CHECK);
			// Delete
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_DELETE))
				list.add(Claim.DELETE);
			// Claim
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_CLAIM))
				list.add(Claim.CLAIM);
			// Expand
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_EXPAND))
				list.add(Claim.EXPAND);
			// ExpandCheck
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_EXPANDCHECK))
				list.add(Claim.EXPANDCHECK);
			// Retract
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_RETRACT))
				list.add(Claim.RETRACT);
			// Height
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_HEIGHT))
				list.add(Claim.HEIGHT);
			// Priority
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_PRIORITY))
				list.add(Claim.PRIORITY);
			// Set
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_SET))
				list.add(Claim.SET);
			// Info
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_INFO))
				list.add(Claim.INFO);
			// List
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_LIST))
				list.add(Claim.LIST);
			// Plugin
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_PLUGIN))
				list.add(Claim.PLUGIN);
			// Reload
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_RELOAD))
				list.add(Claim.RELOAD);
			if (args.length == 1) {
				return list.stream().filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
			}
		} else {
			if (args[0].equalsIgnoreCase(Claim.SET) && Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_SET)) {
				if (args.length == 2) {
					list.addAll(Arrays.asList(CmdSet.CMD_PAGE_LENGTH, CmdSet.CMD_GAP_XZ, CmdSet.CMD_GAP_Y, CmdSet.CMD_EXPAND_VERT, CmdSet.CMD_OVERLAPPING, CmdSet.CMD_OWN_OVERLAPPING,
							CmdSet.CMD_CHECK, CmdSet.CMD_FLAG, CmdSet.CMD_TIME, CmdSet.CMD_PATTERN, CmdSet.CMD_RADIUS, CmdSet.CMD_HEIGHT_MIN, CmdSet.CMD_HEIGHT_MAX));
					return list.stream().filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
				}
				FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
				if (args[1].equalsIgnoreCase(CmdSet.CMD_FLAG)) {
					if (args.length == 3)
						// Show all Flags
						return registry.getAll().stream().map(Flag::getName).filter(f -> f.startsWith(args[2]))
								.collect(Collectors.toList());
					if (args.length == 4) {
						Flag<?> flag = registry.get(args[2]);
						if (flag != null) {
							if (flag instanceof BooleanFlag) {
								list.addAll(Arrays.asList("True", "False"));
								return list.stream().filter(s -> s.startsWith(args[3])).collect(Collectors.toList());
							}
							if (flag instanceof StateFlag) {
								list.addAll(Arrays.asList("Allow", "Deny"));
								return list.stream().filter(s -> s.startsWith(args[3])).collect(Collectors.toList());
							}
							if (flag instanceof StringFlag) {
								list.addAll(List.of("Text"));
								return list.stream().filter(s -> s.startsWith(args[3])).collect(Collectors.toList());
							}
						}
					}
				}
			}
		}
		return Collections.emptyList();
	}

}
