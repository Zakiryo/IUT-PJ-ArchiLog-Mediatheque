package tasks;

import data.DataHandler;

import java.util.Objects;
import java.util.TimerTask;

public class AnnulationReservation extends TimerTask {

    private final int documentID;

    public AnnulationReservation(int documentID) {
        this.documentID = documentID;
    }

    @Override
    public void run() {
        Objects.requireNonNull(DataHandler.getDocumentById(documentID)).retour();
        DataHandler.removeTimer(documentID);
        cancel();
    }
}
