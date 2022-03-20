package de.bergtiger.claim.data.language;

import java.util.Collection;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.chat.ClickEvent.Action;

/**
 * 
 * @author Bergtiger
 *
 */
public enum Lang implements Cons {
	
	NOPERMISSION		("&cYou have no permission"),
	
	NOARGUMENT			(String.format("&cNot a valid option &e'&4%s&e'", VALUE)),
	NONUMBER			("&cNot a number"),
	NONUMBERVALUE		(String.format("&e'&4%s&e' &cis not a number", VALUE)),
	NOSUCHFLAG			(String.format("&e'&4%s&e' &cis not a flag", VALUE)),
	NOFLAGVALUE			(String.format("&e'&4%s&e' &cis not a valid value for &6%s", VALUE, TYPE)),
	NOPLAYER			("&cYou have to be a player to perform this command."),
	NOREGIONS			("&cNo regions"),
	NOSUCHREGION		(String.format("&cNo such region &e'&4%s&e'", VALUE)),
	NOTINREGION			("&cYou are not standing in a region"),
	TOMANYREGIONS		("&cYou are standing in several regions, please specify one of them."),
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
	CMD_CHECK			("&7check"),
	CMD_DELETE			("&7delete"),
	CMD_INFO			("&7info"),
	CMD_INSERT			("&7claim"),
	CMD_RELOAD			("&7reload"),
	CMD_PLUGIN			("&7plugin"),
	CMD_WIKI			("&7more information in the wiki"),
	// Commands Hover
	CMD_HOVER_SET		("set config."),
	CMD_HOVER_INFO		("information how you create a Claim."),
	CMD_HOVER_LIST		("list all Regions where you are an owner."),
	CMD_HOVER_CHECK		("checks if you can claim your selection."),
	CMD_HOVER_DELETE	("deletes your region."),
	CMD_HOVER_INSERT	("created a new Claim around your Position."),
	CMD_HOVER_RELOAD	("reloads TigerClaimPlugin(config and cache)"),
	CMD_HOVER_PLUGIN	("shows plugin info"),
	CMD_HOVER_WIKI		("opens the wiki page"),
	// Insert
	INSERT_OVERLAPPING	("&cOverlapping Regions."),
	INSERT_SUCCESS		("&aCreated Claim successfully."),
	INSERT_EXISTING		("&cClaim &e'&6" + VALUE + "&e' &calready exists."),
	INSERT_CANCEL		("&cClaim canceled."),
	INSERT_LIMIT		("&cYou reached your claim-limit&e[&6" + VALUE + "&e/&6" + LIMIT + "&e]"),
	// claim insert
	CLAIM_MESSAGE		("&eConfirm Claim&f:"),
	CLAIM_YES			(" &aYes"),
	CLAIM_NO			(" &cNo"),
	INSERT_HOVER_SUCCESS("&aShow Claim."),
	CLAIM_HOVER_TEXT	("&ecreates a claim at your position."),
	CLAIM_HOVER_YES		("&ecreates your claim."),
	CLAIM_HOVER_NO		("&eabort claim."),
	// Info
	INFO				("How to claim a region?\n"),
	// List
	LIST_HEADER			("&a----<(&e" + VALUE + "'s Regions&a)>----"),
	LIST_REGION			("&e- " + VALUE),
	LIST_FOOTER_NEXT	("&f[&eNext&f]"),
	LIST_FOOTER_PREV	("&f[&eprev&f]"),
	LIST_FOOTER_PLAYER	("&a-----&6[&e" + PAGE + "&f/&e" + PAGEMAX + "&6]&a-----"),
	LIST_FOOTER_CONSOLE	("&a-----&6[&e" + PAGE + "&f/&e" + PAGEMAX + "&6]&a-----"),
	
	LIST_HOVER			("&f Show region info for: &e" + ID),
	
