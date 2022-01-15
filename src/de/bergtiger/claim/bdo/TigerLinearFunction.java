package de.bergtiger.claim.bdo;

public class TigerLinearFunction {

	public final TigerPoint m, n;

	public TigerLinearFunction(TigerPoint pointA, TigerPoint pointB) {
		// f(x) = mx + n
		m = new TigerPoint(pointB.x - pointA.x, pointB.z - pointA.z);
		n = pointA;
	}

	public TigerPoint getIntersection(TigerLinearFunction g) {
		// f(r) und g(s)
		// I  = mx = ax*s
		// II = mz = az*s
		// s  = mx/ax
		// s in II mz = az*(mx/ax)
		if (m.z != (g.m.z * (m.x / g.m.x))) {
			// linearly independent
			double s, r;
			// f(r) = g(s)
			// I : nx + mx*r = bx + ax*s
			// II: nz + mz*r = bz + az*s
			if(m.x == 0) {
				// m.x == 0 => g.m.x != 0 else linearly dependent
				// if mx = 0 -> 
				// bx + ax*s = nx
				//		ax*s = nx - bx
				// 		   s =(nx - bx)/ax
				s = (n.x - g.n.x)/g.m.x;
				// s in II nz + mz*r = bz + az*s
				//				mz*r = bz + az*s - nz
				//				   r =(bz + az*s - nz)/mz
				r = (g.n.z + g.m.z * s - n.z)/m.z;
			} else
			if(m.z == 0) {
				// m.z == 0 => g.m.z != 0
				// II = nz = bz + az * s
				//		nz - bz = az * s
				//	   (nz - bz) /az = s
				s = (n.z - g.n.z)/g.m.z;
				// s in I nx + mx*r = bx + ax*s
				//			   mx*r = bx + ax*s - nx
				//				  r =(bx + ax*s - nx)/mx
				r = (g.n.x + g.m.x * s - n.x)/m.x;
			} else
			if(g.m.x == 0) {
				// g.m.x == 0 => m.x != 0
				// I: nx + mx*r = bx
				//		   mx*r = bx - nx
				//			  r =(bx - nx)/mx
				r = (g.n.x - n.x)/m.x;
				// r in II bz + az*s = nz + mz*r
				//				az*s = nz + mz*r - bz
				//				   s =(nz + mz*r - bz)/az
				s = (n.z + m.z * r - g.n.z)/g.m.z;
			} else
			if(g.m.z == 0) {
				// g.m.z == 0 => m.z != 0
				// II: nz + mz*r = bz
				//             r =(bz - nz)/mz
				r = (g.n.z - n.z)/m.z;
				// r in I bx + ax*s = nx + mx*r
				//			   ax*s = nx + mx*r - bx
				//				  s =(nx + mx*r - bx)/ax
				s = (n.x + m.x * r - g.n.x)/g.m.x;
			} else {
				// I mx*r =  bx - nx + ax*s 
				//		r =	(bx - nx + ax*s)/mx
				//		r =	(bx - nx) / mx + ax*s*(1/mx)
				//		r =	(bx - nx) / mx + (ax/mx)*s
				// r in II = nz + mz*((bx - nx)/mx + (ax/mx)*s)		= bz + az*s
				// 			 nz + mz*((bx - nx)/mx + mz*(ax/mx)*s	= bz + az*s
				// 				  mz*(ax/mx)*s						= bz + az*s - nz - mz*((bx - nx)/mx)
				// 				  mz*(ax/mx)*s - az*s				= bz 		- nz - mz*((bx - nx)/mx)
				// 			   s*(mz*(ax/mx)   - az) 				= bz 		- nz - mz*((bx - nx)/mx)
				// 			   s 									=(bz 		- nz - mz*((bx - nx)/mx)) / (mz*(ax/mx)-az)
				s = (g.n.z - n.z - m.z * ((g.n.x - n.x) / m.x))/(m.z * ((g.m.x / m.x)) - g.m.z);
				// s in I r = nx + mx*r = bx + ax*s
				// mx*r = bx + ax*s -nx
				// r = (bx + ax*s - nx)/mx
				r = (g.n.x + g.m.x * s - n.x) / m.x;
			}
//			System.out.println(String.format("f(r) = (%f|%f) + (%f|%f)*r = (%f|%f) + (%f|%f)*s = g(s) r = %f, s = %f",
//				n.x, n.z,
//				m.x, m.z,
//				g.n.x, g.n.z,
//				g.m.x, g.m.z, 
//				r, s));
			return new TigerPoint(n.x + m.x * r, n.z + m.z * r);
		}
		return null;
	}
}
