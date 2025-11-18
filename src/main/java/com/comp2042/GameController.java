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
    //private HoldManager holdManager;
    //private Brick currentBrick;

    public GameController(GuiController c) {
        viewGuiController = c;
        this.scoreManager = new ScoreManager();
        this.timerManager = new TimerManager();
        //this.holdManager = new HoldManager();

        BrickGenerator generator = ((SimpleBoard) board).getBrickGenerator();
        //this.currentBrick = generator.getBrick();

        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());

        updateNextPiecePreview();
        //updateHoldDisplay();

        timerManager.start();
    }

    private void updateNextPiecePreview() {
        BrickGenerator generator = ((SimpleBoard) board).getBrickGenerator();
        if (generator instanceof RandomBrickGenerator) {
            RandomBrickGenerator boardGenerator = (RandomBrickGenerator) generator;
            List<Brick> nextBricks  = boardGenerator.getNextBricks(2);
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
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                scoreManager.addLinesCleared(clearRow.getLinesRemoved());
                viewGuiController.updateScoreDisplay(scoreManager.getScore(), scoreManager.getLinesCleared());
            }
            if (board.createNewBrick()) {
                viewGuiController.gameOver();
                timerManager.pause();
            } else {
                BrickGenerator generator = ((SimpleBoard) board).getBrickGenerator();
                //this.currentBrick = generator.getBrick();
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());
            updateNextPiecePreview();

            //holdManager.resetHold();
        } else {
            if (event.getEventSource() == EventSource.USER) {
                scoreManager.addSoftDropPoints();
                viewGuiController.updateScoreDisplay(scoreManager.getScore(), scoreManager.getLinesCleared());
            }
        }

        updateGhostPiece();
        viewGuiController.updateTimerDisplay(timerManager.getFormattedTime());

        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        if (isPaused) return board.getViewData();
        board.moveBrickLeft();
        updateGhostPiece();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        if (isPaused) return board.getViewData();
        board.moveBrickRight();
        updateGhostPiece();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        if (isPaused) return board.getViewData();
        board.rotateLeftBrick();
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

        scoreManager.addHardDropPoints(dropDistance);
        viewGuiController.updateScoreDisplay(scoreManager.getScore(), scoreManager.getLinesCleared());

        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();
        if (clearRow.getLinesRemoved() > 0) {
            scoreManager.addLinesCleared(clearRow.getLinesRemoved());
            viewGuiController.updateScoreDisplay(scoreManager.getScore(), scoreManager.getLinesCleared());
        }

        if (board.createNewBrick()) {
            viewGuiController.gameOver();
            timerManager.pause();
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        updateNextPiecePreview();
        updateGhostPiece();
        viewGuiController.updateTimerDisplay(timerManager.getFormattedTime());
        viewGuiController.showHardDropEffect();
        //holdManager.resetHold();

        return board.getViewData();
    }

    /*@Override
    public ViewData onHoldEvent(MoveEvent event) {
        if (!holdManager.canHold()) {
            System.out.println("❌ CANNOT HOLD - hold used already for current piece");
            return board.getViewData();
        }

        System.out.println("=== HOLD DEBUG ===");
        System.out.println("🎮 Current brick on board: " + currentBrick.getClass().getSimpleName());
        System.out.println("💾 Currently in hold: " + (holdManager.getHeldBrick() != null ? holdManager.getHeldBrick().getClass().getSimpleName() : "EMPTY"));

        // ✅ Use the ACTUAL current brick, not some random next brick
        HoldManager.BrickResult result = holdManager.holdBrick(currentBrick);

        System.out.println("🔄 Hold result:");
        System.out.println("   - shouldGetNewBrick: " + result.shouldGetNewBrick);
        System.out.println("   - brickToUse: " + (result.brick != null ? result.brick.getClass().getSimpleName() : "NULL"));

        if (result.shouldGetNewBrick) {
            System.out.println("🎯 FIRST HOLD: Storing " + currentBrick.getClass().getSimpleName() + ", getting NEW brick");
            // Get new brick and track it
            BrickGenerator generator = ((SimpleBoard) board).getBrickGenerator();
            this.currentBrick = generator.getBrick();
            ((SimpleBoard) board).setCurrentBrick(this.currentBrick);
        } else if (result.brick != null) {
            System.out.println("🔄 SWAPPING: Taking " + result.brick.getClass().getSimpleName() + " from hold");
            // Use the held brick and track it
            this.currentBrick = result.brick;
            ((SimpleBoard) board).setCurrentBrick(this.currentBrick);
        }

        updateHoldDisplay();
        updateGhostPiece();
        updateNextPiecePreview();

        System.out.println("✅ Hold complete. Current brick: " + currentBrick.getClass().getSimpleName());
        return board.getViewData();
    }


    private void updateHoldDisplay() {
        Brick heldBrick = holdManager.getHeldBrick();
        viewGuiController.updateHoldDisplay(heldBrick, holdManager.canHold());
    }

     */

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
        //holdManager.reset();

        viewGuiController.updateScoreDisplay(0, 0); // resets display
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        viewGuiController.updateHoldDisplay(null, true);
        updateNextPiecePreview();
        updateGhostPiece();
    }
}
