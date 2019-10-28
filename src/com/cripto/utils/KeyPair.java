
package com.cripto.utils;

import java.util.ArrayList;

/**
 * KeyPair Class.
 * @author Eduardo Angulo
 * @author Sebastián Cabarcas
 * @author Andrés Duarte
 * @author Jorge Pinzón
 */
public class KeyPair {
    
    private String privateKey;
    private ArrayList<String> publicKey;
    
    /**
     * Default Constructor Method.
     */
    public KeyPair() {
        this.privateKey = "";
        this.publicKey = new ArrayList<>();
        this.publicKey.add("");
        this.publicKey.add("");
    }
    
    /**
     * Constructor Method.
     * @param private_seed Private Seed of LUOV Cryptosystem.
     * @param public_seed Public Seed of LUOV Cryptosystem.
     * @param Q2_string Q2 String of LUOV Cryptosystem.
     */
    public KeyPair(String private_seed, String public_seed, String Q2_string) {
        this.privateKey = private_seed;
        this.publicKey = new ArrayList<>();
        this.publicKey.add(public_seed);
        this.publicKey.add(Q2_string);
    }

    /**
     * Get the Private Key.
     * @return The Private Key.
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * Set the Private Key.
     * @param privateKey Private Seed of LUOV Cryptosystem.
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
    
    /**
     * Get the Public Key.
     * @return The Public Key.
     */
    public ArrayList<String> getPublicKey() {
        return publicKey;
    }
    
    /**
     * Set the Public Key.
     * @param public_seed Public Seed of LUOV Cryptosystem.
     * @param Q2_string Q2 String of LUOV Cryptosystem.
     */
    public void setPublicKey(String public_seed, String Q2_string) {
        this.publicKey = new ArrayList<>();
        this.publicKey.add(public_seed);
        this.publicKey.add(Q2_string);
    }
    
    /**
     * Override of toString of the Object.
     * @return To String of the Object.
     */
    @Override
    public String toString() {
        return "Private Key: " + privateKey + "\nPublic Key: " + publicKey;
    }
    
}
