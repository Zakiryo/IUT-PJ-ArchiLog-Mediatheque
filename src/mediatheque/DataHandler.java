package mediatheque;

import bttp2.Codage;

import java.sql.*;
import java.util.ArrayList;

public final class DataHandler {
    private static final String HOST = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USER = "IUT-PJ-ArchiLog";
    private static final String PASSWORD = "12345";
    private static Connection connection = null;
    private static final ArrayList<Document> documents = new ArrayList<>();
    private static final ArrayList<Abonne> abonnes = new ArrayList<>();

    public DataHandler() throws SQLException {
        connection = DriverManager.getConnection(HOST, USER, PASSWORD);
        this.updateAbonnes();
        this.fetchAllDocuments();
    }

    public static Connection getConnection() {
        return connection;
    }

    public void fetchAllDocuments() throws SQLException {
        documents.clear();
        PreparedStatement psDocs = connection.prepareStatement("""
        SELECT doc.*, dvd.adulte
        FROM DOCUMENT doc
        LEFT JOIN DVD dvd ON doc.numero = dvd.numero
        """);
        ResultSet resDocs = psDocs.executeQuery();
        while(resDocs.next()) {
            int numero = resDocs.getInt("numero");
            String titre = resDocs.getString("titre");
            String type = resDocs.getString("type");
            boolean adulte = resDocs.getInt("adulte") == 1;

            System.out.println(numero + " " + titre + " " + type + " " + adulte);
            switch(type.toLowerCase()) {
                case "dvd":
                    documents.add(new DVD(numero, titre, adulte));
                    break;
                default:
                    throw new RuntimeException("Type de document non pris en charge par l'application.");
            }
        }
    }

    public void updateAbonnes() throws SQLException {
        abonnes.clear();
        PreparedStatement psAbonnes = connection.prepareStatement("SELECT * FROM ABONNE");
        ResultSet resAbonnes = psAbonnes.executeQuery();

        while (resAbonnes.next()) {
            abonnes.add(new Abonne(resAbonnes.getInt("numero"), resAbonnes.getString("nom"), resAbonnes.getDate("date_de_naissance")));
        }

        resAbonnes.close();
        psAbonnes.close();
    }

    public static ArrayList<Document> getDocuments() {
        return documents;
    }

    public static ArrayList<Abonne> getAbonnes() {
        return abonnes;
    }

    public static StringBuilder getCatalogue() throws SQLException {
        StringBuilder catalogue = new StringBuilder();
        PreparedStatement psTitres = connection.prepareStatement("SELECT NUMERO, TITRE, TYPE FROM DOCUMENT");
        ResultSet resTitres = psTitres.executeQuery();
        while (resTitres.next()) {
            catalogue.append(resTitres.getString("type").toUpperCase()).append(" : ")
                    .append("N°").append(resTitres.getInt("numero")).append(" - ")
                    .append(resTitres.getString("titre"))
                    .append("\n");
        }
        return catalogue;
    }

    // Essayer de factoriser getEmprunteur & getReservataire
    public static Abonne getEmprunteur(int numero) throws SQLException {
        PreparedStatement psEmpr = connection.prepareStatement("SELECT EMPRUNTE_PAR FROM DOCUMENT WHERE numero = ?");
        psEmpr.setInt(1, numero);
        ResultSet resEmpr = psEmpr.executeQuery();
        resEmpr.next();
        int numeroAbonne = resEmpr.getInt("EMPRUNTE_PAR");

        psEmpr.close();
        resEmpr.close();

        // Si pas de réservataire
        if(numeroAbonne == 0) {
            return null;
        }

        for( Abonne abonne : abonnes) {
            if(abonne.getNumero() == numeroAbonne) {
                return abonne;
            }
        }

        throw new RuntimeException("Erreur lors de la vérification de l'emprunteur.");
    }

    public static Abonne getReservataire(int numero) throws SQLException {
        PreparedStatement psReserv = connection.prepareStatement("SELECT RESERVE_PAR FROM DOCUMENT WHERE numero = ?");
        psReserv.setInt(1, numero);
        ResultSet resReserv = psReserv.executeQuery();
        resReserv.next();
        int numeroAbonne = resReserv.getInt("RESERVE_PAR");

        psReserv.close();
        resReserv.close();

        // Si pas de réservataire
        if(numeroAbonne == 0) {
            return null;
        }

        for( Abonne abonne : abonnes) {
            if(abonne.getNumero() == numeroAbonne) {
                return abonne;
            }
        }

        throw new RuntimeException("Erreur lors de la vérification du réservataire.");
    }

    public static Abonne getAbonneById(int numeroAbonne) {
        for (Abonne a : DataHandler.getAbonnes()) {
            if (a.getNumero() == numeroAbonne) {
                return a;
            }
        }

        return null;
    }
}
