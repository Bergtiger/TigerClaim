package de.bergtiger.claim;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.bergtiger.claim.cmd.Claim;
import de.bergtiger.claim.cmd.ClaimTabCompleter;
import de.bergtiger.claim.data.Config;
import de.bergtiger.claim.data.ReadMe;
import de.bergtiger.claim.listener.ConfirmationListener;
import de.bergtiger.claim.listener.PlayerListener;

public class Claims extends JavaPlugin {

	private static Claims instance;
	
	public static Claims inst() {
		return instance;
	}
	
	@Override
	public void onEnable() {
		instance = this;
		// load config
		Config.inst().loadConfig();
		Logger.getLogger(Claims.class.getName()).log(Level.INFO, "loaded config");
		// save ReadMe
		ReadMe.save();
		// Commands
		getCommand("claim").setExecutor(new Claim());
		getCommand("claim").setTabCompleter(new ClaimTabCompleter());
		// Listener
		Bukkit.getPluginManager().registerEvents(ConfirmationListener.inst(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
	}
}
