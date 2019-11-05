
package com.cripto.luov.utils;

import static com.cripto.luov.LUOV.OIL_VAR;
import static com.cripto.luov.LUOV.VINEGAR_VAR;
import com.cripto.utils.functions.Functions;
import java.math.BigInteger;

/**
 * Linear Transformation Class.
 * @author Eduardo Angulo
 * @author Sebastián Cabarcas
 * @author Andrés Duarte
 * @author Jorge Pinzón
 */
public class LinearTransformation {
    
    private String T;

    /**
     * Constructor Method.
     * @param T T Matrix Hex String.
     */
    public LinearTransformation(String T) {
        this.T = T;
    }

    /**
     * Get T Matrix Hex String.
     * @return T Matrix Hex String.
     */
    public String getT() {
        return T;
    }
    
    /**
     * Generates a matrix of 0s and 1s from linearTrans Hex String.
     * @return Integer (Binary) linearTrans Matrix.
     */
    public int[][] getTMatrix() {
        int[][] T_matrix = new int[VINEGAR_VAR][OIL_VAR];
        for (int i = 0; i < VINEGAR_VAR; i++) {
            String hex = T.substring(16*i, 16*(i+1));
            String bin = Functions.padding((new BigInteger(
                    hex, 16)).toString(2), OIL_VAR);
            for (int j = 0; j < OIL_VAR; j++) {
                T_matrix[i][j] = (int) (bin.charAt(j)) - 48;
            }
        }
        return T_matrix;
    }
    
    /**
     * Builds the Linear Transformation Matrix [[1v, linearTrans]; [0, 1m]].
     * @return Linear Transformation Matrix (n x n).
     */
    public int[][] buildLinearTransMatrix() {
        int[][] T_matrix = getTMatrix();
        int[][] upper = Functions.matrixColumnUnion(
                Functions.identityMatrix(VINEGAR_VAR), T_matrix);
        int[][] lower = Functions.matrixColumnUnion(
                Functions.sameValueMatrix(OIL_VAR, VINEGAR_VAR, 0), 
                Functions.identityMatrix(OIL_VAR));
        return Functions.matrixRowUnion(upper, lower);
    }
    
}
