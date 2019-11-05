
package com.cripto.luov.utils;

import static com.cripto.luov.LUOV.OIL_VAR;
import static com.cripto.luov.LUOV.VINEGAR_VAR;
import com.cripto.utils.functions.Functions;
import java.security.SecureRandom;
import java.util.Arrays;
import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Hex;

/**
 * PRNG Class.
 * @author Eduardo Angulo
 * @author Sebastián Cabarcas
 * @author Andrés Duarte
 * @author Jorge Pinzón
 */
public class PRNG {
    
    /**
     * Generates a Random Byte Array of the Given Length.
     * @param length Array Length.
     * @return Random Byte Array.
     * @throws java.lang.Exception     
     */
    public static byte[] randomBytes(int length) throws Exception {
        byte[] rnd = new byte[length];
        SecureRandom.getInstanceStrong().nextBytes(rnd);
        return rnd;
    }
    
    /**
     * Hash Given Data using Keccak512 taking only the Required Bits.
     * @param data Data to be Hashed.
     * @param bitLength Required Bit Length.
     * @return Hashed Byte Array.
     */
    public static byte[] getHashDigest(byte[] data, double bitLength) {
        int byteLength = (int) (Math.ceil(bitLength/8));
        KeccakDigest digest = new KeccakDigest(512);
        digest.update(data, 0, data.length);
        byte[] afterHash = new byte[64];
        digest.doFinal(afterHash, 0);
        byte[] result = Arrays.copyOf(afterHash, byteLength);
        if(result[0] < 0) {
            result[0] = (byte) (128 + result[0]);
        }
        return result;
    }
    
    /**
     * Generates a pseudo random C, L, Q1 using Chacha Engine.
     * @param public_seed Public Seed of LUOV cryptosystem.
     * @return Array that contains C, L, Q1 Hex Strings.
     * @throws java.lang.Exception
     */
    public static PublicMapParts generateCLQ1(String public_seed) throws Exception {
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
        return new PublicMapParts(C, L, Q1);
    }
    
    /**
     * Create the SHA-512 Hash of given data.
     * @param data Data to be hashed.
     * @return Sha512 Hash of data.
     */
    private static byte[] generateInternalHash512(byte[] data) {
        SHA512Digest digest = new SHA512Digest();
        digest.update(data, 0, data.length);
        byte[] result = new byte[64];
        digest.doFinal(result, 0);
        return result;
    }
    
}
