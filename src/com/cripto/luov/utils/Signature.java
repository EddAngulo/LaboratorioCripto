
package com.cripto.luov.utils;

import static com.cripto.luov.LUOV.OIL_VAR;
import static com.cripto.luov.LUOV.VINEGAR_VAR;
import com.cripto.utils.functions.Pack;
import org.bouncycastle.util.encoders.Hex;

/**
 * Signature Object Class.
 * @author Eduardo Angulo
 * @author Sebastián Cabarcas
 * @author Andrés Duarte
 * @author Jorge Pinzón
 */
public class Signature {
    
    private String s;
    private String salt;
    
    /**
     * Constructor Method.
     * @param s S solution Hex String.
     * @param salt Salt Bytes Hex String.    
     */
    public Signature(String s, String salt) {
        this.s = s;
        this.salt = salt;
    }

    /**
     * Get S solution Hex String.
     * @return S Hex String.
     */
    public String getS() {
        return s;
    }

    /**
     * Get Salt Bytes Hex String.
     * @return Salt Bytes Hex String.
     */
    public String getSalt() {
        return salt;
    }
    
    /**
     * Get S solution Matrix over GF(2^7).
     * @return S Integer Matrix over GF(2^7).
     */
    public int[][] getSMatrix() {
        return Pack.unpack(s, OIL_VAR + VINEGAR_VAR, 1);
    }
    
    /**
     * Get Salt Bytes Vector.
     * @return Salt Bytes Vector.
     */
    public byte[] getSaltBytes() {
        return Hex.decode(salt);
    }
    
    /**
     * Override of toString of the Object.
     * @return To String of the Object.
     */
    @Override
    public String toString() {
        return "[" + s + ", " + salt + "]";
    }
    
}
