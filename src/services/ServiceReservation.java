package services;

import bserveur.Service;
import bttp.Codage;
import exception.RestrictionException;
import mediatheque.Abonne;
import data.DataHandler;
import mediatheque.Document;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;

public class ServiceReservation extends Service implements Runnable {
    public ServiceReservation(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public void run() {
        PrintWriter out = getOut();
        Socket client = getClient();
        try {
            out.println(Codage.coder("Bienvenue au service de réservation ! Voici notre catalogue :\n" + DataHandler.getCatalogue() + "Veuillez saisir votre numéro d'abonné\n" + "> "));

            Abonne abonne = checkAbonne();
            if (abonne == null) {
                client.close();
                return;
            }

            out.println(Codage.coder("Veuillez maintenant saisir le numéro du document que vous souhaitez réserver\n" + "> "));

            Document doc = checkDocument();
            if (doc == null) {
                client.close();
                return;
            }

            if (doc.reservePar() != null) {
                LocalDateTime availabilityTime = DataHandler.getReservationExpirationDate(doc.numero());
                out.println(Codage.coder("Ce document est réservé jusqu'à " + availabilityTime.getHour() + "h" + availabilityTime.getMinute() + ". " +
                        "Souhaitez-vous placer une alerte par mail lorsque celui-ci sera de nouveau disponible ? (O/N)\n" + "> "));
                proceedAlertResponse(doc);
                client.close();
                return;
            } else if (doc.empruntePar() != null) {
                out.println(Codage.coder("Ce document est emprunté." +
                        "Souhaitez-vous placer une alerte par mail lorsque celui-ci sera de nouveau disponible ? (O/N)\n" + "> "));
                proceedAlertResponse(doc);
                client.close();
                return;
            }

            doc.reservation(abonne);
            DataHandler.reservationTimerTaskStart(doc.numero());
            out.println(Codage.coder("Le document a bien été réservé ! Vous avez deux heures pour le retirer à la borne emprunt de la médiathèque."));
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (RestrictionException e) {
            out.println(Codage.coder(e.getMessage()));
        }
    }
}
