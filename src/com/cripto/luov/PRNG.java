
package com.cripto.luov;

import java.security.SecureRandom;
import java.util.Arrays;
import org.bouncycastle.crypto.digests.KeccakDigest;

/**
 *
 * @author Eduardo Angulo
 * @author Sebastián Cabarcas
 * @author Andrés Duarte
 * @author Jorge Pinzón
 */
public class PRNG {
    
    /**
     * 
     * @param lenght     
     * @return      
     * @throws java.lang.Exception     
     */
    public static byte[] randomBytes(int lenght) throws Exception {
        byte[] rnd = new byte[lenght];
        SecureRandom.getInstanceStrong().nextBytes(rnd);
        return rnd;
    }
    
    /**
     * 
     * @param data     
     * @param bitLength     
     * @return      
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
    
}
