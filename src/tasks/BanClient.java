package tasks;

import mediatheque.Abonne;

import java.util.TimerTask;

public class BanClient extends TimerTask {
    private final Abonne abonne;

    public BanClient(Abonne abonne) {
        this.abonne = abonne;
    }

    @Override
    public void run() {
        abonne.setBanned(true);
        cancel();
    }
}
