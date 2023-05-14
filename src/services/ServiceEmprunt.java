package services;

import bttp2.Codage;
import exception.RestrictionException;
import mediatheque.Abonne;
import mediatheque.DataHandler;
import mediatheque.Document;
import serveur.Service;

import java.io.IOException;
import java.net.Socket;

public class ServiceEmprunt extends Service implements Runnable {
    public ServiceEmprunt(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public void run() {
        try {
            getOut().println(Codage.coder("Bienvenue au service d'emprunt ! Voici notre catalogue :\n" + DataHandler.getCatalogue() + "Veuillez saisir votre numéro d'abonné afin d'emprunter\n" + "> "));
            Abonne abonne;
            int numeroAbonne;
            try {
                numeroAbonne = Integer.parseInt(getIn().readLine());
            } catch (NumberFormatException e) {
                getOut().println(Codage.coder("Numéro d'abonné incorrect."));
                getClient().close();
                return;
            }
            abonne = DataHandler.getAbonneById(numeroAbonne);
            if (abonne == null) {
                getOut().println(Codage.coder("Ce numéro d'abonné n'est pas enregistré."));
                getClient().close();
                return;
            }
            getOut().println(Codage.coder("Veuillez maintenant saisir le numéro du document que vous souhaitez emprunter\n" + "> "));
            int numeroDocument;
            try {
                numeroDocument = Integer.parseInt(getIn().readLine());
            } catch (NumberFormatException e) {
                getOut().println(Codage.coder("Numéro de document incorrect."));
                getClient().close();
                return;
            }
            for (Document doc : DataHandler.getDocuments()) {
                if (doc.numero() == numeroDocument) {
                    if (doc.empruntePar() != null) {
                        getOut().println(Codage.coder("Ce document est déjà emprunté."));
                        getClient().close();
                        return;
                    }
                    try {
                        doc.emprunt(abonne);
                    } catch (RestrictionException e) {
                        getOut().println(Codage.coder(e.getMessage()));
                        getClient().close();
                        return;
                    }
                    getOut().println(Codage.coder("Le document a bien été emprunté."));
                    getClient().close();
                    return;
                }
            }
            getOut().println(Codage.coder("Document introuvable."));
            getClient().close();
        } catch (IOException e) {
            System.out.println("Un utilisateur a interrompu sa connexion avec le serveur. / Une erreur est survenue.");
        }
    }
}
