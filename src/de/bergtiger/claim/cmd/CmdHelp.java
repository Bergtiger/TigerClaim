package de.bergtiger.claim.cmd;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import de.bergtiger.claim.Claims;
import de.bergtiger.claim.data.language.Lang;
import de.bergtiger.claim.data.permission.Perm;

import java.util.ArrayList;
import java.util.List;

public class CmdHelp {
	
	public static void help(CommandSender cs) {
		Bukkit.getScheduler().runTaskAsynchronously(Claims.inst(), () -> new CmdHelp().sendHelp(cs));
	}
	
	private void sendHelp(CommandSender cs) {
		if (Perm.hasPermissionAny(cs)) {
			List<TextComponent> components = new ArrayList<>();
			// Header
			components.add(Lang.build(Lang.CMD_HEADER));
			components.add(Lang.newLine());
			// Claim
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_CLAIM)) {
				components.add(Lang.build(Lang.CMD_INSERT, String.format("/claim %s", Claim.CLAIM), Lang.build(Lang.CMD_HOVER_INSERT), null));
				components.add(Lang.newLine());
			}
			// Info
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_INFO)) {
				components.add(
						Lang.build(Lang.CMD_INFO, String.format("/claim %s", Claim.INFO), Lang.build(Lang.CMD_HOVER_INFO), null));
				components.add(Lang.newLine());
			}
			// List
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_LIST)) {
				components.add(
						Lang.build(Lang.CMD_LIST, String.format("/claim %s", Claim.LIST), Lang.build(Lang.CMD_HOVER_LIST), null));
				components.add(Lang.newLine());
			}
			// Set
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_SET)) {
				components.add(
						Lang.build(Lang.CMD_SET, null, Lang.build(Lang.CMD_HOVER_SET), String.format("/claim %s ", Claim.SET)));
				components.add(Lang.newLine());
			}
			// Plugin
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_PLUGIN)) {
				components.add(
						Lang.build(Lang.CMD_PLUGIN, String.format("/claim %s", Claim.PLUGIN), Lang.build(Lang.CMD_HOVER_PLUGIN), null));
				components.add(Lang.newLine());
			}
			// Reload
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_RELOAD)) {
				components.add(
						Lang.build(Lang.CMD_RELOAD, String.format("/claim %s", Claim.RELOAD), Lang.build(Lang.CMD_HOVER_RELOAD), null));
				components.add(Lang.newLine());
			}
			// Wiki
			if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN)) {
				TextComponent tc = Lang.build(Lang.CMD_WIKI, null, Lang.build(Lang.CMD_HOVER_WIKI), null);
				tc.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Bergtiger/TigerClaim/wiki/TigerClaim"));
				components.add(tc);
				components.add(Lang.newLine());
			}
			// Footer
			components.add(Lang.build(Lang.CMD_FOOTER));
			// show message
			cs.spigot().sendMessage(Lang.combine(components));
		} else {
			cs.sendMessage(Lang.NOPERMISSION.get());
		}
	}
}
