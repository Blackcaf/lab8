package commands;

import managers.CollectionManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class Info extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public Info(Console console, CollectionManager collectionManager) {
        super("info", "вывести информацию о коллекции");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(HumanBeing argument, Integer userId) {
        console.println("Выполняется команда: info, userId: " + userId);

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
            return new ExecutionResponse(false, bundle.getString("info.error.not_authorized"));
        }

        List<HumanBeing> collection = collectionManager.getCollection();
        int totalElements = collection.size();
        int userElements = 0;
        for (HumanBeing human : collection) {
            if (human.getUserId().equals(userId)) userElements++;
        }

        StringBuilder response = new StringBuilder();
        response.append(bundle.getString("info.type")).append(" ").append(collection.getClass().getSimpleName()).append("\n");
        response.append(bundle.getString("info.init_date")).append(" ").append(collectionManager.getInitializationDate()).append("\n");
        response.append(bundle.getString("info.total_elements")).append(" ").append(totalElements).append("\n");
        response.append(bundle.getString("info.user_elements")).append(" ").append(userElements);

        return new ExecutionResponse(true, response.toString());
    }
}