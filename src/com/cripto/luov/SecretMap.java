
package com.cripto.luov;

import com.cripto.utils.functions.Functions;
import java.util.ArrayList;

/**
 * Secret Map Class.
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
     * Add a Quadratic Part of Secret Polynomial.
     * @param poly Polynomial to be Added.
     */
    public void addSecretPoly(SecretPolynomial poly) {
        this.secretPolys.add(poly);
    }
    
    /**
     * Get a Quadratic Part of Secret Polynomial.
     * @param index Index of Desired Polynomial.
     * @return A Quadratic Part of Secret Polynomial.
     */
    public SecretPolynomial getSecretPoly(int index) {
        return secretPolys.get(index);
    }
    
    /**
     * Get the Number of Polynomials in Secret Map.
     * @return Number of Polynomials.
     */
    public int getSecretMapLength() {
        return secretPolys.size();
    }
    
    /**
     * Evaluates the Quadratic Part of Secret Map in given Vars Assignment.
     * @param v Vinegar Vars Assign.
     * @param o Oil Vars Assign.
     * @return Evaluation of Quadratic Part of Secret Map.
     */
    public int[][] evalSecretMap(int[][] v, int[][] o) {
        int[][] eval = new int[LUOV.OIL_VAR][1];
        int[][] vars = Functions.matrixRowUnion(v, o);
        int[][] vars_tran = Functions.transposeMatrix(vars);
        for (int i = 0; i < LUOV.OIL_VAR; i++) {
            int[][] left = Functions.matrixMult(LUOV.FIELD, LUOV.POLY, 
                    vars_tran, secretPolys.get(i).getQuadraticPart());
            int[][] result = Functions.matrixMult(LUOV.FIELD, LUOV.POLY, 
                    left, vars);
            eval[i][0] = result[0][0];
        }
        return eval;
    }
    
}
