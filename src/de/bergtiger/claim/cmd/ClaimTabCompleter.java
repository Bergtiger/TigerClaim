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
				list.add("claim");
			// Set
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_SET))
				list.add("set");
			// Plugin
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_PLUGIN))
				list.add("plugin");
			// Reload
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_RELOAD))
				list.add("reload");
			if (args.length == 1) {
				return list.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
			}
		} else {
			if (args[0].equalsIgnoreCase("set") && Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_SET)) {
				if (args.length == 2) {
					list.addAll(Arrays.asList(Claim.CMD_EXPAND_VERT, Claim.CMD_FLAG, Claim.CMD_PATTERN, Claim.CMD_RADIUS));
					return list.stream().filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
				}
				FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
				if (args.length == 3 && args[1].equalsIgnoreCase("flag")) {
					// Show all Flags
					return registry.getAll().stream().map(f -> f.getName()).filter(f -> f.startsWith(args[2]))
							.collect(Collectors.toList());
				}
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
		return Arrays.asList();
	}

}
