package de.bergtiger.claim.data.configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

import de.bergtiger.claim.cmd.CmdList;
import de.bergtiger.claim.data.ReadMe;
import de.bergtiger.claim.data.language.Cons;
import de.bergtiger.claim.data.language.Lang;
import de.bergtiger.claim.data.logger.TigerLogger;
import de.bergtiger.claim.listener.ConfirmationListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import de.bergtiger.claim.Claims;

/**
 * 
 * @author Bergtiger
 *
 */
public class Config {

	private static Config instance;

	private Config() {
	}

	public static Config inst() {
		if (instance == null)
			instance = new Config();
		return instance;
	}

	public static final String
		// Config
		CONFIG				= "config",
		// Time
		TIME_PATTERN		= CONFIG + ".Time.Pattern",
		// Page
		PAGE_LENGTH			= CONFIG + ".PageLength",
		// Region
		REGION_PATTERN		= CONFIG + ".Region.Pattern", 
		REGION_EXPAND_VERT	= CONFIG + ".Region.ExpandVert",
		REGION_OVERLAPPING	= CONFIG + ".Region.Overlapping",
		REGION_RADIUS		= CONFIG + ".Region.Radius", 
		REGION_FLAGS		= CONFIG + ".Region.Flags",
		REGION_GAP			= CONFIG + ".Region.Gap",
		// height
		REGION_MIN_HEIGHT	= CONFIG + ".Region.Height.Min",
		REGION_MAX_HEIGHT	= CONFIG + ".Region.Height.Max";

	/**
	 * Check if key exists
	 * @param key to check
	 * @return true if key ecists in configuration
	 */
	public static boolean hasValue(String key) {
		return Claims.inst().getConfig().contains(key);
	}

	/**
	 * Get value
	 * @param key to value
	 * @return Object or null if key does not exist
	 */
	public static Object getValue(String key) {
		FileConfiguration cfg = Claims.inst().getConfig();
		return cfg.contains(key) ? cfg.get(key) : null;
	}

	/**
	 * Get String
	 * @param key to value
	 * @return String or null if key does not exist
	 */
	public static String getString(String key) {
		Object o = getValue(key);
		if (o != null) {
			return o instanceof String s ? s : o.toString();
		}
		return null;
	}

	/**
	 * Get boolean
	 * @param key to value
	 * @return boolean or false if key does not exist
	 */
	public static boolean getBoolean(String key) {
		Object o = getValue(key);
		if (o != null) {
			return o instanceof Boolean b ? b : Boolean.parseBoolean(o.toString());
		}
		return false;
	}

	/**
	 * Get int
	 * @param key to value
	 * @return int or 0 if key does not exist
	 */
	public static int getInt(String key) {
		Object o = getValue(key);
		if (o != null) {
			try {
				return o instanceof Integer i ? i : Integer.parseInt(o.toString());
			} catch (NumberFormatException e) {
				TigerLogger.log(Level.WARNING, String.format("&6'&e%s&6' &cis not a number", key));
			}
		}
		return 0;
	}

	public static HashMap<Flag<?>, Object> getFlags() {
		FileConfiguration cfg = Claims.inst().getConfig();
		HashMap<Flag<?>, Object> flags = new HashMap<>();
		if (cfg.contains(REGION_FLAGS)) {
			cfg.getConfigurationSection(REGION_FLAGS).getKeys(false).forEach(k -> {
				FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
				Flag<?> flag = registry.get(k);
				if (flag != null) {
					// add to HashMap
					// check which type
					if (flag instanceof StateFlag) {
						flags.put(flag, StateFlag.State.valueOf(cfg.getString(String.format("%s.%s", REGION_FLAGS, k)).toUpperCase()));
					} else if (flag instanceof BooleanFlag) {
						flags.put(flag, cfg.getBoolean((String.format("%s.%s", REGION_FLAGS, k))));
					} else if (flag instanceof IntegerFlag) {
						flags.put(flag, cfg.getInt(String.format("%s.%s", REGION_FLAGS, k)));
					} else if (flag instanceof DoubleFlag) {
						flags.put(flag, cfg.getDouble(String.format("%s.%s", REGION_FLAGS, k)));
					} else {
						flags.put(flag, cfg.get(String.format("%s.%s", REGION_FLAGS, k)));
					}
				}
			});
		}
		return flags;
	}

	/**
	 * Set value
	 * @param key to value
	 * @param value to set, if value 'null' then element will be removed
	 */
	public static void setValue(String key, Object value) {
		if (key != null && !key.isBlank()) {
			Claims.inst().getConfig().set(key, value);
			// save configuration
			Claims.inst().saveConfig();
			// reload
			load();
		}
	}

