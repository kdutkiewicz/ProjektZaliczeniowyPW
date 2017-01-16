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
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 *
 * @author kiper
 */
public class Komunikator {
    //kolorki
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    static final String NAZWA_PLIKU_Z_ODBIORCAMI = "odbiorcy.txt";
    static final int PORT = 3000;
    static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    static String imie = "Tomek";

    public static void wyswietlMenu() {
        System.out.println(">>>MENU<<<");
        System.out.println("1. Nawiaz polaczenie");
        System.out.println("2. Dodaj odbiorce");
        System.out.println("3. Nasluchuj");
        System.out.println("4. Zmien imie");
        System.out.println("5. Wyjscie");
    }
    //metoda obslugujaca prace aplikacji jako klient
    public static void nawiazywaniePolaczenia() throws IOException, InterruptedException {

        try {
            List<String> listaOdbiorcow = wczytajOdbiorcow();
            int indexOdbiorcy = 0;
            //obsluga wyboru odbiorcy
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
            }
            //uruchomienie watkow
            WatekOdbierajacy w1 = new WatekOdbierajacy(sock);
            WatekWysylajacy w2 = new WatekWysylajacy(sock, imie);
            
            w1.start();
            w2.start();
            w2.join();
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
    //metoda ustawiajaca aplikacje jako serwer
    public static void nasluchuj() throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(PORT);
   
        try {
            System.out.println("Nasluchuje polaczenia na porcie: " + PORT);
            Socket klient = serverSocket.accept();
            WatekOdbierajacy w1 = new WatekOdbierajacy(klient);
            WatekWysylajacy w2 = new WatekWysylajacy(klient, imie);
            //potwierdzenie polaczenia
            System.out.println(ANSI_GREEN + "Nawiazano polaczenie z adresem " + klient.getInetAddress() + " na porcie " + klient.getPort() + ANSI_RESET);
            w1.start();
            w2.start();
            w2.join();

        } finally {
            //zamykanie gniazda serwera po zakonczeniu polaczenia
            serverSocket.close();
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
    //mozliwosc zmiany imienia ktore aktualnie jest na sztywno zapisane w kodzie
    public static void zmienImie() throws IOException {
        imie = "";
        do {
            System.out.println("Podaj swoje imie:");
            imie = bufferedReader.readLine();
        } while (imie.equals(""));
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
                        System.exit(0);
                        break;                  
                    default:
                        System.out.println(ANSI_RED + "Wybrano nie poprawna opcje" + ANSI_RESET);
                        break;

                }
            } catch (NumberFormatException e) {
                System.out.println(ANSI_RED + "Podaj liczbe z zaresu 1-5!" + ANSI_RESET);
            }

        } while (zmiennaSterujaca != 5);

    }

}
