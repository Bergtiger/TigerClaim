package de.bergtiger.claim.bdo;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.flags.Flag;

import de.bergtiger.claim.data.Config;
import de.bergtiger.claim.data.Lang;

import static de.bergtiger.claim.data.Cons.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;

public class Confirmation {

	private final Player player;
	private final Location location;
	private final Integer radius;
	private final Boolean vert;
	private final HashMap<Flag<?>, Object> flags;
	
	public Confirmation(Player p, Location loc, Integer r, boolean expandVert, HashMap<Flag<?>, Object> flags) {
		this.player = p;
		this.location = loc;
		this.radius = r;
		this.vert = expandVert;
		this.flags = flags;
		if((flags != null) && !flags.isEmpty()) {
			Iterator<Flag<?>> i = flags.keySet().iterator();
			while(i.hasNext()) {
				Flag<?> f = i.next();
				Object o = flags.get(f);
				if(o instanceof String && o.toString().contains("@p"))
					flags.put(f, o.toString().replaceAll("@p", p.getName()));
			}
		}
	}

	/**
	 * Builds Region Name.
	 * @return
	 */
	public String getRegionName() {
		if(player != null) {
			Config c = Config.inst();
			String pattern = null;
			if(c.hasValue(Config.REGION_PATTERN))
				pattern = c.getValue(Config.REGION_PATTERN);
			if(pattern != null) {
				return pattern
						.replace(PLAYER, player.getName())
						.replace(TIME, DateTimeFormatter.ofPattern(Lang.FORMAT_TIME.get()).format(LocalDateTime.now()));
			} else {
				// Error
				return player.getName();
			}
		}
		return null;
	}
	
	/**
	 * Get Player.
	 * @return the p
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * 
	 * @return
	 */
	public Location getLocation() {
		return location;
	}
	
	public Integer getMinX() {
		return Math.min(location.getBlockX() - radius, location.getBlockX() + radius);
	}
	
	public Integer getMaxX() {
		return Math.max(location.getBlockX() - radius, location.getBlockX() + radius);
	}
	
	public Integer getMinY() {
		if(isExpandVert())
			return 0;
		return Math.min(location.getBlockY() - radius, location.getBlockY() + radius);
	}
	
	public Integer getMaxY() {
		if(isExpandVert())
			return 255;
		return Math.max(location.getBlockY() - radius, location.getBlockY() + radius);
	}
	
	public Integer getMinZ() {
		return Math.min(location.getBlockZ() - radius, location.getBlockZ() + radius);
	}
	
	public Integer getMaxZ() {
		return Math.max(location.getBlockZ() - radius, location.getBlockZ() + radius);
	}

	/**
	 * Get Radius.
	 * @return the r
	 */
	public Integer getR() {
		return radius;
	}
	
	public boolean isExpandVert() {
		return vert;
	}
	
	public HashMap<Flag<?>, Object> getFlags() {
		return flags;
	}
}
