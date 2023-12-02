package de.bergtiger.claim.cmd;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.*;
import de.bergtiger.claim.bdo.*;
import de.bergtiger.claim.data.IntersectionResult;
import de.bergtiger.claim.data.UnitePolygonsResult;
import de.bergtiger.claim.data.UnitePolygonsResultType;
import de.bergtiger.claim.data.language.Lang;
import de.bergtiger.claim.data.permission.Perm;
import de.bergtiger.claim.events.PreExpandCheckConfirmationEvent;
import de.bergtiger.claim.events.PreExpandConfirmationEvent;
import de.bergtiger.claim.listener.ConfirmationListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static de.bergtiger.claim.data.ClaimUtils.*;

public class CmdExpand {
    public static void expand(CommandSender cs, String[] args, boolean thisIsACheck, boolean priorityPermission) {
        if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN) || priorityPermission ||
                (thisIsACheck && Perm.hasPermission(cs, Perm.CLAIM_EXPANDCHECK)) ||
                (!thisIsACheck && Perm.hasPermission(cs, Perm.CLAIM_CHECK))
        ) {
            if (cs instanceof Player player) {
                // get RegionManager for world
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));
                ProtectedRegion oldRegion = null;
                ArrayList<String> directions = new ArrayList<>();
                directions.add("north");
                directions.add("east");
                directions.add("south");
                directions.add("west");
                BlockFace direction = null;
                boolean regionAngegeben = false;
                if (args.length >= 2) {
                    String regionName = args[1];
                    if (regions.hasRegion(args[1])) {
                        oldRegion = regions.getRegion(regionName);
                        regionAngegeben = true;
                        if (!(oldRegion.getOwners().contains(player.getUniqueId()) || Perm.hasPermission(cs, Perm.CLAIM_ADMIN))) {
                            // not the owner
                            player.spigot().sendMessage(Lang.build("Es existiert keine Region mit dem Namen " + regionName + " oder du hast keine Berechtigung für diese Region."));
                            return;
                        }
                    } else {
                        if (!directions.contains(args[1])) {
                            //Region existiert nicht + es wurde keine Richtung angegeben, die dem Befehl eine andere Bedeutung geben würde
                            player.spigot().sendMessage(Lang.build("Es existiert keine Region mit dem Namen " + regionName + " oder du hast keine Berechtigung für diese Region."));
                            return;
                        }
                    }
                }
                if (oldRegion == null) {
                    // player wants to expand region where he stands
                    ApplicableRegionSet set = regions.getApplicableRegions(BukkitAdapter.asBlockVector(player.getLocation()));
                    if (set.size() == 0) {
                        // not in a region
                        player.spigot().sendMessage(Lang.build("Du stehst in keiner Region."));
                        return;
                    } else if (set.size() > 1) {
                        // to many regions
                        player.spigot().sendMessage(Lang.build("Du stehst in mehreren Regionen. Bitte gib eine Region an."));
                        return;
                    } else {
                        // exact one region
                        oldRegion = set.getRegions().stream().findFirst().get();
                        if (!(oldRegion.getOwners().contains(player.getUniqueId()) || Perm.hasPermission(cs, Perm.CLAIM_ADMIN))) {
                            // not the owner
                            player.spigot().sendMessage(Lang.build(Lang.NOPERMISSION));
                            return;
                        }
                    }
                }

                int argumentenVerschiebung = 0;
                if (regionAngegeben) {
                    argumentenVerschiebung = 1;
                }
                boolean cuboidRegion = (oldRegion.getType() == RegionType.CUBOID);
                boolean worldEditPermission = Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_WORLDEDIT);
                if (!cuboidRegion && !worldEditPermission) {
                    player.spigot().sendMessage(Lang.build("Du kannst diese Polygon-Region nicht vergrößern, da in diesem Fall World-Edit-Markierungen benötigt werden, " +
                            "du aber keine Rechte dafür besitzt."));
                    return;
                }
                boolean isDirectionalExtension = false;
                Integer extendLength = null;
                if (args.length >= 2 + argumentenVerschiebung) {
                    if (directions.contains(args[1 + argumentenVerschiebung].toLowerCase())) {
                        if (cuboidRegion) {
                            if (args.length >= 3 + argumentenVerschiebung) {
                                try {
                                    extendLength = Integer.parseInt(args[2 + argumentenVerschiebung]);
                                } catch (NumberFormatException throwables) {
                                }
                                if (extendLength != null && extendLength > 0) {
                                    isDirectionalExtension = true;
                                    switch (args[1 + argumentenVerschiebung]) {
                                        case "north" : direction = BlockFace.NORTH; break;
                                        case "east" : direction = BlockFace.EAST; break;
                                        case "south" : direction = BlockFace.SOUTH; break;
                                        case "west" : direction = BlockFace.WEST; break;
                                    }
                                } else {
                                    player.spigot().sendMessage(Lang.build(args[2 + argumentenVerschiebung] + " ist keine gültige Zahl. Bitte gib eine positive ganze Zahl an, " +
                                            "um wie viele Blöcke du deine Region erweitern möchtest."));
                                    return;
                                }
                            } else {
                                player.spigot().sendMessage(Lang.build("Wenn du die Region, in eine Richtung erweitern willst, gib bitte noch an um wieviele Blöcke."));
                                return;
                            }
                        } else {
                            if (regionAngegeben) {
                                player.spigot().sendMessage(Lang.build("Die angegebene Region ist eine Polygon-Region " +
                                        "und lässt sich somit nicht in eine bestimmte Richtung erweitern, sondern nur durch eine markierte Fläche ergänzen."));
                            } else {
                                player.spigot().sendMessage(Lang.build("Die Region, in der du stehst, ist eine Polygon-Region " +
                                        "und lässt sich somit nicht in eine bestimmte Richtung erweitern, sondern nur durch eine markierte Fläche ergänzen."));
                            }
                            return;
                        }
                    } else {
                        if (!worldEditPermission) {
                            player.spigot().sendMessage(Lang.build("Du müsstest noch eine Richtung (north/east/south/west) und eine positive ganze Zahl angeben, " +
                                    "damit du deine Region entsprechend vergrößern kannst."));
                        }
                    }
                }
                if (isDirectionalExtension) {
                    //Erweiterung in bestimmte Richtung (geht nur bei Cuboid-Regionen):
                    double alteFläche = getArea(oldRegion);
                    double breite = 0;
                    if (direction == BlockFace.NORTH || direction == BlockFace.SOUTH) {
                        breite = 1 + oldRegion.getMaximumPoint().getX() - oldRegion.getMinimumPoint().getX();
                    } else if (direction == BlockFace.EAST || direction == BlockFace.WEST) {
                        breite = 1 + oldRegion.getMaximumPoint().getZ() - oldRegion.getMinimumPoint().getZ();
                    }
                    double neueFläche = alteFläche + breite * extendLength;
                    if (thisIsACheck) {
                        String message;
                        if (regionAngegeben) {
                            message = "Möchtest du überprüfen, ob die angegebene Region sich um " + extendLength + " Blöcke in Richtung " + direction.name() + " erweitern lässt?";
                        } else {
                            message = "Möchtest du überprüfen, ob die Region, in der du stehst, sich um " + extendLength + " Blöcke in Richtung " + direction.name() + " erweitern lässt?";
                        }
                        PreExpandCheckConfirmationEvent event = new PreExpandCheckConfirmationEvent(
                                player, true, regionAngegeben, alteFläche, neueFläche, direction, extendLength, message);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            ConfirmationListener.inst().addConfirmation(new ExpandDirectionalQueue(oldRegion, player.getWorld(), player, direction, extendLength, regionAngegeben, true));
                            if (regionAngegeben) {
                                player.spigot().sendMessage(Lang.build(event.getMessage()),
                                        Lang.build(Lang.EXPAND_YES, "/yes", null, null),
                                        Lang.build(Lang.EXPAND_NO, "/no", null, null));
                            } else {
                                player.spigot().sendMessage(Lang.build(event.getMessage()),
                                        Lang.build(Lang.EXPAND_YES, "/yes", null, null),
                                        Lang.build(Lang.EXPAND_NO, "/no", null, null));
                            }
                        }
                    } else {
                        expandQuestion(player, player.getWorld(), oldRegion, regionAngegeben, alteFläche, neueFläche, isDirectionalExtension,
                                null, null,
                                direction, extendLength, null);
                    }
                }
                else {
                    // Erweiterung um Markierung:
                    String noRegionMessage;
                    if (cuboidRegion) {
                        noRegionMessage = "Du müsstest noch eine Fläche markieren, um die du deine Region erweitern möchtest oder " +
                                "eine Richtung (north/east/south/west) und eine positive ganze Zahl angeben, damit du deine Region entsprechend vergrößern kannst.";
                    } else {
                        noRegionMessage = "Du müsstest noch eine Fläche markieren, um die du deine Region erweitern möchtest.";
                    }
                    try {
                        WorldEdit we = WorldEdit.getInstance();
                        BukkitPlayer bp = BukkitAdapter.adapt(player);
                        Region markierung = we.getSessionManager().get(bp).getSelection(bp.getWorld());
                        if (markierung != null) {
                            // Build new Region from old Region and World Edit Selection
                            List<BlockVector2> alteRegionsBlockPolygon;
                            if (oldRegion instanceof ProtectedCuboidRegion) {
                                alteRegionsBlockPolygon = polygonAusKuboidRegion((ProtectedCuboidRegion) oldRegion);
                            } else {
                                alteRegionsBlockPolygon = oldRegion.getPoints();
                            }

                            //Liegt Markierung in Region?:
                            if(region1LiegtInRegion2(markierung,oldRegion)) {
                                //Selbe Nachrichten weiter unten benötigt
                                if (regionAngegeben) {
                                    player.spigot().sendMessage(Lang.build("Deine Markierung liegt komplett innerhalb der angegebenen Region. " +
                                            "So kannst du die Region nicht erweitern."));
                                } else {
                                    player.spigot().sendMessage(Lang.build("Deine Markierung liegt komplett innerhalb der Region, in der du gerade stehst. " +
                                            "So kannst du die Region nicht erweitern."));
                                }
                                return;
                            }
                            List<BlockVector2> markierungsBlockPolygon;

                            if (markierung instanceof CuboidRegion) {
                                // Wenn Rechtecks-Markierung nur ein Block breit ist, dann wird sie in jede Richtung um einen Block breiter gemacht, wo sie auf das alteRegionsBlockPolygon trifft:
                                CuboidRegion rechteckMarkierung = (CuboidRegion) markierung.clone();
                                ArrayList<Vector2> alteRegionsPolygon = eckpunkteExaktAusEckpunkteGanz(alteRegionsBlockPolygon);
                                boolean markierungNichtMitRegionVerbunden = false;
                                System.out.println("markierung instanceof CuboidRegion");
                                if (rechteckMarkierung.getMaximumPoint().getX() == rechteckMarkierung.getMinimumPoint().getX()) {
                                    //schmal in x-Richtung:
                                    int x = rechteckMarkierung.getMaximumPoint().getX();
                                    boolean ostErweiterung = false;
                                    for (int z = rechteckMarkierung.getMinimumPoint().getBlockZ(); z <= rechteckMarkierung.getMaximumPoint().getBlockZ(); z++) {
                                        if (liegtPunktInPolygon(
                                                Vector2.at(x + 0.5 + 1.0, z + 0.5), alteRegionsPolygon, true
                                        )) {
                                            ostErweiterung = true;
                                        }
                                    }
                                    boolean westErweiterung = false;
                                    for (int z = rechteckMarkierung.getMinimumPoint().getBlockZ(); z <= rechteckMarkierung.getMaximumPoint().getBlockZ(); z++) {
                                        if (liegtPunktInPolygon(
                                                Vector2.at(x + 0.5 - 1.0, z + 0.5), alteRegionsPolygon, true
                                        )) {
                                            westErweiterung = true;
                                        }
                                    }
                                    int zMin = rechteckMarkierung.getMinimumPoint().getZ();
                                    int zMax = rechteckMarkierung.getMaximumPoint().getZ();
                                    if (ostErweiterung) {
                                        rechteckMarkierung.setPos1(BlockVector3.at(
                                                rechteckMarkierung.getMinimumPoint().getX(),
                                                rechteckMarkierung.getMinimumY(),
                                                zMin));
                                        rechteckMarkierung.setPos2(BlockVector3.at(
                                                x + 1,
                                                rechteckMarkierung.getMaximumY(),
                                                zMax));
                                    }
                                    if (westErweiterung) {
                                        rechteckMarkierung.setPos1(BlockVector3.at(
                                                x - 1,
                                                rechteckMarkierung.getMinimumY(),
                                                zMin));
                                        rechteckMarkierung.setPos2(BlockVector3.at(
                                                rechteckMarkierung.getMaximumPoint().getX(),
                                                rechteckMarkierung.getMaximumY(),
                                                zMax));
                                    }
                                    if (!ostErweiterung && !westErweiterung) {
                                        markierungNichtMitRegionVerbunden = true;
                                    }
                                    System.out.println("schmal in x-Richtung - ostErweiterung: " + ostErweiterung + "; westErweiterung: " + westErweiterung);
                                }
                                if (rechteckMarkierung.getMaximumPoint().getZ() == rechteckMarkierung.getMinimumPoint().getZ()) {
                                    //schmal in z-Richtung:
                                    int z = markierung.getMaximumPoint().getZ();
                                    boolean südErweiterung = false;
                                    for (int x = rechteckMarkierung.getMinimumPoint().getBlockX(); x <= rechteckMarkierung.getMaximumPoint().getBlockX(); x++) {
                                        if (liegtPunktInPolygon(
                                                Vector2.at(x + 0.5, z + 0.5 + 1.0), alteRegionsPolygon, true
                                        )) {
                                            südErweiterung = true;
                                        }
                                    }
                                    boolean nordErweiterung = false;
                                    for (int x = rechteckMarkierung.getMinimumPoint().getBlockX(); x <= rechteckMarkierung.getMaximumPoint().getBlockX(); x++) {
                                        if (liegtPunktInPolygon(
                                                Vector2.at(x + 0.5, z + 0.5 - 1.0), alteRegionsPolygon, true
                                        )) {
                                            nordErweiterung = true;
                                        }
                                    }
                                    int xMin = rechteckMarkierung.getMinimumPoint().getX();
                                    int xMax = rechteckMarkierung.getMaximumPoint().getX();
                                    if (südErweiterung) {
                                        rechteckMarkierung.setPos1(BlockVector3.at(
                                                xMin,
                                                rechteckMarkierung.getMinimumY(),
                                                rechteckMarkierung.getMinimumPoint().getZ()));
                                        rechteckMarkierung.setPos2(BlockVector3.at(
                                                xMax,
                                                rechteckMarkierung.getMaximumY(),
                                                z + 1));
                                        markierungNichtMitRegionVerbunden = false;
                                    }
                                    if (nordErweiterung) {
                                        rechteckMarkierung.setPos1(BlockVector3.at(
                                                xMin,
                                                rechteckMarkierung.getMinimumY(),
                                                z - 1));
                                        rechteckMarkierung.setPos2(BlockVector3.at(
                                                rechteckMarkierung.getMaximumPoint().getX(),
                                                rechteckMarkierung.getMaximumY(),
                                                xMax));
                                        markierungNichtMitRegionVerbunden = false;
                                    }
                                    System.out.println("schmal in z-Richtung - südErweiterung: " + südErweiterung + "; nordErweiterung: " + nordErweiterung);
                                }
                                markierungsBlockPolygon = polygonAusKuboidRegion(rechteckMarkierung);
                                if (markierungNichtMitRegionVerbunden) {
                                    if (regionAngegeben) { //Selbe Nachrichten weiter unten benötigt
                                        player.spigot().sendMessage(Lang.build("Die Fläche, die du markiert hast, überschneidet oder berührt die angegebene Region nicht."));
                                    } else {
                                        player.spigot().sendMessage(Lang.build("Die Fläche, die du markiert hast, überschneidet oder berührt die Region nicht, in der du gerade stehst."));
                                    }
                                    return;
                                }
                            }
                            else {
                                markierungsBlockPolygon = ((Polygonal2DRegion) markierung).getPoints();
                            }

                            List<BlockVector2> ergebnisPolygon = null;
                            if (oldRegion instanceof ProtectedCuboidRegion) {
                                // Wenn oldRegion nur ein Block breit ist, dann wird sie in jede Richtung um einen Block breiter gemacht, wo sie auf das Polygon markierungsBlockPolygon trifft:
                                CuboidRegion alteRechtecksRegion = new CuboidRegion(oldRegion.getMinimumPoint(), oldRegion.getMaximumPoint());
                                ArrayList<Vector2> alteRechtecksRegionEckpunkte = exakteEckpunkteEinerKuboidRegion(alteRechtecksRegion);
                                //Fehlermeldung:
                                if (polygon1LiegtInPolygon2(
                                        alteRechtecksRegionEckpunkte,
                                        eckpunkteExaktAusEckpunkteGanz(markierungsBlockPolygon)
                                )) {
                                    //Neue Region soll komplett der Markierung entsprechen
                                    ergebnisPolygon = markierungsBlockPolygon;
                                }
                                boolean markierungNichtMitRegionVerbunden = false;
                                System.out.println("oldRegion instanceof ProtectedCuboidRegion");
                                if (alteRechtecksRegion.getMaximumPoint().getX() == alteRechtecksRegion.getMinimumPoint().getX()) {
                                    //schmal in x-Richtung:
                                    int x = alteRechtecksRegion.getMaximumPoint().getX();
                                    boolean ostErweiterung = false;
                                    for (int z = alteRechtecksRegion.getMinimumPoint().getBlockZ(); z <= alteRechtecksRegion.getMaximumPoint().getBlockZ(); z++) {
                                        if (liegtPunktInPolygon(
                                                Vector2.at(x + 0.5 + 1.0, z + 0.5), eckpunkteExaktAusEckpunkteGanz(markierungsBlockPolygon), true
                                        )) {
                                            ostErweiterung = true;
                                        }
                                    }
                                    boolean westErweiterung = false;
                                    for (int z = alteRechtecksRegion.getMinimumPoint().getBlockZ(); z <= alteRechtecksRegion.getMaximumPoint().getBlockZ(); z++) {
                                        if (liegtPunktInPolygon(
                                                Vector2.at(x + 0.5 - 1.0, z + 0.5), eckpunkteExaktAusEckpunkteGanz(markierungsBlockPolygon), true
                                        )) {
                                            westErweiterung = true;
                                        }
                                    }
                                    int zMin = alteRechtecksRegion.getMinimumPoint().getZ();
                                    int zMax = alteRechtecksRegion.getMaximumPoint().getZ();
                                    if (ostErweiterung) {
                                        alteRechtecksRegion.setPos1(BlockVector3.at(
                                                alteRechtecksRegion.getMinimumPoint().getX(),
                                                alteRechtecksRegion.getMinimumY(),
                                                zMin));
                                        alteRechtecksRegion.setPos2(BlockVector3.at(
                                                x + 1,
                                                alteRechtecksRegion.getMaximumY(),
                                                zMax));
                                    }
                                    if (westErweiterung) {
                                        alteRechtecksRegion.setPos1(BlockVector3.at(
                                                x - 1,
                                                alteRechtecksRegion.getMinimumY(),
                                                zMin));
                                        alteRechtecksRegion.setPos2(BlockVector3.at(
                                                alteRechtecksRegion.getMaximumPoint().getX(),
                                                alteRechtecksRegion.getMaximumY(),
                                                zMax));
                                    }
                                    if (!ostErweiterung && !westErweiterung) {
                                        markierungNichtMitRegionVerbunden = true;
                                    }
                                    System.out.println("schmal in x-Richtung - ostErweiterung: " + ostErweiterung + "; westErweiterung: " + westErweiterung);
                                }
                                if (alteRechtecksRegion.getMaximumPoint().getZ() == alteRechtecksRegion.getMinimumPoint().getZ()) {
                                    //schmal in z-Richtung:
                                    int z = markierung.getMaximumPoint().getZ();
                                    boolean südErweiterung = false;
                                    for (int x = alteRechtecksRegion.getMinimumPoint().getBlockX(); x <= alteRechtecksRegion.getMaximumPoint().getBlockX(); x++) {
                                        if (liegtPunktInPolygon(
                                                Vector2.at(x + 0.5, z + 0.5 + 1.0), eckpunkteExaktAusEckpunkteGanz(markierungsBlockPolygon), true
                                        )) {
                                            südErweiterung = true;
                                        }
                                    }
                                    boolean nordErweiterung = false;
                                    for (int x = alteRechtecksRegion.getMinimumPoint().getBlockX(); x <= alteRechtecksRegion.getMaximumPoint().getBlockX(); x++) {
                                        if (liegtPunktInPolygon(
                                                Vector2.at(x + 0.5, z + 0.5 - 1.0), eckpunkteExaktAusEckpunkteGanz(markierungsBlockPolygon), true
                                        )) {
                                            nordErweiterung = true;
                                        }
                                    }
                                    int xMin = alteRechtecksRegion.getMinimumPoint().getX();
                                    int xMax = alteRechtecksRegion.getMaximumPoint().getX();
                                    if (südErweiterung) {
                                        alteRechtecksRegion.setPos1(BlockVector3.at(
                                                xMin,
                                                alteRechtecksRegion.getMinimumY(),
                                                alteRechtecksRegion.getMinimumPoint().getZ()));
                                        alteRechtecksRegion.setPos2(BlockVector3.at(
                                                xMax,
                                                alteRechtecksRegion.getMaximumY(),
                                                z + 1));
                                        markierungNichtMitRegionVerbunden = false;
                                    }
                                    if (nordErweiterung) {
                                        alteRechtecksRegion.setPos1(BlockVector3.at(
                                                xMin,
                                                alteRechtecksRegion.getMinimumY(),
                                                z - 1));
                                        alteRechtecksRegion.setPos2(BlockVector3.at(
                                                alteRechtecksRegion.getMaximumPoint().getX(),
                                                alteRechtecksRegion.getMaximumY(),
                                                xMax));
                                        markierungNichtMitRegionVerbunden = false;
                                    }
                                    System.out.println("schmal in z-Richtung - südErweiterung: " + südErweiterung + "; nordErweiterung: " + nordErweiterung);
                                }
                                alteRegionsBlockPolygon = polygonAusKuboidRegion(alteRechtecksRegion);
                                if (markierungNichtMitRegionVerbunden) {
                                    if (regionAngegeben) { //Selbe Nachrichten weiter unten benötigt
                                        player.spigot().sendMessage(Lang.build("Die Fläche, die du markiert hast, überschneidet oder berührt die angegebene Region nicht."));
                                    } else {
                                        player.spigot().sendMessage(Lang.build("Die Fläche, die du markiert hast, überschneidet oder berührt die Region nicht, in der du gerade stehst."));
                                    }
                                    return;
                                }
                            }

                            for (BlockVector2 alteRegionsEckpunkt : alteRegionsBlockPolygon) {
                                System.out.println("CmdExpand, Test1, alteRegionsEckpunkt: (" + alteRegionsEckpunkt.getX() + "," + alteRegionsEckpunkt.getZ() + ")");
                            }
                            Boolean containsGapsFromOldRegionAndSelection = false;
                            if (ergebnisPolygon == null) { //ergebnisPolygon ist != null, wenn die Region kubisch ist und komplett in Markierung liegt
                                UnitePolygonsResult result = uniteTwoPolygons(alteRegionsBlockPolygon, markierungsBlockPolygon);
                                containsGapsFromOldRegionAndSelection = result.getContainsGapsFromOldRegionAndSelection();
                                if (result.getPolygon() == null) {
                                    if (result.getResultType() == UnitePolygonsResultType.POLYGONS_DONT_INTERSECT_EACH_OTHER) {
                                        if (regionAngegeben) {
                                            player.spigot().sendMessage(Lang.build("Die Fläche, die du markiert hast, überschneidet oder berührt die angegebene Region nicht."));
                                        } else {
                                            player.spigot().sendMessage(Lang.build("Die Fläche, die du markiert hast, überschneidet oder berührt die Region nicht, in der du gerade stehst."));
                                        }
                                        return;
                                    } else if (result.getResultType() == UnitePolygonsResultType.OLD_REGION_INTERSECTS_ITSELF) {
                                        if (regionAngegeben) {
                                            player.spigot().sendMessage(Lang.build("Die angegebene Region überschneidet sich selbst, was eigentlich hier nicht vorkommen dürfte."));
                                        } else {
                                            player.spigot().sendMessage(Lang.build("Die Region, in der du gerade stehst, überschneidet sich selbst, was eigentlich hier nicht vorkommen dürfte."));
                                        }
                                        return;
                                    } else if (result.getResultType() == UnitePolygonsResultType.SELECTION_INTERSECTS_ITSELF) {
                                        player.spigot().sendMessage(Lang.build("Die Fläche, die du markiert hast, überschneidet sich selbst."));
                                        return;
                                    } else if (result.getResultType() == UnitePolygonsResultType.BOTH_POLYGONS_INTERSECT_THEMSELVES) {
                                        if (regionAngegeben) {
                                            player.spigot().sendMessage(Lang.build("Die angegebene Region überschneidet sich selbst, was eigentlich hier nicht vorkommen dürfte. " +
                                                    "Deine markierte Fläche übrigens auch, damit kannst du keine Region erweitern."));
                                        } else {
                                            player.spigot().sendMessage(Lang.build("Die Region, in der du gerade stehst, überschneidet sich selbst, was eigentlich hier nicht vorkommen dürfte.. " +
                                                    "Deine markierte Fläche übrigens auch, damit kannst du keine Region erweitern."));
                                        }
                                        return;
                                    } else if (result.getResultType() == UnitePolygonsResultType.SELECTION_IS_INSIDE_OLD_REGION) {
                                        if (regionAngegeben) {
                                            player.spigot().sendMessage(Lang.build("Deine Markierung liegt komplett innerhalb der angegebenen Region. " +
                                                    "So kannst du die Region nicht erweitern."));
                                        } else {
                                            player.spigot().sendMessage(Lang.build("Deine Markierung liegt komplett innerhalb der Region, in der du gerade stehst. " +
                                                    "So kannst du die Region nicht erweitern."));
                                        }
                                        return;
                                    } else if (result.getResultType() == UnitePolygonsResultType.RESULT_POLYGON_HAS_POINT_MULTIPLE) {
                                        if (regionAngegeben) {
                                            player.spigot().sendMessage(Lang.build("Die angegebene Region und deine Markierung berühren sich zu wenig, " +
                                                    "um eine zusammenhängende neue Region daraus zu bilden."));
                                        } else {
                                            player.spigot().sendMessage(Lang.build("Die Region, in der du stehst, und deine Markierung berühren sich zu wenig, " +
                                                    "um eine zusammenhängende neue Region daraus zu bilden."));
                                        }
                                        return;
                                    } else if (result.getResultType() == UnitePolygonsResultType.OLD_REGION_HAS_POINT_MULTIPLE) {
                                        if (regionAngegeben) {
                                            player.spigot().sendMessage(Lang.build("Die angegebene Region verwendet Eckpunkte mehrfach, was nicht vorkommen sollte."));
                                        } else {
                                            player.spigot().sendMessage(Lang.build("Die Region, in der du stehst, verwendet Eckpunkte mehrfach, was nicht vorkommen sollte."));
                                        }
                                        return;
                                    } else if (result.getResultType() == UnitePolygonsResultType.SELECTION_HAS_POINT_MULTIPLE) {
                                        player.spigot().sendMessage(Lang.build("Deine Markierung verwendet Eckpunkte mehrfach.")); //Selbe Nachricht weiter oben benötigt
                                        return;
                                    } else if (result.getResultType() == UnitePolygonsResultType.POLYGONS_ARE_EQUAL) {
                                        if (regionAngegeben) {
                                            player.spigot().sendMessage(Lang.build("Deine Markierung entspricht genau deiner angegebenen Region, also so keine Erweiterung möglich."));
                                        } else {
                                            player.spigot().sendMessage(Lang.build("Deine Markierung entspricht genau deiner Region, in der du stehst, also so keine Erweiterung möglich."));
                                        }
                                        return;
                                    }
                                    player.spigot().sendMessage(Lang.build("Ergebnis-Polygon existiert aus unbekanntem Grund nicht: " + result.getResultType().name()));
                                    return;
                                }
                                ergebnisPolygon = result.getPolygon();
                            }
                            Polygonal2DRegion newRegion = new Polygonal2DRegion(markierung.getWorld(), ergebnisPolygon, oldRegion.getMinimumPoint().getY(), oldRegion.getMaximumPoint().getY());
                            for (BlockVector2 vector2 : ergebnisPolygon) {
                                System.out.println(ChatColor.DARK_RED + "Test2 - Eckpunkte: " + vector2);
                            }
                            // inform Player
                            double alteFläche = getArea(oldRegion);
                            double neueFläche = flächeEinesPixelPolygons(scharfePolgonFläche(newRegion.getPoints()),newRegion.getPoints());
                            if (thisIsACheck) {
                                String message;
                                if (regionAngegeben) {
                                    message = "Möchtest du überprüfen ob die Erweiterung der angegebenen Region mit deiner Markierung verfügbar ist?";
                                } else {
                                    message = "Möchtest du überprüfen ob die Erweiterung der Region, in der du stehst, mit deiner Markierung verfügbar ist?";
                                }
                                PreExpandCheckConfirmationEvent event = new PreExpandCheckConfirmationEvent(
                                        player, false, regionAngegeben, getArea(oldRegion), getArea(newRegion),
                                        null, null, message);
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled()) {
                                    Polygonal2DRegion markierungAlsPolygon = markierungAlsPolygon(oldRegion, markierung);
                                    ConfirmationListener.inst().addConfirmation(new ExpandSelectionalQueue(
                                            oldRegion, player.getWorld(), player, ergebnisPolygon, regionAngegeben, true,
                                            markierungAlsPolygon, containsGapsFromOldRegionAndSelection));
                                    if (regionAngegeben) {
                                        player.spigot().sendMessage(Lang.build(event.getMessage()),
                                                Lang.build(Lang.EXPAND_YES, "/yes", null, null),
                                                Lang.build(Lang.EXPAND_NO, "/no", null, null));
                                    } else {
                                        player.spigot().sendMessage(Lang.build(event.getMessage()),
                                                Lang.build(Lang.EXPAND_YES, "/yes", null, null),
                                                Lang.build(Lang.EXPAND_NO, "/no", null, null));
                                    }
                                }
                            } else {
                                Polygonal2DRegion markierungAlsPolygon = markierungAlsPolygon(oldRegion, markierung);
                                CmdExpand.expandQuestion(player, player.getWorld(), oldRegion, regionAngegeben, alteFläche, neueFläche, false,
                                        markierungAlsPolygon, containsGapsFromOldRegionAndSelection,
                                        null, null, ergebnisPolygon);
                            }
                        } else {
                            // No Region
                            player.spigot().sendMessage(Lang.build(noRegionMessage));
                            return;
                        }
                    } catch (IncompleteRegionException e) {
                        // No Region
                        player.spigot().sendMessage(Lang.build(noRegionMessage));
                        return;
                    }
                }
            } else {
                // no player
                cs.spigot().sendMessage(Lang.build(Lang.NOPLAYER));
                return;
            }
        } else {
            cs.sendMessage(Lang.NOPERMISSION.get());
            return;
        }
    }

    public static void expandQuestion(Player player, World world, ProtectedRegion oldRegion, boolean regionStated, double alteFläche, double neueFläche, boolean isDirectionalExtension,
                                      Polygonal2DRegion selectionWithCorrectHeight, Boolean containsGapsFromOldRegionAndSelection,
                                      BlockFace direction, Integer extendLength, List<BlockVector2> eckpunkteDerNeuenRegion
    ) {
        String message;
        if (isDirectionalExtension) {
            if (regionStated) {
                message = "Möchtest du die angegebene Region (Fläche: " + alteFläche + "m^2) um " + extendLength + " Blöcke in Richtung " +
                        direction.name() + " erweitern? Die neue Region hätte eine Fläche von " + neueFläche + "m^2.";
            } else {
                message = "Möchtest du die Region, auf der du stehst (Fläche: " + alteFläche + "m^2), um " + extendLength + " Blöcke in Richtung " +
                        direction.name() + " erweitern? Die neue Region hätte eine Fläche von " + neueFläche + "m^2.";
            }
        } else {
            if (regionStated) {
                //message = "Möchtest du die angegebene Region um deine Markierung erweitern?";
                message = "Möchtest du die angegebene Region (Fläche: " + alteFläche + "m^2) um deine Markierung erweitern? " +
                        "Die neue Region hätte eine Fläche von " + neueFläche + "m^2.";
            } else {
                //message = "Möchtest du die Region, in der du stehst, um deine Markierung erweitern?";
                message = "Möchtest du die Region, auf der du stehst (Fläche: " + alteFläche + "m^2) um deine Markierung erweitern? " +
                        "Die neue Region hätte eine Fläche von " + neueFläche + "m^2.";
            }
        }
        PreExpandConfirmationEvent event = new PreExpandConfirmationEvent(
                player, isDirectionalExtension, regionStated, alteFläche, neueFläche, direction, extendLength, message);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            if (isDirectionalExtension) {
                ConfirmationListener.inst().addConfirmation(new ExpandDirectionalQueue(
                        oldRegion, world, player, direction, extendLength, regionStated, false));
            } else {
                ConfirmationListener.inst().addConfirmation(new ExpandSelectionalQueue(
                        oldRegion, world, player, eckpunkteDerNeuenRegion, regionStated, false, selectionWithCorrectHeight, containsGapsFromOldRegionAndSelection));
            }
            player.spigot().sendMessage(Lang.build(event.getMessage()),
                    Lang.build(Lang.EXPAND_YES, "/yes", null, null),
                    Lang.build(Lang.EXPAND_NO, "/no", null, null));
        }
    }

    public static CuboidRegion theDirectionalExpansion (BlockFace direction, ProtectedCuboidRegion oldRegion, int extendLength) {
        if (extendLength > 0) {
            switch (direction) {
                case NORTH:
                    return new CuboidRegion(
                            BlockVector3.at(oldRegion.getMinimumPoint().getX(), oldRegion.getMinimumPoint().getY(), oldRegion.getMinimumPoint().getZ() - extendLength),
                            BlockVector3.at(oldRegion.getMaximumPoint().getX(), oldRegion.getMaximumPoint().getY(), oldRegion.getMinimumPoint().getZ() - 1));
                case EAST:
                    return new CuboidRegion(
                            BlockVector3.at(oldRegion.getMaximumPoint().getX() + 1, oldRegion.getMinimumPoint().getY(), oldRegion.getMinimumPoint().getZ()),
                            BlockVector3.at(oldRegion.getMaximumPoint().getX() + extendLength, oldRegion.getMaximumPoint().getY(), oldRegion.getMaximumPoint().getZ()));
                case SOUTH:
                    return new CuboidRegion(
                            BlockVector3.at(oldRegion.getMinimumPoint().getX(), oldRegion.getMinimumPoint().getY(), oldRegion.getMaximumPoint().getZ() + 1),
                            BlockVector3.at(oldRegion.getMaximumPoint().getX(), oldRegion.getMaximumPoint().getY(), oldRegion.getMaximumPoint().getZ() + extendLength));
                case WEST:
                    return new CuboidRegion(
                            BlockVector3.at(oldRegion.getMinimumPoint().getX() - extendLength, oldRegion.getMinimumPoint().getY(), oldRegion.getMinimumPoint().getZ()),
                            BlockVector3.at(oldRegion.getMinimumPoint().getX() - 1, oldRegion.getMaximumPoint().getY(), oldRegion.getMaximumPoint().getZ()));
                default:
                    return null;
            }
        } else {
            switch (direction) {
                case NORTH:
                    return new CuboidRegion(
                            BlockVector3.at(oldRegion.getMinimumPoint().getX(), oldRegion.getMinimumPoint().getY(), oldRegion.getMinimumPoint().getZ() - (extendLength + 1)),
                            BlockVector3.at(oldRegion.getMaximumPoint().getX(), oldRegion.getMaximumPoint().getY(), oldRegion.getMinimumPoint().getZ()));
                case EAST:
                    return new CuboidRegion(
                            BlockVector3.at(oldRegion.getMaximumPoint().getX(), oldRegion.getMinimumPoint().getY(), oldRegion.getMinimumPoint().getZ()),
                            BlockVector3.at(oldRegion.getMaximumPoint().getX() + (extendLength + 1), oldRegion.getMaximumPoint().getY(), oldRegion.getMaximumPoint().getZ()));
                case SOUTH:
                    return new CuboidRegion(
                            BlockVector3.at(oldRegion.getMinimumPoint().getX(), oldRegion.getMinimumPoint().getY(), oldRegion.getMaximumPoint().getZ()),
                            BlockVector3.at(oldRegion.getMaximumPoint().getX(), oldRegion.getMaximumPoint().getY(), oldRegion.getMaximumPoint().getZ() + (extendLength + 1)));
                case WEST:
                    return new CuboidRegion(
                            BlockVector3.at(oldRegion.getMinimumPoint().getX() - (extendLength + 1), oldRegion.getMinimumPoint().getY(), oldRegion.getMinimumPoint().getZ()),
                            BlockVector3.at(oldRegion.getMinimumPoint().getX(), oldRegion.getMaximumPoint().getY(), oldRegion.getMaximumPoint().getZ()));
                default:
                    return null;
            }
        }
    }

    public static UnitePolygonsResult uniteTwoPolygons (List<BlockVector2> alteRegionsBlockPolygon, List<BlockVector2> markierungsBlockPolygon) {
        if (polygonHatEckpunkteMehrfach(alteRegionsBlockPolygon)) {
            return new UnitePolygonsResult(null,UnitePolygonsResultType.OLD_REGION_HAS_POINT_MULTIPLE, null);
        }
        if (polygonHatEckpunkteMehrfach(markierungsBlockPolygon)) {
            return new UnitePolygonsResult(null,UnitePolygonsResultType.SELECTION_HAS_POINT_MULTIPLE, null);
        }
        ArrayList<Vector2> alteRegionsPolygon = new ArrayList<>();
        for (BlockVector2 blockVector2 : alteRegionsBlockPolygon) {
            alteRegionsPolygon.add(Vector2.at( blockVector2.getX() + 0.5,blockVector2.getZ() + 0.5));
        }
        ArrayList<Vector2> markierungsPolygon = new ArrayList<>();
        for (BlockVector2 blockVector2 : markierungsBlockPolygon) {
            markierungsPolygon.add(Vector2.at( blockVector2.getX() + 0.5,blockVector2.getZ() + 0.5));
        }
        // Die Richtung ist entgegen des Uhrzeigersinns:
        Boolean region1ImUhrzeigerSinn = verläuftPolygonImUhrzeigersinn(alteRegionsPolygon);
        Boolean region2ImUhrzeigerSinn = verläuftPolygonImUhrzeigersinn(markierungsPolygon);
        if (region2ImUhrzeigerSinn == null && region1ImUhrzeigerSinn == null) {
            return new UnitePolygonsResult(null, UnitePolygonsResultType.BOTH_POLYGONS_INTERSECT_THEMSELVES, null);
        }
        if (region1ImUhrzeigerSinn == null) {
            return new UnitePolygonsResult(null, UnitePolygonsResultType.OLD_REGION_INTERSECTS_ITSELF, null);
        }
        if (region2ImUhrzeigerSinn == null) {
            return new UnitePolygonsResult(null, UnitePolygonsResultType.SELECTION_INTERSECTS_ITSELF, null);
        }
        if (region2ImUhrzeigerSinn) {
            markierungsPolygon = punktListeInvertiert(markierungsPolygon);
        }
        if (region1ImUhrzeigerSinn) {
            alteRegionsPolygon = punktListeInvertiert(alteRegionsPolygon);
        }
        if (sindPolygoneGleich(
                eckpunkteGanzAusEckpunkteExakt(markierungsPolygon), eckpunkteGanzAusEckpunkteExakt(alteRegionsPolygon))
        ) {
            return new UnitePolygonsResult(null, UnitePolygonsResultType.POLYGONS_ARE_EQUAL, null);
        }
        if (polygon1LiegtInPolygon2(alteRegionsPolygon, markierungsPolygon)) {
            // Markierung liegt komplett innerhalb Region
            List<BlockVector2> ergebnisPolygon = polygonOhneRedundantePunkte(eckpunkteGanzAusEckpunkteExakt(markierungsPolygon));
            return new UnitePolygonsResult(ergebnisPolygon, UnitePolygonsResultType.BOUNDING_POLYGON_FOUND, false);
        }
        ArrayList<Vector2> bereitsÜberprüfteStartpunkte = new ArrayList<>();
        //Für jeden geeigneten Startpunkt:
        ArrayList<ArrayList<Vector2>> potentielleVereintePolygone = new ArrayList<>();
        int i = 1;
        boolean markierungLiegtOffensichtlichKomplettInRegion = true;
        for (Vector2 startpunkt : markierungsPolygon) {
            //... der nicht im zweiten Polygon liegt...
            if (!liegtPunktInPolygon(startpunkt, alteRegionsPolygon, true)) {
                markierungLiegtOffensichtlichKomplettInRegion = false;
                //... und nicht bereits überprüft wurde (zu einem potentiellen neuem Polygon gehört)...
                if (!bereitsÜberprüfteStartpunkte.contains(startpunkt)) {
                    //... wird ein potentielles neues Polygon gefunden
                    System.out.println("Potentielles Polygon " + i + "; startpunkt: (" + startpunkt.getX() + "," + startpunkt.getZ() + ")"); i++;
                    ArrayList<Vector2> potentiellVereintesPolygon = potentiellesVereintesPolygonBildung(
                            startpunkt, markierungsPolygon, alteRegionsPolygon, //Bleibt unverändert
                            startpunkt, true, new ArrayList<>()); //Wird in Rekursion verändert
                    potentielleVereintePolygone.add(potentiellVereintesPolygon);
                    bereitsÜberprüfteStartpunkte.addAll(potentiellVereintesPolygon);
                }
            }
        }
        if (markierungLiegtOffensichtlichKomplettInRegion) {
            return new UnitePolygonsResult(null, UnitePolygonsResultType.SELECTION_IS_INSIDE_OLD_REGION, null);
        }
        //Für alle potentiellen neuen Polygone ist noch nicht klar, ob sie das umgebende Polygon sind oder ein Polygon, welches von den zwei Ausgangspolygonen eingeschlossen wird
        //Deswegen ist das neue Polygon nun das mit der größten Fläche:
        ArrayList<Vector2> neuesPolygon = null;
        double größtePolygonfläche = 0.0;
        double lückenFläche = 0.0;
        for (ArrayList<Vector2> potentiellesNeuesPolygon : potentielleVereintePolygone) {
            double polygonfläche = scharfePolgonFläche(potentiellesNeuesPolygon);
            lückenFläche = lückenFläche + polygonfläche;
            if (polygonfläche > größtePolygonfläche) {
                größtePolygonfläche = polygonfläche;
                neuesPolygon = potentiellesNeuesPolygon;
            }
        }
        lückenFläche = lückenFläche - größtePolygonfläche;
        //Wenn die Flächen vom alten und neuen Polygon gleich sind, überschneiden sich die Polygone nicht:
        double scharfePolgonFläche = scharfePolgonFläche(alteRegionsPolygon);
        if (scharfePolgonFläche == größtePolygonfläche) {
            if (polygon1LiegtInPolygon2(markierungsPolygon,alteRegionsPolygon)) {
                // Markierung liegt komplett innerhalb Region
                return new UnitePolygonsResult(null, UnitePolygonsResultType.SELECTION_IS_INSIDE_OLD_REGION, null);
            } else {
                // Markierung und Region überschneiden sich nicht
                return new UnitePolygonsResult(null, UnitePolygonsResultType.POLYGONS_DONT_INTERSECT_EACH_OTHER, null);
            }
        }
        if (neuesPolygon != null) {
            for (BlockVector2 vector2 : eckpunkteGanzAusEckpunkteExakt(neuesPolygon)) {
                System.out.println(ChatColor.RED + "Test1 - Eckpunkte: " + vector2);
            }
        } else {
            System.out.println(ChatColor.RED + "Test1 - Eckpunkte: null");
        }
        List<BlockVector2> neuesBlockPolygon = polygonOhneRedundantePunkte(eckpunkteGanzAusEckpunkteExakt(neuesPolygon));
        if (polygonHatEckpunkteMehrfach(neuesBlockPolygon)) {
            return new UnitePolygonsResult(null, UnitePolygonsResultType.RESULT_POLYGON_HAS_POINT_MULTIPLE, null);
        }
        return new UnitePolygonsResult(neuesBlockPolygon, UnitePolygonsResultType.BOUNDING_POLYGON_FOUND, lückenFläche > 0.0);
    }

    private static ArrayList<Vector2> potentiellesVereintesPolygonBildung(
            //Bleibt unverändert:
            Vector2 startpunkt, ArrayList<Vector2> polygon1punkte, ArrayList<Vector2> polygon2punkte,
            //Wird in Rekursion verändert:
            Vector2 aktuellerPunkt, boolean aktuellerPunktIstVonP1, ArrayList<Vector2> potentiellNeuesPolygon
    ) {
        if (potentiellNeuesPolygon.size() == 6) {
            double a = 1;
        }
        if (potentiellNeuesPolygon.contains(startpunkt)) {
            //Sobald man wieder beim Start angekommen ist, ist das potentielle vereinigte Polygon komplett und wird zurück gegeben
            return potentiellNeuesPolygon;
        } else {
            //Zuerst nächsten Eckpunkt bestimmen und dann ggf. den nächsten Schnittpunkt auf der Strecke zwischen dem alten und neuen Eckpunkt
            IntersectionResult result;

            Vector2 nächsterEckpunkt;
            boolean aktuellerPunktLiegtAufBeidenPolygonen = liegtPunktAufPolygonKante(polygon1punkte, aktuellerPunkt) &&
                    liegtPunktAufPolygonKante(polygon2punkte, aktuellerPunkt);
            boolean nunPolygon1Verwenden;
            if (aktuellerPunktLiegtAufBeidenPolygonen) {
                Vector2 vorherigerEckpunkt = vorherigerEckpunkt(aktuellerPunkt, potentiellNeuesPolygon);
                //aktueller Punkt liegt Auf beiden Polygonen
                //Welcher nächster Eckpunkt liegt weiter außen?:
                Vector2 nächsterEckpunktBeiPolygon1 = nächsterEckpunkt(aktuellerPunkt, polygon1punkte);
                Vector2 nächsterEckpunktBeiPolygon2 = nächsterEckpunkt(aktuellerPunkt, polygon2punkte);
                Boolean polygon1EckpunktLiegtWeiterAußenBzwRechts =
                        liegtStreckeAWeiterRechtsAlsStreckeB(vorherigerEckpunkt, aktuellerPunkt, nächsterEckpunktBeiPolygon1, nächsterEckpunktBeiPolygon2);
                if (polygon1EckpunktLiegtWeiterAußenBzwRechts == null) {
                    nunPolygon1Verwenden = aktuellerPunktIstVonP1;
                } else {
                    nunPolygon1Verwenden = polygon1EckpunktLiegtWeiterAußenBzwRechts;
                }
            } else {
                nunPolygon1Verwenden = aktuellerPunktIstVonP1;
            }
            if (nunPolygon1Verwenden) {
                //Polygon A = Polygon 1, Polygon B = Polygon 2
                nächsterEckpunkt = nächsterEckpunkt(aktuellerPunkt, polygon1punkte);
                result = ersterSchnittpunktMitPolygonAufStrecke(aktuellerPunkt, nächsterEckpunkt, polygon2punkte, false);
            } else {
                //Polygon A = Polygon 2, Polygon B = Polygon 1
                nächsterEckpunkt = nächsterEckpunkt(aktuellerPunkt, polygon2punkte);
                result = ersterSchnittpunktMitPolygonAufStrecke(aktuellerPunkt, nächsterEckpunkt, polygon1punkte, false);
            }

            //Wenn im letzten Schritt das Polygon bei einer übereinanderliegenden Kante gewechselt wurde
            Vector2 nächsterSchnittpunkt = null;
            if (result != null) {
                nächsterSchnittpunkt = result.getSchnittpunkt();
            }
            if (result != null && !nächsterSchnittpunkt.equals(aktuellerPunkt)) {
                //Polygon A schneidet sich zwischen aktuellerPunkt und nächsterEckpunkt mit Polygon B:
                // der erste Schnittpunkt wird als Eckpunkt des neuen Polygons hinzugefügt, das Polygon wird zu Polygon B gewechselt
                // und der nächste Punkt auf Polygon B wird zum neuen Polygon hinzugefügt
                // danach wird rekursiv die nächste Kante in Polygon B betrachtet
                Vector2 ersterSchnittpunkt = result.getSchnittpunkt();
                Vector2 nächsteEckeVonSchnittpunktKante = result.getEcke2();
                System.out.println(ChatColor.RED + "potentiellNeuesPolygon: Schnittpunkt: (" + ersterSchnittpunkt.getX() + "," + ersterSchnittpunkt.getZ() + ")");
                potentiellNeuesPolygon.add(ersterSchnittpunkt);
                //Wenn letzter Wechsel des Polygons auf gemeinsamer Kante stattfand, soll nicht direkt wieder gewechselt werden
                return potentiellesVereintesPolygonBildung(startpunkt, polygon1punkte, polygon2punkte,
                        ersterSchnittpunkt, !aktuellerPunktIstVonP1, potentiellNeuesPolygon);
                //eventuell muss aktuellerPunktIstAlterEckpunkt hier verallgemeinert werden
            } else {
                //Polygon A schneidet sich zwischen aktuellerPunkt und nächsterEckpunkt nicht mit Polygon B:
                // und der nächste Punkt auf Polygon A wird zum neuen Polygon hinzugefügt
                // danach wird rekursiv die nächste Kante in Polygon A betrachtet (Polygon wird also nicht gewechselt)
                potentiellNeuesPolygon.add(nächsterEckpunkt);
                System.out.println(ChatColor.RED + "potentiellNeuesPolygon: nächsterEckpunkt: (" + nächsterEckpunkt.getX() + "," + nächsterEckpunkt.getZ() + ")");
                return potentiellesVereintesPolygonBildung(startpunkt, polygon1punkte, polygon2punkte,
                        nächsterEckpunkt, aktuellerPunktIstVonP1,  potentiellNeuesPolygon);
            }
        }
    }

}
