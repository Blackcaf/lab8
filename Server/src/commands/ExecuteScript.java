package commands;

import models.HumanBeing;
import models.Coordinates;
import models.Car;
import models.WeaponType;
import models.Mood;
import utility.Console;
import utility.ExecutionResponse;
import managers.CommandManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class ExecuteScript extends Command {
    private final Console console;
    private final CommandManager commandManager;
    private final Set<String> executedFiles = new HashSet<>();
    private final List<String> inputLines = new ArrayList<>();

    public ExecuteScript(Console console, CommandManager commandManager) {
        super("execute_script", "исполнить скрипт из файла");
        this.console = console;
        this.commandManager = commandManager;
    }

    @Override
    public ExecutionResponse execute(HumanBeing humanBeing, Integer userId) {
        if (humanBeing == null || humanBeing.getName() == null || humanBeing.getName().trim().isEmpty()) {
            return new ExecutionResponse(false, "Требуется путь к файлу скрипта");
        }

        String scriptPath = humanBeing.getName().trim();

        try {
            String absolutePath = Paths.get(scriptPath).toAbsolutePath().toString();

            if (executedFiles.contains(absolutePath)) {
                return new ExecutionResponse(false, "Обнаружена рекурсия: файл " + scriptPath + " уже выполняется");
            }

            executedFiles.add(absolutePath);
            StringBuilder scriptOutput = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new FileReader(absolutePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;

                    String[] parts = line.split("\\s+", 2);
                    String commandName = parts[0].toLowerCase();
                    HumanBeing commandHumanBeing = new HumanBeing();

                    if (commandName.equals("execute_script")) {
                        commandHumanBeing.setName(parts.length > 1 ? parts[1] : "");
                    } else if (commandName.equals("add") || commandName.equals("update")) {
                        // Читаем следующие 9 строк для команды add/update
                        inputLines.clear();
                        for (int i = 0; i < 9; i++) {
                            String nextLine = reader.readLine();
                            if (nextLine == null) {
                                return new ExecutionResponse(false, 
                                    String.format("Неожиданный конец файла при чтении параметров команды %s. " +
                                    "Прочитано %d из 9 параметров. Проверьте, что все параметры указаны и нет пустых строк.", 
                                    commandName, inputLines.size()));
                            }
                            nextLine = nextLine.trim();
                            if (nextLine.isEmpty()) {
                                i--; // Пропускаем пустые строки
                                continue;
                            }
                            inputLines.add(nextLine);
                        }
                        // Устанавливаем параметры в объект HumanBeing в правильном порядке
                        if (commandName.equals("update")) {
                            try {
                                Long id = Long.parseLong(inputLines.get(0));
                                commandHumanBeing.setId(id);
                                // Удаляем ID из списка параметров, чтобы не мешал установке остальных полей
                                inputLines.remove(0);
                            } catch (NumberFormatException e) {
                                return new ExecutionResponse(false, "Ошибка: ID должен быть числом");
                            }
                        }
                        if (inputLines.size() >= 1) commandHumanBeing.setName(inputLines.get(0));
                        if (inputLines.size() >= 2) {
                            try {
                                int x = Integer.parseInt(inputLines.get(1));
                                int y = Integer.parseInt(inputLines.get(2));
                                commandHumanBeing.setCoordinates(new Coordinates(x, y));
                            } catch (NumberFormatException e) {
                                return new ExecutionResponse(false, "Ошибка: координаты должны быть числами");
                            }
                        }
                        if (inputLines.size() >= 4) {
                            try {
                                Long impactSpeed = Long.parseLong(inputLines.get(3));
                                commandHumanBeing.setImpactSpeed(impactSpeed);
                            } catch (NumberFormatException e) {
                                return new ExecutionResponse(false, "Ошибка: скорость удара должна быть числом");
                            }
                        }
                        if (inputLines.size() >= 5) {
                            try {
                                boolean realHero = Boolean.parseBoolean(inputLines.get(4));
                                commandHumanBeing.setRealHero(realHero);
                            } catch (Exception e) {
                                return new ExecutionResponse(false, "Ошибка: параметр realHero должен быть true или false");
                            }
                        }
                        if (inputLines.size() >= 6) {
                            String hasToothpick = inputLines.get(5);
                            if (!hasToothpick.equals("null")) {
                                try {
                                    boolean hasToothpickValue = Boolean.parseBoolean(hasToothpick);
                                    commandHumanBeing.setHasToothpick(hasToothpickValue);
                                } catch (Exception e) {
                                    return new ExecutionResponse(false, "Ошибка: параметр hasToothpick должен быть true, false или null");
                                }
                            }
                        }
                        if (inputLines.size() >= 7) {
                            try {
                                WeaponType weaponType = WeaponType.valueOf(inputLines.get(6).toUpperCase());
                                commandHumanBeing.setWeaponType(weaponType);
                            } catch (IllegalArgumentException e) {
                                return new ExecutionResponse(false, "Ошибка: неверный тип оружия");
                            }
                        }
                        if (inputLines.size() >= 8) {
                            String moodStr = inputLines.get(7).toUpperCase();
                            commandHumanBeing.setMood(moodStr);
                        }
                        if (inputLines.size() >= 9) {
                            String carName = inputLines.get(8);
                            commandHumanBeing.setCar(new Car(carName));
                        }
                        // Устанавливаем дату создания для команды add
                        if (commandName.equals("add")) {
                            commandHumanBeing.setCreationDate(LocalDateTime.now());
                        }
                    } else if (commandName.equals("removebyid")) {
                        if (parts.length > 1) {
                            try {
                                commandHumanBeing.setId(Long.parseLong(parts[1]));
                            } catch (NumberFormatException e) {
                                return new ExecutionResponse(false, "Ошибка: ID должен быть числом");
                            }
                        }
                    } else if (parts.length > 1) {
                        commandHumanBeing.setName(parts[1]);
                    }

                    ExecutionResponse response = commandManager.executeCommand(commandName, commandHumanBeing, userId);
                    scriptOutput.append(response.getMessage()).append("\n");
                }
            } finally {
                executedFiles.remove(absolutePath);
            }

            return new ExecutionResponse(true, scriptOutput.toString().trim());
        } catch (IOException e) {
            return new ExecutionResponse(false, "Ошибка при чтении файла: " + e.getMessage());
        }
    }
}