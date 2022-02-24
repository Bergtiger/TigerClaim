package de.bergtiger.claim.data;

import com.sk89q.worldedit.math.BlockVector2;

import java.util.List;

public class UnitePolygonsResult {
    public final UnitePolygonsResultType resultType;
    public final List<BlockVector2> polygon;

    public UnitePolygonsResult (List<BlockVector2> polygon, UnitePolygonsResultType resultType) {
        this.resultType = resultType;
        this.polygon = polygon;
    }

    public UnitePolygonsResultType getResultType() {
        return resultType;
    }

    public List<BlockVector2> getPolygon() {
        return polygon;
    }
}
