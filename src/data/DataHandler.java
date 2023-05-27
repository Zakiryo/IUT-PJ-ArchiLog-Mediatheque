package data;

import mediatheque.*;

import java.sql.*;
import java.util.*;

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

    public static Abonne getAbonneById(int numero) {
        for (Abonne a : abonnes) {
            if (a.getNumero() == numero) {
                return a;
            }
        }
        return null;
    }

    public static Document getDocumentById(int numero) {
        for (Document d : documents) {
            if (d.numero() == numero) {
                return d;
            }
        }
        return null;
    }

    private static void fetchAbonnes() throws SQLException {
        PreparedStatement psAbonnes = connection.prepareStatement("SELECT NUMERO, DATE_DE_NAISSANCE FROM ABONNE");
        ResultSet resAbonnes = psAbonnes.executeQuery();
        while (resAbonnes.next()) {
            abonnes.add(new Abonne(resAbonnes.getInt("numero"), resAbonnes.getDate("date_de_naissance").toLocalDate()));
        }
        psAbonnes.close();
        resAbonnes.close();
    }

    private static void fetchAllDocuments() throws SQLException {
        PreparedStatement psDocs = connection.prepareStatement("SELECT doc.*, dvd.adulte FROM DOCUMENT doc LEFT JOIN DVD dvd ON doc.numero = dvd.numero");
        ResultSet resDocs = psDocs.executeQuery();
        while (resDocs.next()) {
            int numero = resDocs.getInt("numero");
            String type = resDocs.getString("type");
            boolean adulte = resDocs.getInt("adulte") == 1;
            Abonne emprunteur = getAbonneById(resDocs.getInt("emprunte_par"));
            Abonne reserveur = getAbonneById(resDocs.getInt("reserve_par"));
            switch (type.toLowerCase()) {
                case "dvd" -> documents.add(new DVD(numero, adulte, emprunteur, reserveur));
                default -> throw new RuntimeException("Type de document non pris en charge par l'application.");
            }
        }
        psDocs.close();
        resDocs.close();
    }

    public static void updateDatabase(Document document) throws SQLException {
        PreparedStatement psUpdate = connection.prepareStatement("UPDATE DOCUMENT SET EMPRUNTE_PAR = ?, RESERVE_PAR = ? WHERE NUMERO = ?");
        Integer numeroEmprunteur = (document.empruntePar() != null) ? document.empruntePar().getNumero() : null;
        Integer numeroReserveur = (document.reservePar() != null) ? document.reservePar().getNumero() : null;
        psUpdate.setObject(1, numeroEmprunteur, Types.INTEGER);
        psUpdate.setObject(2, numeroReserveur, Types.INTEGER);
        psUpdate.setInt(3, document.numero());
        psUpdate.executeUpdate();
        psUpdate.close();
    }

    public static StringBuilder getCatalogue() {
        StringBuilder catalogue = new StringBuilder();
        try (PreparedStatement psTitres = connection.prepareStatement("SELECT NUMERO, TITRE, TYPE FROM DOCUMENT ORDER BY 1");
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
