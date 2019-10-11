
package com.cripto.utils;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.params.DESParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;

/**
 *
 * @author Eduardo Angulo
 */
public class Utils {
    
    //Parameters
    public static final int FIELD = 7;
    public static final int OIL_VAR = 57;
    public static final int VINEGAR_VAR = 197;
    
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
     * <p>
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
     * Generates a pseudo random T matrix using AES Engine.
     * @param private_seed Private seed of LUOV cryptosystem.
     * @return T matrix String
     * @throws java.lang.Exception 
     */
    public static String generateT(String private_seed) throws Exception {
        String T = "";
        byte[] initKey = Hex.decode(private_seed);
        byte[] partialKey1 = Arrays.copyOfRange(initKey, 0, 16);
        byte[] partialKey2 = Arrays.copyOfRange(initKey, 16, 32);
        AESEngine aes1 = new AESEngine();
        AESEngine aes2 = new AESEngine();
        aes1.init(true, new KeyParameter(partialKey2));
        aes2.init(true, new KeyParameter(partialKey1));
        byte[] ciphData1 = Arrays.copyOfRange(initKey, 0, 16);
        byte[] ciphData2 = Arrays.copyOfRange(initKey, 16, 32);
        for (int i = 0; i < (int) (VINEGAR_VAR/4); i++) {
            byte[] ciphResult1 = new byte[16];
            byte[] ciphResult2 = new byte[16];
            aes1.processBlock(ciphData1, 0, ciphResult1, 0);
            aes2.processBlock(ciphData2, 0, ciphResult2, 0);
            ciphData1 = Arrays.copyOf(ciphResult1, ciphResult1.length);
            ciphData2 = Arrays.copyOf(ciphResult2, ciphResult2.length);
            ciphResult1[0] = (byte) (Math.abs((int) ciphResult1[0] % 2));
            ciphResult1[8] = (byte) (Math.abs((int) ciphResult1[8] % 2));
            ciphResult2[0] = (byte) (Math.abs((int) ciphResult2[0] % 2));
            ciphResult2[8] = (byte) (Math.abs((int) ciphResult2[8] % 2));
            T += Hex.toHexString(ciphResult1);
            T += Hex.toHexString(ciphResult2);
        }
        byte[] ciphResult = new byte[16];
        Random random = new Random(System.nanoTime());
        int rnd = random.nextInt((1000 - 1) + 1) + 1;
        if(rnd % 2 == 0) {
            aes1.processBlock(ciphData2, 0, ciphResult, 0);
        }else {
            aes2.processBlock(ciphData1, 0, ciphResult, 0);
        }
        ciphResult[0] = (byte) (Math.abs((int) ciphResult[0] % 2));
        T += Hex.toHexString(Arrays.copyOfRange(ciphResult, 0, 8));
        return T;
    }
    
}
