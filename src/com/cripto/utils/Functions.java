
package com.cripto.utils;

import java.util.Arrays;
import org.bouncycastle.pqc.math.linearalgebra.GF2mField;

/**
 *
 * @author eduar
 */
public class Functions {
    
    /**
     * Calculates XOR between two numbers x and y.
     * @param x First Integer.
     * @param y Second Integer.
     * @return XOR between x and y.
     */
    public static int XOR(int x, int y) {
        return (x | y) & (~x | ~y);
    }
    
    /**
     * Calculates Matrix XOR Add over GF(2^r).
     * @param mat1 First Matrix.
     * @param mat2 Second Matrix.
     * @return Result Matrix.
     */
    public static int[][] matrixAdd(int[][] mat1, int[][] mat2) {
        int[][] result = new int[mat1.length][mat1[0].length];
        for (int i = 0; i < mat1.length; i++) {
            for (int j = 0; j < mat1[0].length; j++) {
                result[i][j] = XOR(mat1[i][j], mat2[i][j]);
            }
        }
        return result;
    }
    
    /**
     * Calculates Matrix multiplication over GF(2^r).
     * @param r Degree of Galois Field
     * @param poly Irreducible Polynomial
     * @param mat1 First Matrix.
     * @param mat2 Second Matrix.
     * @return Result Matrix.
     */
    public static int[][] matrixMult(int r, int poly, int[][] mat1, int[][] mat2) {
        GF2mField field = new GF2mField(r, poly);
        int[][] result = new int[mat1.length][mat2[0].length];
        for (int i = 0; i < mat1.length; i++) {
            for (int j = 0; j < mat2[0].length; j++) {
                for (int k = 0; k < mat1[0].length; k++) {
                    result[i][j] = XOR(result[i][j], field.mult(mat1[i][k], mat2[k][j]));
                }
            }
        }
        return result;
    }
    
    /**
     * Calculates the transpose of a Matrix.
     * @param mat Matrix to be transposed.
     * @return Transposed Matrix.
     */
    public static int[][] transposeMatrix(int[][] mat) {
        int[][] result = new int[mat[0].length][mat.length];
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                result[j][i] = mat[i][j];
            }
        }
        return result;
    }
    
    /**
     * Calculates Vector XOR Add over GF(2^r).
     * @param vec1 First Vector.
     * @param vec2 Second Vector.
     * @return Result Vector.
     */
    public static int[] vectorAdd(int[] vec1, int[] vec2) {
        int[] result = new int[vec1.length];
        for (int i = 0; i < vec1.length; i++) {
            result[i] = XOR(vec1[i], vec2[i]);
        }
        return result;
    }
    
    /**
     * Calculates the transpose of a Row Vector.
     * @param vec Row Vector to be transposed.
     * @return Transposed Vector.
     */
    public static int[][] transposeRowVector(int[] vec) {
        int[][] result = new int[vec.length][1];
        for (int i = 0; i < vec.length; i++) {
            result[i][1] = vec[i];
        }
        return result;
    }
    
    /**
     * 
     * @param row
     * @param column
     * @param value
     * @return 
     */
    public static int[][] sameValueMatrix(int row, int column, int value) {
        int[][] result = new int[row][column];
        for (int i = 0; i < row; i++) {
            Arrays.fill(result[i], value);
        }
        return result;
    }
    
    /**
     * 
     * @param dim
     * @return 
     */
    public static int[][] identityMatrix(int dim) {
        int[][] result = new int[dim][dim];
        for (int i = 0; i < dim; i++) {
            result[i][i] = 1;
        }
        return result;
    }
    
    /**
     * 
     * @param mat1
     * @param mat2
     * @return 
     */
    public static int[][] matrixRowUnion(int[][] mat1, int[][] mat2) {
        int row1 = mat1.length;
        int row2 = mat2.length;
        int column = mat1[0].length;
        int[][] result = new int[row1 + row2][column];
        for (int i = 0; i < row1; i++) {
            result[i] = Arrays.copyOf(mat1[i], column);
        }
        for (int i = row1; i < row1 + row2; i++) {
            result[i] = Arrays.copyOf(mat2[i - row1], column);
        }
        return result;
    }
    
    /**
     * 
     * @param mat1
     * @param mat2
     * @return 
     */
    public static int[][] matrixColumnUnion(int[][] mat1, int[][] mat2) {
        int row = mat1.length;
        int column1 = mat1[0].length;
        int column2 = mat2[0].length;
        int[][] result = new int[row][column1 + column2];
        for (int i = 0; i < row; i++) {
            result[i] = concatenateVectors(mat1[i], mat2[i]);
        }
        return result;
    }
    
    /**
     * 
     * @param vec1
     * @param vec2
     * @return 
     */
    public static int[] concatenateVectors(int[] vec1, int[] vec2) {
        int[] result = new int[vec1.length + vec2.length];
        System.arraycopy(vec1, 0, result, 0, vec1.length);
        System.arraycopy(vec2, 0, result, vec1.length, vec2.length);  
        return result;
    }
    
    /**
     * 
     * @param mat1     
     * @param mat2     
     * @return      
     */
    public static boolean matrixEquals(int[][] mat1, int[][] mat2) {
        if(!(mat1.length == mat2.length && mat1[0].length == mat2[0].length)) {
            return false;
        }
        for (int i = 0; i < mat1.length; i++) {
            for (int j = 0; j < mat1[0].length; j++) {
                if(mat1[i][j] != mat2[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
    
}
