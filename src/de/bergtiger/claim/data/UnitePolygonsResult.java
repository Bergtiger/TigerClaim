package de.bergtiger.claim.data;

import com.sk89q.worldedit.math.BlockVector2;

import java.util.List;

public class UnitePolygonsResult {
    public final UnitePolygonsResultType resultType;
    public final List<BlockVector2> polygon;
    public final Boolean containsGapsFromOldRegionAndSelection;

    public UnitePolygonsResult(List<BlockVector2> polygon, UnitePolygonsResultType resultType, Boolean containsGapsFromOldRegionAndSelection) {
        this.resultType = resultType;
        this.polygon = polygon;
        this.containsGapsFromOldRegionAndSelection = containsGapsFromOldRegionAndSelection;
    }

    public UnitePolygonsResultType getResultType() {
        return resultType;
    }

    public List<BlockVector2> getPolygon() {
        return polygon;
    }

    public Boolean getContainsGapsFromOldRegionAndSelection() {
        return containsGapsFromOldRegionAndSelection;
    }
}
