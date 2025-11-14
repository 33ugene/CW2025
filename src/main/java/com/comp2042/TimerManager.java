package com.comp2042;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class TimerManager {
    private Timeline timeline;
    private long startTime;
    private long elapsedTime;
    private boolean isRunning;

    public TimerManager() {
        this.elapsedTime = 0;
        this.isRunning = false;

        // Create timeline that updates every second
        timeline = new Timeline(new KeyFrame(
                Duration.seconds(1),
                ae -> updateTimer()
        ));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    public void start() {
        if (!isRunning) {
            startTime = System.currentTimeMillis() - elapsedTime;
            timeline.play();
            isRunning = true;
        }
    }

    public void pause() {
        if (isRunning) {
            timeline.pause();
            isRunning = false;
            // Update elapsed time when pausing
            elapsedTime = System.currentTimeMillis() - startTime;
        }
    }

    public void reset() {
        timeline.stop();
        elapsedTime = 0;
        isRunning = false;
    }

    private void updateTimer() {
        elapsedTime = System.currentTimeMillis() - startTime;
    }

    // Format time as MM:SS
    public String getFormattedTime() {
        long seconds = elapsedTime / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // Get raw elapsed time in seconds (useful for scoring)
    public long getElapsedSeconds() {
        return elapsedTime / 1000;
    }

    public boolean isRunning() {
        return isRunning;
    }
}