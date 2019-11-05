
package com.cripto.luov.utils;

import static com.cripto.luov.LUOV.OIL_VAR;
import static com.cripto.luov.LUOV.VINEGAR_VAR;
import com.cripto.utils.functions.Pack;

/**
 * Public Map Constant, Linear and First Quadratic Part Class.
 * @author Eduardo Angulo
 * @author Sebastián Cabarcas
 * @author Andrés Duarte
 * @author Jorge Pinzón
 */
public class PublicMapParts {
    
    private String C;
    private String L;
    private String Q1;

    /**
     * Constructor Method.
     * @param C Constant Part Matrix Hex String of Public Map of LUOV Cryptosystem.
     * @param L Linear Part Matrix Hex String of Public Map of LUOV Cryptosystem.
     * @param Q1 First Part of Quadratic Part Matrix Hex String of Public Map of LUOV Cryptosystem.
     */
    public PublicMapParts(String C, String L, String Q1) {
        this.C = C;
        this.L = L;
        this.Q1 = Q1;
    }

    /**
     * Get C Constant Part Hex String.
     * @return C Hex String.
     */
    public String getC() {
        return C;
    }

    /**
     * Get L Linear Part Hex String.
     * @return L Hex String.
     */
    public String getL() {
        return L;
    }

    /**
     * Get Q1 First Quadratic Part Hex String.
     * @return Q1 Hex String.
     */
    public String getQ1() {
        return Q1;
    }
    
    /**
     * Get C Constant Part Matrix over GF(2^7).
     * @return C Integer Matrix over GF(2^7).
     */
    public int[][] getCMatrix() {
        return Pack.unpack(C, OIL_VAR, 1);
    }
    
    /**
     * Get L Linear Part Matrix over GF(2^7).
     * @return L Integer Matrix over GF(2^7).
     */
    public int[][] getLMatrix() {
        return Pack.unpack(L, OIL_VAR, OIL_VAR + VINEGAR_VAR);
    }
    
    /**
     * Get Q1 First Quadratic Part Matrix over GF(2^7).
     * @return Q1 Integer Matrix over GF(2^7).
     */
    public int[][] getQ1Matrix() {
        return Pack.unpack(Q1, OIL_VAR, 
                (VINEGAR_VAR*(VINEGAR_VAR + 1)/2) + (VINEGAR_VAR * OIL_VAR));
    }
    
}
