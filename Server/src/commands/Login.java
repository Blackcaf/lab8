package commands;

import managers.DatabaseManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

public class Login extends Command {
    private final DatabaseManager dbManager;
    private final Console console;

    public Login(DatabaseManager dbManager, Console console) {
        super("login", "авторизоваться с существующим пользователем");
        this.dbManager = dbManager;
        this.console = console;
    }

    @Override
    public ExecutionResponse execute(HumanBeing user, Integer userId) {
        console.println("Выполняется команда: login, userId: " + userId);
        if (user == null || user.getName() == null || user.getCar() == null || user.getCar().getName() == null) {
            return new ExecutionResponse(false, "Ошибка: " + "необходимо указать имя пользователя и пароль");
        }

        String username = user.getName();
        String password = user.getCar().getName();
        console.println("Попытка авторизации: username=" + username);
        Integer resultId = dbManager.loginUser(username, password);
        if (resultId != null) {
            return new ExecutionResponse(true, String.valueOf(resultId));
        } else {
            return new ExecutionResponse(false, "Ошибка авторизации: неверный username или password");
        }
    }
}