package com.example;

import com.example.handler.ClientHandler;
import com.example.service.UserManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerApplication {
    private static final int PORT = 8080;
    private ServerSocket serverSocket;
    private final UserManager userService = new UserManager();
    private static final List<ClientHandler> clients = new ArrayList<>(); // Додаємо список клієнтів

    public static void main(String[] args) {
        new ServerApplication().startServer();
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, userService);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stopServer();
        }
    }

    public static List<ClientHandler> getClients() {
        return clients;
    }

    public static void addClient(ClientHandler client) {
        clients.add(client);
    }

    public static void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
