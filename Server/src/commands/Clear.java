package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

import java.util.Locale;
import java.util.ResourceBundle;

public class Clear extends Command {
    private final Console console;
    private final CollectionManager collectionManager;
    private final DatabaseManager dbManager;

    public Clear(Console console, CollectionManager collectionManager, DatabaseManager dbManager) {
        super("clear", "очистить коллекцию");
        this.console = console;
        this.collectionManager = collectionManager;
        this.dbManager = dbManager;
    }

    @Override
    public ExecutionResponse execute(HumanBeing argument, Integer userId) {
        console.println("Выполняется команда: clear, userId: " + userId);

        String lang = "ru";
        if (argument != null && argument.getName() != null && !argument.getName().trim().isEmpty()) {
            lang = argument.getName().trim();
        }
        ResourceBundle bundle;
        try {
            bundle = ResourceBundle.getBundle("messages", new Locale(lang));
        } catch (Exception e) {
            bundle = ResourceBundle.getBundle("messages", new Locale("ru"));
        }

        if (userId == null) {
            return new ExecutionResponse(false, bundle.getString("clear.error.not_authorized"));
        }

        boolean success = collectionManager.clear(userId);
        if (success) {
            return new ExecutionResponse(true, bundle.getString("clear.success") + " " + userId);
        } else {
            return new ExecutionResponse(false, bundle.getString("clear.error.failed"));
        }
    }
}