package server.mediatheque;

import server.exception.RestrictionException;

import java.time.LocalDateTime;

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

    // BRETTESOFT "GERONIMO" : return date limite de retour du document fixée lors de l'emprunt
    LocalDateTime dateRetour();
}
