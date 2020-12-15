package de.bergtiger.claim.bdo;

public class TigerPoint {
	
	public final double x, z;

	public TigerPoint(double x, double z) {
		this.x = x;
		this.z = z;
//		System.out.println(String.format("Double (%f, %f)", x, z));
	}

	public TigerPoint(int x, int z) {
		this.x = x;
		this.z = z;
//		System.out.println(String.format("Integer (%d, %d)", x, z));
	}
}
