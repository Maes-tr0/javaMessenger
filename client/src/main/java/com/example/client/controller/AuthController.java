package com.example.client.controller;

import com.example.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthController {
    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    private Stage stage;
    private Parent root;
    private Scene scene;

    private final ServerConnection serverConnection = new ServerConnection();

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField loginEmailField;
    @FXML
    private PasswordField loginPasswordField;

    @FXML
    private void handleSignUpLink() {
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/client/auth/RegistrationForm.fxml")));
            stage = getCurrentStage();
            scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/client/auth/styles.css")).toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSignInLink() {
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/client/auth/LoginForm.fxml")));
            stage = getCurrentStage();
            scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/client/auth/styles.css")).toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void checkRegistrationInformation() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        User user = new User(firstName, lastName, email, password);
        try {
            serverConnection.connect("localhost", 8080); // Під'єднання до сервера
            serverConnection.registerUser(user);
            serverConnection.disconnect(); // Від'єднання від сервера після реєстрації
            handleSignInLink();
            showAlert(Alert.AlertType.INFORMATION, "Registration Successful!", "Now sign in to your account " + firstName + "!");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Error registering user. Please try again.");
            e.printStackTrace();
        }
    }

    @FXML
    private void checkLoginInformation() {
        String email = loginEmailField.getText();
        String password = loginPasswordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill in all fields");
            return;
        }

        try {
            serverConnection.connect("localhost", 8080); // Під'єднання до сервера
            User user = serverConnection.loginUser(email, password);
            if (user == null) {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid email or password");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/client/chat/ChatForm.fxml"));
            Parent root = loader.load();

            ChatController chatController = loader.getController();
            chatController.setServerConnection(serverConnection);
            chatController.setCurrentUser(user);
            chatController.startReceivingMessages();

            Scene chatScene = new Scene(root);
            chatScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/client/chat/styles.css")).toExternalForm());

            Stage chatStage = new Stage();
            chatStage.setTitle("Chat");
            chatStage.setScene(chatScene);
            chatStage.show();

            Stage loginStage = (Stage) loginEmailField.getScene().getWindow();
            loginStage.close();

            showAlert(Alert.AlertType.INFORMATION, "Login Successful!", "Welcome back, " + user.getFirstName() + "!");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Error logging in. Please try again.");
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    private Stage getCurrentStage() {
        return (Stage) Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
    }
}
