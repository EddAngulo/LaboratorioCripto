
package com.cripto.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.params.DESParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
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
     * @return T matrix String.
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
            ciphData1 = Arrays.copyOf(ciphResult1, ciphResult1.length); //Se podria cruzar
            ciphData2 = Arrays.copyOf(ciphResult2, ciphResult2.length); //Se podria cruzar
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
    
    /**
     * Generates a matrix of 0s and 1s from T Hex String.
     * @param T Matrix T String in hexagesimal.
     * @return Integer (Binary) T Matrix.
     */
    public static int[][] getTMatrix(String T) {
        int[][] T_matrix = new int[VINEGAR_VAR][OIL_VAR];
        for (int i = 0; i < VINEGAR_VAR; i++) {
            String hex = T.substring(16*i, 16*(i+1));
            String bin = padding((new BigInteger(hex, 16)).toString(2));
            for (int j = 0; j < OIL_VAR; j++) {
                T_matrix[i][j] = (int) (bin.charAt(j)) - 48;
            }
        }
        return T_matrix;
    }
    
    /**
     * Make padding of a Binary String with 0s.
     * @param binStr Binary String to be padded.
     * @return Padded String.
     */
    public static String padding(String binStr) {
        String aux = binStr;
        while(aux.length() < OIL_VAR) {
            aux = "0" + aux;
        }
        return aux;
    }
    
    /**
     * 
     * @param public_seed
     * @throws java.lang.Exception
     */
    public static void generateCLQ1(String public_seed) throws Exception {
        byte[] initKey = Hex.decode(public_seed);
        ChaChaEngine chacha = new ChaChaEngine();
        chacha.init(true, new ParametersWithIV(new KeyParameter(initKey), new byte[16]));
    }
    
    /**
     * Generates a vector of elements in GF(2^7) from C Hex String.
     * @param C Matrix C String in hexagesimal.
     * @return Integer (GF(2^7) elements) C Vector.
     */
    public static int[] getCMatrix(String C) {
        int[] C_matrix = new int[OIL_VAR];
        for (int i = 0; i < OIL_VAR; i++) {
            String hex = C.substring(2*i, 2*(i+1));
            C_matrix[i] = (new BigInteger(hex, 16)).intValue();
        }
        return C_matrix;
    }
    
    /**
     * Generates a matrix of elements in GF(2^7) from L Hex String.
     * @param L Matrix L String in hexagesimal.
     * @return Integer (GF(2^7) elements) L Matrix.
     */
    public static int[][] getLMatrix(String L) {
        int N = OIL_VAR + VINEGAR_VAR;
        int[][] L_matrix = new int[OIL_VAR][N];
        for (int i = 0; i < N; i++) {
            String row = L.substring(2*N*i, 2*N*(i+1));
            for (int j = 0; j < OIL_VAR; j++) {
                String hex = row.substring(2*j, 2*(j+1));
                L_matrix[i][j] = (new BigInteger(hex, 16)).intValue();
            }
        }
        return L_matrix;
    }
    
    /**
     * Generates a matrix of elements in GF(2^7) from Q1 Hex String.
     * @param Q1 Matrix C String in hexagesimal.
     * @return Integer (GF(2^7) elements) Q1 Matrix.
     */
    public static int[][] getQ1Matrix(String Q1) {
        int COLUMNS = (VINEGAR_VAR*(VINEGAR_VAR + 1)/2) + (VINEGAR_VAR * OIL_VAR);
        int[][] Q1_matrix = new int[OIL_VAR][COLUMNS];
        for (int i = 0; i < COLUMNS; i++) {
            String row = Q1.substring(2*COLUMNS*i, 2*COLUMNS*(i+1));
            for (int j = 0; j < OIL_VAR; j++) {
                String hex = row.substring(2*j, 2*(j+1));
                Q1_matrix[i][j] = (new BigInteger(hex, 16)).intValue();
            }
        }
        return Q1_matrix;
    }
    
    /**
     * 
     * @param Q1 
     * @param T 
     * @return 
     */
    public static int[][] findQ2(String Q1, String T) {
        int DIM = OIL_VAR*(OIL_VAR + 1)/2;
        int[][] Q1_matrix = getQ1Matrix(Q1);
        int[][] T_matrix = getTMatrix(T);
        int[][] Q2 = new int[OIL_VAR][DIM];
        for (int k = 0; k < OIL_VAR; k++) {
            int[][] Pk1 = findPk1(k, Q1_matrix);
            int[][] Pk2 = findPk2(k, Q1_matrix);
            int[][] Pk3 = new int[OIL_VAR][OIL_VAR]; //Operacion qlera
            int column = 0;
            for (int i = 0; i < OIL_VAR; i++) {
                Q2[k][column] = Pk3[i][i];
                column++;
                for (int j = i+1; j < OIL_VAR; j++) {
                    Q2[k][column] = XOR(Pk3[i][j], Pk3[j][i]); //Pk3[i][j] + Pk3[j][i]???
                    column++;
                }
            }
        }
        return Q2;
    }
    
    /**
     * Find the part of Pk that is quadratic in vinegar variables.
     * @param k Iteration Number.
     * @param Q1 Integer GF(2^7) Q1 Matrix (Quadratic part of Public Map). 
     * @return Pk part Pk1.
     */
    public static int[][] findPk1(int k, int[][] Q1) {
        int[][] Pk1 = new int[VINEGAR_VAR][VINEGAR_VAR];
        int column = 0;
        for (int i = 0; i < VINEGAR_VAR; i++) {
            for (int j = i; j < VINEGAR_VAR; j++) {
                Pk1[i][j] = Q1[k][column];
                column++;
            }
            column += OIL_VAR;
        }
        return Pk1;
    }
    
    /**
     * Find the part of Pk that is bilinear in vinegar and oil variables.
     * @param k Iteration Number.
     * @param Q1 Integer GF(2^7) Q1 Matrix (Quadratic part of Public Map).
     * @return Pk part Pk2.
     */
    public static int[][] findPk2(int k, int[][] Q1) {
        int[][] Pk2 = new int[VINEGAR_VAR][OIL_VAR];
        int column = 0;
        for (int i = 0; i < VINEGAR_VAR; i++) {
            column += (VINEGAR_VAR - i + 1);
            for (int j = 0; j < OIL_VAR; j++) {
                Pk2[i][j] = Q1[k][column];
                column++;
            }
        }
        return Pk2;
    }
    
    /**
     * Calculates XOR between two numbers x and y.
     * @param x First Integer.
     * @param y Second Integer.
     * @return XOR between x and y.
     */
    public static int XOR(int x, int y) {
        return (x | y) & (~x | ~y);
    }
    
}
