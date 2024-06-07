package com.example.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessengerApplication extends Application {
    private static final Logger logger = Logger.getLogger(MessengerApplication.class.getName());

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("auth/RegistrationForm.fxml")));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("auth/styles.css")).toExternalForm());
            primaryStage.setTitle("Whisper");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(400);
            primaryStage.show();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error starting application", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
