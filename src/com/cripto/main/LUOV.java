/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cripto.main;

import com.cripto.utils.Utils;

/**
 *
 * @author Eduardo Angulo
 */
public class LUOV {
    
    public static void main(String[] args) throws Exception {
        String private_seed = Utils.generatePrivateSeed();
        String public_seed = Utils.generatePublicSeed(private_seed);
        String T = Utils.generateT(private_seed);
        System.out.println("Private seed: " + private_seed);
        System.out.println("Public seed: " + public_seed);
        //System.out.println("T: " + T);
        //System.out.println(T.length());
    }
    
}
