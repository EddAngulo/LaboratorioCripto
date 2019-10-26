
package com.cripto.luov;

import com.cripto.utils.functions.Functions;
import java.util.ArrayList;

/**
 *
 * @author Eduardo Angulo
 * @author Sebastián Cabarcas
 * @author Andrés Duarte
 * @author Jorge Pinzón
 */
public class SecretMap {
    
    private final ArrayList<SecretPolynomial> secretPolys;
    
    /**
     * Constructor Method.
     */
    public SecretMap() {
        this.secretPolys = new ArrayList<>();
    }
    
    /**
     * 
     * @param poly     
     */
    public void addSecretPoly(SecretPolynomial poly) {
        this.secretPolys.add(poly);
    }
    
    /**
     * 
     * @param index     
     * @return      
     */
    public SecretPolynomial getSecretPoly(int index) {
        return secretPolys.get(index);
    }
    
    /**
     * 
     * @return 
     */
    public int getSecretMapLength() {
        return secretPolys.size();
    }
    
    /**
     * 
     * @param v     
     * @param o     
     * @return      
     */
    public int[][] evalSecretMap(int[][] v, int[][] o) {
        int[][] eval = new int[LUOV.OIL_VAR][1];
        int[][] vars = Functions.matrixRowUnion(v, o);
        int[][] vars_tran = Functions.transposeMatrix(vars);
        for (int i = 0; i < LUOV.OIL_VAR; i++) {
            int[][] left = Functions.matrixMult(LUOV.FIELD, LUOV.POLY, 
                    vars_tran, secretPolys.get(i).getPolyMatrix());
            int[][] result = Functions.matrixMult(LUOV.FIELD, LUOV.POLY, 
                    left, vars);
            eval[i][0] = result[0][0];
        }
        return eval;
    }
    
}
