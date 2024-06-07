package com.example.client.controller;

import com.example.model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ChatController {
    @FXML
    private TextArea chatArea;
    @FXML
    private TextField messageField;

    private ServerConnection serverConnection;
    private User currentUser;

    public void setServerConnection(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    @FXML
    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            serverConnection.sendMessage(message);
            messageField.clear();
            addMessageToChat("You: " + message);
        }
    }

    public void startReceivingMessages() {
        Thread receiveMessagesThread = new Thread(() -> {
            try {
                String message;
                while ((message = serverConnection.receiveMessage()) != null) {
                    String finalMessage = message;
                    Platform.runLater(() -> addMessageToChat(finalMessage));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        receiveMessagesThread.setDaemon(true);
        receiveMessagesThread.start();
    }

    @FXML
    public void initialize() {
        chatArea.setEditable(false);
    }

    private void addMessageToChat(String message) {
        chatArea.appendText(message + "\n");
    }
}
