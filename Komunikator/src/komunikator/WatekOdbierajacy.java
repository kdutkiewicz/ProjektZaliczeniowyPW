/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package komunikator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kiper
 */
public class WatekOdbierajacy extends Thread {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    Socket soket = null;
    BufferedReader bufferedReader = null;

    public WatekOdbierajacy(Socket soketKlienta) throws IOException {
        this.soket = soketKlienta;
        bufferedReader = new BufferedReader(new InputStreamReader(soketKlienta.getInputStream()));
    }

    public void run() {
        try {
            String messageString;          
            while (true) {
                while ((messageString = bufferedReader.readLine()) != null) {
                    System.out.println(ANSI_YELLOW + messageString );   
//                    System.out.println(System.currentTimeMillis());
                   
                }              
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }finally{
            try {
                soket.close();
            } catch (IOException ex) {
                Logger.getLogger(WatekOdbierajacy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
