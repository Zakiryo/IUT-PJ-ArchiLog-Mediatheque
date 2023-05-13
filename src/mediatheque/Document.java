package mediatheque;

import exception.RestrictionException;

import java.sql.SQLException;

public interface Document {
    // return numéro du document
    int numero();

    // return null si pas emprunté ou pas réservé
    Abonne empruntePar();

    Abonne reservePar();

    // precondition ni réservé ni emprunté
    void reservation(Abonne ab) throws RestrictionException;

    // precondition libre ou réservé par l’abonné qui vient emprunter
    void emprunt(Abonne ab) throws RestrictionException;

    // retour d’un document ou annulation d‘une réservation
    void retour();
}
