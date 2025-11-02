package ru.prohor.universe.padawan.college.crypto;

public class CryptoMath {
    public static class EuclideanCoefficients {
        public int i;
        public int j;

        public EuclideanCoefficients(int i, int j) {
            this.i = i;
            this.j = j;
        }

        EuclideanCoefficients() {}
    }

    public static EuclideanCoefficients recursiveExtendedEuclideanAlgorithm(int m, int n) {
        if (n == 0)
            return new EuclideanCoefficients(1, 0);
        EuclideanCoefficients cfs = recursiveExtendedEuclideanAlgorithm(n, m % n);
        return new EuclideanCoefficients(cfs.j, cfs.i - cfs.j * (m / n));
    }

    public static EuclideanCoefficients nonRecursiveExtendedEuclideanAlgorithm(int a, int b) {
        EuclideanCoefficients cfs = new EuclideanCoefficients();
        if (b == 0) {
            cfs.i = 1;
            cfs.j = 0;
            return cfs;
        }
        long x1 = 0, x2 = 1, y1 = 1, y2 = 0;
        while (b > 0) {
            long q = a / b;
            long r = a - q * b;
            cfs.i = (int) (x2 - q * x1);
            cfs.j = (int) (y2 - q * y1);
            a = b;
            b = (int) r;
            x2 = x1;
            x1 = cfs.i;
            y2 = y1;
            y1 = cfs.j;
        }
        cfs.i = (int) x2;
        cfs.j = (int) y2;
        return cfs;
    }

    /**
     * Euclidean algorithm. Greatest Common Divisor
     */
    public static int gcd(int a, int b) {
        while (a != 0 && b != 0) {
            if (a > b)
                a = a % b;
            else
                b = b % a;
        }
        return a + b;
    }

    public static int fastModuloPow(long base, int exponent, int modulus) {
        if (modulus == 1)
            return 0;
        long result = 1;
        while (exponent > 0) {
            base = base % modulus;
            if ((exponent & 1) == 1)
                result = result * base % modulus;
            exponent >>>= 1;
            base = base * base;
        }
        return (int) result;
    }

    public static boolean isPrimeSlow(int n) {
        if (n <= 1)
            return false;
        if (n <= 3)
            return true;
        if (n % 2 == 0 || n % 3 == 0)
            return false;
        int sqrt = (int) Math.sqrt(n) + 1;
        for (int i = 5; i <= sqrt; i += 6)
            if (n % i == 0 || n % (i + 2) == 0)
                return false;
        return true;
    }
}
