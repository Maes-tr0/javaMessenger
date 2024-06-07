package com.example.handler;

import com.example.ServerApplication;
import com.example.model.User;
import com.example.service.UserManager;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final UserManager userService;
    private BufferedReader in;
    private PrintWriter out;
    private User currentUser;
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    public ClientHandler(Socket socket, UserManager userService) {
        this.socket = socket;
        this.userService = userService;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String command = in.readLine();
            if ("REGISTER".equals(command)) {
                handleRegister();
            } else if ("LOGIN".equals(command)) {
                handleLogin();
            }

            while (currentUser != null) {
                String message = in.readLine();
                if (message == null) {
                    break;
                }
                handleMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    // Відправка помилок користувачу
    private void sendError(String errorMessage) {
        out.println("ERROR");
        out.println(errorMessage);
    }

    private void handleRegister() throws IOException {
        String firstName = in.readLine();
        String lastName = in.readLine();
        String email = in.readLine();
        String password = in.readLine();

        if (!UserManager.isValidEmail(email)) {
            sendError("Invalid email address.");
            return;
        }

        if (!UserManager.isValidPassword(password)) {
            sendError("""
                Invalid password.
                Password must:
                - Be at least 8 characters long
                - Contain at least one uppercase letter
                - Contain at least one lowercase letter
                - Contain at least one number""");
            return;
        }

        User user = new User(firstName, lastName, email, password);
        if (userService.registerUser(user)) {
            out.println("REGISTERED");
        } else {
            sendError("User already registered.");
        }
    }

    private void handleLogin() throws IOException {
        String email = in.readLine();
        String password = in.readLine();

        User user = userService.authenticateUser(email, password);
        if (user != null && !userService.isUserConnected(email)) {
            currentUser = user;
            userService.addConnectedUser(email);
            out.println("LOGIN SUCCESS\n" + user.getFirstName() + "\n" + user.getLastName());
            ServerApplication.addClient(this);  // Додаємо клієнта до списку клієнтів у ServerApplication
        } else {
            sendError("Invalid email or password.");
        }
    }

    private void handleMessage(String message) {
        String formattedMessage = currentUser.getFullName() + ": " + message;
        broadcast(formattedMessage, this);
    }

    private void broadcast(String message, ClientHandler excludeClient) {
        for (ClientHandler client : ServerApplication.getClients()) {
            if (client != excludeClient) {
                client.sendMessage(message);
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    private void disconnect() {
        if (currentUser != null) {
            userService.removeConnectedUser(currentUser.getEmail());
            ServerApplication.removeClient(this); // Видаляємо клієнта зі списку клієнтів у ServerApplication
        }
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
