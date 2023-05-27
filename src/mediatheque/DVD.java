package mediatheque;

import exception.RestrictionException;

public class DVD extends DocumentFactory implements Document {
    private final boolean adulte;

    public DVD(int numero, boolean adulte, Abonne emprunteur, Abonne reserveur) {
        super(numero, emprunteur, reserveur);
        this.adulte = adulte;
    }

    @Override
    public int numero() {
        return super.numero();
    }

    @Override
    public Abonne empruntePar() {
        return super.empruntePar();
    }

    @Override
    public Abonne reservePar() {
        return super.reservePar();
    }

    @Override
    public void reservation(Abonne ab) throws RestrictionException {
        assert (reservePar() == null && empruntePar() == null);
        if (adulte && ab.getAge() < 18) {
            throw new RestrictionException("Vous n'avez pas l'âge requis pour réserver ce document.");
        }
        super.reservation(ab);
    }

    @Override
    public void emprunt(Abonne ab) throws RestrictionException {
        assert (reservePar() != null && reservePar() != ab);
        if (adulte && ab.getAge() < 18) {
            throw new RestrictionException("Vous n'avez pas l'âge requis pour emprunter ce document.");
        }
        super.emprunt(ab);
    }

    @Override
    public void retour() {
        super.retour();
    }
}