	LIST_FOOTER			("&a---------------------------"),
	LIST_ROW			("&e- " + VALUE),
	// Claim
	CLAIM_POLYGON		("&aRegion&e: &6" + ID + "\n&apoints&e:" + VALUE),
	CLAIM_CUBOID		("&aRegion&e: &6" + ID + "\n&apos1&e: " + POS1 + "\n&apos2&e: " + POS2),
	CLAIM_RADIUS		("&aRegion&e: &6" + ID + "\n&aCenter&e: " + POS1 + "\n&aradius&e: &6" + VALUE),
	CLAIM_PATTERN_POINTS("  &6" + X + "&f, &6" + Z),
	CLAIM_PATTERN_LOC	("&6" + X + "&f, &6" + Y + "&f, &6" + Z),
	// Check
	CHECK_MESSAGE		("&eDo you want to check this region?"),
	CHECK_YES			(" &aYes"),
	CHECK_NO			(" &cNo"),
	CHECK_HOVER_TEXT	("&eCheck if this region is empty and if you can claim this region."),
	CHECK_HOVER_YES		("&eperforms check."),
	CHECK_HOVER_NO		("&eaborts check."),

	CHECK_LIMIT			(String.format("&cYou reached your limit of &e'&6%s&e' regions and you can not claim another one.", VALUE)),
	CHECK_AVAILABLE		("&aYour selection is available."),
	CHECK_OVERLAPPING	("&cYour selection is overlapping with another region."),

	// Delete
	DELETE_MESSAGE		(String.format("&eDo you really want to delete this region &e'&e%s&6'&e?", VALUE)),
	DELETE_YES			(" &aYes"),
	DELETE_NO			(" &cNo"),
	DELETE_HOVER_TEXT	("&eDeletes your region. There is no way back. Be sure you really want this."),
	DELETE_HOVER_YES	("&eWill delete your region forever. It is a really long time."),
	DELETE_HOVER_NO		("&eaborts delete."),
	DELETE_SUCCESS		(String.format("&aRemoved region &e'&6%s&e' &asuccessfully.", VALUE)),

	// Expand
	EXPAND_YES			(" &aYes"),
	EXPAND_NO			(" &cNo"),

	// Height
	HEIGHT_YES			(" &aYes"),
	HEIGHT_NO			(" &cNo"),

	// Reload
	RELOAD_FINISHED		("&aReload finished."),
	// PluginInfo
	PLUGIN_HEADER		("&a----<(&eTigerClaim&a)>----"),
	PLUGIN_FOOTER		("&a------------------------"),
	PLUGIN_VERSION		("&aVersion&e: &6" + VALUE),
	PLUGIN_RADIUS		("&aRadius&e: &6" + VALUE),
	PLUGIN_GAP_XZ		("&aGapXZ&e: &6" + VALUE),
	PLUGIN_GAP_Y		("&aGapY&e: &6" + VALUE),
	PLUGIN_EXPANDVERT	("&aExpandVert&e: &6" + VALUE),
	PLUGIN_OVERLAPPING	("&aOverlapping&e: &6" + VALUE),
	PLUGIN_CHECK		("&aCheck&e: &6" + VALUE),
	PLUGIN_PATTERN_ID	("&aRegionPattern&e: &6" + VALUE),
	PLUGIN_PATTERN_TIME	("&aTimePattern&e: &6" + VALUE),
	PLUGIN_HEIGHT_MIN	(String.format("&aMin height&e: &6%s", VALUE)),
	PLUGIN_HEIGHT_MAX	(String.format("&aMax height&e: &6%s", VALUE)),
	PLUGIN_FLAGS		("&aFlags"),
	PLUGIN_FLAG_LIST	("  &6" + FLAG + "&e: &f" + VALUE),
	PLUGIN_PAGE_LENGTH	("&aPage length&e: &6" + VALUE),
	// PluginInfo Hover
	PLUGIN_HOVER_RADIUS			("&aSet radius"),
	PLUGIN_HOVER_GAP_XZ("&aSet horizontal gap between claim and other regions"),
	PLUGIN_HOVER_GAP_Y("&aSet vertikal gap between claim and other regions"),
	PLUGIN_HOVER_EXPANDVERT		("&aChange expand vertical"),
	PLUGIN_HOVER_OVERLAPPING	("&aChange if claims are allowed to overlap existing regions."),
	PLUGIN_HOVER_CHECK			("&aChange if areas should be separately checked before claiming."),
	PLUGIN_HOVER_PATTERN_ID		("&aSet a new region pattern"),
	PLUGIN_HOVER_PATTERN_TIME	("&aSet a new time pattern"),
	PLUGIN_HOVER_HEIGHT_MIN		("&aSet a new min height for expand vert"),
	PLUGIN_HOVER_HEIGHT_MAX		("&aSet a new max height for expand vert"),
	PLUGIN_HOVER_FLAGS			("&aSet a flag"),
	PLUGIN_HOVER_PAGE_LENGTH	("&aSet page length for claim_list command");
	
