
package com.cripto.main;

import com.cripto.luov.LUOV;
import static com.cripto.luov.LUOV.FIELD;
import org.bouncycastle.pqc.math.linearalgebra.GF2mField;

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
        luov.keyGen();
        luov.printKeyPair();
    }
    
}
