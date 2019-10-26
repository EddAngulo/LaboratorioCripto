
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
     * 
     * @param private_seed     
     * @param public_seed     
     * @param Q2_string     
     */
    public KeyPair(String private_seed, String public_seed, String Q2_string) {
        this.privateKey = private_seed;
        this.publicKey = new ArrayList<>();
        this.publicKey.add(public_seed);
        this.publicKey.add(Q2_string);
    }

    /**
     * 
     * @return     
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * 
     * @param privateKey
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
    
    /**
     * 
     * @return     
     */
    public ArrayList<String> getPublicKey() {
        return publicKey;
    }
    
    /**
     * 
     * @param public_seed     
     * @param Q2_string     
     */
    public void setPublicKey(String public_seed, String Q2_string) {
        this.publicKey = new ArrayList<>();
        this.publicKey.add(public_seed);
        this.publicKey.add(Q2_string);
    }
    
    /**
     * 
     * @return     
     */
    @Override
    public String toString() {
        return "Private Key: " + privateKey + "\nPublic Key: " + publicKey;
    }
    
}
