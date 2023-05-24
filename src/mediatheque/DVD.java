package mediatheque;

import data.DataHandler;
import exception.RestrictionException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DVD implements Document {
    private final int numero;
    private final boolean adulte;

    public DVD(int numero, boolean adulte) {
        this.numero = numero;
        this.adulte = adulte;
    }

    @Override
    public int numero() {
        return numero;
    }

    @Override
    public Abonne empruntePar() {
        try {
            PreparedStatement psEmpruntePar;
            ResultSet resEmpruntePar;
            synchronized (DataHandler.getConnection()) {
                psEmpruntePar = DataHandler.getConnection().prepareStatement("SELECT EMPRUNTE_PAR FROM DOCUMENT WHERE NUMERO = ?");
                psEmpruntePar.setInt(1, numero);
                resEmpruntePar = psEmpruntePar.executeQuery();
            }
            if (resEmpruntePar.next()) {
                int emprunteParId = resEmpruntePar.getInt("EMPRUNTE_PAR");
                Abonne empruntePar = DataHandler.getAbonneById(emprunteParId);
                resEmpruntePar.close();
                psEmpruntePar.close();
                return empruntePar;
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
            PreparedStatement psReservePar;
            ResultSet resReservePar;
            synchronized (DataHandler.getConnection()) {
                psReservePar = DataHandler.getConnection().prepareStatement("SELECT RESERVE_PAR FROM DOCUMENT WHERE NUMERO = ?");
                psReservePar.setInt(1, numero);
                resReservePar = psReservePar.executeQuery();
            }
            if (resReservePar.next()) {
                int reserveParId = resReservePar.getInt("RESERVE_PAR");
                Abonne reservePar = DataHandler.getAbonneById(reserveParId);
                resReservePar.close();
                psReservePar.close();
                return reservePar;
            }
            resReservePar.close();
            psReservePar.close();
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reservation(Abonne ab) throws RestrictionException {
        if (adulte && ab.getAge() < 18) {
            throw new RestrictionException("Vous n'avez pas l'âge requis pour réserver ce document.");
        }
        PreparedStatement psReservation;
        try {
            synchronized (DataHandler.getConnection()) {
                assert (reservePar() == null && empruntePar() == null);
                psReservation = DataHandler.getConnection().prepareStatement("UPDATE DOCUMENT SET RESERVE_PAR = ? WHERE NUMERO = ?");
                psReservation.setInt(1, ab.getNumero());
                psReservation.setInt(2, numero);
                psReservation.executeUpdate();
            }
            psReservation.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void emprunt(Abonne ab) throws RestrictionException {
        if (adulte && ab.getAge() < 18) {
            throw new RestrictionException("Vous n'avez pas l'âge requis pour emprunter ce document.");
        }
        PreparedStatement psEmprunt;
        try {
            synchronized (DataHandler.getConnection()) {
                // Demander au prof si le if throw est pas mieux / suffit dans notre situation
                // assert (reservePar() == null || reservePar() == ab);
                if (reservePar() != null && reservePar() != ab) {
                    throw new RestrictionException("Ce document a déjà été réservé ou emprunté.");
                }
                psEmprunt = DataHandler.getConnection().prepareStatement("UPDATE DOCUMENT SET EMPRUNTE_PAR = ?, RESERVE_PAR = NULL WHERE NUMERO = ?");
                psEmprunt.setInt(1, ab.getNumero());
                psEmprunt.setInt(2, numero);
                psEmprunt.executeUpdate();
            }
            psEmprunt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void retour() {
        PreparedStatement psRetour;
        try {
            synchronized (DataHandler.getConnection()) {
                psRetour = DataHandler.getConnection().prepareStatement("UPDATE DOCUMENT SET RESERVE_PAR = NULL, EMPRUNTE_PAR = NULL WHERE NUMERO = ?");
                psRetour.setInt(1, numero);
                psRetour.executeUpdate();
                DataHandler.sendMailAlert(this);
            }
            psRetour.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