	private String value;
	
	Lang(String value) {
		this.value = value;
	}
	
	/**
	 * set value
	 * @param value to set
	 */
	public void set(String value) {
		this.value = value;
	}
	
	/**
	 * get value
	 * @return value
	 */
	public String get() {
		return value;
	}

	/**
	 * Combines TextComponents to one.
	 * @param args {@link TextComponent} to combine
	 * @return a single {@link TextComponent}
	 */
	public static TextComponent combine(TextComponent...args) {
		if(args != null) {
			TextComponent tc = new TextComponent();
			for(TextComponent t : args) {
				if(t != null)
					tc.addExtra(t);
			}
			return tc;
		}
		return null;
	}

	/**
	 * Combines TextComponents to one.
	 * @param args {@link TextComponent} to combine
	 * @return a single {@link TextComponent}
	 */
	public static TextComponent combine(Collection<? extends TextComponent> args) {
		if(args != null) {
			TextComponent tc = new TextComponent();
			for(TextComponent t : args) {
				if(t != null)
					tc.addExtra(t);
			}
			return tc;
		}
		return null;
	}

	/**
	 * Builds a TextComponent with text and colors
	 * @param args - Text
	 * @return TextComponent
	 */
	public static TextComponent build(Lang args) {
		return build(args.value);
	}

	/**
	 * Builds a TextComponent with text and colors
	 * @param args - Text
	 * @return TextComponent
	 */
	public static TextComponent build(String args) {
		return build(args, null, null, null);
	}

	/**
	 * Adds Extras to TextComponent no Color or anything!
	 * @param args - TextComponent witch will be modified
	 * @param cmd - onClick command
	 * @param hover - onHover text
	 * @param cmd_suggestion - onClick suggest command in chat
	 * @return TextComponent
	 */
	public static TextComponent build(Object args, String cmd, Object hover, String cmd_suggestion) {
		if (args != null) {
			TextComponent tc;
			if (args instanceof String s)
				tc = (TextComponent) rgbColor(new TextComponent(s), null);
			else
			if (args instanceof Lang l)
				tc = (TextComponent) rgbColor(new TextComponent(l.value), null);
			else
			if (args instanceof TextComponent t)
				tc = (TextComponent) rgbColor(t, null);
			else
				return null;
			if (cmd != null && !cmd.isEmpty())
				tc.setClickEvent(new ClickEvent(Action.RUN_COMMAND, cmd));
			if (hover != null) {
				if (hover instanceof String s)
					tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new Text(new BaseComponent[] {build(s)})));
				if (hover instanceof Lang l)
					tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new Text(new BaseComponent[] {build(l)})));
				if (hover instanceof BaseComponent hb)
					tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new Text(new BaseComponent[] {hb})));
			}
			if (cmd_suggestion != null && !cmd_suggestion.isEmpty())
				tc.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, cmd_suggestion));
			return tc;
		}
		return null;
	}

	/**
	 * builds recursive BaseComponent with RGB colors
	 * @param bc - BaseComponent, TextComponent
	 * @param color - Color for BaseComponent
	 * @return BaseComponent with color
	 */
	private static BaseComponent rgbColor(BaseComponent bc, ChatColor color) {
		if(bc instanceof TextComponent) {
			if(color != null)
				bc.setColor(color);
			// getText
			String text = ((TextComponent)bc).getText();
			// if text contains hexColor
			if(text.contains("&#")) {
				// find first hexColor
				int i = text.indexOf("&#");
				// substring first part(old color)
				((TextComponent)bc).setText(ChatColor.translateAlternateColorCodes('&', text.substring(0, i)));
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

	/**
	 * Creates a new line.
	 * @return TextComponent representing new line
	 */
	public static TextComponent newLine() {
		return build("\n");
	}

	/**
	 * Replaces each substring of this string that matches the literal target sequence with the specified literal replacement sequence. The replacement proceeds from the beginning of the string to the end, for example, replacing "aa" with "b" in the string "aaa" will result in "ba" rather than "ab".
	 * @param target The sequence of char values to be replaced
	 * @param replacement The replacement sequence of char values
	 * @return The resulting string
	 */
	public String replace(String target, String replacement) {
		return value.replace(target, replacement);
	}
}
