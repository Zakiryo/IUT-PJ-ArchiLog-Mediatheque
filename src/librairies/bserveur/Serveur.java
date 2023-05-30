package librairies.bserveur;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur implements Runnable {
    private final Class<? extends Runnable> service;
    private final ServerSocket listen_socket;

    public Serveur(Class<? extends Runnable> service, int port) throws IOException {
        this.service = service;
        this.listen_socket = new ServerSocket(port);
    }

    public void run() {
        while (!this.listen_socket.isClosed()) {
            try {
                new Thread(this.service.getConstructor(Socket.class).newInstance(this.listen_socket.accept())).start();
            } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException |
                     IOException | InstantiationException | IllegalAccessException e) {
                System.err.println("Erreur lors de l'ouverture du serveur : " + e.getMessage());
            }
        }
    }
}
