package mediatheque;

import exception.DocumentUnavailableException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DVD implements Document {

    private final int numero;
    private final String titre;
    private final boolean adulte;

    public DVD(int numero, String titre, boolean adulte) {
        this.numero = numero;
        this.titre = titre;
        this.adulte = adulte;
    }

    @Override
    public int numero() {
        return numero;
    }

    @Override
    public Abonne empruntePar() throws SQLException {
        return DataHandler.getEmprunteur(this.numero);
    }

    @Override
    public Abonne reservePar() throws SQLException {
        return DataHandler.getReservataire(this.numero);
    }

    @Override
    synchronized public void reservation(Abonne ab) throws SQLException, DocumentUnavailableException {

        Connection conn = DataHandler.getConnection();
        conn.setAutoCommit(false);

        PreparedStatement psEmprReserv = conn.prepareStatement("SELECT EMPRUNTE_PAR, RESERVE_PAR FROM DOCUMENT WHERE numero = ? FOR UPDATE");
        psEmprReserv.setInt(1, numero);
        ResultSet resEmprReserv = psEmprReserv.executeQuery();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        resEmprReserv.next();
        boolean isAvailable = resEmprReserv.getInt("EMPRUNTE_PAR") == 0 && resEmprReserv.getInt("RESERVE_PAR") == 0;


        // Si pas de réservataire
        if (!isAvailable) {
            psEmprReserv.close();
            resEmprReserv.close();
            throw new DocumentUnavailableException();
        }

        PreparedStatement psReservation = conn.prepareStatement("UPDATE DOCUMENT SET RESERVE_PAR = ? WHERE NUMERO = ?");
        psReservation.setInt(1, ab.getNumero());
        psReservation.setInt(2, numero);
        psReservation.executeUpdate();
        conn.commit();
        conn.setAutoCommit(true);

        psEmprReserv.close();
        resEmprReserv.close();
        psReservation.close();
    }

    // A faire
    @Override
    public void emprunt(Abonne ab) throws SQLException, DocumentUnavailableException {
    }

    // A faire
    @Override
    public void retour() {
    }
}
