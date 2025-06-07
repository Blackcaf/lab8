package main.gui;

import utility.ExecutionResponse;

import java.io.*;
import java.net.Socket;

public class NetworkClient implements Closeable {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public ExecutionResponse sendCommand(String command, Object argument, Integer userId) {
        try {
            output.writeObject(command);
            output.writeObject(argument);
            output.writeObject(userId);
            output.flush();
            return (ExecutionResponse) input.readObject();
        } catch (Exception e) {
            return new ExecutionResponse(false, "Ошибка клиента: " + e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        if (output != null) output.close();
        if (input != null) input.close();
        if (socket != null) socket.close();
    }
}