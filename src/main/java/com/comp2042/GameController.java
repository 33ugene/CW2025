package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.*;
import java.util.List;

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(25, 10);
    private ScoreManager scoreManager;
    private final GuiController viewGuiController;
    private TimerManager timerManager;
    private boolean isPaused = false;
    private int lastLevel = 1;
    private final HoldManager holdManager;
    private final HighScoreManager highScoreManager;
    private final SoundManager soundManager;

    public GameController(GuiController c) {
        viewGuiController = c;
        this.scoreManager = new ScoreManager();
        this.timerManager = new TimerManager();
        this.lastLevel = 1;

        holdManager = new HoldManager();
        highScoreManager = new HighScoreManager();
        soundManager = new SoundManager();
        
        // Display initial high score
        viewGuiController.updateHighScore(highScoreManager.getHighScore());

        BrickGenerator generator = ((SimpleBoard) board).getBrickGenerator();
        //this.currentBrick = generator.getBrick();

        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());

        viewGuiController.updateHoldDisplay(holdManager.getHeldBrick(), holdManager.canHold());
        updateNextPiecePreview();
        viewGuiController.updateGameSpeed(1);
        timerManager.start();

    }

    private void updateNextPiecePreview() {
        BrickGenerator generator = ((SimpleBoard) board).getBrickGenerator();
        if (generator instanceof RandomBrickGenerator) {
            RandomBrickGenerator boardGenerator = (RandomBrickGenerator) generator;
            List<Brick> nextBricks  = boardGenerator.getNextBricks(3);
            if (nextBricks != null && !nextBricks.isEmpty()) {
                viewGuiController.updateNextPiecePreview(nextBricks);
            }

        }
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        if (isPaused) {
            System.out.println("Game paused - input ignored");
            return new DownData(null, board.getViewData());
        }
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            board.mergeBrickToBackground();
            soundManager.playSound("lock");
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                soundManager.playLineClear(clearRow.getLinesRemoved());
                // Animate line clear, then refresh board after animation
                viewGuiController.animateLineClear(clearRow.getClearedRowIndices(), clearRow.getLinesRemoved(), 
                    () -> viewGuiController.refreshGameBackground(board.getBoardMatrix()));
                scoreManager.addLinesCleared(clearRow.getLinesRemoved());
                viewGuiController.updateScoreDisplay(scoreManager.getScore(), scoreManager.getLinesCleared(), scoreManager.getLevel());
                checkLevelSpeedIncrease();
            } else {
                // No lines cleared, refresh immediately
                viewGuiController.refreshGameBackground(board.getBoardMatrix());
            }
            holdManager.resetHold();
            viewGuiController.updateHoldDisplay(holdManager.getHeldBrick(), holdManager.canHold());

            if (board.createNewBrick()) {
                // Check for new high score before game over
                int finalScore = scoreManager.getScore();
                boolean isNewHighScore = highScoreManager.updateHighScore(finalScore);
                soundManager.playSound("game_over");
                viewGuiController.gameOver(finalScore, isNewHighScore);
                timerManager.pause();
            } else {
                BrickGenerator generator = ((SimpleBoard) board).getBrickGenerator();
                //this.currentBrick = generator.getBrick();
            }
            updateNextPiecePreview();

            //holdManager.resetHold();
        } else {
            if (event.getEventSource() == EventSource.USER) {
                soundManager.playSound("drop");
                scoreManager.addSoftDropPoints();
                viewGuiController.updateScoreDisplay(scoreManager.getScore(), scoreManager.getLinesCleared(), scoreManager.getLevel());
            }
        }

        updateGhostPiece();
        viewGuiController.updateTimerDisplay(timerManager.getFormattedTime());

        return new DownData(clearRow, board.getViewData());
    }

    private void checkLevelSpeedIncrease() {
        int currentLevel = scoreManager.getLevel();

        if (currentLevel > lastLevel) {
            System.out.println(" Level up " + lastLevel + "->" + currentLevel);
            soundManager.playSound("level_up");
            viewGuiController.updateGameSpeed(currentLevel);
            lastLevel = currentLevel;
        }
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        if (isPaused) return board.getViewData();
        board.moveBrickLeft();
        soundManager.playSound("move");
        updateGhostPiece();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        if (isPaused) return board.getViewData();
        board.moveBrickRight();
        soundManager.playSound("move");
        updateGhostPiece();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        if (isPaused) return board.getViewData();
        board.rotateLeftBrick();
        soundManager.playSound("rotate");
        updateGhostPiece();
        return board.getViewData();
    }

    @Override
    public ViewData onHardDropEvent(MoveEvent event) {
        if (isPaused) return board.getViewData();
        int dropDistance = calculateDropDistance();

        for (int i = 0; i < dropDistance; i++) {
            board.moveBrickDown();
        }

        soundManager.playSound("hard_drop");
        scoreManager.addHardDropPoints(dropDistance);
        viewGuiController.updateScoreDisplay(scoreManager.getScore(), scoreManager.getLinesCleared(), scoreManager.getLevel());

        board.mergeBrickToBackground();
        soundManager.playSound("lock");
        ClearRow clearRow = board.clearRows();
        if (clearRow.getLinesRemoved() > 0) {
            soundManager.playLineClear(clearRow.getLinesRemoved());
            viewGuiController.animateLineClear(clearRow.getClearedRowIndices(), clearRow.getLinesRemoved(),
                () -> viewGuiController.refreshGameBackground(board.getBoardMatrix()));
            scoreManager.addLinesCleared(clearRow.getLinesRemoved());
            viewGuiController.updateScoreDisplay(scoreManager.getScore(), scoreManager.getLinesCleared(), scoreManager.getLevel());
            checkLevelSpeedIncrease();
        } else {
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
        }

        holdManager.resetHold();
        viewGuiController.updateHoldDisplay(holdManager.getHeldBrick(), holdManager.canHold());

        if (board.createNewBrick()) {
            // Check for new high score before game over
            int finalScore = scoreManager.getScore();
            boolean isNewHighScore = highScoreManager.updateHighScore(finalScore);
            viewGuiController.gameOver(finalScore, isNewHighScore);
            timerManager.pause();
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        updateNextPiecePreview();
        updateGhostPiece();
        viewGuiController.updateTimerDisplay(timerManager.getFormattedTime());
        viewGuiController.showHardDropEffect();

        return board.getViewData();
    }

    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        if (isPaused) {
            return board.getViewData();
        }

        Brick currentBrick = ((SimpleBoard) board).getCurrentBrick();
        if (currentBrick == null) {
            return board.getViewData();
        }

        HoldManager.BrickResult result = holdManager.holdBrick(currentBrick);
        if (!result.shouldGetNewBrick && result.brick == currentBrick) {
            viewGuiController.updateHoldDisplay(holdManager.getHeldBrick(), holdManager.canHold());
            return board.getViewData();
        }
        soundManager.playSound("hold");

        boolean gameOverTriggered = false;
        if (result.shouldGetNewBrick) {
            gameOverTriggered = board.createNewBrick();
        } else if (result.brick != null) {
            boolean canPlace = ((SimpleBoard) board).setCurrentBrick(result.brick);
            if (!canPlace) {
                gameOverTriggered = true;
            }
        }

        if (gameOverTriggered) {
            viewGuiController.gameOver();
            timerManager.pause();
        }

        updateNextPiecePreview();
        updateGhostPiece();
        viewGuiController.updateHoldDisplay(holdManager.getHeldBrick(), holdManager.canHold());
        return board.getViewData();
    }

    @Override
    public ViewData onPauseEvent(MoveEvent event) {
        togglePause();
        return null;
    }

    public void togglePause() {
        isPaused = !isPaused;

        if (isPaused) {
            pauseTimer();
            System.out.println("Game Paused");
        } else {
            resumeTimer();
            System.out.println("Game Resumed");
        }

        viewGuiController.updatePauseDisplay(isPaused);
    }

    public boolean isPaused() {
        return isPaused;
    }

    private int calculateDropDistance() {
        int[][] currentShape = board.getViewData().getBrickData();
        int currentX = board.getViewData().getxPosition();
        int currentY = board.getViewData().getyPosition();

        int dropDistance = 0;
        while (!checkCollision(currentShape, currentX, currentY + dropDistance + 1)) {
            dropDistance++;
        }
        return dropDistance;
    }

    private void updateGhostPiece() {
        int[][] ghostData = getGhostPiecePosition();
        viewGuiController.drawGhostPiece(ghostData);
    }

    private int[][] getGhostPiecePosition() {
        int[][] currentShape = board.getViewData().getBrickData();
        Point currentPos = new Point(board.getViewData().getxPosition(), board.getViewData().getyPosition());

        Point ghostPos = new Point(currentPos);
        while (true) {
            ghostPos.translate(0, 1);
            boolean collision = checkCollision(currentShape, (int) ghostPos.getX(), (int) ghostPos.getY());
            if (collision) {
                ghostPos.translate(0, -1);

                break;
            }
        }

        return new int[][] {
                {currentShape[0][0], currentShape[0][1], currentShape[0][2], currentShape[0][3]},
                {currentShape[1][0], currentShape[1][1], currentShape[1][2], currentShape[1][3]},
                {currentShape[2][0], currentShape[2][1], currentShape[2][2], currentShape[2][3]},
                {currentShape[3][0], currentShape[3][1], currentShape[3][2], currentShape[3][3]},
                {(int) ghostPos.getX(), (int) ghostPos.getY()} // Store position in the array
        };
    }

    private boolean checkCollision(int[][] shape, int x,int y) {
        int[][] boardMatrix = board.getBoardMatrix();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int boardX = x + j;
                    int boardY = y + i;

                    if (boardX < 0 || boardX >= boardMatrix[0].length || boardY >= boardMatrix.length || (boardY >= 0 && boardMatrix[boardY][boardX] != 0)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void pauseTimer() {
        timerManager.pause();
    }

    public void resumeTimer() {
        timerManager.start();
    }




    @Override
    public void createNewGame() {
        board.newGame();
        scoreManager.reset(); // resets the scores for new game
        timerManager.reset();
        timerManager.start();
        lastLevel = 1;

        viewGuiController.updateScoreDisplay(0, 0, 1); // resets display
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        holdManager.reset();
        viewGuiController.updateHoldDisplay(holdManager.getHeldBrick(), holdManager.canHold());
        updateNextPiecePreview();
        updateGhostPiece();
    }
}
