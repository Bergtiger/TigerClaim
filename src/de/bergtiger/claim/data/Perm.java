package de.bergtiger.claim.data;

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
	CLAIM_CLAIM		("tclaim.claim"),
	CLAIM_LIMIT		("tclaim.limit"),
	CLAIM_LIMITLESS	("tclaim.nolimit"),
	CLAIM_PLUGIN	("tbuttons.plugin"),
	CLAIM_RELOAD	("tbuttons.reload"),
	CLAIM_WORLDEDIT	("tbuttons.worldedit");
	
	private String value;
	
	private Perm(String value) {
		this.value = value;
	}
	
	public String get() {
		return value;
	}
	
	/**
	 * Check if cs has any of the following permissions
	 * @param cs
	 * @param permission
	 * @return
	 */
	public static boolean hasPermission(CommandSender cs, Perm ... permission) {
		if((cs != null) && (permission != null)) {
			for(Perm p : permission) {
				if(cs.hasPermission(p.get()))
					return true;
			}
		}
		return false;
	}
}