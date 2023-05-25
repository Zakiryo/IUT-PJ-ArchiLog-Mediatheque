package bserveur;

import bttp.Codage;
import data.DataHandler;
import mediatheque.Abonne;
import mediatheque.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class Service implements Runnable {
    private final Socket client;
    private final BufferedReader in;
    private final PrintWriter out;

    public Service(Socket socket) throws IOException {
        this.client = socket;
        this.in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
        this.out = new PrintWriter(this.client.getOutputStream(), true);
    }

    public BufferedReader getIn() {
        return this.in;
    }

    public PrintWriter getOut() {
        return this.out;
    }

    public Socket getClient() {
        return this.client;
    }

    public Abonne checkAbonne() throws IOException {
        int numeroAbonne;
        try {
            numeroAbonne = Integer.parseInt(in.readLine());
        } catch (NumberFormatException e) {
            out.println(Codage.coder("Numéro d'abonné incorrect."));
            return null;
        }
        Abonne abonne = DataHandler.getAbonneById(numeroAbonne);
        if (abonne == null) {
            out.println(Codage.coder("Ce numéro d'abonné n'est pas enregistré."));
            return null;
        }
        return abonne;
    }

    public Document checkDocument() throws IOException {
        int numeroDocument;
        try {
            numeroDocument = Integer.parseInt(in.readLine());
        } catch (NumberFormatException e) {
            out.println(Codage.coder("Numéro de document incorrect."));
            return null;
        }
        Document document = DataHandler.getDocumentById(numeroDocument);
        if (document == null) {
            out.println(Codage.coder("Ce numéro de document n'existe pas."));
            return null;
        }
        return document;
    }
}
