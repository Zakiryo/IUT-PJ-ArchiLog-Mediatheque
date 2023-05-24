package tasks;

import data.DataHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.TimerTask;

public class AnnulationReservation extends TimerTask {

    private final int documentID;

    public AnnulationReservation(int documentID) {
        this.documentID = documentID;
    }

    @Override
    public void run() {
        PreparedStatement psAnnulationReservation;
        try {
            synchronized (DataHandler.getConnection()) {
                psAnnulationReservation = DataHandler.getConnection().prepareStatement("UPDATE DOCUMENT SET EMPRUNTE_PAR = NULL, RESERVE_PAR = NULL WHERE NUMERO = ?");
                psAnnulationReservation.setInt(1, documentID);
                psAnnulationReservation.executeUpdate();
                DataHandler.sendMailAlert(DataHandler.getDocumentById(documentID));
            }
            psAnnulationReservation.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DataHandler.removeTimer(documentID);
        cancel();
    }
}
