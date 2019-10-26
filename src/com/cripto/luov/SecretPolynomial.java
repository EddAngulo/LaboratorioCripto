
package com.cripto.luov;

import com.cripto.utils.Polynomial;

/**
 *
 * @author Eduardo Angulo
 * @author Sebastián Cabarcas
 * @author Andrés Duarte
 * @author Jorge Pinzón
 */
public class SecretPolynomial extends Polynomial {
    
    /**
     * Constructor Method.
     * @param polyMatrix     
     */
    public SecretPolynomial(int[][] polyMatrix) {
        this.polyMatrix = polyMatrix;
    }
    
    /**
     * 
     * @return     
     */
    public int[][] getPolyMatrix() {
        return polyMatrix;
    }
    
}
