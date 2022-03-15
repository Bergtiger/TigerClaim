package de.bergtiger.claim.data;

import com.sk89q.worldedit.math.BlockVector2;

import java.util.List;

public class PolygonSubtractionResult {
    public final PolygonSubtractionResultType resultType;
    public final List<BlockVector2> polygon;

    public PolygonSubtractionResult(List<BlockVector2> polygon, PolygonSubtractionResultType resultType) {
        this.resultType = resultType;
        this.polygon = polygon;
    }

    public PolygonSubtractionResultType getResultType() {
        return resultType;
    }

    public List<BlockVector2> getPolygon() {
        return polygon;
    }
}
