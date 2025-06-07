package commands;

import managers.CollectionManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

public class Add extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public Add(Console console, CollectionManager collectionManager) {
        super("add", "добавить новый элемент в коллекцию");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(HumanBeing argument, Integer userId) {
        String lang = "ru";
        if (argument != null && argument.getName() != null && !argument.getName().isEmpty())
            lang = argument.getName();
        ResourceBundle bundle;
        try {
            bundle = ResourceBundle.getBundle("messages", new Locale(lang));
        } catch (Exception e) {
            bundle = ResourceBundle.getBundle("messages", new Locale("ru"));
        }

        if (userId == null) {
            return new ExecutionResponse(false, bundle.getString("add.error.not_authorized"));
        }

        if (argument == null) {
            return new ExecutionResponse(false, bundle.getString("add.error.no_argument"));
        }

        argument.setUserId(userId);
        argument.setCreationDate(LocalDateTime.now());
        boolean added = collectionManager.add(argument, userId);

        if (added) {
            // argument уже содержит id после добавления в базу/коллекцию
            return new ExecutionResponse(true, bundle.getString("add.success"), argument);
        } else {
            return new ExecutionResponse(false, bundle.getString("add.error"));
        }
    }
}