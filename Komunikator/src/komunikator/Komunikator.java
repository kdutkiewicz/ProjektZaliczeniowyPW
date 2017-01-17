/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package komunikator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wojtek Bałchanowski & Kacper Dutkiewicz
 */
public class Komunikator {

    //kolorki
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";

    static final String NAZWA_PLIKU_Z_ODBIORCAMI = "odbiorcy.txt";
    static int PORT = 3000;
    static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    static String imie = "Tomek";

    public static void wyswietlMenu() {
        System.out.println(ANSI_BLUE + "************************");
        System.out.println("*" + ANSI_RESET + "        >MENU<        " + ANSI_BLUE + "*");
        System.out.println("*" + ANSI_RESET + " 1. Nawiaz polaczenie " + ANSI_BLUE + "*");
        System.out.println("*" + ANSI_RESET + " 2. Dodaj odbiorce    " + ANSI_BLUE + "*");
        System.out.println("*" + ANSI_RESET + " 3. Nasluchuj         " + ANSI_BLUE + "*");
        System.out.println("*" + ANSI_RESET + " 4. Zmien imie        " + ANSI_BLUE + "*");
        System.out.println("*" + ANSI_RESET + " 5. Zmien port        " + ANSI_BLUE + "*");
        System.out.println("*" + ANSI_RESET + " 6. Wyjscie           " + ANSI_BLUE + "*");
        System.out.println("************************" + ANSI_RESET);
    }

    //przejscie w tryb klienta
    public static void nawiazywaniePolaczenia() throws IOException, InterruptedException {

        try {
            List<String> listaOdbiorcow = wczytajOdbiorcow();
            int indexOdbiorcy = 0;
            //wyboru odbiorcy
            do {
                try {
                    System.out.println("Wybierz odbiorce podajac jego numer");
                    wyswietlOdbiorcow();
                    indexOdbiorcy = Integer.parseInt(bufferedReader.readLine());
                } catch (NumberFormatException e) {
                    System.out.println(ANSI_RED + "Podaj liczbe za zakresu 1-" + listaOdbiorcow.size() + ANSI_RESET);
                }

            } while (indexOdbiorcy <= 0 || indexOdbiorcy > listaOdbiorcow.size());
            //otiweranie polaczenia z serwerem na podstawie nazwy odbiorcy
            Socket sock = new Socket(listaOdbiorcow.get(indexOdbiorcy - 1), PORT);
            //potwierdzenie polaczenia           
            if (sock.isConnected()) {
                System.out.println(ANSI_GREEN + "Polaczono" + ANSI_RESET);
                System.out.println("Aby zakończyć polaczenie wpisz: WYJSCIE");
            }
            //uruchomienie watkow
            WatekOdbierajacy watekOdbierajacy = new WatekOdbierajacy(sock);
            WatekWysylajacy watekWysylajacy = new WatekWysylajacy(sock, imie);

            watekOdbierajacy.start();
            watekWysylajacy.start();
            watekWysylajacy.join();
            //obsluga bledow
        } catch (UnknownHostException e) {
            System.out.println(ANSI_RED + "Wybrany host jest bledny" + ANSI_RESET);
        } catch (ConnectException e) {
            System.out.println(ANSI_RED + "Wystapil problem z polaczeniem" + ANSI_RESET);
        }
    }

    //metoda odpowiedzialna za wprowadzanie nowego odbiorcy
    public static void dodajOdbiorce() throws IOException {
        String nowyOdbiorca = "";
        do {
            System.out.println("Podaj adres odbiorcy");
            nowyOdbiorca = bufferedReader.readLine();

        } while (nowyOdbiorca.equals(""));
        dodajOdbiorceDoPliku(nowyOdbiorca);

    }

    //przejscie w tryb serwera
    public static void nasluchuj() throws IOException, InterruptedException {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);

            try {
                System.out.println("Nasluchuje polaczenia na porcie: " + PORT);
                Socket klient = serverSocket.accept();
                WatekOdbierajacy w1 = new WatekOdbierajacy(klient);
                WatekWysylajacy w2 = new WatekWysylajacy(klient, imie);
                //potwierdzenie polaczenia
                System.out.println(ANSI_GREEN + "Nawiazano polaczenie z adresem " + klient.getInetAddress() + " na porcie " + klient.getPort() + ANSI_RESET);
                System.out.println("Aby zakończyć polaczenie wpisz: WYJSCIE");
                w1.start();
                w2.start();
                w2.join();

            } finally {
                //zamykanie gniazda serwera po zakonczeniu polaczenia
                serverSocket.close();
            }
        } catch (BindException e) {
            System.out.println("Nie mozna rozpoczac nasluchiwania, port " + PORT + " jest zajety");
        }

    }

    public static void wyswietlOdbiorcow() throws IOException {
        int i = 1;
        for (String s : wczytajOdbiorcow()) {
            System.out.println(i++ + ". " + s);
        }
    }

    //wczytywanie odbiorcow z pliku
    public static List<String> wczytajOdbiorcow() throws FileNotFoundException, IOException {
        List<String> listaStringow = new ArrayList<>();
        BufferedReader bR = new BufferedReader(new FileReader(new File(NAZWA_PLIKU_Z_ODBIORCAMI)));
        try {
            String linia = bR.readLine();
            while (linia != null) {
                listaStringow.add(linia);
                linia = bR.readLine();
            }
        } finally {
            bR.close();
        }
        return listaStringow;
    }

    //obsluga zapisania nowego odbiorcy do pliku
    public static void dodajOdbiorceDoPliku(String nowyOdbiorca) throws FileNotFoundException, IOException {
        List<String> listaStringow = wczytajOdbiorcow();
        listaStringow.add(nowyOdbiorca);
        BufferedWriter bW = new BufferedWriter(new FileWriter(NAZWA_PLIKU_Z_ODBIORCAMI));
        for (String odbiorca : listaStringow) {
            bW.write(odbiorca + "\n");
        }
        bW.close();
    }

    public static void zmienImie() throws IOException {
        imie = "";
        do {
            System.out.println("Podaj swoje imie:");
            imie = bufferedReader.readLine();
        } while (imie.equals(""));
    }

    private static void zmienPort() throws IOException {
        int nowyPort = -1;
        try {
            do {
                System.out.println("Podaj numer portu(0-65000):");
                nowyPort = Integer.parseInt(bufferedReader.readLine());
            } while (nowyPort < 0 && nowyPort > 65000);
            PORT = nowyPort;
        } catch (NumberFormatException e) {
            System.out.println("Zły format");
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int zmiennaSterujaca = 0;
        do {
            try {
                wyswietlMenu();
                zmiennaSterujaca = Integer.parseInt(bufferedReader.readLine());

                switch (zmiennaSterujaca) {
                    case 1:
                        nawiazywaniePolaczenia();
                        break;
                    case 2:
                        dodajOdbiorce();
                        break;
                    case 3:
                        nasluchuj();
                        break;
                    case 4:
                        zmienImie();
                        break;
                    case 5:
                        zmienPort();
                        break;
                    case 6:
                        System.exit(0);
                        break;
                    default:
                        System.out.println(ANSI_RED + "Wybrano niepoprawna opcje" + ANSI_RESET);
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println(ANSI_RED + "Podaj liczbe z zakresu 1-6!" + ANSI_RESET);
            }

        } while (zmiennaSterujaca != 6);

    }

}
