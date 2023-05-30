package server.mediatheque;

import server.exception.RestrictionException;

public class DVD extends AbstractDocument {
    private final boolean adulte;

    public DVD(int numero, boolean adulte, Abonne emprunteur, Abonne reserveur) {
        super(numero, emprunteur, reserveur);
        this.adulte = adulte;
    }

    @Override
    public void reservation(Abonne ab) throws RestrictionException {
        assert (reservePar() == null && empruntePar() == null);
        if (adulte && ab.getAge() < 16) {
            throw new RestrictionException("Vous n'avez pas l'âge requis pour réserver ce document.");
        }
        super.reservation(ab);
    }

    @Override
    public void emprunt(Abonne ab) throws RestrictionException {
        assert (reservePar() != null && reservePar() != ab);
        if (adulte && ab.getAge() < 16) {
            throw new RestrictionException("Vous n'avez pas l'âge requis pour emprunter ce document.");
        }
        super.emprunt(ab);
    }
}
