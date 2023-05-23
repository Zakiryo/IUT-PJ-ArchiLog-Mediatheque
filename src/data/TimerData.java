package data;

import java.time.LocalDateTime;
import java.util.Timer;

public class TimerData {
    private final Timer timer;
    private final LocalDateTime date;

    public TimerData(Timer timer, LocalDateTime date) {
        this.timer = timer;
        this.date = date;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Timer getTimer() {
        return timer;
    }
}
