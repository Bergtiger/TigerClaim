package de.bergtiger.claim.cmd;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.bergtiger.claim.data.configuration.Config;
import de.bergtiger.claim.data.language.Lang;
import de.bergtiger.claim.data.permission.Perm;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdPriority {
    public static void changePriority(CommandSender cs, String[] args) {
        if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_PRIORITY)) {
            if (cs instanceof Player player) {
                Integer maxPriority = Config.getInt(Config.REGION_MAX_PRIORITY);
                ProtectedRegion region;
                // get RegionManager for world
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));
                boolean regionAngegeben = false;
                int argumentenVerschiebung = 0;
                if (args.length >= 2) {
                    String regionName = args[1];
                    region = regions.getRegion(regionName);
                    if (regions.hasRegion(regionName)) {
                        if (region.getOwners().contains(player.getUniqueId()) || Perm.hasPermission(cs, Perm.CLAIM_ADMIN)) {
                            if (args.length < 3) {
                                // Nicht genügend Argumente angegeben
                                player.spigot().sendMessage(Lang.build("Um die Priorität der angegebenen Region zu verändern, " +
                                        "gib bitte noch eine Priorität zwischen 0 und " + maxPriority + " an."));
                                return;
                            }
                            region = regions.getRegion(regionName);
                            regionAngegeben = true;
                            argumentenVerschiebung = 1;
                        } else {
                            // no owner
                            player.spigot().sendMessage(Lang.build(Lang.NOPERMISSION));
                            return;
                        }
                    } else {
                        Integer priority = null;
                        try {
                            priority = Integer.parseInt(args[1]);
                        } catch (NumberFormatException throwables) {}
                        if (priority == null) {
                            // no region
                            player.spigot().sendMessage(Lang.build(Lang.NOSUCHREGION.replace(Lang.VALUE, regionName)));
                            return;
                        }
                    }
                } else {
                    // Nicht genügend Argumente angegeben
                    player.spigot().sendMessage(Lang.build("Um die Priorität der angegebenen Region zu verändern, " +
                            "gib bitte noch eine Priorität zwischen 0 und " + maxPriority + " an."));
                    return;
                }
                if (!regionAngegeben) {
                    ApplicableRegionSet set = regions.getApplicableRegions(BukkitAdapter.asBlockVector(player.getLocation()));
                    if (set.size() == 0) {
                        // not in a region
                        player.spigot().sendMessage(Lang.build(Lang.NOTINREGION));
                        return;
                    } else if (set.size() > 1) {
                        // to many regions
                        player.spigot().sendMessage(Lang.build(Lang.TOMANYREGIONS));
                        return;
                    } else {
                        // exact one region
                        region = set.getRegions().stream().findFirst().get();
                        if (!region.getOwners().contains(player.getUniqueId()) && !Perm.hasPermission(cs, Perm.CLAIM_ADMIN)) {
                            // no owner
                            player.spigot().sendMessage(Lang.build(Lang.NOPERMISSION));
                            return;
                        }
                    }
                }
                Integer priority = null;
                try {
                    priority = Integer.parseInt(args[1 + argumentenVerschiebung]);
                } catch (NumberFormatException throwables) {}
                if (priority != null) {
                    if (priority < 0) {
                        player.spigot().sendMessage(Lang.build("Prioritäten können nicht negativ sein."));
                        return;
                    }

                    int oldPriority = region.getPriority();
                    // set priority and inform Player
                    if (priority > maxPriority && !Perm.hasPermission(cs, Perm.CLAIM_ADMIN)) {
                        player.spigot().sendMessage(Lang.build("Die angegebene Priorität (" + priority + ") ist größer als die maximal erlaubte Priorität (" + maxPriority + ")."));
                        return;
                    } else {
                        if (priority > maxPriority) {
                            player.spigot().sendMessage(Lang.build("Priorität der Region " + region.getId() + " von " + oldPriority + " auf " + priority + " gesetzt " +
                                    "(überhalb der maximalen Priorität für Spielergrundstücke: " + maxPriority + ")."));
                        } else {
                            player.spigot().sendMessage(Lang.build("Priorität der Region " + region.getId() + " von " + oldPriority + " auf " + priority + " gesetzt."));
                        }
                    }
                    region.setPriority(priority);
                } else {
                    if (priority == null) {
                        player.spigot().sendMessage(Lang.build(args[1 + argumentenVerschiebung] + " ist keine gültig Zahl."));
                        return;
                    }
                }
            } else {
                // no player
                cs.spigot().sendMessage(Lang.build(Lang.NOPLAYER));
                return;
            }
        }
    }
}
