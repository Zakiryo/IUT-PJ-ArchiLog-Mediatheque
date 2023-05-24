import bserveur.Serveur;
import services.ServiceEmprunt;
import services.ServiceReservation;
import services.ServiceRetour;

import javax.mail.MessagingException;
import java.io.IOException;

public class Application {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, MessagingException {
        new Thread(new Serveur(ServiceReservation.class, 1000)).start();
        new Thread(new Serveur(ServiceEmprunt.class, 1001)).start();
        new Thread(new Serveur(ServiceRetour.class, 1002)).start();

        Class.forName("data.DataHandler").newInstance();
    }
}
