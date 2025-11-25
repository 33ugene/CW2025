package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL location = getClass().getClassLoader().getResource("mainMenu.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Parent root = fxmlLoader.load();
        MainMenuController controller = fxmlLoader.getController();
        ThemeManager themeManager = new ThemeManager();
        controller.setPrimaryStage(primaryStage);
        controller.setThemeManager(themeManager);

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(root, 900, 620);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
