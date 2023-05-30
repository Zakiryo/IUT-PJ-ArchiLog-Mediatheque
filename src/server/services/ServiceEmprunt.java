package server.services;

import librairies.bserveur.Service;
import librairies.bttp.Codage;
import server.data.TimerHandler;
import server.exception.RestrictionException;
import server.mediatheque.Abonne;
import server.data.DataHandler;
import server.mediatheque.Document;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;

public class ServiceEmprunt extends Service implements Runnable {
    private final PrintWriter out = getOut();
    private final Socket client = getClient();

    public ServiceEmprunt(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public void run() {
        try {
            out.println(Codage.coder("Bienvenue au service d'emprunt ! Voici notre catalogue :\n" + DataHandler.getCatalogue() + "Veuillez saisir votre numéro d'abonné.\n" + "> "));

            Abonne abonne = null;
            while (abonne == null && !client.isClosed()) {
                abonne = checkAbonne();
            }

            out.println(Codage.coder("Veuillez maintenant saisir le numéro du document que vous souhaitez emprunter.\n" + "> "));

            Document doc = null;
            while (doc == null) {
                doc = checkDocument();
            }

            if (doc.reservePar() != null && doc.reservePar() != abonne) {
                LocalDateTime availabilityTime = TimerHandler.getReservationExpirationDate(doc);
                out.println(Codage.coder("Ce document est réservé jusqu'à " + availabilityTime.getHour() + "h" + availabilityTime.getMinute() + "."));
                client.close();
                return;

            } else if (doc.empruntePar() != null) {
                out.println(Codage.coder("Ce document est déjà emprunté."));
                client.close();
                return;
            }

            doc.emprunt(abonne);
            out.println(Codage.coder("Le document a bien été emprunté !"));
            client.close();

        } catch (IOException e) {
            System.err.println("Un utilisateur a interrompu sa connexion avec le serveur.");
        } catch (RestrictionException e) {
            out.println(Codage.coder(e.getMessage()));
            try {
                client.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
