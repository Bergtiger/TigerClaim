package de.bergtiger.claim.data.permission;

import org.bukkit.command.CommandSender;

/**
 * 
 * @author Bergtiger
 *
 */
public enum Perm {

	/**
	 * includes all Permissions
	 */
	CLAIM_ADMIN		("tclaim.admin"),
	CLAIM_USER		("tclaim.user"),
	
	CLAIM_SET		("tclaim.set"),
	CLAIM_INFO		("tclaim.info"),
	CLAIM_LIST		("tclaim.list"),
	CLAIM_CLAIM		("tclaim.claim"),
	CLAIM_LIMIT		("tclaim.limit"),
	CLAIM_LIMITLESS	("tclaim.nolimit"),
	CLAIM_PLUGIN	("tclaim.plugin"),
	CLAIM_RELOAD	("tclaim.reload"),
	CLAIM_WORLDEDIT	("tclaim.worldedit"),
	CLAIM_DELETE	("tclaim.delete"),
	CLAIM_CHECK		("tclaim.check"),
	CLAIM_EXPAND	("tclaim.expand");

	private final String value;
	
	Perm(String value) {
		this.value = value;
	}

	/**
	 * Get permission String
	 * @return value
	 */
	public String get() {
		return value;
	}

	/**
	 * Check if cs has any of the following permissions
	 * @param cs CommandSender
	 * @param permission to check for
	 * @return true if cs has any of the given permissions
	 */
	public static boolean hasPermission(CommandSender cs, Perm ... permission) {
		if ((cs != null) && (permission != null)) {
			for(Perm p : permission) {
				if (cs.hasPermission(p.value))
					return true;
			}
		}
		return false;
	}

	/**
	 * Check if cs has any of the plugin permissions
	 * @param cs CommandSender
	 * @return true if cs has any of the plugin permissions
	 */
	public static boolean hasPermissionAny(CommandSender cs) {
		if (cs != null) {
			for (Perm p : Perm.values()) {
				if (cs.hasPermission(p.value))
					return true;
			}
		}
		return false;
	}
}