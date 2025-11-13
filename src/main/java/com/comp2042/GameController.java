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

    public GameController(GuiController c) {
        viewGuiController = c;
        this.scoreManager = new ScoreManager();

        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());

        updateNextPiecePreview();
    }

    private void updateNextPiecePreview() {
        BrickGenerator generator = ((SimpleBoard) board).getBrickGenerator();
        if (generator instanceof RandomBrickGenerator) {
            RandomBrickGenerator brickGenerator = (RandomBrickGenerator) generator;
            List<Brick> nextBricks  = brickGenerator.getNextBricks(2);
            if (nextBricks != null && !nextBricks.isEmpty()) {
                viewGuiController.updateNextPiecePreview(nextBricks);
            }

        }
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
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
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());

            updateNextPiecePreview();

        }
        updateGhostPiece();

        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        updateGhostPiece();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        updateGhostPiece();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        updateGhostPiece();
        return board.getViewData();
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

    @Override
    public void createNewGame() {
        board.newGame();
        scoreManager.reset(); // resets the scores for new game
        viewGuiController.updateScoreDisplay(0, 0); // resets display
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        updateNextPiecePreview();
        updateGhostPiece();
    }
}
