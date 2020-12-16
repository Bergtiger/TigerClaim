package de.bergtiger.claim.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import de.bergtiger.claim.data.Perm;

public class ClaimTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
		List<String> list = new ArrayList<String>();
		if (args.length <= 1) {
			// Claim
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_CLAIM))
				list.add(Claim.CLAIM);
			// Set
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_SET))
				list.add(Claim.SET);
			// Plugin
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_PLUGIN))
				list.add(Claim.PLUGIN);
			// Reload
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_RELOAD))
				list.add(Claim.RELOAD);
			if (args.length == 1) {
				return list.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
			}
		} else {
			if (args[0].equalsIgnoreCase(Claim.SET) && Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_SET)) {
				if (args.length == 2) {
					list.addAll(Arrays.asList(CmdSet.CMD_GAP, CmdSet.CMD_EXPAND_VERT, CmdSet.CMD_OVERLAPPING,
							CmdSet.CMD_FLAG, CmdSet.CMD_TIME, CmdSet.CMD_PATTERN, CmdSet.CMD_RADIUS));
					return list.stream().filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
				}
				FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
				if (args.length >= 3 && args[1].equalsIgnoreCase(CmdSet.CMD_FLAG)) {
					if (args.length == 3)
						// Show all Flags
						return registry.getAll().stream().map(f -> f.getName()).filter(f -> f.startsWith(args[2]))
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
								list.addAll(Arrays.asList("Text"));
								return list.stream().filter(s -> s.startsWith(args[3])).collect(Collectors.toList());
							}
							if (flag instanceof IntegerFlag) {
							}
							if (flag instanceof DoubleFlag) {
							}
						}
					}
				}
			}
		}
		return Arrays.asList();
	}

}
