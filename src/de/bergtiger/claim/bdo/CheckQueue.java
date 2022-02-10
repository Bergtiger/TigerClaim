package de.bergtiger.claim.bdo;

public class CheckQueue {
    private final TigerClaim tigerClaim;

    public CheckQueue(TigerClaim tigerClaim) {
        this.tigerClaim = tigerClaim;
    }

    public TigerClaim getRegion() {
        return tigerClaim;
    }
}
