package de.bergtiger.claim.cmd;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.bergtiger.claim.bdo.AdjustHeightsQueue;
import de.bergtiger.claim.data.language.Lang;
import de.bergtiger.claim.data.permission.Perm;
import de.bergtiger.claim.listener.ConfirmationListener;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdHeight {
    public static void adjustHeights(CommandSender cs, String[] args) {
        if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_HEIGHT)) {
            if (cs instanceof Player player) {
                ProtectedRegion region = null;
                // get RegionManager for world
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));
                boolean regionAngegeben = false;
                int argumentenVerschiebung = 0;
                if (args.length >= 3) {
                    String regionName = args[1];
                    if (regions.hasRegion(regionName)) {
                        region = regions.getRegion(regionName);
                        if (region.getOwners().contains(player.getUniqueId()) || Perm.hasPermission(cs, Perm.CLAIM_ADMIN)) {
                            if (args.length < 4) {
                                // Nicht genügend Argumente angegeben
                                player.spigot().sendMessage(Lang.build("Um die Minimal- und Maximalhöhe der angegebenen Region zu verändern, " +
                                        "gib bitte noch zuerst die Minimal- und danach die Maximalhöhe an."));
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
                        Integer newMinHeight = null;
                        try {
                            newMinHeight = Integer.parseInt(args[1]);
                        } catch (NumberFormatException throwables) {}
                        if (newMinHeight == null) {
                            // no region
                            player.spigot().sendMessage(Lang.build(Lang.NOSUCHREGION.replace(Lang.VALUE, regionName)));
                            return;
                        }
                    }
                } else {
                    // Nicht genügend Argumente angegeben
                    player.spigot().sendMessage(Lang.build("Um die Minimal- und Maximalhöhe einer Region zu verändern, " +
                            "gib bitte noch zuerst die Minimal- und danach die Maximalhöhe an."));
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
                Integer newMinHeight = null;
                try {
                    newMinHeight = Integer.parseInt(args[1 + argumentenVerschiebung]);
                } catch (NumberFormatException throwables) {}
                Integer newMaxHeight = null;
                try {
                    newMaxHeight = Integer.parseInt(args[2 + argumentenVerschiebung]);
                } catch (NumberFormatException throwables) {}
                if (newMinHeight != null && newMaxHeight != null) {
                    if (newMinHeight > newMaxHeight) {
                        player.spigot().sendMessage(Lang.build("Die angegebene Minimalhöhe (" + newMinHeight + ") ist größer als die angegebene Maximalhöhe (" + newMaxHeight + ")."));
                        return;
                    }
                    if (newMinHeight < player.getWorld().getMinHeight() && newMaxHeight > player.getWorld().getMaxHeight()) {
                        player.spigot().sendMessage(Lang.build("Die angegebene Minimalhöhe (" + newMinHeight + ") ist kleiner als die minimale Höhe der Welt (" +
                                player.getWorld().getMinHeight() + ") und die angegebene Maximalhöhe (" + newMaxHeight + ") ist größer als die maximale Höhe der Welt (" + player.getWorld().getMaxHeight() + ")."));
                        return;
                    }
                    if (newMinHeight < player.getWorld().getMinHeight()) {
                        player.spigot().sendMessage(Lang.build("Die angegebene Minimalhöhe (" + newMinHeight + ") ist kleiner als die minimale Höhe der Welt (" + player.getWorld().getMinHeight() + ")."));
                        return;
                    }
                    if (newMaxHeight > player.getWorld().getMaxHeight()) {
                        player.spigot().sendMessage(Lang.build("Die angegebene Maximalhöhe (" + newMaxHeight + ") ist größer als die maximale Höhe der Welt (" + player.getWorld().getMaxHeight() + ")."));
                        return;
                    }

                    ConfirmationListener.inst().addConfirmation(new AdjustHeightsQueue(region, player.getWorld(), player, newMinHeight, newMaxHeight));
                    // inform Player
                    if (regionAngegeben) {
                        player.spigot().sendMessage(Lang.build("Möchtest du von der angegebenen Region die minimale Höhe von " + region.getMinimumPoint().getY() +
                                        " auf " + newMinHeight + " und die maximale Höhe von " + region.getMaximumPoint().getY() + " auf " + newMaxHeight + " setzen?"),
                                Lang.build(Lang.HEIGHT_YES, "/yes", null, null),
                                Lang.build(Lang.HEIGHT_NO, "/no", null, null));
                    } else {
                        player.spigot().sendMessage(Lang.build("Möchtest du von der Region, in der du stehst, die minimale Höhe von " + region.getMinimumPoint().getY() +
                                        " auf " + newMinHeight + " und die maximale Höhe von " + region.getMaximumPoint().getY() + " auf " + newMaxHeight + " setzen?"),
                                Lang.build(Lang.HEIGHT_YES, "/yes", null, null),
                                Lang.build(Lang.HEIGHT_NO, "/no", null, null));
                    }
                } else {
                    if (newMinHeight == null && newMaxHeight == null) {
                        player.spigot().sendMessage(Lang.build(args[1 + argumentenVerschiebung] + " und " + args[2 + argumentenVerschiebung] + " sind keine gültigen Zahlen."));
                        return;
                    }
                    if (newMinHeight == null) {
                        player.spigot().sendMessage(Lang.build(args[1 + argumentenVerschiebung] + " ist keine gültig Zahl."));
                        return;
                    }
                    if (newMaxHeight == null) {
                        player.spigot().sendMessage(Lang.build(args[2 + argumentenVerschiebung] + " ist keine gültig Zahl."));
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
