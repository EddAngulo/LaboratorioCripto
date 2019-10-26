
package com.cripto.utils.functions;

import java.math.BigInteger;

/**
 * Hex String and GF(2^7) Arrays Packer Class.
 * @author Eduardo Angulo
 * @author Sebastián Cabarcas
 * @author Andrés Duarte
 * @author Jorge Pinzón
 */
public class Pack {
    
    /**
     * Generates the Hex String of Given Matrix.
     * @param mat Integer GF(2^7) Matrix.
     * @return Hex String of Matrix.
     */
    public static String pack(int[][] mat) {
        String result = "";
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                result += Functions.padding((new BigInteger(
                        "" + mat[i][j])).toString(16), 2);
            }
        }
        return result;
    }
    
    /**
     * Generates the Hex String of Given Vector.
     * @param vec Integer GF(2^7) Vector.
     * @return Hex String of Vector.
     */
    public static String pack(int[] vec) {
        String result = "";
        for (int i = 0; i < vec.length; i++) {
            result += Functions.padding((new BigInteger(
                    "" + vec[i])).toString(16), 2);
        }
        return result;
    }
    
    /**
     * Generates a matrix of elements in GF(2^7) from Given Hex String.
     * @param hex Matrix String in hexagesimal.
     * @param row Matrix Row Dimension.
     * @param column Matrix Column Dimension.
     * @return Integer (GF(2^7) elements) Matrix.
     */
    public static int[][] unpack(String hex, int row, int column) {
        int[][] unpackedMatrix = new int[row][column];
        for (int i = 0; i < row; i++) {
            String rowHex = hex.substring(2*column*i, 2*column*(i+1));
            for (int j = 0; j < column; j++) {
                String actual = rowHex.substring(2*j, 2*(j+1));
                unpackedMatrix[i][j] = (new BigInteger(actual, 16)).intValue();
            }
        }
        return unpackedMatrix;
    }
    
    /**
     * Generates a vector of elements in GF(2^7) from Given Hex String.
     * @param hex Vector String in hexagesimal.
     * @param dim Vector Dimension.
     * @return Integer (GF(2^7) elements) Vector.
     */
    public static int[] unpack(String hex, int dim) {
        int[] unpackedVector = new int[dim];
        for (int i = 0; i < dim; i++) {
            String actual = hex.substring(2*i, 2*(i+1));
            unpackedVector[i] = (new BigInteger(actual, 16)).intValue();
        }
        return unpackedVector;
    }
    
}
