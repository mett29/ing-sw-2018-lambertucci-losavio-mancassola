package it.polimi.se2018.network.server;

import java.util.Timer;
import java.util.TimerTask;

public class CountdownTimer {
    private Timer timer;
    private int duration;
    private int t;

    private Runnable onTick;
    private Runnable onTimerReset;
    private Runnable onTimerEnd;

    /**
     * A timer that lasts `duration` seconds and then resets, can be reset and runs actions when the timer ends and when it resets
     * @param duration Timer duration in seconds
     * @param onTimerEnd Runnable run when the timer reaches 0
     * @param onTimerReset Runnable run every time the function `reset` is called
     * @param onTick
     */
    public CountdownTimer(int duration, Runnable onTimerEnd, Runnable onTimerReset, Runnable onTick){
        timer = new Timer();
        this.duration = duration;
        t = duration;

        this.onTimerEnd = onTimerEnd;
        this.onTimerReset = onTimerReset;
        this.onTick = onTick;
    }

    public int getDuration(){
        return duration;
    }

    /**
     * Start the countdown
     */
    public void start(){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                countdown();
                onTick.run();
            }
        }, 0, 1000);
    }

    private void countdown(){
        if(t == 0){
            onTimerEnd.run();
        } else {
            t--;
        }
    }

    /**
     * Reset timer to its initial value (`this.duration`)
     */
    public void reset(){
        t = duration;
        onTimerReset.run();
    }

    /**
     * Delete timer
     */
    public void cancel(){
        timer.cancel();
    }
}
