package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

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
        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }


    @Override
    public void createNewGame() {
        board.newGame();
        scoreManager.reset(); // resets the scores for new game
        viewGuiController.updateScoreDisplay(0, 0); // resets display
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        updateNextPiecePreview();
    }
}
