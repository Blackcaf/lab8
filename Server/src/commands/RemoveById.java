package commands;

import managers.CollectionManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

import java.util.List;

public class RemoveById extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public RemoveById(Console console, CollectionManager collectionManager) {
        super("removebyid", "удалить элемент из коллекции по его id");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(HumanBeing argument, Integer userId) {
        console.println("Выполняется команда: removebyid, userId: " + userId);
        if (userId == null) {
            return new ExecutionResponse(false, "Ошибка: пользователь не авторизован");
        }
        if (argument == null || argument.getId() == null) {
            return new ExecutionResponse(false, "Ошибка: необходимо указать id для удаления");
        }

        Long id = argument.getId();
        List<HumanBeing> collection = collectionManager.getCollection();
        
        HumanBeing humanBeing = collection.stream()
                .filter(hb -> hb.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (humanBeing == null) {
            console.println("Элемент с id " + id + " не найден в коллекции");
            return new ExecutionResponse(false, "Ошибка: элемент с id " + id + " не существует в коллекции");
        }
        if (!humanBeing.getUserId().equals(userId)) {
            console.println("Элемент с id " + id + " принадлежит userId=" + humanBeing.getUserId());
            return new ExecutionResponse(false, "Ошибка: элемент с id " + id + " принадлежит другому пользователю");
        }

        boolean success = collectionManager.remove(id, userId);
        if (success) {
            console.println("Элемент с id " + id + " успешно удален");
            return new ExecutionResponse(true, "Элемент с id " + id + " успешно удален");
        } else {
            console.println("Ошибка удаления id=" + id + " из базы данных");
            return new ExecutionResponse(false, "Ошибка при удалении элемента с id " + id + ": не удалось удалить из базы данных");
        }
    }
}