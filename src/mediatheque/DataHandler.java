package mediatheque;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class DataHandler {
    private static final String HOST = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USER = "IUT-PJ-ArchiLog";
    private static final String PASSWORD = "12345";
    private static Connection connection = null;
    private static final List<Document> documents = new ArrayList<>();
    private static final List<Abonne> abonnes = new ArrayList<>();

    public DataHandler() throws SQLException {
        connection = DriverManager.getConnection(HOST, USER, PASSWORD);
        fetchAbonnes();
        fetchAllDocuments();
    }

    public static Connection getConnection() {
        return connection;
    }

    public static List<Document> getDocuments() {
        return documents;
    }

    public static Abonne getAbonneById(int numero) {
        for (Abonne a : abonnes) {
            if (a.getNumero() == numero) {
                return a;
            }
        }
        return null;
    }

    private static void fetchAllDocuments() throws SQLException {
        PreparedStatement psDocs = connection.prepareStatement("SELECT doc.*, dvd.adulte FROM DOCUMENT doc LEFT JOIN DVD dvd ON doc.numero = dvd.numero");
        ResultSet resDocs = psDocs.executeQuery();
        while (resDocs.next()) {
            int numero = resDocs.getInt("numero");
            String type = resDocs.getString("type");
            boolean adulte = resDocs.getInt("adulte") == 1;
            switch (type.toLowerCase()) {
                case "dvd" -> documents.add(new DVD(numero, adulte));
                default -> throw new RuntimeException("Type de document non pris en charge par l'application.");
            }
        }
    }

    private static void fetchAbonnes() throws SQLException {
        try (PreparedStatement psAbonnes = connection.prepareStatement("SELECT NUMERO, DATE_DE_NAISSANCE FROM ABONNE");
             ResultSet resAbonnes = psAbonnes.executeQuery()) {
            while (resAbonnes.next()) {
                abonnes.add(new Abonne(resAbonnes.getInt("numero"), resAbonnes.getDate("date_de_naissance").toLocalDate()));
            }
        }
    }

    public static StringBuilder getCatalogue() {
        StringBuilder catalogue = new StringBuilder();
        try (PreparedStatement psTitres = connection.prepareStatement("SELECT NUMERO, TITRE, TYPE FROM DOCUMENT");
             ResultSet resTitres = psTitres.executeQuery()) {
            while (resTitres.next()) {
                catalogue.append(resTitres.getString("type").toUpperCase()).append(" : ")
                        .append("N°").append(resTitres.getInt("numero")).append(" - ")
                        .append(resTitres.getString("titre"))
                        .append("\n");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du catalogue : " + e.getMessage());
        }
        return catalogue;
    }
}
