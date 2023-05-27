package mediatheque;

import data.DataHandler;
import data.MailAlert;
import data.TimerHandler;
import exception.RestrictionException;

import java.sql.SQLException;

public abstract class DocumentFactory implements Document {
    private final int numero;
    private Abonne emprunteur;
    private Abonne reserveur;

    public DocumentFactory(int numero, Abonne emprunteur, Abonne reserveur) {
        this.numero = numero;
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
        synchronized (this) {
            emprunteur = ab;
            reserveur = null;
            try {
                DataHandler.updateDatabase(this);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            TimerHandler.validReservation(this);
        }
    }

    @Override
    public void retour() {
        synchronized (this) {
            reserveur = null;
            emprunteur = null;
            MailAlert.sendMailAlert(this);
            try {
                DataHandler.updateDatabase(this);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
