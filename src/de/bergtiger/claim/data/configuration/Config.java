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
		REGION_OWN_OVERLAPPING = CONFIG + ".Region.OwnOverlapping",
		REGION_CHECK		= CONFIG + ".Region.Check",
		REGION_RADIUS		= CONFIG + ".Region.Radius", 
		REGION_FLAGS		= CONFIG + ".Region.Flags",
		REGION_GAP_XZ		= CONFIG + ".Region.GapXZ",
		REGION_GAP_Y		= CONFIG + ".Region.GapY",
		// height
		REGION_MIN_HEIGHT	= CONFIG + ".Region.Height.Min",
		REGION_MAX_HEIGHT	= CONFIG + ".Region.Height.Max",
		// priority
		REGION_MAX_PRIORITY = CONFIG + ".Region.MaxPriority";


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
		// gap xz size
		if (!cfg.contains(REGION_GAP_XZ))
			cfg.addDefault(REGION_GAP_XZ, 10);
		// gap y size
		if (!cfg.contains(REGION_GAP_Y))
			cfg.addDefault(REGION_GAP_Y, 384);
		// expand vert
		if (!cfg.contains(REGION_EXPAND_VERT))
			cfg.addDefault(REGION_EXPAND_VERT, true);
		// overlap
		if (!cfg.contains(REGION_OVERLAPPING))
			cfg.addDefault(REGION_OVERLAPPING, false);
		// own region overlap
		if (!cfg.contains(REGION_OWN_OVERLAPPING))
			cfg.addDefault(REGION_OWN_OVERLAPPING, true);
		// check
		if (!cfg.contains(REGION_CHECK))
			cfg.addDefault(REGION_CHECK, false);
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
