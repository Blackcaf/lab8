package commands;

import managers.CollectionManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

public class CountLessThanImpactSpeed extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public CountLessThanImpactSpeed(Console console, CollectionManager collectionManager) {
        super("count_less_than_impact_speed", "вывести количество элементов, значение поля impactSpeed которых меньше заданного");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(HumanBeing argument, Integer userId) {
        console.println("Выполняется команда: count_less_than_impact_speed, userId: " + userId);
        if (argument == null || argument.getImpactSpeed() == null) {
            return new ExecutionResponse(false, "Ошибка: необходимо указать значение impactSpeed");
        }

        long threshold = argument.getImpactSpeed();
        long count = collectionManager.getCollection().stream()
                .filter(h -> h.getUserId().equals(userId))
                .filter(h -> h.getImpactSpeed() < threshold)
                .count();

        return new ExecutionResponse(true, "Количество элементов с impactSpeed меньше " + threshold + ": " + count);
    }
}