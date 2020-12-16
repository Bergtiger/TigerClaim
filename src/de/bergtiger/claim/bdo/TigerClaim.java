package de.bergtiger.claim.bdo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.bergtiger.claim.Claims;
import de.bergtiger.claim.data.Config;

import static de.bergtiger.claim.data.Cons.PLAYER;
import static de.bergtiger.claim.data.Cons.COUNTER;
import static de.bergtiger.claim.data.Cons.TIME;

public abstract class TigerClaim {
	
	private final Player player;
	private final World world;
	private final HashMap<Flag<?>, Object> flags;
	private final Boolean expandVert;
	private final Boolean overlapping;
	private final String pattern;
	private final String timePattern;
	private final Integer gap;
	
	private Integer playerRegionCount = 0;
	private Integer regionCounter = 1;

	public TigerClaim(@Nonnull Player player, World world) {
		this.player = player;
		this.world = world;
		// load missing values from Configuration
		Config c = Config.inst();
		// Flags
		if(c.hasFlags())
			flags = c.getFlags();
		else
			flags = null;
		// timePattern
		if(c.hasValue(Config.TIME_PATTERN))
			timePattern = c.getValue(Config.TIME_PATTERN).toString();
		else
			timePattern = "dd-MM-yyyy";
		// IdPattern
		if(c.hasValue(Config.REGION_PATTERN))
			pattern = c.getValue(Config.REGION_PATTERN).toString();
		else
			pattern = null;
		// Gap
		if(c.hasValue(Config.REGION_GAP)) {
			Integer i = null;
			try {
				i = Integer.valueOf(c.getValue(Config.REGION_GAP).toString());
			} catch (NumberFormatException e) {
				Claims.inst().getLogger().log(Level.SEVERE, Config.REGION_GAP + " has to be a Number.", e);
			}
			gap = i;
		}
		else
			gap = null;
		// Expand Vertical
		if(c.hasValue(Config.REGION_EXPAND_VERT))
			expandVert = Boolean.valueOf(c.getValue(Config.REGION_EXPAND_VERT).toString());
		else
			expandVert = false;
		// Overlapping
		if(c.hasValue(Config.REGION_OVERLAPPING))
			overlapping = Boolean.valueOf(c.getValue(Config.REGION_OVERLAPPING).toString());
		else
			overlapping = false;
	}
	
	public TigerClaim(@Nonnull Player player, World world, HashMap<Flag<?>, Object> flags, String time, String pattern, Integer gab, Boolean expantVert, Boolean overlapping) {
		this.player = player;
		this.world = world;
		this.flags = flags;
		this.timePattern = time;
		this.pattern = pattern;
		this.gap = gab;
		this.expandVert = expantVert;
		this.overlapping = overlapping;
	}

	/**
	 * Player who wants to claim.
	 * @return Player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * World where claim will be.
	 * @return World
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * Counts regions from this Player.
	 */
	public void addPlayerRegionCount() {
		playerRegionCount++;
	}

	/**
	 * Return regions counted from this Player.
	 * @return Integer
	 */
	public Integer getPlayerRegionCount() {
		return playerRegionCount;
	}

	/**
	 * Counts regions with equal name.
	 */
	public void addRegionCounter() {
		regionCounter++;
	}

	/**
	 * Return regions counted with equal name.
	 * @return Integer
	 */
	public Integer getRegionCounter() {
		return regionCounter;
	}
	
	/**
	 * Return if region pattern contains counter.
	 * @return Boolean
	 */
	public Boolean hasRegionCounter() {
		return pattern.contains(COUNTER);
	}
	
	/**
	 * Flags that will be set when claim is created.
	 * @return
	 */
	public HashMap<Flag<?>, Object> getFlags() {
		return flags;
	}
	
	/**
	 * Get id/name of claim.
	 * if no pattern is given, the name of the player is returned.
	 * @return String
	 */
	public String getId() {
		if(pattern != null)
			return pattern
					.replace(PLAYER, player.getName())
					.replace(TIME, DateTimeFormatter.ofPattern(timePattern).format(LocalDateTime.now()))
					.replace(COUNTER, Integer.toString(regionCounter));
		return player.getName();
	}
	
	/**
	 * Gap between regions and claim.
	 * @return Integer
	 */
	public Integer getGap() {
		return gap;
	}
	
	/**
	 * Region is supposed to be expanded vertically.
	 * @return true if expanded
	 */
	public boolean isExpandVert() {
		return expandVert;
	}
	
	/**
	 * Region is allowed to overlap other regions.
	 * @return false if not allowed to overlap
	 */
	public boolean isOverlapping() {
		return overlapping;
	}
	
	/**
	 * Build Region.
	 * @return
	 */
	public abstract ProtectedRegion getRegion();

	/**
	 * Build Region with gap.
	 * @return
	 */
	public abstract ProtectedRegion getRegionWithGab();
	
	/**
	 * Build Hover.
	 * @return
	 */
	public abstract String buildHover();
}
