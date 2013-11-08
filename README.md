cs290g-Crypto-HW2
=================

elliptic curve cryptography

May 11, 2013

1 Introduction

In this homework, I chose GP (p) in NIST curves. It is a 192-bit curve. Below are the parameters of this curve.
p = 6277101735386680763835789423207666416083908700390324961279 
(or 0xffffffff ffffffff ffffffff fffffffe ffffffff ffffffff)
a = -3
b = 0x64210519 e59c80e7 0fa7e9ab 72243049 feb8deec c146b9b1
Generating point p is (Gx, Gy), where
Gx = 0x188da80e b03090f6 7cbf20eb 43a18800 f4ff0afd 82ff1012
Gy = 0x07192b95 ffc8da78 631011ed 6b24cdd5 73f977a1 1e794811
n = 6277101735386680763835789423176059013767194773182842284081
(or 0xffffffff ffffffff ffffffff 99def836 146bc9b1 b4d22831)

2 Implementation

The platform is my laptop with Intel Core i3 processor @ 2.53GHz, 2GB RAM. I implement EC point multiplication [d]P using Java JDK 1.6. The BigInteger library in java is very useful for multi-precision arithmetic. Below is brief summary of each java file.
ECC.java: This file contains all the parameters and constants, the canonical recording algorithm (signed-digit expansion of exponent d) and the final test result.
ECPoint.java: This file contains the definition of points in affine coordinate system and all the operations, including point addition, point doubling, point multiplication and unit tests.
ProjPoint.java: This file contains the definition of points in projective coordinate system. Operations include point addition, doubling and multiplication. It also includes the translation for points in affine and projective coordinate systems.

3 Evaluation of Timing
Since the order of n is 192 bit, its 100%, 75%, and 50% size would be 192 bits, 144 bits and 96 bits.
I chose 192-bit, 144-bit, 96-bit random d and got the following table. For each d, I tested three times in order to get the average execution time. The last column is the performance gain for projective system compared to affine in percentage.

192-bit d
Affine
Projective
saved time
4f617d26e438ac137b45cbc88dbb2a577ecd2081e7fe30e
28.31ms
20.19ms
28.7%
53729dfbaf1e2312577a757511f25f862a278cbabb6d5ed7
28.69ms
20.25ms
29.4%
a16f9a1a9b31e5fc3171d7e8cc76d2610ad877be4874346a
28.97ms
20.11ms
30.5%

144-bit d
Affine
Projective
saved time
9d95bce72610333ce6807b4273b27c0d9ae5
24.72ms
18.08ms
26.9%
41bce5ac66f05b9cd0e6c0c009ce874a2059
24.55ms
18.08ms
26.3%
e2a390e330b3fceaeaa18a0dd453d773ed11
24.57ms
18.32ms
25.43%

96-bit d
Affine
Projective
saved time
f17b91c432f7b57125fc2ed1
21.22ms
14.35ms
32.3%
e26ac5f4719d488e132ea7bc
20.90ms
14.49ms
30.6%
bab3170b42c5f3fe6dd12279
21.57ms
14.35ms
33.4%

From the above tables, we can see projective system has less execution time than affine system for all the bit sizes. For 192-bit d, the saved time is approximately 28~30%. For 144-bit d, the saved time is approx. 25~27%. For 96-bit d, the saved time is approx. 30~33%. It may look a little strange why when decreasing the bit size, the saved time is not proportionally decreased. But obviously the overall gain in performance is more than 25% for the projective system.


