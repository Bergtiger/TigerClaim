package de.bergtiger.claim.data;

import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.chat.ClickEvent.Action;

import static de.bergtiger.claim.data.Cons.*;

/**
 * 
 * @author Bergtiger
 *
 */
public enum Lang {
	
	NOPERMISSION		("&cYou have no Permission"),
	
	NOARGUMENT			("&cNot a valid Option &e'&4" + VALUE + "&e'"),
	NONUMBER			("&cNot a Number"),
	NONUMBERVALUE		("&e'&4" + VALUE + "&e' &cis not a Number"),
	NOSUCHFLAG			("&e'&4" + VALUE + "&e' &cis not a Flag"),
	NOFLAGVALUE			("&e'&4" + VALUE + "&e' &cis not a valid value for &6" + TYPE),
	NOPLAYER			("&cYou have to be a Player to perform this command."),
	NOREGIONS			("&cNo Regions"),
	// Config
	CONFIG_LOAD_START	("Loading Config"),
	CONFIG_LOAD_FINISH	("Finished loading"),
	
	CONFIG_SAVE_START	("Saving Config"),
	CONFIG_SAVE_FINISH	("Finished saving"),
	// Set
	SET_SAVED			("&aSaved &6" + TYPE + "&f: &e" + VALUE),
	SET_MISSING			("&cMissing values"),
	SET_TIME_FAILED		("&cCan't use &e'&4" + VALUE + "&e' &cas time pattern."),
	SET_FLAG_SAVED		("&aSaved &eFlag &f(&6" + TYPE + "&f): &e" + VALUE),
	SET_FLAG_REMOVED	("&cRemoved &eFlag &f(&6" + TYPE + "&f)"),
	SET_FLAG_MISSING	("&cMissing flag value."),
	// Commands
	CMD_HEADER			("&a----<(&eTigerClaim&a)>----"),
	CMD_FOOTER			("&a---------------------------"),
	CMD_SET				("&7set"),
	CMD_LIST			("&7list [page]"),
	CMD_INFO			("&7info"),
	CMD_INSERT			("&7claim"),
	CMD_RELOAD			("&7reload"),
	CMD_PLUGIN			("&7plugin"),
	// Commands Hover
	CMD_HOVER_SET		("set config."),
	CMD_HOVER_INFO		("informatio how you create a Claim."),
	CMD_HOVER_LIST		("list all Regions where you are an owner."),
	CMD_HOVER_INSERT	("created a new Claim around your Position."),
	CMD_HOVER_RELOAD	("reloads TigerClaimPlugin(config and cache)"),
	CMD_HOVER_PLUGIN	("shows plugin info"),
	// Insert
	INSERT_OVERLAPPING	("&cOverlapping Regions."),
	INSERT_SUCCESS		("&aCreated Claim successfully."),
	INSERT_EXISTING		("&cClaim &e'&6" + VALUE + "&e' &calready exists."),
	INSERT_CANCEL		("&cClaim canceled."),
	INSERT_LIMIT		("&cYou reached your claim-limit&e[&6" + VALUE + "&e/&6" + LIMIT + "&e]"),
	INSERT_TEXT			("&eConfirm Claim&f: "),
	INSERT_YES			("&aYes"),
	INSERT_NO			(" &cNo"),
	INSERT_HOVER_SUCCESS("&aShow Claim."),
	INSERT_HOVER_TEXT	("&eClaim at your position."),
	INSERT_HOVER_YES	("&ecreates Claim."),
	INSERT_HOVER_NO		("&eabort Claim."),
	// Info
	INFO				("How to claim a region?\n"),
	// List
	LIST_HEADER			("&a----<(&e" + VALUE + "'s Regions&a)>----"),
	LIST_REGION			("&e- " + VALUE),
	LIST_FOOTER_NEXT	("&f[&eNext&f]"),
	LIST_FOOTER_PREV	("&f[&eprev&f]"),
	LIST_FOOTER_PLAYER	("&a-----&6[&e" + PAGE + "&f/&e" + PAGEMAX + "&6]&a-----"),
	LIST_FOOTER_CONSOLE	("&a-----&6[&e" + PAGE + "&f/&e" + PAGEMAX + "&6]&a-----"),
	
