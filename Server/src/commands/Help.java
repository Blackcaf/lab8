package commands;

import managers.CommandManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

import java.util.Locale;
import java.util.ResourceBundle;

public class Help extends Command {
    private final Console console;
    private final CommandManager commandManager;

    public Help(Console console, CommandManager commandManager) {
        super("help", "вывести справку по доступным командам");
        this.console = console;
        this.commandManager = commandManager;
    }

    @Override
    public ExecutionResponse execute(HumanBeing argument, Integer userId) {
        String lang = "ru";
        if (argument != null && argument.getName() != null && !argument.getName().trim().isEmpty()) {
            lang = argument.getName().trim();
        }
        Locale locale = new Locale(lang);

        ResourceBundle bundle; // <-- объявляем тут!ф
        try {
            bundle = ResourceBundle.getBundle("messages", locale);
        } catch (Exception e) {
            bundle = ResourceBundle.getBundle("messages", new Locale("ru"));
        }

        StringBuilder response = new StringBuilder(
                bundle.containsKey("help.title")
                        ? bundle.getString("help.title") + "\n"
                        : "Доступные команды:\n"
        );

        for (var entry : commandManager.getCommands().entrySet()) {
            String name = entry.getKey();
            Command command = entry.getValue();
            String key = "help." + name;
            String description = bundle.containsKey(key)
                    ? bundle.getString(key)
                    : command.getDescription();
            response.append(name).append(": ").append(description).append("\n");
        }
        return new ExecutionResponse(true, response.toString());
    }
}