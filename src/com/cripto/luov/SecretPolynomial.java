
package com.cripto.luov;

import com.cripto.utils.Polynomial;

/**
 * Secret Polynomial Class.
 * @author Eduardo Angulo
 * @author Sebastián Cabarcas
 * @author Andrés Duarte
 * @author Jorge Pinzón
 */
public class SecretPolynomial extends Polynomial {
    
    /**
     * Constructor Method.
     * @param quadraticPart Matrix Corresponding to a Polynomial Quadratic Part.
     */
    public SecretPolynomial(int[][] quadraticPart) {
        this.quadraticPart = quadraticPart;
    }
    
    /**
     * Get the Matrix Corresponding to a Polynomial Quadratic Part.
     * @return Quadratic Part of Desired Polynomial.
     */
    public int[][] getQuadraticPart() {
        return quadraticPart;
    }
    
}
