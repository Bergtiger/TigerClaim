package de.bergtiger.claim.cmd;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.bergtiger.claim.Claims;
import de.bergtiger.claim.bdo.DeleteQueue;
import de.bergtiger.claim.data.language.Lang;
import de.bergtiger.claim.data.permission.Perm;
import de.bergtiger.claim.listener.ConfirmationListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdDelete {

    public static void delete(CommandSender cs, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(Claims.inst(), () -> new CmdDelete().deleteAsynchron(cs, args));
    }

    /**
     * Set a value in Configuration. claim set type value.
     * /claim (0)delete (1)region_name
     * @param cs command sender
     * @param args arguments
     */
    private void deleteAsynchron(CommandSender cs, String[] args) {
        if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_DELETE)) {
            if (cs instanceof Player p) {
                // get RegionManager for world
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionManager regions = container.get(BukkitAdapter.adapt(p.getWorld()));
                if (args.length >= 2) {
                    String regionName = args[1];
                    if (regions.hasRegion(regionName)) {
                        ProtectedRegion region = regions.getRegion(regionName);
                        if (region.getOwners().contains(p.getUniqueId())) {
                            //call RegionDeleteEvent
                            ConfirmationListener.inst().addConfirmation(new DeleteQueue(region, p));
                            // inform Player
                            p.spigot().sendMessage(Lang.build("&eWillst du die angegebene Region (" + regionName + ") löschen? ", null, null, null),
                                    Lang.build(Lang.INSERT_YES, "/yes", Lang.build(Lang.INSERT_HOVER_YES), null),
                                    Lang.build(Lang.INSERT_NO, "/no", Lang.build(Lang.INSERT_HOVER_NO), null));
                        } else {
                            //Du darfst diese Region nicht löschen
                            p.sendMessage("Du darfst diese Region nicht löschen");
                        }
                    } else {
                        //Es gibt keine Region
                        p.sendMessage("Es gibt keine Region");
                    }
                } else {
                    //Das ist der Fall, wenn der Spieler in einer Region steht
                    ApplicableRegionSet set = regions.getApplicableRegions(BukkitAdapter.asBlockVector(p.getLocation()));
                    if (set.size() == 0) {
                        //Du stehst in keiner Region
                        p.sendMessage("Du stehst in keiner Region");
                    } else if (set.size() > 1) {
                        //Du stehst in mehreren Regionen, bitte Wähle eine Region aus
                        p.sendMessage("Du stehst in mehreren Regionen, bitte Wähle eine Region aus");
                    } else {
                        //Spieler steht in genau einer Region:
                        ProtectedRegion region = set.getRegions().stream().findFirst().get();
                        if (region.getOwners().contains(p.getUniqueId())) {
                            //call RegionDeleteEvent
                            ConfirmationListener.inst().addConfirmation(new DeleteQueue(region, p));
                            // inform Player
                            p.spigot().sendMessage(Lang.build("&eWillst du die Region, in der du stehst (" + region.getId() + ") , löschen? ", null, null, null),
                                    Lang.build(Lang.INSERT_YES, "/yes", Lang.build(Lang.INSERT_HOVER_YES), null),
                                    Lang.build(Lang.INSERT_NO, "/no", Lang.build(Lang.INSERT_HOVER_NO), null));
                            p.sendMessage("Region " + region.getId() + " erfolgreich gelöscht.");
                        } else {
                            //Du darfst diese Region nicht löschen
                            p.sendMessage("Du darfst diese Region nicht löschen");
                        }
                    }
                }
            }
        } else {
            cs.spigot().sendMessage(Lang.build(Lang.NOPERMISSION));
        }
    }
}
