package services;

import bttp2.Codage;
import exception.RestrictionException;
import mediatheque.Abonne;
import Data.DataHandler;
import mediatheque.Document;
import serveur.Service;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;

public class ServiceReservation extends Service implements Runnable {
    public ServiceReservation(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public void run() {
        try {
            Abonne abonne;
            int numeroAbonne;

            getOut().println(Codage.coder("Bienvenue au service de réservation ! Voici notre catalogue :\n" + DataHandler.getCatalogue() + "Veuillez saisir votre numéro d'abonné\n" + "> "));

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

            getOut().println(Codage.coder("Veuillez maintenant saisir le numéro du document que vous souhaitez réserver\n" + "> "));
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
                    if (doc.reservePar() != null) {
                        LocalDateTime availabilityTime = DataHandler.getReservationExpirationDate(doc.numero());
                        String alreadyBorrowedResponse = "Ce document est réservé. Il sera disponible à "
                                + availabilityTime.getHour()
                                + "h"
                                + availabilityTime.getMinute();
                        getOut().println(Codage.coder(alreadyBorrowedResponse));
                        getClient().close();
                        return;
                    } else if (doc.empruntePar() != null) {
                        getOut().println(Codage.coder("Ce document est déjà emprunté."));
                        getClient().close();
                        return;
                    } else {
                        doc.reservation(abonne);
                        DataHandler.reservationTimerTaskStart(doc.numero());
                        getOut().println(Codage.coder("Le document a bien été réservé ! Vous avez deux heures pour le retirer à la borne emprunt de la médiathèque."));
                        getClient().close();
                        return;
                    }
                }
            }

            getOut().println(Codage.coder("Ce numéro de document n'existe pas."));
            getClient().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (RestrictionException e) {
            getOut().println(Codage.coder(e.getMessage()));
        }
    }
}
