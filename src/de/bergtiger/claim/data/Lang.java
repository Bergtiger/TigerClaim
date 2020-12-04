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
	
	NOVALIDID			("&cNot a valid ButtonId &e'&4" + VALUE + "&e'"),
	NOVALIDMATERIAL		("&cNot a valid Material &e'&4" + VALUE + "&e'"),
	NOBUTTONFOUND		("&cCould not find Button&e[&6" + VALUE + "&e]"),
	NOBUTTONS			("&cNo Buttons found."),
	NOOPTION			("&cNot a valid Option &e'&4" + VALUE + "&e'"),
	NOSUCHWORLD			("&e'&4" + VALUE + "&e' &cis not a World"),
	NONUMBER			("&cNot a Number"),
	NONUMBERVALUE		("&e'&4" + VALUE + "&e' &cis not a Number"),
	
	
	FORMAT_TIME			("dd-MM-yyyy_HH:mm"),
	
	// Config
	CONFIG_LOAD_START	("Loading Config"),
	CONFIG_LOAD_FINISH	("Finished loading"),
	
	CONFIG_SAVE_START	("Saving Config"),
	CONFIG_SAVE_FINISH	("Finished saving"),
	// Commands
	CMD_HEADER			("&a----<(&eTigerClaim&a)>----"),
	CMD_FOOTER			("&a---------------------------"),
	CMD_SET				("&7set"),
	CMD_INSERT			("&7claim"),
	CMD_RELOAD			("&7reload"),
	CMD_PLUGIN			("&7plugin"),
	// Commands Hover
	CMD_HOVER_SET		("set config."),
	CMD_HOVER_INSERT	("created a new Claim around your Position."),
	CMD_HOVER_RELOAD	("reloads TigerClaimPlugin(config and cache)"),
	CMD_HOVER_PLUGIN	("shows plugin info"),
	// Insert
	INSERT_EXIST		("&cThere is already &4Button&e[&6" + VALUE + "&e] at this Block."),
	INSERT_ERROR		("&cCould not create Button."),
	INSERT_SUCCESS		("&aButton&f[&6" + VALUE + "&f] &acreated successfully."),
	INSERT_HOVER		("&aShow Button&f[&6" + VALUE + "&f] &aoverview"),
	// Reload
	RELOAD_FINISHED		("&aReload finished."),
	// PluginInfo
	PLUGIN_HEADER		("&a----<(&eTigerClaim&a)>----"),
	PLUGIN_FOOTER		("&a------------------------"),
	PLUGIN_VERSION		("&aVersion&e: &6" + VALUE),
	PLUGIN_RADIUS		("&aRadius&e: &6" + VALUE),
	PLUGIN_EXPANDVERT	("&aExpandVert&e: &6" + VALUE),
	PLUGIN_PATTERN		("&aRegionPattern&e: &6" + VALUE),
	PLUGIN_FLAGS		("&aFlags"),
	PLUGIN_FLAG_LIST	("  &6" + FLAG + "&e: &f" + VALUE),
	// PluginInfo Hover
	PLUGIN_HOVER_RADIUS		("&aSet radius"),
	PLUGIN_HOVER_EXPANDVERT	("&aChange expand vertical"),
	PLUGIN_HOVER_PATTERN	("&aSet a new RegionPattern"),
	PLUGIN_HOVER_FLAGS		("&aSet a Flag");
	
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
