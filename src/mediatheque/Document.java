package mediatheque;

import exception.DocumentUnavailableException;

import java.sql.SQLException;

public interface Document {
    // return numéro du document
    int numero();

    // return null si pas emprunté ou pas réservé
    Abonne empruntePar() throws SQLException; // Abonné qui a emprunté ce document

    Abonne reservePar() throws SQLException; // Abonné qui a réservé ce document

    // precondition ni réservé ni emprunté
    void reservation(Abonne ab) throws SQLException, DocumentUnavailableException;

    // precondition libre ou réservé par l’abonné qui vient emprunter
    void emprunt(Abonne ab) throws SQLException, DocumentUnavailableException;

    // retour d’un document ou annulation d‘une réservation
    void retour();
}
