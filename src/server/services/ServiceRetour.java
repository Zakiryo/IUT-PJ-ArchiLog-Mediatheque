package server.services;

import librairies.bserveur.serveur.Service;
import librairies.bttp2.Codage;
import server.data.TimerHandler;
import server.mediatheque.Abonne;
import server.mediatheque.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ServiceRetour extends Service implements Runnable {
    private final BufferedReader in = getIn();
    private final PrintWriter out = getOut();
    private final Socket client = getClient();

    public ServiceRetour(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public void run() {
        try {
            out.println(Codage.coder("Bienvenue au service de retour !\nVeuillez saisir le numéro du document à retourner.\n" + "> "));

            Document doc = null;
            while (doc == null) {
                doc = checkDocument();
            }

            if (!isBorrowed(doc)) {
                client.close();
                return;
            }

            out.println(Codage.coder("Des dégradations ont-elles été constatées sur le document ? (O/N)\n" + "> "));
            while (!client.isClosed()) {
                proceedDegradationResponse(doc);
            }

        } catch (IOException e) {
            System.err.println("Un utilisateur a interrompu sa connexion avec le serveur.");
        }
    }

    private boolean isBorrowed(Document document) throws IOException {
        if (document.empruntePar() == null) {
            out.println(Codage.coder("Ce document n'est pas emprunté."));
            return false;
        }
        return true;
    }

    private void proceedDegradationResponse(Document document) throws IOException {
        switch (in.readLine()) {
            case "O" -> {
                if (isBorrowed(document)) {
                    Abonne emprunteur = document.empruntePar();
                    emprunteur.setBanned(true);
                    TimerHandler.addToBanList(emprunteur);
                    document.retour();
                    out.println(Codage.coder("Suite à cette dégradation, le client n°" + emprunteur.getNumero() + " a été banni du service pendant un mois."));
                    client.close();
                }
            }
            case "N" -> {
                if (isBorrowed(document)) {
                    document.retour();
                    out.println(Codage.coder("Le document a bien été retourné."));
                    client.close();
                }
            }
            default ->
                    out.println(Codage.coder("Réponse non reconnue. Merci de répondre par O (Oui) ou N (Non).\n" + "> "));
        }
    }
}
