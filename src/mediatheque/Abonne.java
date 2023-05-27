package mediatheque;

import java.time.LocalDate;
import java.time.Period;

public class Abonne {
    private final int numero;
    private final LocalDate dateNaissance;
    private boolean banned;

    public Abonne(int numero, LocalDate dateNaissance) {
        this.numero = numero;
        this.dateNaissance = dateNaissance;
    }

    public int getNumero() {
        return numero;
    }

    public int getAge() {
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }
}
