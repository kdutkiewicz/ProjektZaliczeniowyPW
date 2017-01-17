package komunikator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wojtek Bałchanowski & Kacper Dutkiewicz
 */
public class WatekOdbierajacy extends Thread {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    Socket soket;
    BufferedReader bufferedReader;

    public WatekOdbierajacy(Socket soket) throws IOException {
        this.soket = soket;
        bufferedReader = new BufferedReader(new InputStreamReader(soket.getInputStream()));
    }

    public void run() {
        try {
            String messageString;
            while (true) {
                while ((messageString = bufferedReader.readLine()) != null) {
                    System.out.println(ANSI_YELLOW + messageString + ANSI_RESET);
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                soket.close();
            } catch (IOException ex) {
                Logger.getLogger(WatekOdbierajacy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
