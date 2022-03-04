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
import de.bergtiger.claim.data.ClaimUtils;
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
                ProtectedRegion oldRegion;
                ArrayList<String> directions = new ArrayList<>();
                directions.add("north");
                directions.add("east");
                directions.add("south");
                directions.add("west");
                BlockFace direction = null;
                boolean regionAngegeben = false;
                if (args.length >= 2) {
                    String regionName = args[1];
                    oldRegion = regions.getRegion(regionName);
                    if (regions.hasRegion(args[1])) {
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
                                        "und lässt sich somit nicht in eine bestimmte Richtung erweitern, sondern nur durch eine markierte Region ergänzen."));
                            } else {
                                player.spigot().sendMessage(Lang.build("Die Region, in der du stehst, ist eine Polygon-Region " +
                                        "und lässt sich somit nicht in eine bestimmte Richtung erweitern, sondern nur durch eine markierte Region ergänzen."));
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
                    double alteFläche = ClaimUtils.getArea(oldRegion);
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
                } else {
                    // Erweiterung um Markierung:
                    try {
                        WorldEdit we = WorldEdit.getInstance();
                        BukkitPlayer bp = BukkitAdapter.adapt(player);
                        Region markierung = we.getSessionManager().get(bp).getSelection(bp.getWorld());
                        if (markierung != null) {
                            // Build new Region from old Region and World Edit Selection
                            List<BlockVector2> alteRegionsBlockPolygon;
                            if (oldRegion instanceof ProtectedCuboidRegion) {
                                alteRegionsBlockPolygon = ClaimUtils.polygonAusKuboidRegion((ProtectedCuboidRegion) oldRegion);
                            } else {
                                alteRegionsBlockPolygon = oldRegion.getPoints();
                            }
                            List<BlockVector2> markierungsBlockPolygon;
                            if (markierung instanceof CuboidRegion) {
                                // Wenn Rechtecks-Markierung nur ein Block breit ist, dann wird sie in jede Richtung um einen Block breiter gemacht, wo sie auf das Polygon trifft:
                                CuboidRegion rechteckMarkierung = (CuboidRegion) markierung.clone();
                                boolean markierungNichtMitRegionVerbunden = false;
                                System.out.println("markierung instanceof CuboidRegion");
                                if (rechteckMarkierung.getMaximumPoint().getX() == rechteckMarkierung.getMinimumPoint().getX()) {
                                    //schmal in x-Richtung:
                                    int x = rechteckMarkierung.getMaximumPoint().getX();
                                    boolean ostErweiterung = false;
                                    for (int z = rechteckMarkierung.getMinimumPoint().getBlockZ(); z <= rechteckMarkierung.getMaximumPoint().getBlockZ(); z++) {
                                        if (ClaimUtils.liegtPunktInPolygon(
                                                Vector2.at(x + 0.5 + 1.0, z + 0.5), ClaimUtils.eckpunkteExaktAusEckpunkteGanz(alteRegionsBlockPolygon)
                                        )) {
                                            ostErweiterung = true;
                                        }
                                    }
                                    boolean westErweiterung = false;
                                    for (int z = rechteckMarkierung.getMinimumPoint().getBlockZ(); z <= rechteckMarkierung.getMaximumPoint().getBlockZ(); z++) {
                                        if (ClaimUtils.liegtPunktInPolygon(
                                                Vector2.at(x + 0.5 - 1.0, z + 0.5), ClaimUtils.eckpunkteExaktAusEckpunkteGanz(alteRegionsBlockPolygon)
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
                                        if (ClaimUtils.liegtPunktInPolygon(
                                                Vector2.at(x + 0.5, z + 0.5 + 1.0), ClaimUtils.eckpunkteExaktAusEckpunkteGanz(alteRegionsBlockPolygon)
                                        )) {
                                            südErweiterung = true;
                                        }
                                    }
                                    boolean nordErweiterung = false;
                                    for (int x = rechteckMarkierung.getMinimumPoint().getBlockX(); x <= rechteckMarkierung.getMaximumPoint().getBlockX(); x++) {
                                        if (ClaimUtils.liegtPunktInPolygon(
                                                Vector2.at(x + 0.5, z + 0.5 - 1.0), ClaimUtils.eckpunkteExaktAusEckpunkteGanz(alteRegionsBlockPolygon)
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
                                markierungsBlockPolygon = ClaimUtils.polygonAusKuboidRegion(rechteckMarkierung);
                                if (markierungNichtMitRegionVerbunden) {
                                    if (regionAngegeben) { //Selbe Nachrichten weiter unten benötigt
                                        player.spigot().sendMessage(Lang.build("Die Fläche, die du markiert hast, überschneidet oder berührt die angegebene Region nicht."));
                                    } else {
                                        player.spigot().sendMessage(Lang.build("Die Fläche, die du markiert hast, überschneidet oder berührt die Region nicht, in der du gerade stehst."));
                                    }
                                    return;
                                }
                            } else {
                                markierungsBlockPolygon = ((Polygonal2DRegion) markierung).getPoints();
                            }
                            for (BlockVector2 polygon1punkt : alteRegionsBlockPolygon) {
                                Bukkit.broadcastMessage("CmdExpand, Test1, polygon1punkt: (" + polygon1punkt.getX() + "," + polygon1punkt.getZ() + ")");
                            }
                            UnitePolygonsResult result = uniteTwoPolygons(alteRegionsBlockPolygon, markierungsBlockPolygon);
                            if (result.getPolygon() == null) {
                                if (result.getResultType() == UnitePolygonsResultType.POLYGON1_NOT_INTERSECTS_POLYGON2) {
                                    if (regionAngegeben) {
                                        player.spigot().sendMessage(Lang.build("Die Fläche, die du markiert hast, überschneidet oder berührt die angegebene Region nicht."));
                                    } else {
                                        player.spigot().sendMessage(Lang.build("Die Fläche, die du markiert hast, überschneidet oder berührt die Region nicht, in der du gerade stehst."));
                                    }
                                    return;
                                } else if (result.getResultType() == UnitePolygonsResultType.POLYGON1_INTERSECTS_ITSELF) {
                                    if (regionAngegeben) {
                                        player.spigot().sendMessage(Lang.build("Die angegebene Region überschneidet sich selbst, was eigentlich hier nicht vorkommen dürfte."));
                                    } else {
                                        player.spigot().sendMessage(Lang.build("Die Region, in der du gerade stehst, überschneidet sich selbst, was eigentlich hier nicht vorkommen dürfte."));
                                    }
                                    return;
                                } else if (result.getResultType() == UnitePolygonsResultType.POLYGON2_INTERSECTS_ITSELF) {
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
                                } else if (result.getResultType() == UnitePolygonsResultType.POLYGON2_IS_INSIDE_POLYGON1) {
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
                                                "um eine zusammenhängende neue Region daraus zu bilden"));
                                    } else {
                                        player.spigot().sendMessage(Lang.build("Die Region, in der du stehst, und deine Markierung berühren sich zu wenig, " +
                                                "um eine zusammenhängende neue Region daraus zu bilden"));
                                    }
                                    return;
                                } else if (result.getResultType() == UnitePolygonsResultType.POLYGON1_HAS_POINT_MULTIPLE) {
                                    if (regionAngegeben) {
                                        player.spigot().sendMessage(Lang.build("Die angegebene Region verwendet Eckpunkte mehrfach, was nicht vorkommen sollte."));
                                    } else {
                                        player.spigot().sendMessage(Lang.build("Die Region, in der du stehst, verwendet Eckpunkte mehrfach, was nicht vorkommen sollte."));
                                    }
                                    return;
                                } else if (result.getResultType() == UnitePolygonsResultType.POLYGON2_HAS_POINT_MULTIPLE) {
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
                            List<BlockVector2> ergebnisPolygon = result.getPolygon();
                            Polygonal2DRegion newRegion = new Polygonal2DRegion(markierung.getWorld(), ergebnisPolygon, oldRegion.getMinimumPoint().getY(), oldRegion.getMaximumPoint().getY());
                            for (BlockVector2 vector2 : ergebnisPolygon) {
                                Bukkit.broadcastMessage(ChatColor.DARK_RED + "Test2 - Eckpunkte: " + vector2);
                            }
                            // inform Player
                            double alteFläche = ClaimUtils.getArea(oldRegion);
                            double neueFläche = ClaimUtils.flächeEinesPixelPolygons(ClaimUtils.scharfePolgonFläche(newRegion.getPoints()),newRegion.getPoints());
                            if (thisIsACheck) {
                                String message;
                                if (regionAngegeben) {
                                    message = "Möchtest du überprüfen ob die Erweiterung der angegebenen Region mit deiner Markierung verfügbar ist?";
                                } else {
                                    message = "Möchtest du überprüfen ob die Erweiterung der Region, in der du stehst, mit deiner Markierung verfügbar ist?";
                                }
                                PreExpandCheckConfirmationEvent event = new PreExpandCheckConfirmationEvent(
                                        player, false, regionAngegeben, ClaimUtils.getArea(oldRegion), ClaimUtils.getArea(newRegion),
                                        null, null, message);
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled()) {
                                    Polygonal2DRegion markierungAlsPolygon = ClaimUtils.markierungAlsPolygon(oldRegion, markierung);
                                    ConfirmationListener.inst().addConfirmation(new ExpandSelectionalQueue(
                                            oldRegion, player.getWorld(), player, ergebnisPolygon, regionAngegeben, true,
                                            markierungAlsPolygon, result.getContainsGapsFromOldRegionAndSelection()));
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
                                Polygonal2DRegion markierungAlsPolygon = ClaimUtils.markierungAlsPolygon(oldRegion, markierung);
                                CmdExpand.expandQuestion(player, player.getWorld(), oldRegion, regionAngegeben, alteFläche, neueFläche, false,
                                        markierungAlsPolygon, result.getContainsGapsFromOldRegionAndSelection(),
                                        null, null, ergebnisPolygon);
                            }
                        } else {
                            // No Region
                            if (cuboidRegion) {
                                player.spigot().sendMessage(Lang.build("Du müsstest noch eine Fläche markieren, um die du deine Region erweitern möchtest oder " +
                                        "eine Richtung (north/east/south/west) und eine positive ganze Zahl angeben, damit du deine Region entsprechend vergrößern kannst."));
                            } else {
                                player.spigot().sendMessage(Lang.build("Du müsstest noch eine Fläche markieren, um die du deine Region erweitern möchtest."));
                            }
                            return;
                        }
                    } catch (IncompleteRegionException e) {
                        // No Region
                        if (cuboidRegion) {
                            player.spigot().sendMessage(Lang.build("Du müsstest noch eine Fläche markieren, um die du deine Region erweitern möchtest oder " +
                                    "eine Richtung (north/east/south/west) und eine positive ganze Zahl angeben, damit du deine Region entsprechend vergrößern kannst."));
                        } else {
                            player.spigot().sendMessage(Lang.build("Du müsstest noch eine Fläche markieren, um die du deine Region erweitern möchtest."));
                        }
                        return;
                    }


                    if (cuboidRegion) {

                    } else {

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
        switch (direction) {
            case NORTH: return new CuboidRegion(
                    BlockVector3.at(oldRegion.getMinimumPoint().getX(), oldRegion.getMinimumPoint().getY(), oldRegion.getMinimumPoint().getZ() - extendLength),
                    BlockVector3.at(oldRegion.getMaximumPoint().getX(), oldRegion.getMaximumPoint().getY(), oldRegion.getMinimumPoint().getZ() - 1));
            case EAST: return new CuboidRegion(
                    BlockVector3.at(oldRegion.getMaximumPoint().getX() + extendLength, oldRegion.getMinimumPoint().getY(), oldRegion.getMinimumPoint().getZ()),
                    BlockVector3.at(oldRegion.getMaximumPoint().getX() + 1, oldRegion.getMaximumPoint().getY(), oldRegion.getMaximumPoint().getZ()));
            case SOUTH: return new CuboidRegion(
                    BlockVector3.at(oldRegion.getMinimumPoint().getX() - extendLength, oldRegion.getMinimumPoint().getY(), oldRegion.getMaximumPoint().getZ() + extendLength),
                    BlockVector3.at(oldRegion.getMinimumPoint().getX() - 1, oldRegion.getMaximumPoint().getY(), oldRegion.getMaximumPoint().getZ() + 1));
            case WEST: return new CuboidRegion(
                    BlockVector3.at(oldRegion.getMinimumPoint().getX(), oldRegion.getMinimumPoint().getY(), oldRegion.getMinimumPoint().getZ()),
                    BlockVector3.at(oldRegion.getMaximumPoint().getX(), oldRegion.getMaximumPoint().getY(), oldRegion.getMaximumPoint().getZ()));
            default: return null;
        }
    }

    private static UnitePolygonsResult uniteTwoPolygons (List<BlockVector2> alteRegionsBlockPolygon, List<BlockVector2> markierungsBlockPolygon) {
        if (ClaimUtils.polygonHatEckpunkteMehrfach(alteRegionsBlockPolygon)) {
            return new UnitePolygonsResult(null,UnitePolygonsResultType.POLYGON1_HAS_POINT_MULTIPLE, null);
        }
        if (ClaimUtils.polygonHatEckpunkteMehrfach(markierungsBlockPolygon)) {
            return new UnitePolygonsResult(null,UnitePolygonsResultType.POLYGON2_HAS_POINT_MULTIPLE, null);
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
        Boolean region1ImUhrzeigerSinn = ClaimUtils.verläuftPolygonImUhrzeigersinn(alteRegionsPolygon);
        Boolean region2ImUhrzeigerSinn = ClaimUtils.verläuftPolygonImUhrzeigersinn(markierungsPolygon);
        if (region2ImUhrzeigerSinn == null && region1ImUhrzeigerSinn == null) {
            return new UnitePolygonsResult(null, UnitePolygonsResultType.BOTH_POLYGONS_INTERSECT_THEMSELVES, null);
        }
        if (region1ImUhrzeigerSinn == null) {
            return new UnitePolygonsResult(null, UnitePolygonsResultType.POLYGON1_INTERSECTS_ITSELF, null);
        }
        if (region2ImUhrzeigerSinn == null) {
            return new UnitePolygonsResult(null, UnitePolygonsResultType.POLYGON2_INTERSECTS_ITSELF, null);
        }
        if (region2ImUhrzeigerSinn) {
            markierungsPolygon = ClaimUtils.punktListeInvertiert(markierungsPolygon);
        }
        if (region1ImUhrzeigerSinn) {
            alteRegionsPolygon = ClaimUtils.punktListeInvertiert(alteRegionsPolygon);
        }
        if (ClaimUtils.sindPolygoneGleich(
                ClaimUtils.eckpunkteGanzAusEckpunkteExakt(markierungsPolygon), ClaimUtils.eckpunkteGanzAusEckpunkteExakt(alteRegionsPolygon))
        ) {
            return new UnitePolygonsResult(null, UnitePolygonsResultType.POLYGONS_ARE_EQUAL, null);
        }
        ArrayList<Vector2> bereitsÜberprüfteStartpunkte = new ArrayList<>();
        //Für jeden geeigneten Startpunkt:
        ArrayList<ArrayList<Vector2>> potentielleNeuePolygone = new ArrayList<>();
        int i = 1;
        for (Vector2 startpunkt : markierungsPolygon) {
            //... der nicht im zweiten Polygon liegt...
            if (!ClaimUtils.liegtPunktInPolygon(startpunkt, alteRegionsPolygon)) {
                //... und nicht bereits überprüft wurde (zu einem potentiellen neuem Polygon gehört)...
                if (!bereitsÜberprüfteStartpunkte.contains(startpunkt)) {
                    //... wird ein potentielles neues Polygon gefunden
                    Bukkit.broadcastMessage("Potentielles Polygon " + i + "; startpunkt: (" + startpunkt.getX() + "," + startpunkt.getZ() + ")"); i++;
                    ArrayList<Vector2> potentiellNeuesPolygon = potentiellNeuesPolygon(
                            startpunkt, markierungsPolygon, alteRegionsPolygon, //Bleibt unverändert
                            startpunkt, true, new ArrayList<>()); //Wird in Rekursion verändert
                    potentielleNeuePolygone.add(potentiellNeuesPolygon);
                    bereitsÜberprüfteStartpunkte.addAll(potentiellNeuesPolygon);
                }
            }
        }
        //Für alle potentiellen neuen Polygon ist noch nicht klar, ob sie das umgebende Polygon sind oder ein Polygon, welches von den zwei Ausgangspolygonen eingeschlossen wird
        //Deswegen ist das neue Polygon nun das mit der größten Fläche:
        ArrayList<Vector2> neuesPolygon = null;
        double größtePolygonfläche = 0.0;
        double lückenFläche = 0.0;
        for (ArrayList<Vector2> potentiellesNeuesPolygon : potentielleNeuePolygone) {
            double polygonfläche = ClaimUtils.scharfePolgonFläche(potentiellesNeuesPolygon);
            lückenFläche = lückenFläche + polygonfläche;
            if (polygonfläche > größtePolygonfläche) {
                größtePolygonfläche = polygonfläche;
                neuesPolygon = potentiellesNeuesPolygon;
            }
        }
        lückenFläche = lückenFläche - größtePolygonfläche;
        //Wenn die Flächen vom alten und neuen Polygon gleich sind, überschneiden sich die Polygone nicht:
        if (ClaimUtils.scharfePolgonFläche(alteRegionsPolygon) == größtePolygonfläche) {
            boolean polygon2LiegtInPolygon1 = true;
            for (Vector2 polygon2punkt : markierungsPolygon) {
                if (!ClaimUtils.liegtPunktInPolygon(polygon2punkt,alteRegionsPolygon)) {
                    polygon2LiegtInPolygon1 = false;
                }
            }
            if (polygon2LiegtInPolygon1) {
                // Markierung liegt komplett innerhalb Region
                return new UnitePolygonsResult(null, UnitePolygonsResultType.POLYGON2_IS_INSIDE_POLYGON1, null);
            } else {
                // Markierung und Region überschneiden sich nicht
                return new UnitePolygonsResult(null, UnitePolygonsResultType.POLYGON1_NOT_INTERSECTS_POLYGON2, null);
            }
        }
        if (neuesPolygon != null) {
            for (BlockVector2 vector2 : ClaimUtils.eckpunkteGanzAusEckpunkteExakt(neuesPolygon)) {
                Bukkit.broadcastMessage(ChatColor.RED + "Test1 - Eckpunkte: " + vector2);
            }
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + "Test1 - Eckpunkte: null");
        }
        List<BlockVector2> neuesBlockPolygon = ClaimUtils.polygonOhneRedundantePunkte(ClaimUtils.eckpunkteGanzAusEckpunkteExakt(neuesPolygon));
        if (ClaimUtils.polygonHatEckpunkteMehrfach(neuesBlockPolygon)) {
            return new UnitePolygonsResult(null, UnitePolygonsResultType.RESULT_POLYGON_HAS_POINT_MULTIPLE, null);
        }
        return new UnitePolygonsResult(neuesBlockPolygon, UnitePolygonsResultType.BOUNDING_POLYGON_FOUND, lückenFläche > 0.0);
    }

    private static ArrayList<Vector2> potentiellNeuesPolygon (
            //Bleibt unverändert:
            Vector2 startpunkt, ArrayList<Vector2> polygon1punkte, ArrayList<Vector2> polygon2punkte,
            //Wird in Rekursion verändert:
            Vector2 punktAufPolygonKante, boolean eckpunktIstVonP1, ArrayList<Vector2> potentiellNeuesPolygon
    ) {
        if (potentiellNeuesPolygon.contains(startpunkt)) {
            //Sobald man wieder beim Start angekommen ist, ist das neue Polygon komplett und wird zurück gegeben
            return potentiellNeuesPolygon;
        } else {
            IntersectionResult result;
            Vector2 nächsterEckpunkt;
            if (eckpunktIstVonP1) {
                //Polygon A = Polygon 1, Polygon B = Polygon 2
                nächsterEckpunkt = ClaimUtils.nächsterEckpunkt(punktAufPolygonKante, polygon1punkte);
                result = ClaimUtils.ersterSchnittpunktMitPolygonAufStrecke(punktAufPolygonKante, nächsterEckpunkt, polygon2punkte);
            } else {
                //Polygon A = Polygon 2, Polygon B = Polygon 1
                nächsterEckpunkt = ClaimUtils.nächsterEckpunkt(punktAufPolygonKante, polygon2punkte);
                result = ClaimUtils.ersterSchnittpunktMitPolygonAufStrecke(punktAufPolygonKante, nächsterEckpunkt, polygon1punkte);
            }
            if (result != null && !result.getSchnittpunkt().equals(punktAufPolygonKante)) {
                //Polygon A schneidet sich zwischen punktAufPolygonKante und nächsterEckpunkt mit Polygon B:
                // der erste Schnittpunkt wird als Eckpunkt des neuen Polygons hinzugefügt, das Polygon wird zu Polygon B gewechselt
                // und der nächste Punkt auf Polygon B wird zum neuen Polygon hinzugefügt
                // danach wird rekursiv die nächste Kante in Polygon B betrachtet
                Vector2 ersterSchnittpunkt = result.getSchnittpunkt();
                Vector2 nächsteEckeVonSchnittpunktKante = result.getEcke2();
                Bukkit.broadcastMessage(ChatColor.RED + "potentiellNeuesPolygon: Schnittpunkt: (" + ersterSchnittpunkt.getX() + "," + ersterSchnittpunkt.getZ() + ")");
                potentiellNeuesPolygon.add(ersterSchnittpunkt);
                return potentiellNeuesPolygon (startpunkt, polygon1punkte, polygon2punkte,
                        ersterSchnittpunkt, !eckpunktIstVonP1,  potentiellNeuesPolygon);
            } else {
                //Polygon A schneidet sich zwischen punktAufPolygonKante und nächsterEckpunkt nicht mit Polygon B:
                // und der nächste Punkt auf Polygon A wird zum neuen Polygon hinzugefügt
                // danach wird rekursiv die nächste Kante in Polygon A betrachtet (Polygon wird also nicht gewechselt)
                potentiellNeuesPolygon.add(nächsterEckpunkt);
                Bukkit.broadcastMessage(ChatColor.RED + "potentiellNeuesPolygon: nächsterEckpunkt: (" + nächsterEckpunkt.getX() + "," + nächsterEckpunkt.getZ() + ")");
                return potentiellNeuesPolygon (startpunkt, polygon1punkte, polygon2punkte,
                        nächsterEckpunkt, eckpunktIstVonP1,  potentiellNeuesPolygon);
            }
        }
    }
}
