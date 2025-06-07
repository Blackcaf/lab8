package utility;

import models.*;

import java.io.Serializable;

public class HumanBeingAsker implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Console console;

    public HumanBeingAsker(Console console) {
        this.console = console;
    }

    public HumanBeing askHumanBeing() {
        HumanBeing humanBeing = new HumanBeing();

        while (true) {
            console.println("Введите имя (не пустое):");
            String name = console.read().trim();
            if (name == null || name.isEmpty()) {
                console.println("Ошибка: имя не может быть пустым");
                continue;
            }
            humanBeing.setName(name);
            break;
        }

        double x = 0.0;
        while (true) {
            try {
                console.println("Введите координату x (дробное число, не больше " + Double.MAX_VALUE + "):");
                String xInput = console.read().trim();
                if (xInput.isEmpty()) {
                    console.println("Ошибка: координата x не может быть пустой");
                    continue;
                }
                x = Double.parseDouble(xInput);
                if (Double.isInfinite(x) || Double.isNaN(x)) {
                    console.println("Ошибка: координата x должна быть конечным числом");
                    continue;
                }
                if (Math.abs(x) > Double.MAX_VALUE) {
                    console.println("Ошибка: координата x превышает допустимый диапазон (±" + Double.MAX_VALUE + ")");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                console.println("Ошибка: введите корректное дробное число для x");
            }
        }

        float y = 0.0f;
        while (true) {
            try {
                console.println("Введите координату y (дробное число, не больше " + Float.MAX_VALUE + "):");
                String yInput = console.read().trim();
                if (yInput.isEmpty()) {
                    console.println("Ошибка: координата y не может быть пустой");
                    continue;
                }
                y = Float.parseFloat(yInput);
                if (Float.isInfinite(y) || Float.isNaN(y)) {
                    console.println("Ошибка: координата y должна быть конечным числом");
                    continue;
                }
                if (Math.abs(y) > Float.MAX_VALUE) {
                    console.println("Ошибка: координата y превышает допустимый диапазон (±" + Float.MAX_VALUE + ")");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                console.println("Ошибка: введите корректное дробное число для y");
            }
        }

        humanBeing.setCoordinates(new Coordinates(x, y));

        while (true) {
            console.println("Введите realHero (true/false):");
            String realHeroInput = console.read().trim().toLowerCase();
            if (realHeroInput.equals("true") || realHeroInput.equals("false")) {
                humanBeing.setRealHero(Boolean.parseBoolean(realHeroInput));
                break;
            }
            console.println("Ошибка: введите true или false");
        }

        while (true) {
            console.println("Введите hasToothpick (true/false/null):");
            String hasToothpickInput = console.read().trim().toLowerCase();
            if (hasToothpickInput.equals("true") || hasToothpickInput.equals("false")) {
                humanBeing.setHasToothpick(Boolean.parseBoolean(hasToothpickInput));
                break;
            } else if (hasToothpickInput.equals("null")) {
                humanBeing.setHasToothpick(null);
                break;
            }
            console.println("Ошибка: введите true, false или null");
        }

        while (true) {
            console.println("Введите impactSpeed (целое число):");
            String input = console.read().trim();
            if (input.isEmpty()) {
                console.println("Ошибка: значение не может быть пустым");
                continue;
            }
            try {
                long impactSpeed = Long.parseLong(input);
                humanBeing.setImpactSpeed(impactSpeed);
                break;
            } catch (NumberFormatException e) {
                console.println("Ошибка: введите корректное целое число");
            }
        }

        while (true) {
            console.println("Введите weaponType (AXE/SHOTGUN/RIFLE/KNIFE/MACHINE_GUN):");
            String weaponInput = console.read().trim();
            if (weaponInput.isEmpty()) {
                console.println("Ошибка: значение не может быть пустым");
                continue;
            }
            try {
                humanBeing.setWeaponType(WeaponType.valueOf(weaponInput.toUpperCase()));
                break;
            } catch (IllegalArgumentException e) {
                console.println("Ошибка: выберите один из типов оружия: AXE, SHOTGUN, RIFLE, KNIFE, MACHINE_GUN");
            }
        }

        while (true) {
            console.println("Введите mood (SADNESS/LONGING/CALM/RAGE/FRENZY):");
            String moodInput = console.read().trim();
            if (moodInput.isEmpty()) {
                console.println("Ошибка: значение не может быть пустым");
                continue;
            }
            try {
                Mood mood = Mood.valueOf(moodInput.toUpperCase());
                humanBeing.setMood(mood.toString());
                break;
            } catch (IllegalArgumentException e) {
                console.println("Ошибка: выберите одно из настроений: SADNESS, LONGING, CALM, RAGE, FRENZY");
            }
        }

        while (true) {
            console.println("Введите car.name (не пустое):");
            String carName = console.read().trim();
            if (carName.isEmpty()) {
                console.println("Ошибка: имя машины не может быть пустым");
                continue;
            }
            humanBeing.setCar(new Car(carName));
            break;
        }

        humanBeing.setCreationDate(java.time.LocalDateTime.now());

        return humanBeing;
    }
}