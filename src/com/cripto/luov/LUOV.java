
package com.cripto.luov;

import com.cripto.utils.KeyPair;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.params.DESParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.pqc.math.linearalgebra.GF2mField;
import org.bouncycastle.util.encoders.Hex;

/**
 * LUOV Cryptosystem Class
 * @author Eduardo Angulo
 * @author Sebastián Cabarcas
 * @author Andrés Duarte
 * @author Jorge Pinzón
 */
public class LUOV {
    
    //Parameters
    public static final int FIELD = 7;
    public static final int OIL_VAR = 57;
    public static final int VINEGAR_VAR = 197;
    
    private KeyPair keyPair;
    
    /**
     * Constructor Method.
     */
    public LUOV() {
        this.keyPair = new KeyPair();
    }
    
    /**
     * Generates a Key Pair (Private Key, Public Key).
     * <p>
     * Private Key = private_seed.
     * <p>
     * Public Key = (public_seed, Q2).
     * @throws java.lang.Exception
     */
    public void keyGen() throws Exception {
        String private_seed = generatePrivateSeed();
        String public_seed = generatePublicSeed(private_seed);
        String T = generateT(private_seed);
        ArrayList<String> CLQ1 = generateCLQ1(public_seed);
        String Q2 = packQ2(findQ2(CLQ1.get(2), T));
        this.keyPair = new KeyPair(private_seed, public_seed, Q2);
    }
    
    /**
     * Print the Key Pair (Private Key, Public Key).
     */
    public void printKeyPair() {
        System.out.println(keyPair.toString());
    }
    
