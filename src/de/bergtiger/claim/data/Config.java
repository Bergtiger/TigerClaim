package de.bergtiger.claim.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import de.bergtiger.claim.TigerClaim;

/**
 * 
 * @author Bergtiger
 *
 */
public class Config {

	private static Config instance;
	
	private Config() {};
	
	public static Config inst() {
		if(instance == null)
			instance = new Config();
		return instance;
	}

	private TigerClaim plugin = TigerClaim.inst();
	private HashMap<String, String> values;
	private HashMap<Flag<?>, Object> flags;
	
	public static final String	
	// Langues
	LANG				= "lang",
	DE					= "DE",
	EN					= "EN",
	// Config
	CONFIG				= "config",
	// Region
	REGION_PATTERN		= CONFIG + ".Region.Pattern",
	REGION_EXPAND_VERT	= CONFIG + ".Region.ExpandVert",
	REGION_RADIUS		= CONFIG + ".Region.Radius",
	REGION_FLAGS		= CONFIG + ".Region.Flags",
	
	LANGUAGE			= CONFIG + ".Language",
	PAGE_LENGTH			= CONFIG + ".PageLength",
	LIMIT				= CONFIG + ".Limit";
	
	public Boolean hasValue(String key) {
		if((values != null) && (!values.isEmpty())) {
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
		if(flag != null) {
			if((o == null) && (flags != null) && (flags.containsKey(flag))) {
				flags.remove(flag);
			} else {
				if(flags == null)
					flags = new HashMap<Flag<?>, Object>();
				flags.put(flag, o);
			}
		}
	}
	
	public String getValue(String key) {
		if((values != null) && (!values.isEmpty())) {
			return values.get(key);
		}
		return null;
	}
	
	public void setValue(String key, String value) {
		if((key != null) && (!key.isEmpty())) {
			if(values == null)
				values = new HashMap<>();
			values.put(key, value);
		}
	}
	
	protected void checkConfigBoolean(FileConfiguration cfg, String path, Boolean value) {
		if((cfg != null) && (path != null) && (!path.isEmpty()) && (value != null)) {
			if(cfg.contains(path)) {
				// Check value
				String s = cfg.getString(path);
				if(!(s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false")))
					cfg.set(path, value);
			} else {
				// Add Value
				cfg.addDefault(path, value);
			}
		}
	}
	
	protected void checkConfigInteger(FileConfiguration cfg, String path, Integer value) {
		if((cfg != null) && (path != null) && (!path.isEmpty())) {
			if(cfg.contains(path)) {
				// Check value
				String s = cfg.getString(path);
				try {
					Integer.valueOf(s);
				} catch (NumberFormatException e) {
					if(!s.equalsIgnoreCase("false"))
						cfg.set(path, (value != null) ? value : false);
				}
			} else {
				// Add Value
				cfg.addDefault(path, (value != null) ? value : false);
			}
		}
	}
	
	protected void checkConfigString(FileConfiguration cfg, String path, String value) {
		if((cfg != null) && (path != null) && (!path.isEmpty())) {
			if(cfg.contains(path)) {
				// Check value
				String s = cfg.getString(path);
				if(s == null) {
					cfg.set(path, value);
				}
			} else {
				// Add Value
				cfg.addDefault(path, value);
			}
		}
	}
	
	protected void checkConfigLanguage(FileConfiguration cfg, String path) {
		if((cfg != null) && (path != null) && (!path.isEmpty())) {
			if(cfg.contains(path)) {
				// Check value
				String s = cfg.getString(path);
				if(!((s != null) && (s.equalsIgnoreCase(DE) || s.equalsIgnoreCase(EN))))
					cfg.set(path, EN);
			} else {
				// Add value
				cfg.addDefault(path, EN);
			}
		}
	}
	
	public void checkConfig() {
		FileConfiguration cfg = plugin.getConfig();
		if (cfg != null) {
			// Region
			checkConfigBoolean(cfg, REGION_EXPAND_VERT, true);
			checkConfigInteger(cfg, REGION_RADIUS, 39);
			// Language
			checkConfigLanguage(cfg, LANGUAGE);
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
				if(k.contains(REGION_FLAGS)) {
					System.out.println("Load flag: " + k);
					FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
					Flag<?> f = registry.get(k.replace(REGION_FLAGS + ".", ""));
					if(f != null) {
						setFlag(f, cfg.getString(k));
					}
				} else {
					if (values == null)
						values = new HashMap<>();
					values.put(k, cfg.getString(k));
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
		File file = new File("plugins/" + plugin.getName() + "/" + getValue(LANGUAGE) + ".yml");
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
			// Region
			cfg.addDefault(REGION_PATTERN, Cons.PLAYER + "_claim_" + Cons.TIME);
			cfg.addDefault(REGION_RADIUS, 39);
			cfg.addDefault(REGION_EXPAND_VERT, true);
			
			// Values
			if(values != null && !values.isEmpty()) {
				// Set Values
				values.forEach((k,v) -> {
					cfg.set(k, v);
				});
			}
			// Flags
			if(cfg.contains(REGION_FLAGS))
				cfg.set(REGION_FLAGS, null);
			if(flags != null && !flags.isEmpty()) {
				// Set Flags
				flags.forEach((f, v) -> {
					if(v != null)
						cfg.set(REGION_FLAGS + "." + f.getName(), v.toString());
				});
			}
			// Other
			cfg.addDefault(LANGUAGE, EN);
			cfg.options().header(plugin.getName() + " (Version: " + plugin.getDescription().getVersion() + ")");
			cfg.options().copyDefaults(true);
			cfg.options().copyHeader(true);
			plugin.saveConfig();
			Logger.getLogger(Config.class.getName()).log(Level.FINE, Lang.CONFIG_SAVE_FINISH.get());
		}
	}

	public void saveLanguage() {
		File file = new File("plugins/" + plugin.getName() + "/" + getValue(LANGUAGE) + ".yml");
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
