
package com.cripto.utils;

import java.security.SecureRandom;
import java.util.Arrays;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.params.DESParameters;
import org.bouncycastle.util.encoders.Hex;

/**
 *
 * @author Eduardo Angulo
 */
public class Utils {
    
    /**
     * Generates a pseudo random private key for LUOV cryptosystem.
     * @return Hex String corresponding to the Private Seed.
     * @throws java.lang.Exception
     */
    public static String generatePrivateSeed() throws Exception {
        byte[] private_seed = new byte[32];
        SecureRandom.getInstanceStrong().nextBytes(private_seed);
        return Hex.toHexString(private_seed);
    }
    
    /**
     * Generates a pseudo random public seed using DES Engine.
     * public_seed = Des_k1(k2)||Des_k2(k4)||Des_k3(k1)||Des_k4(k3)
     * being ki = private_seed[8*i:8*(i+1)].
     * @param private_seed Private seed of LUOV cryptosystem.
     * @return Hex String corresponding to the Public Seed.
     * @throws java.lang.Exception 
     */
    public static String generatePublicSeed(String private_seed) throws Exception {
        byte[] initKey = Hex.decode(private_seed);
        DESEngine des = new DESEngine();
        String public_seed = "";
        for (int i = 0; i < 4; i++) {
            int ciphIndex = 2*(i+1) % 5 - 1;
            byte[] partialKey = Arrays.copyOfRange(initKey, 8*i, 8*(i+1));
            des.init(true, new DESParameters(partialKey));
            byte[] ciphData = Arrays.copyOfRange(initKey, 8*ciphIndex, 8*(ciphIndex+1));
            byte[] ciphResult = new byte[8];
            des.processBlock(ciphData, 0, ciphResult, 0);
            public_seed += Hex.toHexString(ciphResult);
        }
        return public_seed;
    }
    
    /**
     * 
     * @param private_seed Private seed of LUOV cryptosystem.
     * @return 
     * @throws java.lang.Exception 
     */
    public static String generateT(String private_seed) throws Exception {
        return "";
    }
    
}
