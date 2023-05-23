package services;

import bserveur.Service;
import bttp.Codage;
import mediatheque.Document;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ServiceRetour extends Service implements Runnable {
    public ServiceRetour(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public void run() {
        PrintWriter out = getOut();
        Socket client = getClient();
        try {
            out.println(Codage.coder("Bienvenue au service de retour !\nVeuillez saisir le numéro du document à retourner\n" + "> "));

            Document doc = checkDocument();
            if (doc == null) {
                client.close();
                return;
            }

            if (doc.empruntePar() == null && doc.reservePar() == null) {
                out.println(Codage.coder("Ce document n'est ni réservé ni emprunté."));
                client.close();
                return;
            } else {
                doc.retour();
                out.println(Codage.coder("Le document a bien été retourné."));
            }

            client.close();
        } catch (IOException e) {
            System.out.println("Un utilisateur a interrompu sa connexion avec le serveur. / Une erreur est survenue.");
        }

    }
}
