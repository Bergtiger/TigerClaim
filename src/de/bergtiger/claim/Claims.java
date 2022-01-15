package de.bergtiger.claim;

import de.bergtiger.claim.data.logger.TigerLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.bergtiger.claim.cmd.Claim;
import de.bergtiger.claim.cmd.ClaimTabCompleter;
import de.bergtiger.claim.data.configuration.Config;
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
		// set logger
		TigerLogger.setLogger(getLogger());
		// load config
		Config.load();
		// Commands
		getCommand(Claim.CMD).setExecutor(new Claim());
		getCommand(Claim.CMD).setTabCompleter(new ClaimTabCompleter());
		// Listener
		Bukkit.getPluginManager().registerEvents(ConfirmationListener.inst(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
	}
}
