package de.bergtiger.claim.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	};

	public static Config inst() {
		if (instance == null)
			instance = new Config();
		return instance;
	}

	private Claims plugin = Claims.inst();
	private HashMap<String, Object> values;
	private HashMap<Flag<?>, Object> flags;

	public static final String
			// Config
			CONFIG = "config",
			// Time
			TIME_PATTERN = CONFIG + ".Time.Pattern",
			// Region
			REGION_PATTERN = CONFIG + ".Region.Pattern",
			REGION_EXPAND_VERT = CONFIG + ".Region.ExpandVert",
			REGION_RADIUS = CONFIG + ".Region.Radius",
			REGION_FLAGS = CONFIG + ".Region.Flags",
			REGION_GAP = CONFIG + ".Region.Gap";

	public Boolean hasValue(String key) {
		if ((values != null) && (!values.isEmpty())) {
			return values.containsKey(key);
		}
		return false;
	}

	public boolean hasFlags() {
		return flags != null && !flags.isEmpty();
	}

	public HashMap<Flag<?>, Object> getFlags() {
		return flags;
	}

	public void setFlag(Flag<?> flag, Object o) {
		if (flag != null) {
			if ((o == null) && (flags != null) && (flags.containsKey(flag))) {
				flags.remove(flag);
			} else {
				if (o != null) {
					// Check flag value
					try {
						if (flags == null)
							flags = new HashMap<Flag<?>, Object>();
						if (flag instanceof StateFlag) {
							flags.put(flag, StateFlag.State.valueOf(o.toString().toUpperCase()));
						} else if (flag instanceof BooleanFlag) {
							flags.put(flag, Boolean.valueOf(o.toString()));
						} else if (flag instanceof IntegerFlag) {
							flags.put(flag, Integer.valueOf(o.toString()));
						} else if (flag instanceof DoubleFlag) {
							flags.put(flag, Double.valueOf(o.toString()));
						} else {
							flags.put(flag, o);
						}
					} catch (NumberFormatException e) {
						Claims.inst().getLogger().log(Level.WARNING,
								o + " is not a valid number value for Flag (" + flag.getName() + ")");
					} catch (Exception e) {
						Claims.inst().getLogger().log(Level.WARNING,
								o + " is not a valid value for Flag (" + flag.getName() + ")");
					}
				}
			}
		}
	}

	public Object getValue(String key) {
		if ((values != null) && (!values.isEmpty())) {
			return values.get(key);
		}
		return null;
	}

	public void setValue(String key, Object value) {
		if ((key != null) && (!key.isEmpty())) {
			if (values == null)
				values = new HashMap<>();
			values.put(key, value);
		}
	}

	protected void checkConfigBoolean(FileConfiguration cfg, String path, Boolean value) {
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
	}

	public void checkConfig() {
		FileConfiguration cfg = plugin.getConfig();
		if (cfg != null) {
			// Region
			checkConfigBoolean(cfg, REGION_EXPAND_VERT, true);
			checkConfigInteger(cfg, REGION_RADIUS, 39);
			checkConfigInteger(cfg, REGION_GAP, 39);

			cfg.options().copyDefaults(true);
			cfg.options().copyHeader(true);
			plugin.saveConfig();
		}
		Logger.getLogger(Config.class.getName()).log(Level.FINE, "");
	}

	public void loadConfig() {
		Boolean save = false;
		Logger.getLogger(Config.class.getName()).log(Level.FINE, Lang.CONFIG_LOAD_START.get());
		plugin.reloadConfig();
		FileConfiguration cfg = plugin.getConfig();
		if ((cfg != null) && (cfg.contains(CONFIG))) {
			checkConfig();
			for (String k : cfg.getKeys(true)) {
				if (k.contains(REGION_FLAGS)) {
					FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
					Flag<?> f = registry.get(k.replace(REGION_FLAGS + ".", ""));
					if (f != null) {
						setFlag(f, cfg.getString(k));
					}
				} else {
					if (!cfg.isConfigurationSection(k)) {
						if (values == null)
							values = new HashMap<>();
						values.put(k, cfg.get(k));
					}
				}
			}
		} else {
			if (!save) {
				save = true;
				saveConfig();
				loadConfig();
			} else {
				Logger.getLogger(Config.class.getName()).log(Level.SEVERE, "Could not save Config");
			}
		}
		// Load Language
		loadLanguage();
		Logger.getLogger(Config.class.getName()).log(Level.FINE, Lang.CONFIG_LOAD_FINISH.get());
	}

	public void loadLanguage() {
		File file = new File("plugins/" + plugin.getName() + "/language.yml");
		if (file.exists()) {
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			for (Lang l : Lang.values()) {
				if (cfg.contains(l.name().replace("_", "."))) {
					l.setValue(cfg.getString(l.name().replace("_", ".")));
				} else {
					cfg.addDefault(l.name().replace("_", "."), l.getValue());
				}
			}
			try {
				cfg.options().copyDefaults(true);
				cfg.options().copyHeader(true);
				cfg.save(file);
			} catch (Exception e) {
				Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, e);
			}
		} else {
			// create Language
			saveLanguage();
		}
	}

	public void saveConfig() {
		FileConfiguration cfg = plugin.getConfig();
		if (cfg != null) {
			Logger.getLogger(Config.class.getName()).log(Level.FINE, Lang.CONFIG_SAVE_START.get());
			// Time
			if(!cfg.contains(TIME_PATTERN))
				cfg.addDefault(TIME_PATTERN, "dd-MM-yyyy");
			// Region
			if (!cfg.contains(REGION_PATTERN))
				cfg.addDefault(REGION_PATTERN, Cons.PLAYER + "_claim_" + Cons.TIME);
			if (!cfg.contains(REGION_RADIUS))
				cfg.addDefault(REGION_RADIUS, 39);
			if (!cfg.contains(REGION_GAP))
				cfg.addDefault(REGION_GAP, 10);
			if (!cfg.contains(REGION_EXPAND_VERT))
				cfg.addDefault(REGION_EXPAND_VERT, true);
			// Values
			if (values != null && !values.isEmpty())
				// Set Values
				values.forEach((k, v) -> cfg.set(k, v));
			// Flags
			if (cfg.contains(REGION_FLAGS))
				cfg.set(REGION_FLAGS, null);
			if (flags != null && !flags.isEmpty()) {
				// Set Flags
				flags.forEach((f, v) -> {
					if (v != null)
						cfg.set(REGION_FLAGS + "." + f.getName(), v.toString());
				});
			}
			// Options
			cfg.options().header(plugin.getName() + " (Version: " + plugin.getDescription().getVersion() + ")");
			cfg.options().copyDefaults(true);
			cfg.options().copyHeader(true);
			plugin.saveConfig();
			Logger.getLogger(Config.class.getName()).log(Level.FINE, Lang.CONFIG_SAVE_FINISH.get());
		}
	}

	public void saveLanguage() {
		File file = new File("plugins/" + plugin.getName() + "/language.yml");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		for (Lang l : Lang.values()) {
			if (cfg.contains(l.name().replace("_", ".")))
				cfg.set(l.name().replace("_", "."), l.getValue());
			else
				cfg.addDefault(l.name().replace("_", "."), l.getValue());
		}
		try {
			cfg.options().header(
					plugin.getName() + " Language File (Version: " + plugin.getDescription().getVersion() + ")");
			cfg.options().copyDefaults(true);
			cfg.options().copyHeader(true);
			cfg.save(file);
		} catch (IOException e) {
			Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, e);
		}
	}
}
