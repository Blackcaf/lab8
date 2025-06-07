package server;

import managers.CommandManager;
import models.HumanBeing;
import utility.ExecutionResponse;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final int port;
    private final CommandManager commandManager;

    public Server(int port, CommandManager commandManager) {
        this.port = port;
        this.commandManager = commandManager;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту " + port);
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Клиент подключен: " + clientSocket.getInetAddress());
                    handleClient(clientSocket);
                } catch (Exception e) {
                    System.out.println("Ошибка обработки клиента: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) throws IOException {
        try (ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream())) {

            while (!clientSocket.isClosed()) {
                try {
                    String commandName = (String) input.readObject();
                    Object argument = input.readObject();
                    Integer userId = (Integer) input.readObject();

                    System.out.println("Получен запрос: command=" + commandName + ", userId=" + userId);
                    if (argument != null) {
                        if (argument instanceof HumanBeing) {
                            HumanBeing hb = (HumanBeing) argument;
                            System.out.println("Аргумент (HumanBeing): id=" + hb.getId() + ", userId=" + hb.getUserId());
                        } else if (argument instanceof Long) {
                            System.out.println("Аргумент: id=" + argument);
                        } else {
                            System.out.println("Аргумент: " + argument);
                        }
                    } else {
                        System.out.println("Аргумент: null");
                    }

                    HumanBeing humanBeing = null;
                    if (argument instanceof HumanBeing) {
                        humanBeing = (HumanBeing) argument;
                    } else if (argument instanceof Long) {
                        humanBeing = new HumanBeing();
                        humanBeing.setId((Long) argument);
                    }

                    ExecutionResponse response = commandManager.executeCommand(commandName, humanBeing, userId);

                    output.writeObject(response);
                    output.flush();

                    if (commandName.equals("exit")) {
                        System.out.println("Клиент отключен: " + clientSocket.getInetAddress());
                        break;
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Ошибка чтения данных: " + e.getMessage());
                    output.writeObject(new ExecutionResponse(false, "Ошибка чтения данных: " + e.getMessage()));
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка обработки клиента: " + e.getMessage());
            throw e;
        }
    }
}