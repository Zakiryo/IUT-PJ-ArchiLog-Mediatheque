package services;

import bserveur.Service;
import bttp.Codage;
import data.TimerHandler;
import mediatheque.Abonne;
import mediatheque.Document;

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
            }

            out.println(Codage.coder("Des dégradations ont-elles été constatées sur le document ? (O/N)\n" + "> "));
            proceedDegradationResponse(doc);
            client.close();
        } catch (IOException e) {
            System.out.println("Un utilisateur a interrompu sa connexion avec le serveur. / Une erreur est survenue.");
        }
    }

    private void proceedDegradationResponse(Document document) throws IOException {
        switch (in.readLine()) {
            case "O":
                Abonne emprunteur = document.empruntePar();
                emprunteur.setBanned(true);
                TimerHandler.addToBanList(emprunteur);
                document.retour();
                out.println(Codage.coder("Suite à cette dégradation, le client n°" + emprunteur.getNumero() + " a été banni du service pendant un mois."));
            case "N":
                document.retour();
                out.println(Codage.coder("Le document a bien été retourné."));
            default:
                out.println(Codage.coder("Réponse non reconnue. Merci de répondre par O (Oui) ou N (Non)."));
        }
    }
}
