
package com.cripto.luov;

import com.cripto.luov.utils.PRNG;
import com.cripto.luov.utils.SecretMap;
import com.cripto.utils.functions.Functions;
import com.cripto.luov.utils.KeyPair;
import com.cripto.luov.utils.LinearTransformation;
import com.cripto.luov.utils.PrivateKey;
import com.cripto.luov.utils.PublicKey;
import com.cripto.luov.utils.PublicMapParts;
import com.cripto.luov.utils.Signature;
import com.cripto.utils.functions.Pack;
import java.math.BigInteger;
import java.util.Arrays;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.params.DESParameters;
import org.bouncycastle.crypto.params.KeyParameter;
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
    
    private PrivateKey privateKey;
    public PublicKey publicKey;
    private LinearTransformation linearTrans;
    private PublicMapParts publicMapParts;
    
    /**
     * Constructor Method.
     * @throws java.lang.Exception
     */
    public LUOV() throws Exception {
        System.out.println("Initializing LUOV Cryptosystem...");
        this.secretMap = new SecretMap();
        this.keyGen();
        System.out.println("LUOV Cryptosystem Successfully Initialized");
    }
    
    /**
     * Generates a Key Pair (Private Key, Public Key).
     * <p>
     * Private Key = private_seed.
     * </p>
     * <p>
     * Public Key = (publicSeed, Q2).
     * </p>
     * @throws java.lang.Exception
     */
    private void keyGen() throws Exception {
        this.privateKey = new PrivateKey(generatePrivateSeed());
        String publicSeed = generatePublicSeed(privateKey);
        this.linearTrans = generateLinearTransformation(privateKey);
        this.publicMapParts = PRNG.generateCLQ1(publicSeed);
        String Q2 = Pack.pack(findQ2(publicMapParts.getQ1Matrix(), linearTrans));
        this.publicKey = new PublicKey(publicSeed, Q2);
        this.keyPair = new KeyPair(privateKey, publicKey);
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
 publicSeed = Des_k1(k2)||Des_k2(k4)||Des_k3(k1)||Des_k4(k3)
 being ki = private_seed[8*i:8*(i+1)].
 </p>
     * @param privateKey Private seed of LUOV cryptosystem.
     * @return Hex String corresponding to the Public Seed.
     * @throws java.lang.Exception 
     */
    private String generatePublicSeed(PrivateKey privateKey) throws Exception {
        byte[] initKey = privateKey.getPrivateSeedBytes();
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
     * Generates a pseudo random linearTrans matrix using AES Engine.
     * @param privateKey Private seed of LUOV cryptosystem.
     * @return linearTrans matrix String.
     * @throws java.lang.Exception 
     */
    private LinearTransformation generateLinearTransformation(PrivateKey privateKey) throws Exception {
        String T = "";
        byte[] initKey = privateKey.getPrivateSeedBytes();
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
        if(initKey[0] < 0) {
            aes1.processBlock(ciphData2, 0, ciphResult, 0);
        }else {
            aes2.processBlock(ciphData1, 0, ciphResult, 0);
        }
        ciphResult[0] = (byte) (Math.abs((int) ciphResult[0] % 2));
        T += Hex.toHexString(Arrays.copyOfRange(ciphResult, 0, 8));
        return new LinearTransformation(T);
    }
    
    /**
     * Calculates the second part of the quadratic part of the Public Map.
     * @param Q1 Matrix Q1.
     * @param T Matrix LinearTrans.
     * @return Q2 Matrix
     */
    private int[][] findQ2(int[][] Q1, LinearTransformation linearTrans) {
        int DIM = OIL_VAR*(OIL_VAR + 1)/2;
        int[][] T = linearTrans.getTMatrix();
        int[][] Q2 = new int[OIL_VAR][DIM];
        for (int k = 0; k < OIL_VAR; k++) {
            int[][] Pk1 = findPk1(k, Q1);
            int[][] Pk2 = findPk2(k, Q1);
            int[][] Pk3 = findPk3(T, Pk1, Pk2);
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
     * Pk3 = -Tt*Pk1*linearTrans + Tt*Pk2 over GF(2^7).
     * @param T linearTrans Matrix.
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
     * @param T linearTrans matrix.
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
     * @param C C Matrix.
     * @param L L Matrix.
     * @param Q1 Q1 Matrix.
     * @param T LinearTrans Matrix.
     * @param h Int Message Vector over GF(2^r).
     * @param v Random Assign for Vinegar Vars.
     * @return Augmneted Matrix (LHS||RHS).
     */
    private int[][] buildAugmentedMatrix(int[][] C, int[][] L, int[][] Q1, int[][] T, int[][] h, int[][] v) {
        int[][] RHS = Functions.matrixAdd(Functions.matrixAdd(h, C), 
                Functions.matrixMult(FIELD, POLY, L, 
                        Functions.matrixRowUnion(v, 
                                Functions.sameValueMatrix(OIL_VAR, 1, 0))));
        int[][] LHS = Functions.matrixMult(FIELD, POLY, L, 
                Functions.matrixRowUnion(T, Functions.identityMatrix(OIL_VAR)));
        for (int k = 0; k < OIL_VAR; k++) {
            int[][] Pk1 = findPk1(k, Q1);
            int[][] Pk2 = findPk2(k, Q1);
            int[][] temp1 = Functions.matrixMult(FIELD, POLY, 
                    Functions.matrixMult(FIELD, POLY, 
                            Functions.transposeMatrix(v), Pk1), v);
            RHS[k][0] = Functions.XOR(RHS[k][0], temp1[0][0]);
            int[][] Fk2 = Functions.matrixAdd(Functions.matrixMult(FIELD, POLY, 
                    Functions.matrixAdd(Pk1, Functions.transposeMatrix(Pk1)), T), Pk2);
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
    public Signature sign(String M) throws Exception {
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
            int[][] A = buildAugmentedMatrix(publicMapParts.getCMatrix(), 
                    publicMapParts.getLMatrix(), publicMapParts.getQ1Matrix(), 
                    linearTrans.getTMatrix(), h, v);
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
                linearTrans.buildLinearTransMatrix(), s_prime);
        return new Signature(Pack.pack(s), Hex.toHexString(salt));
    }
    
    /**
     * Calculates the Evaluation of s in the Public Map P.
     * <p>
     * For a Given Signature s, calculates P(s) = C + L(s) + Q(s), 
     * with Q = (Q1||Q2).
     * </p>
     * @param publicKey Public Key used for Verification.
     * @param s s Signature Matrix.
     * @return Evaluation of s in the Public Map P, i.e. P(s).
     */
    private int[][] evaluatePublicMap(PublicKey publicKey, int[][] s) {
        int N = OIL_VAR + VINEGAR_VAR;
        int[][] C = publicMapParts.getCMatrix();
        int[][] L = publicMapParts.getLMatrix();
        int[][] Q1 = publicMapParts.getQ1Matrix();
        int[][] Q2 = publicKey.getQ2Matrix();
        int[][] Q = Functions.matrixColumnUnion(Q1, Q2);
        int[][] e = Functions.matrixAdd(C, Functions.matrixMult(FIELD, POLY, L, s));
        int column = 0;
        for (int i = 0; i < N; i++) {
            for (int j = i; j < N; j++) {
                for (int k = 0; k < OIL_VAR; k++) {
                    e[k][0] = Functions.XOR(e[k][0], 
                            Functions.fieldMult(FIELD, POLY, 
                                    Functions.fieldMult(FIELD, POLY, 
                                            Q[k][column], s[i][0]), s[j][0]));
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
     * @param publicKey Public Key used for Verification.
     * @param M Message to be Verified.
     * @param sign Sign to be Verified.
     * @return Verification of a Signature P(s) == h.
     * @throws java.lang.Exception     
     */
    public boolean verify(PublicKey publicKey, String M, Signature sign) throws Exception {
        byte[] zero = {0};
        byte[] salt = sign.getSaltBytes();
        byte[] finalMsg = Functions.concatenateVectors(
                Functions.concatenateVectors(M.getBytes(), zero), salt);
        byte[] hashedMsg = PRNG.getHashDigest(finalMsg, FIELD*OIL_VAR); 
        int[][] h = buildMessageVector(hashedMsg);
        int[][] e = evaluatePublicMap(publicKey, sign.getSMatrix());
        return Functions.matrixEquals(e, h);
    }
    
}
