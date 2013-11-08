/* Author: Liang Xia, UC Santa Barbara, May 11, 2013
 * Description: Point class in projective coordinate system
 */

import java.math.BigInteger;
import java.util.ArrayList;


public class ProjPoint {
	public BigInteger x;
	public BigInteger y;
	public BigInteger z;
	public boolean isInfinity;
	public static final ProjPoint the_Infinity = new ProjPoint(true);

	public ProjPoint() {
		x = new BigInteger("0");
		y = new BigInteger("0");
		z = new BigInteger("0");
		isInfinity = false;		
	}
	
	public ProjPoint(ECPoint ecp) {
		x = ecp.x;
		y = ecp.y;
		z = BigInteger.ONE;
		isInfinity = false;		
	}
	
	public ProjPoint(BigInteger bx, BigInteger by, BigInteger bz) {
		x = bx;
		y = by;
		z = bz;
	}

	public ProjPoint(boolean infinity) {
		assert infinity == true;
		isInfinity = true;		
	}
	
	public ECPoint getAffineECPoint(BigInteger the_p) {		
		BigInteger z_inv = z.modInverse(the_p);
		BigInteger z_inv2 = z_inv.multiply(z_inv).mod(the_p);
		BigInteger z_inv3 = z_inv2.multiply(z_inv).mod(the_p);
		BigInteger ecp_x = x.multiply(z_inv2);
		BigInteger ecp_y = y.multiply(z_inv3);
		ecp_x = ecp_x.mod(the_p);
		ecp_y = ecp_y.mod(the_p);
		return new ECPoint(ecp_x, ecp_y);
	}

	public ProjPoint negate() {
		ProjPoint point = new ProjPoint();
		point.x = x;
		point.y = y.negate();
		point.z = z;
		return point;
	}
	
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";		
	}
	
	public static ProjPoint projectiveAddition(ProjPoint pp1, ProjPoint pp2) {
		if (pp1.isInfinity == true)
			return pp2;
		else if (pp2.isInfinity == true)
			return pp1;
		BigInteger X1 = pp1.x;
		BigInteger Y1 = pp1.y;
		BigInteger Z1 = pp1.z;
		BigInteger X2 = pp2.x;
		BigInteger Y2 = pp2.y;
		BigInteger Z2 = pp2.z;
		BigInteger Z2_sqrt = Z2.multiply(Z2).mod(ECC.the_p);  // mult
		BigInteger Z1_sqrt = Z1.multiply(Z1).mod(ECC.the_p);  // mult
		BigInteger U1 = X1.multiply(Z2_sqrt).mod(ECC.the_p);  // mult
		BigInteger U2 = X2.multiply(Z1_sqrt).mod(ECC.the_p);  // mult
		BigInteger W = U1.subtract(U2);
		BigInteger S1 = Y1.multiply(Z2).multiply(Z2_sqrt).mod(ECC.the_p); // mult * 2
		BigInteger S2 = Y2.multiply(Z1).multiply(Z1_sqrt).mod(ECC.the_p); // mult * 2
		BigInteger R = S1.subtract(S2);
		BigInteger T = U1.add(U2);
		BigInteger M = S1.add(S2).mod(ECC.the_p);
		
		if (U1.compareTo(U2) == 0 &&
			M.compareTo(BigInteger.ZERO) == 0) {
			return ProjPoint.the_Infinity;
		}
		
		BigInteger Z3 = Z1.multiply(Z2).multiply(W).mod(ECC.the_p); // mult * 2
		BigInteger W_sqrt = W.multiply(W);
		BigInteger TW2 = T.multiply(W_sqrt);
		BigInteger X3 = R.multiply(R).subtract( TW2 ).mod(ECC.the_p);
		BigInteger V = TW2.subtract( X3.multiply(new BigInteger("2")) );
		BigInteger Y3 = W_sqrt.multiply(W).multiply(M);
		Y3 = ( ( V.multiply(R) ).subtract(Y3) ).multiply(ECC.the_2_inv); //TODO: 2_inv is divide by 2?
		Y3 = Y3.mod(ECC.the_p);
		return new ProjPoint(X3, Y3, Z3);
	}
	
	public static ProjPoint projectiveDoubling(ProjPoint pp1) {
		if (pp1.isInfinity == true)
			return pp1;
		BigInteger X1 = pp1.x;
		BigInteger Y1 = pp1.y;
		BigInteger Z1 = pp1.z;
		BigInteger M = X1.multiply(X1).multiply(new BigInteger("3")).mod(ECC.the_p);
		BigInteger aZ4 = Z1.multiply(Z1).mod(ECC.the_p);
		aZ4 = aZ4.multiply(aZ4).multiply(ECC.the_a).mod(ECC.the_p);
		M = M.add(aZ4).mod(ECC.the_p);
		BigInteger Z3 = Y1.multiply(Z1).multiply(new BigInteger("2")).mod(ECC.the_p);
		BigInteger Y1_sqrt = Y1.multiply(Y1).mod(ECC.the_p);
		BigInteger S = Y1_sqrt.multiply(X1).multiply(new BigInteger("4")).mod(ECC.the_p);
		BigInteger X3 = M.multiply(M).subtract( S.multiply(new BigInteger("2")) );
		X3 = X3.mod(ECC.the_p);
		BigInteger T = Y1_sqrt.multiply(Y1_sqrt).multiply(new BigInteger("8")).mod(ECC.the_p);
		BigInteger Y3 = S.subtract(X3).multiply(M).subtract(T).mod(ECC.the_p);
		return new ProjPoint(X3, Y3, Z3);
	}
	
	public static ECPoint pointMultiplication(BigInteger d, ECPoint ecp) {
		ArrayList<Integer> list = ECC.getCanonicalVector(d);
		assert list != null;
		// use recording binary method
		ProjPoint pp = new ProjPoint(ecp);
		ProjPoint resultPoint;
		Integer pos_one = new Integer(1);
		Integer neg_one = new Integer(-1);
		if (list.get(list.size()-1).compareTo(pos_one) == 0) {
			resultPoint = new ProjPoint(ecp);
		}
		else {
			resultPoint = ProjPoint.the_Infinity;
		}
		
		for (int i = list.size()-2; i >= 0; i--) {
			resultPoint = projectiveDoubling(resultPoint);
			
			if (list.get(i).compareTo(pos_one) == 0) {
				resultPoint = projectiveAddition(resultPoint, pp);
			}
			else if (list.get(i).compareTo(neg_one) == 0){
				resultPoint = projectiveAddition(resultPoint, pp.negate());
			}
		}
		return resultPoint.getAffineECPoint(ECC.the_p);
	}

	public static void test(BigInteger d) {
		long startTime, endTime, duration;
		ECPoint resultP;

		startTime = System.nanoTime();
		resultP = ProjPoint.pointMultiplication(d, ECC.the_Point);
		endTime = System.nanoTime();
		duration = endTime - startTime;
		System.out.println("Projective");
		System.out.println("result = " + resultP);
		System.out.println("nanoTime = " + duration);	
		System.out.println();
	}
}
