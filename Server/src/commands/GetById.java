package commands;

import managers.CollectionManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;
import java.util.List;

public class GetById extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public GetById(Console console, CollectionManager collectionManager) {
        super("get_by_id", "получить элемент по id");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(HumanBeing argument, Integer userId) {
        if (userId == null) {
            return new ExecutionResponse(false, "Ошибка: пользователь не авторизован");
        }

        if (argument == null || argument.getId() == null) {
            return new ExecutionResponse(false, "Ошибка: не указан id элемента");
        }

        Long id = argument.getId();
        List<HumanBeing> collection = collectionManager.getCollection();
        HumanBeing humanBeing = collection.stream()
                .filter(hb -> hb.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (humanBeing == null) {
            return new ExecutionResponse(false, "Элемент с id " + id + " не найден в коллекции");
        }

        if (!humanBeing.getUserId().equals(userId)) {
            return new ExecutionResponse(false, "Элемент с id " + id + " не принадлежит вам");
        }

        return new ExecutionResponse(true, "Элемент успешно получен", humanBeing);
    }
} 