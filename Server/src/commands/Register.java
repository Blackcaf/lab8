package commands;

import managers.DatabaseManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

public class Register extends Command {
    private final DatabaseManager dbManager;
    private final Console console;

    public Register(DatabaseManager dbManager, Console console) {
        super("register", "зарегистрировать нового пользователя");
        this.dbManager = dbManager;
        this.console = console;
    }

    @Override
    public ExecutionResponse execute(HumanBeing user, Integer userId) {
        console.println("Выполняется команда: register, userId: " + userId);
        if (user == null || user.getName() == null || user.getCar() == null || user.getCar().getName() == null) {
            return new ExecutionResponse(false, "Ошибка: необходимо указать username и password");
        }

        String username = user.getName();
        String password = user.getCar().getName();
        console.println("Попытка регистрации: username=" + username);

        Integer newUserId = dbManager.registerUser(username, password);
        if (newUserId != null) {
            return new ExecutionResponse(true, String.valueOf(newUserId));
        } else {
            return new ExecutionResponse(false, "Ошибка регистрации: пользователь уже существует или неверные данные");
        }
    }
}