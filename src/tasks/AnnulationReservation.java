package tasks;

import Data.DataHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.TimerTask;

public class AnnulationReservation extends TimerTask {

    private final int documentID;
    private final LocalDateTime reservationExpirationDate;

    public AnnulationReservation(int documentID, LocalDateTime reservationExpirationDate) {
        this.documentID = documentID;
        this.reservationExpirationDate = reservationExpirationDate;
    }
    @Override
    public void run() {
        PreparedStatement psAnnulationReservation;

        try {
            synchronized (DataHandler.getConnection()) {
                psAnnulationReservation = DataHandler.getConnection().prepareStatement("UPDATE DOCUMENT SET EMPRUNTE_PAR = NULL, RESERVE_PAR = NULL WHERE NUMERO = ?");
                psAnnulationReservation.setInt(1, documentID);
                psAnnulationReservation.executeUpdate();
            }
            psAnnulationReservation.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Eh il a pas emprunté à temps");
        DataHandler.removeTimer(documentID);
        cancel();
    }

    public LocalDateTime getReservationExpirationDate() {
        return reservationExpirationDate;
    }
}
