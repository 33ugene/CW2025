package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.scene.effect.InnerShadow;
import javafx.scene.shape.StrokeType;
import javafx.scene.control.Separator;

import javafx.scene.control.Label;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GameOverPanel gameOverPanel;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private Rectangle[][] ghostPieceRectangles = null;

    @FXML private Label scoreLabel;
    @FXML private Label linesLabel;
    @FXML private Label timerLabel;
    @FXML private GridPane nextPiecePanel;
    @FXML private Label nextPieceLabel;
    @FXML private GridPane holdPiecePanel;
    @FXML private Label holdLabel;
    @FXML private Label pauseLabel;
    @FXML private VBox pauseOverlay;
    @FXML private Label levelLabel;

    private Rectangle[][] holdPieceRectangles = null;
    private int currentLevel = 1;
    private static final int BASE_DROP_SPEED = 1000;
    private int[][] boardMatrix;
    private ViewData currentBrickData;
    private List<Brick> nextBricks;
    private int[][] ghostData;
    private HoldManager holdManager;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.P || keyEvent.getCode() == KeyCode.ESCAPE) {
                        eventListener.onPauseEvent(new MoveEvent(EventType.PAUSE, EventSource.USER));
                        keyEvent.consume();
                        return; // Don't process other keys when pausing
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.SPACE) {
                        refreshBrick(eventListener.onHardDropEvent(new MoveEvent(EventType.HARD_DROP, EventSource.USER)));
                        keyEvent.consume();
                    }
                }
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }
            }
        });
        gameOverPanel.setVisible(false);

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        this.boardMatrix = boardMatrix;
        this.currentBrickData = brick;
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);


        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    private Paint getFillColor(int brickType) {
        // ✅ PROPER COLOR MAPPING - Make sure this method exists and works
        switch (brickType) {
            case 0: return Color.TRANSPARENT;    // Empty cell
            case 1: return Color.AQUA;           // I-Brick - Cyan
            case 2: return Color.BLUEVIOLET;     // Z-Brick - Purple
            case 3: return Color.DARKGREEN;      // T-Brick - Green
            case 4: return Color.YELLOW;         // O-Brick - Yellow
            case 5: return Color.RED;            // S-Brick - Red
            case 6: return Color.BEIGE;          // L-Brick - Orange
            case 7: return Color.BURLYWOOD;      // J-Brick - Brown
            default: return Color.WHITE;         // Fallback
        }
    }


    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            // ✅ SIMPLIFIED POSITIONING - Remove magic numbers
            double xPos = gamePanel.getLayoutX() + (brick.getxPosition() * (BRICK_SIZE + 1)); // +1 for gap
            double yPos = gamePanel.getLayoutY() + (brick.getyPosition() * (BRICK_SIZE + 1)); // +1 for gap

            brickPanel.setLayoutX(xPos);
            brickPanel.setLayoutY(yPos);

            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
        }
    }
    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));

        // ✅ MUCH NICER STYLING
        rectangle.setArcHeight(8);  // Smoother rounded corners
        rectangle.setArcWidth(8);   // Smoother rounded corners

        // Add a subtle border for definition
        if (color != 0) { // Only for actual bricks, not empty cells
            rectangle.setStroke(Color.rgb(255, 255, 255, 0.3)); // Semi-transparent white border
            rectangle.setStrokeWidth(0.8);

            // Add subtle inner shadow for depth
            rectangle.setEffect(new InnerShadow(5, Color.rgb(0, 0, 0, 0.3)));
        } else {
            rectangle.setStroke(null); // No border for empty cells
        }
    }

    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());

            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    public void updateGameSpeed(int level) {
        if (this.currentLevel == level) {
            return; // Already at this speed
        }

        this.currentLevel = level;
        int newSpeed = calculateDropSpeed(level);

        System.out.println("⚡ LEVEL " + level + " - Speed: " + newSpeed + "ms per drop");

        // Stop current timeline
        if (timeLine != null) {
            timeLine.stop();
        }

        // Create new timeline with updated speed
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(newSpeed),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    private int calculateDropSpeed(int level) {
        switch (level) {
            case 1:  return 1000; // 1.0 seconds
            case 2:  return 793;  // 0.8 seconds
            case 3:  return 618;  // 0.6 seconds
            case 4:  return 473;  // 0.47 seconds
            case 5:  return 355;  // 0.35 seconds
            case 6:  return 262;  // 0.26 seconds
            case 7:  return 190;  // 0.19 seconds
            case 8:  return 135;  // 0.14 seconds
            case 9:  return 94;   // 0.09 seconds
            case 10: return 64;   // 0.06 seconds
            case 11: return 43;   // 0.04 seconds
            case 12: return 28;   // 0.03 seconds
            case 13: return 18;   // 0.02 seconds
            case 14: return 13;   // 0.01 seconds
            case 15: return 10;   // 0.01 seconds (max speed)
            default: return 10;   // Stay at max speed for levels 16+
        }
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    public void updateTimerDisplay(String time) {
        if (timerLabel != null) {
            timerLabel.setText("Time: " + time);
        }
    }

    public void updateScoreDisplay(int score, int lines, int level) {
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + score);
        }
        if (linesLabel != null) {
            linesLabel.setText("Lines: " + lines);
        }
        if (levelLabel != null) {
            levelLabel.setText("Level: " + level);
        }

        // Print for console debugging
        System.out.println("Score updated - Total: " + score + ", Lines: " + lines + "Level: " + level);
    }

    // method for displaying next piece
    public void updateNextPiecePreview(List<Brick> nextBricks) {
        this.nextBricks = nextBricks;
        if (nextBricks == null || nextBricks.isEmpty() || nextPiecePanel == null) return;

        // Clear previous previews
        nextPiecePanel.getChildren().clear();

        // Display the first next piece
        Brick firstBrick = nextBricks.get(0);
        List<int[][]> shapes = firstBrick.getShapeMatrix();
        if (!shapes.isEmpty()) {
            int[][] shape = shapes.get(0); // get first rotation
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[i].length; j++) {
                    if (shape[i][j] != 0) {
                        Rectangle rect = new Rectangle(BRICK_SIZE - 2, BRICK_SIZE - 2);
                        rect.setFill(getFillColor(shape[i][j]));
                        rect.setArcHeight(5);
                        rect.setArcWidth(5);
                        nextPiecePanel.add(rect, j, i);
                    }
                }
            }
        }

        if (nextPieceLabel != null) {
            nextPieceLabel.setText("Next Piece");
        }
    }

    public void drawGhostPiece(int[][] ghostData) {
        clearGhostPiece();

        if (ghostData == null || ghostData.length < 5) return;

        int ghostX = ghostData[4][0];
        int ghostY = ghostData[4][1];

        ghostPieceRectangles = new Rectangle[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (ghostData[i][j] != 0) {
                    Rectangle ghostRect = new Rectangle(BRICK_SIZE, BRICK_SIZE);

                    Color pieceColor = (Color) getFillColor(ghostData[i][j]);
                    // ✅ BETTER GHOST APPEARANCE
                    Color ghostColor = new Color(
                            pieceColor.getRed(),
                            pieceColor.getGreen(),
                            pieceColor.getBlue(),
                            0.2 // Even more transparent
                    );

                    ghostRect.setFill(ghostColor);
                    ghostRect.setStroke(Color.rgb(255, 255, 255, 0.5)); // White outline
                    ghostRect.setStrokeWidth(1);
                    ghostRect.setArcHeight(8);
                    ghostRect.setArcWidth(8);
                    ghostRect.setStrokeType(StrokeType.INSIDE);

                    // Dashed border effect
                    ghostRect.getStrokeDashArray().addAll(4.0, 4.0);

                    // ✅ FIXED GHOST POSITIONING
                    gamePanel.add(ghostRect, ghostX + j, ghostY + i);
                    ghostPieceRectangles[i][j] = ghostRect;
                }
            }
        }
    }

    private void clearGhostPiece() {
        if (ghostPieceRectangles != null) {
            for (int i = 0; i < ghostPieceRectangles.length; i++) {
                for (int j = 0; j < ghostPieceRectangles.length; j++) {
                    if (ghostPieceRectangles[i][j] != null) {
                        gamePanel.getChildren().remove(ghostPieceRectangles[i][j]);
                    }
                }
            }
            ghostPieceRectangles = null;
        }
    }

    public void updateHoldDisplay(Brick heldBrick, boolean canHold) {
        if (holdPiecePanel == null) return;

        holdPiecePanel.getChildren().clear();

        if (holdLabel != null) {
            if (canHold) {
                holdLabel.setText("Hold: Ready");
                holdLabel.setStyle("-fx-text-fill: green;");
            } else {
                holdLabel.setText("Hold: Used");
                holdLabel.setStyle("-fx-text-fill: gray;");
            }
        }

        if (heldBrick != null) {
            displayBrickInPanel(heldBrick, holdPiecePanel);
        }
    }




    private void displayBrickInPanel(Brick brick, GridPane panel) {
        if (brick == null || panel == null) return;

        List<int[][]> shapes = brick.getShapeMatrix();
        if (!shapes.isEmpty()) {
            int[][] shape = shapes.get(0);
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[i].length; j++) {
                    if (shape[i][j] != 0) {
                        Rectangle rect = new Rectangle(BRICK_SIZE - 2, BRICK_SIZE - 2);
                        rect.setFill(getFillColor(shape[i][j]));
                        rect.setArcWidth(5);
                        rect.setArcHeight(5);
                        panel.add(rect, j, i);
                    }
                }
            }
        }
    }

    public void showHardDropEffect() {
        // Quick visual feedback that hard drop happened
        gamePanel.setStyle("-fx-background-color: rgba(255,255,255,0.1);");

        Timeline flash = new Timeline(
                new KeyFrame(Duration.millis(100),
                        ae -> gamePanel.setStyle("-fx-background-color: transparent;")
                ));
        flash.play();
    }

    public void updatePauseDisplay(boolean isPaused) {
        if (pauseLabel != null) {
            if (isPaused) {
                timeLine.pause();
                pauseLabel.setText("PAUSED");
                pauseLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: yellow; -fx-font-weight: bold;");
                pauseLabel.setVisible(true);
            } else {
                timeLine.play();
                pauseLabel.setVisible(false);
            }
        }

        // Optional: Add semi-transparent overlay when paused
        if (pauseOverlay != null) {
            pauseOverlay.setVisible(isPaused);
        }

        System.out.println("GUI Pause state: " + (isPaused ? "PAUSED" : "RUNNING"));
    }





    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
}
