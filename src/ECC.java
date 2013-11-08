/* Author: Liang Xia, UC Santa Barbara, May 11, 2013
 * Description: Implementation of a 192 bit Elliptical Curve point multiplication
 * Curve Parameters:
 *     Modulus p = 6277101735386680763835789423207666416083908700390324961279 
 *               = 0xfffffffffffffffffffffffffffffffeffffffffffffffff
 *   Parameter a = -3
 *             b = 0x64210519e59c80e70fa7e9ab72243049feb8deecc146b9b1
 *   Generating point p is (Gx, Gy), where
 *            Gx = 0x188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012
 *            Gy = 0x07192b95ffc8da78631011ed6b24cdd573f977a11e794811
 *   Order of this point
 *             n = 0xffffffffffffffffffffffff99def836146bc9b1b4d22831
 * */

import java.math.*;
import java.util.*;

public class ECC {

	// y^2 = x^3 + ax + b (mod p)
	public static final int bitsizes = 192;
	public static final int bitsizes75 = 144;
	public static final int bitsizes50 = 96;
	
	public static final BigInteger the_a = new BigInteger("-3");
	public static final BigInteger the_b = new BigInteger("64210519e59c80e70fa7e9ab72243049feb8deecc146b9b1", 16);
	public static final BigInteger the_p = new BigInteger("fffffffffffffffffffffffffffffffeffffffffffffffff", 16);
	public static final BigInteger the_Gx = new BigInteger("188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012", 16);
	public static final BigInteger the_Gy = new BigInteger("07192b95ffc8da78631011ed6b24cdd573f977a11e794811", 16);
	//public static final BigInteger the_d = new BigInteger(bitsizes, new Random());
	public static final ECPoint the_Point = new ECPoint(the_Gx, the_Gy);
	public static final BigInteger the_c = new BigInteger("3099d2bbbfcb2538542dcd5fb078b6ef5f3d6fe2c745de65", 16);
	public static final BigInteger the_n = new BigInteger("ffffffffffffffffffffffff99def836146bc9b1b4d22831", 16);
	public static final BigInteger the_n_1 = the_n.subtract(new BigInteger("1"));
	public static final BigInteger the_2_inv = new BigInteger("2").modInverse(the_p);
	
		
	public static ArrayList<Integer> getCanonicalVector(BigInteger d) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		boolean ci = false;
		boolean ei, ei2;
		int fi;  // -1, 0, 1
		int i;
		for (i = 0; i < d.bitLength()-1; i++) {
			ei = d.testBit(i);
			ei2 = d.testBit(i+1);
			if (false == ci) {
				if (false == ei2 && false == ei) {	   ci = false; fi = 0;  }
				else if (false == ei2 && true == ei) { ci = false; fi = 1;  }
				else if (true == ei2 && false == ei) { ci = false; fi = 0;  }
				else /*true == ei2 && true == ei)*/  { ci = true;  fi = -1; }
			}
			else {
				if (false == ei2 && false == ei) {	   ci = false; fi = 1;  }
				else if (false == ei2 && true == ei) { ci = true;  fi = 0;  }
				else if (true == ei2 && false == ei) { ci = true;  fi = -1; }
				else /*true == ei2 && true == ei)*/  { ci = true;  fi = 0;  }
			}
			list.add(new Integer(fi));
		}
		assert d.testBit(i) == true;
		if (false == ci) {
			fi = 1;
			list.add(new Integer(fi));
			list.add(new Integer(0));
		}
		else {
			list.add(new Integer(0));
			list.add(new Integer(1));
		}
		return list;
	}

	public static void test1() {
		long startTime, endTime, duration;
		ECPoint resultP;
		BigInteger d1 = new BigInteger("4f617d26e438ac137b45cbc88dbb2a577ecd2081e7fe30e", 16);
		//BigInteger d1 = the_d;
		System.out.println("d1 = " + d1.toString(16));

		startTime = System.nanoTime();
		resultP = ProjPoint.pointMultiplication(d1, ECC.the_Point);
		endTime = System.nanoTime();
		duration = endTime - startTime;
		System.out.println("Projective");
		System.out.println("result = " + resultP);
		System.out.println("nanoTime = " + duration);	
		System.out.println();
		
		startTime = System.nanoTime();
		resultP = ECPoint.pointMultiplication(d1, ECC.the_Point);
		endTime = System.nanoTime();
		duration = endTime - startTime;
		System.out.println("Affine");
		System.out.println("result = " + resultP);
		System.out.println("nanoTime = " + duration);
		System.out.println();

	}
	
	public static void main(String[] args) {
		//BigInteger d = new BigInteger(ECC.bitsizes, new Random());
		//System.out.println("d = " + d.toString(16));
		BigInteger d = new BigInteger("4f617d26e438ac137b45cbc88dbb2a577ecd2081e7fe30e", 16);
		ECPoint.test(d);
		//ProjPoint.test(d);
	}
}
