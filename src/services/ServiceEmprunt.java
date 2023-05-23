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

public class ServiceEmprunt extends Service implements Runnable {
    public ServiceEmprunt(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public void run() {
        PrintWriter out = getOut();
        Socket client = getClient();
        try {
            out.println(Codage.coder("Bienvenue au service d'emprunt ! Voici notre catalogue :\n" + DataHandler.getCatalogue() + "Veuillez saisir votre numéro d'abonné\n" + "> "));

            Abonne abonne = checkAbonne();
            if (abonne == null) {
                client.close();
                return;
            }

            out.println(Codage.coder("Veuillez maintenant saisir le numéro du document que vous souhaitez emprunter\n" + "> "));

            Document doc = checkDocument();
            if (doc == null) {
                client.close();
                return;
            }

            if (doc.reservePar() != null && doc.reservePar() != abonne) {
                LocalDateTime availabilityTime = DataHandler.getReservationExpirationDate(doc.numero());
                out.println(Codage.coder("Ce document est réservé. Il sera disponible à " + availabilityTime.getHour() + "h" + availabilityTime.getMinute()));
                client.close();
                return;
            } else if (doc.empruntePar() != null) {
                out.println(Codage.coder("Ce document est déjà emprunté."));
                client.close();
                return;
            }

            doc.emprunt(abonne);
            DataHandler.validReservation(doc.numero());
            out.println(Codage.coder("Le document a bien été emprunté !"));
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (RestrictionException e) {
            out.println(Codage.coder(e.getMessage()));
        }
    }
}
