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
            getOut().println(Codage.coder("Bienvenue au service de retour !\n Veuillez saisir votre numéro de document à retourner : \n" + "> "));
            int numeroDoc;

            try {
                numeroDoc = Integer.parseInt(getIn().readLine());
            } catch (NumberFormatException e) {
                getOut().println(Codage.coder("Numéro de document incorrect."));
                getClient().close();
                return;
            }

            for(Document doc : DataHandler.getDocuments()) {
                if(doc.numero() == numeroDoc) {
                    doc.retour();
                    getClient().close();
                    getOut().println(Codage.coder("Document rendu !"));
                    return;
                }
            }

            getClient().close();
            getOut().println(Codage.coder("Désolé, ce numéro de document n'est pas enregistré."));
        } catch (IOException e) {
            System.out.println("Un utilisateur a interrompu sa connexion avec le serveur. / Une erreur est survenue.");
        }

    }
}
