package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.ParallelTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Duration;
import javafx.scene.effect.InnerShadow;
import javafx.scene.shape.StrokeType;
import javafx.scene.control.Separator;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.io.IOException;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 24;
    private static final int HIDDEN_ROWS = 2;

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

    @FXML private StackPane rootContainer;
    @FXML private BorderPane mainContainer;
    @FXML private BorderPane boardFrame;
    @FXML private VBox nextCard;
    @FXML private VBox holdCard;
    @FXML private VBox statsCard;
    @FXML private VBox controlsCard;
    @FXML private Label nextPieceTitle;
    @FXML private Label holdTitle;
    @FXML private Label statsTitle;
    @FXML private Label controlsTitle;

    @FXML private Label scoreLabel;
    @FXML private Label linesLabel;
    @FXML private Label timerLabel;
    @FXML private GridPane nextPiecePanel;
    @FXML private GridPane holdPiecePanel;
    @FXML private Label holdLabel;
    @FXML private VBox nextThreeContainer;
    @FXML private Label pauseLabel;
    @FXML private VBox pauseOverlay;
    @FXML private Button pauseResumeButton;
    @FXML private Button pauseMenuButton;
    @FXML private Label levelLabel;
    @FXML private Label highScoreLabel;

    private Rectangle[][] holdPieceRectangles = null;
    private int currentLevel = 1;
    private static final int BASE_DROP_SPEED = 1000;
    private int[][] boardMatrix;
    private ViewData currentBrickData;
    private List<Brick> nextBricks;
    private int[][] ghostData;
    private HoldManager holdManager;
    private Stage primaryStage;
    private ThemeManager themeManager = new ThemeManager();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);

        // Allow the active piece overlay to float above the board without being re-laid out.
        if (brickPanel != null) {
            brickPanel.setManaged(false);
            brickPanel.setMouseTransparent(true);
            brickPanel.toFront();
        }

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
                    if (keyEvent.getCode() == KeyCode.C) {
                        ViewData holdData = eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER));
                        if (holdData != null) {
                            refreshBrick(holdData);
                        }
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

        applyThemeStyles();
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        this.boardMatrix = boardMatrix;
        this.currentBrickData = brick;
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = HIDDEN_ROWS; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - HIDDEN_ROWS);
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
        updateActiveBrickPosition(brick);

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    private Paint getFillColor(int brickType) {
        if (themeManager != null) {
            Paint color = themeManager.getCurrentColor(brickType);
            if (color != null) {
                return color;
            }
        }
        switch (brickType) {
            case 0: return Color.TRANSPARENT;
            case 1: return Color.AQUA;
            case 2: return Color.BLUEVIOLET;
            case 3: return Color.DARKGREEN;
            case 4: return Color.YELLOW;
            case 5: return Color.RED;
            case 6: return Color.BEIGE;
            case 7: return Color.BURLYWOOD;
            default: return Color.WHITE;
        }
    }


    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            updateActiveBrickPosition(brick);

            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
        }
    }

    private void updateActiveBrickPosition(ViewData brick) {
        if (brick == null || brickPanel == null || gamePanel == null) {
            return;
        }

        double cellWidth = getCellWidth();
        double cellHeight = getCellHeight();

        double xPos = brick.getxPosition() * cellWidth;
        double yPos = (brick.getyPosition() - HIDDEN_ROWS) * cellHeight;

        brickPanel.setLayoutX(xPos);
        brickPanel.setLayoutY(yPos);
    }

    private double getCellWidth() {
        return BRICK_SIZE + (gamePanel != null ? gamePanel.getHgap() : 0);
    }

    private double getCellHeight() {
        return BRICK_SIZE + (gamePanel != null ? gamePanel.getVgap() : 0);
    }
    public void refreshGameBackground(int[][] board) {
        for (int i = HIDDEN_ROWS; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    public void animateLineClear(List<Integer> clearedRowIndices, int linesCleared, Runnable onComplete) {
        if (clearedRowIndices == null || clearedRowIndices.isEmpty() || displayMatrix == null) {
            return;
        }

        ParallelTransition parallelTransition = new ParallelTransition();

        for (Integer rowIndex : clearedRowIndices) {
            if (rowIndex < HIDDEN_ROWS || rowIndex >= displayMatrix.length) {
                continue; // Skip hidden rows or out of bounds
            }

            int visibleRow = rowIndex - HIDDEN_ROWS;
            
            // Animate all cells in this row
            for (int col = 0; col < displayMatrix[rowIndex].length; col++) {
                Rectangle rect = displayMatrix[rowIndex][col];
                if (rect == null) continue;

                // Create flash effect - bright white glow
                Glow glow = new Glow(1.0);
                rect.setEffect(glow);
                rect.setFill(Color.WHITE);

                // Fade out animation
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), rect);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);

                // Scale animation for extra effect
                ScaleTransition scale = new ScaleTransition(Duration.millis(300), rect);
                scale.setFromX(1.0);
                scale.setFromY(1.0);
                scale.setToX(1.5);
                scale.setToY(1.5);
                scale.setAutoReverse(false);

                // Combine animations
                ParallelTransition cellAnimation = new ParallelTransition(fadeOut, scale);
                parallelTransition.getChildren().add(cellAnimation);
            }
        }

        // Play animation
        parallelTransition.setOnFinished(e -> {
            // Reset effects on all rectangles
            for (Integer rowIndex : clearedRowIndices) {
                if (rowIndex < HIDDEN_ROWS || rowIndex >= displayMatrix.length) {
                    continue;
                }
                for (int col = 0; col < displayMatrix[rowIndex].length; col++) {
                    Rectangle rect = displayMatrix[rowIndex][col];
                    if (rect != null) {
                        rect.setEffect(null);
                        rect.setScaleX(1.0);
                        rect.setScaleY(1.0);
                        rect.setOpacity(1.0);
                    }
                }
            }
            // Call the completion callback to refresh the board
            if (onComplete != null) {
                onComplete.run();
            }
        });

        parallelTransition.play();
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
                // Animation is handled in GameController, just show notification
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

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setThemeManager(ThemeManager themeManager) {
        if (themeManager == null) {
            return;
        }
        this.themeManager = themeManager;
        applyThemeStyles();
        refreshThemeColors();
    }

    public void gameOver() {
        gameOver(0, false);
    }

    public void gameOver(int finalScore, boolean isNewHighScore) {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
        if (isNewHighScore) {
            System.out.println("🎉 NEW HIGH SCORE: " + finalScore);
        }
    }

    public void updateHighScore(int highScore) {
        if (highScoreLabel != null) {
            highScoreLabel.setText(String.valueOf(highScore));
        }
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
        if (nextPiecePanel != null) {
            nextPiecePanel.getChildren().clear();

            if (nextBricks != null && !nextBricks.isEmpty()) {
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
            }
        }

        if (nextPieceTitle != null) {
            nextPieceTitle.setText("Next Piece");
        }

        renderNextThreePreview(nextBricks);
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

                    // Align ghost rows with the visible grid (skip the hidden spawn rows)
                    int targetRow = ghostY + i - HIDDEN_ROWS;
                    int visibleRows = displayMatrix != null ? displayMatrix.length - HIDDEN_ROWS : Integer.MAX_VALUE;
                    if (targetRow >= 0 && targetRow < visibleRows) {
                        gamePanel.add(ghostRect, ghostX + j, targetRow);
                    }
                    ghostPieceRectangles[i][j] = ghostRect;
                }
            }
        }
        this.ghostData = ghostData;
    }

    private void clearGhostPiece() {
        if (ghostPieceRectangles != null) {
            for (int i = 0; i < ghostPieceRectangles.length; i++) {
                for (int j = 0; j < ghostPieceRectangles[i].length; j++) {
                    if (ghostPieceRectangles[i][j] != null) {
                        gamePanel.getChildren().remove(ghostPieceRectangles[i][j]);
                    }
                }
            }
            ghostPieceRectangles = null;
        }
        ghostData = null;
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

    private void renderNextThreePreview(List<Brick> bricks) {
        if (nextThreeContainer == null) {
            return;
        }
        nextThreeContainer.getChildren().clear();
        if (bricks == null || bricks.isEmpty()) {
            return;
        }

        int limit = Math.min(3, bricks.size());
        for (int idx = 0; idx < limit; idx++) {
            Brick brick = bricks.get(idx);
            GridPane previewGrid = buildPreviewGrid(brick);
            nextThreeContainer.getChildren().add(previewGrid);
        }
    }

    private GridPane buildPreviewGrid(Brick brick) {
        GridPane previewGrid = new GridPane();
        previewGrid.setHgap(1);
        previewGrid.setVgap(1);

        if (brick == null) {
            return previewGrid;
        }

        List<int[][]> shapes = brick.getShapeMatrix();
        if (shapes.isEmpty()) {
            return previewGrid;
        }

        int[][] shape = shapes.get(0);
        double previewSize = Math.max(8, BRICK_SIZE - 8);

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    Rectangle rect = new Rectangle(previewSize, previewSize);
                    rect.setFill(getFillColor(shape[i][j]));
                    rect.setArcWidth(4);
                    rect.setArcHeight(4);
                    previewGrid.add(rect, j, i);
                }
            }
        }
        return previewGrid;
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
                pauseLabel.setVisible(true);
            } else {
                timeLine.play();
                pauseLabel.setVisible(false);
            }
        }

        if (pauseOverlay != null) {
            pauseOverlay.setVisible(isPaused);
            if (isPaused) {
                pauseOverlay.toFront();
            }
        }

        System.out.println("GUI Pause state: " + (isPaused ? "PAUSED" : "RUNNING"));
    }





    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }

    @FXML
    private void handleResumeGame(ActionEvent actionEvent) {
        if (isPause.getValue()) {
            eventListener.onPauseEvent(new MoveEvent(EventType.PAUSE, EventSource.USER));
        }
    }

    @FXML
    private void handleReturnToMenu(ActionEvent actionEvent) {
        if (primaryStage == null) {
            return;
        }

        if (timeLine != null) {
            timeLine.stop();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainMenu.fxml"));
            Parent root = loader.load();
            MainMenuController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            controller.setThemeManager(themeManager);
            // Refresh high score display when returning to menu
            controller.refreshHighScore();
            Scene menuScene = new Scene(root, primaryStage.getScene() != null ? primaryStage.getScene().getWidth() : 900,
                    primaryStage.getScene() != null ? primaryStage.getScene().getHeight() : 620);
            primaryStage.setScene(menuScene);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void applyThemeStyles() {
        if (themeManager == null) {
            return;
        }
        ThemeManager.ThemePalette palette = themeManager.getCurrentPalette();
        if (palette == null) {
            return;
        }

        setRegionStyle(rootContainer, palette.getRootBackgroundStyle());
        setRegionStyle(boardFrame, palette.getBoardFrameStyle());
        setRegionStyle(gamePanel, palette.getBoardGridStyle());
        setRegionStyle(nextCard, palette.getCardStyle());
        setRegionStyle(holdCard, palette.getCardStyle());
        setRegionStyle(statsCard, palette.getCardStyle());
        setRegionStyle(controlsCard, palette.getControlsCardStyle());
        setRegionStyle(nextPiecePanel, palette.getMiniGridStyle());
        setRegionStyle(holdPiecePanel, palette.getMiniGridStyle());
        setRegionStyle(pauseOverlay, palette.getPauseOverlayStyle());

        if (pauseResumeButton != null) {
            pauseResumeButton.setStyle(palette.getPrimaryButtonStyle());
        }
        if (pauseMenuButton != null) {
            pauseMenuButton.setStyle(palette.getSecondaryButtonStyle());
        }

        setLabelColor(scoreLabel, palette.getPrimaryText());
        setLabelColor(linesLabel, palette.getPrimaryText());
        setLabelColor(timerLabel, palette.getPrimaryText());
        setLabelColor(levelLabel, palette.getPrimaryText());
        setLabelColor(highScoreLabel, palette.getAccentText());
        setLabelColor(nextPieceTitle, palette.getPrimaryText());
        setLabelColor(holdTitle, palette.getPrimaryText());
        setLabelColor(statsTitle, palette.getPrimaryText());
        setLabelColor(controlsTitle, palette.getPrimaryText());
        setLabelColor(holdLabel, palette.getAccentText());
        setLabelColor(pauseLabel, palette.getAccentText());

        applyTextColorToLabels(statsCard, palette.getSecondaryText(), scoreLabel, linesLabel, timerLabel, levelLabel, statsTitle);
        applyTextColorToLabels(controlsCard, palette.getSecondaryText(), controlsTitle);
        applyTextColorToLabels(nextCard, palette.getSecondaryText(), nextPieceTitle);
        applyTextColorToLabels(holdCard, palette.getSecondaryText(), holdTitle, holdLabel);
    }

    private void refreshThemeColors() {
        if (boardMatrix != null) {
            refreshGameBackground(boardMatrix);
        }
        if (currentBrickData != null) {
            refreshBrick(currentBrickData);
        }
        if (ghostData != null) {
            drawGhostPiece(ghostData);
        }
    }

    private void setRegionStyle(Region region, String style) {
        if (region != null && style != null && !style.isEmpty()) {
            region.setStyle(style);
        }
    }

    private void setLabelColor(Label label, Color color) {
        if (label != null && color != null) {
            label.setTextFill(color);
        }
    }

    private void applyTextColorToLabels(VBox container, Color color, Label... exclusions) {
        if (container == null || color == null) {
            return;
        }
        Set<Label> excluded = new HashSet<>();
        if (exclusions != null) {
            Arrays.stream(exclusions)
                    .filter(Objects::nonNull)
                    .forEach(excluded::add);
        }
        for (Node node : container.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                if (!excluded.contains(label)) {
                    label.setTextFill(color);
                }
            }
        }
    }
}
