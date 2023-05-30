package server.mediatheque;

import server.data.DataHandler;
import server.data.MailAlert;
import server.data.TimerHandler;
import server.exception.RestrictionException;

import java.sql.SQLException;
import java.time.LocalDateTime;

public abstract class AbstractDocument implements Document {
    private final int numero;
    private Abonne emprunteur;
    private Abonne reserveur;
    private LocalDateTime dateRetour;

    public AbstractDocument(int numero, Abonne emprunteur, Abonne reserveur) {
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
        }
        try {
            DataHandler.updateDatabase(this);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void emprunt(Abonne ab) throws RestrictionException {
        assert (reservePar() != null && reservePar() != ab);
        synchronized (this) {
            emprunteur = ab;
            reserveur = null;
            dateRetour = LocalDateTime.now().plusMonths(1); // date limite de retour fixée à 1 mois après l'emprunt
        }
        try {
            DataHandler.updateDatabase(this);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        TimerHandler.validReservation(this);
        TimerHandler.borrowTimerTaskStart(this);
    }

    @Override
    public void retour() {
        synchronized (this) {
            reserveur = null;
            emprunteur = null;
        }
        try {
            DataHandler.updateDatabase(this);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        MailAlert.sendMailAlert(this);
        TimerHandler.resetBorrow(this);
    }

    @Override
    public LocalDateTime dateRetour() {
        return dateRetour;
    }
}
