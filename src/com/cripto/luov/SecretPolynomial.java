
package com.cripto.luov;

import com.cripto.utils.Polynomial;

/**
 *
 * @author eduar
 */
public class SecretPolynomial extends Polynomial {
    
    public SecretPolynomial(int[][] polyMatrix) {
        this.polyMatrix = polyMatrix;
    }
    
    public int[][] getPolyMatrix() {
        return polyMatrix;
    }
    
}
