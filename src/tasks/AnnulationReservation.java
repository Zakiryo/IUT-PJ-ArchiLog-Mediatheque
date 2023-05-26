package tasks;

import data.DataHandler;
import mediatheque.Document;

import java.util.Objects;
import java.util.TimerTask;

public class AnnulationReservation extends TimerTask {

    private final Document document;

    public AnnulationReservation(Document document) {
        this.document = document;
    }

    @Override
    public void run() {
        document.retour();
        DataHandler.removeTimer(document);
        cancel();
    }
}