	LIST_HOVER			("&e" + ID),
	
	LIST_FOOTER			("&a---------------------------"),
	LIST_ROW			("&e- " + VALUE),
	// Claim
	CLAIM_POLYGON		("&aRegion&e: &6" + ID + "\n&apoints&e:" + VALUE),
	CLAIM_CUBOID		("&aRegion&e: &6" + ID + "\n&apos1&e: " + POS1 + "\n&apos2&e: " + POS2),
	CLAIM_RADIUS		("&aRegion&e: &6" + ID + "\n&aCenter&e: " + POS1 + "\n&aradius&e: &6" + VALUE),
	CLAIM_PATTERN_POINTS("  &6" + X + "&f, &6" + Z),
	CLAIM_PATTERN_LOC	("&6" + X + "&f, &6" + Y + "&f, &6" + Z),
	// Reload
	RELOAD_FINISHED		("&aReload finished."),
	// PluginInfo
	PLUGIN_HEADER		("&a----<(&eTigerClaim&a)>----"),
	PLUGIN_FOOTER		("&a------------------------"),
	PLUGIN_VERSION		("&aVersion&e: &6" + VALUE),
	PLUGIN_RADIUS		("&aRadius&e: &6" + VALUE),
	PLUGIN_GAP			("&aGap&e: &6" + VALUE),
	PLUGIN_EXPANDVERT	("&aExpandVert&e: &6" + VALUE),
	PLUGIN_OVERLAPPING	("&aOverlapping&e: &6" + VALUE),
	PLUGIN_PATTERN_ID	("&aRegionPattern&e: &6" + VALUE),
	PLUGIN_PATTERN_TIME	("&aTimePattern&e: &6" + VALUE),
	PLUGIN_FLAGS		("&aFlags"),
	PLUGIN_FLAG_LIST	("  &6" + FLAG + "&e: &f" + VALUE),
	// PluginInfo Hover
	PLUGIN_HOVER_RADIUS			("&aSet radius"),
	PLUGIN_HOVER_GAP			("&aSet gap between claim and other regions"),
	PLUGIN_HOVER_EXPANDVERT		("&aChange expand vertical"),
	PLUGIN_HOVER_OVERLAPPING	("&aChange if claims are allowed to overlap existing regions."),
	PLUGIN_HOVER_PATTERN_ID		("&aSet a new region pattern"),
	PLUGIN_HOVER_PATTERN_TIME	("&aSet a new time pattern"),
	PLUGIN_HOVER_FLAGS			("&aSet a flag");
	
	private String value;
	
	private Lang(String value) {
		this.value = value;
	}
	
	/**
	 * uncolored
	 * @return
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * uncolored
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * get String colored.
	 * only legacy colors
	 * @return Value colored
	 */
	public String get() {
		return color(value);
	}
	
	/**
	 * get String colored.
	 * only legacy colors
	 * @param args
	 * @return
	 */
	public static String color(String args) {
		if(args != null)
			return ChatColor.translateAlternateColorCodes('&', args);
		return args;
	}
	
	/**
	 * Builds a TextComponent with text and colors
	 * @param args - Text
	 * @return TextComponent
	 */
	public static TextComponent buildTC(String args) {
		return buildTC(args, null, null, null);
	}
	
	/**
	 * Builds a TextComponent with colored text, and extras
	 * @param args - text
	 * @param cmd - onClick command
	 * @param hover - onHover text
	 * @param cmd_suggestion - onClick suggestion
	 * @return TextComponent
	 */
	public static TextComponent buildTC(String args, String cmd, String hover, String cmd_suggestion) {
		return buildTC2(args, cmd, hover != null ? rgbColor(new TextComponent(hover), null) : null, cmd_suggestion);
	}
	
