package tasks;

import mediatheque.Abonne;

import java.util.TimerTask;

public class UnbanClient extends TimerTask {
    private final Abonne abonne;

    public UnbanClient(Abonne abonne) {
        this.abonne = abonne;
    }

    @Override
    public void run() {
        abonne.setBanned(false);
        cancel();
    }
}
