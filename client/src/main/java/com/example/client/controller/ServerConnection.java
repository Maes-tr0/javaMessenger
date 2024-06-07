package com.example.client.controller;

import com.example.model.User;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerConnection {
    private static final Logger logger = Logger.getLogger(ServerConnection.class.getName());

    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;

    // Під'єднання до сервера
    public void connect(String serverAddress, int port) throws IOException {
        socket = new Socket(serverAddress, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    // Відправка даних для реєстрації користувача
    public void registerUser(User user) throws IOException {
        out.println("REGISTER");
        out.println(user.getFirstName());
        out.println(user.getLastName());
        out.println(user.getEmail());
        out.println(user.getPassword());

        String response = in.readLine();
        if (!"REGISTERED".equals(response)) {
            throw new IOException("Registration failed: " + response);
        }
    }

    // Відправка даних для входу користувача
    public User loginUser(String email, String password) throws IOException {
        out.println("LOGIN");
        out.println(email);
        out.println(password);

        String response = in.readLine();
        if ("LOGIN SUCCESS".equals(response)) {
            String firstName = in.readLine();
            String lastName = in.readLine();
            return new User(firstName, lastName, email, password);
        } else {
            return null;
        }
    }

    // Відправка повідомлення
    public void sendMessage(String message) {
        out.println(message);
    }

    // Отримання повідомлення
    public String receiveMessage() throws IOException {
        return in.readLine();
    }

    // Відключення від сервера
    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
