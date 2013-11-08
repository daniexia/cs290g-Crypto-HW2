/* Author: Liang Xia, UC Santa Barbara, May 11, 2013
 * Description: EC Point class in affine coordinate system
 */

import java.math.*;
import java.util.ArrayList;
import java.util.Random;

public class ECPoint { // Affine coordinate system
	public BigInteger x;
	public BigInteger y;
	public boolean isInfinity;
	public static final ECPoint the_Infinity = new ECPoint(true);
	
	public ECPoint(){
		x = new BigInteger("0");
		y = new BigInteger("0");
		isInfinity = false;
	}
	
	public ECPoint(BigInteger x1, BigInteger y1){
		x = x1;
		y = y1;
		isInfinity = false;
	}

	public ECPoint(boolean infinity) {
		assert infinity == true;
		isInfinity = true;		
	}
	
	public ECPoint negate() {
		ECPoint point = new ECPoint();
		point.x = x;
		point.y = y.negate();
		return point;
	}
	
	public String toString() {
		if (isInfinity == false)
			return "(" + x + ", " + y + ")";
		else
			return "(The Infinity Point)";
	}
	
	public static ECPoint pointAddition(ECPoint p1, ECPoint p2) {
		if (p1.isInfinity == true)
			return p2;
		else if (p2.isInfinity == true)
			return p1;
		else if (p1.x.compareTo(p2.x) == 0 && 
				//p1.y.compareTo(p2.y.negate()) == 0
				p1.y.add(p2.y).mod(ECC.the_p).compareTo(BigInteger.ZERO) == 0
				) {
			return ECPoint.the_Infinity;
		}
		else {
			// normal addition
			BigInteger m = computeSlope(p1, p2);
			BigInteger x3 = m.multiply(m);
			x3 = x3.subtract(p1.x).subtract(p2.x);
			x3 = x3.mod(ECC.the_p);
			BigInteger y3 = p1.x.subtract(x3);
			y3 = m.multiply(y3);
			y3 = y3.subtract(p1.y);
			y3 = y3.mod(ECC.the_p);
			return new ECPoint(x3, y3);
		}
	}
	
	public static ECPoint pointDoubling(ECPoint p1) {
		return pointAddition(p1, p1);
	}
		
	public static BigInteger computeSlope(ECPoint p1, ECPoint p2) {
		if (0 == p1.x.compareTo(p2.x)) { // if x1==x2
			BigInteger slope = p1.x.multiply(p1.x);
			slope = slope.multiply(new BigInteger("3"));
			slope = slope.add(ECC.the_a);
			BigInteger inverse = p1.y.multiply(new BigInteger("2"));
			inverse = inverse.modInverse(ECC.the_p);
			slope = slope.multiply(inverse);
			slope = slope.mod(ECC.the_p);
			return slope;
		}
		else { // x1 != x2
			BigInteger slope = p2.y.subtract(p1.y);
			BigInteger inverse = p2.x.subtract(p1.x);
			inverse = inverse.modInverse(ECC.the_p);
			slope = slope.multiply(inverse);
			slope = slope.mod(ECC.the_p);
			return slope;
		}
	}
	
	public static ECPoint pointMultiplication(BigInteger d, ECPoint p) {
		ArrayList<Integer> list = ECC.getCanonicalVector(d);
		assert list != null;
		// use recording binary method
		ECPoint resultPoint;
		Integer pos_one = new Integer(1);
		Integer neg_one = new Integer(-1);
		if (list.get(list.size()-1).compareTo(pos_one) == 0) {
			resultPoint = p;
		}
		else {
			resultPoint = ECPoint.the_Infinity;
		}
		
		for (int i = list.size()-2; i >= 0; i--) {			
			resultPoint = pointDoubling(resultPoint);
			
			if (list.get(i).compareTo(pos_one) == 0) {
				resultPoint = pointAddition(resultPoint, p);
			}
			else if (list.get(i).compareTo(neg_one) == 0){
				resultPoint = pointAddition(resultPoint, p.negate());
			}
		}
		return resultPoint;
	}
	
	public static void test(BigInteger d) {
		long startTime, endTime, duration;
		ECPoint resultP;
		
		startTime = System.nanoTime();
		resultP = ECPoint.pointMultiplication(d, ECC.the_Point);
		endTime = System.nanoTime();
		duration = endTime - startTime;
		System.out.println("Affine");
		System.out.println("result = " + resultP);
		System.out.println("nanoTime = " + duration);
		System.out.println();
	}
}
