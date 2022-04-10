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
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionType;
import de.bergtiger.claim.bdo.RetractDirectionalQueue;
import de.bergtiger.claim.bdo.RetractSelectionalQueue;
import de.bergtiger.claim.data.*;
import de.bergtiger.claim.data.language.Lang;
import de.bergtiger.claim.data.permission.Perm;
import de.bergtiger.claim.events.PreRetractConfirmationEvent;
import de.bergtiger.claim.listener.ConfirmationListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdRetract {
    public static void retract(CommandSender cs, String[] args) {
        if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_RETRACT)) {
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
                    player.spigot().sendMessage(Lang.build("Du kannst diese Polygon-Region nicht verkleinern, da in diesem Fall World-Edit-Markierungen benötigt werden, " +
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
                                switch (args[1 + argumentenVerschiebung]) {
                                    case "north" : direction = BlockFace.NORTH; break;
                                    case "east" : direction = BlockFace.EAST; break;
                                    case "south" : direction = BlockFace.SOUTH; break;
                                    case "west" : direction = BlockFace.WEST; break;
                                }
                                if (extendLength != null && extendLength > 0) {
                                    isDirectionalExtension = true;
                                } else {
                                    player.spigot().sendMessage(Lang.build(args[2 + argumentenVerschiebung] + " ist keine gültige Zahl. Bitte gib eine positive ganze Zahl an, " +
                                            "um wie viele Blöcke du deine Region auf Seite " + direction + " verkleinern möchtest."));
                                    return;
                                }
                            } else {
                                player.spigot().sendMessage(Lang.build("Wenn du die Region, auf einer Seite verkürzen willst, gib bitte noch an um wieviele Blöcke."));
                                return;
                            }
                        } else {
                            if (regionAngegeben) {
                                player.spigot().sendMessage(Lang.build("Die angegebene Region ist eine Polygon-Region " +
                                        "und lässt sich somit nicht auf einer bestimmten Seite verkleinern, sondern nur durch eine markierte Fläche beschneiden."));
                            } else {
                                player.spigot().sendMessage(Lang.build("Die Region, in der du stehst, ist eine Polygon-Region " +
                                        "und lässt sich somit nicht auf einer bestimmten Seite verkleinern, sondern nur durch eine markierte Fläche beschneiden."));
                            }
                            return;
                        }
                    } else {
                        if (!worldEditPermission) {
                            player.spigot().sendMessage(Lang.build("Du müsstest noch eine Richtung (north/east/south/west) und eine positive ganze Zahl angeben, " +
                                    "damit du deine Region entsprechend verkleinern kannst."));
                        }
                    }
                }
                String noRegionMessage;
                if (cuboidRegion) {
                    noRegionMessage = "Du müsstest noch eine Fläche markieren, um die du deine Region beschneiden möchtest oder " +
                            "eine Richtung (north/east/south/west) und eine positive ganze Zahl angeben, damit du deine Region entsprechend verkleinern kannst.";
                } else {
                    noRegionMessage = "Du müsstest noch eine Fläche markieren, um die du deine Region beschneiden möchtest.";
                }
                if (isDirectionalExtension) {
                    //Verkleinerung auf bestimmter Seite(geht nur bei Cuboid-Regionen):
                    double alteFläche = ClaimUtils.getArea(oldRegion);
                    double breite = 0;
                    if (direction == BlockFace.NORTH || direction == BlockFace.SOUTH) {
                        breite = 1 + oldRegion.getMaximumPoint().getX() - oldRegion.getMinimumPoint().getX();
                    } else if (direction == BlockFace.EAST || direction == BlockFace.WEST) {
                        breite = 1 + oldRegion.getMaximumPoint().getZ() - oldRegion.getMinimumPoint().getZ();
                    }
                    double alteLänge = 0;
                    if (breite != 0) {
                        alteLänge = alteFläche / breite;
                    }
                    if (alteLänge <= extendLength) {
                        if (regionAngegeben) {
                            player.spigot().sendMessage(Lang.build("Die angegebene Region ist nur " + alteLänge + " Blöcke lang und lässt sich somit nicht um " +
                            extendLength + " Blöcke verkürzen. Um deine Region zu löschen, verwende /claim delete"));
                        } else {
                            player.spigot().sendMessage(Lang.build("Die Region, in der du stehst, ist nur " + alteLänge + " Blöcke lang und lässt sich somit nicht um " +
                                    extendLength + " Blöcke verkürzen. Um deine Region zu löschen, verwende /claim delete"));
                        }
                        return;
                    }
                    double neueFläche = alteFläche - breite * extendLength;
                    retractQuestion(player, player.getWorld(), oldRegion, regionAngegeben, alteFläche, neueFläche, true,
                            null,
                            direction, extendLength, null);
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
                                // Eine Rechtecks-Markierung wird in jede Richtung um einen Block breiter gemacht:
                                CuboidRegion rechteckMarkierung = (CuboidRegion) markierung.clone();
                                System.out.println("CmdRetract, Test0 markierungsEckpunktMin: (" + markierung.getMinimumPoint().getX() + "," + markierung.getMinimumPoint().getZ() + ")");
                                System.out.println("CmdRetract, Test1 markierungsEckpunktMax: (" + markierung.getMaximumPoint().getX() + "," + markierung.getMaximumPoint().getZ() + ")");
                                rechteckMarkierung.setPos1(BlockVector3.at(
                                        markierung.getMinimumPoint().getX() - 1,
                                        markierung.getMinimumPoint().getY(),
                                        markierung.getMinimumPoint().getZ() - 1));
                                rechteckMarkierung.setPos2(BlockVector3.at(
                                        markierung.getMaximumPoint().getX() + 1,
                                        markierung.getMaximumPoint().getY(),
                                        markierung.getMaximumPoint().getZ() + 1));
                                markierungsBlockPolygon = ClaimUtils.polygonAusKuboidRegion(rechteckMarkierung);
                                System.out.println("CmdRetract, Test2 rechteckMarkierungEckpunktMin: (" + rechteckMarkierung.getMinimumPoint().getX() + "," + rechteckMarkierung.getMinimumPoint().getZ() + ")");
                                System.out.println("CmdRetract, Test3 rechteckMarkierungEckpunktMax: (" + rechteckMarkierung.getMaximumPoint().getX() + "," + rechteckMarkierung.getMaximumPoint().getZ() + ")");

                            } else {
                                markierungsBlockPolygon = ((Polygonal2DRegion) markierung).getPoints();
                            }
                            for (BlockVector2 markierungsEckpunkt : markierungsBlockPolygon) {
                                System.out.println("CmdRetract, Test4, markierungsEckpunkt: (" + markierungsEckpunkt.getX() + "," + markierungsEckpunkt.getZ() + ")");
                            }
                            for (BlockVector2 regionsEckpunkt : alteRegionsBlockPolygon) {
                                System.out.println("CmdRetract, Test5, regionsEckpunkt: (" + regionsEckpunkt.getX() + "," + regionsEckpunkt.getZ() + ")");
                            }
                            PolygonSubtractionResult result = subtractPolygons(alteRegionsBlockPolygon, markierungsBlockPolygon);
                            if (result.getPolygon() == null) {
                                if (result.getResultType() == PolygonSubtractionResultType.POLYGONS_DONT_INTERSECT_EACH_OTHER) {
                                    if (regionAngegeben) {
                                        player.spigot().sendMessage(Lang.build("Die Fläche, die du markiert hast, überschneidet oder berührt die angegebene Region nicht."));
                                    } else {
                                        player.spigot().sendMessage(Lang.build("Die Fläche, die du markiert hast, überschneidet oder berührt die Region nicht, in der du gerade stehst."));
                                    }
                                    return;
                                } else if (result.getResultType() == PolygonSubtractionResultType.OLD_REGION_INTERSECTS_ITSELF) {
                                    if (regionAngegeben) {
                                        player.spigot().sendMessage(Lang.build("Die angegebene Region überschneidet sich selbst, was eigentlich hier nicht vorkommen dürfte."));
                                    } else {
                                        player.spigot().sendMessage(Lang.build("Die Region, in der du gerade stehst, überschneidet sich selbst, was eigentlich hier nicht vorkommen dürfte."));
                                    }
                                    return;
                                } else if (result.getResultType() == PolygonSubtractionResultType.SELECTION_INTERSECTS_ITSELF) {
                                    player.spigot().sendMessage(Lang.build("Die Fläche, die du markiert hast, überschneidet sich selbst."));
                                    return;
                                } else if (result.getResultType() == PolygonSubtractionResultType.BOTH_POLYGONS_INTERSECT_THEMSELVES) {
                                    if (regionAngegeben) {
                                        player.spigot().sendMessage(Lang.build("Die angegebene Region überschneidet sich selbst, was eigentlich hier nicht vorkommen dürfte. " +
                                                "Deine markierte Fläche übrigens auch, damit kannst du keine Region beschneiden."));
                                    } else {
                                        player.spigot().sendMessage(Lang.build("Die Region, in der du gerade stehst, überschneidet sich selbst, was eigentlich hier nicht vorkommen dürfte.. " +
                                                "Deine markierte Fläche übrigens auch, damit kannst du keine Region beschneiden."));
                                    }
                                    return;
                                } else if (result.getResultType() == PolygonSubtractionResultType.SELECTION_IS_INSIDE_OLD_REGION) {
                                    if (regionAngegeben) {
                                        player.spigot().sendMessage(Lang.build("Deine Markierung liegt komplett innerhalb der angegebenen Region, " +
                                                "du kannst aber keine Löcher in Regionen schneiden."));
                                    } else {
                                        player.spigot().sendMessage(Lang.build("Deine Markierung liegt komplett innerhalb der Region, in der du gerade stehst, " +
                                                "du kannst aber keine Löcher in Regionen schneiden."));
                                    }
                                    return;
                                } else if (result.getResultType() == PolygonSubtractionResultType.OLD_REGION_HAS_POINT_MULTIPLE) {
                                    if (regionAngegeben) {
                                        player.spigot().sendMessage(Lang.build("Die angegebene Region verwendet Eckpunkte mehrfach, was nicht vorkommen sollte."));
                                    } else {
                                        player.spigot().sendMessage(Lang.build("Die Region, in der du stehst, verwendet Eckpunkte mehrfach, was nicht vorkommen sollte."));
                                    }
                                    return;
                                } else if (result.getResultType() == PolygonSubtractionResultType.SELECTION_HAS_POINT_MULTIPLE) {
                                    player.spigot().sendMessage(Lang.build("Deine Markierung verwendet Eckpunkte mehrfach.")); //Selbe Nachricht weiter oben benötigt
                                    return;
                                } else if (result.getResultType() == PolygonSubtractionResultType.POLYGONS_ARE_EQUAL) {
                                    if (regionAngegeben) {
                                        player.spigot().sendMessage(Lang.build("Deine Markierung entspricht genau deiner angegebenen Region, " +
                                                "also so keine sinnvolle Beschneidung möglich. Um deine Region zu löschen, verwende /claim delete"));
                                    } else {
                                        player.spigot().sendMessage(Lang.build("Deine Markierung entspricht genau deiner Region, in der du stehst, " +
                                                "also so keine sinnvolle Beschneidung möglich. Um deine Region zu löschen, verwende /claim delete"));
                                    }
                                    return;
                                }  else if (result.getResultType() == PolygonSubtractionResultType.OLD_REGION_IS_INSIDE_SELECTION) {
                                    if (regionAngegeben) {
                                        player.spigot().sendMessage(Lang.build("Die angegebenen Region liegt komplett innerhalb deiner Markierung, " +
                                                "also so keine sinnvolle Beschneidung möglich. Um deine Region zu löschen, verwende /claim delete"));
                                    } else {
                                        player.spigot().sendMessage(Lang.build("Die Region, in der du stehst, liegt komplett innerhalb deiner Markierung, " +
                                                "also so keine sinnvolle Beschneidung möglich. Um deine Region zu löschen, verwende /claim delete"));
                                    }
                                    return;
                                } else if (result.getResultType() == PolygonSubtractionResultType.SELECTION_DIVIDES_OLD_REGION_INTO_PARTS) {
                                    if (regionAngegeben) {
                                        player.spigot().sendMessage(Lang.build("Deine Markierung zerteilt die angegebene Region, was nicht vorkommen sollte."));
                                    } else {
                                        player.spigot().sendMessage(Lang.build("Deine Markierung zerteilt die Region, in der du stehst, was nicht vorkommen sollte."));
                                    }
                                    return;
                                } else if (result.getResultType() == PolygonSubtractionResultType.SUBTRACTION_NOT_BIG_ENOUGH_TO_REMOVE_WHOLE_BLOCKS) {
                                    if (regionAngegeben) {
                                        player.spigot().sendMessage(Lang.build("Deine Markierung würde so wenig Fläche von der angegebenen Region entfernen, " +
                                                "dass keine ganzen Blöcke entfernt werden würden."));
                                    } else {
                                        player.spigot().sendMessage(Lang.build("Deine Markierung würde so wenig Fläche von der Region, in der du stehst, entfernen, " +
                                                "dass keine ganzen Blöcke entfernt werden würden."));}
                                    return;
                                } else if (result.getResultType() == PolygonSubtractionResultType.RESULT_POLYGON_HAS_POINT_MULTIPLE) {
                                    if (regionAngegeben) {
                                        player.spigot().sendMessage(Lang.build("Deine Markierung berührt die angegebene Region nur. " +
                                                "Um die Region zu beschneiden, müsste sie aber einen Teil der Region abdecken."));
                                    } else {
                                        player.spigot().sendMessage(Lang.build("Deine Markierung berührt die Region, in der du stehst, nur. " +
                                                "Um die Region zu beschneiden, müsste sie aber einen Teil der Region abdecken."));
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
                            Polygonal2DRegion markierungAlsPolygon = ClaimUtils.markierungAlsPolygon(oldRegion, markierung);
                            retractQuestion(player, player.getWorld(), oldRegion, regionAngegeben, alteFläche, neueFläche, false,
                                    markierungAlsPolygon,
                                    null, null, ergebnisPolygon);
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

    public static void retractQuestion(Player player, World world, ProtectedRegion oldRegion, boolean regionStated, double alteFläche, double neueFläche, boolean isDirectionalExtension,
                                      Polygonal2DRegion selectionWithCorrectHeight,
                                      BlockFace direction, Integer extendLength, List<BlockVector2> eckpunkteDerNeuenRegion
    ) {
        String message;
        if (isDirectionalExtension) {
            if (regionStated) {
                message = "Möchtest du die angegebene Region (Fläche: " + alteFläche + "m^2) um " + extendLength + " Blöcke auf Seite " +
                        direction.name() + " verkleinern? Die neue Region hätte eine Fläche von " + neueFläche + "m^2.";
            } else {
                message = "Möchtest du die Region, auf der du stehst (Fläche: " + alteFläche + "m^2), um " + extendLength + " Blöcke auf Seite " +
                        direction.name() + " verkleinern? Die neue Region hätte eine Fläche von " + neueFläche + "m^2.";
            }
        } else {
            if (regionStated) {
                message = "Möchtest du die angegebene Region (Fläche: " + alteFläche + "m^2) um deine Markierung beschneiden? " +
                        "Die neue Region hätte eine Fläche von " + neueFläche + "m^2.";
            } else {
                message = "Möchtest du die Region, auf der du stehst (Fläche: " + alteFläche + "m^2) um deine Markierung beschneiden? " +
                        "Die neue Region hätte eine Fläche von " + neueFläche + "m^2.";
            }
        }
        PreRetractConfirmationEvent event = new PreRetractConfirmationEvent(
                player, isDirectionalExtension, regionStated, alteFläche, neueFläche, direction, extendLength, message);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            if (isDirectionalExtension) {
                ConfirmationListener.inst().addConfirmation(new RetractDirectionalQueue(
                        oldRegion, world, player, direction, extendLength, regionStated));
            } else {
                ConfirmationListener.inst().addConfirmation(new RetractSelectionalQueue(
                        oldRegion, world, player, eckpunkteDerNeuenRegion, regionStated, selectionWithCorrectHeight));
            }
            player.spigot().sendMessage(Lang.build(event.getMessage()),
                    Lang.build(Lang.EXPAND_YES, "/yes", null, null),
                    Lang.build(Lang.EXPAND_NO, "/no", null, null));
        }
    }

    public static PolygonSubtractionResult subtractPolygons(List<BlockVector2> alteRegionsBlockPolygon, List<BlockVector2> markierungsBlockPolygon) {
        if (ClaimUtils.polygonHatEckpunkteMehrfach(alteRegionsBlockPolygon)) {
            return new PolygonSubtractionResult(null,PolygonSubtractionResultType.OLD_REGION_HAS_POINT_MULTIPLE);
        }
        if (ClaimUtils.polygonHatEckpunkteMehrfach(markierungsBlockPolygon)) {
            return new PolygonSubtractionResult(null,PolygonSubtractionResultType.SELECTION_HAS_POINT_MULTIPLE);
        }
        ArrayList<Vector2> alteRegionsPolygon = ClaimUtils.eckpunkteExaktAusEckpunkteGanz(alteRegionsBlockPolygon);
        ArrayList<Vector2> markierungsPolygon = ClaimUtils.eckpunkteExaktAusEckpunkteGanz(markierungsBlockPolygon);
        // Die Richtung ist entgegen des Uhrzeigersinns:
        Boolean region1ImUhrzeigerSinn = ClaimUtils.verläuftPolygonImUhrzeigersinn(alteRegionsPolygon);
        Boolean region2ImUhrzeigerSinn = ClaimUtils.verläuftPolygonImUhrzeigersinn(markierungsPolygon);
        if (region2ImUhrzeigerSinn == null && region1ImUhrzeigerSinn == null) {
            return new PolygonSubtractionResult(null, PolygonSubtractionResultType.BOTH_POLYGONS_INTERSECT_THEMSELVES);
        }
        if (region1ImUhrzeigerSinn == null) {
            return new PolygonSubtractionResult(null, PolygonSubtractionResultType.OLD_REGION_INTERSECTS_ITSELF);
        }
        if (region2ImUhrzeigerSinn == null) {
            return new PolygonSubtractionResult(null, PolygonSubtractionResultType.SELECTION_INTERSECTS_ITSELF);
        }
        if (region2ImUhrzeigerSinn) {
            markierungsPolygon = ClaimUtils.punktListeInvertiert(markierungsPolygon);
        }
        if (region1ImUhrzeigerSinn) {
            alteRegionsPolygon = ClaimUtils.punktListeInvertiert(alteRegionsPolygon);
        }
        if (ClaimUtils.sindPolygoneGleich(markierungsBlockPolygon, alteRegionsBlockPolygon)) {
            return new PolygonSubtractionResult(null, PolygonSubtractionResultType.POLYGONS_ARE_EQUAL);
        }
        //Wenn Polygone sich kein bisschen überlappen:
        if (!ClaimUtils.polygoneÜberlappen(alteRegionsPolygon, markierungsPolygon)) {
            // Markierung und Region überschneiden sich nicht
            return new PolygonSubtractionResult(null, PolygonSubtractionResultType.POLYGONS_DONT_INTERSECT_EACH_OTHER);
        }
        ArrayList<Vector2> differenzPolygon = null;
        for (Vector2 startpunkt : alteRegionsPolygon) {
            //... der nicht in der Markierung liegt...
            if (!ClaimUtils.liegtPunktInPolygon(startpunkt, markierungsPolygon, true)) {
                if (differenzPolygon == null) {
                    System.out.println("STARTPUNKT: (" + startpunkt.getX() + "," + startpunkt.getZ() + ")");
                    differenzPolygon = differenzPolygonBildung(startpunkt, alteRegionsPolygon, markierungsPolygon,
                            startpunkt, true, new ArrayList<>());
                }
            }
        }
        //Wenn kein Differenzpolygon gebildet werden konnte, liegt die Region komplett in der Markierung (unbestätigt)
        if (differenzPolygon == null) {
            // Region liegt komplett innerhalb Markierung
            System.out.println("CmdRetract.subtractPolygons - Bestätigung: Wenn kein Differenzpolygon gebildet werden konnte, liegt die Region komplett in der Markierung.");
            return new PolygonSubtractionResult(null, PolygonSubtractionResultType.OLD_REGION_IS_INSIDE_SELECTION);
        }
        //Wenn das neue Polygon der alten Region entspricht, überschneiden sich die Polygone nicht:
        if (ClaimUtils.scharfePolgonFläche(alteRegionsPolygon) == ClaimUtils.scharfePolgonFläche(differenzPolygon)) {
            boolean markierungLiegtInAlterRegion = true;
            for (Vector2 polygon2punkt : markierungsPolygon) {
                if (!ClaimUtils.liegtPunktInPolygon(polygon2punkt,alteRegionsPolygon, true)) {
                    markierungLiegtInAlterRegion = false;
                }
            }
            if (markierungLiegtInAlterRegion) {
                // Markierung liegt komplett innerhalb Region
                return new PolygonSubtractionResult(null, PolygonSubtractionResultType.SELECTION_IS_INSIDE_OLD_REGION);
            } else {
                boolean alteRegionLiegtInMarkierung = true;
                System.out.println("boolean alteRegionLiegtInMarkierung = true;");
                for (Vector2 polygon2punkt : alteRegionsPolygon) {
                    if (!ClaimUtils.liegtPunktInPolygon(polygon2punkt, markierungsPolygon, true)) {
                        alteRegionLiegtInMarkierung = false;
                    }
                }
                if (alteRegionLiegtInMarkierung) {
                    // alte Region liegt komplett innerhalb Markierung
                    return new PolygonSubtractionResult(null, PolygonSubtractionResultType.OLD_REGION_IS_INSIDE_SELECTION);
                } else {
                    // Markierung und Region überschneiden sich nicht
                    return new PolygonSubtractionResult(null, PolygonSubtractionResultType.POLYGONS_DONT_INTERSECT_EACH_OTHER);
                }
            }
        }
        //Wenn aus dem Polygon der ursprünglichen Region Eckpunkte gibt, die weder im Ergebnis-Polygon noch in der Markierung liegen,
        // dann wurde die alte Region durch die Markierung in mehrere Teile zerteilt (soll der Spieler nicht so machen)
        boolean regionWirdDurchMarkierungZerteilt = false;
        for (Vector2 alteRegionsEckpunkt : alteRegionsPolygon) {
            if (!ClaimUtils.liegtPunktInPolygon(alteRegionsEckpunkt, markierungsPolygon, true) && !ClaimUtils.liegtPunktInPolygon(alteRegionsEckpunkt, differenzPolygon, true)) {
                regionWirdDurchMarkierungZerteilt = true;
            }
        }
        if (regionWirdDurchMarkierungZerteilt) {
            return new PolygonSubtractionResult(null, PolygonSubtractionResultType.SELECTION_DIVIDES_OLD_REGION_INTO_PARTS);
        }
        List<BlockVector2> differenzBlockPolygon = ClaimUtils.polygonOhneRedundantePunkte(ClaimUtils.eckpunkteGanzAusEckpunkteExakt(differenzPolygon));
        if (ClaimUtils.polygonHatEckpunkteMehrfach(differenzBlockPolygon)) {
            return new PolygonSubtractionResult(null, PolygonSubtractionResultType.RESULT_POLYGON_HAS_POINT_MULTIPLE);
        }
        if (ClaimUtils.flächeEinesPixelPolygons(differenzBlockPolygon) == ClaimUtils.flächeEinesPixelPolygons(alteRegionsBlockPolygon)) {
            return new PolygonSubtractionResult(null, PolygonSubtractionResultType.SUBTRACTION_NOT_BIG_ENOUGH_TO_REMOVE_WHOLE_BLOCKS);
        }
        return new PolygonSubtractionResult(differenzBlockPolygon, PolygonSubtractionResultType.BOUNDING_POLYGON_FOUND);
    }

    private static ArrayList<Vector2> differenzPolygonBildung(
            //Bleibt unverändert:
            Vector2 startpunkt, ArrayList<Vector2> polygon1punkte, ArrayList<Vector2> polygon2punkte,
            //Wird in Rekursion verändert:
            Vector2 punktAufPolygonKante, boolean eckpunktIstVonP1, ArrayList<Vector2> differenzPolygon
    ) {
        if (differenzPolygon.contains(startpunkt)) {
            //Sobald man wieder beim Start angekommen ist, ist das neue Polygon komplett und wird zurück gegeben
            return differenzPolygon;
        } else {
            IntersectionResult result;
            Vector2 nächsterEckpunkt;
            if (eckpunktIstVonP1) {
                //Polygon A = Polygon 1, Polygon B = Polygon 2
                nächsterEckpunkt = ClaimUtils.nächsterEckpunkt(punktAufPolygonKante, polygon1punkte);
                result = ClaimUtils.ersterSchnittpunktMitPolygonAufStrecke(punktAufPolygonKante, nächsterEckpunkt, polygon2punkte, true);
            } else {
                //Polygon A = Polygon 2, Polygon B = Polygon 1 ==> Vorheriger Eckpunkt muss gewählt werden
                nächsterEckpunkt = ClaimUtils.vorherigerEckpunkt(punktAufPolygonKante, polygon2punkte);
                result = ClaimUtils.ersterSchnittpunktMitPolygonAufStrecke(punktAufPolygonKante, nächsterEckpunkt, polygon1punkte, true);
            }
            if (result != null && !result.getSchnittpunkt().equals(punktAufPolygonKante)) {
                //Polygon A schneidet sich zwischen punktAufPolygonKante und nächsterEckpunkt mit Polygon B:
                // der erste Schnittpunkt wird als Eckpunkt des neuen Polygons hinzugefügt, das Polygon wird zu Polygon B gewechselt
                // und der nächste Punkt auf Polygon B wird zum neuen Polygon hinzugefügt
                // danach wird rekursiv die nächste Kante in Polygon B betrachtet
                Vector2 ersterSchnittpunkt = result.getSchnittpunkt();
                Bukkit.broadcastMessage(ChatColor.RED + "differenzPolygon: Schnittpunkt: (" + ersterSchnittpunkt.getX() + "," + ersterSchnittpunkt.getZ() + ")");
                differenzPolygon.add(ersterSchnittpunkt);
                return differenzPolygonBildung(startpunkt, polygon1punkte, polygon2punkte,
                        ersterSchnittpunkt, !eckpunktIstVonP1,  differenzPolygon);
            } else {
                //Polygon A schneidet sich zwischen punktAufPolygonKante und nächsterEckpunkt nicht mit Polygon B:
                // und der nächste Punkt auf Polygon A wird zum neuen Polygon hinzugefügt
                // danach wird rekursiv die nächste Kante in Polygon A betrachtet (Polygon wird also nicht gewechselt)
                differenzPolygon.add(nächsterEckpunkt);
                Bukkit.broadcastMessage(ChatColor.RED + "differenzPolygon: nächsterEckpunkt: (" + nächsterEckpunkt.getX() + "," + nächsterEckpunkt.getZ() + ")");
                return differenzPolygonBildung(startpunkt, polygon1punkte, polygon2punkte,
                        nächsterEckpunkt, eckpunktIstVonP1,  differenzPolygon);
            }
        }
    }
}
