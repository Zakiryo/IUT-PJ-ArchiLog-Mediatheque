package client;

import librairies.bttp2.Codage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class ClientBttp implements Runnable {
    private final Socket clientSocket;
    private final BufferedReader sIn;
    private final PrintWriter sOut;
    private final BufferedReader input;

    public ClientBttp(String host, int port) throws IOException {
        clientSocket = new Socket(host, port);
        sIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        sOut = new PrintWriter(clientSocket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        try {
            while (true) {
                read();
                send();
            }
        } catch (IOException e) {
            System.out.println("Échanges terminés. Merci de votre visite.");
        }
    }

    private void read() throws IOException {
        System.out.print(Codage.decoder(sIn.readLine()));
    }

    private void send() throws IOException {
        String userInput = input.readLine();
        if (Objects.equals(userInput, "stop")) {
            clientSocket.close();
            return;
        }
        sOut.println(Codage.coder(userInput));
    }
}
