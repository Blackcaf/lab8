package models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class HumanBeing implements Comparable<HumanBeing>, Serializable {
    private static final long serialVersionUID = 1L;
    private Long id; // Не может быть null, > 0, уникальное, генерируется автоматически
    private String name; // Не может быть null, не пустое
    private Coordinates coordinates; // Не может быть null
    private LocalDateTime creationDate; // Не может быть null, генерируется автоматически
    private boolean realHero;
    private Boolean hasToothpick; // Может быть null
    private Long impactSpeed;
    private WeaponType weaponType; // Не может быть null
    private String mood; // Не может быть null
    private Car car; // Не может быть null
    private Integer userId; // Для идентификации пользователя

    public HumanBeing() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть null или пустым");
        }
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Координаты не могут быть null");
        }
        this.coordinates = coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        if (creationDate == null) {
            throw new IllegalArgumentException("Дата создания не может быть null");
        }
        this.creationDate = creationDate;
    }

    public boolean getRealHero() {
        return realHero;
    }

    public void setRealHero(boolean realHero) {
        this.realHero = realHero;
    }

    public Boolean getHasToothpick() {
        return hasToothpick;
    }

    public void setHasToothpick(Boolean hasToothpick) {
        this.hasToothpick = hasToothpick;
    }

    public Long getImpactSpeed() {
        return impactSpeed;
    }

    public void setImpactSpeed(Long impactSpeed) {
        this.impactSpeed = impactSpeed;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public void setWeaponType(WeaponType weaponType) {
        if (weaponType == null) {
            throw new IllegalArgumentException("Тип оружия не может быть null");
        }
        this.weaponType = weaponType;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        if (mood == null) {
            throw new IllegalArgumentException("Настроение не может быть null");
        }
        this.mood = mood;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("Машина не может быть null");
        }
        this.car = car;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public int compareTo(HumanBeing other) {
        return Long.compare(this.impactSpeed != null ? this.impactSpeed : 0,
                other.impactSpeed != null ? other.impactSpeed : 0);
    }

    @Override
    public String toString() {
        return String.format("HumanBeing{id=%d, name='%s', coordinates=%s, creationDate=%s, realHero=%b, hasToothpick=%s, impactSpeed=%d, weaponType=%s, mood=%s, car=%s, userId=%d}",
                id, name, coordinates, creationDate, realHero, hasToothpick, impactSpeed, weaponType, mood, car, userId);
    }
}