package data;

import mediatheque.Document;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MailAlert {
    private static final List<Document> mailAlerts = new ArrayList<>();

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
