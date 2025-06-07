package main;

import models.Car;
import models.HumanBeing;
import models.Mood;
import models.WeaponType;
import utility.*;
import utility.Console;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientMain {
    private static Integer userId = null;
    private static List<String> users = new ArrayList<>();

    public static void main(String[] args) {
        String host = "localhost";
        int port = 5000;
        Console console = new StandardConsole();

        try (Socket socket = new Socket(host, port);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            console.println("Подключено к серверу: " + host + ":" + port);

            while (true) {
                console.println("Введите команду: ");
                String command = console.read().trim();
                Object argument = null;

                if (command.equals("exit")) {
                    output.writeObject(command);
                    output.writeObject(null);
                    output.writeObject(userId);
                    break;
                }

                String[] parts = command.split("\\s+", 2);
                String commandName = parts[0].toLowerCase();

                if (commandName.equals("delete_user")) {
                    if (userId == null) {
                        console.println("Ошибка: необходимо авторизоваться");
                        continue;
                    }
                    output.writeObject("get_users");
                    output.writeObject(null);
                    output.writeObject(userId);
                    output.flush();

                    ExecutionResponse response = (ExecutionResponse) input.readObject();
                    if (!response.isSuccess()) {
                        console.println("Ошибка при получении списка пользователей: " + response.getMessage());
                        continue;
                    }

                    String[] usersList = response.getMessage().split("\n");
                    users.clear();
                    for (String user : usersList) {
                        if (!user.trim().isEmpty()) {
                            users.add(user.trim());
                        }
                    }

                    if (users.isEmpty()) {
                        console.println("Список пользователей пуст");
                        continue;
                    }

                    console.println("Список пользователей:");
                    for (int i = 0; i < users.size(); i++) {
                        console.println((i + 1) + ". " + users.get(i));
                    }

                    console.println("Введите номер пользователя для удаления:");
                    String inputStr = console.read().trim();
                    int userNumber;
                    try {
                        userNumber = Integer.parseInt(inputStr);
                        if (userNumber < 1 || userNumber > users.size()) {
                            console.println("Ошибка: неверный номер пользователя");
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        console.println("Ошибка: введите корректный номер");
                        continue;
                    }

                    String usernameToDelete = users.get(userNumber - 1);
                    console.println("Вы уверены, что хотите удалить пользователя " + usernameToDelete + "? (да/нет)");
                    String confirmation = console.read().trim().toLowerCase();
                    if (!confirmation.equals("да")) {
                        console.println("Удаление отменено");
                        continue;
                    }

                    users.remove(userNumber - 1);
                    console.println("Пользователь " + usernameToDelete + " успешно удален");
                    continue;
                }

                if (commandName.equals("register") || commandName.equals("login")) {
                    console.println("Введите имя пользователя (не пустое):");
                    String username = console.read().trim();
                    if (username.isEmpty()) {
                        console.println("Ошибка: имя пользователя не может быть пустым");
                        continue;
                    }

                    console.println("Введите пароль (не пустой):");
                    String password = console.read().trim();
                    if (password.isEmpty()) {
                        console.println("Ошибка: пароль не может быть пустым");
                        continue;
                    }

                    HumanBeing humanBeing = new HumanBeing();
                    humanBeing.setName(username);
                    humanBeing.setCar(new Car(password));
                    argument = humanBeing;
                } else if (commandName.equals("execute_script")) {
                    console.println("Введите путь к файлу скрипта:");
                    String scriptPath = console.read().trim();
                    if (scriptPath.isEmpty()) {
                        console.println("Ошибка: путь к файлу не может быть пустым");
                        continue;
                    }
                    HumanBeing humanBeing = new HumanBeing();
                    humanBeing.setName(scriptPath);
                    argument = humanBeing;
                } else if (commandName.equals("removebyid")) {
                    console.println("Введите id элемента для удаления (целое положительное число):");
                    String idInput = console.read().trim();
                    Long id;
                    try {
                        id = Long.parseLong(idInput);
                        if (id <= 0) {
                            console.println("Ошибка: id должен быть положительным числом");
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        console.println("Ошибка: введите корректное целое число для id");
                        continue;
                    }
                    argument = id;
                } else {
                    if (userId == null) {
                        console.println("Ошибка: необходимо войти в систему (login) или зарегистрироваться (register)");
                        continue;
                    }

                    if (commandName.equals("add")) {
                        try {
                            HumanBeingAsker asker = new HumanBeingAsker(console);
                            HumanBeing humanBeing = asker.askHumanBeing();
                            argument = humanBeing;
                        } catch (Exception e) {
                            console.println("Ошибка при создании объекта: " + e.getMessage());
                            continue;
                        }
                    } else if (commandName.equals("update")) {
                        console.println("Введите id элемента для обновления (целое положительное число):");
                        String idInput = console.read().trim();
                        Long id;
                        try {
                            id = Long.parseLong(idInput);
                            if (id <= 0) {
                                console.println("Ошибка: id должен быть положительным числом");
                                continue;
                            }
                        } catch (NumberFormatException e) {
                            console.println("Ошибка: введите корректное целое число для id");
                            continue;
                        }
                        try {
                            HumanBeingAsker asker = new HumanBeingAsker(console);
                            HumanBeing humanBeing = asker.askHumanBeing();
                            humanBeing.setId(id);
                            argument = humanBeing;
                        } catch (Exception e) {
                            console.println("Ошибка при создании объекта: " + e.getMessage());
                            continue;
                        }
                    } else if (commandName.equals("filter_starts_with_name")) {
                        console.println("Введите имя (не пустое):");
                        String name = console.read().trim();
                        if (name.isEmpty()) {
                            console.println("Ошибка: имя не может быть пустым");
                            continue;
                        }
                        HumanBeing humanBeing = new HumanBeing();
                        humanBeing.setName(name);
                        argument = humanBeing;
                    } else if (commandName.equals("count_less_than_impact_speed")) {
                        console.println("Введите impactSpeed (целое число):");
                        String impactSpeedInput = console.read().trim();
                        try {
                            long impactSpeed = Long.parseLong(impactSpeedInput);
                            HumanBeing humanBeing = new HumanBeing();
                            humanBeing.setImpactSpeed(impactSpeed);
                            argument = humanBeing;
                        } catch (NumberFormatException e) {
                            console.println("Ошибка: введите корректное целое число");
                            continue;
                        }
                    }
                }

                output.writeObject(commandName);
                output.writeObject(argument);
                output.writeObject(userId);
                output.flush();

                ExecutionResponse response = (ExecutionResponse) input.readObject();
                console.println("Ответ сервера: " + response.getMessage());

                if (commandName.equals("execute_script")) {
                    if (response.isSuccess()) {
                        console.println("Результат выполнения скрипта:");
                        String message = response.getMessage();
                        if (message != null && !message.isEmpty()) {
                            String[] lines = message.split("\\r?\\n");
                            for (String line : lines) {
                                console.println(line);
                            }
                        } else {
                            console.println("Пустой результат от сервера");
                        }
                    } else {
                        console.println("Ошибка выполнения скрипта: " + response.getMessage());
                    }
                }

                if ((commandName.equals("login") || commandName.equals("register")) && response.isSuccess()) {
                    try {
                        userId = Integer.parseInt(response.getMessage());
                    } catch (NumberFormatException e) {
                        console.println("Ошибка: некорректный userId от сервера");
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            console.println("Ошибка клиента: " + e.getMessage());
            e.printStackTrace();
        }
    }
}