package utility;

import java.io.Serializable;
import models.HumanBeing;

public class ExecutionResponse implements Serializable {
    private boolean success;
    private String message;
    private HumanBeing humanBeing;

    public ExecutionResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.humanBeing = null;
    }

    public ExecutionResponse(boolean success, String message, HumanBeing humanBeing) {
        this.success = success;
        this.message = message;
        this.humanBeing = humanBeing;
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
}