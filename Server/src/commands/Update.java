package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;
import java.util.List;

public class Update extends Command {
    private final Console console;
    private final CollectionManager collectionManager;
    private final DatabaseManager dbManager;

    public Update(Console console, CollectionManager collectionManager, DatabaseManager dbManager) {
        super("update", "обновить элемент коллекции по его id");
        this.console = console;
        this.collectionManager = collectionManager;
        this.dbManager = dbManager;
    }

    @Override
    public ExecutionResponse execute(HumanBeing argument, Integer userId) {
        console.println("Выполняется команда: update, userId: " + userId);
        if (userId == null) {
            return new ExecutionResponse(false, "Ошибка: пользователь не авторизован");
        }
        if (argument == null || argument.getId() == null) {
            return new ExecutionResponse(false, "Ошибка: необходимо указать id для обновления");
        }

        Long id = argument.getId();
        console.println("Содержимое коллекции перед обновлением id=" + id + ":");
        for (Long key : collectionManager.getCollectionMap().keySet()) {
            HumanBeing hb = collectionManager.getCollectionMap().get(key);
            console.println("id=" + hb.getId() + ", userId=" + hb.getUserId());
        }

        List<HumanBeing> collection = collectionManager.getCollection();
        HumanBeing oldHuman = collection.stream()
                .filter(hb -> hb.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (oldHuman == null) {
            console.println("Элемент с id " + id + " не найден в коллекции");
            return new ExecutionResponse(false, "Ошибка: элемент с id " + id + " не существует в коллекции");
        }
        if (!oldHuman.getUserId().equals(userId)) {
            console.println("Элемент с id " + id + " принадлежит userId=" + oldHuman.getUserId());
            return new ExecutionResponse(false, "Ошибка: элемент с id " + id + " принадлежит другому пользователю");
        }

        argument.setUserId(userId);
        argument.setCreationDate(oldHuman.getCreationDate());

        boolean success = collectionManager.update(id, argument, userId);
        if (success) {
            console.println("Элемент с id " + id + " успешно обновлен");
            return new ExecutionResponse(true, "Элемент с id " + id + " успешно обновлен");
        } else {
            console.println("Ошибка обновления id=" + id + " в базе данных");
            return new ExecutionResponse(false, "Ошибка при обновлении элемента с id " + id + ": не удалось обновить в базе данных");
        }
    }
}