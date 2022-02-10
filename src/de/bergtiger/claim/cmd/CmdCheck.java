package de.bergtiger.claim.cmd;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import de.bergtiger.claim.bdo.*;
import de.bergtiger.claim.data.language.Lang;
import de.bergtiger.claim.data.permission.Perm;
import de.bergtiger.claim.events.PreCheckConfirmationEvent;
import de.bergtiger.claim.listener.ConfirmationListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdCheck {

    public static void check(CommandSender cs) {
        if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_CHECK)) {
            if (cs instanceof Player p) {
                // Get amount of claims
                TigerClaim tc;
                // WorldGuard has dependency for WorldEdit
                if (Perm.hasPermission(cs, Perm.CLAIM_ADMIN, Perm.CLAIM_WORLDEDIT)) {
                    WorldEdit we = WorldEdit.getInstance();
                    BukkitPlayer bp = BukkitAdapter.adapt(p);
                    try {
                        Region s = we.getSessionManager().get(bp).getSelection(bp.getWorld());
                        if (s != null) {
                            if (s instanceof CuboidRegion) {
                                // Cuboid
                                tc = new TigerClaimCuboid(p, p.getWorld(), (CuboidRegion) s);
                            } else {
                                // Polygon
                                tc = new TigerClaimPolygon(p, p.getWorld(), (Polygonal2DRegion) s);
                            }
                        } else {
                            // No Region
                            tc = new TigerClaimRadius(p, p.getLocation());
                        }
                    } catch (IncompleteRegionException e) {
                        // No Region
                        tc = new TigerClaimRadius(p, p.getLocation());
                    }
                } else {
                    // Without WorlEdit
                    tc = new TigerClaimRadius(p, p.getLocation());
                }
                //We call PreCheckConfirmationEvent
                PreCheckConfirmationEvent event = new PreCheckConfirmationEvent(tc, p, null);
                Bukkit.getPluginManager().callEvent(event);
                String spielerBenachrichtigung = "&eWillst du die ausgewählte Fläche überprüfen? ";
                if (event.getSpielerBenachrichtigung() != null) {
                    spielerBenachrichtigung = event.getSpielerBenachrichtigung();
                }
                if (!event.isCancelled()) {
                    // send Confirmation
                    ConfirmationListener.inst().addConfirmation(new CheckQueue(tc));
                    // inform Player
                    p.spigot().sendMessage(Lang.build(spielerBenachrichtigung, null, Lang.build(tc.buildHover()), null),
                            Lang.build(Lang.INSERT_YES, "/yes", Lang.build(Lang.INSERT_HOVER_YES), null),
                            Lang.build(Lang.INSERT_NO, "/no", Lang.build(Lang.INSERT_HOVER_NO), null));
                }
            } else {
                // Not a player
                cs.sendMessage(Lang.NOPLAYER.get());
            }
        } else {
            cs.sendMessage(Lang.NOPERMISSION.get());
        }
    }
}
