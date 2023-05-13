package services;

import bttp2.Codage;
import exception.RestrictionException;
import mediatheque.Abonne;
import mediatheque.DataHandler;
import mediatheque.Document;
import serveur.Service;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ServiceReservation extends Service implements Runnable {
    public ServiceReservation(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public void run() {
        try {
            getOut().println(Codage.coder("Bienvenue au service de réservation ! Voici notre catalogue :\n" + DataHandler.getCatalogue() + "Veuillez saisir votre numéro d'abonné\n" + "> "));
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
                getOut().println(Codage.coder("Désolé, ce numéro d'abonné n'est pas enregistré."));
                getClient().close();
                return;
            }
            getOut().println(Codage.coder("Veuillez maintenant saisir le numéro du document que vous souhaitez réserver\n" + "> "));
            int numeroDocument;
            try {
                numeroDocument = Integer.parseInt(getIn().readLine());
            } catch (NumberFormatException e) {
                getOut().println(Codage.coder("Numéro de document incorrect."));
                getClient().close();
                return;
            }
            for (Document d : DataHandler.getDocuments()) {
                if (d.numero() == numeroDocument) {
                    if (d.reservePar() != null) {
                        getOut().println(Codage.coder("Ce document est déjà réservé."));
                        getClient().close();
                        return;
                    } else if (d.empruntePar() != null) {
                        getOut().println(Codage.coder("Ce document est déjà emprunté."));
                        getClient().close();
                        return;
                    } else {
                        d.reservation(abonne);
                        getOut().println(Codage.coder("Le document a bien été réservé ! Vous avez deux heures pour le retirer à la borne emprunt de la médiathèque."));
                        getClient().close();
                        return;
                    }
                }
            }
            getOut().println(Codage.coder("Ce numéro de document n'existe pas."));
            getClient().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("Un utilisateur a interrompu sa connexion avec le serveur.");
        } catch (RestrictionException e) {
            getOut().println(Codage.coder(e.getMessage()));
        }
    }
}
