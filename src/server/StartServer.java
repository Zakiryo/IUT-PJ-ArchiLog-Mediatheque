package server;

import librairies.bserveur.serveur.Serveur;
import server.services.ServiceEmprunt;
import server.services.ServiceReservation;
import server.services.ServiceRetour;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class StartServer {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        new Thread(new Serveur(ServiceReservation.class, 1000)).start();
        new Thread(new Serveur(ServiceEmprunt.class, 1001)).start();
        new Thread(new Serveur(ServiceRetour.class, 1002)).start();

        Class.forName("server.data.DataHandler").getConstructor().newInstance();
    }
}
