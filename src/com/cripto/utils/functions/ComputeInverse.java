
package com.cripto.utils.functions;

/**
 * Modified Inverse Matrix Class.
 * @author Eduardo Angulo
 * @author Sebastián Cabarcas
 * @author Andrés Duarte
 * @author Jorge Pinzón
 */
public class ComputeInverse extends ComputeGaussian {
    
    /**
     * Constructor Method.
     * @param degree
     * @param poly
     */
    public ComputeInverse(int degree, int poly) {
        super(degree, poly);
    }
    
    /**
     * This function computes the inverse of a given matrix using the Gauss-
     * Elimination method.
     * <p>
     * An exception is thrown if the matrix has no inverse
     *
     * @param coef the matrix which inverse matrix is needed
     * @return inverse matrix of the input matrix.
     * If the matrix is singular, null is returned.
     * @throws RuntimeException if the given matrix is not invertible
     */
    public int[][] inverse(int[][] coef) {
        try {
            /** Initialization: **/
            int factor;
            int[][] inverse;
            A = new int[coef.length][2 * coef.length];
            if (coef.length != coef[0].length) {
                throw new RuntimeException(
                    "The matrix is not invertible. Please choose another one!");
            }

            /** prepare: Copy coef and the identity matrix into the global A. **/
            for (int i = 0; i < coef.length; i++) {
                for (int j = 0; j < coef.length; j++) {
                    //copy the input matrix coef into A
                    A[i][j] = coef[i][j];
                }
                // copy the identity matrix into A.
                for (int j = coef.length; j < 2 * coef.length; j++) {
                    A[i][j] = 0;
                }
                A[i][i + A.length] = 1;
            }

            /** Elimination operations to get the identity matrix from the left side of A. **/
            // modify A to get 0s under the diagonal.
            computeZerosUnder(true);

            // modify A to get only 1s on the diagonal: A[i][j] =A[i][j]/A[i][i].
            for (int i = 0; i < A.length; i++) {
                factor = gf.inverse(A[i][i]);
                for (int j = i; j < 2 * A.length; j++) {
                    A[i][j] = gf.mult(A[i][j], factor);
                }
            }

            //modify A to get only 0s above the diagonal.
            computeZerosAbove();

            // copy the result (the second half of A) in the matrix inverse.
            inverse = new int[A.length][A.length];
            for (int i = 0; i < A.length; i++) {
                for (int j = A.length; j < 2 * A.length; j++) {
                    inverse[i][j - A.length] = A[i][j];
                }
            }
            return inverse;

        }catch (RuntimeException rte) {
            // The matrix is not invertible! A new one should be generated!
            return null;
        }
    }
    
    /**
     * Elimination above the diagonal.
     * This function changes a matrix so that it contains only zeros above the
     * diagonal(Ai,i) using only Gauss-Elimination operations.
     * <p>
     * It is used in the inverse-function
     * The result is stored in the global matrix A
     * </p>
     *
     * @throws RuntimeException in case a multiplicative inverse of 0 is needed
     */
    private void computeZerosAbove() throws RuntimeException {
        int tmp = 0;
        for (int k = A.length - 1; k > 0; k--) { // the fixed row
            for (int i = k - 1; i >= 0; i--) { // rows
                int factor1 = A[i][k];
                int factor2 = gf.inverse(A[k][k]);
                if (factor2 == 0) {
                    throw new RuntimeException("The matrix is not invertible");
                }
                for (int j = k; j < 2 * A.length; j++) { // columns
                    // tmp = A[k,j] / A[k,k]
                    tmp = gf.mult(A[k][j], factor2);
                    // tmp = A[i,k] * A[k,j] / A[k,k]
                    tmp = gf.mult(factor1, tmp);
                    // A[i,j] = A[i,j] - A[i,k] / A[k,k] * A[k,j];
                    A[i][j] = gf.add(A[i][j], tmp);
                }
            }
        }
    }
    
}