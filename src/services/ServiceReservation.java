package services;

import bserveur.Service;
import bttp.Codage;
import data.MailAlert;
import data.TimerHandler;
import exception.RestrictionException;
import mediatheque.Abonne;
import data.DataHandler;
import mediatheque.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;

public class ServiceReservation extends Service implements Runnable {
    private final BufferedReader in = getIn();
    private final PrintWriter out = getOut();
    private final Socket client = getClient();

    public ServiceReservation(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public void run() {
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
                LocalDateTime availabilityTime = TimerHandler.getReservationExpirationDate(doc);
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
            TimerHandler.reservationTimerTaskStart(doc);
            out.println(Codage.coder("Le document a bien été réservé ! Vous avez deux heures pour le retirer à la borne emprunt de la médiathèque."));
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (RestrictionException e) {
            out.println(Codage.coder(e.getMessage()));
        }
    }

    private void proceedAlertResponse(Document document) throws IOException {
        switch (in.readLine()) {
            case "O":
                MailAlert.addToAlertList(document);
                out.println(Codage.coder("Merci. Un mail sera envoyé lorsque le document n°" + document.numero() + " sera disponible."));
            case "N":
                out.println(Codage.coder("Merci de votre visite."));
            default:
                out.println(Codage.coder("Réponse non reconnue. Merci de répondre par O (Oui) ou N (Non)."));
        }
    }
}
