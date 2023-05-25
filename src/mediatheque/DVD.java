package mediatheque;

import data.DataHandler;
import exception.RestrictionException;

import java.sql.SQLException;

public class DVD implements Document {
    private final int numero;
    private final boolean adulte;
    private Abonne emprunteur;
    private Abonne reserveur;

    public DVD(int numero, boolean adulte, Abonne emprunteur, Abonne reserveur) {
        this.numero = numero;
        this.adulte = adulte;
        this.emprunteur = emprunteur;
        this.reserveur = reserveur;
    }

    @Override
    public int numero() {
        return numero;
    }

    @Override
    public Abonne empruntePar() {
        return emprunteur;
    }

    @Override
    public Abonne reservePar() {
        return reserveur;
    }

    @Override
    public void reservation(Abonne ab) throws RestrictionException {
        assert (reservePar() == null && empruntePar() == null);
        if (adulte && ab.getAge() < 18) {
            throw new RestrictionException("Vous n'avez pas l'âge requis pour réserver ce document.");
        }
        synchronized (this) {
            reserveur = ab;
            try {
                DataHandler.updateDatabase(this);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void emprunt(Abonne ab) throws RestrictionException {
        assert (reservePar() != null && reservePar() != ab);
        if (adulte && ab.getAge() < 18) {
            throw new RestrictionException("Vous n'avez pas l'âge requis pour emprunter ce document.");
        }
        synchronized (this) {
            emprunteur = ab;
            reserveur = null;
            try {
                DataHandler.updateDatabase(this);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void retour() {
        synchronized (this) {
            reserveur = null;
            emprunteur = null;
            DataHandler.sendMailAlert(this);
            try {
                DataHandler.updateDatabase(this);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
