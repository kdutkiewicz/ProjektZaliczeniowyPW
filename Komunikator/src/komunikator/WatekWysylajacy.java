package komunikator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Wojtek Bałchanowski & Kacper Dutkiewicz
 */
public class WatekWysylajacy extends Thread {

    Socket soket;
    BufferedReader brBufferedReader;
    PrintWriter printWriter;
    String imie;

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
