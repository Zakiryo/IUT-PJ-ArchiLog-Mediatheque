package services;

import bttp2.Codage;
import mediatheque.DataHandler;
import mediatheque.Document;
import serveur.Service;

import java.io.IOException;
import java.net.Socket;

public class ServiceRetour extends Service implements Runnable {
    public ServiceRetour(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public void run() {
        try {
            getOut().println(Codage.coder("Bienvenue au service de retour !\nVeuillez saisir le numéro du document à retourner\n" + "> "));
            int numeroDoc;
            try {
                numeroDoc = Integer.parseInt(getIn().readLine());
            } catch (NumberFormatException e) {
                getOut().println(Codage.coder("Numéro de document incorrect."));
                getClient().close();
                return;
            }
            for (Document doc : DataHandler.getDocuments()) {
                if (doc.numero() == numeroDoc) {
                    if (doc.empruntePar() == null && doc.reservePar() == null) {
                        getOut().println(Codage.coder("Ce document n'est ni réservé ni emprunté."));
                    } else {
                        doc.retour();
                        getOut().println(Codage.coder("Le document a bien été retourné."));
                    }
                    getClient().close();
                    return;
                }
            }
            getOut().println(Codage.coder("Ce numéro de document n'existe pas."));
            getClient().close();
        } catch (IOException e) {
            System.out.println("Un utilisateur a interrompu sa connexion avec le serveur. / Une erreur est survenue.");
        }

    }
}
