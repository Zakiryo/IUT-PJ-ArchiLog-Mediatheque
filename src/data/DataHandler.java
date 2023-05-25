package data;

import mediatheque.Abonne;
import mediatheque.DVD;
import mediatheque.Document;
import tasks.AnnulationReservation;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.PasswordAuthentication;
import javax.mail.Authenticator;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.Date;

public final class DataHandler {
    private static final String HOST = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USER = "IUT-PJ-ArchiLog";
    private static final String PASSWORD = "12345";
    private static Connection connection = null;
    private static final List<Document> documents = new ArrayList<>();
    private static final List<Abonne> abonnes = new ArrayList<>();
    private static HashMap<Integer, TimerData> activeTimers;
    private static List<Document> mailAlerts = new ArrayList<>();

    public DataHandler() throws SQLException {
        activeTimers = new HashMap<>();
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
    }

    public static void updateDatabase(Document document) throws SQLException {
        PreparedStatement psUpdate = connection.prepareStatement("UPDATE DOCUMENT SET EMPRUNTE_PAR = ?, RESERVE_PAR = ? WHERE NUMERO = ?");
        Integer numeroEmprunteur = (document.empruntePar() != null) ? document.empruntePar().getNumero() : null;
        Integer numeroReserveur = (document.reservePar() != null) ? document.reservePar().getNumero() : null;
        if ((numeroEmprunteur == null)) {
            psUpdate.setNull(1, Types.INTEGER);
        } else {
            psUpdate.setInt(1, numeroEmprunteur);
        }
        if ((numeroReserveur == null)) {
            psUpdate.setNull(2, Types.INTEGER);
        } else {
            psUpdate.setInt(2, numeroReserveur);
        }
        psUpdate.setInt(3, document.numero());
        psUpdate.executeUpdate();
        psUpdate.close();
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

    public static void validReservation(int documentID) {
        if (activeTimers.containsKey(documentID)) {
            activeTimers.get(documentID).getTimer().cancel();
            activeTimers.remove(documentID);
        }
    }

    public static void reservationTimerTaskStart(int documentID) {
        Timer timer = new Timer();
        LocalDateTime reservationExpiration = LocalDateTime.now().plusHours(2);
        TimerTask task = new AnnulationReservation(documentID);
        Date scheduledExpirationDate = Date.from(reservationExpiration.atZone(ZoneId.systemDefault()).toInstant());
        timer.schedule(task, scheduledExpirationDate);
        activeTimers.put(documentID, new TimerData(timer, reservationExpiration));
    }

    public static void removeTimer(int documentID) {
        activeTimers.remove(documentID);
    }

    public static LocalDateTime getReservationExpirationDate(int documentID) {
        return activeTimers.get(documentID).getDate();
    }

    public static void addToAlertList(Document doc) {
        mailAlerts.add(doc);
    }

    public static void sendMailAlert(Document doc) {
        if (mailAlerts.contains(doc)) {
            Properties prop = new Properties();
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "587");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(prop, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("athomosop@gmail.com", "jccshxiopsaziezo");
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("athomosop@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("yohan.rudny@etu.u-paris.fr"));
                message.setSubject("[Médiathèque] Document n°" + doc.numero() + " disponible");
                message.setText("Bonjour grand Wakan Tanka.<br>" +
                        "Nous vous informons par ce signal de fumée que le document n°" + doc.numero() + " peut de nouveau être envouté par les papooses !<br>" +
                        "S'il est envouté, veillez bien à ce que celui-ci soit rendu dans les temps et sans dégradation au grand chef Geronimo.");
                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            mailAlerts.remove(doc);
        }
    }
}
