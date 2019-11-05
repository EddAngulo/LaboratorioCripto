
package com.cripto.luov.utils;

/**
 * KeyPair Class.
 * @author Eduardo Angulo
 * @author Sebastián Cabarcas
 * @author Andrés Duarte
 * @author Jorge Pinzón
 */
public class KeyPair {
    
    private PrivateKey privateKey;
    private PublicKey publicKey;
    
    /**
     * Constructor Method.
     * @param privateKey Private Key of LUOV Cryptosystem.
     * @param publicKey Public Key of LUOV Cryptosystem.
     */
    public KeyPair(PrivateKey privateKey, PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    /**
     * Get the Private Key.
     * @return The Private Key.
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * Set the Private Key.
     * @param privateKey Private Seed of LUOV Cryptosystem.
     */
    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }
    
    /**
     * Get the Public Key.
     * @return The Public Key.
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }
    
    /**
     * Set the Public Key.
     * @param publicKey Public Key of LUOV Cryptosystem.
     */
    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }
    
    /**
     * Override of toString of the Object.
     * @return To String of the Object.
     */
    @Override
    public String toString() {
        return "Private Key: " + privateKey.getPrivateSeed() +
                "\nPublic Key: " + publicKey.toString();
    }
    
}
