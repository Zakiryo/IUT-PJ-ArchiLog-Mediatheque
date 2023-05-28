package tasks;

import data.TimerHandler;
import mediatheque.Abonne;
import mediatheque.Document;

import java.util.TimerTask;

public class BanClient extends TimerTask {
    private final Document document;

    public BanClient(Document document) {
        this.document = document;
    }

    @Override
    public void run() {
        Abonne emprunteur = document.empruntePar();
        emprunteur.setBanned(true);
        TimerHandler.addToBanList(emprunteur);
        document.retour();
        cancel();
    }
}
