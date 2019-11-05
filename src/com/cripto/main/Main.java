
package com.cripto.main;

import com.cripto.luov.LUOV;
import com.cripto.luov.utils.Signature;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Main Class.
 * @author Eduardo Angulo
 * @author Sebastián Cabarcas
 * @author Andrés Duarte
 * @author Jorge Pinzón
 */
public class Main {
    
    /**
     * Main Method
     * @param args args.
     * @throws java.lang.Exception     
     */
    public static void main(String[] args) throws Exception {
        LUOV luov = new LUOV();
        //luov.printKeyPair();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Message to be Signed: ");
        String messageToBeSigned = br.readLine();
        Signature sign = luov.sign(messageToBeSigned);
        //System.out.println("Signed Message: " + sign.toString());
        System.out.print("Message to be Verified: ");
        String messageToBeVerified = br.readLine();
        //Signature wrongSign = new Signature(sign.getS(), "00000000000000000000000000000000");
        System.out.println("Valid Message Signature: " + 
                luov.verify(luov.publicKey, messageToBeVerified, sign));
    }
    
}
