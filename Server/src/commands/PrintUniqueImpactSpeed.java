package commands;

import managers.CollectionManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

import java.util.HashSet;
import java.util.Set;

public class PrintUniqueImpactSpeed extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public PrintUniqueImpactSpeed(Console console, CollectionManager collectionManager) {
        super("print_unique_impact_speed", "вывести уникальные значения поля impactSpeed");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(HumanBeing argument, Integer userId) {
        console.println("Выполняется команда: print_unique_impact_speed, userId: " + userId);
        if (userId == null) {
            return new ExecutionResponse(false, "Ошибка: необходимо авторизоваться (login) или зарегистрироваться (register)");
        }

        Set<Long> uniqueSpeeds = new HashSet<>();
        for (HumanBeing human : collectionManager.getCollection()) {
            if (human.getUserId().equals(userId)) {
                uniqueSpeeds.add(human.getImpactSpeed());
            }
        }

        if (uniqueSpeeds.isEmpty()) {
            return new ExecutionResponse(true, "У вас нет элементов в коллекции.");
        }

        StringBuilder response = new StringBuilder("Уникальные значения impactSpeed:\n");
        for (Long speed : uniqueSpeeds) {
            response.append(speed).append("\n");
        }

        return new ExecutionResponse(true, response.toString());
    }
}