package commands;

import managers.CollectionManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class Show extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public Show(Console console, CollectionManager collectionManager) {
        super("show", "вывести все элементы коллекции");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(HumanBeing argument, Integer userId) {
        console.println("Выполняется команда: show, userId: " + userId);

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
            return new ExecutionResponse(false, bundle.getString("show.error.not_authorized"));
        }

        List<HumanBeing> allHumanBeingsList = collectionManager.getCollection();

        StringBuilder response = new StringBuilder(bundle.getString("show.header") + "\n");
        boolean found = false;
        int totalElements = allHumanBeingsList.size();
        int userElements = 0;

        for (HumanBeing human : allHumanBeingsList) {
            if (human.getUserId() != null && human.getUserId().equals(userId)) {
                response.append(human.toString()).append("\n");
                found = true;
                userElements++;
            }
        }

        console.println(bundle.getString("show.console.total") + totalElements);
        console.println(bundle.getString("show.console.user") + userElements);

        if (!found) {
            response.append(bundle.getString("show.no_elements"));
        }

        // Возвращаем полный список для клиента (таблица)
        return new ExecutionResponse(true, "Список успешно получен", allHumanBeingsList);
    }
}