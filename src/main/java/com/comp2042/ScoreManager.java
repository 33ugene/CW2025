package com.comp2042;

public class ScoreManager {
    private int score;
    private int linesCleared;
    private int level = 1;

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
        this.level = 1;
    }

    public void addLinesCleared(int lines) {
        this.linesCleared += lines;

        // Point calculating system
        int basePoints = switch (lines) {
            case 1 -> SINGLE_LINE;
            case 2 -> DOUBLE_LINE;
            case 3 -> TRIPLE_LINE;
            case 4 -> TETRIS;
            default -> 0;
        };

        int points = basePoints * (level + 1);
        score += points;
        updateLevel();
    }

    private void updateLevel() {
        int newLevel = (linesCleared/10) + 1;
        if (newLevel > level) {
            level = newLevel;
            onLevelUp();
        }
    }

    private void onLevelUp() {
        System.out.println("Level UP! Now level: " + level);
    }

    public int getLevel() {
        return level;
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
        level = 1;
    }
}
