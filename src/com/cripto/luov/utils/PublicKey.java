
package com.cripto.luov.utils;

import static com.cripto.luov.LUOV.OIL_VAR;
import com.cripto.utils.functions.Pack;
import org.bouncycastle.util.encoders.Hex;

/**
 * Public Key Object Class.
 * @author Eduardo Angulo
 * @author Sebastián Cabarcas
 * @author Andrés Duarte
 * @author Jorge Pinzón
 */
public class PublicKey {
    
    private String publicSeed;
    private String Q2;

    /**
     * Constructor Method.
     * @param publicSeed Public Seed Hex String of LUOV Cryptosystem.
     * @param Q2 Q2 Matrix Hex String of LUOV Cryptosystem.
     */
    public PublicKey(String publicSeed, String Q2) {
        this.publicSeed = publicSeed;
        this.Q2 = Q2;
    }

    /**
     * Get Public Seed Hex String of LUOV Cryptosystem.
     * @return Public Seed Hex String.
     */
    public String getPublicSeed() {
        return publicSeed;
    }
    
    /**
     * Get Q2 Matrix Hex String of LUOV Cryptosystem.
     * @return Q2 Matrix Hex String.
     */
    public String getQ2() {
        return Q2;
    }
    
    /**
     * Get Public Seed Byte Vector of LUOV Cryptosystem.
     * @return Public Seed Byte Vector.
     */
    public byte[] getPublicSeedBytes() {
        return Hex.decode(publicSeed);
    }
    
    /**
     * Get Q2 Matrix over GF(2^7) of LUOV Cryptosystem.
     * @return Q2 Integer Matrix over GF(2^7).
     */
    public int[][] getQ2Matrix() {
        return Pack.unpack(Q2, OIL_VAR, OIL_VAR*(OIL_VAR + 1)/2);
    }
    
    /**
     * Override of toString of the Object.
     * @return To String of the Object.
     */
    @Override
    public String toString() {
        return "[" + publicSeed + ", " + Q2 + "]";
    }
    
}
