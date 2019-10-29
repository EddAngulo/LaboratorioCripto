
package com.cripto.luov;

import com.cripto.utils.functions.Functions;
import com.cripto.utils.KeyPair;
import com.cripto.utils.functions.Pack;
import java.math.BigInteger;
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
    public static final int POLY = 131;
    public static final int OIL_VAR = 57;
    public static final int VINEGAR_VAR = 197;
    
    private KeyPair keyPair;
    private SecretMap secretMap;
    
    private String private_seed;
    private String public_seed;
    private String T;
    private String C;
    private String L;
    private String Q1;
    private String Q2;
    
    /**
     * Constructor Method.
     * @throws java.lang.Exception
     */
    public LUOV() throws Exception {
        System.out.println("Initializing LUOV Cryptosystem...");
        this.keyPair = new KeyPair();
        this.secretMap = new SecretMap();
        this.keyGen();
        System.out.println("LUOV Cryptosystem Successfully Initialized");
    }
    
    /**
     * Generates a Key Pair (Private Key, Public Key).
     * <p>
     * Private Key = private_seed.
     * <p>
     * Public Key = (public_seed, Q2).
     * @throws java.lang.Exception
     */
    private void keyGen() throws Exception {
        this.private_seed = generatePrivateSeed();
        this.public_seed = generatePublicSeed(private_seed);
        this.T = generateT(private_seed);
        ArrayList<String> CLQ1 = generateCLQ1(public_seed);
        this.C = CLQ1.get(0);
        this.L = CLQ1.get(1);
        this.Q1 = CLQ1.get(2);
        this.Q2 = Pack.pack(findQ2(Q1, T));
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
        byte[] private_seed = PRNG.randomBytes(32);
        return Hex.toHexString(private_seed);
    }
    
    /**
     * Generates a pseudo random public seed using DES Engine.
     * <p>
     * public_seed = Des_k1(k2)||Des_k2(k4)||Des_k3(k1)||Des_k4(k3)
     * being ki = private_seed[8*i:8*(i+1)].
     * </p>
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
            String bin = Functions.padding((new BigInteger(
                    hex, 16)).toString(2), OIL_VAR);
            for (int j = 0; j < OIL_VAR; j++) {
                T_matrix[i][j] = (int) (bin.charAt(j)) - 48;
            }
        }
        return T_matrix;
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
        String hash = Hex.toHexString(generateInternalHash512(Arrays.copyOf(
                initKey, initKey.length)));
        String processHash = hash + hash + hash + hash;
        byte[] processData = Hex.decode(processHash);
        ChaChaEngine chacha = new ChaChaEngine();
        for (int i = 0; i < OIL_VAR; i++) {
            String nonce = Functions.padding("" + i, 8);
            chacha.init(true, new ParametersWithIV(new KeyParameter(initKey), 
                    nonce.getBytes()));
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
            String nonce = Functions.padding("" + i, 8);
            chacha.init(true, new ParametersWithIV(new KeyParameter(initKey), 
                    nonce.getBytes()));
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
    private byte[] generateInternalHash512(byte[] data) {
        SHA512Digest digest = new SHA512Digest();
        digest.update(data, 0, data.length);
        byte[] result = new byte[64];
        digest.doFinal(result, 0);
        return result;
    }
    
    /**
     * Calculates the second part of the quadratic part of the Public Map.
     * @param Q1 Matrix Q1 Hex String
     * @param T Matrix T Hex String
     * @return Q2 Matrix
     */
    private int[][] findQ2(String Q1, String T) {
        int DIM = OIL_VAR*(OIL_VAR + 1)/2;
        int[][] Q1_matrix = Pack.unpack(Q1, OIL_VAR, 
                (VINEGAR_VAR*(VINEGAR_VAR + 1)/2) + (VINEGAR_VAR * OIL_VAR));
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
                    Q2[k][column] = Functions.XOR(Pk3[i][j], Pk3[j][i]);
                    column++;
                }
            }
        }
        return Q2;
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
        int[][] T_transposed = Functions.transposeMatrix(T);
        int[][] first = Functions.matrixMult(FIELD, POLY,
                Functions.matrixMult(FIELD, POLY, T_transposed, Pk1), T);
        int[][] second = Functions.matrixMult(FIELD, POLY, T_transposed, Pk2);
        int[][] Pk3 = Functions.matrixAdd(first, second);
        return Pk3;
    }
    
    /**
     * Generates the Quadratic Part of Secrets Polynomials.
     * @param T T matrix.
     * @param Pk1 Pk1 Matrix (Quadratic in Vinegar Vars).
     * @param Pk2 Pk2 Matrix (Bilineal in Oil and Vinegar Vars).
     * @return Matrix that represents the Quadratic Part of a Secret Polynomial.
     */
    private int[][] generateSecretPoly(int[][] T, int[][] Pk1, int[][] Pk2) {
        int[][] T_transposed = Functions.transposeMatrix(T);
        int[][] part2 = Functions.matrixAdd(Functions.matrixMult(FIELD, POLY, 
                Pk1, T), Pk2);
        int[][] part3 = Functions.matrixMult(FIELD, POLY, T_transposed, Pk1);
        int[][] part4 = Functions.sameValueMatrix(OIL_VAR, OIL_VAR, 0);
        int[][] upper = Functions.matrixColumnUnion(Pk1, part2);
        int[][] lower = Functions.matrixColumnUnion(part3, part4);
        return Functions.matrixRowUnion(upper, lower);
    }
    
    /**
     * Builds the Linear Transformation Matrix [[1v, T]; [0, 1m]].
     * @param T Hex String of T matrix.
     * @return Linear Transformation Matrix (n x n).
     */
    private int[][] buildLinearTransMatrix(String T) {
        int[][] T_matrix = getTMatrix(T);
        int[][] upper = Functions.matrixColumnUnion(
                Functions.identityMatrix(VINEGAR_VAR), T_matrix);
        int[][] lower = Functions.matrixColumnUnion(
                Functions.sameValueMatrix(OIL_VAR, VINEGAR_VAR, 0), 
                Functions.identityMatrix(OIL_VAR));
        return Functions.matrixRowUnion(upper, lower);
    }
    
    /**
     * Generates the int Message Vector over GF(2^r) from the message byte array
     * after hashing.
     * @param msg Byte Array of the Message after hashing.
     * @return Message Vector over GF(2^r).
     */
    private int[][] buildMessageVector(byte[] msg) {
        int[][] msgVector = new int[OIL_VAR][1];
        String bitString = Functions.padding((new BigInteger(
                Hex.toHexString(msg), 16)).toString(2), FIELD*OIL_VAR);
        for (int i = 0; i < OIL_VAR; i++) {
            String bits = bitString.substring(FIELD*i, FIELD*(i+1));
            msgVector[i][0] = (new BigInteger(bits, 2)).intValue();
        }
        return msgVector;
    }
    
    /**
     * Builds the Augmented Matrix for the Equation System to solve.
     * @param C Hex String of C Matrix.
     * @param L Hex String of L Matrix.
     * @param Q1 Hex String of Q1 Matrix.
     * @param T Hex String of T Matrix.
     * @param h Int Message Vector over GF(2^r).
     * @param v Random Assign for Vinegar Vars.
     * @return Augmneted Matrix (LHS||RHS).
     */
    private int[][] buildAugmentedMatrix(String C, String L, String Q1, String T, int[][] h, int[][] v) {
        int[][] T_matrix = getTMatrix(T);
        int[][] C_matrix = Pack.unpack(C, OIL_VAR, 1);
        int[][] L_matrix = Pack.unpack(L, OIL_VAR, OIL_VAR + VINEGAR_VAR);
        int[][] Q1_matrix = Pack.unpack(Q1, OIL_VAR, 
                (VINEGAR_VAR*(VINEGAR_VAR + 1)/2) + (VINEGAR_VAR * OIL_VAR));
        int[][] RHS = Functions.matrixAdd(Functions.matrixAdd(h, C_matrix), 
                Functions.matrixMult(FIELD, POLY, L_matrix, 
                        Functions.matrixRowUnion(v, 
                                Functions.sameValueMatrix(OIL_VAR, 1, 0))));
        int[][] LHS = Functions.matrixMult(FIELD, POLY, L_matrix, 
                Functions.matrixRowUnion(T_matrix, 
                        Functions.identityMatrix(OIL_VAR)));
        for (int k = 0; k < OIL_VAR; k++) {
            int[][] Pk1 = findPk1(k, Q1_matrix);
            int[][] Pk2 = findPk2(k, Q1_matrix);
            int[][] temp1 = Functions.matrixMult(FIELD, POLY, 
                    Functions.matrixMult(FIELD, POLY, 
                            Functions.transposeMatrix(v), Pk1), v);
            RHS[k][0] = Functions.XOR(RHS[k][0], temp1[0][0]);
            int[][] Fk2 = Functions.matrixAdd(Functions.matrixMult(FIELD, POLY, 
                    Functions.matrixAdd(Pk1, Functions.transposeMatrix(Pk1)), 
                    T_matrix), Pk2);
            int[][] temp2 = Functions.matrixMult(FIELD, POLY, 
                    Functions.transposeMatrix(v), Fk2);
            LHS[k] = Functions.vectorAdd(LHS[k], temp2[0]);
        }
        return Functions.matrixColumnUnion(LHS, RHS);
    }
    
    /**
     * Sign the given Message.
     * @param M Message to be Signed.
     * @return Message Signature (s, salt).
     * @throws java.lang.Exception
     */
    public ArrayList<String> sign(String M) throws Exception {
        ArrayList<String> signResult = new ArrayList<>();
        boolean solutionFound = false;
        int[][] s_prime = null;
        byte[] zero = {0};
        byte[] salt = PRNG.randomBytes(16);
        byte[] finalMsg = Functions.concatenateVectors(
                Functions.concatenateVectors(M.getBytes(), zero), salt);
        byte[] hashedMsg = PRNG.getHashDigest(finalMsg, FIELD*OIL_VAR); 
        int[][] h = buildMessageVector(hashedMsg);
        while(!solutionFound) {
            byte[] vinegarAssign = PRNG.randomBytes(VINEGAR_VAR);
            int[][] v = Functions.bytesToFieldVector(vinegarAssign);
            int[][] A = buildAugmentedMatrix(C, L, Q1, T, h, v);
            int[] oVect = Functions.gaussianElimination(FIELD, POLY, 
                            Functions.equationCoeficients(A), 
                            Functions.equationConstants(A));
            if(oVect != null) {
                int[][] o = Functions.transposeRowVector(oVect);
                solutionFound = true;
                s_prime = Functions.matrixRowUnion(v, o);
            }
        }
        int[][] s = Functions.matrixMult(FIELD, POLY, 
                buildLinearTransMatrix(T), s_prime);
        signResult.add(Pack.pack(s));
        signResult.add(Hex.toHexString(salt));
        return signResult;
    }
    
    /**
     * Calculates the Evaluation of s in the Public Map P.
     * <p>
     * For a Given Signature s, calculates P(s) = C + L(s) + Q(s), 
     * with Q = (Q1||Q2).
     * </p>
     * @param C Hex String of C Matrix.
     * @param L Hex String of L Matrix.
     * @param Q1 Hex String of Q1 Matrix.
     * @param Q2 Hex String of Q2 Matrix.
     * @param s Hex String of s Signature Matrix.
     * @return Evaluation of s in the Public Map P, i.e. P(s).
     */
    private int[][] evaluatePublicMap(String C, String L, String Q1, String Q2, String s) {
        int N = OIL_VAR + VINEGAR_VAR;
        int[][] C_matrix = Pack.unpack(C, OIL_VAR, 1);
        int[][] L_matrix = Pack.unpack(L, OIL_VAR, N);
        int[][] Q1_matrix = Pack.unpack(Q1, OIL_VAR, 
                (VINEGAR_VAR*(VINEGAR_VAR + 1)/2) + (VINEGAR_VAR * OIL_VAR));
        int[][] Q2_matrix = Pack.unpack(Q2, OIL_VAR, OIL_VAR*(OIL_VAR + 1)/2);
        int[][] Q = Functions.matrixColumnUnion(Q1_matrix, Q2_matrix);
        int[][] s_matrix = Pack.unpack(s, N, 1);
        int[][] e = Functions.matrixAdd(C_matrix, 
                Functions.matrixMult(FIELD, POLY, L_matrix, s_matrix));
        int column = 0;
        for (int i = 0; i < N; i++) {
            for (int j = i; j < N; j++) {
                for (int k = 0; k < OIL_VAR; k++) {
                    e[k][0] = Functions.XOR(e[k][0], 
                            Functions.fieldMult(FIELD, POLY, 
                                    Functions.fieldMult(FIELD, POLY, 
                                            Q[k][column], s_matrix[i][0]), 
                                    s_matrix[j][0]));
                }
                column++;
            }
        }
        return e;
    }
    
    /**
     * Verify if a Signature (s, salt) is Valid for a Message M.
     * <p>
     * For a signature (s, salt) verify if the Public Map (P) evaluated in s is
     * equal to Hashed Message M, i.e. P(s) == [h = Hash(M||0x00||salt)].
     * </p>
     * @param M Message to be Verified.
     * @param sign Sign to be Verified.
     * @return Verification of a Signature P(s) == h.
     * @throws java.lang.Exception     
     */
    public boolean verify(String M, ArrayList<String> sign) throws Exception {
        byte[] zero = {0};
        byte[] salt = Hex.decode(sign.get(1));
        byte[] finalMsg = Functions.concatenateVectors(
                Functions.concatenateVectors(M.getBytes(), zero), salt);
        byte[] hashedMsg = PRNG.getHashDigest(finalMsg, FIELD*OIL_VAR); 
        int[][] h = buildMessageVector(hashedMsg);
        int[][] e = evaluatePublicMap(C, L, Q1, Q2, sign.get(0));
        return Functions.matrixEquals(e, h);
    }
    
}
