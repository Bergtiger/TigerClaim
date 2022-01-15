package de.bergtiger.claim.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;

import de.bergtiger.claim.data.logger.TigerLogger;
import org.bukkit.Bukkit;

import de.bergtiger.claim.Claims;

public class ReadMe {

	private final File file = new File(Claims.inst().getDataFolder(), "ReadMe.txt");
	
	public static void save() {
		Bukkit.getScheduler().runTaskAsynchronously(Claims.inst(), () -> {
			ReadMe rm = new ReadMe();
			if(!rm.checkVersion())
				rm.saveReadMe();
		});
	}
	
	public boolean checkVersion() {
		if(file.exists()) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String line;
				if((line = br.readLine()) != null) {
					String[] lines = line.split(" ");
					// Check PluginName and Version
					return lines.length >= 2 && lines[0].equalsIgnoreCase(Claims.inst().getName()) && lines[1].equalsIgnoreCase(Claims.inst().getDescription().getVersion());
				}
			} catch (FileNotFoundException e) {
				TigerLogger.log(Level.SEVERE, "&cCould not find file", e);
			} catch (IOException e) {
				TigerLogger.log(Level.SEVERE, "&cError on checkVersion", e);
			} finally {
				if(br != null)
					try {
						br.close();
					} catch (IOException e) {
						TigerLogger.log(Level.SEVERE, "&cError on checkVersion", e);
					}
			}
		}
		return false;
	}
	
	public void saveReadMe() {
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new InputStreamReader(Claims.inst().getResource("ReadMe.txt")));
			bw = new BufferedWriter(new FileWriter(file));
			String line;
			while((line = br.readLine()) != null) {
				bw.write(line.replace("%version%", Claims.inst().getDescription().getVersion()));
				bw.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(bw != null)
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}
