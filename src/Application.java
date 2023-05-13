import serveur.Serveur;
import services.ServiceEmprunt;
import services.ServiceReservation;
import services.ServiceRetour;

import java.io.IOException;

public class Application {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        new Thread(new Serveur(ServiceReservation.class, 1000)).start();
        new Thread(new Serveur(ServiceEmprunt.class, 1001)).start();
        new Thread(new Serveur(ServiceRetour.class, 1002)).start();
        Class.forName("mediatheque.DataHandler").newInstance();
    }
}