	/**
	 * Adds Extras to TextComponent no Color or anything!
	 * @param tc - TextComponent witch will be modified
	 * @param cmd - onClick command
	 * @param hover - onHover text
	 * @param cmd_suggestion
	 * @return TextComponent
	 */
	public static TextComponent buildTC2(String args, String cmd, BaseComponent hover, String cmd_suggestion) {
		if (args != null) {
			TextComponent tc = (TextComponent) rgbColor(new TextComponent(args),null);
			if (cmd != null && !cmd.isEmpty())
				tc.setClickEvent(new ClickEvent(Action.RUN_COMMAND, cmd));
			if (hover != null) {
				tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new Text(new BaseComponent[] {hover})));
			}
			if (cmd_suggestion != null && !cmd_suggestion.isEmpty())
				tc.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, cmd_suggestion));
			return tc;
		}
		return null;
	}
	
	public static TextComponent replaceColorless(TextComponent tc, String replace, String value) {
		if (tc != null) {
			if(tc.getText().contains(replace)) {
				tc.setText(tc.getText().replace(replace, value));
				return tc;
			}
			List<BaseComponent> components = tc.getExtra();
			if(components != null && !components.isEmpty()) {
				for(BaseComponent bc : components) {
					if(bc != null && bc instanceof TextComponent) {
						TextComponent t = (TextComponent)bc;
						if(t.getText().contains(replace)) {
							t.setText(t.getText().replace(replace, value));
							return tc;
						} else {
							replaceColorless(t, replace, value);
						}
					}
				}
			}
		}
		return tc;
	}
	
	/**
	 * Config
	 * @return
	 */
	public String nameModified() {
		return name().replace("_", ".");
	}
	
	/**
	 * Adds color code to String.
	 * Red false, Green true
	 * @param args - boolean as String
	 * @return colored String
	 */
	public static String coloredBoolean(String args) {
		if(args.equalsIgnoreCase("true") || args.equalsIgnoreCase("false"))
			return ChatColor.translateAlternateColorCodes('&', (args.equalsIgnoreCase("true")) ? "&2true" : "&cfalse");
		return args;
	}

	/**
	 * Adds color code to String.
	 * Red false, Green true
	 * @param args - boolean as String
	 * @param bold - if string should be bold
	 * @return colored String
	 */
	public static String coloredBoolean(String args, boolean bold) {
		if(args.equalsIgnoreCase("true") || args.equalsIgnoreCase("false"))
			if(bold)
				return ChatColor.translateAlternateColorCodes('&', (args.equalsIgnoreCase("true")) ? "&2&ltrue&r" : "&c&lfalse&r");
			else
				return ChatColor.translateAlternateColorCodes('&', (args.equalsIgnoreCase("true")) ? "&2true&r" : "&cfalse&r");
		if(bold)
			return ChatColor.translateAlternateColorCodes('&', "&l" + args + "&r");
		return args;
	}
	
	/**
	 * builds recursive BaseComponent with RGB colors
	 * @param bc - BaseComponent, TextComponent
	 * @param color - Color for BaseComponent
	 * @return
	 */
	private static BaseComponent rgbColor(BaseComponent bc, ChatColor color) {
		if(bc instanceof TextComponent) {
			if(color != null)
				((TextComponent)bc).setColor(color);
			// getText
			String text = ((TextComponent)bc).getText();
			// if text contains hexColor
			if(text.contains("&#")) {
				// find first hexColor
				int i = text.indexOf("&#");
				// substring first part(old color)
				((TextComponent)bc).setText(text.substring(0, i));
				// substring last part(new color)
				bc.addExtra(
						rgbColor(
								new TextComponent(
										text.substring(i + 8)),
										ChatColor.of(text.substring(i + 1, i + 8))));
			} else {
				// text with replaced + legacy ChatColor
				((TextComponent)bc).setText(ChatColor.translateAlternateColorCodes('&', text));
			}
		}
		return bc;
	}
}
