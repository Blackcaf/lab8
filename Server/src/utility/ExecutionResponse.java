package utility;

import java.io.Serializable;
import java.util.List;
import models.HumanBeing;

public class ExecutionResponse implements Serializable {
    private boolean success;
    private String message;
    private HumanBeing humanBeing;
    private List<HumanBeing> humanBeings; // <-- Добавить это поле

    public ExecutionResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.humanBeing = null;
        this.humanBeings = null;
    }

    public ExecutionResponse(boolean success, String message, HumanBeing humanBeing) {
        this.success = success;
        this.message = message;
        this.humanBeing = humanBeing;
        this.humanBeings = null;
    }

    public ExecutionResponse(boolean success, String message, List<HumanBeing> humanBeings) {
        this.success = success;
        this.message = message;
        this.humanBeing = null;
        this.humanBeings = humanBeings;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public HumanBeing getHumanBeing() {
        return humanBeing;
    }

    public List<HumanBeing> getHumanBeings() {
        return humanBeings;
    }
}