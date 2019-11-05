
package com.cripto.luov.utils;

import org.bouncycastle.util.encoders.Hex;

/**
 * Private Key Object Class.
 * @author Eduardo Angulo
 * @author Sebastián Cabarcas
 * @author Andrés Duarte
 * @author Jorge Pinzón
 */
public class PrivateKey {
    
    private String privateSeed;

    /**
     * Constructor Method.
     * @param privateSeed Private Seed Hex String of LUOV Cryptosystem.
     */
    public PrivateKey(String privateSeed) {
        this.privateSeed = privateSeed;
    }
    
    /**
     * Get Private Seed Hex String of LUOV Cryptosystem.
     * @return Private Seed Hex String.
     */
    public String getPrivateSeed() {
        return privateSeed;
    }
    
    /**
     * Get Private Seed Byte Vector of LUOV Cryptosystem.
     * @return Private Seed Byte Vector.
     */
    public byte[] getPrivateSeedBytes() {
        return Hex.decode(privateSeed);
    }
    
}
