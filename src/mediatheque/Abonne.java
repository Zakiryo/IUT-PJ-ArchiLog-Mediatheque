package mediatheque;

import java.util.Date;

public class Abonne {
    private int numero;
    private String nom;
    private Date dateNaissance;

    public Abonne(int numero, String nom, Date dateNaissance) {
        this.numero = numero;
        this.nom = nom;
        this.dateNaissance = dateNaissance;
    }

    public String getNom() {
        return nom;
    }

    public int getNumero() {
        return numero;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }
}
