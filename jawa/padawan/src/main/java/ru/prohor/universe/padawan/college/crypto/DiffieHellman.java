package ru.prohor.universe.padawan.college.crypto;

import java.math.BigInteger;

public class DiffieHellman {
    public static int getKey(int a, int b, int g, int p) {
        return CryptoMath.fastModuloPow(CryptoMath.fastModuloPow(g, b, p), a, p);
    }

    public static BigInteger getKey(BigInteger a, BigInteger b, BigInteger g, BigInteger p) {
        return g.modPow(b, p).modPow(a, p);
    }

    public static BigInteger getKey(int a, int b, BigInteger g, BigInteger p) {
        return getKey(BigInteger.valueOf(a), BigInteger.valueOf(b), g, p);
    }
}
