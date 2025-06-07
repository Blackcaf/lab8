package commands;

import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

public class Exit extends Command {
    private final Console console;

    public Exit(Console console) {
        super("exit", "завершить работу клиента");
        this.console = console;
    }

    @Override
    public ExecutionResponse execute(HumanBeing argument, Integer userId) {
        console.println("Завершение работы клиента");
        return new ExecutionResponse(true, "Клиент отключен");
    }
}