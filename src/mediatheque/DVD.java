package mediatheque;

import exception.RestrictionException;

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
    public Abonne empruntePar() {
        try {
            PreparedStatement psEmpruntePar = DataHandler.getConnection().prepareStatement("SELECT EMPRUNTE_PAR FROM DOCUMENT WHERE NUMERO = ?");
            psEmpruntePar.setInt(1, numero);
            ResultSet resEmpruntePar = psEmpruntePar.executeQuery();
            resEmpruntePar.next();
            for (Abonne a : DataHandler.getAbonnes()) {
                if (a.getNumero() == resEmpruntePar.getInt("emprunte_par")) {
                    return a;
                }
            }
            resEmpruntePar.close();
            psEmpruntePar.close();
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Abonne reservePar() {
        try {
            PreparedStatement psReservePar = DataHandler.getConnection().prepareStatement("SELECT RESERVE_PAR FROM DOCUMENT WHERE NUMERO = ?");
            psReservePar.setInt(1, numero);
            ResultSet resReservePar = psReservePar.executeQuery();
            resReservePar.next();
            for (Abonne a : DataHandler.getAbonnes()) {
                if (a.getNumero() == resReservePar.getInt("reserve_par")) {
                    return a;
                }
            }
            resReservePar.close();
            psReservePar.close();
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    synchronized public void reservation(Abonne ab) throws RestrictionException {
        assert (reservePar() == null && empruntePar() == null);
        if (adulte && ab.getAge() < 18) {
            throw new RestrictionException("Vous n'avez pas l'âge requis pour réserver ce document.");
        }
        try {
            PreparedStatement psReversation = DataHandler.getConnection().prepareStatement("UPDATE DOCUMENT SET RESERVE_PAR = ? WHERE NUMERO = ?");
            psReversation.setInt(1, ab.getNumero());
            psReversation.setInt(2, numero);
            psReversation.executeUpdate();
            psReversation.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void emprunt(Abonne ab) throws RestrictionException {
        assert (reservePar() == null || reservePar() == ab);
        if (adulte && ab.getAge() < 18) {
            throw new RestrictionException("Vous n'avez pas l'âge requis pour emprunter ce document.");
        }
        try {
            PreparedStatement psEmprunt = DataHandler.getConnection().prepareStatement("UPDATE DOCUMENT SET EMPRUNTE_PAR = ?, RESERVE_PAR = NULL WHERE NUMERO = ?");
            psEmprunt.setInt(1, ab.getNumero());
            psEmprunt.setInt(2, numero);
            psEmprunt.executeUpdate();
            psEmprunt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void retour() {
        try {
            PreparedStatement psRetour = DataHandler.getConnection().prepareStatement("UPDATE DOCUMENT SET RESERVE_PAR = NULL, EMPRUNTE_PAR = NULL WHERE NUMERO = ?");
            psRetour.setInt(1, numero);
            psRetour.executeUpdate();
            psRetour.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
