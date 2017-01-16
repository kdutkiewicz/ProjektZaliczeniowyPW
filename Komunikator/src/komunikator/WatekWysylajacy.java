/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package komunikator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author kiper
 */
public class WatekWysylajacy extends Thread {

    Socket soket = null;
    BufferedReader brBufferedReader = null;
    PrintWriter printWriter = null;
    String imie = "";

    public WatekWysylajacy(Socket clientSocket, String imie) {
        this.soket = clientSocket;
        this.imie = imie;
    }

    public void run() {
        try {
            printWriter = new PrintWriter(new OutputStreamWriter(this.soket.getOutputStream()));
            while (true) {
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                String msgToClientString = input.readLine();
                if (msgToClientString.equals("WYJSCIE")) {
                    break;
                }
                printWriter.println(imie + ": " + msgToClientString);
                printWriter.flush();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
