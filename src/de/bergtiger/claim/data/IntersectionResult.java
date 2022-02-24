package de.bergtiger.claim.data;

import com.sk89q.worldedit.math.Vector2;

public class IntersectionResult {
    public final Vector2 schnittpunkt, ecke1, ecke2;

    public IntersectionResult(Vector2 schnittpunkt, Vector2 ecke1, Vector2 ecke2) {
        this.schnittpunkt = schnittpunkt;
        this.ecke1 = ecke1;
        this.ecke2 = ecke2;
    }

    public Vector2 getSchnittpunkt() {
        return schnittpunkt;
    }

    public Vector2 getEcke1() {
        return ecke1;
    }

    public Vector2 getEcke2() {
        return ecke2;
    }
}
