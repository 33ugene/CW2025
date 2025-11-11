package com.comp2042;

public class ScoreManager {
    private int score;
    private int linesCleared;

    // Standard point counting system for tetris
    private static final int SINGLE_LINE = 100;
    private static final int DOUBLE_LINE = 300;
    private static final int TRIPLE_LINE = 500;
    private static final int TETRIS = 800;
    private static final int SOFT_DROP_POINTS = 1;
    private static final int HARD_DROP_POINTS = 2;

    public ScoreManager() {
        this.score = 0;
        this.linesCleared = 0;
    }

    public void addLinesCleared(int lines) {
        this.linesCleared += lines;

        // Point calculating system
        int points = switch (lines) {
            case 1 -> SINGLE_LINE;
            case 2 -> DOUBLE_LINE;
            case 3 -> TRIPLE_LINE;
            case 4 -> TETRIS;
            default -> 0;
        };

        score += points;
    }

    public void addSoftDropPoints() {
        score += SOFT_DROP_POINTS;
    }

    public void addHardDropPoints(int distance) {
        score += HARD_DROP_POINTS * distance;
    }


    public int getScore() {return score;}
    public int getLinesCleared() {return linesCleared;}

    public void reset() {
        score = 0;
        linesCleared = 0;
    }
}