	/**
	 * Set flag
	 * @param flag to set
	 * @param value to set, if value 'null' then element will be removed
	 */
	public static void setFlag(Flag<?> flag, Object value) {
		if (flag != null) {
			Claims.inst().getConfig().set(String.format("%s.%s", REGION_FLAGS, flag.getName()), value.toString());
			// save configuration
			Claims.inst().saveConfig();
			// reload
			load();
		}
	}

/*	protected void checkConfigBoolean(FileConfiguration cfg, String path, Boolean value) {
		if ((cfg != null) && (path != null) && (!path.isEmpty()) && (value != null)) {
			if (cfg.contains(path)) {
				// Check value
				String s = cfg.getString(path);
				if (!(s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false")))
					cfg.set(path, value);
			} else {
				// Add Value
				cfg.addDefault(path, value);
			}
		}
	}

	protected void checkConfigInteger(FileConfiguration cfg, String path, Integer value) {
		if ((cfg != null) && (path != null) && (!path.isEmpty())) {
			if (cfg.contains(path)) {
				// Check value
				String s = cfg.getString(path);
				try {
					Integer.valueOf(s);
				} catch (NumberFormatException e) {
					if (!s.equalsIgnoreCase("false"))
						cfg.set(path, (value != null) ? value : false);
				}
			} else {
				// Add Value
				cfg.addDefault(path, (value != null) ? value : false);
			}
		}
	}

	protected void checkConfigString(FileConfiguration cfg, String path, String value) {
		if ((cfg != null) && (path != null) && (!path.isEmpty())) {
			if (cfg.contains(path)) {
				// Check value
				String s = cfg.getString(path);
				if (s == null) {
					cfg.set(path, value);
				}
			} else {
				// Add Value
				cfg.addDefault(path, value);
			}
		}
	}*/

	public static void load() {
		Claims.inst().reloadConfig();
		// clear cache
		ConfirmationListener.inst().clearQueue();
		CmdList.clear();
		// load configuration
		inst().save();
		// load language
		inst().handleLanguage();
		// save ReadMe
		ReadMe.save();
	}

	private void save() {
		FileConfiguration cfg = Claims.inst().getConfig();
		// Time
		if (!cfg.contains(TIME_PATTERN))
			cfg.addDefault(TIME_PATTERN, "dd-MM-yyyy");
		// Page
		if (!cfg.contains(PAGE_LENGTH))
			cfg.addDefault(PAGE_LENGTH, 15);
		// Region
		// region pattern
		if (!cfg.contains(REGION_PATTERN))
			cfg.addDefault(REGION_PATTERN, Cons.PLAYER + "_" + Cons.COUNTER + "_" + Cons.TIME);
		// default radius
		if (!cfg.contains(REGION_RADIUS))
			cfg.addDefault(REGION_RADIUS, 39);
		// gap size
		if (!cfg.contains(REGION_GAP))
			cfg.addDefault(REGION_GAP, 10);
		// expand vert
		if (!cfg.contains(REGION_EXPAND_VERT))
			cfg.addDefault(REGION_EXPAND_VERT, true);
		// overlap
		if (!cfg.contains(REGION_OVERLAPPING))
			cfg.addDefault(REGION_OVERLAPPING, false);
		// height
		cfg.addDefault(REGION_MIN_HEIGHT, -64);
		cfg.addDefault(REGION_MAX_HEIGHT, 320);
		// save
		cfg.options().header(String.format("%s (Version: %s)", Claims.inst().getName(), Claims.inst().getDescription().getVersion()));
		cfg.options().copyHeader(true);
		cfg.options().copyDefaults(true);
		Claims.inst().saveConfig();
	}

	private void handleLanguage() {
		File file = new File(Claims.inst().getDataFolder(), "language.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		String k;
		// check Config
		for (Lang l : Lang.values()) {
			k = l.name().replaceAll("_", ".");
			// load value, if value not exist add
			if (cfg.contains(k))
				l.set(cfg.getString(k));
			else
				cfg.set(k, l.get());
		}
		// save modified configuration
		cfg.options().header(String.format("%s Language File (Version: %s)", Claims.inst().getName(), Claims.inst().getDescription().getVersion()));
		cfg.options().copyHeader(true);
		try {
			cfg.save(file);
		} catch (IOException e) {
			TigerLogger.log(Level.SEVERE, "&cCould not save language configuration.", e);
		}
	}
}
