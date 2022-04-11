package de.bergtiger.claim.bdo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.logging.Level;

import de.bergtiger.claim.data.logger.TigerLogger;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.bergtiger.claim.data.configuration.Config;

import static de.bergtiger.claim.data.language.Cons.PLAYER;
import static de.bergtiger.claim.data.language.Cons.COUNTER;
import static de.bergtiger.claim.data.language.Cons.TIME;

public abstract class TigerClaim {
	
	private final Player player;
	private final World world;
	private HashMap<Flag<?>, Object> flags;
	private final Boolean expandVert;
	private final Boolean overlappingAllowed;
	private final Boolean ownOverlappingAllowed;
	private final String pattern;
	private String timePattern;
	private final Integer gapXZ;
	private final Integer gapY;
	
	private Integer playerRegionCount = 0;
	private Integer regionCounter = 1;

	protected final int minHeight = Config.getInt(Config.REGION_MIN_HEIGHT);
	protected final int maxHeight = Config.getInt(Config.REGION_MAX_HEIGHT);

	public TigerClaim(Player player, World world) {
		this.player = player;
		this.world = world;
		// load missing values from Configuration
		// Flags
		if(Config.hasValue(Config.REGION_FLAGS))
			flags = Config.getFlags();
		else
			flags = null;
		// timePattern
		if ((timePattern = Config.getString(Config.TIME_PATTERN)) == null)
			timePattern = "dd-MM-yyyy";
		// IdPattern
		pattern = Config.getString(Config.REGION_PATTERN);
		// GapXZ
		if(Config.hasValue(Config.REGION_GAP_XZ)) {
			Integer i = null;
			try {
				i = Config.getInt(Config.REGION_GAP_XZ);
			} catch (NumberFormatException e) {
				TigerLogger.log(Level.SEVERE, String.format("&6'&e%s&6' &chas to be a Number.", Config.REGION_GAP_XZ), e);
			}
			gapXZ = i;
		}
		else
			gapXZ = null;
		// GapY
		if(Config.hasValue(Config.REGION_GAP_Y)) {
			Integer i = null;
			try {
				i = Config.getInt(Config.REGION_GAP_Y);
			} catch (NumberFormatException e) {
				TigerLogger.log(Level.SEVERE, String.format("&6'&e%s&6' &chas to be a Number.", Config.REGION_GAP_Y), e);
			}
			gapY = i;
		}
		else
			gapY = null;
		// Expand Vertical
		expandVert = Config.getBoolean(Config.REGION_EXPAND_VERT);
		// Overlapping
		overlappingAllowed = Config.getBoolean(Config.REGION_OVERLAPPING);
		ownOverlappingAllowed = Config.getBoolean(Config.REGION_OWN_OVERLAPPING);
	}
	
	public TigerClaim(Player player, World world, HashMap<Flag<?>, Object> flags, String time, String pattern, Integer gapXZ, Integer gapY, Boolean expandVert, Boolean overlappingAllowed, Boolean ownOverlappingAllowed) {
		this.player = player;
		this.world = world;
		this.flags = flags;
		this.timePattern = time;
		this.pattern = pattern;
		this.gapXZ = gapXZ;
		this.gapY = gapY;
		this.expandVert = expandVert;
		this.overlappingAllowed = overlappingAllowed;
		this.ownOverlappingAllowed = ownOverlappingAllowed;
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
	 * @return map of flags and their value
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
	 * GapXZ between regions and claim.
	 * @return Integer
	 */
	public Integer getGapXZ() {
		return gapXZ;
	}

	/**
	 * GapY between regions and claim.
	 * @return Integer
	 */
	public Integer getGapY() {
		return gapY;
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
	 * @return false if not allowed overlapping
	 */
	public boolean getOverlappingAllowed() {
		return overlappingAllowed;
	}

	/**
	 * The region may overlap other regions whose owner is the same.
	 * @return false if not allowed own overlapping
	 */
	public Boolean getOwnOverlappingAllowed() {
		return ownOverlappingAllowed;
	}

	/**
	 * Build Region.
	 * @return ProtectedRegion
	 */
	public abstract ProtectedRegion getRegion();

	/**
	 * Build Region with gap.
	 * @return ProtectedRegion
	 */
	public abstract ProtectedRegion getRegionWithGab();
	
	/**
	 * Build Hover.
	 * @return String
	 */
	public abstract String buildHover();

	/**
	 * Get the area from this region
	 * @return double
	 */
	public abstract double getArea();
}
