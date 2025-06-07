package commands;

import models.HumanBeing;
import utility.ExecutionResponse;

public abstract class Command {
    protected String name;
    protected String description;

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public abstract ExecutionResponse execute(HumanBeing argument, Integer userId);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}