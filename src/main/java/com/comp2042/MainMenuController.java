package com.comp2042;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    @FXML private StackPane menuRoot;
    @FXML private VBox menuCard;
    @FXML private Button startButton;
    @FXML private Button exitButton;
    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label themeLabel;
    @FXML private Label footerLabel;
    @FXML private Label highScoreLabel;
    @FXML private ComboBox<ThemeManager.Theme> themePicker;

    private Stage primaryStage;
    private ThemeManager themeManager = new ThemeManager();
    private HighScoreManager highScoreManager = new HighScoreManager();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (themePicker != null) {
            ObservableList<ThemeManager.Theme> themes = FXCollections.observableArrayList(
                    ThemeManager.Theme.PASTEL,
                    ThemeManager.Theme.NEON,
                    ThemeManager.Theme.DARK,
                    ThemeManager.Theme.LIGHT
            );
            themePicker.setItems(themes);
            themePicker.setValue(themeManager.getCurrentTheme());
            themePicker.setConverter(new StringConverter<ThemeManager.Theme>() {
                @Override
                public String toString(ThemeManager.Theme object) {
                    if (object == null) return "";
                    return convertThemeToString(object);
                }

                @Override
                public ThemeManager.Theme fromString(String string) {
                    return themePicker.getItems().stream()
                            .filter(t -> toString(t).equals(string))
                            .findFirst()
                            .orElse(ThemeManager.Theme.DARK);
                }
            });

            themePicker.valueProperty().addListener((obs, oldTheme, newTheme) -> {
                if (newTheme != null) {
                    themeManager.setTheme(newTheme);
                    applyThemeStyles();
                }
            });
        }

        // Load and display high score
        updateHighScoreDisplay();
        applyThemeStyles();
    }

    private void updateHighScoreDisplay() {
        if (highScoreLabel != null) {
            int highScore = highScoreManager.getHighScore();
            highScoreLabel.setText(String.valueOf(highScore));
        }
    }

    public void refreshHighScore() {
        // Reload high score from file in case it was updated
        highScoreManager = new HighScoreManager();
        updateHighScoreDisplay();
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void setThemeManager(ThemeManager themeManager) {
        if (themeManager == null) {
            return;
        }
        this.themeManager = themeManager;
        if (themePicker != null) {
            themePicker.setValue(themeManager.getCurrentTheme());
        }
        applyThemeStyles();
    }

    @FXML
    private void handleStartGame(ActionEvent event) {
        if (primaryStage == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gameLayout.fxml"));
            Parent gameRoot = loader.load();
            GuiController guiController = loader.getController();

            ThemeManager.Theme selectedTheme = themePicker != null && themePicker.getValue() != null
                    ? themePicker.getValue()
                    : ThemeManager.Theme.DARK;

            if (themeManager == null) {
                themeManager = new ThemeManager();
            }

            themeManager.setTheme(selectedTheme);
            guiController.setThemeManager(themeManager);
            guiController.setPrimaryStage(primaryStage);

            Scene gameScene = new Scene(gameRoot, 900, 620);
            primaryStage.setScene(gameScene);
            primaryStage.centerOnScreen();
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            new GameController(guiController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExit(ActionEvent event) {
        Platform.exit();
    }

    private void applyThemeStyles() {
        if (themeManager == null) {
            return;
        }
        ThemeManager.ThemePalette palette = themeManager.getCurrentPalette();
        if (palette == null) {
            return;
        }

        setRegionStyle(menuRoot, palette.getRootBackgroundStyle());
        setRegionStyle(menuCard, palette.getCardStyle());

        if (startButton != null) {
            startButton.setStyle(palette.getPrimaryButtonStyle());
        }
        if (exitButton != null) {
            exitButton.setStyle(palette.getSecondaryButtonStyle());
        }

        setLabelColor(titleLabel, palette.getPrimaryText());
        setLabelColor(subtitleLabel, palette.getSecondaryText());
        setLabelColor(themeLabel, palette.getSecondaryText());
        setLabelColor(footerLabel, palette.getSecondaryText());
        
        // High score label uses accent color for visibility
        if (highScoreLabel != null) {
            highScoreLabel.setTextFill(palette.getAccentText());
        }

        styleThemePicker(palette);
    }

    private void styleThemePicker(ThemeManager.ThemePalette palette) {
        if (themePicker == null) {
            return;
        }
        String baseStyle = palette.getMiniGridStyle();
        if (baseStyle == null) {
            baseStyle = "";
        }
        themePicker.setStyle(baseStyle + " -fx-border-color: transparent; -fx-background-radius: 12; -fx-border-radius: 12; -fx-padding: 4;");

        themePicker.setCellFactory(listView -> createThemeCell(palette));
        themePicker.setButtonCell(createThemeCell(palette));
    }

    private ListCell<ThemeManager.Theme> createThemeCell(ThemeManager.ThemePalette palette) {
        return new ListCell<>() {
            @Override
            protected void updateItem(ThemeManager.Theme item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(convertThemeToString(item));
                    setTextFill(palette.getPrimaryText());
                    setStyle(palette.getMiniGridStyle());
                }
            }
        };
    }

    private String convertThemeToString(ThemeManager.Theme theme) {
        switch (theme) {
            case PASTEL: return "Pastel";
            case NEON: return "Neon";
            case DARK: return "Dark Mode";
            case LIGHT: return "Light Mode";
            case CLASSIC:
            default: return "Classic";
        }
    }

    private void setRegionStyle(StackPane region, String style) {
        if (region != null && style != null && !style.isEmpty()) {
            region.setStyle(style);
        }
    }

    private void setRegionStyle(VBox region, String style) {
        if (region != null && style != null && !style.isEmpty()) {
            region.setStyle(style);
        }
    }

    private void setLabelColor(Label label, javafx.scene.paint.Color color) {
        if (label != null && color != null) {
            label.setTextFill(color);
        }
    }
}

