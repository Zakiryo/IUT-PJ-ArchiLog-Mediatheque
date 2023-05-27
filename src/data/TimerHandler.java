package data;

import mediatheque.Abonne;
import mediatheque.Document;
import tasks.CancelReservation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class TimerHandler {
    private static final HashMap<Document, TimerData> activeReservations = new HashMap<>();
    private static final HashMap<Abonne, TimerData> activeBorrows = new HashMap<>();

    public static void validReservation(Document document) {
        if (activeReservations.containsKey(document)) {
            activeReservations.get(document).timer().cancel();
            activeReservations.remove(document);
        }
    }

    public static void reservationTimerTaskStart(Document document) {
        Timer timer = new Timer();
        LocalDateTime reservationExpiration = LocalDateTime.now().plusHours(2);
        TimerTask task = new CancelReservation(document);
        Date scheduledExpirationDate = Date.from(reservationExpiration.atZone(ZoneId.systemDefault()).toInstant());
        timer.schedule(task, scheduledExpirationDate);
        activeReservations.put(document, new TimerData(timer, reservationExpiration));
    }

    public static void removeTimer(Document document) {
        activeReservations.remove(document);
    }

    public static LocalDateTime getReservationExpirationDate(Document document) {
        return activeReservations.get(document).date();
    }

    private record TimerData(Timer timer, LocalDateTime date) {
    }
}
