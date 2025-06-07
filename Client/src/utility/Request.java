package utility;

import models.HumanBeing;

public class Request implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String commandName;
    private HumanBeing argument;
    private Integer userId;

    public Request(String commandName, HumanBeing argument, Integer userId) {
        this.commandName = commandName;
        this.argument = argument;
        this.userId = userId;
    }

    public String getCommandName() {
        return commandName;
    }

    public HumanBeing getArgument() {
        return argument;
    }

    public Integer getUserId() {
        return userId;
    }
}