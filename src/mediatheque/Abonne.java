package mediatheque;

import java.util.Date;

public class Abonne {
    private final int numero;
    private final String nom;
    private final Date dateNaissance;

    public Abonne(int numero, String nom, Date dateNaissance) {
        this.numero = numero;
        this.nom = nom;
        this.dateNaissance = dateNaissance;
    }

    public int getNumero() {
        return numero;
    }

    public int getAge() {
        Date current = new Date();
        long difference = current.getTime() - dateNaissance.getTime();
        long age = difference / (365 * 24 * 60 * 60 * 1000L);
        return (int) age;
    }
}
