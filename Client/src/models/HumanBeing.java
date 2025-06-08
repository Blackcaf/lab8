package models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class HumanBeing implements Comparable<HumanBeing>, Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private Coordinates coordinates;
    private LocalDateTime creationDate;
    private boolean realHero;
    private Boolean hasToothpick;
    private Long impactSpeed;
    private WeaponType weaponType;
    private String mood;
    private Car car;
    private Integer userId;

    public HumanBeing() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Coordinates getCoordinates() { return coordinates; }
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }
    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }
    public boolean getRealHero() { return realHero; }
    public void setRealHero(boolean realHero) { this.realHero = realHero; }
    public Boolean getHasToothpick() { return hasToothpick; }
    public void setHasToothpick(Boolean hasToothpick) { this.hasToothpick = hasToothpick; }
    public Long getImpactSpeed() { return impactSpeed; }
    public void setImpactSpeed(Long impactSpeed) { this.impactSpeed = impactSpeed; }
    public WeaponType getWeaponType() { return weaponType; }
    public void setWeaponType(WeaponType weaponType) { this.weaponType = weaponType; }
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    @Override
    public int compareTo(HumanBeing other) {
        return Long.compare(this.impactSpeed != null ? this.impactSpeed : 0,
                other.impactSpeed != null ? other.impactSpeed : 0);
    }
}