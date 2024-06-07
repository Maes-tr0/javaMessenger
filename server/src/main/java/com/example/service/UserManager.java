package com.example.service;

import com.example.model.User;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class UserManager {
    private final Set<User> registeredUsers = new HashSet<>();
    private final Set<String> connectedUsers = new HashSet<>();
    private final File usersFile = new File("server/src/main/resources/users.txt");

    public UserManager() {
        loadRegisteredUsers();
    }

    // Завантаження зареєстрованих користувачів з файлу
    private void loadRegisteredUsers() {
        if (usersFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(", ");
                    if (parts.length == 4) {
                        registeredUsers.add(new User(parts[0], parts[1], parts[2], parts[3]));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Збереження зареєстрованих користувачів у файл
    private void saveRegisteredUser(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFile, true))) {
            writer.write(user.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Реєстрація користувача
    public boolean registerUser(User user) {
        for (User registeredUser : registeredUsers) {
            if (registeredUser.getEmail().equals(user.getEmail())) {
                return false; // Користувач вже зареєстрований
            }
        }
        registeredUsers.add(user);
        saveRegisteredUser(user);
        return true;
    }

    // Аутентифікація користувача
    public User authenticateUser(String email, String password) {
        for (User user : registeredUsers) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    // Додавання підключеного користувача
    public boolean addConnectedUser(String email) {
        return connectedUsers.add(email);
    }

    // Видалення підключеного користувача
    public void removeConnectedUser(String email) {
        connectedUsers.remove(email);
    }

    // Перевірка, чи користувач підключений
    public boolean isUserConnected(String email) {
        return connectedUsers.contains(email);
    }

    // Валідація email
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    // Валідація паролю
    public static boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        return pattern.matcher(password).matches();
    }
}
