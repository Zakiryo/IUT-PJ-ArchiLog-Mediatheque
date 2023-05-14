package mediatheque;

import java.time.LocalDate;
import java.time.Period;

public class Abonne {
    private final int numero;
    private final LocalDate dateNaissance;

    public Abonne(int numero, LocalDate dateNaissance) {
        this.numero = numero;
        this.dateNaissance = dateNaissance;
    }

    public int getNumero() {
        return numero;
    }

    public int getAge() {
        LocalDate currentDate = LocalDate.now();
        Period age = Period.between(dateNaissance, currentDate);
        return age.getYears();
    }
}
