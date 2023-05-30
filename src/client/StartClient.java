package client;

import librairies.bttp.ClientBttp;

import java.io.IOException;
import java.util.List;

import static java.lang.Integer.parseInt;

public class StartClient {
    private static final List<Integer> PORTS = List.of(1000, 1001, 1002);

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Veuillez spécifier un numéro de port.");
            return;
        }

        int port = parseInt(args[0]);
        if (PORTS.contains(port)) {
            try {
                new Thread(new ClientBttp("localhost", port)).start();
                System.out.println("Si vous souhaitez sortir à tout moment de l'application, saisissez 'stop'.\n");
            } catch (IOException e) {
                System.err.println("Le serveur auquel vous essayez d'accéder est indisponible.");
            }
        } else {
            System.err.println("Numéro de port invalide.");
        }
    }
}
