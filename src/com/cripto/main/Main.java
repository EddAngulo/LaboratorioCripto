
package com.cripto.main;

import com.cripto.luov.LUOV;

/**
 * Main Class
 * @author Eduardo Angulo
 * @author Sebastián Cabarcas
 * @author Andrés Duarte
 * @author Jorge Pinzón
 */
public class Main {
    
    /**
     * Main Method
     * @param args args.
     * @throws java.lang.Exception     
     */
    public static void main(String[] args) throws Exception {
        LUOV luov = new LUOV();
        luov.printKeyPair();
    }
    
}
