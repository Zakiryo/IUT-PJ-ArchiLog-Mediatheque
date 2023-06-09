package server.tasks;

import server.data.TimerHandler;
import server.mediatheque.Abonne;

import java.util.TimerTask;

public class UnbanClient extends TimerTask {
    private final Abonne abonne;

    public UnbanClient(Abonne abonne) {
        this.abonne = abonne;
    }

    @Override
    public void run() {
        abonne.setBanned(false);
        TimerHandler.removeFromBanList(abonne);
        cancel();
    }
}
