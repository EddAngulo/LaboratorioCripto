
package com.cripto.luov;

import java.security.SecureRandom;
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
     * @param bitLenght     
     * @return      
     */
    public static byte[] getHashDigest(byte[] data, int bitLenght) {
        KeccakDigest digest = new KeccakDigest(bitLenght);
        digest.update(data, 0, data.length);
        int resultLenght = (int) (Math.ceil(bitLenght/8));
        byte[] result = new byte[resultLenght];
        digest.doFinal(result, 0);
        return result;
    }
    
}
