package de.bergtiger.claim;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.math.Vector3;

import java.util.Comparator;

public final class TigerVector2 {
    public static final TigerVector2 ZERO = new TigerVector2(0, 0);
    public static final TigerVector2 UNIT_X = new TigerVector2(1, 0);
    public static final TigerVector2 UNIT_Z = new TigerVector2(0, 1);
    public static final TigerVector2 ONE = new TigerVector2(1, 1);
    private final double x;
    private final double z;

    public static TigerVector2 at(double x, double z) {
        return new TigerVector2(x,z);
    }

    public TigerVector2(double x, double z) {
        this.x = x;
        this.z = z;
    }

    public static TigerVector2 fromBlockVector2(BlockVector2 blockVector2) {
        return new TigerVector2(blockVector2.getX(), blockVector2.getZ());
    }

    public double getX() {
        return this.x;
    }

    public double getBlockX() {
        return this.x;
    }

    public TigerVector2 withX(double x) {
        return at(x, this.z);
    }

    public double getZ() {
        return this.z;
    }

    public double getBlockZ() {
        return this.z;
    }

    public TigerVector2 withZ(double z) {
        return at(this.x, z);
    }

    public TigerVector2 add(TigerVector2 other) {
        return this.add(other.x, other.z);
    }

    public TigerVector2 add(double x, double z) {
        return at(this.x + x, this.z + z);
    }

    public TigerVector2 add(TigerVector2... others) {
        double newX = this.x;
        double newZ = this.z;
        TigerVector2[] var4 = others;
        int var5 = others.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            TigerVector2 other = var4[var6];
            newX += other.x;
            newZ += other.z;
        }

        return at(newX, newZ);
    }

    public TigerVector2 subtract(TigerVector2 other) {
        return this.subtract(other.x, other.z);
    }

    public TigerVector2 subtract(double x, double z) {
        return at(this.x - x, this.z - z);
    }

    public TigerVector2 subtract(TigerVector2... others) {
        double newX = this.x;
        double newZ = this.z;
        TigerVector2[] var4 = others;
        int var5 = others.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            TigerVector2 other = var4[var6];
            newX -= other.x;
            newZ -= other.z;
        }

        return at(newX, newZ);
    }

    public TigerVector2 multiply(TigerVector2 other) {
        return this.multiply(other.x, other.z);
    }

    public TigerVector2 multiply(double x, double z) {
        return at(this.x * x, this.z * z);
    }

    public TigerVector2 multiply(TigerVector2... others) {
        double newX = this.x;
        double newZ = this.z;
        TigerVector2[] var4 = others;
        int var5 = others.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            TigerVector2 other = var4[var6];
            newX *= other.x;
            newZ *= other.z;
        }

        return at(newX, newZ);
    }

    public TigerVector2 multiply(int n) {
        return this.multiply(n, n);
    }

    public TigerVector2 divide(TigerVector2 other) {
        return this.divide(other.x, other.z);
    }

    public TigerVector2 divide(double x, double z) {
        return at(this.x / x, this.z / z);
    }

    public TigerVector2 divide(double n) {
        return this.divide(n, n);
    }

    public double length() {
        return Math.sqrt((double)this.lengthSq());
    }

    public double lengthSq() {
        return this.x * this.x + this.z * this.z;
    }

    public double distance(TigerVector2 other) {
        return Math.sqrt((double)this.distanceSq(other));
    }

    public double distanceSq(TigerVector2 other) {
        double dx = other.x - this.x;
        double dz = other.z - this.z;
        return dx * dx + dz * dz;
    }

    public TigerVector2 normalize() {
        double len = this.length();
        double x = (double)this.x / len;
        double z = (double)this.z / len;
        return at(x, z);
    }

    public double dot(TigerVector2 other) {
        return this.x * other.x + this.z * other.z;
    }

    public boolean containedWithin(TigerVector2 min, TigerVector2 max) {
        return this.x >= min.x && this.x <= max.x && this.z >= min.z && this.z <= max.z;
    }

    public TigerVector2 floor() {
        return this;
    }

    public TigerVector2 ceil() {
        return this;
    }

    public TigerVector2 round() {
        return this;
    }

    public TigerVector2 abs() {
        return at(Math.abs(this.x), Math.abs(this.z));
    }

    public TigerVector2 transform2D(double angle, double aboutX, double aboutZ, double translateX, double translateZ) {
        angle = Math.toRadians(angle);
        double x = (double)this.x - aboutX;
        double z = (double)this.z - aboutZ;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x2 = x * cos - z * sin;
        double z2 = x * sin + z * cos;
        return at(x2 + aboutX + translateX, z2 + aboutZ + translateZ);
    }

    public TigerVector2 getMinimum(TigerVector2 v2) {
        return new TigerVector2(Math.min(this.x, v2.x), Math.min(this.z, v2.z));
    }

    public TigerVector2 getMaximum(TigerVector2 v2) {
        return new TigerVector2(Math.max(this.x, v2.x), Math.max(this.z, v2.z));
    }

    public Vector2 toVector2() {
        return Vector2.at((double)this.x, (double)this.z);
    }

    public Vector3 toVector3() {
        return this.toVector3(0.0D);
    }

    public Vector3 toVector3(double y) {
        return Vector3.at((double)this.x, y, (double)this.z);
    }

    public BlockVector3 toBlockVector3() {
        return this.toBlockVector3(0);
    }

    public BlockVector3 toBlockVector3(int y) {
        return BlockVector3.at(this.x, y, this.z);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof TigerVector2)) {
            return false;
        } else {
            TigerVector2 other = (TigerVector2)obj;
            return other.x == this.x && other.z == this.z;
        }
    }

    public String toString() {
        return "(" + this.x + ", " + this.z + ")";
    }

    public String toParserString() {
        return this.x + "," + this.z;
    }
}