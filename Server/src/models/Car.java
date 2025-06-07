package models;

import java.io.Serializable;

public class Car implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name; // Не может быть null

    public Car(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Имя машины не может быть null или пустым");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Car{name='" + name + "'}";
    }
}