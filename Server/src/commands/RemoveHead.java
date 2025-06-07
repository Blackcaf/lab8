package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

import java.util.List;

public class RemoveHead extends Command {
    private final CollectionManager collectionManager;
    private final DatabaseManager databaseManager;
    private final Console console;

    public RemoveHead(CollectionManager collection, Console console, DatabaseManager databaseManager) {
        super("remove_head", "вывести и удалить первый элемент коллекции");
        this.collectionManager = collection;
        this.databaseManager = databaseManager;
        this.console = console;
    }

    @Override
    public ExecutionResponse execute(HumanBeing argument, Integer userId) {
        console.println("Выполняется команда: remove_head, userId: " + userId);
        if (userId == null) {
            return new ExecutionResponse(false, "Ошибка: пользователь не авторизован");
        }

        List<HumanBeing> collection = collectionManager.getCollection();
        if (collection.isEmpty()) {
            return new ExecutionResponse(false, "Коллекция пуста");
        }

        HumanBeing head = collection.stream()
                .filter(h -> h.getUserId().equals(userId))
                .findFirst()
                .orElse(null);

        if (head == null) {
            return new ExecutionResponse(false, "Нет элементов, принадлежащих пользователю с ID " + userId);
        }

        boolean success = collectionManager.remove(head.getId(), userId);
        if (success) {
            return new ExecutionResponse(true, "Первый элемент: " + head.toString() + "\nЭлемент успешно удален");
        } else {
            return new ExecutionResponse(false, "Ошибка при удалении первого элемента");
        }
    }
}