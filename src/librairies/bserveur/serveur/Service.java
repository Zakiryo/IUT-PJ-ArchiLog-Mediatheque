package librairies.bserveur.serveur;

import librairies.bttp2.Codage;
import server.data.DataHandler;
import server.data.TimerHandler;
import server.mediatheque.Abonne;
import server.mediatheque.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;

public abstract class Service implements Runnable {
    private final Socket client;
    private final BufferedReader in;
    private final PrintWriter out;

    public Service(Socket socket) throws IOException {
        this.client = socket;
        this.in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
        this.out = new PrintWriter(this.client.getOutputStream(), true);
    }

    protected BufferedReader getIn() {
        return this.in;
    }

    protected PrintWriter getOut() {
        return this.out;
    }

    protected Socket getClient() {
        return this.client;
    }

    protected Abonne checkAbonne() throws IOException {
        int numeroAbonne;

        try {
            numeroAbonne = Integer.parseInt(in.readLine());
        } catch (NumberFormatException e) {
            out.println(Codage.coder("Numéro d'abonné incorrect. Merci de retaper le numéro.\n" + "> "));
            return null;
        }

        Abonne abonne = DataHandler.getAbonneById(numeroAbonne);
        if (abonne == null) {
            out.println(Codage.coder("Ce numéro d'abonné n'est pas enregistré. Merci de retaper le numéro.\n" + "> "));
            return null;
        }

        if (abonne.isBanned()) {
            LocalDateTime banExpiration = TimerHandler.getUnbanDateTime(abonne);
            out.println(Codage.coder("Cet abonné est banni de la médiathèque jusqu'au "
                    + banExpiration.getDayOfMonth() + "/" + banExpiration.getMonthValue() + "/" + banExpiration.getYear() +
                    " à " + banExpiration.getHour() + "h" + banExpiration.getMinute() + "."));
            client.close();
        }
        return abonne;
    }

    protected Document checkDocument() throws IOException {
        int numeroDocument;

        try {
            numeroDocument = Integer.parseInt(in.readLine());
        } catch (NumberFormatException e) {
            out.println(Codage.coder("Numéro de document incorrect. Merci de retaper le numéro.\n" + "> "));
            return null;
        }

        Document document = DataHandler.getDocumentById(numeroDocument);
        if (document == null) {
            out.println(Codage.coder("Ce numéro de document n'existe pas. Merci de retaper le numéro.\n" + "> "));
            return null;
        }
        return document;
    }
}