    /**
     * Generates a pseudo random private key for LUOV cryptosystem.
     * @return Hex String corresponding to the Private Seed.
     * @throws java.lang.Exception
     */
    private String generatePrivateSeed() throws Exception {
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
    private String generatePublicSeed(String private_seed) throws Exception {
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
    private String generateT(String private_seed) throws Exception {
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
    
    /**
     * Generates a matrix of 0s and 1s from T Hex String.
     * @param T Matrix T String in hexagesimal.
     * @return Integer (Binary) T Matrix.
     */
    private int[][] getTMatrix(String T) {
        int[][] T_matrix = new int[VINEGAR_VAR][OIL_VAR];
        for (int i = 0; i < VINEGAR_VAR; i++) {
            String hex = T.substring(16*i, 16*(i+1));
            String bin = padding((new BigInteger(hex, 16)).toString(2), OIL_VAR);
            for (int j = 0; j < OIL_VAR; j++) {
                T_matrix[i][j] = (int) (bin.charAt(j)) - 48;
            }
        }
        return T_matrix;
    }
    
    /**
     * Make padding of a Binary String with 0s.
     * @param binStr Binary String to be padded.
     * @param N Lenght to be padded.
     * @return Padded String.
     */
    private String padding(String binStr, int N) {
        String aux = binStr;
        while(aux.length() < N) {
            aux = "0" + aux;
        }
        return aux;
    }
    
    /**
     * Generates a pseudo random C, L, Q1 using Chacha Engine.
     * @param public_seed Public Seed of LUOV cryptosystem.
     * @return Array that contains C, L, Q1 Hex Strings.
     * @throws java.lang.Exception
     */
    private ArrayList<String> generateCLQ1(String public_seed) throws Exception {
        ArrayList<String> CLQ1 = new ArrayList<>();
        int N = OIL_VAR + VINEGAR_VAR;
        int DIM = (VINEGAR_VAR*(VINEGAR_VAR + 1)/2) + (VINEGAR_VAR * OIL_VAR);
        String C = "";
        String L = "";
        String Q1 = "";
        byte[] initKey = Hex.decode(public_seed);
        String hash = Hex.toHexString(getHash512(Arrays.copyOf(initKey, initKey.length)));
        String processHash = hash + hash + hash + hash;
        byte[] processData = Hex.decode(processHash);
        ChaChaEngine chacha = new ChaChaEngine();
        for (int i = 0; i < OIL_VAR; i++) {
            String nonce = padding("" + i, 8);
            chacha.init(true, new ParametersWithIV(new KeyParameter(initKey), nonce.getBytes()));
            byte[] resultData = new byte[512];
            chacha.processBytes(processData, 0, processData.length, resultData, 0);
            processData = Arrays.copyOf(resultData, resultData.length);
            //For C
            byte[] aux_C = Arrays.copyOfRange(resultData, 0, 1);
            for (int j = 0; j < aux_C.length; j++) {
                if(aux_C[j] < 0) {
                    aux_C[j] = (byte) (128 + aux_C[j]);
                }
            }
            C += Hex.toHexString(aux_C);
            //For L
            byte[] aux_L = Arrays.copyOfRange(resultData, 1, N+1);
            for (int j = 0; j < aux_L.length; j++) {
                if(aux_L[j] < 0) {
                    aux_L[j] = (byte) (128 + aux_L[j]);
                }
            }
            L += Hex.toHexString(aux_L);
            //For Q1
            byte[] aux_Q1 = Arrays.copyOfRange(resultData, N+1, resultData.length);
            for (int j = 0; j < aux_Q1.length; j++) {
                if(aux_Q1[j] < 0) {
                    aux_Q1[j] = (byte) (128 + aux_Q1[j]);
                }
            }
            Q1 += Hex.toHexString(aux_Q1);
        }
        for (int i = OIL_VAR; i < OIL_VAR*(OIL_VAR + 4); i++) {
            String nonce = padding("" + i, 8);
            chacha.init(true, new ParametersWithIV(new KeyParameter(initKey), nonce.getBytes()));
            byte[] resultData = new byte[512];
            chacha.processBytes(processData, 0, processData.length, resultData, 0);
            processData = Arrays.copyOf(resultData, resultData.length);
            //For Q1
            byte[] aux_Q1 = Arrays.copyOf(resultData, resultData.length);
            for (int j = 0; j < aux_Q1.length; j++) {
                if(aux_Q1[j] < 0) {
                    aux_Q1[j] = (byte) (128 + aux_Q1[j]);
                }
            }
            Q1 += Hex.toHexString(aux_Q1);
        }
        Q1 = Q1.substring(0, 2*OIL_VAR*DIM);
        CLQ1.add(C);
        CLQ1.add(L);
        CLQ1.add(Q1);
        return CLQ1;
    }
    
    /**
     * Create the SHA-512 Hash of given data.
     * @param data Data to be hashed.
     * @return Sha512 Hash of data.
     */
    private byte[] getHash512(byte[] data) {
        SHA512Digest digest = new SHA512Digest();
        digest.update(data, 0, data.length);
        byte[] result = new byte[64];
        digest.doFinal(result, 0);
        return result;
    }
    
    /**
     * Generates a vector of elements in GF(2^7) from C Hex String.
     * @param C Matrix C String in hexagesimal.
     * @return Integer (GF(2^7) elements) C Vector.
     */
    private int[] getCMatrix(String C) {
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
    private int[][] getLMatrix(String L) {
        int N = OIL_VAR + VINEGAR_VAR;
        int[][] L_matrix = new int[OIL_VAR][N];
        for (int i = 0; i < OIL_VAR; i++) {
            String row = L.substring(2*N*i, 2*N*(i+1));
            for (int j = 0; j < N; j++) {
                String hex = row.substring(2*j, 2*(j+1));
                L_matrix[i][j] = (new BigInteger(hex, 16)).intValue();
            }
        }
        return L_matrix;
    }
    
    /**
     * Generates a matrix of elements in GF(2^7) from Q1 Hex String.
     * @param Q1 Matrix Q1 String in hexagesimal.
     * @return Integer (GF(2^7) elements) Q1 Matrix.
     */
    private int[][] getQ1Matrix(String Q1) {
        int COLUMNS = (VINEGAR_VAR*(VINEGAR_VAR + 1)/2) + (VINEGAR_VAR * OIL_VAR);
        int[][] Q1_matrix = new int[OIL_VAR][COLUMNS];
        for (int i = 0; i < OIL_VAR; i++) {
            String row = Q1.substring(2*COLUMNS*i, 2*COLUMNS*(i+1));
            for (int j = 0; j < COLUMNS; j++) {
                String hex = row.substring(2*j, 2*(j+1));
                Q1_matrix[i][j] = (new BigInteger(hex, 16)).intValue();
            }
        }
        return Q1_matrix;
    }
    
    /**
     * Calculates the second part of the quadratic part of the Public Map.
     * @param Q1 Matrix Q1 Hex String
     * @param T Matrix T Hex String
     * @return Q2 Matrix
     */
    private int[][] findQ2(String Q1, String T) {
        int DIM = OIL_VAR*(OIL_VAR + 1)/2;
        int[][] Q1_matrix = getQ1Matrix(Q1);
        int[][] T_matrix = getTMatrix(T);
        int[][] Q2 = new int[OIL_VAR][DIM];
        for (int k = 0; k < OIL_VAR; k++) {
            int[][] Pk1 = findPk1(k, Q1_matrix);
            int[][] Pk2 = findPk2(k, Q1_matrix);
            int[][] Pk3 = findPk3(T_matrix, Pk1, Pk2);
            int column = 0;
            for (int i = 0; i < OIL_VAR; i++) {
                Q2[k][column] = Pk3[i][i];
                column++;
                for (int j = i+1; j < OIL_VAR; j++) {
                    Q2[k][column] = XOR(Pk3[i][j], Pk3[j][i]);
                    column++;
                }
            }
        }
        return Q2;
    }
    
    /**
     * Generates the Hex String of Matrix Q2.
     * @param Q2 Integer GF(2^7) Q2 Matrix (Quadratic part of Public Map).
     * @return Hex String of Q2.
     */
    private String packQ2(int[][] Q2) {
        String Q2_hex = "";
        for (int i = 0; i < Q2.length; i++) {
            for (int j = 0; j < Q2[0].length; j++) {
                Q2_hex += padding((new BigInteger("" + Q2[i][j])).toString(16), 2);
            }
        }
        return Q2_hex;
    }
    
    /**
     * Calculates the part of Pk that is quadratic in vinegar variables.
     * @param k Iteration Number.
     * @param Q1 Integer GF(2^7) Q1 Matrix (Quadratic part of Public Map). 
     * @return Pk part Pk1.
     */
    private int[][] findPk1(int k, int[][] Q1) {
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
     * Calculates the part of Pk that is bilinear in vinegar and oil variables.
     * @param k Iteration Number.
     * @param Q1 Integer GF(2^7) Q1 Matrix (Quadratic part of Public Map).
     * @return Pk part Pk2.
     */
    private int[][] findPk2(int k, int[][] Q1) {
        int[][] Pk2 = new int[VINEGAR_VAR][OIL_VAR];
        int column = 0;
        for (int i = 0; i < VINEGAR_VAR; i++) {
            column +=  (VINEGAR_VAR - (i+1) + 1);
            for (int j = 0; j < OIL_VAR; j++) {
                Pk2[i][j] = Q1[k][column];
                column++;
            }
        }
        return Pk2;
    }
    
    /**
     * Calculates the last part of Pk using the formula
     * Pk3 = -Tt*Pk1*T + Tt*Pk2 over GF(2^7).
     * @param T T Matrix.
     * @param Pk1 Pk1 Matrix.
     * @param Pk2 Pk2 Matrix.
     * @return Pk part Pk3.
     */
    private int[][] findPk3(int[][] T, int[][] Pk1, int[][] Pk2) {
        int[][] T_transposed = transposeMatrix(T);
        int[][] first = matrixMult(matrixMult(T_transposed, Pk1), T);
        int[][] second = matrixMult(T_transposed, Pk2);
        int[][] Pk3 = matrixAdd(first, second);
        return Pk3;
    }
    
    /**
     * Calculates XOR between two numbers x and y.
     * @param x First Integer.
     * @param y Second Integer.
     * @return XOR between x and y.
     */
    private int XOR(int x, int y) {
        return (x | y) & (~x | ~y);
    }
    
    /**
     * Calculates Matrix XOR Add over GF(2^7).
     * @param mat1 First Matrix.
     * @param mat2 Second Matrix.
     * @return Result Matrix.
     */
    private int[][] matrixAdd(int[][] mat1, int[][] mat2) {
        int[][] result = new int[mat1.length][mat1[0].length];
        for (int i = 0; i < mat1.length; i++) {
            for (int j = 0; j < mat1[0].length; j++) {
                result[i][j] = XOR(mat1[i][j], mat2[i][j]);
            }
        }
        return result;
    }
    
    /**
     * Calculates Matrix multiplication over GF(2^7).
     * @param mat1 First Matrix.
     * @param mat2 Second Matrix.
     * @return Result Matrix.
     */
    private int[][] matrixMult(int[][] mat1, int[][] mat2) {
        GF2mField field = new GF2mField(FIELD, 131);
        int[][] result = new int[mat1.length][mat2[0].length];
        for (int i = 0; i < mat1.length; i++) {
            for (int j = 0; j < mat2[0].length; j++) {
                for (int k = 0; k < mat1[0].length; k++) {
                    result[i][j] = XOR(result[i][j], field.mult(mat1[i][k], mat2[k][j]));
                }
            }
        }
        return result;
    }
    
    /**
     * Calculates the transpose of a Matrix.
     * @param mat Matrix to be transposed.
     * @return Transposed Matrix.
     */
    private int[][] transposeMatrix(int[][] mat) {
        int[][] result = new int[mat[0].length][mat.length];
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                result[j][i] = mat[i][j];
            }
        }
        return result;
    }
    
}
