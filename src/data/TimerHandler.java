package data;

import mediatheque.Abonne;
import mediatheque.Document;
import tasks.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class TimerHandler {
    private static final Map<Document, TimerData> activeReservations = new ConcurrentHashMap<>(); // équivalent thread-safe à HashMap
    private static final Map<Document, TimerData> activeBorrows = new ConcurrentHashMap<>();
    private static final Map<Abonne, TimerData> banList = new ConcurrentHashMap<>();

    public static void reservationTimerTaskStart(Document document) {
        Timer timer = new Timer();
        LocalDateTime reservationExpiration = LocalDateTime.now().plusHours(2);
        TimerTask task = new CancelReservation(document);
        Date scheduledExpirationDate = Date.from(reservationExpiration.atZone(ZoneId.systemDefault()).toInstant());
        timer.schedule(task, scheduledExpirationDate);
        activeReservations.put(document, new TimerData(timer, reservationExpiration));
    }

    public static void borrowTimerTaskStart(Document document) {
        Timer timer = new Timer();
        LocalDateTime borrowTimeLimit = document.dateRetour().plusWeeks(2);
        TimerTask task = new BanClient(document);
        Date scheduledExpirationDate = Date.from(borrowTimeLimit.atZone(ZoneId.systemDefault()).toInstant());
        timer.schedule(task, scheduledExpirationDate);
        activeBorrows.put(document, new TimerData(timer, borrowTimeLimit));
    }

    public static void addToBanList(Abonne bannedClient) {
        Timer timer = new Timer();
        LocalDateTime unbanDate = LocalDateTime.now().plusMonths(1);
        TimerTask task = new UnbanClient(bannedClient);
        Date scheduledExpirationDate = Date.from(unbanDate.atZone(ZoneId.systemDefault()).toInstant());
        timer.schedule(task, scheduledExpirationDate);
        banList.put(bannedClient, new TimerData(timer, unbanDate));
    }

    public static void validReservation(Document document) {
        if (activeReservations.containsKey(document)) {
            activeReservations.get(document).timer().cancel();
            activeReservations.remove(document);
        }
    }

    public static void resetBorrow(Document document) {
        if (activeBorrows.containsKey(document)) {
            activeBorrows.get(document).timer().cancel();
            activeBorrows.remove(document);
        }
    }

    public static void removeFromReservations(Document document) {
        activeReservations.remove(document);
    }

    public static void removeFromBanList(Abonne unbannedClient) {
        banList.remove(unbannedClient);
    }

    public static LocalDateTime getReservationExpirationDate(Document document) {
        return activeReservations.get(document).date();
    }

    public static LocalDateTime getUnbanDateTime(Abonne bannedUser) {
        return banList.get(bannedUser).date();
    }

    private record TimerData(Timer timer, LocalDateTime date) { // classe Java concise ne comportant que des données immuables
    }
}
